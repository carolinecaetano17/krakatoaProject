package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
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
