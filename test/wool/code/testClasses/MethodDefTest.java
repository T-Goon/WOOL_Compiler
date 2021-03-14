package wool.code.testClasses;

import wool.*;

public class MethodDefTest extends K{
	
	 public MethodDefTest()
	    {
	        System.out.println("SUCCESS, MethodDefTest");
	        run();
	    }

	    /**
	     * Description 
	     * @param args
	     */
	    public void run()
	    {
	    	// Very basic methods
	    	System.out.println("MethodA returns 3: "+(methodA() == 3));
	    	System.out.println("MethodB returns true: "+(methodB() == true));
	    	System.out.println("MethodC returns \"hello\": "+(methodC().toString().equals("hello")));
	    	
	    	// Method with arguments
	    	int a = 3;
	    	int b = 4;
	    	int c = 5;
	    	System.out.println("MethodD returns 23: "+(methodD(a, b, c) == 23));
	    	
	    	wool.Str d = new wool.Str("hello");
	    	System.out.println("MethodE returns \"hello\": "+(methodE(d).toString().equals("hello")));
	    	
	    	// Method with local variables
	    	System.out.println("MethodF returns 2: "+(methodF(a) == 2));
	    	
	    	// Method gets class variables
	    	System.out.println("MethodG returns 251: "+(methodG(a) == 251));
	    	System.out.println("MethodH returns 254: "+(methodH(a) == 254));
	    }
}