package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoLiquidacaoAutomaticaQuery</p>
 * <p>Description: Listagem de consignações em processo de compra para liquidação
 * automática após X dias passados da compra.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoLiquidacaoAutomaticaQuery extends HQuery {

    private final int diasLiqAutomatica;

    public ListaConsignacaoLiquidacaoAutomaticaQuery(int diasLiqAutomatica) {
        this.diasLiqAutomatica = diasLiqAutomatica;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT adeOrigem.adeCodigo ");
        corpoBuilder.append("FROM RelacionamentoAutorizacao rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("INNER JOIN adeDestino.registroServidor rse ");
        corpoBuilder.append("WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("AND adeOrigem.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("' ");
        corpoBuilder.append("AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' ");
        corpoBuilder.append("AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");

        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radData) and current_date()) > :diasLiqAutomatica ");
        } else {
            corpoBuilder.append("AND (TO_DAYS(current_date()) - TO_DAYS(rad.radData)) >= :diasLiqAutomatica ");
        }

        corpoBuilder.append("GROUP BY adeOrigem.adeCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setParameter("diasLiqAutomatica", diasLiqAutomatica);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
