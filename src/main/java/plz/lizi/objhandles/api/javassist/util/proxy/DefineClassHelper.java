/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package plz.lizi.objhandles.api.javassist.util.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;
import plz.lizi.objhandles.api.javassist.CannotCompileException;
import plz.lizi.objhandles.api.javassist.bytecode.ClassFile;

/**
 * Helper class for invoking {@link ClassLoader#defineClass(String,byte[],int,int)}.
 *
 * @since 3.22
 */
public class DefineClassHelper {

    /**
     * Loads a class file by a given class loader.
     *
     * <p>This first tries to use {@code java.lang.invoke.MethodHandle} to load a class.
     * Otherwise, or if {@code neighbor} is null,
     * this tries to use {@code sun.misc.Unsafe} to load a class.
     * Then it tries to use a {@code protected} method in {@code java.lang.ClassLoader}
     * via {@code PrivilegedAction}.  Since the latter approach is not available
     * any longer by default in Java 9 or later, the JVM argument
     * {@code --add-opens java.base/java.lang=ALL-UNNAMED} must be given to the JVM.
     * If this JVM argument cannot be given, {@link #toPublicClass(String,byte[])}
     * should be used instead.
     * </p>
     *
     * @param className     the name of the loaded class.
     * @param neighbor      the class contained in the same package as the loaded class.
     * @param loader        the class loader.  It can be null if {@code neighbor} is not null
     *                      and the JVM is Java 11 or later.
     * @param domain        if it is null, a default domain is used.
     * @param bcode         the bytecode for the loaded class.
     * @since 3.22
     */
    public static Class<?> toClass(String className, Class<?> neighbor, ClassLoader loader,
                                   ProtectionDomain domain, byte[] bcode)
        throws CannotCompileException
    {
        try {
            
            return (Class<?>) SecurityActions.getMethodHandle(ClassLoader.class, "defineClass1", new Class[] { ClassLoader.class, String.class, byte[].class, int.class, int.class,
                    ProtectionDomain.class, String.class }).invoke(loader, className, bcode, 0, bcode.length, domain, null);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (CannotCompileException e) {
            throw e;
        }
        catch (ClassFormatError e) {
            Throwable t = e.getCause();
            throw new CannotCompileException(t == null ? e : t);
        }
        catch (Exception e) {
            throw new CannotCompileException(e);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Loads a class file by {@code java.lang.invoke.MethodHandles.Lookup}.
     * It is obtained by using {@code neighbor}.
     *
     * @param neighbor  a class belonging to the same package that the loaded
     *                  class belogns to.
     * @param bcode     the bytecode.
     * @since 3.24
     */
    public static Class<?> toClass(Class<?> neighbor, byte[] bcode)
        throws CannotCompileException
    {
        try {
            DefineClassHelper.class.getModule().addReads(neighbor.getModule());
            Lookup lookup = MethodHandles.lookup();
            Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
            return prvlookup.defineClass(bcode);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new CannotCompileException(e.getMessage() + ": " + neighbor.getName()
                                             + " has no permission to define the class");
        }
    }

    /**
     * Loads a class file by {@code java.lang.invoke.MethodHandles.Lookup}.
     * It can be obtained by {@code MethodHandles.lookup()} called from
     * somewhere in the package that the loaded class belongs to.
     *
     * @param bcode     the bytecode.
     * @since 3.24
     */
    public static Class<?> toClass(Lookup lookup, byte[] bcode)
        throws CannotCompileException
    {
        try {
            return lookup.defineClass(bcode);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new CannotCompileException(e.getMessage());
        }
    }

    /**
     * Loads a class file by {@code java.lang.invoke.MethodHandles.Lookup}.
     *
     * @since 3.22
     */
    static Class<?> toPublicClass(String className, byte[] bcode)
        throws CannotCompileException
    {
        try {
            Lookup lookup = MethodHandles.lookup();
            lookup = lookup.dropLookupMode(java.lang.invoke.MethodHandles.Lookup.PRIVATE);
            return lookup.defineClass(bcode);
        }
        catch (Throwable t) {
            throw new CannotCompileException(t);
        }
    }

    private DefineClassHelper() {}
}
