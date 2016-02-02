/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.Variable;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;

/**
 *
 * @author fabio
 */
public class CVariable extends CExpression implements Variable {
   
   private String name;

   public CVariable(String name) {
      this.name = name;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation).append(name);
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public VariableDeclaration declare(DataType dataType) {
      return new CVariableDeclaration(dataType, this);
   }

   @Override
   public VariableDeclaration declare(DataType dataType, Expression initialization) {
      return new CVariableDeclaration(dataType, this, initialization);
   }

   @Override
   public boolean containsVariableReference(String varName) {
     return name.equals(varName);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Variable) {
         return name.equals(((Variable)obj).getName());
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
      return hash;
   }
}
