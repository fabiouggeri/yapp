/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.ReturnStatement;

/**
 *
 * @author fabio
 */
public class JavaReturnStatement extends AbstractJavaCodeFragment implements ReturnStatement {

   private final Expression expression;

   public JavaReturnStatement(Expression expression) {
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
