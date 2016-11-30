package test.net.sourceforge.pmd.jsp.ast;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.jsp.ast.ASTAttribute;
import net.sourceforge.pmd.jsp.ast.ASTAttributeValue;
import net.sourceforge.pmd.jsp.ast.ASTCData;
import net.sourceforge.pmd.jsp.ast.ASTCommentTag;
import net.sourceforge.pmd.jsp.ast.ASTDoctypeDeclaration;
import net.sourceforge.pmd.jsp.ast.ASTDoctypeExternalId;
import net.sourceforge.pmd.jsp.ast.ASTElement;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
/**
 * Test parsing of a JSP in document style, by checking the generated AST.
 * 
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 * 
 */
public class JspDocStyleTest extends AbstractJspNodesTst {

	/**
	 * Smoke test for JSP parser.
	 * 
	 * @throws Throwable
	 */
    @Test
    public void testSimplestJsp() throws Throwable {
		assertNumberOfNodes(ASTElement.class, TEST_SIMPLEST_HTML, 1);
	}

	/**
	 * Test the information on a Element and Attribute.
	 * 
	 * @throws Throwable
	 */
    @Test
	public void testElementAttributeAndNamespace() throws Throwable {
		Set nodes = getNodes(null, TEST_ELEMENT_AND_NAMESPACE);

		Set<ASTElement> elementNodes = getNodesOfType(ASTElement.class, nodes);
		assertEquals("One element node expected!", 1, elementNodes.size());
		ASTElement element = elementNodes.iterator().next();
		assertEquals("Correct name expected!", "h:html", element.getName());
		assertEquals("Has namespace prefix!", true, element.isHasNamespacePrefix());
		assertEquals("Element is empty!", true, element.isEmpty());
		assertEquals("Correct namespace prefix of element expected!", "h", element
				.getNamespacePrefix());
		assertEquals("Correct local name of element expected!", "html", element
				.getLocalName());

		Set attributeNodes = getNodesOfType(ASTAttribute.class, nodes);
		assertEquals("One attribute node expected!", 1, attributeNodes.size());
		ASTAttribute attribute = (ASTAttribute) attributeNodes.iterator().next();
		assertEquals("Correct name expected!", "MyNsPrefix:MyAttr", attribute
				.getName());
		assertEquals("Has namespace prefix!", true, attribute.isHasNamespacePrefix());
		assertEquals("Correct namespace prefix of element expected!", "MyNsPrefix",
				attribute.getNamespacePrefix());
		assertEquals("Correct local name of element expected!", "MyAttr", attribute
				.getLocalName());

	}
	
	/**
	 * Test exposing a bug of parsing error when having a hash as last character
	 * in an attribute value.
	 *
	 */
    @Test
    public void testAttributeValueContainingHash() 
	{
		Set nodes = getNodes(null, TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);
		
		Set<ASTAttribute> attributes = getNodesOfType(ASTAttribute.class, nodes);
		assertEquals("Three attributes expected!", 3, attributes.size());
		
		List<ASTAttribute> attrsList = new ArrayList<ASTAttribute>(attributes);
		Collections.sort(attrsList, new Comparator<ASTAttribute>() {
			public int compare(ASTAttribute arg0, ASTAttribute arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		
		ASTAttribute attr = attrsList.get(0);
		assertEquals("Correct attribute name expected!", 
				"foo", attr.getName());
		assertEquals("Correct attribute value expected!", 
				"CREATE", attr.getFirstChildOfType(ASTAttributeValue.class).getImage());
		
		attr = attrsList.get(1);
		assertEquals("Correct attribute name expected!", 
				"href", attr.getName());
		assertEquals("Correct attribute value expected!", 
				"#", attr.getFirstChildOfType(ASTAttributeValue.class).getImage());
		
		attr = attrsList.get(2);
		assertEquals("Correct attribute name expected!", 
				"something", attr.getName());
		assertEquals("Correct attribute value expected!", 
				"#yes#", attr.getFirstChildOfType(ASTAttributeValue.class).getImage());
	}

	/**
	 * Test correct parsing of CDATA.
	 */
    @Test
    public void testCData() {
		Set cdataNodes = getNodes(ASTCData.class, TEST_CDATA);

		assertEquals("One CDATA node expected!", 1, cdataNodes.size());
		ASTCData cdata = (ASTCData) cdataNodes.iterator().next();
		assertEquals("Content incorrectly parsed!", " some <cdata> ]] ]> ", cdata
				.getImage());
	}

	/**
	 * Test parsing of Doctype declaration.
	 */
    @Test
    public void testDoctype() {
		Set nodes = getNodes(null, TEST_DOCTYPE);

		Set<ASTDoctypeDeclaration> docTypeDeclarations = getNodesOfType(ASTDoctypeDeclaration.class, nodes);
		assertEquals("One doctype declaration expected!", 1, docTypeDeclarations
				.size());
		ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations
				.iterator().next();
		assertEquals("Correct doctype-name expected!", "html", docTypeDecl.getName());
		
		Set externalIds = getNodesOfType(ASTDoctypeExternalId.class, nodes);
		assertEquals("One doctype external id expected!", 1, externalIds
				.size());
		ASTDoctypeExternalId externalId = (ASTDoctypeExternalId) externalIds
				.iterator().next();
		assertEquals("Correct external public id expected!", "-//W3C//DTD XHTML 1.1//EN", 
				externalId.getPublicId());
		assertEquals("Correct external uri expected!", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd",
				externalId.getUri());
		
	}
	
	/**
	 * Test parsing of a XML comment.
	 *
	 */
    @Test
    public void testComment() {
		Set comments = getNodes(ASTCommentTag.class, TEST_COMMENT);
		assertEquals("One comment expected!", 1, comments.size());
		ASTCommentTag comment = (ASTCommentTag) comments.iterator().next();
		assertEquals("Correct comment content expected!", "comment", comment.getImage());
	}

	private static final String TEST_SIMPLEST_HTML = "<html/>";

	private static final String TEST_ELEMENT_AND_NAMESPACE = "<h:html MyNsPrefix:MyAttr='MyValue'/>";

	private static final String TEST_CDATA = "<html><![CDATA[ some <cdata> ]] ]> ]]></html>";

	private static final String TEST_DOCTYPE = "<?xml version=\"1.0\" standalone='yes'?>\n"
			+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" "
			+ "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"
			+ "<greeting>Hello, world!</greeting>";
	
	private static final String TEST_COMMENT = "<html><!-- comment --></html>";
	
	private static final String TEST_ATTRIBUTE_VALUE_CONTAINING_HASH = 
		"<tag:if something=\"#yes#\" foo=\"CREATE\">  <a href=\"#\">foo</a> </tag:if>";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JspDocStyleTest.class);
    }
}
