package com.zetra.econsig.persistence.query.sdp.plano;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;

/**
 * <p>Title: ListaPlanosDescontoTaxaUsoQuery</p>
 * <p>Description: Lista Planos de Desconto de Taxa de Uso com desconto por posto.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPlanosDescontoTaxaUsoQuery extends HNativeQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String rseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        Short plaAtivo = CodedValues.STS_ATIVO;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append(Columns.PLA_CODIGO).append(", ");
        corpoBuilder.append(Columns.CNV_CODIGO).append(", ");
        corpoBuilder.append(Columns.PLA_SVC_CODIGO).append(", ");
        corpoBuilder.append(Columns.PLA_CSA_CODIGO).append(", ");
        corpoBuilder.append(Columns.PLA_NPL_CODIGO).append(", ");
        corpoBuilder.append(Columns.PLA_ATIVO).append(" ");
        corpoBuilder.append("FROM ").append(Columns.TB_PLANO).append(" ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (").append(Columns.PLA_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO).append(") ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.CNV_CSA_CODIGO).append(" ");
        corpoBuilder.append("AND ").append(Columns.PLA_SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(" ");
        corpoBuilder.append("AND ").append(Columns.CNV_SCV_CODIGO).append(" = '").append(CodedValues.SCV_ATIVO).append("') ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" ON (").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.CNV_ORG_CODIGO).append(") ");
        corpoBuilder.append("WHERE 1 = 1 ");
        corpoBuilder.append("AND ").append(Columns.PLA_NPL_CODIGO).append(" = '").append(NaturezaPlanoEnum.TAXA_USO.getCodigo()).append("' ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.RSE_CODIGO).append(" ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(" ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(" ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" AND ").append(Columns.PLA_ATIVO).append(" ").append(criaClausulaNomeada("plaAtivo", plaAtivo));

        // TODO Incluir ordenacao?

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        defineValorClausulaNomeada("plaAtivo", plaAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PLA_CODIGO,
                Columns.CNV_CODIGO,
                Columns.SVC_CODIGO,
                Columns.CSA_CODIGO,
                Columns.NPL_CODIGO,
                Columns.PLA_ATIVO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
