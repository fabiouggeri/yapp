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
public class Position {

   private final int line;
   private final int column;

   public Position(int line, int column) {
      this.line = line;
      this.column = column;
   }

   public int getLine() {
      return line;
   }

   public int getColumn() {
      return column;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Position)) {
         return false;
      }
      return column == ((Position) o).column && line == ((Position) o).line;

   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 83 * hash + this.line;
      hash = 83 * hash + this.column;
      return hash;
   }
}
