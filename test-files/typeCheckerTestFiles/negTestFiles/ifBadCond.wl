class A inherits B{

method():C{
if 4 then new B else new D fi
}
}

class B inherits C {}

class C{}

class D inherits C {}