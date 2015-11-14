package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class CompositeExprStatement extends Statement {
    private Expr compExpr;

    public CompositeExprStatement( Expr compositeExpr ) {
        this.compExpr = compositeExpr;
    }

    @Override
    public void genC( PW pw ) {
        pw.printIdent( "" );
        this.compExpr.genC( pw, false );
    }
}
