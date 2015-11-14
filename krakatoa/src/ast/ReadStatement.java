package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class ReadStatement extends Statement {

    private ExprList readList;

    public ReadStatement( ExprList list ) {
        this.readList = list;
    }

    public ExprList getVarList() {
        return readList;
    }

    public void setVarList( ExprList el ) {
        this.readList = el;
    }

    @Override
    public void genC( PW pw ) {
        ArrayList<Expr> expressions = readList.getExprList();
        for ( Expr e : expressions ) {
            if ( e.getType() == Type.stringType ) {
                pw.printlnIdent( "{" );
                pw.add();
                pw.printlnIdent( "char __s[512];" );
                pw.printlnIdent( "gets(__s);" );
                pw.printIdent( "" );
                e.genC( pw, false );
                pw.println( " = malloc(strlen(__s) + 1);" );
                pw.printIdent( "strcpy( _" );
                e.genC( pw, true );
                pw.println( ", __s);" );
                pw.sub();
                pw.printlnIdent( "}" );
            } else {
                pw.printlnIdent( "{" );
                pw.add();
                pw.printlnIdent( "char __s[512];" );
                pw.printlnIdent( "gets(__s);" );
                pw.printIdent( "sscanf(__s, \"%d\", &_" );
                e.genC( pw, true );
                pw.println( ");" );
                pw.sub();
                pw.printlnIdent( "}" );
            }
        }
    }
}
