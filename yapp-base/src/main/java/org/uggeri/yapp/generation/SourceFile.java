/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation;

import java.io.File;
import java.net.URI;

/**
 *
 * @author fabio
 */
public class SourceFile extends File {
   
   private CharSequence content;

   public SourceFile(final String pathname, final CharSequence content) {
      super(pathname);
      this.content = content;
   }

   public SourceFile(final File parent, final String child, final CharSequence content) {
      super(parent, child);
      this.content = content;
   }

   public SourceFile(final String parent, final String child, final CharSequence content) {
      super(parent, child);
      this.content = content;
   }

   public SourceFile(final URI uri, final CharSequence content) {
      super(uri);
      this.content = content;
   }

   public SourceFile(final String pathname) {
      this(pathname, null);
   }

   public SourceFile(final File parent, final String child) {
      this(parent, child, null);
   }

   public SourceFile(final String parent, final String child) {
      this(parent, child, null);
   }

   public SourceFile(final URI uri) {
      this(uri, null);
   }

   public CharSequence getContent() {
      return content;
   }

   public void setContent(CharSequence content) {
      this.content = content;
   }
}
