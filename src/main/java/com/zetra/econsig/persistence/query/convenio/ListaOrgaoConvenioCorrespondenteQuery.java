package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOrgaoConvenioCorrespondenteQuery</p>
 * <p>Description: Lista os órgão que estão relacionados para convênio de um
 * correspondente, de acordo com os convênios ativos de sua consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOrgaoConvenioCorrespondenteQuery extends HQuery {

    public String corCodigo;
    public String svcCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "org.orgCodigo, " +
                "org.orgNome, " +
                "org.orgIdentificador, " +
                "est.estIdentificador, " +
                "cnv.cnvCodigo, " +
                "COALESCE(ccr.statusConvenio.scvCodigo, '2') AS STATUS ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" left outer join cnv.correspondenteConvenioSet ccr with ");
        corpoBuilder.append(" ccr.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" and cnv.consignataria.csaCodigo = ");
        corpoBuilder.append(" (select cor.consignataria.csaCodigo from Correspondente cor ");
        corpoBuilder.append(" where cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo)).append(")");                    
        corpoBuilder.append(" order by est.estIdentificador, org.orgNome");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("corCodigo", corCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.EST_IDENTIFICADOR,
                Columns.CNV_CODIGO,
                "STATUS"
        };
    }    
}
