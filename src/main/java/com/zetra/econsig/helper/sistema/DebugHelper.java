package com.zetra.econsig.helper.sistema;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.report.dao.MyDataSourceFactory;

/**
 * <p>Title: DebugHelper</p>
 * <p>Description: Classe com métodos auxiliares para logging.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DebugHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DebugHelper.class);

    private final StackTraceElement stackTraceElement;

    private static final Set<String> ignoredClasses = new HashSet<>();

    static {
        ignoredClasses.add(DebugHelper.class.getName());
        ignoredClasses.add(MySqlGenericDAO.class.getName());
        ignoredClasses.add(MyDataSourceFactory.class.getName());
    }

    public DebugHelper(StackTraceElement stackTraceElement) {
        super();
        this.stackTraceElement = stackTraceElement;
    }

    /**
     * Navega na StackTrace tentando descobrir qual é classe deve ser mostrada no log,
     * desconsiderando as que estão listadas em <em>ignoredclasses</em>.
     * @return o DebugHelper para o StackTraceElement da classe original.
     */
    public static DebugHelper getCallerLog() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ignoredClasses.contains(ste.getClassName())) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return new DebugHelper(ste);
                }
            }
        }
        StackTraceElement ste = null;
        if (stElements.length == 1) {
            ste = stElements[0];
        } else if (stElements.length > 1) {
            ste = stElements[1];
        }
        return new DebugHelper(ste);
    }


    /**
     * Navega na StackTrace tentando descobrir qual é classe deve ser mostrada no log, desconsiderando as que estão listadas em <em>ignoredclasses</em>.
     * @return o nome da classe original.
     */
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(DebugHelper.class.getName())) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return null;
     }

    public static String generateActualSql(String sqlQuery, Object... parameters) {
        StringBuilder sb = new StringBuilder(sqlQuery);
        int index = 0;
        // por convenção os índices de parametros de preparedstatement começam de 1.
        // espera-se que na posição 0 (zero) algo apenas para fazer o shift.
        int i = 1;
        while ((index = sb.indexOf("?", index)) != -1) {
            if (i < parameters.length) {
                sb.replace(index, index+1, formatParameter(parameters[i]));
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * Formata um objeto para ser impresso em um comando SQL.
     * @param parameter Objeto a ser formatado
     * @return O texto formatado que representa o objeto.
     */
    public static String formatParameter(Object parameter) {
        if (parameter != null) {
            if (parameter instanceof String) {
                return "'" + ((String) parameter).replace("'", "''") + "'";
            }
            if (parameter instanceof Boolean) {
                return ((Boolean) parameter).booleanValue() ? "1" : "0";
            }
            if (DAOFactory.isOracle()) {
                if (parameter instanceof Timestamp) {
                    return "to_timestamp('" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(parameter) + "', 'mm/dd/yyyy hh24:mi:ss.ff3')";
                }
                if (parameter instanceof Date) {
                    return "to_date('" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(parameter) + "', 'mm/dd/yyyy hh24:mi:ss')";
                }
            }
            return parameter.toString();
        }
        return "NULL";
    }

    /**
     * Prepara a mensagem para ser impressa no log.
     * @param arg0 Objeto a ser impresso no log junto com o prefixo.
     * @return Texto no formato "[(NomeDaClasse.nomeDoMetodo(linhaDoCodigoFonte)] arg0" caso o stackTraceElment não esteja nulo.
     *         Se estiver nulo, retorna apenas "arg0".
     */
    private String buildMessage(Object arg0) {
        StringBuilder message = new StringBuilder();
        if (stackTraceElement != null) {
            String[] nameArray = TextHelper.split(stackTraceElement.getClassName(), ".");
            String className = nameArray[nameArray.length - 1];
            message.append("[")
                    .append(className)
                    .append(".")
                    .append(stackTraceElement.getMethodName())
                    .append("(")
                    .append(stackTraceElement.getLineNumber())
                    .append(")] ");
        }
        if (arg0 != null) {
            message.append(arg0);
        }
        return message.toString();
    }

    public void info(Object arg0) {
        LOG.info(buildMessage(arg0));
    }
    public void error(Object arg0) {
        LOG.error(buildMessage(arg0));
    }
    public void debug(Object arg0) {
        LOG.debug(buildMessage(arg0));
    }
    public void warn(Object arg0) {
        LOG.warn(buildMessage(arg0));
    }
    public void trace(Object arg0) {
        LOG.trace(buildMessage(arg0));
    }
}
