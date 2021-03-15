package wool.code.testClasses;

import wool.*;

public class MethodDispatchTest extends M{
	
	 public MethodDispatchTest()
	    {
	        System.out.println("SUCCESS, MethodDispatchTest");
	        run();
	    }

	    public void run()
	    {
	    	// Local method calls
	    	System.out.println("a is \"hwllo\": "+(a.toString().equals("hwllo")));
	    	System.out.println("b is N: "+(b instanceof N));
	    	
	    	// Object method calls
	    	System.out.println("c is 6: "+(c == 6));
	    	System.out.println("d is 7: "+(d == 7));
	    	System.out.println("e is \"What?\": "+(e.toString().equals("What?")));
	    	
	    	// self_type method calls
	    	System.out.println("f is N: "+(f instanceof N));
	    	System.out.println("g is M: "+(g instanceof M));
	    }
}