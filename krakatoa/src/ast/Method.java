package ast;

import java.util.ArrayList;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class Method extends ASTNode {
	private Type type;
	private String name;
	private ParamList paramList;
	private ArrayList<Statement> statementList;
	private String qualifier;
	
	public Method(Type type, String name, String qualifier) {
		super();
		this.type = type;
		this.name = name;
		this.paramList = new ParamList();
		this.statementList = new ArrayList<Statement>();
		this.qualifier = qualifier;
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
		return name;
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
	
}
