/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java;

import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationException;
import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.generation.ParserGenerator;
import org.uggeri.yapp.grammar.Grammar;
import org.uggeri.yapp.grammar.GrammarException;
import org.uggeri.yapp.grammar.GrammarLoader;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import org.uggeri.yapp.grammar.GrammarSyntaxException;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.EOIRule;
import org.uggeri.yapp.grammar.rules.EmptyRule;
import org.uggeri.yapp.grammar.rules.GrammarRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseCharRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TerminalRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fabio
 */
public class TesteGeracao {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      //new TesteGeracao().showFirstLiterals();
      //new TesteGeracao().testeGeracaoJava();
      //new TesteGeracao().testeGeracaoHarbourPP();
      new TesteGeracao().testeGeracaoHarbour();
   }

   private void showFirstLiterals() {
      try {
         GrammarLoader gr;
         Grammar g;
         gr = new GrammarLoader();
         g = gr.loadGrammar(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test\\Java.gy"));
         for (NonTerminalRule rule : g.getDeclaredRules()) {
            final FirstLiteralsVisitor visitor = new FirstLiteralsVisitor();
            rule.visit(null, visitor);
            System.out.println("[" + rule.getName() + "] => " + Arrays.toString(visitor.getFirstLiterals()));
         }
      } catch (GrammarSyntaxException ex) {
         System.out.println("Error at: " + ex.getLine() + "," + ex.getColumn());
         ex.printStackTrace(System.out);
      } catch (GrammarException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeGeracaoJava() {
      try {
         JavaParserGenerationOptions options;
         ParserGenerator pg;
         GrammarLoader gr;
         Grammar g;
         gr = new GrammarLoader();
         g = gr.loadGrammar(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test\\Java.gy"));
         options = new JavaParserGenerationOptions(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test"),
                 "br.com.sicredi.yapp.runtime.java.test");
         options.setGenerateTraceCode(false);
         options.setProfile(false);
         options.setCatchMismatches(false);
         //options.setMemoizeMode(MemoizeMode.ALL);
         options.setMemoizeMode(MemoizeMode.EXPLICIT);
         //options.setGenerateSingleFileSource(true);
         pg = new JavaParserGenerator(g, options);
         //pg.validate();
         pg.generateParser();
      } catch (GrammarSyntaxException ex) {
         System.out.println("Error at: " + ex.getLine() + "," + ex.getColumn());
         ex.printStackTrace(System.out);
      } catch (GrammarException ex) {
         ex.printStackTrace(System.out);
      } catch (ParserGenerationException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeGeracaoHarbourPP() {
      try {
         JavaParserGenerationOptions options;
         ParserGenerator pg;
         GrammarLoader gr;
         Grammar g;
         gr = new GrammarLoader();
         g = gr.loadGrammar(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test\\HarbourPP.gy"));
         options = new JavaParserGenerationOptions(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test"),
                 "br.com.sicredi.yapp.runtime.java.test");
         options.setGenerateTraceCode(false);
         options.setProfile(false);
         options.setCatchMismatches(false);
         options.setMemoizeMode(MemoizeMode.EXPLICIT);
         //options.setGenerateSingleFileSource(true);
         pg = new JavaParserGenerator(g, options);
         pg.generateParser();
      } catch (GrammarSyntaxException ex) {
         System.out.println("Error at: " + ex.getLine() + "," + ex.getColumn());
         ex.printStackTrace(System.out);
      } catch (GrammarException ex) {
         ex.printStackTrace(System.out);
      } catch (ParserGenerationException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeGeracaoHarbour() {
      try {
         JavaParserGenerationOptions options;
         ParserGenerator pg;
         GrammarLoader gr;
         Grammar g;
         gr = new GrammarLoader();
         g = gr.loadGrammar(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test\\HarbourUnprocessed.gy"));
         options = new JavaParserGenerationOptions(new File("D:\\Users\\fabio_uggeri\\Sicredi\\sistemas\\externos\\yapp\\yapp-java-runtime\\src\\main\\java\\br\\com\\sicredi\\yapp\\runtime\\java\\test"),
                 "br.com.sicredi.yapp.runtime.java.test");
         options.setGenerateTraceCode(false);
         options.setProfile(false);
         options.setCatchMismatches(false);
         options.setMemoizeMode(MemoizeMode.AUTO);
         //options.setGenerateSingleFileSource(true);
         pg = new JavaParserGenerator(g, options);
         pg.generateParser();
      } catch (GrammarSyntaxException ex) {
         System.out.println("Error at: " + ex.getLine() + "," + ex.getColumn());
         ex.printStackTrace(System.out);
      } catch (GrammarException ex) {
         ex.printStackTrace(System.out);
      } catch (ParserGenerationException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private CharSequence formatText(CharSequence text) {
      StringBuilder sb = new StringBuilder((int) (text.length() * 1.1));
      for (int i = 0; i < text.length(); i++) {
         final char c = text.charAt(i);
         switch (c) {
            case '\n':
               sb.append("\\n");
               break;
            case '\r':
               sb.append("\\r");
               break;
            case '\t':
               sb.append("\\t");
               break;
            case '\f':
               sb.append("\\f");
               break;
            default:
               sb.append(c);
               break;
         }
      }
      return sb;
   }

   public String repeat(char c, int n) {
      if (n > 0) {
         char[] array = new char[n];
         Arrays.fill(array, c);
         return String.valueOf(array);
      } else {
         return "";
      }
   }

   public String rightPad(String s, int size, char c) {
      if (s.length() < size) {
         return s + repeat(c, size - s.length());
      } else if (s.length() > size) {
         return s.substring(0, size);
      } else {
         return s;
      }
   }

   private static class FirstLiteralsVisitor implements GrammarRuleVisitor {

      final private Set<TerminalRule> rulesSet = new HashSet<TerminalRule>();

      private boolean optional = false;

      public TerminalRule[] getFirstLiterals() {
         final TerminalRule[] result;
         int index = 0;
         removeRedundantRules();
         result = new TerminalRule[rulesSet.size()];
         for (TerminalRule t : rulesSet) {
            result[index++] = t;
         }
         return result;
      }

      private void removeRedundantRules() {
         final List<TerminalRule> rules = new ArrayList<TerminalRule>(rulesSet.size());
         for (TerminalRule sourceRule : rulesSet) {
            boolean addRule = true;
            for (TerminalRule targetRule : rulesSet) {
               if (targetRule != sourceRule && ruleMatch(targetRule, sourceRule)) {
                  addRule = false;
                  break;
               }
            }
            if (addRule) {
               rules.add(sourceRule);
            }
         }
         rulesSet.clear();
         rulesSet.addAll(rules);
      }

      private boolean ruleMatch(TerminalRule targetRule, TerminalRule sourceRule) {
         if (targetRule instanceof CharRangeRule) {
            if (sourceRule instanceof CharRangeRule) {
               return ((CharRangeRule)sourceRule).getStart() >= ((CharRangeRule)targetRule).getStart() && ((CharRangeRule)sourceRule).getStart() <= ((CharRangeRule)targetRule).getStart();
            } else if (sourceRule instanceof IgnoreCaseCharRule) {
               return Character.toLowerCase(((IgnoreCaseCharRule)targetRule).getCharacter()) >= ((CharRangeRule)sourceRule).getStart() && Character.toLowerCase(((IgnoreCaseCharRule)targetRule).getCharacter()) <= ((CharRangeRule)sourceRule).getEnd() &&
                      Character.toUpperCase(((IgnoreCaseCharRule)targetRule).getCharacter()) >= ((CharRangeRule)sourceRule).getStart() && Character.toUpperCase(((IgnoreCaseCharRule)targetRule).getCharacter()) <= ((CharRangeRule)sourceRule).getEnd();
            } else if (sourceRule instanceof CharRule) {
               return ((CharRule)sourceRule).getCharacter() >= ((CharRangeRule)targetRule).getStart() && ((CharRule)sourceRule).getCharacter() <= ((CharRangeRule)targetRule).getEnd();
            }
         } else if (targetRule instanceof IgnoreCaseCharRule) {
            if (sourceRule instanceof IgnoreCaseCharRule) {
               return Character.toLowerCase(((IgnoreCaseCharRule)targetRule).getCharacter()) == Character.toLowerCase(((IgnoreCaseCharRule)sourceRule).getCharacter());
            } else if (sourceRule instanceof CharRule) {
               return Character.toLowerCase(((IgnoreCaseCharRule)targetRule).getCharacter()) == ((CharRule)sourceRule).getCharacter() ||
                       Character.toUpperCase(((IgnoreCaseCharRule)targetRule).getCharacter()) == ((CharRule)sourceRule).getCharacter();
            }
         } else if (targetRule instanceof CharRule) {
            if (sourceRule instanceof CharRule) {
               return ((CharRule)targetRule).getCharacter() == ((CharRule)sourceRule).getCharacter();
            }
         }
         return false;
      }

      @Override
      public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitAnd(ParserGenerationOptions options, AndRule rule) {
         boolean oldOptional = optional;
         int index = 0;
         do {
            optional = false;
            rule.getRules().get(index++).visit(options, this);
         } while (optional && index < rule.getRules().size());
         optional = oldOptional;
      }

      @Override
      public void visitOr(ParserGenerationOptions options, OrRule rule) {
         for (GrammarRule child : rule.getRules()) {
            child.visit(options, this);
         }
      }

      @Override
      public void visitChar(ParserGenerationOptions options, CharRule rule) {
         rulesSet.add(rule);
      }

      @Override
      public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
         rulesSet.add(rule);
      }

      @Override
      public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
         rulesSet.add(rule);
      }

      @Override
      public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
      }

      @Override
      public void visitString(ParserGenerationOptions options, StringRule rule) {
         rulesSet.add(new CharRule(rule.getText().charAt(0)));
      }

      @Override
      public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
         rulesSet.add(new IgnoreCaseCharRule(rule.getText().charAt(0)));
      }

      @Override
      public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
         optional = true;
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitTest(ParserGenerationOptions options, TestRule rule) {
         optional = true;
      }

      @Override
      public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
         optional = true;
      }

      @Override
      public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
         optional = true;
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
      }

      @Override
      public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
      }
   }
}
