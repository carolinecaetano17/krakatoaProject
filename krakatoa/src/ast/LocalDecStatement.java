package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class LocalDecStatement extends Statement {
    private ExprList localDecs;

    public LocalDecStatement() {
        this.localDecs = null;
    }

    public void genC(PW pw) {

    }

    public ExprList getLocalDecs() {
        return localDecs;
    }

    public void setLocalDecs(ExprList localDecs) {
        this.localDecs = localDecs;
    }
}
