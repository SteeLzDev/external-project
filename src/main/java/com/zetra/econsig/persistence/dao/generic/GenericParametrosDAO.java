package com.zetra.econsig.persistence.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.ParametrosDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericParametrosDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parametros. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParametrosDAO implements ParametrosDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParametrosDAO.class);

    @Override
    public void deleteParamIgualCse(List<TransferObject> parametros) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;

        try {
            conn = DBHelper.makeConnection();

            StringBuilder query = new StringBuilder();
            Iterator<TransferObject> it = parametros.iterator();
            TransferObject cto = null;

            while (it.hasNext()) {
                cto = it.next();

                query.append("DELETE FROM ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
                query.append(" WHERE ").append(Columns.PSC_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSC_CSA_CODIGO).append(" = ? AND ");
                query.append(Columns.PSC_SVC_CODIGO).append(" = ? ");
                
                preStat = conn.prepareStatement(query.toString());
                preStat.setString(1, cto.getAttribute(Columns.TPS_CODIGO).toString());
                preStat.setString(2, cto.getAttribute(Columns.PSC_CSA_CODIGO).toString());
                preStat.setString(3, cto.getAttribute(Columns.PSC_SVC_CODIGO).toString());
                
                preStat.executeUpdate();
                query.setLength(0);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void deleteParamIgualCseRse(List<TransferObject> parametros) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;

        try {
            conn = DBHelper.makeConnection();

            StringBuilder query = new StringBuilder();
            Iterator<TransferObject> it = parametros.iterator();
            TransferObject cto = null;

            while (it.hasNext()) {
                cto = it.next();

                query.append("DELETE FROM ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR);
                query.append(" WHERE ").append(Columns.PSR_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSR_RSE_CODIGO).append(" = ? AND ");
                query.append(Columns.PSR_SVC_CODIGO).append(" = ?");

                preStat = conn.prepareStatement(query.toString());
                preStat.setString(1, cto.getAttribute(Columns.PSR_TPS_CODIGO).toString());
                preStat.setString(2, cto.getAttribute(Columns.PSR_RSE_CODIGO).toString());
                preStat.setString(3, cto.getAttribute(Columns.PSR_SVC_CODIGO).toString());

                preStat.executeUpdate();
                query.setLength(0);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }
    @Override
    public void updateParamSvcCsa(List<TransferObject> parametros) throws DAOException {
        PreparedStatement preStat = null;
        PreparedStatement preStatUpd = null;
        Connection conn = null;

        try {
            String fields = Columns.PSC_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSC_SVC_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSC_CSA_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSC_TPS_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSC_VLR + MySqlDAOFactory.SEPARADOR
                    + Columns.PSC_VLR_REF;

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" (");
            query.append(fields).append(") VALUES (?, ?, ?, ?, ?, ?)");

            conn = DBHelper.makeConnection();
            preStat = conn.prepareStatement(query.toString());

            String pscVlr, pscVlrRef, tpsCodigo, csaCodigo, svcCodigo;
            TransferObject cto = null;
            Iterator<TransferObject> it = parametros.iterator();
            while (it.hasNext()) {
                cto = it.next();
                pscVlr = cto.getAttribute(Columns.PSC_VLR).toString();
                pscVlrRef = (cto.getAttribute(Columns.PSC_VLR_REF) != null) ? cto.getAttribute(Columns.PSC_VLR_REF).toString() : "";
                csaCodigo = cto.getAttribute(Columns.PSC_CSA_CODIGO).toString();
                svcCodigo = cto.getAttribute(Columns.PSC_SVC_CODIGO).toString();
                tpsCodigo = cto.getAttribute(Columns.TPS_CODIGO).toString();

                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
                query.append(" SET ");
                query.append(Columns.PSC_VLR).append(" = ?, ");
                query.append(Columns.PSC_VLR_REF).append(" = ? ");
                query.append(" WHERE ").append(Columns.PSC_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSC_CSA_CODIGO).append(" = ? AND ");
                query.append(Columns.PSC_SVC_CODIGO).append(" = ? AND ");
                query.append(Columns.PSC_DATA_INI_VIG).append(" IS NULL AND ");
                query.append(Columns.PSC_DATA_FIM_VIG).append(" IS NULL");
                                               
                preStatUpd = conn.prepareStatement(query.toString());
                preStatUpd.setString(1, pscVlr);
                preStatUpd.setString(2, pscVlrRef);
                preStatUpd.setString(3, tpsCodigo);
                preStatUpd.setString(4, csaCodigo);
                preStatUpd.setString(5, svcCodigo);
                int alterados = preStatUpd.executeUpdate();
                
                if (alterados == 0) {
                    preStat.setString(1, DBHelper.getNextId());
                    preStat.setString(2, svcCodigo);
                    preStat.setString(3, csaCodigo);
                    preStat.setString(4, tpsCodigo);
                    preStat.setString(5, pscVlr);
                    preStat.setString(6, pscVlrRef);
                    preStat.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.closeStatement(preStatUpd);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void updateParamSvcSobrepoe(List<TransferObject> parametros) throws DAOException {
        PreparedStatement preStat = null;
        PreparedStatement preStatUpd = null;
        Connection conn = null;

        try {
            String fields = Columns.PSR_TPS_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSR_SVC_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSR_RSE_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSR_VLR;

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" (");
            query.append(fields).append(") VALUES (?, ?, ?, ?)");

            conn = DBHelper.makeConnection();
            preStat = conn.prepareStatement(query.toString());

            String psrVlr, tpsCodigo, rseCodigo, svcCodigo;
            TransferObject cto = null;
            Iterator<TransferObject> it = parametros.iterator();
            while (it.hasNext()) {
                cto = it.next();
                psrVlr = cto.getAttribute(Columns.PSR_VLR).toString();
                rseCodigo = cto.getAttribute(Columns.PSR_RSE_CODIGO).toString();
                svcCodigo = cto.getAttribute(Columns.PSR_SVC_CODIGO).toString();
                tpsCodigo = cto.getAttribute(Columns.PSR_TPS_CODIGO).toString();

                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR);
                query.append(" SET ");
                query.append(Columns.PSR_VLR).append(" = ? ");
                query.append(" WHERE ").append(Columns.PSR_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSR_RSE_CODIGO).append(" = ? AND ");
                query.append(Columns.PSR_SVC_CODIGO).append(" = ? ");
                
                preStatUpd = conn.prepareStatement(query.toString());
                preStatUpd.setString(1, psrVlr);
                preStatUpd.setString(2, tpsCodigo);
                preStatUpd.setString(3, rseCodigo);
                preStatUpd.setString(4, svcCodigo);
                int alterados = preStatUpd.executeUpdate();
                
                if (alterados == 0) {
                    preStat.setString(1, tpsCodigo);
                    preStat.setString(2, svcCodigo);
                    preStat.setString(3, rseCodigo);
                    preStat.setString(4, psrVlr);
                    preStat.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.closeStatement(preStatUpd);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void updateParamSvcCor(List<TransferObject> parametros) throws DAOException {
        PreparedStatement preStat = null;
        PreparedStatement preStatUpd = null;
        Connection conn = null;

        try {
            String fields = Columns.PSO_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSO_SVC_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSO_COR_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSO_TPS_CODIGO + MySqlDAOFactory.SEPARADOR
                    + Columns.PSO_VLR + MySqlDAOFactory.SEPARADOR
                    + Columns.PSO_VLR_REF;

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(Columns.TB_PARAM_SVC_CORRESPONDENTE).append(" (");
            query.append(fields).append(") VALUES (?, ?, ?, ?, ?, ?)");

            conn = DBHelper.makeConnection();
            preStat = conn.prepareStatement(query.toString());

            String psoVlr, psoVlrRef, tpsCodigo, corCodigo, svcCodigo;
            TransferObject cto = null;
            Iterator<TransferObject> it = parametros.iterator();
            while (it.hasNext()) {
                cto = it.next();
                psoVlr = cto.getAttribute(Columns.PSO_VLR).toString();
                psoVlrRef = (cto.getAttribute(Columns.PSO_VLR_REF) != null) ? cto.getAttribute(Columns.PSO_VLR_REF).toString() : "";
                corCodigo = cto.getAttribute(Columns.PSO_COR_CODIGO).toString();
                svcCodigo = cto.getAttribute(Columns.PSO_SVC_CODIGO).toString();
                tpsCodigo = cto.getAttribute(Columns.PSO_TPS_CODIGO).toString();

                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_PARAM_SVC_CORRESPONDENTE);
                query.append(" SET ");
                query.append(Columns.PSO_VLR).append(" = ?, ");
                query.append(Columns.PSO_VLR_REF).append(" = ? ");
                query.append(" WHERE ").append(Columns.PSO_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSO_COR_CODIGO).append(" = ? AND ");
                query.append(Columns.PSO_SVC_CODIGO).append(" = ? AND ");
                query.append(Columns.PSO_DATA_INI_VIG).append(" IS NULL AND ");
                query.append(Columns.PSO_DATA_FIM_VIG).append(" IS NULL");
                                               
                preStatUpd = conn.prepareStatement(query.toString());
                preStatUpd.setString(1, psoVlr);
                preStatUpd.setString(2, psoVlrRef);
                preStatUpd.setString(3, tpsCodigo);
                preStatUpd.setString(4, corCodigo);
                preStatUpd.setString(5, svcCodigo);
                int alterados = preStatUpd.executeUpdate();
                
                if (alterados == 0) {
                    preStat.setString(1, DBHelper.getNextId());
                    preStat.setString(2, svcCodigo);
                    preStat.setString(3, corCodigo);
                    preStat.setString(4, tpsCodigo);
                    preStat.setString(5, psoVlr);
                    preStat.setString(6, psoVlrRef);
                    preStat.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.closeStatement(preStatUpd);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void deleteParamIgualCsa(List<TransferObject> parametros) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;

        try {
            conn = DBHelper.makeConnection();

            StringBuilder query = new StringBuilder();
            Iterator<TransferObject> it = parametros.iterator();
            TransferObject cto = null;

            while (it.hasNext()) {
                cto = it.next();

                query.append("DELETE FROM ").append(Columns.TB_PARAM_SVC_CORRESPONDENTE);
                query.append(" WHERE ").append(Columns.PSO_TPS_CODIGO).append(" = ? AND ");
                query.append(Columns.PSO_COR_CODIGO).append(" = ? AND ");
                query.append(Columns.PSO_SVC_CODIGO).append(" = ? ");
                
                preStat = conn.prepareStatement(query.toString());
                preStat.setString(1, cto.getAttribute(Columns.PSO_TPS_CODIGO).toString());
                preStat.setString(2, cto.getAttribute(Columns.PSO_COR_CODIGO).toString());
                preStat.setString(3, cto.getAttribute(Columns.PSO_SVC_CODIGO).toString());
                
                preStat.executeUpdate();
                query.setLength(0);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }
}
