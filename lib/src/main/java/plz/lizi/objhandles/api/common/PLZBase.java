package plz.lizi.objhandles.api.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sun.misc.Unsafe;

public class PLZBase {
	public static final Unsafe UNSAFE = getUnsafe();
	public static final MethodHandles.Lookup LOOKUP = getLookup();
	public static final MethodHandle ClassLoader_defineClass0;
	public static final MethodHandle ClassLoader_defineClass1;
	public static final Map<String, Class<?>> HIDDEN_CLASSES_MAP = new HashMap<>();
	@SuppressWarnings("resource")
	public static final Scanner scanner = new Scanner(System.in);
	static {
		try {
			ClassLoader_defineClass0 = LOOKUP.findStatic(ClassLoader.class, "defineClass0", MethodType.methodType(Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class));
			ClassLoader_defineClass1 = LOOKUP.findStatic(ClassLoader.class, "defineClass1", MethodType.methodType(Class.class, ClassLoader.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, String.class));
		} catch (Throwable e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Unsafe getUnsafe() {
		if (UNSAFE != null)
			return UNSAFE;
		Unsafe instance = null;
		try {
			Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor();
			c.setAccessible(true);
			instance = c.newInstance();
		} catch (Throwable var3) {
			var3.printStackTrace();
		}
		return instance;
	}

	public static MethodHandles.Lookup getLookup() {
		try {
			return (MethodHandles.Lookup) UNSAFE.getObjectVolatile(UNSAFE.staticFieldBase(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP")), UNSAFE.staticFieldOffset(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP")));
		} catch (Exception e) {
			try {
				Constructor<MethodHandles.Lookup> c = MethodHandles.Lookup.class.getDeclaredConstructor();
				c.setAccessible(true);
				return c.newInstance();
			} catch (Throwable var3) {
				var3.printStackTrace();
			}
		}
		return null;
	}

	public static void klassPtr(Object o, Class<?> clazz) {
		if (o == null || clazz == null)
			return;
		if (o.getClass().equals(clazz) || clazz.isInstance(o))
			return;
			
		UnsafeBase.ensureClassInitialized(clazz);
		try {
			int klass_ptr = UnsafeBase.getIntVolatile(UnsafeBase.allocateInstance(clazz), UnsafeBase.addressSize());
			UnsafeBase.putIntVolatile(o, UnsafeBase.addressSize(), klass_ptr);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static void extractJar(String jarPath, String destDir) throws IOException {
		File destDirectory = new File(destDir);
		if (!destDirectory.exists()) {
			destDirectory.mkdirs();
		}
		try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarPath))) {
			JarEntry entry;
			while ((entry = jarInputStream.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				File entryFile = new File(destDir, entryName);
				if (entry.isDirectory()) {
					entryFile.mkdirs();
					continue;
				}
				File parent = entryFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				try (OutputStream outputStream = new FileOutputStream(entryFile)) {
					byte[] buffer = readAllBytes(jarInputStream);
					outputStream.write(buffer);
				}
			}
		}
	}

	public static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, bytesRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

	public static List<String> matchAllReplace(String s, String from, String to) {
		List<String> r = new ArrayList<>();
		if (from.isEmpty())
			return Collections.singletonList(s);
		int i = s.indexOf(from);
		if (i == -1)
			return Collections.singletonList(s);
		for (String t : matchAllReplace(s.substring(i + from.length()), from, to)) {
			r.add(s.substring(0, i) + from + t);
			r.add(s.substring(0, i) + to + t);
		}
		return r;
	}

	public static List<String> matchAllReplace2(String s, String from, String to) {
		return from.isEmpty() ? Collections.singletonList(s) : s.contains(from) ? Stream.concat(matchAllReplace(s.substring(s.indexOf(from) + from.length()), from, to).stream().map(t -> s.substring(0, s.indexOf(from)) + from + t), matchAllReplace(s.substring(s.indexOf(from) + from.length()), from, to).stream().map(t -> s.substring(0, s.indexOf(from)) + to + t)).collect(Collectors.toList()) : Collections.singletonList(s);
	}

	public static byte[] getClassBytes(String jarPath, String className) throws Exception {
		try (JarFile jarFile = new JarFile(jarPath)) {
			String classPath = className.replace('.', '/') + ".class";
			JarEntry entry = jarFile.getJarEntry(classPath);
			if (entry == null) {
				jarFile.close();
				throw new ClassNotFoundException("Class not found in JAR: " + className);
			}
			try (InputStream is = jarFile.getInputStream(entry)) {
				return readAllBytes(is);
			}
		}
	}

	public static byte[] getClassBytes(Class<?> clazz, boolean fromSource) throws Exception {
		if (fromSource) {
			return getClassBytes(getJarPath(clazz), clazz.getName());
		} else {
			return readAllBytes(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
		}
	}

	public static String getJarPath() {
		try {
			return PLZBase.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().split("#")[0];
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getJarPath(Class<?> cls) {
		try {
			return cls.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().split("#")[0];
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List<String> getClassNamesFromJar(String jarPath) {
		List<String> classNames = new ArrayList<>();
		for (String path : matchAllReplace(jarPath, "_", "!")) {
			try (JarFile jarFile = new JarFile(path)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					String entryName = entries.nextElement().getName();
					if (entryName.endsWith(".class")) {
						classNames.add(entryName.replace('/', '.').substring(0, entryName.length() - 6));
					}
				}
				jarFile.close();
				break;
			} catch (Throwable t) {}
		}
		return classNames;
	}

	public static Map<Object, Object> getManifestAttributes(String jarPath) {
		try (JarFile jarFile = new JarFile(jarPath)) {
			Manifest manifest = jarFile.getManifest();
			return (manifest != null) ? manifest.getMainAttributes() : null;
		} catch (IOException e) {
			return new HashMap<>();
		}
	}

	public static List<String> filesInFolder(String folderPath, String extension) {
		List<String> filePaths = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return filePaths;
		}
		String suffix = extension.startsWith(".") ? extension : "." + extension;
		suffix = suffix.toLowerCase();
		File[] files = folder.listFiles();
		if (files == null) {
			return filePaths;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				filePaths.addAll(filesInFolder(file.getAbsolutePath(), extension));
			} else {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(suffix)) {
					filePaths.add(file.getAbsolutePath());
				}
			}
		}
		return filePaths;
	}

	public static String readFile(String filePath) throws Exception {
		Path path = Paths.get(filePath);
		File file = path.toFile();
		if (!file.exists()) {
			throw new RuntimeException("null");
		}
		byte[] encoded = Files.readAllBytes(path);
		return new String(encoded, StandardCharsets.UTF_8);
	}

	public static void writeFile(String filePath, String content) throws Throwable {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(content);
		}
	}

	public static String getStackTrace() {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			builder.append(stackTrace);
			builder.append("\n");
		}
		return builder.toString();
	}

