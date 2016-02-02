/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.StatementCode;

/**
 *
 * @author fabio
 */
public class JavaStatementCode extends AbstractJavaCodeFragment implements StatementCode {
   
   private final CodeFragment code;

   public JavaStatementCode(CodeFragment code) {
      this.code = code;
   }

   @Override
   public CodeFragment getCode() {
      return code;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation);
      code.appendCode(text, 0);
      text.append(';');
   }
}
