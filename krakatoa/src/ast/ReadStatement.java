package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class ReadStatement extends Statement {

    private ExprList readList;

    public ReadStatement( ExprList list ) {
        this.readList = list;
    }

    public ExprList getVarList() {
        return readList;
    }

    public void setVarList( ExprList el ) {
        this.readList = el;
    }

    @Override
    public void genC(PW pw) {
        // TODO Auto-generated method stub

    }
}
