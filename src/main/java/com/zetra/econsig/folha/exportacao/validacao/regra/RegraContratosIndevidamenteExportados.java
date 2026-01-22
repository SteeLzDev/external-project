package com.zetra.econsig.folha.exportacao.validacao.regra;

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

/**
 * <p>Title: RegraContratosIndevidamenteExportados</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação dos contratos que foram exportados, mas não deveriam ter sido.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraContratosIndevidamenteExportados extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraContratosIndevidamenteExportados.class);

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

        List<TransferObject> erros = buscaContratosIndevidamenteExportados();
        int qtdErros = erros.size();
        long qtdBase = buscaQtdRegistrosArquivo();

        rrvValorEncontrado.append("Contratos exportados indevidamente ").append(qtdErros).append("/").append(qtdBase);
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
                rrvValorEncontrado.append(getListaContratosComProblema(false, erros));
            }
        }
        rrvValorEncontrado.append("<br>");
        LOG.info(rrvValorEncontrado.toString());
        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    /**
     * Busca contratos que não foram exportados, mas deveriam ter sido.
     * @return
     */
    private List<TransferObject> buscaContratosIndevidamenteExportados() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            List<String> camposChave = getAmvCamposPreenchidos();
            StringBuilder query = new StringBuilder();

            // VERIFICA SE TEM ALGUM CONTRATO QUE FOI EXPORTADO A MAIS
            query.append("SELECT amv.rse_matricula");
            for (String campo : camposChave) {
                query.append(", amv.").append(campo);
            }
            query.append(" FROM tb_arquivo_movimento_validacao amv");
            query.append(" LEFT OUTER JOIN tb_tmp_movimento_validacao tmp ON (amv.rse_matricula = tmp.rse_matricula");
            for (String campo : camposChave) {
                if (campo.equalsIgnoreCase("ade_vlr")) {
                    query.append(" AND (amv.").append(campo).append(" <=> tmp.").append(campo);
                    if (!camposChave.contains("ade_tipo_vlr")) {
                        query.append(" OR (tmp.ade_tipo_vlr = 'P' AND amv.ade_tipo_vlr IS NULL)");
                    }
                    query.append(")");
                } else {
                    query.append(" AND amv.").append(campo).append(" = tmp.").append(campo);
                }
            }
            query.append(")");
            query.append(" WHERE tmp.rse_matricula IS NULL");

            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND tmp.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND tmp.org_codigo IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            LOG.debug(query.toString());

            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), TextHelper.join(camposChave, MySqlDAOFactory.SEPARADOR), MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
