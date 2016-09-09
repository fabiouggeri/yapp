/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java.stmt;

import static org.uggeri.yapp.generation.java.stmt.AbstractJavaCodeFragment.asExpression;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.LiteralExpression;

/**
 *
 * @author fabio
 * @param <T>
 */
public class JavaLiteralExpression<T> extends JavaExpression implements LiteralExpression<T> {

   private final T value;

   public JavaLiteralExpression(T value) {
      this.value = value;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation).append(value.toString());
   }

   @Override
   public T getValue() {
      return value;
   }

   @Override
   public Expression member(Object expr) {
      if (value.toString().equals("this")) {
         return asExpression(expr);
      } else {
         return super.member(expr);
      }
   }

   @Override
   public boolean containsVariableReference(String varName) {
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof LiteralExpression) {
         return value.equals(((LiteralExpression) obj).getValue());
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
      return hash;
   }
}
