class A inherits B{
b : A;
a : B <- b <- new C;
}

class B inherits C{}

class C{}