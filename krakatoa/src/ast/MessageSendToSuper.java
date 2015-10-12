package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class MessageSendToSuper extends MessageSend { 
	
	private KraClass to;
	private KraClass from;
	Method method;
	
	public MessageSendToSuper(KraClass to, KraClass from, Method method) {
		super();
		this.to = to;
		this.from = from;
		this.method = method;
	}

    public KraClass getTo() {
		return to;
	}

	public void setTo(KraClass to) {
		this.to = to;
	}

	public KraClass getFrom() {
		return from;
	}

	public void setFrom(KraClass from) {
		this.from = from;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Type getType() { 
        return null;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public void genKra(){
    	//System.out.println("super." + this.method.getName() + "();");
    }
    
}