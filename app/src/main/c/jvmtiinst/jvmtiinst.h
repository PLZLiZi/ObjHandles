﻿// 下列 ifdef 块是创建使从 DLL 导出更简单的
// 宏的标准方法。此 DLL 中的所有文件都是用命令行上定义的 JVMTIINST_EXPORTS
// 符号编译的。在使用此 DLL 的
// 任何项目上不应定义此符号。这样，源文件中包含此文件的任何其他项目都会将
// JVMTIINST_API 函数视为是从 DLL 导入的，而此 DLL 则将用此宏定义的
// 符号视为是被导出的。
#ifdef JVMTIINST_EXPORTS
#define JVMTIINST_API __declspec(dllexport)
#else
#define JVMTIINST_API __declspec(dllimport)
#endif

static JavaVM* JVM = nullptr;
static jvmtiEnv* JVMTI = nullptr;
static jclass instrumentationClass = nullptr;
static jmethodID transformMethod = nullptr;
static bool classFileLoadHookAdded = false;

static void JNICALL IClassFileLoadHookCallback(jvmtiEnv* jvmti_env, JNIEnv* jni_env, jclass class_being_redefined, jobject loader, const char* name, jobject protection_domain, jint class_data_len, const unsigned char* class_data, jint* new_class_data_len, unsigned char** new_class_data) {
	try {
		jstring className = jni_env->NewStringUTF(name);
		jbyteArray originalBytes = jni_env->NewByteArray(class_data_len);
		jni_env->SetByteArrayRegion(originalBytes, 0, class_data_len, (const jbyte*)class_data);
		jobject result = jni_env->CallStaticObjectMethod(
			instrumentationClass,
			transformMethod,
			loader,
			className,
			class_being_redefined,
			protection_domain,
			originalBytes);
		jni_env->DeleteLocalRef(className);
		jni_env->DeleteLocalRef(originalBytes);
		if (result != nullptr) {
			jbyteArray resultArray = (jbyteArray)result;
			jsize new_len = jni_env->GetArrayLength(resultArray);
			jbyte* new_data = jni_env->GetByteArrayElements(resultArray, nullptr);
			unsigned char* new_class_data_ptr = nullptr;
			jvmti_env->Allocate(new_len, &new_class_data_ptr);
			memcpy(new_class_data_ptr, new_data, new_len);
			*new_class_data_len = new_len;
			*new_class_data = new_class_data_ptr;
			jni_env->ReleaseByteArrayElements(resultArray, new_data, JNI_ABORT);
			jni_env->DeleteLocalRef(result);
		}
	}
	catch (...) {
		*new_class_data_len = 0;
		*new_class_data = nullptr;
	}
}

