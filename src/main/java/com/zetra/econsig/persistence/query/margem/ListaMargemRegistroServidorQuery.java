package com.zetra.econsig.persistence.query.margem;

import java.util.List;

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
 * <p>Title: ListaMargemRegistroServidorQuery</p>
 * <p>Description: Listagem de margens do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemRegistroServidorQuery extends HNativeQuery {

    public String csaCodigo;
    public String orgCodigo;
    public String rseCodigo;
    public String svcCodigo;
    public String nseCodigo;
    public Short marCodigo;
    public List<Short> marCodigosPai;
    public boolean temConvenioAtivo = true;
    public boolean alteracaoMultiplaAde = false;

    public ListaMargemRegistroServidorQuery() {
        this(true);
    }

    public ListaMargemRegistroServidorQuery(boolean temConvenioAtivo) {
        addFieldType(Columns.MAR_CODIGO, StandardBasicTypes.SHORT);
        this.temConvenioAtivo = temConvenioAtivo;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
        rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
        svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
        nseCodigo = (String) criterio.getAttribute(Columns.NSE_CODIGO);
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
                     + Columns.MAR_TIPO_VLR  + ","
                     + Columns.MRS_MARGEM_REST + ","
                     + Columns.MRS_MARGEM + ","
                     + Columns.MRS_MARGEM_USADA + ","
                     + Columns.MRS_MEDIA_MARGEM + ","
                     + Columns.MAR_COD_ADEQUACAO + ","
                     + Columns.MAR_PORCENTAGEM
                   ;

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM tb_margem");
        corpoBuilder.append(" LEFT OUTER JOIN tb_margem_registro_servidor ON (tb_margem_registro_servidor.mar_codigo = tb_margem.mar_codigo");
        corpoBuilder.append(" AND tb_margem_registro_servidor.rse_codigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(")");

        corpoBuilder.append(" WHERE tb_margem.mar_codigo <> 0");

        // DESENV-15898 : Retirei a cláusula para não exibir margem pai para poder relacionar margens
        // que possam ser exibidas mesmo sem incidir em algum serviço, vide caso do STJ. Se não quiser
        // exibir uma margem pai, basta desabilitar sua visualização nas configurações de margem
        // corpoBuilder.append(" AND tb_margem.mar_codigo NOT IN (SELECT coalesce(mar_codigo_pai, 0) FROM tb_margem)");

        if (temConvenioAtivo) {
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_convenio");
            corpoBuilder.append(" INNER JOIN tb_servico ON (tb_convenio.svc_codigo = tb_servico.svc_codigo)");
            corpoBuilder.append(" LEFT OUTER JOIN tb_param_svc_consignante ON (tb_servico.svc_codigo = tb_param_svc_consignante.svc_codigo AND tb_param_svc_consignante.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");

            if (!TextHelper.isNull(rseCodigo)) {
                corpoBuilder.append(" INNER JOIN tb_registro_servidor ON (tb_convenio.org_codigo = tb_registro_servidor.org_codigo)");
            }

            corpoBuilder.append(" WHERE tb_convenio.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" AND tb_servico.svc_ativo = ").append(CodedValues.SCV_ATIVO);
            if (!alteracaoMultiplaAde) {
                corpoBuilder.append(" AND (coalesce(tb_param_svc_consignante.pse_vlr, '").append(CodedValues.INCIDE_MARGEM_SIM).append("') = to_string(tb_margem.mar_codigo)");
                corpoBuilder.append(" OR coalesce(tb_param_svc_consignante.pse_vlr, '").append(CodedValues.INCIDE_MARGEM_SIM).append("') = to_string(tb_margem.mar_codigo_pai))");
            }

            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND tb_convenio.csa_codigo").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" AND tb_convenio.org_codigo").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            }
            if (!TextHelper.isNull(rseCodigo)) {
                corpoBuilder.append(" AND tb_registro_servidor.rse_codigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            }
            if (!TextHelper.isNull(svcCodigo)) {
                corpoBuilder.append(" AND tb_convenio.svc_codigo").append(criaClausulaNomeada("svcCodigo", svcCodigo));
            }
            if (!TextHelper.isNull(nseCodigo)) {
                corpoBuilder.append(" AND tb_servico.nse_codigo").append(criaClausulaNomeada("nseCodigo", nseCodigo));
            }
            corpoBuilder.append(")");
        }

        if (!TextHelper.isNull(marCodigo)) {
            corpoBuilder.append(" AND tb_margem.mar_codigo ").append(criaClausulaNomeada("marCodigo", marCodigo));
        } else if ((marCodigosPai != null) && !marCodigosPai.isEmpty()) {
            corpoBuilder.append(" AND tb_margem.mar_codigo not in (:marCodigosPai)");
            corpoBuilder.append(" AND tb_margem.mar_codigo_pai in (:marCodigosPai)");
        }

        if (alteracaoMultiplaAde) {
            corpoBuilder.append(" AND coalesce(tb_margem.mar_exibe_alt_mult_contratos, 'N') = 'S'");
        }

        corpoBuilder.append(" ORDER BY coalesce(tb_margem.mar_sequencia, 0), tb_margem.mar_codigo");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo) && temConvenioAtivo) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo) && temConvenioAtivo) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo) && temConvenioAtivo) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(nseCodigo) && temConvenioAtivo) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(marCodigo)) {
            defineValorClausulaNomeada("marCodigo", marCodigo, query);
        } else if ((marCodigosPai != null) && !marCodigosPai.isEmpty()) {
            defineValorClausulaNomeada("marCodigosPai", marCodigosPai, query);
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
                Columns.MAR_TIPO_VLR,
                Columns.MRS_MARGEM_REST,
                Columns.MRS_MARGEM,
                Columns.MRS_MARGEM_USADA,
                Columns.MRS_MEDIA_MARGEM,
                Columns.MAR_COD_ADEQUACAO,
                Columns.MAR_PORCENTAGEM
        };
    }
}
