package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioInformacaoBancariaDivergenteQuery</p>
 * <p> Description: Relatório de contratos com informação bancária divergente.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInformacaoBancariaDivergenteQuery extends ReportHQuery {

    public Date periodoIni;
    public Date periodoFim;
    public String csaCodigo;
    public List<String> orgCodigos;
    public List<String> svcCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO))) {
            try {
                periodoIni = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO), "yyyy-MM-dd");
            } catch (ParseException ex) {
                periodoIni = null;
            }
        }
        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM))) {
            try {
                periodoFim  = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM), "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException ex) {
                periodoFim = null;
            }
        }
        csaCodigo = (String) criterio.getAttribute(Columns.CNV_CSA_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.CNV_ORG_CODIGO);
        svcCodigo = (List<String>) criterio.getAttribute(Columns.CNV_SVC_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String campoVerba = ParamSist.paramEquals(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())
                          ? "cnv.cnvCodVerbaRef" : "cnv.cnvCodVerba";
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ser.serNome AS ser_nome, ser.serCpf AS ser_cpf, rse.rseMatricula AS rse_matricula, ");
        corpoBuilder.append("srs.srsDescricao as srs_descricao, ");
        corpoBuilder.append("rse.rseBancoSal AS rse_banco_sal, rse.rseAgenciaSal AS rse_agencia_sal, rse.rseContaSal AS rse_conta_sal, ");
        corpoBuilder.append("ade.adeCodigo AS ade_codigo, CAST(ade.adeNumero AS string) AS ade_numero, ade.adeData AS ade_data, ade.adeVlr AS ade_vlr, ");
        corpoBuilder.append("ade.adeBanco AS ade_banco, ade.adeAgencia AS ade_agencia, ade.adeConta AS ade_conta, ");
        corpoBuilder.append("       ").append(campoVerba).append(" AS verba, csa.csaIdentificador AS csa_identificador, csa.csaNome AS csa_nome, svc.svcDescricao AS svc_descricao ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.statusRegistroServidor srs ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE ((rse.rseBancoSal IS NOT NULL AND ade.adeBanco IS NOT NULL AND format_for_comparision(rse.rseBancoSal, 3) <> format_for_comparision(ade.adeBanco, 3)) OR ");
        corpoBuilder.append("       (rse.rseAgenciaSal IS NOT NULL AND ade.adeAgencia IS NOT NULL AND format_for_comparision(rse.rseAgenciaSal, 30) <> format_for_comparision(ade.adeAgencia, 30)) OR ");
        corpoBuilder.append("       (rse.rseContaSal IS NOT NULL AND ade.adeConta IS NOT NULL AND format_for_comparision(rse.rseContaSal, 11) <> format_for_comparision(ade.adeConta, 11))) ");
        corpoBuilder.append("AND ade.adeData BETWEEN :periodoIni AND :periodoFim ");
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (svcCodigo != null && svcCodigo.size() > 0) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        // ORDER BY csa_identificador, csa_nome, verba, rse_matricula, ser_nome, ade_data DESC, ade_numero DESC
        corpoBuilder.append(" ORDER BY 15, 16, 14, 3, 1, 9 DESC, 8 DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodoIni", periodoIni, query);
        defineValorClausulaNomeada("periodoFim", periodoFim, query);
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (svcCodigo != null && svcCodigo.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA,
                Columns.SRS_DESCRICAO,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_BANCO,
                Columns.ADE_AGENCIA,
                Columns.ADE_CONTA,
                "verba",
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.SVC_DESCRICAO
        };
    }
}
