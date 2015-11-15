package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class ReturnStatement extends Statement {
    private Expr expr;

    public ReturnStatement( Expr expr ) {
        this.expr = expr;
    }

    @Override
    public void genC( PW pw ) {
        pw.printIdent( "return " );
        this.expr.genC( pw, false );
        pw.println( ";" );
    }

    public Expr getExpr() {
        return expr;
    }
}
