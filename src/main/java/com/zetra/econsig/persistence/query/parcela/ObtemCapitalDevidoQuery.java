package com.zetra.econsig.persistence.query.parcela;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemCapitalDevidoQuery</p>
 * <p>Description: Obtém o total de saldo devedor das parcelas parciais de um contrato
 *                 , mais o somatório das parcelas rejeitadas, se o sistema e/ou CSA não preservam parcela.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemCapitalDevidoQuery extends HQuery {

    public String adeCodigo;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(adeCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        ConsignacaoDelegate adeDelegate = new ConsignacaoDelegate();
        boolean preservaParcela = false;
        try {
            preservaParcela = adeDelegate.sistemaPreservaParcela(adeCodigo, responsavel);
        } catch (AutorizacaoControllerException e) {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel, e);
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select sum(prd.prdVlrPrevisto - prd.prdVlrRealizado) ");
        corpoBuilder.append("from ParcelaDesconto prd ");
        corpoBuilder.append(" where prd.autDesconto.adeCodigo").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" and prd.prdVlrPrevisto <> prd.prdVlrRealizado");
        if (preservaParcela) {
            corpoBuilder.append(" and prd.statusParcelaDesconto.spdCodigo <> '").append(CodedValues.SPD_REJEITADAFOLHA).append("'");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CAPITAL_DEVIDO"
        };
    }

}
