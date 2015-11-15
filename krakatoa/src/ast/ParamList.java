package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class ParamList {

    private ArrayList<Variable> paramList;

    public ParamList() {
        paramList = new ArrayList<Variable>();
    }

    public void addElement( Variable v ) {
        paramList.add( v );
    }

    public int getSize() {
        return paramList.size();
    }

    public ArrayList<Variable> getParamList() {
        return paramList;
    }

    public void genC( PW pw, boolean isStatic ) {
        int size = paramList.size();
        for ( Variable v : paramList ) {
            if ( v.getType() instanceof KraClass )
                pw.print( v.getType().getCname() + " * _" + v.getName() );
            else
                pw.print( v.getType().getCname() + " _" + v.getName() );
            if ( --size > 0 )
                pw.print( ", " );
        }
    }

    public void genCTypesOnly( PW pw ) {
        int size = paramList.size();
        for ( Variable v : paramList ) {
            if ( v.getType() instanceof KraClass )
                pw.print( v.getType().getCname() + " *" );
            else
                pw.print( v.getType().getCname() );
            if ( --size > 0 )
                pw.print( ", " );
        }
    }


}
