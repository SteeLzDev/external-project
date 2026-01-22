package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoDeferManualDataMenorQuery</p>
 * <p>Description: Listagem de Consignações para Deferimento Manual Com data menor que a informada</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoDeferManualDataMenorQuery extends HQuery {
    public String rseCodigo;
    public Date adeDataContrato;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");

        // Busca consignações Aguard. Deferimento e Aguard. Liquidação
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(CodedValues.SAD_AGUARD_DEFER).append("','").append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("') ");

        // Ignora contratos aguard. liquidação origem de relacionamentos de renegociação
        // pois estes não podem ser liquidados diretamente, e sim o novo contrato confirmado/deferido
        corpoBuilder.append("AND NOT EXISTS ( ");
        corpoBuilder.append("SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("WHERE rad.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("' ");
        corpoBuilder.append("AND adeDestino.statusAutorizacaoDesconto.sadCodigo IN ('").append(CodedValues.SAD_AGUARD_CONF).append("','").append(CodedValues.SAD_AGUARD_DEFER).append("') ");
        corpoBuilder.append(") ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(" ");
        } else {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        if (!TextHelper.isNull(adeDataContrato)) {
            corpoBuilder.append("AND ade.adeData < :adeDataContrato ");
        } else {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(adeDataContrato)) {
            defineValorClausulaNomeada("adeDataContrato", adeDataContrato, query);
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
