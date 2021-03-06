package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.io.PrintWriter;


public class PW extends ASTNode {

    static final private String space = "                                                                                                        ";
    public int step = 3;
    int currentIndent = 0;
    private PrintWriter out;

    public void add() {
        currentIndent += step;
    }

    public void sub() {
        currentIndent -= step;
    }

    public void set( PrintWriter out ) {
        this.out = out;
        currentIndent = 0;
    }

    public void set( int indent ) {
        currentIndent = indent;
    }

    public void printIdent( String s ) {
        out.print( space.substring( 0, currentIndent ) );
        out.print( s );
    }

    public void printlnIdent( String s ) {
        out.print( space.substring( 0, currentIndent ) );
        out.println( s );
    }

    public void print( String s ) {
        out.print( s );
    }

    public void println( String s ) {
        out.println( s );
    }

}
      
       
