#ifndef _YAPP_BUFFER_

#define _YAPP_BUFFER_

#include "yapp_defs.h"

typedef char * ( * BUFFER_GET_TEXT )(void *data, INT32 startIndex, INT32 endIndex);
typedef char * ( * BUFFER_GET_TEXT_PTR )(void *data, INT32 index);
/* Deve retornar '\0' em caso de indice invalido */
typedef char   ( * BUFFER_GET_CHAR )(void *data, INT32 index);
typedef INT32  ( * BUFFER_LENGTH   )(void *data);
typedef void   ( * BUFFER_FREE   )(void *data);


typedef struct _YAPP_BUFFER {
   BUFFER_GET_TEXT getText;
   BUFFER_GET_TEXT_PTR getTextPointer;
   BUFFER_GET_CHAR getChar;
   BUFFER_LENGTH   length;
   BUFFER_FREE     free;
   void *data;
} YAPP_BUFFER;

YAPP_BUFFER* yapp_buffer_new();

void yapp_buffer_free(YAPP_BUFFER *buffer);

char * yapp_buffer_getText(YAPP_BUFFER *buffer, INT32 startIndex, INT32 endIndex);

char * yapp_buffer_getTextPointer(YAPP_BUFFER *buffer, INT32 index);

char yapp_buffer_getChar(YAPP_BUFFER *buffer, INT32 index);
   
BOOL yapp_buffer_matchChar(YAPP_BUFFER *buffer, INT32 index, char c);

BOOL yapp_buffer_matchCharRange(YAPP_BUFFER *buffer, INT32 index, char charIni, char charEnd);
   
BOOL yapp_buffer_matchIgnoreCaseChar(YAPP_BUFFER *buffer, INT32 index, char c);
 
BOOL yapp_buffer_matchString(YAPP_BUFFER *buffer, INT32 index, char *str, INT32 strLen);
   
BOOL yapp_buffer_matchIgnoreCaseString(YAPP_BUFFER *buffer, INT32 index, char *str, INT32 strLen);
   
BOOL yapp_buffer_isEoi(YAPP_BUFFER *buffer, INT32 index);
   
INT32 yapp_buffer_length(YAPP_BUFFER *buffer);

#endif
