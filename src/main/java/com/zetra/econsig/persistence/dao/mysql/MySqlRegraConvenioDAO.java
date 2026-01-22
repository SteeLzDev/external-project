package com.zetra.econsig.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.hibernate.Session;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.RegraConvenioDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlMargemDAO</p>
 * <p>Description: Implementação para MySql do DAO de Regra de Convênio.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlRegraConvenioDAO implements RegraConvenioDAO {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlRegraConvenioDAO.class);

	@Override
	public void insereRegrasConvenio(List<CustomTransferObject> listParams) throws DAOException {
		Connection conn = null;
        PreparedStatement psInsereRegra = null;
        final Session session = SessionUtil.getSession();
        try {
            conn = DBHelper.makeConnection();
            final String insereRegra = "INSERT INTO tb_regra_convenio (RCO_CODIGO,RCO_CAMPO_CODIGO,RCO_CAMPO_NOME,RCO_CAMPO_VALOR,CSA_CODIGO,SVC_CODIGO,ORG_CODIGO,MAR_CODIGO) VALUES (?,?,?,?,?,?,?,?)";
            
            psInsereRegra = conn.prepareStatement(insereRegra);
            String objectId = null;
            
            for (final CustomTransferObject regra : listParams) {
                // Insere regra
            	objectId = DBHelper.getNextId();
            	psInsereRegra.setString(1, objectId);
                psInsereRegra.setString(2, regra.getAttribute(Columns.RCO_CAMPO_CODIGO).toString());
                psInsereRegra.setString(3, regra.getAttribute(Columns.RCO_CAMPO_NOME).toString());
                psInsereRegra.setString(4, (String) regra.getAttribute(Columns.RCO_CAMPO_VALOR));
                psInsereRegra.setString(5, regra.getAttribute(Columns.RCO_CSA_CODIGO).toString());
                psInsereRegra.setString(6, (String) regra.getAttribute(Columns.RCO_SVC_CODIGO));
                psInsereRegra.setString(7, (String) regra.getAttribute(Columns.RCO_ORG_CODIGO));
                psInsereRegra.setObject(8, !TextHelper.isNull(regra.getAttribute(Columns.RCO_MAR_CODIGO)) ? (Short) regra.getAttribute(Columns.RCO_MAR_CODIGO) : null, java.sql.Types.SMALLINT);

                psInsereRegra.addBatch();
            }
            psInsereRegra.executeBatch();

            session.flush();
        } catch (final Exception ex) {
        	LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex); 
        } finally {
            SessionUtil.closeSession(session);
            DBHelper.closeStatement(psInsereRegra);
            DBHelper.releaseConnection(conn);
        }
	}

}
