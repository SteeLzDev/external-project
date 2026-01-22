package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioCorrespondenteIncluirQuery</p>
 * <p>Description: Lista os convênios que devem ser incluidos para o correspondente
 * caso o usuário queira desbloquear todos os convênios para o correspondente.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioCorrespondenteIncluirQuery extends HQuery {

    public String corCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select cnv.cnvCodigo ");
        corpoBuilder.append(" from Correspondente cor ");
        corpoBuilder.append(" inner join cor.consignataria csa ");
        corpoBuilder.append(" inner join csa.convenioSet cnv ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        corpoBuilder.append(" and not exists (select 1 from CorrespondenteConvenio ccr ");
        corpoBuilder.append(" where ccr.cnvCodigo = cnv.cnvCodigo ");
        corpoBuilder.append(" and ccr.corCodigo = cor.corCodigo) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("corCodigo", corCodigo, query);

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO
        };
    }    
}
