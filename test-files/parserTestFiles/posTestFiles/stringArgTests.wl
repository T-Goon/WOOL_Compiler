class ClassNameWithNumbers1234 {
	attribute_with_underscore1 : boolean <- true;
	attribute_with_underscore2 : int <- 123;

	testChaining(s1 : String, s2 : String, s3 : String) : boolean {
		"".concat(s1).concat(s2).concat(s3) = "is this the string that's formed?"	
	}
	
	testMultilineStrArgPass() : boolean {
		{
			testChaining("first line \
second line \
this is the third line...",
						"a\nlot\nof\ncharacters\n!@#$%^^&*()",
						"\n \n \t \\ \\ \\ \
\n");
	
			true;
		}	
	}
	
	pow(base : int, exp : int) : int {
		p : int <- 1;
		c : int <- 0;
		{
			while c < exp loop {
				p <- p*base;
				c <- c+1;
			} pool;
			p;
		}
	}
	
	return_true() : boolean {
		b_ : boolean <- (isnull null);
		~(isnull this)
	}
}