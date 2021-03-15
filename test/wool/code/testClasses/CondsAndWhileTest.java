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
	    	
	    	// while statement
	    	System.out.println("c is 10: "+(c == 10));
	    	System.out.println("d is Object: "+(d instanceof wool.Object));
	    	
	    	// Equality operators
	    	System.out.println("e is 6: "+(e == true));
	    	System.out.println("f is 6: "+(f == false));
	    	System.out.println("h is 6: "+(h == true));
	    	
	    	// select statement
	    	System.out.println("e is \"second\": "+(i.toString().equals("second")));
	    }
}