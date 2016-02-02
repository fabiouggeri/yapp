/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author fabio
 */
public class FileInputBuffer extends AbstractInputBuffer {

   private static final int DEFAULT_BUFFER_SIZE = 4096;//8192;

   private int bufferSize;

   private File file;
   
   private final CharSequence buffer;
   
   public FileInputBuffer(File file, int bufferSize) throws IOException {
      this.file = file;
      this.bufferSize = bufferSize;
      buffer = readFileContent(file);
   }
   
   public FileInputBuffer(File file) throws IOException {
      this(file, DEFAULT_BUFFER_SIZE);
   }
   
   public File getFile() {
      return file;
   }

   @Override
   protected CharSequence getBuffer() {
      return buffer;
   }

   private CharSequence readFileContent(File file) throws IOException {
      FileReader reader = null;
      StringBuilder fileContent;
      try {
         final char[] chunk = new char[bufferSize];
         int charsRead;
         reader = new FileReader(file);
         fileContent = new StringBuilder((int)file.length());
         charsRead = reader.read(chunk);
         while (charsRead >= 0) {
            fileContent.append(chunk, 0, charsRead);
            charsRead = reader.read(chunk);
         }
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
      return fileContent;
   }
   
}