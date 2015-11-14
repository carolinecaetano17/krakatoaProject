package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

abstract public class Expr extends ASTNode {
    abstract public void genC( PW pw, boolean putParenthesis );

    // new method: the type of the expression
    abstract public Type getType();
}