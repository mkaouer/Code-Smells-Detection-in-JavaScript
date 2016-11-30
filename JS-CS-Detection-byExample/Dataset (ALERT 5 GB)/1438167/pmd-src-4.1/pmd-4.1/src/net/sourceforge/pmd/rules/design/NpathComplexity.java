package net.sourceforge.pmd.rules.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.ast.ASTConditionalExpression;
import net.sourceforge.pmd.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * NPath complexity is a measurement of the acyclic execution paths through a
 * function. See Nejmeh, Communications of the ACM Feb 1988 pp 188-200.
 * 
 * @author Jason Bennett
 */
public class NpathComplexity extends StatisticalRule {

	
	private int complexityMultipleOf(SimpleJavaNode node, int npathStart, Object data) {
		
		int npath = npathStart;		
		SimpleJavaNode simpleNode;
		
	    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
	        simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
	        npath *= ((Integer) simpleNode.jjtAccept( this, data ));
	      }
	    
	    return npath;
	}
	
	private int complexitySumOf(SimpleJavaNode node, int npathStart, Object data) {
		
		int npath = npathStart;		
		SimpleJavaNode simpleNode;
		
	    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
	        simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
	        npath += (Integer) simpleNode.jjtAccept( this, data );
	      }
	    
	    return npath;
	}
	
  public Object visit(ASTMethodDeclaration node, Object data) {

//    int npath = 1;
//
//    // Basic NPath functionality multiplies the complexity of peer nodes
//    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
//      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
//      Integer complexity = (Integer) simpleNode.jjtAccept( this, data );
//      npath *= complexity.intValue();
//    }
	  
	  int npath = complexityMultipleOf(node, 1, data);

    DataPoint point = new DataPoint();
    point.setNode( node );
    point.setScore( 1.0 * npath );
    point.setMessage( getMessage() );
    addDataPoint( point );

    return Integer.valueOf( npath );
  }

  public Object visit(SimpleJavaNode node, Object data) {
//    int npath = 1;
//
//    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
//      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
//      Integer complexity = (Integer) simpleNode.jjtAccept( this, data );
//      npath *= complexity.intValue();
//    }

	 int npath = complexityMultipleOf(node, 1, data);
	 
    return Integer.valueOf( npath );
  }

  public Object visit(ASTIfStatement node, Object data) {
    // (npath of if + npath of else (or 1) + bool_comp of if) * npath of next

    int boolCompIf = sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );

    int complexity = 0;

    List<SimpleJavaNode> statementChildren = new ArrayList<SimpleJavaNode>();
    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      if ( node.jjtGetChild( i ).getClass() == ASTStatement.class ) {
        statementChildren.add((SimpleJavaNode) node.jjtGetChild( i ) );
      }
    }

    if ( statementChildren.isEmpty()
        || ( statementChildren.size() == 1 && node.hasElse() )
        || ( statementChildren.size() != 1 && !node.hasElse() ) ) {
      throw new IllegalStateException( "If node has wrong number of children" );
    }

    // add path for not taking if
    if ( !node.hasElse() ) {
      complexity++;
    }

    for (SimpleJavaNode element: statementChildren) {
      complexity += (Integer) element.jjtAccept( this, data );
    }

    return Integer.valueOf( boolCompIf + complexity );
  }

  public Object visit(ASTWhileStatement node, Object data) {
    // (npath of while + bool_comp of while + 1) * npath of next

    int boolCompWhile = sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );

    Integer nPathWhile = (Integer) ( (SimpleJavaNode) node.getFirstChildOfType( ASTStatement.class ) ).jjtAccept(
        this, data );

    return Integer.valueOf( boolCompWhile + nPathWhile + 1 );
  }

  public Object visit(ASTDoStatement node, Object data) {
    // (npath of do + bool_comp of do + 1) * npath of next

    int boolCompDo = sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );

    Integer nPathDo = (Integer) ( (SimpleJavaNode) node.getFirstChildOfType( ASTStatement.class ) ).jjtAccept(
        this, data );

    return Integer.valueOf( boolCompDo + nPathDo + 1 );
  }

  public Object visit(ASTForStatement node, Object data) {
    // (npath of for + bool_comp of for + 1) * npath of next

    int boolCompFor = sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );

    Integer nPathFor = (Integer) ( (SimpleJavaNode) node.getFirstChildOfType( ASTStatement.class ) ).jjtAccept(
        this, data );

    return Integer.valueOf( boolCompFor + nPathFor + 1 );
  }

  public Object visit(ASTReturnStatement node, Object data) {
    // return statements are valued at 1, or the value of the boolean expression

    ASTExpression expr = node.getFirstChildOfType( ASTExpression.class );

    if ( expr == null ) {
      return NumericConstants.ONE;
    }

    List andNodes = expr.findChildrenOfType( ASTConditionalAndExpression.class );
    List orNodes = expr.findChildrenOfType( ASTConditionalOrExpression.class );
    int boolCompReturn = andNodes.size() + orNodes.size();

    if ( boolCompReturn > 0 ) {
      return Integer.valueOf( boolCompReturn );
    }
    return NumericConstants.ONE;
  }

  public Object visit(ASTSwitchStatement node, Object data) {
    // bool_comp of switch + sum(npath(case_range))

    int boolCompSwitch = sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );

    int npath = 0;
    int caseRange = 0;
    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );

      // Fall-through labels count as 1 for complexity
      if ( simpleNode instanceof ASTSwitchLabel ) {
        npath += caseRange;
        caseRange = 1;
      } else {
        Integer complexity = (Integer) simpleNode.jjtAccept( this, data );
        caseRange *= complexity;
      }
    }
    // add in npath of last label
    npath += caseRange;
    return Integer.valueOf( boolCompSwitch + npath );
  }

  public Object visit(ASTTryStatement node, Object data) {
    /*
     * This scenario was not addressed by the original paper. Based on the
     * principles outlined in the paper, as well as the Checkstyle NPath
     * implementation, this code will add the complexity of the try to the
     * complexities of the catch and finally blocks.
     */

//    int npath = 0;
//
//    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
//      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
//      Integer complexity = (Integer) simpleNode.jjtAccept( this, data );
//      npath += complexity.intValue();
//    }

	  int npath = complexitySumOf(node, 0, data);
	  
    return Integer.valueOf( npath );

  }

  public Object visit(ASTConditionalExpression node, Object data) {
    if ( node.isTernary() ) {
//      int npath = 0;
//
//      for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
//        SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
//        Integer complexity = (Integer) simpleNode.jjtAccept( this, data );
//        npath += complexity.intValue();
//      }
    	int npath = complexitySumOf(node, 0, data);
    	
      npath += 2;
      return Integer.valueOf( npath );
    }
    return NumericConstants.ONE;
  }

  /**
   * Calculate the boolean complexity of the given expression. NPath boolean
   * complexity is the sum of && and || tokens. This is calculated by summing
   * the number of children of the &&'s (minus one) and the children of the ||'s
   * (minus one).
   * <p>
   * Note that this calculation applies to Cyclomatic Complexity as well.
   * 
   * @param expr
   *          control structure expression
   * @return complexity of the boolean expression
   */
  public static int sumExpressionComplexity(ASTExpression expr) {
    if (expr == null) {
      return 0;
    }

    List<ASTConditionalAndExpression> andNodes = expr.findChildrenOfType( ASTConditionalAndExpression.class );
    List<ASTConditionalOrExpression> orNodes = expr.findChildrenOfType( ASTConditionalOrExpression.class );

    int children = 0;

    for ( ASTConditionalOrExpression element: orNodes ) {
      children += element.jjtGetNumChildren();
      children--;
    }

    for ( ASTConditionalAndExpression element: andNodes ) {
      children += element.jjtGetNumChildren();
      children--;
    }

    return children;
  }

  protected void makeViolations(RuleContext ctx, Set<DataPoint> p) {
    for ( DataPoint point: p ) {
      addViolation( ctx, point.getNode(), new String[] {
          ( (ASTMethodDeclaration) point.getNode() ).getMethodName(),
          String.valueOf( (int) point.getScore() ) } );
    }
  }

}
