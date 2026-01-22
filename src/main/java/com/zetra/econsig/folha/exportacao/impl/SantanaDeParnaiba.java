package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;

/**
 * <p>Title: SantandaDeParnaiba</p>
 * <p>Description: Implementações específicas para Santana de Parnaíba.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SantanaDeParnaiba extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SantanaDeParnaiba.class);

    private static final String DATA_INI_MARGEM_ADICIONAL = "2021-05-07 00:00:00";
    private static final String DATA_FIM_MARGEM_ADICIONAL = "2021-12-31 23:59:59";

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Só analisa contratos que incidem na margem 1, margem Empréstimo
        List<Short> marCodigos = new ArrayList<Short>();
        marCodigos.add(CodedValues.INCIDE_MARGEM_SIM);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("SantandaDeParnaiba.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, marCodigos);
        LOG.debug("fim - SantandaDeParnaiba.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    /**
     * Atualiza o motivo de não exportação dos contratos passados por parâmetro,
     * previamente selecionados na rotina de validação de margem
     * @param adeImpropria
     * @param tipoMotivoNaoExportacao
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void gravaMotivoNaoExportacao(List<String> adeImpropria, TipoMotivoNaoExportacaoEnum tipoMotivoNaoExportacao) throws DataAccessException {
        if (adeImpropria != null && !adeImpropria.isEmpty()) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("update tb_aut_desconto ");
            query.append("set mne_codigo = :mneCodigo ");
            query.append("where ade_codigo in (:adeCodigos) ");
            query.append("and (ade_data not between '").append(DATA_INI_MARGEM_ADICIONAL).append("' and '").append(DATA_FIM_MARGEM_ADICIONAL).append("') ");

            queryParams.addValue("adeCodigos", adeImpropria);
            queryParams.addValue("mneCodigo", tipoMotivoNaoExportacao.getCodigo());
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Remove da tabela de exportação as consignações na lista passada por parâmetro
     * @param adeCodigos
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void excluirContratos(List<String> adeCodigos) throws DataAccessException {
        if (adeCodigos != null && !adeCodigos.isEmpty()) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.exportacao.removendo.contratos.sem.margem", (AcessoSistema)null));

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();
            query.append("delete from tb_tmp_exportacao ");
            query.append("where ade_codigo in (:adeCodigos) ");
            query.append("and (ade_data not between '").append(DATA_INI_MARGEM_ADICIONAL).append("' and '").append(DATA_FIM_MARGEM_ADICIONAL).append("') ");
            queryParams.addValue("adeCodigos", adeCodigos);
            jdbc.update(query.toString(), queryParams);
        }
    }

    /**
     * Atualiza o valor dos contratos para pagamento parcial daqueles que não cabem integralmente na
     * margem, em sistemas que permite esta rotina, somente na tabela de exportação
     * @param parcialmenteSemMargem
     * @param stat
     * @throws SQLException
     */
    @Override
    protected void atualizarParcelaPgtParcial(Map<String, BigDecimal> parcialmenteSemMargem) throws SQLException {
        if (parcialmenteSemMargem != null && !parcialmenteSemMargem.isEmpty()) {
            StringBuilder query = new StringBuilder();
            query.append("/*skip_log*/");
            query.append("update tb_tmp_exportacao ");
            query.append("set ade_vlr = ? ");
            query.append("where ade_codigo = ? ");
            query.append("and (ade_data not between '").append(DATA_INI_MARGEM_ADICIONAL).append("' and '").append(DATA_FIM_MARGEM_ADICIONAL).append("') ");

            Connection conn = null;
            PreparedStatement preStat = null; 
            try {
                conn = DBHelper.makeConnection();
                preStat = conn.prepareStatement(query.toString());
                for (Map.Entry<String, BigDecimal> entrada : parcialmenteSemMargem.entrySet()) {
                    preStat.setBigDecimal(1, entrada.getValue());
                    preStat.setString(2, entrada.getKey());
                    preStat.executeUpdate();
                }
            } finally {
                DBHelper.closeStatement(preStat);
                DBHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Lista os contratos de servidores com margem negativa, deconsiderando as mudanças após o corte, pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * @param stat
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 2 and tmp.rse_margem_rest_2 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        if (marCodigos != null && !marCodigos.isEmpty()) {
            query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_data,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

}
