package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemIncidenteQuery</p>
 * <p>Description: Listagem de margens incidentes.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemIncidenteQuery extends HNativeQuery {

    public String csaCodigo = null;
    public String orgCodigo = null;
    public String rseCodigo = null;
    public String svcCodigo = null;
    public String estCodigo = null;
    public Short marCodigo = null;

    public ListaMargemIncidenteQuery() {
        addFieldType(Columns.MAR_CODIGO, StandardBasicTypes.SHORT);
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
        rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
        svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);

        marCodigo = (Short) criterio.getAttribute(Columns.MAR_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT "
                     + Columns.MAR_CODIGO + ","
                     + Columns.MAR_CODIGO_PAI + ","
                     + Columns.MAR_DESCRICAO + ","
                     + Columns.MAR_SEQUENCIA + ","
                     + Columns.MAR_EXIBE_CSE + ","
                     + Columns.MAR_EXIBE_ORG + ","
                     + Columns.MAR_EXIBE_SER + ","
                     + Columns.MAR_EXIBE_CSA + ","
                     + Columns.MAR_EXIBE_COR + ","
                     + Columns.MAR_EXIBE_SUP + ","
                     + Columns.MAR_TIPO_VLR;

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM tb_margem");
        corpoBuilder.append(" WHERE tb_margem.mar_codigo <> 0");
        corpoBuilder.append(" AND tb_margem.mar_codigo NOT IN (SELECT coalesce(mar_codigo_pai, 0) FROM tb_margem)");
        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_convenio");
        corpoBuilder.append(" INNER JOIN tb_servico ON (tb_convenio.svc_codigo = tb_servico.svc_codigo)");
        corpoBuilder.append(" INNER JOIN tb_param_svc_consignante ON (tb_servico.svc_codigo = tb_param_svc_consignante.svc_codigo)");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" INNER JOIN tb_registro_servidor ON (tb_convenio.org_codigo = tb_registro_servidor.org_codigo)");
        }

        if (!TextHelper.isNull(estCodigo)){
            corpoBuilder.append(" INNER JOIN tb_orgao ON (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
        }

        corpoBuilder.append(" WHERE tb_convenio.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" AND tb_servico.svc_ativo = ").append(CodedValues.SCV_ATIVO);
        corpoBuilder.append(" AND tb_param_svc_consignante.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("'");
        corpoBuilder.append(" AND (tb_param_svc_consignante.pse_vlr = to_string(tb_margem.mar_codigo) OR tb_param_svc_consignante.pse_vlr = to_string(tb_margem.mar_codigo_pai))");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND tb_convenio.csa_codigo").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if(!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND tb_orgao.est_codigo").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND tb_convenio.org_codigo").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND tb_registro_servidor.rse_codigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND tb_convenio.svc_codigo").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        corpoBuilder.append(")");
        if (!TextHelper.isNull(marCodigo)) {
            corpoBuilder.append(" AND tb_margem.mar_codigo").append(criaClausulaNomeada("marCodigo", marCodigo));
        }

        corpoBuilder.append(" ORDER BY tb_margem.mar_codigo, tb_margem.mar_sequencia");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if(!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        } else if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(marCodigo)) {
            defineValorClausulaNomeada("marCodigo", marCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MAR_CODIGO,
                Columns.MAR_CODIGO_PAI,
                Columns.MAR_DESCRICAO,
                Columns.MAR_SEQUENCIA,
                Columns.MAR_EXIBE_CSE,
                Columns.MAR_EXIBE_ORG,
                Columns.MAR_EXIBE_SER,
                Columns.MAR_EXIBE_CSA,
                Columns.MAR_EXIBE_COR,
                Columns.MAR_EXIBE_SUP,
                Columns.MAR_TIPO_VLR
        };
    }
}
