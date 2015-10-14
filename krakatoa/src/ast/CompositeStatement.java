package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class CompositeStatement extends Statement {
    private ArrayList<Statement> statementList;

    public CompositeStatement(ArrayList<Statement> stmts) {
        this.statementList = stmts;
    }


    public void genC(PW pw) {

    }
}
