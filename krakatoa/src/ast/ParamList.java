package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;
import java.util.Iterator;

public class ParamList {

    private ArrayList<Variable> paramList;

    public ParamList() {
        paramList = new ArrayList<Variable>();
    }

    public void addElement( Variable v ) {
        paramList.add( v );
    }

    public Iterator<Variable> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    public ArrayList<Variable> getParamList() {
        return paramList;
    }

    public void genC( PW pw ) {
        for ( Variable v : paramList ) {
            pw.print( ", " );
            pw.print( v.getType().getCname() + " _" + v.getName() );
        }
    }


}
