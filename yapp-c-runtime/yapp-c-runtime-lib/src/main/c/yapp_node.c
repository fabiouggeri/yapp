#include <stdio.h>
#include <stdlib.h>
#include "yapp_defs.h"
#include "yapp_node.h"

static INT32 countChildren(YAPP_NODE *node, BOOL semantic) {
   if (node->firstChild == NULL) {
      return 0;
   } else {
      int count = 0;
      YAPP_NODE *lastNode = node->firstChild;
      while (lastNode != NULL) {
         if (lastNode->semantic || ! semantic) {
            ++count;
         }
         lastNode = lastNode->sibling;
      }
      return count;
   }
}

static INT64 s_nodesCreated = 0;

void printNodesCount() {
   printf("Created: %d \n", s_nodesCreated);
}

YAPP_NODE * yapp_node_newManaged(YAPP_MEM_MANAGER *memManager, YAPP_RULE *rule, INT32 startIndex, INT32 endIndex, BOOL semantic, BOOL skipNode) {
   YAPP_NODE *node = (YAPP_NODE *) yapp_mem_alloc(memManager, sizeof(YAPP_NODE));
   node->rule = rule;
   node->semantic = semantic;
   node->skiped = skipNode;
   node->startIndex = startIndex;
   node->endIndex = endIndex;
   node->nextNode = NULL;
   node->value = NULL;
   node->sibling = NULL;
   node->firstChild = NULL;
   return node;
}

YAPP_NODE * yapp_node_new(YAPP_RULE *rule, INT32 startIndex, INT32 endIndex, BOOL semantic, BOOL skipNode) {
   YAPP_NODE *node = (YAPP_NODE *) malloc(sizeof(YAPP_NODE));
   node->rule = rule;
   node->semantic = semantic;
   node->skiped = skipNode;
   node->startIndex = startIndex;
   node->endIndex = endIndex;
   node->nextNode = NULL;
   node->value = NULL;
   node->sibling = NULL;
   node->firstChild = NULL;
   return node;
}

void yapp_node_free(YAPP_NODE *node) {
   free(node);
}

void yapp_node_freeTree(YAPP_NODE *node) {
   YAPP_NODE *lastNode = node->firstChild;
   YAPP_NODE *nextNode;
   while (lastNode != NULL) {
      nextNode = lastNode->sibling;
      yapp_node_free(lastNode);
      lastNode = nextNode;
   }
   free(node);
}

void * yapp_node_getValue(YAPP_NODE *node) {
   return node->value;
}

void yapp_node_setValue(YAPP_NODE *node, void *value) {
   node->value = value;
}

YAPP_RULE * yapp_node_getRule(YAPP_NODE *node) {
   return node->rule;
}

INT32 yapp_node_getStartIndex(YAPP_NODE *node) {
   return node->startIndex;
}

void yapp_node_setStartIndex(YAPP_NODE *node, INT32 index) {
   node->startIndex = index;
}

INT32 yapp_node_getEndIndex(YAPP_NODE *node) {
   return node->endIndex;
}

void yapp_node_setEndIndex(YAPP_NODE *node, INT32 index) {
   node->endIndex = index;
}

char * yapp_node_getText(YAPP_NODE *node, YAPP_BUFFER *buffer) {
   return yapp_buffer_getText(buffer, node->startIndex, node->endIndex);
}

YAPP_NODE* yapp_node_getSibling(YAPP_NODE *node) {
   return node->sibling;
}

void yapp_node_setSibling(YAPP_NODE *node, YAPP_NODE *sibling) {
   node->sibling = sibling;
}

YAPP_NODE* yapp_node_getFirstChild(YAPP_NODE *node) {
   return node->firstChild;
}

void yapp_node_setFirstChild(YAPP_NODE *node, YAPP_NODE *child) {
   node->firstChild = child;
}

YAPP_NODE** yapp_node_getSemanticChildren(YAPP_NODE *node) {
   int count = countChildren(node, TRUE);
   int index = 0;
   YAPP_NODE** children = (YAPP_NODE**) malloc(sizeof(YAPP_NODE*) * count);
   YAPP_NODE *lastNode = node->firstChild;
   while (lastNode != NULL) {
      if (lastNode->semantic) {
         children[index++] = lastNode;
      }
      lastNode = lastNode->sibling;
   }
   return children;
}

YAPP_NODE** yapp_node_getChildren(YAPP_NODE *node) {
   int count = countChildren(node, FALSE);
   int index = 0;
   YAPP_NODE** children = (YAPP_NODE**) malloc(sizeof(YAPP_NODE*) * count);
   YAPP_NODE *lastNode = node->firstChild;
   while (lastNode != NULL) {
      children[index++] = lastNode;
      lastNode = lastNode->sibling;
   }
   return children;
}

BOOL yapp_node_isSemantic(YAPP_NODE *node) {
   return node->semantic;
}

BOOL yapp_node_isSkiped(YAPP_NODE *node) {
   return node->skiped;
}
