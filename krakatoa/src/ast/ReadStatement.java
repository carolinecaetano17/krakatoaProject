package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class ReadStatement extends Statement {

    private ArrayList<Variable> varList;

    public ReadStatement(ArrayList<Variable> list) {
        this.varList = list;
    }

    public ArrayList<Variable> getVarList() {
        return varList;
    }

    public void setVarList(ArrayList<Variable> varList) {
        this.varList = varList;
    }

    @Override
    public void genC(PW pw) {
        // TODO Auto-generated method stub

    }
}
