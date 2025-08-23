package plz.lizi.objhandles;

import java.lang.reflect.Field;
import plz.lizi.objhandles.HandleBase.HandleInstance;
import plz.lizi.objhandles.api.common.PLZBase;

public class FieldHandle implements HandleInstance {
	private Field field;
	private HandleInstance handle;

	public FieldHandle(ObjectHandle objHandle, String fieldName) {
		this.field = getFieldRef(objHandle.getRealClass(), fieldName);
		this.handle = objHandle;
	}

	public FieldHandle(ClassHandle clsHandle, String fieldName) {
		this.field = getFieldRef(clsHandle.getRealClass(), fieldName);
		this.handle = clsHandle;
	}

	public FieldHandle(String fieldPath) {
		String[] paths = PLZBase.splitLast(fieldPath, ".");
		this.handle = new ClassHandle(paths[0]);
		this.field = getFieldRef(this.handle.getRealClass(), paths[1]);
	}

	public FieldHandle field(String fname) {
		return new FieldHandle(new ObjectHandle(getObject()), fname);
	}

	public Object getObject() {
		try {
			field.setAccessible(true);
			return field.get(handle.getObject());
		} catch (Exception e) {
			try {
				if (handle.getObject() == null) {
					return PLZBase.LOOKUP.unreflectGetter(field).invoke();
				}
				return PLZBase.LOOKUP.unreflectGetter(field).invoke(handle.getObject() == null ? new Object[] {} : handle.getObject());
			} catch (Throwable e1) {
				try {
					Object base = handle.getObject() == null ? PLZBase.UNSAFE.staticFieldBase(field) : handle.getObject();
					long offset = handle.getObject() == null ? PLZBase.UNSAFE.staticFieldOffset(field) : PLZBase.UNSAFE.objectFieldOffset(field);
					return PLZBase.UNSAFE.getObject(base, offset);
				} catch (Exception e2) {
					throw e2;
				}
			}
		}
	}

	public void set(Object value) {
		try {
			field.setAccessible(true);
			field.set(handle.getObject(), value);
		} catch (Exception e) {
			try {
				if (handle.getObject() == null) {
					PLZBase.LOOKUP.unreflectSetter(field).invoke(value);
				} else {
					PLZBase.LOOKUP.unreflectSetter(field).invoke(handle.getObject(), value);
				}
			} catch (Throwable e1) {
				try {
					Object base = handle.getObject() == null ? PLZBase.UNSAFE.staticFieldBase(field) : handle.getObject();
					long offset = handle.getObject() == null ? PLZBase.UNSAFE.staticFieldOffset(field) : PLZBase.UNSAFE.objectFieldOffset(field);
					PLZBase.UNSAFE.putObject(base, offset, value);
				} catch (Exception e2) {
					throw e2;
				}
			}
		}
	}

	public Class<?> getRealClass() {
		return getObject().getClass();
	}

	public static Field getFieldRef(Class<?> klass, String fname) {
		Class<?> currentClass = klass;
		while (currentClass != null) {
			try {
				return currentClass.getDeclaredField(fname);
			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass();
			}
		}
		return null;
	}
}
