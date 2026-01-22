package com.zetra.econsig.persistence.query.margem;

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
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaContratosCompulsoriosQuery</p>
 * <p>Description: Pesquisa os contratos que podem ser colocados em estoque para
 * que um contrato de serviço compulsório seja incluido no sistema. A ordenação
 * é feita pela prioridade dos serviços e pela data de inclusão dos contratos.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratosCompulsoriosQuery extends HQuery{

    public boolean somatorioValor = false;
    public String svcCodigo;
    public String rseCodigo;
    public String svcPrioridade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        svcPrioridade = (TextHelper.isNull(svcPrioridade)) ? "0" : svcPrioridade;

        final List<String> sadCodigoCompulsorio = new ArrayList<>();

        if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_ESTOQUE_NAO_CONTABILIZAM_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_SEM_ESTOQUE);
        } else {
            sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_COM_ESTOQUE);
        }

        String corpo = "";

        if (somatorioValor) {
        	// Recupera a soma dos contratos que podem ser colocados em estoque mais o saldo restante de contratos já em estoque
            corpo = "SELECT SUM(CASE WHEN rad.adeCodigoDestino IS NULL THEN ade.adeVlr ELSE 0.00 END) + COALESCE((" +
            		" SELECT SUM(adeDestino.adeVlr) " +
            		" FROM AutDesconto adeDestino " +
            		" WHERE adeDestino.registroServidor.rseCodigo = :rseCodigo " +
            		" AND EXISTS (" +
            		"   SELECT radIn.adeCodigoDestino FROM adeDestino.relacionamentoAutorizacaoByAdeCodigoDestinoSet radIn " +
            		"   WHERE radIn.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_COMPULSORIOS + "') " +
            		" ),0.00) - COALESCE(( " +
            		" SELECT SUM(CASE WHEN hmr.hmrMargemAntes > 0 THEN (adeOrigem.adeVlr - hmr.hmrMargemAntes) ELSE adeOrigem.adeVlr END) " +
            		" FROM AutDesconto adeOrigem " +
                    " INNER JOIN adeOrigem.verbaConvenio vco " +
                    " INNER JOIN vco.convenio cnv " +
                    " INNER JOIN cnv.servico svc " +
                    " INNER JOIN svc.paramSvcConsignanteSet pse94 " +
                    " WITH pse94.tipoParamSvc.tpsCodigo = '" + CodedValues.TPS_SERVICO_COMPULSORIO + "' " +
                    " INNER JOIN adeOrigem.ocorrenciaAutorizacaoSet oca " +
                    " WITH oca.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_TARIF_RESERVA + "'" +
                    " INNER JOIN oca.historicoMargemRseSet hmr " +
                    " WITH hmr.margem.marCodigo = adeOrigem.adeIncMargem " +
                    " WHERE adeOrigem.registroServidor.rseCodigo = :rseCodigo " +
                    " AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('" + TextHelper.join(sadCodigoCompulsorio, "','") + "') " +
                    " AND pse94.pseVlr = '1' " +
                    " AND EXISTS (" +
                    "   SELECT radIn.adeCodigoOrigem FROM adeOrigem.relacionamentoAutorizacaoByAdeCodigoOrigemSet radIn " +
                    "   WHERE radIn.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_COMPULSORIOS + "') " +
            		"),0.00) AS VLR ";
        } else {
            corpo = "SELECT DISTINCT " +
            "ade.adeCodigo, " +
            "ade.adeNumero, " +
            "ade.adeIdentificador, " +
            "ade.adeVlr, " +
            "ade.adePrazo, " +
            "ade.adePrdPagas, " +
            "ade.adeData, " +
            "ade.adeTipoVlr, " +
            "ade.adeIndice, " +
            "sad.sadDescricao, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao, " +
            "cnv.cnvCodVerba, " +
            "csa.csaIdentificador, " +
            "csa.csaNomeAbrev, " +
            "csa.csaNome, " +
            "rad.adeCodigoDestino, " +
    		" (COALESCE((SELECT SUM(adeDestino.adeVlr) " +
    		" FROM AutDesconto adeDestino " +
    		" WHERE adeDestino.registroServidor.rseCodigo = :rseCodigo " +
    		" AND EXISTS (SELECT radIn.adeCodigoDestino FROM adeDestino.relacionamentoAutorizacaoByAdeCodigoDestinoSet radIn WHERE radIn.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_COMPULSORIOS + "') " +
    		"),0.00) - COALESCE(( " +
    		" SELECT SUM(CASE WHEN hmr.hmrMargemAntes > 0 THEN (adeOrigem.adeVlr - hmr.hmrMargemAntes) ELSE adeOrigem.adeVlr END) " +
    		" FROM AutDesconto adeOrigem " +
            " INNER JOIN adeOrigem.verbaConvenio vco " +
            " INNER JOIN vco.convenio cnv " +
            " INNER JOIN cnv.servico svc " +
            " INNER JOIN svc.paramSvcConsignanteSet pse94 " +
            " WITH pse94.tipoParamSvc.tpsCodigo = '" + CodedValues.TPS_SERVICO_COMPULSORIO + "' " +
            " INNER JOIN adeOrigem.ocorrenciaAutorizacaoSet oca " +
            " WITH oca.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_TARIF_RESERVA + "'" +
            " INNER JOIN oca.historicoMargemRseSet hmr " +
            " WITH hmr.margem.marCodigo = adeOrigem.adeIncMargem " +
            " WHERE adeOrigem.registroServidor.rseCodigo = :rseCodigo " +
            " AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('" + CodedValues.SAD_DEFERIDA + "','" + CodedValues.SAD_EMANDAMENTO + "') " +
            " AND pse94.pseVlr = '1' " +
            " AND EXISTS (SELECT radIn.adeCodigoOrigem FROM adeOrigem.relacionamentoAutorizacaoByAdeCodigoOrigemSet radIn WHERE radIn.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_COMPULSORIOS + "') " +
    		"),0.00)) AS SALDO_ESTOQUE, " +
            " (SELECT adeSaldo.adeCodigo FROM AutDesconto adeSaldo " +
    		" INNER JOIN adeSaldo.verbaConvenio vcoSaldo " +
    		" INNER JOIN vcoSaldo.convenio cnvSaldo " +
    		" INNER JOIN cnvSaldo.servico svcSaldo " +
    		" WHERE adeSaldo.registroServidor.rseCodigo = :rseCodigo " +
    		" AND concatenar(concatenar(format_for_comparision(to_numeric(COALESCE(svcSaldo.svcPrioridade, '99999')) + 0, 5), adeSaldo.adeData), format_for_comparision(adeSaldo.adeNumero,20)) = (" +
            " SELECT MIN(concatenar(concatenar(format_for_comparision(to_numeric(COALESCE(svc2.svcPrioridade, '99999')) + 0, 5), adeDestino2.adeData), format_for_comparision(adeDestino2.adeNumero, 20))) as adeOrdem " +
            " FROM AutDesconto adeDestino2 " +
            " INNER JOIN adeDestino2.relacionamentoAutorizacaoByAdeCodigoDestinoSet radComp2 " +
            " WITH radComp2.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_COMPULSORIOS + "' " +
            " INNER JOIN radComp2.autDescontoByAdeCodigoOrigem adeOrigem2 " +
    		" INNER JOIN adeDestino2.verbaConvenio vco2 " +
    		" INNER JOIN vco2.convenio cnv2 " +
    		" INNER JOIN cnv2.servico svc2 " +
    		" WHERE adeDestino2.registroServidor.rseCodigo = :rseCodigo " +
    		" AND adeDestino2.statusAutorizacaoDesconto.sadCodigo IN ('" + TextHelper.join(sadCodigoCompulsorio, "','") + "') " +
    		" AND adeOrigem2.statusAutorizacaoDesconto.sadCodigo IN ('" + CodedValues.SAD_DEFERIDA + "','" + CodedValues.SAD_EMANDAMENTO + "') " +
    		")) AS ADE_SALDO_ESTOQUE, " +
            "to_numeric(COALESCE(svc.svcPrioridade, '99999')) + 0 AS PRIORIDADE ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM  AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" LEFT OUTER JOIN ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad ");
        corpoBuilder.append(" WITH rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPULSORIOS).append("' ");

        if (!somatorioValor) {
            corpoBuilder.append(" INNER JOIN cnv.consignataria csa ");
            corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        }

        // Parâmetro de serviço que diz se o serviço é compulsório
        corpoBuilder.append(" LEFT OUTER JOIN svc.paramSvcConsignanteSet pse94 ");
        corpoBuilder.append(" WITH pse94.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_SERVICO_COMPULSORIO).append("' ");

        // Parâmetro de serviço que diz se os contratos do serviço podem cair para entrar um compulsório
        corpoBuilder.append(" LEFT OUTER JOIN svc.paramSvcConsignanteSet pse95 ");
        corpoBuilder.append(" WITH pse95.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO).append("' ");

        // Onde o serviço não é compulsório ...
        corpoBuilder.append(" WHERE (pse94.pseVlr IS NULL OR pse94.pseVlr = '0' OR ");
        // ou é um compulsório que pode dar lugar a um compulsório de maior prioridade
        corpoBuilder.append(" (pse94.pseVlr = '1' AND pse95.pseVlr = '1' AND (svc.svcPrioridade IS NULL OR svc.svcPrioridade > :svcPrioridade))) ");

        corpoBuilder.append(" AND (svc.svcCodigo <> :svcCodigo) ");
        corpoBuilder.append(" AND (ade.registroServidor.rseCodigo = :rseCodigo) ");
        corpoBuilder.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigoCompulsorio, "','")).append("'))");

        if (!somatorioValor) {
            corpoBuilder.append(" ORDER BY (to_numeric(COALESCE(svc.svcPrioridade, '99999')) + 0) DESC, ade.adeData DESC, ade.adeNumero DESC");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("svcPrioridade", svcPrioridade, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        if (somatorioValor) {
            return new String[] {"VLR"};
        } else {
            return new String[] {
                    Columns.ADE_CODIGO,
                    Columns.ADE_NUMERO,
                    Columns.ADE_IDENTIFICADOR,
                    Columns.ADE_VLR,
                    Columns.ADE_PRAZO,
                    Columns.ADE_PRD_PAGAS,
                    Columns.ADE_DATA,
                    Columns.ADE_TIPO_VLR,
                    Columns.ADE_INDICE,
                    Columns.SAD_DESCRICAO,
                    Columns.SVC_IDENTIFICADOR,
                    Columns.SVC_DESCRICAO,
                    Columns.CNV_COD_VERBA,
                    Columns.CSA_IDENTIFICADOR,
                    Columns.CSA_NOME_ABREV,
                    Columns.CSA_NOME,
                    Columns.RAD_ADE_CODIGO_DESTINO,
                    "SALDO_ESTOQUE",
                    "ADE_SALDO_ESTOQUE",
                    "PRIORIDADE"
            };
        }
    }

}
