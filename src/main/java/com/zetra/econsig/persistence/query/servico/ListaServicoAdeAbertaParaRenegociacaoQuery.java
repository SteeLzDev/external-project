package com.zetra.econsig.persistence.query.servico;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoAdeAbertaParaRenegociacaoQuery</p>
 * <p>Description: Lista os códigos dos serviços dos contratos abertos passíveis de renegociação via lote.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoAdeAbertaParaRenegociacaoQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String adeIdentificador;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigo = new ArrayList<String>();
        sadCodigo.add(CodedValues.SAD_DEFERIDA);
        sadCodigo.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigo.add(CodedValues.SAD_ESTOQUE);
        sadCodigo.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigo.add(CodedValues.SAD_EMCARENCIA);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cnv.servico.svcCodigo");

        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN ade.dadosAutorizacaoDescontoSet dad14 with dad14.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_IDENTIFICADOR_RENEGOCIACAO).append("'");

        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND dad14.dadValor ").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO
        };
    }

}
