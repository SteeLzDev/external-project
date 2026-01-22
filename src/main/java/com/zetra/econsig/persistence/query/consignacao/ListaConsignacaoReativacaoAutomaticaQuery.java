package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaConsignacaoReativacaoAutomaticaQuery</p>
 * <p>Description: Listagem de Consignações para reativação automática</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoReativacaoAutomaticaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        // Consignações suspensas que possuem data de reativação automática menor que data atual
        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_SUSPENSOS, "','")).append("') ");
        corpoBuilder.append("AND adeDataReativacaoAutomatica <= current_date() ");

        return instanciarQuery(session, corpoBuilder.toString());
    }
}
