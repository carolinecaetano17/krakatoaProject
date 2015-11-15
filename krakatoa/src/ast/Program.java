package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import comp.CompilationError;

import java.util.ArrayList;

public class Program extends ASTNode {

    ArrayList<CompilationError> compilationErrorList;
    private ArrayList<KraClass> classList;
    private ArrayList<MetaobjectCall> metaobjectCallList;

    public Program( ArrayList<KraClass> classList, ArrayList<MetaobjectCall> metaobjectCallList,
                    ArrayList<CompilationError> compilationErrorList ) {
        this.classList = classList;
        this.metaobjectCallList = metaobjectCallList;
        this.compilationErrorList = compilationErrorList;
    }

    public void genC( PW pw ) {
        int programRunFunctionNumber = -1;

        //Declara headers necessários para a tradução
        pw.println( "#include <malloc.h>" );
        pw.println( "#include <stdlib.h>" );
        pw.println( "#include <stdio.h>" );
        pw.println( "" );
        pw.println( "typedef int boolean;" );
        pw.println( "#define true 1" );
        pw.println( "#define false 0" );
        pw.println( "" );
        pw.println( "typedef void (*Func)();" );
        pw.println( "" );

        //Gera o código de todas as classes
        for ( KraClass kc : classList ) {
            //Usado para pegar o índice correto do método run
            if ( kc.getName().equals( "Program" ) ) {
                ArrayList<Method> methods = kc.getPublicMethodList();
                for ( Method m : methods ) {
                    if ( m.getName().equals( "run" ) )
                        programRunFunctionNumber = methods.indexOf( m );

                }
            }
            kc.genC( pw, false );
            pw.println( "" );
        }

        //Gera o método principal
        pw.println( "int main () {" );
        pw.add();
        pw.printlnIdent( "_class_Program *program;" );
        pw.printlnIdent( "" );
        pw.printlnIdent( "program = new_Program();" );
        pw.printlnIdent( "( ( void (*)(_class_Program *) ) program->vt[" + programRunFunctionNumber + "] )(program);" );
        pw.printlnIdent( "return 0;" );
        pw.sub();
        pw.println( "}" );
    }

    public ArrayList<MetaobjectCall> getMetaobjectCallList() {
        return metaobjectCallList;
    }

    public boolean hasCompilationErrors() {
        return compilationErrorList != null && compilationErrorList.size() > 0;
    }

    public ArrayList<CompilationError> getCompilationErrorList() {
        return compilationErrorList;
    }

}