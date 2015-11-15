package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class MessageSendToSuper extends MessageSend {

    private Method method;
    private KraClass to;
    private KraClass from;
    private String variableName;
    private ExprList exprList;

    public MessageSendToSuper( KraClass to, KraClass from, Method method, String variableName, ExprList exprList ) {
        super();
        this.to = to;
        this.from = from;
        this.method = method;
        this.variableName = variableName;
        this.exprList = exprList;
    }

    //TODO: Return correct type based on Variable or Method.
    public Type getType() {
        return method.getType();
    }

    public void genC( PW pw, boolean putParenthesis ) {
        int programRunFunctionNumber = to.getPublicMethodList().indexOf( method );
        ParamList methodParameters = method.getParamList();

        if ( putParenthesis )
            pw.print( "_" + to.getName() + "_" + method.getName() + "( " + "(" + to.getCname() + " *)" + " this" );
        else
            pw.printIdent( "_" + to.getName() + "_" + method.getName() + "( " + "(" + to.getCname() + " *)" + " this" );
        if ( exprList.getExprList().size() != 0 )
            pw.print( ", " );
        exprList.genC( pw );
        pw.print( " )" );
    }
}