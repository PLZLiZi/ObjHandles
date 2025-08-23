package plz.lizi.objhandles;

import java.util.Arrays;
import com.sun.tools.attach.VirtualMachine;
import plz.lizi.objhandles.api.common.PLZBase;

public class Main {
	public static void main(String[] args) throws Throwable {
		HandleBase.init(null);
		new ClassHandle(Main.class).configure((ctClass) -> {
			System.out.println(Arrays.toString(ctClass.getMethods()));
		});;
	}
}
