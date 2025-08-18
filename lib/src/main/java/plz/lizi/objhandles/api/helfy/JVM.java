package plz.lizi.objhandles.api.helfy;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import sun.misc.Unsafe;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.management.ManagementFactory;
import java.util.*;

public final class JVM {
    public static final Unsafe UNSAFE;
    public static final MethodHandles.Lookup LOOKUP;
    public static final boolean ENABLE_EXTRA_CHECK = true;
    public static final boolean isHotspotJVM;
    public static final boolean isWindows;
    public static final String cpu = PlatformInfo.getCPU();
    private static final NativeLibrary JVM;
    public static final Class<?> nativeLibImpl;
    private static final MethodHandle find;
    private static final MethodHandle load;
    private static final MethodHandle findSymbol;
    private static final VarHandle handleVar;
    private static final java.lang.reflect.Field nameField;
    private static final Map<String, Type> types = new LinkedHashMap<>();
    private static final Map<String, Number> constants = new LinkedHashMap<>();
    private static final Object2LongMap<String> functions = new Object2LongOpenHashMap<>();
    private static final Set<String> jvmciOnlyConstants = new LinkedHashSet<>();
    public static final int oopSize;
    public static final int intSize;
    public static final int longSize;
    public static final int size_tSize;
    public static final int floatSize;
    public static final int doubleSize;
    public static final int unsignedSize;
    public static final boolean includeJVMTI;
    public static final boolean usingClientCompiler;
    public static final boolean usingServerCompiler;
    public static final boolean usingSharedSpaces;
    public static final boolean usingCompressedClassPointers;
    public static final boolean usingCompressedOops;
    public static final boolean usingTLAB;
    public static final boolean includeJVMCI;
    private static final Map<Type, Long> type2vtblMap = new HashMap<>();
    private static final Object2LongOpenHashMap<Type> type2vtbl = new Object2LongOpenHashMap<>();
    public static final int invocationEntryBci;
    public static final long PerMethodRecompilationCutoff;
    public static final int objectAlignmentInBytes;
    public static final int logMinObjAlignmentInBytes;
    public static final int heapOopSize;
    public static final boolean isLP64;
    public static final int LogBytesPerShort = 1;
    public static final int LogBytesPerInt = 2;
    public static final int LogBytesPerWord;
    public static final int LogBytesPerLong = 3;
    public static final int BytesPerShort = 1 << LogBytesPerShort;
    public static final int BytesPerInt = 1 << LogBytesPerInt;
    public static final int BytesPerWord;
    public static final int BytesPerLong = 1 << LogBytesPerLong;
    public static final int LogBitsPerByte = 3;
    public static final int LogBitsPerShort = LogBitsPerByte + LogBytesPerShort;
    public static final int LogBitsPerInt = LogBitsPerByte + LogBytesPerInt;
    public static final int LogBitsPerWord;
    public static final int LogBitsPerLong = LogBitsPerByte + LogBytesPerLong;
    public static final int BitsPerByte = 1 << LogBitsPerByte;
    public static final int BitsPerShort = 1 << LogBitsPerShort;
    public static final int BitsPerInt = 1 << LogBitsPerInt;
    public static final int BitsPerWord;
    public static final int BitsPerLong = 1 << LogBitsPerLong;
    public static final int WordAlignmentMask;
    public static final int LongAlignmentMask = (1 << LogBytesPerLong) - 1;
    public static final int LogHeapWordSize;
    public static final int HeapWordsPerLong;
    public static final int LogHeapWordsPerLong;
    public static final boolean restrictContended;
    public static final boolean enableContended;
    public static final boolean restrictReservedStack;
    public static final int diagnoseSyncOnValueBasedClasses;
    public static final boolean dumpSharedSpaces;
    public static final boolean bytecodeVerificationLocal;
    public static final boolean bytecodeVerificationRemote;
    public static final int wordSize;
    public static final boolean includeCDS;
    public static final boolean includeG1GC;
    public static final boolean includeCDSJavaHeap;
    public static final boolean includeJFR;
    public static final boolean classUnloading;
    public static final boolean product;
    public static final long codeEntryAlignment;
    public static final boolean includeAssert;
    public static final boolean usePerfData;
    public static final boolean specialAlignment;

