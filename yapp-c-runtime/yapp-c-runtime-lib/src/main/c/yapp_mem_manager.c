#include <stdlib.h>
#include "yapp_mem_manager.h"

YAPP_MEM_MANAGER *yapp_mem_newPageSize(INT32 pageSize) {
   YAPP_MEM_MANAGER *memMan = (YAPP_MEM_MANAGER *)malloc(sizeof(YAPP_MEM_MANAGER));
   memMan->firstPage = (YAPP_MEM_PAGE *)malloc(pageSize + sizeof(struct _YAPP_MEM_PAGE));
   memMan->currentPage = memMan->firstPage;
   memMan->pageSize = pageSize;
   memMan->nextFree = 0;
   memMan->firstPage->nextPage = NULL;
   return memMan;
}

YAPP_MEM_MANAGER *yapp_mem_new() {
   return yapp_mem_newPageSize(DEFAULT_PAGE_SIZE);
}

void yapp_mem_reset(YAPP_MEM_MANAGER *memManager) {
   memManager->currentPage = memManager->firstPage;
   memManager->nextFree = 0;
}

void yapp_mem_free(YAPP_MEM_MANAGER *memManager) {
   YAPP_MEM_PAGE *currentPage = memManager->firstPage;
   YAPP_MEM_PAGE *nextPage;
   while (currentPage != NULL) {
      nextPage = currentPage->nextPage;
      free(currentPage);
      currentPage = nextPage;
   }
   free(memManager);
}

BYTE *yapp_mem_alloc(YAPP_MEM_MANAGER *memManager, INT32 size) {
   if (size <= memManager->pageSize) {
      BYTE *addr;
      if (memManager->nextFree + size > memManager->pageSize) {
         if (memManager->currentPage->nextPage == NULL) {
            YAPP_MEM_PAGE *newPage = (YAPP_MEM_PAGE *)malloc(memManager->pageSize + sizeof(struct _YAPP_MEM_PAGE));
            newPage->nextPage = NULL;
            memManager->currentPage->nextPage = newPage;
            memManager->currentPage = newPage;
         } else {
            memManager->currentPage = memManager->currentPage->nextPage;
         }
         memManager->nextFree = 0;
      }
      addr = &memManager->currentPage->buffer[memManager->nextFree];
      memManager->nextFree += size;
      return addr;
   }
   return NULL;
}
