package ast;

import java.util.ArrayList;

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

    public void setPublicMethodList( ArrayList<Method> publicMethodList ) {
        this.publicMethodList = publicMethodList;
    }

    public ArrayList<Method> getPrivateMethodList() {
        return privateMethodList;
    }

    public void setPrivateMethodList( ArrayList<Method> privateMethodList ) {
        this.privateMethodList = privateMethodList;
    }

    public ArrayList<Method> getStaticMethodList() {
        return staticMethodList;
    }

    public void setStaticMethodList( ArrayList<Method> staticMethodList ) {
        this.staticMethodList = staticMethodList;
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
        //Gera estrutura da classe
        pw.println( "typedef struct _St_" + this.getName() + " {" );
        pw.add();
        pw.printlnIdent( "Func *vt;" );
        this.instanceVariableList.genC( pw );
        this.staticVariableList.genC( pw );
        pw.sub();
        pw.println( "} " + getCname() + ";" );

        //Gera protótipo de new
        pw.println( getCname() + " *new_" + this.getName() + "(void);" );
        pw.println( "" );

        //Gera os métodos
        for ( Method m : this.publicMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }
        for ( Method m : this.privateMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }
        for ( Method m : this.staticMethodList ) {
            m.genC( pw, this );
            pw.println( "" );
        }

        //Gera o vetor de funções
        pw.println( "Func VTclass_" + getName() + "[] = {" );
        pw.add();
        for ( Method m : this.publicMethodList )
            pw.printlnIdent( "( void (*)() ) _" + getName() + "_" + m.getName() );
        pw.sub();
        pw.println( "};" );
        pw.println( "" );

        //Gera o método new
        pw.println( getCname() + " *new_" + this.getName() + "()" );
        pw.println( "{" );
        pw.add();
        pw.printlnIdent( getCname() + " *t;" );
        pw.println( "" );
        pw.printlnIdent( "if ( (t = malloc(sizeof(" + getCname() + "))) != NULL )" );
        pw.add();
        pw.printlnIdent( "t->vt = VTclass_" + getName() + ";" );
        pw.sub();
        pw.printlnIdent( "return t;" );
        pw.sub();
        pw.println( "}" );
    }
}
