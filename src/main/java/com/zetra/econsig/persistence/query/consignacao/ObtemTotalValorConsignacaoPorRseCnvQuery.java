package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import br.com.nostrum.simpletl.util.TextHelper;

/**
 * <p>Title: ObtemTotalValorConsignacaoPorRseCnvQuery</p>
 * <p>Description: Recupera o somatório do valor de consignações de um registro servidor em um ou vários convênios</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorConsignacaoPorRseCnvQuery extends HQuery {

    public String rseCodigo;
    public List<String> sadCodigos;
    public List<String> cnvCodigos;
    public java.sql.Date periodoAtual;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(ade.adeVlr) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");

        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append("with pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS).append("' ");

        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigos", cnvCodigos));

        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        corpoBuilder.append(" and (coalesce(pse.pseVlr, '").append(CodedValues.PSE_BOOLEANO_NAO).append("') = '").append(CodedValues.PSE_BOOLEANO_NAO).append("' ");
        if (!TextHelper.isNull(periodoAtual)){
            corpoBuilder.append(" or (coalesce(pse.pseVlr, '").append(CodedValues.PSE_BOOLEANO_NAO).append("') = '").append(CodedValues.PSE_BOOLEANO_SIM).append("' ");
            corpoBuilder.append(" and ade.adeAnoMesIni >= :periodoAtual)");
        }
        corpoBuilder.append(" )" );

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("cnvCodigos", cnvCodigos, query);
        if (!TextHelper.isNull(periodoAtual)){
            defineValorClausulaNomeada("periodoAtual", periodoAtual, query);
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_VLR
        };
    }
}
