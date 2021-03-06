package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class NullExpr extends Expr {

    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( "NULL" );
    }

    public Type getType() {
        return Type.undefinedType;
    }
}