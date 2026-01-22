package com.zetra.econsig.persistence.query.extrato;

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
 * <p>Title: ListaExtratoMargemContratosAbertosQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 1) SOMA OS CONTRATOS ABERTOS DO SERVIDOR, DE ACORDO COM O PARAMETRO DE CONTROLE DE MARGEM
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemContratosAbertosQuery extends HQuery {

    private final String rseCodigo;

    public ListaExtratoMargemContratosAbertosQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'1' as TIPO, ");
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append("CASE WHEN ocaPrende.tocCodigo IS NULL OR ocaLibera.tocCodigo IS NOT NULL THEN ade.adeVlr ELSE ocaPrende.ocaAdeVlrAnt END, ");
        } else {
            sql.append("ade.adeVlr, ");
        }
        sql.append("ade.adeCodigo, ");
        sql.append("ade.adeData, ");
        sql.append("ade.adeNumero, ");
        sql.append("ade.adeVlr, ");
        sql.append("ade.adeVlrFolha, ");
        sql.append("ade.adeTipoVlr, ");
        sql.append("coalesce(ade.adeIncMargem, 1), ");
        sql.append("sad.sadDescricao, ");
        sql.append("csa.csaIdentificador, ");
        sql.append("csa.csaNome, ");
        sql.append("csa.csaNomeAbrev, ");
        sql.append("ade.adeUltPeriodoExportacao, ");
        sql.append("ade.adeAnoMesIni ");

        if(ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(", svc.svcDescricao, ");
            sql.append("cnv.cnvCodVerba ");
        }

        sql.append(" FROM AutDesconto ade ");
        sql.append(" INNER JOIN ade.verbaConvenio vco ");
        sql.append(" INNER JOIN vco.convenio cnv ");
        sql.append(" INNER JOIN cnv.servico svc ");
        sql.append(" INNER JOIN cnv.consignataria csa ");
        sql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" LEFT JOIN ade.ocorrenciaAutorizacaoSet ocaPrende WITH ocaPrende.tocCodigo = '").append(CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO).append("' ");
            sql.append(" LEFT JOIN ade.ocorrenciaAutorizacaoSet ocaLibera WITH ocaLibera.tocCodigo = '").append(CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO).append("' ");
        }
        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND (coalesce(ade.adeIncMargem, 1) <> 0)");

        if (ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Folha envia margem líquida: adiciona contratos que não estão na margem
            final List<String> sadCodigos = new ArrayList<>(CodedValues.SAD_CODIGOS_INATIVOS);

            // nem pode estar sendo pagos
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_SUSPENSOS);
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);

            sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('");
            sql.append(TextHelper.join(sadCodigos, "','")).append("')");

            // ou os que podem estar sendo pagos, mas não estão
            sadCodigos.clear();
            if (!ParamSist.paramEquals(CodedValues.TPC_RETEM_MARGEM_ADE_EM_ANDAMENTO_NAO_PAGO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
                sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            }
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_SUSPENSOS);
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);

            sql.append(" OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('");
            sql.append(TextHelper.join(sadCodigos, "','")).append("')");
            sql.append(" AND coalesce(ade.adePaga, 'N') <> 'S' and ade.adeVlrFolha is null)");
            sql.append(")");

        } else {
            // Folha envia margem bruta: adiciona todos status não inativos
            sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('");
            sql.append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("'))");
        }

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV,
                                 Columns.ADE_ULT_PERIODO_EXPORTACAO,
                                 Columns.ADE_ANO_MES_INI
            };
        } else {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV,
                                 Columns.ADE_ULT_PERIODO_EXPORTACAO,
                                 Columns.ADE_ANO_MES_INI,
                                 Columns.SVC_DESCRICAO,
                                 Columns.CNV_COD_VERBA
            };
        }
    }
}
