package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarOperacoesLiberacaoMargemParaConfirmacaoQuery</p>
 * <p>Description: Listar dados das operações de liberação de margem pendentes para confirmar se houve liberação de margem</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarOperacoesLiberacaoMargemParaConfirmacaoQuery extends HQuery {
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select adeDestino.adeCodigo ");
        corpoBuilder.append(", adeDestino.adeVlr ");
        corpoBuilder.append(", olm.registroServidor.rseCodigo ");
        corpoBuilder.append(", olm.usuario.usuCodigo ");
        corpoBuilder.append(", olm.consignataria.csaCodigo ");
        corpoBuilder.append(", olm.olmIpAcesso ");
        corpoBuilder.append(", sum(ade.adeVlr) ");
        corpoBuilder.append("from OperacaoLiberaMargem olm ");
        corpoBuilder.append("join olm.autDesconto ade ");
        corpoBuilder.append("join ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad ");
        corpoBuilder.append("join rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("where olm.olmBloqueio = 'N' ");
        corpoBuilder.append("and olm.olmConfirmada = 'N' ");
        corpoBuilder.append("and rad.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
        corpoBuilder.append("group by adeDestino.adeCodigo ");
        corpoBuilder.append(", adeDestino.adeVlr ");
        corpoBuilder.append(", olm.registroServidor.rseCodigo ");
        corpoBuilder.append(", olm.usuario.usuCodigo ");
        corpoBuilder.append(", olm.consignataria.csaCodigo ");
        corpoBuilder.append(", olm.olmIpAcesso ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OLM_ADE_CODIGO,
                Columns.ADE_VLR,
                Columns.OLM_RSE_CODIGO,
                Columns.OLM_USU_CODIGO,
                Columns.OLM_CSA_CODIGO,
                Columns.OLM_IP_ACESSO,
                "TOTAL_VLR_ORIGEM",
        };
    }

}
