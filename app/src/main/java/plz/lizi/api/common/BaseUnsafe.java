package plz.lizi.api.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.lang.reflect.Constructor;
import sun.misc.Unsafe;

public final class BaseUnsafe {
	private static final Object jdkUnsafeInstance;

	private static final MethodHandle getInt;

	private static final MethodHandle putInt;

	private static final MethodHandle getReference;

	private static final MethodHandle putReference;

	private static final MethodHandle getBoolean;

	private static final MethodHandle putBoolean;

	private static final MethodHandle getByte;

	private static final MethodHandle putByte;

	private static final MethodHandle getShort;

	private static final MethodHandle putShort;

	private static final MethodHandle getChar;

	private static final MethodHandle putChar;

	private static final MethodHandle getLong;

	private static final MethodHandle putLong;

	private static final MethodHandle getFloat;

	private static final MethodHandle putFloat;

	private static final MethodHandle getDouble;

	private static final MethodHandle putDouble;

	private static final MethodHandle getUncompressedObject;

	private static final MethodHandle writeback0;

	private static final MethodHandle writebackPreSync0;

	private static final MethodHandle writebackPostSync0;

	private static final MethodHandle defineClass0;

	private static final MethodHandle allocateInstance;

	private static final MethodHandle throwException;

	private static final MethodHandle compareAndSetReference;

	private static final MethodHandle compareAndExchangeReference;

	private static final MethodHandle compareAndSetInt;

	private static final MethodHandle compareAndExchangeInt;

	private static final MethodHandle compareAndSetLong;

	private static final MethodHandle compareAndExchangeLong;

	private static final MethodHandle getReferenceVolatile;

	private static final MethodHandle putReferenceVolatile;

	private static final MethodHandle getIntVolatile;

	private static final MethodHandle putIntVolatile;

	private static final MethodHandle getBooleanVolatile;

	private static final MethodHandle putBooleanVolatile;

	private static final MethodHandle getByteVolatile;

	private static final MethodHandle putByteVolatile;

	private static final MethodHandle getShortVolatile;

	private static final MethodHandle putShortVolatile;

	private static final MethodHandle getCharVolatile;

	private static final MethodHandle putCharVolatile;

	private static final MethodHandle getLongVolatile;

	private static final MethodHandle putLongVolatile;

	private static final MethodHandle getFloatVolatile;

	private static final MethodHandle putFloatVolatile;

	private static final MethodHandle getDoubleVolatile;

	private static final MethodHandle putDoubleVolatile;

	private static final MethodHandle unpark;

	private static final MethodHandle park;

	private static final MethodHandle loadFence;

	private static final MethodHandle storeFence;

	private static final MethodHandle fullFence;

	private static final MethodHandle allocateMemory0;

	private static final MethodHandle reallocateMemory0;

	private static final MethodHandle freeMemory0;

	private static final MethodHandle setMemory0;

	private static final MethodHandle copyMemory0;

	private static final MethodHandle copySwapMemory0;

	private static final MethodHandle objectFieldOffset0;

	private static final MethodHandle objectFieldOffset1;

	private static final MethodHandle staticFieldOffset0;

	private static final MethodHandle staticFieldBase0;

	private static final MethodHandle shouldBeInitialized0;

	private static final MethodHandle ensureClassInitialized0;

	private static final MethodHandle arrayBaseOffset0;

	private static final MethodHandle arrayIndexScale0;

	private static final MethodHandle getLoadAverage0;

	private static final int ADDRESS_SIZE;

	private static final int PAGE_SIZE;

	private static final boolean BIG_ENDIAN;

	private static final boolean UNALIGNED_ACCESS;

	private static final int DATA_CACHE_LINE_FLUSH_SIZE;

	private static final Class<?> directBuffer;

	public static final Unsafe UNSAFE = getUnsafe();

	public static final MethodHandles.Lookup LOOKUP = getLookup();

	public static int getInt(Object o, long offset) {
		return ((Integer) invokeSafe(() -> getInt.invoke(o, offset))).intValue();
	}

	public static void putInt(Object o, long offset, int x) {
		invokeSafe(() -> putInt.invoke(o, offset, x));
	}

	public static Object getReference(Object o, long offset) {
		return invokeSafe(() -> getReference.invoke(o, offset));
	}

	public static void putReference(Object o, long offset, Object x) {
		invokeSafe(() -> putReference.invoke(o, offset, x));
	}

	public static boolean getBoolean(Object o, long offset) {
		return ((Boolean) invokeSafe(() -> getBoolean.invoke(o, offset))).booleanValue();
	}

	public static void putBoolean(Object o, long offset, boolean x) {
		invokeSafe(() -> putBoolean.invoke(o, offset, x));
	}

	public static byte getByte(Object o, long offset) {
		return ((Byte) invokeSafe(() -> getByte.invoke(o, offset))).byteValue();
	}

	public static void putByte(Object o, long offset, byte x) {
		invokeSafe(() -> putByte.invoke(o, offset, x));
	}

	public static short getShort(Object o, long offset) {
		return ((Short) invokeSafe(() -> getShort.invoke(o, offset))).shortValue();
	}

	public static void putShort(Object o, long offset, short x) {
		invokeSafe(() -> putShort.invoke(o, offset, x));
	}

	public static char getChar(Object o, long offset) {
		return ((Character) invokeSafe(() -> getChar.invoke(o, offset))).charValue();
	}

	public static void putChar(Object o, long offset, char x) {
		invokeSafe(() -> putChar.invoke(o, offset, x));
	}

	public static long getLong(Object o, long offset) {
		return ((Long) invokeSafe(() -> getLong.invoke(o, offset))).longValue();
	}

	public static void putLong(Object o, long offset, long x) {
		invokeSafe(() -> putLong.invoke(o, offset, x));
	}

	public static float getFloat(Object o, long offset) {
		return ((Float) invokeSafe(() -> getFloat.invoke(o, offset))).floatValue();
	}

	public static void putFloat(Object o, long offset, float x) {
		invokeSafe(() -> putFloat.invoke(o, offset, x));
	}

	public static double getDouble(Object o, long offset) {
		return ((Double) invokeSafe(() -> getDouble.invoke(o, offset))).doubleValue();
	}

	public static void putDouble(Object o, long offset, double x) {
		invokeSafe(() -> putDouble.invoke(o, offset, x));
	}

	public static long getAddress(Object o, long offset) {
		return (ADDRESS_SIZE == 4) ? Integer.toUnsignedLong(getInt(o, offset)) : getLong(o, offset);
	}

	public static void putAddress(Object o, long offset, long x) {
		if (ADDRESS_SIZE == 4) {
			putInt(o, offset, (int) x);
		} else {
			putLong(o, offset, x);
		}
	}

	public static Object getUncompressedObject(long address) {
		return invokeSafe(() -> getUncompressedObject.invoke(address));
	}

	public static byte getByte(long address) {
		return getByte(null, address);
	}

	public static void putByte(long address, byte x) {
		putByte(null, address, x);
	}

	public static short getShort(long address) {
		return getShort(null, address);
	}

	public static void putShort(long address, short x) {
		putShort(null, address, x);
	}

	public static char getChar(long address) {
		return getChar(null, address);
	}

	public static void putChar(long address, char x) {
		putChar(null, address, x);
	}

	public static int getInt(long address) {
		return getInt(null, address);
	}

	public static void putInt(long address, int x) {
		putInt(null, address, x);
	}

	public static long getLong(long address) {
		return getLong(null, address);
	}

	public static void putLong(long address, long x) {
		putLong(null, address, x);
	}

	public static float getFloat(long address) {
		return getFloat(null, address);
	}

	public static void putFloat(long address, float x) {
		putFloat(null, address, x);
	}

	public static double getDouble(long address) {
		return getDouble(null, address);
	}

	public static void putDouble(long address, double x) {
		putDouble(null, address, x);
	}

	public static long getAddress(long address) {
		return getAddress(null, address);
	}

	public static void putAddress(long address, long x) {
		putAddress(null, address, x);
	}

	private static RuntimeException invalidInput() {
		return new IllegalArgumentException();
	}

	private static boolean is32BitClean(long value) {
		return (value >>> 32L == 0L);
	}

	private static void checkSize(long size) {
		if (ADDRESS_SIZE == 4) {
			if (!is32BitClean(size))
				throw invalidInput();
		} else if (size < 0L) {
			throw invalidInput();
		}
	}

	private static void checkNativeAddress(long address) {
		if (ADDRESS_SIZE == 4 && ((address >> 32L) + 1L & 0xFFFFFFFFFFFFFFFEL) != 0L)
			throw invalidInput();
	}

	private static void checkOffset(Object o, long offset) {
		if (ADDRESS_SIZE == 4) {
			if (!is32BitClean(offset))
				throw invalidInput();
		} else if (offset < 0L) {
			throw invalidInput();
		}
	}

	private static void checkPointer(Object o, long offset) {
		if (o == null) {
			checkNativeAddress(offset);
		} else {
			checkOffset(o, offset);
		}
	}

	private static void checkPrimitiveArray(Class<?> c) {
		Class<?> componentType = c.getComponentType();
		if (componentType == null || !componentType.isPrimitive())
			throw invalidInput();
	}

	private static void checkPrimitivePointer(Object o, long offset) {
		checkPointer(o, offset);
		if (o != null)
			checkPrimitiveArray(o.getClass());
	}

	private static long alignToHeapWordSize(long bytes) {
		if (bytes >= 0L)
			return bytes + ADDRESS_SIZE - 1L & -ADDRESS_SIZE;
		throw invalidInput();
	}

	public static long allocateMemory(long bytes) {
		bytes = alignToHeapWordSize(bytes);
		allocateMemoryChecks(bytes);
		if (bytes == 0L)
			return 0L;
		long p = allocateMemory0(bytes);
		if (p == 0L)
			throw new OutOfMemoryError("Unable to allocate " + bytes + " bytes");
		return p;
	}

	private static void allocateMemoryChecks(long bytes) {
		checkSize(bytes);
	}

	public static long reallocateMemory(long address, long bytes) {
		bytes = alignToHeapWordSize(bytes);
		reallocateMemoryChecks(address, bytes);
		if (bytes == 0L) {
			freeMemory(address);
			return 0L;
		}
		long p = (address == 0L) ? allocateMemory0(bytes) : reallocateMemory0(address, bytes);
		if (p == 0L)
			throw new OutOfMemoryError("Unable to allocate " + bytes + " bytes");
		return p;
	}

