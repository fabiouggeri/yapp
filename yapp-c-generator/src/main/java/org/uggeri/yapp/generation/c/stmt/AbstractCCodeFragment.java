/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.AbstractParserGenerator;
import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.LiteralExpression;
import org.uggeri.yapp.generation.stmt.Variable;

/**
 *
 * @author fabio
 */
public abstract class AbstractCCodeFragment implements CodeFragment {

   protected StringBuilder indent(final StringBuilder text, final int indentation) {
      for (int i = 0; i < indentation; i++) {
         text.append(' ');
      }
      return text;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      appendCode(sb, 0);
      return sb.toString();
   }

   public static Expression asExpression(Object o) {
      if (o == null) {
         return new CLiteralExpression<String>("NULL");
      } else if (o instanceof Expression) {
         return (Expression) o;
      } else if (o instanceof String) {
         return quote((String) o);
      } else if (Boolean.class.isInstance(o)) {
         return new CLiteralExpression<String>(((Boolean) o) ? "TRUE" : "FALSE");
      } else if (Character.class.isInstance(o)) {
         return new CLiteralExpression<String>("'" + AbstractParserGenerator.escapeChar((Character) o) + "'");
      } else if (Number.class.isInstance(o)) {
         return new CLiteralExpression<String>(((Number) o).toString());
      }
      return new CLiteralExpression<String>(o.toString());
   }

   public static Variable asVariable(Object o) {
      if (o instanceof Variable) {
         return (Variable) o;
      }
      return new CVariable(o.toString());
   }

   public static LiteralExpression<String> quote(String value) {
      return new CLiteralExpression<String>("\"" + value + "\"");
   }
}
