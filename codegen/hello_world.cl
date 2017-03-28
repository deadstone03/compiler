class Main inherits IO {
   main(): SELF_TYPE {
      {
        false;
        not true;
	out_string("OMG, Hello, World.\n");
        out_int(42);
	out_string("\n");
        out_int(13 + 30);
	out_string("\n");
        out_int(2  * 22);
	out_string("\n");
        out_int(48 - 3);
	out_string("\n");
        out_int(92 / 2);
	out_string("\n");
        out_int(1 + 2 * 3 / 4 - 4);
	out_string("\n");
        1;
        if (1 <= 1) then out_string("true branch")
        else out_string("false branch") fi;
        let x: Int <- 0 in
          while x <= 10 loop
            {
              out_int(x);
              out_string("\n");
              x <- x + 1;
            }
          pool;
        out_string("done");
      }
   };
};
