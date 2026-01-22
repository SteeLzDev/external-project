package com.zetra.econsig.persistence.query.endereco;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaEnderecoQuery</p>
 * <p>Description: Listagem de endereços</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision:  $
 * $Date: 2012-12-04 11:25:00 -0300 (ter, 04 dez 2012) $
 */
public class ListaEnderecoQuery extends HQuery {
    public String echCodigo;
    public Object csaCodigo;
    public String echIdentificador;
    public String echDescricao;
    public String echCondominio;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "SELECT ech.echCodigo," +
            		"ech.consignataria," +
            		"ech.echIdentificador," +
            		"ech.echDescricao," +
            		"ech.echCondominio," +
            		"ech.echQtdUnidades";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from EnderecoConjHabitacional ech");
        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and ech.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" and ech.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(echDescricao)) {
        	corpoBuilder.append(" and ").append(criaClausulaNomeada("ech.echDescricao", "echDescricao", CodedValues.LIKE_MULTIPLO + echDescricao));
        }

        if (!TextHelper.isNull(echCondominio)) {
            corpoBuilder.append(" and ech.echCondominio ").append(criaClausulaNomeada("echCondominio", echCondominio));
        }

        if (!TextHelper.isNull(echIdentificador)) {
            corpoBuilder.append(" and ech.echIdentificador ").append(criaClausulaNomeada("echIdentificador", echIdentificador));
        }

        if (!count) {
            corpoBuilder.append(" order by ech.echDescricao");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(echCondominio)) {
            defineValorClausulaNomeada("echCondominio", echCondominio, query);
        }

        if (!TextHelper.isNull(echIdentificador)) {
            defineValorClausulaNomeada("echIdentificador", echIdentificador, query);
        }

        if (!TextHelper.isNull(echDescricao)) {
            defineValorClausulaNomeada("echDescricao", CodedValues.LIKE_MULTIPLO + echDescricao + CodedValues.LIKE_MULTIPLO, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ECH_CODIGO,
                Columns.ECH_CSA_CODIGO,
                Columns.ECH_IDENTIFICADOR,
                Columns.ECH_DESCRICAO,
                Columns.ECH_CONDOMINIO,
                Columns.ECH_QTD_UNIDADES
        };
    }
}