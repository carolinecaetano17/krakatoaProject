package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class KraClass extends Type {
    private KraClass superclass;
    private InstanceVariableList instanceVariableList;
    private InstanceVariableList staticVariableList;
    private ArrayList<Method> publicMethodList;
    private ArrayList<Method> privateMethodList;
    private ArrayList<Method> staticMethodList;

    private boolean isFinal;

    public KraClass( String name ) {
        super( name );
        this.publicMethodList = new ArrayList<Method>();
        this.privateMethodList = new ArrayList<Method>();
        this.staticMethodList = new ArrayList<Method>();
        this.isFinal = false;
        this.superclass = null;
    }

    public KraClass( String name, boolean isFinal ) {
        super( name );
        this.publicMethodList = new ArrayList<Method>();
        this.privateMethodList = new ArrayList<Method>();
        this.staticMethodList = new ArrayList<Method>();
        this.isFinal = isFinal;
        this.superclass = null;
    }

    public String getCname() {
        return "_class_" + getName();
    }
    // métodos públicos get e set para obter e iniciar as variáveis acima,
    // entre outros métodos

    public KraClass getSuperclass() {
        return superclass;
    }

    public void setSuperclass( KraClass superclass ) {
        this.superclass = superclass;
    }

    public String getName() {
        return super.getName();
    }

    public InstanceVariableList getInstanceVariableList() {
        return instanceVariableList;
    }

    public void setInstanceVariableList( InstanceVariableList instanceVariableList ) {
        this.instanceVariableList = instanceVariableList;
    }

    public InstanceVariableList getStaticVariableList() {
        return staticVariableList;
    }

    public void setStaticVariableList( InstanceVariableList staticVariableList ) {
        this.staticVariableList = staticVariableList;
    }

    public ArrayList<Method> getPublicMethodList() {
        return publicMethodList;
    }

    public ArrayList<Method> getPrivateMethodList() {
        return privateMethodList;
    }

    public ArrayList<Method> getStaticMethodList() {
        return staticMethodList;
    }

    public void addMethod( Method newMethod ) {
        if ( newMethod.isStatic() ) {
            this.staticMethodList.add( newMethod );
        } else if ( newMethod.getQualifier().equals( "private" ) ) {
            this.privateMethodList.add( newMethod );
        } else if ( newMethod.getQualifier().equals( "public" ) ) {
            this.publicMethodList.add( newMethod );
        }

    }

    public boolean isFinal() {
        return isFinal;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        //Gera a hierarquia da classe.
        ArrayList<KraClass> superClassHierarchy = new ArrayList<KraClass>();
        superClassHierarchy.add( this );
        KraClass superClass = this.getSuperclass();
        while ( superClass != null ) {
            superClassHierarchy.add( superClass );
            superClass = superClass.getSuperclass();
        }
        //Invertemos a ordem, para adicionarmos os métodos e variáveis da superclasse mais acima até a classe atual
        Collections.reverse( superClassHierarchy );

        //Gera estrutura da classe
        pw.println( "typedef struct _St_" + this.getName() + " {" );
        pw.add();
        pw.printlnIdent( "Func *vt;" );
        for ( KraClass kc : superClassHierarchy ) {
            kc.getInstanceVariableList().genC( pw, kc.getName() );
        }
        pw.sub();
        pw.println( "} " + getCname() + ";" );

        //Gera variáveis estáticas
        this.staticVariableList.genC( pw, this.getName() );

        //Gera protótipo de new
        pw.println( getCname() + " *new_" + this.getName() + "(void);" );
        pw.println( "" );

        //Gera os métodos
        for ( Method m : this.staticMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }
        for ( Method m : this.privateMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }
        for ( Method m : this.publicMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }

        /*
         * Depois de construída a hierarquia, adicionamos os métodos públicos à um LinkedHashMap, que respeita ordem de
         * inserção.
         * Isso porque métodos redefinidos substituirão métodos da superclasse, na mesma ordem em que apareceram
         * primeiro.
         */
        HashMap<String, String> methodMap = new LinkedHashMap<String, String>();
        for ( KraClass kc : superClassHierarchy ) {
            ArrayList<Method> publicMethods = kc.getPublicMethodList();
            for ( Method m : publicMethods ) {
                methodMap.put( m.getName(), "( void (*)() ) _" + kc.getName() + "_" + m.getName() );
            }
        }

        pw.println( "Func VTclass_" + this.getName() + "[] = {" );
        pw.add();
        //Precisamos deste código para adicionarmos o número correto de vírgulas
        for ( String s : methodMap.keySet() ) {
            pw.printIdent( methodMap.remove( s ) );
            break;
        }
        for ( String s : methodMap.values() ) {
            pw.println( "," );
            pw.printIdent( s );
        }
        pw.sub();
        pw.println( "" );
        pw.println( "};" );
        pw.println( "" );

        //Gera o método new
        pw.println( this.getCname() + " *new_" + this.getName() + "()" );
        pw.println( "{" );
        pw.add();
        pw.printlnIdent( this.getCname() + " *t;" );
        pw.println( "" );
        pw.printlnIdent( "if ( (t = malloc(sizeof(" + this.getCname() + "))) != NULL )" );
        pw.add();
        pw.printlnIdent( "t->vt = VTclass_" + this.getName() + ";" );
        pw.sub();
        pw.printlnIdent( "return t;" );
        pw.sub();
        pw.println( "}" );
    }
}
