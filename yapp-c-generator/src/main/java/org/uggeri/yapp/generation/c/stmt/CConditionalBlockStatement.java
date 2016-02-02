/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.ConditionalBlockStatement;
import org.uggeri.yapp.generation.stmt.Expression;

/**
 *
 * @author fabio
 */
public class CConditionalBlockStatement extends CBlockStatement implements ConditionalBlockStatement {
   
   private final Expression expressionTest;

   public CConditionalBlockStatement(Expression expressionTest) {
      this.expressionTest = expressionTest;
   }

   @Override
   public Expression getTestExpression() {
      return expressionTest;
   }
   
}