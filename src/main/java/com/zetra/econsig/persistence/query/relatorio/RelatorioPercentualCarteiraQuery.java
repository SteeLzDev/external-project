package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioPercentualCarteiraQuery</p>
 * <p> Description: Recupera o percentual de carteira</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPercentualCarteiraQuery extends ReportHNativeQuery {

    public String dataIni;
    public String dataFim;
    public List<String> orgCodigo;
    public List<String> svcCodigos;
    public List<String> origemAdes;
    public String campo = "QTDE";
    private List<String> tiposNatureza;

    public RelatorioPercentualCarteiraQuery(String dataIni, String dataFim, List<String> svcCodigos, List<String> orgCodigo, List<String> origensAdes, String campo) {
        this.dataIni = dataIni;
        this.dataFim = dataFim;
        this.orgCodigo = orgCodigo;
        this.svcCodigos = svcCodigos;
        origemAdes = origensAdes;
        this.campo = campo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");
        corpoBuilder.append(" csa.csa_codigo as CSA_CODIGO, ");
        corpoBuilder.append(" csa.csa_identificador as CSA_IDENTIFICADOR, ");
        corpoBuilder.append(" csa_identificador_interno as CSA_ID, ");
        corpoBuilder.append(" coalesce(nullif(csa_nome_abrev, ''), csa_nome) as CSA, ");
        if (campo.equals("QTDE")) {
            corpoBuilder.append(" to_decimal(count(csa.csa_codigo), 13, 2) as PERC_QTDE ");
        } else if (campo.equals("PARCELA")) {
            corpoBuilder.append(" to_decimal(sum(ade.ade_vlr), 13, 2) as PERC_PARCELA ");
        } else if (campo.equals("TOTAL")) {
            corpoBuilder.append(" to_decimal(sum(ade.ade_vlr * ade.ade_prazo), 13, 2) as PERC_TOTAL ");
        } else {
            corpoBuilder.append(" to_decimal(count(csa.csa_codigo), 13, 2) as PERC_QTDE, ");
            corpoBuilder.append(" to_decimal(sum(ade.ade_vlr), 13, 2) as PERC_PARCELA, ");
            corpoBuilder.append(" to_decimal(sum(ade.ade_vlr * ade.ade_prazo), 13, 2) as PERC_TOTAL ");
        }
        corpoBuilder.append(" from tb_aut_desconto ade ");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append(" inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append(" where 1 = 1 ");

        filtroOrigemAde(corpoBuilder);
        corpoBuilder.append(" and svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        corpoBuilder.append(" and ade.sad_codigo ").append(" not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");

        if (dataIni != null && dataFim != null) {
            corpoBuilder.append(" and ade.ade_ano_mes_ini between :dataIni and :dataFim");
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" group by csa.csa_codigo, csa.csa_identificador, ");
        corpoBuilder.append(" csa_identificador_interno, coalesce(nullif(csa_nome_abrev, ''), csa_nome) ");
        if (campo.equals("PARCELA")) {
            corpoBuilder.append(" order by sum(ade.ade_vlr) desc ");
        } else if (campo.equals("TOTAL")) {
            corpoBuilder.append(" order by sum(ade.ade_vlr * ade.ade_prazo) desc ");
        } else {
            corpoBuilder.append(" order by count(csa.csa_codigo) desc ");
        }

        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        if (dataIni != null && dataFim != null) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), queryInst);
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), queryInst);
        }
        if (tiposNatureza != null && !tiposNatureza.isEmpty()) {
            defineValorClausulaNomeada("tiposNatureza", tiposNatureza, queryInst);
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, queryInst);
        }
        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        if (campo.equals("QTDE")) {
            return new String [] {
                    Columns.CSA_CODIGO,
                    Columns.CSA_IDENTIFICADOR,
                    "CSA_ID",
                    "CSA",
                    "PERC_QTDE"};
        } else if (campo.equals("PARCELA")) {
            return new String [] {
                    Columns.CSA_CODIGO,
                    Columns.CSA_IDENTIFICADOR,
                    "CSA_ID",
                    "CSA",
                    "PERC_PARCELA"};
        } else if (campo.equals("TOTAL")) {
            return new String [] {
                    Columns.CSA_CODIGO,
                    Columns.CSA_IDENTIFICADOR,
                    "CSA_ID",
                    "CSA",
                    "PERC_TOTAL"};
        } else {
            return new String [] {
                    Columns.CSA_CODIGO,
                    Columns.CSA_IDENTIFICADOR,
                    "CSA_ID",
                    "CSA",
                    "PERC_QTDE",
                    "PERC_PARCELA",
                    "PERC_TOTAL"};
        }
    }

    protected void filtroOrigemAde(StringBuilder corpoBuilder) {
        if (origemAdes != null && !origemAdes.isEmpty()) {
            tiposNatureza = new ArrayList<>();
            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA)) {
                if (!origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    tiposNatureza.add(CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                }
                if (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    tiposNatureza.add(CodedValues.TNT_CONTROLE_COMPRA);
                }
                if (!tiposNatureza.isEmpty()) {
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM tb_relacionamento_autorizacao rad ");
                    corpoBuilder.append(" where rad.tnt_codigo ").append(criaClausulaNomeada("tiposNatureza", tiposNatureza));
                    corpoBuilder.append(" AND rad.ade_codigo_destino = ade.ade_codigo)) ");
                }

            } else {
                if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    tiposNatureza.add(CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                }
                if (origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    tiposNatureza.add(CodedValues.TNT_CONTROLE_COMPRA);
                }
                if (!tiposNatureza.isEmpty()) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM tb_relacionamento_autorizacao rad ");
                    corpoBuilder.append(" where rad.tnt_codigo ").append(criaClausulaNomeada("tiposNatureza", tiposNatureza));
                    corpoBuilder.append(" AND rad.ade_codigo_destino = ade.ade_codigo)) ");
                }
            }
        }
    }
}

