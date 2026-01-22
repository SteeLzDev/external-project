package com.zetra.econsig.persistence.query.leilao;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaQtdeContratosQuery</p>
 * <p>Description: Lista quantidade de contratos em um determinado status</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAdeSolicitadaLeilaoQuery extends HQuery {

    public String rseCodigo;

    public Date dataInicial;

    public boolean solicitacaoLeilao = false;

    public boolean concretizado = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo();
        String ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT COUNT(*) ");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");

        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.adeData >= :dataInicial ");

        if (solicitacaoLeilao) {
            corpoBuilder.append(" AND EXISTS (");
            corpoBuilder.append(" SELECT 1 FROM ade.solicitacaoAutorizacaoSet soa ");

            if (concretizado) {
                corpoBuilder.append(" INNER JOIN soa.autDesconto origem");
                corpoBuilder.append(" INNER JOIN origem.relacionamentoAutorizacaoByAdeCodigoOrigemSet rel");
                corpoBuilder.append(" INNER JOIN rel.autDescontoByAdeCodigoDestino destino");
                corpoBuilder.append(" INNER JOIN destino.ocorrenciaAutorizacaoSet oca");
                corpoBuilder.append(" WITH oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_DEFERIMENTO_CONTRATO).append("'");
            }

            corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
            corpoBuilder.append(" AND soa.soaDataValidade >= :dataInicial ");
            if (concretizado) {
                corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
            }
            corpoBuilder.append(") ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("dataInicial", dataInicial, query);

        if (solicitacaoLeilao) {
            defineValorClausulaNomeada("tisCodigo", tisCodigo, query);

            if (concretizado) {
                defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.ADE_CODIGO };
    }
}
