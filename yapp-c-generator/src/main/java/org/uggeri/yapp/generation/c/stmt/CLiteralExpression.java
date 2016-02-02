/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.LiteralExpression;

/**
 *
 * @author fabio
 */
public class CLiteralExpression<T> extends CExpression implements LiteralExpression<T> {

   private T value;

   public CLiteralExpression(T value) {
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
   public boolean containsVariableReference(String varName) {
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof LiteralExpression) {
         return value.equals(((LiteralExpression)obj).getValue());
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
