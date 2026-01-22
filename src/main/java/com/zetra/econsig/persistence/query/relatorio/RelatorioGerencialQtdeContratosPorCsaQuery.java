package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioGerencialQtdeContratosPorCsaQuery</p>
 * <p>Description: Retorna a quantidade de contratos ativos por consignatária.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeContratosPorCsaQuery extends ReportHNativeQuery {
    private int maxResultados = 0;
    private String periodo;
    private boolean status = false;

    public RelatorioGerencialQtdeContratosPorCsaQuery() {
    }

    public RelatorioGerencialQtdeContratosPorCsaQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialQtdeContratosPorCsaQuery(int maxResultados, String periodo, boolean status) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
        this.status = status;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String srsCodigo = CodedValues.SRS_ATIVO;
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        final StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT coalesce(csa.csa_nome, '").append(ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)).append("') AS CONSIGNATARIA, ");
        corpo.append("coalesce(csa.csa_nome_abrev, '') AS CSA_NOME_ABREV, ");
        corpo.append("CASE WHEN csa.csa_ativo = ").append(CodedValues.STS_ATIVO).append(" ");
        corpo.append("THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.desbloqueado", (AcessoSistema) null)).append("' ");
        corpo.append("ELSE '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.bloqueado", (AcessoSistema) null)).append("' END AS STATUS, ");
        corpo.append("COUNT(DISTINCT ade.ade_numero) AS QUANTIDADE_MENSAL, ");
        corpo.append("SUM(ade.ade_vlr) AS VLR_MENSAL, ");
        corpo.append("t.QUANTIDADE_TOTAL, ");
        corpo.append("t.VLR_TOTAL ");
        corpo.append("FROM tb_aut_desconto ade ");
        corpo.append("INNER JOIN tb_registro_servidor rse ON ade.rse_codigo = rse.rse_codigo ");
        corpo.append("INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo ");
        corpo.append("INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo ");
        corpo.append("INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo ");
        corpo.append("INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo ");
        corpo.append("INNER JOIN ( ");
        corpo.append("SELECT csa.csa_codigo, ");
        corpo.append("COUNT(DISTINCT ade.ade_numero) AS QUANTIDADE_TOTAL, ");
        corpo.append("SUM(ade.ade_vlr*coalesce(ade.ade_prazo, 1)) AS VLR_TOTAL ");
        corpo.append("FROM tb_aut_desconto ade ");
        corpo.append("INNER JOIN tb_registro_servidor rse ON ade.rse_codigo = rse.rse_codigo ");
        corpo.append("INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo ");
        corpo.append("INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo ");
        corpo.append("INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo ");
        corpo.append("INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo ");
        corpo.append("WHERE rse.srs_codigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        corpo.append(" GROUP BY csa.csa_codigo ");
        corpo.append(") t ON t.csa_codigo = csa.csa_codigo ");
        corpo.append("WHERE rse.srs_codigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.ADE_ANO_MES_INI ").append(criaClausulaNomeada("periodo", periodo));
        }
        if (status) {
            corpo.append(" AND csa.csa_ativo = ").append(CodedValues.STS_ATIVO).append(" ");
        }
        corpo.append(" GROUP BY csa.csa_nome, csa.csa_nome_abrev, csa.csa_ativo, ade.ade_numero, ade.ade_vlr, t.QUANTIDADE_TOTAL, t.VLR_TOTAL ");
        corpo.append(" ORDER BY COUNT(DISTINCT ade.ade_numero) DESC ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("srsCodigo", srsCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CONSIGNATARIA",
                "CSA_NOME_ABREV",
                "STATUS",
                "QUANTIDADE_MENSAL",
                "VLR_MENSAL",
                "QUANTIDADE_TOTAL",
                "VLR_TOTAL"
        };
    }
}