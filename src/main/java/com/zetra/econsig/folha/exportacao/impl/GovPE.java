package com.zetra.econsig.folha.exportacao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;

/**
 * <p>Title: GovPE</p>
 * <p>Description: Implementações específicas para GovPE - Governo do Estado de Pernambuco.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GovPE extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GovPE.class);

    /**
     * Remove do conteúdo do movimento as consignações que incidem na margem 1 caso o
     * servidor não possua margem para o envio do valor da parcela.
     * @param orgCodigos
     * @param estCodigos
     * @param verbas
     * @param acao
     * @param opcao
     */
    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Só analisa contratos que incidem na margem 1, margem Empréstimo
        List<Short> marCodigos = new ArrayList<>();
        marCodigos.add(CodedValues.INCIDE_MARGEM_SIM);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("GovPE.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(false, marCodigos);
        LOG.debug("fim - GovPE.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }


    /**
     * Atualiza o motivo de não exportação dos contratos passados por parâmetro,
     * previamente selecionados na rotina de validação de margem. Altera o status
     * também para Estoque Menal, somente para contratos deferidos ou em andamento.
     * @param adeImpropria
     * @param tipoMotivoNaoExportacao
     * @throws DataAccessException
     */
    @Override
    protected void gravaMotivoNaoExportacao(List<String> adeImpropria, TipoMotivoNaoExportacaoEnum tipoMotivoNaoExportacao) throws DataAccessException {
        if (adeImpropria != null && !adeImpropria.isEmpty()) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            String query = "update tb_aut_desconto "
                             + "set mne_codigo = :mneCodigo, "
                             + "sad_codigo = (case when sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "') then '" + CodedValues.SAD_ESTOQUE_MENSAL + "' else sad_codigo end) "
                             + "where ade_codigo in (:adeImpropria)";
            LOG.debug(query);
            queryParams.addValue("adeImpropria", adeImpropria);
            queryParams.addValue("mneCodigo", tipoMotivoNaoExportacao.getCodigo());
            jdbc.update(query, queryParams);
        }
    }

    /**
     * Volta o status dos Estoques para Deferido ou Em andamento para que
     * sejam candidatos na exportação.
     * @param orgCodigos
     * @param estCodigos
     * @param verbas
     * @param acao
     * @param opcao
     * @param responsavel
     */
    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            String query = "update tb_aut_desconto ade "
                         + "set sad_codigo = (case when coalesce(ade_prd_pagas, 0) > 0 then '" + CodedValues.SAD_EMANDAMENTO + "' else '" + CodedValues.SAD_DEFERIDA + "' end) "
                         + "where sad_codigo = '" + CodedValues.SAD_ESTOQUE_MENSAL + "'";

            LOG.debug(query);
            int linhasAfetadas = jdbc.update(query, queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
