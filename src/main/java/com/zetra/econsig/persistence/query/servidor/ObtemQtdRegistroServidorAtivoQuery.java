package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemQtdRegistroServidorAtivoQuery</p>
 * <p>Description: Retorna a quantidade de registros servidores ativos</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemQtdRegistroServidorAtivoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select count(*) as QTD from RegistroServidor rse ");

        if (tipoEntidade.equalsIgnoreCase("EST")) {
            corpoBuilder.append("inner join rse.orgao org ");
        }

        corpoBuilder.append("where rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");

        if (tipoEntidade.equalsIgnoreCase("EST")) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo = :codigoEntidade");
        } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
            corpoBuilder.append(" and rse.orgao.orgCodigo = :codigoEntidade");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tipoEntidade.equalsIgnoreCase("EST") || tipoEntidade.equalsIgnoreCase("ORG")) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"QTD"};
    }
}