	private static void reallocateMemoryChecks(long address, long bytes) {
		checkPointer(null, address);
		checkSize(bytes);
	}

	public static void setMemory(Object o, long offset, long bytes, byte value) {
		setMemoryChecks(o, offset, bytes, value);
		if (bytes != 0L)
			setMemory0(o, offset, bytes, value);
	}

	public static void setMemory(long address, long bytes, byte value) {
		setMemory(null, address, bytes, value);
	}

	private static void setMemoryChecks(Object o, long offset, long bytes, byte value) {
		checkPrimitivePointer(o, offset);
		checkSize(bytes);
	}

	public static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
		copyMemoryChecks(srcBase, srcOffset, destBase, destOffset, bytes);
		if (bytes != 0L)
			copyMemory0(srcBase, srcOffset, destBase, destOffset, bytes);
	}

	public static void copyMemory(long srcAddress, long destAddress, long bytes) {
		copyMemory(null, srcAddress, null, destAddress, bytes);
	}

	private static void copyMemoryChecks(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
		checkSize(bytes);
		checkPrimitivePointer(srcBase, srcOffset);
		checkPrimitivePointer(destBase, destOffset);
	}

	public static void copySwapMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes, long elemSize) {
		copySwapMemoryChecks(srcBase, srcOffset, destBase, destOffset, bytes, elemSize);
		if (bytes != 0L)
			copySwapMemory0(srcBase, srcOffset, destBase, destOffset, bytes, elemSize);
	}

	private static void copySwapMemoryChecks(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes, long elemSize) {
		checkSize(bytes);
		if (elemSize != 2L && elemSize != 4L && elemSize != 8L)
			throw invalidInput();
		if (bytes % elemSize != 0L)
			throw invalidInput();
		checkPrimitivePointer(srcBase, srcOffset);
		checkPrimitivePointer(destBase, destOffset);
	}

	public static void copySwapMemory(long srcAddress, long destAddress, long bytes, long elemSize) {
		copySwapMemory(null, srcAddress, null, destAddress, bytes, elemSize);
	}

	public static void freeMemory(long address) {
		freeMemoryChecks(address);
		if (address != 0L)
			freeMemory0(address);
	}

	private static void freeMemoryChecks(long address) {
		checkPointer(null, address);
	}

	public static void writebackMemory(long address, long length) {
		checkWritebackEnabled();
		checkWritebackMemory(address, length);
		writebackPreSync0();
		long line = dataCacheLineAlignDown(address);
		for (long end = address + length; line < end; line += dataCacheLineFlushSize())
			writeback0(line);
		writebackPostSync0();
	}

	private static void checkWritebackMemory(long address, long length) {
		checkNativeAddress(address);
		checkSize(length);
	}

	private static void checkWritebackEnabled() {
		if (!isWritebackEnabled())
			throw new RuntimeException("writebackMemory not enabled!");
	}

	private static void writeback0(long address) {
		invokeSafe(() -> writeback0.invoke(address));
	}

	private static void writebackPreSync0() {
		MethodHandle var10000 = writebackPreSync0;
		Objects.requireNonNull(var10000);
		Objects.requireNonNull(var10000);
		invokeSafe(var10000::invoke);
	}

	private static void writebackPostSync0() {
		MethodHandle var10000 = writebackPostSync0;
		Objects.requireNonNull(var10000);
		Objects.requireNonNull(var10000);
		invokeSafe(var10000::invoke);
	}

	public static long objectFieldOffset(Field f) {
		if (f == null)
			throw new NullPointerException();
		return objectFieldOffset0(f);
	}

	public static long objectFieldOffset(Class<?> c, String name) {
		if (c != null && name != null)
			return objectFieldOffset1(c, name);
		throw new NullPointerException();
	}

	public static long staticFieldOffset(Field f) {
		if (f == null)
			throw new NullPointerException();
		return staticFieldOffset0(f);
	}

	public static Object staticFieldBase(Field f) {
		if (f == null)
			throw new NullPointerException();
		return staticFieldBase0(f);
	}

	public static boolean shouldBeInitialized(Class<?> c) {
		if (c == null)
			throw new NullPointerException();
		return shouldBeInitialized0(c);
	}

	public static void ensureClassInitialized(Class<?> c) {
		if (c == null)
			throw new NullPointerException();
		ensureClassInitialized0(c);
	}

	public static int arrayBaseOffset(Class<?> arrayClass) {
		if (arrayClass == null)
			throw new NullPointerException();
		return arrayBaseOffset0(arrayClass);
	}

	public static int arrayIndexScale(Class<?> arrayClass) {
		if (arrayClass == null)
			throw new NullPointerException();
		return arrayIndexScale0(arrayClass);
	}

	public static int addressSize() {
		return ADDRESS_SIZE;
	}

	public static int pageSize() {
		return PAGE_SIZE;
	}

	public static int dataCacheLineFlushSize() {
		return DATA_CACHE_LINE_FLUSH_SIZE;
	}

	public static long dataCacheLineAlignDown(long address) {
		return address & -DATA_CACHE_LINE_FLUSH_SIZE;
	}

	public static boolean isWritebackEnabled() {
		return (DATA_CACHE_LINE_FLUSH_SIZE != 0);
	}

	public static Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain) {
		if (b == null)
			throw new NullPointerException();
		if (len < 0)
			throw new ArrayIndexOutOfBoundsException();
		return defineClass0(name, b, off, len, loader, protectionDomain);
	}

	public static Class<?> defineClass0(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain) {
		return (Class<?>) invokeSafe(() -> defineClass0.invoke(name, b, off, len, loader, protectionDomain));
	}

	public static Object allocateInstance(Class<?> cls) {
		return invokeSafe(() -> allocateInstance.invoke(cls));
	}

	public static Object allocateUninitializedArray(Class<?> componentType, int length) {
		if (componentType == null)
			throw new IllegalArgumentException("Component type is null");
		if (!componentType.isPrimitive())
			throw new IllegalArgumentException("Component type is not primitive");
		if (length < 0)
			throw new IllegalArgumentException("Negative length");
		return allocateUninitializedArray0(componentType, length);
	}

	private static Object allocateUninitializedArray0(Class<?> componentType, int length) {
		if (componentType == byte.class)
			return new byte[length];
		if (componentType == boolean.class)
			return new boolean[length];
		if (componentType == short.class)
			return new short[length];
		if (componentType == char.class)
			return new char[length];
		if (componentType == int.class)
			return new int[length];
		if (componentType == float.class)
			return new float[length];
		if (componentType == long.class)
			return new long[length];
		return (componentType == double.class) ? new double[length] : null;
	}

	public static void throwException(Throwable ee) {
		invokeSafe(() -> throwException.invoke(ee));
	}

	public static boolean compareAndSetReference(Object o, long offset, Object expected, Object x) {
		return ((Boolean) invokeSafe(() -> compareAndSetReference.invoke(o, offset, expected, x))).booleanValue();
	}

	public static Object compareAndExchangeReference(Object o, long offset, Object expected, Object x) {
		return invokeSafe(() -> compareAndExchangeReference.invoke(o, offset, expected, x));
	}

	public static Object compareAndExchangeReferenceAcquire(Object o, long offset, Object expected, Object x) {
		return compareAndExchangeReference(o, offset, expected, x);
	}

	public static Object compareAndExchangeReferenceRelease(Object o, long offset, Object expected, Object x) {
		return compareAndExchangeReference(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetReferencePlain(Object o, long offset, Object expected, Object x) {
		return compareAndSetReference(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetReferenceAcquire(Object o, long offset, Object expected, Object x) {
		return compareAndSetReference(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetReferenceRelease(Object o, long offset, Object expected, Object x) {
		return compareAndSetReference(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetReference(Object o, long offset, Object expected, Object x) {
		return compareAndSetReference(o, offset, expected, x);
	}

	public static boolean compareAndSetInt(Object o, long offset, int expected, int x) {
		return ((Boolean) invokeSafe(() -> compareAndSetInt.invoke(o, offset, expected, x))).booleanValue();
	}

	public static int compareAndExchangeInt(Object o, long offset, int expected, int x) {
		return ((Integer) invokeSafe(() -> compareAndExchangeInt.invoke(o, offset, expected, x))).intValue();
	}

	public static int compareAndExchangeIntAcquire(Object o, long offset, int expected, int x) {
		return compareAndExchangeInt(o, offset, expected, x);
	}

	public static int compareAndExchangeIntRelease(Object o, long offset, int expected, int x) {
		return compareAndExchangeInt(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetIntPlain(Object o, long offset, int expected, int x) {
		return compareAndSetInt(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetIntAcquire(Object o, long offset, int expected, int x) {
		return compareAndSetInt(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetIntRelease(Object o, long offset, int expected, int x) {
		return compareAndSetInt(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetInt(Object o, long offset, int expected, int x) {
		return compareAndSetInt(o, offset, expected, x);
	}

	public static byte compareAndExchangeByte(Object o, long offset, byte expected, byte x) {
		int fullWord;
		long wordOffset = offset & 0xFFFFFFFFFFFFFFFCL;
		int shift = (int) (offset & 0x3L) << 3;
		if (BIG_ENDIAN)
			shift = 24 - shift;
		int mask = 255 << shift;
		int maskedExpected = (expected & 0xFF) << shift;
		int maskedX = (x & 0xFF) << shift;
		do {
			fullWord = getIntVolatile(o, wordOffset);
			if ((fullWord & mask) != maskedExpected)
				return (byte) ((fullWord & mask) >> shift);
		} while (!weakCompareAndSetInt(o, wordOffset, fullWord, fullWord & (mask ^ 0xFFFFFFFF) | maskedX));
		return expected;
	}

	public static boolean compareAndSetByte(Object o, long offset, byte expected, byte x) {
		return (compareAndExchangeByte(o, offset, expected, x) == expected);
	}

	public static boolean weakCompareAndSetByte(Object o, long offset, byte expected, byte x) {
		return compareAndSetByte(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetByteAcquire(Object o, long offset, byte expected, byte x) {
		return weakCompareAndSetByte(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetByteRelease(Object o, long offset, byte expected, byte x) {
		return weakCompareAndSetByte(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetBytePlain(Object o, long offset, byte expected, byte x) {
		return weakCompareAndSetByte(o, offset, expected, x);
	}

	public static byte compareAndExchangeByteAcquire(Object o, long offset, byte expected, byte x) {
		return compareAndExchangeByte(o, offset, expected, x);
	}

	public static byte compareAndExchangeByteRelease(Object o, long offset, byte expected, byte x) {
		return compareAndExchangeByte(o, offset, expected, x);
	}

	public static short compareAndExchangeShort(Object o, long offset, short expected, short x) {
		int fullWord;
		if ((offset & 0x3L) == 3L)
			throw new IllegalArgumentException("Update spans the word, not supported");
		long wordOffset = offset & 0xFFFFFFFFFFFFFFFCL;
		int shift = (int) (offset & 0x3L) << 3;
		if (BIG_ENDIAN)
			shift = 16 - shift;
		int mask = 65535 << shift;
		int maskedExpected = (expected & 0xFFFF) << shift;
		int maskedX = (x & 0xFFFF) << shift;
		do {
			fullWord = getIntVolatile(o, wordOffset);
			if ((fullWord & mask) != maskedExpected)
				return (short) ((fullWord & mask) >> shift);
		} while (!weakCompareAndSetInt(o, wordOffset, fullWord, fullWord & (mask ^ 0xFFFFFFFF) | maskedX));
		return expected;
	}

	public static boolean compareAndSetShort(Object o, long offset, short expected, short x) {
		return (compareAndExchangeShort(o, offset, expected, x) == expected);
	}

	public static boolean weakCompareAndSetShort(Object o, long offset, short expected, short x) {
		return compareAndSetShort(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetShortAcquire(Object o, long offset, short expected, short x) {
		return weakCompareAndSetShort(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetShortRelease(Object o, long offset, short expected, short x) {
		return weakCompareAndSetShort(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetShortPlain(Object o, long offset, short expected, short x) {
		return weakCompareAndSetShort(o, offset, expected, x);
	}

	public static short compareAndExchangeShortAcquire(Object o, long offset, short expected, short x) {
		return compareAndExchangeShort(o, offset, expected, x);
	}

	public static short compareAndExchangeShortRelease(Object o, long offset, short expected, short x) {
		return compareAndExchangeShort(o, offset, expected, x);
	}

	private static char s2c(short s) {
		return (char) s;
	}

	private static short c2s(char s) {
		return (short) s;
	}

	public static boolean compareAndSetChar(Object o, long offset, char expected, char x) {
		return compareAndSetShort(o, offset, c2s(expected), c2s(x));
	}

	public static char compareAndExchangeChar(Object o, long offset, char expected, char x) {
		return s2c(compareAndExchangeShort(o, offset, c2s(expected), c2s(x)));
	}

	public static char compareAndExchangeCharAcquire(Object o, long offset, char expected, char x) {
		return s2c(compareAndExchangeShortAcquire(o, offset, c2s(expected), c2s(x)));
	}

	public static char compareAndExchangeCharRelease(Object o, long offset, char expected, char x) {
		return s2c(compareAndExchangeShortRelease(o, offset, c2s(expected), c2s(x)));
	}

	public static boolean weakCompareAndSetChar(Object o, long offset, char expected, char x) {
		return weakCompareAndSetShort(o, offset, c2s(expected), c2s(x));
	}

	public static boolean weakCompareAndSetCharAcquire(Object o, long offset, char expected, char x) {
		return weakCompareAndSetShortAcquire(o, offset, c2s(expected), c2s(x));
	}

	public static boolean weakCompareAndSetCharRelease(Object o, long offset, char expected, char x) {
		return weakCompareAndSetShortRelease(o, offset, c2s(expected), c2s(x));
	}

	public static boolean weakCompareAndSetCharPlain(Object o, long offset, char expected, char x) {
		return weakCompareAndSetShortPlain(o, offset, c2s(expected), c2s(x));
	}

	private static boolean byte2bool(byte b) {
		return (b != 0);
	}

	private static byte bool2byte(boolean b) {
		return (byte) (b ? 1 : 0);
	}

	public static boolean compareAndSetBoolean(Object o, long offset, boolean expected, boolean x) {
		return compareAndSetByte(o, offset, bool2byte(expected), bool2byte(x));
	}

	public static boolean compareAndExchangeBoolean(Object o, long offset, boolean expected, boolean x) {
		return byte2bool(compareAndExchangeByte(o, offset, bool2byte(expected), bool2byte(x)));
	}

	public static boolean compareAndExchangeBooleanAcquire(Object o, long offset, boolean expected, boolean x) {
		return byte2bool(compareAndExchangeByteAcquire(o, offset, bool2byte(expected), bool2byte(x)));
	}

	public static boolean compareAndExchangeBooleanRelease(Object o, long offset, boolean expected, boolean x) {
		return byte2bool(compareAndExchangeByteRelease(o, offset, bool2byte(expected), bool2byte(x)));
	}

	public static boolean weakCompareAndSetBoolean(Object o, long offset, boolean expected, boolean x) {
		return weakCompareAndSetByte(o, offset, bool2byte(expected), bool2byte(x));
	}

	public static boolean weakCompareAndSetBooleanAcquire(Object o, long offset, boolean expected, boolean x) {
		return weakCompareAndSetByteAcquire(o, offset, bool2byte(expected), bool2byte(x));
	}

	public static boolean weakCompareAndSetBooleanRelease(Object o, long offset, boolean expected, boolean x) {
		return weakCompareAndSetByteRelease(o, offset, bool2byte(expected), bool2byte(x));
	}

	public static boolean weakCompareAndSetBooleanPlain(Object o, long offset, boolean expected, boolean x) {
		return weakCompareAndSetBytePlain(o, offset, bool2byte(expected), bool2byte(x));
	}

	public static boolean compareAndSetFloat(Object o, long offset, float expected, float x) {
		return compareAndSetInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
	}

	public static float compareAndExchangeFloat(Object o, long offset, float expected, float x) {
		int w = compareAndExchangeInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
		return Float.intBitsToFloat(w);
	}

	public static float compareAndExchangeFloatAcquire(Object o, long offset, float expected, float x) {
		int w = compareAndExchangeIntAcquire(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
		return Float.intBitsToFloat(w);
	}

	public static float compareAndExchangeFloatRelease(Object o, long offset, float expected, float x) {
		int w = compareAndExchangeIntRelease(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
		return Float.intBitsToFloat(w);
	}

	public static boolean weakCompareAndSetFloatPlain(Object o, long offset, float expected, float x) {
		return weakCompareAndSetIntPlain(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
	}

	public static boolean weakCompareAndSetFloatAcquire(Object o, long offset, float expected, float x) {
		return weakCompareAndSetIntAcquire(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
	}

	public static boolean weakCompareAndSetFloatRelease(Object o, long offset, float expected, float x) {
		return weakCompareAndSetIntRelease(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
	}

	public static boolean weakCompareAndSetFloat(Object o, long offset, float expected, float x) {
		return weakCompareAndSetInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
	}

	public static boolean compareAndSetDouble(Object o, long offset, double expected, double x) {
		return compareAndSetLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
	}

	public static double compareAndExchangeDouble(Object o, long offset, double expected, double x) {
		long w = compareAndExchangeLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
		return Double.longBitsToDouble(w);
	}

	public static double compareAndExchangeDoubleAcquire(Object o, long offset, double expected, double x) {
		long w = compareAndExchangeLongAcquire(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
		return Double.longBitsToDouble(w);
	}

	public static double compareAndExchangeDoubleRelease(Object o, long offset, double expected, double x) {
		long w = compareAndExchangeLongRelease(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
		return Double.longBitsToDouble(w);
	}

	public static boolean weakCompareAndSetDoublePlain(Object o, long offset, double expected, double x) {
		return weakCompareAndSetLongPlain(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
	}

	public static boolean weakCompareAndSetDoubleAcquire(Object o, long offset, double expected, double x) {
		return weakCompareAndSetLongAcquire(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
	}

	public static boolean weakCompareAndSetDoubleRelease(Object o, long offset, double expected, double x) {
		return weakCompareAndSetLongRelease(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
	}

	public static boolean weakCompareAndSetDouble(Object o, long offset, double expected, double x) {
		return weakCompareAndSetLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
	}

	public static boolean compareAndSetLong(Object o, long offset, long expected, long x) {
		return ((Boolean) invokeSafe(() -> compareAndSetLong.invoke(o, offset, expected, x))).booleanValue();
	}

	public static long compareAndExchangeLong(Object o, long offset, long expected, long x) {
		return ((Long) invokeSafe(() -> compareAndExchangeLong.invoke(o, offset, expected, x))).longValue();
	}

	public static long compareAndExchangeLongAcquire(Object o, long offset, long expected, long x) {
		return compareAndExchangeLong(o, offset, expected, x);
	}

	public static long compareAndExchangeLongRelease(Object o, long offset, long expected, long x) {
		return compareAndExchangeLong(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetLongPlain(Object o, long offset, long expected, long x) {
		return compareAndSetLong(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetLongAcquire(Object o, long offset, long expected, long x) {
		return compareAndSetLong(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetLongRelease(Object o, long offset, long expected, long x) {
		return compareAndSetLong(o, offset, expected, x);
	}

	public static boolean weakCompareAndSetLong(Object o, long offset, long expected, long x) {
		return compareAndSetLong(o, offset, expected, x);
	}

	public static Object getReferenceVolatile(Object o, long offset) {
		return invokeSafe(() -> getReferenceVolatile.invoke(o, offset));
	}

	public static void putReferenceVolatile(Object o, long offset, Object x) {
		invokeSafe(() -> putReferenceVolatile.invoke(o, offset, x));
	}

	public static int getIntVolatile(Object o, long offset) {
		return ((Integer) invokeSafe(() -> getIntVolatile.invoke(o, offset))).intValue();
	}

	public static void putIntVolatile(Object o, long offset, int x) {
		invokeSafe(() -> putIntVolatile.invoke(o, offset, x));
	}

	public static boolean getBooleanVolatile(Object o, long offset) {
		return ((Boolean) invokeSafe(() -> getBooleanVolatile.invoke(o, offset))).booleanValue();
	}

	public static void putBooleanVolatile(Object o, long offset, boolean x) {
		invokeSafe(() -> putBooleanVolatile.invoke(o, offset, x));
	}

	public static byte getByteVolatile(Object o, long offset) {
		return ((Byte) invokeSafe(() -> getByteVolatile.invoke(o, offset))).byteValue();
	}

	public static void putByteVolatile(Object o, long offset, byte x) {
		invokeSafe(() -> putByteVolatile.invoke(o, offset, x));
	}

	public static short getShortVolatile(Object o, long offset) {
		return ((Short) invokeSafe(() -> getShortVolatile.invoke(o, offset))).shortValue();
	}

	public static void putShortVolatile(Object o, long offset, short x) {
		invokeSafe(() -> putShortVolatile.invoke(o, offset, x));
	}

	public static char getCharVolatile(Object o, long offset) {
		return ((Character) invokeSafe(() -> getCharVolatile.invoke(o, offset))).charValue();
	}

	public static void putCharVolatile(Object o, long offset, char x) {
		invokeSafe(() -> putCharVolatile.invoke(o, offset, x));
	}

	public static long getLongVolatile(Object o, long offset) {
		return ((Long) invokeSafe(() -> getLongVolatile.invoke(o, offset))).longValue();
	}

	public static void putLongVolatile(Object o, long offset, long x) {
		invokeSafe(() -> putLongVolatile.invoke(o, offset, x));
	}

	public static float getFloatVolatile(Object o, long offset) {
		return ((Float) invokeSafe(() -> getFloatVolatile.invoke(o, offset))).floatValue();
	}

	public static void putFloatVolatile(Object o, long offset, float x) {
		invokeSafe(() -> putFloatVolatile.invoke(o, offset, x));
	}

	public static double getDoubleVolatile(Object o, long offset) {
		return ((Double) invokeSafe(() -> getDoubleVolatile.invoke(o, offset))).doubleValue();
	}

	public static void putDoubleVolatile(Object o, long offset, double x) {
		invokeSafe(() -> putDoubleVolatile.invoke(o, offset, x));
	}

	public static Object getReferenceAcquire(Object o, long offset) {
		return getReferenceVolatile(o, offset);
	}

	public static boolean getBooleanAcquire(Object o, long offset) {
		return getBooleanVolatile(o, offset);
	}

	public static byte getByteAcquire(Object o, long offset) {
		return getByteVolatile(o, offset);
	}

	public static short getShortAcquire(Object o, long offset) {
		return getShortVolatile(o, offset);
	}

	public static char getCharAcquire(Object o, long offset) {
		return getCharVolatile(o, offset);
	}

	public static int getIntAcquire(Object o, long offset) {
		return getIntVolatile(o, offset);
	}

	public static float getFloatAcquire(Object o, long offset) {
		return getFloatVolatile(o, offset);
	}

	public static long getLongAcquire(Object o, long offset) {
		return getLongVolatile(o, offset);
	}

	public static double getDoubleAcquire(Object o, long offset) {
		return getDoubleVolatile(o, offset);
	}

	public static void putReferenceRelease(Object o, long offset, Object x) {
		putReferenceVolatile(o, offset, x);
	}

	public static void putBooleanRelease(Object o, long offset, boolean x) {
		putBooleanVolatile(o, offset, x);
	}

	public static void putByteRelease(Object o, long offset, byte x) {
		putByteVolatile(o, offset, x);
	}

	public static void putShortRelease(Object o, long offset, short x) {
		putShortVolatile(o, offset, x);
	}

	public static void putCharRelease(Object o, long offset, char x) {
		putCharVolatile(o, offset, x);
	}

	public static void putIntRelease(Object o, long offset, int x) {
		putIntVolatile(o, offset, x);
	}

	public static void putFloatRelease(Object o, long offset, float x) {
		putFloatVolatile(o, offset, x);
	}

	public static void putLongRelease(Object o, long offset, long x) {
		putLongVolatile(o, offset, x);
	}

	public static void putDoubleRelease(Object o, long offset, double x) {
		putDoubleVolatile(o, offset, x);
	}

	public static Object getReferenceOpaque(Object o, long offset) {
		return getReferenceVolatile(o, offset);
	}

	public static boolean getBooleanOpaque(Object o, long offset) {
		return getBooleanVolatile(o, offset);
	}

	public static byte getByteOpaque(Object o, long offset) {
		return getByteVolatile(o, offset);
	}

	public static short getShortOpaque(Object o, long offset) {
		return getShortVolatile(o, offset);
	}

	public static char getCharOpaque(Object o, long offset) {
		return getCharVolatile(o, offset);
	}

	public static int getIntOpaque(Object o, long offset) {
		return getIntVolatile(o, offset);
	}

	public static float getFloatOpaque(Object o, long offset) {
		return getFloatVolatile(o, offset);
	}

	public static long getLongOpaque(Object o, long offset) {
		return getLongVolatile(o, offset);
	}

	public static double getDoubleOpaque(Object o, long offset) {
		return getDoubleVolatile(o, offset);
	}

	public static void putReferenceOpaque(Object o, long offset, Object x) {
		putReferenceVolatile(o, offset, x);
	}

	public static void putBooleanOpaque(Object o, long offset, boolean x) {
		putBooleanVolatile(o, offset, x);
	}

	public static void putByteOpaque(Object o, long offset, byte x) {
		putByteVolatile(o, offset, x);
	}

	public static void putShortOpaque(Object o, long offset, short x) {
		putShortVolatile(o, offset, x);
	}

	public static void putCharOpaque(Object o, long offset, char x) {
		putCharVolatile(o, offset, x);
	}

	public static void putIntOpaque(Object o, long offset, int x) {
		putIntVolatile(o, offset, x);
	}

	public static void putFloatOpaque(Object o, long offset, float x) {
		putFloatVolatile(o, offset, x);
	}

	public static void putLongOpaque(Object o, long offset, long x) {
		putLongVolatile(o, offset, x);
	}

	public static void putDoubleOpaque(Object o, long offset, double x) {
		putDoubleVolatile(o, offset, x);
	}

	public static void unpark(Object thread) {
		invokeSafe(() -> unpark.invoke(thread));
	}

	public static void park(boolean isAbsolute, long time) {
		invokeSafe(() -> park.invoke(isAbsolute, time));
	}

	public static int getLoadAverage(double[] loadavg, int nelems) {
		if (nelems >= 0 && nelems <= 3 && nelems <= loadavg.length)
			return getLoadAverage0(loadavg, nelems);
		throw new ArrayIndexOutOfBoundsException();
	}

	public static int getAndAddInt(Object o, long offset, int delta) {
		int v;
		do {
			v = getIntVolatile(o, offset);
		} while (!weakCompareAndSetInt(o, offset, v, v + delta));
		return v;
	}

	public static int getAndAddIntRelease(Object o, long offset, int delta) {
		int v;
		do {
			v = getInt(o, offset);
		} while (!weakCompareAndSetIntRelease(o, offset, v, v + delta));
		return v;
	}

	public static int getAndAddIntAcquire(Object o, long offset, int delta) {
		int v;
		do {
			v = getIntAcquire(o, offset);
		} while (!weakCompareAndSetIntAcquire(o, offset, v, v + delta));
		return v;
	}

	public static long getAndAddLong(Object o, long offset, long delta) {
		long v;
		do {
			v = getLongVolatile(o, offset);
		} while (!weakCompareAndSetLong(o, offset, v, v + delta));
		return v;
	}

	public static long getAndAddLongRelease(Object o, long offset, long delta) {
		long v;
		do {
			v = getLong(o, offset);
		} while (!weakCompareAndSetLongRelease(o, offset, v, v + delta));
		return v;
	}

	public static long getAndAddLongAcquire(Object o, long offset, long delta) {
		long v;
		do {
			v = getLongAcquire(o, offset);
		} while (!weakCompareAndSetLongAcquire(o, offset, v, v + delta));
		return v;
	}

	public static byte getAndAddByte(Object o, long offset, byte delta) {
		byte v;
		do {
			v = getByteVolatile(o, offset);
		} while (!weakCompareAndSetByte(o, offset, v, (byte) (v + delta)));
		return v;
	}

	public static byte getAndAddByteRelease(Object o, long offset, byte delta) {
		byte v;
		do {
			v = getByte(o, offset);
		} while (!weakCompareAndSetByteRelease(o, offset, v, (byte) (v + delta)));
		return v;
	}

	public static byte getAndAddByteAcquire(Object o, long offset, byte delta) {
		byte v;
		do {
			v = getByteAcquire(o, offset);
		} while (!weakCompareAndSetByteAcquire(o, offset, v, (byte) (v + delta)));
		return v;
	}

	public static short getAndAddShort(Object o, long offset, short delta) {
		short v;
		do {
			v = getShortVolatile(o, offset);
		} while (!weakCompareAndSetShort(o, offset, v, (short) (v + delta)));
		return v;
	}

	public static short getAndAddShortRelease(Object o, long offset, short delta) {
		short v;
		do {
			v = getShort(o, offset);
		} while (!weakCompareAndSetShortRelease(o, offset, v, (short) (v + delta)));
		return v;
	}

	public static short getAndAddShortAcquire(Object o, long offset, short delta) {
		short v;
		do {
			v = getShortAcquire(o, offset);
		} while (!weakCompareAndSetShortAcquire(o, offset, v, (short) (v + delta)));
		return v;
	}

	public static char getAndAddChar(Object o, long offset, char delta) {
		return (char) getAndAddShort(o, offset, (short) delta);
	}

	public static char getAndAddCharRelease(Object o, long offset, char delta) {
		return (char) getAndAddShortRelease(o, offset, (short) delta);
	}

	public static char getAndAddCharAcquire(Object o, long offset, char delta) {
		return (char) getAndAddShortAcquire(o, offset, (short) delta);
	}

	public static float getAndAddFloat(Object o, long offset, float delta) {
		int expectedBits;
		float v;
		do {
			expectedBits = getIntVolatile(o, offset);
			v = Float.intBitsToFloat(expectedBits);
		} while (!weakCompareAndSetInt(o, offset, expectedBits, Float.floatToRawIntBits(v + delta)));
		return v;
	}

	public static float getAndAddFloatRelease(Object o, long offset, float delta) {
		int expectedBits;
		float v;
		do {
			expectedBits = getInt(o, offset);
			v = Float.intBitsToFloat(expectedBits);
		} while (!weakCompareAndSetIntRelease(o, offset, expectedBits, Float.floatToRawIntBits(v + delta)));
		return v;
	}

	public static float getAndAddFloatAcquire(Object o, long offset, float delta) {
		int expectedBits;
		float v;
		do {
			expectedBits = getIntAcquire(o, offset);
			v = Float.intBitsToFloat(expectedBits);
		} while (!weakCompareAndSetIntAcquire(o, offset, expectedBits, Float.floatToRawIntBits(v + delta)));
		return v;
	}

	public static double getAndAddDouble(Object o, long offset, double delta) {
		long expectedBits;
		double v;
		do {
			expectedBits = getLongVolatile(o, offset);
			v = Double.longBitsToDouble(expectedBits);
		} while (!weakCompareAndSetLong(o, offset, expectedBits, Double.doubleToRawLongBits(v + delta)));
		return v;
	}

	public static double getAndAddDoubleRelease(Object o, long offset, double delta) {
		long expectedBits;
		double v;
		do {
			expectedBits = getLong(o, offset);
			v = Double.longBitsToDouble(expectedBits);
		} while (!weakCompareAndSetLongRelease(o, offset, expectedBits, Double.doubleToRawLongBits(v + delta)));
		return v;
	}

	public static double getAndAddDoubleAcquire(Object o, long offset, double delta) {
		long expectedBits;
		double v;
		do {
			expectedBits = getLongAcquire(o, offset);
			v = Double.longBitsToDouble(expectedBits);
		} while (!weakCompareAndSetLongAcquire(o, offset, expectedBits, Double.doubleToRawLongBits(v + delta)));
		return v;
	}

	public static int getAndSetInt(Object o, long offset, int newValue) {
		int v;
		do {
			v = getIntVolatile(o, offset);
		} while (!weakCompareAndSetInt(o, offset, v, newValue));
		return v;
	}

	public static int getAndSetIntRelease(Object o, long offset, int newValue) {
		int v;
		do {
			v = getInt(o, offset);
		} while (!weakCompareAndSetIntRelease(o, offset, v, newValue));
		return v;
	}

	public static int getAndSetIntAcquire(Object o, long offset, int newValue) {
		int v;
		do {
			v = getIntAcquire(o, offset);
		} while (!weakCompareAndSetIntAcquire(o, offset, v, newValue));
		return v;
	}

	public static long getAndSetLong(Object o, long offset, long newValue) {
		long v;
		do {
			v = getLongVolatile(o, offset);
		} while (!weakCompareAndSetLong(o, offset, v, newValue));
		return v;
	}

	public static long getAndSetLongRelease(Object o, long offset, long newValue) {
		long v;
		do {
			v = getLong(o, offset);
		} while (!weakCompareAndSetLongRelease(o, offset, v, newValue));
		return v;
	}

	public static long getAndSetLongAcquire(Object o, long offset, long newValue) {
		long v;
		do {
			v = getLongAcquire(o, offset);
		} while (!weakCompareAndSetLongAcquire(o, offset, v, newValue));
		return v;
	}

	public static Object getAndSetReference(Object o, long offset, Object newValue) {
		Object v;
		do {
			v = getReferenceVolatile(o, offset);
		} while (!weakCompareAndSetReference(o, offset, v, newValue));
		return v;
	}

	public static Object getAndSetReferenceRelease(Object o, long offset, Object newValue) {
		Object v;
		do {
			v = getReference(o, offset);
		} while (!weakCompareAndSetReferenceRelease(o, offset, v, newValue));
		return v;
	}

	public static Object getAndSetReferenceAcquire(Object o, long offset, Object newValue) {
		Object v;
		do {
			v = getReferenceAcquire(o, offset);
		} while (!weakCompareAndSetReferenceAcquire(o, offset, v, newValue));
		return v;
	}

	public static byte getAndSetByte(Object o, long offset, byte newValue) {
		byte v;
		do {
			v = getByteVolatile(o, offset);
		} while (!weakCompareAndSetByte(o, offset, v, newValue));
		return v;
	}

	public static byte getAndSetByteRelease(Object o, long offset, byte newValue) {
		byte v;
		do {
			v = getByte(o, offset);
		} while (!weakCompareAndSetByteRelease(o, offset, v, newValue));
		return v;
	}

	public static byte getAndSetByteAcquire(Object o, long offset, byte newValue) {
		byte v;
		do {
			v = getByteAcquire(o, offset);
		} while (!weakCompareAndSetByteAcquire(o, offset, v, newValue));
		return v;
	}

	public static boolean getAndSetBoolean(Object o, long offset, boolean newValue) {
		return byte2bool(getAndSetByte(o, offset, bool2byte(newValue)));
	}

	public static boolean getAndSetBooleanRelease(Object o, long offset, boolean newValue) {
		return byte2bool(getAndSetByteRelease(o, offset, bool2byte(newValue)));
	}

	public static boolean getAndSetBooleanAcquire(Object o, long offset, boolean newValue) {
		return byte2bool(getAndSetByteAcquire(o, offset, bool2byte(newValue)));
	}

	public static short getAndSetShort(Object o, long offset, short newValue) {
		short v;
		do {
			v = getShortVolatile(o, offset);
		} while (!weakCompareAndSetShort(o, offset, v, newValue));
		return v;
	}

	public static short getAndSetShortRelease(Object o, long offset, short newValue) {
		short v;
		do {
			v = getShort(o, offset);
		} while (!weakCompareAndSetShortRelease(o, offset, v, newValue));
		return v;
	}

	public static short getAndSetShortAcquire(Object o, long offset, short newValue) {
		short v;
		do {
			v = getShortAcquire(o, offset);
		} while (!weakCompareAndSetShortAcquire(o, offset, v, newValue));
		return v;
	}

	public static char getAndSetChar(Object o, long offset, char newValue) {
		return s2c(getAndSetShort(o, offset, c2s(newValue)));
	}

	public static char getAndSetCharRelease(Object o, long offset, char newValue) {
		return s2c(getAndSetShortRelease(o, offset, c2s(newValue)));
	}

	public static char getAndSetCharAcquire(Object o, long offset, char newValue) {
		return s2c(getAndSetShortAcquire(o, offset, c2s(newValue)));
	}

	public static float getAndSetFloat(Object o, long offset, float newValue) {
		int v = getAndSetInt(o, offset, Float.floatToRawIntBits(newValue));
		return Float.intBitsToFloat(v);
	}

	public static float getAndSetFloatRelease(Object o, long offset, float newValue) {
		int v = getAndSetIntRelease(o, offset, Float.floatToRawIntBits(newValue));
		return Float.intBitsToFloat(v);
	}

	public static float getAndSetFloatAcquire(Object o, long offset, float newValue) {
		int v = getAndSetIntAcquire(o, offset, Float.floatToRawIntBits(newValue));
		return Float.intBitsToFloat(v);
	}

	public static double getAndSetDouble(Object o, long offset, double newValue) {
		long v = getAndSetLong(o, offset, Double.doubleToRawLongBits(newValue));
		return Double.longBitsToDouble(v);
	}

	public static double getAndSetDoubleRelease(Object o, long offset, double newValue) {
		long v = getAndSetLongRelease(o, offset, Double.doubleToRawLongBits(newValue));
		return Double.longBitsToDouble(v);
	}

	public static double getAndSetDoubleAcquire(Object o, long offset, double newValue) {
		long v = getAndSetLongAcquire(o, offset, Double.doubleToRawLongBits(newValue));
		return Double.longBitsToDouble(v);
	}

	public static boolean getAndBitwiseOrBoolean(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseOrByte(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseOrBooleanRelease(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseOrByteRelease(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseOrBooleanAcquire(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseOrByteAcquire(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseAndBoolean(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseAndByte(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseAndBooleanRelease(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseAndByteRelease(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseAndBooleanAcquire(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseAndByteAcquire(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseXorBoolean(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseXorByte(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseXorBooleanRelease(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseXorByteRelease(o, offset, bool2byte(mask)));
	}

	public static boolean getAndBitwiseXorBooleanAcquire(Object o, long offset, boolean mask) {
		return byte2bool(getAndBitwiseXorByteAcquire(o, offset, bool2byte(mask)));
	}

	public static byte getAndBitwiseOrByte(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByteVolatile(o, offset);
		} while (!weakCompareAndSetByte(o, offset, current, (byte) (current | mask)));
		return current;
	}

	public static byte getAndBitwiseOrByteRelease(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteRelease(o, offset, current, (byte) (current | mask)));
		return current;
	}

	public static byte getAndBitwiseOrByteAcquire(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteAcquire(o, offset, current, (byte) (current | mask)));
		return current;
	}

	public static byte getAndBitwiseAndByte(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByteVolatile(o, offset);
		} while (!weakCompareAndSetByte(o, offset, current, (byte) (current & mask)));
		return current;
	}

	public static byte getAndBitwiseAndByteRelease(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteRelease(o, offset, current, (byte) (current & mask)));
		return current;
	}

	public static byte getAndBitwiseAndByteAcquire(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteAcquire(o, offset, current, (byte) (current & mask)));
		return current;
	}

	public static byte getAndBitwiseXorByte(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByteVolatile(o, offset);
		} while (!weakCompareAndSetByte(o, offset, current, (byte) (current ^ mask)));
		return current;
	}

	public static byte getAndBitwiseXorByteRelease(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteRelease(o, offset, current, (byte) (current ^ mask)));
		return current;
	}

	public static byte getAndBitwiseXorByteAcquire(Object o, long offset, byte mask) {
		byte current;
		do {
			current = getByte(o, offset);
		} while (!weakCompareAndSetByteAcquire(o, offset, current, (byte) (current ^ mask)));
		return current;
	}

	public static char getAndBitwiseOrChar(Object o, long offset, char mask) {
		return s2c(getAndBitwiseOrShort(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseOrCharRelease(Object o, long offset, char mask) {
		return s2c(getAndBitwiseOrShortRelease(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseOrCharAcquire(Object o, long offset, char mask) {
		return s2c(getAndBitwiseOrShortAcquire(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseAndChar(Object o, long offset, char mask) {
		return s2c(getAndBitwiseAndShort(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseAndCharRelease(Object o, long offset, char mask) {
		return s2c(getAndBitwiseAndShortRelease(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseAndCharAcquire(Object o, long offset, char mask) {
		return s2c(getAndBitwiseAndShortAcquire(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseXorChar(Object o, long offset, char mask) {
		return s2c(getAndBitwiseXorShort(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseXorCharRelease(Object o, long offset, char mask) {
		return s2c(getAndBitwiseXorShortRelease(o, offset, c2s(mask)));
	}

	public static char getAndBitwiseXorCharAcquire(Object o, long offset, char mask) {
		return s2c(getAndBitwiseXorShortAcquire(o, offset, c2s(mask)));
	}

	public static short getAndBitwiseOrShort(Object o, long offset, short mask) {
		short current;
		do {
			current = getShortVolatile(o, offset);
		} while (!weakCompareAndSetShort(o, offset, current, (short) (current | mask)));
		return current;
	}

	public static short getAndBitwiseOrShortRelease(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortRelease(o, offset, current, (short) (current | mask)));
		return current;
	}

	public static short getAndBitwiseOrShortAcquire(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortAcquire(o, offset, current, (short) (current | mask)));
		return current;
	}

	public static short getAndBitwiseAndShort(Object o, long offset, short mask) {
		short current;
		do {
			current = getShortVolatile(o, offset);
		} while (!weakCompareAndSetShort(o, offset, current, (short) (current & mask)));
		return current;
	}

	public static short getAndBitwiseAndShortRelease(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortRelease(o, offset, current, (short) (current & mask)));
		return current;
	}

	public static short getAndBitwiseAndShortAcquire(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortAcquire(o, offset, current, (short) (current & mask)));
		return current;
	}

	public static short getAndBitwiseXorShort(Object o, long offset, short mask) {
		short current;
		do {
			current = getShortVolatile(o, offset);
		} while (!weakCompareAndSetShort(o, offset, current, (short) (current ^ mask)));
		return current;
	}

	public static short getAndBitwiseXorShortRelease(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortRelease(o, offset, current, (short) (current ^ mask)));
		return current;
	}

	public static short getAndBitwiseXorShortAcquire(Object o, long offset, short mask) {
		short current;
		do {
			current = getShort(o, offset);
		} while (!weakCompareAndSetShortAcquire(o, offset, current, (short) (current ^ mask)));
		return current;
	}

	public static int getAndBitwiseOrInt(Object o, long offset, int mask) {
		int current;
		do {
			current = getIntVolatile(o, offset);
		} while (!weakCompareAndSetInt(o, offset, current, current | mask));
		return current;
	}

	public static int getAndBitwiseOrIntRelease(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntRelease(o, offset, current, current | mask));
		return current;
	}

	public static int getAndBitwiseOrIntAcquire(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntAcquire(o, offset, current, current | mask));
		return current;
	}

	public static int getAndBitwiseAndInt(Object o, long offset, int mask) {
		int current;
		do {
			current = getIntVolatile(o, offset);
		} while (!weakCompareAndSetInt(o, offset, current, current & mask));
		return current;
	}

	public static int getAndBitwiseAndIntRelease(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntRelease(o, offset, current, current & mask));
		return current;
	}

	public static int getAndBitwiseAndIntAcquire(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntAcquire(o, offset, current, current & mask));
		return current;
	}

	public static int getAndBitwiseXorInt(Object o, long offset, int mask) {
		int current;
		do {
			current = getIntVolatile(o, offset);
		} while (!weakCompareAndSetInt(o, offset, current, current ^ mask));
		return current;
	}

	public static int getAndBitwiseXorIntRelease(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntRelease(o, offset, current, current ^ mask));
		return current;
	}

	public static int getAndBitwiseXorIntAcquire(Object o, long offset, int mask) {
		int current;
		do {
			current = getInt(o, offset);
		} while (!weakCompareAndSetIntAcquire(o, offset, current, current ^ mask));
		return current;
	}

	public static long getAndBitwiseOrLong(Object o, long offset, long mask) {
		long current;
		do {
			current = getLongVolatile(o, offset);
		} while (!weakCompareAndSetLong(o, offset, current, current | mask));
		return current;
	}

	public static long getAndBitwiseOrLongRelease(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongRelease(o, offset, current, current | mask));
		return current;
	}

	public static long getAndBitwiseOrLongAcquire(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongAcquire(o, offset, current, current | mask));
		return current;
	}

	public static long getAndBitwiseAndLong(Object o, long offset, long mask) {
		long current;
		do {
			current = getLongVolatile(o, offset);
		} while (!weakCompareAndSetLong(o, offset, current, current & mask));
		return current;
	}

	public static long getAndBitwiseAndLongRelease(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongRelease(o, offset, current, current & mask));
		return current;
	}

	public static long getAndBitwiseAndLongAcquire(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongAcquire(o, offset, current, current & mask));
		return current;
	}

	public static long getAndBitwiseXorLong(Object o, long offset, long mask) {
		long current;
		do {
			current = getLongVolatile(o, offset);
		} while (!weakCompareAndSetLong(o, offset, current, current ^ mask));
		return current;
	}

	public static long getAndBitwiseXorLongRelease(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongRelease(o, offset, current, current ^ mask));
		return current;
	}

	public static long getAndBitwiseXorLongAcquire(Object o, long offset, long mask) {
		long current;
		do {
			current = getLong(o, offset);
		} while (!weakCompareAndSetLongAcquire(o, offset, current, current ^ mask));
		return current;
	}

	public static void loadFence() {
		MethodHandle var10000 = loadFence;
		Objects.requireNonNull(var10000);
		Objects.requireNonNull(var10000);
		invokeSafe(var10000::invoke);
	}

	public static void storeFence() {
		MethodHandle var10000 = storeFence;
		Objects.requireNonNull(var10000);
		Objects.requireNonNull(var10000);
		invokeSafe(var10000::invoke);
	}

	public static void fullFence() {
		MethodHandle var10000 = fullFence;
		Objects.requireNonNull(var10000);
		Objects.requireNonNull(var10000);
		invokeSafe(var10000::invoke);
	}

	public static void loadLoadFence() {
		loadFence();
	}

	public static void storeStoreFence() {
		storeFence();
	}
	
	private static void throwIllegalAccessError() {
		throw new IllegalAccessError();
	}

	private static void throwNoSuchMethodError() {
		throw new NoSuchMethodError();
	}

	public static boolean isBigEndian() {
		return BIG_ENDIAN;
	}

	public static boolean unalignedAccess() {
		return UNALIGNED_ACCESS;
	}

	public static long getLongUnaligned(Object o, long offset) {
		if ((offset & 0x7L) == 0L)
			return getLong(o, offset);
		if ((offset & 0x3L) == 0L)
			return makeLong(getInt(o, offset), getInt(o, offset + 4L));
		return ((offset & 0x1L) == 0L) ? makeLong(getShort(o, offset), getShort(o, offset + 2L), getShort(o, offset + 4L), getShort(o, offset + 6L)) : makeLong(getByte(o, offset), getByte(o, offset + 1L), getByte(o, offset + 2L), getByte(o, offset + 3L), getByte(o, offset + 4L), getByte(o, offset + 5L), getByte(o, offset + 6L), getByte(o, offset + 7L));
	}

	public static long getLongUnaligned(Object o, long offset, boolean bigEndian) {
		return convEndian(bigEndian, getLongUnaligned(o, offset));
	}

	public static int getIntUnaligned(Object o, long offset) {
		if ((offset & 0x3L) == 0L)
			return getInt(o, offset);
		return ((offset & 0x1L) == 0L) ? makeInt(getShort(o, offset), getShort(o, offset + 2L)) : makeInt(getByte(o, offset), getByte(o, offset + 1L), getByte(o, offset + 2L), getByte(o, offset + 3L));
	}

	public static int getIntUnaligned(Object o, long offset, boolean bigEndian) {
		return convEndian(bigEndian, getIntUnaligned(o, offset));
	}

	public static short getShortUnaligned(Object o, long offset) {
		return ((offset & 0x1L) == 0L) ? getShort(o, offset) : makeShort(getByte(o, offset), getByte(o, offset + 1L));
	}

	public static short getShortUnaligned(Object o, long offset, boolean bigEndian) {
		return convEndian(bigEndian, getShortUnaligned(o, offset));
	}

	public static char getCharUnaligned(Object o, long offset) {
		return ((offset & 0x1L) == 0L) ? getChar(o, offset) : (char) makeShort(getByte(o, offset), getByte(o, offset + 1L));
	}

	public static char getCharUnaligned(Object o, long offset, boolean bigEndian) {
		return convEndian(bigEndian, getCharUnaligned(o, offset));
	}

	public static void putLongUnaligned(Object o, long offset, long x) {
		if ((offset & 0x7L) == 0L) {
			putLong(o, offset, x);
		} else if ((offset & 0x3L) == 0L) {
			putLongParts(o, offset, (int) x, (int) (x >>> 32L));
		} else if ((offset & 0x1L) == 0L) {
			putLongParts(o, offset, (short) (int) x, (short) (int) (x >>> 16L), (short) (int) (x >>> 32L), (short) (int) (x >>> 48L));
		} else {
			putLongParts(o, offset, (byte) (int) x, (byte) (int) (x >>> 8L), (byte) (int) (x >>> 16L), (byte) (int) (x >>> 24L), (byte) (int) (x >>> 32L), (byte) (int) (x >>> 40L), (byte) (int) (x >>> 48L), (byte) (int) (x >>> 56L));
		}
	}

	public static void putLongUnaligned(Object o, long offset, long x, boolean bigEndian) {
		putLongUnaligned(o, offset, convEndian(bigEndian, x));
	}

	public static void putIntUnaligned(Object o, long offset, int x) {
		if ((offset & 0x3L) == 0L) {
			putInt(o, offset, x);
		} else if ((offset & 0x1L) == 0L) {
			putIntParts(o, offset, (short) x, (short) (x >>> 16));
		} else {
			putIntParts(o, offset, (byte) x, (byte) (x >>> 8), (byte) (x >>> 16), (byte) (x >>> 24));
		}
	}

	public static void putIntUnaligned(Object o, long offset, int x, boolean bigEndian) {
		putIntUnaligned(o, offset, convEndian(bigEndian, x));
	}

	public static void putShortUnaligned(Object o, long offset, short x) {
		if ((offset & 0x1L) == 0L) {
			putShort(o, offset, x);
		} else {
			putShortParts(o, offset, (byte) x, (byte) (x >>> 8));
		}
	}

	public static void putShortUnaligned(Object o, long offset, short x, boolean bigEndian) {
		putShortUnaligned(o, offset, convEndian(bigEndian, x));
	}

	public static void putCharUnaligned(Object o, long offset, char x) {
		putShortUnaligned(o, offset, (short) x);
	}

	public static void putCharUnaligned(Object o, long offset, char x, boolean bigEndian) {
		putCharUnaligned(o, offset, convEndian(bigEndian, x));
	}

	private static int pickPos(int top, int pos) {
		return BIG_ENDIAN ? (top - pos) : pos;
	}

	private static long makeLong(byte i0, byte i1, byte i2, byte i3, byte i4, byte i5, byte i6, byte i7) {
		return toUnsignedLong(i0) << pickPos(56, 0) | toUnsignedLong(i1) << pickPos(56, 8) | toUnsignedLong(i2) << pickPos(56, 16) | toUnsignedLong(i3) << pickPos(56, 24) | toUnsignedLong(i4) << pickPos(56, 32) | toUnsignedLong(i5) << pickPos(56, 40) | toUnsignedLong(i6) << pickPos(56, 48) | toUnsignedLong(i7) << pickPos(56, 56);
	}

	private static long makeLong(short i0, short i1, short i2, short i3) {
		return toUnsignedLong(i0) << pickPos(48, 0) | toUnsignedLong(i1) << pickPos(48, 16) | toUnsignedLong(i2) << pickPos(48, 32) | toUnsignedLong(i3) << pickPos(48, 48);
	}

	private static long makeLong(int i0, int i1) {
		return toUnsignedLong(i0) << pickPos(32, 0) | toUnsignedLong(i1) << pickPos(32, 32);
	}

	private static int makeInt(short i0, short i1) {
		return toUnsignedInt(i0) << pickPos(16, 0) | toUnsignedInt(i1) << pickPos(16, 16);
	}

	private static int makeInt(byte i0, byte i1, byte i2, byte i3) {
		return toUnsignedInt(i0) << pickPos(24, 0) | toUnsignedInt(i1) << pickPos(24, 8) | toUnsignedInt(i2) << pickPos(24, 16) | toUnsignedInt(i3) << pickPos(24, 24);
	}

	private static short makeShort(byte i0, byte i1) {
		return (short) (toUnsignedInt(i0) << pickPos(8, 0) | toUnsignedInt(i1) << pickPos(8, 8));
	}

	private static byte pick(byte le, byte be) {
		return BIG_ENDIAN ? be : le;
	}

	private static short pick(short le, short be) {
		return BIG_ENDIAN ? be : le;
	}

	private static int pick(int le, int be) {
		return BIG_ENDIAN ? be : le;
	}

	private static void putLongParts(Object o, long offset, byte i0, byte i1, byte i2, byte i3, byte i4, byte i5, byte i6, byte i7) {
		putByte(o, offset, pick(i0, i7));
		putByte(o, offset + 1L, pick(i1, i6));
		putByte(o, offset + 2L, pick(i2, i5));
		putByte(o, offset + 3L, pick(i3, i4));
		putByte(o, offset + 4L, pick(i4, i3));
		putByte(o, offset + 5L, pick(i5, i2));
		putByte(o, offset + 6L, pick(i6, i1));
		putByte(o, offset + 7L, pick(i7, i0));
	}

	private static void putLongParts(Object o, long offset, short i0, short i1, short i2, short i3) {
		putShort(o, offset, pick(i0, i3));
		putShort(o, offset + 2L, pick(i1, i2));
		putShort(o, offset + 4L, pick(i2, i1));
		putShort(o, offset + 6L, pick(i3, i0));
	}

	private static void putLongParts(Object o, long offset, int i0, int i1) {
		putInt(o, offset, pick(i0, i1));
		putInt(o, offset + 4L, pick(i1, i0));
	}

	private static void putIntParts(Object o, long offset, short i0, short i1) {
		putShort(o, offset, pick(i0, i1));
		putShort(o, offset + 2L, pick(i1, i0));
	}

	private static void putIntParts(Object o, long offset, byte i0, byte i1, byte i2, byte i3) {
		putByte(o, offset, pick(i0, i3));
		putByte(o, offset + 1L, pick(i1, i2));
		putByte(o, offset + 2L, pick(i2, i1));
		putByte(o, offset + 3L, pick(i3, i0));
	}

	private static void putShortParts(Object o, long offset, byte i0, byte i1) {
		putByte(o, offset, pick(i0, i1));
		putByte(o, offset + 1L, pick(i1, i0));
	}

	private static int toUnsignedInt(byte n) {
		return n & 0xFF;
	}

	private static int toUnsignedInt(short n) {
		return n & 0xFFFF;
	}

	private static long toUnsignedLong(byte n) {
		return n & 0xFFL;
	}

	private static long toUnsignedLong(short n) {
		return n & 0xFFFFL;
	}

	private static long toUnsignedLong(int n) {
		return n & 0xFFFFFFFFL;
	}

	private static char convEndian(boolean big, char n) {
		return (big == BIG_ENDIAN) ? n : Character.reverseBytes(n);
	}

	private static short convEndian(boolean big, short n) {
		return (big == BIG_ENDIAN) ? n : Short.reverseBytes(n);
	}

	private static int convEndian(boolean big, int n) {
		return (big == BIG_ENDIAN) ? n : Integer.reverseBytes(n);
	}

	private static long convEndian(boolean big, long n) {
		return (big == BIG_ENDIAN) ? n : Long.reverseBytes(n);
	}

	private static long allocateMemory0(long bytes) {
		return ((Long) invokeSafe(() -> allocateMemory0.invoke(bytes))).longValue();
	}

	private static long reallocateMemory0(long address, long bytes) {
		return ((Long) invokeSafe(() -> reallocateMemory0.invoke(address, bytes))).longValue();
	}

	private static void freeMemory0(long address) {
		invokeSafe(() -> freeMemory0.invoke(address));
	}

	private static void setMemory0(Object o, long offset, long bytes, byte value) {
		invokeSafe(() -> setMemory0.invoke(o, offset, bytes, value));
	}

	private static void copyMemory0(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
		invokeSafe(() -> copyMemory0.invoke(srcBase, srcOffset, destBase, destOffset, bytes));
	}

	private static void copySwapMemory0(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes, long elemSize) {
		invokeSafe(() -> copySwapMemory0.invoke(srcBase, srcOffset, destBase, destOffset, bytes, elemSize));
	}

	private static long objectFieldOffset0(Field f) {
		return ((Long) invokeSafe(() -> objectFieldOffset0.invoke(f))).longValue();
	}

	private static long objectFieldOffset1(Class<?> c, String name) {
		return ((Long) invokeSafe(() -> objectFieldOffset1.invoke(c, name))).longValue();
	}

	private static long staticFieldOffset0(Field f) {
		return ((Long) invokeSafe(() -> staticFieldOffset0.invoke(f))).longValue();
	}

	private static Object staticFieldBase0(Field f) {
		return invokeSafe(() -> staticFieldBase0.invoke(f));
	}

	private static boolean shouldBeInitialized0(Class<?> c) {
		return ((Boolean) invokeSafe(() -> shouldBeInitialized0.invoke(c))).booleanValue();
	}

	private static void ensureClassInitialized0(Class<?> c) {
		invokeSafe(() -> ensureClassInitialized0.invoke(c));
	}

	private static int arrayBaseOffset0(Class<?> arrayClass) {
		return ((Integer) invokeSafe(() -> arrayBaseOffset0.invoke(arrayClass))).intValue();
	}

	private static int arrayIndexScale0(Class<?> arrayClass) {
		return ((Integer) invokeSafe(() -> arrayIndexScale0.invoke(arrayClass))).intValue();
	}

	private static int getLoadAverage0(double[] loadavg, int nelems) {
		return ((Integer) invokeSafe(() -> getLoadAverage0.invoke(loadavg, nelems))).intValue();
	}

	private static <T> T invokeSafe(Runner<T> run) {
		try {
			return run.run();
		} catch (Throwable var2) {
			return null;
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

	static {
		Class<?> jdkUnsafe, jdkUnsafeConstants;
		MethodHandles.Lookup lookup = LOOKUP;
		try {
			jdkUnsafe = Class.forName("jdk.internal.misc.Unsafe");
			jdkUnsafeConstants = Class.forName("jdk.internal.misc.UnsafeConstants");
			directBuffer = Class.forName("sun.nio.ch.DirectBuffer");
		} catch (ClassNotFoundException var7) {
			ClassNotFoundException e = var7;
			throw new RuntimeException(e);
		}
		try {
			jdkUnsafeInstance = lookup.findStaticVarHandle(jdkUnsafe, "theUnsafe", jdkUnsafe).get();
			ADDRESS_SIZE = (int) lookup.findStaticVarHandle(jdkUnsafeConstants, "ADDRESS_SIZE0", int.class).get();
			PAGE_SIZE = (int) lookup.findStaticVarHandle(jdkUnsafeConstants, "PAGE_SIZE", int.class).get();
			BIG_ENDIAN = (boolean) lookup.findStaticVarHandle(jdkUnsafeConstants, "BIG_ENDIAN", boolean.class).get();
			UNALIGNED_ACCESS = (boolean) lookup.findStaticVarHandle(jdkUnsafeConstants, "UNALIGNED_ACCESS", boolean.class).get();
			DATA_CACHE_LINE_FLUSH_SIZE = (int) lookup.findStaticVarHandle(jdkUnsafeConstants, "DATA_CACHE_LINE_FLUSH_SIZE", int.class).get();
		} catch (IllegalAccessException | NoSuchFieldException var6) {
			ReflectiveOperationException e = var6;
			throw new RuntimeException(e);
		}
		try {
			getInt = lookup.findVirtual(jdkUnsafe, "getInt", MethodType.methodType(int.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putInt = lookup.findVirtual(jdkUnsafe, "putInt", MethodType.methodType(void.class, Object.class, new Class[] { long.class, int.class })).bindTo(jdkUnsafeInstance);
			getReference = lookup.findVirtual(jdkUnsafe, "getReference", MethodType.methodType(Object.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putReference = lookup.findVirtual(jdkUnsafe, "putReference", MethodType.methodType(void.class, Object.class, new Class[] { long.class, Object.class })).bindTo(jdkUnsafeInstance);
			getBoolean = lookup.findVirtual(jdkUnsafe, "getBoolean", MethodType.methodType(boolean.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putBoolean = lookup.findVirtual(jdkUnsafe, "putBoolean", MethodType.methodType(void.class, Object.class, new Class[] { long.class, boolean.class })).bindTo(jdkUnsafeInstance);
			getByte = lookup.findVirtual(jdkUnsafe, "getByte", MethodType.methodType(byte.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putByte = lookup.findVirtual(jdkUnsafe, "putByte", MethodType.methodType(void.class, Object.class, new Class[] { long.class, byte.class })).bindTo(jdkUnsafeInstance);
			getShort = lookup.findVirtual(jdkUnsafe, "getShort", MethodType.methodType(short.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putShort = lookup.findVirtual(jdkUnsafe, "putShort", MethodType.methodType(void.class, Object.class, new Class[] { long.class, short.class })).bindTo(jdkUnsafeInstance);
			getChar = lookup.findVirtual(jdkUnsafe, "getChar", MethodType.methodType(char.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putChar = lookup.findVirtual(jdkUnsafe, "putChar", MethodType.methodType(void.class, Object.class, new Class[] { long.class, char.class })).bindTo(jdkUnsafeInstance);
			getLong = lookup.findVirtual(jdkUnsafe, "getLong", MethodType.methodType(long.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putLong = lookup.findVirtual(jdkUnsafe, "putLong", MethodType.methodType(void.class, Object.class, new Class[] { long.class, long.class })).bindTo(jdkUnsafeInstance);
			getFloat = lookup.findVirtual(jdkUnsafe, "getFloat", MethodType.methodType(float.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putFloat = lookup.findVirtual(jdkUnsafe, "putFloat", MethodType.methodType(void.class, Object.class, new Class[] { long.class, float.class })).bindTo(jdkUnsafeInstance);
			getDouble = lookup.findVirtual(jdkUnsafe, "getDouble", MethodType.methodType(double.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putDouble = lookup.findVirtual(jdkUnsafe, "putDouble", MethodType.methodType(void.class, Object.class, new Class[] { long.class, double.class })).bindTo(jdkUnsafeInstance);
			getUncompressedObject = lookup.findVirtual(jdkUnsafe, "getUncompressedObject", MethodType.methodType(Object.class, long.class)).bindTo(jdkUnsafeInstance);
			writeback0 = lookup.findVirtual(jdkUnsafe, "writeback0", MethodType.methodType(void.class, long.class)).bindTo(jdkUnsafeInstance);
			writebackPreSync0 = lookup.findVirtual(jdkUnsafe, "writebackPreSync0", MethodType.methodType(void.class)).bindTo(jdkUnsafeInstance);
			writebackPostSync0 = lookup.findVirtual(jdkUnsafe, "writebackPostSync0", MethodType.methodType(void.class)).bindTo(jdkUnsafeInstance);
			defineClass0 = lookup.findVirtual(jdkUnsafe, "defineClass0", MethodType.methodType(Class.class, String.class, new Class[] { byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class })).bindTo(jdkUnsafeInstance);
			allocateInstance = lookup.findVirtual(jdkUnsafe, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(jdkUnsafeInstance);
			throwException = lookup.findVirtual(jdkUnsafe, "throwException", MethodType.methodType(void.class, Throwable.class)).bindTo(jdkUnsafeInstance);
			compareAndSetReference = lookup.findVirtual(jdkUnsafe, "compareAndSetReference", MethodType.methodType(boolean.class, Object.class, new Class[] { long.class, Object.class, Object.class })).bindTo(jdkUnsafeInstance);
			compareAndExchangeReference = lookup.findVirtual(jdkUnsafe, "compareAndExchangeReference", MethodType.methodType(Object.class, Object.class, new Class[] { long.class, Object.class, Object.class })).bindTo(jdkUnsafeInstance);
			compareAndSetInt = lookup.findVirtual(jdkUnsafe, "compareAndSetInt", MethodType.methodType(boolean.class, Object.class, new Class[] { long.class, int.class, int.class })).bindTo(jdkUnsafeInstance);
			compareAndExchangeInt = lookup.findVirtual(jdkUnsafe, "compareAndExchangeInt", MethodType.methodType(int.class, Object.class, new Class[] { long.class, int.class, int.class })).bindTo(jdkUnsafeInstance);
			compareAndSetLong = lookup.findVirtual(jdkUnsafe, "compareAndSetLong", MethodType.methodType(boolean.class, Object.class, new Class[] { long.class, long.class, long.class })).bindTo(jdkUnsafeInstance);
			compareAndExchangeLong = lookup.findVirtual(jdkUnsafe, "compareAndExchangeLong", MethodType.methodType(long.class, Object.class, new Class[] { long.class, long.class, long.class })).bindTo(jdkUnsafeInstance);
			getReferenceVolatile = lookup.findVirtual(jdkUnsafe, "getReferenceVolatile", MethodType.methodType(Object.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putReferenceVolatile = lookup.findVirtual(jdkUnsafe, "putReferenceVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, Object.class })).bindTo(jdkUnsafeInstance);
			getIntVolatile = lookup.findVirtual(jdkUnsafe, "getIntVolatile", MethodType.methodType(int.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putIntVolatile = lookup.findVirtual(jdkUnsafe, "putIntVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, int.class })).bindTo(jdkUnsafeInstance);
			getBooleanVolatile = lookup.findVirtual(jdkUnsafe, "getBooleanVolatile", MethodType.methodType(boolean.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putBooleanVolatile = lookup.findVirtual(jdkUnsafe, "putBooleanVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, boolean.class })).bindTo(jdkUnsafeInstance);
			getByteVolatile = lookup.findVirtual(jdkUnsafe, "getByteVolatile", MethodType.methodType(byte.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putByteVolatile = lookup.findVirtual(jdkUnsafe, "putByteVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, byte.class })).bindTo(jdkUnsafeInstance);
			getShortVolatile = lookup.findVirtual(jdkUnsafe, "getShortVolatile", MethodType.methodType(short.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putShortVolatile = lookup.findVirtual(jdkUnsafe, "putShortVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, short.class })).bindTo(jdkUnsafeInstance);
			getCharVolatile = lookup.findVirtual(jdkUnsafe, "getCharVolatile", MethodType.methodType(char.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putCharVolatile = lookup.findVirtual(jdkUnsafe, "putCharVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, char.class })).bindTo(jdkUnsafeInstance);
			getLongVolatile = lookup.findVirtual(jdkUnsafe, "getLongVolatile", MethodType.methodType(long.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putLongVolatile = lookup.findVirtual(jdkUnsafe, "putLongVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, long.class })).bindTo(jdkUnsafeInstance);
			getFloatVolatile = lookup.findVirtual(jdkUnsafe, "getFloatVolatile", MethodType.methodType(float.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putFloatVolatile = lookup.findVirtual(jdkUnsafe, "putFloatVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, float.class })).bindTo(jdkUnsafeInstance);
			getDoubleVolatile = lookup.findVirtual(jdkUnsafe, "getDoubleVolatile", MethodType.methodType(double.class, Object.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			putDoubleVolatile = lookup.findVirtual(jdkUnsafe, "putDoubleVolatile", MethodType.methodType(void.class, Object.class, new Class[] { long.class, double.class })).bindTo(jdkUnsafeInstance);
			unpark = lookup.findVirtual(jdkUnsafe, "unpark", MethodType.methodType(void.class, Object.class)).bindTo(jdkUnsafeInstance);
			park = lookup.findVirtual(jdkUnsafe, "park", MethodType.methodType(void.class, boolean.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			loadFence = lookup.findVirtual(jdkUnsafe, "loadFence", MethodType.methodType(void.class)).bindTo(jdkUnsafeInstance);
			storeFence = lookup.findVirtual(jdkUnsafe, "storeFence", MethodType.methodType(void.class)).bindTo(jdkUnsafeInstance);
			fullFence = lookup.findVirtual(jdkUnsafe, "fullFence", MethodType.methodType(void.class)).bindTo(jdkUnsafeInstance);
			allocateMemory0 = lookup.findVirtual(jdkUnsafe, "allocateMemory0", MethodType.methodType(long.class, long.class)).bindTo(jdkUnsafeInstance);
			reallocateMemory0 = lookup.findVirtual(jdkUnsafe, "reallocateMemory0", MethodType.methodType(long.class, long.class, new Class[] { long.class })).bindTo(jdkUnsafeInstance);
			freeMemory0 = lookup.findVirtual(jdkUnsafe, "freeMemory0", MethodType.methodType(void.class, long.class)).bindTo(jdkUnsafeInstance);
			setMemory0 = lookup.findVirtual(jdkUnsafe, "setMemory0", MethodType.methodType(void.class, Object.class, new Class[] { long.class, long.class, byte.class })).bindTo(jdkUnsafeInstance);
			copyMemory0 = lookup.findVirtual(jdkUnsafe, "copyMemory0", MethodType.methodType(void.class, Object.class, new Class[] { long.class, Object.class, long.class, long.class })).bindTo(jdkUnsafeInstance);
			copySwapMemory0 = lookup.findVirtual(jdkUnsafe, "copySwapMemory0", MethodType.methodType(void.class, Object.class, new Class[] { long.class, Object.class, long.class, long.class, long.class })).bindTo(jdkUnsafeInstance);
			objectFieldOffset0 = lookup.findVirtual(jdkUnsafe, "objectFieldOffset0", MethodType.methodType(long.class, Field.class)).bindTo(jdkUnsafeInstance);
			objectFieldOffset1 = lookup.findVirtual(jdkUnsafe, "objectFieldOffset1", MethodType.methodType(long.class, Class.class, new Class[] { String.class })).bindTo(jdkUnsafeInstance);
			staticFieldOffset0 = lookup.findVirtual(jdkUnsafe, "staticFieldOffset0", MethodType.methodType(long.class, Field.class)).bindTo(jdkUnsafeInstance);
			staticFieldBase0 = lookup.findVirtual(jdkUnsafe, "staticFieldBase0", MethodType.methodType(Object.class, Field.class)).bindTo(jdkUnsafeInstance);
			shouldBeInitialized0 = lookup.findVirtual(jdkUnsafe, "shouldBeInitialized0", MethodType.methodType(boolean.class, Class.class)).bindTo(jdkUnsafeInstance);
			ensureClassInitialized0 = lookup.findVirtual(jdkUnsafe, "ensureClassInitialized0", MethodType.methodType(void.class, Class.class)).bindTo(jdkUnsafeInstance);
			arrayBaseOffset0 = lookup.findVirtual(jdkUnsafe, "arrayBaseOffset0", MethodType.methodType(int.class, Class.class)).bindTo(jdkUnsafeInstance);
			arrayIndexScale0 = lookup.findVirtual(jdkUnsafe, "arrayIndexScale0", MethodType.methodType(int.class, Class.class)).bindTo(jdkUnsafeInstance);
			getLoadAverage0 = lookup.findVirtual(jdkUnsafe, "getLoadAverage0", MethodType.methodType(int.class, double[].class, new Class[] { int.class })).bindTo(jdkUnsafeInstance);
		} catch (IllegalAccessException | NoSuchMethodException var5) {
			ReflectiveOperationException e = var5;
			throw new RuntimeException(e);
		}
	}

	private static interface Runner<T> {
		T run() throws Throwable;
	}
}
