package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaCodigoVerbaInvalidoQuery</p>
 * <p>Description: Lista os convênios que possuem contratos ativos no sistema que não
 * tem o código de verba informado, ou o código de verba é inválido.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCodigoVerbaInvalidoQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ListaCodigoVerbaInvalidoQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select coalesce(nullif(csa.csaNomeAbrev, ''), csa.csaNome), svc.svcDescricao, coalesce(nullif(org.orgNomeAbrev, ''), org.orgNome), count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.orgao org ");

        corpoBuilder.append("where ade.statusAutorizacaoDesconto.sadCodigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
        corpoBuilder.append("and nullif(substituir(trim(cnv.cnvCodVerba), '0', ''), '') is null ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" group by csa.csaNomeAbrev, csa.csaNome, svc.svcDescricao, org.orgNomeAbrev, org.orgNome");
        corpoBuilder.append(" order by 1,2,3");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CSA",
                "SVC",
                "ORG",
                "QTD"
        };
    }
}
