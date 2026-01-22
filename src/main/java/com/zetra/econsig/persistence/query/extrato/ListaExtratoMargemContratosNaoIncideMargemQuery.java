package com.zetra.econsig.persistence.query.extrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemContratosNaoIncideMargemQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 9) ADICIONA CONTRATOS QUE NAO INCIDEM NA MARGEM
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemContratosNaoIncideMargemQuery extends HQuery {

    private final String rseCodigo;

    public ListaExtratoMargemContratosNaoIncideMargemQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'0' as TIPO, ");
        sql.append("(case when ade.adeTipoVlr = 'P' then coalesce(ade.adeVlrFolha, ade.adeVlr) else ade.adeVlr end), ");
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
        sql.append("svc.svcDescricao, ");
        sql.append("cnv.cnvCodVerba, ");
        sql.append("rse.rseCodigo ");

        sql.append(" FROM AutDesconto ade ");
        sql.append(" INNER JOIN ade.verbaConvenio vco ");
        sql.append(" INNER JOIN vco.convenio cnv ");
        sql.append(" INNER JOIN cnv.consignataria csa ");
        sql.append(" INNER JOIN cnv.servico svc ");
        sql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        sql.append(" INNER JOIN ade.registroServidor rse ");

        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND coalesce(ade.adeIncMargem, 0) = 0");
        sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('");
        sql.append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("'))");

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
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
                             Columns.SVC_DESCRICAO,
                             Columns.CNV_COD_VERBA,
                             Columns.RSE_CODIGO
                     };
    }
}
