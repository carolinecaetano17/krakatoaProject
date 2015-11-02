package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class MessageSendToVariable extends MessageSend {
    private KraClass theClass;
    private Variable theVariable;

    public MessageSendToVariable(KraClass c, Variable v) {
        this.theClass = c;
        this.theVariable = v;
    }

    public Type getType() {
        return theVariable.getType();
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        
    }

    
}    