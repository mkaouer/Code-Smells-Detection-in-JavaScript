package ij.macro;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.util.*;

/** This is the recursive descent parser/interpreter for the ImageJ macro language. */
public class Interpreter implements MacroConstants {

	static final int STACK_SIZE=1000;
	static final int MAX_ARGS=20;

	Tokenizer tok;
	int pc;
	int token;
	int tokenAddress;
	double tokenValue;
	String tokenString;
	boolean looseSyntax = true;
	double darg1,darg2,darg3,darg4;
	int lineNumber;
	boolean ignoreEOL = true;
	boolean statusUpdated;
	boolean showingProgress;
	
	Variable[] stack;
	int topOfStack = -1;
	int topOfGlobals = -1;
	int startOfLocals = 0;

	static Interpreter instance;
	boolean done;
	Program pgm;
	Functions func;
	boolean inFunction;
	String macroName;

	/** Interprets the specified string. */
	public void run(String str) {
		Tokenizer tok = new Tokenizer();
		Program pgm = tok.tokenize(str);
		if (pgm.hasVars)
			saveGlobals2(pgm);
		run(pgm);
	}

	/** Interprets the specified tokenized macro file starting at location 0. */
	public void run(Program pgm) {
		this.pgm = pgm;
		pc = -1;
		instance = this;
		pushGlobals();
		if (func==null)
			func = new Functions(this, pgm);
		IJ.showStatus("interpreting");
		if ((pgm.code[pc+1]&0xff)==MACRO && (pgm.code[pc+2]&0xff)==STRING_CONSTANT) {
		   // run macro instead of skipping over it
		   getToken();
		   getToken();
		}
		doStatements();
		func.updateDisplay();
		instance = null;
		if (!statusUpdated) IJ.showStatus("");
		if (showingProgress) IJ.showProgress(0, 0);
	}

	/** Interprets the specified tokenized macro starting at the specified location. */
	public void runMacro(Program pgm, int macroLoc, String macroName) {
		this.pgm = pgm;
		this.macroName = macroName;
		pc = macroLoc-1;
		instance = this;
		IJ.showStatus("interpreting");
		pushGlobals();
		if (func==null)
			func = new Functions(this, pgm);
		if (macroLoc==0)
			doStatements();
		else
			doBlock(); 
		func.updateDisplay();
		instance = null;
		if (!statusUpdated) IJ.showStatus("");
		if (showingProgress) IJ.showProgress(0, 0);
	}
	
	/** Saves global variablesk. */
	public void saveGlobals(Program pgm) {
		saveGlobals2(pgm);
	}
	
	void saveGlobals2(Program pgm) {
		this.pgm = pgm;
		pc = -1;
		instance = this;
		func = new Functions(this, pgm);
		while (!done) {
			getToken();
			switch (token) {
				case VAR: doVar(); break;
				case MACRO: done=true;; break;
				case FUNCTION: done=true; break;
				default:
			}
		}
		instance = null;
		pgm.saveGlobals(this);
		pc = -1;
		topOfStack = -1;
		done = false;
	}

	final void getToken() {
		if (done)
			return;
		token = pgm.code[++pc];
		//IJ.log(pc+" "+pgm.decodeToken(token));
		if (token<=127)
			return;
		while (token==EOL && ignoreEOL)
			token = pgm.code[++pc];
		tokenAddress = token>>16;
		token = token&0xffff;
		Symbol sym = pgm.table[tokenAddress];
		tokenString = sym.str;
		tokenValue = sym.value;
		done = token==EOF;
	}

	final int nextToken() {
		return pgm.code[pc+1]&0xffff;
	}

	final int nextNonEolToken() {
		int tok, i=1;
		do {
			tok = pgm.code[pc+i];
			i++;
		} while (tok==EOL);
		return tok&0xffff;
	}

	final int nextNextNonEolToken() {
		int tok, i=1;
		do tok = pgm.code[pc+(i++)]; while (tok==EOL);
		do tok = pgm.code[pc+(i++)]; while (tok==EOL);
		return tok&0xffff;
	}

	final void putTokenBack() {
		pc--;
		if (pc<0)
			pc = -1;
	}

	void doStatements() {
		while (!done)
			doStatement();
	}

