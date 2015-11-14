package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;
import java.util.Iterator;

public class InstanceVariableList {

    private ArrayList<InstanceVariable> instanceVariableList;

    public InstanceVariableList() {
        instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement( InstanceVariable instanceVariable ) {
        instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
        return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }

    public ArrayList<InstanceVariable> getInstanceVariableList() {
        return instanceVariableList;
    }

    public void setInstanceVariableList(
            ArrayList<InstanceVariable> instanceVariableList ) {
        this.instanceVariableList = instanceVariableList;
    }

    //Join two different lists within InstanceVariableList passed and this one
    public void join( InstanceVariableList list ) {
        this.instanceVariableList.addAll( list.getInstanceVariableList() );
    }

    public void genC( PW pw ) {
        for ( InstanceVariable iv : instanceVariableList )
            iv.genC( pw );
    }


}
