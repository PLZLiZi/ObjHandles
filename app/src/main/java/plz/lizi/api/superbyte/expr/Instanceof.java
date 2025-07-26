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

package plz.lizi.api.superbyte.expr;

import plz.lizi.api.superbyte.CannotCompileException;
import plz.lizi.api.superbyte.ClassPool;
import plz.lizi.api.superbyte.CtBehavior;
import plz.lizi.api.superbyte.CtClass;
import plz.lizi.api.superbyte.NotFoundException;
import plz.lizi.api.superbyte.bytecode.BadBytecode;
import plz.lizi.api.superbyte.bytecode.Bytecode;
import plz.lizi.api.superbyte.bytecode.CodeAttribute;
import plz.lizi.api.superbyte.bytecode.CodeIterator;
import plz.lizi.api.superbyte.bytecode.ConstPool;
import plz.lizi.api.superbyte.bytecode.MethodInfo;
import plz.lizi.api.superbyte.bytecode.Opcode;
import plz.lizi.api.superbyte.compiler.CompileError;
import plz.lizi.api.superbyte.compiler.Javac;
import plz.lizi.api.superbyte.compiler.JvstCodeGen;
import plz.lizi.api.superbyte.compiler.JvstTypeChecker;
import plz.lizi.api.superbyte.compiler.ProceedHandler;
import plz.lizi.api.superbyte.compiler.ast.ASTList;

/**
 * Instanceof operator.
 */
public class Instanceof extends Expr {
    /**
     * Undocumented constructor.  Do not use; internal-use only.
     */
    protected Instanceof(int pos, CodeIterator i, CtClass declaring,
                         MethodInfo m) {
        super(pos, i, declaring, m);
    }

    /**
     * Returns the method or constructor containing the instanceof
     * expression represented by this object.
     */
    @Override
    public CtBehavior where() { return super.where(); }

    /**
     * Returns the line number of the source line containing the
     * instanceof expression.
     *
     * @return -1       if this information is not available.
     */
    @Override
    public int getLineNumber() {
        return super.getLineNumber();
    }

    /**
     * Returns the source file containing the
     * instanceof expression.
     *
     * @return null     if this information is not available.
     */
    @Override
    public String getFileName() {
        return super.getFileName();
    }

    /**
     * Returns the <code>CtClass</code> object representing
     * the type name on the right hand side
     * of the instanceof operator.
     */
    public CtClass getType() throws NotFoundException {
        ConstPool cp = getConstPool();
        int pos = currentPos;
        int index = iterator.u16bitAt(pos + 1);
        String name = cp.getClassInfo(index);
        return thisClass.getClassPool().getCtClass(name);
    }

    /**
     * Returns the list of exceptions that the expression may throw.
     * This list includes both the exceptions that the try-catch statements
     * including the expression can catch and the exceptions that
     * the throws declaration allows the method to throw.
     */
    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    /**
     * Replaces the instanceof operator with the bytecode derived from
     * the given source text.
     *
     * <p>$0 is available but the value is <code>null</code>.
     *
     * @param statement         a Java statement except try-catch.
     */
    @Override
    public void replace(String statement) throws CannotCompileException {
        thisClass.getClassFile();   // to call checkModify().
        @SuppressWarnings("unused")
        ConstPool constPool = getConstPool();
        int pos = currentPos;
        int index = iterator.u16bitAt(pos + 1);

        Javac jc = new Javac(thisClass);
        ClassPool cp = thisClass.getClassPool();
        CodeAttribute ca = iterator.get();

        try {
            CtClass[] params
                = new CtClass[] { cp.get(javaLangObject) };
            CtClass retType = CtClass.booleanType;

            int paramVar = ca.getMaxLocals();
            jc.recordParams(javaLangObject, params, true, paramVar,
                            withinStatic());
            int retVar = jc.recordReturnType(retType, true);
            jc.recordProceed(new ProceedForInstanceof(index));

            // because $type is not the return type...
            jc.recordType(getType());

            /* Is $_ included in the source code?
             */
            checkResultValue(retType, statement);

            Bytecode bytecode = jc.getBytecode();
            storeStack(params, true, paramVar, bytecode);
            jc.recordLocalVariables(ca, pos);

            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);     // initialize $_

            jc.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);

            replace0(pos, bytecode, 3);
        }
        catch (CompileError e) { throw new CannotCompileException(e); }
        catch (NotFoundException e) { throw new CannotCompileException(e); }
        catch (BadBytecode e) {
            throw new CannotCompileException("broken method");
        }
    }

    /* boolean $proceed(Object obj)
     */
    static class ProceedForInstanceof implements ProceedHandler {
        int index;

        ProceedForInstanceof(int i) {
            index = i;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args, int lineNumber)
            throws CompileError
        {
            if (gen.getMethodArgsLength(args) != 1)
                throw new CompileError(Javac.proceedName
                        + "() cannot take more than one parameter "
                        + "for instanceof", lineNumber);

            gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
            bytecode.addOpcode(Opcode.INSTANCEOF);
            bytecode.addIndex(index);
            gen.setType(CtClass.booleanType, lineNumber);
        }

        @Override
        public void setReturnType(JvstTypeChecker c, ASTList args, int lineNumber)
            throws CompileError
        {
            c.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c.setType(CtClass.booleanType, lineNumber);
        }
    }
}
