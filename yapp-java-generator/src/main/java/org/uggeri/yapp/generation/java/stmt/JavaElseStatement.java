/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.ElseStatement;

/**
 *
 * @author fabio
 */
public class JavaElseStatement extends JavaBlockStatement implements ElseStatement {

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append(" else {");
      for (CodeFragment code : listStatements()) {
         code.appendCode(text, indentation + 3);
      }
      text.append('\n');
      indent(text, indentation).append('}');
   }
   
}
