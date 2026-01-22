package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoRelacionamentoQuery</p>
 * <p>Description: Listagem de relacionamentos de consignação</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoRelacionamentoQuery extends HQuery {

    public List<String> adeCodigoList;
    public String adeCodigoOrigem;
    public String adeCodigoDestino;

    public String csaCodigoOrigem;
    public String csaCodigoDestino;

    public String tntCodigo;
    public List<String> stcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select rad.adeCodigoOrigem, rad.adeCodigoDestino, rad.radDataInfSaldo, rad.radDataAprSaldo, rad.radDataPgtSaldo, rad.radDataLiquidacao, rad.tntCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM RelacionamentoAutorizacao rad ");

        if (!TextHelper.isNull(csaCodigoOrigem)) {
            corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");
            corpoBuilder.append(" INNER JOIN adeOrigem.verbaConvenio vcoOrigem ");
            corpoBuilder.append(" INNER JOIN vcoOrigem.convenio cnvOrigem ");
        }
        if (!TextHelper.isNull(csaCodigoDestino)) {
            corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
            corpoBuilder.append(" INNER JOIN adeDestino.verbaConvenio vcoDestino ");
            corpoBuilder.append(" INNER JOIN vcoDestino.convenio cnvDestino ");
        }

        corpoBuilder.append(" WHERE 1=1 ");

        if (adeCodigoList != null && !adeCodigoList.isEmpty()) {
            corpoBuilder.append(" AND (rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoList", adeCodigoList));
            corpoBuilder.append(" OR rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoList", adeCodigoList)).append(")");
        }

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            corpoBuilder.append(" AND rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));
        }

        if (!TextHelper.isNull(adeCodigoDestino)) {
            corpoBuilder.append(" AND rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));
        }

        if (!TextHelper.isNull(csaCodigoOrigem)) {
            corpoBuilder.append(" AND cnvOrigem.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem));
        }

        if (!TextHelper.isNull(csaCodigoDestino)) {
            corpoBuilder.append(" AND cnvDestino.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigoDestino", csaCodigoDestino));
        }

        if (!TextHelper.isNull(tntCodigo)) {
            corpoBuilder.append(" and rad.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));
        }

        if (stcCodigo != null && !stcCodigo.isEmpty()) {
            corpoBuilder.append(" and rad.statusCompra.stcCodigo ").append(criaClausulaNomeada("stcCodigo", stcCodigo));
        }

        corpoBuilder.append(" order by rad.radData desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (adeCodigoList != null && !adeCodigoList.isEmpty()) {
            defineValorClausulaNomeada("adeCodigoList", adeCodigoList, query);
        }

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        }

        if (!TextHelper.isNull(adeCodigoDestino)) {
            defineValorClausulaNomeada("adeCodigoDestino", adeCodigoDestino, query);
        }

        if (!TextHelper.isNull(csaCodigoOrigem)) {
            defineValorClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem, query);
        }

        if (!TextHelper.isNull(csaCodigoDestino)) {
            defineValorClausulaNomeada("csaCodigoDestino", csaCodigoDestino, query);
        }

        if (!TextHelper.isNull(tntCodigo)) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        if (stcCodigo != null && !stcCodigo.isEmpty()) {
            defineValorClausulaNomeada("stcCodigo", stcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RAD_ADE_CODIGO_ORIGEM,
                Columns.RAD_ADE_CODIGO_DESTINO,
                Columns.RAD_DATA_INF_SALDO,
                Columns.RAD_DATA_APR_SALDO,
                Columns.RAD_DATA_PGT_SALDO,
                Columns.RAD_DATA_LIQUIDACAO,
                Columns.RAD_TNT_CODIGO
        };
    }
}
