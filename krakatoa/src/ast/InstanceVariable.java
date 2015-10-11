package ast;

public class InstanceVariable extends Variable {
    private boolean isStatic;

    public InstanceVariable( String name, Type type ) {
        super(name, type);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}