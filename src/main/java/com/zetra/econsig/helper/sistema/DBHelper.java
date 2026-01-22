package com.zetra.econsig.helper.sistema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.HibernateSessionFactory;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.jdbc.AuditedConnecton;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: DBHelper</p>
 * <p>Description: Classe com métodos auxiliares para acesso ao banco de dados.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class DBHelper {

    public static final String getNextId() throws MissingPrimaryKeyException {
        /**
         * UUID.randomUUID gera chaves no padrão:
         *
         *       xxxxxxxx-xxxx-Bxxx-Axxx-xxxxxxxxxxxx
         *  Ex.: 123e4567-e89b-42d3-a456-556642440000
         *
         *  Como os campos de PK são varchar(32), removemos os hífens.
         *  Como os valores também são Hexadecimais, para evitar conflito com o gerador antigo
         *  substituímos o F pelo Z.
         */
        return UUID.randomUUID().toString().toUpperCase().replace("-", "").replace("F", "Z");
    }

    public static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return ApplicationContextProvider.getApplicationContext().getBean(NamedParameterJdbcTemplate.class);
    }

    public static Connection makeConnection() throws SQLException {
        final HibernateSessionFactory factory = ApplicationContextProvider.getApplicationContext().getBean(HibernateSessionFactory.class);
        return getAuditedConnecton(factory.getConnection());
    }

    public static Connection getAuditedConnecton(Connection sqlConnection) {
        if (isUseAuditedClasses()) {
            final Set<String> excludedClasses = new HashSet<>();

            final String excludedClassesParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IGNORAR_CLASSES_AO_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO, AcessoSistema.getAcessoUsuarioSistema());
            if (!TextHelper.isNull(excludedClassesParam)) {
                Collections.addAll(excludedClasses, excludedClassesParam.split("[\\p{Space},;]"));
            }

            // stack[0] = java.lang.Thread.getStackTrace(
            // stack[1] = DBHelper.getAuditedConnecton((Connection)
            // stack[2] = DBHelper.makeConnection(Connection)
            // stack[3] = DBHelper.makeConnection()
            // stack[4] = método que solicita o DBHelper.makeConnection()
            final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            final String className = ((stack != null) && (stack.length >= 5)) ? stack[4].getClassName() : DBHelper.class.getName();
            if (!excludedClasses.contains(className)) {
                sqlConnection = new AuditedConnecton(sqlConnection, excludedClasses);
            }
        }
        return sqlConnection;
    }

    /**
     * Fecha o ResultSet, ignorando exceções
     * @param res
     */
    public static void closeResultSet(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (final SQLException e) {
                // just ignore it
            }
        }
    }

    /**
     * Fecha o Statement, ignorando exceções
     * @param stat
     */
    public static void closeStatement(Statement stat) {
        if (stat != null) {
            try {
                stat.close();
            } catch (final SQLException e) {
                // just ignore it
            }
        }
    }

    /**
     * Fecha a conecxão, ignorando exceções.
     * @param conn
     */
    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            // try {
				// conn.close();
			// } catch (SQLException e) {
			    // just ignore it
			// }
        }
    }

    /**
     * Oracle tem limitação de 30 caracteres em nomes de objetos (tabelas, campos, alias ...).
     * O tratamento abaixo irá cortar os nomes de tabelas com mais que 30 caracteres,
     * evitando erro nas querys nativas, que não usam o mapeamento do Hibernate para
     * construção do SQL final.
     * @param sql
     * @return
     */
    public static String applyTableNameRestriction(String sql) {
        if (DAOFactory.isOracle()) {
            final Pattern pattern = Pattern.compile("(tb_[a-z0-9_]+)[ \\.]");
            final Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                sql = sql.replace(matcher.group(1), matcher.group(1).substring(0, Math.min(matcher.group(1).length(), 30)));
            }
        }
        return sql;
    }

    public static boolean isUseAuditedClasses() {
        return ParamSist.paramEquals(CodedValues.TPC_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
    }

    @Deprecated
    public static String getDbowner() {
        return "dbo";
    }
}