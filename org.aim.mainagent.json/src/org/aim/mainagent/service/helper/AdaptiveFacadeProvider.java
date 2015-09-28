package org.aim.mainagent.service.helper;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.aim.mainagent.AdaptiveInstrumentationFacadeMBean;

@SuppressWarnings("restriction")
public class AdaptiveFacadeProvider {
	private static AdaptiveInstrumentationFacadeMBean proxy;

	static {
		JMXServiceURL url;
		try {
			url =  new JMXServiceURL(sun.management.ConnectorAddressLink.importFrom(33274));
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			final ObjectName mbeanName = new ObjectName("org.aim:type=AdaptiveInstrumentationFacade");
			proxy = JMX.newMBeanProxy(mbsc, mbeanName, AdaptiveInstrumentationFacadeMBean.class, true);

		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static AdaptiveInstrumentationFacadeMBean getAdaptiveInstrumentation() {
		return proxy;
	}
}
