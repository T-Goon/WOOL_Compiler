class I{
a : int <- 1 + 2 + 3;
b : int <- a - 1;
c : int <- a + b;

d : int <- a + 1 * 3 / 2;
e : int <- (a + 1) * 3 / 2;
f : int <- -5 * -(1 + 2);

g : Str;
h : Str;
i : Str <- h <- g <- "hello";

j : Bool <- 5 < 4;
k : Bool <- 4 < 4;
l : Bool <- 3 < 4;

m : Bool <- 5 > 4;
n : Bool <- 4 > 4;
o : Bool <- 3 > 4;

p : Bool <- 5 <= 4;
q : Bool <- 4 <= 4;
r : Bool <- 3 <= 4;

s : Bool <- 5 >= 4;
t : Bool <- 4 >= 4;
u : Bool <- 3 >= 4;

v : Bool <- ~(3 > 1 + 1);
w : Bool <- ~(3 < 1 + 1);
}
