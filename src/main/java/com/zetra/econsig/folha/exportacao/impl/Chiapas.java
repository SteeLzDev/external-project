package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Chiapas</p>
 * <p>Description: Implementações específicas para o sistema Chiapas/MX.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Chiapas extends Quinzenal {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Chiapas.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // Altera a situação dos comandos para Alteração quando o contrato estiver aberto (4, 5, 11 e 15)
            // e já possuir parcelas geradas, porém não pagas, visto que a folha preserva parcela e deve ser
            // enviado comando de alteração, ao invés de inclusão.
            final StringBuilder query = new StringBuilder();
            query.append("update tb_tmp_exportacao tmp ");
            query.append("set tmp.situacao = 'A' ");
            query.append("where tmp.sad_codigo in ('4','5','11','15') ");
            query.append("and coalesce(tmp.ade_prd_pagas, 0) = 0 ");
            query.append("and exists (select 1 from tb_parcela_desconto prd where tmp.ade_codigo = prd.ade_codigo) ");
            // DESENV-7510: se houver ocorrência de reimplante para o contrato no período do movimento, não deve fazer o update para 'A'
            query.append("and not exists (select 1 from tb_ocorrencia_autorizacao oca ");
            query.append("where oca.toc_codigo in ('");
            query.append(CodedValues.TOC_RELANCAMENTO).append("','").append(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR).append("') ");
            query.append("and tmp.ade_codigo = oca.ade_codigo and tmp.oca_periodo = oca.oca_periodo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}