class A inherits B{
a : C <- select 5 : new B; false :new A ; true : new D; end;
b : Int <- select true = false : 2; false :4 ; true : 1+3*6; end;
}

class B inherits C {}

class C{}

class D inherits C {}