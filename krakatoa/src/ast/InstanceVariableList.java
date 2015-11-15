package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

public class InstanceVariableList {

    private ArrayList<InstanceVariable> instanceVariableList;

    public InstanceVariableList() {
        instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement( InstanceVariable instanceVariable ) {
        instanceVariableList.add( instanceVariable );
    }

    public ArrayList<InstanceVariable> getInstanceVariableList() {
        return instanceVariableList;
    }

    //Join two different lists within InstanceVariableList passed and this one
    public void join( InstanceVariableList list ) {
        this.instanceVariableList.addAll( list.getInstanceVariableList() );
    }

    public void genC( PW pw, String className ) {
        for ( InstanceVariable iv : instanceVariableList )
            iv.genC( pw, className );
    }


}
