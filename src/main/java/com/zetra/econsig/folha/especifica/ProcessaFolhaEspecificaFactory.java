package com.zetra.econsig.folha.especifica;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.python.core.PyInteger;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.zetra.econsig.exception.ProcessaFolhaEspecificaException;
import com.zetra.econsig.helper.parametro.ParamSist;

/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Factory class para importar classe especifica no processamento sem bloqueio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ProcessaFolhaEspecificaFactory {
    public static ProcessaFolhaEspecifica getExportador(String className) throws ProcessaFolhaEspecificaException {
        ProcessaFolhaEspecifica myPythonInterface = null;
        final String pathPython = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "admin" + File.separatorChar + className;

        System.setProperty("python.path", pathPython);

        try (PythonInterpreter pythonInterpreter = new PythonInterpreter()) {
            pythonInterpreter.exec("from custom_proc_folha import CustomProcessarFolha");
            PyObject myPythonClass = pythonInterpreter.get("CustomProcessarFolha");

            myPythonInterface = (ProcessaFolhaEspecifica) Proxy.newProxyInstance(
                    ProcessaFolhaEspecifica.class.getClassLoader(),
                    new Class[]{ProcessaFolhaEspecifica.class},
                    new PythonInterfaceInvocationHandler(myPythonClass));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myPythonInterface;
    }

    private static class PythonInterfaceInvocationHandler implements InvocationHandler {
        private final PyObject pyObject;

        public PythonInterfaceInvocationHandler(PyObject pyObject) {
            this.pyObject = pyObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;

            PyObject pyMethod = pyObject.__getattr__(method.getName());
            if (args != null && args.length > 0) {
                switch (args.length) {
                    case 1:
                        result = pyMethod.__call__(getPyObject(args[0]));
                        break;
                    case 2:
                        result = pyMethod.__call__(getPyObject(args[0]), getPyObject(args[1]));
                        break;
                    case 3:
                        result = pyMethod.__call__(getPyObject(args[0]), getPyObject(args[1]), getPyObject(args[2]));
                        break;
                    case 4:
                        result = pyMethod.__call__(getPyObject(args[0]), getPyObject(args[1]), getPyObject(args[2]), getPyObject(args[3]));
                        break;
                }
            } else {
                result = pyMethod.__call__();
            }

            if (result == null || result instanceof PyNone) {
                return null;
            } else if (result instanceof PyString pyString) {
                return pyString.asString();
            } else if (result instanceof PyInteger pyInteger) {
                return pyInteger.asInt();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static PyObject getPyObject(Object javaObject) {
        if (javaObject != null) {
            if (javaObject instanceof String stringObject) {
                return new PyString(stringObject);
            } else if (javaObject instanceof Integer integerObject) {
                return new PyInteger(integerObject);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return null;
    }

}
