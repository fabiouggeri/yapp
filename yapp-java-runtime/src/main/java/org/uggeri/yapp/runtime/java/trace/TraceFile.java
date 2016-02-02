/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.trace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author fabio
 */
public class TraceFile extends AbstractTraceParser {

   private final Writer writer;

   public TraceFile(File file) throws IOException {
      this.writer = new BufferedWriter(new FileWriter(file));
   }
   
   @Override
   protected AbstractTraceParser append(CharSequence text) {
      try {
         writer.append(text);
      } catch (IOException ex) {
      }
      return this;
   }

   @Override
   protected AbstractTraceParser append(char c) {
      try {
         writer.append(Character.toString(c));
      } catch (IOException ex) {
      }
      return this;
   }

   @Override
   protected AbstractTraceParser append(int value) {
      try {
         writer.append(Integer.toString(value));
      } catch (IOException ex) {
      }
      return this;
   }

   @Override
   protected AbstractTraceParser append(long value) {
      try {
         writer.append(Long.toString(value));
      } catch (IOException ex) {
      }
      return this;
   }

   @Override
   protected AbstractTraceParser append(boolean value) {
      try {
         writer.append(Boolean.toString(value));
      } catch (IOException ex) {
      }
      return this;
   }

   @Override
   public void close() throws IOException {
      if (writer != null) {
         writer.close();
      }
   }
   
}
