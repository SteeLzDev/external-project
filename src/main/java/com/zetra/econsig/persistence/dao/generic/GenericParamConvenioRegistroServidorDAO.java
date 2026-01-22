package com.zetra.econsig.persistence.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.ParamConvenioRegistroServidorDAO;
import com.zetra.econsig.persistence.entity.ConvenioVinculoRegistro;
import com.zetra.econsig.persistence.entity.ConvenioVinculoRegistroHome;
import com.zetra.econsig.persistence.query.vinculo.ListaCnvVinculoRegistroFaltanteQuery;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericParamConvenioRegistroServidorDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parametros de convênio por
 * Registro Servidor. Instruções SQLs contidas aqui devem funcionar em todos
 * os SGDBs suportados pelo sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParamConvenioRegistroServidorDAO implements ParamConvenioRegistroServidorDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParamConvenioRegistroServidorDAO.class);

    /**
     * Copia todos os bloqueios de convênio do servidor antigo para o novo servidor.
     * Rotina utilizada pela transferência de servidor.
     * @param rseCodNovo : código do novo registro servidor
     * @param rseCodAnt  : código do antigo registro servidor
     */
    @Override
    public void copiaBloqueioCnv(String rseCodNovo, String rseCodAnt) throws DAOException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" (rse_codigo, cnv_codigo, tps_codigo, pcr_vlr, pcr_vlr_ser, pcr_vlr_csa, pcr_vlr_cse, pcr_obs, pcr_data_cadastro) ");
            query.append(" SELECT rseNovo.rse_codigo, cnvNovo.cnv_codigo, pcr.tps_codigo, pcr.pcr_vlr, pcr.pcr_vlr_ser, pcr.pcr_vlr_csa, pcr.pcr_vlr_cse, pcr.pcr_obs, :dataAtual ");
            query.append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" pcr ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnvAntigo ON (pcr.cnv_codigo = cnvAntigo.cnv_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rseAntigo ON (pcr.rse_codigo = rseAntigo.rse_codigo and rseAntigo.org_codigo = cnvAntigo.org_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnvNovo ON (cnvNovo.svc_codigo = cnvAntigo.svc_codigo and cnvNovo.csa_codigo = cnvAntigo.csa_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rseNovo ON (rseNovo.org_codigo = cnvNovo.org_codigo)");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" pcrNovo on (rseNovo.rse_codigo = pcrNovo.rse_codigo and cnvNovo.cnv_codigo = pcrNovo.cnv_codigo and pcr.tps_codigo = pcrNovo.tps_codigo) ");
            query.append(" WHERE pcr.tps_codigo = :tpsCodigo ");
            query.append(" AND rseAntigo.rse_codigo = :rseCodAnt ");
            query.append(" AND rseNovo.rse_codigo = :rseCodNovo ");
            query.append(" AND pcrNovo.rse_codigo IS NULL ");

            queryParams.addValue("rseCodNovo", rseCodNovo);
            queryParams.addValue("rseCodAnt", rseCodAnt);
            queryParams.addValue("tpsCodigo", CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            queryParams.addValue("dataAtual", DateHelper.getSystemDatetime());

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Copia todos os bloqueios de convênio do servidor de um convênio origem para outro destino, registrando
     * log da operação. ATENÇÃO: só faz insert diretamente, pressupondo que não existem bloqueios para o convênio destino
     * @param cnvCodOrigem : código do convênio de origem
     * @param cnvCodDestino  : código do convênio de destino
     * @param responsavel : usuário responsável pela operação
     */
    @Override
    public void copiaBloqueioCnvPorConvenio(String cnvCodOrigem, String cnvCodDestino, AcessoSistema responsavel) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;
        try {
            Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());

            conn = DBHelper.makeConnection();
            StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" (rse_codigo, cnv_codigo, tps_codigo, pcr_vlr, pcr_obs, pcr_data_cadastro) ");
            query.append(" SELECT rse_codigo, ? as cnv_codigo, tps_codigo, pcr_vlr, pcr_vlr_ser, pcr_vlr_csa, pcr_vlr_cse, pcr_obs, ? ");
            query.append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR);
            query.append(" WHERE cnv_codigo = ? ");
            query.append(" AND tps_codigo = ? ");

            preStat = conn.prepareStatement(query.toString());
            preStat.setString(1, cnvCodDestino);
            preStat.setTimestamp(2, agora);
            preStat.setString(3, cnvCodOrigem);
            preStat.setString(4, CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            LOG.trace(query.toString());
            preStat.executeUpdate();
            preStat.close();

            // TEN_CAMPO_ENT_00: TPS_CODIGO
            // TEN_CAMPO_ENT_01: RSE_CODIGO
            // TEN_CAMPO_ENT_02: CNV_CODIGO

            query.setLength(0);
            query.append(" INSERT INTO ").append(Columns.TB_LOG).append(" (tlo_codigo, ten_codigo, usu_codigo, fun_codigo, log_ip, log_data, log_obs, log_canal, log_cod_ent_00, log_cod_ent_01, log_cod_ent_02) ");
            query.append(" SELECT ?, ?, ?, ?, ?, ?, ?, ?, tps_codigo, rse_codigo, cnv_codigo ");
            query.append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR);
            query.append(" WHERE cnv_codigo = ? ");
            query.append(" AND tps_codigo = ? ");

            String logObs = ApplicationResourcesHelper.getMessage("rotulo.log.acao", responsavel) + ": "
                    + ApplicationResourcesHelper.getMessage("rotulo.log.operacoes.1", responsavel) + "<BR>"
                    + ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.convenios.para.novo.convenio", responsavel);

            preStat = conn.prepareStatement(query.toString());
            preStat.setString(1, Log.LOG_INFORMACAO);
            preStat.setString(2, Log.PARAM_CNV_REGISTRO_SERVIDOR);
            preStat.setString(3, responsavel.getUsuCodigo());
            preStat.setString(4, responsavel.getFunCodigo());
            preStat.setString(5, responsavel.getIpUsuario());
            preStat.setTimestamp(6, agora);
            preStat.setString(7, logObs);
            preStat.setString(8, CanalEnum.WEB.getCodigo());
            preStat.setString(9, cnvCodDestino);
            preStat.setString(10, CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            LOG.trace(query.toString());
            preStat.executeUpdate();

        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void updateCnvVincCsaSvc(String csaCodigo, String svcCodigo, List<String> vrsCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        Connection conn = null;
        String insertSql = "INSERT INTO tb_convenio_vinculo_registro (vrs_codigo, csa_codigo, svc_codigo) VALUES (?,?,?)";
        String deleteSql = "DELETE FROM tb_convenio_vinculo_registro WHERE vrs_codigo=? AND csa_codigo=? AND svc_codigo=?";
        PreparedStatement psi = null;
        PreparedStatement psd = null;

        try {
            conn = DBHelper.makeConnection();
            psi = conn.prepareStatement(insertSql);
            psd = conn.prepareStatement(deleteSql);

            final Collection<ConvenioVinculoRegistro> cvrList = ConvenioVinculoRegistroHome.findByCsaSvcCodigo(csaCodigo, svcCodigo);

            final ListaCnvVinculoRegistroFaltanteQuery query = new ListaCnvVinculoRegistroFaltanteQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.vrsCodigos = vrsCodigos;

            final List<TransferObject> cvrFaltante = query.executarDTO();
            for (TransferObject cvr : cvrFaltante) {
                final String vrsCodigo = cvr.getAttribute(Columns.VRS_CODIGO).toString();
                psi.setString(1, vrsCodigo);
                psi.setString(2, csaCodigo);
                psi.setString(3, svcCodigo);
                psi.addBatch();
            }

            for (ConvenioVinculoRegistro cvr : cvrList) {
                final String vrsCodigo = cvr.getVrsCodigo();
                if (vrsCodigos.contains(vrsCodigo)) {
                    psd.setString(1, vrsCodigo);
                    psd.setString(2, csaCodigo);
                    psd.setString(3, svcCodigo);
                    psd.addBatch();
                }
            }

            if (!cvrList.isEmpty()) {
                psd.executeBatch();
            }

            if (!cvrFaltante.isEmpty()) {
                psi.executeBatch();
            }
        } catch (SQLException | HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(psi);
            DBHelper.closeStatement(psd);
            DBHelper.releaseConnection(conn);
        }
    }
}
