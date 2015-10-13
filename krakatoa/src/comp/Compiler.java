package comp;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import ast.*;
import lexer.Lexer;
import lexer.Symbol;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Compiler {

    // compile must receive an input with an character less than
    // p_input.lenght

    private KraClass currentClass;
    private Method currentMethod;

    public Program compile(char[] input, PrintWriter outError) {

        ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
        signalError = new SignalError(outError, compilationErrorList);
        symbolTable = new SymbolTable();
        lexer = new Lexer(input, signalError);
        signalError.setLexer(lexer);

        Program program = null;
        lexer.nextToken();
        program = program(compilationErrorList);
        return program;
    }

    /* Program::= { MOCall } ClassDec { ClassDec } */
    private Program program(ArrayList<CompilationError> compilationErrorList) {
        ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
        ArrayList<KraClass> kraClassList = new ArrayList<>();

        Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
        try {
            while (lexer.token == Symbol.MOCall) {
                metaobjectCallList.add(metaobjectCall());
            }

            kraClassList.add(classDec());

            while (lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL)
                kraClassList.add(classDec());

            if (lexer.token != Symbol.EOF) {
                signalError.show("End of file expected");
            }

        } catch (RuntimeException e) {
            // if there was an exception, there is a compilation signalError
        }

        //Checks the program to see if there is a Program class, with a parameterless method run
        for (KraClass kc : kraClassList) {
            if (kc.getName().equals("Program")) {
                ArrayList<Method> methods = kc.getPublicMethodList();
                for (Method m : methods) {
                    if (m.getName().equals("run")) {
                        if (m.getParamList().getSize() == 0) {
                            return program;
                        }
                    }
                }
            }
        }
        signalError.show("No class Program with a public, parameterless method called run found.");
        return program;
    }

    /* MOCall::= â€œ@â€� Id [ â€œ(â€� { MOParam } â€œ)â€� ]
       MOParam::= IntValue | StringValue | Id */
    private MetaobjectCall metaobjectCall() {
        String name = lexer.getMetaobjectName();
        lexer.nextToken();
        ArrayList<Object> metaobjectParamList = new ArrayList<>();
        if (lexer.token == Symbol.LEFTPAR) {
            // metaobject call with parameters
            lexer.nextToken();
            while (lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
                    lexer.token == Symbol.IDENT) {
                switch (lexer.token) {
                    case LITERALINT:
                        metaobjectParamList.add(lexer.getNumberValue());
                        break;
                    case LITERALSTRING:
                        metaobjectParamList.add(lexer.getLiteralStringValue());
                        break;
                    case IDENT:
                        metaobjectParamList.add(lexer.getStringValue());
                        break;
                    default:
                        break;
                }
                lexer.nextToken();
                if (lexer.token == Symbol.COMMA)
                    lexer.nextToken();
                else
                    break;
            }
            if (lexer.token != Symbol.RIGHTPAR)
                signalError.show("')' expected after metaobject call with parameters");
            else
                lexer.nextToken();
        }
        switch (name) {
            case "nce":
                if (metaobjectParamList.size() != 0)
                    signalError.show("Metaobject '@nce' does not take parameters");
                break;
            case "ce":
                if (metaobjectParamList.size() < 2 || metaobjectParamList.size() > 4)
                    signalError.show("Metaobject '@ce' takes two to four parameters");
                if (!(metaobjectParamList.get(0) instanceof Integer))
                    signalError.show("The first parameter of metaobject '@ce' must be an integer");
                if (!(metaobjectParamList.get(1) instanceof String) || !(metaobjectParamList.get(2) instanceof String))
                    signalError.show("The second and third parameters of metaobject '@ce' must be strings");
                if (metaobjectParamList.size() >= 4 && !(metaobjectParamList.get(3) instanceof String))
                    signalError.show("The fourth parameter of metaobject '@ce' must be a string");

                break;
            default:
                signalError.show("Unknown metaobject " + name);
                break;
        }

        return new MetaobjectCall(name, metaobjectParamList);
    }

    /* ClassDec::= ["final"] â€œclassâ€� Id [ â€œextendsâ€� Id ] â€œ{â€� MemberList â€œ}â€�
       MemberList::= { Qualifier Member }
       Qualifier::= [ â€œfinalâ€� ] [ â€œstaticâ€� ] ( â€œprivateâ€� | â€œpublicâ€�)
       Member::= InstVarDec | MethodDec
       */
    private KraClass classDec() {
        if (lexer.token == Symbol.FINAL) {
            isCurrentClassFinal = true;
            lexer.nextToken();
        }

        if (lexer.token != Symbol.CLASS)
            signalError.show("'class' expected");
        lexer.nextToken();

        if (lexer.token != Symbol.IDENT)
            signalError.show(SignalError.ident_expected);
        String className = lexer.getStringValue();

        KraClass helper = symbolTable.getInGlobal(className);
        if (helper != null)
            signalError.show("The class" + className + " already exists!");

        KraClass newClass = new KraClass(className, isCurrentClassFinal);
        this.currentClass = newClass;
        lexer.nextToken();

        if (lexer.token == Symbol.EXTENDS) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT)
                signalError.show(SignalError.ident_expected);
            String superclassName = lexer.getStringValue();

            superClass = symbolTable.getInGlobal(superclassName);
            if (superClass == null)
                signalError.show("Super class " + superclassName + " does not exist");

            if (superClass.isFinal())
                signalError.show("Final class " + superclassName + " cannot be inherited");

            newClass.setSuperclass(superClass);

            lexer.nextToken();
        }

        if (lexer.token != Symbol.LEFTCURBRACKET)
            signalError.show("{ expected", true);
        lexer.nextToken();

        InstanceVariableList instanceVariableList = new InstanceVariableList();

        while (lexer.token == Symbol.PRIVATE ||
                lexer.token == Symbol.PUBLIC ||
                lexer.token == Symbol.FINAL ||
                lexer.token == Symbol.STATIC) {

            Symbol qualifier;

            if (lexer.token == Symbol.FINAL) {
                isCurrentMemberFinal = true;
                lexer.nextToken();
            }
            if (lexer.token == Symbol.STATIC) {
                isCurrentMemberStatic = true;
                lexer.nextToken();
            }

            switch (lexer.token) {
                case PRIVATE:
                    lexer.nextToken();
                    qualifier = Symbol.PRIVATE;
                    break;
                case PUBLIC:
                    lexer.nextToken();
                    qualifier = Symbol.PUBLIC;
                    break;
                default:
                    signalError.show("public or private qualifier expected");
                    qualifier = Symbol.PUBLIC;
            }

            Type t = type();
            if (lexer.token != Symbol.IDENT)
                signalError.show("Identifier expected");

            String name = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token == Symbol.LEFTPAR) {
                if (qualifier == Symbol.PRIVATE && isCurrentMemberFinal)
                    signalError.show("Final method " + name + " must be public.");

                newClass.addMethod(methodDec(t, name, qualifier));
            } else {
                if (qualifier == Symbol.PUBLIC)
                    signalError.show("Instance variables must be private.");

                instanceVariableList.join(instanceVarDec(t, name));
            }

            //Resets the properties for the next member
            isCurrentMemberFinal = isCurrentMemberStatic = false;
        }
        if (lexer.token != Symbol.RIGHTCURBRACKET)
            signalError.show("} expected");
        lexer.nextToken();

        symbolTable.putInGlobal(className, newClass);

        //Clear Instance Variables and Class Methods
        symbolTable.removeInstanceIdents();
        symbolTable.removeMethodIdents();

        //This Class is not the current Class anymore cause its compilation is done
        this.currentClass = null;

        newClass.setInstanceVariableList(instanceVariableList);

        //Reset property for next class
        isCurrentClassFinal = false;
        superClass = null;

        return newClass;
    }

    /* InstVarDec::= Type IdList â€œ;â€�
       IdList::= Id { â€œ,â€� Id } */
    private InstanceVariableList instanceVarDec(Type type, String name) {
        InstanceVariable newVar;

        //Building AST
        InstanceVariableList newVarList = new InstanceVariableList();

        //First variable sent as a parameter
        InstanceVariable helper = (InstanceVariable) symbolTable.getInstanceVar(name);
        if (helper != null) {
            if (helper.getName().equals(name)) {
                if (helper.isStatic() && isCurrentMemberStatic)
                    signalError.show("The static variable " + name + " already exists!");
                else if (!helper.isStatic() && !isCurrentMemberStatic)
                    signalError.show("The variable " + name + " already exists!");
            }
        }

        //Building AST
        newVar = new InstanceVariable(name, type);
        newVar.setIsStatic(isCurrentMemberStatic);
        newVarList.addElement(newVar);
        symbolTable.putInstanceVar(name, newVar);

        if (lexer.token != Symbol.COMMA && lexer.token != Symbol.SEMICOLON)
            signalError.show(", or ; expected.");

        if (lexer.token == Symbol.COMMA)
            lexer.nextToken();

        while (lexer.token == Symbol.IDENT) {
            name = lexer.getStringValue();
            //Checks for instance variables (including checks for static members)
            helper = (InstanceVariable) symbolTable.getInstanceVar(name);
            if (helper != null) {
                if (helper.getName().equals(name)) {
                    if (helper.isStatic() && isCurrentMemberStatic)
                        signalError.show("The static variable " + name + " already exists!");
                    else if (!helper.isStatic() && !isCurrentMemberStatic)
                        signalError.show("The variable " + name + " already exists!");
                }
            }

            //Building AST
            newVar = new InstanceVariable(name, type);
            newVar.setIsStatic(isCurrentMemberStatic);
            newVarList.addElement(newVar);
            symbolTable.putInstanceVar(name, newVar);

            lexer.nextToken();
            if (lexer.token == Symbol.COMMA)
                lexer.nextToken();
        }

        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();

        return newVarList;

    }

    /* MethodDec::= Type Id â€œ(â€� [ FormalParamDec ] â€œ)â€� â€œ{â€� StatementList â€œ}â€� */
    private Method methodDec(Type type, String name, Symbol qualifier) {

        //Check to see if any local methods have already been declared with this name and properties
        Method helper = symbolTable.getMethod(name);
        if (helper != null) {
            if (helper.getName().equals(name)) {
                if (helper.isStatic() && isCurrentMemberStatic)
                    signalError.show("The static method " + name + " already exists!");
                else if (!helper.isStatic() && !isCurrentMemberStatic)
                    signalError.show("The method " + name + " already exists!");
            }
        }

        //If class is declared final, no final methods can be declared
        if (isCurrentClassFinal && isCurrentMemberFinal)
            signalError.show("Cannot declare final methods inside final class.");

        //Checks to see if any super classes have a final method with this name declared
        KraClass superClassHelper = superClass;
        while (superClassHelper != null) {
            ArrayList<Method> superClassMethods = superClassHelper.getPublicMethodList();
            if (superClassMethods != null) {
                for (Method m : superClassMethods) {
                    if (m.getName().equals(name) && m.isFinal()) {
                        signalError.show("Cannot redefine final method " + name + " from superclass " + superClassHelper.getName());
                        break;
                    }
                }
            }
            superClassHelper = superClassHelper.getSuperclass();
        }


        //Build AST
        Method newMethod = new Method(type, name, qualifier.toString(),
                isCurrentMemberStatic, isCurrentMemberFinal);

        this.currentMethod = newMethod;

        lexer.nextToken();

        //Building AST
        if (lexer.token != Symbol.RIGHTPAR) newMethod.setParamList(formalParamDec());
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTCURBRACKET) signalError.show("{ expected");

        lexer.nextToken();

        //Building AST
        newMethod.setStatementList(statementList());

        if (lexer.token != Symbol.RIGHTCURBRACKET) signalError.show("} expected");

        lexer.nextToken();

        //Clear Method Scope (including variables)
        this.currentMethod = null;
        symbolTable.removeLocalIdents();

        symbolTable.putMethod(name, newMethod);
        return newMethod;
    }

    /* LocalDec ::= Type IdList ";" */
    private void localDec() {
        
        Type type = type();
        if (lexer.token != Symbol.IDENT) signalError.show("Identifier expected");

        Variable v = new Variable(lexer.getStringValue(), type);

        //Check if variable was already declared inside the current method
        Variable localVarHelper = symbolTable.getLocalVar(v.getName());
        if (localVarHelper != null)
            signalError.show("Local variable or parameter" + v.getName() + " already declared");
        else {
            //Building AST
            this.currentMethod.addElement(v);
            symbolTable.putLocalVar(v.getName(), v);
        }

        lexer.nextToken();
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT)
                signalError.show("Identifier expected");
            v = new Variable(lexer.getStringValue(), type);

            //Check if variable was already declared inside the current method
            localVarHelper = symbolTable.getLocalVar(v.getName());
            if (localVarHelper != null)
                signalError.show("Local variable or parameter" + v.getName() + " already declared");
            else {
                //Building AST
                this.currentMethod.addElement(v);
                symbolTable.putLocalVar(v.getName(), v);
            }

            lexer.nextToken();
        }

        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
    }

    /* FormalParamDec::= ParamDec { â€œ,â€� ParamDec }
       ParamDec::= Type Id */
    private ParamList formalParamDec() {
        ParamList newParamList = new ParamList();

        //Building AST
        newParamList.addElement(paramDec());

        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            //Building AST
            newParamList.addElement(paramDec());
        }

        return newParamList;
    }

    /* ParamDec ::= Type Id */
    private Parameter paramDec() {
        Type t;
        String name = "";

        t = type();
        if (lexer.token != Symbol.IDENT)
            signalError.show("Identifier expected");
        else name = lexer.getStringValue();

        Variable localVarHelper = symbolTable.getLocalVar(name);
        if (localVarHelper != null)
            signalError.show("Local variable or parameter" + name + " already declared");

        //Building AST
        Parameter newParam = new Parameter(name, t);
        symbolTable.putLocalVar(name, newParam);

        lexer.nextToken();

        return newParam;
    }

    /* Type::= BasicType | Id
       BasicType::= â€œvoidâ€� | â€œintâ€� | â€œbooleanâ€� | â€œStringâ€� */
    private Type type() {
        Type result;

        switch (lexer.token) {
            case VOID:
                result = Type.voidType;
                break;
            case INT:
                result = Type.intType;
                break;
            case BOOLEAN:
                result = Type.booleanType;
                break;
            case STRING:
                result = Type.stringType;
                break;
            case IDENT:
                result = symbolTable.getInGlobal(lexer.getStringValue());
                if (result == null)
                    signalError.show("Identifier " + lexer.getStringValue() + " does not correspond to a class");
                break;
            default:
                signalError.show("Type expected");
                result = Type.undefinedType;
        }
        lexer.nextToken();
        return result;
    }

    /* StatementList::= { Statement } */
    private ArrayList<Statement> statementList() {

        ArrayList<Statement> statementList = new ArrayList<Statement>();
        Statement st;
        Symbol tk;

        // statements always begin with an identifier, if, read, write, ...
        while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET && tk != Symbol.ELSE) {
            st = statement();
            statementList.add(st);
        }

        return statementList;
    }

    private Statement statement() {
        /*
         * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

        Statement st = null;

        switch (lexer.token) {
            case THIS:
            case IDENT:
            case SUPER:
            case INT:
            case BOOLEAN:
            case STRING:
                st = assignExprLocalDec();
                break;
            case RETURN:
            	/*ERROR ALL DOWN*/
                //st = returnStatement();
                break;
            case READ:
                //st = readStatement();
                break;
            case WRITE:
                //st = writeStatement();
                break;
            case WRITELN:
                //st = writelnStatement();
                break;
            case IF:
            	/*ERROR*/
                //st = ifStatement();
                break;
            case BREAK:
                //Returns a BreakStatement that extends Statement
                st = breakStatement();
                break;
            case WHILE:
            	/*ERROR*/
                //st = whileStatement();
                break;
            case SEMICOLON:
                //Returns a NullStatement that extends Statement
                st = nullStatement();
                break;
            case LEFTCURBRACKET:
            	/*ERROR*/
                //st = compositeStatement();
                break;
            default:
                signalError.show("Statement expected");
        }
        return st;
    }

    /* AssignExprLocalDec ::= Expression [ "=" Expression ] | LocalDec
       LocalDec ::= Type IdList ";" */
    /*ERROR*/
    private Statement assignExprLocalDec() {
        if (lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
                || lexer.token == Symbol.STRING ||
                (lexer.token == Symbol.IDENT && isType(lexer.getStringValue()))) {

            // All semantic checks for local declarations are treated in localDec
           localDec();
           return new EmptyStatement();
        } else {
            Expr left, right = null;
            left = expr();
            
            //Assignment statement, do all semantic checks here
            if (lexer.token == Symbol.ASSIGN) {
                lexer.nextToken();
                right = expr();

                //Check if they're both basic types
                if (isBasicType(left.getType()) && left.getType() != right.getType())
                    signalError.show("Trying to assign different basic types.");

                //If not, do class checks
                /*ERROR*/
                /*
                if (left.getType() instanceof ClassType) {
                    Type l = left.getType();
                    Type r = left.getType();
                    //Not the same class, check if subclass
                    if (!l.getName().equals(r.getName())) {
                        KraClass classHelper = symbolTable.getInGlobal(r.getName());
                        boolean isSubClass = false;
                        while (classHelper != null) {
                            classHelper = classHelper.getSuperclass();
                            if (classHelper != null && classHelper.getName().equals(l.getName()))
                                isSubClass = true;
                        }
                        if (!isSubClass)
                            signalError.show("Trying to assign incompatible class types.");
                    }
                }*/

                if (lexer.token != Symbol.SEMICOLON)
                    signalError.show("';' expected", true);
                else
                    lexer.nextToken();
            }
            /*ERROR*/
            //if (right != null)
            	/*ERROR*/
                //return new CompositeStatement(new CompositeExpr(left, Symbol.ASSIGN, right));
            	/*ERROR*/
            //else
            	/*ERROR*/
                //return new ExprStatement(left);
        }
        /*ERROR IT MUST GO*/
        return null;
    }

    private ExprList realParameters() {
        ExprList anExprList = null;

        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        if (startExpr(lexer.token)) anExprList = exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        return anExprList;
    }

    private void whileStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        expr();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        statement();
    }

    private void ifStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        expr();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        statement();
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            statement();
        }
    }

    private void returnStatement() {

        lexer.nextToken();
        expr();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
    }

    private void readStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        while (true) {
            if (lexer.token == Symbol.THIS) {
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) signalError.show(". expected");
                lexer.nextToken();
            }
            if (lexer.token != Symbol.IDENT)
                signalError.show(SignalError.ident_expected);

            String name = lexer.getStringValue();
            lexer.nextToken();
            if (lexer.token == Symbol.COMMA)
                lexer.nextToken();
            else
                break;
        }

        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
    }

    private void writeStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
    }

    private void writelnStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
    }

    private BreakStatement breakStatement() {

        BreakStatement b = new BreakStatement();
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();

        return b;
    }

    private NullStatement nullStatement() {
        NullStatement n = new NullStatement();
        lexer.nextToken();

        return n;
    }

    private void compositeStatement() {

        lexer.nextToken();
        statementList();
        if (lexer.token != Symbol.RIGHTCURBRACKET)
            signalError.show("} expected");
        else
            lexer.nextToken();
    }

    private ExprList exprList() {
        // ExpressionList ::= Expression { "," Expression }

        ExprList anExprList = new ExprList();
        anExprList.addElement(expr());
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            anExprList.addElement(expr());
        }
        return anExprList;
    }

    private Expr expr() {

        Expr left = simpleExpr();
        Symbol op = lexer.token;
        if (op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
                || op == Symbol.LT || op == Symbol.GE || op == Symbol.GT) {
            lexer.nextToken();
            Expr right = simpleExpr();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr simpleExpr() {
        Symbol op;

        Expr left = term();
        while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
                || op == Symbol.OR) {
            lexer.nextToken();
            Expr right = term();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr term() {
        Symbol op;

        Expr left = signalFactor();
        while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
                || op == Symbol.AND) {
            lexer.nextToken();
            Expr right = signalFactor();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr signalFactor() {
        Symbol op;
        if ((op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS) {
            lexer.nextToken();
            return new SignalExpr(op, factor());
        } else
            return factor();
    }

    /*
     * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
     *      ObjectCreation | PrimaryExpr
     *
     * BasicValue ::= IntValue | BooleanValue | StringValue
     * BooleanValue ::=  "true" | "false"
     * ObjectCreation ::= "new" Id "(" ")"
     * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  |
     *                 Id  |
     *                 Id "." Id |
     *                 Id "." Id "(" [ ExpressionList ] ")" |
     *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
     *                 "this" |
     *                 "this" "." Id |
     *                 "this" "." Id "(" [ ExpressionList ] ")"  |
     *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
     */
    private Expr factor() {

        Expr e;
        ExprList exprList;
        String messageName, ident;

        switch (lexer.token) {
            case LITERALINT:
                return literalInt();
            case FALSE:
                lexer.nextToken();
                return LiteralBoolean.False;
            case TRUE:
                lexer.nextToken();
                return LiteralBoolean.True;
            case LITERALSTRING:
                String literalString = lexer.getLiteralStringValue();
                lexer.nextToken();
                return new LiteralString(literalString);
            case LEFTPAR:
                lexer.nextToken();
                e = expr();
                if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
                lexer.nextToken();
                return new ParenthesisExpr(e);
            case NULL:
                lexer.nextToken();
                return new NullExpr();
            case NOT:
                lexer.nextToken();
                e = expr();
                return new UnaryExpr(e, Symbol.NOT);
            // ObjectCreation ::= "new" Id "(" ")"
            case NEW:
                lexer.nextToken();
                if (lexer.token != Symbol.IDENT)
                    signalError.show("Identifier expected");

                String className = lexer.getStringValue();
                
                // encontre a classe className in symbol table KraClass
                KraClass aClass = symbolTable.getInGlobal(className);
                if (aClass == null)
                    signalError.show("Class " + className + " not found.");

                lexer.nextToken();
                if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
                lexer.nextToken();
                if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
                lexer.nextToken();
            /*
             * return an object representing the creation of an object
			 */
                return new ObjectExpr(aClass);
            /*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
            case SUPER:
                // "super" "." Id "(" [ ExpressionList ] ")"
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    signalError.show("'.' expected");
                } else
                    lexer.nextToken();
                if (lexer.token != Symbol.IDENT)
                    signalError.show("Identifier expected");
                else{
	                /*
	                 * para fazer as conferencias semanticas, procure por 'messageName'
	    			 * na superclasse/superclasse da superclasse etc
	    			 */
	                
	                messageName = lexer.getStringValue();
	                
	                boolean find = false;
	                KraClass superHelper = this.currentClass.getSuperclass();
	                ArrayList<Method> privateHelper;
	                ArrayList<Method> publicHelper;
	                Method desiredM = new Method("NotAMethod");
	                KraClass desiredC = new KraClass("NotAKraClass");
	                int i;
	                
	                while(!find && (superHelper != null)){
	                	//Try to find messageName
	                	privateHelper = superHelper.getPrivateMethodList();
	                	publicHelper = superHelper.getPublicMethodList();
	                	
	                	for (Method privateM : privateHelper) {
	                		if(messageName.equals(privateM.getName())){
	                			desiredM = privateM;
	                			desiredC = superHelper;
	                			find = true;
	                			break;
	                		}
	                	}
	                	
	                	if(!find){
	                		for (Method publicM : publicHelper) {
	                    		if(messageName.equals(publicM.getName())){
	                    			desiredM = publicM;
	                    			desiredC = superHelper;
	                    			find = true;
	                    			break;
	                    		}
	                    	}
	                	}
	                	
	                	superHelper = superHelper.getSuperclass();
	                	
	                }
	                
	                if(!find){
	                	signalError.show("Method " + messageName + " was not found!");
	                }
	                
	                lexer.nextToken();
	                exprList = realParameters();
	                
	                if(find){
	                	checkParams(desiredM, exprList);
	                	return new MessageSendToSuper(desiredC,this.currentClass,desiredM);
	                }
                }
                break;
                
            case IDENT:
            /*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

                String firstId = lexer.getStringValue();
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    // Id
                    // retorne um objeto da ASA que representa um identificador
                	Variable helperVar = symbolTable.getLocalVar(firstId);
                	
                	if(helperVar != null){
                		return new IdentifierExpr(helperVar);
                	}else
                		signalError.show("The identifier " + firstId + " was not found!");
                	
                    return null;
                
                    /*GET BACK HERE*/
                } else { // Id "."
                    lexer.nextToken(); // coma o "."
                    if (lexer.token != Symbol.IDENT) {
                        signalError.show("Identifier expected");
                    } else {
                        // Id "." Id
                        lexer.nextToken();
                        ident = lexer.getStringValue();
                        if (lexer.token == Symbol.DOT) {
                            // Id "." Id "." Id "(" [ ExpressionList ] ")"
                        /*
                         * se o compilador permite variaveis estáticas, é possível
						 * ter esta opção, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se variáveis estáticas não estiver nas especifições,
						 * sinalize um erro neste ponto.
						 */
                            lexer.nextToken();
                            if (lexer.token != Symbol.IDENT)
                                signalError.show("Identifier expected");
                            messageName = lexer.getStringValue();
                            lexer.nextToken();
                            exprList = this.realParameters();

                        } else if (lexer.token == Symbol.LEFTPAR) {
                            // Id "." Id "(" [ ExpressionList ] ")"
                            exprList = this.realParameters();
                        /*
                         * para fazer as conferÃªncias semÃ¢nticas, procure por
						 * mÃ©todo 'ident' na classe de 'firstId'
						 */
                        } else {
                            // retorne o objeto da ASA que representa Id "." Id
                        }
                    }
                }
                break;
            case THIS:
            /*
			 * Este 'case THIS:' trata os seguintes casos: 
          	 * PrimaryExpr ::= 
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    // only 'this'
                    // retorne um objeto da ASA que representa 'this'
                    // confira se nÃ£o estamos em um mÃ©todo estÃ¡tico
                    return null;
                } else {
                    lexer.nextToken();
                    if (lexer.token != Symbol.IDENT)
                        signalError.show("Identifier expected");
                    ident = lexer.getStringValue();
                    lexer.nextToken();
                    // jï¿½ analisou "this" "." Id
                    if (lexer.token == Symbol.LEFTPAR) {
                        // "this" "." Id "(" [ ExpressionList ] ")"
                    /*
                     * Confira se a classe corrente possui um mÃ©todo cujo nome Ã©
					 * 'ident' e que pode tomar os parÃ¢metros de ExpressionList
					 */
                        exprList = this.realParameters();
                    } else if (lexer.token == Symbol.DOT) {
                        // "this" "." Id "." Id "(" [ ExpressionList ] ")"
                        lexer.nextToken();
                        if (lexer.token != Symbol.IDENT)
                            signalError.show("Identifier expected");
                        lexer.nextToken();
                        exprList = this.realParameters();
                    } else {
                        // retorne o objeto da ASA que representa "this" "." Id
                    /*
                     * confira se a classe corrente realmente possui uma
					 * variÃ¡vel de instÃ¢ncia 'ident'
					 */
                        return null;
                    }
                }
                break;
            default:
                signalError.show("Expression expected");
        }
        return null;
    }
    
    private void checkParams(Method desiredM, ExprList exprList){
    	int i = 0;
    	ParamList paramList = desiredM.getParamList();
    	ArrayList<Variable> params = paramList.getParamList(); 
    	ArrayList<Expr> exprs = exprList.getExprList();
    	while(i < params.size() && i < exprs.size()){
    		if(params.get(i).getType() != exprs.get(i).getType()){
    			int index = i + 1;
    			signalError.show("Method " + desiredM.getName() + " : the " + index + "th parameter has the wrong type" );
    		}
    		i++;
    	}
    	
    	if(i != params.size() || i != exprs.size()){
    		signalError.show("Method " + desiredM.getName() + " : received the wrong number of parameters");
    	}
    }

    private LiteralInt literalInt() {
        // the number value is stored in lexer.getToken().value as an object of
        // Integer.
        // Method intValue returns that value as an value of type int.
        int value = lexer.getNumberValue();
        lexer.nextToken();
        return new LiteralInt(value);
    }

    private static boolean startExpr(Symbol token) {

        return token == Symbol.FALSE || token == Symbol.TRUE
                || token == Symbol.NOT || token == Symbol.THIS
                || token == Symbol.LITERALINT || token == Symbol.SUPER
                || token == Symbol.LEFTPAR || token == Symbol.NULL
                || token == Symbol.IDENT || token == Symbol.LITERALSTRING;

    }

    private boolean isType(String name) {
        return symbolTable.getInGlobal(name) != null;
    }

    private boolean isBasicType(Type t) {
        return (t == Type.booleanType || t == Type.intType || t == Type.stringType);
    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private SignalError signalError;

    private KraClass superClass = null;
    boolean isCurrentMemberFinal = false;
    boolean isCurrentMemberStatic = false;
    boolean isCurrentClassFinal = false;

}
