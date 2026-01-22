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
 * <p>Title: ListaComprasParaConclusaoQuery</p>
 * <p>Description: Listagem de consignações em processo de compra que devem ser
 * concluídas na importação do retorno atual.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaComprasParaConclusaoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT adeOrigem.adeCodigo, adeDestino.adeCodigo ");
        corpoBuilder.append("FROM RelacionamentoAutorizacao rad ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");

        corpoBuilder.append("INNER JOIN adeDestino.verbaConvenio vcoDestino ");
        corpoBuilder.append("INNER JOIN vcoDestino.convenio cnvDestino ");
        corpoBuilder.append("INNER JOIN cnvDestino.orgao orgDestino ");
        corpoBuilder.append("INNER JOIN orgDestino.periodoExportacaoSet pex ");

        corpoBuilder.append("WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("AND adeOrigem.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("' ");
        corpoBuilder.append("AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("' ");

        corpoBuilder.append("AND pex.pexPeriodo >= add_month(adeOrigem.adeAnoMesFim, coalesce(adeOrigem.adeCarenciaFinal, 0) + :carenciaFolha) "); // Se está no período de conclusão,
        corpoBuilder.append("AND coalesce(adeOrigem.adePrazo, 999999999) <= coalesce(adeOrigem.adePrdPagas, 0) "); // já pagou todas as parcelas,
        corpoBuilder.append("AND coalesce(adeOrigem.adeIntFolha, 1) = 1 "); // o contrato integra na folha,
        corpoBuilder.append("AND adeOrigem.adeVlrSdoMov IS NULL "); // e não tem controle de saldo
        corpoBuilder.append("AND adeOrigem.adeVlrSdoRet IS NULL "); // então deve ser concluído.

        corpoBuilder.append("ORDER BY adeDestino.adeCodigo");

        // Instancia o objeto para setar os parâmetros
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Obtém o parâmetro de carência da folha
        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
        short carenciaFolha = 0;
        if (!TextHelper.isNull(param)) {
            carenciaFolha = Short.parseShort(param.toString());
        }

        query.setParameter("carenciaFolha", carenciaFolha);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RAD_ADE_CODIGO_ORIGEM,
                Columns.RAD_ADE_CODIGO_DESTINO
        };
    }
}
