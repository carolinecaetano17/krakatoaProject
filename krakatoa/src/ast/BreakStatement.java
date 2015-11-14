package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class BreakStatement extends Statement {
    public void genC( PW pw ) {
        pw.printlnIdent( "break;" );
    }
}
