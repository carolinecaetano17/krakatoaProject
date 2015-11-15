package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class Method extends ASTNode {
    private boolean isStatic, isFinal;
    private Type type;
    private String name;
    private ParamList paramList;
    private ArrayList<Statement> statementList;
    private String qualifier;
    private LocalVariableList variableList;

    public Method( String name ) {
        this.name = name;
    }

    public Method( Type type, String name, String qualifier, boolean isStatic, boolean isFinal ) {
        super();
        this.type = type;
        this.name = name;
        this.paramList = new ParamList();
        this.statementList = new ArrayList<Statement>();
        this.qualifier = qualifier;
        this.variableList = new LocalVariableList();
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public String getQualifier() {
        return qualifier;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public ParamList getParamList() {
        return paramList;
    }

    public void setParamList( ParamList paramList ) {
        this.paramList = paramList;
    }

    public ArrayList<Statement> getStatementList() {
        return statementList;
    }

    public void setStatementList( ArrayList<Statement> statementList ) {
        this.statementList = statementList;
    }

    public void addElement( Variable v ) {
        this.variableList.addElement( v );
    }

    public int getParamListSize() {
        return this.paramList.getSize();
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void genC( PW pw, KraClass declaringClass ) {
        if ( isStatic ) {
            if ( type instanceof KraClass )
                pw.print( type.getCname() + " * _static_" + declaringClass.getName() + "_" + this.getName() + "( " );
            else
                pw.print( type.getCname() + " _static_" + declaringClass.getName() + "_" + this.getName() + "( " );
        } else if ( type instanceof KraClass )
            pw.print( type.getCname() + " * _" + declaringClass.getName() + "_" + this.getName() + "( " + declaringClass.getCname() + " *this " );
        else
            pw.print( type.getCname() + " _" + declaringClass.getName() + "_" + this.getName() + "( " + declaringClass.getCname() + " *this " );
        if ( paramList.getSize() != 0 ) {
            if ( !isStatic )
                pw.print( ", " );
            paramList.genC( pw, isStatic );
        }
        pw.println( " )" );
        pw.println( "{" );
        pw.add();
        this.variableList.genC( pw );
        pw.println( "" );
        for ( Statement st : statementList )
            st.genC( pw );
        pw.println( "" );
        pw.sub();
        pw.printlnIdent( "}" );
    }

}
