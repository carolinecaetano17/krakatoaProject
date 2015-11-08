package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
import java.util.ArrayList;
import java.util.Iterator;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class Method extends ASTNode {
    boolean isStatic, isFinal;
    private Type type;
    private String name;
    private ParamList paramList;
    private ArrayList<Statement> statementList;
    private String qualifier;
    private LocalVariableList variableList;
    public Method(String name){ this.name = name;}
    
    public Method(Type type, String name, String qualifier, boolean isStatic, boolean isFinal) {
        super();
        this.type = type;
        this.name = name;
        this.paramList = new ParamList();
        this.statementList = new ArrayList<Statement>();
        this.qualifier = qualifier;
        this.variableList = new LocalVariableList();
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParamList getParamList() {
        return paramList;
    }

    public void setParamList(ParamList paramList) {
        this.paramList = paramList;
    }

    public ArrayList<Statement> getStatementList() {
        return statementList;
    }

    public void setStatementList(ArrayList<Statement> statementList) {
        this.statementList = statementList;
    }

    public void addElement(Variable v) {
        this.variableList.addElement(v);
    }

    public Iterator<Variable> elements() {
        return this.variableList.elements();
    }

    public int getSize() {
        return this.variableList.getSize();
    }
    
    public int getParamListSize() {
        return this.paramList.getSize();
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean varExist(Variable v) {
        if (this.variableList.getList().contains(v)) {
            return true;
        }
        return false;
    }

}
