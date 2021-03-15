class O{

a : int <- if 3>4 then 5 else 6 fi;
b : Str <- if true then "true" else "false" fi;

larger(a : int, b : int) : int{
if a > b then a else b fi
}

c : int <- 0;
d : Object <- while c < 10 loop c<-c+1 pool;

e : Bool <- false = 1 = 2;
f : boolean <- true ~= true;
g : Object <- new Object;
h : boolean <- g = g;

i : Str <- select 1>1 : "first"; 2<3 : "second"; true : "third"; end;

}
