package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import lexer.Symbol;

public class IfStatement extends Statement {
    private Expr e;
    private Statement thenPart, elsePart;

    public IfStatement( Expr e, Statement thenPart, Statement elsePart ) {
        this.e = e;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    public void genC( PW pw ) {
        pw.printIdent( "if (" );

        e.genC( pw, true );
        if ( e instanceof UnaryExpr && (( UnaryExpr ) e).getOp() == Symbol.NOT )
            pw.print( " =" );
        else
            pw.print( " !" );
        pw.print( "= false)" );
        pw.println( "" );
        pw.add();
        thenPart.genC( pw );
        pw.sub();
        if ( elsePart != null ) {
            pw.printlnIdent( "else" );
            pw.add();
            elsePart.genC( pw );
            pw.sub();
        }
    }
}