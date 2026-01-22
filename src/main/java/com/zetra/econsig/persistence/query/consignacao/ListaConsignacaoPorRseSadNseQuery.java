package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoPorRseSadNseQuery</p>
 * <p>Description: Listagem de Consignações de um registro servidor por status autorização desconto e natureza de serviço</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * @param <E>
 */
public class ListaConsignacaoPorRseSadNseQuery extends HQuery {

    public String rseCodigo;
    public List<String> sadCodigos;
    public List<String> nseCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "ade.adeCodigo, " +
                "ade.adeNumero, " +
                "ade.adeVlr, " +
                "ade.adeData, " +
                "ade.adeAnoMesIni, " +
                "ade.adeAnoMesFim, " +
                "ade.adePrazo, " +
                "ade.adePrdPagas, " +
                "ade.adeCodReg, " +
                "ade.adeIndice, " +
                "ade.adePeriodicidade, " +
                "ade.statusAutorizacaoDesconto.sadCodigo, " +
                "vco.convenio.cnvCodigo, " +
                "ade.adeIncMargem, " +
                "ade.adeVlrLiquido ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join svc.naturezaServico nse ");


        corpoBuilder.append(" where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            corpoBuilder.append(" AND nse.nseCodigo ").append(criaClausulaNomeada("nseCodigos", nseCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigos", nseCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_COD_REG,
                Columns.ADE_INDICE,
                Columns.ADE_PERIODICIDADE,
                Columns.ADE_SAD_CODIGO,
                Columns.CNV_CODIGO,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_VLR_LIQUIDO
        };
    }
}
