class Foo inherits IO {
  s : Str <- "hello";

  bar(s : Str) : IO { outStr(s) }
}