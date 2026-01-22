package com.zetra.econsig.persistence.query.historico;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoOcorrenciaParcelaQuery</p>
 * <p>Description: Listagem de ocorrências de parcela para
 * exibição do histórico</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoConsultaParcelaQuery extends HNativeQuery {

    public String csaCodigo;
    public List<String> spdCodigos;
    public Date prdDataDesconto;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "prd.prd_numero," +
                       "prd.prd_data_desconto, " +
                       "prd.prd_data_realizado, " +
                       "prd.prd_vlr_previsto, " +
                       "prd.prd_vlr_realizado, " +
                       "spd.spd_codigo, " +
                       "spd.spd_descricao, " +
                       "ocp.ocp_data, " +
                       "ocp.ocp_obs, " +
                       "ade.ade_numero, " +
                       "ade.ade_vlr, " +
                       "ade.ade_prazo, " +
                       "ade.ade_prd_pagas, " +
                       "ade.ade_indice, " +
                       "cnv.cnv_cod_verba, " +
                       "rse.rse_matricula, " +
                       "ser.ser_cpf, " +
                       "ser.ser_nome "
                       ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        // Busca da tabela de histórico
        corpoBuilder.append(" FROM ht_aut_desconto ade ");
        corpoBuilder.append(" INNER JOIN ht_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd ON (spd.spd_codigo = prd.spd_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        corpoBuilder.append(" LEFT OUTER JOIN ht_ocorrencia_parcela ocp ON (prd.prd_codigo = ocp.prd_codigo) ");
        corpoBuilder.append(" WHERE cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("prdDataDesconto", prdDataDesconto));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spd_codigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        // Busca da tabela de parcela
        corpoBuilder.append(" UNION ALL ");
        corpoBuilder.append(corpo);
        corpoBuilder.append(" FROM tb_aut_desconto ade ");
        corpoBuilder.append(" INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd ON (spd.spd_codigo = prd.spd_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp ON (prd.prd_codigo = ocp.prd_codigo) ");
        corpoBuilder.append(" WHERE cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("prdDataDesconto", prdDataDesconto));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spd_codigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        // Busca da tabela de parcela período
        corpoBuilder.append(" UNION ALL ");
        corpoBuilder.append(corpo);
        corpoBuilder.append(" FROM tb_aut_desconto ade ");
        corpoBuilder.append(" INNER JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd ON (spd.spd_codigo = prd.spd_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append(" INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela_periodo ocp ON (prd.prd_codigo = ocp.prd_codigo) ");
        corpoBuilder.append(" WHERE cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("prdDataDesconto", prdDataDesconto));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spd_codigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("prdDataDesconto", prdDataDesconto, query);

        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_NUMERO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                Columns.OCP_DATA,
                Columns.OCP_OBS,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_INDICE,
                Columns.CNV_COD_VERBA,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SER_NOME
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