    private JVM() {}

    private static Map<String, Set<Field>> readVmStructs() {
        long entry = getSymbol("gHotSpotVMStructs");
        long typeNameOffset = getSymbol("gHotSpotVMStructEntryTypeNameOffset");
        long fieldNameOffset = getSymbol("gHotSpotVMStructEntryFieldNameOffset");
        long typeStringOffset = getSymbol("gHotSpotVMStructEntryTypeStringOffset");
        long isStaticOffset = getSymbol("gHotSpotVMStructEntryIsStaticOffset");
        long offsetOffset = getSymbol("gHotSpotVMStructEntryOffsetOffset");
        long addressOffset = getSymbol("gHotSpotVMStructEntryAddressOffset");
        long arrayStride = getSymbol("gHotSpotVMStructEntryArrayStride");
        Map<String, Set<Field>> structs = new HashMap<>();
        for (;; entry += arrayStride) {
            String typeName = getStringRef(entry + typeNameOffset);
            String fieldName = getStringRef(entry + fieldNameOffset);
            if (fieldName == null)
                break;
            String typeString = getStringRef(entry + typeStringOffset);
            boolean isStatic = UNSAFE.getInt(entry + isStaticOffset) != 0;
            long offset = UNSAFE.getLong(entry + (isStatic ? addressOffset : offsetOffset));
            Set<Field> fields = structs.computeIfAbsent(typeName, k -> new TreeSet<>());
            fields.add(new Field(fieldName, typeString, offset, isStatic));
        }
        return structs;
    }

    private static Map<String, Set<Field>> readVmCIStructs() {
        long entry = getSymbol("jvmciHotSpotVMStructs");
        long typeNameOffset = getSymbol("gHotSpotVMStructEntryTypeNameOffset");
        long fieldNameOffset = getSymbol("gHotSpotVMStructEntryFieldNameOffset");
        long typeStringOffset = getSymbol("gHotSpotVMStructEntryTypeStringOffset");
        long isStaticOffset = getSymbol("gHotSpotVMStructEntryIsStaticOffset");
        long offsetOffset = getSymbol("gHotSpotVMStructEntryOffsetOffset");
        long addressOffset = getSymbol("gHotSpotVMStructEntryAddressOffset");
        long arrayStride = getSymbol("gHotSpotVMStructEntryArrayStride");
        Map<String, Set<Field>> structs = new HashMap<>();
        for (;; entry += arrayStride) {
            String typeName = getStringRef(entry + typeNameOffset);
            String fieldName = getStringRef(entry + fieldNameOffset);
            if (fieldName == null)
                break;
            String typeString = getStringRef(entry + typeStringOffset);
            boolean isStatic = UNSAFE.getInt(entry + isStaticOffset) != 0;
            long offset = UNSAFE.getLong(entry + (isStatic ? addressOffset : offsetOffset));
            Set<Field> fields = structs.computeIfAbsent(typeName, k -> new TreeSet<>());
            fields.add(new Field(fieldName, typeString, offset, isStatic, true));
        }
        return structs;
    }

    private static void readVmTypes(Map<String, Set<Field>> structs) {
        long entry = getSymbol("gHotSpotVMTypes");
        long typeNameOffset = getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
        long superclassNameOffset = getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
        long isOopTypeOffset = getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
        long isIntegerTypeOffset = getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
        long isUnsignedOffset = getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
        long sizeOffset = getSymbol("gHotSpotVMTypeEntrySizeOffset");
        long arrayStride = getSymbol("gHotSpotVMTypeEntryArrayStride");
        for (;; entry += arrayStride) {
            String typeName = getStringRef(entry + typeNameOffset);
            if (typeName == null)
                break;
            String superclassName = getStringRef(entry + superclassNameOffset);
            boolean isOop = UNSAFE.getInt(entry + isOopTypeOffset) != 0;
            boolean isInt = UNSAFE.getInt(entry + isIntegerTypeOffset) != 0;
            boolean isUnsigned = UNSAFE.getInt(entry + isUnsignedOffset) != 0;
            int size = UNSAFE.getInt(entry + sizeOffset);
            Set<Field> fields = structs.get(typeName);
            types.put(typeName, new Type(typeName, superclassName, size, isOop, isInt, isUnsigned, fields));
        }
    }