	final void doStatement() {
		getToken();
		switch (token) {
			case VAR:
				doVar();
				break;
			case PREDEFINED_FUNCTION:
				func.doFunction(pgm.table[tokenAddress].type);
				break;
			case USER_FUNCTION:
				runUserFunction();
				break;
			case RETURN:
				doReturn();
				break;
			case WORD:
				doAssignment();
				break;
			case '(': case PLUS_PLUS:
				putTokenBack();
				getAssignmentExpression();
				break;
			case IF:
				doIf();
				return;
			case ELSE:
				error("Else without if");
				return;
			case FOR:
				doFor();
				return;
			case WHILE:
				doWhile();
				return;
			case DO:
				doDo();
				return;
			case MACRO:
				skipMacro();
				return;
			case FUNCTION:
				skipFunction();
				return;
			case ';':
				return;
			case '{':
				putTokenBack();
				doBlock();
				return;
			case NUMBER:
			case NUMERIC_FUNCTION:
			case STRING_FUNCTION:
			case STRING_CONSTANT:
				putTokenBack();
				IJ.log(getString());
				return;
			case EOF: break;
			default:
				error("Statement cannot begin with '"+pgm.decodeToken(token, tokenAddress)+"'");
		}
		if (!looseSyntax) {
			getToken();
			if (token!=';')
				error("';' expected");
		}
	}

	Variable runUserFunction() {
		int newPC = (int)tokenValue;
		int saveStartOfLocals = startOfLocals;
		startOfLocals = topOfStack+1;
		int saveTOS = topOfStack;		
		int nArgs = pushArgs();
		int savePC = pc;
		Variable value = null;
		pc = newPC;
		setupArgs(nArgs);
		boolean saveInFunction = inFunction;
		inFunction = true;
		try {
			doBlock();
		} catch (ReturnException e) {
			value = new Variable(0, e.value, e.str, e.array);
		}
		inFunction = saveInFunction;
		pc = savePC;
		trimStack(saveTOS, saveStartOfLocals);
		return value;
	}

	/** Push function arguments onto the stack. */
	int pushArgs() {
		getLeftParen();
		int count = 0;
		Variable[] args = new Variable[MAX_ARGS];
		double value;
		if (nextToken()!=')') {
			do {
				if (count==MAX_ARGS)
					error("Too many arguments");
				int next = nextToken();
				if (next==STRING_CONSTANT || next==STRING_FUNCTION)
					args[count] = new Variable(0, 0.0, getString());
				else if (next==USER_FUNCTION) {
					int savePC = pc;
					getToken(); // the function
					boolean simpleFunctionCall = isSimpleFunctionCall(false);
					pc = savePC;
					if (simpleFunctionCall) {
						getToken(); // the function
						Variable v2 = runUserFunction();
						if (v2==null)
							error("No return value");
						args[count] = v2;
					} else
						args[count] = new Variable(0, getExpression(), null);	
				} else if (next==WORD && ((pgm.code[pc+2]&0xff)==',' || (pgm.code[pc+2]&0xff)==')')) {
					value = 0.0;
					Variable[] array = null;
					String str = null;
					getToken();
					Variable v = lookupNumericVariable();
					if (v!=null) {
						int type = v.getType();
						if (type==Variable.VALUE)
							value = v.getValue();
						else if (type==Variable.ARRAY)
							array = v.getArray();
						else
							str = v.getString();
					}
					args[count] = new Variable(0, value, str, array);
				} else
					args[count] = new Variable(0, getExpression(), null);
				count++;
				getToken();
			} while (token==',');
			putTokenBack();
		}
		int nArgs = count;
		while(count>0)
			push(args[--count], this);
		getRightParen();
		return nArgs;
	}

	void setupArgs(int nArgs) {
		getLeftParen();
		int i = topOfStack;
		int count = nArgs;
		if (nextToken()!=')') {
			do {
			   getToken();
			   if (i>=0)
				  stack[i].symTabIndex = tokenAddress;
			   i--;
			   count--;
			   getToken();
			} while (token==',');
			putTokenBack();
		}
		if (count!=0)
		   error(nArgs+" argument"+(nArgs==1?"":"s")+" expected");
		getRightParen();
	}
	
