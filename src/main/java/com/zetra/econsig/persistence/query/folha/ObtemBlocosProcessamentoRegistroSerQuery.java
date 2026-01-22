package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemBlocosProcessamentoRegistroSerQuery</p>
 * <p>Description: Obtém os blocos de processamento agrupados de um mesmo registro servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBlocosProcessamentoRegistroSerQuery extends HQuery  {
    private Integer bprCodigo;
    private String rseCodigo;
    private String rseMatricula;
    private String orgIdentificador;
    private String estIdentificador;

    public ObtemBlocosProcessamentoRegistroSerQuery(Integer bprCodigo) {
        super();
        this.bprCodigo = bprCodigo;
    }

    public ObtemBlocosProcessamentoRegistroSerQuery(String rseCodigo, String rseMatricula, String orgIdentificador, String estIdentificador) {
        super();
        this.rseCodigo = rseCodigo;
        this.rseMatricula = rseMatricula;
        this.orgIdentificador = orgIdentificador;
        this.estIdentificador = estIdentificador;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("  bpr.bprCodigo, ");
        corpoBuilder.append("  bpr.tipoBlocoProcessamento.tbpCodigo, ");
        corpoBuilder.append("  bpr.statusBlocoProcessamento.sbpCodigo, ");
        corpoBuilder.append("  bpr.convenio.cnvCodigo, ");
        corpoBuilder.append("  bpr.estabelecimento.estCodigo, ");
        corpoBuilder.append("  bpr.orgao.orgCodigo, ");
        corpoBuilder.append("  bpr.registroServidor.rseCodigo, ");
        corpoBuilder.append("  bpr.bprPeriodo, ");
        corpoBuilder.append("  bpr.bprDataInclusao, ");
        corpoBuilder.append("  bpr.bprDataProcessamento, ");
        corpoBuilder.append("  bpr.bprOrdemExecucao, ");
        corpoBuilder.append("  bpr.bprMensagem, ");
        corpoBuilder.append("  bpr.bprLinha, ");
        corpoBuilder.append("  bpr.bprNumLinha, ");
        corpoBuilder.append("  bpr.bprCampos, ");
        corpoBuilder.append("  bpr.cnvCodVerba, ");
        corpoBuilder.append("  bpr.svcIdentificador, ");
        corpoBuilder.append("  bpr.csaIdentificador, ");
        corpoBuilder.append("  bpr.estIdentificador, ");
        corpoBuilder.append("  bpr.orgIdentificador, ");
        corpoBuilder.append("  bpr.rseMatricula, ");
        corpoBuilder.append("  bpr.serCpf, ");
        corpoBuilder.append("  bpr.adeNumero, ");
        corpoBuilder.append("  bpr.adeIndice ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");

        if (bprCodigo != null) {
            corpoBuilder.append("WHERE EXISTS (");
            corpoBuilder.append("  SELECT 1 ");
            corpoBuilder.append("  FROM BlocoProcessamento bpr2 ");
            corpoBuilder.append("  WHERE bpr2.bprCodigo = :bprCodigo ");
            corpoBuilder.append("    AND (bpr.registroServidor.rseCodigo = bpr2.registroServidor.rseCodigo ");
            corpoBuilder.append("     OR (bpr.rseMatricula = bpr2.rseMatricula AND COALESCE(bpr.orgIdentificador, '') = COALESCE(bpr2.orgIdentificador, '') AND COALESCE(bpr.estIdentificador, '') = COALESCE(bpr2.estIdentificador, '')) ");
            corpoBuilder.append("    ) ");
            corpoBuilder.append(")");

        } else if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("WHERE bpr.registroServidor.rseCodigo = :rseCodigo");

        } else {
            corpoBuilder.append("WHERE bpr.rseMatricula = :rseMatricula");
            corpoBuilder.append(" AND COALESCE(bpr.orgIdentificador, '') = :orgIdentificador");
            corpoBuilder.append(" AND COALESCE(bpr.estIdentificador, '') = :estIdentificador");
        }

        corpoBuilder.append(" ORDER BY bpr.bprOrdemExecucao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (bprCodigo != null) {
            defineValorClausulaNomeada("bprCodigo", bprCodigo, query);
        } else if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        } else {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador == null ? "" : orgIdentificador, query);
            defineValorClausulaNomeada("estIdentificador", estIdentificador == null ? "" : estIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BPR_CODIGO,
                Columns.TBP_CODIGO,
                Columns.SBP_CODIGO,
                Columns.CNV_CODIGO,
                Columns.EST_CODIGO,
                Columns.ORG_CODIGO,
                Columns.RSE_CODIGO,
                Columns.BPR_PERIODO,
                Columns.BPR_DATA_INCLUSAO,
                Columns.BPR_DATA_PROCESSAMENTO,
                Columns.BPR_ORDEM_EXECUCAO,
                Columns.BPR_MENSAGEM,
                Columns.BPR_LINHA,
                Columns.BPR_NUM_LINHA,
                Columns.BPR_CAMPOS,
                Columns.BPR_CNV_COD_VERBA,
                Columns.SVC_IDENTIFICADOR,
                Columns.CSA_IDENTIFICADOR,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_IDENTIFICADOR,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.ADE_NUMERO,
                Columns.ADE_INDICE,
        };
    }
}
