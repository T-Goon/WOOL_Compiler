class A inherits B{
a : B <- new B;
b : B <- a.giveB(2, false=false, "Definitely not a string", new A);
c : B <- givA();
d : B <- this.givA();
givA():A{
new A}
}

class B{

giveB(a:int, c:Bool, d:Str, e:A): A{
new A
}
}