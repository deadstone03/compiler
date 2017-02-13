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

    private int block_comment_count = 0;

    void inc_block_comment_count() {
      block_comment_count++;
    }
    int dec_block_comment_count() {
      block_comment_count--;
      return block_comment_count;
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
%line

%state LINE_COMMENT_S, BLOCK_COMMENT_S, STRING_S

Aa=(A|a)
Bb=(B|b)
Cc=(C|c)
Dd=(D|d)
Ee=(E|e)
Ff=(F|f)
Gg=(G|g)
Hh=(H|h)
Ii=(I|i)
Jj=(J|j)
Kk=(K|k)
Ll=(L|l)
Mm=(M|m)
Nn=(N|n)
Oo=(O|o)
Pp=(P|p)
Qq=(Q|q)
Rr=(R|r)
Ss=(S|s)
Tt=(T|t)
Uu=(U|u)
Vv=(V|v)
Ww=(W|w)
Xx=(X|x)
Yy=(Y|y)
Zz=(Z|z)

%%

<YYINITIAL>"=>" {
  /* Sample lexical rule for "=>" arrow.
     Further lexical rules should be defined
     here, after the last %% separator */
  return new Symbol(TokenConstants.DARROW); }

<YYINITIAL>";" { return new Symbol(TokenConstants.SEMI); }
<YYINITIAL>"," { return new Symbol(TokenConstants.COMMA); }
<YYINITIAL>":" { return new Symbol(TokenConstants.COLON); }
<YYINITIAL>"(" { return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL>")" { return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL>"{" { return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL>"}" { return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL>"." { return new Symbol(TokenConstants.DOT); }
<YYINITIAL>"@" { return new Symbol(TokenConstants.AT); }
<YYINITIAL>"~" { return new Symbol(TokenConstants.NEG); }
<YYINITIAL>"*" { return new Symbol(TokenConstants.MULT); }
<YYINITIAL>"/" { return new Symbol(TokenConstants.DIV); }
<YYINITIAL>"+" { return new Symbol(TokenConstants.PLUS); }
<YYINITIAL>"-" { return new Symbol(TokenConstants.MINUS); }
<YYINITIAL>"<=" { return new Symbol(TokenConstants.LE); }
<YYINITIAL>"<" { return new Symbol(TokenConstants.LT); }
<YYINITIAL>"=" { return new Symbol(TokenConstants.EQ); }
<YYINITIAL>"<-" { return new Symbol(TokenConstants.ASSIGN); }

<YYINITIAL>{Cc}{Ll}{Aa}{Ss}{Ss} {
  return new Symbol(TokenConstants.CLASS);
}
<YYINITIAL>{Ee}{Ll}{Ss}{Ee} {
  return new Symbol(TokenConstants.ELSE);
}
<YYINITIAL>{Ii}{Ff} {
  return new Symbol(TokenConstants.IF);
}
<YYINITIAL>{Ff}{Ii} {
  return new Symbol(TokenConstants.FI);
}
<YYINITIAL>{Ii}{Nn} {
  return new Symbol(TokenConstants.IN);
}
<YYINITIAL>{Ii}{Nn}{Hh}{Ee}{Rr}{Ii}{Tt}{Ss} {
  return new Symbol(TokenConstants.INHERITS);
}
<YYINITIAL>{Ii}{Ss}{Vv}{Oo}{Ii}{Dd} {
  return new Symbol(TokenConstants.ISVOID);
}
<YYINITIAL>{Ll}{Ee}{Tt} {
  return new Symbol(TokenConstants.LET);
}
<YYINITIAL>{Ll}{Oo}{Oo}{Pp} {
  return new Symbol(TokenConstants.LOOP);
}
<YYINITIAL>{Pp}{Oo}{Oo}{Ll} {
  return new Symbol(TokenConstants.POOL);
}
<YYINITIAL>{Tt}{Hh}{Ee}{Nn} {
  return new Symbol(TokenConstants.THEN);
}
<YYINITIAL>{Ww}{Hh}{Ii}{Ll}{Ee} {
  return new Symbol(TokenConstants.WHILE);
}
<YYINITIAL>{Cc}{Aa}{Ss}{Ee} {
  return new Symbol(TokenConstants.CASE);
}
<YYINITIAL>{Ee}{Ss}{Aa}{Cc} {
  return new Symbol(TokenConstants.ESAC);
}
<YYINITIAL>{Nn}{Ee}{Ww} {
  return new Symbol(TokenConstants.NEW);
}
<YYINITIAL>{Oo}{Ff} {
  return new Symbol(TokenConstants.OF);
}
<YYINITIAL>{Nn}{Oo}{Tt} {
  return new Symbol(TokenConstants.NOT);
}
<YYINITIAL>t{Rr}{Uu}{Ee} {
  // boolean true
  return new Symbol(
      TokenConstants.BOOL_CONST,
      java.lang.Boolean.TRUE);
}
<YYINITIAL>f{Aa}{Ll}{Ss}{Ee} {
  // boolean false
  return new Symbol(
      TokenConstants.BOOL_CONST,
      java.lang.Boolean.FALSE);
}

<YYINITIAL>"--" {
  /* line comment start
   */
   yybegin(LINE_COMMENT_S);
}
<LINE_COMMENT_S>.*\n?$ {
  /* line comment end */
  java.lang.System.out.println("Line Comment: " + yytext());
  yybegin(YYINITIAL);
}
<YYINITIAL>"(*" {
  /* block comment started */
  yybegin(BLOCK_COMMENT_S);
  string_buf.delete(0, string_buf.length());
  inc_block_comment_count();
  string_buf.append(yytext());
}
<BLOCK_COMMENT_S>[^\*\(\)]* {
  // not special char
  string_buf.append(yytext());
}
<BLOCK_COMMENT_S>"(*" {
  // another lay of block comment
  inc_block_comment_count();
  string_buf.append(yytext());
}
<BLOCK_COMMENT_S>"*)" {
  // end of a lay of comment
  string_buf.append(yytext());
  if (dec_block_comment_count() == 0) {
    java.lang.System.out.println("Block Comment: " + string_buf);
    yybegin(YYINITIAL);
  }
}
<BLOCK_COMMENT_S>[\*\(\)] {
  // special char
  string_buf.append(yytext());
}

<YYINITIAL>"\"" {
  // string begin
  yybegin(STRING_S);
  string_buf.delete(0, string_buf.length());
}
<STRING_S>[^\"\n\\]* {
  // not special char
  string_buf.append(yytext());
}
<STRING_S>(\\\")|\\ {
  // special char
  string_buf.append(yytext());
}
<STRING_S>\" {
  // end of string 
  yybegin(YYINITIAL);
  return new Symbol(
      TokenConstants.STR_CONST,
      AbstractTable.stringtable.addString(string_buf.toString()));
}
<STRING_S>\n {
  // return line in string, it's an error
  yybegin(YYINITIAL);
  return new Symbol(
      TokenConstants.ERROR,
      java.lang.String.format("%d: String should close with \".", yyline));
}

<YYINITIAL>[0-9]+ {
  // integers
  return new Symbol(
      TokenConstants.INT_CONST,
      AbstractTable.inttable.addString(yytext()));
}

<YYINITIAL>"self" {
  // self
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}

<YYINITIAL>"SELF TYPE" {
  // SELF_TYPE
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}

<YYINITIAL>[a-z][a-zA-z0-9_]* {
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}

<YYINITIAL>[A-Z][a-zA-z0-9_]* {
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}

<YYINITIAL>[ \n\f\r\t\v] {
  /* white space
     blank (ascii 32), \n (newline, ascii 10), \f (form feed, ascii 12),
     \r (carriage return, ascii 13), \t (tab, ascii 9), \v (vertical tab, ascii 11)
   */
   // do nothing
}

. {
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
