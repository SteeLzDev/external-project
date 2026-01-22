package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery</p>
 * <p>Description: Lista solicitação de proposta leilão que
 * geram bloqueio para o servidor segundo parâmetros 484 e 550</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tocCodigo = CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA;
        String tntCodigo = CodedValues.TNT_LEILAO_SOLICITACAO;
        String sadCodigo = CodedValues.SAD_CANCELADA;

        Object paramQtdDiasBloqSer = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_SERVIDOR_COM_LEILAO_CANCELADO, AcessoSistema.getAcessoUsuarioSistema());
        int qtdDiasBloqSer = (!TextHelper.isNull(paramQtdDiasBloqSer) && TextHelper.isNum(paramQtdDiasBloqSer)) ? Integer.parseInt(paramQtdDiasBloqSer.toString()) : 0;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT count(oca.ocaCodigo) AS TOTAL");
        corpoBuilder.append(" FROM OcorrenciaAutorizacao oca");
        corpoBuilder.append(" INNER JOIN oca.autDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
        corpoBuilder.append(" WITH rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));

        corpoBuilder.append(" WHERE oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" AND oca.ocaData > add_day(current_date(), ").append(qtdDiasBloqSer * -1).append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL"
        };
    }
}