	void doReturn() {
		if (inFunction) {
			double value = 0.0;
			String str = null;
			Variable[] array = null;
			getToken();
			if (token!=';') {
				boolean isString = token==STRING_CONSTANT || token==STRING_FUNCTION;
				boolean isArrayFunction = token==ARRAY_FUNCTION;
				if (token==WORD) {
					Variable v = lookupVariable(tokenAddress);
					if (v!=null && nextToken()==';') {
						array = v.getArray();
						isString = v.getString()!=null;
						//IJ.log("token==WORD: "+isString+" "+pgm.decodeToken(token, tokenAddress));
					}
				}
				putTokenBack();
				if (isString)
					str = getString();
				else if (isArrayFunction) {
					getToken();
					array = func.getArrayFunction(pgm.table[tokenAddress].type);
				} else if (array==null)
					value = getExpression();
			}
			throw new ReturnException(value, str, array);
		} else
			error("Return outside of function");
	}
	
	void doFor() {
		boolean saveLooseSyntax = looseSyntax;
		looseSyntax = false;
		getToken();
		if (token!='(')
			error("'(' expected");
		getToken(); // skip 'var'
		if (token!=VAR)
			putTokenBack();
		do {
			if (nextToken()!=';')
			   getAssignmentExpression();
			getToken();
		} while (token==',');
		//IJ.log("token: "+pgm.decodeToken(token,tokenAddress));
		if (token!=';')
			error("';' expected");
		int condPC = pc;
		int incPC2, startPC=0;
		double cond = 1;
		while (true) {
			if (pgm.code[pc+1]!=';')
			   cond = getLogicalExpression();
			if (startPC==0)
				checkBoolean(cond);
			getToken();
			if (token!=';')
				error("';' expected");
			int incPC = pc;
			// skip to start of code
			if (startPC!=0)
				pc = startPC;
			else {
			  while (token!=')') {
				getToken();
				//IJ.log(pgm.decodeToken(token,tokenAddress));
				if (token=='{' || token==';' || token=='(' || done)
					error("')' expected");
			   }
			}
			startPC = pc;
			if (cond==1)
				doStatement();
			else {
				skipStatement();
				break;
			}
			pc = incPC; // do increment
			do {
				 if (nextToken()!=')')
					getAssignmentExpression();
				getToken();
			} while (token==',');
			pc = condPC;
		}
		looseSyntax = saveLooseSyntax;
	}

	void doWhile() {
		looseSyntax = false;
		int savePC = pc;
		boolean isTrue;
		do {
			pc = savePC;
			isTrue = getBoolean();
			if (isTrue)
				doStatement();
			else
				skipStatement();
		} while (isTrue && !done);
	}

	void doDo() {
		looseSyntax = false;
		int savePC = pc;
		boolean isTrue;
		do {
			doStatement();
			getToken();
			if (token!=WHILE)
				error("'while' expected");
			isTrue = getBoolean();
			if (isTrue)
				pc = savePC;
		} while (isTrue && !done);
	}

	final void doBlock() {
		getToken();
		if (token!='{')
			error("'{' expected");
		while (!done) {
			getToken();
			if(token=='}')
				break;
			putTokenBack();
			doStatement();
		}
		if (token!='}')
			error("'}' expected");
	}

	final void skipStatement() {
		getToken();
		//IJ.write("skipStatement: " +pgm.decodeToken(token, tokenAddress));
		switch (token) {
			case PREDEFINED_FUNCTION: case USER_FUNCTION: case VAR:
			case WORD: case '(': case PLUS_PLUS: case RETURN:
				skipSimpleStatement();
				break;
			case IF:
				skipParens();
				skipStatement();
				getToken();
				if (token==ELSE)
					skipStatement();
				else
					putTokenBack();
				break;
			case FOR:
				skipParens();
				skipStatement();
				break;
			case WHILE:
				skipParens();
				skipStatement();
				break;
			case DO:
				skipStatement();
				getToken(); // skip 'while'
				skipParens();
				break;
			case ';':
				break;
			case '{':
				putTokenBack();
				skipBlock();
				break;
			default:
				error("Skipped statement cannot begin with '"+pgm.decodeToken(token, tokenAddress)+"'");
		}
	}

	final void skipBlock() {
		int count = 0;
		do {
			getToken();
			if (token=='{')
				count++;
			else if (token=='}')
				count--;
			else if (done) {
				error("'}' expected");
				return;
			}
		} while (count>0);
	}

