package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class IdentifierExpr extends Expr {
	
	private Variable localVar;
	
	public IdentifierExpr(Variable localVar) {
		super();
		this.localVar = localVar;
	}
	
	public Variable getLocalVar() {
		return localVar;
	}

	public void setLocalVar(Variable localVar) {
		this.localVar = localVar;
	}
	
	@Override
	public void genC(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getType() {
		return this.localVar.getType();
	}

}