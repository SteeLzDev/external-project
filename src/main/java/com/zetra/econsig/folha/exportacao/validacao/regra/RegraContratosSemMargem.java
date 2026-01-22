package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegraContratosSemMargem</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se
 *    existe no movimento, contratos sem saldo de margem.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraContratosSemMargem extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraContratosSemMargem.class);

    /** Parâmetros de sistema necessários para validação da regra */
    private final boolean exportacaoInicial;

    public RegraContratosSemMargem() {
        exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        LOG.debug("Exportação somente inicial? " + exportacaoInicial);
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        if (!exportacaoInicial) {
            LOG.warn("A regra de validação de movimento 'RegraContratosSemMargem' somente se aplica a sistemas de movimento inicial.");
            return;
        }

        // Define os códigos da regra atual.
        rvaCodigo = rva.getRvaCodigo();
        rvmCodigo = regra.getRvmCodigo();
        this.estCodigos = estCodigos;
        this.orgCodigos = orgCodigos;

        periodo = DateHelper.format(rva.getRvaPeriodo(), "yyyy-MM-dd");

        resultado = new ResultadoRegraValidacaoMovimentoTO(rvaCodigo, regra.getRvmCodigo());
        String rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK;
        StringBuilder rrvValorEncontrado = new StringBuilder();

        long qtdErros = buscaQtdLinhasInvalidas();
        long qtdBase = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_ALTERACAO)
                     + buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_EXCLUSAO)
                     + buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_INCLUSAO);
        rrvValorEncontrado.append(qtdErros).append("/").append(qtdBase);
        // Se for menor que zero é porque deu erro na busca do valor
        if (qtdBase < 0) {
            rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
        } else {
            long diferencaPercentual;
            if (qtdBase > 0) {
                diferencaPercentual = Math.round(100 * ((double) qtdErros / qtdBase));
            } else {
                diferencaPercentual = 0;
            }
            int limiteErro = regra.getRvmLimiteErro() != null ? Integer.parseInt(regra.getRvmLimiteErro()) : Integer.MAX_VALUE;
            int limiteAviso = regra.getRvmLimiteAviso() != null ? Integer.parseInt(regra.getRvmLimiteAviso()) : Integer.MAX_VALUE;

            if (diferencaPercentual > limiteErro) {
                rrvValorEncontrado.append(" **");
                rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
            } else if (diferencaPercentual > limiteAviso) {
                rrvValorEncontrado.append(" *");
                if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(rrvResultado)) {
                    rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                }
            }
        }
        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    /**
     * Busca a qtd de servidores que o saldo do movimento não cabe na margem enviada pela folha.
     * @return
     */
    private long buscaQtdLinhasInvalidas() {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_movimento_matricula ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_movimento_matricula (rse_codigo varchar(32), ade_numero int, ade_inc_margem smallint, valor decimal(13,2)) ");
            query.append("SELECT DISTINCT amv.rse_codigo, ade.ade_numero, ade.ade_inc_margem, ");
            query.append("case when amv.amv_operacao = 'I' then amv.ade_vlr else (case when amv.amv_operacao = 'E' then -1 * coalesce(ade.ade_vlr_folha, 0) else amv.ade_vlr - coalesce(ade.ade_vlr_folha, 0) end) end AS valor ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
            query.append("INNER JOIN tb_arquivo_movimento_validacao amv ON (amv.rse_codigo = ade.rse_codigo AND amv.cnv_codigo = cnv.cnv_codigo AND coalesce(amv.ade_cod_reg, '6') = ade.ade_cod_reg AND coalesce(amv.ade_indice, '') = coalesce(ade.ade_indice, '')) ");
            query.append("LEFT OUTER JOIN tb_ocorrencia_autorizacao oca ON (oca.ade_codigo = ade.ade_codigo AND oca.toc_codigo = '6') ");
            query.append("WHERE (ade.ade_int_folha = 1) ");
            query.append("AND (ade.ade_inc_margem in (1,2,3)) ");
            query.append("AND (ade.ade_ano_mes_ini <= pex.pex_periodo) ");
            query.append("AND ((ade.sad_codigo = '4' AND amv.amv_operacao = 'I') ");
            query.append("  OR (ade.sad_codigo = '5' AND amv.amv_operacao in ('I', 'A') AND (ade.ade_vlr_folha IS NULL OR ade.ade_vlr_folha <> ade.ade_vlr)) ");
            query.append("  OR (ade.sad_codigo = '8' AND amv.amv_operacao = 'E' AND oca.oca_periodo = pex.pex_periodo) ");
            query.append("  OR (ade.sad_codigo = '8' AND amv.amv_operacao in ('I', 'A') AND oca.oca_periodo > pex.pex_periodo)) ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND org.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND org.org_codigo IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_consolidado_movimento ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_consolidado_movimento (rse_codigo varchar(32), ade_inc_margem smallint, movimento decimal(13,2), primary key (rse_codigo, ade_inc_margem)) ");
            query.append("SELECT rse_codigo, ade_inc_margem, sum(valor) AS movimento FROM tb_tmp_movimento_matricula ");
            query.append("GROUP BY rse_codigo, ade_inc_margem");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("SELECT count(*) AS qtd ");
            query.append("FROM tb_tmp_consolidado_movimento tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("WHERE (movimento > 0) ");
            query.append("AND (rse.srs_codigo ").append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("'))");
            query.append("and ((tmp.ade_inc_margem = 1 and tmp.movimento - rse.rse_margem   > 0.01) or ");
            query.append("     (tmp.ade_inc_margem = 2 and tmp.movimento - rse.rse_margem_2 > 0.01) or ");
            query.append("     (tmp.ade_inc_margem = 3 and tmp.movimento - rse.rse_margem_3 > 0.01)) ");
            LOG.debug(query.toString());
            final Long qtd = jdbc.queryForObject(query.toString(), queryParams, Long.class);

            if (qtd != null) {
                 return qtd;
            } else {
                return -1;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }
}
