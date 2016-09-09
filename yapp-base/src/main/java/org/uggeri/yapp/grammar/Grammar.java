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

   private NonTerminalRule mainRule = null;

   private final Map<String, NonTerminalRule> rules = new HashMap<String, NonTerminalRule>();

   private final List<NonTerminalRule> declaredRules = new ArrayList<NonTerminalRule>();

   private File grammarFile = null;
   
   private String charset = null;

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
      return rules.get(ruleName);
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

   public String getCharset() {
      return charset;
   }
   
   public String getCharset(boolean defValue) {
      if (charset == null && defValue) {
         return "UTF8";
      }
      return charset;
   }

   public void setCharset(String charset) {
      this.charset = charset;
   }
}
