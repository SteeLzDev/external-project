package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AbstractRegraVrcMediaRegistros</p>
 * <p>Description: Classe abstrata com a implementação MYSQL da regra com a comparação da quantidade de registros gerados
 *                 ao longo dos últimos períodos.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractRegraVrcMediaRegistros extends Regra {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractRegraVrcMediaRegistros.class);

    protected String operacao;
    private static final short PERIODO_MEDIA = 12;

    public AbstractRegraVrcMediaRegistros(String operacao) {
        this.operacao = operacao;
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        // Define os códigos da regra atual.
        rvaCodigo = rva.getRvaCodigo();
        rvmCodigo = regra.getRvmCodigo();
        this.estCodigos = estCodigos;
        this.orgCodigos = orgCodigos;
        periodo = DateHelper.format(rva.getRvaPeriodo(), "yyyy-MM-dd");

        int limiteErro = Integer.parseInt(regra.getRvmLimiteErro());
        int limiteAviso = Integer.parseInt(regra.getRvmLimiteAviso());

        resultado = new ResultadoRegraValidacaoMovimentoTO(rvaCodigo, rvmCodigo);
        String rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK;
        StringBuilder rrvValorEncontrado = new StringBuilder();

        List<TransferObject> variacaoCsa = buscaVariacaoPorCsa();

        if (variacaoCsa != null) {
            rrvValorEncontrado.append("Consignatárias com variação acima do limite de comandos de ");
            rrvValorEncontrado.append((operacao.equals("I") ? "inclusão" : (operacao.equals("A") ? "alteração" : "exclusão"))).append(": ");

            Iterator<TransferObject> it = variacaoCsa.iterator();
            while (it.hasNext()) {
                TransferObject next = it.next();
                String csaNome = next.getAttribute("CSA_NOME").toString();
                String codVerba = (next.getAttribute("COD_VERBA") != null ? next.getAttribute("COD_VERBA").toString() : "VAZIO");
                BigDecimal qtdAtual = new BigDecimal(next.getAttribute("QTD_ATUAL").toString());
                BigDecimal qtdAnterior = new BigDecimal(next.getAttribute("QTD_ANTERIOR").toString());
                if (qtdAnterior.compareTo(new BigDecimal(0)) > 0 ) {
                    Double diferencaPercentual =  100 * Math.abs(qtdAtual.doubleValue() - qtdAnterior.doubleValue()) / qtdAnterior.doubleValue();

                    if (diferencaPercentual >= limiteErro) {
                        rrvValorEncontrado.append("<br>\n").append(csaNome).append(" - ").append(codVerba).append(": ").append(qtdAtual).append("/").append(qtdAnterior).append(" **");
                        rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
                    } else if (diferencaPercentual >= limiteAviso) {
                        rrvValorEncontrado.append("<br>\n").append(csaNome).append(": ").append(qtdAtual).append("/").append(qtdAnterior).append(" *");
                        if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(rrvResultado)) {
                            rrvResultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                        }
                    }
                }
            }

            if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(rrvResultado)) {
                rrvValorEncontrado.append("OK");
            }
        }

        resultado.setRrvResultado(rrvResultado);
        resultado.setRrvValorEncontrado(rrvValorEncontrado.toString());
    }

    private List<TransferObject> buscaVariacaoPorCsa() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            query.append("select coalesce(nullif(csa.csa_nome_abrev, ''), csa.csa_nome) as CSA_NOME, cnv.cnv_cod_verba as COD_VERBA, ");

            query.append("sum(coalesce((select sum(hmf_qtd)/").append("(select case when count(distinct innerhmf.hmf_periodo) = 0 then 1 when count(distinct innerhmf.hmf_periodo) < ");
            query.append(PERIODO_MEDIA).append(" then count(distinct innerhmf.hmf_periodo) else ").append(PERIODO_MEDIA).append(" end");
            query.append(" from tb_historico_mov_fin innerhmf where innerhmf.cnv_codigo = cnv.cnv_codigo and hmf_operacao = '").append(operacao).append("' group by cnv.cnv_cod_verba) ");
            query.append(" from tb_historico_mov_fin hmf where hmf.cnv_codigo = cnv.cnv_codigo and hmf_periodo > date_sub('").append(periodo).append("', interval ").append("(select case when count(distinct interhmf.hmf_periodo) = 0 then 1 when count(distinct interhmf.hmf_periodo) < ");
            query.append(PERIODO_MEDIA).append(" then count(distinct interhmf.hmf_periodo) else ").append(PERIODO_MEDIA).append(" end");
            query.append(" from tb_historico_mov_fin interhmf where interhmf.cnv_codigo = cnv.cnv_codigo and hmf_operacao = '").append(operacao).append("' group by cnv.cnv_cod_verba) ").append(" month) group by  cnv.cnv_cod_verba), 0)) as QTD_ANTERIOR, ");

            query.append("sum(coalesce((select count(*) from tb_arquivo_movimento_validacao amv where amv.cnv_codigo = cnv.cnv_codigo and amv_operacao = '").append(operacao).append("'), 0)) as QTD_ATUAL ");
            query.append("from tb_consignataria csa ");
            query.append("inner join tb_convenio cnv on (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("where 1=1 ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("and org.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND org.org_codigo in (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            query.append("group by csa.csa_codigo, cnv.cnv_cod_verba ");
            query.append("order by 1 ");

            LOG.debug(query.toString());
            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "CSA_NOME,COD_VERBA,QTD_ANTERIOR,QTD_ATUAL", MySqlDAOFactory.SEPARADOR);
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
