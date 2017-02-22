/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

import java.util.Arrays;

/**
 *
 * @author fabio
 */
public abstract class AbstractInputBuffer implements InputBuffer {

   protected abstract CharSequence getBuffer();

   private int[] newlines;

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

   @Override
   public Position getPosition(int index) {
      buildNewlines();
      int line = getLine0(newlines, index);
      int column = index - (line > 0 ? newlines[line - 1] : -1);
      return new Position(line + 1, column);
   }

   @Override
   public int lineLength(int line) {
      buildNewlines();
      int start = line > 1 ? newlines[line - 2] + 1 : 0;
      int end = line <= newlines.length ? newlines[line - 1] : length();
      if (getBuffer().charAt(end - 1) == '\r') {
         end--;
      }
      return end - start;
   }

   private static int getLine0(int[] newlines, int index) {
      int j = Arrays.binarySearch(newlines, index);
      return j >= 0 ? j : -(j + 1);
   }

   private void buildNewlines() {
      if (newlines == null) {
         final int len = getBuffer().length();
         final IntArrayStack newlinesStack = new IntArrayStack((int) len / 50);
         for (int i = 0; i < len; i++) {
            if (getBuffer().charAt(i) == '\n') {
               newlinesStack.push(i);
            }
         }
         this.newlines = new int[newlinesStack.size()];
         newlinesStack.getElements(this.newlines, 0);
      }
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