    private static void readVmCITypes(Map<String, Set<Field>> structs) {
        long entry = getSymbol("jvmciHotSpotVMTypes");
        long typeNameOffset = getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
        long superclassNameOffset = getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
        long isOopTypeOffset = getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
        long isIntegerTypeOffset = getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
        long isUnsignedOffset = getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
        long sizeOffset = getSymbol("gHotSpotVMTypeEntrySizeOffset");
        long arrayStride = getSymbol("gHotSpotVMTypeEntryArrayStride");
        for (Map.Entry<String, Set<Field>> entry1 : structs.entrySet()) {
            if (!types.containsKey(entry1.getKey())) {
                types.put(entry1.getKey(), new Type.UnknownType(entry1.getKey(), entry1.getValue()));
            } else {
                types.compute(entry1.getKey(), (k, old) -> new Type(old.name, old.superName, old.size, old.isOop, old.isInt, old.isUnsigned, updateOrCreateFields(entry1.getValue(), old)));
            }
        }
        for (;; entry += arrayStride) {
            String typeName = getStringRef(entry + typeNameOffset);
            if (typeName == null)
                break;
            String superclassName = getStringRef(entry + superclassNameOffset);
            boolean isOop = UNSAFE.getInt(entry + isOopTypeOffset) != 0;
            boolean isInt = UNSAFE.getInt(entry + isIntegerTypeOffset) != 0;
            boolean isUnsigned = UNSAFE.getInt(entry + isUnsignedOffset) != 0;
            int size = UNSAFE.getInt(entry + sizeOffset);
            Set<Field> fields = updateOrCreateFields(structs.get(typeName), types.get(typeName));
            types.put(typeName, new Type(typeName, superclassName, size, isOop, isInt, isUnsigned, fields));
        }
    }

    private static Set<Field> updateOrCreateFields(Set<Field> newFields, Type oldType) {
        if (oldType != null) {
            TreeSet<Field> re = new TreeSet<>(List.of(oldType.fields));
            if (newFields != null) {
                re.addAll(newFields);
            }
            return re;
        }
        return newFields;
    }

    private static void readVmIntConstants() {
        long entry = getSymbol("gHotSpotVMIntConstants");
        long nameOffset = getSymbol("gHotSpotVMIntConstantEntryNameOffset");
        long valueOffset = getSymbol("gHotSpotVMIntConstantEntryValueOffset");
        long arrayStride = getSymbol("gHotSpotVMIntConstantEntryArrayStride");
        for (;; entry += arrayStride) {
            String name = getStringRef(entry + nameOffset);
            if (name == null)
                break;
            int value = UNSAFE.getInt(entry + valueOffset);
            constants.put(name, value);
        }
    }

    private static void readVmCIIntConstants() {
        long entry = getSymbol("jvmciHotSpotVMIntConstants");
        long nameOffset = getSymbol("gHotSpotVMIntConstantEntryNameOffset");
        long valueOffset = getSymbol("gHotSpotVMIntConstantEntryValueOffset");
        long arrayStride = getSymbol("gHotSpotVMIntConstantEntryArrayStride");
        for (;; entry += arrayStride) {
            String name = getStringRef(entry + nameOffset);
            if (name == null)
                break;
            int value = UNSAFE.getInt(entry + valueOffset);
            if (!constants.containsKey(name)) {
                constants.put(name, value);
                jvmciOnlyConstants.add(name);
            }
        }
    }

