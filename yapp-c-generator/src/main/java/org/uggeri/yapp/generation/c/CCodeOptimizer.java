/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c;

import org.uggeri.yapp.generation.AbstractCodeOptimizer;
import org.uggeri.yapp.generation.c.stmt.CStatementCode;
import org.uggeri.yapp.generation.c.stmt.CIfStatement;
import org.uggeri.yapp.generation.c.stmt.CLiteralExpression;
import org.uggeri.yapp.generation.stmt.StatementCode;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.IfStatement;
import org.uggeri.yapp.generation.stmt.LiteralExpression;

/**
 *
 * @author fabio
 */
public class CCodeOptimizer extends AbstractCodeOptimizer {

   private final static LiteralExpression<String> FALSE_LITERAL = new CLiteralExpression<String>("FALSE");
   
   private final static LiteralExpression<String> TRUE_LITERAL = new CLiteralExpression<String>("TRUE");
   
   @Override
   protected IfStatement createIfStatement(Expression expr) {
      return new CIfStatement(expr);
   }

   @Override
   protected StatementCode createExecutionStatement(Expression expr) {
      return new CStatementCode(expr);
   }

   @Override
   protected LiteralExpression trueValue() {
      return TRUE_LITERAL;
   }

   @Override
   protected LiteralExpression falseValue() {
      return FALSE_LITERAL;
   }
   
}
