/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar;

import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.EOIRule;
import org.uggeri.yapp.grammar.rules.EmptyRule;
import org.uggeri.yapp.grammar.rules.GrammarRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseCharRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.NonTerminalOption;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author fabio
 */
public class GrammarLoader {

   private void loadGrammar(Grammar grammar, File file, boolean importing) throws GrammarSyntaxException, GrammarException {
      FileReader fr = null;
      try {
         final StringBuilder grammarText = new StringBuilder(4096);
         final BufferedReader br;
         String lineReaded;

         fr = new FileReader(file);
         br = new BufferedReader(fr);
         lineReaded = br.readLine();
         if (lineReaded != null) {
            grammarText.append(lineReaded);
            lineReaded = br.readLine();
            while (lineReaded != null) {
               grammarText.append('\n').append(lineReaded);
               lineReaded = br.readLine();
            }
         }
         grammar.setGrammarFile(file);
         grammarEntries(grammar, new GrammarParsingContext(grammarText), importing);
      } catch (FileNotFoundException ex) {
         throw new GrammarException("Grammar file not found!", ex);
      } catch (IOException ex) {
         throw new GrammarException("Erro reading grammar file!", ex);
      } finally {
         try {
            if (fr != null) {
               fr.close();
            }
         } catch (IOException ex) {
            throw new GrammarException("Erro closing grammar file!", ex);
         }
      }
   }

   public Grammar loadGrammar(File file) throws GrammarSyntaxException, GrammarException {
      Grammar grammar = new Grammar();
      loadGrammar(grammar, file, false);
      return grammar;
   }

   public Grammar loadGrammar(CharSequence grammarText) throws GrammarSyntaxException, GrammarException {
      Grammar grammar = new Grammar();
      grammarEntries(grammar, new GrammarParsingContext(grammarText), false);
      return grammar;
   }

   private void grammarEntries(Grammar grammar, GrammarParsingContext ctx, boolean importing) throws GrammarSyntaxException, GrammarException {
      while (ctx.hasNext()) {
         grammarEntry(grammar, ctx, importing);
      }
   }

   /* Sintaxe:
    *    package nome_qualificao=do;
    *    grammar nome [extends nome];
    *    regra : (r1|r2)r3+r4*r5?r6!r7&;
    */
   private void grammarEntry(Grammar grammar, GrammarParsingContext ctx, boolean importing) throws GrammarSyntaxException, GrammarException {
      final char c;
      ctx.skipSpaces();
      c = ctx.currentChar();
      if (Character.isLetter(c)) {
         final CharSequence identifier = consumeIdentifier(ctx);
         if (charSequencesEquals(identifier, "grammar")) {
            grammarNameEntry(grammar, ctx, importing);
         } else if (charSequencesEquals(identifier, "import")) {
            importGrammarEntry(grammar, ctx);
         } else {
            nonTerminalEntry(grammar, ctx, identifier, importing);
         }
      } else if (c == '@') {
         optionEntry(ctx);
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Unknown character found.");
      }
   }

