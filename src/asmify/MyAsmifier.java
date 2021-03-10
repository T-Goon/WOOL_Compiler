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
package asmify;

import java.io.*;
import org.objectweb.asm.*;
import org.objectweb.asm.util.*;
import test.TestClass;

/**
 * Description
 */
public class MyAsmifier
{
    public static void main(String[] args) throws Exception
    {
        Class cls = TestClass.class;
        InputStream inputStream 
            = cls.getResourceAsStream(cls.getSimpleName() + ".class");
        ClassReader reader = new ClassReader(inputStream);
//      ClassVisitor visitor = new TraceClassVisitor(new PrintWriter(System.out));	// Bytecode
        ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));  // ASMified code
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
    }
}
