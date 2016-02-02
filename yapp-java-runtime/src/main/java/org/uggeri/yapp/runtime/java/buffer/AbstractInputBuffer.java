/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

/**
 *
 * @author fabio
 */
public abstract class AbstractInputBuffer implements InputBuffer {

   protected abstract CharSequence getBuffer();
   
   @Override
   public char getChar(int index) {
      if (index >= 0 && index < getBuffer().length()) {
         return getBuffer().charAt(index);
      }
      return '\0';
   }

   @Override
   public CharSequence getText(int starIndex, int endIndex) {
      return getBuffer().subSequence(starIndex, endIndex);
   }

   @Override
   public boolean isEoi(int index) {
      return index >= getBuffer().length();
   }

   @Override
   public boolean matchIgnoreCaseString(final int index, final String str, int strLen) {
      if (strLen > getBuffer().length() - index) {
         return false;
      }
      for (int i1 = index, i2 = 0; i2 < strLen; i1++, i2++) {
         if (Character.toLowerCase(getBuffer().charAt(i1)) != Character.toLowerCase(str.charAt(i2))) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean matchCharRange(int index, char charIni, char charEnd) {
      if (index < getBuffer().length()) {
         final char c = getBuffer().charAt(index);
         return c >= charIni && c <= charEnd;
      }
      return false;
   }

   @Override
   public boolean matchChar(final int index, final char c) {
      return index < getBuffer().length() && getBuffer().charAt(index) == c;
   }
   
   @Override
   public boolean matchIgnoreCaseChar(final int index, final char c) {
      return index < getBuffer().length() && Character.toLowerCase(getBuffer().charAt(index)) == Character.toLowerCase(c);
   }
   
   @Override
   public boolean matchString(final int index, final String str, int strLen) {
      if (strLen > getBuffer().length() - index) {
         return false;
      }
      for (int i1 = index, i2 = 0; i2 < strLen; i1++, i2++) {
         if (getBuffer().charAt(i1) != str.charAt(i2)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public int length() {
      return getBuffer().length();
   }

   @Override
   public CharSequence subsequence(int index) {
      if (index < getBuffer().length()) {
         return new BufferSubsequence(index);
      }
      return "";
   }
   
   private final class BufferSubsequence implements CharSequence {

      private final int indexBase;

      public BufferSubsequence(int index) {
         this.indexBase = index;
      }
      
      @Override
      public int length() {
         return getBuffer().length() - indexBase;
      }

      @Override
      public char charAt(int index) {
         return getBuffer().charAt(indexBase + index);
      }

      @Override
      public CharSequence subSequence(int start, int end) {
         return new BufferSubsequence(indexBase + start);
      }
      
   }

}