    private static void readVmLongConstants() {
        long entry = getSymbol("gHotSpotVMLongConstants");
        long nameOffset = getSymbol("gHotSpotVMLongConstantEntryNameOffset");
        long valueOffset = getSymbol("gHotSpotVMLongConstantEntryValueOffset");
        long arrayStride = getSymbol("gHotSpotVMLongConstantEntryArrayStride");
        for (;; entry += arrayStride) {
            String name = getStringRef(entry + nameOffset);
            if (name == null)
                break;
            long value = UNSAFE.getLong(entry + valueOffset);
            constants.put(name, value);
        }
    }

    private static void readVmCILongConstants() {
        long entry = getSymbol("jvmciHotSpotVMLongConstants");
        long nameOffset = getSymbol("gHotSpotVMLongConstantEntryNameOffset");
        long valueOffset = getSymbol("gHotSpotVMLongConstantEntryValueOffset");
        long arrayStride = getSymbol("gHotSpotVMLongConstantEntryArrayStride");
        for (;; entry += arrayStride) {
            String name = getStringRef(entry + nameOffset);
            if (name == null)
                break;
            long value = UNSAFE.getLong(entry + valueOffset);
            if (!constants.containsKey(name)) {
                constants.put(name, value);
                jvmciOnlyConstants.add(name);
            }
        }
    }

    private static void readVmCIAddresses() {
        long entry = getSymbol("jvmciHotSpotVMAddresses");
        long nameOffset = 0;
        long valueOffset = oopSize;
        long arrayStride = 2L * oopSize;
        for (;; entry += arrayStride) {
            String name = getStringRef(entry + nameOffset);
            if (name == null)
                break;
            if (!functions.containsKey(name)) {
                long value = UNSAFE.getAddress(entry + valueOffset);
                functions.put(name, value);
            }
        }
    }

    public static String getString(long addr) {
        if (addr == 0) {
            return null;
        }
        char[] chars = new char[40];
        int offset = 0;
        for (byte b; (b = UNSAFE.getByte(addr + offset)) != 0;) {
            if (offset >= chars.length)
                chars = Arrays.copyOf(chars, offset * 2);
            chars[offset++] = (char) b;
        }
        return new String(chars, 0, offset);
    }

    public static String getStringRef(long addr) {
        return getString(UNSAFE.getAddress(addr));
    }

    public static void putStringRef(long addr, String str) {
        byte[] bytes = str.getBytes();
        int len = bytes.length;
        long base = UNSAFE.allocateMemory(len + 1);
        for (int i = 0; i < len; i++) {
            UNSAFE.putByte(base + i, bytes[i]);
        }
        UNSAFE.putByte(base + len, (byte) 0);
        UNSAFE.putAddress(addr, base);
    }

    public static long getSymbol(String name) {
        long address = JVM.lookup(name);
        if (address == 0) {
            throw new NoSuchElementException("No such symbol: " + name);
        }
        return UNSAFE.getLong(address);
    }

    public static long lookupSymbol(String name) {
        return JVM.lookup(name);
    }

    public static Type type(String name) {
        if (!isHotspotJVM) {
            return Type.EMPTY;
        }
        Type type = types.get(name);
        if (type == null) {
            throw new NoSuchElementException("No such type: " + name);
        }
        return type;
    }

    public static Number constant(String name) {
        if (!isHotspotJVM) {
            return 0;
        }
        Number constant = constants.get(name);
        if (constant == null) {
            throw new NoSuchElementException("No such constant: " + name);
        }
        return constant;
    }

    public static int intConstant(String name) {
        return constant(name).intValue();
    }

    public static long longConstant(String name) {
        return constant(name).longValue();
    }

    public static long function(String name) {
        return includeJVMCI ? functions.getLong(name) : 0;
    }

    public static void printAllTypes() {
        if (!isHotspotJVM) {
            System.err.println("NO TYPES");
        }
        for (Type type : types.values()) {
            System.err.println(type);
        }
    }

