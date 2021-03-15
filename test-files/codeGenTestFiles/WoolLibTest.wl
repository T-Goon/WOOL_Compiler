class R{

main():int{

b : Str <- "hel\tl\\o\n \
h";
c : Str <- b.concat("a\n\t\nb\n");
d : Str <- c.substr(1, 2);

e : IO <- new IO;
g : int <- 9;

{
e.outStr(c);
e.outInt(g);
e.copy();
g <- g + b.length() + c.length() ;
}
}

}
