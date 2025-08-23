package plz.lizi.objhandles;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import com.sun.tools.attach.VirtualMachine;
import plz.lizi.objhandles.api.common.PLZBase;

public final class HandleBase {
	public static Instrumentation INST;

	public static void init() {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		try {
			Field f = Class.forName("sun.tools.attach.HotSpotVirtualMachine").getDeclaredField("ALLOW_ATTACH_SELF");
			PLZBase.UNSAFE.putObject(PLZBase.UNSAFE.staticFieldBase(f), PLZBase.UNSAFE.staticFieldOffset(f), true);
			VirtualMachine vm = VirtualMachine.attach(String.valueOf(ProcessHandle.current().pid()));
			vm.loadAgent(PLZBase.getJarPath());
			vm.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void init(Instrumentation inst) {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		INST = inst;
		if (INST == null) {
			INST = new JVMTIInstrumentation();
		}
	}

	public static interface HandleInstance {
		Class<?> getRealClass();
		Object getObject();
	}
}

