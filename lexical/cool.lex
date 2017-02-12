/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup


Ai="A|a"
Ci="C|c"
Li="L|l"
Si="S|s"

CLASSi={Ci}{Li}{Ai}{Si}{SI}

%%

<YYINITIAL>"=>" {
  /* Sample lexical rule for "=>" arrow.
     Further lexical rules should be defined
     here, after the last %% separator */
  return new Symbol(TokenConstants.DARROW); }

<YYINITIAL>"{CLASSi}" {
  /* KeyWords
  The keywords of cool are: class, else, false, fi, if, in, inherits, isvoid,
  let, loop, pool, then, while, case, esac, new, of, not, true. Except for the
  constants true and false, keywords are case insensitive.
  To conform to the rules for other obj
  */
  return new Symbol(TokenConstants.CLASS);
}

. {
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
