class Parent {}
class TestClass inherits Parent {
	a : Str <- "abcd";
	
	g() : Str {
		f(a, a)	
	}
	
	f(b : Str, c : Str) : Str {
		c
	}
	
	fact(n : int) : int {
		if n = 0 then
			1
		else
			n * fact(n-1)
		fi
	}
	
	fib(n : int) : int {
		if n <= 1 then
			n
		else
			fact(n-1) + fact(n-2)
		fi
	}
	
	while_test(m : int) : int {
		s : int <- 0;
		c : int <- 0;
		{
			while c <= m loop {
				s <- s+c;
				c <- c+1;
			} pool;
			s;
		}
	}
	
}