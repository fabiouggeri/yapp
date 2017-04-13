/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

/**
 *
 * @author fabio_uggeri
 */
public class IntArrayStack {

   public static class UnderflowException extends RuntimeException {

      public UnderflowException(String message) {
         super(message);
      }
   }

   private static final int INITIAL_CAPACITY = 16;
   private int[] array;
   private int top;

   public IntArrayStack(int iniCap) {
      array = new int[iniCap < 0 ? INITIAL_CAPACITY : iniCap];
      top = -1;
   }

   public IntArrayStack() {
      this(INITIAL_CAPACITY);
   }

   /**
    * Tests if the stack is empty.
    *
    * @return true if empty, false otherwise.
    */
   public boolean isEmpty() {
      return top == -1;
   }

   /**
    * Returns the number of element currently on the stack.
    *
    * @return the number of element currently on the stack
    */
   public int size() {
      return top + 1;
   }

   /**
    * Copies all elements currently on the stack into the given array.
    *
    * @param destArray the array
    * @param destStartIndex the index to start copying into
    */
   public void getElements(int[] destArray, int destStartIndex) {
      System.arraycopy(array, 0, destArray, destStartIndex, size());
   }

   /**
    * @return all elements in a new array.
    */
   public int[] toArray() {
      int[] array = new int[size()];
      getElements(array, 0);
      return array;
   }

   /**
    * Empties the stack.
    */
   public void clear() {
      top = -1;
   }

   /**
    * Returns the item at the top of the stack without removing it.
    *
    * @return the most recently inserted item in the stack.
    * @throws UnderflowException if the stack is empty.
    */
   public int peek() {
      if (isEmpty()) {
         throw new UnderflowException("IntArrayStack peek");
      }
      return array[top];
   }

   /**
    * Removes the most recently inserted item from the stack.
    *
    * @return the top stack item
    * @throws UnderflowException if the stack is empty.
    */
   public int pop() {
      if (isEmpty()) {
         throw new UnderflowException("IntArrayStack pop");
      }
      return array[top--];
   }

   /**
    * Pushes a new item onto the stack.
    *
    * @param x the item to add.
    */
   public void push(int x) {
      if (top == array.length - 1) {
         expandCapacity();
      }
      array[++top] = x;
   }

   private void expandCapacity() {
      int[] newArray = new int[array.length * 2];
      System.arraycopy(array, 0, newArray, 0, array.length);
      array = newArray;
   }
}
