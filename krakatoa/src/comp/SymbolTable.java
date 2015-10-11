package comp;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import ast.KraClass;
import ast.Method;
import ast.Variable;

import java.util.HashMap;

public class SymbolTable {

    public SymbolTable() {
        globalTable = new HashMap<String, KraClass>();
        instanceVariableTable = new HashMap<String, Variable>();
        methodTable = new HashMap<String, Method>();
        localVariableTable = new HashMap<String, Variable>();
    }

    public Object putInGlobal(String key, KraClass value) {
        return globalTable.put(key, value);
    }

    public KraClass getInGlobal(String key) {
        return globalTable.get(key);
    }

    public Variable putInstanceVar(String key, Variable value) {
        return instanceVariableTable.put(key, value);
    }

    public Variable getInstanceVar(String key) {
        return instanceVariableTable.get(key);
    }

    public Variable putLocalVar(String key, Variable value) {
        return localVariableTable.put(key, value);
    }

    public Variable getLocalVar(String key) {
        return localVariableTable.get(key);
    }

    public Method putMethod(String key, Method value) {
        return methodTable.put(key, value);
    }

    public Method getMethod(String key) {
        return methodTable.get(key);
    }

    public void removeInstanceIdents() {
        // remove all local identifiers from the table
        instanceVariableTable.clear();
    }

    public void removeMethodIdents() {
        // remove all method identifiers from the table
        methodTable.clear();
    }

    public void removeLocalIdents() {
        // remove all local identifiers from the table
        localVariableTable.clear();
    }

    private HashMap<String, KraClass> globalTable;
    private HashMap<String, Variable> instanceVariableTable;
    private HashMap<String, Variable> localVariableTable;
    private HashMap<String, Method> methodTable;
}
