package com.zetra.econsig.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlGenericDAO</p>
 * <p>Description: Implementacao Generica do DAO para o MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class MySqlGenericDAO {
    /**TODO Renomear classe para AbstractDAO */

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlGenericDAO.class);
    
    public static List<TransferObject> getFieldsValuesList(MapSqlParameterSource queryParams, String query, String fieldsNames, String delim) throws DAOException {
        return getFieldsValuesList(queryParams, query, fieldsNames, delim, CustomTransferObject.class);
    }

    public static List<TransferObject> getFieldsValuesList(MapSqlParameterSource queryParams, String query, String fieldsNames, String delim, Class<? extends TransferObject> dtoClass) throws DAOException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final List<Map<String, Object>> resultList = jdbc.queryForList(query.toString(), queryParams);
            final List<TransferObject> dtoList = new ArrayList<>();

            for (Map<String, Object> result : resultList) {
                final TransferObject row = dtoClass.getDeclaredConstructor().newInstance();
                final StringTokenizer stn = new StringTokenizer(fieldsNames, delim);
                while (stn.hasMoreTokens()) {
                    final String token = stn.nextToken();

                    Object fieldValue = result.get(token);
                    if (TextHelper.isNull(fieldValue)) {
                        fieldValue = result.get(Columns.getColumnName(token));
                    }
                    row.setAttribute(token, (fieldValue instanceof String) ? fieldValue.toString().trim() : fieldValue);
                }
                dtoList.add(row);
            }

            return dtoList;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