	final void skipParens() {
		int count = 0;
		do {
			getToken();
			if (token=='(')
				count++;
			else if (token==')')
				count--;
			else if (done) {
				error("')' expected");
				return;
			}
		} while (count>0);
	}

	final void skipSimpleStatement() {
		boolean finished = done;
		getToken();
		while (!finished && !done) {
			if (token==';')
				finished = true;
			else
				getToken();
		}
	}

	/** Skips a user-defined function. */
	void skipFunction() {
		getToken(); // skip function id
		skipParens();
		skipBlock();
	}

	void skipMacro() {
		getToken(); // skip macro label
		skipBlock();
	}

	final void doAssignment() {
		if ((pgm.code[pc+1]&0xff)=='[')
			{doArrayElementAssignment(); return;}
		int type = getExpressionType();		
		if (type==Variable.STRING)
			doStringAssignment();
		else if (type==Variable.ARRAY)
			doArrayAssignment();
		else if (type==USER_FUNCTION)
			doUserFunctionAssignment();
		else {
			putTokenBack();
			getAssignmentExpression();
		}
	}

	int getExpressionType() {
		int rightSideToken = pgm.code[pc+2];
		int tok = rightSideToken&0xff;
		if (tok==STRING_CONSTANT || tok==STRING_FUNCTION)
			return Variable.STRING;
		if (tok==ARRAY_FUNCTION)
			return Variable.ARRAY;
		if (tok==USER_FUNCTION)
			return USER_FUNCTION;
		if (tok!=WORD)
			return Variable.VALUE;
		Variable v = lookupVariable(rightSideToken>>16);
		if (v==null)
			return Variable.VALUE;
		int type = v.getType();
		if (type!=Variable.ARRAY)
			return type;
		if (pgm.code[pc+3]=='.')
			return Variable.VALUE;		
		if (pgm.code[pc+3]!='[')
			return Variable.ARRAY;
		int savePC = pc;
		getToken(); //"="
		getToken(); //the variable
		int index = getIndex();
		pc = savePC-1;
		getToken();
		Variable[] array = v.getArray();
		if (index<0 || index>=array.length)
			return Variable.VALUE;
		return array[index].getType();
	}
	
	final void doArrayElementAssignment() {
		Variable v = lookupVariable(tokenAddress);
		if (v==null)
				error("Undefined identifier");
		if (pgm.code[pc+5]==';'&&(pgm.code[pc+4]==PLUS_PLUS||pgm.code[pc+4]==MINUS_MINUS))
			{putTokenBack(); getFactor(); return;}
		int index = getIndex();
		int expressionType = getExpressionType();
		getToken();
		int op = token;
		if (!(op=='='||op==PLUS_EQUAL||op==MINUS_EQUAL||op==MUL_EQUAL||op==DIV_EQUAL))
			{error("'=', '+=', '-=', '*=' or '/=' expected"); return;}
		if (op!='=' && (expressionType==Variable.STRING||expressionType==Variable.ARRAY))
			{error("'=' expected"); return;}
		Variable[] array = v.getArray();
		if (array==null)
			error("Array expected");
		if (index<0 || index>=array.length)
			error("Index ("+index+") out of 0-"+(array.length-1)+" range");
		int next = nextToken();
		switch (expressionType) {
			case Variable.STRING:
				array[index].setString(getString());
				break;
			case Variable.ARRAY:
				getToken();
				if (token==ARRAY_FUNCTION)
					array[index].setArray(func.getArrayFunction(pgm.table[tokenAddress].type));
				break;
			default:
				switch (op) {
					case '=': array[index].setValue(getExpression()); break;
					case PLUS_EQUAL: array[index].setValue(array[index].getValue()+getExpression()); break;
					case MINUS_EQUAL: array[index].setValue(array[index].getValue()-getExpression()); break;
					case MUL_EQUAL: array[index].setValue(array[index].getValue()*getExpression()); break;
					case DIV_EQUAL: array[index].setValue(array[index].getValue()/getExpression()); break;
				}
				break;
		}				
	}

