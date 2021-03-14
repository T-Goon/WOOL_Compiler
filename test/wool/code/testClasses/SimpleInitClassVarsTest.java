package wool.code.testClasses;

import wool.*;

public class SimpleInitClassVarsTest extends G{
	
	 public SimpleInitClassVarsTest()
	    {
	        System.out.println("SUCCESS, SimpleInitClassVarsTest");
	        run();
	    }

	    /**
	     * Description 
	     * @param args
	     */
	    public void run()
	    {
	    	System.out.println("a is 100: "+(a==100));
	    	System.out.println("b is true: "+b);
	    	System.out.println("c is \"This is a string\": "+ c.toString().equals("This is a string"));
	    	System.out.println("d is class H: "+(d instanceof H));
	    }
}
