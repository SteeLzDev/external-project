package com.zetra.econsig.persistence.query.margem;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemRegistroServidoresQuery</p>
 * <p>Description: Listagem de margens do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemRegistroServidoresQuery extends HNativeQuery {

    public List<String> rseCodigo;
    public boolean margensComSvcAtivo;

    @Override
    public void setCriterios(TransferObject criterio) {
        List<String> rseCodigos = new ArrayList<>();
        if (!TextHelper.isNull(criterio.getAttribute(Columns.RSE_CODIGO))) {
            rseCodigos.add((String) criterio.getAttribute(Columns.RSE_CODIGO));
        }
        rseCodigo = rseCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT "
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
                     + Columns.MRS_MEDIA_MARGEM
                   ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM tb_margem");
        corpoBuilder.append(" LEFT OUTER JOIN tb_margem_registro_servidor ON (tb_margem_registro_servidor.mar_codigo = tb_margem.mar_codigo");
        corpoBuilder.append(" AND tb_margem_registro_servidor.rse_codigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(")");

        corpoBuilder.append(" WHERE tb_margem.mar_codigo <> 0");

        if (margensComSvcAtivo) {
        	corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_param_svc_consignante INNER JOIN tb_servico ON (tb_param_svc_consignante.svc_codigo = tb_servico.svc_codigo) ");
        	corpoBuilder.append("WHERE tb_param_svc_consignante.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND ");
        	corpoBuilder.append("tb_param_svc_consignante.pse_vlr = tb_margem.mar_codigo AND tb_servico.svc_ativo = ").append(CodedValues.STS_ATIVO).append(")");
        }

        corpoBuilder.append(" ORDER BY coalesce(tb_margem.mar_sequencia, 0), tb_margem.mar_codigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

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
                Columns.MRS_MEDIA_MARGEM
        };
    }
}
