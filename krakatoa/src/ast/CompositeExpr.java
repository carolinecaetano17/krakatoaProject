package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import lexer.Symbol;

import java.util.HashMap;


public class CompositeExpr extends Expr {

    private static HashMap<Symbol, String> arrayOper;

    static {
        arrayOper = new HashMap<Symbol, String>();
        arrayOper.put( Symbol.PLUS, "+" );
        arrayOper.put( Symbol.MINUS, "-" );
        arrayOper.put( Symbol.MULT, "*" );
        arrayOper.put( Symbol.DIV, "/" );
        arrayOper.put( Symbol.LT, "<" );
        arrayOper.put( Symbol.LE, "<=" );
        arrayOper.put( Symbol.GT, ">" );
        arrayOper.put( Symbol.GE, ">=" );
        arrayOper.put( Symbol.NEQ, "!=" );
        arrayOper.put( Symbol.EQ, "==" );
        arrayOper.put( Symbol.ASSIGN, "=" );
        arrayOper.put( Symbol.AND, "&&" );
        arrayOper.put( Symbol.OR, "||" );
    }

    private Expr left, right;
    private Symbol oper;
    public CompositeExpr( Expr pleft, Symbol poper, Expr pright ) {
        left = pleft;
        oper = poper;
        right = pright;
    }

    @Override
    public void genC( PW pw, boolean putParenthesis ) {
        if ( putParenthesis ) {
            pw.print( "(" );
            left.genC( pw, true );
        } else {
            left.genC( pw, false );
        }

        String strSymbol = arrayOper.get( oper );
        if ( strSymbol == null )
            pw.println( "internal error in CompositeExpr::genC" );
        else
            pw.print( " " + strSymbol + " " );

        if ( putParenthesis ) {
            right.genC( pw, true );
            pw.print( ")" );
        } else {
            right.genC( pw, false );
            pw.println( ";" );
        }

    }

    @Override
    public Type getType() {
        // left and right must be the same type
        if ( oper == Symbol.EQ || oper == Symbol.NEQ || oper == Symbol.LE || oper == Symbol.LT ||
                oper == Symbol.GE || oper == Symbol.GT )
            return Type.booleanType;
        else if ( oper == Symbol.AND || oper == Symbol.OR )
            return Type.booleanType;
        else
            return Type.intType;
    }
}