extern "C" {
	JNIEXPORT void JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_accessReload0(JNIEnv* jni, jclass) {
		if (!JVM) {
			jni->GetJavaVM(&JVM);
		}
		if (!JVMTI) {
			JVM->GetEnv((void**)&JVMTI, JVMTI_VERSION_1_1);
		}
		JVMTIAllAccess(JVMTI);
	}
	JNIEXPORT jobjectArray JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_getAllLoadedClasses0(JNIEnv* jni, jclass) {
		jint size = 0;
		jclass* classes{};
		JVMTI->GetLoadedClasses(&size, &classes);
		jobjectArray jclasses = toJobjectArray(jni, "Ljava/lang/Class;", (jobject*)classes, size);
		JVMTI->Deallocate((unsigned char*)classes);
		return jclasses;
	}
	JNIEXPORT void JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_enableClassLoadHook0(JNIEnv* jni, jclass) {
		if (!classFileLoadHookAdded) {
			classFileLoadHookAdded = true;
			jvmtiEventCallbacks callbacks;
			memset(&callbacks, 0, sizeof(callbacks));
			callbacks.ClassFileLoadHook = &IClassFileLoadHookCallback;
			JVMTI->SetEventCallbacks(&callbacks, sizeof(callbacks));
			instrumentationClass = (jclass)jni->NewGlobalRef(jni->FindClass("plz/lizi/objhandles/JVMTIInstrumentation"));
			if (instrumentationClass == nullptr) {
				return;
			}
			transformMethod = jni->GetStaticMethodID(instrumentationClass, "transform", "(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;Ljava/security/ProtectionDomain;[B)[B");
			if (transformMethod == nullptr) {
				return;
			}
			JVMTI->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, nullptr);
		}
	}
	JNIEXPORT void JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_retransformClass0(JNIEnv* jni, jclass, jclass klass) {
		
		jclass array[] = { klass };
		jvmtiError err = JVMTI->RetransformClasses(1, array);
		if (err != JVMTI_ERROR_NONE) {
			jni->ThrowNew(jni->FindClass("Ljava/lang/RuntimeException;"), to_string(err).c_str());
		}
	}
	JNIEXPORT void JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_redefineClass0(JNIEnv* jni, jclass, jclass klass, jbyteArray bytes) {
		jvmtiClassDefinition def{};
		jint count = 0;
		def.klass = klass;
		def.class_bytes = jbyteArray2BYTE(jni, bytes, &count);
		def.class_byte_count = count;
		jvmtiClassDefinition defs[] = { def };
		jvmtiError err = JVMTI->RedefineClasses(1, defs);
		if (err != JVMTI_ERROR_NONE) {
			jni->ThrowNew(jni->FindClass("Ljava/lang/RuntimeException;"), to_string(err).c_str());
		}
	}
	JNIEXPORT void JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_appendToClassLoaderSearch0(JNIEnv* jni, jclass, jstring jar, jboolean bootLoader) {
		jvmtiError err = JVMTI_ERROR_NONE;
		if (bootLoader)
		{
			err = JVMTI->AddToBootstrapClassLoaderSearch(Jstring2String(jni, jar).c_str());
		}
		else
		{
			err = JVMTI->AddToSystemClassLoaderSearch(Jstring2String(jni, jar).c_str());
		}
		if (err != JVMTI_ERROR_NONE) {
			jni->ThrowNew(jni->FindClass("Ljava/lang/RuntimeException;"), to_string(err).c_str());
		}
	}
	JNIEXPORT jlong JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_getObjectSize0(JNIEnv* jni, jclass, jobject obj) {
		jvmtiError err = JVMTI_ERROR_NONE;
		jlong size = 0;
		err = JVMTI->GetObjectSize(obj, &size);
		if (err != JVMTI_ERROR_NONE) {
			jni->ThrowNew(jni->FindClass("Ljava/lang/RuntimeException;"), to_string(err).c_str());
		}
		return size;
	}
	JNIEXPORT jstring JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_getJVMObjState0(JNIEnv* jni, jclass) {
		string state;
		state += "JVM   : 0x" + ptr2a(JVM, true) + "\n";
		state += "JVMTI : 0x" + ptr2a(JVMTI, true) + "\n";
		state += "JNI   : 0x" + ptr2a(jni, true);
		return String2Jstring(jni, state);
	}
	JNIEXPORT jobjectArray JNICALL Java_plz_lizi_objhandles_JVMTIInstrumentation_getClassInstances0(JNIEnv* jni, jclass, jclass klass) {
		jlong tag = 1;
		jint count = 0;
		jobject* instances = nullptr;
		JVMTI->GetObjectsWithTags(1, &tag, &count, &instances, 0);
		for (jint i = 0; i < count; i++) {
			JVMTI->SetTag(instances[i], 0);
		}
		count = 0;
		JVMTI->Deallocate((unsigned char*)instances);
		jni->ExceptionClear();
		JVMTI->IterateOverInstancesOfClass(klass, jvmtiHeapObjectFilter::JVMTI_HEAP_OBJECT_EITHER, [](jlong class_tag, jlong size, jlong* tag_ptr, void* user_data) -> jvmtiIterationControl {
			*tag_ptr = *(jlong*)user_data;
			return JVMTI_ITERATION_CONTINUE;
			}, &tag);
		JVMTI->GetObjectsWithTags(1, &tag, &count, &instances, 0);
		jobjectArray instancesArray = jni->NewObjectArray(count, klass, 0);
		for (jint i = 0; i < count; i++) {
			JVMTI->SetTag(instances[i], 0);
			jni->SetObjectArrayElement(instancesArray, i, instances[i]);
		}
		JVMTI->Deallocate((unsigned char*)instances);
		return instancesArray;
	}
}