	final void doUserFunctionAssignment() {
		//IJ.log("doUserFunctionAssignment0: "+pgm.decodeToken(token, tokenAddress));
		putTokenBack();
		int savePC = pc;
		getToken(); // the variable
		getToken(); // '='
		getToken(); // the function
		boolean simpleAssignment = isSimpleFunctionCall(true);
		pc = savePC;
		getToken();		
		if (!simpleAssignment)
			getAssignmentExpression();
		else {
			Variable v1 = lookupVariable(tokenAddress);
			if (v1==null)
				v1 = push(tokenAddress, 0.0, null, this);
			getToken();
			if (token!='=')
				error("'=' expected");
			getToken(); // the function
			Variable v2 = runUserFunction();
			if (v2==null)
				error("No return value");
			int type = v2.getType();
			if (type==Variable.VALUE)
				v1.setValue(v2.getValue());
			else if (type==Variable.ARRAY)
				v1.setArray(v2.getArray());
			else
				v1.setString(v2.getString());
		}	
	}
	
	boolean isSimpleFunctionCall(boolean assignment) {
		int count = 0;
		do {
			getToken();
			//IJ.log(pgm.decodeToken(token, tokenAddress));
			if (token=='(')
				count++;
			else if (token==')')
				count--;
			else if (done)
				error("')' expected");
		} while (count>0);
		getToken();
		if (assignment)
			return token==';';
		else
			return token==','||token==')';
	}
	
	final void doStringAssignment() {
		Variable v = lookupVariable(tokenAddress);
		if (v==null) {
			if (nextToken()=='=')
				v = push(tokenAddress, 0.0, null, this);
			else
				error("Undefined identifier");
		}
		getToken();
		if (token!='=') {
			error("'=' expected");
			return;
		}
		v.setString(getString());
	}

	final void doArrayAssignment() {
		Variable v = lookupVariable(tokenAddress);
		if (v==null) {
			if (nextToken()=='=')
				v = push(tokenAddress, 0.0, null, this);
			else
				error("Undefined identifier");
		}
		getToken();
		if (token!='=') {
			error("'=' expected");
			return;
		}
		getToken();
		if (token==ARRAY_FUNCTION)
			v.setArray(func.getArrayFunction(pgm.table[tokenAddress].type));
		else if (token==WORD) {
			Variable v2 = lookupVariable(tokenAddress);
			v.setArray(v2.getArray());
		} else
			error("Array expected");
	}

	final void doIf() {
		looseSyntax = false;
		boolean b = getBoolean();
		if (b)
			doStatement();
		else
			skipStatement();
		getToken();
		if (token==ELSE) {
			if (b)
				skipStatement();
			else
				doStatement();
		} else
			putTokenBack();
	}

	final boolean getBoolean() {
		getLeftParen();
		Variable.doHash = true;
		double value = getLogicalExpression();
		Variable.doHash = false;
		checkBoolean(value);
		getRightParen();
		return value==0.0?false:true;
	}

	final double getLogicalExpression() {
		double v1 = getBooleanExpression();
		int next = nextToken();
		if (!(next==LOGICAL_AND || next==LOGICAL_OR))
			return v1;
		checkBoolean(v1);
		getToken();
		int op = token;	
		double v2 = getLogicalExpression();
		checkBoolean(v2);
		if (op==LOGICAL_AND)
			return (int)v1 & (int)v2;
		else if (op==LOGICAL_OR)
			return (int)v1 | (int)v2;
		return v1;
	}

	final double getBooleanExpression() {
		double v1;
		int next = nextToken();
		if (next==STRING_CONSTANT || next==STRING_FUNCTION)
			v1 = new Variable(0, 0.0, getString()).getHashCode();
		else
			v1 = getExpression();
		next = nextToken();
		if (next>=EQ && next<=LTE) {
			getToken();
			int op = token;
			double v2;
			next = nextToken();
			if (next==STRING_CONSTANT || next==STRING_FUNCTION)
				v2 = new Variable(0, 0.0, getString()).getHashCode();
			else
				v2 = getExpression();
			switch (op) {
			case EQ:
				v1 = v1==v2?1.0:0.0;
				break;
			case NEQ:
				v1 = v1!=v2?1.0:0.0;
				break;
			case GT:
				v1 = v1>v2?1.0:0.0;
				break;
			case GTE:
				v1 = v1>=v2?1.0:0.0;
				break;
			case LT:
				v1 = v1<v2?1.0:0.0;
				break;
			case LTE:
				v1 = v1<=v2?1.0:0.0;
				break;
			}
		}
		return v1;
	}

