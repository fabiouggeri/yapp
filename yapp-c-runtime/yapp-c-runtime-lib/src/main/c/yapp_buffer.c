#include <stdlib.h>
#include <string.h>
#include "yapp_buffer.h"

YAPP_BUFFER* yapp_buffer_new() {
   YAPP_BUFFER *buffer = (YAPP_BUFFER*) malloc(sizeof(YAPP_BUFFER));
   memset(buffer, NULL, sizeof(YAPP_BUFFER));
   return buffer;
}

void yapp_buffer_free(YAPP_BUFFER *buffer) {
   if (buffer->free != NULL) {
      buffer->free(buffer->data);
   }
   free(buffer);
}

char * yapp_buffer_getText(YAPP_BUFFER *buffer, INT32 startIndex, INT32 endIndex) {
   return buffer->getText(buffer->data, startIndex, endIndex);
}

char * yapp_buffer_getTextPointer(YAPP_BUFFER *buffer, INT32 index) {
   return buffer->getTextPointer(buffer->data, index);
}

char yapp_buffer_getChar(YAPP_BUFFER *buffer, INT32 index) {
   return buffer->getChar(buffer->data, index);
}
   
BOOL yapp_buffer_matchChar(YAPP_BUFFER *buffer, INT32 index, char c) {
   return buffer->getChar(buffer->data, index) == c;
}

BOOL yapp_buffer_matchCharRange(YAPP_BUFFER *buffer, INT32 index, char charIni, char charEnd) {
   char c = buffer->getChar(buffer->data, index);
   return c >= charIni && c <= charEnd;
}
   
BOOL yapp_buffer_matchIgnoreCaseChar(YAPP_BUFFER *buffer, INT32 index, char c) {
   return tolower(buffer->getChar(buffer->data, index)) == tolower(c);
}
 
BOOL yapp_buffer_matchString(YAPP_BUFFER *buffer, INT32 index, char *str, INT32 strLen) {
   int i;
   int j;
   for (i = 0, j = index; i < strLen; i++, j++) {
      if (str[i] != buffer->getChar(buffer->data, j)) {
         return FALSE;
      }
   }
   return TRUE;
}
   
BOOL yapp_buffer_matchIgnoreCaseString(YAPP_BUFFER *buffer, INT32 index, char *str, INT32 strLen) {
   int i;
   int j;
   for (i = 0, j = index; i < strLen; i++, j++) {
      if (tolower(str[i]) != tolower(buffer->getChar(buffer->data, j))) {
         return FALSE;
      }
   }
   return TRUE;
}
   
BOOL yapp_buffer_isEoi(YAPP_BUFFER *buffer, INT32 index) {
   return buffer->getChar(buffer->data, index) == '\0';
}
   
INT32 yapp_buffer_length(YAPP_BUFFER *buffer) {
   return buffer->length(buffer->data);
}
