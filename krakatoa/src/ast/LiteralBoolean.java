package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class LiteralBoolean extends Expr {

    public static LiteralBoolean True = new LiteralBoolean( true );
    public static LiteralBoolean False = new LiteralBoolean( false );
    private boolean value;

    public LiteralBoolean( boolean value ) {
        this.value = value;
    }

    @Override
    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( value ? "true" : "false" );
    }

    @Override
    public Type getType() {
        return Type.booleanType;
    }
}
