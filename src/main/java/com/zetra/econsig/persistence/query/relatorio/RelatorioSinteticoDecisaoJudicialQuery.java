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
 * <p>Title: RelatorioSinteticoDecisaoJudicialQuery</p>
 * <p>Description: Query de relatório Sintético de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoDecisaoJudicialQuery extends RelatorioConsignacoesQuery {

    public List<String> campos;
    public List<String> sadCodigo;
    public List<String> svcCodigos;
    public String fields[];
    private List<String> srsCodigos;
    public String tjuCodigo;
    public String cidCodigo;
    public String ufCodigo;

    public Map<String,String> tipoOrdMap;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean temStatus = (srsCodigos != null && srsCodigos.size() > 0);

        StringBuilder corpoBuilder = new StringBuilder();

        List<String> camposQuery = new ArrayList<>();
        CamposRelatorioSinteticoEnum camposEnum = null;
        for (int i = 0; i < campos.size(); i++) {
            String key = campos.get(i).toString();
            camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
            if (camposEnum != null) {
                camposQuery.add(camposEnum.getCampo());
            }
        }

        String sql = TextHelper.join(camposQuery, ",");
        String groupBy = TextHelper.join(camposQuery, ",");
        String orderBy = parseOderByClause(order, tipoOrdMap);

        // renomeia os campos do select, do agrupamento e da ordenacao
        sql = sql.replaceAll(Columns.EST_NOME, "est.estNome AS EST_NOME");
        sql = sql.replaceAll(Columns.SVC_DESCRICAO, "svc.svcDescricao AS SVC_DESCRICAO");
        sql = sql.replaceAll(Columns.ORG_NOME, "org.orgNome AS ORG_NOME");
        sql = sql.replaceAll(Columns.SAD_DESCRICAO, "sad.sadDescricao AS SAD_DESCRICAO");
        sql = sql.replaceAll(Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev AS CSA_NOME_ABREV");
        sql = sql.replaceAll(Columns.CSA_NOME, "csa.csaNome AS CSA_NOME");
        sql = sql.replaceAll(Columns.COR_NOME, "cor.corNome AS COR_NOME");
        sql = sql.replaceAll(Columns.TJU_DESCRICAO, "tju.tjuDescricao AS TJU_DESCRICAO");
        sql = sql.replaceAll(Columns.CID_NOME, "concatenar(concatenar(cid.cidNome,' - '), cid.uf.ufCod) AS CID_NOME");

        sql = sql.replaceAll(Columns.DJU_DATA, "to_locale_date(dju.djuData) AS DJU_DATA");
        sql = sql.replaceAll(Columns.ADE_DATA, "to_locale_date(ade.adeData) AS ADE_DATA");
        sql = sql.replaceAll(Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni) AS ADE_ANO_MES_INI");
        sql = sql.replaceAll(Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim) AS ADE_ANO_MES_FIM");

        groupBy = groupBy.replaceAll(Columns.EST_NOME, "est.estNome");
        groupBy = groupBy.replaceAll(Columns.SVC_DESCRICAO, "svc.svcDescricao");
        groupBy = groupBy.replaceAll(Columns.ORG_NOME, "org.orgNome");
        groupBy = groupBy.replaceAll(Columns.SAD_DESCRICAO, "sad.sadDescricao");
        groupBy = groupBy.replaceAll(Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev");
        groupBy = groupBy.replaceAll(Columns.CSA_NOME, "csa.csaNome");
        groupBy = groupBy.replaceAll(Columns.COR_NOME, "cor.corNome");
        groupBy = groupBy.replaceAll(Columns.TJU_DESCRICAO, "tju.tjuDescricao");
        groupBy = groupBy.replaceAll(Columns.CID_NOME, "cid.cidNome, cid.uf.ufCod");

        groupBy = groupBy.replaceAll(Columns.DJU_DATA, "to_locale_date(dju.djuData)");
        groupBy = groupBy.replaceAll(Columns.ADE_DATA, "to_locale_date(ade.adeData)");
        groupBy = groupBy.replaceAll(Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni)");
        groupBy = groupBy.replaceAll(Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim)");

        // criando a consulta.
        corpoBuilder.append("SELECT ").append(sql).append(", COUNT(*) AS CONTRATOS, ");
        corpoBuilder.append(" SUM(ade.adeVlr) AS VALOR, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo, 1)) AS PRESTACAO, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo - coalesce(ade.adePrdPagas,0), 1)) AS CAPITAL_DEVIDO ");

        // JOINS
        corpoBuilder.append(" FROM DecisaoJudicial dju ");
        corpoBuilder.append(" INNER JOIN dju.ocorrenciaAutorizacao oca ");
        corpoBuilder.append(" INNER JOIN dju.tipoJustica tju ");
        corpoBuilder.append(" INNER JOIN dju.cidade cid ");
        corpoBuilder.append(" INNER JOIN oca.autDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");

        if (camposQuery.contains(Columns.COR_NOME)) {
            corpoBuilder.append(" LEFT OUTER JOIN ade.correspondente cor");
        }

        //CLAUSULA WHERE
        corpoBuilder.append(" WHERE dju.djuData BETWEEN :dataIni AND :dataFim");
        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }
        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            corpoBuilder.append(" AND ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (svcCodigos != null && svcCodigos.size() > 0) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (sadCodigo != null && sadCodigo.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }
        if (!TextHelper.isNull(tjuCodigo)) {
            corpoBuilder.append(" AND tju.tjuCodigo ").append(criaClausulaNomeada("tjuCodigo", tjuCodigo));
        }
        if (!TextHelper.isNull(cidCodigo)) {
            corpoBuilder.append(" AND cid.cidCodigo ").append(criaClausulaNomeada("cidCodigo", cidCodigo));
        }
        if (!TextHelper.isNull(ufCodigo)) {
            corpoBuilder.append(" AND cid.uf.ufCod ").append(criaClausulaNomeada("ufCodigo", ufCodigo));
        }

        corpoBuilder.append(" GROUP BY ").append(groupBy);
        corpoBuilder.append(" ORDER BY ").append(orderBy);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }
        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty())  {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (svcCodigos != null && svcCodigos.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }
        if (sadCodigo != null && sadCodigo.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }
        if (!TextHelper.isNull(tjuCodigo)) {
            defineValorClausulaNomeada("tjuCodigo", tjuCodigo, query);
        }
        if (!TextHelper.isNull(cidCodigo)) {
            defineValorClausulaNomeada("cidCodigo", cidCodigo, query);
        }
        if (!TextHelper.isNull(ufCodigo)) {
            defineValorClausulaNomeada("ufCodigo", ufCodigo, query);
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
        order = (String) criterio.getAttribute("ORDER");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        campos = (List<String>) criterio.getAttribute("CAMPOS");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigo = (List<String>) criterio.getAttribute("SAD_CODIGO");
        tipoOrdMap = (Map<String,String>) criterio.getAttribute("TIPO_ORD");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        tjuCodigo = (String) criterio.getAttribute("TJU_CODIGO");
        cidCodigo = (String) criterio.getAttribute("CID_CODIGO");
        ufCodigo = (String) criterio.getAttribute("UF_CODIGO");
    }

    @Override
    public String[] getFields() {
        return fields;
    }
}
