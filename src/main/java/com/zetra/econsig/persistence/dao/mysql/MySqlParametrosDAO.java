package com.zetra.econsig.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericParametrosDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlParametrosDAO</p>
 * <p>Description: Implementacao do DAO de parametros para o MySql</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlParametrosDAO extends GenericParametrosDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlParametrosDAO.class);

    @Override
    public void ativaParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos) throws DAOException {
        Connection conn = null;
    	PreparedStatement preStat = null;
        PreparedStatement preStatUpd = null;
        PreparedStatement preStatDel = null;

        try {
            conn = DBHelper.makeConnection();

            StringBuilder query = new StringBuilder();

            //0 - apaga os parâmetros com data_ini maior do que hoje, ou seja aqueles que ainda
            //    não estão em vigor e ainda não foram usados
            query.append("DELETE FROM ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
            query.append(" WHERE ");
            query.append(Columns.PSC_CSA_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_SVC_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_DATA_INI_VIG).append(" > CURDATE()");
            if (tpsCodigos != null && tpsCodigos.size() > 0) {
                query.append(" AND ").append(Columns.PSC_TPS_CODIGO).append(" IN ( ? ) ");
            }
            
            StringBuilder lstTpsCodigoDel = new StringBuilder();
            lstTpsCodigoDel.append("'").append(TextHelper.join(tpsCodigos, "','")).append("'");

            preStatDel = conn.prepareStatement(query.toString());
            preStatDel.setString(1, csaCodigo);
            preStatDel.setString(2, svcCodigo);
            preStatDel.setString(3, lstTpsCodigoDel.toString());
            preStatDel.executeUpdate();

            //1 - seta data_fim para hoje 24:00 onde data_ini != null && data_fim == null
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
            query.append(" SET ");
            query.append(Columns.PSC_DATA_FIM_VIG).append(" = DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59'),");
            query.append(Columns.PSC_ATIVO).append(" = 0 WHERE ");
            query.append(Columns.PSC_CSA_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_SVC_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_DATA_INI_VIG).append(" IS NOT NULL AND ");
            query.append(Columns.PSC_DATA_FIM_VIG).append(" IS NULL");
            if (tpsCodigos != null && tpsCodigos.size() > 0) {
                query.append(" AND ").append(Columns.PSC_TPS_CODIGO).append(" IN ( ? )");
            }
            
            StringBuilder lstTpsCodigoUpd = new StringBuilder();
            lstTpsCodigoUpd.append("'").append(TextHelper.join(tpsCodigos, "','")).append("'");

            preStatUpd = conn.prepareStatement(query.toString());
            preStatUpd.setString(1, csaCodigo);
            preStatUpd.setString(2, svcCodigo);
            preStatUpd.setString(3, lstTpsCodigoUpd.toString());
            preStatUpd.executeUpdate();

            //2 - seta data_ini para amanhã onde data_ini == null
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
            query.append(" SET ");
            query.append(Columns.PSC_DATA_INI_VIG).append(" = DATE_ADD(CURDATE(), INTERVAL 1 DAY),");
            query.append(Columns.PSC_ATIVO).append(" = 1 WHERE ");
            query.append(Columns.PSC_CSA_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_SVC_CODIGO).append(" = ? AND ");
            query.append(Columns.PSC_DATA_INI_VIG).append(" IS NULL");
            if (tpsCodigos != null && tpsCodigos.size() > 0) {
                query.append(" AND ").append(Columns.PSC_TPS_CODIGO).append(" IN ( ? )");
            }
            
            StringBuilder lstTpsCodigo = new StringBuilder();
        	lstTpsCodigo.append("'").append(TextHelper.join(tpsCodigos, "','")).append("'");

            preStat = conn.prepareStatement(query.toString());
            preStat.setString(1, csaCodigo);
            preStat.setString(2, svcCodigo);
            preStat.setString(3, lstTpsCodigo.toString());
            
            preStat.executeUpdate();

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStatDel);
            DBHelper.closeStatement(preStatUpd);
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }
}
