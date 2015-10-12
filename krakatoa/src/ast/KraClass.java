package ast;

import java.util.ArrayList;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class KraClass extends Type {
    private String name;
    private KraClass superclass;
    private InstanceVariableList instanceVariableList;
    private ArrayList<Method> publicMethodList;
    private ArrayList<Method> privateMethodList;

    private boolean isFinal;
    
    public KraClass(String name){super(name);}

    public KraClass(String name, boolean isFinal) {
        super(name);
        this.publicMethodList = new ArrayList<Method>();
        this.privateMethodList = new ArrayList<Method>();
        this.isFinal = isFinal;
        this.superclass = null;
    }

    public String getCname() {
        return getName();
    }
    // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
    // entre outros m�todos

    public KraClass getSuperclass() {
        return superclass;
    }

    public void setSuperclass(KraClass superclass) {
        this.superclass = superclass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstanceVariableList getInstanceVariableList() {
        return instanceVariableList;
    }

    public void setInstanceVariableList(InstanceVariableList instanceVariableList) {
        this.instanceVariableList = instanceVariableList;
    }

    public ArrayList<Method> getPublicMethodList() {
        return publicMethodList;
    }

    public void setPublicMethodList(ArrayList<Method> publicMethodList) {
        this.publicMethodList = publicMethodList;
    }

    public ArrayList<Method> getPrivateMethodList() {
        return privateMethodList;
    }

    public void setPrivateMethodList(ArrayList<Method> privateMethodList) {
        this.privateMethodList = privateMethodList;
    }

    public void addMethod(Method newMethod) {

        if (newMethod.getQualifier() == "private") {
            this.privateMethodList.add(newMethod);
        } else if (newMethod.getQualifier() == "public") {
            this.publicMethodList.add(newMethod);
        }

    }

    public boolean isFinal() {
        return isFinal;
    }
}
