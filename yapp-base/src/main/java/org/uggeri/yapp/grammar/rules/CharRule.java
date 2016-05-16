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
public class CharRule extends TerminalRule {

   private final char character;

   public CharRule(char character) {
      this.character = character;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitChar(options, this);
   }

   public char getCharacter() {
      return character;
   }

   @Override
   public String toString() {
      return "'" + character + "'";
   }

   @Override
   public String getText() {
      return Character.toString(character);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CharRule) {
         return ((CharRule)obj).character == character;
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 53 * hash + this.character;
      return hash;
   }

   @Override
   public int length() {
      return 1;
   }
}
