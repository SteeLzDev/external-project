package com.zetra.econsig.persistence.query.prazo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: ListaPrazoCoeficienteEmprestimoQuery</p>
 * <p>Description: Seleciona os prazos que possuem coeficientes cadastrados de
 * qualquer serviço, para utilização na tela de simulação em sistemas com
 * simulador agrupado pela natureza do serviço.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPrazoCoeficienteEmprestimoQuery extends HNativeQuery {

    public String orgCodigo;
    public int dia;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT prz.prz_vlr");
        corpoBuilder.append(" FROM tb_prazo prz");
        corpoBuilder.append(" LEFT OUTER JOIN tb_param_svc_consignante pseMaxPrazo ON (prz.svc_codigo = pseMaxPrazo.svc_codigo");
        corpoBuilder.append(" AND pseMaxPrazo.tps_codigo = '").append(CodedValues.TPS_MAX_PRAZO).append("')");
        corpoBuilder.append(" WHERE (prz.prz_ativo = ").append(CodedValues.STS_ATIVO).append(" OR prz.prz_ativo IS NULL)");
        corpoBuilder.append(" AND (nullif(trim(pseMaxPrazo.pse_vlr), '') IS NULL OR prz.prz_vlr <= to_numeric(coalesce(nullif(trim(pseMaxPrazo.pse_vlr), ''), '0')))");

        // Possui relação com serviço da natureza de empréstimo, com convênios ativos
        corpoBuilder.append(" AND prz.svc_codigo IN (");
        corpoBuilder.append(" SELECT svc.svc_codigo");
        corpoBuilder.append(" FROM tb_servico svc");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (svc.svc_codigo = cnv.svc_codigo)");
        corpoBuilder.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpoBuilder.append(" WHERE (svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("')");
        corpoBuilder.append(" AND (svc.svc_ativo = ").append(CodedValues.STS_ATIVO).append(" OR svc.svc_ativo IS NULL)");
        corpoBuilder.append(" AND (csa.csa_ativo = ").append(CodedValues.STS_ATIVO).append(" OR csa.csa_ativo IS NULL)");
        corpoBuilder.append(" AND (cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("')");
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND (cnv.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");
        }
        corpoBuilder.append(" GROUP BY svc.svc_codigo");
        corpoBuilder.append(")");

        // Possui prazo e taxa ativas para alguma consignatária
        corpoBuilder.append(" AND prz.prz_codigo IN (");
        corpoBuilder.append(" SELECT pzc.prz_codigo");
        corpoBuilder.append(" FROM tb_prazo_consignataria pzc");
        corpoBuilder.append(" INNER JOIN tb_coeficiente_ativo cft ON (pzc.prz_csa_codigo = cft.prz_csa_codigo)");
        corpoBuilder.append(" WHERE (pzc.prz_csa_ativo = ").append(CodedValues.STS_ATIVO).append(" OR pzc.prz_csa_ativo IS NULL)");
        corpoBuilder.append(" AND (cft.cft_data_ini_vig <= data_corrente())");
        corpoBuilder.append(" AND (cft.cft_data_fim_vig >= data_corrente() OR cft.cft_data_fim_vig IS NULL)");
        corpoBuilder.append(" AND (cft.cft_vlr > 0.000000) ");
        corpoBuilder.append(" AND (cft.cft_dia = ").append(dia).append(" OR cft.cft_dia = 0)");
        corpoBuilder.append(" GROUP BY pzc.prz_codigo");
        corpoBuilder.append(")");

        // Agrupado e ordenado pelo prazo
        corpoBuilder.append(" GROUP BY prz.prz_vlr");
        corpoBuilder.append(" ORDER BY prz.prz_vlr");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRZ_VLR
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        //
    }
}
