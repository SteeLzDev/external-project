package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery</p>
 * <p>Description: Retorna valores estatísticos de margem por natureza de serviço.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery extends ReportHQuery {
    private final String naturezaSvc;

    public RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery(String naturezaSvc) {
        this.naturezaSvc = naturezaSvc;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsCodigo = CodedValues.SRS_ATIVO;
        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT SUM(ade.adeVlr) AS TOTAL_PRESTACAO, ");
        corpo.append("SUM((coalesce(ade.adePrazo, 1) - coalesce(ade.adePrdPagas, 0)) * ade.adeVlr) AS SALDO_DEVEDOR ");
        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");
        corpo.append("INNER JOIN ade.verbaConvenio vco ");
        corpo.append("INNER JOIN vco.convenio cnv ");
        corpo.append("INNER JOIN cnv.servico svc ");
        corpo.append("INNER JOIN svc.naturezaServico nse ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        if (!TextHelper.isNull(naturezaSvc)) {
            corpo.append(" AND nse.nseCodigo ").append(criaClausulaNomeada("naturezaSvc", naturezaSvc));
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("srsCodigo", srsCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!TextHelper.isNull(naturezaSvc)) {
            defineValorClausulaNomeada("naturezaSvc", naturezaSvc, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL_PRESTACAO",
                "SALDO_DEVEDOR"
        };
    }
}
