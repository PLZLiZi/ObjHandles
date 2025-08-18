package plz.lizi.objhandles;

import com.sun.tools.attach.VirtualMachine;

public class Main {
	public static void main(String[] args) {
		System.out.println(new FieldHandle("sun.tools.attach.HotSpotVirtualMachine.ALLOW_ATTACH_SELF").getObject());
		new FieldHandle("sun.tools.attach.HotSpotVirtualMachine.ALLOW_ATTACH_SELF").set(true);
		System.out.println(new FieldHandle("sun.tools.attach.HotSpotVirtualMachine.ALLOW_ATTACH_SELF").getObject());
		new FieldHandle("sun.tools.attach.HotSpotVirtualMachine.ALLOW_ATTACH_SELF").set(false);
		System.out.println(new FieldHandle("sun.tools.attach.HotSpotVirtualMachine.ALLOW_ATTACH_SELF").getObject());
	}
}
