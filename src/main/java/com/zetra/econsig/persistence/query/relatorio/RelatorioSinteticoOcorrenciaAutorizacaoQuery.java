package com.zetra.econsig.persistence.query.relatorio;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSinteticoQuery</p>
 * <p>Description: Query para relatório Sintético de Ocorrencia de Consignações</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoOcorrenciaAutorizacaoQuery extends RelatorioConsignacoesQuery {

    public List<String> campos;
    public List<String> sadCodigo;
    public List<String> svcCodigos;
    public String fields[];
    private List<String> srsCodigos;
    protected boolean relatorioSinteticoDescontos;
    public String echCodigo;
    public String plaCodigo;
    public String cnvCodVerba;
    public String usuLogin;
    public String corCodigo;
    public List<String> tmoCodigos;
    public boolean agruparServicoAnalitico;

    public Map<String,String> tipoOrdMap;
    private Date dataPeriodo;

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

        String stuExcluido = CodedValues.STU_EXCLUIDO;
        String tntControleRenegociacao = CodedValues.TNT_CONTROLE_RENEGOCIACAO;
        String tntControleCompra = CodedValues.TNT_CONTROLE_COMPRA;

        List<String> tntCodigos = new ArrayList<>();
        tntCodigos.add(tntControleRenegociacao);
        tntCodigos.add(tntControleCompra);

        List<String> usuLogins = null;
        if (!TextHelper.isNull(usuLogin)) {
            String[] usuarios = TextHelper.split(usuLogin.replaceAll(" ", ""), ",");
            if (usuarios != null && usuarios.length > 0) {
                usuLogins = Arrays.asList(usuarios);
            }
        }

        String sql = TextHelper.join(camposQuery, ",");
        String groupBy = TextHelper.join(camposQuery, ",");
        String orderBy = parseOderByClause(order, tipoOrdMap);

        // renomeia os campos do select e da ordenacao
        sql = sql.replaceAll(Columns.EST_NOME, "est.estNome AS EST_NOME");
        sql = sql.replaceAll(Columns.SVC_DESCRICAO, "svc.svcDescricao AS SVC_DESCRICAO");
        sql = sql.replaceAll(Columns.ORG_NOME, "org.orgNome AS ORG_NOME");
        sql = sql.replaceAll(Columns.SAD_DESCRICAO, "sad.sadDescricao AS SAD_DESCRICAO");
        sql = sql.replaceAll(Columns.CSA_NOME_ABREV, "csa.csaNomeAbrev AS CSA_NOME_ABREV");
        sql = sql.replaceAll(Columns.CSA_NOME, "csa.csaNome AS CSA_NOME");
        sql = sql.replaceAll(Columns.COR_NOME, "cor.corNome AS COR_NOME");
        sql = sql.replaceAll(Columns.CNV_COD_VERBA, "cnv.cnvCodVerba AS CNV_COD_VERBA");
        sql = sql.replaceAll(Columns.NSE_DESCRICAO, "svc.naturezaServico.nseDescricao AS NSE_DESCRICAO");
        sql = sql.replaceAll(Columns.TOC_DESCRICAO, "toc.tocDescricao AS TOC_DESCRICAO");
        sql = sql.replaceAll(Columns.TMO_DESCRICAO, "tmo.tmoDescricao AS TMO_DESCRICAO");

        sql = sql.replaceAll(Columns.OCA_DATA, "to_locale_date(oca.ocaData) AS OCA_DATA");
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
        groupBy = groupBy.replaceAll(Columns.CNV_COD_VERBA, "cnv.cnvCodVerba");
        groupBy = groupBy.replaceAll(Columns.NSE_DESCRICAO, "svc.naturezaServico.nseDescricao");

        groupBy = groupBy.replaceAll(Columns.TOC_DESCRICAO, "toc.tocDescricao");
        groupBy = groupBy.replaceAll(Columns.TMO_DESCRICAO, "tmo.tmoDescricao");

        groupBy = groupBy.replaceAll(Columns.OCA_DATA, "to_locale_date(oca.ocaData)");
        groupBy = groupBy.replaceAll(Columns.ADE_DATA, "to_locale_date(ade.adeData)");
        groupBy = groupBy.replaceAll(Columns.ADE_ANO_MES_INI, "to_period(ade.adeAnoMesIni)");
        groupBy = groupBy.replaceAll(Columns.ADE_ANO_MES_FIM, "to_period(ade.adeAnoMesFim)");

        // criando a consulta.
        corpoBuilder.append("SELECT ").append(sql).append(", COUNT(*) AS CONTRATOS, ");
        corpoBuilder.append(" SUM(ade.adeVlr) AS VALOR, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo, 1)) AS PRESTACAO, ");
        corpoBuilder.append(" SUM(ade.adeVlr * COALESCE(ade.adePrazo - coalesce(ade.adePrdPagas,0), 1)) AS CAPITAL_DEVIDO ");

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

        corpoBuilder.append(" INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append(" INNER JOIN oca.usuario usu ");
        corpoBuilder.append(" INNER JOIN oca.tipoOcorrencia toc ");
        corpoBuilder.append(" LEFT OUTER JOIN oca.tipoMotivoOperacao tmo ");
        corpoBuilder.append(" LEFT OUTER JOIN usu.usuarioCsaSet usuarioCsa ");

        if (camposQuery.contains(Columns.COR_NOME)) {
            corpoBuilder.append(" LEFT OUTER JOIN ade.correspondente cor");
        }

        //CLAUSULA WHERE
        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
        	corpoBuilder.append(" and oca.ocaData between :dataIni and :dataFim ");
        }

        if (!TextHelper.isNull(dataPeriodo)) {
        	corpoBuilder.append(" AND oca.ocaPeriodo").append(criaClausulaNomeada("dataPeriodo", dataPeriodo));
        }

        if (temStatus) {
            corpoBuilder.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        filtroOrigemAde(corpoBuilder);
        filtroTerminoAde(corpoBuilder);

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

        if (usuLogins != null && !usuLogins.isEmpty()) {
            corpoBuilder.append(" and (usu.usuLogin ").append(criaClausulaNomeada("usuLogins", usuLogins));
            corpoBuilder.append(" or (usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuExcluido", stuExcluido));
            corpoBuilder.append(" and usu.usuTipoBloq ").append(criaClausulaNomeada("usuLogins", usuLogins)).append(")) ");
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            corpoBuilder.append(" and tmo.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigos));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (responsavel != null && responsavel.isCsa()) {
            corpoBuilder.append(" AND (toc.tocCodigo <> '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO)
                        .append("' OR (toc.tocCodigo = '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO).append("' AND ");
            corpoBuilder.append(" usuarioCsa.consignataria.csaCodigo = :csaCodigoUsuario)) ");
        }

        corpoBuilder.append(" GROUP BY ").append(groupBy);
        corpoBuilder.append(" ORDER BY ").append(orderBy);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
        	defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (!TextHelper.isNull(dataPeriodo)) {
            defineValorClausulaNomeada("dataPeriodo", dataPeriodo, query);
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (svcCodigos != null && svcCodigos.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (sadCodigo != null && sadCodigo.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
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

        if (usuLogins != null && !usuLogins.isEmpty()) {
            defineValorClausulaNomeada("usuLogins", usuLogins, query);
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigos, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (query.getQueryString().contains("tntControleRenegociacao")) {
            defineValorClausulaNomeada("tntControleRenegociacao", tntControleRenegociacao, query);
        }
        if (query.getQueryString().contains("tntControleCompra")) {
            defineValorClausulaNomeada("tntControleCompra", tntControleCompra, query);
        }
        if (query.getQueryString().contains("tntCodigos")) {
            defineValorClausulaNomeada("tntCodigos", tntCodigos, query);
        }
        if (responsavel != null && responsavel.isCsa()) {
            defineValorClausulaNomeada("csaCodigoUsuario", responsavel.getCsaCodigo(), query);
        }

        fields = TextHelper.join(camposQuery, ",").concat(",CONTRATOS,VALOR,PRESTACAO,CAPITAL_DEVIDO").split(",");

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {

        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        dataPeriodo = (Date) criterio.getAttribute("DATA_PERIODO");
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        usuLogin = (String) criterio.getAttribute(Columns.USU_LOGIN);
        tmoCodigos = (List<String>) criterio.getAttribute(Columns.TMO_CODIGO);
        tocCodigos = (List<String>) criterio.getAttribute(Columns.TOC_CODIGO);
        sadCodigos = (List<String>) criterio.getAttribute(Columns.SAD_CODIGO);
        motivoTerminoAdes = (List<String>) criterio.getAttribute("TERMINO_ADE");
        responsavel = (AcessoSistema) criterio.getAttribute("RESPONSAVEL");
        order = (String) criterio.getAttribute("ORDER");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        campos = (List<String>) criterio.getAttribute("CAMPOS");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigo = (List<String>) criterio.getAttribute("SAD_CODIGO");
        origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
        tipoOrdMap = (Map<String,String>) criterio.getAttribute("TIPO_ORD");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
    }


    @Override
    public String[] getFields() {
        return fields;
    }
}
