/*******************************************************************************
 * This files was developed for CS4533-Techniques of Program Translation and
 * CS544-Compiler Construction at Worcester Polytechnic Institute.
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
import java.nio.file.*;
import org.objectweb.asm.*;
import org.objectweb.asm.util.*;

/**
 * Description
 */
public class ASMRunner
{
    /**
     * Description
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args)
    {
            try {
                String userDirectory = new File(".").getCanonicalPath();
                System.out.println(userDirectory + "/bin/test/TestClass.class");
                byte[] bytes =
                        Files.readAllBytes(Paths.get(userDirectory + "/bin/test/TestClass.class"));
                ClassReader reader = new ClassReader(bytes);
                StringWriter sw = new StringWriter();
                ClassVisitor visitor = new TraceClassVisitor(new PrintWriter(System.out));
//                ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
                reader.accept(visitor, 0);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    }
}
