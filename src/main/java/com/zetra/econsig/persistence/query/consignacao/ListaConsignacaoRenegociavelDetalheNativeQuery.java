package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;

public class ListaConsignacaoRenegociavelDetalheNativeQuery extends ListaConsignacaoRenegociavelNativeQuery {

    public String adeCodigo;
    public boolean servidorPossuiAde;
    public boolean adePossuiSolicitacaoSaldoLiq;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        tipoOperacao = (tipoOperacao == null) ? "" : tipoOperacao;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on vco.vco_codigo = ade.vco_codigo ");
        corpoBuilder.append("inner join tb_convenio cnv on cnv.cnv_codigo = vco.cnv_codigo ");
        corpoBuilder.append("inner join tb_servico svc on svc.svc_codigo = cnv.svc_codigo ");
        corpoBuilder.append(" WHERE ade.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (servidorPossuiAde) {
            adicionaClausulaServidorPossuiAde(corpoBuilder);
        }
        if (adePossuiSolicitacaoSaldoLiq) {
            adicionaClausulaAdePossuiSolicitacaoSaldoLiq(corpoBuilder);
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (corpoBuilder.toString().contains(":adeCodigo")) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }
        if (corpoBuilder.toString().contains(":csaCodigo")) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (corpoBuilder.toString().contains(":svcCodigo")) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        return query;
    }
}
