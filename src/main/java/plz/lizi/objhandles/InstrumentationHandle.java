package plz.lizi.objhandles;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;
import plz.lizi.objhandles.HandleBase.Handle;
import plz.lizi.objhandles.api.common.PLZBase;

public class InstrumentationHandle implements Handle, Instrumentation {
	private final Set<ClassFileTransformer> TRANSFORMERS = new CopyOnWriteArraySet<>();
	private final Map<Object, byte[]> DIRECTSUPPLIER = new ConcurrentHashMap<>();
	private Object inst;

	public InstrumentationHandle(Instrumentation _inst) {
		if (_inst == null)
			return;
		inst = _inst;
		((Instrumentation) inst).addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				byte[] directValue = DIRECTSUPPLIER.remove(className);
				if (directValue == null && classBeingRedefined != null) {
					directValue = DIRECTSUPPLIER.remove(classBeingRedefined);
				}
				if (directValue != null) {
					return directValue;
				}
				byte[] buf = classfileBuffer;
				if (!TRANSFORMERS.isEmpty()) {
					for (ClassFileTransformer transformer : TRANSFORMERS) {
						if (transformer == null) {
							continue;
						}
						try {
							byte[] newBuf = transformer.transform(loader, className, classBeingRedefined, protectionDomain, buf);
							if (newBuf != null) {
								buf = newBuf;
							}
						} catch (Throwable e) {
							PLZBase.throwEx(e);
						}
					}
				}
				return classfileBuffer;
			}
		}, isRetransformClassesSupported() && isRedefineClassesSupported());
	}

	@Override
	public Class<?> zhisClass() {
		return inst.getClass();
	}

	@Override
	public Object zhis() {
		return inst;
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
		TRANSFORMERS.add(transformer);
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		TRANSFORMERS.add(transformer);
	}

	public void addDirectSupplier(Object key, byte[] buf) {
		DIRECTSUPPLIER.put(key, buf);
	}

	@Override
	public boolean removeTransformer(ClassFileTransformer transformer) {
		return TRANSFORMERS.remove(transformer);
	}

	@Override
	public boolean isRetransformClassesSupported() {
		return ((Instrumentation) inst).isRetransformClassesSupported();
	}

	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
		((Instrumentation) inst).retransformClasses(classes);
	}

	@Override
	public boolean isRedefineClassesSupported() {
		return ((Instrumentation) inst).isRedefineClassesSupported();
	}

	@Override
	public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
		((Instrumentation) inst).redefineClasses(definitions);
	}

	@Override
	public boolean isModifiableClass(Class<?> theClass) {
		return ((Instrumentation) inst).isModifiableClass(theClass);
	}

	@Override
	public Class<?>[] getAllLoadedClasses() {
		return ((Instrumentation) inst).getAllLoadedClasses();
	}

	@Override
	public Class<?>[] getInitiatedClasses(ClassLoader loader) {
		return ((Instrumentation) inst).getInitiatedClasses(loader);
	}

	@Override
	public long getObjectSize(Object objectToSize) {
		return ((Instrumentation) inst).getObjectSize(objectToSize);
	}

	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
		((Instrumentation) inst).appendToBootstrapClassLoaderSearch(jarfile);
	}

	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
		((Instrumentation) inst).appendToSystemClassLoaderSearch(jarfile);
	}

	@Override
	public boolean isNativeMethodPrefixSupported() {
		return ((Instrumentation) inst).isNativeMethodPrefixSupported();
	}

	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
		((Instrumentation) inst).setNativeMethodPrefix(transformer, prefix);
	}

	@Override
	public void redefineModule(Module module, Set<Module> extraReads, Map<String, Set<Module>> extraExports, Map<String, Set<Module>> extraOpens, Set<Class<?>> extraUses, Map<Class<?>, List<Class<?>>> extraProvides) {
		((Instrumentation) inst).redefineModule(module, extraReads, extraExports, extraOpens, extraUses, extraProvides);
	}

	@Override
	public boolean isModifiableModule(Module module) {
		return ((Instrumentation) inst).isModifiableModule(module);
	}
	
}
