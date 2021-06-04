package wool.code.testClasses;

import wool.*;

public class CondsAndWhileTest extends O{
	
	 public CondsAndWhileTest()
	    {
	        System.out.println("SUCCESS, CondsAndWhileTest");
	        run();
	    }

	    public void run()
	    {
	    	// if statement
	    	System.out.println("a is 6: "+(a == 6));
	    	System.out.println("b is \"true\": "+(b.toString().equals("true")));
	    	
	    	System.out.println("larger() is 7: "+(larger(6,7) == 7));
	    	System.out.println("childAndThis() is O: "+(childAndThis() instanceof O));
	    	System.out.println("childAndThis() is O: "+(methodB() instanceof P));
	    	
	    	// while statement
	    	System.out.println("c is 10: "+(c == 10));
	    	System.out.println("d is null: "+(d == null));
	    	System.out.println("methodA() returns 10: "+(methodA() == 10));
	    	
	    	// Equality operators
	    	System.out.println("e is 6: "+(e == true));
	    	System.out.println("f is 6: "+(f == false));
	    	System.out.println("h is 6: "+(h == true));
	    	
	    	// select statement
	    	System.out.println("e is \"second\": "+(i.toString().equals("second")));
	    	System.out.println("n is \"\": "+(n.toString().equals("")));
	    	System.out.println("o is null: "+(o == null));
	    	
	    	// isnull
	    	System.out.println("j is false: "+(j == false));
	    	System.out.println("l is true: "+(l == true));
	    	System.out.println("m is false: "+(m == false));
	    	System.out.println("m is false: "+(p == true));
	    }
}