package org.aim.mainagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aim.mainagent.events.MonitorEventListener;

public class DummyJVMTIFacade {

	public DummyJVMTIFacade() throws NoSuchMethodException, SecurityException {
	}

	public void newMonitorWaitEvent(Thread thread, Object monitor, long timestamp) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		CEventAgentAdapter.onMonitorWait(thread, monitor, timestamp);
	}

	public void newMonitorEnteredEvent(Thread thread, Object monitor, long timestamp) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		CEventAgentAdapter.onMonitorEntered(thread, monitor, timestamp);
	}

}
