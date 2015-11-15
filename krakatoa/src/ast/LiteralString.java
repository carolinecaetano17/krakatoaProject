package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class LiteralString extends Expr {

    private String literalString;

    public LiteralString( String literalString ) {
        this.literalString = literalString;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( "\"" + literalString + "\"" );
    }

    public Type getType() {
        return Type.stringType;
    }
}
