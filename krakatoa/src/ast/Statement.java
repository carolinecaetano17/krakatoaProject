package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

abstract public class Statement extends ASTNode {

    abstract public void genC( PW pw );

}
