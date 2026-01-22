package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoDeferAutomaticoQuery</p>
 * <p>Description: Listagem de Consignações para Deferimento Automático</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoDeferAutomaticoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico.paramSvcConsignanteSet pse ");

        // Busca consignações Aguard. Deferimento
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_DEFER).append("' ");

        // De serviços configurados com parâmetro de dias para deferimento automático
        corpoBuilder.append("AND pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS).append("' ");
        corpoBuilder.append("AND NULLIF(TRIM(pse.pseVlr), '') IS NOT NULL ");

        // Onde o prazo, em dias úteis ou corridos, já foi alcançado
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_DEFER_AUTOMATICO_ADE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // OBS: O operador aqui deve ser ">" apenas, pois o cálculo do between entre duas datas retorna um valor maior que
            // a subtração dos dias entre as duas datas.
            corpoBuilder.append("AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(COALESCE(ade.adeDataConfirmacao, ade.adeData)) and current_date()) > ");
        } else {
            corpoBuilder.append("AND (TO_DAYS(current_date()) - TO_DAYS(COALESCE(ade.adeDataConfirmacao, ade.adeData))) >= ");
        }
        corpoBuilder.append("(CASE ISNUMERIC(pse.pseVlr) WHEN 1 THEN TO_NUMERIC(COALESCE(NULLIF(TRIM(pse.pseVlr), ''), '0')) ELSE 99999 END) ");

        // Agrupa o resultado para retornar apenas os distintos
        corpoBuilder.append("GROUP BY ade.adeCodigo");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
