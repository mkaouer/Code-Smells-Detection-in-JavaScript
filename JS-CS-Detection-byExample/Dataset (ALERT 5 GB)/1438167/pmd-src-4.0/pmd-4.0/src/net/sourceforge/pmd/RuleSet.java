/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.util.Benchmark;


/**
 * This class represents a collection of rules.
 *
 * @see Rule
 */
public class RuleSet {

    private List<Rule> rules = new ArrayList<Rule>();
    private String name = "";
    private String description = "";
    private Language language;

    /**
     * Returns the number of rules in this ruleset
     *
     * @return an int representing the number of rules
     */
    public int size() {
        return rules.size();
    }

    /**
     * Add a new rule to this ruleset
     *
     * @param rule the rule to be added
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new RuntimeException("Null Rule reference added to a RuleSet; that's a bug somewhere in PMD");
        }
        rules.add(rule);
    }

    /**
     * Returns the actual Collection of rules in this ruleset
     *
     * @return a Collection with the rules. All objects are of type {@link Rule}
     */
    public Collection<Rule> getRules() {
        return rules;
    }

    /**
     * @return true if any rule in the RuleSet needs the DFA layer
     */
    public boolean usesDFA() {
        for (Rule r: rules) {
            if (r.usesDFA()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Rule with the given name
     *
     * @param ruleName the name of the rule to find
     * @return the rule or null if not found
     */
    public Rule getRuleByName(String ruleName) {
        Rule rule = null;
        for (Iterator<Rule> i = rules.iterator(); i.hasNext() && (rule == null);) {
            Rule r = i.next();
            if (r.getName().equals(ruleName)) {
                rule = r;
            }
        }
        return rule;
    }

    /**
     * Add a whole RuleSet to this RuleSet
     *
     * @param ruleSet the RuleSet to add
     */
    public void addRuleSet(RuleSet ruleSet) {
        rules.addAll(rules.size(), ruleSet.getRules());
    }

    public void apply(List acuList, RuleContext ctx) {
        long start = System.nanoTime();
        for (Rule rule: rules) {
            if (!rule.usesRuleChain()) {
                rule.apply(acuList, ctx);
                long end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_RULE, rule.getName(), end - start, 1);
                start = end;
            }
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof RuleSet)) {
            return false; // Trivial
        }

        if (this == o) {
            return true; // Basic equality
        }

        RuleSet ruleSet = (RuleSet) o;
        return this.getName().equals(ruleSet.getName()) && this.getRules().equals(ruleSet.getRules());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.getName().hashCode() + 13 * this.getRules().hashCode();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public boolean usesTypeResolution() {
        for (Rule r: rules) {
            if (r.usesTypeResolution()) {
                return true;
            }
        }
        return false;
	}

}
