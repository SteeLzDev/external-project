package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoLimitadorNovosAdes</p>
 * <p>Description: Listagem de Consignações ativas de serviços com relacionamento de limite de nova reserva para o serviço corrente</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoLimitadorNovosAdesQuery extends HQuery {

    public String svcCodigo;
    public String rseCodigo;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo)) {
            throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo, svc.svcDescricao ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("LEFT OUTER JOIN svc.relacionamentoServicoByOrigemSet relOri with relOri.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_IMPEDIMENTO_RESERVA_SVC_DESTINO).append("' and relOri.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append("LEFT OUTER JOIN svc.relacionamentoServicoByDestinoSet relDest with relDest.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_IMPEDIMENTO_RESERVA_SVC_DESTINO).append("' and relDest.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" where ");
        corpoBuilder.append(" ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "' , '")).append("') ");
        corpoBuilder.append(" and ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and (relOri.servicoBySvcCodigoOrigem.svcCodigo IS NOT NULL or relDest.servicoBySvcCodigoDestino.svcCodigo IS NOT NULL)");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.SVC_DESCRICAO
        };
    }

}
