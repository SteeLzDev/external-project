package com.zetra.econsig.persistence.query.saldodevedor;

import java.util.Arrays;
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
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery</p>
 * <p>Description: Lista solicitação de liquidação de contrato com saldo pago e anexo e não liquidado.</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery extends HQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_ENTRE_COMP_SALDO_LIQ_ADE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        String tpsPrazoSolicitacoesSaldoDevedor = CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER;
        String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo();
        String ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();

        /*
         * Status que permitem liquidação de contrato,
         * se alterados, devem ser alterados também na opção de incluir anexo no pagamento na edição de consignação para servidor.
         *
         */
        List<String> sadCodigo = Arrays.asList(new String[]{
                CodedValues.SAD_DEFERIDA,
                CodedValues.SAD_EMANDAMENTO,
                CodedValues.SAD_ESTOQUE,
                CodedValues.SAD_ESTOQUE_MENSAL,
                CodedValues.SAD_ESTOQUE_NAO_LIBERADO,
                CodedValues.SAD_EMCARENCIA
        });

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT csa.csaCodigo, csa.csaAtivo, csa.ncaCodigo, soa.soaData ");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append(" INNER JOIN ade.solicitacaoAutorizacaoSet soa ");
        corpoBuilder.append(" WHERE 1 = 1 ");
        corpoBuilder.append(" AND pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsPrazoSolicitacoesSaldoDevedor));
        corpoBuilder.append(" AND soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (usaDiasUteis) {
            corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(soa.soaData) and current_date()) > ");
        } else {
            corpoBuilder.append(" AND (TO_DAYS(CURRENT_DATE()) - TO_DAYS(soa.soaData)) >= ");
        }

        corpoBuilder.append(" (CASE WHEN isnumeric_ne(pse.pseVlr) = 1 THEN to_numeric_ne(pse.pseVlr) ELSE 0 END) ");
        corpoBuilder.append(" ORDER BY soa.soaData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tpsCodigo", tpsPrazoSolicitacoesSaldoDevedor, query);
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_ATIVO,
                Columns.NCA_CODIGO,
                Columns.SOA_DATA
        };
    }
}
