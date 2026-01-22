package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSinteticoQuery</p>
 * <p>Description: Query para relatório Sintético de Consignações</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoQuery extends RelatorioConsignacoesQuery {

    public List<String> campos;
    public List<String> sadCodigo;
    public List<String> svcCodigos;
    public String[] fields;
    private List<String> srsCodigos;
    protected boolean relatorioSinteticoDescontos;
    public String echCodigo;
    public String plaCodigo;
    public String cnvCodVerba;
    public String sboCodigo;
    public String uniCodigo;
    public List<String> nseCodigos;

    public Map<String,String> tipoOrdMap;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean temStatus = (srsCodigos != null && !srsCodigos.isEmpty());

        StringBuilder corpoBuilder = new StringBuilder();

        List<String> camposQuery = new ArrayList<>();
        CamposRelatorioSinteticoEnum camposEnum = null;
        for (int i = 0; i < campos.size(); i++) {
            String key = campos.get(i);
            camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
            if (camposEnum != null) {
                camposQuery.add(camposEnum.getCampo());
            }
        }

        String sql = TextHelper.join(camposQuery, ",");
        String groupBy = TextHelper.join(camposQuery, ",");
        String orderBy = parseOderByClause(order, tipoOrdMap);

        // renomeia os campos do select e da ordenacao
        sql = sql.replace(Columns.EST_NOME, "est.estNome AS EST_NOME");
        sql = sql.replace(Columns.SVC_DESCRICAO, "svc.svcDescricao AS SVC_DESCRICAO");
        sql = sql.replace(Columns.ORG_NOME, "org.orgNome AS ORG_NOME");
        sql = sql.replace(Columns.SAD_DESCRICAO, "sad.sadDescricao AS SAD_DESCRICAO");
        sql = sql.replace(Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev AS CSA_NOME_ABREV");
        sql = sql.replace(Columns.CSA_NOME, "csa.csaNome AS CSA_NOME");
        sql = sql.replace(Columns.COR_NOME, "cor.corNome AS COR_NOME");

        sql = sql.replace(Columns.ADE_DATA, "to_locale_date(ade.adeData) AS ADE_DATA");
        sql = sql.replace(Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni) AS ADE_ANO_MES_INI");
        sql = sql.replace(Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim) AS ADE_ANO_MES_FIM");
        sql = sql.replace(Columns.NSE_DESCRICAO, "nse.nseDescricao as NSE_DESCRICAO");

        groupBy = groupBy.replace(Columns.EST_NOME, "est.estNome");
        groupBy = groupBy.replace(Columns.SVC_DESCRICAO, "svc.svcDescricao");
        groupBy = groupBy.replace(Columns.ORG_NOME, "org.orgNome");
        groupBy = groupBy.replace(Columns.SAD_DESCRICAO, "sad.sadDescricao");
        groupBy = groupBy.replace(Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev");
        groupBy = groupBy.replace(Columns.CSA_NOME, "csa.csaNome");
        groupBy = groupBy.replace(Columns.COR_NOME, "cor.corNome");

        groupBy = groupBy.replace(Columns.ADE_DATA, "to_locale_date(ade.adeData)");
        groupBy = groupBy.replace(Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni)");
        groupBy = groupBy.replace(Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim)");
        groupBy = groupBy.replace(Columns.NSE_DESCRICAO, "nse.nseDescricao");

        // criando a consulta.
        corpoBuilder.append("SELECT ").append(sql).append(", COUNT(*) AS CONTRATOS, ");
        corpoBuilder.append(" SUM(ade.adeVlr) AS VALOR, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo, 1)) AS PRESTACAO, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo - coalesce(ade.adePrdPagas,0), 1)) AS CAPITAL_DEVIDO");

        // JOINS
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" INNER JOIN svc.naturezaServico nse");

        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" INNER JOIN rse.subOrgao sbo");
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" INNER JOIN rse.unidade uni");
        }

        if (camposQuery.contains(Columns.COR_NOME)) {
            corpoBuilder.append(" LEFT OUTER JOIN ade.correspondente cor");
        }

        if (relatorioSinteticoDescontos) {
            corpoBuilder.append(" INNER JOIN ade.despesaIndividualSet des ");

            if (!TextHelper.isNull(plaCodigo)) {
                corpoBuilder.append(" INNER JOIN des.plano pla ");
            }

            if (!TextHelper.isNull(echCodigo)) {
                corpoBuilder.append(" INNER JOIN des.permissionario per ");
            }
        }

        //CLAUSULA WHERE
        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
        	corpoBuilder.append(" WHERE ade.adeData BETWEEN :dataIni AND :dataFim");
        }

        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        filtroOrigemAde(corpoBuilder);

        if (corCodigos != null && !corCodigos.isEmpty() && !corCodigos.contains("-1") && !corCodigos.contains("") && corCodigos.get(0) != null) {
            corpoBuilder.append(" AND ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" and sbo.sboCodigo ").append(criaClausulaNomeada("sboCodigo", sboCodigo));
        }
        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" and uni.uniCodigo ").append(criaClausulaNomeada("uniCodigo", uniCodigo));
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (sadCodigo != null && !sadCodigo.isEmpty()) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }
        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.servico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigos));
        }

        if (relatorioSinteticoDescontos) {
            if (!TextHelper.isNull(plaCodigo)) {
                corpoBuilder.append(" AND pla.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
            }

            if (!TextHelper.isNull(echCodigo)) {
                corpoBuilder.append(" AND per.enderecoConjHabitacional.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
            }

            if (!TextHelper.isNull(cnvCodVerba)) {
                corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
            }
        }

        corpoBuilder.append(" GROUP BY ").append(groupBy);
        corpoBuilder.append(" ORDER BY ").append(orderBy);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
	        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
	        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (corCodigos != null && !corCodigos.isEmpty() && !corCodigos.contains("-1") && !corCodigos.contains("") && corCodigos.get(0) != null) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }
        if (!TextHelper.isNull(uniCodigo)) {
            defineValorClausulaNomeada("uniCodigo", uniCodigo, query);
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }
        if (sadCodigo != null && !sadCodigo.isEmpty()) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }
        if (nseCodigos != null && !nseCodigos.isEmpty()) {
        	defineValorClausulaNomeada("nseCodigo", nseCodigos, query);
        }

        if (relatorioSinteticoDescontos) {
            if (!TextHelper.isNull(plaCodigo)) {
                defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
            }

            if (!TextHelper.isNull(echCodigo)) {
                defineValorClausulaNomeada("echCodigo", echCodigo, query);
            }

            if (!TextHelper.isNull(cnvCodVerba)) {
                defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
            }
        }

        fields = TextHelper.join(camposQuery, ",").concat(",CONTRATOS,VALOR,PRESTACAO,CAPITAL_DEVIDO").split(",");

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {

        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        sboCodigo = (String) criterio.getAttribute("SBO_CODIGO");
        uniCodigo = (String) criterio.getAttribute("UNI_CODIGO");
        order = (String) criterio.getAttribute("ORDER");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        campos = (List<String>) criterio.getAttribute("CAMPOS");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigo = (List<String>) criterio.getAttribute("SAD_CODIGO");
        origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
        tipoOrdMap = (Map<String,String>) criterio.getAttribute("TIPO_ORD");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
    }

    @Override
    public String[] getFields() {
        return fields;
    }
}