	public static void openAccess(Module targetModule, Class<?> myClass) {
		try {
			if (targetModule.canRead(myClass.getModule())) {
				return;
			}
			LOOKUP.unreflect(Module.class.getDeclaredMethod("implAddReads", Module.class)).bindTo(targetModule).invoke(myClass.getModule());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void println(Object o) {
		System.out.println(o);
	}

	public static void println() {
		System.out.println();
	}

	public static void print(Object o) {
		System.out.print(o);
	}

	public static String input(String s) {
		System.out.print(s);
		return scanner.nextLine();
	}

	public static void cls() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
		}
	}

	public static String[] splitFirst(String input, String target) {
		int firstDotIndex = input.indexOf(target);
		if (firstDotIndex == -1) {
			return new String[] { input, "" };
		}
		return new String[] { input.substring(0, firstDotIndex), input.substring(firstDotIndex + 1) };
	}

	public static String[] splitLast(String input, String target) {
		int lastIndex = input.lastIndexOf(target);
		if (lastIndex == -1) {
			return new String[] { input, "" };
		}
		return new String[] { input.substring(0, lastIndex), input.substring(lastIndex + 1) };
	}

	public static List<String> dumpCmdline(String cmdline) {
		List<String> argv = new ArrayList<>();
		StringBuilder arg = new StringBuilder();
		boolean inQuotes = false;
		char quoteChar = 0;
		for (char c : cmdline.toCharArray()) {
			if (c == '"' || c == '\'') {
				if (!inQuotes) {
					inQuotes = true;
					quoteChar = c;
				} else if (quoteChar == c)
					inQuotes = false;
				else
					arg.append(c);
			} else if (Character.isWhitespace(c) && !inQuotes) {
				if (arg.length() > 0) {
					argv.add(arg.toString());
					arg.setLength(0);
				}
			} else
				arg.append(c);
		}
		if (arg.length() > 0)
			argv.add(arg.toString());
		return argv;
	}

