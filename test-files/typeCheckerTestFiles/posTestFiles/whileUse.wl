class A
{
	get(a: int): Str
	{
		b: int <- 0;
		while b < a loop b <- b + 1 pool.typeName()
	}
}