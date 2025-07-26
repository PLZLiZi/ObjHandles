package plz.lizi.objhandles;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import plz.lizi.api.common.PLZBase;

public final class ObjectHandle {
	private Object obj;
	private Class<?> objKlass;

	public ObjectHandle(Object o) {
		this.obj = o;
		objKlass = o.getClass();
	}

	public ObjectHandle(String fieldToDump) throws Throwable {
		String[] v = PLZBase.splitLast(fieldToDump, ".");
		obj = new ClassHandle(Class.forName(v[0])).getStaticField(v[1]);
		objKlass = obj.getClass();
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

	public Object getField(String fname) throws Throwable {
		Field f = objKlass.getDeclaredField(fname);
		try {
			return f.get(obj);
		} catch (Exception e) {
			try {
				return PLZBase.LOOKUP.unreflectGetter(f).invoke(obj);
			} catch (Throwable e1) {
				try {
					return PLZBase.UNSAFE.getObject(obj, PLZBase.UNSAFE.objectFieldOffset(f));
				} catch (Exception e2) {
					e2.initCause(e);
					e2.initCause(e1);
					throw e2;
				}
			}
		}
	}

	public void setField(String fname, Object o) throws Throwable {
		Field f = objKlass.getDeclaredField(fname);
		try {
			f.set(obj, o);
		} catch (Exception e) {
			try {
				PLZBase.LOOKUP.unreflectSetter(f).invoke(obj, o);
			} catch (Throwable e1) {
				try {
					PLZBase.UNSAFE.putObject(obj, PLZBase.UNSAFE.objectFieldOffset(f), o);
				} catch (Exception e2) {
					e2.initCause(e);
					e2.initCause(e1);
					throw e2;
				}
			}
		}
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
