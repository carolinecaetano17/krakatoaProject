package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class IfStatement extends Statement {
    private Expr e;
    private Statement thenPart, elsePart;

    public IfStatement(Expr e, Statement thenPart, Statement elsePart) {
        this.e = e;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    public void genC(PW pw) {

    }
}