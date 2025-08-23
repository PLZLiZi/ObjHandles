package plz.lizi.objhandles;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.instrument.UnmodifiableModuleException;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import plz.lizi.objhandles.api.common.PLZBase;

public class JVMTIInstrumentation implements Instrumentation {
	static {
		try {
			loadLibraryFromJar("plz/lizi/objhandles/jvmtiinst-x" + (System.getProperty("os.arch").contains("64") ? "64" : "86") + ".dll");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		accessReload0();
		enableClassLoadHook0();
	}

	protected final static List<ClassFileTransformer> transformers = new ArrayList<>();

	public JVMTIInstrumentation() {
	}

	private static void loadLibraryFromJar(String dllName) throws IOException {
		String resourcePath = "/" + dllName;
		Path tempFile = Files.createTempFile(PLZBase.splitLast(dllName, "/")[1], ".dll");
		try {
			InputStream inputStream = JVMTIInstrumentation.class.getResourceAsStream(resourcePath);
			Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
			Thread.sleep(200);
			System.load(tempFile.toAbsolutePath().toString());
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			tempFile.toFile().deleteOnExit();
		}
	}

	protected static byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
		byte[] newBuf = classfileBuffer;
		for (ClassFileTransformer transformer : new ArrayList<>(transformers)) {
			try {
				newBuf = transformer == null ?  newBuf : transformer.transform(loader, PLZBase.fromVMName(className), classBeingRedefined, protectionDomain, newBuf);
			} catch (Throwable e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return newBuf;
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
		transformers.add(transformer);
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		transformers.add(transformer);
	}

	@Override
	public boolean removeTransformer(ClassFileTransformer transformer) {
		transformers.remove(transformer);
		return true;
	}

	@Override
	public boolean isRetransformClassesSupported() {
		return true;
	}

	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
		for (Class<?> klass : classes) {
			retransformClass0(klass);
		}
	}

	@Override
	public boolean isRedefineClassesSupported() {
		return true;
	}

	@Override
	public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
		for (ClassDefinition definition : definitions) {
			redefineClass0(definition.getDefinitionClass(), definition.getDefinitionClassFile());
		}
	}

	@Override
	public boolean isModifiableClass(Class<?> theClass) {
		return true;
	}

	@Override
	public Class<?>[] getAllLoadedClasses() {
		return getAllLoadedClasses0();
	}

	@Override
	public Class<?>[] getInitiatedClasses(ClassLoader loader) {
		try {
			return new ArrayList<>((ArrayList<Class<?>>) PLZBase.LOOKUP.findGetter(ClassLoader.class, "classes", ArrayList.class).invoke(loader)).toArray(new Class<?>[0]);
		} catch (Throwable e) {
			return new Class<?>[0];
		}
	}

