package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioGerencialCsaQtdeContratosPorSvcQuery</p>
 * <p>Description: Retorna a quantidade de contratos ativos por serviço.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialCsaQtdeContratosPorSvcQuery extends ReportHQuery {
    private int maxResultados = 0;
    private String periodo;
    private boolean porcentagem;

    public RelatorioGerencialCsaQtdeContratosPorSvcQuery() {
    }

    public RelatorioGerencialCsaQtdeContratosPorSvcQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialCsaQtdeContratosPorSvcQuery(int maxResultados, String periodo, boolean porcentagem) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
        this.porcentagem = porcentagem;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsCodigo = CodedValues.SRS_ATIVO;
        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        StringBuilder corpo = new StringBuilder();
        if (porcentagem) {
            corpo.append("SELECT '' AS SVC_DESCRICAO, '' AS QUANTIDADE, '' AS VLR_MENSAL, '' AS VLR_TOTAL");
            corpo.append(", SUM(ade.adeVlr*coalesce((ade.adePrazo - coalesce(ade.adePrdPagas, 0)), 1)) AS VLR_TOTAL_GERAL ");
            corpo.append(", COUNT(DISTINCT ade.adeNumero) AS QTDE_TOTAL_GERAL ");
        } else {
            corpo.append("SELECT svc.svcDescricao AS SVC_DESCRICAO");
            corpo.append(", COUNT(DISTINCT ade.adeNumero) AS QUANTIDADE");
            corpo.append(", SUM(ade.adeVlr) AS VLR_MENSAL");
            corpo.append(", SUM(ade.adeVlr*coalesce((ade.adePrazo - coalesce(ade.adePrdPagas, 0)), 1)) AS VLR_TOTAL");
            corpo.append(", '' AS VLR_TOTAL_GERAL");
            corpo.append(", '' AS QTDE_TOTAL_GERAL ");
        }
        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");
        corpo.append("INNER JOIN ade.verbaConvenio vco ");
        corpo.append("INNER JOIN vco.convenio cnv ");
        corpo.append("INNER JOIN cnv.servico svc ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));
        }
        if (!porcentagem) {
            corpo.append(" GROUP BY svc.svcDescricao ");
            corpo.append(" ORDER BY COUNT(DISTINCT ade.adeNumero) DESC ");
        }
        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if ((maxResultados > 0) && (!porcentagem)){
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
                Columns.SVC_DESCRICAO,
                "QUANTIDADE",
                "VLR_MENSAL",
                "VLR_TOTAL",
                "VLR_TOTAL_GERAL",
                "QTDE_TOTAL_GERAL"
        };
    }
}
