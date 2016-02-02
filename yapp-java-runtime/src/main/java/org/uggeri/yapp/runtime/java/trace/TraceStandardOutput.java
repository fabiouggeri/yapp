/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.trace;

import java.io.IOException;

/**
 *
 * @author fabio
 */
public class TraceStandardOutput extends AbstractTraceParser {

   @Override
   protected AbstractTraceParser append(CharSequence text) {
      System.out.print(text);
      return this;
   }

   @Override
   protected AbstractTraceParser append(char c) {
      System.out.print(c);
      return this;
   }

   @Override
   protected AbstractTraceParser append(int value) {
      System.out.print(value);
      return this;
   }

   @Override
   protected AbstractTraceParser append(long value) {
      System.out.print(value);
      return this;
   }

   @Override
   protected AbstractTraceParser append(boolean value) {
      System.out.print(value);
      return this;
   }

   @Override
   public void close() throws IOException {
   }

}
