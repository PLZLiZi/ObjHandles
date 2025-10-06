package plz.lizi.objhandles;

import plz.lizi.objhandles.api.common.PLZBase;

public class Main {
	public static class A {
		private boolean isRemove = false;

		public float getHealth() {
			return 20.0F;
		}

		public void setRemoved() {
			isRemove = true;
		}
		
		public boolean isRemoved() {
			return isRemove;
		}
	}

	public static class B extends A {
		@Override
		public float getHealth() {
			return 114514.0F;
		}

		@Override
		public boolean isRemoved() {
			return false;
		}
	}

	public static void main(String[] args) throws Throwable {
		PLZBase.cls();
		HandleBase.init(null);
		A a = new A();
		System.out.println("Health : " + a.getHealth());
		System.out.println("Removed : " + a.isRemoved());
		new ClassHandle(a.getClass()).configure(ctClass -> {
			try {
				ctClass.getDeclaredMethod("setRemoved").setBody("{}");
			} catch (Throwable e) {
				PLZBase.throwEx(e);
			}
		});
		//new ObjectHandle(a).setClass(B.class);
		a.setRemoved();
		System.out.println("Health : " + a.getHealth());
		System.out.println("Removed : " + a.isRemoved());
	}
}
