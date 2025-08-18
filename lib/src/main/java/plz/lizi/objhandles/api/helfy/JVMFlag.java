package plz.lizi.objhandles.api.helfy;

import static plz.lizi.objhandles.api.helfy.JVM.*;
import java.util.NoSuchElementException;

public class JVMFlag extends JVMObject {
	public static final Type TYPE = JVM.type("JVMFlag");
	public static final int SIZE = TYPE.size;
	public static final long FLAGS_ADDRESS = TYPE.global("flags");
	public static final long NUM_FLAGS_ADDRESS = TYPE.global("numFlags");
	public static final long ADDR_OFFSET = TYPE.offset("_addr");
	public static final long NAME_OFFSET = TYPE.offset("_name");
	public static final long FLAGS_OFFSET = TYPE.offset("_flags");
	public static final long TYPE_OFFSET = TYPE.offset("_type");
	private static final String[] type2string = { "bool", "int", "uint", "intx", "uintx", "uint64_t", "size_t", "double", "ccstr", "ccstrlist" };

	public JVMFlag(long addr) {
		super(addr);
	}

	public long getAddress() {
		return UNSAFE.getAddress(this.address + ADDR_OFFSET);
	}

	public void setAddress(long addr) {
		UNSAFE.putAddress(this.address + ADDR_OFFSET, addr);
	}

	public boolean getBool() {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 0) {
				throw new AssertionError("This JVMFlag is not a bool flag");
			}
		}
		return UNSAFE.getByte(this.getAddress()) != 0;
	}

	public void setBool(boolean b) {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 0) {
				throw new AssertionError("This JVMFlag is not a bool flag");
			}
		}
		UNSAFE.putByte(this.getAddress(), (byte) (b ? 1 : 0));
	}

	public int getInt() {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 1) {
				throw new AssertionError("This JVMFlag is not a int flag");
			}
		}
		return UNSAFE.getInt(this.getAddress());
	}

	public void setInt(int i) {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 1) {
				throw new AssertionError("This JVMFlag is not a int flag");
			}
		}
		UNSAFE.putInt(this.getAddress(), i);
	}

	public long getIntx() {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 3) {
				throw new AssertionError("This JVMFlag is not a intx flag");
			}
		}
		return UNSAFE.getAddress(this.getAddress());
	}

	public void setIntx(long l) {
		if (JVM.ENABLE_EXTRA_CHECK) {
			if (this.getType() != 3) {
				throw new AssertionError("This JVMFlag is not a intx flag");
			}
		}
		UNSAFE.putAddress(this.getAddress(), l);
	}

	public String getName() {
		return JVM.getStringRef(this.address + NAME_OFFSET);
	}

	public int getFlags() {
		return UNSAFE.getInt(this.address + FLAGS_OFFSET);
	}

	public void setFlags(int flags) {
		UNSAFE.putInt(this.address + FLAGS_OFFSET, flags);
	}

	public int getType() {
		return UNSAFE.getInt(this.address + TYPE_OFFSET);
	}

	public void setType(int type) {
		UNSAFE.putInt(this.address + TYPE_OFFSET, type);
	}

	public static String type2String(int typeIndex) {
		if (0 <= typeIndex && typeIndex < type2string.length) {
			return type2string[typeIndex];
		} else {
			return "unknown";
		}
	}

	private static JVMFlag[] cache;
	private static long cacheAddr;

	public static JVMFlag[] getAllFlags() {
		int len = getNumFlags() - 1;
		if (cache == null || cache.length != len || cacheAddr != UNSAFE.getAddress(FLAGS_ADDRESS)) {
			cache = new JVMFlag[len];
			long addr = cacheAddr = UNSAFE.getAddress(FLAGS_ADDRESS);
			for (int i = 0; i < len; i++, addr += SIZE) {
				cache[i] = new JVMFlag(addr);
			}
		}
		return cache;
	}

	public static JVMFlag getFlagAt(int index) {
		if (index < 0 || index >= getNumFlags() - 1) {
			throw new NoSuchElementException();
		}
		return new JVMFlag(UNSAFE.getAddress(FLAGS_ADDRESS) + (long) index * SIZE);
	}
	// public static void setFlagsHead(@Nonnull JVMFlag flag){
	// UNSAFE.putAddress(FLAGS_ADDRESS,flag.address);
	// }

	public static int getNumFlags() {
		return (int) UNSAFE.getAddress(NUM_FLAGS_ADDRESS);
	}
	// public static void setNumFlags(int numFlags){
	// UNSAFE.putAddress(NUM_FLAGS_ADDRESS,numFlags);
	// }
}
