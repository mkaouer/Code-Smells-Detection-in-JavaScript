/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

public interface Rule {

    public static final int LOWEST_PRIORITY = 5;
    public static final String[] PRIORITIES = {"High", "Medium High", "Medium", "Medium Low", "Low"};

    String getName();

    String getMessage();

    String getDescription();

    String getExample();

    String getExternalInfoUrl();

    void setName(String name);

    String getRuleSetName();

    void setRuleSetName(String name);

    void setMessage(String message);

    void setDescription(String description);

    void setExample(String example);

    void setExternalInfoUrl(String url);

    void apply(List astCompilationUnits, RuleContext ctx);

    boolean hasProperty(String name);

    void addProperty(String name, String property);

    void addProperties(Properties properties);

    int getIntProperty(String name);

    boolean getBooleanProperty(String name);

    String getStringProperty(String name);

    double getDoubleProperty(String name);

    Properties getProperties();

    boolean include();

    void setInclude(boolean include);

    int getPriority();

    String getPriorityName();

    void setPriority(int priority);

    void setUsesDFA();

    boolean usesDFA();
    
    PropertyDescriptor propertyDescriptorFor(String name);

	void setUsesTypeResolution();

	boolean usesTypeResolution();
}
