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
public class IgnoreCaseCharRule extends TerminalRule {

   private final char character;

   public IgnoreCaseCharRule(char character) {
      this.character = character;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitCharIgnoreCase(options, this);
   }

   public char getCharacter() {
      return character;
   }

   @Override
   public String toString() {
      return "\"" + character + "\"";
   }

   @Override
   public String getText() {
      return Character.toString(character);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof IgnoreCaseCharRule) {
         return Character.toLowerCase(((IgnoreCaseCharRule)obj).character) == Character.toLowerCase(character);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 97 * hash + Character.toLowerCase(this.character);
      return hash;
   }

   @Override
   public int length() {
      return 1;
   }

   @Override
   public boolean canBeEmpty() {
      return false;
   }
}
