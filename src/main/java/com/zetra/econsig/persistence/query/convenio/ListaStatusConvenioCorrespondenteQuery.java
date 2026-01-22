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
public class ListaStatusConvenioCorrespondenteQuery extends HQuery {

    public String corCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select svc.svcCodigo, " +
                        "svc.svcIdentificador, " +
                        "svc.svcDescricao, " +
                        "CASE WHEN COUNT(*) = COUNT(ccr.statusConvenio.scvCodigo) THEN '1' " +
                        "     WHEN COUNT(ccr.statusConvenio.scvCodigo) = 0 THEN '2' " +
                        "ELSE '0' END AS STATUS ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" left outer join cnv.correspondenteConvenioSet ccr with ");
        corpoBuilder.append(" ccr.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo)).append(" and ");
        corpoBuilder.append(" ccr.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cnv.consignataria.csaCodigo = ");
        corpoBuilder.append(" (select cor.consignataria.csaCodigo from Correspondente cor ");
        corpoBuilder.append(" where cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo)).append(")");
        corpoBuilder.append(" group by svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao ");
        corpoBuilder.append(" order by MAX(COALESCE(ccr.statusConvenio.scvCodigo, '2')), svc.svcDescricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("corCodigo", corCodigo, query);

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
