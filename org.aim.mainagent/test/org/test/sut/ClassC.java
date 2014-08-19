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
package org.test.sut;

public class ClassC implements InterfaceA{

	public void methodC1() {
		System.out.println("methodC1");
	}

	public void methodC2(Integer i) {
		System.out.println("methodC2");
	}

	public void methodC3(Integer i) {
		System.out.println("methodC3");
	}

	@Override
	public void ifMethodA() {
		System.out.println("C@ifMethodA");
		
	}

	@Override
	public void ifMethodB() {
		System.out.println("C@ifMethodB");
		
	}
}
