package net.sourceforge.pmd.util.viewer.model;


import net.sourceforge.pmd.jaxen.Attribute;


/**
 * A toolkit for vaious attribute translations
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: AttributeToolkit.java 4710 2006-10-20 02:40:14Z hooperbloob $
 */

public class AttributeToolkit {

    /**
     * formats a value for its usage in XPath expressions
     *
     * @param attribute atribute which value should be formatted
     * @return formmated value
     */
    public static String formatValueForXPath(Attribute attribute) {
        return '\'' + attribute.getValue() + '\'';
    }

    /**
     * constructs a predicate from the given attribute
     *
     * @param attribute attribute to be formatted as predicate
     * @return predicate
     */
    public static String constructPredicate(Attribute attribute) {
        return "[@" + attribute.getName() + '=' +
                formatValueForXPath(attribute) + ']';
    }
}