/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.stmt;

/**
 *
 * @author fabio
 */
public interface CodeFragment {
   
   public void appendCode(final StringBuilder text, final int indentation);
   
}
