/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import java.io.PrintStream;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

class CgenNode extends class_ {
    /** The parent of this node in the inheritance tree */
    private CgenNode parent;

    /** The children of this node in the inheritance tree */
    private Vector children;

    /** Indicates a basic class */
    final static int Basic = 0;

    /** Indicates a class that came from a Cool program */
    final static int NotBasic = 1;
    
    /** Does this node correspond to a basic class? */
    private int basic_status;

    /** class tag */
    private int tag;

    /** attributes include parent. **/
    private List<attr> attrs = null;

    private CgenClassTable table = null;

    /** Constructs a new CgenNode to represent class "c".
     * @param c the class
     * @param basic_status is this class basic or not
     * @param table the class table
     * */
    CgenNode(Class_ c, int basic_status, CgenClassTable table) {
	super(0, c.getName(), c.getParent(), c.getFeatures(), c.getFilename());
	this.parent = null;
	this.children = new Vector();
	this.basic_status = basic_status;
        this.table = table;
	AbstractTable.stringtable.addString(name.getString());
    }

    void addChild(CgenNode child) {
	children.addElement(child);
    }

    /** Gets the children of this class
     * @return the children
     * */
    Enumeration getChildren() {
	return children.elements(); 
    }

    /** Sets the parent of this class.
     * @param parent the parent
     * */
    void setParentNd(CgenNode parent) {
	if (this.parent != null) {
	    Utilities.fatalError("parent already set in CgenNode.setParent()");
	}
	if (parent == null) {
	    Utilities.fatalError("null parent in CgenNode.setParent()");
	}
	this.parent = parent;
    }    
	

    /** Gets the parent of this class
     * @return the parent
     * */
    CgenNode getParentNd() {
	return parent; 
    }

    /** Returns true is this is a basic class.
     * @return true or false
     * */
    boolean basic() { 
	return basic_status == Basic; 
    }

    /**
     * set class tag.
     */
    public void setTag(int t) {
      tag = t;
    }

    /**
     * get class Tag
     */
    public int getTag() {
      return tag;
    }

    public List<attr> getAttrs() {
      if (attrs != null) {
        return attrs;
      }
      if (basic()) {
        attrs = new ArrayList<attr>();
      } else {
        attrs = new ArrayList<attr>(getParentNd().getAttrs());
        for (Enumeration e = features.getElements(); e.hasMoreElements();) {
          Feature f = (Feature)e.nextElement();
          if (f instanceof attr) {
            attrs.add((attr)f);
          }
        }
      }
      return attrs;
    }

    public int getAttrNum() {
      return getAttrs().size();
    }

    private void emitAttrs(PrintStream str) {
      for(attr a: getAttrs()) {
        emitAttr(a, str);
      }
    }

    private void emitAttr(attr a, PrintStream str) {
      if (a.type_decl.equals(TreeConstants.Int)) {
        str.print(CgenSupport.WORD); ((IntSymbol)AbstractTable.inttable.addString("0")).codeRef(str); str.println("");
      } else if (a.type_decl.equals(TreeConstants.Bool)) {
        str.print(CgenSupport.WORD); BoolConst.falsebool.codeRef(str); str.println("");
      } else if (a.type_decl.equals(TreeConstants.Str)) {
       str.print(CgenSupport.WORD); ((StringSymbol)AbstractTable.stringtable.addString("")).codeRef(str); str.println("");
      } else {
        // everything else should be an Object, and should be set to void
        str.println(CgenSupport.WORD + "0");
      }
    }

    public void defPrototype(PrintStream str) {
      // Add -1 eye catcher
      str.println(CgenSupport.WORD + "-1");
      // class prototype label
      CgenSupport.emitProtObjRef(getName(), str); str.printf(CgenSupport.LABEL);
      // class tag
      str.println(CgenSupport.WORD + getTag());
      // class size
      str.println(CgenSupport.WORD +
          (CgenSupport.DEFAULT_OBJFIELDS + getAttrNum()));
      // dispatch pointer
      str.println(CgenSupport.WORD + "");
      // attributes
      emitAttrs(str);

      for (Enumeration e = getChildren(); e.hasMoreElements();) {
        CgenNode c = (CgenNode)e.nextElement();
        c.defPrototype(str);
      }
    }
}
    

    
