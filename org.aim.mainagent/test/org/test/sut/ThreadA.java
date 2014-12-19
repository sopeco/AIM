package org.test.sut;

public class ThreadA extends Thread {
	
	private final long thisId;
	
	public ThreadA(long id) {
		thisId = id;
	}
	
	@Override
	public void run() {
		ClassA a = new ClassA();
		a.methodA1();
	}
	
	@Override
	public long getId() {
		return thisId;
	}
	
}
