package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaCodigoVerbaFeriasInvalidoQuery</p>
 * <p>Description: Lista códigos de verbas de férias que também são utilizados como
 * código de verba normal, indicando uma inconsistência no cadastro de convênios.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCodigoVerbaFeriasInvalidoQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ListaCodigoVerbaFeriasInvalidoQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select coalesce(nullif(csa.csaNomeAbrev, ''), csa.csaNome), svc.svcDescricao, coalesce(nullif(org.orgNomeAbrev, ''), org.orgNome), cnv.cnvCodVerbaFerias ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("where nullif(trim(cnv.cnvCodVerbaFerias), '') is not null ");
        corpoBuilder.append("and exists (select 1 from Convenio cnv2 where cnv.cnvCodVerbaFerias = cnv2.cnvCodVerba) ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" group by csa.csaNomeAbrev, csa.csaNome, svc.svcDescricao, org.orgNomeAbrev, org.orgNome, cnv.cnvCodVerbaFerias");
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
                "VERBA_FERIAS"
        };
    }
}
