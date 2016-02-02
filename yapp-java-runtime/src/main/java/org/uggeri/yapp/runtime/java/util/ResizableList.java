/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 *
 * @author fabio
 * @param <T>
 */
public class ResizableList<T> implements List<T> {

   private Object[] values;
   private int size = 0;
   protected transient int modCount = 0;

   public ResizableList(int initialCapcity) {
      values = new Object[initialCapcity];
   }

   public ResizableList() {
      this(8);
   }

   private void ensureCapacity(int minCapacity) {
      // overflow-conscious code
      if (minCapacity - values.length > 0) {
         grow(minCapacity);
      }
   }

   private void grow(int minCapacity) {
      // overflow-conscious code
      int oldCapacity = values.length;
      int newCapacity = oldCapacity + (oldCapacity >> 1);
      if (newCapacity - minCapacity < 0) {
         newCapacity = minCapacity;
      }
      values = Arrays.copyOf(values, newCapacity);
   }

   public void setSize(int size) {
      ++modCount;
      this.size = size;
   }

   @Override
   public int size() {
      return size;
   }

   @Override
   public boolean isEmpty() {
      return size == 0;
   }

   @Override
   public boolean contains(Object o) {
      return indexOf(o) >= 0;
   }

   @Override
   public Iterator<T> iterator() {
      return new Itr();
   }

   @Override
   public Object[] toArray() {
      return Arrays.copyOf(values, size);
   }

   @Override
   public <T> T[] toArray(T[] a) {
      if (a.length < size) {
         // Make a new array of a's runtime type, but my contents:
         return (T[]) Arrays.copyOf(values, size, a.getClass());
      }
      System.arraycopy(values, 0, a, 0, size);
      if (a.length > size) {
         a[size] = null;
      }
      return a;
   }

   @Override
   public boolean add(T e) {
      ensureCapacity(++size);  // Increments modCount!!
      values[size - 1] = e;
      return true;
   }

   protected void removeRange(int fromIndex, int toIndex) {
      int numMoved = size - toIndex;
      ++modCount;
      System.arraycopy(values, toIndex, values, fromIndex, numMoved);

      // Let gc do its work
      int newSize = size - (toIndex - fromIndex);
      while (size != newSize) {
         values[--size] = null;
      }
   }

