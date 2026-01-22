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
 * <p>Title: RelatorioBloqueioServidorQuery</p>
 * <p>Description: Relatório de Bloqueio de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBloqueioServidorQuery extends ReportHNativeQuery {

    private String dataIni;
    private String dataFim;
    private List<String> orgCodigos;
    private String csaCodigo;
    private List<String> svcCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT SER_NOME AS SER_NOME, SER_CPF AS SER_CPF, SRS_DESCRICAO AS SRS_DESCRICAO, SVC_DESCRICAO AS SVC_DESCRICAO, ");
        sql.append("ORG_NOME AS ORG_NOME, CSA_NOME AS CSA_NOME, CNV_COD_VERBA AS CNV_COD_VERBA,");
        sql.append("PCR_VLR AS PCR_VLR, to_string(PCR_OBS) AS PCR_OBS, PCR_DATA_CADASTRO AS PCR_DATA_CADASTRO FROM (");

        // CONVENIOS BLOQUEADOS
        sql.append("SELECT ");
        // Dados do Servidor
        sql.append("concatenar(concatenar(rse.rse_matricula, ' - '), ser.ser_nome) AS SER_NOME,");
        sql.append("ser.ser_cpf AS SER_CPF,");
        sql.append("srs.srs_descricao AS SRS_DESCRICAO,");
        // Dados do serviço
        sql.append("concatenar(concatenar(svc.svc_identificador, ' - '), svc.svc_descricao) AS SVC_DESCRICAO,");
        // Dados do órgão
        sql.append("concatenar(concatenar(org.org_identificador, ' - '), org.org_nome) AS ORG_NOME,");
        // Dados da consignatária
        sql.append("concatenar(concatenar(csa.csa_identificador, ' - '), COALESCE(NULLIF(csa.csa_nome_abrev,''),csa.csa_nome)) AS CSA_NOME,");
        // Demais campos
        sql.append("cnv.cnv_cod_verba AS CNV_COD_VERBA, pcr.pcr_vlr AS PCR_VLR, pcr.pcr_obs AS PCR_OBS, ");
        sql.append(" to_locale_datetime(pcr.pcr_data_cadastro) AS PCR_DATA_CADASTRO");

        sql.append(" FROM tb_param_convenio_registro_ser pcr");
        sql.append(" INNER JOIN tb_registro_servidor rse on (rse.rse_codigo = pcr.rse_codigo)");
        sql.append(" INNER JOIN tb_status_registro_servidor srs on (srs.srs_codigo = rse.srs_codigo)");
        sql.append(" INNER JOIN tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");
        sql.append(" INNER JOIN tb_orgao org on (org.org_codigo = rse.org_codigo)");
        sql.append(" INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = pcr.cnv_codigo and cnv.org_codigo = org.org_codigo)");
        sql.append(" INNER JOIN tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo)");
        sql.append(" INNER JOIN tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
        sql.append(" WHERE NULLIF(TRIM(pcr.pcr_vlr), '') IS NOT NULL");

        if (svcCodigos != null && svcCodigos.size() > 0) {
            sql.append(" AND svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            sql.append(" AND pcr.pcr_data_cadastro BETWEEN :dataIni AND :dataFim");
        }

        sql.append(" UNION ALL ");

        // SERVICOS BLOQUEADOS
        sql.append(" SELECT ");
        // Dados do Servidor
        sql.append("concatenar(concatenar(rse.rse_matricula, ' - '), ser.ser_nome) AS SER_NOME,");
        sql.append("ser.ser_cpf AS SER_CPF,");
        sql.append("srs.srs_descricao AS SRS_DESCRICAO,");
        // Dados do serviço
        sql.append("concatenar(concatenar(svc.svc_identificador, ' - '), svc.svc_descricao) AS SVC_DESCRICAO,");
        // Dados do órgão
        sql.append("concatenar(concatenar(org.org_identificador, ' - '), org.org_nome) AS ORG_NOME,");
        // Dados da consignatária
        sql.append("' ---------- N/A ---------- ' AS CSA_NOME,");
        // Demais campos
        sql.append("' --- N/A --- ' AS CNV_COD_VERBA,");
        sql.append("psr.psr_vlr AS PCR_VLR, psr.psr_obs AS PCR_OBS, ");
        sql.append(" to_locale_datetime(psr.psr_data_cadastro) AS PCR_DATA_CADASTRO");

        sql.append(" FROM tb_param_servico_registro_ser psr");
        sql.append(" INNER JOIN tb_registro_servidor rse on (rse.rse_codigo = psr.rse_codigo)");
        sql.append(" INNER JOIN tb_status_registro_servidor srs on (srs.srs_codigo = rse.srs_codigo)");
        sql.append(" INNER JOIN tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");
        sql.append(" INNER JOIN tb_orgao org on (org.org_codigo = rse.org_codigo)");
        sql.append(" INNER JOIN tb_servico svc on (svc.svc_codigo = psr.svc_codigo)");
        sql.append(" WHERE NULLIF(TRIM(psr.psr_vlr), '') IS NOT NULL");

        if (svcCodigos != null && svcCodigos.size() > 0) {
            sql.append(" AND svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            sql.append(" AND psr.psr_data_cadastro BETWEEN :dataIni AND :dataFim");
        }

        sql.append(") RELATORIO ");
        sql.append(" ORDER BY SVC_DESCRICAO, CSA_NOME, ORG_NOME, SER_NOME");

       Query<Object[]> query = instanciarQuery(session, sql.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (svcCodigos != null && svcCodigos.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SRS_DESCRICAO,
                Columns.SVC_DESCRICAO,
                Columns.ORG_NOME,
                Columns.CSA_NOME,
                Columns.CNV_COD_VERBA,
                Columns.PCR_VLR,
                Columns.PCR_OBS,
                Columns.PCR_DATA_CADASTRO
        };
    }

}
