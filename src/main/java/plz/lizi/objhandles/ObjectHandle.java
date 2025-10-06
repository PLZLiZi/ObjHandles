package plz.lizi.objhandles;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import plz.lizi.objhandles.HandleBase.Handle;
import plz.lizi.objhandles.api.common.PLZBase;

public final class ObjectHandle implements Handle {
	private Object _container;
	private Field _field;

	public ObjectHandle(Object object) {
		_container = object;
	}

	public ObjectHandle(Object base, String field) {
		_container = base;
		_field = PLZBase.findField(((base instanceof Class cls) ? cls : base.getClass()), field);
	}

	public ObjectHandle(Object base, Field f) {
		_container = base;
		_field = f;
	}

	public ObjectHandle setClass(Class<?> newC) {
		PLZBase.klassPtr(zhis(), newC);
		return this;
	}

	public Object zhis() {
		if (_field == null) {
			return _container;
		}
		return PLZBase.getField(_container, _field);
	}

	public Class<?> zhisClass() {
		return zhis().getClass();
	}

	public ObjectHandle copyOf() throws Throwable {
		return new ObjectHandle(PLZBase.copy(zhis()));
	}

	public ObjectHandle bind(Object container, Field field) {
		_container = container;
		_field = field;
		return this;
	}

	public ObjectHandle bind(String field) {
		String[] path = PLZBase.splitLast(field, ".");
		String clsName = path[0], fieldName = path[1];
		Class<?> cls = PLZBase.findClass(clsName);
		_container = cls;
		_field = PLZBase.findField(cls, fieldName);
		return this;
	}

	public ObjectHandle field(String fname) {
		Object zhis = zhis();
		return new ObjectHandle(zhis, PLZBase.findField(zhis.getClass(), fname));
	}

	public ObjectHandle set(Object value) {
		if (_container != null && _field != null) {
			return new ObjectHandle(PLZBase.setField(_container, _field, value));
		}
		return null;
	}

	public Object invoke(String mname, Object... args) {
		return PLZBase.invoke(zhis(), mname, args);
	}

	@Override
	public String toString() {
		String path = _field == null ? "?" : ((Modifier.isStatic(_field.getModifiers()) ? _field.getDeclaringClass().getName() : "@ " + _container) + "." + _field.getName());
		return "ObjectHandle [ " + path + " = " + zhis() + " ]";
	}
}
