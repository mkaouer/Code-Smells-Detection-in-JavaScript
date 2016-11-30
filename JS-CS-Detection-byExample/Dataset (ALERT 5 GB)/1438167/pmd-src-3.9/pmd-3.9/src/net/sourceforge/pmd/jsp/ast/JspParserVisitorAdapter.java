package net.sourceforge.pmd.jsp.ast;


public class JspParserVisitorAdapter implements JspParserVisitor {

    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTContent node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspDirective node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspDirectiveAttribute node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspScriptlet node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspComment node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTText node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTUnparsedText node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTElExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTValueBinding node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTCData node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTElement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAttribute node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAttributeValue node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTJspExpressionInAttribute node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTCommentTag node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTDoctypeDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTDoctypeExternalId node, Object data) {
        return visit((SimpleNode) node, data);
    }

}
