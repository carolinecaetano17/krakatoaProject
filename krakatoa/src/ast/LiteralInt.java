package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class LiteralInt extends Expr {

    private int value;

    public LiteralInt( int value ) {
        this.value = value;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( "" + value );
    }

    public Type getType() {
        return Type.intType;
    }
}
