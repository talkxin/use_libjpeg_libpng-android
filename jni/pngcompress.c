#include "png.h"
#include "pngcompress.h"
#include <stdio.h>
#include <stdlib.h>
#include <setjmp.h>
#define tmin(a, b) ((a)>(b) ? (b):(a))
#define tmax(a,b)  ((a) > (b)?(a):(b))
//!1不要将这三个宏同时打开
//需要互斥
//#define USE_PREMULTIPLY_APLHA (0)//使用预乘
#define USE_RGBA (1)//使用 r g b a
//#define USE_ABGR (0)//使用 a b g r

//预乘 aplha
#define CC_RGB_PREMULTIPLY_APLHA(vr, vg, vb, va) \
 (unsigned)(((unsigned)((unsigned char)(vr) * ((unsigned char)(va) + 1)) >> 8) | \
 ((unsigned)((unsigned char)(vg) * ((unsigned char)(va) + 1) >> 8) << 8) | \
 ((unsigned)((unsigned char)(vb) * ((unsigned char)(va) + 1) >> 8) << 16) | \
 ((unsigned)(unsigned char)(va) << 24))

//r g b a
#define CC_RGB_PREMULTIPLY_APLHA_RGBA(vr, vg, vb, va)\
 ( (unsigned)(vr))|\
    ( (unsigned)(vg) << 8)|\
  ( (unsigned)(vb) << 16)|\
  ((unsigned)(va) << 24)

//a b g r
//使用该宏的时候 write png时需要调用函数 png_set_swap_alpha
#define CC_RGB_PREMULTIPLY_APLHA_ABGR(vr, vg, vb, va)\
 ( (unsigned)(vr) << 8)|\
 ( (unsigned)(vg) << 16)|\
 ( (unsigned)(vb) << 24)|\
 ((unsigned)(va))

//读取png图片，并返回宽高，若出错则返回NULL
unsigned char* ReadPng(const char* path, int* width, int* height) {
	FILE* file = fopen(path, "rb");
	png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, 0, 0,
			0);
	png_infop info_ptr = png_create_info_struct(png_ptr);
	setjmp(png_jmpbuf(png_ptr));
	png_init_io(png_ptr, file);
	png_read_png(png_ptr, info_ptr, PNG_TRANSFORM_EXPAND, 0);
	int m_width = *width = png_get_image_width(png_ptr, info_ptr);
	int m_height = *height = png_get_image_height(png_ptr, info_ptr);
	int color_type = png_get_color_type(png_ptr, info_ptr);
	int bytesPerComponent = 3, i = 0, j = 0, p = 0;
	if (color_type & PNG_COLOR_MASK_ALPHA) {
		bytesPerComponent = 4;
		p = 1;
	}
	int size = m_height * m_width * bytesPerComponent;
	unsigned char *pImateRawData = (unsigned char *) malloc(size);
	png_bytep* rowPointers = png_get_rows(png_ptr, info_ptr);
	int bytesPerRow = m_width * bytesPerComponent;
	if (p == 1) {
		unsigned int *tmp = (unsigned int *) pImateRawData;
		for (i = 0; i < m_height; i++) {
			for (j = 0; j < bytesPerRow; j += 4) {
#if USE_PREMULTIPLY_APLHA
				*tmp++ = CC_RGB_PREMULTIPLY_APLHA( rowPointers[i][j], rowPointers[i][j + 1],
						rowPointers[i][j + 2], rowPointers[i][j + 3] );
#elif USE_RGBA
				*tmp++ = CC_RGB_PREMULTIPLY_APLHA_RGBA(rowPointers[i][j],
						rowPointers[i][j + 1], rowPointers[i][j + 2],
						rowPointers[i][j + 3]);
#elif USE_ABGR
				*tmp++ = CC_RGB_PREMULTIPLY_APLHA_ABGR( rowPointers[i][j], rowPointers[i][j + 1],
						rowPointers[i][j + 2], rowPointers[i][j + 3] );
#endif
			}
		}
	} else {
		for (j = 0; j < m_height; ++j) {
			memcpy(pImateRawData + j * bytesPerRow, rowPointers[j],
					bytesPerRow);
		}
	}
	return pImateRawData;
}
