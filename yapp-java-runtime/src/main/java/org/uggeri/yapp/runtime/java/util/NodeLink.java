/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.runtime.java.util;

/**
 *
 * @author fabio
 * @param <T>
 */
public class NodeLink<T> {

   private T value;

   private NodeLink<T> previous;

   public NodeLink(T value, NodeLink<T> previous) {
      this.value = value;
      this.previous = previous;
   }

   public T getValue() {
      return value;
   }

   public void setValue(T value) {
      this.value = value;
   }

   public NodeLink<T> getPrevious() {
      return previous;
   }

   public void setPrevious(NodeLink<T> previous) {
      this.previous = previous;
   }

   @Override
   public String toString() {
      return "" + value;
   }
}
