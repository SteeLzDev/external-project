package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCodVerbaConvenioAtivoQuery</p>
 * <p>Description: Lista os órgãos juntamente com os códigos de verba para os convênios ativos
 * de um determinado serviço / consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCodVerbaConvenioAtivoQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select "
                     + "cnv.cnvCodigo, "
                     + "cnv.cnvCodVerba, "
                     + "cnv.cnvCodVerbaRef, "
                     + "cnv.cnvCodVerbaFerias, "
                     + "est.estIdentificador, "
                     + "org.orgIdentificador, "
                     + "org.orgNome ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" order by est.estIdentificador, org.orgNome, org.orgIdentificador ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_COD_VERBA_REF,
                Columns.CNV_COD_VERBA_FERIAS,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME
        };
    }
}
