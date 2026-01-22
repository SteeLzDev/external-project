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
 * <p>Title: RelatorioHistoricoDescontosSerQuery</p>
 * <p>Description: Query Relatório de Histórico de Descontos de Servidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioHistoricoDescontosSerQuery extends ReportHQuery {

    public String serCpf;
    public String rseMatricula;
    public List<Long> adeNumeroLista;
    public String csaCodigo;
    public String corCodigo;
    public String cnvCodVerba;
    public List<String> sadCodigos;
    public List<String> svcCodigo;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
        responsavel = (AcessoSistema) criterio.getAttribute("RESPONSAVEL");
        if (responsavel.isSer()){
            rseMatricula = responsavel.getRseMatricula();
        }else{
            rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
        }
        adeNumeroLista = (List<Long>) criterio.getAttribute("ADE_NUMERO_LIST");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
        cnvCodVerba = (String) criterio.getAttribute(Columns.CNV_COD_VERBA);
        sadCodigos = (List<String>) criterio.getAttribute("SAD_CODIGO");
        svcCodigo = (List<String>) criterio.getAttribute("SVC_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean permiteCadIndice = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel);
        String stuExcluido = CodedValues.STU_EXCLUIDO;

        StringBuilder fields = new StringBuilder();
        fields.append(" ade.adeNumero as ade_numero, ");
        fields.append(" to_locale_datetime(ade.adeData) as ade_data,");
        fields.append(" ade.adeVlr as ade_vlr, ");
        fields.append(" coalesce(str(ade.adePrazo), '"+ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel)+"') as ade_prazo, ");
        fields.append(" coalesce(str(ade.adePrdPagas), '0') as ade_prd_pagas, ");
        fields.append(" sad.sadDescricao as sad_descricao, ");
        fields.append(" csa.csaNome as csa_nome,");
        fields.append(" spd.spdDescricao as spd_descricao,");

        if (permiteCadIndice) {
            fields.append("concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            fields.append("       case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) as codigo_servico,");
            fields.append("concat(concat(coalesce(concat(cnv.cnvCodVerba, coalesce(ade.adeIndice,'')),svc.svcIdentificador),");
            fields.append("       ' - '),concat(svc.svcDescricao, case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) as servico,");
            fields.append("svc.svcDescricao as svc_descricao,");
        } else {
            fields.append("concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), case when coalesce(ade.adeCodReg,'6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end) as codigo_servico,");
            fields.append("concat(concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), ' - '),concat(svc.svcDescricao,case when coalesce(ade.adeCodReg, '6') = '4' then ' - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel)).append("' else '' end)) as servico,");
            fields.append("svc.svcDescricao as svc_descricao,");
        }

        fields.append(" concatenar(concatenar(concatenar(concatenar(rse.rseMatricula, ' - '), ser.serNome), ' - '), ser.serCpf) as servidor, ");
        fields.append(" ser.serCpf as cpf, ");
        fields.append(" rse.rseMatricula as matricula, ");
        fields.append(" ser.serNome as nome, ");
        fields.append(" ocp.ocpCodigo as ocp_codigo, ");
        fields.append(" toc.tocDescricao as toc_descricao, ");
        fields.append(" prd.prdNumero as prd_numero, ");
        fields.append(" to_month_year(prd.prdDataDesconto) as prd_data_desconto, ");
        fields.append(" prd.prdVlrPrevisto as prd_vlr_previsto, ");
        fields.append(" prd.prdVlrRealizado as prd_vlr_realizado, ");
        fields.append(" to_locale_date(ocp.ocpData) as ocp_data, ");
        fields.append(" substituir(substituir(substituir(text_to_string(ocp.ocpObs),'</B>',''),'<B>',''),'<BR>',' ') as observacao, ");
        fields.append(" usuarioCsa.csaCodigo as csa_codigo, ");
        fields.append(" usuarioCse.cseCodigo as cse_codigo, ");
        fields.append(" usuarioCor.corCodigo as cor_codigo, ");
        fields.append(" usuarioOrg.orgCodigo as org_codigo, ");
        fields.append(" usuarioSer.serCodigo as ser_codigo, ");
        fields.append(" usuarioSup.cseCodigo as sup_cse_codigo, ");
        fields.append(" case ");
        fields.append("           when usu.statusLogin.stuCodigo " + criaClausulaNomeada("stuExcluido", stuExcluido) + " then coalesce(nullif(concat(usu.usuTipoBloq, '(*)'), ''), usu.usuLogin) ");
        fields.append("           else usu.usuLogin ");
        fields.append(" end as usu_login ");

        StringBuilder corpoBuilder = new StringBuilder("select");
        corpoBuilder.append(fields);
        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");

        if (!TextHelper.isNull(corCodigo) || (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
            corpoBuilder.append(" inner join ade.correspondente cor");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor");
        }

        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join ade.parcelaDescontoSet prd ");
        corpoBuilder.append(" inner join prd.statusParcelaDesconto spd ");
        corpoBuilder.append(" inner join prd.ocorrenciaParcelaSet ocp ");
        corpoBuilder.append(" inner join ocp.tipoOcorrencia toc ");
        corpoBuilder.append(" inner join ocp.usuario usu ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" and cnv.cnvCodVerba").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if(svcCodigo != null && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if(adeNumeroLista != null && !adeNumeroLista.isEmpty()) {
            corpoBuilder.append(" and ade.adeNumero ").append(criaClausulaNomeada("adeNumeroLista", adeNumeroLista));
        }

        corpoBuilder.append(" order by ade.adeNumero, prd.prdDataDesconto asc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("stuExcluido", stuExcluido, query);

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (svcCodigo != null && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (adeNumeroLista != null && !adeNumeroLista.isEmpty()) {
            defineValorClausulaNomeada("adeNumeroLista", adeNumeroLista, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        return query;
    }
}
