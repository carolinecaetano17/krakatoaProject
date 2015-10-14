package ast;

/**
 * Created by hsq on 10/11/15.
 */
public class CompositeStatement extends Statement {
    public CompositeStatement(Expr compositeExpr) {
        this.compExpr = compositeExpr;
    }

    @Override
    public void genC(PW pw) {
        this.compExpr.genC(pw, false);
    }

    private Expr compExpr;
}
