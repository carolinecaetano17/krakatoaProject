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

    boolean isCurrentMemberFinal = false;
    boolean isCurrentMemberStatic = false;
    boolean isCurrentClassFinal = false;
    private KraClass currentClass;
    private Method currentMethod;
    private SymbolTable symbolTable;
    private Lexer lexer;
    private SignalError signalError;
    private KraClass superClass = null;
    private ArrayList<Statement> currentMethodReturnStatementList = new ArrayList<Statement>();

    private static boolean startExpr(Symbol token) {

        return token == Symbol.FALSE || token == Symbol.TRUE
                || token == Symbol.NOT || token == Symbol.THIS
                || token == Symbol.LITERALINT || token == Symbol.SUPER
                || token == Symbol.LEFTPAR || token == Symbol.NULL
                || token == Symbol.IDENT || token == Symbol.LITERALSTRING;

    }

    // compile must receive an input with an character less than
    // p_input.lenght
    public Program compile(char[] input, PrintWriter outError) {

        ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
        signalError = new SignalError(outError, compilationErrorList);
        symbolTable = new SymbolTable();
        lexer = new Lexer(input, signalError);
        signalError.setLexer(lexer);

        Program program;
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

            KraClass aux = classDec();
            kraClassList.add(aux);

            while (lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL)
                kraClassList.add(classDec());

            if (lexer.token != Symbol.EOF) {
                signalError.show("End of file expected");
            }

            //Checks the program to see if there is a Program class, with a parameterless method run
            for (KraClass kc : kraClassList) {
                if (kc.getName().equals("Program")) {
                    ArrayList<Method> methods = kc.getPublicMethodList();
                    for (Method m : methods) {
                        if (m.getName().equals("run")) {
                            if (m.getParamListSize() == 0) {
                                return program;
                            }
                        }
                    }
                }
            }

            signalError.show("No class Program with a public, parameterless method called run found.");

        } catch (Exception e) {
            // if there was an exception, there is a compilation signalError
        }

        return program;
    }

    /* MOCall::= "@" Id [ "(" { MOParam } ")" ]
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

    /* ClassDec::= ["final"] "class" Id [ "extends" Id ] "{" MemberList "}"
       MemberList::= { Qualifier Member }
       Qualifier::= [ "final" ] [ "static" ] ( "private" | "public")
       Member::= InstVarDec | MethodDec */
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
        symbolTable.putInGlobal(className, newClass);

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
        InstanceVariableList staticVariableList = new InstanceVariableList();

        while (lexer.token == Symbol.PRIVATE ||
                lexer.token == Symbol.PUBLIC ||
                lexer.token == Symbol.FINAL ||
                lexer.token == Symbol.STATIC) {

            String qualifier;

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
                    qualifier = "private";
                    break;
                case PUBLIC:
                    lexer.nextToken();
                    qualifier = "public";
                    break;
                default:
                    signalError.show("public or private qualifier expected");
                    qualifier = "public";
            }

            Type t = type();
            if (lexer.token != Symbol.IDENT)
                signalError.show("Identifier expected");

            String name = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token == Symbol.LEFTPAR) {
                if (qualifier.equals("private") && isCurrentMemberFinal)
                    signalError.show("Final method " + name + " must be public.");
                newClass.addMethod(methodDec(t, name, qualifier));
            } else {
                if (qualifier.equals("public"))
                    signalError.show("Instance variables must be private.");
                if (t == Type.voidType)
                    signalError.show("Variables cannot be of type void.");

                if (isCurrentMemberStatic)
                    staticVariableList.join(instanceVarDec(t, name));
                else
                    instanceVariableList.join(instanceVarDec(t, name));
            }

            //Resets the properties for the next member
            isCurrentMemberFinal = isCurrentMemberStatic = false;
        }
        if (lexer.token != Symbol.RIGHTCURBRACKET)
            signalError.show("} expected");
        lexer.nextToken();

        //Clear Instance Variables and Class Methods
        symbolTable.removeInstanceIdents();
        symbolTable.removeMethodIdents();

        //Class compilation done
        this.currentClass = null;

        newClass.setInstanceVariableList(instanceVariableList);
        newClass.setStaticVariableList(staticVariableList);

        //Reset property for next class
        isCurrentClassFinal = false;
        superClass = null;

        return newClass;

    }

    /* InstVarDec::= Type IdList ";"
       IdList::= Id { "," Id } */
    private InstanceVariableList instanceVarDec(Type type, String name) {
        InstanceVariable newVar;

        //Building AST
        InstanceVariableList newVarList = new InstanceVariableList();
        InstanceVariable helper;

        //First variable sent as a parameter
        if (isCurrentMemberStatic)
            helper = (InstanceVariable) symbolTable.getStaticVar(name);
        else
            helper = (InstanceVariable) symbolTable.getInstanceVar(name);

        if (helper != null) {
            if (helper.getName().equals(name)) {
                if (helper.isStatic())
                    signalError.show("The static variable " + name + " already exists!");
                else
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
            if (isCurrentMemberStatic)
                helper = (InstanceVariable) symbolTable.getStaticVar(name);
            else
                helper = (InstanceVariable) symbolTable.getInstanceVar(name);

            if (helper != null) {
                if (helper.getName().equals(name)) {
                    if (helper.isStatic())
                        signalError.show("The static variable " + name + " already exists!");
                    else
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

    /* MethodDec::= Type Id "(" [ FormalParamDec ] ")" "{" StatementList "}" */
    private Method methodDec(Type type, String name, String qualifier) {

        //Check to see if any local methods have already been declared with this name and properties
        Method helper;

        boolean returnStatementFound;

        if (isCurrentMemberStatic)
            helper = symbolTable.getStaticMethod(name);
        else
            helper = symbolTable.getMethod(name);

        if (helper != null) {
            if (helper.getName().equals(name)) {
                if (helper.isStatic())
                    signalError.show("The static method " + name + " already exists!");
                else
                    signalError.show("The method " + name + " already exists!");
            }
        }

        //Checa se o nome do método conflita com nome de variável de instância
        if (symbolTable.getInstanceVar(name) != null)
            signalError.show("Cannot declare method " + name + " with same name as instance var.");

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
        Method newMethod = new Method(type, name, qualifier,
                isCurrentMemberStatic, isCurrentMemberFinal);

        this.currentMethod = newMethod;

        symbolTable.putMethod(name, newMethod);

        lexer.nextToken();

        //Building AST
        if (lexer.token != Symbol.RIGHTPAR) newMethod.setParamList(formalParamDec());
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTCURBRACKET) signalError.show("{ expected");

        lexer.nextToken();

        //Building AST
        newMethod.setStatementList(statementList());

        returnStatementFound = false;

        //Check statements for a return statement
        for (Statement s : currentMethodReturnStatementList) {
            Expr returnExpression = ((ReturnStatement) s).getExpr();

            if (currentMethod.getType() == Type.voidType)
                signalError.show("Void methods cannot have return statements.");
                //Check if they're both basic types
            else if (isBasicType(returnExpression.getType()) && returnExpression.getType() != currentMethod.getType())
                signalError.show("Trying to return different basic types.");
                //If not, do class checks
            else if (returnExpression.getType() instanceof KraClass) {
                KraClass returnClass = (KraClass) returnExpression.getType();
                KraClass methodReturnClass = (KraClass) currentMethod.getType();

                boolean isSubClass = false;

                while (returnClass != null) {
                    if (returnClass.getName().equals(methodReturnClass.getName()))
                        isSubClass = true;
                    returnClass = returnClass.getSuperclass();
                }

                if (!isSubClass)
                    signalError.show("Trying to return incompatible class types.");

            }
            returnStatementFound = true;
        }
        if (currentMethod.getType() != Type.voidType && !returnStatementFound) {
            signalError.show("Missing return statement in method " + currentMethod.getName());
        }

        if (lexer.token != Symbol.RIGHTCURBRACKET) signalError.show("} expected");

        lexer.nextToken();

        //Clear Method Scope (including variables and return statements list)
        this.currentMethod = null;
        symbolTable.removeLocalIdents();
        currentMethodReturnStatementList.clear();
        return newMethod;
    }

    /* LocalDec ::= Type IdList ";" */
    private void localDec() {

        Type type = type();
        if (type == Type.voidType)
            signalError.show("Variables cannot be of type void.");

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

    /* FormalParamDec::= ParamDec { "," ParamDec }
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
        if (t == Type.voidType)
            signalError.show("Parameters cannot be of type void.");
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
       BasicType::= "void" | "int" | "boolean" | "String" */
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
                String className = lexer.getStringValue();
                if (className.equals(currentClass.getName()))
                    result = currentClass;
                else
                    result = symbolTable.getInGlobal(className);
                if (result == null)
                    signalError.show("Identifier " + className + " does not correspond to a class");
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

        ArrayList<Statement> statementList = new ArrayList<>();
        Statement st;
        Symbol tk;

        if (lexer.token == Symbol.RIGHTCURBRACKET) {
            st = new NullStatement();
            statementList.add(st);
            return statementList;
        }

        // statements always begin with an identifier, if, read, write, ...
        while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET && tk != Symbol.ELSE) {
            st = statement();
            statementList.add(st);
        }

        return statementList;
    }

    /* Statement ::= Assignment ";'' | IfStat | WhileStat | MessageSend
     ";'' | ReturnStat ";'' | ReadStat ";'' | WriteStat ";'' |
     "break'' ";'' | ";'' | CompStatement | LocalDec */
    private Statement statement() {

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
                lexer.nextToken();
                st = returnStatement();
                currentMethodReturnStatementList.add(st);
                break;
            case READ:
                lexer.nextToken();
                st = readStatement();
                break;
            case WRITE:
                lexer.nextToken();
                st = writeStatement();
                break;
            case WRITELN:
                lexer.nextToken();
                st = writelnStatement();
                break;
            case IF:
                lexer.nextToken();
                st = ifStatement();
                break;
            case BREAK:
                st = breakStatement();
                break;
            case WHILE:
                lexer.nextToken();
                st = whileStatement();
                break;
            case SEMICOLON:
                st = nullStatement();
                break;
            case LEFTCURBRACKET:
                lexer.nextToken();
                st = compositeStatement();
                break;
            default:
                signalError.show("Statement expected");
        }
        return st;
    }

    /* AssignExprLocalDec ::= Expression [ "=" Expression ] | LocalDec
       LocalDec ::= Type IdList ";" */
    private Statement assignExprLocalDec() {

        //Tipos Básicos
        if (lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
                || lexer.token == Symbol.STRING) {

            // All semantic checks for local declarations are treated in localDec
            localDec();
            return new EmptyStatement();
        }
        //Ident seguido de Ident
        else if ((lexer.token == Symbol.IDENT && lexer.peek() == Symbol.IDENT && isClassType(lexer.getStringValue()))) {
            // All semantic checks for local declarations are treated in localDec
            localDec();
            return new EmptyStatement();
        }

        Expr left, right = null;
        left = expr();

        //Assignment statement, do all semantic checks here
        if (lexer.token == Symbol.ASSIGN) {
            lexer.nextToken();
            right = expr();

            //Check if they're both basic types
            if (isBasicType(left.getType()) && left.getType() != right.getType())
                signalError.show("Trying to assign different basic types.");

            //Check if left expression is a class and right is a basic type
            if (!isBasicType(left.getType()) && isBasicType(right.getType()))
                signalError.show("Trying to assign basic type to class.");

            //Check if left expression is object creation
            if (left instanceof ObjectExpr)
                signalError.show("Left part of assignment cannot be object creation");

            //If not, do class checks
            if (left.getType() instanceof KraClass && !(right instanceof NullExpr)) {
                KraClass receivingClass = (KraClass) left.getType();
                KraClass assignmentClass = (KraClass) right.getType();

                boolean isSubClass = false;

                while (assignmentClass != null) {
                    if (assignmentClass.getName().equals(receivingClass.getName()))
                        isSubClass = true;
                    assignmentClass = assignmentClass.getSuperclass();
                }

                if (!isSubClass)
                    signalError.show("Trying to return incompatible class types.");

            }

            if (lexer.token != Symbol.SEMICOLON)
                signalError.show("';' expected", true);
            else
                lexer.nextToken();
        }

        if (left instanceof MessageSendToClass && left.getType() != Type.voidType) {
            signalError.show("Message send returns a value that is not used");
        }

        if (left instanceof MessageSendToSuper && left.getType() != Type.voidType) {
            signalError.show("Message send returns a value that is not used");
        }

        if (right != null)
            return new CompositeExprStatement(new CompositeExpr(left, Symbol.ASSIGN, right));
        else
            return new ExprStatement(left);

    }

    private ExprList messageSendParameters() {
        ExprList anExprList = new ExprList();

        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        if (startExpr(lexer.token)) anExprList = exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        return anExprList;
    }

    private Statement whileStatement() {

        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        Expr e = expr();
        if (e.getType() != Type.booleanType) signalError.show("Boolean expression expected");
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        Statement s = statement();

        if (s instanceof CompositeStatement && ((CompositeStatement) s).isHasVarDeclarations())
            signalError.show("Trying to declare variables inside while");

        return new WhileStatement(e, s);
    }

    private Statement ifStatement() {

        Statement elsePart = null;

        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        Expr e = expr();
        if (e.getType() != Type.booleanType) signalError.show("Boolean expression expected");
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
        lexer.nextToken();
        Statement ifPart = statement();
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            elsePart = statement();
        }

        //Check to see if there is a break statement inside if
        if (ifPart instanceof BreakStatement) signalError.show("Break statement inside if");
        if (elsePart != null && elsePart instanceof BreakStatement) signalError.show("Break statement inside else");

        //Check to see if there is a break, or variable declarations inside if and else
        if (ifPart instanceof CompositeStatement) {
            if (((CompositeStatement) ifPart).isHasBreakStatement()) signalError.show("Break statement inside if");
            if (((CompositeStatement) ifPart).isHasVarDeclarations())
                signalError.show("Trying to declare variables inside if");
        }

        if (elsePart != null && elsePart instanceof CompositeStatement) {
            if (((CompositeStatement) elsePart).isHasBreakStatement()) signalError.show("Break statement inside else");
            if (((CompositeStatement) elsePart).isHasVarDeclarations())
                signalError.show("Trying to declare variables inside else");
        }

        return new IfStatement(e, ifPart, elsePart);
    }

    private Statement returnStatement() {

        ReturnStatement rs = new ReturnStatement(expr());
        if (rs.getExpr().getType() == Type.voidType)
            signalError.show("Cannot return a void expression.");
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
        return rs;
    }

    private ReadStatement readStatement() {
        ArrayList<Variable> varList = new ArrayList<>();
        boolean isInstance = false;
        Variable varHelper;

        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        while (true) {
            if (lexer.token == Symbol.THIS) {
                //It's an instance variable
                isInstance = true;
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) signalError.show(". expected");
                lexer.nextToken();
            }
            if (lexer.token != Symbol.IDENT)
                signalError.show(SignalError.ident_expected);

            String name = lexer.getStringValue();

            if (isInstance) {
                //It's an instance variable
                varHelper = symbolTable.getInstanceVar(name);
            } else {
                //It's a local variable
                varHelper = symbolTable.getLocalVar(name);
            }

            if (varHelper == null)
                signalError.show("Variable " + name + " does not exist");
            else {
                varList.add(varHelper);
            }

            lexer.nextToken();
            if (lexer.token == Symbol.COMMA)
                lexer.nextToken();
            else
                break;

            isInstance = false;
        }

        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");

        for (Variable v : varList) {
            if (v.getType() != Type.intType && v.getType() != Type.stringType)
                signalError.show("Read Statement only accepts expressions of type Int or String");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();

        return new ReadStatement(varList);
    }

    private WriteStatement writeStatement() {

        ExprList exprList;
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        exprList = exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");


        for (Expr e : exprList.getExprList()) {
            if (e.getType() != Type.intType && e.getType() != Type.stringType)
                signalError.show("Write Statement only accepts expressions of type Int or String");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON)
            signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
        return new WriteStatement(exprList);
    }

    private WritelnStatement writelnStatement() {
        ExprList exprList;
        if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
        lexer.nextToken();
        exprList = exprList();
        if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");

        for (Expr e : exprList.getExprList()) {
            if (e.getType() != Type.intType && e.getType() != Type.stringType)
                signalError.show("Writeln Statement only accepts expressions of type Int or String");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) signalError.show(SignalError.semicolon_expected);
        lexer.nextToken();
        return new WritelnStatement(exprList);

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
        lexer.nextToken();
        return new NullStatement();
    }

    private Statement compositeStatement() {
        CompositeStatement cs = new CompositeStatement(statementList());
        if (lexer.token != Symbol.RIGHTCURBRACKET)
            signalError.show("} expected");
        else
            lexer.nextToken();

        return cs;
    }

    /* ExpressionList ::= Expression { "," Expression } */
    private ExprList exprList() {
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
        String messageName;

        boolean methodFound;
        ArrayList<Method> publicMethods;
        ArrayList<Method> privateMethods;
        Method desiredMethod = new Method("nullMethod");
        KraClass desiredClass = new KraClass("nullClass");


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
            case NEW:
                lexer.nextToken();
                if (lexer.token != Symbol.IDENT)
                    signalError.show("Identifier expected");

                KraClass createdClass;
                String className = lexer.getStringValue();
                if (className.equals(currentClass.getName()))
                    createdClass = currentClass;
                else
                    createdClass = symbolTable.getInGlobal(className);
                if (createdClass == null)
                    signalError.show("Class " + className + " not found.");

                lexer.nextToken();
                if (lexer.token != Symbol.LEFTPAR) signalError.show("( expected");
                lexer.nextToken();
                if (lexer.token != Symbol.RIGHTPAR) signalError.show(") expected");
                lexer.nextToken();
                return new ObjectExpr(createdClass);
            case SUPER:
                /*
                 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"
                 */
                if (currentMethod.isStatic()) {
                    signalError.show("super cannot be used inside a static method");
                    return null;
                }

                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    signalError.show("'.' expected");
                    return null;
                }
                lexer.nextToken();
                if (lexer.token != Symbol.IDENT) {
                    signalError.show("Identifier expected");
                    return null;
                }

                /*
                 * para fazer as conferencias semanticas, procure por 'messageName'
                 * na superclasse/superclasse da superclasse etc
                 */
                messageName = lexer.getStringValue();

                methodFound = false;
                KraClass superClass = this.currentClass.getSuperclass();

                while (!methodFound && superClass != null) {
                    //Try to find messageName
                    publicMethods = superClass.getPublicMethodList();
                    for (Method m : publicMethods) {
                        if (messageName.equals(m.getName())) {
                            desiredMethod = m;
                            desiredClass = superClass;
                            methodFound = true;
                        }
                    }
                    superClass = superClass.getSuperclass();
                }

                lexer.nextToken();
                exprList = messageSendParameters();

                if (!methodFound) {
                    signalError.show("Method " + messageName + " was not found!");
                } else if (desiredMethod.isStatic()) {
                    signalError.show("Static methods cannot be called through super.");
                } else {
                    checkParams(desiredMethod, exprList);
                    return new MessageSendToSuper(desiredClass, this.currentClass, desiredMethod);
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
                    Variable localVariable = symbolTable.getLocalVar(firstId);
                    if (localVariable == null) {
                        signalError.show("The identifier " + firstId + " was not found!");
                    }
                    return new IdentifierExpr(localVariable);
                } else { // Id "."
                    lexer.nextToken(); // coma o "."
                    if (lexer.token != Symbol.IDENT) {
                        signalError.show("Identifier expected");
                    } else {
                        // Id "." Id
                        // Chamada à variável, variável static ou a método
                        KraClass calledClass;

                        //Checa se há variável local com esse nome
                        Variable localVar = symbolTable.getLocalVar(firstId);

                        if (localVar != null && localVar.getType() instanceof KraClass) {
                            calledClass = (KraClass) localVar.getType();
                        } else {
                            //Chamada a um método estático ou var estática da própria classe
                            if (firstId.equals(currentClass.getName()))
                                calledClass = currentClass;
                                //Chamada a método estático de outra classe
                            else
                                calledClass = symbolTable.getInGlobal(firstId);
                        }

                        if (calledClass == null) {
                            signalError.show("Identifier " + firstId + " not found.");
                            return null;
                        }

                        String secondId = lexer.getStringValue();
                        lexer.nextToken();

                        // Id "." Id "." Id "(" [ ExpressionList ] ")"
                        if (lexer.token == Symbol.DOT) {
                            /*
                             * se o compilador permite variaveis estáticas, é possível
                             * ter esta opção, como
                             *     Clock.currentDay.setDay(12);
                             * Contudo, se variáveis estáticas não estiver nas especificações,
                             * sinalize um erro neste ponto.
                             */

                            if (!calledClass.getName().equals(currentClass.getName()))
                                signalError.show("Cannot access static variables of another class.");

                            Variable staticVariable = symbolTable.getStaticVar(secondId);

                            if (staticVariable == null) {
                                signalError.show("Static variable " + secondId + " not found.");
                                return null;
                            } else if (!isClassType(staticVariable.getType().getName())) {
                                signalError.show("Message must be sent to a class.");
                                return null;
                            }

                            lexer.nextToken();
                            if (lexer.token != Symbol.IDENT)
                                signalError.show("Identifier expected.");

                            String thirdId = lexer.getStringValue();

                            KraClass staticClass = (KraClass) staticVariable.getType();

                            methodFound = false;

                            //First search that class' static methods
                            ArrayList<Method> staticMethods = staticClass.getStaticMethodList();
                            for (Method m : staticMethods) {
                                if (secondId.equals(m.getName()) && m.getQualifier().equals("public")) {
                                    desiredMethod = m;
                                    desiredClass = calledClass;
                                    methodFound = true;
                                }
                            }

                            while (!methodFound && staticClass != null) {
                                //Try to find messageName
                                publicMethods = staticClass.getPublicMethodList();
                                for (Method m : publicMethods) {
                                    if (thirdId.equals(m.getName())) {
                                        desiredMethod = m;
                                        desiredClass = staticClass;
                                        methodFound = true;
                                    }
                                }
                                staticClass = staticClass.getSuperclass();
                            }

                            lexer.nextToken();
                            exprList = messageSendParameters();

                            if (!methodFound) {
                                signalError.show("Method " + thirdId + " was not found!");
                                return null;
                            } else {
                                checkParams(desiredMethod, exprList);
                                return new MessageSendToClass(desiredClass, this.currentClass, desiredMethod);
                            }


                        } else if (lexer.token == Symbol.LEFTPAR) {
                            // Id "." Id "(" [ ExpressionList ] ")"
                            exprList = messageSendParameters();

                            methodFound = false;

                            //Se não foi achada variável de nome firstId, estamos fazendo chamada a método estático
                            if (localVar == null)
                                publicMethods = calledClass.getStaticMethodList();
                            else
                                publicMethods = calledClass.getPublicMethodList();

                            if (publicMethods == null) {
                                signalError.show("Class " + calledClass.getName() + " does not have public methods");
                                return null;
                            }

                            //Se é chamada de método estático, procure somente na classe especificada
                            if (localVar == null) {
                                //Se for um método estático da classe atual, precisamos procurar na symbolTable
                                if (calledClass.getName().equals(currentClass.getName())) {
                                    desiredMethod = symbolTable.getStaticMethod(secondId);
                                    if (desiredMethod != null) {
                                        desiredClass = calledClass;
                                        methodFound = true;
                                    }
                                }
                                //Caso contrário, procure pelo método na classe específica
                                else {
                                    for (Method m : publicMethods) {
                                        if (secondId.equals(m.getName())) {
                                            desiredMethod = m;
                                            desiredClass = calledClass;
                                            methodFound = true;
                                        }
                                    }
                                }
                            }
                            //Se não for chamada a método estático, temos chamada de método na classe atual
                            else if (calledClass.getName().equals(currentClass.getName())) {
                                //Primeiro procuramos nos métodos da própria classe (chamada recursiva)
                                desiredMethod = symbolTable.getMethod(secondId);
                                if (desiredMethod != null) {
                                    desiredClass = calledClass;
                                    methodFound = true;
                                }
                                //Se não acharmos, procuramos nas superclasses
                                if (!methodFound) {
                                    calledClass = calledClass.getSuperclass();
                                    while (!methodFound && calledClass != null) {
                                        //Try to find messageName
                                        publicMethods = calledClass.getPublicMethodList();
                                        for (Method m : publicMethods) {
                                            if (secondId.equals(m.getName())) {
                                                desiredMethod = m;
                                                desiredClass = calledClass;
                                                methodFound = true;
                                            }
                                        }
                                    }
                                }
                            }
                            //Se tudo falhar, estamos chamando um método não estático de alguma classe
                            else {
                                while (!methodFound && calledClass != null) {
                                    //Try to find messageName
                                    publicMethods = calledClass.getPublicMethodList();
                                    for (Method m : publicMethods) {
                                        if (secondId.equals(m.getName())) {
                                            desiredMethod = m;
                                            desiredClass = calledClass;
                                            methodFound = true;
                                        }
                                    }
                                    calledClass = calledClass.getSuperclass();
                                }
                            }

                            if (!methodFound) {
                                signalError.show("Method " + secondId + " was not found!");
                                return null;
                            } else {
                                checkParams(desiredMethod, exprList);
                                return new MessageSendToClass(desiredClass, this.currentClass, desiredMethod);
                            }

                        } else {
                            // retorne o objeto da ASA que representa Id "." Id
                            // ou seja, variável estática da própria classe.
                            if (!calledClass.getName().equals(currentClass.getName())) {
                                signalError.show("Cannot access static variables of other class");
                            }
                            InstanceVariableList currentClassStaticVariables = calledClass.getStaticVariableList();
                            if (currentClassStaticVariables.getInstanceVariableList() == null) {
                                signalError.show("Class " + firstId + " does not have static variables.");
                                return null;
                            }
                            for (Variable v : currentClassStaticVariables.getInstanceVariableList()) {
                                if (v.getName().equals(secondId))
                                    return new MessageSendToVariable(currentClass, v);
                            }
                            signalError.show("Identifier " + secondId + " not found.");
                            return null;
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
                if (currentMethod.isStatic()) {
                    signalError.show("this cannot be used inside a static method");
                    return null;
                }

                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    return new VariableExpr(new Variable(currentClass.getName(), currentClass));
                } else {
                    lexer.nextToken();
                    if (lexer.token != Symbol.IDENT)
                        signalError.show("Identifier expected");
                    firstId = lexer.getStringValue();
                    lexer.nextToken();
                    // já analisou "this" "." Id
                    if (lexer.token == Symbol.LEFTPAR) {
                        // "this" "." Id "(" [ ExpressionList ] ")"
                        /*
                         * Confira se a classe corrente possui um método cujo nome é
                         * 'ident' e que pode tomar os parâmetros de ExpressionList
                         */
                        exprList = messageSendParameters();

                        methodFound = false;

                        //First search on current class private methods
                        privateMethods = currentClass.getPrivateMethodList();
                        if (privateMethods == null) {
                            signalError.show("Class " + currentClass.getName() + " does not have private methods");
                            return null;
                        }

                        for (Method m : privateMethods) {
                            if (firstId.equals(m.getName())) {
                                desiredMethod = m;
                                desiredClass = currentClass;
                                methodFound = true;
                            }
                        }

                        //Then search in its public methods
                        if (!methodFound) {
                            publicMethods = currentClass.getPublicMethodList();
                            if (publicMethods == null) {
                                signalError.show("Class " + currentClass.getName() + " does not have public methods");
                                return null;
                            }

                            for (Method m : publicMethods) {
                                if (firstId.equals(m.getName()) && m.getQualifier().equals("public")) {
                                    desiredMethod = m;
                                    desiredClass = currentClass;
                                    methodFound = true;
                                }
                            }
                        }

                        //Finally search its superclasses
                        KraClass thisSuperClass = currentClass.getSuperclass();
                        while (!methodFound && thisSuperClass != null) {
                            //Try to find messageName
                            publicMethods = thisSuperClass.getPublicMethodList();
                            for (Method m : publicMethods) {
                                if (firstId.equals(m.getName())) {
                                    desiredMethod = m;
                                    desiredClass = thisSuperClass;
                                    methodFound = true;
                                }
                            }
                            thisSuperClass = thisSuperClass.getSuperclass();
                        }

                        if (!methodFound) {
                            signalError.show("Method " + firstId + " was not found!");
                            return null;
                        } else {
                            checkParams(desiredMethod, exprList);
                            return new MessageSendToClass(desiredClass, this.currentClass, desiredMethod);
                        }
                    } else if (lexer.token == Symbol.DOT) {
                        // "this" "." Id "." Id "(" [ ExpressionList ] ")"
                        lexer.nextToken();
                        if (lexer.token != Symbol.IDENT)
                            signalError.show("Identifier expected");
                        String secondId = lexer.getStringValue();

                        Variable calledVariable = symbolTable.getInstanceVar(firstId);
                        if (calledVariable == null) {
                            signalError.show("Variable " + firstId + " not found.");
                            return null;
                        }
                        if (!(calledVariable.getType() instanceof KraClass)) {
                            signalError.show("Variable must be a class to receive messages.");
                        }
                        KraClass calledClass = (KraClass) calledVariable.getType();

                        methodFound = false;
                        //Search for the desired method
                        while (!methodFound && calledClass != null) {
                            //Try to find messageName
                            publicMethods = calledClass.getPublicMethodList();
                            for (Method m : publicMethods) {
                                if (secondId.equals(m.getName())) {
                                    desiredMethod = m;
                                    desiredClass = calledClass;
                                    methodFound = true;
                                }
                            }
                            calledClass = calledClass.getSuperclass();
                        }

                        lexer.nextToken();
                        exprList = this.messageSendParameters();

                        if (!methodFound) {
                            signalError.show("Method " + secondId + " was not found!");
                            return null;
                        } else {
                            checkParams(desiredMethod, exprList);
                            return new MessageSendToClass(desiredClass, this.currentClass, desiredMethod);
                        }
                    } else {
                        Variable messageReceptor = symbolTable.getInstanceVar(firstId);
                        if (messageReceptor != null) {
                            return new MessageSendToVariable(currentClass, messageReceptor);
                        } else {
                            signalError.show("Identifier " + firstId + " not found.");
                            return null;
                        }
                    }
                }
            default:
                signalError.show("Expression expected");
                break;
        }
        return null;
    }

    private void checkParams(Method desiredM, ExprList exprList) {
        int i = 0;
        ParamList paramList = desiredM.getParamList();
        ArrayList<Variable> params = paramList.getParamList();
        ArrayList<Expr> exprs = exprList.getExprList();
        while (i < params.size() && i < exprs.size()) {
            if (params.get(i).getType() == Type.voidType)
                signalError.show("Parameters cannot be of type void.");
            else if (isBasicType(params.get(i).getType()) && params.get(i).getType() != exprs.get(i).getType())
                signalError.show("Trying to assign different basic types on method " + desiredM.getName() + ".");
            else if (params.get(i).getType() instanceof KraClass) {
                KraClass parameterReturnClass = (KraClass) params.get(i).getType();
                KraClass expressionReturnClass = (KraClass) exprs.get(i).getType();

                boolean isSubClass = false;

                while (expressionReturnClass != null) {
                    if (expressionReturnClass.getName().equals(parameterReturnClass.getName()))
                        isSubClass = true;
                    expressionReturnClass = expressionReturnClass.getSuperclass();
                }

                if (!isSubClass)
                    signalError.show("Trying to assign incompatible class types on method " + desiredM.getName() + ".");

            }
            i++;
        }

        if (i != params.size() || i != exprs.size()) {
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

    private boolean isClassType(String name) {
        return symbolTable.getInGlobal(name) != null;
    }

    private boolean isBasicType(Type t) {
        return (t == Type.booleanType || t == Type.intType || t == Type.stringType);
    }

}
