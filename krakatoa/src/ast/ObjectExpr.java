package ast;

public class ObjectExpr extends Expr {
	
	private KraClass classType;
	private Type type;
	
	public ObjectExpr(KraClass classType) {
		super();
		this.classType = classType;
		this.type = new ClassType(classType.getName());
	}

	@Override
	public void genC(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		//Create an object of this.classType
		//System.out.println("new " + this.classType.getName() + "();");

	}

	@Override
	public Type getType() {
		return this.type;
	}
	
	public String getClassType(){
		return this.classType.getCname();
	}

}
