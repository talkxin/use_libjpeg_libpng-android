#pragma hdrstop
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <openssl/des.h>
#include <jni.h>

#pragma argsused
#define BUFSIZE 256
#define CFBMODE 64

unsigned char* encrypt(unsigned char* key, unsigned char* IV, unsigned char* in,
		int insize, int* pl) {
	DES_key_schedule schedule;
	DES_key_schedule schedule2;
	DES_key_schedule schedule3;
	DES_cblock desKey = { 0 };
	DES_cblock iv = { 0 };

	memcpy(desKey, key, 8);
	DES_set_key_unchecked(&desKey, &schedule);
	memcpy(desKey, key + 8, 8);
	DES_set_key_unchecked(&desKey, &schedule2);
	memcpy(desKey, key + 16, 8);
	DES_set_key_unchecked(&desKey, &schedule3);
	memcpy(iv, IV, 8);

	const size_t paddingLength = *pl = (8 - insize % 8);
	unsigned char* padding = malloc(insize + paddingLength);
	unsigned char* result = malloc(insize + paddingLength);
	memcpy(padding, in, insize);
	int i = 0;
	for (i = insize; i < insize + paddingLength; i++) {
		padding[i] = paddingLength;
	}
	DES_ede3_cfb_encrypt(&padding[0], &result[0], CFBMODE,
			insize + paddingLength, &schedule, &schedule2, &schedule3, &iv,
			DES_ENCRYPT);
	return result;
}

unsigned char* decrypt(unsigned char* key, unsigned char* IV, unsigned char* in,
		int insize, int* pl) {
	DES_key_schedule schedule;
	DES_key_schedule schedule2;
	DES_key_schedule schedule3;
	DES_cblock desKey = { 0 };
	DES_cblock iv = { 0 };
	memcpy(desKey, key, 8);
	DES_set_key_unchecked(&desKey, &schedule);
	memcpy(desKey, key + 8, 8);
	DES_set_key_unchecked(&desKey, &schedule2);
	memcpy(desKey, key + 16, 8);
	DES_set_key_unchecked(&desKey, &schedule3);
	memcpy(iv, IV, 8);
	unsigned char* result = malloc(insize);
	DES_ede3_cfb_encrypt(&in[0], &result[0], CFBMODE, insize, &schedule,
			&schedule2, &schedule3, &iv, DES_DECRYPT);
	int paddingLength = *pl = insize - result[insize - 1];
	unsigned char* padding = NULL;
	if (paddingLength > 0 && result[insize - 1] == result[paddingLength]) {
		padding = malloc(paddingLength);
		memcpy(padding, result, paddingLength);
	}
	return padding;
}

unsigned char* jstrinTostring(JNIEnv* env, jbyteArray barr) {
	unsigned char* rtn = NULL;
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

jbyteArray Java_com_allstar_cinclient_tools_encryption_BusinessUtiles_ndecrypt(
		JNIEnv* env, jobject thiz, jbyteArray key, jbyteArray iv,
		jbyteArray encryptContent, int size) {
	unsigned char* dk = jstrinTostring(env, key);
	unsigned char* IV = jstrinTostring(env, iv);
	unsigned char* in = jstrinTostring(env, encryptContent);
	int i = 0;
	unsigned char* result = decrypt(dk, IV, in, size, &i);
	jbyte *by = (jbyte*) result;
	jbyteArray jarray = (*env)->NewByteArray(env,  i >= 0 ? i : 0);
	(*env)->SetByteArrayRegion(env, jarray, 0,  i, by);
	return jarray;
}
jbyteArray Java_com_allstar_cinclient_tools_encryption_BusinessUtiles_nencrypt(
		JNIEnv* env, jobject thiz, jbyteArray key, jbyteArray iv,
		jbyteArray encryptContent, int size) {
	unsigned char* dk = jstrinTostring(env, key);
	unsigned char* IV = jstrinTostring(env, iv);
	unsigned char* in = jstrinTostring(env, encryptContent);
	int i = 0;
	unsigned char* result = encrypt(dk, IV, in, size, &i);
	jbyte *by = (jbyte*) result;
	jbyteArray jarray = (*env)->NewByteArray(env, size + (i >= 0 ? i : 0));
	(*env)->SetByteArrayRegion(env, jarray, 0, size + i, by);
	return jarray;
}
