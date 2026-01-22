package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTransfereAdeQuery</p>
 * <p>Description: Listagem de transferências de autorização.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTransfereAdeQuery extends ReportHNativeQuery {

    private String csaCodigoOrigem;
    private String csaCodigoDestino;
    private String svcCodigoOrigem;
    private String svcCodigoDestino;
    private String orgCodigo;
    private List<String> sadCodigo;
    private String periodoIni;
    private String periodoFim;
    private List<Long> adeNumero;
    private String rseMatricula;
    private String serCpf;
    private boolean somenteConveniosAtivos = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("ade.ade_codigo as ADE_CODIGO, ");
        corpoBuilder.append("to_string(ade.ade_numero) as ADE_NUMERO, ");
        corpoBuilder.append("ade.ade_identificador as ADE_IDENTIFICADOR, ");
        corpoBuilder.append("ade.ade_vlr as ADE_VLR, ");
        corpoBuilder.append("ade.ade_prazo as ADE_PRAZO, ");
        corpoBuilder.append("to_decimal(coalesce(ade.ade_prd_pagas, 0), 9, 0) as ADE_PRD_PAGAS, ");
        corpoBuilder.append("rse.rse_matricula as RSE_MATRICULA, ");
        corpoBuilder.append("ser.ser_nome as SER_NOME, ");
        corpoBuilder.append("ser.ser_cpf as SER_CPF, ");
        corpoBuilder.append("vco2.vco_codigo as VCO_CODIGO_NEW, ");
        corpoBuilder.append("csa.csa_nome as CSA_ORIGEM, ");
        corpoBuilder.append("svc.svc_descricao as SVC_ORIGEM, ");
        corpoBuilder.append("cnv.cnv_cod_verba as VERBA_ORIGEM, ");
        corpoBuilder.append("csa2.csa_nome as CSA_DESTINO, ");
        corpoBuilder.append("svc2.svc_descricao as SVC_DESTINO, ");
        corpoBuilder.append("cnv2.cnv_cod_verba as VERBA_DESTINO, ");
        corpoBuilder.append("concatenar(concatenar(concatenar(csa.csa_codigo, cnv.cnv_cod_verba), csa2.csa_codigo), cnv2.cnv_cod_verba) as ORDEM ");

        corpoBuilder.append(" from tb_aut_desconto ade");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");

        corpoBuilder.append(" inner join tb_convenio cnv2 on (cnv.org_codigo = cnv2.org_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco2 on (vco2.cnv_codigo = cnv2.cnv_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa2 on (cnv2.csa_codigo = csa2.csa_codigo)");
        corpoBuilder.append(" inner join tb_servico svc2 on (cnv2.svc_codigo = svc2.svc_codigo)");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(csaCodigoOrigem)) {
            corpoBuilder.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem));
        }
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            corpoBuilder.append(" AND svc.svc_codigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }

        if (!TextHelper.isNull(csaCodigoDestino)) {
            corpoBuilder.append(" AND csa2.csa_codigo ").append(criaClausulaNomeada("csaCodigoDestino", csaCodigoDestino));
        } else {
            corpoBuilder.append(" AND csa2.csa_codigo = csa.csa_codigo");
        }
        if (!TextHelper.isNull(svcCodigoDestino)) {
            corpoBuilder.append(" AND svc2.svc_codigo ").append(criaClausulaNomeada("svcCodigoDestino", svcCodigoDestino));
        } else {
            corpoBuilder.append(" AND svc2.svc_codigo = svc.svc_codigo");
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        // DESENV-20772: Só permite transferências para convênios destino ativos.
        if (somenteConveniosAtivos) {
            corpoBuilder.append(" AND cnv2.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("' ");
        }

        corpoBuilder.append(" AND ade.ade_data BETWEEN :periodoIni AND :periodoFim ");

        if (sadCodigo != null && sadCodigo.size() > 0) {
            corpoBuilder.append(" AND ade.sad_codigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }

        if (adeNumero != null && !adeNumero.isEmpty()) {
            corpoBuilder.append(" AND ade.ade_numero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        // Gera cláusula de Matricula e CPF
        corpoBuilder.append(ListaServidorQuery.gerarClausulaNativaMatriculaCpf(rseMatricula, serCpf, true));

        corpoBuilder.append(" ORDER BY concatenar(concatenar(concatenar(csa.csa_codigo, cnv.cnv_cod_verba), csa2.csa_codigo), cnv2.cnv_cod_verba) ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, true, query);
        if (!TextHelper.isNull(csaCodigoOrigem)) {
            defineValorClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem, query);
        }
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }
        if (!TextHelper.isNull(csaCodigoDestino)) {
            defineValorClausulaNomeada("csaCodigoDestino", csaCodigoDestino, query);
        }
        if (!TextHelper.isNull(svcCodigoDestino)) {
            defineValorClausulaNomeada("svcCodigoDestino", svcCodigoDestino, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (sadCodigo != null && sadCodigo.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }
        if (adeNumero != null && !adeNumero.isEmpty()) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        defineValorClausulaNomeada("periodoIni", parseDateTimeString(periodoIni), query);
        defineValorClausulaNomeada("periodoFim", parseDateTimeString(periodoFim), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                "VCO_CODIGO_NEW",
                "CSA_ORIGEM",
                "SVC_ORIGEM",
                "VERBA_ORIGEM",
                "CSA_DESTINO",
                "SVC_DESTINO",
                "VERBA_DESTINO",
                "ORDEM"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigoOrigem = (String) criterio.getAttribute("csaCodigoOrigem");
        csaCodigoDestino = (String) criterio.getAttribute("csaCodigoDestino");
        svcCodigoOrigem = (String) criterio.getAttribute("svcCodigoOrigem");
        svcCodigoDestino = (String) criterio.getAttribute("svcCodigoDestino");
        orgCodigo = (String) criterio.getAttribute("orgCodigo");
        sadCodigo = (List<String>) criterio.getAttribute("sadCodigo");
        periodoIni = (String) criterio.getAttribute("periodoIni");
        periodoFim = (String) criterio.getAttribute("periodoFim");
        adeNumero = (List<Long>) criterio.getAttribute("adeNumero");
        rseMatricula = (String) criterio.getAttribute("rseMatricula");
        serCpf = (String) criterio.getAttribute("serCpf");
        somenteConveniosAtivos = criterio.getAttribute("somenteConveniosAtivos") != null && (Boolean) criterio.getAttribute("somenteConveniosAtivos");
    }
}
