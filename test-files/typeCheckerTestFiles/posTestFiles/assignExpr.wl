class A inherits B{
b : B;
a : C <- b <- new A;
}

class B inherits C{}

class C{}