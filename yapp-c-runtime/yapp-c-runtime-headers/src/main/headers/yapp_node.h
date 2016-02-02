#ifndef _YAPP_NODE_

#define _YAPP_NODE_

#include "yapp_defs.h"
#include "yapp_buffer.h"
#include "yapp_rule.h"
#include "yapp_mem_manager.h"

typedef struct _YAPP_NODE {
   YAPP_RULE* rule;
   BOOL semantic;
   BOOL skiped;
   INT32 startIndex;
   INT32 endIndex;
   void *value;
   struct _YAPP_NODE *nextNode;
   struct _YAPP_NODE *firstChild;
   struct _YAPP_NODE *sibling;
} YAPP_NODE;

YAPP_NODE * yapp_node_newManaged(YAPP_MEM_MANAGER *memManager, YAPP_RULE *rule, INT32 startIndex, INT32 endIndex, BOOL semantic, BOOL skipNode);

YAPP_NODE * yapp_node_new(YAPP_RULE *rule, INT32 startIndex, INT32 endIndex, BOOL semantic, BOOL skipNode);

void yapp_node_free(YAPP_NODE *node);

void yapp_node_freeTree(YAPP_NODE *node);

void * yapp_node_getValue(YAPP_NODE *node);

void yapp_node_setValue(YAPP_NODE *node, void *value);

YAPP_RULE * yapp_node_getRule(YAPP_NODE *node);

INT32 yapp_node_getStartIndex(YAPP_NODE *node);

void yapp_node_setStartIndex(YAPP_NODE *node, INT32 index);

INT32 yapp_node_getEndIndex(YAPP_NODE *node);

void yapp_node_setEndIndex(YAPP_NODE *node, INT32 index);

char * yapp_node_getText(YAPP_NODE *node, YAPP_BUFFER *buffer);

YAPP_NODE* yapp_node_getSibling(YAPP_NODE *node);

void yapp_node_setSibling(YAPP_NODE *node, YAPP_NODE *sibling);

YAPP_NODE* yapp_node_getFirstChild(YAPP_NODE *node);

void yapp_node_setFirstChild(YAPP_NODE *node, YAPP_NODE *child);

YAPP_NODE** yapp_node_getChildren(YAPP_NODE *node);

YAPP_NODE** yapp_node_getSemanticChildren(YAPP_NODE *node);

BOOL yapp_node_isSemantic(YAPP_NODE *node);

BOOL yapp_node_isSkiped(YAPP_NODE *node);

void printNodesCount();

#endif
