package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class MessageSendToClass extends MessageSend {

    Method method;
    private KraClass to;
    private KraClass from;

    public MessageSendToClass(KraClass to, KraClass from, Method method) {
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

    //TODO: Return correct type based on Variable or Method.
    public Type getType() {
        returnull;
        n
    }

    public void genC(PW pw, boolean putParenthesis) {

    }

    public void genKra() {
        //System.out.println("super." + this.method.getName() + "();");
    }

}