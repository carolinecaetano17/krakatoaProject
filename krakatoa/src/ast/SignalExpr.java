package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import lexer.Symbol;

public class SignalExpr extends Expr {

    private Expr expr;
    private Symbol oper;

    public SignalExpr( Symbol oper, Expr expr ) {
        this.oper = oper;
        this.expr = expr;
    }

    @Override
    public void genC( PW pw, boolean putParenthesis ) {
        if ( putParenthesis )
            pw.print( "(" );
        pw.print( oper == Symbol.PLUS ? "+" : "-" );
        if ( putParenthesis )
            expr.genC( pw, true );
        else
            expr.genC( pw, false );
        if ( putParenthesis )
            pw.print( ")" );
    }

    @Override
    public Type getType() {
        return expr.getType();
    }
}
