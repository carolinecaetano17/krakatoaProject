package ast;

import java.util.ArrayList;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
      publicMethodList = new ArrayList<Method>();
      privateMethodList = new ArrayList<Method>();
   }

   public String getCname() {
      return getName();
   }
   
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private ArrayList<Method> publicMethodList;
   private ArrayList<Method> privateMethodList;
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
	public void addMethod(Method newMethod){
		
		if(newMethod.getQualifier() == "private"){
			this.privateMethodList.add(newMethod);
		}else if(newMethod.getQualifier() == "public"){
			this.publicMethodList.add(newMethod);
		}
		
	}
}
