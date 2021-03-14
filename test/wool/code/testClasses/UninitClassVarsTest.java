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
	    	System.out.println("a is 0: "+ (a == 0));
	    	System.out.println("b is false: "+(b == false));
	    	System.out.println("c is "+"\""+c+"\": "+ c.toString().equals(""));
	    	System.out.println("d is null: " + (d == null));
	    }
}