	@Override
	public long getObjectSize(Object objectToSize) {
		return getObjectSize0(objectToSize);
	}

	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
		appendToClassLoaderSearch0(jarfile.getName(), true);
	}

	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
		appendToClassLoaderSearch0(jarfile.getName(), false);
	}

	@Override
	public boolean isNativeMethodPrefixSupported() {
		return false;
	}

	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {}

	@Override
	public void redefineModule(Module module, Set<Module> extraReads, Map<String, Set<Module>> extraExports, Map<String, Set<Module>> extraOpens, Set<Class<?>> extraUses, Map<Class<?>, List<Class<?>>> extraProvides) {
		if (!module.isNamed())
			return;
		if (!isModifiableModule(module))
			throw new UnmodifiableModuleException(module.getName());
		// copy and check reads
		extraReads = new HashSet<>(extraReads);
		if (extraReads.contains(null))
			throw new NullPointerException("'extraReads' contains null");
		// copy and check exports and opens
		extraExports = cloneAndCheckMap(module, extraExports);
		extraOpens = cloneAndCheckMap(module, extraOpens);
		// copy and check uses
		extraUses = new HashSet<>(extraUses);
		if (extraUses.contains(null))
			throw new NullPointerException("'extraUses' contains null");
		// copy and check provides
		Map<Class<?>, List<Class<?>>> tmpProvides = new HashMap<>();
		for (Map.Entry<Class<?>, List<Class<?>>> e : extraProvides.entrySet()) {
			Class<?> service = e.getKey();
			if (service == null)
				throw new NullPointerException("'extraProvides' contains null");
			List<Class<?>> providers = new ArrayList<>(e.getValue());
			if (providers.isEmpty())
				throw new IllegalArgumentException("list of providers is empty");
			providers.forEach(p -> {
				if (p.getModule() != module)
					throw new IllegalArgumentException(p + " not in " + module);
				if (!service.isAssignableFrom(p))
					throw new IllegalArgumentException(p + " is not a " + service);
			});
			tmpProvides.put(service, providers);
		}
		extraProvides = tmpProvides;
		// update reads
		extraReads.forEach(m -> {
			try {
				PLZBase.LOOKUP.findStatic(Class.forName("jdk.internal.module.Modules"), "addReads", MethodType.methodType(void.class, Module.class, Module.class)).invoke(module, m);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
			// Modules.addReads(module, m);
		});
		// update exports
		for (Map.Entry<String, Set<Module>> e : extraExports.entrySet()) {
			String pkg = e.getKey();
			Set<Module> targets = e.getValue();
			targets.forEach(m -> {
				try {
					PLZBase.LOOKUP.findStatic(Class.forName("jdk.internal.module.Modules"), "addExports", MethodType.methodType(void.class, Module.class, String.class, Module.class)).invoke(module, pkg, m);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				// Modules.addExports(module, pkg, m);
			});
		}
		// update opens
		for (Map.Entry<String, Set<Module>> e : extraOpens.entrySet()) {
			String pkg = e.getKey();
			Set<Module> targets = e.getValue();
			targets.forEach(m -> {
				try {
					PLZBase.LOOKUP.findStatic(Class.forName("jdk.internal.module.Modules"), "addOpens", MethodType.methodType(void.class, Module.class, String.class, Module.class)).invoke(module, pkg, m);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				// Modules.addOpens(module, pkg, m);
			});
		}
		// update uses
		extraUses.forEach(service -> {
			try {
				PLZBase.LOOKUP.findStatic(Class.forName("jdk.internal.module.Modules"), "addUses", MethodType.methodType(void.class, Module.class, Class.class)).invoke(module, service);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
			// Modules.addUses(module, service);
		});
		// update provides
		for (Map.Entry<Class<?>, List<Class<?>>> e : extraProvides.entrySet()) {
			Class<?> service = e.getKey();
			List<Class<?>> providers = e.getValue();
			providers.forEach(p -> {
				try {
					PLZBase.LOOKUP.findStatic(Class.forName("jdk.internal.module.Modules"), "addProvides", MethodType.methodType(void.class, Module.class, Class.class, Class.class)).invoke(module, service);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				// Modules.addProvides(module, service, p);
			});
		}
	}

	private Map<String, Set<Module>> cloneAndCheckMap(Module module, Map<String, Set<Module>> map) {
		if (map.isEmpty())
			return Collections.emptyMap();
		Map<String, Set<Module>> result = new HashMap<>();
		Set<String> packages = module.getPackages();
		for (Map.Entry<String, Set<Module>> e : map.entrySet()) {
			String pkg = e.getKey();
			if (pkg == null)
				throw new NullPointerException("package cannot be null");
			if (!packages.contains(pkg))
				throw new IllegalArgumentException(pkg + " not in module");
			Set<Module> targets = new HashSet<>(e.getValue());
			if (targets.isEmpty())
				throw new IllegalArgumentException("set of targets is empty");
			if (targets.contains(null))
				throw new NullPointerException("set of targets cannot include null");
			result.put(pkg, targets);
		}
		return result;
	}

	@Override
	public boolean isModifiableModule(Module module) {
		return true;
	}

	protected static native void accessReload0();

	protected static native Class<?>[] getAllLoadedClasses0();

	protected static native int enableClassLoadHook0();

	protected static native int redefineClass0(Class<?> klass, byte[] bytes);

	protected static native int retransformClass0(Class<?> klass);

	protected static native void appendToClassLoaderSearch0(String jarfile, boolean bootLoader);

	protected static native long getObjectSize0(Object objectToSize);

	public static native String getJVMObjState0();

	public static native Object[] getClassInstances0(Class<?> cls);
}
