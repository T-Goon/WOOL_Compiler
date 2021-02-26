class A inherits C
{
	c: int <- 5;
	get(a: int): int
	{
		a
	}
}
class B inherits A
{
	get(b: int): A
	{
		new A
	}
}
class C inherits A
{
	c: int <- 5;
}