	final double getAssignmentExpression() {
		int tokPlus2 = pgm.code[pc+2];
		if ((pgm.code[pc+1]&0xff)==WORD && (tokPlus2=='='||tokPlus2==PLUS_EQUAL
		||tokPlus2==MINUS_EQUAL||tokPlus2==MUL_EQUAL||tokPlus2==DIV_EQUAL)) {
			getToken();
			Variable v = lookupVariable(tokenAddress);
			if (v==null)
				v = push(tokenAddress, 0.0, null, this);
			getToken();
			double value = 0.0;
			if (token=='=')
				value = getAssignmentExpression();
			else {
				value = v.getValue();
				switch (token) {
					case PLUS_EQUAL: value += getAssignmentExpression(); break;
					case MINUS_EQUAL: value -= getAssignmentExpression(); break;
					case MUL_EQUAL: value *= getAssignmentExpression(); break;
					case DIV_EQUAL: value /= getAssignmentExpression(); break;
				}
			}
			v.setValue(value);
			return value;
		} else
			return getLogicalExpression();
	}

	final void checkBoolean(double value) {
		if (!(value==0.0 || value==1.0))
			error("Boolean expression expected");
	}

	void doVar() {
		getToken();
		while (token==WORD) {
			if (nextToken()=='=')
				doAssignment();
			else {
				Variable v = lookupVariable(tokenAddress);
				if (v==null)
					push(tokenAddress, 0.0, null, this);
			}
			getToken();
			if (token==',')
				getToken();
			else {
				putTokenBack();
				break;
			}
		}
	}

	final void getLeftParen() {
		getToken();
		if (token!='(')
			error("'(' expected");
	}

	final void getRightParen() {
		getToken();
		if (token!=')')
			error("')' expected");
	}

	final void getParens() {
		if (nextToken()=='(') {
			getLeftParen();
			getRightParen();
		}
	}

	final void getComma() {
		getToken();
		if (token!=',') {
			if (looseSyntax)
				putTokenBack();
			else
				error("',' expected");
		}
	}

	void error (String message) {
		boolean showMessage = !done;
		token = EOF;
		tokenString = "";
		IJ.showStatus("");
		if (showingProgress) IJ.showProgress(0, 0);
		Variable.doHash = false;
		if (showMessage) {
			String line = getErrorLine();
			IJ.showMessage("Macro Error", message+" in line "+lineNumber+".\n \n"+line);
			throw new RuntimeException("Macro canceled");
		}
		done = true;
	}

	String getErrorLine() {
		int savePC = pc;
		lineNumber = 1;
		ignoreEOL = false;
		pc = -1;
		int lineStart = -1;
		while(pc<savePC) {
			getToken();
			if (token==EOL) {
				lineNumber++;
				lineStart = pc;
			}
		}
		String line = "";
		pc = lineStart;
		getToken();
		String str;
		double v;
		while (token!=EOL && !done) {
			str = pgm.decodeToken(token, tokenAddress);
			if (pc==savePC)
				str = "<"+str+">";
			line += str+" ";
			getToken();
		}
		return line;
	}

	final String getString() {
		String str = getStringTerm();
		while (true) {
			getToken();
			if (token=='+')
				str += getStringTerm();
			else {
				putTokenBack();
				break;
			}
		};
		return str;
	}

	final String getStringTerm() {
		String str;
		getToken();
		switch (token) {
		case STRING_CONSTANT:
			str = tokenString;
			break;
		case STRING_FUNCTION:
			str = func.getStringFunction(pgm.table[tokenAddress].type);
			break;
		case USER_FUNCTION:
			Variable v = runUserFunction();
			if (v==null)
				error("No return value");
			str = v.getString();
			if (str==null) {
				double value = v.getValue();
				if ((int)value==value)
					str = IJ.d2s(value,0);
				else
					str = ""+value;
			}
			break;
		case WORD:
			str = lookupStringVariable();
			if (str!=null)
				break;
			// else fall through
		default:
			putTokenBack();
			double value = getStringExpression();
			if ((int)value==value)
				str = IJ.d2s(value,0);
			else {
				str = ""+value;
				if (value!=Double.POSITIVE_INFINITY && value!=Double.NEGATIVE_INFINITY
						&& value!=Double.NaN && (str.length()-str.indexOf('.'))>6 && str.indexOf('E')==-1)
					str = IJ.d2s(value, 4);
			}
		}
		return str;
	}

