package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class ExprList {

    private ArrayList<Expr> exprList;

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add( expr );
    }

    public void genC( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
            e.genC( pw, true );
            if ( --size > 0 )
                pw.print( ", " );
        }
    }

    public ArrayList<Expr> getExprList() {
        return exprList;
    }

}
