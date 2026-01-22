package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaMargensIncideEmprestimoQuery extends HNativeQuery {

    public String rseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("  mar.mar_codigo, ");
        corpoBuilder.append("  mar.mar_descricao, ");
        corpoBuilder.append("  coalesce(mar.mar_porcentagem, 0), ");
        
        corpoBuilder.append("  coalesce(case when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM).append(" then rse.rse_margem ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" then rse.rse_margem_2 ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" then rse.rse_margem_3 ");
        corpoBuilder.append("       else mrs.mrs_margem ");
        corpoBuilder.append("  end, 0) as margem_folha, ");
        
        corpoBuilder.append("  coalesce(case when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM).append(" then rse.rse_margem_rest ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" then rse.rse_margem_rest_2 ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" then rse.rse_margem_rest_3 ");
        corpoBuilder.append("       else mrs.mrs_margem_rest ");
        corpoBuilder.append("  end, 0) as margem_rest, ");
        
        corpoBuilder.append("  coalesce(case when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM).append(" then rse.rse_margem_usada ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" then rse.rse_margem_usada_2 ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" then rse.rse_margem_usada_3 ");
        corpoBuilder.append("       else mrs.mrs_margem_usada ");
        corpoBuilder.append("  end, 0) as margem_usada, ");
        
        corpoBuilder.append("  coalesce(case when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM).append(" then rse.rse_media_margem ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" then rse.rse_media_margem_2 ");
        corpoBuilder.append("       when mar.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" then rse.rse_media_margem_3 ");
        corpoBuilder.append("       else mrs.mrs_media_margem ");
        corpoBuilder.append("  end, 0) as media_margem ");
        
        corpoBuilder.append("from tb_margem mar ");
        corpoBuilder.append("cross join tb_registro_servidor rse ");
        corpoBuilder.append("left outer join tb_margem_registro_servidor mrs on (rse.rse_codigo = mrs.rse_codigo and mar.mar_codigo = mrs.mar_codigo) ");
        corpoBuilder.append("where rse.rse_codigo = :rseCodigo ");
        corpoBuilder.append("  and mar.mar_codigo <> ").append(CodedValues.INCIDE_MARGEM_NAO).append(" ");
        corpoBuilder.append("  and exists ( ");
        corpoBuilder.append("    select 1 from tb_servico svc ");
        corpoBuilder.append("    left outer join tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("') ");
        corpoBuilder.append("    where svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        corpoBuilder.append("      and coalesce(pse.pse_vlr, '").append(CodedValues.INCIDE_MARGEM_SIM).append("') = cast(mar.mar_codigo as char) ");
        corpoBuilder.append("  ) ");
        corpoBuilder.append("order by 3 desc, 4 desc ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        query.setMaxResults(1);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
            Columns.MAR_CODIGO,
            Columns.MAR_DESCRICAO,
            Columns.MAR_PORCENTAGEM,
            Columns.MRS_MARGEM,
            Columns.MRS_MARGEM_REST,
            Columns.MRS_MARGEM_USADA,
            Columns.MRS_MEDIA_MARGEM
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        throw new UnsupportedOperationException("Unimplemented method 'setCriterios'");
    }
}

