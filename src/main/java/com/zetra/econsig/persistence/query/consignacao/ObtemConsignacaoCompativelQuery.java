package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConsignacaoCompativelQuery</p>
 * <p>Description: Recupera consignação compatível com a consignação a ser comparada.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignacaoCompativelQuery extends HNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ObtemConsignacaoCompativelQuery.class);

    public String adeCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        adeCodigo = (String) criterio.getAttribute(Columns.ADE_CODIGO);
        // TODO Incluir parâmetro para passar incidências de margem
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(adeCodigo)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.informe.ade.codigo.autorizacao", (AcessoSistema) null));
            throw new HQueryException("mensagem.erro.informe.ade.codigo.autorizacao", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT rse.RSE_CODIGO, ade2.ADE_CODIGO, ade2.ADE_NUMERO, ade2.ADE_VLR ");
        corpoBuilder.append("FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" vco ON (ade.VCO_CODIGO = vco.vco_codigo) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnv ON (vco.CNV_CODIGO = cnv.CNV_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_SERVICO).append(" svc ON (svc.SVC_CODIGO = cnv.SVC_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rse ON (rse.RSE_CODIGO = ade.RSE_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnv2 ON (cnv.CNV_CODIGO <> cnv2.CNV_CODIGO ");
        corpoBuilder.append("AND cnv.SVC_CODIGO <> cnv2.SVC_CODIGO AND cnv.ORG_CODIGO = cnv2.ORG_CODIGO AND cnv.CSA_CODIGO = cnv2.CSA_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" vco2 ON (cnv2.CNV_CODIGO = vco2.CNV_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_SERVICO).append(" svc2 ON (svc2.SVC_CODIGO = cnv2.SVC_CODIGO AND svc2.NSE_CODIGO <> svc.NSE_CODIGO) ");
        corpoBuilder.append("INNER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade2 ON (ade2.VCO_CODIGO = vco2.VCO_CODIGO ");
        corpoBuilder.append("AND ade2.RSE_CODIGO = ade.RSE_CODIGO ");
        corpoBuilder.append("AND ade2.SAD_CODIGO").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS));

        List<String> incideMargem = new ArrayList<>();
        incideMargem.add(CodedValues.NOT_EQUAL_KEY);
        incideMargem.add(CodedValues.INCIDE_MARGEM_NAO.toString());
        corpoBuilder.append(" AND ade2.ADE_INC_MARGEM").append(criaClausulaNomeada("incideMargem", incideMargem)).append(") ");

        corpoBuilder.append("WHERE 1 = 1 ");

        corpoBuilder.append("AND ade.ade_codigo").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);
        defineValorClausulaNomeada("incideMargem", incideMargem, query);
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR
        };
    }

}
