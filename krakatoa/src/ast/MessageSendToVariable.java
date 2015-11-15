package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class MessageSendToVariable extends MessageSend {
    private KraClass theClass;
    private Variable theVariable;
    private boolean staticVariable;

    public MessageSendToVariable( KraClass c, Variable v ) {
        this.theClass = c;
        this.theVariable = v;
        this.staticVariable = false;
    }

    public MessageSendToVariable( KraClass c, Variable v, boolean staticVariable ) {
        this.theClass = c;
        this.theVariable = v;
        this.staticVariable = staticVariable;
    }

    public Type getType() {
        return theVariable.getType();
    }

    public void genC( PW pw, boolean putParenthesis ) {
        if ( this.staticVariable )
            pw.print( "_static_" + theClass.getName() + "_" + theVariable.getName() );
        else
            pw.print( "this->_" + theClass.getName() + "_" + theVariable.getName() );

    }


}    