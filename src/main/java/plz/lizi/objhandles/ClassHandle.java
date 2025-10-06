package plz.lizi.objhandles;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import plz.lizi.objhandles.HandleBase.Handle;
import plz.lizi.objhandles.api.common.PLZBase;
import plz.lizi.objhandles.api.javassist.ClassPool;
import plz.lizi.objhandles.api.javassist.CtClass;

public final class ClassHandle implements Handle {
	private static final Map<String, Class<?>> HIDDEN_CLASS_MAP = new HashMap<>();
	private Class<?> _class;
	private byte[] _bytes;
	private String _internalName;
	private String _className;
	private ClassLoader _loader;

	public ClassHandle(Class<?> klass) {
		try {
			this._class = klass;
			this._internalName = klass.getName();
			this._className = _internalName;
			this._loader = klass.getClassLoader();
			this._bytes = PLZBase.getClassBytes(klass, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassHandle(String name) {
		try {
			this._class = Class.forName(name);
			this._internalName = _class.getName();
			this._className = _internalName;
			this._loader = _class.getClassLoader();
			this._bytes = PLZBase.getClassBytes(_class, false);
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
				this._class = Class.forName(name, false, loader);
				this._internalName = _class.getName();
				this._className = _class.getName();
				this._loader = _class.getClassLoader();
				this._bytes = PLZBase.getClassBytes(_class, true);
			} else {
				this._internalName = name;
				this._className = name;
				this._loader = loader;
				this._bytes = PLZBase.getClassBytes(PLZBase.getJarPath(lookup), name);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassHandle(String name, ClassLoader loader, byte[] bytes) {
		try {
			if (PLZBase.wasLoaded(name, loader)) {
				this._class = Class.forName(name, false, loader);
				this._internalName = _class.getName();
				this._className = _class.getName();
				this._loader = _class.getClassLoader();
				this._bytes = PLZBase.getClassBytes(_class, false);
			} else {
				this._internalName = name;
				this._className = name;
				this._loader = loader;
				this._bytes = bytes;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void redefine(byte[] newBuf) throws Throwable {
		HandleBase.getInst().addDirectSupplier(_class, newBuf);
		HandleBase.getInst().redefineClasses(new ClassDefinition(_class, newBuf));
	}

	public void retransform(byte[] newBuf) throws Throwable {
		HandleBase.getInst().addDirectSupplier(_class, newBuf);
		HandleBase.getInst().retransformClasses(_class);
	}

	public static Class<?> findHidden(String name) {
		return HIDDEN_CLASS_MAP.get(name);
	}

	public Class<?> define() throws Throwable {
		if (_class != null) {
			return _class;
		}
		_class = (Class<?>) PLZBase.LOOKUP.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class)).invoke(_loader, _className, _bytes, 0, _bytes.length, null);
		if (_class != null)
			PLZBase.LOOKUP.findSetter(Class.class, "name", String.class).invoke(_class, _className);
		return _class;
	}

	public Class<?> defineHidden() throws Throwable {
		if (_class != null) {
			return _class;
		}
		if (HIDDEN_CLASS_MAP.containsKey(_internalName) && HIDDEN_CLASS_MAP.get(_internalName) != null) {
			return HIDDEN_CLASS_MAP.get(_internalName);
		}
		int flags = 6;
		if (_loader == null || _loader == ClassLoader.getPlatformClassLoader()) {
			flags |= 8;
		}
		_class = (Class<?>) PLZBase.LOOKUP.findStatic(ClassLoader.class, "defineClass0", MethodType.methodType(Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class)).invoke(_loader, ClassHandle.class, _className, _bytes, 0, _bytes.length, null, true, flags, null);
		if (_class != null)
			PLZBase.LOOKUP.findSetter(Class.class, "name", String.class).invoke(_class, _className);
		HIDDEN_CLASS_MAP.put(_internalName, _class);
		return _class;
	}

	public byte[] getBytes() {
		if (_bytes == null) {
			try {
				_bytes = PLZBase.getClassBytes(_class, true);
			} catch (Exception e) {
				try {
					_bytes = PLZBase.getClassBytes(_class, false);
				} catch (Exception e2) {
					PLZBase.throwEx(e2);
				}
			}
		}
		return _bytes;
	}

	public Class<?> zhisClass() {
		return _class;
	}

	public CtClass getCtClass() {
		return ClassPool.getDefault().getOrNull(_className);
	}

	public CtClass getCtClass(ClassPool pool) {
		return pool.getOrNull(_className);
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
				PLZBase.throwEx(e1);
			}
		}
		ctClass.detach();
	}

	public Object invoke(String mname, Object... args) {
		return new ObjectHandle(PLZBase.invoke(zhis(), mname, args));
	}

	public Object zhis() {
		return zhisClass();
	}

	public static List<Class<?>> getAllLoadedClasses() {
		return Arrays.asList(HandleBase.getInst().getAllLoadedClasses());
	}

	public static List<Object> getClassInstances(Class<?> cls) {
		return List.of(InstrumentationImpl.getClassInstances0(cls));
	}
}
