/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.api.measurement.sampling;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes a sample job with a certain delay.
 * 
 * @author Alexander Wert
 * 
 */
public class SamplingExecuter implements Runnable {
	private long delay;
	private boolean finished;
	private boolean stop;
	private final List<AbstractSampler> sampler;

	/**
	 * Construcotr.
	 * 
	 * @param delay
	 *            sampling delay
	 */
	public SamplingExecuter(long delay) {
		this.setDelay(delay);
		sampler = new ArrayList<>();
	}

	/**
	 * adds a sampler to the job.
	 * 
	 * @param smp
	 *            sampler to add
	 */
	public void addSampler(AbstractSampler smp) {
		sampler.add(smp);
	}

	@Override
	public void run() {
		stop = false;
		finished = false;
		while (!stop) {
			try {
				Thread.sleep(getDelay());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			for (AbstractSampler samp : sampler) {
				samp.sample();
			}

		}
		synchronized (this) {
			finished = true;
			this.notifyAll();
		}
	}

	/**
	 * stops sampling.
	 */
	public void stop() {
		stop = true;
	}

	/**
	 * Waits until sampling thread terminated.
	 */
	public synchronized void waitForTermination() {
		while (!finished) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @param delay
	 *            the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

}
