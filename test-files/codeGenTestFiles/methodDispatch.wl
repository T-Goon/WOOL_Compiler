class M inherits N{

a : Str <- methodA("hwllo");
b : N <- methodB();
c : int <- b.methodC(6);
d : int <- new N.methodC(7);
e : Str <- this.methodA("What?");

f : N <- b.copy();
g : M <- copy();

methodA(s:Str):Str{
s
}
}

class N{
methodB(): N{
new N
}

methodC(a:int): int{
a
}
}
