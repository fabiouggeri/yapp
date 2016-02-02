/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.runtime.java.parser;

/**
 *
 * @author fabio
 */
public class RuleProfile {

   private Rule rule;
   
   private long memoMatchCount = 0L; 
   
   private long memoMatchTimeMillis = 0L; 
   
   private long memoMismatchCount = 0L; 
   
   private long memoMismatchTimeMillis = 0L; 

   private long matchCount = 0L; 
   
   private long matchTimeMillis = 0L; 
   
   private long mismatchCount = 0L; 
   
   private long mismatchTimeMillis = 0L; 
   
   public RuleProfile(Rule rule) {
      this.rule = rule;
   }

   public Rule getRule() {
      return rule;
   }

   /**
    * @return the memoMatchCount
    */
   public long getMemoMatchCount() {
      return memoMatchCount;
   }

   public long getMemoMatchTimeMillis() {
      return memoMatchTimeMillis;
   }

   /**
    * @return the memoMismatchCount
    */
   public long getMemoMismatchCount() {
      return memoMismatchCount;
   }

   public long getMemoMismatchTimeMillis() {
      return memoMismatchTimeMillis;
   }

   /**
    * @return the matchCount
    */
   public long getMatchCount() {
      return matchCount;
   }

   public long getMatchTimeMillis() {
      return matchTimeMillis;
   }

   /**
    * @return the mismatchCount
    */
   public long getMismatchCount() {
      return mismatchCount;
   }

   public long getMismatchTimeMillis() {
      return mismatchTimeMillis;
   }

   public void incMemoMatchCount(long spentTimeMillis) {
      memoMatchTimeMillis += spentTimeMillis;
      ++memoMatchCount;
   }
   
   public void incMemoMatchCount() {
      ++memoMatchCount;
   }
   
   public void incMemoMismatchCount(long spentTimeMillis) {
      memoMismatchTimeMillis += spentTimeMillis;
      ++memoMismatchCount;
   }
   
   public void incMemoMismatchCount() {
      ++memoMismatchCount;
   }

   public void incMatchCount(long spentTimeMillis) {
      matchTimeMillis += spentTimeMillis;
      ++matchCount;
   }

   public void incMatchCount() {
      ++matchCount;
   }

   public void incMismatchCount(long spentTimeMillis) {
      mismatchTimeMillis += spentTimeMillis;
      ++mismatchCount;
   }
   
   public void incMismatchCount() {
      ++mismatchCount;
   }
}
