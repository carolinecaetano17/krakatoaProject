package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;
import java.util.Iterator;

public class LocalVariableList extends Statement {

    private ArrayList<Variable> localList;

    public LocalVariableList() {
        localList = new ArrayList<Variable>();
    }

    public void addElement( Variable v ) {
        localList.add( v );
    }

    public Iterator<Variable> elements() {
        return localList.iterator();
    }

    public int getSize() {
        return localList.size();
    }

    public void genC( PW pw ) {
        for ( Variable v : localList ) {
            pw.printIdent( v.getType().getCname() );
            if ( v.getType() instanceof KraClass )
                pw.print( " *" );
            pw.println( " _" + v.getName() + ";" );
        }
    }

    public ArrayList<Variable> getList() {
        return this.localList;
    }

}
