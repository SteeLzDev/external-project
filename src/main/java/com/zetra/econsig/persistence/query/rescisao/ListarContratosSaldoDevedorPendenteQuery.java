package com.zetra.econsig.persistence.query.rescisao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;

/**
 * <p>Title: ListarContratosSaldoDevedorPendenteQuery</p>
 * <p>Description: Listar os contratos de um colaborador que continuaram com saldo devedor após retenção da verba rescisória</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarContratosSaldoDevedorPendenteQuery extends HQuery  {

    public String vrrCodigo;

    public ListarContratosSaldoDevedorPendenteQuery() {
        super();
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // lista de status de autorização que permitem solicitação de saldo
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_LIQUIDADA);

        // lista de status de parcelas pagas
        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
        spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo,");
        corpoBuilder.append(" ade.adeNumero, ");
        corpoBuilder.append(" ade.adeData, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" svc.svcDescricao, ");
        corpoBuilder.append(" SUM(COALESCE(prd.prdVlrRealizado,0.00)) ");
        corpoBuilder.append("FROM VerbaRescisoriaRse vrr ");
        corpoBuilder.append("INNER JOIN vrr.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN ade.saldoDevedorSet sdv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append("LEFT OUTER JOIN ade.parcelaDescontoSet prd ");
        corpoBuilder.append("      WITH prd.statusParcelaDesconto.spdCodigo IN ('").append(TextHelper.join(spdCodigos, "','")).append("') ");
        corpoBuilder.append("LEFT OUTER JOIN ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad ");
        corpoBuilder.append("      WITH rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_VERBA_RESCISORIA).append("' ");
        corpoBuilder.append("LEFT OUTER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append("WHERE vrr.vrrCodigo ").append(criaClausulaNomeada("vrrCodigo", vrrCodigo));
        corpoBuilder.append("AND vrr.statusVerbaRescisoria.svrCodigo = '").append(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo()).append("' ");
        corpoBuilder.append("AND svc.naturezaServico.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("'");
        corpoBuilder.append("AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
        corpoBuilder.append("AND ade.adePrazo > ade.adePrdPagas ");
        corpoBuilder.append("AND (adeDestino.adeCodigo IS NULL OR adeDestino.adeVlr < sdv.sdvValor) ");
        corpoBuilder.append("AND oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("'");
        corpoBuilder.append("AND oca.ocaData > vrr.vrrDataIni ");

        corpoBuilder.append("GROUP BY ade.adeCodigo, ");
        corpoBuilder.append(" ade.adeNumero, ");
        corpoBuilder.append(" ade.adeData, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" svc.svcDescricao ");

        corpoBuilder.append("ORDER BY coalesce(").append("ade.adeAnoMesIniRef").append(", ").append("ade.adeAnoMesIni").append("), ");
        corpoBuilder.append("coalesce(").append("ade.adeDataRef").append(", ").append("ade.adeData").append("), ");
        corpoBuilder.append("ade.adeNumero ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("vrrCodigo", vrrCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_DATA,
                Columns.CSA_NOME_ABREV,
                Columns.SVC_DESCRICAO,
                Columns.PRD_VLR_REALIZADO
        };
    }
}
