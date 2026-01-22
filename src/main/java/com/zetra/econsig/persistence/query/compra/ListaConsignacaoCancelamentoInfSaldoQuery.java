package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: ListaConsignacaoCancelamentoInfSaldoQuery</p>
 * <p>Description: Listagem de consignações para cancelamento do processo de compra em virtude do atraso
 * na informação do saldo devedor de contratos envolvidos em processo de compra.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoCancelamentoInfSaldoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT adeDestino.adeCodigo ");
        corpoBuilder.append("FROM RelacionamentoAutorizacao rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");
        corpoBuilder.append("INNER JOIN adeOrigem.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN adeDestino.verbaConvenio vcoDestino ");
        corpoBuilder.append("INNER JOIN vcoDestino.convenio cnvDestino ");
        corpoBuilder.append("INNER JOIN cnvDestino.consignataria csaDestino ");
        corpoBuilder.append("INNER JOIN cnv.servico.paramSvcConsignanteSet pse149 ");
        corpoBuilder.append("INNER JOIN cnv.servico.paramSvcConsignanteSet pse152 ");
        corpoBuilder.append("WHERE pse149.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("AND pse152.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV).append("' ");
        corpoBuilder.append("AND rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("AND adeOrigem.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("' ");
        corpoBuilder.append("AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' ");
        corpoBuilder.append("AND NULLIF(TRIM(pse149.pseVlr), '') IS NOT NULL ");
        corpoBuilder.append("AND COALESCE(pse152.pseVlr, '0') = '2' ");
        corpoBuilder.append("AND csa.csaCodigo <> csaDestino.csaCodigo ");
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataRefInfSaldo) and current_date()) > ");
        } else {
            corpoBuilder.append("AND (TO_DAYS(current_date()) - TO_DAYS(rad.radDataRefInfSaldo)) >= ");
        }
        corpoBuilder.append("(CASE ISNUMERIC(pse149.pseVlr) WHEN 1 THEN TO_NUMERIC(COALESCE(NULLIF(TRIM(pse149.pseVlr), ''), '0')) ELSE 99999 END) ");

        corpoBuilder.append(" AND rad.radDataRefInfSaldo IS NOT NULL AND rad.radDataInfSaldo IS NULL ");
        corpoBuilder.append(" AND rad.statusCompra.stcCodigo = '").append(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo()).append("' ");

        // Adiciona cláusula que verifica se a compra pode ser cancelada
        corpoBuilder.append(CompraPassivelCancelamentoQuery.gerarClausulaPendenciaCancelCompra());

        corpoBuilder.append("GROUP BY adeDestino.adeCodigo ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
