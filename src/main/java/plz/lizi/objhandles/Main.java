package plz.lizi.objhandles;

import plz.lizi.objhandles.api.common.PLZBase;
import plz.lizi.objhandles.api.javassist.CtMethod;

public class Main {

	public static void main(String[] args) throws Throwable {
		PLZBase.cls();
		HandleBase.init(null);
		new ClassHandle("sun.instrument.InstrumentationImpl").configure(ctClass -> {
			try {
				CtMethod ctMethod = ctClass.getDeclaredMethod("transform");
				ctMethod.setBody("{ return $6; }");
				ctClass.writeFile("D:\\PLZLiZi\\Java\\ObjHandles\\output");
			} catch (Exception e) {
				PLZBase.throwEx(e);
			}
		});
	}
}
