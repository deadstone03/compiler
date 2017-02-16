/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
    private boolean string_contains_null = false;
    private int block_comment_count = 0;
    void inc_block_comment_count() {
      block_comment_count++;
    }
    int dec_block_comment_count() {
      block_comment_count--;
      return block_comment_count;
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int BLOCK_COMMENT_S = 2;
	private final int STRING_S = 3;
	private final int LINE_COMMENT_S = 1;
	private final int yy_state_dtrans[] = {
		0,
		100,
		107,
		59
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_END,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_END,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NOT_ACCEPT,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"19,60:8,67,56,67:2,57,60:18,67,60,59,60:4,18,6,7,12,14,4,15,10,13,21:10,5,3" +
",16,1,2,60,17,26,66,22,48,30,34,66,38,32,66:2,24,66,36,46,50,66,40,28,42,54" +
",44,52,66,63,66,65,20,65:2,62,65,27,61,23,49,31,35,64,39,33,64:2,25,64,37,4" +
"7,51,64,41,29,43,55,45,53,64:3,8,60,9,11,60,0,58")[0];

	private int yy_rmap[] = unpackFromString(1,196,
"0,1,2,1:4,3,1:5,4,1:2,5,6,1,7,8,9,1:9,10,11,10:2,1,10:5,12,10:3,12,10:2,12," +
"10:4,1,13,14,1:3,15,1:11,16,17,18,19,12,20,12:15,21,1,22,23,24,25,26,27,28," +
"29,30,31,24,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52," +
"53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77," +
"78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,1" +
"02,103,104,105,106,107,108,10,12,109,110,111,112,113,114,115,116,117,118,11" +
"9,120")[0];

	private int yy_nxt[][] = unpackFromString(121,68,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,72,3:2,19,20,21,136,137,182,18" +
"3,184,185,186,187,73,74,97,98,138,139,182,183,182,183,188,189,182,183,102,1" +
"03,182,183,190,191,192,193,182,183,22,23,1,24,3,183,3,182,183,3,182,23,-1:7" +
"0,25,-1:77,26,-1:62,27,-1:75,28,-1:53,29,-1:13,30,-1:73,19,-1:66,182:4,194:" +
"2,140:2,182:28,-1:5,182:6,-1:21,183:4,195:2,141:2,183:28,-1:5,183:6,-1:21,1" +
"82:36,-1:5,182:6,-1:21,182:18,164:2,182:16,-1:5,182:6,-1:21,183:36,-1:5,183" +
":6,-1:2,54:5,-1:2,54:4,-1,54:43,-1,54,-1,54:9,-1:12,57,-1:55,1,94:18,60,101" +
",94:35,61,94,1,62,94:8,-1:18,35,-1:50,71:19,95,71:35,-1:3,71:9,-1:20,182:8," +
"150:2,182:4,31:2,32:2,182:18,-1:5,182:6,-1:21,183:8,151:2,183:4,75:2,76:2,1" +
"83:18,-1:5,183:6,-1:21,183:18,167:2,183:16,-1:5,183:6,-1:57,53,-1:12,94:18," +
"-1:2,94:35,-1,94,-1:2,94:8,-1:18,35,-1:2,95,-1:47,104:55,53,92,53,104:9,-1:" +
"20,182:12,33:2,182:22,-1:5,182:6,-1:21,183:6,153:2,183:4,77:2,183:22,-1:5,1" +
"83:6,-1:8,58,-1:60,1,96:55,53,92,53,96:9,-1,63:18,64,65,63:14,66,63,67,63:5" +
",68,63:12,69,-1:2,63:2,70,63:6,-1:20,182:14,34:2,182:20,-1:5,182:6,-1:21,18" +
"3:14,78:2,183:20,-1:5,183:6,-1:21,182:22,36:2,182:12,-1:5,182:6,-1:21,183:2" +
"2,79:2,183:12,-1:5,183:6,-1,1,54:5,55,93,54:4,99,54:43,56,54,1,54:9,-1:20,1" +
"82:32,37:2,182:2,-1:5,182:6,-1:21,183:32,80:2,183:2,-1:5,183:6,-1:21,182:22" +
",38:2,182:12,-1:5,182:6,-1:21,183:22,81:2,183:12,-1:5,183:6,-1:21,182:10,39" +
":2,182:24,-1:5,182:6,-1:21,183:10,82:2,183:24,-1:5,183:6,-1:21,182:30,40:2," +
"182:4,-1:5,182:6,-1:21,183:30,83:2,183:4,-1:5,183:6,-1:21,182:10,42:2,182:2" +
"4,-1:5,182:6,-1:21,183:15,41,183:20,-1:5,183:6,-1:21,182:2,43:2,182:32,-1:5" +
",182:6,-1:21,183:10,84:2,183:24,-1:5,183:6,-1:21,182:16,44:2,182:18,-1:5,18" +
"2:6,-1:21,183:2,85:2,183:32,-1:5,183:6,-1:21,182:4,46:2,182:30,-1:5,182:6,-" +
"1:21,183:16,86:2,183:18,-1:5,183:6,-1:21,182:8,47:2,182:26,-1:5,182:6,-1:21" +
",183:10,45:2,183:24,-1:5,183:6,-1:21,182:10,49:2,182:24,-1:5,182:6,-1:21,18" +
"3:4,87:2,183:30,-1:5,183:6,-1:21,182:28,50:2,182:6,-1:5,182:6,-1:21,183:8,8" +
"8:2,183:26,-1:5,183:6,-1:21,182:8,51:2,182:26,-1:5,182:6,-1:21,183:10,48:2," +
"183:24,-1:5,183:6,-1:21,182:10,52,182:25,-1:5,182:6,-1:21,183:10,89:2,183:2" +
"4,-1:5,183:6,-1:21,183:28,90:2,183:6,-1:5,183:6,-1:21,183:8,91:2,183:26,-1:" +
"5,183:6,-1:21,182:10,105:2,182:14,142:2,182:8,-1:5,182:6,-1:21,183:10,106:2" +
",183:14,143:2,183:8,-1:5,183:6,-1:21,182:10,108:2,182:14,110:2,182:8,-1:5,1" +
"82:6,-1:21,183:10,109:2,183:14,111:2,183:8,-1:5,183:6,-1:21,182:8,112:2,182" +
":26,-1:5,182:6,-1:21,183:8,113:2,183:26,-1:5,183:6,-1:21,182:26,114:2,182:8" +
",-1:5,182:6,-1:21,183:26,115:2,183:8,-1:5,183:6,-1:21,182:4,160,182:31,-1:5" +
",182:6,-1:21,183:5,117,183:30,-1:5,183:6,-1:21,182:8,116:2,182:26,-1:5,182:" +
"6,-1:21,183:8,119:2,183:26,-1:5,183:6,-1:21,182:6,118:2,182:28,-1:5,182:6,-" +
"1:21,183:6,121:2,183:28,-1:5,183:6,-1:21,182:24,162:2,182:10,-1:5,182:6,-1:" +
"21,183:24,165:2,183:10,-1:5,183:6,-1:21,182:10,120:2,182:24,-1:5,182:6,-1:2" +
"1,183:4,169:2,183:30,-1:5,183:6,-1:21,182:26,122:2,182:8,-1:5,182:6,-1:21,1" +
"83:10,123:2,183:24,-1:5,183:6,-1:21,182:12,166:2,182:22,-1:5,182:6,-1:21,18" +
"3:34,125:2,-1:5,183:6,-1:21,182:8,124:2,182:26,-1:5,182:6,-1:21,183:26,127:" +
"2,183:8,-1:5,183:6,-1:21,182:14,168,182:21,-1:5,182:6,-1:21,183:12,171:2,18" +
"3:22,-1:5,183:6,-1:21,182:26,170:2,182:8,-1:5,182:6,-1:21,183:8,129:2,183:2" +
"6,-1:5,183:6,-1:21,182:10,172:2,182:24,-1:5,182:6,-1:21,183:26,173:2,183:8," +
"-1:5,183:6,-1:21,182:4,126:2,182:30,-1:5,182:6,-1:21,183:10,175:2,183:24,-1" +
":5,183:6,-1:21,182:36,-1:5,182,174,182:4,-1:21,183:8,131:2,183:26,-1:5,183:" +
"6,-1:21,182:12,128:2,182:22,-1:5,182:6,-1:21,183:4,133:2,183:30,-1:5,183:6," +
"-1:21,182:20,176:2,182:14,-1:5,182:6,-1:21,183:12,134:2,183:22,-1:5,183:6,-" +
"1:21,182:22,178,182:13,-1:5,182:6,-1:21,183:20,177:2,183:14,-1:5,183:6,-1:2" +
"1,182:12,180:2,182:22,-1:5,182:6,-1:21,183:12,179:2,183:22,-1:5,183:6,-1:21" +
",182:36,-1:5,182:2,181,182:3,-1:21,183:22,135:2,183:12,-1:5,183:6,-1:21,182" +
":22,130:2,182:12,-1:5,182:6,-1:21,182:30,132,182:5,-1:5,182:6,-1:21,182:10," +
"144,182:25,-1:5,182:6,-1:21,183:11,145,183:24,-1:5,183:6,-1:21,182:4,146:2," +
"182:2,148:2,182:26,-1:5,182:6,-1:21,183:4,147:2,183:2,149:2,183:26,-1:5,183" +
":6,-1:21,182:18,152:2,182:16,-1:5,182:6,-1:21,183:18,155:2,157:2,183:14,-1:" +
"5,183:6,-1:21,182:26,154:2,182:8,-1:5,182:6,-1:21,183:26,159:2,183:8,-1:5,1" +
"83:6,-1:21,182:18,156:2,182:16,-1:5,182:6,-1:21,183:18,161:2,183:16,-1:5,18" +
"3:6,-1:21,182:6,158:2,182:28,-1:5,182:6,-1:21,183:6,163:2,183:28,-1:5,183:6" +
",-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
    case BLOCK_COMMENT_S:
        yybegin(YYINITIAL);
        return new Symbol(
            TokenConstants.ERROR,
            "EOF in comment");
    case STRING_S:
        yybegin(YYINITIAL);
        return new Symbol(
            TokenConstants.ERROR,
            "EOF in string constant");
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(TokenConstants.EQ); }
					case -3:
						break;
					case 3:
						{
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  return new Symbol(
      TokenConstants.ERROR,
      yytext());
}
					case -4:
						break;
					case 4:
						{ return new Symbol(TokenConstants.SEMI); }
					case -5:
						break;
					case 5:
						{ return new Symbol(TokenConstants.COMMA); }
					case -6:
						break;
					case 6:
						{ return new Symbol(TokenConstants.COLON); }
					case -7:
						break;
					case 7:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -11:
						break;
					case 11:
						{ return new Symbol(TokenConstants.DOT); }
					case -12:
						break;
					case 12:
						{ return new Symbol(TokenConstants.NEG); }
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.MULT); }
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.DIV); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.PLUS); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.MINUS); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.LT); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.AT); }
					case -19:
						break;
					case 19:
						{
  // integers
  return new Symbol(
      TokenConstants.INT_CONST,
      AbstractTable.inttable.addString(yytext()));
}
					case -20:
						break;
					case 20:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -21:
						break;
					case 21:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -22:
						break;
					case 22:
						{
  curr_lineno++;
}
					case -23:
						break;
					case 23:
						{
  /* white space
     blank (ascii 32), \n (newline, ascii 10), \f (form feed, ascii 12),
     \r (carriage return, ascii 13), \t (tab, ascii 9), \v (vertical tab, ascii 11)
   */
   // do nothing
}
					case -24:
						break;
					case 24:
						{
  // string begin
  yybegin(STRING_S);
  string_contains_null = false;
  string_buf.delete(0, string_buf.length());
}
					case -25:
						break;
					case 25:
						{
  /* Sample lexical rule for "=>" arrow.
     Further lexical rules should be defined
     here, after the last %% separator */
  return new Symbol(TokenConstants.DARROW); }
					case -26:
						break;
					case 26:
						{
  /* block comment started */
  yybegin(BLOCK_COMMENT_S);
  string_buf.delete(0, string_buf.length());
  inc_block_comment_count();
  string_buf.append(yytext());
}
					case -27:
						break;
					case 27:
						{
  /* unmatched block comment end*/
  return new Symbol(
      TokenConstants.ERROR,
      "unmatched close comment.");
}
					case -28:
						break;
					case 28:
						{
  /* line comment start
   */
   yybegin(LINE_COMMENT_S);
}
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.LE); }
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -31:
						break;
					case 31:
						{
  return new Symbol(TokenConstants.IF);
}
					case -32:
						break;
					case 32:
						{
  return new Symbol(TokenConstants.IN);
}
					case -33:
						break;
					case 33:
						{
  return new Symbol(TokenConstants.FI);
}
					case -34:
						break;
					case 34:
						{
  return new Symbol(TokenConstants.OF);
}
					case -35:
						break;
					case 35:
						{
  return new Symbol(
      TokenConstants.STR_CONST,
      AbstractTable.stringtable.addString(
      yytext().substring(1, yytext().length() - 1)));
}
					case -36:
						break;
					case 36:
						{
  return new Symbol(TokenConstants.LET);
}
					case -37:
						break;
					case 37:
						{
  return new Symbol(TokenConstants.NEW);
}
					case -38:
						break;
					case 38:
						{
  return new Symbol(TokenConstants.NOT);
}
					case -39:
						break;
					case 39:
						{
  return new Symbol(TokenConstants.CASE);
}
					case -40:
						break;
					case 40:
						{
  return new Symbol(TokenConstants.LOOP);
}
					case -41:
						break;
					case 41:
						{
  // self
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -42:
						break;
					case 42:
						{
  return new Symbol(TokenConstants.ELSE);
}
					case -43:
						break;
					case 43:
						{
  return new Symbol(TokenConstants.ESAC);
}
					case -44:
						break;
					case 44:
						{
  return new Symbol(TokenConstants.THEN);
}
					case -45:
						break;
					case 45:
						{
  // boolean true
  return new Symbol(
      TokenConstants.BOOL_CONST,
      java.lang.Boolean.TRUE);
}
					case -46:
						break;
					case 46:
						{
  return new Symbol(TokenConstants.POOL);
}
					case -47:
						break;
					case 47:
						{
  return new Symbol(TokenConstants.CLASS);
}
					case -48:
						break;
					case 48:
						{
  // boolean false
  return new Symbol(
      TokenConstants.BOOL_CONST,
      java.lang.Boolean.FALSE);
}
					case -49:
						break;
					case 49:
						{
  return new Symbol(TokenConstants.WHILE);
}
					case -50:
						break;
					case 50:
						{
  return new Symbol(TokenConstants.ISVOID);
}
					case -51:
						break;
					case 51:
						{
  return new Symbol(TokenConstants.INHERITS);
}
					case -52:
						break;
					case 52:
						{
  // SELF_TYPE
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -53:
						break;
					case 53:
						{
  /* line comment end */
  yybegin(YYINITIAL);
}
					case -54:
						break;
					case 54:
						{
  // not special char
  string_buf.append(yytext());
}
					case -55:
						break;
					case 55:
						{
  // special char
  string_buf.append(yytext());
}
					case -56:
						break;
					case 56:
						{
  // special char \n
  curr_lineno++;
  string_buf.append(yytext());
}
					case -57:
						break;
					case 57:
						{
  // another lay of block comment
  inc_block_comment_count();
  string_buf.append(yytext());
}
					case -58:
						break;
					case 58:
						{
  // end of a lay of comment
  string_buf.append(yytext());
  if (dec_block_comment_count() == 0) {
    yybegin(YYINITIAL);
  }
}
					case -59:
						break;
					case 59:
						{
  // not special char
  string_buf.append(yytext());
}
					case -60:
						break;
					case 60:
						{
  // special char
  string_contains_null = true;
  return new Symbol(
      TokenConstants.ERROR,
      "String contains null character");
}
					case -61:
						break;
					case 61:
						{
  // return line in string, it's an error
  yybegin(YYINITIAL);
  curr_lineno++;
  if (!string_contains_null) {
    return new Symbol(
        TokenConstants.ERROR,
        "String should close with \".");
  }
}
					case -62:
						break;
					case 62:
						{
  // end of string
  yybegin(YYINITIAL);
  if (!string_contains_null) {
    if (string_buf.length() >= MAX_STR_CONST) {
      return new Symbol(
          TokenConstants.ERROR,
          "String constant too long");
    }
    return new Symbol(
        TokenConstants.STR_CONST,
        AbstractTable.stringtable.addString(string_buf.toString()));
  }
}
					case -63:
						break;
					case 63:
						{
  // special char
  string_buf.append(yytext().charAt(1));
}
					case -64:
						break;
					case 64:
						{
  // special char
  string_contains_null = true;
  return new Symbol(
      TokenConstants.ERROR,
      "String contains escaped null character");
}
					case -65:
						break;
					case 65:
						{
  // special char
  string_buf.append("\\");
}
					case -66:
						break;
					case 66:
						{
  // special char
  string_buf.append("\f");
}
					case -67:
						break;
					case 67:
						{
  // special char
  string_buf.append("\n");
}
					case -68:
						break;
					case 68:
						{
  // special char
  string_buf.append("\t");
}
					case -69:
						break;
					case 69:
						{
  // not end
  curr_lineno++;
  string_buf.append("\n");
}
					case -70:
						break;
					case 70:
						{
  // special char
  string_buf.append("\b");
}
					case -71:
						break;
					case 72:
						{
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  return new Symbol(
      TokenConstants.ERROR,
      yytext());
}
					case -72:
						break;
					case 73:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -73:
						break;
					case 74:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -74:
						break;
					case 75:
						{
  return new Symbol(TokenConstants.IF);
}
					case -75:
						break;
					case 76:
						{
  return new Symbol(TokenConstants.IN);
}
					case -76:
						break;
					case 77:
						{
  return new Symbol(TokenConstants.FI);
}
					case -77:
						break;
					case 78:
						{
  return new Symbol(TokenConstants.OF);
}
					case -78:
						break;
					case 79:
						{
  return new Symbol(TokenConstants.LET);
}
					case -79:
						break;
					case 80:
						{
  return new Symbol(TokenConstants.NEW);
}
					case -80:
						break;
					case 81:
						{
  return new Symbol(TokenConstants.NOT);
}
					case -81:
						break;
					case 82:
						{
  return new Symbol(TokenConstants.CASE);
}
					case -82:
						break;
					case 83:
						{
  return new Symbol(TokenConstants.LOOP);
}
					case -83:
						break;
					case 84:
						{
  return new Symbol(TokenConstants.ELSE);
}
					case -84:
						break;
					case 85:
						{
  return new Symbol(TokenConstants.ESAC);
}
					case -85:
						break;
					case 86:
						{
  return new Symbol(TokenConstants.THEN);
}
					case -86:
						break;
					case 87:
						{
  return new Symbol(TokenConstants.POOL);
}
					case -87:
						break;
					case 88:
						{
  return new Symbol(TokenConstants.CLASS);
}
					case -88:
						break;
					case 89:
						{
  return new Symbol(TokenConstants.WHILE);
}
					case -89:
						break;
					case 90:
						{
  return new Symbol(TokenConstants.ISVOID);
}
					case -90:
						break;
					case 91:
						{
  return new Symbol(TokenConstants.INHERITS);
}
					case -91:
						break;
					case 92:
						{
  /* line comment end */
  yybegin(YYINITIAL);
}
					case -92:
						break;
					case 93:
						{
  // special char
  string_buf.append(yytext());
}
					case -93:
						break;
					case 94:
						{
  // not special char
  string_buf.append(yytext());
}
					case -94:
						break;
					case 96:
						{
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  return new Symbol(
      TokenConstants.ERROR,
      yytext());
}
					case -95:
						break;
					case 97:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -96:
						break;
					case 98:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -97:
						break;
					case 99:
						{
  // special char
  string_buf.append(yytext());
}
					case -98:
						break;
					case 101:
						{
  /* This rule should be the very last
     in your lexical specification and
     will match match everything not
     matched by other lexical rules. */
  return new Symbol(
      TokenConstants.ERROR,
      yytext());
}
					case -99:
						break;
					case 102:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -100:
						break;
					case 103:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -101:
						break;
					case 105:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -102:
						break;
					case 106:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -103:
						break;
					case 108:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -104:
						break;
					case 109:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -105:
						break;
					case 110:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -106:
						break;
					case 111:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -107:
						break;
					case 112:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -108:
						break;
					case 113:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -109:
						break;
					case 114:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -110:
						break;
					case 115:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -111:
						break;
					case 116:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -112:
						break;
					case 117:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -113:
						break;
					case 118:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -114:
						break;
					case 119:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -115:
						break;
					case 120:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -116:
						break;
					case 121:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -117:
						break;
					case 122:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -118:
						break;
					case 123:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -119:
						break;
					case 124:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -120:
						break;
					case 125:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -121:
						break;
					case 126:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -122:
						break;
					case 127:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -123:
						break;
					case 128:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -124:
						break;
					case 129:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -125:
						break;
					case 130:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -126:
						break;
					case 131:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -127:
						break;
					case 132:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -128:
						break;
					case 133:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -129:
						break;
					case 134:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -130:
						break;
					case 135:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -131:
						break;
					case 136:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -132:
						break;
					case 137:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -133:
						break;
					case 138:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -134:
						break;
					case 139:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -135:
						break;
					case 140:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -136:
						break;
					case 141:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -137:
						break;
					case 142:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -138:
						break;
					case 143:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -139:
						break;
					case 144:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -140:
						break;
					case 145:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -141:
						break;
					case 146:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -142:
						break;
					case 147:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -143:
						break;
					case 148:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -144:
						break;
					case 149:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -145:
						break;
					case 150:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -146:
						break;
					case 151:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -147:
						break;
					case 152:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -148:
						break;
					case 153:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -149:
						break;
					case 154:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -150:
						break;
					case 155:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -151:
						break;
					case 156:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -152:
						break;
					case 157:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -153:
						break;
					case 158:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -154:
						break;
					case 159:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -155:
						break;
					case 160:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -156:
						break;
					case 161:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -157:
						break;
					case 162:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -158:
						break;
					case 163:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -159:
						break;
					case 164:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -160:
						break;
					case 165:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -161:
						break;
					case 166:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -162:
						break;
					case 167:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -163:
						break;
					case 168:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -164:
						break;
					case 169:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -165:
						break;
					case 170:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -166:
						break;
					case 171:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -167:
						break;
					case 172:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -168:
						break;
					case 173:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -169:
						break;
					case 174:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -170:
						break;
					case 175:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -171:
						break;
					case 176:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -172:
						break;
					case 177:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -173:
						break;
					case 178:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -174:
						break;
					case 179:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -175:
						break;
					case 180:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -176:
						break;
					case 181:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -177:
						break;
					case 182:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -178:
						break;
					case 183:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -179:
						break;
					case 184:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -180:
						break;
					case 185:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -181:
						break;
					case 186:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -182:
						break;
					case 187:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -183:
						break;
					case 188:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -184:
						break;
					case 189:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -185:
						break;
					case 190:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -186:
						break;
					case 191:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -187:
						break;
					case 192:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -188:
						break;
					case 193:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -189:
						break;
					case 194:
						{
  // object id
  return new Symbol(
      TokenConstants.TYPEID,
      AbstractTable.idtable.addString(yytext()));
}
					case -190:
						break;
					case 195:
						{
  // object id
  return new Symbol(
      TokenConstants.OBJECTID,
      AbstractTable.idtable.addString(yytext()));
}
					case -191:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
