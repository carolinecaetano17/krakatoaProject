package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class WriteStatement extends Statement {
    private ExprList exprList;

    public WriteStatement(ExprList list) {
        this.exprList = list;
    }

    public ExprList getExprList() {
        return exprList;
    }

    public void setExprList(ExprList exprList) {
        this.exprList = exprList;
    }

    @Override
    public void genC(PW pw) {
        // TODO Auto-generated method stub

    }
}
