#ifndef _YAPP_MEM_MANAGER_

#define _YAPP_MEM_MANAGER_

#include "yapp_defs.h"

#define DEFAULT_PAGE_SIZE 1024 * 1024

typedef struct _YAPP_MEM_PAGE {
   struct _YAPP_MEM_PAGE *nextPage;
   BYTE buffer[1];
} YAPP_MEM_PAGE;

typedef struct _YAPP_MEM_MANAGER {
   YAPP_MEM_PAGE *firstPage;
   YAPP_MEM_PAGE *currentPage;
   INT32 pageSize;
   INT32 nextFree;
} YAPP_MEM_MANAGER;

YAPP_MEM_MANAGER *yapp_mem_newPageSize();
YAPP_MEM_MANAGER *yapp_mem_new();
void yapp_mem_reset(YAPP_MEM_MANAGER *memManager);
void yapp_mem_free(YAPP_MEM_MANAGER *memManager);
BYTE *yapp_mem_alloc(YAPP_MEM_MANAGER *memManager, INT32 size);

#endif