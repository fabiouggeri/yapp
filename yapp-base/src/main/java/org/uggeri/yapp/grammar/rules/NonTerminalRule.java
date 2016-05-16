/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author fabio
 */
public class NonTerminalRule implements GrammarRule, SimpleGrammarRule {

   private Map<NonTerminalOption, String> options = null;

   private final String name;

   private GrammarRule rule;

   private String methodName;

   private String ruleId;

   private String label;

   private Pattern patternToFollow = null;

   private boolean followIfMatch = true;
   
   private boolean leftRecursion = false;

   public NonTerminalRule(String name, GrammarRule rule) {
      this.name = name;
      this.label = name;
      this.rule = rule;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitNonTerminal(options, this);
   }

   public String getName() {
      return name;
   }

   @Override
   public GrammarRule getRule() {
      return rule;
   }

   public void addOption(NonTerminalOption option) {
      if (options == null) {
         options = new EnumMap<NonTerminalOption, String>(NonTerminalOption.class);
      }
      options.put(option, "");
   }

   public void addOption(NonTerminalOption option, String value) {
      if (options == null) {
         options = new EnumMap<NonTerminalOption, String>(NonTerminalOption.class);
      }
      options.put(option, value);
   }

   public void removeOption(NonTerminalOption option) {
      if (options != null) {
         options.remove(option);
      }
   }

   public void clearOptions() {
      if (options != null) {
         options.clear();
      }
   }

   public Map<NonTerminalOption, String> getOptions() {
      if (options == null) {
         return Collections.emptyMap();
      }
      return options;
   }

   public void setMethodName(String methodName) {
      this.methodName = methodName;
   }

   public String getMethodName() {
      return methodName;
   }

   @Override
   public String toString() {
      return name; // + " : " + rule.toString();
   }

   public void setRuleId(String ruleName) {
      this.ruleId = ruleName;
   }

   public String getRuleId() {
      return ruleId;
   }

   @Override
   public void setRule(GrammarRule rule) {
      this.rule = rule;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   public Pattern getPatternToFollow() {
      return patternToFollow;
   }

   public void setPatternToFollow(Pattern patternToFollow) {
      this.patternToFollow = patternToFollow;
   }

   public boolean isFollowIfMatch() {
      return followIfMatch;
   }

   public void setFollowIfMatch(boolean followIfMatch) {
      this.followIfMatch = followIfMatch;
   }

   public boolean hasLeftRecursion() {
      return leftRecursion;
   }

   public void setLeftRecursion(boolean leftRecursion) {
      this.leftRecursion = leftRecursion;
   }
}
