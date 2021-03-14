class K{
  r : int <- 500;

  methodA() : int{
    3
  }

  methodB() : boolean{
    true
  }

  methodC() : Str{
    "hello"
  }

  methodD(a:int, b:int, c:int) : int{
    a+b*c
  }

  methodE(a:Str) : Str{
    a
  }

  methodF(a:int) : int{
    r : int <- a + 1;
    c : int;
    c <- r/2
  }

  methodG(a:int) : int{
    b : int <- a + r;
    c : int;
    c <- b/2
  }

  methodH(a:int) : int{
    b : int <- a + r;
    r : int <- 5;
    c : int <- r + b;
    c <- c/2
  }
}
