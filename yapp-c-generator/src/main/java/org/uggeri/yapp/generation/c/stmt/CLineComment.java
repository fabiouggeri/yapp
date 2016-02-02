/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.LineComment;
import org.uggeri.yapp.generation.stmt.Statement;

/**
 *
 * @author fabio
 */
public class CLineComment extends AbstractCCodeFragment implements LineComment, Statement {

   private final String comment;

   public CLineComment(String comment) {
      this.comment = comment;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("//").append(comment);
   }
   
}
