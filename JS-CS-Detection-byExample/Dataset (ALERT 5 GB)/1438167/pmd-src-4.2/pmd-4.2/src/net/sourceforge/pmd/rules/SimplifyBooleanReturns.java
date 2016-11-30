/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

public class SimplifyBooleanReturns extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        // only boolean methods should be inspected
        ASTResultType r = node.getResultType();
        
        if (!r.isVoid()) {
            Node t = r.jjtGetChild(0);
            if (t.jjtGetNumChildren() == 1) {
                t = t.jjtGetChild(0);
                if ((t instanceof ASTPrimitiveType) && ((ASTPrimitiveType) t).isBoolean()) {
                    return super.visit(node, data);
                }
            }
        }
        // skip method
        return data;
    }

    public Object visit(ASTIfStatement node, Object data) {
        // only deal with if..then..else stmts
        if (node.jjtGetNumChildren() != 3) {
            return super.visit(node, data);
        }

        // don't bother if either the if or the else block is empty
        if (node.jjtGetChild(1).jjtGetNumChildren() == 0 || node.jjtGetChild(2).jjtGetNumChildren() == 0) {
            return super.visit(node, data);
        }

        // if we have something like
        // if(true) or if(false)
        if (false && // FIXME: disabling moved in first position to avoid NPE but why is this here?
            node.jjtGetChild(0).jjtGetChild(0) instanceof ASTPrimaryExpression &&
            node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTPrimaryPrefix &&
            node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTLiteral &&
            node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTBooleanLiteral) {
          addViolation(data, node);
        }
        else {
          SimpleNode returnStatement1 = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);
          SimpleNode returnStatement2 = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0);

          if (returnStatement1 instanceof ASTReturnStatement && returnStatement2 instanceof ASTReturnStatement) {
            //if we have 2 return;
            if(isSimpleReturn(returnStatement1) && isSimpleReturn(returnStatement2)) {
                // first case:
                // If
                //  Expr
                //  Statement
                //   ReturnStatement
                //  Statement
                //   ReturnStatement
                // i.e.,
                // if (foo)
                //  return true;
                // else
                //  return false;
              addViolation(data, node);
            }
            else {
              SimpleNode expression1 = (SimpleNode)returnStatement1.jjtGetChild(0).jjtGetChild(0);
              SimpleNode expression2 = (SimpleNode)returnStatement2.jjtGetChild(0).jjtGetChild(0);
              if(terminatesInBooleanLiteral(returnStatement1) && terminatesInBooleanLiteral(returnStatement2)) {
                addViolation(data, node);
              }
              else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
                //We get the nodes under the '!' operator
                //If they are the same => error
                if(isNodesEqualWithUnaryExpression(expression1, expression2)) {
                    // second case:
                    // If
                    //  Expr
                    //  Statement
                    //   ReturnStatement
                    //     UnaryExpressionNotPlusMinus '!'
                    //       Expression E
                    //  Statement
                    //   ReturnStatement
                    //       Expression E
                    // i.e.,
                    // if (foo)
                    //  return !a;
                    // else
                    //  return a;
                  addViolation(data, node);
                }
              }
            }
          } else if (hasOneBlockStmt((SimpleNode) node.jjtGetChild(1)) && hasOneBlockStmt((SimpleNode) node.jjtGetChild(2))) {
            //We have blocks so we must go down three levels (BlockStatement, Statement, ReturnStatement)
            returnStatement1 = (SimpleNode)returnStatement1.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            returnStatement2 = (SimpleNode)returnStatement2.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);

            //if we have 2 return;
            if(isSimpleReturn(returnStatement1) && isSimpleReturn(returnStatement2)) {
                // third case
                // If
                // Expr
                // Statement
                //  Block
                //   BlockStatement
                //    Statement
                //     ReturnStatement
                // Statement
                //  Block
                //   BlockStatement
                //    Statement
                //     ReturnStatement
                // i.e.,
                // if (foo) {
                //  return true;
                // } else {
                //  return false;
                // }
              addViolation(data, node);
            }
            else {
              SimpleNode expression1 = getDescendant(returnStatement1, 4);
              SimpleNode expression2 = getDescendant(returnStatement2, 4);
              if(terminatesInBooleanLiteral((SimpleNode)node.jjtGetChild(1).jjtGetChild(0)) && terminatesInBooleanLiteral((SimpleNode) node.jjtGetChild(2).jjtGetChild(0))) {
                addViolation(data, node);
              } else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
                //We get the nodes under the '!' operator
                //If they are the same => error
                if(isNodesEqualWithUnaryExpression(expression1, expression2)) {
                    // forth case
                    // If
                    // Expr
                    // Statement
                    //  Block
                    //   BlockStatement
                    //    Statement
                    //     ReturnStatement
                    //       UnaryExpressionNotPlusMinus '!'
                    //         Expression E
                    // Statement
                    //  Block
                    //   BlockStatement
                    //    Statement
                    //     ReturnStatement
                    //      Expression E
                    // i.e.,
                    // if (foo) {
                    //  return !a;
                    // } else {
                    //  return a;
                    // }
                  addViolation(data, node);
                }
              }
            }
          }
        }
        return super.visit(node, data);
    }

    private boolean hasOneBlockStmt(SimpleNode node) {
        return node.jjtGetChild(0) instanceof ASTBlock && node.jjtGetChild(0).jjtGetNumChildren() == 1 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTBlockStatement && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTStatement && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTReturnStatement;
    }

    /**
     * Returns the first child node going down 'level' levels or null if level is invalid
     */
    private SimpleNode getDescendant(SimpleNode node, int level) {
        Node n = node;
        for(int i = 0; i < level; i++) {
            if (n.jjtGetNumChildren() == 0) {
                return null;
            }
            n = n.jjtGetChild(0);
        }
        return (SimpleNode) n;
    }

    private boolean terminatesInBooleanLiteral(SimpleNode node) {

        return eachNodeHasOneChild(node) && (getLastChild(node) instanceof ASTBooleanLiteral);
    }

    private boolean eachNodeHasOneChild(SimpleNode node) {
        if (node.jjtGetNumChildren() > 1) {
            return false;
        }
        if (node.jjtGetNumChildren() == 0) {
            return true;
        }
        return eachNodeHasOneChild((SimpleNode) node.jjtGetChild(0));
    }

    private SimpleNode getLastChild(SimpleNode node) {
        if (node.jjtGetNumChildren() == 0) {
            return node;
        }
        return getLastChild((SimpleNode) node.jjtGetChild(0));
    }

    private boolean isNodesEqualWithUnaryExpression(SimpleNode n1, SimpleNode n2) {
      SimpleNode node1, node2;
      if(n1 instanceof ASTUnaryExpressionNotPlusMinus) {
        node1 = (SimpleNode)n1.jjtGetChild(0);
      } else {
        node1 = n1;
      }
      if(n2 instanceof ASTUnaryExpressionNotPlusMinus) {
        node2 = (SimpleNode)n2.jjtGetChild(0);
      } else {
        node2 = n2;
      }
      return isNodesEquals(node1, node2);
    }

    private boolean isNodesEquals(SimpleNode n1, SimpleNode n2) {
        int numberChild1 = n1.jjtGetNumChildren();
        int numberChild2 = n2.jjtGetNumChildren();
        if(numberChild1 != numberChild2)
          return false;
        if(!n1.getClass().equals(n2.getClass()))
          return false;
        if(!n1.toString().equals(n2.toString()))
          return false;
        for(int i = 0 ; i < numberChild1 ; i++) {
          if( !isNodesEquals( (SimpleNode)n1.jjtGetChild(i), (SimpleNode)n2.jjtGetChild(i) ) )
            return false;
        }
        return true;
    }

    private boolean isSimpleReturn(SimpleNode node) {
      return ( ( node instanceof ASTReturnStatement ) && ( node.jjtGetNumChildren() == 0 ) );
    }

}
