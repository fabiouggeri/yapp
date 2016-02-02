/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

/**
 *
 * @author fabio
 */
public enum NonTerminalOption {

   /* Indica que a regra nao deve criar um noh semantico */
   SYNTAX_ONLY {
      @Override
      public boolean isOptionName(final String optionName) {
         return "SyntaxOnly".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   /* Indica que nao deve criar nos para as sub-regras */
   ,ATOMIC {
      @Override
      public boolean isOptionName(final String optionName) {
         return "Atomic".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   /* Indica que a regra deve seguir todas as regras literais */
   ,FOLLOW_LITERALS {
      @Override
      public boolean isOptionName(final String optionName) {
         return "FollowLiterals".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return true;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return false;
      }
   }
   
   /* Indica que os listerais que compoem a regra nao devem ser extendidos por FOLLOW_... */
   ,NOT_EXTEND_LITERALS {
      @Override
      public boolean isOptionName(final String optionName) {
         return "NotExtendLiterals".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   /* Pode declarar um nome diferente daquele encontrado na gramatica */
   ,RULE_NAME {
      @Override
      public boolean isOptionName(final String optionName) {
         return "Rule".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return true;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   /* Indica que eh a regra de entrada na gramatica */
   ,MAIN_RULE {
      @Override
      public boolean isOptionName(final String optionName) {
         return "Main".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   ,SKIP_NODE {
      @Override
      public boolean isOptionName(final String optionName) {
         return "SkipNode".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   }
   
   ,CATCH_MISMATCH {
      @Override
      public boolean isOptionName(final String optionName) {
         return "CatchMismatch".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   },
   
   MEMOIZE {
      @Override
      public boolean isOptionName(final String optionName) {
         return "Memoize".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   },
   
   FRAGMENT {
      @Override
      public boolean isOptionName(final String optionName) {
         return "Fragment".equalsIgnoreCase(optionName);
      }

      @Override
      public boolean isParameterized() {
         return false;
      }
      
      @Override
      public boolean isParameterMandatory() {
         return true;
      }
   };
   
   public abstract boolean isOptionName(final String optionName);
   public abstract boolean isParameterized();
   public abstract boolean isParameterMandatory();
}
