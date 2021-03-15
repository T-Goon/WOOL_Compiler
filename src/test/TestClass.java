/*******************************************************************************
 * This files was developed for CS4533: Techniques of Programming Language Translation
 * and/or CS544: Compiler Construction
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020-21 Gary F. Pollice
 *******************************************************************************/
package test;

import wool.Str;

/**
 * Description
 */
public class TestClass
{
	A a = new A();
	int h = a(1, 3);

	public int a(int b, int c) {
		if (b == 1) {
			return 1;
		} else if(c == 1){
			return 2;
		} else if (b ==3) {
			return 3;
		} else {
			return 4;
		}
	}
	
}

class A{
	public A() {
		
	}
	
	public int a(int b, int c) {
		if (b == 1) {
			return 1;
		} else if(c == 1){
			return 2;
		} else if (b ==3) {
			return 3;
		} else {
			return 4;
		}
	}
}