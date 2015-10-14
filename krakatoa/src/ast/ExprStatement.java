package ast;

/**
 * Created by hsq on 10/11/15.
 */
public class ExprStatement extends Statement {
    public ExprStatement(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void genC(PW pw) {
        this.expr.genC(pw, false);
    }

    private Expr expr;
}
