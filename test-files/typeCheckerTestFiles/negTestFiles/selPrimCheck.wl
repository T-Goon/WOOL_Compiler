class A inherits B{
a : C <- select true = false : new B; false :new A ; true : new D; end;
b : Int <- select true = false : 2; false :true ; true : 1+3*6; end;
}

class B inherits C {}

class C{}

class D inherits C {}