package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageSendToClass extends MessageSend {

    private Method method;
    private KraClass to;
    private KraClass from;
    private String variableName;
    private ExprList exprList;

    public MessageSendToClass( KraClass to, KraClass from, Method method, String variableName, ExprList exprList ) {
        super();
        this.to = to;
        this.from = from;
        this.method = method;
        this.variableName = variableName;
        this.exprList = exprList;
    }

    public Type getType() {
        return method.getType();
    }

    public void genC( PW pw, boolean putParenthesis ) {
        //Caso seja uma chamada para método estático
        if ( method.isStatic() ) {
            if ( putParenthesis ) {
                pw.print( "_static_" + to.getName() + "_" + method.getName() + "( " );

            } else {
                pw.printIdent( "_static_" + to.getName() + "_" + method.getName() + "( " );
            }
            exprList.genC( pw );
            pw.print( " )" );
        } else {

            int programRunFunctionNumber = -1;

            //Gera a hierarquia da classe.
            ArrayList<KraClass> superClassHierarchy = new ArrayList<KraClass>();
            superClassHierarchy.add( to );
            KraClass superClass = to.getSuperclass();
            while ( superClass != null ) {
                superClassHierarchy.add( superClass );
                superClass = superClass.getSuperclass();
            }
            Collections.reverse( superClassHierarchy );

            //Utilizamos a mesma técnica usada em KraClass para construir o vetor de métodos, com algumas modificações
            List<String> availableMethods = new LinkedList<String>();
            for ( KraClass kc : superClassHierarchy ) {
                ArrayList<Method> publicMethods = kc.getPublicMethodList();
                for ( Method m : publicMethods ) {
                    if ( !availableMethods.contains( m.getName() ) )
                        availableMethods.add( m.getName() );
                }
            }
            programRunFunctionNumber = availableMethods.indexOf( method.getName() );

            //Precisamos também da classe que é dona do método
            Collections.reverse( superClassHierarchy );
            for ( KraClass kc : superClassHierarchy ) {
                if ( kc.getPublicMethodList().contains( method ) ) {
                    to = kc;
                    break;
                }
            }

            ParamList methodParameters = method.getParamList();

            //Se o método é público, precisamos fazer chamada dinâmica
            if ( method.getQualifier().equals( "public" ) ) {
                //Convertemos o ponteiro para o tipo correto
                if ( putParenthesis ) {
                    if ( method.getType() instanceof KraClass )
                        pw.print( "( (" + method.getType().getCname() + " * (*)(" + to.getCname() + " *" );
                    else
                        pw.print( "( (" + method.getType().getCname() + " (*)(" + to.getCname() + " *" );
                } else {
                    if ( method.getType() instanceof KraClass )
                        pw.printIdent( "( (" + method.getType().getCname() + " * (*)(" + to.getCname() + " *" );
                    else
                        pw.printIdent( "( (" + method.getType().getCname() + " (*)(" + to.getCname() + " *" );
                }
                //Caso o método tenha parâmetros, precisamos incluir os tipos na conversão
                if ( methodParameters.getSize() != 0 ) {
                    pw.print( "," );
                    methodParameters.genCTypesOnly( pw );
                }
                //Como o primeiro parâmetro é sempre uma variável representando uma classe, precisamos converter para a
                //classe correta
                if ( !variableName.equals( "this" ) ) {
                    pw.print( " )) _" + variableName + "->vt[" + programRunFunctionNumber + "])( " );
                    pw.print( "(" + to.getCname() + " *) " + "_" + variableName );
                }
                //Caso o primeiro parâmetro seja uma variável chamada this. Nesse caso, não colocamos _ antes do nome
                else {
                    pw.print( " )) " + variableName + "->vt[" + programRunFunctionNumber + "])( " );
                    pw.print( "(" + to.getCname() + " *) " + variableName );
                }
                if ( exprList.getExprList().size() != 0 ) {
                    pw.print( ", " );
                }
                exprList.genC( pw );
                pw.print( " )" );
                //Chamada estática para um método privado
            } else {
                if ( putParenthesis ) {
                    pw.print( "_" + to.getName() + "_" + method.getName() + "( " + "(" + to.getCname() + " *)" + " this" );

                } else {
                    pw.printIdent( "_" + to.getName() + "_" + method.getName() + "( " + "(" + to.getCname() + " *)" + " this" );
                }
                if ( exprList.getExprList().size() != 0 )
                    pw.print( ", " );
                exprList.genC( pw );
                pw.print( " )" );
            }
        }
    }
}