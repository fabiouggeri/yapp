/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.ReturnStatement;

/**
 *
 * @author fabio
 */
public class CReturnStatement extends AbstractCCodeFragment implements ReturnStatement {

   private Expression expression;

   public CReturnStatement(Expression expression) {
      this.expression = expression;
   }
   
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("return ");
      expression.appendCode(text, 0);
      text.append(';');
   }

   @Override
   public Expression getExpression() {
      return expression;
   }
   
}
