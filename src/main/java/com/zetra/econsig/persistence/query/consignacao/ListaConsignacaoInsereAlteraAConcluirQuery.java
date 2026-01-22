package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoInsereAlteraAConcluirQuery</p>
 * <p>Description: Lista contratos destino de relacionamento insere/altera não automático de uma origem concluída.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoInsereAlteraAConcluirQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT adeDestino.adeCodigo ");
        corpoBuilder.append("FROM RelacionamentoAutorizacao rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");

        corpoBuilder.append("INNER JOIN adeDestino.verbaConvenio vcoDestino ");
        corpoBuilder.append("INNER JOIN vcoDestino.convenio cnvDestino ");
        corpoBuilder.append("INNER JOIN cnvDestino.orgao orgDestino ");
        corpoBuilder.append("INNER JOIN orgDestino.periodoExportacaoSet pex ");

        corpoBuilder.append("WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA).append("' ");
        corpoBuilder.append("AND adeOrigem.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
        corpoBuilder.append("AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' ");

        // Instancia o objeto para setar os parâmetros
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RAD_ADE_CODIGO_DESTINO
        };
    }

}
