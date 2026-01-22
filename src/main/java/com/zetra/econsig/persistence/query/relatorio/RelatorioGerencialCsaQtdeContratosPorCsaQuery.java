package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioGerencialCsaQtdeContratosPorCsaQuery</p>
 * <p>Description: Retorna a quantidade de contratos ativos por consignatária.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialCsaQtdeContratosPorCsaQuery extends ReportHQuery {
    private int maxResultados = 0;
    private String periodo;
    public List<String> csaCodigo = null;

    public RelatorioGerencialCsaQtdeContratosPorCsaQuery() {
    }

    public RelatorioGerencialCsaQtdeContratosPorCsaQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialCsaQtdeContratosPorCsaQuery(int maxResultados, String periodo) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String srsCodigo = CodedValues.SRS_ATIVO;
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        final StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT coalesce(csa.csaNome, '").append(ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)).append("') AS CONSIGNATARIA, ");
        corpo.append("coalesce(csa.csaNomeAbrev, '') AS CSA_NOME_ABREV, ");
        corpo.append("CASE WHEN csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(" ");
        corpo.append("THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.desbloqueado", (AcessoSistema) null)).append("' ");
        corpo.append("ELSE '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.bloqueado", (AcessoSistema) null)).append("' END AS STATUS, ");
        corpo.append("COUNT(DISTINCT ade.adeNumero) AS QUANTIDADE, ");
        corpo.append("SUM(ade.adeVlr) AS VLR_MENSAL, ");
        corpo.append("SUM(ade.adeVlr*coalesce((ade.adePrazo - coalesce(ade.adePrdPagas, 0)), 1)) AS VLR_TOTAL ");
        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");
        corpo.append("INNER JOIN ade.verbaConvenio vco ");
        corpo.append("INNER JOIN vco.convenio cnv ");
        corpo.append("INNER JOIN cnv.servico svc ");
        corpo.append("INNER JOIN cnv.consignataria csa ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if (!TextHelper.isNull(csaCodigo) && !csaCodigo.isEmpty()) {
            corpo.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));
        }
        corpo.append(" GROUP BY csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo ");
        corpo.append(" ORDER BY COUNT(DISTINCT ade.adeNumero) DESC ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("srsCodigo", srsCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        if (!TextHelper.isNull(csaCodigo) && !csaCodigo.isEmpty()) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
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
                "QUANTIDADE",
                "VLR_MENSAL",
                "VLR_TOTAL"
        };
    }
}
