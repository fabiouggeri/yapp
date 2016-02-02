#ifndef _YAPP_FILE_BUFFER_

#define _YAPP_FILE_BUFFER_

#include "yapp_buffer.h"

#define DEFAULT_BUFFER_SIZE 4096 

YAPP_BUFFER* yapp_file_buffer_new(char *filePathName);

#endif
