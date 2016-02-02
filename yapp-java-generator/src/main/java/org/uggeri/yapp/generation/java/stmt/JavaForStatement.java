/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.ForStatement;

/**
 *
 * @author fabio
 */
public class JavaForStatement extends JavaBlockStatement implements ForStatement {

   private final Expression initExpression;
   private final Expression testExpression;
   private final Expression incrementExpression;

   public JavaForStatement(Expression initExpression, Expression testExpression, Expression incrementExpression) {
      this.initExpression = initExpression;
      this.testExpression = testExpression;
      this.incrementExpression = incrementExpression;
   }
   
   @Override
   public Expression getInitExpression() {
      return initExpression;
   }

   @Override
   public Expression getTestExpression() {
      return testExpression;
   }

   @Override
   public Expression getIncrementExpression() {
      return incrementExpression;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("for (");
      getInitExpression().appendCode(text, 0);
      text.append("; ");
      getTestExpression().appendCode(text, 0);
      text.append("; ");
      getIncrementExpression().appendCode(text, 0);
      text.append(")");
      super.appendCode(text, indentation);
   }
}
