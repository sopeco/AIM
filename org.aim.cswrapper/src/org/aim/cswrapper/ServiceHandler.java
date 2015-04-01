package org.aim.cswrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.cswrapper.Configuration.ConfigurationKey;
import org.aim.cswrapper.aspect.AspectDescription;
import org.aim.cswrapper.aspect.AspectDescriptionUtils;
import org.aim.cswrapper.aspect.MulticastDescriptionBuilder;
import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.csharp.services.CsServiceHandler;
import org.aim.mainagent.sampling.Sampling;

public final class ServiceHandler implements CsServiceHandler {

	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(ServiceHandler.class);

	public ServiceHandler() {
	}

	@Override
	public void instrument(InstrumentationDescription description) {
		LOGGER.debug("Instrument");

		MulticastDescriptionBuilder builder = new MulticastDescriptionBuilder();

		Set<String> usedAspects = new HashSet<>();

		for (InstrumentationEntity<?> entity : description.getInstrumentationEntities()) {
			List<AspectDescription> descriptionList = AspectDescriptionUtils.convert(entity);
			builder.addAspectDescription(descriptionList);

			for (AspectDescription aspDesc : descriptionList) {
				usedAspects.add(aspDesc.getAspectClass());
			}
		}

		// TODO - Solve this hack: Constructor exclusion
		for (String aspectClass : usedAspects) {
			AspectDescription ctorExcl = new AspectDescription();
			ctorExcl.setAspectClass(aspectClass);
			ctorExcl.setAttributeExclude(true);
			ctorExcl.setPriority(9);
			ctorExcl.setTargetMembers("regex:ctor");
			builder.addAspectDescription(ctorExcl);
		}

		LOGGER.debug("Build AspectDescriptionFile..");
		builder.buildFile(Configuration.get(ConfigurationKey.CS_APP_ASPECT_DESCRIPTION_FILE));
		LOGGER.debug("AspectDescriptionFile built.");

		LOGGER.debug("Compile C#-Project '%s'", Configuration.get(ConfigurationKey.CS_APP_PROJECT));

		IISExpressController.kill();
		Utils.compileAndPublish();
		IISExpressController.start(Configuration.get(ConfigurationKey.IIS_EXPRESS_SITE));

		// Start sampler
		if (!description.getSamplingDescriptions().isEmpty()) {
			try {
				Sampling.getInstance().addMonitoringJob(description.getSamplingDescriptions());
			} catch (MeasurementException e) {
				throw new RuntimeException(e);
			}
		}

		// Wait for IIS
		LOGGER.debug("Waiting for IIS");
		if (IISExpressController.getSiteUrl() != null) {
			try {
				LOGGER.debug("Fetching IIS site..");
				fetchSite();
			} catch (Exception e) {
			}
		}
	}

	private void fetchSite() {
		URL url;
		InputStream is = null;
		BufferedReader br;

		try {
			System.out.println("Fetching site");
			url = new URL(IISExpressController.getSiteUrl());
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));

			while ((br.readLine()) != null);

			System.out.println("finished");
		} catch (MalformedURLException mue) {
			throw new RuntimeException();
		} catch (IOException ioe) {
			throw new RuntimeException();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}
		}
	}

	@Override
	public void uninstrument() {
		LOGGER.debug("Uninstrument");

		LOGGER.debug("Clear AspectDescriptionFile..");
		MulticastDescriptionBuilder.clearDescriptionFile(Configuration
				.get(ConfigurationKey.CS_APP_ASPECT_DESCRIPTION_FILE));

		LOGGER.debug("Compile C#-Project '%s'", Configuration.get(ConfigurationKey.CS_APP_PROJECT));
		// Utils.compile(Configuration.get(ConfigurationKey.CS_APP_PROJECT));

		IISExpressController.kill();
		Utils.compileAndPublish();
		IISExpressController.start(Configuration.get(ConfigurationKey.IIS_EXPRESS_SITE));

		Sampling.getInstance().clearMonitoringJobs();
		InstrumentationUtilsController.getInstance().clear();
	}
}
