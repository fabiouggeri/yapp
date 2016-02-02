/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.WhileStatement;

/**
 *
 * @author fabio
 */
public class JavaWhileStatement extends JavaConditionalBlockStatement implements WhileStatement {

   public JavaWhileStatement(Expression expressionTest) {
      super(expressionTest);
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("while(");
      getTestExpression().appendCode(text, 0);
      text.append(") {");
      for (CodeFragment code : listStatements()) {
         code.appendCode(text, indentation + 3);
      }
      text.append('\n');
      indent(text, indentation).append('}');
   }
}
