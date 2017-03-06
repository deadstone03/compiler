
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

class Main {
  main():Int { 0 };
};

class Foo {
  a: Int;
  f():Int { 1234 };
};

class Bar inherits Foo{
  b: Int;
  f():Int { 1234 };
};

class Bazz inherits Bar{
  c: Int;
};
