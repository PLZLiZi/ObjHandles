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
package plz.lizi.api.superbyte.bytecode.analysis;

import java.io.PrintStream;
import plz.lizi.api.superbyte.CtClass;
import plz.lizi.api.superbyte.CtMethod;
import plz.lizi.api.superbyte.Modifier;
import plz.lizi.api.superbyte.NotFoundException;
import plz.lizi.api.superbyte.bytecode.BadBytecode;
import plz.lizi.api.superbyte.bytecode.CodeAttribute;
import plz.lizi.api.superbyte.bytecode.CodeIterator;
import plz.lizi.api.superbyte.bytecode.ConstPool;
import plz.lizi.api.superbyte.bytecode.Descriptor;
import plz.lizi.api.superbyte.bytecode.InstructionPrinter;
import plz.lizi.api.superbyte.bytecode.MethodInfo;

/**
 * A utility class for printing a merged view of the frame state and the
 * instructions of a method.
 *
 * @author Jason T. Greene
 */
public final class FramePrinter {
    private final PrintStream stream;

    /**
     * Constructs a bytecode printer.
     */
    public FramePrinter(PrintStream stream) {
        this.stream = stream;
    }

    /**
     * Prints all the methods declared in the given class. 
     */
    public static void print(CtClass clazz, PrintStream stream) {
        (new FramePrinter(stream)).print(clazz);
    }

    /**
     * Prints all the methods declared in the given class. 
     */
    public void print(CtClass clazz) {
        CtMethod[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            print(methods[i]);
        }
    }

    private String getMethodString(CtMethod method) {
        try {
            return Modifier.toString(method.getModifiers()) + " "
                    + method.getReturnType().getName() + " " + method.getName()
                    + Descriptor.toString(method.getSignature()) + ";";
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints the instructions and the frame states of the given method.
     */
    public void print(CtMethod method) {
        stream.println("\n" + getMethodString(method));
        MethodInfo info = method.getMethodInfo2();
        ConstPool pool = info.getConstPool();
        CodeAttribute code = info.getCodeAttribute();
        if (code == null)
            return;

        Frame[] frames;
        try {
            frames = (new Analyzer()).analyze(method.getDeclaringClass(), info);
        } catch (BadBytecode e) {
            throw new RuntimeException(e);
        }

        int spacing = String.valueOf(code.getCodeLength()).length();

        CodeIterator iterator = code.iterator();
        while (iterator.hasNext()) {
            int pos;
            try {
                pos = iterator.next();
            } catch (BadBytecode e) {
                throw new RuntimeException(e);
            }

            stream.println(pos + ": " + InstructionPrinter.instructionString(iterator, pos, pool));

            addSpacing(spacing + 3);
            Frame frame = frames[pos];
            if (frame == null) {
                stream.println("--DEAD CODE--");
                continue;
            }
            printStack(frame);

            addSpacing(spacing + 3);
            printLocals(frame);
        }

    }

    private void printStack(Frame frame) {
        stream.print("stack [");
        int top = frame.getTopIndex();
        for (int i = 0; i <= top; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getStack(i);
            stream.print(type);
        }
        stream.println("]");
    }

    private void printLocals(Frame frame) {
        stream.print("locals [");
        int length = frame.localsLength();
        for (int i = 0; i < length; i++) {
            if (i > 0)
                stream.print(", ");
            Type type = frame.getLocal(i);
            stream.print(type == null ? "empty" : type.toString());
        }
        stream.println("]");
    }

    private void addSpacing(int count) {
        while (count-- > 0)
            stream.print(' ');
    }
}
