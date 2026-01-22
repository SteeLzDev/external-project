package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;

/**
 * <p>Title: CamprevAutomatizacao</p>
 * <p>Description: Implementações específicas para Camprev validando margens na tabela de registro servidor.
 * Essa classe deverá ser excluída após implantação das modificações para utilização da classe {@link Camprev} que valida as margens na tabela de margem registro servidor.
 * </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CamprevAutomatizacao2 extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CamprevAutomatizacao2.class);

    private static final String DATA_INI_MARGEM_ADICIONAL = "2021-09-25 00:00:00";
    private static final String DATA_FIM_MARGEM_ADICIONAL = "2021-12-31 23:59:59";

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            // Remove da tabela de exportação as ADE que não cabem na margem
            LOG.debug("CamprevAutomatizacao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
            removerContratosSemMargemMovimentoMensal();
            LOG.debug("fim - CamprevAutomatizacao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Remove da tabela de exportação os contratos que não cabem na margem.
     * A implementação considera qualquer uma das margens 1, 2 ou 3.
     * Se for margem casada, faz tratamento diferencial.
     * Método implementado para exportação mensal.
     * @param stat
     * @throws ExportaMovimentoException
     */
    @Override
    @Deprecated
    protected void removerContratosSemMargemMovimentoMensal() throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

        StringBuilder query = new StringBuilder();

        // Lista os contratos de servidores com margem negativa pela ordem de exportação
        if (margem1CasadaMargem3 || margem123Casadas || margem1CasadaMargem3Esq || margem123CasadasEsq || margem1CasadaMargem3Lateral) {
            throw new ExportaMovimentoException("mensagem.erro.exportacao.rotina.casamento.margem.nao.suportado", responsavel);
        } else {
            // OBS: A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
            // contratos removidos à margem restante até que a mesma seja positiva.
            query.append("select tmp.rse_codigo, tmp.rse_margem_rest, tmp.rse_margem_rest_2, tmp.rse_margem_rest_3, tmp.ade_codigo, tmp.ade_vlr, ade.ade_inc_margem ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) ");
            query.append("where ((ade.ade_inc_margem = '1' and tmp.rse_margem_rest   < 0.00) ");
            query.append("    OR (ade.ade_inc_margem = '2' and tmp.rse_margem_rest_2 < 0.00) ");
            query.append("    OR (ade.ade_inc_margem = '3' and tmp.rse_margem_rest_3 < 0.00)) ");
            query.append("order by tmp.rse_codigo, ade.ade_inc_margem, ");
            query.append("coalesce(svc_prioridade, 9999999) + 0 DESC, coalesce(cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
        }

        LOG.debug(query.toString());

        try {
            String fieldsNames = "rse_codigo,rse_margem_rest,rse_margem_rest_2,rse_margem_rest_3,ade_codigo,ade_vlr,ade_inc_margem";
            List<TransferObject> contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
            List<String> adeImpropria = obterContratosSemMargemMovimentoMensal(contratos);

            // Apaga os contratos que não devem ser lançados do último servidor
            if (!adeImpropria.isEmpty()) {
                gravaMotivoNaoExportacao(adeImpropria, TipoMotivoNaoExportacaoEnum.SERVIDOR_SEM_MARGEM_SUFICIENTE);

                excluirContratos(adeImpropria);
                adeImpropria.clear();
            }
        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
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
            query.append("update tb_aut_desconto ade ");
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
}
