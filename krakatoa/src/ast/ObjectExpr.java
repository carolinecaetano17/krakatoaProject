package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class ObjectExpr extends Expr {

    private Type type;

    public ObjectExpr( KraClass classType ) {
        super();
        this.type = classType;
    }

    @Override
    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( "new_" + type.getName() + "()" );
    }

    @Override
    public Type getType() {
        return this.type;
    }

}
