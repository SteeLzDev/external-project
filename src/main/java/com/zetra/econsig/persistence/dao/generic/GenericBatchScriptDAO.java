package com.zetra.econsig.persistence.dao.generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.BatchScriptDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;

/**
 * <p>Title: GenericBatchScriptDAO </p>
 * <p>Description: Implementação Genérica do DAO para execução de múltiplos SQLs</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GenericBatchScriptDAO implements BatchScriptDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericBatchScriptDAO.class);

    private static final int MYSQL_ERROR_CODE_DUPLICATE_KEY      = 1062;
    private static final int MYSQL_ERROR_CODE_PARENT_FOREIGN_KEY = 1451;
    private static final int MYSQL_ERROR_CODE_CHILD_FOREIGN_KEY  = 1452;

    private static final int ORACLE_ERROR_CODE_DUPLICATE_KEY     = 1;

    @Override
    @SuppressWarnings("resource") // DESENV-11498 : falso positivo
    public void executeBatch(String batch) throws DAOException {
        Connection con = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            con = DBHelper.makeConnection();
            stm = con.createStatement();

            // Configurações disponíveis no batch
            // -- @@delimiter=;
            String delimiter = ";";
            Pattern pattern = Pattern.compile("@@delimiter\\s*=\\s*([\\p{Punct}])");
            Matcher matcher = pattern.matcher(batch);
            if (matcher.find()) {
                delimiter = matcher.group(1);
            }

            // -- @@ignoreDuplicateKeyError=true
            boolean ignoreDuplicateKeyError = true;
            pattern = Pattern.compile("@@ignoreDuplicateKeyError\\s*=\\s*(?i:false|no|0)");
            matcher = pattern.matcher(batch);
            if (matcher.find()) {
                ignoreDuplicateKeyError = false;
            }

            // -- @@ignoreForeignKeyError=false
            boolean ignoreForeignKeyError = false;
            pattern = Pattern.compile("@@ignoreForeignKeyError\\s*=\\s*(?i:true|yes|1)");
            matcher = pattern.matcher(batch);
            if (matcher.find()) {
                ignoreForeignKeyError = true;
            }

            String content = cleanSqlFile(batch);
            String[] querys = content.split(delimiter);
            for (String query : querys) {
                if (query != null && !query.trim().equals("")) {
                    LOG.info(query); // usa log INFO pois não deve ser omitido do LOG

                    boolean hasResultSet = false;
                    boolean hasError = false;

                    try {
                        // Trata exceção da excecução separadamente para poder
                        // ver os erros e determinar se podem ser ignorados ou
                        // se devem abortar o processo
                        hasResultSet = stm.execute(query);
                    } catch (SQLException ex) {
                        hasError = true;

                        int errorCode = ex.getErrorCode();
                        LOG.error("Error Code: \"" + errorCode + "\"");

                        if (ignoreDuplicateKeyError && isDuplicateKeyError(errorCode)) {
                            LOG.warn("Ignorando o erro: " + ex.getMessage());
                        } else if (ignoreForeignKeyError && isForeignKeyError(errorCode)) {
                            LOG.warn("Ignorando o erro: " + ex.getMessage());
                        } else if (SysConfig.isTestProfile()) {
                            LOG.warn("Ignorando o erro: " + ex.getMessage());
                        } else {
                            throw ex;
                        }
                    }

                    if (!hasError) {
                        if (hasResultSet) {
                            rst = stm.getResultSet();
                            ResultSetMetaData meta  = rst.getMetaData();
                            int numColumns = meta.getColumnCount();
                            StringBuilder resultLine = new StringBuilder();

                            for (int j = 1; j <= numColumns; j++) {
                                String columnName = meta.getColumnName(j);
                                resultLine.append(columnName + "\t");
                            }
                            LOG.info(resultLine.toString());
                            resultLine.setLength(0);

                            while (rst.next()) {
                                for (int j = 1; j <= numColumns; j++) {
                                    String columnName = meta.getColumnName(j);
                                    Object value = rst.getObject(columnName);
                                    resultLine.append(value + "\t");
                                }
                                LOG.info(resultLine.toString());
                                resultLine.setLength(0);
                            }
                        } else {
                            int updateCount = stm.getUpdateCount();
                            LOG.trace("Rows Affected: " + updateCount);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // Erros relacionados a banco de dados que não sejam da execução da query
            throw new DAOException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        } finally {
            DBHelper.closeResultSet(rst);
            DBHelper.closeStatement(stm);
            DBHelper.releaseConnection(con);
        }
    }

    /**
     * Remove os comentários aceitos pelo MySQL
     */
    private String cleanSqlFile(String content) {
        return content.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "")
                      .replaceAll("-{2,}(?:.)*?[\\n\\r]", "")
                      .replaceAll("#{1,}(?:.)*?[\\n\\r]", "")
                      .replaceAll("[\\n\\r]", " ")
                      .replaceAll("\\s{2,}", " ");
    }

    private boolean isDuplicateKeyError(int errorCode) {
        if (DAOFactory.isMysql() && errorCode == MYSQL_ERROR_CODE_DUPLICATE_KEY) {
            return true;
        }
        if (DAOFactory.isOracle() && errorCode == ORACLE_ERROR_CODE_DUPLICATE_KEY) {
            return true;
        }
        return false;
    }

    private boolean isForeignKeyError(int errorCode) {
        if (DAOFactory.isMysql() && (errorCode == MYSQL_ERROR_CODE_PARENT_FOREIGN_KEY || errorCode == MYSQL_ERROR_CODE_CHILD_FOREIGN_KEY)) {
            return true;
        }
        return false;
    }
}
