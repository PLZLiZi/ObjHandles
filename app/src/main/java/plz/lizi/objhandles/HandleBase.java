package plz.lizi.objhandles;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import com.sun.tools.attach.VirtualMachine;
import plz.lizi.api.common.PLZBase;

public final class HandleBase {
	static Instrumentation INST;

	public static void init() {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		try {
			Field f = Class.forName("sun.tools.attach.HotSpotVirtualMachine").getDeclaredField("ALLOW_ATTACH_SELF");
			PLZBase.UNSAFE.putObject(PLZBase.UNSAFE.staticFieldBase(f), PLZBase.UNSAFE.staticFieldOffset(f), true);
			VirtualMachine vm = VirtualMachine.attach(String.valueOf(ProcessHandle.current().pid()));
			vm.loadAgent(PLZBase.getJarPath());
			vm.detach();
		} catch (Exception e) {
		}
		if (INST == null) {
			System.out.println("[OBJHANDLE] Can't init INST env");
		}
	}

	public static void init(Instrumentation inst) {
		ClassLoader.getSystemClassLoader().getResourceAsStream("java/lang/Object.class");
		INST = inst;
		if (INST == null) {
			INST = new JVMTIInstrumentation();
		}
	}
}

