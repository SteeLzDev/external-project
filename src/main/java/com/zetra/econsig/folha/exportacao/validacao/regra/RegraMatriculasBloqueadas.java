package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegraMatriculasBloqueadas</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se não
 *    está enviado contratos de matrículas BLOQUEADAS.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraMatriculasBloqueadas extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraMatriculasBloqueadas.class);

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
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
        long qtdInclusaoArqMov  = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_INCLUSAO);

        long qtdAlteracao = 0;
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
            } else if (operacao.equals(Regra.TIPO_OPERACAO_INCLUSAO)) {
                qtdInclusao = Long.parseLong(next.getAttribute("qtd").toString());
            }
        }

        String[] titulos = {"A", "I"};
        long[] qtds = {qtdAlteracao, qtdInclusao};
        long[] qtdsBase = {qtdAlteracaoArqMov, qtdInclusaoArqMov};
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

    private List<TransferObject> buscaQtdLinhasInvalidas() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT amv_operacao, count(*) AS qtd ");
            query.append("FROM tb_arquivo_movimento_validacao ");
            query.append("INNER JOIN tb_registro_servidor ON (tb_registro_servidor.rse_codigo = tb_arquivo_movimento_validacao.rse_codigo) ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN tb_param_convenio_registro_ser ON (tb_registro_servidor.rse_codigo = tb_param_convenio_registro_ser.rse_codigo ");
            query.append("AND tb_convenio.cnv_codigo = tb_param_convenio_registro_ser.cnv_codigo ");
            query.append("AND (tb_param_convenio_registro_ser.pcr_vlr = '0' and tb_param_convenio_registro_ser.tps_codigo = '20')) ");
            query.append("WHERE tb_arquivo_movimento_validacao.amv_operacao in ('A', 'I') ");
            query.append("AND (tb_registro_servidor.srs_codigo IN ('" + TextHelper.join(CodedValues.SRS_BLOQUEADOS, "','") + "') or tb_param_convenio_registro_ser.rse_codigo is not null) ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            query.append("GROUP BY amv_operacao ");

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
            List<String> camposChave = getAmvCamposPreenchidos();
            String fields = TextHelper.join(camposChave, ",tb_arquivo_movimento_validacao.");
            StringBuilder query = new StringBuilder();
            query.append("SELECT tb_arquivo_movimento_validacao.").append(fields);
            query.append(" FROM tb_arquivo_movimento_validacao ");
            query.append("INNER JOIN tb_registro_servidor ON (tb_registro_servidor.rse_codigo = tb_arquivo_movimento_validacao.rse_codigo) ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN tb_param_convenio_registro_ser ON (tb_registro_servidor.rse_codigo = tb_param_convenio_registro_ser.rse_codigo ");
            query.append("AND tb_convenio.cnv_codigo = tb_param_convenio_registro_ser.cnv_codigo ");
            query.append("AND (tb_param_convenio_registro_ser.pcr_vlr = '0' and tb_param_convenio_registro_ser.tps_codigo = '20')) ");
            query.append("WHERE tb_arquivo_movimento_validacao.amv_operacao = '").append(operacao).append("' ");
            query.append("AND (tb_registro_servidor.srs_codigo IN ('" + TextHelper.join(CodedValues.SRS_BLOQUEADOS, "','") + "') or tb_param_convenio_registro_ser.rse_codigo is not null) ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos) ");
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
