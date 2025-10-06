package plz.lizi.objhandles;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import com.sun.tools.attach.VirtualMachine;
import plz.lizi.objhandles.api.common.PLZBase;

public final class HandleBase {
	private static InstrumentationHandle INST;

	public static void init() {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		try {
			Field f = Class.forName("sun.tools.attach.HotSpotVirtualMachine").getDeclaredField("ALLOW_ATTACH_SELF");
			PLZBase.UNSAFE.putBoolean(PLZBase.UNSAFE.staticFieldBase(f), PLZBase.UNSAFE.staticFieldOffset(f), true);
			VirtualMachine vm = VirtualMachine.attach(String.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
			vm.loadAgent(PLZBase.getJarPath());
			vm.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static InstrumentationHandle getInst() {
		return INST;
	}

	public static void init(Instrumentation inst) {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		if (INST == null) {
			INST = new InstrumentationHandle(new InstrumentationImpl());
		} else {
			INST = new InstrumentationHandle(inst);
		}
	}

	public static interface Handle {
		Class<?> zhisClass();
		Object zhis();
	}
}

