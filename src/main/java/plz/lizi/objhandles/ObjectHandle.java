package plz.lizi.objhandles;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import plz.lizi.objhandles.HandleBase.HandleInstance;
import plz.lizi.objhandles.api.common.PLZBase;

public final class ObjectHandle implements HandleInstance {
	private Object obj;
	private Class<?> objKlass;

	public ObjectHandle(Object o) {
		obj = o;
		objKlass = o.getClass();
	}

	public ObjectHandle setObjectClass(Class<?> newC) {
		PLZBase.klassPtr(obj, newC);
		objKlass = newC;
		return this;
	}

	public Object getObject() {
		return obj;
	}

	public ObjectHandle setObject(Object next) {
		return new ObjectHandle(next);
	}

	public Class<?> getRealClass() {
		return obj.getClass();
	}

	public Class<?> getHandleClass() {
		return objKlass;
	}

	public Object copyOf() throws Throwable {
		return PLZBase.copy(obj);
	}

	public FieldHandle getField(String fname) {
		return new FieldHandle(this, fname);
	}

	public void setField(String fname, Object o) throws Throwable {
		new FieldHandle(this, fname).set(o);
	}

	public Object invokeMethod(String mname, MethodType type, Object... args) throws Throwable {
		Method m = objKlass.getDeclaredMethod(mname, type.parameterArray());
		try {
			return m.invoke(obj, args);
		} catch (Exception e) {
			try {
				return PLZBase.LOOKUP.unreflect(m).invoke(obj, args);
			} catch (Throwable e1) {
				e1.initCause(e);
				throw e1;
			}
		}
	}
}
