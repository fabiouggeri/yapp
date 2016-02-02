/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.DoWhileStatement;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.Statement;

/**
 *
 * @author fabio
 */
public class JavaDoWhileStatement extends JavaConditionalBlockStatement implements DoWhileStatement {

   public JavaDoWhileStatement(Expression expressionTest) {
      super(expressionTest);
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("do {");
      for (Statement stmt : listStatements()) {
         stmt.appendCode(text, indentation + 3);
      }
      text.append('\n');
      indent(text, indentation).append("} while(");
      getTestExpression().appendCode(text, 0);
      text.append(");");
   }
}
