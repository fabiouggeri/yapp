/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.runtime.java.trace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author fabio
 */
public class LimitedTraceFile extends AbstractTraceParser {

   private final File file;
   private final char[] buffer;
   private long size = 0;

   public LimitedTraceFile(File file, int size) {
      this.file = file;
      this.buffer = new char[size];
   }
   
   @Override
   protected AbstractTraceParser append(CharSequence text) {
      try {
         for (int i = 0; i < text.length(); i++) {
            buffer[(int)(size++ % buffer.length)] = text.charAt(i);
         }
      } catch (Exception ex) {
         System.out.printf("size=%d len=%d", size, buffer.length);
         ex.printStackTrace(System.out);
      }
      return this;
   }

   @Override
   protected AbstractTraceParser append(char c) {
      buffer[(int)(size++ % buffer.length)] = c;
      return this;
   }

   @Override
   protected AbstractTraceParser append(int value) {
      append(Integer.toString(value));
      return this;
   }

   @Override
   protected AbstractTraceParser append(long value) {
      append(Long.toString(value));
      return this;
   }

   @Override
   protected AbstractTraceParser append(boolean value) {
      append(Boolean.toString(value));
      return this;
   }

   @Override
   public void close() throws IOException {
      Writer writer = new FileWriter(file);
      if (size <= buffer.length) {
         writer.write(buffer, 0, (int)size);
      } else {
         long start = size % buffer.length + 1L;
         try {
         writer.write(buffer, (int)start, buffer.length - (int)start);
         writer.write(buffer, 0, (int)start);
         } catch(IndexOutOfBoundsException ex) {
            System.out.printf("Size=%d Len=%d Start=%d End=%d\n", size, buffer.length, start, buffer.length-start);
         }
      }
      writer.close();
   }
   
}
