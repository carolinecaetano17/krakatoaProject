package comp;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import ast.InstanceVariable;
import ast.KraClass;
import ast.Method;
import ast.Variable;

import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, KraClass> globalTable;
    private HashMap<String, Variable> instanceVariableTable;
    private HashMap<String, Variable> staticVariableTable;
    private HashMap<String, Variable> localVariableTable;
    private HashMap<String, Method> methodTable;
    private HashMap<String, Method> staticMethodTable;

    public SymbolTable() {
        this.globalTable = new HashMap<String, KraClass>();
        this.instanceVariableTable = new HashMap<String, Variable>();
        this.staticVariableTable = new HashMap<String, Variable>();
        this.methodTable = new HashMap<String, Method>();
        this.staticMethodTable = new HashMap<String, Method>();
        this.localVariableTable = new HashMap<String, Variable>();
    }

    public Object putInGlobal( String key, KraClass value ) {
        return globalTable.put( key, value );
    }

    public KraClass getInGlobal( String key ) {
        return globalTable.get( key );
    }

    public Variable putInstanceVar( String key, InstanceVariable value ) {
        if ( value.isStatic() )
            return staticVariableTable.put( key, value );
        else
            return instanceVariableTable.put( key, value );
    }

    public Variable getInstanceVar( String key ) {
        return instanceVariableTable.get( key );
    }

    public Variable getStaticVar( String key ) {
        return staticVariableTable.get( key );
    }

    public Variable putLocalVar( String key, Variable value ) {
        return localVariableTable.put( key, value );
    }

    public Variable getLocalVar( String key ) {
        return localVariableTable.get( key );
    }

    public Method putMethod( String key, Method value ) {
        if ( value.isStatic() )
            return staticMethodTable.put( key, value );
        else
            return methodTable.put( key, value );
    }

    public Method getMethod( String key ) {
        return methodTable.get( key );
    }

    public Method getStaticMethod( String key ) {
        return staticMethodTable.get( key );
    }

    public void removeInstanceIdents() {
        // remove all local identifiers from the table
        instanceVariableTable.clear();
        staticVariableTable.clear();
    }

    public void removeMethodIdents() {
        // remove all method identifiers from the table
        methodTable.clear();
        staticMethodTable.clear();
    }

    public void removeLocalIdents() {
        // remove all local identifiers from the table
        localVariableTable.clear();
    }
}