   private void nonTerminalEntry(final Grammar grammar, final GrammarParsingContext ctx, final CharSequence ruleName, boolean importing) throws GrammarSyntaxException, GrammarException {
      NonTerminalRule rule = grammar.getRule(ruleName.toString());

      if (charSequencesEquals(ruleName, "EOI")) {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "EOI is a reserved rule name. ");
      } else if (charSequencesEquals(ruleName, "EMPTY")) {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "EMPTY is a reserved rule name. ");
      }

      if (rule == null) {
         rule = new NonTerminalRule(ruleName.toString(), null);
         grammar.addRule(rule);
      } else if (rule.getRule() != null) {
         warning(ctx, "Rule " + rule.getName()+ " redefined.");
      }
      ctx.skipSpaces();
      if (ctx.currentChar() == ':') {
         GrammarRule execRule;
         ctx.advanceIndex();
         execRule = orRule(grammar, ctx);
         if (execRule != null) {
            rule.setRule(execRule);
            grammar.declareRule(rule);
            for (Entry<NonTerminalOption, String> option : ctx.getOptions().entrySet()) {
               rule.addOption(option.getKey(), option.getValue());
            }
            if (! importing) {
               if (ctx.getOptions().containsKey(NonTerminalOption.MAIN_RULE)) {
                  if (! ctx.explicitMainRule) {
                     grammar.setMainRule(rule);
                     ctx.explicitMainRule = true;
                  } else {
                     throw new GrammarSyntaxException(ctx.line, ctx.column, "Rule '" + grammar.getMainRule().getName() + "' is already defined as main rule.");
                  }
               } else if (grammar.getMainRule() == null) {
                  grammar.setMainRule(rule);
               }
            }
            ctx.clearOptions();
            ctx.skipSpaces();
            if (ctx.currentChar() == ';') {
               ctx.advanceIndex();
            } else {
               throw new GrammarSyntaxException(ctx.line, ctx.column, "; not found after rule definition!");
            }
         } else {
            throw new GrammarSyntaxException(ctx.line, ctx.column, "Unknown " + ctx.currentChar() + " found!");
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, ": not found after rule name.");
      }
   }

   private CharSequence consumeUp(final GrammarParsingContext ctx, char endChar) {
      final StringBuilder text = new StringBuilder(32);
      char lastChar = '\0';

      ctx.skipSpaces();
      while (ctx.hasNext()) {
         final char c = ctx.currentChar();
         if (lastChar == '\\') {
            text.append(c);
            lastChar = '\0';
         } else {
            if (c == endChar) {
               break;
            } else if (c != '\\' ) {
               text.append(c);
            }
            lastChar = c;
         }
         ctx.advanceIndex();
      }
      return text;
   }

   private CharSequence consumeIdentifier(final GrammarParsingContext ctx) {
      final StringBuilder id = new StringBuilder(32);

      ctx.skipSpaces();
      id.append(ctx.currentChar());
      ctx.advanceIndex();
      while (ctx.hasNext()) {
         final char c = ctx.currentChar();
         if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
            id.append(c);
            ctx.advanceIndex();
         } else {
            break;
         }
      }
      return id;
   }

   private GrammarRule orRule(final Grammar grammar, final GrammarParsingContext ctx) throws GrammarSyntaxException {
      final List<GrammarRule> rules = new ArrayList<GrammarRule>();
      GrammarRule rule = andRule(grammar, ctx);
      if (rule != null) {
         char c;
         do {
            rules.add(rule);
            ctx.skipSpaces();
            c = ctx.currentChar();
            if (c == '|') {
               ctx.advanceIndex();
               rule = andRule(grammar, ctx);
            } else {
               rule = null;
            }
         } while (rule != null);

         rule = rules.size() == 1 ? rules.get(0) : new OrRule(rules);
      }
      return rule;
   }

   private GrammarRule andRule(final Grammar grammar, final GrammarParsingContext ctx) throws GrammarSyntaxException {
      final List<GrammarRule> rules = new ArrayList<GrammarRule>();
      GrammarRule rule = postFixedRule(grammar, ctx);
      if (rule != null) {
         do {
            rules.add(rule);
            rule = postFixedRule(grammar, ctx);
         } while (rule != null);

         rule = rules.size() == 1 ? rules.get(0) : new AndRule(rules);
      }
      return rule;
   }

   private GrammarRule postFixedRule(final Grammar grammar, final GrammarParsingContext ctx) throws GrammarSyntaxException {
      GrammarRule rule = simpleRule(grammar, ctx);
      if (rule != null) {
         final char c;
         ctx.skipSpaces();
         c = ctx.currentChar();
         switch (c) {
            case '?':
               rule = new OptionalRule(rule);
               ctx.advanceIndex();
               break;
            case '+':
               rule = new OneOrMoreRule(rule);
               ctx.advanceIndex();
               break;
            case '*':
               rule = new ZeroOrMoreRule(rule);
               ctx.advanceIndex();
               break;
            case '&':
               rule = new TestRule(rule);
               ctx.advanceIndex();
               break;
            case '!':
               rule = new TestNotRule(rule);
               ctx.advanceIndex();
               break;
         }
      }
      return rule;
   }

   private GrammarRule simpleRule(final Grammar grammar, final GrammarParsingContext ctx) throws GrammarSyntaxException {
      GrammarRule rule = null;
      char c;
      ctx.skipSpaces();
      c = ctx.currentChar();
      switch(c) {
         case '"':
            rule = ignoreCaseLiteralRule(ctx);
            break;
         case '\'':
            rule = literalRule(ctx);
            break;
         case '(':
            rule = groupedRule(grammar, ctx);
            break;
         case '[':
            rule = charRangeRule(ctx);
            break;
         case '.':
            rule = new AnyCharRule();
            ctx.advanceIndex();
            break;
         default:
            if (Character.isLetter(c)) {
               rule = identifierRule(grammar, ctx);
            }
      }
      return rule;
   }

   private GrammarRule ignoreCaseLiteralRule(GrammarParsingContext ctx) throws GrammarSyntaxException {
      final StringBuilder literal = new StringBuilder();
      char lastChar = '\0';
      GrammarRule rule = null;
      ctx.advanceIndex();
      while (ctx.hasNext()) {
         final char c = ctx.currentChar();
         ctx.advanceIndex();
         if (lastChar == '\\') {
            appendEscapedChar(literal, c);
            lastChar = '\0';
         } else {
            if (c == '"') {
               break;
            } else if (c == '\\') {
               lastChar = c;
            } else {
               literal.append(c);
            }
         }
      }
      ctx.skipSpaces();
      if (literal.length() > 0) {
         if (ctx.currentChar() == ':') {
            ctx.advanceIndex();
            ctx.skipSpaces();
            if (Character.isDigit(ctx.currentChar())) {
               final CharSequence number = consumeNumber(ctx);
               if (literal.length() > 1) {
                  rule = new IgnoreCaseStringRule(literal.toString(), Integer.parseInt(number.toString()));
               } else {
                  throw new GrammarSyntaxException(ctx.line, ctx.column, "Partial match not allowed in literal of length one!");
               }
            } else {
               throw new GrammarSyntaxException(ctx.line, ctx.column, "Literal partial match value not found!");
            }
         } else if (literal.length() > 1) {
            rule = new IgnoreCaseStringRule(literal.toString());
         } else {
            rule = new IgnoreCaseCharRule(literal.charAt(0));
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Found empty literal!");
      }
      return rule;
   }

   private GrammarRule literalRule(GrammarParsingContext ctx) throws GrammarSyntaxException {
      final StringBuilder literal = new StringBuilder();
      char lastChar = '\0';
      GrammarRule rule = null;
      ctx.advanceIndex();
      while (ctx.hasNext()) {
         final char c = ctx.currentChar();
         ctx.advanceIndex();
         if (lastChar == '\\') {
            appendEscapedChar(literal, c);
            lastChar = '\0';
         } else {
            if (c == '\'') {
               break;
            } else if (c == '\\') {
               lastChar = c;
            } else {
               literal.append(c);
            }
         }
      }
      ctx.skipSpaces();
      if (literal.length() > 0) {
         if (ctx.currentChar() == ':') {
            ctx.advanceIndex();
            ctx.skipSpaces();
            if (Character.isDigit(ctx.currentChar())) {
               final CharSequence number = consumeNumber(ctx);
               if (literal.length() > 1) {
                  rule = new StringRule(literal.toString(), Integer.parseInt(number.toString()));
               } else {
                  throw new GrammarSyntaxException(ctx.line, ctx.column, "Partial match not allowed in literal of length one!");
               }
            } else {
               throw new GrammarSyntaxException(ctx.line, ctx.column, "Literal partial match value not found!");
            }
         } else if (literal.length() > 1) {
            rule = new StringRule(literal.toString());
         } else {
            rule = new CharRule(literal.charAt(0));
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Found empty literal!");
      }
      return rule;
   }

   private CharSequence consumeNumber(final GrammarParsingContext ctx) {
      final StringBuilder number = new StringBuilder(32);

      ctx.skipSpaces();
      number.append(ctx.currentChar());
      ctx.advanceIndex();
      while (ctx.hasNext()) {
         final char c = ctx.currentChar();
         if (Character.isDigit(c)) {
            number.append(c);
            ctx.advanceIndex();
         } else {
            break;
         }
      }
      return number;
   }


   private GrammarRule groupedRule(final Grammar grammar, final GrammarParsingContext ctx) throws GrammarSyntaxException {
      GrammarRule rule;
      ctx.advanceIndex();
      rule = orRule(grammar, ctx);
      ctx.skipSpaces();
      if (ctx.currentChar() != ')') {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Closing parenthesis not found.");
      }
      ctx.advanceIndex();
      return rule;
   }

   private GrammarRule charRangeRule(final GrammarParsingContext ctx) throws GrammarSyntaxException {
      char startChar;
      char endChar;
      ctx.advanceIndex();
      ctx.skipSpaces();
      startChar = ctx.currentChar();
      ctx.advanceIndex();
      ctx.skipSpaces();
      if (ctx.currentChar() != '-') {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Character - not found between start and end character in range rule.");
      }
      ctx.advanceIndex();
      ctx.skipSpaces();
      endChar = ctx.currentChar();
      ctx.advanceIndex();
      ctx.skipSpaces();
      if (ctx.currentChar() != ']') {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Closing brackets not found.");
      }
      ctx.advanceIndex();
      return new CharRangeRule(startChar, endChar);
   }

   private GrammarRule identifierRule(Grammar grammar, GrammarParsingContext ctx) {
      GrammarRule rule;
      final CharSequence id = consumeIdentifier(ctx);
      if (charSequencesEquals(id, "EOI")) {
         rule = new EOIRule();
      } else if (charSequencesEquals(id, "EMPTY")) {
         rule = new EmptyRule();
      } else {
         rule = grammar.getRule(id.toString());
         if (rule == null) {
            rule = createEmptyNonTerminal(grammar, id);
         }
      }
      return rule;
   }

   private NonTerminalRule createEmptyNonTerminal(Grammar grammar, CharSequence id) {
      NonTerminalRule rule = new NonTerminalRule(id.toString(), null);
      grammar.addRule(rule);
      return rule;
   }

   private boolean charSequencesEquals(CharSequence text1, CharSequence text2) {
      if (text1.length() != text2.length()) {
         return false;
      }
      for (int i = 0; i < text1.length(); i++) {
         if (text1.charAt(i) != text2.charAt(i)) {
            return false;
         }
      }
      return true;
   }

   private CharSequence adjustFileName(CharSequence fileName) {
      final StringBuilder adjustedFileName = new StringBuilder(fileName.length());
      boolean withExtension = false;
      for (int i = 0; i < fileName.length(); i++) {
         final char c = fileName.charAt(i);
         switch(c) {
            case '/':
            case '\\':
               adjustedFileName.append(File.separatorChar);
               break;
            case '.':
               withExtension = true;
            default:
               adjustedFileName.append(c);
         }
      }
      if (! withExtension) {
         adjustedFileName.append(".gy");
      }
      return adjustedFileName;
   }

   private void importGrammar(Grammar grammar, CharSequence importGrammar) throws GrammarException {
      File grammarFile;
      importGrammar = adjustFileName(importGrammar);
      grammarFile = new File(importGrammar.toString());
      if (! grammarFile.isAbsolute()) {
         /* Se a gramatica esta sendo lida de um arquivo, entao usa o diretorio onde ela esta como path relativo */
         if (grammar.getGrammarFile() != null && grammar.getGrammarFile().getParentFile() != null) {
            grammarFile = new File(grammar.getGrammarFile().getParent(), grammarFile.getPath());
         }
      }
      //grammar.addImportGrammar(loadGrammar(grammarFile));
      loadGrammar(grammar, grammarFile, true);
   }

   private void importGrammarEntry(Grammar grammar, GrammarParsingContext ctx) throws GrammarSyntaxException, GrammarException {
      ctx.skipSpaces();
      if (Character.isLetter(ctx.currentChar())) {
         final CharSequence importName  = consumeUp(ctx, ';');
         importGrammar(grammar, importName);
         if (ctx.currentChar() == ';') {
            ctx.advanceIndex();
         } else {
            throw new GrammarSyntaxException(ctx.line, ctx.column, "; not found after grammar entry!");
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Grammar name not found!");
      }
   }

   private void grammarNameEntry(Grammar grammar, GrammarParsingContext ctx, boolean importing) throws GrammarSyntaxException {
      if (grammar.getGrammarName() == null || importing) {
         ctx.skipSpaces();
         if (Character.isLetter(ctx.currentChar())) {
            final CharSequence name  = consumeIdentifier(ctx);
            if (! importing) {
               grammar.setGrammarName(name.toString());
            }
            ctx.skipSpaces();
            if (ctx.currentChar() == ';') {
               ctx.advanceIndex();
            } else {
               throw new GrammarSyntaxException(ctx.line, ctx.column, "; not found after grammar entry!");
            }
         } else {
            throw new GrammarSyntaxException(ctx.line, ctx.column, "Grammar name not found!");
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Grammar name already defined!");
      }
   }

   private void optionEntry(GrammarParsingContext ctx) throws GrammarSyntaxException {
      boolean foundOption = false;
      final CharSequence identifier;
      ctx.advanceIndex();
      if (Character.isLetter(ctx.currentChar())) {
         identifier = consumeIdentifier(ctx);
         for (NonTerminalOption option : NonTerminalOption.values()) {
            if (option.isOptionName(identifier.toString())) {
               if (option.isParameterized()) {
                  ctx.skipSpaces();
                  if (ctx.currentChar() == '(') {
                     CharSequence optionValue;
                     ctx.advanceIndex();
                     optionValue = consumeUp(ctx, ')');
                     ctx.skipSpaces();
                     if (ctx.currentChar() == ')') {
                        ctx.advanceIndex();
                        ctx.addOption(option, optionValue.toString());
                        foundOption = true;
                        break;
                     } else {
                        throw new GrammarSyntaxException(ctx.line, ctx.column, "expected ) not found on option parameter.");
                     }
                  } else if (! option.isParameterMandatory()) {
                     ctx.addOption(option);
                     foundOption = true;
                     break;
                  } else {
                     throw new GrammarSyntaxException(ctx.line, ctx.column, "Expected option parameter not found.");
                  }
               } else {
                  ctx.addOption(option);
                  foundOption = true;
                  break;
               }
            }
         }
         if (! foundOption) {
            throw new GrammarSyntaxException(ctx.line, ctx.column, "Unknown option specified.");
         }
      } else {
         throw new GrammarSyntaxException(ctx.line, ctx.column, "Invalid character after option marker.");
      }
   }

   private void appendEscapedChar(final StringBuilder literal, final char c) {
      switch(c) {
         case 'n':
            literal.append('\n');
            break;
         case 'r':
            literal.append('\r');
            break;
         case 't':
            literal.append('\t');
            break;
         case 'b':
            literal.append('\b');
            break;
         case 'f':
            literal.append('\f');
            break;
         default:
            literal.append(c);
      }
   }

   private void warning(GrammarParsingContext ctx, String warnMsg) {
      System.out.println(warnMsg);
   }

   private class GrammarParsingContext {

      private static final int NO_COMMENT = 0;
      private static final int LINE_COMMENT = 1;
      private static final int BLOCK_COMMENT = 2;
      int index;
      CharSequence text;
      int line = 1;
      int column = 1;
      Map<NonTerminalOption, String> options = new EnumMap<NonTerminalOption, String>(NonTerminalOption.class);
      boolean explicitMainRule = false;

      public GrammarParsingContext(CharSequence text) {
         this.text = text;
         skipSpaces();
      }

      public char currentChar() {
         return text.charAt(index);
      }

      public final void skipSpaces() {
         int state = NO_COMMENT;
         while (index < text.length()) {
            final char c = text.charAt(index);
            switch (c) {
               case ' ':
               case '\t':
                  ++column;
                  break;
               case '\r':
                  break;
               case '\n':
                  if (state == LINE_COMMENT) {
                     state = NO_COMMENT;
                  }
                  ++line;
                  column = 1;
                  break;
               case '*':
                  if (state == BLOCK_COMMENT) {
                     if (index + 1 < text.length() && text.charAt(index + 1) == '/') {
                        ++index;
                        ++column;
                        state = NO_COMMENT;
                     }
                  } else {
                     return;
                  }
                  ++column;
                  break;
               case '/':
                  if (index + 1 < text.length()) {
                     final char nextChar = text.charAt(index + 1);
                     if (nextChar == '*') {
                        state = BLOCK_COMMENT;
                        ++index;
                        ++column;
                     } else if (nextChar == '/') {
                        state = LINE_COMMENT;
                        ++index;
                        ++column;
                     } else {
                        return;
                     }
                  }
                  break;
               default:
                  if (state == NO_COMMENT) {
                     return;
                  } else {
                     ++column;
                  }
            }
            ++index;
         }
      }

      public void advanceIndex() {
         ++column;
         ++index;
      }

      public boolean hasNext() {
         return index < text.length();
      }

      public void addOption(NonTerminalOption option) {
         options.put(option, "");
      }

      public void addOption(NonTerminalOption option, String value) {
         options.put(option, value);
      }

      public void clearOptions() {
         options.clear();
      }

      public Map<NonTerminalOption, String> getOptions() {
         return options;
      }
   }
}
