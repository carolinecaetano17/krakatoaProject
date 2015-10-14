package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
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
