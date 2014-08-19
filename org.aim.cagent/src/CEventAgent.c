/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 ============================================================================
 Name        : JVMTITest.c
 Author      : Henning Schulz
 Version     :
 Copyright   :
 Description :
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>

#include <jvmti.h>
#include <jni.h>
#include "CEventAgent.h"

static jvmtiEnv * jvmti;
static jvmtiError error;
static jclass agentClass;
static jmethodID onMonitorWait;
static jmethodID onMonitorEntered;

void JNICALL jvmti_wait_for_monitor_enter(jvmtiEnv *jvmti_env, JNIEnv* jni_env,
		jthread thread, jobject object) {
	jlong nanos_ptr;
	(*jvmti)->GetTime(jvmti, &nanos_ptr);
	(*jni_env)->CallStaticVoidMethod(jni_env, agentClass, onMonitorWait, thread,
			object, nanos_ptr);
}

void JNICALL jvmti_monitor_entered(jvmtiEnv *jvmti_env, JNIEnv* jni_env,
		jthread thread, jobject object) {
	jlong nanos_ptr;
	(*jvmti)->GetTime(jvmti, &nanos_ptr);
	(*jni_env)->CallStaticVoidMethod(jni_env, agentClass, onMonitorEntered, thread,
			object, nanos_ptr);
}

JNIEXPORT void JNICALL Java_org_aim_mainagent_CEventAgentAdapter_printlnNonBlocking(JNIEnv *jni_env,
		jclass class, jstring javaMessage) {
	const char *cMessage = (*jni_env)->GetStringUTFChars(jni_env, javaMessage,
			NULL);
	if (NULL == cMessage)
	return;

	printf("%s\n", cMessage);
	(*jni_env)->ReleaseStringUTFChars(jni_env, javaMessage, cMessage);
}

JNIEXPORT void JNICALL Java_org_aim_mainagent_CEventAgentAdapter_init(JNIEnv* jni_env, jclass aClass) {
	agentClass = aClass;
	onMonitorWait = (*jni_env)->GetStaticMethodID(jni_env, agentClass,
			"onMonitorWait", "(Ljava/lang/Thread;Ljava/lang/Object;J)V");
	if (onMonitorWait == NULL) {
		printf("WARN CEventAgent: onMonitorWait jmethodID is NULL");
	}

	onMonitorEntered = (*jni_env)->GetStaticMethodID(jni_env, agentClass,
			"onMonitorEntered", "(Ljava/lang/Thread;Ljava/lang/Object;J)V");
	if (onMonitorWait == NULL) {
		printf("WARN CEventAgent: onMonitorEntered jmethodID is NULL");
	}
}

JNIEXPORT void JNICALL Java_org_aim_mainagent_CEventAgentAdapter_activateMonitorEvents(JNIEnv* jni_env,
		jclass class) {
	jvmtiCapabilities caps;
	memset(&caps, 0, sizeof(jvmtiCapabilities));
	error = (*jvmti)->GetCapabilities(jvmti, &caps);
	caps.can_generate_monitor_events = 1;
	error = (*jvmti)->AddCapabilities(jvmti, &caps);

	error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_MONITOR_CONTENDED_ENTER, NULL);
	error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_MONITOR_CONTENDED_ENTERED, NULL);
}

JNIEXPORT void JNICALL Java_org_aim_mainagent_CEventAgentAdapter_deactivateMonitorEvents(JNIEnv* jni_env,
		jclass class) {
	jvmtiCapabilities caps;
	memset(&caps, 0, sizeof(jvmtiCapabilities));
	caps.can_generate_monitor_events = 1;
	error = (*jvmti)->RelinquishCapabilities(jvmti, &caps);

	error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_MONITOR_CONTENDED_ENTER, NULL);
	error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_MONITOR_CONTENDED_ENTERED, NULL);
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
	jvmtiEventCallbacks callbacks;
	jvmtiCapabilities caps;
	memset(&caps, 0, sizeof(jvmtiCapabilities));

	jvmti = NULL;

	(*jvm)->GetEnv(jvm, &jvmti, JVMTI_VERSION_1_0);

	caps.can_generate_monitor_events = 0;
	error = (*jvmti)->AddCapabilities(jvmti, &caps);

	callbacks.MonitorContendedEnter = &jvmti_wait_for_monitor_enter;
	callbacks.MonitorContendedEntered = &jvmti_monitor_entered;
	error = (*jvmti)->SetEventCallbacks(jvmti, &callbacks,
			(jint) sizeof(callbacks));

	printf("INFO CEventAgent: initialized.\n");

	return 0;
}
