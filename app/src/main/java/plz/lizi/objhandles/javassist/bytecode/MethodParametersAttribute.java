package plz.lizi.objhandles.javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * <code>MethodParameters_attribute</code>.
 */
public class MethodParametersAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"MethodParameters"</code>.
     */
    public static final String tag = "MethodParameters";

    MethodParametersAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Constructs an attribute.
     *
     * @param cp            a constant pool table.
     * @param names         an array of parameter names.
     *                      The i-th element is the name of the i-th parameter.
     * @param flags         an array of parameter access flags.
     */
    public MethodParametersAttribute(ConstPool cp, String[] names, int[] flags) {
        super(cp, tag);
        byte[] data = new byte[names.length * 4 + 1];
        data[0] = (byte)names.length;
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            ByteArray.write16bit(name == null ? 0 : cp.addUtf8Info(name), data, i * 4 + 1);
            ByteArray.write16bit(flags[i], data, i * 4 + 3);
        }

        set(data);
    }

    /**
     * Returns <code>parameters_count</code>, which is the number of
     * parameters.
     */
    public int size() {
        return info[0] & 0xff;
    }

    /**
     * Returns the value of <code>name_index</code> of the i-th element of <code>parameters</code>.
     *
     * @param i         the position of the parameter.
     */
    public int name(int i) {
        return ByteArray.readU16bit(info, i * 4 + 1);
    }

    /**
     * Returns the name of the i-th element of <code>parameters</code>.
     * @param i         the position of the parameter.
     */
    public String parameterName(int i) {
        int index = name(i);
        return index == 0 ? null : getConstPool().getUtf8Info(index);
    }

    /**
     * Returns the value of <code>access_flags</code> of the i-th element of <code>parameters</code>.
     *
     * @param i         the position of the parameter.
     * @see AccessFlag
     */
    public int accessFlags(int i) {
        return ByteArray.readU16bit(info, i * 4 + 3);
    }

    /**
     * Makes a copy.
     *
     * @param newCp     the constant pool table used by the new copy.
     * @param classnames        ignored.
     */
    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String,String> classnames) {
        int s = size();
        ConstPool cp = getConstPool();
        String[] names = new String[s];
        int[] flags = new int[s];
        for (int i = 0; i < s; i++) {
            int index = name(i);
            names[i] = index == 0 ? null : cp.getUtf8Info(index);
            flags[i] = accessFlags(i);
        }

        return new MethodParametersAttribute(newCp, names, flags);
    }
}
