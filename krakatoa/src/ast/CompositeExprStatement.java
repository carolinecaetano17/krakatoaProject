package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class CompositeExprStatement extends Statement {
    public CompositeExprStatement(Expr compositeExpr) {
        this.compExpr = compositeExpr;
    }

    @Override
    public void genC(PW pw) {
        this.compExpr.genC(pw, false);
    }

    private Expr compExpr;
}
