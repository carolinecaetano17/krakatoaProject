package ast;

/**
 * Created by hsq on 10/11/15.
 */
public class ReturnStatement extends Statement {
    public ReturnStatement(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void genC(PW pw) {
        this.expr.genC(pw, false);
    }

    public Expr getExpr() {
        return expr;
    }

    private Expr expr;
}
