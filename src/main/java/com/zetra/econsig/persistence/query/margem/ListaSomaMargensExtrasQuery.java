package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSomaMargensExtras</p>
 * <p>Description: Listagem da soma das margens da tb_margem</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSomaMargensExtrasQuery extends HNativeQuery {

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT "
                     + Columns.MAR_CODIGO + ','
                     + Columns.MAR_DESCRICAO + ','
                     + " sum("+Columns.MRS_MARGEM_REST+") AS VALOR";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM tb_margem");
        corpoBuilder.append(" INNER JOIN tb_margem_registro_servidor ON (tb_margem_registro_servidor.mar_codigo = tb_margem.mar_codigo)");

        corpoBuilder.append(" WHERE tb_margem.mar_codigo NOT IN (0,1,2,3)");
        corpoBuilder.append(" AND tb_margem.mar_codigo NOT IN (SELECT coalesce(mar_codigo_pai, 0) FROM tb_margem)");

        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_convenio");
        corpoBuilder.append(" INNER JOIN tb_servico ON (tb_convenio.svc_codigo = tb_servico.svc_codigo)");
        corpoBuilder.append(" INNER JOIN tb_param_svc_consignante ON (tb_servico.svc_codigo = tb_param_svc_consignante.svc_codigo)");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor ON (tb_convenio.org_codigo = tb_registro_servidor.org_codigo)");

        if(responsavel.isCsaCor()) {
            corpoBuilder.append(" INNER JOIN tb_consignataria ON (tb_convenio.csa_codigo = tb_consignataria.csa_codigo)");
        }

        if(responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            corpoBuilder.append(" INNER JOIN tb_correspondente_convenio on (tb_correspondente_convenio.cnv_codigo = tb_convenio.cnv_codigo)");
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                corpoBuilder.append(" INNER JOIN tb_orgao on (tb_orgao.org_codigo = tb_orgao.org_CODIGO)");
                corpoBuilder.append(" INNER JOIN tb_estabelecimento on (tb_estabelecimento.est_codigo = tb_orgao.EST_CODIGO)");
            } else {
                corpoBuilder.append(" AND tb_registro_servidor.org_codigo = :codigoEntidade");
            }
        }

        corpoBuilder.append(" WHERE tb_convenio.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" AND tb_registro_servidor.SRS_CODIGO").append(criaClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS));
        corpoBuilder.append(" AND tb_servico.svc_ativo = ").append(CodedValues.SCV_ATIVO);

        if(responsavel.isCsa()) {
            corpoBuilder.append(" AND tb_consignataria.csa_codigo = :codigoEntidade");
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                corpoBuilder.append(" AND tb_estabelecimento.est_codigo = :codigoEntidade");
            } else {
                corpoBuilder.append(" AND tb_registro_servidor.org_codigo = :codigoEntidade");
            }
        }

        if(responsavel.isCor()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corpoBuilder.append(" AND tb_consignataria.csa_codigo = :codigoEntidade");
            } else {
                corpoBuilder.append(" AND tb_consignataria.csa_codigo = :codigoEntidadePai");
                corpoBuilder.append(" AND tb_correspondente_convenio.cor_codigo = :codigoEntidade");
                corpoBuilder.append(" AND tb_correspondente_convenio.scv_codigo = " + CodedValues.SCV_ATIVO);
            }
        }

        corpoBuilder.append(" AND tb_param_svc_consignante.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("'");
        corpoBuilder.append(" AND (tb_param_svc_consignante.pse_vlr = to_string(tb_margem.mar_codigo) OR tb_param_svc_consignante.pse_vlr = to_string(tb_margem.mar_codigo_pai))");
        corpoBuilder.append(") GROUP BY tb_margem.mar_codigo, tb_margem.mar_descricao;");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS, query);

        if(responsavel.isCsa()) {
            defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidadePai(), query);
            } else {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
            }
        }

        if(responsavel.isCor()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidadePai(), query);
            } else {
                defineValorClausulaNomeada("codigoEntidadePai", responsavel.getCodigoEntidadePai(), query);
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MAR_CODIGO,
                Columns.MAR_DESCRICAO,
                "VALOR"
        };
    }
}
