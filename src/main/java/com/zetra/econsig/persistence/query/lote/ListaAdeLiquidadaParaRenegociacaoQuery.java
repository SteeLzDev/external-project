package com.zetra.econsig.persistence.query.lote;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoLiquidadaQuery</p>
 * <p>Description: Lista consignações liquidadas dentro de um período de datas
 * para relacionamento em renegociação via lote.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAdeLiquidadaParaRenegociacaoQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String svcCodigo;
    public Date ocaData;
    public boolean fixaServico = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.adeCodigo");

        corpoBuilder.append(" FROM OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" INNER JOIN oca.autDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN svc.relacionamentoServicoByDestinoSet rsv ");

        corpoBuilder.append(" WHERE rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("'");
        corpoBuilder.append(" AND oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("'");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_LIQUIDADA).append("'");
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        if (fixaServico) {
            corpoBuilder.append(" AND rsv.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        corpoBuilder.append(" AND oca.ocaData >= :ocaData");

        corpoBuilder.append(" ORDER BY oca.ocaData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("ocaData", ocaData, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
