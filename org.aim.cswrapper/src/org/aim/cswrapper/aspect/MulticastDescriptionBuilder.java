package org.aim.cswrapper.aspect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MulticastDescriptionBuilder {

	private List<AspectDescription> aspectList;

	public MulticastDescriptionBuilder() {
		aspectList = new ArrayList<>();
	}

	public void addAspectDescription(AspectDescription desc) {
		aspectList.add(desc);
	}

	public void addAspectDescription(List<AspectDescription> descriptionList) {
		aspectList.addAll(descriptionList);
	}

	private void buildFile(File outFile) {
		try {
			buildFile(new FileWriter(outFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeHeader(BufferedWriter writer) throws IOException {
		writer.write(String.format("// Generated at %s", new Date()));
		writer.newLine();
		writer.write("using AIM_NET.Aspects; using PostSharp.Extensibility; using System; using System.Collections.Generic; using System.Linq; using System.Text;");
		writer.newLine();
		writer.newLine();
	}

	private void buildFile(Writer outWriter) throws IOException {
		BufferedWriter writer = new BufferedWriter(outWriter);

		writeHeader(writer);

		for (AspectDescription adesc : aspectList) {
			writer.write(adesc.toString());
			writer.newLine();
		}

		writer.close();
	}

	public void buildFile(String outFiles) {
		for (String file : outFiles.split(";")) {
			buildFile(new File(file));
		}
	}

	public static void clearDescriptionFile(String descFiles) {
		for (String file : descFiles.split(";")) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writeHeader(writer);
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
