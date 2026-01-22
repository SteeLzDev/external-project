package com.zetra.econsig.persistence.query.lote;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAdeAbertaParaRenegociacaoQuery</p>
 * <p>Description: Lista consignações abertas para relacionamento em renegociação via lote.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAdeAbertaParaRenegociacaoQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String svcCodigo;
    public String adeIdentificador;
    public boolean fixaServico = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigo = new ArrayList<String>();
        sadCodigo.add(CodedValues.SAD_DEFERIDA);
        sadCodigo.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigo.add(CodedValues.SAD_ESTOQUE);
        sadCodigo.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigo.add(CodedValues.SAD_EMCARENCIA);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.adeCodigo");

        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN svc.relacionamentoServicoByDestinoSet rsv ");
        corpoBuilder.append(" INNER JOIN ade.dadosAutorizacaoDescontoSet dad14 with dad14.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_IDENTIFICADOR_RENEGOCIACAO).append("'");

        corpoBuilder.append(" WHERE rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("'");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        if (fixaServico) {
            corpoBuilder.append(" AND rsv.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        corpoBuilder.append(" AND dad14.dadValor ").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
