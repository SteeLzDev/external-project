package com.zetra.econsig.persistence.query.saldodevedor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoSaldoDevedorPrazoQuery</p>
 * <p>Description: Lista solicitação do saldo devedor pelo prazo.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoSaldoDevedorPrazoQuery extends HQuery {

    private final String adeCodigo;
    private final String tisCodigo;

    public ListaSolicitacaoSaldoDevedorPrazoQuery(String adeCodigo, String tisCodigo) {
        this.adeCodigo = adeCodigo;
        this.tisCodigo = tisCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_ENTRE_SOLICIT_SALDO_DEV, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        String tpsPrazoSolicitacoesSaldoDevedor = CodedValues.TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR;
        String[] ssoCodigo = {StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo()};

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append(" soa.soaData,");
        corpoBuilder.append(" (CASE WHEN isnumeric_ne(pse.pseVlr) = 1 THEN to_numeric_ne(pse.pseVlr) ELSE 0 END)");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append(" INNER JOIN ade.solicitacaoAutorizacaoSet soa ");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsPrazoSolicitacoesSaldoDevedor));
        corpoBuilder.append(" AND soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));

        if (usaDiasUteis) {
            corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(soa.soaData) and current_date()) <= ");
        } else {
            corpoBuilder.append(" AND (TO_DAYS(CURRENT_DATE()) - TO_DAYS(soa.soaData)) < ");
        }

        corpoBuilder.append(" (CASE WHEN isnumeric_ne(pse.pseVlr) = 1 THEN to_numeric_ne(pse.pseVlr) ELSE 0 END) ");
        corpoBuilder.append(" ORDER BY soa.soaData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("tpsCodigo", tpsPrazoSolicitacoesSaldoDevedor, query);
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SOA_DATA,
                Columns.PSE_VLR
        };
    }
}
