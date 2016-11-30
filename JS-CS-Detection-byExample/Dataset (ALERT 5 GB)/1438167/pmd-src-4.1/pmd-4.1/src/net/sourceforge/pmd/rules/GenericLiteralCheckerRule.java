/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.properties.StringProperty;


/**
 * This class allow to match a Literal (most likely a String) with a regex pattern.
 * Obviously, there are many applications of it (such as basic.xml/AvoidUsingHardCodedIP).
 *
 * @author Romain PELISSE, belaran@gmail.com
 */
public class GenericLiteralCheckerRule extends AbstractRule {

	private static final String PROPERTY_NAME = "pattern";
	private static final String DESCRIPTION = "Regular Expression";
	private Pattern pattern;

	private void init() {
		if (pattern == null) {
			// Retrieve the regex pattern set by user
			PropertyDescriptor property = new StringProperty(PROPERTY_NAME,DESCRIPTION,"", 1.0f);
			String stringPattern = super.getStringProperty(property);
			// Compile the pattern only once
			if ( stringPattern != null && stringPattern.length() > 0 ) {
				pattern = Pattern.compile(stringPattern);
			} else {
				throw new IllegalArgumentException("Must provide a value for the '" + PROPERTY_NAME + "' property.");
			}
		}
	}

	/**
	 * This method checks if the Literal matches the pattern. If it does, a violation is logged.
	 */
	public Object visit(ASTLiteral node, Object data) {
		init();
		String image = node.getImage();
		if ( image != null && image.length() > 0 && isMatch(image) ) {
			addViolation(data, node);
		}
		return data;
	}

	private boolean isMatch(String image) {
        Matcher matcher = pattern.matcher(image);
        if (matcher.find()) {
            return true;
        }
		return false;
	}
}
