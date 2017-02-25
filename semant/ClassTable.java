import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private SymbolTable classTable = new SymbolTable();
    
    private int semantErrors;
    private PrintStream errorStream;

    public AbstractSymbol self2real(AbstractSymbol t, AbstractSymbol c) {
      if (t.equals(TreeConstants.SELF_TYPE)) {
        return c;
      }
      return t;
    }

    private void addClass(class_c c) {
      if (classTable.probe(c.getName()) != null) {
        semantError(c).printf("class %s is defined multiple times.\n", c.getName());
      } else {
        classTable.addId(c.getName(), c);
        class_c current = c;
        while (!current.getName().equals(TreeConstants.Object_)) {
          current = (class_c)classTable.lookup(current.getParent());
          if (current == null) {
            break;
          } 
          if (current.getName().equals(c.getName())) {
            semantError(c).printf("class %s has a circle dependences.", c.getName());
            break;
          }
        }
      }
    }

    public class_c getClass(AbstractSymbol s) {
      // get s's correpnding class
      return (class_c)classTable.lookup(s);
    }

    public boolean inherits(AbstractSymbol sub, AbstractSymbol base) {
      class_c current = getClass(sub);
      while (!current.getName().equals(base)) {
        if (current.getName().equals(TreeConstants.Object_)) {
          return false;
        }
        current = getClass(current.getParent());
      }
      return true;
    }

    public void checkInherits(
        AbstractSymbol sub, AbstractSymbol base, AbstractSymbol currentClass, TreeNode t) {
      if (!inherits(sub, base)) {
        semantError(getClass(currentClass).getFilename(), t).printf(
            "%s is not sub type of %s\n", sub, base);
      }
    }

    /**
     * return the nearest common ancestor type.
     *
     * */
    public AbstractSymbol commonAncestor(AbstractSymbol t1, AbstractSymbol t2) {
      Stack<AbstractSymbol> s1 = ancestors(t1);
      Stack<AbstractSymbol> s2 = ancestors(t2);
      AbstractSymbol c1 = s1.pop();
      AbstractSymbol c2 = s2.pop();
      AbstractSymbol prev = c1;
      while(!s1.empty() && !s1.empty() && c1.equals(c2)) {
        prev = c1;
        c1 = s1.pop();
        c2 = s2.pop();
      }
      return prev;
    }

    private Stack<AbstractSymbol> ancestors(AbstractSymbol t) {
      Stack<AbstractSymbol> s = new Stack<AbstractSymbol>();
      s.push(t);
      while (!t.equals(TreeConstants.Object_)) {
        t = getClass(t).getParent();
        s.push(t);
      }
    }

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
	AbstractSymbol filename 
	    = AbstractTable.stringtable.addString("<basic class>");
	
	// The following demonstrates how to create dummy parse trees to
	// refer to basic Cool classes.  There's no need for method
	// bodies -- these are already built into the runtime system.

	// IMPORTANT: The results of the following expressions are
	// stored in local variables.  You will want to do something
	// with those variables at the end of this method to make this
	// code meaningful.

	// The Object class has no parent class. Its methods are
	//        cool_abort() : Object    aborts the program
	//        type_name() : Str        returns a string representation 
	//                                 of class name
	//        copy() : SELF_TYPE       returns a copy of the object

	class_c Object_class = 
	    new class_c(0, 
		       TreeConstants.Object_, 
		       TreeConstants.No_class,
		       new Features(0)
			   .appendElement(new method(0, 
					      TreeConstants.cool_abort, 
					      new Formals(0), 
					      TreeConstants.Object_, 
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.type_name,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.copy,
					      new Formals(0),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0))),
		       filename);
        addClass(Object_class);
	
	// The IO class inherits from Object. Its methods are
	//        out_string(Str) : SELF_TYPE  writes a string to the output
	//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
	//        in_string() : Str            reads a string from the input
	//        in_int() : Int                "   an int     "  "     "

	class_c IO_class = 
	    new class_c(0,
		       TreeConstants.IO,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.out_string,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.out_int,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_string,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_int,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0))),
		       filename);
        addClass(IO_class);

	// The Int class has no methods and only a single attribute, the
	// "val" for the integer.

	class_c Int_class = 
	    new class_c(0,
		       TreeConstants.Int,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);
        addClass(Int_class);

	// Bool also has only the "val" slot.
	class_c Bool_class = 
	    new class_c(0,
		       TreeConstants.Bool,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);
        addClass(Bool_class);

	// The class Str has a number of slots and operations:
	//       val                              the length of the string
	//       str_field                        the string itself
	//       length() : Int                   returns length of the string
	//       concat(arg: Str) : Str           performs string concatenation
	//       substr(arg: Int, arg2: Int): Str substring selection

	class_c Str_class =
	    new class_c(0,
		       TreeConstants.Str,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.Int,
					    new no_expr(0)))
			   .appendElement(new attr(0,
					    TreeConstants.str_field,
					    TreeConstants.prim_slot,
					    new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.length,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.concat,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg, 
								     TreeConstants.Str)),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.substr,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int))
						  .appendElement(new formalc(0,
								     TreeConstants.arg2,
								     TreeConstants.Int)),
					      TreeConstants.Str,
					      new no_expr(0))),
		       filename);
        addClass(Str_class);

	/* Do somethind with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */

    }

    public ClassTable(Classes cls) {
	semantErrors = 0;
	errorStream = System.err;
        classTable.enterScope();
        installBasicClasses();
        for (Enumeration e = cls.getElements(); e.hasMoreElements();){
          class_c c = (class_c)e.nextElement();
          addClass(c);
        }
    }

    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_c c) {
	return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
	errorStream.print(filename + ":" + t.getLineNumber() + ": ");
	return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
	semantErrors++;
	return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
	return semantErrors != 0;
    }

    public void semantErrorValNotDefine(AbstractSymbol currentClass, AbstractSymbol val, TreeNode t) {
      semantError(getClass(currentClass).getFilename(), t)
        .printf("%s is not defined.\n", val);
    }

    public void semantErrorMethodArgsNotRight(AbstractSymbol currentClass, TreeNode t, AbstractSymbol c, AbstractSymbol m, int provided, int expected) {
      semantError(getClass(currentClass).getFilename(), t)
        .printf("Method %s.%s needs %d args, but provide %d.",
            c, m, expected, provided);
    }
}
			  
    
