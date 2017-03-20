class Main inherits IO {
   main(): SELF_TYPE {
      {
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
      }
   };
};
