package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoOrdenadaPorAdeDataQuery</p>
 * <p>Description: Listagem de Consignações ordenadas por ade_data</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoAguardandoLiquidacaoQuery extends HQuery {
    public List<String> rseCodigos;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(rseCodigos)) {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        // Busca consignações Aguard. Deferimento e Aguard. Liquidação
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append(", ade.registroServidor.rseCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos)).append(" ");
        corpoBuilder.append("AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigos)).append(" ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_RSE_CODIGO
        };
    }
}
