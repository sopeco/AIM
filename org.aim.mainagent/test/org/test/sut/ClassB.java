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

public class ClassB {
	
	
	
	public ClassB() {
		methodB3(1);
	}
	
	public void methodB1() {
		System.out.println("methodB1");
	}

	public void methodB2(Integer i) {
		System.out.println("methodB2");
	}

	private void methodB3(Integer i) {
		System.out.println("methodB3");
	}
}
