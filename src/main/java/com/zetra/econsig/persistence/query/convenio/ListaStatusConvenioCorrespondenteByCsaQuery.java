package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusConvenioCorrespondenteQuery</p>
 * <p>Description: Lista o status de todos os convênios, que a consignatária possui,
 * (bloqueado se não existir), para um correspondente.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusConvenioCorrespondenteByCsaQuery extends HQuery {

    //public List<String> corCodigo;
    public String csaCodigo;
    public String orgCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo =  new StringBuilder();
        corpo.append("select svc.svcCodigo, ");
        corpo.append("svc.svcIdentificador, ");
        corpo.append("svc.svcDescricao, ");

        corpo.append("CASE WHEN COUNT(DISTINCT cor.corCodigo) = ");
        corpo.append("(select COUNT(cor.corCodigo) from Correspondente cor ");
        corpo.append("     where cor.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpo.append("       and cor.corAtivo <> ").append(CodedValues.STS_INDISP);
        corpo.append(") ");
        corpo.append("THEN '1' ");
        corpo.append("     WHEN COUNT(ccr.statusConvenio.scvCodigo) = 0 THEN '2' ");
        corpo.append("ELSE '0' END AS STATUS ");

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" left outer join cnv.correspondenteConvenioSet ccr with ");
        corpoBuilder.append(" ccr.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

        corpoBuilder.append(" left outer join ccr.correspondente cor with ");
        corpoBuilder.append(" cor.corAtivo <> ").append(CodedValues.STS_INDISP);

        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        corpoBuilder.append(" group by svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao ");
        corpoBuilder.append(" order by MAX(COALESCE(ccr.statusConvenio.scvCodigo, 2)) DESC, svc.svcDescricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("orgCodigo", orgCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                "STATUS"
        };
    }
}
