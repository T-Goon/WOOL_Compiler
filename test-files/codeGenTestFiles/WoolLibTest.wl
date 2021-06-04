class R{

j : Object <- new Object;

main():int{

b : Str <- "hel\tl\\o\n \
h";
c : Str <- b.concat("a\n\t\nb\n");
d : Str <- c.substr(1, 2);

e : IO <- new IO;
g : int <- 9;
i : Object <- new Object;

{
e.outStr("foo");
e.outStr(c);
e.outInt(g);
g <- e.inInt();
e.outInt(g);
e.copy();
e.outStr(c);
<<<<<<< HEAD
<<<<<<< HEAD
=======
e.outStr(e.typeName());
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c
=======
e.outStr(e.typeName());
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c
g <- g + b.length() + c.length() ;
}
}

}

class S{
r : R <- new R;

methodA():int{
{
r.main();
4;
}
}
}
