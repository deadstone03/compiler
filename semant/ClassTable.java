import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.Queue;
import java.util.Enumeration;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private Map<AbstractSymbol, List<class_c>> children = new HashMap<AbstractSymbol, List<class_c>>();
    private SymbolTable classTable = new SymbolTable();
    // methodEnv is className -> (methodName -> types)
    private SymbolTable methodEnv = new SymbolTable();
    // attrEnv is className -> (attrName -> Type)
    private SymbolTable attrEnv = new SymbolTable();
    private List<class_c> classes = null;
    
    private int semantErrors;
    private PrintStream errorStream;

    public boolean addAttr(AbstractSymbol c, AbstractSymbol name, AbstractSymbol type) {
      if (getAttr(getClass(c).getParent(), name) != null) {
        return false;
      }
      SymbolTable attrs = (SymbolTable)attrEnv.lookup(c);
      if (attrs == null) {
        attrs = new SymbolTable();
        attrs.enterScope();
        attrEnv.addId(c, attrs);
      }
      if (attrs.probe(name) != null) {
        return false;
      }
      attrs.addId(name, type);
      return true;
    }

    public AbstractSymbol getAttr(AbstractSymbol c, AbstractSymbol name) {
      if (name.equals(TreeConstants.self)) {
        return TreeConstants.SELF_TYPE;
      }
      while(!c.equals(TreeConstants.No_class)) {
        SymbolTable attrs = (SymbolTable)attrEnv.lookup(c);
        AbstractSymbol type_name = null;
        if (attrs != null) {
          type_name = (AbstractSymbol)attrs.lookup(name); 
          if (type_name != null) {
            return type_name;
          }
        }
        c = getClass(c).getParent();
      }
      return null;
    }
    
    public boolean addMethod(AbstractSymbol c, AbstractSymbol name, List<AbstractSymbol> types) {
      List<AbstractSymbol> pTypes = getMethod(getClass(c).getParent(), name);
      if (pTypes != null) {
        if (pTypes.size() != types.size()) {
          return false;
        }
        for (int i = 0; i < pTypes.size(); ++i) {
          if (!pTypes.get(i).equals(types.get(i))) {
            return false;
          }
        }
      }
      SymbolTable methods = (SymbolTable)methodEnv.lookup(c);
      if (methods == null) {
        methods = new SymbolTable();
        methods.enterScope();
        methodEnv.addId(c, methods);
      }
      if (methods.probe(name) != null) {
        return false;
      }
      methods.addId(name, types);
      return true;
    }

    public List<AbstractSymbol> probeMethod(AbstractSymbol c, AbstractSymbol name) {
      SymbolTable methods = (SymbolTable)methodEnv.lookup(c);
      if (methods == null) {
        return null;
      }
      return (List<AbstractSymbol>)methods.lookup(name);
    }

    public List<AbstractSymbol> getMethod(AbstractSymbol c, AbstractSymbol name) {
      while(!c.equals(TreeConstants.No_class)) {
        SymbolTable methods = (SymbolTable)methodEnv.lookup(c);
        if (methods != null) {
          List<AbstractSymbol> types = (List<AbstractSymbol>)methods.lookup(name);
          if (types != null) {
            return types;
          }
        }
        c = getClass(c).getParent();
      }
      return null;
    }

    public AbstractSymbol self2real(AbstractSymbol t, AbstractSymbol c) {
      if (t.equals(TreeConstants.SELF_TYPE)) {
        return c;
      }
      return t;
    }

    private void addClass(class_c c) {
      if (c.getName().equals(TreeConstants.SELF_TYPE)) {
        semantError(c).printf("Redefine SELF_TYPE class.\n");
      }
      if (c.getParent().equals(TreeConstants.Str)) {
        semantError(c).printf("class %s inherits from String class.\n", c.getName());
      } else if (c.getParent().equals(TreeConstants.Int)) {
        semantError(c).printf("class %s inherits from Int class.\n", c.getName());
      } else if (c.getParent().equals(TreeConstants.Bool)) {
        semantError(c).printf("class %s inherits from Bool class.\n", c.getName());
      } else if (c.getParent().equals(TreeConstants.SELF_TYPE)) {
        semantError(c).printf("class %s inherits from SELF_TYPE class.\n", c.getName());
      }

      if (classTable.probe(c.getName()) != null) {
        semantError(c).printf("class %s is defined multiple times.\n", c.getName());
      } else {
        // check if there is circle inherit or not
        classTable.addId(c.getName(), c);
        AbstractSymbol p = c.getParent();
        List<class_c> cs = children.get(p);
        if (cs == null) {
          cs = new ArrayList<class_c>();
          children.put(p, cs);
        }
        cs.add(c);
        class_c current = c;
        while (!current.getName().equals(TreeConstants.Object_)) {
          current = getClass(current.getParent());
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
      if (s.equals(TreeConstants.SELF_TYPE)) {
        return (class_c)classTable.lookup(TreeConstants.Object_);
      }
      // get s's correpnding class
      return (class_c)classTable.lookup(s);
    }

    public List<class_c> getClasses() {
      // return topologic class list
      // base to sub
      if (classes == null) {
        classes = new ArrayList<class_c>();
        ArrayList<class_c> q = new ArrayList<class_c>();
        class_c c = getClass(TreeConstants.Object_);
        q.add(c);
        while(!q.isEmpty()) {
          c = q.remove(0);
          classes.add(c);
          List<class_c> cs = children.get(c.getName());
          if (cs != null) {
            for (class_c cc: cs) {
              q.add(cc);
            }
          }
        }
      }
      return classes;
    }

    public boolean inherits(AbstractSymbol sub, AbstractSymbol base) {
      class_c current = getClass(sub);
      if (current == null) {
        return false;
      }
      while (!current.getName().equals(base)) {
        if (current.getName().equals(TreeConstants.Object_)) {
          return false;
        }
        current = getClass(current.getParent());
        if (current == null) {
          return false;
        }
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

    public void checkType(AbstractSymbol type_name, AbstractSymbol currentClass, TreeNode t) {
      if (type_name.equals(TreeConstants.SELF_TYPE)) {
        return;
      }
      if (getClass(type_name) == null) {
        semantError(getClass(currentClass).getFilename(), t).printf(
            "class %s is not defined\n", type_name);
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
      while(c1.equals(c2)) {
        prev = c1;
        if (s1.empty() || s2.empty()) {
          break;
        }
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
      return s;
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
        methodEnv.enterScope();
        attrEnv.enterScope();
        installBasicClasses();
        for (Enumeration e = cls.getElements(); e.hasMoreElements();){
          class_c c = (class_c)e.nextElement();
          addClass(c);
        }
        for (Enumeration e = cls.getElements(); e.hasMoreElements();){
          class_c c = (class_c)e.nextElement();
          if (getClass(c.getParent()) == null) {
            semantError(c).printf(
                "Class %s inherits from undefined class %s.\n",
                c.getName(), c.getParent());
          }
        }
        if (getClass(TreeConstants.Main) == null) {
          semantError().printf("Class Main is not defined.\n");
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
			  
    
