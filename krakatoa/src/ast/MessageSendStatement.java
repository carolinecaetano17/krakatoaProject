package ast;

public class MessageSendStatement extends Statement {

    public MessageSendStatement(MessageSend message) {
        this.messageSend = message;
    }


    public void genC(PW pw) {
        pw.printIdent("");
        messageSend.genC(pw, false);
        pw.println(";");
    }

    private MessageSend messageSend;

}


