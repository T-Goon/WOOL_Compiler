package wool.code.testClasses;

import wool.*;

public class UninitClassVarsTest extends F{
	
	 public UninitClassVarsTest()
	    {
	        System.out.println("SUCCESS, UninitClassVarsTest");
	        run();
	    }

	    /**
	     * Description 
	     * @param args
	     */
	    public void run()
	    {
	    	System.out.println(a); // int, should be 0
	    	System.out.println(b); // bool, should be 0
	    	System.out.println("\""+c+"\""); // string, should be ""
	    	System.out.println(d); // wool object. should be null
	    }
}
