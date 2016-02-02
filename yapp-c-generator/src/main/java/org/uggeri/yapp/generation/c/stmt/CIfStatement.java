/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.IfStatement;

/**
 *
 * @author fabio
 */
public class CIfStatement extends CConditionalBlockStatement implements IfStatement {

   public CIfStatement(Expression expressionTest) {
      super(expressionTest);
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("if (");
      getTestExpression().appendCode(text, 0);
      text.append(')');
      super.appendCode(text, indentation);
   }
}
