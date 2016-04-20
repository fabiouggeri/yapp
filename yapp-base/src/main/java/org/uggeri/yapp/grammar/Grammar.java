/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar;

import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author fabio
 */
public class Grammar {

   private String grammarName;

   private final List<Grammar> importGrammars = new ArrayList<Grammar>();

   private NonTerminalRule mainRule = null;

   private final Map<String, NonTerminalRule> rules = new HashMap<String, NonTerminalRule>();

   private final List<NonTerminalRule> declaredRules = new ArrayList<NonTerminalRule>();

   private File grammarFile = null;

   public String getGrammarName() {
      return grammarName;
   }

   public void setGrammarName(String grammarName) {
      this.grammarName = grammarName;
   }

   public NonTerminalRule getMainRule() {
      return mainRule;
   }

   public void setMainRule(NonTerminalRule mainRule) {
      this.mainRule = mainRule;
   }

   public NonTerminalRule getRule(String ruleName) {
      NonTerminalRule rule = rules.get(ruleName);
      if (rule == null) {
         for (int i = importGrammars.size() - 1; i >= 0; i--) {
            rule = importGrammars.get(i).getRule(ruleName);
            if (rule != null) {
               break;
            }
         }
      }
      return rule;
   }

   /** Regras declaradas explicitamente na gramatica
    *  @return a lista de regras declaradas na gramatica
    */
   public List<NonTerminalRule> getDeclaredRules() {
      return declaredRules;
   }

   public Collection<NonTerminalRule> getRules() {
      return rules.values();
   }

   public List<Grammar> getImportGrammars() {
      return importGrammars;
   }

   public void addImportGrammar(Grammar extendsGrammar) {
      importGrammars.add(extendsGrammar);
   }

   public File getGrammarFile() {
      return grammarFile;
   }

   public void setGrammarFile(File grammarFile) {
      this.grammarFile = grammarFile;
   }

   void addRule(final NonTerminalRule rule) {
      rules.put(rule.getName(), rule);
   }

   void declareRule(final NonTerminalRule rule) {
      declaredRules.add(rule);
   }
}
