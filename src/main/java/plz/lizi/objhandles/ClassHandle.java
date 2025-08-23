package plz.lizi.objhandles;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import plz.lizi.objhandles.HandleBase.HandleInstance;
import plz.lizi.objhandles.api.common.PLZBase;
import plz.lizi.objhandles.api.superbyte.ClassPool;
import plz.lizi.objhandles.api.superbyte.CtClass;

public final class ClassHandle implements HandleInstance {
	private static final Map<String, Class<?>> HIDDEN_CLASS_MAP = new HashMap<>();
	private Class<?> klass;
	private byte[] bytes;
	private String internalName;
	private String className;
	private ClassLoader loader;

	public ClassHandle(Class<?> klass) {
		try {
			this.klass = klass;
			this.internalName = klass.getName();
			this.className = internalName;
			this.loader = klass.getClassLoader();
			this.bytes = PLZBase.getClassBytes(klass, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassHandle(String name) {
		try {
			this.klass = Class.forName(name);
			this.internalName = klass.getName();
			this.className = internalName;
			this.loader = klass.getClassLoader();
			this.bytes = PLZBase.getClassBytes(klass, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassHandle(String name, ClassLoader loader, Class<?> lookup) {
		try {
			if (loader == null && lookup != null) {
				loader = lookup.getClassLoader();
			}
			if (PLZBase.wasLoaded(name, loader)) {
				this.klass = Class.forName(name, false, loader);
				this.internalName = klass.getName();
				this.className = klass.getName();
				this.loader = klass.getClassLoader();
				this.bytes = PLZBase.getClassBytes(klass, true);
			} else {
				this.internalName = name;
				this.className = name;
				this.loader = loader;
				this.bytes = PLZBase.getClassBytes(PLZBase.getJarPath(lookup), name);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassHandle(String name, ClassLoader loader, byte[] bytes) {
		try {
			if (PLZBase.wasLoaded(name, loader)) {
				this.klass = Class.forName(name, false, loader);
				this.internalName = klass.getName();
				this.className = klass.getName();
				this.loader = klass.getClassLoader();
				this.bytes = PLZBase.getClassBytes(klass, false);
			} else {
				this.internalName = name;
				this.className = name;
				this.loader = loader;
				this.bytes = bytes;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void redefine(byte[] newBuf) throws Throwable {
		HandleBase.INST.redefineClasses(new ClassDefinition(klass, newBuf));
	}

	public void retransform(byte[] newBuf) throws Throwable {
		ClassFileTransformer transformer = new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if (className.equals(ClassHandle.this.className)) {
					return newBuf;
				}
				return classfileBuffer;
			}

			@Override
			public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if (className.equals(ClassHandle.this.className)) {
					return newBuf;
				}
				return classfileBuffer;
			}
		};
		HandleBase.INST.addTransformer(transformer, true);
		HandleBase.INST.retransformClasses(klass);
		HandleBase.INST.removeTransformer(transformer);
	}

	public static Class<?> findHidden(String name) {
		return HIDDEN_CLASS_MAP.get(name);
	}

	public Class<?> define() throws Throwable {
		if (klass != null) {
			return klass;
		}
		klass = (Class<?>) PLZBase.LOOKUP.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class)).invoke(loader, className, bytes, 0, bytes.length, null);
		setClassName(className);
		return klass;
	}

	public Class<?> defineHidden() throws Throwable {
		if (klass != null) {
			return klass;
		}
		if (HIDDEN_CLASS_MAP.containsKey(internalName) && HIDDEN_CLASS_MAP.get(internalName) != null) {
			return HIDDEN_CLASS_MAP.get(internalName);
		}
		int flags = 6;
		if (loader == null || loader == ClassLoader.getPlatformClassLoader()) {
			flags |= 8;
		}
		klass = (Class<?>) PLZBase.LOOKUP.findStatic(ClassLoader.class, "defineClass0", MethodType.methodType(Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class)).invoke(loader, ClassHandle.class, className, bytes, 0, bytes.length, null, true, flags, null);
		setClassName(className);
		HIDDEN_CLASS_MAP.put(internalName, klass);
		return klass;
	}

	public ClassLoader getLoader() {
		return loader;
	}

	public ClassHandle setLoader(ClassLoader loader) {
		this.loader = loader;
		return this;
	}

	public byte[] getBytes() {
		if (bytes == null) {
			try {
				bytes = PLZBase.getClassBytes(klass, true);
			} catch (Exception e) {}
		}
		return bytes;
	}

	public ClassHandle setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}

	public ClassHandle setClassName(String newName) throws Throwable {
		className = newName;
		if (klass != null)
			PLZBase.LOOKUP.findSetter(Class.class, "name", String.class).invoke(klass, className);
		return this;
	}

	public ClassHandle setInternalName(String newName) {
		this.internalName = newName;
		return this;
	}

	public String getInternalName() {
		return internalName;
	}

	public String getClassName() {
		return className;
	}

	public Class<?> getRealClass() {
		return klass;
	}

	public CtClass getCtClass() {
		return ClassPool.getDefault().getOrNull(className);
	}

	public CtClass getCtClass(ClassPool pool) {
		return pool.getOrNull(className);
	}
	
	public CtClass toCtClass() throws Exception {
		return ClassPool.getDefault().makeClassIfNew(new ByteArrayInputStream(getBytes()));
	}

	public CtClass toCtClass(ClassPool pool) throws Exception {
		return pool.makeClassIfNew(new ByteArrayInputStream(getBytes()));
	}

	public void configure(Consumer<CtClass> consumer) throws Exception {
		configure(ClassPool.getDefault(), consumer);
	}

	public void configure(ClassPool pool, Consumer<CtClass> consumer) throws Exception {
		CtClass ctClass = toCtClass(pool);
		if (ctClass.isFrozen()) {
			ctClass.defrost();
		}
		consumer.accept(ctClass);
		try {
			redefine(ctClass.toBytecode());
		} catch (Throwable e) {
			try {
				retransform(ctClass.toBytecode());
			} catch (Throwable e1) {
				e.addSuppressed(e1);
				throw new RuntimeException(e);
			}
		}
		ctClass.detach();
	}

	public Object getStaticField(String fname) {
		return new FieldHandle(this, fname).getObject();
	}

	public void setStaticField(String fname, Object o) {
		new FieldHandle(this, fname).set(o);
	}

	public Object invokeStaticMethod(String mname, MethodType type, Object... args) throws Throwable {
		Method m = klass.getDeclaredMethod(mname, type.parameterArray());
		try {
			return m.invoke(null, args);
		} catch (Throwable e) {
			try {
				return PLZBase.LOOKUP.unreflect(m).invoke(args);
			} catch (Throwable e1) {
				e1.initCause(e);
				throw e1;
			}
		}
	}

	public Object getObject() {
		return null;
	}

	public static List<Class<?>> getAllLoadedClasses() {
		return Arrays.asList(HandleBase.INST.getAllLoadedClasses());
	}

	public static List<Object> getClassInstances(Class<?> cls) {
		return List.of(JVMTIInstrumentation.getClassInstances0(cls));
	}
}
