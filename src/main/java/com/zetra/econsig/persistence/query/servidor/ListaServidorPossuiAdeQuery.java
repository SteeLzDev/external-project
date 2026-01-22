package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorPossuiAdeQuery</p>
 * <p>Description: Retornar lista de servidores que possuem consignação em qualquer situação caso seja informada consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorPossuiAdeQuery extends HQuery {

    public String serCpf;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select distinct ser.serCodigo, ser.serNome, ser.serCpf, ser.serEmail, ser.serCelular ");
        corpoBuilder.append(" from Servidor ser ");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" where ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and exists ( ");
            corpoBuilder.append(" select 1 from ser.registroServidorSet rse ");
            corpoBuilder.append(" inner join rse.autDescontoSet ade ");
            corpoBuilder.append(" inner join ade.verbaConvenio vco ");
            corpoBuilder.append(" inner join vco.convenio cnv ");
            corpoBuilder.append(" inner join cnv.consignataria csa ");
            corpoBuilder.append(" where ser.serCodigo = rse.servidor.serCodigo ");
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" ) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("serCpf", serCpf, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
       return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_EMAIL,
                Columns.SER_CELULAR
        };
    }
}