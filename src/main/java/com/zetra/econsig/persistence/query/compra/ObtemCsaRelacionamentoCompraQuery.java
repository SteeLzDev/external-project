package com.zetra.econsig.persistence.query.compra;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemCsaRelacionamentoCompraQuery</p>
 * <p>Description: Retorna as Consignatárias relacionadas em uma compra</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemCsaRelacionamentoCompraQuery extends HQuery {

    public String adeCodigoOrigem;
    public String adeCodigoDestino;
    public String stcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if ((TextHelper.isNull(adeCodigoOrigem) && TextHelper.isNull(adeCodigoDestino)) || TextHelper.isNull(stcCodigo)) {
            throw new HQueryException("mensagem.erro.parametros.ausentes", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select csa.csaCodigo from Consignataria csa ");
        corpoBuilder.append("where exists ( ");
        corpoBuilder.append("  select 1 from RelacionamentoAutorizacao rad ");
        corpoBuilder.append("  inner join rad.autDescontoByAdeCodigoOrigem ade ");
        corpoBuilder.append("  inner join ade.verbaConvenio vco ");
        corpoBuilder.append("  inner join vco.convenio cnv ");
        corpoBuilder.append("  where rad.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("    and rad.statusCompra.stcCodigo ").append(criaClausulaNomeada("stcCodigo", stcCodigo));
        corpoBuilder.append("    and cnv.consignataria.csaCodigo = csa.csaCodigo ");
        if (!TextHelper.isNull(adeCodigoOrigem)) {
            corpoBuilder.append("    and rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));
        }
        if (!TextHelper.isNull(adeCodigoDestino)) {
            corpoBuilder.append("    and rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));
        }
        corpoBuilder.append(") or exists ( ");
        corpoBuilder.append("  select 1 from RelacionamentoAutorizacao rad ");
        corpoBuilder.append("  inner join rad.autDescontoByAdeCodigoDestino ade ");
        corpoBuilder.append("  inner join ade.verbaConvenio vco ");
        corpoBuilder.append("  inner join vco.convenio cnv ");
        corpoBuilder.append("  where rad.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("    and rad.statusCompra.stcCodigo ").append(criaClausulaNomeada("stcCodigo", stcCodigo));
        corpoBuilder.append("    and cnv.consignataria.csaCodigo = csa.csaCodigo ");
        if (!TextHelper.isNull(adeCodigoOrigem)) {
            corpoBuilder.append("    and rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));
        }
        if (!TextHelper.isNull(adeCodigoDestino)) {
            corpoBuilder.append("    and rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));
        }
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("stcCodigo", stcCodigo, query);
        if (!TextHelper.isNull(adeCodigoOrigem)) {
            defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        }
        if (!TextHelper.isNull(adeCodigoDestino)) {
            defineValorClausulaNomeada("adeCodigoDestino", adeCodigoDestino, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO
        };
    }
}
