package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaConsignacaoCancAutomaticoQuery</p>
 * <p>Description: Listagem de Consignações para Cancelamento Automático</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoCancAutomaticoQuery extends HQuery {

	public String rseCodigo;
    public String csaCodigo;
    public List<String> sadCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico.paramSvcConsignanteSet pse ");
        corpoBuilder.append("LEFT OUTER JOIN ade.solicitacaoAutorizacaoSet soa WITH ");
        corpoBuilder.append("soa.tipoSolicitacao.tisCodigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO).append("' ");
        corpoBuilder.append("LEFT OUTER JOIN ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad WITH ");
        corpoBuilder.append("rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // OBS: O operador aqui deve ser ">" apenas, pois o cálculo do between entre duas datas retorna um valor maior que
            // a subtração dos dias entre as duas datas.
            corpoBuilder.append("WHERE (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(ade.adeData) and current_date()) > ");
        } else {
            corpoBuilder.append("WHERE (TO_DAYS(current_date()) - TO_DAYS(ade.adeData)) >= ");
        }

        corpoBuilder.append(" (CASE pse.tipoParamSvc.tpsCodigo WHEN '").append(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).append("' THEN ");
        corpoBuilder.append(" coalesce((SELECT CASE isnumeric_ne(psc9.pscVlr) WHEN 1 THEN to_numeric_ne(psc9.pscVlr) ELSE NULL END ");
        corpoBuilder.append(" FROM cnv.servico.paramSvcConsignatariaSet psc9 WHERE psc9.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).append("' ");
        corpoBuilder.append(" AND psc9.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
        corpoBuilder.append(" AND psc9.servico.svcCodigo = cnv.servico.svcCodigo), (CASE ISNUMERIC(pse.pseVlr) WHEN 1 THEN TO_NUMERIC(COALESCE(NULLIF(TRIM(pse.pseVlr), ''), '0')) ELSE 99999 END)) ");
        corpoBuilder.append(" ELSE ");
        corpoBuilder.append(" (CASE ISNUMERIC(pse.pseVlr) WHEN 1 THEN TO_NUMERIC(COALESCE(NULLIF(TRIM(pse.pseVlr), ''), '0')) ELSE 99999 END) ");
        corpoBuilder.append(" END) ");

        corpoBuilder.append("AND NULLIF(TRIM(pse.pseVlr), '') IS NOT NULL ");

        if (!TextHelper.isNull(rseCodigo)) {
            // Se os parâmetros são válidos, insere as ocorrências para um servidor, mas
            // se os parâmetros são nulos ou vazios, insere ocorrência para todos os servidores
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo = :rseCodigo ");
        }
        if (!TextHelper.isNull(csaCodigo)) {
            // Se o usuário é de consignatária ou correspondente, não cancela as
            // consignações da consignatária do usuário
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo <> :csaCodigo ");
        }

        // Não deixa fazer o cancelamento de contratos cuja compra já teve saldo devedor pago.
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append(" SELECT rad2.adeCodigoDestino");
        corpoBuilder.append(" FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad2");
        corpoBuilder.append(" WHERE rad2.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND (rad2.radDataPgtSaldo IS NOT NULL OR rad2.statusCompra.stcCodigo in ('");
        corpoBuilder.append(StatusCompraEnum.LIQUIDADO.getCodigo()).append("', '");
        corpoBuilder.append(StatusCompraEnum.FINALIZADO.getCodigo()).append("'))");
        corpoBuilder.append(")");

        // Não deixa fazer o cancelamento de contratos derivados de leilão
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append(" SELECT rad3.adeCodigoDestino");
        corpoBuilder.append(" FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad3");
        corpoBuilder.append(" WHERE rad3.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_LEILAO_SOLICITACAO).append("'");
        corpoBuilder.append(") ");

        // Não é uma solicitação de leilão
        corpoBuilder.append(" AND soa.soaCodigo is null ");

        // Verifica de acordo com os sadCodigos informados, quais cancelamentos automáticos devem ser executados
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            StringBuilder corpoComplementarBuilder = new StringBuilder();

            if (sadCodigos.contains(CodedValues.SAD_SOLICITADO)) {
                if (corpoComplementarBuilder.length() > 0) {
                    corpoComplementarBuilder.append(" OR ");
                }
                corpoComplementarBuilder.append("(");
                corpoComplementarBuilder.append("ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SOLICITADO).append("' AND ");
                corpoComplementarBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF).append("'");
                corpoComplementarBuilder.append(")");
            }

            if (sadCodigos.contains(CodedValues.SAD_AGUARD_CONF)) {
                if (corpoComplementarBuilder.length() > 0) {
                    corpoComplementarBuilder.append(" OR ");
                }
                corpoComplementarBuilder.append("(");
                corpoComplementarBuilder.append("ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' AND ");
                corpoComplementarBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).append("' AND ");
                corpoComplementarBuilder.append("rad.adeCodigoDestino IS NULL AND ");
                corpoComplementarBuilder.append("COALESCE(ade.adePodeConfirmar, 'N') <> 'S'");
                corpoComplementarBuilder.append(") OR (");
                corpoComplementarBuilder.append("ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' AND ");
                corpoComplementarBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF).append("' AND ");
                corpoComplementarBuilder.append("rad.adeCodigoDestino IS NOT NULL");
                corpoComplementarBuilder.append(")");
            }

            if (sadCodigos.contains(CodedValues.SAD_AGUARD_DEFER)) {
                if (corpoComplementarBuilder.length() > 0) {
                    corpoComplementarBuilder.append(" OR ");
                }
                corpoComplementarBuilder.append("(");
                corpoComplementarBuilder.append("ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_DEFER).append("' AND ");
                corpoComplementarBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_CONSIG_NAO_DEF).append("'");
                corpoComplementarBuilder.append(")");
            }

            corpoBuilder.append("AND (").append(corpoComplementarBuilder).append(")");
        } else {
            // Se não passou nenhum sadCodigo, então não retorna nada
            corpoBuilder.append("AND 1 = 2");
        }

        // Agrupa o resultado para retornar apenas os distintos
        corpoBuilder.append(" GROUP BY ade.adeCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
        		Columns.ADE_CODIGO
         };
    }
}
