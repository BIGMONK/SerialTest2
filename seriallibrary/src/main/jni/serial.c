//
// Created by win7 on 2016/8/18.
//

#include "com_km1930_dynamicbicycleclient_serialndk_Serial.h"
#include "stdio.h"
#include<fcntl.h>
#include<termios.h>
#include "android/log.h"


#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
int Serial_fd;

/*
 * Class:     com_km1930_dynamicbicycleclient_serialndk_Serial
 * Method:    OpenSerial
 * Signature: (I)I
 */
JNIEXPORT jint
JNICALL Java_com_km1930_dynamicbicycleclient_serialndk_Serial_OpenSerial
        (JNIEnv * env, jclass
jc,
jint num
){
LOGI("Serial_OpenSerial");
Serial_fd = open("/dev/ttyS1", O_RDWR | O_NOCTTY | O_NONBLOCK);
LOGI("fd = %d\n", Serial_fd);
if (Serial_fd == -1) {
return -1;
} else {
return
Serial_fd;
}
}

/*
 * Class:     com_km1930_dynamicbicycleclient_serialndk_Serial
 * Method:    SetSerialBaud
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_km1930_dynamicbicycleclient_serialndk_Serial_SetSerialBaud
(JNIEnv
*env,
jclass jc, jlong
baud){
int ret;

struct termios Options;

ret = tcgetattr(Serial_fd, &Options);
LOGI("tcgetattr ret = %d\n", ret);
if (ret != 0) {
return;
}

//如果发生数据溢出，接收数据，但是不再读取
//   tcflush(Serial_fd,TCIFLUSH);

cfsetispeed(&Options, B115200);//设置输入波特率
cfsetospeed(&Options, B115200);//设置输出波特率

Options.c_cflag |=
CLOCAL;
//修改控制模式，保证程序不会占用串口
Options.c_cflag |=
CREAD;
//修改控制模式，使得能够从串口中读取输入数据
Options.c_cflag &=
~CRTSCTS;//不使用流控

//修改输出模式，原始数据输出
Options.c_oflag &=
~OPOST;
//修改输入模式，原始数据输入
Options.c_lflag &=
~(ICANON | ECHO | ECHOE | ECHOK | ECHONL | NOFLSH);

Options.c_iflag &=
~(ICRNL | BRKINT | IGNBRK);

Options.c_oflag &=
~(ONLCR | OCRNL);

Options.c_iflag &=
~(IXON | IXOFF | IXANY);

//8位数据位
Options.c_cflag &=
~CSIZE; //屏蔽其他标志位
Options.c_cflag |=
CS8; //数据位为8

//无奇偶校验
Options.c_cflag &=
~PARENB;
Options.c_iflag &=
~INPCK;

//停止位为1
Options.c_cflag &=
~CSTOPB;

//设置等待时间和最小接收字符
Options.c_cc[VTIME] = 0; /* 读取一个字符等待1*(1/10)s */
Options.c_cc[VMIN] = 0; /* 读取字符的最少个数为1 */

//如果发生数据溢出，接收数据，但是不再读取
tcflush(Serial_fd, TCIFLUSH
);

//激活配置 (将修改后的termios数据设置到串口中）
ret = tcsetattr(Serial_fd, TCSANOW, &Options);
LOGI("tcgetattr ret = %d\n", ret);
if (ret != 0) {
return;
}
}

/*
 * Class:     com_km1930_dynamicbicycleclient_serialndk_Serial
 * Method:    ReadSerialBuf
 * Signature: ([SI)I
 */
JNIEXPORT jint
JNICALL Java_com_km1930_dynamicbicycleclient_serialndk_Serial_ReadSerialBuf
        (JNIEnv * env, jclass
jc,
jshortArray readArray, jint
length){
jshort *arr;
int ret;
int i;
unsigned char ch[100] = {0};

arr = (*env)->GetShortArrayElements(env, readArray, NULL);
ret = read(Serial_fd, ch, length);
//LOGI("read ret = %d\n", ret);
for(
i = 0;
i<length;
i++) {
arr[i] = ch[i];
//                LOGI("ReadSerialBuf ch[%d] = %x\n",i,ch[i]);

}
//            LOGI("read ret = %d\n",ret);
//            for(i = 0;i < ret;i++)
//                LOGI("arr[%d] = %d\n",i,arr[i]);
(*env)->
SetShortArrayRegion(env, readArray,
0,ret,arr);
usleep(20000);
return
ret;
}

/*
 * Class:     com_km1930_dynamicbicycleclient_serialndk_Serial
 * Method:    WriteSerialBuf
 * Signature: ([SI)I
 */
JNIEXPORT jint
JNICALL Java_com_km1930_dynamicbicycleclient_serialndk_Serial_WriteSerialBuf
        (JNIEnv * env, jclass
jc,
jshortArray writeArray, jint
length){
jshort *arr;
int ret;
int i = 0;
unsigned char ch[100];
//unsigned char *ch = "fengsannan";

arr = (*env)->GetShortArrayElements(env, writeArray, NULL);
for (
i = 0;
i<length;
i++) {
//        LOGI("WriteSerialBuf arr[%d] = %d\n", i, arr[i]);
ch[i] = (unsigned char) arr[i];
}

ret = write(Serial_fd, ch, length);
LOGI("WriteSerialBuf:  %d", ret);
if (ret == -1) {
return -1;
}
usleep(100000);
return
ret;
}

/*
 * Class:     com_km1930_dynamicbicycleclient_serialndk_Serial
 * Method:    CloseSerial
 * Signature: ()I
 */
JNIEXPORT jint
JNICALL Java_com_km1930_dynamicbicycleclient_serialndk_Serial_CloseSerial
        (JNIEnv * env, jclass
jc){
close(Serial_fd);

return 0;
}