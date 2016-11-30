/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAnnotation;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.properties.BooleanProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AvoidDuplicateLiteralsRule extends AbstractRule {

    private static final PropertyDescriptor SKIP_ANNOTATIONS = new BooleanProperty("skipAnnotations",
          "Skip literals within Annotations.", false, 1.0f);

    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(new PropertyDescriptor[] { SKIP_ANNOTATIONS });

    public static class ExceptionParser {

        private static final char ESCAPE_CHAR = '\\';
        private char delimiter;

        public ExceptionParser(char delimiter) {
            this.delimiter = delimiter;
        }

        public Set<String> parse(String in) {
            Set<String> result = new HashSet<String>();
            StringBuffer currentToken = new StringBuffer();
            boolean inEscapeMode = false;
            for (int i = 0; i < in.length(); i++) {
                if (inEscapeMode) {
                    inEscapeMode = false;
                    currentToken.append(in.charAt(i));
                    continue;
                }
                if (in.charAt(i) == ESCAPE_CHAR) {
                    inEscapeMode = true;
                    continue;
                }
                if (in.charAt(i) == delimiter) {
                    result.add(currentToken.toString());
                    currentToken = new StringBuffer();
                } else {
                    currentToken.append(in.charAt(i));
                }
            }
            if (currentToken.length() > 0) {
                result.add(currentToken.toString());
            }
            return result;
        }
    }

    private static final char DEFAULT_SEPARATOR = ',';
    private static final String EXCEPTION_LIST_PROPERTY = "exceptionlist";
    private static final String SEPARATOR_PROPERTY = "separator";
    private static final String EXCEPTION_FILE_NAME_PROPERTY = "exceptionfile";

    private Map<String, List<ASTLiteral>> literals = new HashMap<String, List<ASTLiteral>>();
    private Set<String> exceptions = new HashSet<String>();

    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();

        if (hasProperty(EXCEPTION_LIST_PROPERTY)) {
            ExceptionParser p;
            if (hasProperty(SEPARATOR_PROPERTY)) {
                p = new ExceptionParser(getStringProperty(SEPARATOR_PROPERTY).charAt(0));
            } else {
                p = new ExceptionParser(DEFAULT_SEPARATOR);
            }
            exceptions = p.parse(getStringProperty(EXCEPTION_LIST_PROPERTY));
        } else if (hasProperty(EXCEPTION_FILE_NAME_PROPERTY)) {
            exceptions = new HashSet<String>();
            LineNumberReader reader = null;
            try {
                reader = new LineNumberReader(new BufferedReader(new FileReader(new File(getStringProperty(EXCEPTION_FILE_NAME_PROPERTY)))));
                String line;
                while ((line = reader.readLine()) != null) {
                    exceptions.add(line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        super.visit(node, data);

        int threshold = getIntProperty("threshold");
        for (String key: literals.keySet()) {
            List<ASTLiteral> occurrences = literals.get(key);
            if (occurrences.size() >= threshold) {
                Object[] args = new Object[]{key, Integer.valueOf(occurrences.size()), Integer.valueOf(occurrences.get(0).getBeginLine())};
                addViolation(data, occurrences.get(0), args);
            }
        }
        return data;
    }

    public Object visit(ASTLiteral node, Object data) {
        // just catching strings of 5 chars or more (including the enclosing quotes) for now - no numbers
        if (node.getImage() == null || node.getImage().indexOf('\"') == -1 || node.getImage().length() < 5) {
            return data;
        }

        // skip any exceptions
        if (exceptions.contains(node.getImage().substring(1, node.getImage().length() - 1))) {
            return data;
        }

        // Skip literals in annotations
        if (getBooleanProperty(SKIP_ANNOTATIONS) && node.getFirstParentOfType(ASTAnnotation.class) != null) {
            return data;
        }

        if (literals.containsKey(node.getImage())) {
            List<ASTLiteral> occurrences = literals.get(node.getImage());
            occurrences.add(node);
        } else {
            List<ASTLiteral> occurrences = new ArrayList<ASTLiteral>();
            occurrences.add(node);
            literals.put(node.getImage(), occurrences);
        }

        return data;
    }

    @Override
    protected Map<String, PropertyDescriptor> propertiesByName() {
	return PROPERTY_DESCRIPTORS_BY_NAME;
    }
}

