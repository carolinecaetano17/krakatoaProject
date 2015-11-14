package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class CompositeStatement extends Statement {
    private ArrayList<Statement> statementList;
    private boolean hasBreakStatement;
    private boolean hasVarDeclarations;

    public CompositeStatement( ArrayList<Statement> stmts ) {
        this.statementList = stmts;
        for ( Statement s : this.statementList ) {
            if ( s instanceof BreakStatement )
                this.hasBreakStatement = true;
            else
                this.hasBreakStatement = false;

            if ( s instanceof EmptyStatement )
                this.hasVarDeclarations = true;
            else
                this.hasVarDeclarations = false;
        }
    }

    public boolean isHasBreakStatement() {
        return hasBreakStatement;
    }

    public boolean isHasVarDeclarations() {
        return hasVarDeclarations;
    }

    public void genC( PW pw ) {
        pw.printlnIdent( "{" );
        pw.add();
        for ( Statement s : statementList )
            s.genC( pw );
        pw.sub();
        pw.printlnIdent( "}" );
    }
}
