/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.node.Node;
import org.uggeri.yapp.runtime.java.node.NodeVisitor;

/**
 *
 * @author fabio
 * @param <T>
 */
public interface Rule<T extends NodeVisitor> {

   public final static Rule<NodeVisitor> TERMINAL = new Rule<NodeVisitor>() {

      @Override
      public String getLabel() {
         return "Terminal";
      }

      @Override
      public int getValue() {
         return Integer.MAX_VALUE;
      }

      @Override
      public void enterRule(NodeVisitor visitor, Node node) {
      }

      @Override
      public void exitRule(NodeVisitor visitor, Node node) {
      }

      @Override
      public boolean isAtomic() {
         return true;
      }

      @Override
      public boolean isSkiped() {
         return false;
      }

      @Override
      public String toString() {
         return "TERMINAL";
      }
   };

   public String getLabel();

   public int getValue();

   public boolean isAtomic();

   public boolean isSkiped();

   public void enterRule(T visitor, Node node);

   public void exitRule(T visitor, Node node);

}
