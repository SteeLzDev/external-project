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
 * <p>Title: RelatorioGerencialQtdeContratosPorSvcQuery</p>
 * <p>Description: Retorna a quantidade de contratos ativos por serviço.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeContratosPorSvcQuery extends ReportHQuery {
    private int maxResultados = 0;
    private String periodo;
    private boolean internacional;

    public RelatorioGerencialQtdeContratosPorSvcQuery() {
    }

    public RelatorioGerencialQtdeContratosPorSvcQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialQtdeContratosPorSvcQuery(int maxResultados, String periodo, boolean internacional) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
        this.internacional = internacional;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsCodigo = CodedValues.SRS_ATIVO;
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT cnv.cnvCodVerba AS CNV_COD_VERBA, ");
        corpo.append("csa.csaNome AS CONSIGNATARIA, ");
        corpo.append("svc.svcDescricao AS SVC_DESCRICAO, ");
        corpo.append("COUNT(DISTINCT ade.adeNumero) AS QUANTIDADE, ");
        corpo.append("SUM(ade.adeVlr) AS VLR_MENSAL, ");
        corpo.append("SUM(ade.adeVlr*coalesce(ade.adePrazo, 1)) AS VLR_TOTAL ");
        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");
        corpo.append("INNER JOIN ade.verbaConvenio vco ");
        corpo.append("INNER JOIN vco.convenio cnv ");
        corpo.append("INNER JOIN cnv.servico svc ");
        corpo.append("INNER JOIN cnv.consignataria csa ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));
        }
        if(!internacional) {
            corpo.append(" GROUP BY cnv.cnvCodVerba, csa.csaNome, svc.svcDescricao ");
        } else {
            corpo.append(" GROUP BY svc.svcCodigo, csa.csaNome ");
        }
        corpo.append(" ORDER BY COUNT(DISTINCT ade.adeNumero) DESC ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
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
                Columns.CNV_COD_VERBA,
                "CONSIGNATARIA",
                Columns.SVC_DESCRICAO,
                "QUANTIDADE",
                "VLR_MENSAL",
                "VLR_TOTAL"
        };
    }
}
