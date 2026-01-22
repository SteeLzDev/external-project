package com.zetra.econsig.persistence.query.parcela;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalParcelasPeriodoQuery</p>
 * <p>Description: Retorna o total de parcelas do periodo de um contrato ou período.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasPeriodoQuery extends HQuery {

    public String adeCodigo;
    public String periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM ParcelaDescontoPeriodo prd ");
        corpoBuilder.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }

        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" AND prd.prdDataDesconto = to_date(:periodo) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        if (!TextHelper.isNull(periodo)) {
            try {
                defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodo);
            }
        }

        return query;
    }
}