	final boolean isStringFunction() {
		Symbol symbol = pgm.table[tokenAddress];
		return symbol.type==D2S;
	}

	final double getExpression() {
		double value = getTerm();
		int next;
		while (true) {
			next = nextNonEolToken();
			if (next=='+') {
				getToken();
				value += getTerm();
			} else if (next=='-') {
				getToken();
				value -= getTerm();
			} else
				break;
		}
		return value;
	}

	final double getTerm() {
		double value = getFactor();
		boolean done = false;
		int next;
		while (!done) {
			next = nextToken();
			switch (next) {
				case '*': getToken(); value *= getFactor(); break;
				case '/': getToken(); value /= getFactor(); break;
				case '%': getToken(); value %= getFactor(); break;
				case '&': getToken(); value = (int)value&(int)getFactor(); break;
				case '|': getToken(); value = (int)value|(int)getFactor(); break;
				case '^': getToken(); value = (int)value^(int)getFactor(); break;
				case SHIFT_RIGHT: getToken(); value = (int)value>>(int)getFactor(); break;
				case SHIFT_LEFT: getToken(); value = (int)value<<(int)getFactor(); break;
				default: done = true; break;
			}
		}
		return value;
	}

	final double getFactor() {
		double value = 0.0;
		Variable v = null;
		getToken();
		switch (token) {
			case NUMBER:
				value = tokenValue;
				break;
			case NUMERIC_FUNCTION:
				value = func.getFunctionValue(pgm.table[tokenAddress].type);
				break;
			case USER_FUNCTION:
				v = runUserFunction();
				if (v==null)
					error("No return value");
				if (v.getString()!=null)
					error("Numeric return value expected");
				else
					value = v.getValue();
				break;
			case TRUE:
				value = 1.0;
				break;
			case FALSE:
				value = 0.0;
				break;
			case PI:
				value = Math.PI;
				break;
			case WORD:
				v = lookupNumericVariable();
				if (v==null)
					return 0.0;
				int next = nextToken();
				if (next=='[') {
					v = getArrayElement(v);
					value = v.getValue();
					next = nextToken();
				} else if (next=='.') {
					value = getArrayLength(v);
					next = nextToken();
				} else
					value = v.getValue();
				if (!(next==PLUS_PLUS || next==MINUS_MINUS))
					break;
				getToken();
				if (token==PLUS_PLUS)
					v.setValue(v.getValue()+1);
				else
					v.setValue(v.getValue()-1);
				break;
			case (int)'(':
				value = getLogicalExpression();
				getRightParen();
				break;
			case PLUS_PLUS:
				value = getFactor();
				value++;
				//if (v!=null)
				//	v.setValue(value);
				break;
			case MINUS_MINUS:
				value = getFactor();
				value--;
				//if (v!=null)
				//	v.setValue(value);
				break;
			case '!':
				value = getFactor();
				if (value==0.0 || value==1.0) {
					value = value==0.0?1.0:0.0;
				} else
					error("Boolean expected");
				break;
			case '-':
				value = -getFactor();
				break;
			case '~':
				value = ~(int)getFactor();
				break;
			default:
				error("Number or numeric function expected");
		}
		// IJ.log("getFactor: "+value+" "+pgm.decodeToken(preToken,0));
		return value;
	}

	final Variable getArrayElement(Variable v) {
		int index = getIndex();
		Variable[] array = v.getArray();
		if (array==null)
			error("Array expected");
		if (index<0 || index>=array.length)
			error("Index ("+index+") out of 0-"+(array.length-1)+" range");
		return array[index];
	}
	
	final double getArrayLength(Variable v) {
		getToken(); // '.'
		getToken();
		if (!(token==WORD && tokenString.equals("length")))
			error("'length' expected");
		Variable[] array = v.getArray();
		if (array==null)
			error("Array expected");
		return array.length;
	}
	
	final double getStringExpression() {
		double value = getTerm();
		while (true) {
			getToken();
			if (token=='+') {
				getToken();
				if (token==STRING_CONSTANT || token==STRING_FUNCTION) {
					putTokenBack();
					putTokenBack();
					break;
				}
				putTokenBack();
				value += getTerm();
			} else if (token=='-')
				value -= getTerm();
			else {
				putTokenBack();
				break;
			}
		};
		return value;
	}

