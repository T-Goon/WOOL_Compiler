package wool.code.testClasses;

import wool.*;

public class NonMethExprTest extends I{
	
	 public NonMethExprTest()
	    {
	        System.out.println("SUCCESS, NonMethExprTest");
	        run();
	    }

	    /**
	     * Description 
	     * @param args
	     */
	    public void run()
	    {
	    	System.out.println("a is 6: "+ (a == 6)); // add
	    	System.out.println("b is 5: "+ (b == 5)); // sub
	    	System.out.println("c is 11: "+ (c == 11)); // add and sub with id ref
	    	System.out.println("d is 7: "+ (d == 7)); // mul and div
	    	System.out.println("e is 10: "+ (e == 10)); // paren expressions work
	    	System.out.println("f is 15: "+ (f == 15)); // negative number expr works
	    	
	    	System.out.println("g is \"hello\": "+ (g.toString().equals("hello"))); // assign expr works
	    	System.out.println("h is \"hello\": "+ (h.toString().equals("hello")));
	    	System.out.println("i is \"hello\": "+ (i.toString().equals("hello")));
	    	
	    	// less than works
	    	System.out.println("j is false: "+ (j == false)); 
	    	System.out.println("k is false: "+ (k == false)); 
	    	System.out.println("l is true: "+ (l == true)); 
	    	
	    	// greater than works
	    	System.out.println("m is true: "+ (m == true)); 
	    	System.out.println("n is false: "+ (n == false)); 
	    	System.out.println("o is false: "+ (o == false)); 
	    	
	    	// less than or equal to works
	    	System.out.println("p is false: "+ (p == false)); 
	    	System.out.println("q is true: "+ (q == true)); 
	    	System.out.println("r is true: "+ (r == true)); 
	    	
	    	// greater than or equal to works
	    	System.out.println("s is true: "+ (s == true)); 
	    	System.out.println("t is true: "+ (t == true)); 
	    	System.out.println("u is false: "+ (u == false)); 
	    	
	    	// logical negation works
	    	System.out.println("v is false: "+ (v == false)); 
	    	System.out.println("w is true: "+ (w == true)); 
	    }
}
