#ifndef _YAPP_DEFS_

#define _YAPP_DEFS_

#if ! defined( __NT__ ) && ! defined( __WINDOWS_386__ ) && ! defined( __WINDOWS__ )
#include <stdint.h>
#endif

#ifndef NULL
   #define NULL 0
#endif

#ifndef BOOL
   #define BOOL int
#endif

#ifndef TRUE
   #define TRUE 1
#endif

#ifndef FALSE
   #define FALSE 0
#endif

#ifndef INT32 
   #if defined( __NT__ ) || defined( __WINDOWS_386__ ) || defined( __WINDOWS__ )
      #define INT32 __int32
   #else
      #define INT32 int32_t
   #endif
#endif

#ifndef INT64
   #if defined( __NT__ ) || defined( __WINDOWS_386__ ) || defined( __WINDOWS__ )
      #define INT64 __int64
   #else
      #define INT64 int64_t
   #endif
#endif

#ifndef BYTE 
   #define BYTE unsigned char 
#endif

#endif
