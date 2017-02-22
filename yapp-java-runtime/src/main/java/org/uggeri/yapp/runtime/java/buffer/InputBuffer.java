/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

/**
 *
 * @author fabio
 */
public interface InputBuffer {

   public CharSequence getText(int startIndex, int endIndex);

   public CharSequence subsequence(int startIndex);
   
   public char getChar(int index);
   
   public boolean matchChar(int index, char c);

   public boolean matchCharRange(int index, char charIni, char charEnd);
   
   public boolean matchIgnoreCaseChar(int index, char c);
 
   public boolean matchString(int index, String str, int strLen);
   
   public boolean matchIgnoreCaseString(int index, String str, int strLen);
   
   public boolean isEoi(int index);
   
   public int length();
   
   public Position getPosition(int index);
   
   public int lineLength(int line);
}
