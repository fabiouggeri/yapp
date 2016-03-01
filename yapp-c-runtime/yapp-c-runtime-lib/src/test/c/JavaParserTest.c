#include <stdio.h>
#include <time.h>
#ifdef _MSC_VER
#include <windows.h>
#else
#include <dirent.h>
#endif
#include "JavaParser.h"
#include "yapp_file_buffer.h"
#include "yapp_visitor.h"
#include "yapp_node.h"
#include "yapp_trace_parser.h"
#include "yapp_mem_manager.h"

INT32 totalSize = 0;
INT32 linesCount = 0;

#ifdef _MSC_VER
#define DIR_HANDLE               HANDLE
#define DIR_ENTRY                WIN32_FIND_DATA
#define DIR_DATA                 WIN32_FIND_DATA
#define IS_DIR_OPEN(d)           d != INVALID_HANDLE_VALUE
#define READ_RES                 BOOL
#define IS_VALID_ENTRY(r)        r != 0
#define FILE_NAME(f)             f.cFileName
#define OPEN_DIR(p, e)           FindFirstFile(p, &e)
#define OPEN_SUBDIR(d, p, e)     (d.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY ? FindFirstFile(p, &e) : INVALID_HANDLE_VALUE)
#define READ_DIR(p, e)           FindNextFile(p, &e)
#define CLOSE_DIR(p)             FindClose(p)
#define SET_SUBDIR_NAME(t, d, c) sprintf(t, "%.*s\\%s\\*", strlen(d) - 2, d, c)
#define SET_FILE_NAME(t, d, c)   sprintf(t, "%.*s\\%s", strlen(d) - 2, d, c)
#else
#define DIR_HANDLE               DIR *
#define DIR_ENTRY                struct dirent *
#define DIR_DATA                 void *
#define IS_DIR_OPEN(d)           d != NULL
#define READ_RES                 struct dirent *
#define IS_VALID_ENTRY(r)        r != NULL
#define FILE_NAME(f)             f->d_name
#define OPEN_DIR(p, e)           opendir(p)
#define OPEN_SUBDIR(d, p, e)     opendir(p)
#define READ_DIR(p, e)           e = readdir(p)
#define CLOSE_DIR(p)             closedir(p)
#define SET_SUBDIR_NAME(t, d, c) sprintf(t, "%s\\%s", d, c)
#define SET_FILE_NAME(t, d, c)   sprintf(t, "%s\\%s", d, c)
#endif

static void printNodeText(YAPP_BUFFER *buffer, YAPP_NODE *node) {
   INT32 i;
   INT32 len = yapp_node_getEndIndex(node) - yapp_node_getStartIndex(node);
   char *text = yapp_buffer_getTextPointer(buffer, yapp_node_getStartIndex(node));
   for (i = 0; i < len; i++) {
      switch (text[i]) {
         case '\n':
            putchar('\\');
            putchar('n');
            break;
         case '\r':
            putchar('\\');
            putchar('r');
            break;
         case '\t':
            putchar('\\');
            putchar('t');
            break;
         case '\f':
            putchar('\\');
            putchar('f');
            break;
         default:
            putchar(text[i]);
      }
   }
}

static void printTree(YAPP_BUFFER *buffer, YAPP_NODE *node, INT32 i) {
   INT32 j;
   YAPP_NODE *child;
   for (j = 0; j < i; j++) {
      printf("   ");
   }
   printf("[");
   printf(yapp_rule_getLabel(yapp_node_getRule(node)));
   printf("] : '");
   printNodeText(buffer, node);
   printf("'\n");
   child = yapp_node_getFirstChild(node);
   while (child != NULL) {
      printTree(buffer, child, i + 1);
      child = yapp_node_getSibling(child);
   }
}

static INT32 s_ruleCount = 0;
static INT32 s_ruleStart[1000000];
static char* s_ruleName[1000000];

static void traceEnter(YAPP_BUFFER *inputBuffer, char *ruleName, INT32 index) {
   printf("ENTER %s at %d\n", ruleName, index);
   s_ruleName[s_ruleCount] = ruleName;
   s_ruleStart[s_ruleCount++] = index;
}

static void traceExit(YAPP_BUFFER *inputBuffer, INT32 index, BOOL success) {
   --s_ruleCount;
   printf("%s %s [%d-%d]\n", (success ? "MATCH" : "MISMATCH"), s_ruleName[s_ruleCount], s_ruleStart[s_ruleCount], index);
}

