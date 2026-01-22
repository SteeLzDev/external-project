package com.zetra.econsig.persistence.query.sdp.despesacomum;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusDespesaComumEnum;

/**
 * <p>Title: ListaDespesaComumEnderecoQuery</p>
 * <p>Description: Lista as despeas comuns de um endereço.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaDespesaComumEnderecoQuery extends HQuery {

    public String echCodigo;
    public String plaCodigo;
    public String tppCodigo;
    public String pplValor;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo =  " SELECT dec.decCodigo, ";
        corpo += " ech.echCodigo, ";
        corpo += " pla.plaCodigo, ";
        corpo += " dec.statusDespesaComum, ";
        corpo += " dec.decValor, ";
        corpo += " dec.decValorRateio, ";
        corpo += " dec.decPrazo, ";
        corpo += " dec.decIdentificador, ";
        corpo += " dec.decData, ";
        corpo += " dec.decDataIni, ";
        corpo += " dec.decDataFim, ";
        corpo += " dec.postoRegistroServidor.posCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM DespesaComum dec ");
        corpoBuilder.append(" INNER JOIN dec.enderecoConjHabitacional ech ");
        corpoBuilder.append(" INNER JOIN dec.plano pla ");

        if (!TextHelper.isNull(tppCodigo)) {
            corpoBuilder.append(" INNER JOIN pla.parametroPlanoSet ppl ");
        }

        corpoBuilder.append(" WHERE dec.statusDespesaComum.sdcCodigo = '").append(StatusDespesaComumEnum.ATIVO.getCodigo()).append("'");

        if (!TextHelper.isNull(tppCodigo)) {
            corpoBuilder.append(" AND ppl.tppCodigo ").append(criaClausulaNomeada("tppCodigo", tppCodigo));
        }

        if (!TextHelper.isNull(tppCodigo) && !TextHelper.isNull(pplValor)) {
            corpoBuilder.append(" AND ppl.pplValor ").append(criaClausulaNomeada("pplValor", pplValor));
        }

        if (!TextHelper.isNull(plaCodigo)) {
            corpoBuilder.append(" AND pla.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" AND ech.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (!TextHelper.isNull(tppCodigo)) {
            defineValorClausulaNomeada("tppCodigo", tppCodigo, query);
        }

        if (!TextHelper.isNull(tppCodigo) && !TextHelper.isNull(pplValor)) {
            defineValorClausulaNomeada("pplValor", pplValor, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DEC_CODIGO,
                Columns.ECH_CODIGO,
                Columns.PLA_CODIGO,
                Columns.DEC_SDC_CODIGO,
                Columns.DEC_VALOR,
                Columns.DEC_VALOR_RATEIO,
                Columns.DEC_PRAZO,
                Columns.DEC_IDENTIFICADOR,
                Columns.DEC_DATA,
                Columns.DEC_DATA_INI,
                Columns.DEC_DATA_FIM,
                Columns.DEC_POS_CODIGO
        };
    }

}