    public static void printAllConstants() {
        if (!isHotspotJVM) {
            System.err.println("NO CONSTANTS");
        }
        for (Map.Entry<String, Number> type : constants.entrySet()) {
            System.err.println((jvmciOnlyConstants.contains(type.getKey()) ? "(JVMCI)" : "") + type.getKey() + (type.getValue() instanceof Long ? "(L)" : "(I)") + "=" + type.getValue());
        }
    }

    public static void printAllVTBL() {
        if (!isHotspotJVM || !isWindows) {
            System.err.println("Unsupported");
            return;
        }
        for (Type type : types.values()) {
            long vtbl = vtblForType(type);
            if (vtbl != 0L) {
                System.err.println("vtbl(" + type.name + "):0x" + Long.toHexString(vtbl));
            }
        }
    }

    public static void printAllFunctions() {
        if (!isHotspotJVM) {
            System.err.println("NO FUNCTIONS");
        }
        for (Object2LongMap.Entry<String> entry : functions.object2LongEntrySet()) {
            System.err.println("&" + entry.getKey() + " = 0x" + Long.toHexString(entry.getLongValue()));
        }
        System.err.println("over");
    }

    public static void putCLevelLong(long address, long val) {
        if (longSize == 4) {
            UNSAFE.putInt(address, (int) val);
        } else {
            UNSAFE.putLong(address, val);
        }
    }

    public static long getCLevelLong(long address) {
        if (longSize == 4) {
            return UNSAFE.getInt(address);
        } else {
            return UNSAFE.getLong(address);
        }
    }

    public static void putSizeT(long address, long val) {
        if (size_tSize == 4) {
            UNSAFE.putInt(address, (int) val);
        } else {
            UNSAFE.putLong(address, val);
        }
    }

    public static long getSizeT(long address) {
        if (size_tSize == 4) {
            return UNSAFE.getInt(address);
        } else {
            return UNSAFE.getLong(address);
        }
    }

    private static final String vt;

    public static String vtblSymbolForType(Type type) {
        if (!isWindows && vt == null) {
            throw new IllegalStateException("Unsupported OS");
        }
        return isWindows ? "??_7" + type.name + "@@6B@" : vt + type.name.length() + type.name;
    }

    public static long getVtblForType(Type type) {
        if (type == null || (!isWindows && vt == null)) {
            return 0L;
        } else {
            if (type2vtblMap.containsKey(type)) {
                return type2vtblMap.get(type);
            } else {
                String vtblSymbol = vtblSymbolForType(type);
                if (vtblSymbol == null) {
                    type2vtblMap.put(type, 0L);
                    return 0L;
                }
                try {
                    long addr = JVM.lookup(vtblSymbol);
                    if (addr != 0L) {
                        type2vtblMap.put(type, addr);
                        return addr;
                    }
                    type2vtblMap.put(type, 0L);
                    return 0;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    type2vtblMap.put(type, 0L);
                    return 0;
                }
            }
        }
    }

    public static long vtblForType(Type type) {
        if (!type2vtbl.containsKey(type)) {
            long vtblAddr = type2vtbl.getLong(type);
            if (vtblAddr == 0) {
                vtblAddr = getVtblForType(type);
                if (vtblAddr != 0) {
                    type2vtbl.put(type, vtblAddr);
                }
            }
            return vtblAddr;
        }
        return type2vtbl.getLong(type);
    }

    public static long alignUp(long size, long alignment) {
        return size + alignment - 1 & -alignment;
    }

    public static long alignObjectSize(long size) {
        return alignUp(size, objectAlignmentInBytes);
    }

    public static long alignDown(long size, long alignment) {
        return size & -alignment;
    }

    public static long align_metadata_size(long size) {
        return alignUp(size, 1);
    }

    public static int nthBit(int n) {
        return n >= BitsPerWord ? 0 : 1 << n;
    }

    public static int right_n_bits(int n) {
        return nthBit(n) - 1;
    }

