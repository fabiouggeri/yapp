/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java;

import org.uggeri.yapp.generation.AbstractCodeOptimizer;
import org.uggeri.yapp.generation.java.stmt.JavaStatementCode;
import org.uggeri.yapp.generation.java.stmt.JavaIfStatement;
import org.uggeri.yapp.generation.java.stmt.JavaLiteralExpression;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.StatementCode;
import org.uggeri.yapp.generation.stmt.IfStatement;
import org.uggeri.yapp.generation.stmt.LiteralExpression;

/**
 *
 * @author fabio
 */
public class JavaCodeOptimizer extends AbstractCodeOptimizer {

   private final static LiteralExpression<String> FALSE_LITERAL = new JavaLiteralExpression<String>("false");
   
   private final static LiteralExpression<String> TRUE_LITERAL = new JavaLiteralExpression<String>("true");
   
   @Override
   protected IfStatement createIfStatement(Expression testExpression) {
      return new JavaIfStatement(testExpression);
   }

   @Override
   protected StatementCode createExecutionStatement(Expression assignExpression) {
      return new JavaStatementCode(assignExpression);
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
