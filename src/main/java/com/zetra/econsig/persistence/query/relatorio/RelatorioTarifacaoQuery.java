package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTarifacaoQuery</p>
 * <p>Description: Query para relatório de tarifação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTarifacaoQuery extends ReportHNativeQuery{

    private String periodo;
    private String csaCodigo;
    private List<String> orgCodigos;
    private String cnvCodVerba;
    private List<String> svcCodigos;
    private Boolean tarifacaoPorModalidade;
    private Boolean tarifacaoPorCorrespondente;
    private Boolean exibeColunasAdicionais;
    private List<String> nseCodigos;
    private Boolean servicoSemTarifacao;
    private Boolean tarifacaoPorNatureza;

    public RelatorioTarifacaoQuery() {
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String)criterio.getAttribute("PERIODO");
        csaCodigo = (String)criterio.getAttribute("CSA_CODIGO");
        orgCodigos = (List<String>)criterio.getAttribute("ORG_CODIGO");
        cnvCodVerba = (String)criterio.getAttribute("CNV_COD_VERBA");
        svcCodigos = (List<String>)criterio.getAttribute("SVC_CODIGO");
        if (!TextHelper.isNull(criterio.getAttribute("POR_MODALIDADE"))) {
            tarifacaoPorModalidade = Boolean.valueOf(criterio.getAttribute("POR_MODALIDADE").toString());
        }
        if (!TextHelper.isNull(criterio.getAttribute("POR_CORRESPONDENTE"))) {
            tarifacaoPorCorrespondente = Boolean.valueOf(criterio.getAttribute("POR_CORRESPONDENTE").toString());
        }
        if (!TextHelper.isNull(criterio.getAttribute("COLUNAS_ADICIONAIS"))) {
            exibeColunasAdicionais = Boolean.valueOf(criterio.getAttribute("COLUNAS_ADICIONAIS").toString());
        }
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
        if (!TextHelper.isNull(criterio.getAttribute("SERVICO_SEM_TARIFACAO"))) {
        	servicoSemTarifacao = Boolean.valueOf(criterio.getAttribute("SERVICO_SEM_TARIFACAO").toString());
        }
        if (!TextHelper.isNull(criterio.getAttribute("TARIFACAO_POR_NATUREZA"))) {
        	tarifacaoPorNatureza = Boolean.valueOf(criterio.getAttribute("TARIFACAO_POR_NATUREZA").toString());
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean consolida = ParamSist.paramEquals(CodedValues.TPC_TARIFACAO_CONSOLIDADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CSA AS CSA, ");
        if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            sql.append(" CORRESPONDENTE AS CORRESPONDENTE, ");
            sql.append(" CODIGO_CORRESPONDENTE AS CODIGO_CORRESPONDENTE, ");
        } else {
            sql.append(" '1' AS CORRESPONDENTE, ");
            sql.append(" '1' AS CODIGO_CORRESPONDENTE, ");
        }
        if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
        	sql.append(" SERVICO AS SERVICO, ");
        	sql.append(" NSE_DESCRICAO AS NSE_DESCRICAO, ");
        } else {
        	sql.append(" '1' AS SERVICO, ");
        	sql.append(" '1' AS NSE_DESCRICAO, ");
        }
        sql.append(" CNPJ AS CNPJ, FONE AS FONE, FORMA AS FORMA, to_decimal(TARIFA, 13, 2) AS TARIFA, SUM(QUANTIDADE1) AS QUANTIDADE, to_decimal(SUM(VALOR1), 13, 2) AS VALOR, ");
        sql.append(" to_decimal(CASE WHEN FORMA = 'VALOR FIXO' THEN SUM(QUANTIDADE1 * TARIFA) ELSE SUM((VALOR1 * TARIFA)/100) END, 13, 2) AS VALOR_TARIFADO, ");
        sql.append(" SUM(VALOR1) - to_decimal(CASE WHEN FORMA = 'VALOR FIXO' THEN SUM(QUANTIDADE1 * TARIFA) ELSE SUM((VALOR1 * TARIFA)/100) END, 13, 2) AS VALOR_ADE_REPASSE ");
        sql.append(", VERBA AS VERBA ");
        if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            sql.append(", CSE_NOME AS CSE_NOME ");
            sql.append(", NUM_CRM AS NUM_CRM ");
            sql.append(", PERIODO AS PERIODO ");
            sql.append(", DATA_PROCESSAMENTO AS DATA_PROCESSAMENTO ");
            sql.append(", CODIGO_NATUREZA AS CODIGO_NATUREZA ");
            sql.append(", DESC_NATUREZA AS DESC_NATUREZA ");
            sql.append(", SUM(VALOR_ENVIADO_DESCONTO) AS VALOR_ENVIADO_DESCONTO ");
            sql.append(", SUM(VALOR_DESCONTADO) AS VALOR_DESCONTADO ");
            sql.append(", to_decimal(CASE WHEN FORMA = 'VALOR FIXO' THEN SUM(QUANTIDADE1 * TARIFA) ELSE SUM((VALOR_DESCONTADO * TARIFA)/100) END, 13, 2) AS VALOR_RETIDO ");
            sql.append(", SUM(VALOR_DESCONTADO) - to_decimal(CASE WHEN FORMA = 'VALOR FIXO' THEN SUM(QUANTIDADE1 * TARIFA) ELSE SUM((VALOR_DESCONTADO * TARIFA)/100) END, 13, 2) AS VALOR_REPASSE ");
            sql.append(", SUM(VALOR_ENVIADO_DESCONTO) - SUM(VALOR_DESCONTADO) AS VALOR_NAO_DESCONTADO ");
        } else {
            sql.append(", '' AS CSE_NOME ");
            sql.append(", '' AS NUM_CRM ");
            sql.append(", '' AS PERIODO ");
            sql.append(", '' AS DATA_PROCESSAMENTO ");
            sql.append(", '' AS CODIGO_NATUREZA ");
            sql.append(", '' AS DESC_NATUREZA ");
            sql.append(", '0' AS VALOR_ENVIADO_DESCONTO ");
            sql.append(", '0' AS VALOR_DESCONTADO ");
            sql.append(", '0' AS VALOR_RETIDO ");
            sql.append(", '0' AS VALOR_REPASSE ");
            sql.append(", '0' AS VALOR_NAO_DESCONTADO ");
        }
        sql.append(" FROM (");

        sql.append("SELECT");
        sql.append(" (concat(concat(").append(Columns.CSA_IDENTIFICADOR).append(",'-'),").append(Columns.CSA_NOME).append(")) as CSA,");
        if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
            sql.append(Columns.SVC_DESCRICAO).append(" as SERVICO, ");
            sql.append(Columns.NSE_DESCRICAO).append(" as NSE_DESCRICAO, ");
        }
        sql.append(Columns.CSA_CNPJ).append(" as CNPJ,");
        sql.append(Columns.CSA_TEL).append(" as FONE,");
        sql.append(" CASE WHEN ").append(Columns.PCV_BASE_CALC).append(" = '2' THEN '"+ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.sobre.parcela", (AcessoSistema) null)+"' ELSE '"+ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.sobre.parcela.verba", (AcessoSistema) null)+"' END AS TIPO,");
        if (consolida) {
            sql.append(" to_decimal(COUNT(DISTINCT (concat(concat(").append(Columns.ADE_RSE_CODIGO).append(",'-'),").append(Columns.CNV_COD_VERBA).append("))), 13, 2) AS QUANTIDADE1,");
        } else {
            sql.append(" to_decimal(COUNT(DISTINCT ").append(Columns.PRD_ADE_CODIGO).append("), 13, 2) AS QUANTIDADE1,");
        }
        sql.append(" SUM(").append(Columns.ADE_VLR).append(") AS VALOR1,");

        if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            sql.append(Columns.COR_NOME).append(" as CORRESPONDENTE, ");
            sql.append(Columns.COR_IDENTIFICADOR).append(" as CODIGO_CORRESPONDENTE, ");
            sql.append(" CASE WHEN COALESCE(NULLIF(").append(Columns.PSO_VLR_REF).append(",''), NULLIF(").append(Columns.PSC_VLR_REF).append(",''), to_string(").append(Columns.PCV_FORMA_CALC).append(")) = '1' THEN '"+ApplicationResourcesHelper.getMessage("rotulo.valor.fixo.singular", (AcessoSistema) null)+"' ELSE '"+ApplicationResourcesHelper.getMessage("rotulo.percentual.singular", (AcessoSistema) null)+"' END AS FORMA,");
            sql.append(" COALESCE(NULLIF(").append(Columns.PSO_VLR).append(",''), NULLIF(").append(Columns.PSC_VLR).append(",''), to_string(").append(Columns.PCV_VLR).append(")) AS TARIFA ");
        } else {
            sql.append(" CASE WHEN COALESCE(NULLIF(").append(Columns.PSC_VLR_REF).append(",''), to_string(").append(Columns.PCV_FORMA_CALC).append(")) = '1' THEN '"+ApplicationResourcesHelper.getMessage("rotulo.valor.fixo.singular", (AcessoSistema) null)+"' ELSE '"+ApplicationResourcesHelper.getMessage("rotulo.percentual.singular", (AcessoSistema) null)+"' END AS FORMA,");
            sql.append(" COALESCE(NULLIF(").append(Columns.PSC_VLR).append(",''), to_string(").append(Columns.PCV_VLR).append(")) AS TARIFA ");
        }
        sql.append(", ").append(Columns.CNV_COD_VERBA).append(" AS VERBA ");

        if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            sql.append(", ").append(Columns.CSE_NOME).append(" AS CSE_NOME ");
            sql.append(", ").append(Columns.CSE_IDENTIFICADOR_INTERNO).append(" AS NUM_CRM ");
            sql.append(", ").append(Columns.PRD_DATA_DESCONTO).append(" AS PERIODO ");
            sql.append(", ").append(Columns.PRD_DATA_REALIZADO).append(" AS DATA_PROCESSAMENTO ");
            sql.append(", ").append(Columns.NSE_CODIGO).append(" AS CODIGO_NATUREZA ");
            sql.append(", ").append(Columns.NSE_DESCRICAO).append(" AS DESC_NATUREZA ");
            sql.append(", SUM(").append(Columns.PRD_VLR_PREVISTO).append(") AS VALOR_ENVIADO_DESCONTO ");
            sql.append(", SUM(").append(Columns.PRD_VLR_REALIZADO).append(") AS VALOR_DESCONTADO ");
        }

        sql.append(" FROM ").append(Columns.TB_PARCELA_DESCONTO);
        sql.append(" INNER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        sql.append(" ON (").append(Columns.PRD_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO);
        sql.append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_CONVENIO);
        sql.append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_SERVICO);
        sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.SVC_CODIGO).append(")");
        if (tarifacaoPorModalidade || exibeColunasAdicionais) {
            sql.append(" INNER JOIN ").append(Columns.TB_NATUREZA_SERVICO);
            sql.append(" ON (").append(Columns.SVC_NSE_CODIGO).append(" = ").append(Columns.NSE_CODIGO).append(")");
        }
        if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            sql.append(" INNER JOIN ").append(Columns.TB_ORGAO);
            sql.append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_ESTABELECIMENTO);
            sql.append(" ON (").append(Columns.ORG_EST_CODIGO).append(" = ").append(Columns.EST_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNANTE);
            sql.append(" ON (").append(Columns.EST_CSE_CODIGO).append(" = ").append(Columns.CSE_CODIGO).append(")");
        }
        if (Boolean.TRUE.equals(servicoSemTarifacao) && (tarifacaoPorModalidade || exibeColunasAdicionais)) {
        	sql.append(" RIGHT OUTER JOIN ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE);
	        sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PCV_SVC_CODIGO).append(")");
        } else {
	        sql.append(" INNER JOIN ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE);
	        sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PCV_SVC_CODIGO).append(")");
        }

        sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
        sql.append(" ON (").append(Columns.CNV_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO).append(")");
        if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            sql.append(" INNER JOIN ").append(Columns.TB_CORRESPONDENTE);
            sql.append(" ON (").append(Columns.ADE_COR_CODIGO).append(" = ").append(Columns.COR_CODIGO).append(")");
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CORRESPONDENTE);
            sql.append(" ON (").append(Columns.PSO_TPS_CODIGO).append(" = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("'");
            sql.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" = ").append(Columns.PSO_SVC_CODIGO);
            sql.append(" AND ").append(Columns.COR_CODIGO).append(" = ").append(Columns.PSO_COR_CODIGO);
            sql.append(" AND COALESCE(").append(Columns.PSO_ATIVO).append(",'1') = '").append(CodedValues.STS_ATIVO).append("')");
        }
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
        sql.append(" ON (").append(Columns.PSC_TPS_CODIGO).append(" = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("'");
        sql.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" = ").append(Columns.PSC_SVC_CODIGO);
        sql.append(" AND ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PSC_CSA_CODIGO).append(")");

        sql.append(" WHERE ").append(Columns.PCV_BASE_CALC).append(" = '").append("2").append("'");
        if (Boolean.FALSE.equals(servicoSemTarifacao)) {
        	sql.append(" AND ").append(Columns.PCV_VLR).append(" > 0.00");
        }
        sql.append(" AND ").append(Columns.PRD_SPD_CODIGO).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
        sql.append(" AND ").append(Columns.PRD_DATA_DESCONTO).append(" ").append(criaClausulaNomeada("periodo", periodo));
        if (!TextHelper.isNull(cnvCodVerba)) {
            sql.append(" AND ").append(Columns.CNV_COD_VERBA).append(" ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            sql.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND ").append(Columns.CSA_CODIGO).append(" ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(" ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            sql.append(" AND ").append(Columns.SVC_NSE_CODIGO).append(" ").append(criaClausulaNomeada("nseCodigo", nseCodigos));
        }

        sql.append(" GROUP BY (concat(concat(").append(Columns.CSA_IDENTIFICADOR).append(",'-'),").append(Columns.CSA_NOME).append(")), ");
        sql.append(Columns.CSA_CNPJ).append(", ");
        sql.append(Columns.CSA_TEL).append(", ");
        if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
        	sql.append(Columns.SVC_DESCRICAO).append(", ");
        	sql.append(Columns.NSE_DESCRICAO).append(", ");
        }
        if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            sql.append(Columns.COR_NOME).append(", ");
            sql.append(Columns.COR_IDENTIFICADOR).append(", ");
            sql.append(Columns.PSO_VLR).append(", ");
            sql.append(Columns.PSO_VLR_REF).append(", ");
        }
        sql.append(Columns.PCV_BASE_CALC).append(", ");
        sql.append(Columns.PCV_FORMA_CALC).append(", ").append(Columns.PCV_VLR).append(", ").append(Columns.PSC_VLR).append(", ").append(Columns.PSC_VLR_REF).append(", ").append(Columns.CNV_SVC_CODIGO);
        if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            sql.append(", ").append(Columns.CSE_NOME);
            sql.append(", ").append(Columns.CSE_IDENTIFICADOR_INTERNO);
            sql.append(", ").append(Columns.PRD_DATA_DESCONTO);
            sql.append(", ").append(Columns.PRD_DATA_REALIZADO);
            sql.append(", ").append(Columns.NSE_CODIGO);
            sql.append(", ").append(Columns.NSE_DESCRICAO);
        }
        sql.append(", ").append(Columns.CNV_COD_VERBA);
        if (Boolean.TRUE.equals(servicoSemTarifacao)) {
        	sql.append(" UNION ALL ");
        	sql.append("SELECT");
            sql.append(" (concat(concat(").append(Columns.CSA_IDENTIFICADOR).append(",'-'),").append(Columns.CSA_NOME).append(")) as CSA,");
            if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
                sql.append(Columns.SVC_DESCRICAO).append(" as SERVICO, ");
                sql.append(Columns.NSE_DESCRICAO).append(" as NSE_DESCRICAO, ");
            }
            sql.append(Columns.CSA_CNPJ).append(" as CNPJ,");
            sql.append(Columns.CSA_TEL).append(" as FONE,");
            sql.append(" '' AS TIPO,");
            if (consolida) {
                sql.append(" to_decimal(COUNT(DISTINCT (concat(concat(").append(Columns.ADE_RSE_CODIGO).append(",'-'),").append(Columns.CNV_COD_VERBA).append("))), 13, 2) AS QUANTIDADE1,");
            } else {
                sql.append(" to_decimal(COUNT(DISTINCT ").append(Columns.PRD_ADE_CODIGO).append("), 13, 2) AS QUANTIDADE1,");
            }
            sql.append(" SUM(").append(Columns.ADE_VLR).append(") AS VALOR1,");

            if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
                sql.append(Columns.COR_NOME).append(" as CORRESPONDENTE, ");
                sql.append(Columns.COR_IDENTIFICADOR).append(" as CODIGO_CORRESPONDENTE, ");
            }
			sql.append(" '' AS FORMA,");
		    sql.append(" '' AS TARIFA ");

            if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
                sql.append(", ").append(Columns.CSE_NOME).append(" AS CSE_NOME ");
                sql.append(", ").append(Columns.CSE_IDENTIFICADOR_INTERNO).append(" AS NUM_CRM ");
                sql.append(", ").append(Columns.PRD_DATA_DESCONTO).append(" AS PERIODO ");
                sql.append(", ").append(Columns.PRD_DATA_REALIZADO).append(" AS DATA_PROCESSAMENTO ");
                sql.append(", ").append(Columns.NSE_CODIGO).append(" AS CODIGO_NATUREZA ");
                sql.append(", ").append(Columns.NSE_DESCRICAO).append(" AS DESC_NATUREZA ");
                sql.append(", SUM(").append(Columns.PRD_VLR_PREVISTO).append(") AS VALOR_ENVIADO_DESCONTO ");
                sql.append(", SUM(").append(Columns.PRD_VLR_REALIZADO).append(") AS VALOR_DESCONTADO ");
            }
            sql.append(", ").append(Columns.CNV_COD_VERBA).append(" AS VERBA ");
            sql.append(" FROM ").append(Columns.TB_PARCELA_DESCONTO);
            sql.append(" INNER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO);
            sql.append(" ON (").append(Columns.PRD_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO);
            sql.append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_CONVENIO);
            sql.append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
            sql.append(" ON (").append(Columns.CNV_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_SERVICO);
            sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.SVC_CODIGO).append(")");
            if (tarifacaoPorModalidade || exibeColunasAdicionais) {
                sql.append(" INNER JOIN ").append(Columns.TB_NATUREZA_SERVICO);
                sql.append(" ON (").append(Columns.SVC_NSE_CODIGO).append(" = ").append(Columns.NSE_CODIGO).append(")");
            }
            if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
                sql.append(" INNER JOIN ").append(Columns.TB_ORGAO);
                sql.append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
                sql.append(" INNER JOIN ").append(Columns.TB_ESTABELECIMENTO);
                sql.append(" ON (").append(Columns.ORG_EST_CODIGO).append(" = ").append(Columns.EST_CODIGO).append(")");
                sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNANTE);
                sql.append(" ON (").append(Columns.EST_CSE_CODIGO).append(" = ").append(Columns.CSE_CODIGO).append(")");
            }
            if (tarifacaoPorModalidade || exibeColunasAdicionais) {
            	sql.append(" RIGHT OUTER JOIN ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE);
    	        sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PCV_SVC_CODIGO).append(")");
            } else {
    	        sql.append(" INNER JOIN ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE);
    	        sql.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PCV_SVC_CODIGO).append(")");
            }
            if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
                sql.append(" INNER JOIN ").append(Columns.TB_CORRESPONDENTE);
                sql.append(" ON (").append(Columns.ADE_COR_CODIGO).append(" = ").append(Columns.COR_CODIGO).append(")");
                sql.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CORRESPONDENTE);
                sql.append(" ON (").append(Columns.PSO_TPS_CODIGO).append(" = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("'");
                sql.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" = ").append(Columns.PSO_SVC_CODIGO);
                sql.append(" AND ").append(Columns.COR_CODIGO).append(" = ").append(Columns.PSO_COR_CODIGO);
                sql.append(" AND COALESCE(").append(Columns.PSO_ATIVO).append(",'1') = '").append(CodedValues.STS_ATIVO).append("')");
            }
            sql.append(" WHERE NOT EXISTS ( SELECT 1 FROM ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE).append(" WHERE ").append(Columns.SVC_CODIGO).append(" = ").append(Columns.PCV_SVC_CODIGO).append(" ) ");
            sql.append(" AND ").append(Columns.PRD_SPD_CODIGO).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
            sql.append(" AND ").append(Columns.PRD_DATA_DESCONTO).append(" ").append(criaClausulaNomeada("periodo", periodo));
            if (!TextHelper.isNull(cnvCodVerba)) {
                sql.append(" AND ").append(Columns.CNV_COD_VERBA).append(" ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
            }
            if (svcCodigos != null && !svcCodigos.isEmpty()) {
                sql.append(" AND ").append(Columns.SVC_CODIGO).append(" ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
            }
            if (!TextHelper.isNull(csaCodigo)) {
                sql.append(" AND ").append(Columns.CSA_CODIGO).append(" ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (orgCodigos != null && !orgCodigos.isEmpty()) {
                sql.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(" ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
            }
            if (nseCodigos != null && !nseCodigos.isEmpty()) {
                sql.append(" AND ").append(Columns.SVC_NSE_CODIGO).append(" ").append(criaClausulaNomeada("nseCodigo", nseCodigos));
            }

            sql.append(" GROUP BY (concat(concat(").append(Columns.CSA_IDENTIFICADOR).append(",'-'),").append(Columns.CSA_NOME).append(")), ");
            sql.append(Columns.CSA_CNPJ).append(", ");
            sql.append(Columns.CSA_TEL);
            if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
            	sql.append(", ").append(Columns.SVC_DESCRICAO).append(", ");
            	sql.append(Columns.NSE_DESCRICAO);
            }
            if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            	sql.append(", ").append(Columns.COR_NOME).append(", ");
                sql.append(Columns.COR_IDENTIFICADOR);
            }
            if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            	sql.append(", ").append(Columns.CSE_NOME);
                sql.append(", ").append(Columns.CSE_IDENTIFICADOR_INTERNO);
                sql.append(", ").append(Columns.PRD_DATA_DESCONTO);
                sql.append(", ").append(Columns.PRD_DATA_REALIZADO);
                sql.append(", ").append(Columns.NSE_CODIGO);
                sql.append(", ").append(Columns.NSE_DESCRICAO);
            }
            sql.append(", ").append(Columns.CNV_COD_VERBA);
        }
        sql.append(") X ");
        sql.append(" GROUP BY CSA, CNPJ, FONE, FORMA, TARIFA");
        if (Boolean.TRUE.equals(tarifacaoPorModalidade)) {
            sql.append(", SERVICO");
        }
        if (Boolean.TRUE.equals(tarifacaoPorCorrespondente)) {
            sql.append(", CORRESPONDENTE");
            sql.append(", CODIGO_CORRESPONDENTE ");
        }
        if (Boolean.TRUE.equals(exibeColunasAdicionais)) {
            sql.append(", CSE_NOME");
            sql.append(", NUM_CRM");
            sql.append(", PERIODO");
            sql.append(", DATA_PROCESSAMENTO");
            sql.append(", CODIGO_NATUREZA");
            sql.append(", DESC_NATUREZA");
        }
        sql.append(", VERBA");
        if(tarifacaoPorNatureza && tarifacaoPorModalidade) {
        	sql.append(" ORDER BY SERVICO, CSA, CORRESPONDENTE");
        } else {
        	sql.append(" ORDER BY CSA, CORRESPONDENTE, NSE_DESCRICAO, SERVICO ");
        }

        Query<Object[]> query = instanciarQuery(session, sql.toString());
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigos, query);
        }
        if (!TextHelper.isNull(periodo)) {
            try {
                defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodo);
            }
        }

        return query;
    }
}