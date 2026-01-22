package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioReimplanteLoteQuery</p>
 * <p>Description: Query que gera dados para o relatorio de contratos reimplantados.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioReimplanteLoteQuery extends ReportHNativeQuery {

    private List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("ade.ade_codigo as ADE_CODIGO, ");
        corpoBuilder.append("to_string(ade.ade_numero) as ADE_NUMERO, ");
        corpoBuilder.append("ade.ade_prazo as ADE_PRAZO, ");
        corpoBuilder.append("to_decimal(coalesce(ade.ade_prd_pagas, 0), 9, 0) as ADE_PRD_PAGAS, ");
        corpoBuilder.append("rse.rse_matricula as RSE_MATRICULA, ");
        corpoBuilder.append("csa.csa_nome as CSA_NOME, ");
        corpoBuilder.append("ser.ser_nome as SER_NOME, ");
        corpoBuilder.append("ser.ser_cpf as SER_CPF, ");
        corpoBuilder.append("cnv.cnv_cod_verba as VERBA, ");
        corpoBuilder.append("to_decimal(coalesce(ade.ade_vlr, 0), 9, 0) as ADE_VLR, ");
        corpoBuilder.append("concatenar(csa.csa_codigo, cnv.cnv_cod_verba) as ORDEM, ");
        corpoBuilder.append("to_string(tdad.dad_valor) as ADE_NUM_NOVO ");

        corpoBuilder.append(" from tb_aut_desconto ade");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
        corpoBuilder.append(" left join tb_dados_autorizacao_desconto tdad on (ade.ade_codigo = tdad.ade_codigo and tdad.tda_codigo = '88')");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(adeCodigos) && !adeCodigos.isEmpty()) {
            corpoBuilder.append(" and ade.ade_codigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        }

        corpoBuilder.append(" ORDER BY concatenar(csa.csa_codigo, cnv.cnv_cod_verba) ASC");
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigos) && !adeCodigos.isEmpty()) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "ADE_CODIGO",
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.RSE_MATRICULA,
                Columns.CSA_NOME,
                Columns.SER_NOME,
                Columns.SER_CPF,
                "VERBA",
                Columns.ADE_VLR,
                "ORDEM",
                "ADE_NUM_NOVO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        adeCodigos = (List<String>) criterio.getAttribute("adeCodigos");
    }
}
