class O{

a : int <- if 3>4 then 5 else 6 fi;
b : Str <- if true then "true" else "false" fi;

larger(a : int, b : int) : int{
if a > b then a else b fi
}

c : int <- 0;
d : Object <- while c < 10 loop {new Object; c<-c+1;} pool;

methodA():int{
c:int<-0;
d : Object <- while c < 10 loop {new Object; c<-c+1;} pool;
c
}

e : Bool <- false = 1 = 2;
f : boolean <- true ~= true;
g : Object <- new Object;
h : boolean <- g = g;

i : Str <- select 1>1 : "first"; 2<3 : "second"; true : "third"; end;
n : Str <- select 1>1 : "first"; false : "second"; false : "third"; end;
o : Object <- select 1>1 : new Object; false : new Object; false : new Object; end;

j : boolean <- isnull i;
k : O;
l : boolean <- isnull k;
m : boolean <- isnull 1;
}

class P inherits Q{}
class Q{}
