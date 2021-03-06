package ast;

import java.util.ArrayList;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class WriteStatement extends Statement {
    private ExprList exprList;

    public WriteStatement( ExprList list ) {
        this.exprList = list;
    }

    @Override
    public void genC( PW pw ) {
        ArrayList<Expr> expressions = exprList.getExprList();
        for ( Expr e : expressions ) {
            if ( e.getType() == Type.stringType ) {
                pw.printIdent( "puts( " );
                e.genC( pw, true );
                pw.println( " );" );
            } else {
                pw.printIdent( "printf( \"%d \", " );
                e.genC( pw, true );
                pw.println( " );" );
            }
        }
    }
}