    public static char charAt(String s, int i) {
        if (i == s.length()) {
            return '\0';
        }
        return s.charAt(i);
    }

    /**
     * @param alignment 对于{@code struct}类型：其成员的对齐要求的最大值。 对于基本类型：其占据空间大小。
     * @param originalOffset 不进行内存对齐时的原始偏移量（按{@code byte}计算）
     */
    public static long computeOffset(long alignment, long originalOffset) {
        return (originalOffset + alignment - 1) / alignment * alignment;
    }

    public static long[] computeOffsets(boolean has_vtbl_pointer, long[] alignments, long[] sizes) {
        if (alignments.length != sizes.length) {
            throw new IllegalArgumentException("alignments.length!=sizes.length");
        }
        long[] re = new long[sizes.length + (has_vtbl_pointer ? 1 : 0)];
        int offset = has_vtbl_pointer ? 1 : 0;
        if (has_vtbl_pointer) {
            re[0] = oopSize;
        }
        for (int i = 0; i < sizes.length; i++) {
            re[i + offset] = computeOffset(alignments[i], (i + offset > 0 ? re[i + offset - 1] : 0) + (i > 0 ? sizes[i - 1] : 0));
        }
        return has_vtbl_pointer ? Arrays.copyOfRange(re, 1, re.length) : re;
    }

    public static void assertOffset(long expectation, long actuality) {
        if (expectation != actuality) {
            throw new AssertionError("Unexpected offset! expectation: " + expectation + " actuality: " + actuality);
        }
    }

    public static int extract_low_short_from_int(int x) {
        return x & 0xffff;
    }

    public static int extract_high_short_from_int(int x) {
        return (x >> 16) & 0xffff;
    }

    public static int build_int_from_shorts(int low, int high) {
        return (((high & 0xffff) << 16) | (low & 0xffff));
    }

    private static final HashMap<String, JVMFlag> flagsCache = new HashMap<>();

    public static JVMFlag getFlag(String str) {
        return flagsCache.computeIfAbsent(str, (name) -> {
            for (JVMFlag flag : JVMFlag.getAllFlags()) {
                if (flag.getName().equals(name)) {
                    return flag;
                }
            }
            return null;
        });
    }

