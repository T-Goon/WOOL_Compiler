class OtherClass {
	return_two(n : int) : int {
		2	
	}
	return_myself() : SELF_TYPE {
		this
	}
}

class ExprTest {
	function_to_call() : int {
		3	
	}

	function() : int {
		o : OtherClass <- new OtherClass;
		i : int;
		b : boolean;
		{
			# test new keyword
			o <- new OtherClass;
			# test method call on object
			o.return_two(i);
			# chaining method call
			i <- (new OtherClass).return_myself().return_two(i);
			# call function in same class
			i <- function_to_call();
			# if test
			if i = 3 then
				i <- 4
			else
				while i < 10 loop i <- i+1 pool
			fi;
			# select test
			select
				i = 0 : i <- 10;
				i = 1 : i <- 11;
				i = 2 : i <- 12; 
				i = 3 : i <- 13;
				i = 4 : i <- 14;
			end ;
			# test isnull
			b <- isnull o;
			# test arithmetic
			i <- 5 + 5;
			i <- 5 - 5;
			i <- 5 * 5;
			i <- 5 / 5;
			i <- -6;
			b <- 4 < 5;
			b <- 4 <= 5;
			b <- 4 = 5;
			b <- 4 ~= 5;
			b <- 4 >= 5;
			b <- 4 > 5;
			b <- ~(4 < 5);
			# test true/false/null 
			b <- ~true;
			b <- false;
			o <- null;
			# test more complex expressions
			b <- ((4 + 5) * (5 - 6)) = ((1 / 2) + (-200));
			i <- ~((isnull i) = (o.return_two(1) = (111111*111111)));  
			1;
		}
	}
}