	public static void selectCopyFile(String sourceDir, String targetDirForSpecified, String targetDirForOthers, List<String> specifiedSuffixes) throws IOException {
		Files.createDirectories(Paths.get(targetDirForSpecified));
		Files.createDirectories(Paths.get(targetDirForOthers));
		Files.walk(Paths.get(sourceDir)).filter(Files::isRegularFile).forEach(sourcePath -> {
			try {
				String fileName = sourcePath.getFileName().toString();
				int dotIndex = fileName.lastIndexOf('.');
				String suffix = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
				String targetDir = specifiedSuffixes.contains(suffix.toLowerCase()) ? targetDirForSpecified : targetDirForOthers;
				Path relativePath = Paths.get(sourceDir).relativize(sourcePath);
				Path targetPath = Paths.get(targetDir, relativePath.toString());
				Files.createDirectories(targetPath.getParent());
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static Map<Object, Object> getMainManifestAttributes(String jarPath) {
		try (JarFile jarFile = new JarFile(jarPath)) {
			Manifest manifest = jarFile.getManifest();
			return (manifest != null) ? manifest.getMainAttributes() : null;
		} catch (IOException e) {
			return new HashMap<>();
		}
	}

	public static List<String> getFilePaths(String folderPath, String extension) {
		List<String> filePaths = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return filePaths;
		}
		String suffix = extension.startsWith(".") ? extension : "." + extension;
		suffix = suffix.toLowerCase();
		File[] files = folder.listFiles();
		if (files == null) {
			return filePaths;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				filePaths.addAll(getFilePaths(file.getAbsolutePath(), extension));
			} else {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(suffix)) {
					filePaths.add(file.getAbsolutePath());
				}
			}
		}
		return filePaths;
	}

	public static void copyFields(Object old, Object next) {
		Map<String, Object> oldFieldMap = new HashMap<>();
		for (Field field : old.getClass().getDeclaredFields()) {
			try {
				if (!Modifier.isStatic(field.getModifiers())) {
					oldFieldMap.put(field.getName(), LOOKUP.unreflectGetter(field).invoke(old));
				}
			} catch (Throwable e) {
			}
		}
		for (Field field : next.getClass().getDeclaredFields()) {
			if (oldFieldMap.containsKey(field.getName())) {
				Object obj = oldFieldMap.get(field.getName());
				try {
					LOOKUP.unreflectSetter(field).invoke(next, obj);
				} catch (Throwable e) {
				}
			}
		}
	}

	public static String toVMName(String type) {
		if (type == null || type.isEmpty()) {
			return type;
		}
		switch (type) {
			case "int":
				return "I";
			case "float":
				return "F";
			case "long":
				return "J";
			case "double":
				return "D";
			case "boolean":
				return "Z";
			case "byte":
				return "B";
			case "char":
				return "C";
			case "short":
				return "S";
			case "void":
				return "V";
		}
		if (type.endsWith("[]")) {
			int dimensions = 0;
			while (type.endsWith("[]")) {
				dimensions++;
				type = type.substring(0, type.length() - 2);
			}
			String baseType = toVMName(type);
			return "[".repeat(dimensions) + baseType;
		}
		if (type.contains(".")) {
			return "L" + type.replace('.', '/') + ";";
		}
		return type;
	}

	public static String fromVMName(String vmType) {
		if (vmType == null || vmType.isEmpty()) {
			return vmType;
		}
		switch (vmType) {
			case "I":
				return "int";
			case "F":
				return "float";
			case "J":
				return "long";
			case "D":
				return "double";
			case "Z":
				return "boolean";
			case "B":
				return "byte";
			case "C":
				return "char";
			case "S":
				return "short";
			case "V":
				return "void";
		}
		if (vmType.startsWith("[")) {
			int arrayDepth = 0;
			while (arrayDepth < vmType.length() && vmType.charAt(arrayDepth) == '[') {
				arrayDepth++;
			}
			String elementType = fromVMName(vmType.substring(arrayDepth));
			return elementType + "[]".repeat(arrayDepth);
		}
		if (vmType.startsWith("L") && vmType.endsWith(";")) {
			String className = vmType.substring(1, vmType.length() - 1);
			return className.replace('/', '.');
		}
		return vmType;
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(T original) throws Throwable {
		if (original == null) {
			return null;
		}
		if (original.getClass().isArray()) {
			int length = java.lang.reflect.Array.getLength(original);
			Object newArray = java.lang.reflect.Array.newInstance(original.getClass().getComponentType(), length);
			System.arraycopy(original, 0, newArray, 0, length);
			return (T) newArray;
		}
		T copy = (T) UNSAFE.allocateInstance(original.getClass());
		PLZBase.copyFields(original, copy);
		return copy;
	}

	public static String realClassName(Class<?> klass) {
		return klass.getName().split("\\$\\$")[0].trim();
	}

	public static String realClassName(String name) {
		return name.split("\\$\\$")[0].split("\\$")[0].trim();
	}

	public static String lowCaseFlowString(String str, int size, double speed) {
		StringBuilder builder = new StringBuilder(str);
		long time = System.currentTimeMillis();
		for (int g = 0; g < size; g++) {
			int i = (int) (((long) time / (speed * 1000) + g) % builder.length());
			builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));
		}
		return builder.toString();
	}