   @Override
   public boolean remove(Object o) {
      if (o == null) {
         for (int index = 0; index < size; index++) {
            if (values[index] == null) {
               remove(index);
               return true;
            }
         }
      } else {
         for (int index = 0; index < size; index++) {
            if (o.equals(values[index])) {
               remove(index);
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      for (Object e : c) {
         if (!contains(e)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      Object[] a = c.toArray();
      int numNew = a.length;
      ensureCapacity(size + numNew);  // Increments modCount
      System.arraycopy(a, 0, values, size, numNew);
      size += numNew;
      return numNew != 0;
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      Object[] a = c.toArray();
      int numNew = a.length;
      ensureCapacity(size + numNew);  // Increments modCount

      int numMoved = size - index;
      if (numMoved > 0) {
         System.arraycopy(values, index, values, index + numNew, numMoved);
      }

      System.arraycopy(a, 0, values, index, numNew);
      size += numNew;
      return numNew != 0;
   }

   private boolean batchRemove(Collection<?> c, boolean complement) {
      final Object[] elementData = this.values;
      int r = 0, w = 0;
      boolean modified = false;
      try {
         for (; r < size; r++) {
            if (c.contains(elementData[r]) == complement) {
               elementData[w++] = elementData[r];
            }
         }
      } finally {
         // Preserve behavioral compatibility with AbstractCollection,
         // even if c.contains() throws.
         if (r != size) {
            System.arraycopy(elementData, r,
                    elementData, w,
                    size - r);
            w += size - r;
         }
         if (w != size) {
            for (int i = w; i < size; i++) {
               elementData[i] = null;
            }
            modCount += size - w;
            size = w;
            modified = true;
         }
      }
      return modified;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return batchRemove(c, false);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return batchRemove(c, true);
   }

   @Override
   public void clear() {
      ++modCount;
      size = 0;
   }

   @Override
   public T get(int index) {
      return (T) values[index];
   }

   @Override
   public T set(int index, T element) {
      T oldValue = (T) values[index];
      values[index] = element;
      return oldValue;
   }

   @Override
   public void add(int index, T element) {
      ensureCapacity(size + 1);  // Increments modCount!!
      System.arraycopy(values, index, values, index + 1, size - index);
      values[index] = element;
      ++size;
   }

   @Override
   public T remove(int index) {
      ++modCount;
      T oldValue = (T) values[index];

      int numMoved = size - index - 1;
      if (numMoved > 0) {
         System.arraycopy(values, index + 1, values, index, numMoved);
      }
      values[--size] = null; // Let gc do its work

      return oldValue;
   }

   @Override
   public int indexOf(Object o) {
      if (o == null) {
         for (int i = 0; i < size; i++) {
            if (values[i] == null) {
               return i;
            }
         }
      } else {
         for (int i = 0; i < size; i++) {
            if (o.equals(values[i])) {
               return i;
            }
         }
      }
      return -1;
   }

   @Override
   public int lastIndexOf(Object o) {
      if (o == null) {
         for (int i = size - 1; i >= 0; i--) {
            if (values[i] == null) {
               return i;
            }
         }
      } else {
         for (int i = size - 1; i >= 0; i--) {
            if (o.equals(values[i])) {
               return i;
            }
         }
      }
      return -1;
   }

   @Override
   public ListIterator<T> listIterator() {
      return new ListItr(0);
   }

   @Override
   public ListIterator<T> listIterator(int index) {
      if (index < 0 || index > size) {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
      return new ListItr(index);
   }

   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      subListRangeCheck(fromIndex, toIndex, size);
      return new SubList(this, 0, fromIndex, toIndex);
   }

   static void subListRangeCheck(int fromIndex, int toIndex, int size) {
      if (fromIndex < 0) {
         throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      }
      if (toIndex > size) {
         throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      }
      if (fromIndex > toIndex) {
         throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
      }
   }

   /**
    * An optimized version of AbstractList.Itr
    */
   private class Itr implements Iterator<T> {

      int cursor;       // index of next element to return
      int lastRet = -1; // index of last element returned; -1 if no such
      int expectedModCount = modCount;

      @Override
      public boolean hasNext() {
         return cursor != size;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T next() {
         checkForComodification();
         int i = cursor;
         if (i >= size) {
            throw new NoSuchElementException();
         }
         Object[] values = ResizableList.this.values;
         if (i >= values.length) {
            throw new ConcurrentModificationException();
         }
         cursor = i + 1;
         return (T) values[lastRet = i];
      }

      @Override
      public void remove() {
         if (lastRet < 0) {
            throw new IllegalStateException();
         }
         checkForComodification();

         try {
            ResizableList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = modCount;
         } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
         }
      }

      final void checkForComodification() {
         if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }
   }

   /**
    * An optimized version of AbstractList.ListItr
    */
   private class ListItr extends Itr implements ListIterator<T> {

      ListItr(int index) {
         super();
         cursor = index;
      }

      @Override
      public boolean hasPrevious() {
         return cursor != 0;
      }

      @Override
      public int nextIndex() {
         return cursor;
      }

      @Override
      public int previousIndex() {
         return cursor - 1;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T previous() {
         checkForComodification();
         int i = cursor - 1;
         if (i < 0) {
            throw new NoSuchElementException();
         }
         Object[] elementData = ResizableList.this.values;
         if (i >= elementData.length) {
            throw new ConcurrentModificationException();
         }
         cursor = i;
         return (T) elementData[lastRet = i];
      }

      @Override
      public void set(T e) {
         if (lastRet < 0) {
            throw new IllegalStateException();
         }
         checkForComodification();

         try {
            ResizableList.this.set(lastRet, e);
         } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
         }
      }

      @Override
      public void add(T e) {
         checkForComodification();

         try {
            int i = cursor;
            ResizableList.this.add(i, e);
            cursor = i + 1;
            lastRet = -1;
            expectedModCount = modCount;
         } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
         }
      }
   }

   private class SubList extends ResizableList<T> implements RandomAccess {

      private final ResizableList<T> parent;
      private final int parentOffset;
      private final int offset;
      int size;

      SubList(ResizableList<T> parent, int offset, int fromIndex, int toIndex) {
         this.parent = parent;
         this.parentOffset = fromIndex;
         this.offset = offset + fromIndex;
         this.size = toIndex - fromIndex;
         this.modCount = ResizableList.this.modCount;
      }

      @Override
      public T set(int index, T e) {
         rangeCheck(index);
         checkForComodification();
         T oldValue = (T) ResizableList.this.values[offset + index];
         ResizableList.this.values[offset + index] = e;
         return oldValue;
      }

      @Override
      public T get(int index) {
         rangeCheck(index);
         checkForComodification();
         return (T) ResizableList.this.values[offset + index];
      }

      @Override
      public int size() {
         checkForComodification();
         return this.size;
      }

      @Override
      public void add(int index, T e) {
         rangeCheckForAdd(index);
         checkForComodification();
         parent.add(parentOffset + index, e);
         this.modCount = parent.modCount;
         this.size++;
      }

      @Override
      public T remove(int index) {
         rangeCheck(index);
         checkForComodification();
         T result = parent.remove(parentOffset + index);
         this.modCount = parent.modCount;
         this.size--;
         return result;
      }

      @Override
      protected void removeRange(int fromIndex, int toIndex) {
         checkForComodification();
         parent.removeRange(parentOffset + fromIndex, parentOffset + toIndex);
         this.modCount = parent.modCount;
         this.size -= toIndex - fromIndex;
      }

      @Override
      public boolean addAll(Collection<? extends T> c) {
         return addAll(this.size, c);
      }

      @Override
      public boolean addAll(int index, Collection<? extends T> c) {
         rangeCheckForAdd(index);
         int cSize = c.size();
         if (cSize == 0) {
            return false;
         }

         checkForComodification();
         parent.addAll(parentOffset + index, c);
         this.modCount = parent.modCount;
         this.size += cSize;
         return true;
      }

      @Override
      public Iterator<T> iterator() {
         return listIterator();
      }

      @Override
      public ListIterator<T> listIterator(final int index) {
         checkForComodification();
         rangeCheckForAdd(index);
         final int itOffset = this.offset;

         return new ListIterator<T>() {
            int cursor = index;
            int lastRet = -1;
            int expectedModCount = ResizableList.this.modCount;

            @Override
            public boolean hasNext() {
               return cursor != ResizableList.SubList.this.size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
               checkForComodification();
               int i = cursor;
               if (i >= ResizableList.SubList.this.size) {
                  throw new NoSuchElementException();
               }
               Object[] elementData = ResizableList.this.values;
               if (itOffset + i >= elementData.length) {
                  throw new ConcurrentModificationException();
               }
               cursor = i + 1;
               return (T) elementData[itOffset + (lastRet = i)];
            }

            @Override
            public boolean hasPrevious() {
               return cursor != 0;
            }

            @SuppressWarnings("unchecked")
            @Override
            public T previous() {
               checkForComodification();
               int i = cursor - 1;
               if (i < 0) {
                  throw new NoSuchElementException();
               }
               Object[] elementData = ResizableList.this.values;
               if (itOffset + i >= elementData.length) {
                  throw new ConcurrentModificationException();
               }
               cursor = i;
               return (T) elementData[itOffset + (lastRet = i)];
            }

            @Override
            public int nextIndex() {
               return cursor;
            }

            @Override
            public int previousIndex() {
               return cursor - 1;
            }

            @Override
            public void remove() {
               if (lastRet < 0) {
                  throw new IllegalStateException();
               }
               checkForComodification();

               try {
                  ResizableList.SubList.this.remove(lastRet);
                  cursor = lastRet;
                  lastRet = -1;
                  expectedModCount = ResizableList.this.modCount;
               } catch (IndexOutOfBoundsException ex) {
                  throw new ConcurrentModificationException();
               }
            }

            @Override
            public void set(T e) {
               if (lastRet < 0) {
                  throw new IllegalStateException();
               }
               checkForComodification();

               try {
                  ResizableList.this.set(itOffset + lastRet, e);
               } catch (IndexOutOfBoundsException ex) {
                  throw new ConcurrentModificationException();
               }
            }

            @Override
            public void add(T e) {
               checkForComodification();

               try {
                  int i = cursor;
                  ResizableList.SubList.this.add(i, e);
                  cursor = i + 1;
                  lastRet = -1;
                  expectedModCount = ResizableList.this.modCount;
               } catch (IndexOutOfBoundsException ex) {
                  throw new ConcurrentModificationException();
               }
            }

            final void checkForComodification() {
               if (expectedModCount != ResizableList.this.modCount) {
                  throw new ConcurrentModificationException();
               }
            }
         };
      }

      @Override
      public List<T> subList(int fromIndex, int toIndex) {
         subListRangeCheck(fromIndex, toIndex, size);
         return new ResizableList.SubList(this, offset, fromIndex, toIndex);
      }

      private void rangeCheck(int index) {
         if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
         }
      }

      private void rangeCheckForAdd(int index) {
         if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
         }
      }

      private String outOfBoundsMsg(int index) {
         return "Index: " + index + ", Size: " + this.size;
      }

      private void checkForComodification() {
         if (ResizableList.this.modCount != this.modCount) {
            throw new ConcurrentModificationException();
         }
      }
   }
   
   @Override
    public String toString() {
        if (size == 0)
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            T e = (T) values[i];
            if (i > 0) {
               sb.append(", ");
            }
            sb.append(e == this ? "(this Collection)" : e);
        }
        return sb.append(']').toString();
    }
}
