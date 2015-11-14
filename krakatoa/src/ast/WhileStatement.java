package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class WhileStatement extends Statement {
    Expr e;
    Statement stmt;

    public WhileStatement( Expr e, Statement stmt ) {
        this.e = e;
        this.stmt = stmt;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void genC( PW pw ) {
        pw.printIdent( "while " );
        e.genC( pw, true );
        pw.println( "" );
        pw.add();
        stmt.genC( pw );
        pw.sub();
    }
}
