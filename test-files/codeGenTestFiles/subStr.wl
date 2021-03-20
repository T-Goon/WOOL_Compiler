(* Test for Str.substr *)
class T inherits IO
{
s : Str <- "abcsuccessxyz";

assert(expect : int, actual : int) : boolean {
if expect = actual
then
true
else
{
abort();
false;
}
fi
}

run() : boolean
{
{
assert(7, s.substr(3, 7).length());
outStr(s.substr(3, 7));
true;
}
}
}
