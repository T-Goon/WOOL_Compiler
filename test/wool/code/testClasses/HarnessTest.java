package wool.code.testClasses;

import wool.*;

public class HarnessTest extends Test{
	
	 public HarnessTest()
	    {
	        System.out.println("SUCCESS, HarnessTest");
	        run1();
	    }

	    public void run1()
	    {
	    	System.out.println("assert is true: "+(run() == true));
	    }
}