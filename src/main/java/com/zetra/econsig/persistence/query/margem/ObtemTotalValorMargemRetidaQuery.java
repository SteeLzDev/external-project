package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorMargemRetidaQuery</p>
 * <p>Description: Retorna o valor do somatório dos contratos suspensos marcados como
 * retidos para revisão de margem.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorMargemRetidaQuery extends HQuery {

    public String rseCodigo;
    public Short marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigo = CodedValues.SAD_CODIGOS_SUSPENSOS;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT SUM(ade.adeVlr) ");
        corpoBuilder.append(" FROM AutDesconto ade");

        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.adeIncMargem ").append(criaClausulaNomeada("marCodigo", marCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));

        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.dadosAutorizacaoDescontoSet dad");
        corpoBuilder.append(" WHERE dad.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_VALOR_RETIDO_REVISAO_MARGEM).append("')");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("marCodigo", marCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);

        return query;
    }
}
