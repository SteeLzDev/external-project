package com.zetra.econsig.persistence.query.sdp.despesacomum;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaDespesaComumQuery</p>
 * <p>Description: Listagem de Despesas Comum</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision:  $
 * $Date: $
 */
public class ListaDespesaComumQuery extends HQuery {
    public String echCodigo;
    public String plaCodigo;
    public String decCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  " SELECT dec.decCodigo, ";
            corpo += " dec.enderecoConjHabitacional.echIdentificador, ";
            corpo += " dec.enderecoConjHabitacional.echDescricao, ";
            corpo += " dec.plano.plaIdentificador, ";
            corpo += " dec.plano.plaDescricao, ";
            corpo += " prs.posDescricao, ";
            corpo += " sdc.sdcCodigo, ";
            corpo += " dec.decValor, ";
            corpo += " dec.decValorRateio, ";
            corpo += " dec.decPrazo, ";
            corpo += " dec.decData, ";
            corpo += " dec.decDataIni, ";
            corpo += " dec.decDataFim, ";
            corpo += " dec.decIdentificador, ";
            corpo += " sdc.sdcDescricao ";
        } else {
            corpo = " SELECT COUNT(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM DespesaComum dec ");
        corpoBuilder.append(" INNER JOIN dec.statusDespesaComum sdc ");
        corpoBuilder.append(" LEFT JOIN dec.postoRegistroServidor prs ");
        corpoBuilder.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" AND dec.enderecoConjHabitacional.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(plaCodigo)) {
            corpoBuilder.append(" AND dec.plano.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
        }

        if (!TextHelper.isNull(decCodigo)) {
            corpoBuilder.append(" AND dec.decCodigo ").append(criaClausulaNomeada("decCodigo", decCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (!TextHelper.isNull(decCodigo)) {
            defineValorClausulaNomeada("decCodigo", decCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DEC_CODIGO,
                Columns.ECH_IDENTIFICADOR,
                Columns.ECH_DESCRICAO,
                Columns.PLA_IDENTIFICADOR,
                Columns.PLA_DESCRICAO,
                Columns.POS_DESCRICAO,
                Columns.SDC_CODIGO,
                Columns.DEC_VALOR,
                Columns.DEC_VALOR_RATEIO,
                Columns.DEC_PRAZO,
                Columns.DEC_DATA,
                Columns.DEC_DATA_INI,
                Columns.DEC_DATA_FIM,
                Columns.DEC_IDENTIFICADOR,
                Columns.SDC_DESCRICAO
        };
    }
}