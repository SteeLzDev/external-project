package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOrgaoConvenioAtivoQuery</p>
 * <p>Description: Lista os órgãos que possuem convênio com uma determinada consignatária,
 * ou com alguma delas, ou um determinado correspondente, para todos os serviços.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOrgaoConvenioAtivoQuery extends HQuery {

    public String csaCodigo;
    public String corCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select distinct " +
                "org.orgCodigo, " +
                "org.orgNome, " +
                "org.orgNomeAbrev, " +
                "org.orgIdentificador, " +
                "est.estIdentificador ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc ");
        }
        
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and crc.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        
        corpoBuilder.append(" order by org.orgNome ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.ORG_NOME_ABREV,
                Columns.ORG_IDENTIFICADOR,
                Columns.EST_IDENTIFICADOR
        };
    }    
}
