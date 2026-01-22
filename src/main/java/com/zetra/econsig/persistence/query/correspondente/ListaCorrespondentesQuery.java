package com.zetra.econsig.persistence.query.correspondente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCorrespondentesQuery</p>
 * <p>Description: Listagem de correspondentes</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCorrespondentesQuery extends HQuery {
    public boolean count = false;

    public String csaCodigo;
    public Object corAtivo;
    public String corIdentificador;
    public String corNome;
    public String corCodigo;
    public String ecoCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select correspondente.corCodigo, " +
            "correspondente.corIdentificador, " +
            "correspondente.corNome, " +
            "correspondente.corAtivo, " +
            "correspondente.empresaCorrespondente.ecoCodigo ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Correspondente correspondente WHERE 1=1 ");

        if (corAtivo != null) {
            corpoBuilder.append(" and correspondente.corAtivo ").append(criaClausulaNomeada("corAtivo", corAtivo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and correspondente.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("correspondente.corIdentificador", "corIdentificador", corIdentificador));
        }

        if (!TextHelper.isNull(corNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("correspondente.corNome", "corNome", corNome));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (!TextHelper.isNull(ecoCodigo)) {
            corpoBuilder.append(" and (correspondente.empresaCorrespondente.ecoCodigo IS NULL ");
            corpoBuilder.append(" or correspondente.empresaCorrespondente.ecoCodigo ").append(criaClausulaNomeada("ecoCodigo", ecoCodigo)).append( " ) ");
        }

        if (!count) {
            corpoBuilder.append(" order by correspondente.corNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corIdentificador)) {
            defineValorClausulaNomeada("corIdentificador", corIdentificador, query);
        }

        if (!TextHelper.isNull(corNome)) {
            defineValorClausulaNomeada("corNome", corNome, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (corAtivo != null) {
            defineValorClausulaNomeada("corAtivo", corAtivo, query);
        }

        if (!TextHelper.isNull(ecoCodigo)) {
            defineValorClausulaNomeada("ecoCodigo", ecoCodigo, query);
        }

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.COR_CODIGO,
                Columns.COR_IDENTIFICADOR,
                Columns.COR_NOME,
                Columns.COR_ATIVO,
                Columns.COR_ECO_CODIGO
        };
    }

}