	final Variable lookupVariable(int symTabAddress) {
		//IJ.log("lookupLocalVariable: "+topOfStack+" "+startOfLocals+" "+topOfGlobals);
		Variable v = null;
		for (int i=topOfStack; i>=startOfLocals; i--) {
			if (stack[i].symTabIndex==symTabAddress) {
				v = stack[i];
				break;
			}
		}
		if (v==null) {
			for (int i=topOfGlobals; i>=0; i--) {
				if (stack[i].symTabIndex==symTabAddress) {
					v = stack[i];
					break;
				}
			}
		}
		return v;
	}

	Variable push(Variable var, Interpreter interp) {
		if (stack==null)
			stack = new Variable[STACK_SIZE];
		if (topOfStack>=(STACK_SIZE-2))
			interp.error("Stack overflow");
		else
			topOfStack++;
		stack[topOfStack] = var;
		return var;
	}

	void pushGlobals() {
		if (pgm.globals==null)
			return;
		if (stack==null)
			stack = new Variable[STACK_SIZE];
		for (int i=0; i<pgm.globals.length; i++) {
			topOfStack++;
			stack[topOfStack] = pgm.globals[i];
		}
		topOfGlobals = topOfStack;
	}

	/** Creates a Variable and pushes it onto the stack. */
	Variable push(int symTabLoc, double value, String str, Interpreter interp) {
		Variable var = new Variable(symTabLoc, value, str);
		if (stack==null)
			stack = new Variable[STACK_SIZE];
		if (topOfStack>=(STACK_SIZE-2))
			interp.error("Stack overflow");
		else
			topOfStack++;
		stack[topOfStack] = var;
		return var;
	}

	void trimStack(int previousTOS, int previousStartOfLocals) {
		for (int i=previousTOS+1; i<=topOfStack; i++)
			stack[i] = null;
		topOfStack = previousTOS;
	    startOfLocals = previousStartOfLocals;
	    //IJ.log("trimStack: "+topOfStack);
	}
	
	final Variable lookupNumericVariable() {
		Variable v = null;
		if (stack==null) {
			undefined();
			return v;
		}
		boolean found = false;
		for (int i=topOfStack; i>=0; i--) {
			v = stack[i];
			if (v.symTabIndex==tokenAddress) {
				found = true;
				break;
			}
		}
		if (!found)
			undefined();
		return v;
	}

	final String lookupStringVariable() {
		if (stack==null) {
			undefined();
			return "";
		}
		boolean found = false;
		String str = null;
		for (int i=topOfStack; i>=0; i--) {
			if (stack[i].symTabIndex==tokenAddress) {
				Variable v = stack[i];
				found = true;
				int next = nextToken();
				if (next=='[') {
					int savePC = pc;
					int index = getIndex();
					Variable[] array = v.getArray();
					if (array==null)
						error("Array expected");
					if (index<0 || index>=array.length)
						error("Index ("+index+") out of 0-"+(array.length-1)+" range");
					str = array[index].getString();
					if (str==null) {
						pc = savePC-1;
						getToken();
					}
				} else {
					if (v.getArray()!=null)
						{getToken(); error("'[' expected");}
					str = v.getString();
				}
				break;
			}
		}
		if (!found)
			undefined();
		return str;
	}

	int getIndex() {
		getToken();
		if (token!='[')
			error("'['expected");
		int index = (int)getExpression();
		getToken();
		if (token!=']')
			error("']' expected");
		return index;
	}
	
	void undefined() {
		if (nextToken()=='(')
			error("Undefined identifier");
		else
			error("Undefined variable");
	}
	
	void dump() {
		getParens();
		if (!done) {
			pgm.dumpSymbolTable();
			pgm.dumpProgram();
			dumpStack();
		}
	}

	void dumpStack() {
		IJ.log("");
		IJ.log("Stack");
		if (stack!=null)
			for (int i=topOfStack; i>=0; i--)
				IJ.log(i+" "+pgm.table[stack[i].symTabIndex].str+" "+stack[i]);
	}
	
	/** Aborts currently running macro. */
	public static void abort() {
		if (instance!=null) {
			instance.done = true;
			IJ.showStatus("Macro aborted");
		}
	}
	
	public static Interpreter getInstance() {
		return instance;
	}

} // class Interpreter





