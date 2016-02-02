/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.FunctionCall;

/**
 *
 * @author fabio
 */
public class CFunctionCall extends CExpression implements FunctionCall {

   private final String functioName;
   
   private final Expression[] parameters;

   public CFunctionCall(String functioName, Expression[] parameters) {
      this.functioName = functioName;
      this.parameters = parameters;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      boolean first = true;
      indent(text, indentation).append(functioName).append('(');
      for (Expression par : parameters) {
         if (first) {
            first = false;
         } else {
            text.append(", ");
         }
         par.appendCode(text, 0);
      }
      text.append(')');
   }

   @Override
   public String getFunctionName() {
      return functioName;
   }

   @Override
   public Expression[] getParameters() {
      return parameters;
   }

   @Override
   public boolean containsVariableReference(String varName) {
      for (Expression par : parameters) {
         if (par.containsVariableReference(varName)) {
            return true;
         }
      }
      return false;
   }
   
}