	public static String waveString(String text, int waveHeight, double speed, double distance) {
		char[] chars = text.toCharArray();
		int length = chars.length;
		int totalRows = waveHeight + 2;
		char[][] matrix = new char[totalRows][length];
		for (char[] row : matrix) {
			Arrays.fill(row, ' ');
		}
		double time = System.currentTimeMillis() * -0.001 * speed;
		for (int i = 0; i < length; i++) {
			double wave = Math.sin(time + i * distance) * (waveHeight / 2 + 2);
			int row = (int) (waveHeight / 2 - wave + 1);
			row = Math.max(0, Math.min(row, totalRows - 1));
			matrix[row][i] = chars[i];
		}
		StringBuilder sb = new StringBuilder();
		for (char[] row : matrix) {
			sb.append(row).append("\n");
		}
		return sb.toString();
	}

	public static void unJar(String jarPath, String destDirPath) throws Throwable {
		File jarFile = new File(jarPath);
		if (!jarFile.exists()) {
			return;
		}
		File destDir = new File(destDirPath);
		if (!destDir.exists() && !destDir.mkdirs()) {
			return;
		}
		String targetCanonicalPath = destDir.getCanonicalPath();
		try (JarFile jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				File destFile = new File(destDir, entryName);
				String destCanonicalPath = destFile.getCanonicalPath();
				if (!destCanonicalPath.startsWith(targetCanonicalPath + File.separator)) {
					return;
				}
				if (entry.isDirectory()) {
					if (!destFile.exists() && !destFile.mkdirs()) {
						return;
					}
				} else {
					File parent = destFile.getParentFile();
					if (parent != null && !parent.exists() && !parent.mkdirs()) {
						return;
					}
					try (InputStream is = jar.getInputStream(entry); OutputStream os = new BufferedOutputStream(new FileOutputStream(destFile))) {
						byte[] buffer = new byte[4096];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
					}
				}
			}
		}
	}

	public static boolean wasLoaded(String name, ClassLoader loader) {
		try {
			for (Class<?> c : new ArrayList<>((ArrayList<Class<?>>) PLZBase.LOOKUP.findGetter(ClassLoader.class, "classes", ArrayList.class).invoke(loader))) {
				if (c.getName().equals(name)) {
					return true;
				}
			}
		} catch (Throwable e) {
		}
		return false;
	}

	public static Class<?> defineClass(ClassLoader loader, String name, String newName, byte[] buf) {
		try {
			Class<?> klass = (Class<?>) ClassLoader_defineClass1.invoke(loader, newName != null ? newName : name, buf, 0, buf.length, null, null);
			if (newName != null) {
				LOOKUP.findVarHandle(Class.class, "name", String.class).set(klass, newName);
			}
			return klass;
		} catch (Throwable e1) {
			try {
				return Class.forName(name);
			} catch (Exception e) {
				e1.addSuppressed(e);
				throw new RuntimeException(e1);
			}
		}
	}

	public static Class<?> defineClassInPackage(ClassLoader loader, Class<?> lookup, String name, String newName) {
		try {
			return defineClass(loader, name, newName, getClassBytes(getJarPath(lookup), name));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> defineHiddenClass(ClassLoader loader, Class<?> lookup, String name, String newName, byte[] buf, ClassOption... options) {
		if (HIDDEN_CLASSES_MAP.containsKey(name)) {
			return HIDDEN_CLASSES_MAP.get(name);
		}
		int flags = 2 | ClassOption.optionsToFlag(Set.of(options));
		if (loader == null || loader == ClassLoader.getPlatformClassLoader()) {
			flags |= 8;
		}
		try {
			Class<?> klass = (Class<?>) ClassLoader_defineClass0.invoke(loader, lookup, newName != null ? newName : name, buf, 0, buf.length, null, true, flags, null);
			if (newName != null) {
				LOOKUP.findVarHandle(Class.class, "name", String.class).set(klass, newName);
			}
			HIDDEN_CLASSES_MAP.put(name, klass);
			return klass;
		} catch (Throwable e1) {
			throw new RuntimeException(e1);
		}
	}

	public static Class<?> defineHiddenClassInPackage(ClassLoader loader, Class<?> lookup, String name, String newName, ClassOption... options) {
		try {
			return defineHiddenClass(loader, lookup, name, newName != null ? newName : name, getClassBytes(getJarPath(lookup), name), options);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invoke(Object target, String name, Object... args) {
		Class<?> klass = (target instanceof Class) ? (Class<?>) target : target.getClass();
		Class<?>[] argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		try {
			Method method = klass.getDeclaredMethod(name, argTypes);
			MethodHandle methodHandle;
			methodHandle = LOOKUP.unreflect(method);
			if (target instanceof Class) {
				return methodHandle.invokeWithArguments(args);
			} else {
				Object[] combinedArgs = new Object[args.length + 1];
				combinedArgs[0] = target;
				System.arraycopy(args, 0, combinedArgs, 1, args.length);
				return methodHandle.invokeWithArguments(combinedArgs);
			}
		} catch (NoSuchMethodException e) {
			Method[] methods = klass.getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().equals(name) && m.getParameterCount() == args.length) {
					boolean compatible = true;
					Class<?>[] parameterTypes = m.getParameterTypes();
					for (int i = 0; i < args.length; i++) {
						if (!parameterTypes[i].isAssignableFrom(argTypes[i]) && !(parameterTypes[i].isPrimitive() && isWrapper(argTypes[i], parameterTypes[i]))) {
							compatible = false;
							break;
						}
					}
					if (compatible) {
						try {
							MethodHandle methodHandle = LOOKUP.unreflect(m);
							if (target instanceof Class) {
								return methodHandle.invokeWithArguments(args);
							} else {
								Object[] combinedArgs = new Object[args.length + 1];
								combinedArgs[0] = target;
								System.arraycopy(args, 0, combinedArgs, 1, args.length);
								return methodHandle.invokeWithArguments(combinedArgs);
							}
						} catch (Throwable ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			}
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isWrapper(Class<?> wrapper, Class<?> primitive) {
		try {
			if (primitive == int.class && wrapper == Integer.class)
				return true;
			if (primitive == long.class && wrapper == Long.class)
				return true;
			if (primitive == double.class && wrapper == Double.class)
				return true;
			if (primitive == float.class && wrapper == Float.class)
				return true;
			if (primitive == boolean.class && wrapper == Boolean.class)
				return true;
			if (primitive == byte.class && wrapper == Byte.class)
				return true;
			if (primitive == short.class && wrapper == Short.class)
				return true;
			if (primitive == char.class && wrapper == Character.class)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static byte[] readFileBytes(String fileName) throws IOException {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(fileName));
			out = new ByteArrayOutputStream();
			byte[] tempbytes = new byte[in.available()];
			for (int i = 0; (i = in.read(tempbytes)) != -1;) {
				out.write(tempbytes, 0, i);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return out.toByteArray();
	}

	public static void writeFileBytes(String fileName, byte[] bytes, boolean append) throws IOException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(fileName, append));
			out.write(bytes);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static void writeFileBytes(String fileName, byte[] bytes) throws IOException {
		writeFileBytes(fileName, bytes, false);
	}
}
