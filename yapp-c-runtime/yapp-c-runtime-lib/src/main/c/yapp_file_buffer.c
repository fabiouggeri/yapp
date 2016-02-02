#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "yapp_file_buffer.h"

typedef struct _FILE_BUFFER_DATA  {
   char *filePathName;
   INT32 dataLength;
   char buffer[1];
} FILE_BUFFER_DATA;

static void freeData(void *bufferData) {
   FILE_BUFFER_DATA *data = (FILE_BUFFER_DATA *)bufferData;
   free(data->filePathName);
   free(data);
}

static INT32 lengthData(void *bufferData) {
   return ((FILE_BUFFER_DATA *)bufferData)->dataLength;
}

static char getChar(void *bufferData, INT32 index) {
   if (index >= 0 && index < ((FILE_BUFFER_DATA *)bufferData)->dataLength) {
      return ((FILE_BUFFER_DATA *)bufferData)->buffer[index];
   }
   return '\0';
}

static char* getText(void *bufferData, INT32 startIndex, INT32 endIndex) {
   if (endIndex > startIndex && startIndex >= 0 && endIndex <= ((FILE_BUFFER_DATA *)bufferData)->dataLength) {
      INT32 len = endIndex = startIndex;
      char *text = (char *) malloc(sizeof(char) * (len + 1));
      memcpy(text, &(((FILE_BUFFER_DATA *)bufferData)->buffer[startIndex]), sizeof(char) * len);
      text[len] = '\0';
      return text;
   }
   return NULL;
}

static char* getTextPointer(void *bufferData, INT32 index) {
   if (index >= 0 && index < ((FILE_BUFFER_DATA *)bufferData)->dataLength) {
      return &(((FILE_BUFFER_DATA *)bufferData)->buffer[index]);
   }
   return NULL;
}

YAPP_BUFFER* yapp_file_buffer_new(char *filePathName) {
   YAPP_BUFFER *buffer = NULL;
   INT32 fileSize;
   FILE * pFile;
   FILE_BUFFER_DATA *data;
   pFile = fopen(filePathName, "rb");
   if (pFile != NULL) {
      buffer = yapp_buffer_new();
      fseek(pFile, 0, SEEK_END);
      fileSize = ftell(pFile);
      data  = malloc(sizeof(FILE_BUFFER_DATA) + fileSize);
      fseek(pFile, 0, SEEK_SET);
      if (fread(data->buffer, 1, fileSize, pFile) == fileSize) {
         data->filePathName = strcpy((char *) malloc(sizeof(char) * (strlen(filePathName) + 1)), filePathName);
         data->dataLength = fileSize;
         buffer->data = data;
         buffer->getText = getText;
         buffer->getTextPointer = getTextPointer;
         buffer->getChar = getChar;
         buffer->length = lengthData;
         buffer->free = freeData;
      } else {
         free(data);
         yapp_buffer_free(buffer);
         buffer = NULL;
      }
      fclose(pFile);
   }
   return buffer;
}

