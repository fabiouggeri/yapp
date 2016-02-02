/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.trace;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.parser.ParserContext;

/**
 *
 * @author fabio
 */
public abstract class AbstractTraceParser implements TraceParser {

   protected TraceElement currentElement = null;

   protected abstract AbstractTraceParser append(CharSequence text);

   protected abstract AbstractTraceParser append(char c);

   protected abstract AbstractTraceParser append(int value);

   protected abstract AbstractTraceParser append(long value);

   protected abstract AbstractTraceParser append(boolean value);

   @Override
   public void enterRule(final InputBuffer inputBuffer, final String ruleName, final int index) {
      currentElement = new TraceElement(currentElement, ruleName);
   }

   @Override
   public void exitRule(final InputBuffer inputBuffer, final int index, boolean success) {
      final int startIndex = calcStartIndex(inputBuffer, index);
      append(startIndex).append(" -> ");
      currentElement.printPath();
      if (success) {
         append(" matched in ");
      } else {
         append(" failed in ");
      }
      append(System.currentTimeMillis() - currentElement.timeMillis);
      append("ms : '");
      append(inputBuffer.getText(startIndex, index));
      append("'");
      append('\n');
      currentElement = currentElement.parent;
   }

   public int calcStartIndex(InputBuffer buffer, int endIndex) {
      int startIndex = endIndex - 1;
      while (startIndex > 0) {
         if (buffer.getChar(startIndex) == '\n') {
            return startIndex + 1;
         }
         --startIndex;
      }
      return 0;
   }

   public class TraceElement {
      private TraceElement parent;
      private final String ruleName;
      private final long timeMillis;

      public TraceElement(TraceElement parent, String ruleName) {
         this.parent = parent;
         this.ruleName = ruleName;
         this.timeMillis = System.currentTimeMillis();
      }

      void printPath() {
         if (parent != null) {
            parent.printPath();
            append('/');
         }
         append(ruleName);
      }

      public long getTimeMillis() {
         return timeMillis;
      }

      public String getRuleName() {
         return ruleName;
      }

      public TraceElement getParent() {
         return parent;
      }
   }

}
