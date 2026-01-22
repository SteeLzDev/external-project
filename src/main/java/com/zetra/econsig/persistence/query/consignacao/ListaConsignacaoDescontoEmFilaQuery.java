package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoDescontoEmFilaQuery</p>
 * <p>Description: Listagem de Consignações de desconto em fila</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoDescontoEmFilaQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo, ade.adeVlr, ade.adeData, svc.svcCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA, "','")).append("') ");
        corpoBuilder.append("AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(" ");
        corpoBuilder.append("AND (svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo)).append(" ");
        corpoBuilder.append("OR EXISTS (SELECT 1 FROM svc.relacionamentoServicoByDestinoSet rsv ");
        corpoBuilder.append("WHERE rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo)).append(" ");
        corpoBuilder.append("AND rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_SERVICOS_CONTROLE_DESCONTO_EM_FILA).append("')) ");
        corpoBuilder.append("ORDER BY ade.adeData ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.SVC_CODIGO
        };
    }
}
