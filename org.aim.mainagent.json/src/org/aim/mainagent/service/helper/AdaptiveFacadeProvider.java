package org.aim.mainagent.service.helper;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.aim.mainagent.AdaptiveInstrumentationFacadeMXBean;

public class AdaptiveFacadeProvider {
	private static AdaptiveInstrumentationFacadeMXBean proxy;
	public static String jmxPort = "9010";

	static {
		JMXServiceURL url;
		try {
			url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+ jmxPort + "/jmxrmi");
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			final ObjectName mbeanName = new ObjectName("org.aim:type=AdaptiveInstrumentationFacade");
			proxy = JMX.newMXBeanProxy(mbsc, mbeanName, AdaptiveInstrumentationFacadeMXBean.class, true);

		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static AdaptiveInstrumentationFacadeMXBean getAdaptiveInstrumentation() {
		return proxy;
	}
}
