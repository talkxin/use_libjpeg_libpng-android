#include <string.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include "jpegcompress.h"
#include "pngcompress.h"

char* jstrinTostring(JNIEnv* env, jbyteArray barr) {
	char* rtn = NULL;
	jsize alen = (*env)->GetArrayLength(env, barr);
	jbyte* ba = (*env)->GetByteArrayElements(env, barr, 0);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0);
	return rtn;
}
/**
 * 返回文件大小
 */
long fileSize(const char* inputFile) {
	FILE* f = fopen(inputFile, "rb");
	long s = 0;
	if (f == NULL) {
		return s;
	} else {
		fseek(f, 0, SEEK_END);
		s = ftell(f);
		fclose(f);
		return s;
	}
}
//最大公约数
int proportion(int m, int n) {
	if (m < n) {
		int temp = m;
		m = n;
		n = temp;
	}
	while (m % n != 0) {
		int temp = m % n;
		m = n;
		n = temp;
	}
	return n;
}
//获取文件扩展名
char* substr(const char*str) {
	char *ptr, c = '.';
	int pos;
	ptr = strrchr(str, c);
	pos = ptr - str + 1;
	unsigned n = strlen(str) - pos;
	static char stbuf[256];
	strncpy(stbuf, str + pos, n);
	stbuf[n] = 0;
	return stbuf;
}

/**
 * 检查扩展名是否支持
 */
int checkExt(char *fileName) {
	char* ext = substr(fileName);
	if (strcasecmp(ext, "jpg") == 0) {
		return 1;
	} else if (strcasecmp(ext, "jpeg") == 0) {
		return 1;
	} else if (strcasecmp(ext, "bmp") == 0) {
		return 1;
	} else if (strcasecmp(ext, "png") == 0) {
		return 2;
	} else {
		return -1;
	}
}

/**
 * 缩放规则
 * 1,1/4，清晰度为65的图
 * 2,1/8，清晰度为50的图
 * 3，质量为50的图
 * 4，超小图115*115，质量为10
 */
void imageRule(int* tb_w, int *tb_h, int* q, int w, int h, int quality,
		long size) {
	int rx = w > h ? w : h;
	int ry = w > h ? h : w;
	int rz = proportion(rx, ry);
	switch (quality) {
	case 1:
		if (size > 1024 * 200) {
			if (rx / rz == 3 && ry / rz == 2) {
				*tb_w = w > h ? 1280 : 854;
				*tb_h = h > w ? 1280 : 854;
			} else {
				*tb_w = w > h ? 1280 : 720;
				*tb_h = h > w ? 1280 : 720;
			}
			*q = 65;
		} else {
			*tb_w = w;
			*tb_h = h;
			*q = 65;
		}
		break;
	case 2:
		if (size > 1024 * 50) {
			if (rx / rz == 3 && ry / rz == 2) {
				*tb_w = w > h ? 390 : 260;
				*tb_h = h > w ? 390 : 260;
			} else {
				*tb_w = w > h ? 480 : 270;
				*tb_h = h > w ? 480 : 270;
			}
			*q = 50;
		} else {
			*tb_w = w;
			*tb_h = h;
			*q = 50;
		}
		break;
	case 3:
		if (size > 1024 * 50) {
			*tb_w = w / 2;
			*tb_h = h / 2;
			*q = 50;
		} else {
			*tb_w = w;
			*tb_h = h;
			*q = 65;
		}
		break;
	case 4:
		*tb_w = 115;
		*tb_h = 115;
		*q = 10;
		break;
	}
}

//生成图片的缩略图（图片的一个缩小版本）
int generate_image_thumbnail(const char* inputFile, const char* outputFile,
		jboolean optimize, int quality) {
	if (inputFile == NULL || outputFile == NULL)
		return 0;

	//读取jpeg图片像素数组
	int w = 0, h = 0, tb_w = 0, tb_h = 0, q = 0;
	//图片文件大小
	long size = fileSize(inputFile);
	unsigned char* buff = NULL;
	int ext = checkExt(inputFile);
	switch (ext) {
	case 1:
		buff = ReadJpeg(inputFile, &w, &h);
		break;
	case 2:
		buff = ReadPng(inputFile, &w, &h);
		break;
	default:
		buff = ReadJpeg(inputFile, &w, &h);
		break;
	}

	if (buff == NULL) {
		printf("ReadJpeg Failed\n");
		return 0;
	}
	imageRule(&tb_w, &tb_h, &q, w, h, quality, size);
//	//缩放图片，缩放后的大小为(tb_w,tb_h)
	unsigned char * img_buf = do_Stretch_Linear(tb_w, tb_h, 24, buff, w, h);
	free(buff);

	//将缩放后的像素数组保存到jpeg文件
//	write_JPEG_file(outputFile, img_buf, 65, tb_h, tb_w);
	write_JPEG_file_android(img_buf, tb_w, tb_h, q, outputFile, optimize);
	free(img_buf);
	return 1;
}

long Java_com_example_testjpg_NativeUtil_compressBitmap2(JNIEnv* env,
		jobject thiz, jbyteArray input, jbyteArray output, jboolean optimize,
		int quality) {
	char * inputfile = jstrinTostring(env, input);
	char * outputfile = jstrinTostring(env, output);
	return generate_image_thumbnail(inputfile, outputfile, optimize, quality);
}
