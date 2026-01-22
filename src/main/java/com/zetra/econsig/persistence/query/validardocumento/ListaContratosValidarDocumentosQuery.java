package com.zetra.econsig.persistence.query.validardocumento;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaContratosValidarDocumentosQuery</p>
 * <p>Description: Listagem de contratos para valida��o de documentos</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date
 */
public class ListaContratosValidarDocumentosQuery extends HQuery {

    protected AcessoSistema responsavel;
    public String ssoCodigo;
    public Date periodo;

    public ListaContratosValidarDocumentosQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT ade.adeCodigo, ");
        sql.append("soa.soaCodigo, ");
        sql.append("soa.ssoCodigo, ");
        sql.append("csa.csaNomeAbrev, ");
        sql.append("csa.csaIdentificador, ");
        sql.append("case when usu.statusLogin.stuCodigo <> '").append(CodedValues.STU_EXCLUIDO).append("' ");
        sql.append("then usu.usuLogin else coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin) end AS USU_LOGIN, ");
        sql.append("ade.adeNumero, ");
        sql.append("ade.adeIdentificador, ");
        sql.append("cnv.cnvCodVerba, ");
        sql.append("svc.svcDescricao, ");
        sql.append("ade.adeAnoMesIni, ");
        sql.append("ade.adeAnoMesFim, ");
        sql.append("ade.adeVlr, ");
        sql.append("ade.adeVlrLiquido, ");
        sql.append("case when ade.adePrazo is null then '0.00' else (ade.adeVlr*ade.adePrazo) end as VALOR_TOTAL, ");
        sql.append("ade.adePrazo, ");
        sql.append("ade.sadCodigo, ");
        sql.append("ser.serCpf, ");
        sql.append("(select count(*) from SolicitacaoAutorizacao soa2 where soa2.adeCodigo = ade.adeCodigo and soa2.soaDataResposta is not null and soa2.ssoCodigo = :solicitacaoRejeitada ) AS NUMERO_VALIDACOES, ");
        sql.append("oso.osoDescricao, ");
        sql.append("soa.soaObs, ");
        sql.append("ade.adeData, ");
        sql.append("ser.serNome, ");
        sql.append("org.orgNome, ");
        sql.append("COALESCE(soa.soaDataResposta,soa.soaData) as DATA_SOLICITACAO ");
        sql.append("FROM AutDesconto ade ");
        sql.append("INNER JOIN ade.registroServidor rse  ");
        sql.append("INNER JOIN rse.orgao org  ");
        sql.append("INNER JOIN rse.servidor ser  ");
        sql.append("INNER JOIN ade.verbaConvenio vco  ");
        sql.append("INNER JOIN vco.convenio cnv  ");
        sql.append("INNER JOIN cnv.consignataria csa ");
        sql.append("INNER JOIN cnv.servico svc  ");
        sql.append("INNER JOIN ade.usuario usu  ");
        sql.append("INNER JOIN ade.solicitacaoAutorizacaoSet soa  ");
        sql.append("INNER JOIN soa.origemSolicitacao oso  ");
        sql.append("WHERE soa.tisCodigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        sql.append("AND NOT EXISTS (SELECT 1 FROM SolicitacaoAutorizacao soa1 WHERE soa1.soaData > soa.soaData AND ade.adeCodigo = soa1.adeCodigo AND soa1.tisCodigo ='").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("') ");
        sql.append("AND ade.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "' , '")).append("') ");

        if (responsavel.isCsaCor()) {
            sql.append("AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", responsavel.getCsaCodigo()));
        }

        if (ssoCodigo != null) {
            sql.append("AND soa.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
        }

        if (periodo != null) {
            sql.append("AND soa.soaPeriodo ").append(criaClausulaNomeada("periodo", periodo));
        }

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("solicitacaoRejeitada", StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), query);

        if (responsavel.isCsaCor()) {
            defineValorClausulaNomeada("csaCodigo", responsavel.getCsaCodigo(), query);
        }

        if (ssoCodigo != null) {
            defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        }

        if (periodo != null) {
            defineValorClausulaNomeada("periodo", periodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.ADE_CODIGO,
                              Columns.SOA_CODIGO,
                              Columns.SSO_CODIGO,
                              Columns.CSA_NOME_ABREV,
                              Columns.CSA_IDENTIFICADOR,
                              Columns.USU_LOGIN,
                              Columns.ADE_NUMERO,
                              Columns.ADE_IDENTIFICADOR,
                              Columns.CNV_COD_VERBA,
                              Columns.SVC_DESCRICAO,
                              Columns.ADE_ANO_MES_INI,
                              Columns.ADE_ANO_MES_FIM,
                              Columns.ADE_VLR,
                              Columns.ADE_VLR_LIQUIDO,
                              "VALOR_TOTAL",
                              Columns.ADE_PRAZO,
                              Columns.SAD_CODIGO,
                              Columns.SER_CPF,
                              "NUMERO_VALIDACOES",
                              Columns.OSO_DESCRICAO,
                              Columns.SOA_OBS,
                              Columns.ADE_DATA,
                              Columns.SER_NOME,
                              Columns.ORG_NOME,
                              "DATA_SOLICITACAO"
        };
    }
}
