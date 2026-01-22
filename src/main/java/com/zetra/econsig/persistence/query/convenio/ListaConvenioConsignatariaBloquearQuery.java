package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioConsignatariaBloquearQuery</p>
 * <p>Description: Lista os convênios da consignatária que devem ser bloqueados para o correspondente
 * caso o usuário bloqueie algum convênio da consignatária. Lista apenas os convênios de correspondente
 * que estejam ativos, onde o convênio da consignatária esteja inativo.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioConsignatariaBloquearQuery extends HQuery {

    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select cnv.cnvCodigo, ccr.corCodigo ");
        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.correspondenteConvenioSet ccr ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_INATIVO).append("'");
        corpoBuilder.append(" and ccr.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.COR_CODIGO
        };
    }    
}
