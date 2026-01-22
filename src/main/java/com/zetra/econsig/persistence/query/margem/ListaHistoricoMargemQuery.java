package com.zetra.econsig.persistence.query.margem;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoMargemQuery</p>
 * <p>Description: Pesquisa os registros de histórico de margem para o registro servidor
 * informado pelo parâmetro "rseCodigo". Seleciona apenas os registros
 * que possuem o valor da margem antes e depois.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoMargemQuery extends HQuery {

    private final AcessoSistema responsavel;

    // Filtros obrigatórios
    public boolean count = false;
    public String rseCodigo;

    // Filtros Opcionais
    public Short marCodigo;
    public Long adeNumero;
    public String hmrOperacao;
    public Date periodoIni;
    public Date periodoFim;

    public ListaHistoricoMargemQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if (count) {
            corpo = "SELECT COUNT(*) ";
        } else {
            corpo = "SELECT " +
                    "mar.marCodigo, " +
                    "mar.marDescricao, " +
                    "hmr.hmrMargemAntes, " +
                    "hmr.hmrMargemDepois, " +
                    "hmr.hmrData, " +
                    "hmr.hmrOperacao, " +
                    "toc.tocCodigo, " +
                    "toc.tocDescricao, " +
                    "tmo.tmoDescricao, " +
                    "oca.ocaObs, " +
                    "ade.adeCodigo, " +
                    "ade.adeNumero, " +
                    "ade.adeVlr, " +
                    "ade.adeTipoVlr ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM HistoricoMargemRse hmr ");
        corpoBuilder.append(" INNER JOIN hmr.margem mar ");

        if (!count || (adeNumero != null)) {
            corpoBuilder.append(" LEFT OUTER JOIN hmr.ocorrenciaAutorizacao oca ");
            corpoBuilder.append(" LEFT OUTER JOIN oca.tipoOcorrencia toc ");
            corpoBuilder.append(" LEFT OUTER JOIN oca.tipoMotivoOperacao tmo ");
            corpoBuilder.append(" LEFT OUTER JOIN oca.autDesconto ade ");
        }

        corpoBuilder.append(" WHERE hmr.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (responsavel.isCse()) {
            corpoBuilder.append(" AND mar.marExibeCse <> '0' ");
        } else if (responsavel.isSup()) {
            corpoBuilder.append(" AND mar.marExibeSup <> '0' ");
        } else if (responsavel.isOrg()) {
            corpoBuilder.append(" AND mar.marExibeOrg <> '0' ");
        } else if (responsavel.isCsa()) {
            corpoBuilder.append(" AND mar.marExibeCsa <> '0' ");
        } else if (responsavel.isCor()) {
            corpoBuilder.append(" AND mar.marExibeCor <> '0' ");
        } else if (responsavel.isSer()) {
            corpoBuilder.append(" AND mar.marExibeSer <> '0' ");
        }

        if (marCodigo != null) {
            corpoBuilder.append(" AND (mar.marCodigo ").append(criaClausulaNomeada("marCodigo", marCodigo));
            corpoBuilder.append("   OR mar.margemPai.marCodigo ").append(criaClausulaNomeada("marCodigo", marCodigo));
            corpoBuilder.append(" )");
        }
        if (adeNumero != null) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if (periodoIni != null) {
            corpoBuilder.append(" AND hmr.hmrData >= :periodoIni");
        }
        if (periodoFim != null) {
            corpoBuilder.append(" AND hmr.hmrData <= :periodoFim");
        }
        if (!TextHelper.isNull(hmrOperacao)) {
            corpoBuilder.append(" AND hmr.hmrOperacao ").append(criaClausulaNomeada("hmrOperacao", hmrOperacao));
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY hmr.hmrData DESC, hmr.hmrCodigo DESC, ade.adeNumero DESC");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (marCodigo != null) {
            defineValorClausulaNomeada("marCodigo", marCodigo, query);
        }
        if (adeNumero != null) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        if (periodoIni != null) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
        }
        if (periodoFim != null) {
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }
        if (!TextHelper.isNull(hmrOperacao)) {
            defineValorClausulaNomeada("hmrOperacao", hmrOperacao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MAR_CODIGO,
                Columns.MAR_DESCRICAO,
                Columns.HMR_MARGEM_ANTES,
                Columns.HMR_MARGEM_DEPOIS,
                Columns.HMR_DATA,
                Columns.HMR_OPERACAO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.TMO_DESCRICAO,
                Columns.OCA_OBS,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_TIPO_VLR
        };
    }
}
