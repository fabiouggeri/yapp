/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;

/**
 *
 * @author fabio
 */
public class StringRule extends TerminalRule {

   private final String text;

   private int partialMatchLength;

   public StringRule(String text, int partialMatchLength) {
      this.text = text;
      this.partialMatchLength = partialMatchLength;
   }

   public StringRule(String text) {
      this(text, 0);
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitString(options, this);
   }

   @Override
   public String getText() {
      return text;
   }

   @Override
   public String toString() {
      return "'" + text + "'";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StringRule) {
         return ((StringRule)obj).text.equals(text);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 97 * hash + (this.text != null ? this.text.hashCode() : 0);
      return hash;
   }

   @Override
   public int length() {
      return text.length();
   }

   public int getPartialMatchLength() {
      return partialMatchLength;
   }

   @Override
   public boolean canBeEmpty() {
      return false;
   }
}