    public static NativeLibrary findJvm() {
        if (!isHotspotJVM) {
            throw new RuntimeException("Unsupported JVM!");
        }
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return create("jvm.dll");
            } else {
                try {
                    return create("libjvm.so");
                } catch (UnsatisfiedLinkError e) {
                    return create("libjvm.dylib");
                }
            }
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("JVM library not found", e);
        }
    }

    private static NativeLibrary create(String name) {
        try {
            Object nl = UNSAFE.allocateInstance(nativeLibImpl);
            UNSAFE.putObjectVolatile(nl, UNSAFE.objectFieldOffset(nameField), name);
            try {
                load.invoke(nl, name, false, false, true);
            } catch (UnsatisfiedLinkError t) {
                throw t;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
            long handle = (long) handleVar.get(nl);
            return new NativeLibrary() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public long lookup(String entry) {
                    try {
                        long x = (long) find.invoke(nl, entry);
                        if (x == 0) {
                            try {
                                return (long) findSymbol.invoke(handle, entry);
                            } catch (UnsatisfiedLinkError e) {
                                return 0;
                            }
                        }
                        return x;
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                }
            };
        } catch (InstantiationException t) {
            throw new RuntimeException(t);
        }
    }

    static {
        try {
            boolean _isHotspotJVM = false;
            try {
                com.sun.jna.NativeLibrary.getProcess().getGlobalVariableAddress("gHotSpotVMTypes");
                _isHotspotJVM = true;
            } catch (UnsatisfiedLinkError t) {
            }
            isHotspotJVM = _isHotspotJVM;
            isWindows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
            var constructor = Unsafe.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            UNSAFE = (Unsafe) constructor.newInstance();
            var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            var _LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field));
            LOOKUP = (MethodHandles.Lookup) _LOOKUP.findConstructor(MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, Class.class, int.class)).invoke(Object.class, null, -1);
            nativeLibImpl = Class.forName("jdk.internal.loader.NativeLibraries$NativeLibraryImpl", true, null);
            load = LOOKUP.findStatic(Class.forName("jdk.internal.loader.NativeLibraries", true, null), "load", MethodType.methodType(boolean.class, nativeLibImpl, String.class, boolean.class, boolean.class, boolean.class));
            find = LOOKUP.findStatic(Class.forName("jdk.internal.loader.NativeLibraries", true, null), "findEntry0", MethodType.methodType(long.class, nativeLibImpl, String.class));
            nameField = nativeLibImpl.getDeclaredField("name");
            handleVar = LOOKUP.findVarHandle(nativeLibImpl, "handle", long.class);
            findSymbol = LOOKUP.findStatic(com.sun.jna.Native.class, "findSymbol", MethodType.methodType(long.class, long.class, String.class));
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
        try {
            if (isHotspotJVM) {
                JVM = findJvm();
                readVmTypes(readVmStructs());
                readVmIntConstants();
                readVmLongConstants();
                includeJVMCI = intConstant("INCLUDE_JVMCI") == 1 && lookupSymbol("jvmciHotSpotVMTypes") != 0L;
                oopSize = intConstant("oopSize");
                if (includeJVMCI) {
                    readVmCITypes(readVmCIStructs());
                    readVmCIIntConstants();
                    readVmCILongConstants();
                    readVmCIAddresses();
                }
                intSize = type("int").size;
                longSize = type("long").size;
                size_tSize = type("size_t").size;
                floatSize = type("jfloat").size;
                doubleSize = type("jdouble").size;
                unsignedSize = type("unsigned").size;
                includeJVMTI = type("InstanceKlass").contains("_breakpoints");
                Type type = type("Method");
                if (type.contains("_from_compiled_entry")) {
                    if (types.containsKey("Matcher")) {
                        usingServerCompiler = true;
                        usingClientCompiler = false;
                    } else {
                        usingClientCompiler = true;
                        usingServerCompiler = false;
                    }
                } else {
                    usingClientCompiler = usingServerCompiler = false;
                }
                if (!isWindows) {
                    if (lookupSymbol("__vt_10JavaThread") != 0L) {
                        vt = "__vt_";
                    } else if (lookupSymbol("_vt_10JavaThread") != 0L) {
                        vt = "_vt_";
                    } else if (lookupSymbol("_ZTV10JavaThread") != 0L) {
                        vt = "_ZTV";
                    } else {
                        vt = null;
                    }
                } else {
                    vt = null;
                }
                usingSharedSpaces = getFlag("UseSharedSpaces").getBool();
                usingCompressedOops = getFlag("UseCompressedOops").getBool();
                usingCompressedClassPointers = getFlag("UseCompressedClassPointers").getBool();
                usingTLAB = getFlag("UseTLAB").getBool();
                invocationEntryBci = intConstant("InvocationEntryBci");
                PerMethodRecompilationCutoff = getFlag("PerMethodRecompilationCutoff").getIntx();
                objectAlignmentInBytes = (int) getFlag("ObjectAlignmentInBytes").getIntx();
                logMinObjAlignmentInBytes = Integer.numberOfTrailingZeros(objectAlignmentInBytes);
                if (usingCompressedOops) {
                    heapOopSize = intSize;
                } else {
                    heapOopSize = oopSize;
                }
                LogBytesPerWord = intConstant("LogBytesPerWord");
                isLP64 = LogBytesPerWord == 3;
                BytesPerWord = intConstant("BytesPerWord");
                LogBitsPerWord = LogBitsPerByte + LogBytesPerWord;
                BitsPerWord = 1 << LogBitsPerWord;
                WordAlignmentMask = (1 << LogBytesPerWord) - 1;
                LogHeapWordSize = intConstant("LogHeapWordSize");
                LogHeapWordsPerLong = LogBytesPerLong - LogHeapWordSize;
                HeapWordsPerLong = BytesPerLong / oopSize;
                restrictContended = getFlag("RestrictContended").getBool();
                restrictReservedStack = getFlag("RestrictReservedStack").getBool();
                enableContended = getFlag("EnableContended").getBool();
                diagnoseSyncOnValueBasedClasses = (int) getFlag("DiagnoseSyncOnValueBasedClasses").getIntx();
                dumpSharedSpaces = getFlag("DumpSharedSpaces").getBool();
                bytecodeVerificationRemote = getFlag("BytecodeVerificationRemote").getBool();
                bytecodeVerificationLocal = getFlag("BytecodeVerificationLocal").getBool();
                wordSize = type("char*").size;
                includeCDS = types.containsKey("FileMapInfo");
                includeG1GC = types.containsKey("G1CollectedHeap");
                includeCDSJavaHeap = includeCDS && includeG1GC && isLP64 && !PlatformInfo.getOS().equals("win32");
                includeJFR = getFlag("FlightRecorder") != null;
                classUnloading = getFlag("ClassUnloading").getBool();
                {
                    product = computeOffset(oopSize, includeJFR ? type.offset("_flags") + 4 : type.offset("_flags") + 2) == type.offset("_i2i_entry");
                }
                codeEntryAlignment = getFlag("CodeEntryAlignment").getIntx();
                includeAssert = intConstant("ConstantPool::CPCACHE_INDEX_TAG") != 0;
                usePerfData = getFlag("UsePerfData").getBool();
                specialAlignment = type("Arguments").global("_num_jvm_flags") < type("Arguments").global("_jvm_flags_array");
            } else {
                vt = null;
                isLP64 = "aarch64".equals(cpu) || "amd64".equals(cpu) || "x86_64".equals(cpu) || "ppc64".equals(cpu);
                JVM = null;
                codeEntryAlignment = 0;
                LogHeapWordsPerLong = LogHeapWordSize = WordAlignmentMask = BitsPerWord = LogBitsPerWord = BytesPerWord = LogBytesPerWord = unsignedSize = floatSize = doubleSize = diagnoseSyncOnValueBasedClasses = logMinObjAlignmentInBytes = objectAlignmentInBytes = intSize = size_tSize = oopSize = longSize = 0;
                specialAlignment = usePerfData = includeAssert = product = classUnloading = includeJFR = includeCDSJavaHeap = includeCDS = bytecodeVerificationRemote = bytecodeVerificationLocal = dumpSharedSpaces = includeG1GC = enableContended = restrictReservedStack = restrictContended = includeJVMTI = usingClientCompiler = usingServerCompiler = usingSharedSpaces = usingTLAB = includeJVMCI = false;
                usingCompressedOops = Unsafe.ARRAY_OBJECT_INDEX_SCALE == 4;
                HeapWordsPerLong = BytesPerLong / UNSAFE.addressSize();
                if (usingCompressedOops) {
                    heapOopSize = 4;
                } else {
                    heapOopSize = UNSAFE.addressSize();
                }
                boolean flag = true;
                for (String s : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                    if (s.contains("-UseCompressedClassPointers")) {
                        flag = false;
                        break;
                    }
                }
                usingCompressedClassPointers = flag;
                invocationEntryBci = -1;
                PerMethodRecompilationCutoff = -1;
                wordSize = oopSize;
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public static long pointerDelta(long left, long right, long element_size) {
        if (left < right) {
            throw new IllegalArgumentException("avoid underflow - left: 0x" + Long.toHexString(left) + " right: 0x" + Long.toHexString(right));
        }
        return ((left) - (right)) / element_size;
    }

    public static long pointerDeltaHeapWord(long left, long right) {
        return pointerDelta(left, right, oopSize);
    }

    public static long pointerDeltaMetaWord(long left, long right) {
        return pointerDelta(left, right, oopSize);
    }
}
