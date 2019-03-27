//
// Created by xiao on 2017/9/22.
//
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>
#include <sys/mman.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/poll.h>
#include <sys/stat.h>
#include <linux/videodev2.h>
#include "v4l2_util.h"

#include "urbetter_usbcam_UsbCamControl.h"
#include "v4l2_util.h"
#include<android/log.h>

#define TAG "urbetter_usbcam_jni" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型


VideoBuffer * vbuffers = NULL ;
const int count = 5;
/*
 * Class:     urbetter_usbcam_UsbCamControl
 * Method:    CamOpen
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_urbetter_usbcam_UsbCamControl_CamOpen
  (JNIEnv * env , jclass cla, jstring jstr, jint jw, jint jh)
{
	struct v4l2_capability cap;
	struct v4l2_fmtdesc fmtdesc;
	struct v4l2_format fmt;
	char * name = (char *) (*env)->GetStringUTFChars(env, jstr, 0);  
	int w = jw;
	int h = jh;
	int fd = 0;
	int ret = 0;
	int index = 0;
	int i = 0;
	LOGD("dev name=%s,w=%d,h=%d",name,jw,jh);

	fd = open(name, O_RDWR);
	
    if (fd < 0) {
        LOGE("ERR(%s):Cannot open %s (error : %s)", __func__, name, strerror(errno));
        return 0;
    }else
        LOGD("open %s ok\n",name);

	ret = cam_query_cap(fd,&cap);
	LOGD("Driver Name:%s\nCard Name:%s\nBus info:%s\nDriver Version:%u.%u.%u",cap.driver,cap.card,cap.bus_info,(cap.version>>16)&0XFF, (cap.version>>8)&0XFF,cap.version&0XFF);

	fmtdesc.index = 0;
	fmtdesc.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;
	while(cam_enum_fmt(fd,&fmtdesc)==0){
		LOGD("\t%d.%s",fmtdesc.index,fmtdesc.description);
		fmtdesc.index++;
	}

	ret = cam_set_fmt(fd,w,h);
	if(ret){
		LOGD("set cam fmt failed\n");
		return 0;
	}

	ret = cam_get_fmt(fd,&fmt);
	LOGD("Current data format information:\n\twidth:%d\n\theight:%d\n",
				fmt.fmt.pix.width,fmt.fmt.pix.height);

	
	ret = cam_req_buf(fd, count);
	if(ret){
		LOGD("query bufferfailed\n");
		return 0;
	}

	vbuffers = (VideoBuffer * )calloc( count, sizeof(VideoBuffer) );
	if(vbuffers==NULL){
		LOGD("calloc buffer failed\n");
		return 0;
	}

	for(i=0;i<count;i++){
		ret = cam_query_buf(fd,i,vbuffers+i);
		if(ret){
			LOGD("query buffer failed\n");
			return 0;
		}
		ret = cam_q_buf(fd,i);
		if(ret){
			LOGD("q buffer failed\n");
			return 0;
		}
	}

	ret = cam_stream_on(fd);
	if(ret){
		LOGD("stream on failed\n");
		return 0;
	}

	
	(*env)->ReleaseStringUTFChars(env,jstr, name);
	return fd;
}

/*
 * Class:     urbetter_usbcam_UsbCamControl
 * Method:    CamClose
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_urbetter_usbcam_UsbCamControl_CamClose
  (JNIEnv * env , jclass cla, jint jfd)
{
	int fd = (int)jfd;
	cam_stream_off(fd);
	cam_free_buffer(vbuffers,count);
	close(fd);
}

/*
 * Class:     urbetter_usbcam_UsbCamControl
 * Method:    CamGetWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_urbetter_usbcam_UsbCamControl_CamGetWidth
  (JNIEnv * env , jclass cla, jint jfd)
{
	struct v4l2_format fmt;
	int ret = 0;
	int fd = jfd;
	ret = cam_get_fmt(fd,&fmt);
	LOGD("Current data format information:\n\twidth:%d\n\theight:%d",
				fmt.fmt.pix.width,fmt.fmt.pix.height);
	return fmt.fmt.pix.width;
}

/*
 * Class:     urbetter_usbcam_UsbCamControl
 * Method:    CamGetHeight
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_urbetter_usbcam_UsbCamControl_CamGetHeight
  (JNIEnv * env , jclass cla, jint jfd)
{
	struct v4l2_format fmt;
	int ret = 0;
	int fd = jfd;
	ret = cam_get_fmt(fd,&fmt);
	LOGD("Current data format information:\n\twidth:%d\n\theight:%d",
				fmt.fmt.pix.width,fmt.fmt.pix.height);
	return fmt.fmt.pix.height;
}

/*
 * Class:     urbetter_usbcam_UsbCamControl
 * Method:    CamGetData
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_urbetter_usbcam_UsbCamControl_CamGetData
  (JNIEnv * env , jclass cla, jint jfd)
{
	int ret = 0;
	int fd = jfd;
	int index = 0;
	int i = 0;
	unsigned char * buffer ;
	ret = cam_poll(fd,300);
	
	if(ret>0){
		index = cam_dq_buf(fd);		
		buffer = (unsigned char *)calloc(1,vbuffers[index].length);
//		if(buffer==NULL){
//		    return NULL;
//		}
		memcpy(buffer,vbuffers[index].start,vbuffers[index].length);
		
		cam_q_buf(fd,index);
	}else{
		LOGE("camera poll time out");
		return NULL;
	}

	
	jbyteArray array = (*env)->NewByteArray(env,vbuffers[index].length);
	(*env)->SetByteArrayRegion(env,array,0,vbuffers[index].length,buffer);
	free(buffer);
	return array; 
}
