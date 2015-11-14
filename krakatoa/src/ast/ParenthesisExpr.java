package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class ParenthesisExpr extends Expr {

    private Expr expr;

    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( "(" );
        expr.genC( pw, true );
        pw.printIdent( ")" );
    }

    public Type getType() {
        return expr.getType();
    }
}