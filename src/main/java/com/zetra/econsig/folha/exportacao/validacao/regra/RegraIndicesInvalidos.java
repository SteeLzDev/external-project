package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegraIndicesInvalidos</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se não
 *    está enviando contratos com índices inválidos.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraIndicesInvalidos extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraIndicesInvalidos.class);

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            LOG.warn("A regra de validação de movimento 'RegraIndicesInvalidos' somente se aplica a sistemas que permitem cadastro de índice.");
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

        long qtdAlteracaoArqMov = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_ALTERACAO);
        long qtdExclusaoArqMov  = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_EXCLUSAO);
        long qtdInclusaoArqMov  = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_INCLUSAO);

        long qtdAlteracao = 0;
        long qtdExclusao  = 0;
        long qtdInclusao  = 0;

        List<TransferObject> erros = buscaQtdLinhasInvalidas();
        Iterator<TransferObject> it = erros.iterator();
        TransferObject next = null;
        String operacao = null;
        while (it.hasNext()) {
            next = it.next();
            operacao = next.getAttribute("amv_operacao").toString();
            if (operacao.equals(Regra.TIPO_OPERACAO_ALTERACAO)) {
                qtdAlteracao = Long.parseLong(next.getAttribute("qtd").toString());
            } else if (operacao.equals(Regra.TIPO_OPERACAO_EXCLUSAO)) {
                qtdExclusao = Long.parseLong(next.getAttribute("qtd").toString());
            } else if (operacao.equals(Regra.TIPO_OPERACAO_INCLUSAO)) {
                qtdInclusao = Long.parseLong(next.getAttribute("qtd").toString());
            }
        }

        String[] titulos = {"A", "E", "I"};
        long[] qtds = {qtdAlteracao, qtdExclusao, qtdInclusao};
        long[] qtdsBase = {qtdAlteracaoArqMov, qtdExclusaoArqMov, qtdInclusaoArqMov};
        for (int i=0; i < titulos.length; i++) {
            long qtdErros = qtds[i];
            long qtdBase = qtdsBase[i];
            rrvValorEncontrado.append(titulos[i]).append(": ").append(qtdErros).append("/").append(qtdBase);
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
                if (qtdErros > 0) {
                    rrvValorEncontrado.append(getListaContratosComProblema(false, buscaLinhasInvalidas(titulos[i])));
                }
            }
            rrvValorEncontrado.append("<br/>");
        }
        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    /**
     * Busca a qtd de linhas inválidas, de exclusão e alteração com prazo final passado.
     * @return
     */
    private List<TransferObject> buscaQtdLinhasInvalidas() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            Object paramLimiteMaxIndice = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, AcessoSistema.getAcessoUsuarioSistema());
            String limiteMaxIndice = (!TextHelper.isNull(paramLimiteMaxIndice) ? paramLimiteMaxIndice.toString() : "99");

            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(Columns.AMV_OPERACAO).append(" AS amv_operacao, count(*) AS qtd");
            query.append(" FROM ").append(Columns.TB_ARQUIVO_MOVIMENTO_VALIDACAO);
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            query.append(" WHERE LENGTH(TRIM(COALESCE(").append(Columns.AMV_ADE_INDICE).append(", ''))) <> ").append(limiteMaxIndice.length());
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            query.append(" GROUP BY ").append(Columns.AMV_OPERACAO);

            LOG.debug(query.toString());
            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "amv_operacao,qtd", MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private List<TransferObject> buscaLinhasInvalidas(String operacao) {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            Object paramLimiteMaxIndice = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, AcessoSistema.getAcessoUsuarioSistema());
            String limiteMaxIndice = (!TextHelper.isNull(paramLimiteMaxIndice) ? paramLimiteMaxIndice.toString() : "99");

            List<String> camposChave = getAmvCamposPreenchidos();
            String fields = TextHelper.join(camposChave, ",tb_arquivo_movimento_validacao.");
            StringBuilder query = new StringBuilder();
            query.append("SELECT tb_arquivo_movimento_validacao.").append(fields);
            query.append(" FROM ").append(Columns.TB_ARQUIVO_MOVIMENTO_VALIDACAO);
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            query.append(" WHERE ").append(Columns.AMV_OPERACAO).append(" = '").append(operacao).append("'");
            query.append(" AND LENGTH(TRIM(COALESCE(").append(Columns.AMV_ADE_INDICE).append(", ''))) <> ").append(limiteMaxIndice.length());
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            LOG.debug(query.toString());
            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields.replaceAll("tb_arquivo_movimento_validacao.", ""), MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
