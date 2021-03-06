package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import lexer.Symbol;

public class UnaryExpr extends Expr {

    private Expr expr;
    private Symbol op;

    public UnaryExpr( Expr expr, Symbol op ) {
        this.expr = expr;
        this.op = op;
    }

    public Symbol getOp() {
        return op;
    }

    @Override
    public void genC( PW pw, boolean putParenthesis ) {
        switch ( op ) {
            case PLUS:
                pw.print( "+" );
                break;
            case MINUS:
                pw.print( "-" );
                break;
            case NOT:
                if ( !putParenthesis )
                    pw.print( "!" );
                break;
            default:
                pw.print( " internal error at UnaryExpr::genC" );

        }
        expr.genC( pw, false );
    }

    @Override
    public Type getType() {
        return expr.getType();
    }
}
