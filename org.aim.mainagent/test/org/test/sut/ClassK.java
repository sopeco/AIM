package org.test.sut;

public class ClassK {
	public void returnMethod(final boolean useReturn) {
		System.out.println("ClassK::returnMethod");
		if (useReturn) {
			System.out.println("Early exit...");
			return;
		}
		System.out.println("late exit");
	}
}
