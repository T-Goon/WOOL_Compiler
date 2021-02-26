class A inherits B{
a : C <- if true = false then new B else new D fi;
method():C{
if true = false then new B else new D fi
}
}

class B inherits C {}

class C{}

class D inherits C {}