//private void countLinesJavaFile(File file) {
//   BufferedReader br = null;
//   try {
//      br = new BufferedReader(new FileReader(file));
//      totalSize += file.length();
//      while (br.readLine() != null) {
//         ++linesCount;
//      }
//   } catch (FileNotFoundException ex) {
//   } catch (IOException ex) {
//   } finally {
//      try {
//         if (br != null) {
//            br.close();
//         }
//      } catch (IOException ex) {
//      }
//   }
//}

static clock_t parseJavaFile(YAPP_MEM_MANAGER *memManager, char *file) {
   YAPP_BUFFER *buffer;
   JavaParser *parser;
   YAPP_NODE *node;
   clock_t time;

   buffer = yapp_file_buffer_new(file);
   if (memManager != NULL) {
      parser = javaParser_newManaged(memManager, buffer);
   } else {
      parser = javaParser_new(buffer);
   }
   time = clock();
   node = javaParser_parse(parser);
   if (node != NULL) {
      time = clock() - time;
   } else {
      time = 0;
   }
   javaParser_free(parser);
   yapp_buffer_free(buffer);
   if (memManager != NULL) {
      yapp_mem_reset(memManager);
   }
   return time;
}

static clock_t scanSources(YAPP_MEM_MANAGER *memManager, char *dirName, DIR_HANDLE dir, clock_t totalTime) {
   DIR_ENTRY child;
   DIR_HANDLE childDir;
   char childPathName[512];
   INT32 childLen;
   READ_RES res;
   DIR_DATA dirData;

   res = READ_DIR(dir, child);
   //printf("Read dir result: %d\n", res);
   while (IS_VALID_ENTRY(res)) {
      //printf("Child: %s\n", FILE_NAME(child));
      if (strcmp(FILE_NAME(child), ".") != 0 && strcmp(FILE_NAME(child), "..") != 0) {
         SET_SUBDIR_NAME(childPathName, dirName, FILE_NAME(child));
         childDir = OPEN_SUBDIR(child, childPathName, dirData);
         if (IS_DIR_OPEN(childDir)) {
            //printf("Dir: %s\n", childPathName);
            totalTime += scanSources(memManager, childPathName, childDir, 0);
            CLOSE_DIR(childDir);
         } else {
            //printf("Source name: %s\n", FILE_NAME(child));
            childLen = strlen(FILE_NAME(child));
            if (childLen > 5 && strcmp(&FILE_NAME(child)[childLen - 5], ".java") == 0) {
               SET_FILE_NAME(childPathName, dirName, FILE_NAME(child));
               //printf("Source path: %s\n", childPathName);
               totalTime += parseJavaFile(memManager, childPathName);
               //countLinesJavaFile(file);
            }
         }
      }
      res = READ_DIR(dir, child);
   }
   return totalTime;
}

static void testeParseJDKSources() {
   clock_t initTime = clock();
   clock_t totalTime;
   DIR_HANDLE dir;
   DIR_DATA dirData;

   dir = OPEN_DIR("C:\\temp\\jdk-src", dirData);
   if (IS_DIR_OPEN(dir)) {
      YAPP_MEM_MANAGER *memManager = yapp_mem_new();
      totalTime = scanSources(memManager, "C:\\temp\\jdk-src", dir, 0);
      yapp_mem_free(memManager);
      CLOSE_DIR(dir);
      printf("Parse time: %dms\n", totalTime);
      printf("Total time: %dms\n", (clock() - initTime));
      printf("Total Lines: %d\n", linesCount);
      printf("Total Size: %d KB\n", (totalSize / 1024));
   }
}

static void testeParseJava() {
   YAPP_BUFFER *buffer;
   JavaParser *parser;
   YAPP_NODE *node;
   YAPP_TRACE_PARSER trace;
   clock_t startTimeMillis;

   printf("Criando buffer...\n");
   buffer = yapp_file_buffer_new("C:\\temp\\JavaParser.java");
   printf("Criando parser...\n");
   parser = javaParser_new(buffer);
   printf("Executando parser...\n");
   trace.enterRule = traceEnter;
   trace.exitRule = traceExit;
   //javaParser_setTraceParser(parser, &trace);
   //javaParser_setTrace(parser, TRUE);
   startTimeMillis = clock();
   node = javaParser_parse(parser);
   printf("Tempo: %dms\n", clock() - startTimeMillis);
   if (node != NULL) {
      //YAPP_VISITOR *visitor = yapp_visitor_new(javaParser_rulesCount(parser));
      printf("SUCCESSED!!!\n");
      //printTree(buffer, node, 0);
   } else {
      printf("FAILED!!!\n");
   }
   //   printf("Liberando parser...\n");
   javaParser_free(parser);
   //   printf("Liberando buffer...\n");
   yapp_buffer_free(buffer);
}

int main(int argc, char * argv[]) {
   //testeParseJava();
   testeParseJDKSources();
   //printNodesCount();
}
