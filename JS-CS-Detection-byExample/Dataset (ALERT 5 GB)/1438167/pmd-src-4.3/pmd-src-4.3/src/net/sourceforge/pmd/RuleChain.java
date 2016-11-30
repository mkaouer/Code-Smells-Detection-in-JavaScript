package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.JavaRuleChainVisitor;
import net.sourceforge.pmd.jsp.ast.JspRuleChainVisitor;

/**
 * The RuleChain is a means by which Rules can participate in a uniform
 * visitation of the AST, and not need perform their own independent visitation.
 * The RuleChain exists as a means to improve the speed of PMD when there are
 * many Rules.
 */
public class RuleChain {
    // Mapping from Language to RuleChainVisitor
    private final Map<Language, RuleChainVisitor> languageToRuleChainVisitor = new HashMap<Language, RuleChainVisitor>();

    /**
     * Add all Rules from the given RuleSet which want to participate in the
     * RuleChain.
     * 
     * @param ruleSet
     *            The RuleSet to add Rules from.
     */
    public void add(RuleSet ruleSet) {
        Language language = ruleSet.getLanguage();
        for (Rule r: ruleSet.getRules()) {
            add(ruleSet, r, language);
        }
    }

    /**
     * Add the given Rule if it wants to participate in the RuleChain.
     * 
     * @param ruleSet
     *            The RuleSet to which the rule belongs.
     * @param rule
     *            The Rule to add.
     * @param language
     *            The Language used by the Rule.
     */
    private void add(RuleSet ruleSet, Rule rule, Language language) {
        RuleChainVisitor visitor = getRuleChainVisitor(language);
        if (visitor != null) {
            visitor.add(ruleSet, rule);
        }
    }

    /**
     * Apply the RuleChain to the given ASTCompilationUnits using the given
     * RuleContext, for those rules using the given Language.
     * 
     * @param astCompilationUnits
     *            The ASTCompilationUnits.
     * @param ctx
     *            The RuleContext.
     * @param language
     *            The Language.
     */
    public void apply(List<CompilationUnit> astCompilationUnits, RuleContext ctx,
            Language language) {
        RuleChainVisitor visitor = getRuleChainVisitor(language);
        if (visitor != null) {
            visitor.visitAll(astCompilationUnits, ctx);
        }
    }

    // Get the RuleChainVisitor for the appropriate Language.
    private RuleChainVisitor getRuleChainVisitor(Language language) {
        if (language == null) {
            language = Language.JAVA;
        }
        RuleChainVisitor visitor = languageToRuleChainVisitor.get(language);
        if (visitor == null) {
            if (Language.JAVA.equals(language)) {
                visitor = new JavaRuleChainVisitor();
            } else if (Language.JSP.equals(language)) {
                visitor = new JspRuleChainVisitor();
            } else {
                throw new IllegalArgumentException("Unknown language: "
                        + language);
            }
            languageToRuleChainVisitor.put(language, visitor);
        }
        return visitor;
    }
}
