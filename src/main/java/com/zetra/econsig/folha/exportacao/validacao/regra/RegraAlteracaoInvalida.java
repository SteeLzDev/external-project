package com.zetra.econsig.folha.exportacao.validacao.regra;

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
 * <p>Title: RegraAlteracaoInvalida</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se não
 *    está enviando comandos de alteração.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraAlteracaoInvalida extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraAlteracaoInvalida.class);

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        if (!ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            LOG.warn("A regra de validação de movimento 'RegraAlteracaoInvalida' somente se aplica a sistemas de movimento inicial.");
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

        long qtdRegistrosArqMov = buscaQtdRegistrosArquivo();
        long qtdAlteracaoArqMov = buscaQtdRegistrosArquivo(Regra.TIPO_OPERACAO_ALTERACAO);

        rrvValorEncontrado.append("A: ").append(qtdAlteracaoArqMov).append("/").append(qtdRegistrosArqMov);

        long diferencaPercentual;
        if (qtdRegistrosArqMov > 0) {
            diferencaPercentual = Math.round(100 * ((double) qtdAlteracaoArqMov / qtdRegistrosArqMov));
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
        if (qtdAlteracaoArqMov > 0) {
            rrvValorEncontrado.append(getListaContratosComProblema(false, buscaLinhasInvalidas()));
        }
        rrvValorEncontrado.append("<br/>");

        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    private List<TransferObject> buscaLinhasInvalidas() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            List<String> camposChave = getAmvCamposPreenchidos();
            String fields = TextHelper.join(camposChave, ",tb_arquivo_movimento_validacao.");
            StringBuilder query = new StringBuilder();
            query.append("SELECT tb_arquivo_movimento_validacao.").append(fields);
            query.append(" FROM ").append(Columns.TB_ARQUIVO_MOVIMENTO_VALIDACAO);
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            query.append(" WHERE ").append(Columns.AMV_OPERACAO).append(" = 'A'");
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
