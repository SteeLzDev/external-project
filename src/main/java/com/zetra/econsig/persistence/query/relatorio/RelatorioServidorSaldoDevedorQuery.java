package com.zetra.econsig.persistence.query.relatorio;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;


public class RelatorioServidorSaldoDevedorQuery extends ReportHQuery {
    private String csaCodigo;
    private String dataInicio;
    private String dataFim;
    private String tipoSaldo;
    private String usuCodigo;
    private List<Long> adeNumeros;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        dataInicio = (String) criterio.getAttribute("periodoIni");
        dataFim = (String) criterio.getAttribute("periodoFim");
        tipoSaldo = (String) criterio.getAttribute("tipoSolicitacao");
        usuCodigo = (String) criterio.getAttribute("usuCodigo");
        adeNumeros = (List<Long>) criterio.getAttribute("ADE_NUMERO_LIST");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("csa.csaNome as csaNome, svc.svcDescricao as svcNome, ade.adeNumero as adeNumero, ade.adeVlr as adeVlr, sdv.sdvValor as sdvValor, oca.ocaData as ocaData, sdv.sdvDataMod as sdvDataMod ");
        corpoBuilder.append("from OcorrenciaAutorizacao oca ");
        corpoBuilder.append("inner join SaldoDevedor sdv on oca.adeCodigo = sdv.adeCodigo ");
        corpoBuilder.append("inner join oca.autDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append("AND oca.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));

        if(!TextHelper.isNull(tipoSaldo)) {
            corpoBuilder.append("AND oca.tocCodigo ").append(criaClausulaNomeada("tipoSaldo", tipoSaldo));
        } else {
            corpoBuilder.append("AND oca.tocCodigo in ('").append(CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO + "', '" + CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR).append("') ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(dataInicio) && !TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND sdv.sdvDataMod between :dataIni and :dataFim");
        }

        if(adeNumeros != null && !adeNumeros.isEmpty()) {
            corpoBuilder.append(" and ade.adeNumero ").append(criaClausulaNomeada("adeNumeros", adeNumeros));
        }

        corpoBuilder.append(" order by sdv.sdvDataMod desc, csa.csaNome ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(dataInicio)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataInicio), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (!TextHelper.isNull(tipoSaldo)) {
            defineValorClausulaNomeada("tipoSaldo", tipoSaldo, query);
        }

        if (adeNumeros != null && !adeNumeros.isEmpty()) {
            defineValorClausulaNomeada("adeNumeros", adeNumeros, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_NOME,
                Columns.SVC_DESCRICAO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.SDV_VALOR,
                Columns.OCA_DATA,
                Columns.SDV_DATA_MOD,
        };
    }
}
