package ast;

import java.util.ArrayList;
import java.util.Objects;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class KraClass extends Type {
    private String name;
    private KraClass superclass;
    private InstanceVariableList instanceVariableList;
    private InstanceVariableList staticVariableList;
    private ArrayList<Method> publicMethodList;
    private ArrayList<Method> privateMethodList;
    private ArrayList<Method> staticMethodList;

    private boolean isFinal;

    public KraClass(String name) {
        super(name);
        this.publicMethodList = new ArrayList<Method>();
        this.privateMethodList = new ArrayList<Method>();
        this.staticMethodList = new ArrayList<Method>();
        this.isFinal = false;
        this.superclass = null;
    }

    public KraClass(String name, boolean isFinal) {
        super(name);
        this.publicMethodList = new ArrayList<Method>();
        this.privateMethodList = new ArrayList<Method>();
        this.staticMethodList = new ArrayList<Method>();
        this.isFinal = isFinal;
        this.superclass = null;
    }

    public String getCname() {
        return getName();
    }
    // métodos públicos get e set para obter e iniciar as variáveis acima,
    // entre outros métodos

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

    public InstanceVariableList getStaticVariableList() {
        return staticVariableList;
    }

    public void setStaticVariableList(InstanceVariableList staticVariableList) {
        this.staticVariableList = staticVariableList;
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

    public ArrayList<Method> getStaticMethodList() {
        return staticMethodList;
    }

    public void setStaticMethodList(ArrayList<Method> staticMethodList) {
        this.staticMethodList = staticMethodList;
    }

    public void addMethod(Method newMethod) {
        if (newMethod.isStatic()) {
            this.staticMethodList.add(newMethod);
        } else if (Objects.equals(newMethod.getQualifier(), "private")) {
            this.privateMethodList.add(newMethod);
        } else if (Objects.equals(newMethod.getQualifier(), "public")) {
            this.publicMethodList.add(newMethod);
        }

    }

    public boolean isFinal() {
        return isFinal;
    }
}
