package com.zetra.econsig.persistence.query.movimento;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaResumoExportacaoQuery</p>
 * <p>Description: Lista o resumo da exportação de movimento do período atual</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaResumoExportacaoQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public boolean exportar;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select "
            + "svc.svcCodigo, "
            + "svc.svcIdentificador, "
            + "svc.svcDescricao, "
            + "cnv.cnvCodVerba, "
            + "COUNT(*), "
            + (exportar ? "SUM(ade.adeVlr) " : "SUM(prd.prdVlrPrevisto) ");

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN org.periodoExportacaoSet pex ");

        if (!exportar) {
            // Se está reexportando, obtém os contratos que já possuem parcelas em processamento
            // e soma o valor da parcela e não do contrato
            corpoBuilder.append("INNER JOIN ade.parcelaDescontoPeriodoSet prd ");
        }

        corpoBuilder.append("WHERE ade.adeAnoMesIni <= pex.pexPeriodo ");
        corpoBuilder.append("AND ade.adeData <= pex.pexDataFim ");
        corpoBuilder.append("AND (COALESCE(ade.adePrazo, 999999) > COALESCE(ade.adePrdPagas, 0) OR ade.adeVlrSdoRet IS NOT NULL) ");
        corpoBuilder.append("AND ade.adeIntFolha = ").append(CodedValues.INTEGRA_FOLHA_SIM).append(" ");

        if (!exportar) {
            // Se está reexportando obtém as parcelas do período
            corpoBuilder.append("AND prd.prdDataDesconto = pex.pexPeriodo ");
        }

        // Status dos contratos abertos que podem ser exportados
        final List<String> sadCodigosAtivos = new ArrayList<>();
        sadCodigosAtivos.add(CodedValues.SAD_DEFERIDA);
        sadCodigosAtivos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigosAtivos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigosAtivos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        // Status dos contratos que foram cancelados ou liquidados pós corte ou durante o periodo de corte
        final List<String> sadCodigosInativos = new ArrayList<>();
        sadCodigosInativos.add(CodedValues.SAD_CANCELADA);
        sadCodigosInativos.add(CodedValues.SAD_LIQUIDADA);
        // Ocorrências de cancelamento e liquidação
        final List<String> tocCodigosExclusao = new ArrayList<>();
        tocCodigosExclusao.add(CodedValues.TOC_TARIF_LIQUIDACAO);
        tocCodigosExclusao.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);

        corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigosAtivos, "','")).append("') ");
        corpoBuilder.append("OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigosInativos, "','")).append("') ");
        //Liquidados do período exportado ou pós corte.
        corpoBuilder.append("AND (EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca WHERE 1=1 AND (oca.ocaData > pex.pexDataFim OR oca.ocaPeriodo = pex.pexPeriodo) ");
        corpoBuilder.append("AND oca.tipoOcorrencia.tocCodigo IN ('").append(TextHelper.join(tocCodigosExclusao, "','")).append("')))");
        corpoBuilder.append("))");

        // Exporta contratos de servidores excluidos
        final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, AcessoSistema.getAcessoUsuarioSistema());
        final boolean enviaADEExcluido = ((param == null) || param.equals(CodedValues.TPC_SIM));
        if (!enviaADEExcluido) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if ((estCodigos != null) && (estCodigos.size() > 0)) {
            corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" GROUP BY svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao, cnv.cnvCodVerba");
        corpoBuilder.append(" ORDER BY svc.svcDescricao, cnv.cnvCodVerba");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((estCodigos != null) && (estCodigos.size() > 0)) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "SVC_CODIGO",
                "SVC_IDENTIFICADOR",
                "SVC_DESCRICAO",
                "CNV_COD_VERBA",
                "QTDE",
                "VLR"
        };
    }
}
