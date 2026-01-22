package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioDecisaoJudicialQuery</p>
 * <p>Description: Query Relatório de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioDecisaoJudicialQuery extends ReportHQuery {

    public String serCpf;
    public String rseMatricula;
    public String dataIni;
    public String dataFim;
    public List<String> orgCodigos;
    public String estCodigo;
    public String csaCodigo;
    public String corCodigo;
    public List<String> svcCodigos;
    public List<String> sadCodigos;
    private List<String> srsCodigos;
    private String tipoJustica;
    private String estadoJustica;
    private String comarcaJustica;
    private String numeroProcessoJustica;
    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
        rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        svcCodigos = (List<String>) criterio.getAttribute(Columns.SVC_CODIGO);
        sadCodigos = (List<String>) criterio.getAttribute(Columns.SAD_CODIGO);
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        tipoJustica = (String) criterio.getAttribute(Columns.TJU_CODIGO);
        estadoJustica = (String) criterio.getAttribute(Columns.UF_COD);
        comarcaJustica = (String) criterio.getAttribute(Columns.CID_CODIGO);
        numeroProcessoJustica = (String) criterio.getAttribute(Columns.DJU_NUM_PROCESSO);
        responsavel = (AcessoSistema) criterio.getAttribute("RESPONSAVEL");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean permiteCadIndice = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel);

        final StringBuilder corpoBuilder = new StringBuilder("select");
        if (responsavel.isCsa()) {
            corpoBuilder.append(" org.orgNome as ent_nome, ");
        } else {
            corpoBuilder.append(" csa.csaNome as ent_nome, ");
        }
        corpoBuilder.append(" ade.adeNumero as ade_numero, ");
        corpoBuilder.append(" ade.adeVlr as ade_vlr, ");
        corpoBuilder.append(" coalesce(str(ade.adePrazo), '").append(ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel)).append("') as ade_prazo, ");
        corpoBuilder.append(" coalesce(str(ade.adePrdPagas), '0') as ade_prd_pagas, ");
        corpoBuilder.append(" to_locale_datetime(ade.adeData) as ade_data, ");
        corpoBuilder.append(" to_period(ade.adeAnoMesIni) as ade_ano_mes_ini, ");
        corpoBuilder.append(" to_period(ade.adeAnoMesFim) as ade_ano_mes_fim, ");
        corpoBuilder.append(" sad.sadDescricao as sad_descricao, ");
        corpoBuilder.append(" csa.csaNome as csa_nome,");
        corpoBuilder.append(" cor.corNome as cor_nome,");
        corpoBuilder.append(" est.estNome as est_nome,");
        corpoBuilder.append(" org.orgNome as org_nome,");

        if (permiteCadIndice) {
            corpoBuilder.append("concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            corpoBuilder.append("       case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) as codigo_servico,");
            corpoBuilder.append("concat(concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            corpoBuilder.append("       ' - '),concat(svc.svcDescricao, case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) as servico,");
            corpoBuilder.append("svc.svcDescricao as svc_descricao,");
        } else {
            corpoBuilder.append("concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) as codigo_servico,");
            corpoBuilder.append("concat(concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), ' - '),concat(svc.svcDescricao,case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) as servico,");
            corpoBuilder.append("svc.svcDescricao as svc_descricao,");
        }

        corpoBuilder.append(" concatenar(concatenar(concatenar(concatenar(rse.rseMatricula, ' - '), ser.serNome), ' - '), ser.serCpf) as servidor, ");
        corpoBuilder.append(" ser.serCpf as cpf, ");
        corpoBuilder.append(" rse.rseMatricula as matricula, ");
        corpoBuilder.append(" ser.serNome as nome, ");
        corpoBuilder.append(" srs.srsDescricao as srs_descricao, ");

        // Campos decisão
        corpoBuilder.append(" tju.tjuDescricao as tju_descricao, ");
        corpoBuilder.append(" concatenar(concatenar(cid.cidNome, '/'), cid.uf.ufNome) as comarca, ");
        corpoBuilder.append(" dju.djuNumProcesso as dju_num_processo, ");
        corpoBuilder.append(" to_locale_date(dju.djuData) as dju_data, ");
        corpoBuilder.append(" substituir(substituir(substituir(text_to_string(dju.djuTexto),'</B>',''),'<B>',''),'<BR>',' ') as observacao ");

        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append(" inner join oca.decisaoJudicialSet dju ");
        corpoBuilder.append(" inner join dju.tipoJustica tju ");
        corpoBuilder.append(" inner join dju.cidade cid ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");

        if (!TextHelper.isNull(corCodigo) || (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
            corpoBuilder.append(" inner join ade.correspondente cor");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor");
        }

        corpoBuilder.append(" WHERE dju.djuData BETWEEN :dataIni AND :dataFim");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            corpoBuilder.append(" AND srs.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        if (!TextHelper.isNull(tipoJustica)) {
            corpoBuilder.append(" AND tju.tjuCodigo ").append(criaClausulaNomeada("tipoJustica", tipoJustica));
        }

        if (!TextHelper.isNull(estadoJustica)) {
            corpoBuilder.append(" AND cid.uf.ufCod ").append(criaClausulaNomeada("estadoJustica", estadoJustica));
        }

        if (!TextHelper.isNull(comarcaJustica)) {
            corpoBuilder.append(" AND cid.cidCodigo ").append(criaClausulaNomeada("comarcaJustica", comarcaJustica));
        }

        if (!TextHelper.isNull(numeroProcessoJustica)) {
            corpoBuilder.append(" AND dju.djuNumProcesso ").append(criaClausulaNomeada("numeroProcessoJustica", numeroProcessoJustica));
        }

        corpoBuilder.append(" order by ");
        if (responsavel.isCsa()) {
            corpoBuilder.append(" org.orgNome, ");
        } else {
            corpoBuilder.append(" csa.csaNome, ");
        }
        corpoBuilder.append(" dju.djuData, ser.serNome asc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (!TextHelper.isNull(tipoJustica)) {
            defineValorClausulaNomeada("tipoJustica", tipoJustica, query);
        }

        if (!TextHelper.isNull(estadoJustica)) {
            defineValorClausulaNomeada("estadoJustica", estadoJustica, query);
        }

        if (!TextHelper.isNull(comarcaJustica)) {
            defineValorClausulaNomeada("comarcaJustica", comarcaJustica, query);
        }

        if (!TextHelper.isNull(numeroProcessoJustica)) {
            defineValorClausulaNomeada("numeroProcessoJustica", numeroProcessoJustica, query);
        }

        return query;
    }

}
