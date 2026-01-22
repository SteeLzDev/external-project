package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoRseNseQuery</p>
 * <p>Description: listas os contratos de um servidor cuja natureza do serviço é a mesma
 *                 do serviço dado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoRseNseQuery extends HQuery {

    public List<String> sadCodigos;
    public List<String> tocCodigos;
    public String rseCodigo;
    public String nseCodigo;
    public boolean somenteValorReduzido = false; // Lista ADEs com ade_vlr_folha < ade_vlr

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(nseCodigo)) {
            throw new HQueryException("mensagem.erro.informe.svc.natureza", (AcessoSistema) null);
        }

        String corpo = "select " +
                "nse.nseDescricao, " +
                "max(oca.ocaData) AS OCA_DATA, " +
                "sad.sadCodigo, " +
                "sad.sadDescricao, " +
                "ade.adePrazoRef, " +
                "ade.adePrdPagasTotal ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("inner join ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join svc.naturezaServico nse ");

        corpoBuilder.append("where nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        corpoBuilder.append(" and ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (somenteValorReduzido) {
            corpoBuilder.append(" and ade.adeVlrFolha < ade.adeVlr "); // Reduzidos
        }

        if (tocCodigos != null) {
            corpoBuilder.append(" and oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        if (sadCodigos != null) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        corpoBuilder.append(" group by ade.adeCodigo, nse.nseDescricao, sad.sadCodigo, sad.sadDescricao, ade.adePrazoRef, ade.adePrdPagasTotal ");

        corpoBuilder.append(" order by max(oca.ocaData) desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (tocCodigos != null) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        if (sadCodigos != null) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSE_DESCRICAO,
                Columns.OCA_DATA,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.ADE_PRAZO_REF,
                Columns.ADE_PRD_PAGAS_TOTAL
        };
    }

}
