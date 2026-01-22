package com.zetra.econsig.persistence.query.relatorio;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioOcorrenciaAutorizacaoQuery</p>
 * <p>Description: Query Relatório de Ocorrência Autorização</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaAutorizacaoQuery extends ReportHQuery {

    public String dataIni;
    public String dataFim;
    public List<String> orgCodigos;
    public String serCpf;
    public String rseMatricula;
    public String usuLogin;
    public String csaCodigo;
    public String corCodigo;
    public List<String> tocCodigos;
    public List<String> tmoCodigos;
    public List<String> svcCodigo;
    public List<String> sadCodigos;
    public List<String> origemAdes;
    public List<String> motivoTerminoAdes;
    public List<String> srsCodigos;
    public String agrupamento;
    public AcessoSistema responsavel;
    private Date dataPeriodo;
    public boolean cse;
    public boolean org;
    public boolean csa;
    public boolean cor;
    public boolean ser;
    public boolean sup;


    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        dataPeriodo = (Date) criterio.getAttribute("DATA_PERIODO");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
        rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
        usuLogin = (String) criterio.getAttribute(Columns.USU_LOGIN);
        tocCodigos = (List<String>) criterio.getAttribute(Columns.TOC_CODIGO);
        tmoCodigos = (List<String>) criterio.getAttribute(Columns.TMO_CODIGO);
        svcCodigo = (List<String>) criterio.getAttribute(Columns.SVC_CODIGO);
        agrupamento = (String) criterio.getAttribute("AGRUPAMENTO");
        sadCodigos = (List<String>) criterio.getAttribute(Columns.SAD_CODIGO);
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
        motivoTerminoAdes = (List<String>) criterio.getAttribute("TERMINO_ADE");
        responsavel = (AcessoSistema) criterio.getAttribute("RESPONSAVEL");
        cse = (boolean) criterio.getAttribute("cse");
        org = (boolean) criterio.getAttribute("org");
        csa = (boolean) criterio.getAttribute("csa");
        cor = (boolean) criterio.getAttribute("cor");
        ser = (boolean) criterio.getAttribute("ser");
        sup = (boolean) criterio.getAttribute("sup");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean temStatus = (srsCodigos != null && srsCodigos.size() > 0);

        String stuExcluido = CodedValues.STU_EXCLUIDO;

        String tntControleRenegociacao = CodedValues.TNT_CONTROLE_RENEGOCIACAO;
        String tntControleCompra = CodedValues.TNT_CONTROLE_COMPRA;

        List<String> tntCodigos = new ArrayList<String>();
        tntCodigos.add(tntControleRenegociacao);
        tntCodigos.add(tntControleCompra);

        List<String> sadConcluidoCancelado = new ArrayList<String>();
        sadConcluidoCancelado.add(CodedValues.SAD_CONCLUIDO);
        sadConcluidoCancelado.add(CodedValues.SAD_CANCELADA);

        String sadLiquidada = CodedValues.SAD_LIQUIDADA;
        String sadConcluido = CodedValues.SAD_CONCLUIDO;
        String sadCancelada = CodedValues.SAD_CANCELADA;

        List<String> sadNaoConcluido = new ArrayList<String>();
        sadNaoConcluido.add(CodedValues.NOT_EQUAL_KEY);
        sadNaoConcluido.add(CodedValues.SAD_CONCLUIDO);

        List<String> sadNaoCancelada = new ArrayList<String>();
        sadNaoCancelada.add(CodedValues.NOT_EQUAL_KEY);
        sadNaoCancelada.add(CodedValues.SAD_CANCELADA);

        List<String> usuLogins = null;
        if (!TextHelper.isNull(usuLogin)) {
            String[] usuarios = TextHelper.split(usuLogin.replaceAll(" ", ""), ",");
            if (usuarios != null && usuarios.length > 0) {
                usuLogins = Arrays.asList(usuarios);
            }
        }

        String fields = " select " +
                " case " +
                "           when usu.statusLogin.stuCodigo " + criaClausulaNomeada("stuExcluido", stuExcluido) + " then coalesce(nullif(concat(usu.usuTipoBloq, '(*)'), ''), usu.usuLogin) " +
                "           else usu.usuLogin " +
                " end as usu_login, " +
                " concatenar(concatenar(concatenar(concatenar(rse.rseMatricula, ' - '), ser.serNome), ' - '), ser.serCpf) as servidor, " +
                " srs.srsDescricao as srs_descricao, " +
                " toc.tocDescricao as toc_descricao, " +
                " coalesce(str(ade.adePrdPagas), '0') as ade_prd_pagas, " +
                " ade.adeVlrLiquido as ade_vlr_liquido, " +
                " substituir(substituir(substituir(text_to_string(oca.ocaObs),'</B>',''),'<B>',''),'<BR>',' ') as observacao, " +
                " case when tmo.tmoDescricao is NULL then '' else tmo.tmoDescricao end as tmo_descricao, " +
                " to_locale_datetime(oca.ocaData) as oca_data, " +
                " ade.adeNumero as ade_numero, " +
                " svc.svcDescricao as svc_descricao, " +
                " sad.sadDescricao as sad_descricao, " +
                " ade.adeVlr as ade_vlr, " +
                " coalesce(str(ade.adePrazo), '" + ApplicationResourcesHelper.getMessage("rotulo.relatorio.consignacoes.indeterminado.abreviado", (AcessoSistema) null) + "') as ade_prazo, " +
                " ser.serCpf as cpf, " +
                " rse.rseMatricula as matricula, " +
                " usuarioCsa.csaCodigo as csa_codigo, " +
                " usuarioCse.cseCodigo as cse_codigo, " +
                " usuarioCor.corCodigo as cor_codigo, " +
                " usuarioOrg.orgCodigo as org_codigo, " +
                " usuarioSer.serCodigo as ser_codigo, " +
                " usuarioSup.cseCodigo as sup_cse_codigo, " +
                " ser.serNome as nome, " +
                " oca.ocaIpAcesso as oca_ip_acesso, ";

        if (!TextHelper.isNull(agrupamento) && agrupamento.equals("ORG")) {
            fields += " org.orgNome as ent_nome " ;
        } else {
            fields += " csa.csaNome as ent_nome " ;
        }

        StringBuilder corpoBuilder = new StringBuilder(fields);

        corpoBuilder.append(" from OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" inner join oca.tipoOcorrencia toc ");
        corpoBuilder.append(" inner join oca.usuario usu ");
        corpoBuilder.append(" inner join oca.autDesconto ade ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" inner join ade.correspondente cor ");
        }

        corpoBuilder.append(" left outer join oca.tipoMotivoOperacao tmo ");

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

        filtroOrigemAde(corpoBuilder, tntControleRenegociacao, tntControleCompra, tntCodigos);
        filtroTerminoAde(corpoBuilder, tntControleRenegociacao, tntControleCompra, tntCodigos, sadConcluidoCancelado, sadLiquidada, sadConcluido, sadCancelada, sadNaoConcluido, sadNaoCancelada);

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (usuLogins != null && !usuLogins.isEmpty()) {
            corpoBuilder.append(" and (usu.usuLogin ").append(criaClausulaNomeada("usuLogins", usuLogins));
            corpoBuilder.append(" or (usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuExcluido", stuExcluido));
            corpoBuilder.append(" and usu.usuTipoBloq ").append(criaClausulaNomeada("usuLogins", usuLogins)).append(")) ");
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            corpoBuilder.append(" and tmo.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigos));
        }

        if(svcCodigo != null && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (responsavel != null && responsavel.isCsa()) {
            corpoBuilder.append(" AND (toc.tocCodigo <> '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO)
                        .append("' OR (toc.tocCodigo = '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO).append("' AND ");
            corpoBuilder.append(" usuarioCsa.consignataria.csaCodigo = :csaCodigoUsuario)) ");
        }
        
        if (!cse || !org || !csa || !cor || !ser || !sup) {
            corpoBuilder.append(" and ( 1 = 2 ");
            if (csa) {
                corpoBuilder.append(" or usuarioCsa.csaCodigo is not null");
            }
            if (cor) {
                corpoBuilder.append(" or usuarioCor.corCodigo is not null");
            }
            if (ser) {
                corpoBuilder.append(" or usuarioSer.serCodigo is not null");
            }
            if (cse) {
                corpoBuilder.append(" or usuarioCse.cseCodigo is not null");
            }
            if (sup) {
                corpoBuilder.append(" or usuarioSup.cseCodigo is not null");
            }
            if (org) {
                corpoBuilder.append(" or usuarioOrg.orgCodigo is not null");
            }
            corpoBuilder.append(" )");
        }

        if (!TextHelper.isNull(agrupamento) && agrupamento.equals("ORG")) {
            corpoBuilder.append(" order by org.orgNome, oca.ocaData desc ");
        } else {
            corpoBuilder.append(" order by csa.csaNome, oca.ocaData desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("stuExcluido", stuExcluido, query);
        
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

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigos, query);
        }

        if (svcCodigo != null && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (usuLogins != null && !usuLogins.isEmpty()) {
            defineValorClausulaNomeada("usuLogins", usuLogins, query);
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
        // Somente uma das opções pode existir na query
        if (query.getQueryString().contains("sadConcluidoCancelado")) {
            defineValorClausulaNomeada("sadConcluidoCancelado", sadConcluidoCancelado, query);
        } else if (query.getQueryString().contains("sadConcluido")) {
            defineValorClausulaNomeada("sadConcluido", sadConcluido, query);
        }
        if (query.getQueryString().contains("sadLiquidada")) {
            defineValorClausulaNomeada("sadLiquidada", sadLiquidada, query);
        }
        if (query.getQueryString().contains("sadCancelada")) {
            defineValorClausulaNomeada("sadCancelada", sadCancelada, query);
        }
        if (query.getQueryString().contains("sadNaoConcluido")) {
            defineValorClausulaNomeada("sadNaoConcluido", sadNaoConcluido, query);
        }
        if (query.getQueryString().contains("sadNaoCancelada")) {
            defineValorClausulaNomeada("sadNaoCancelada", sadNaoCancelada, query);
        }
        if (responsavel != null && responsavel.isCsa()) {
            defineValorClausulaNomeada("csaCodigoUsuario", responsavel.getCsaCodigo(), query);
        }

        return query;
    }

    protected void filtroOrigemAde(StringBuilder corpoBuilder, String tntControleRenegociacao, String tntControleCompra, List<String> tntCodigos) {
        if (origemAdes != null && origemAdes.size() > 0) {
            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && !origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                if (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else {
                    corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleRenegociacao", tntControleRenegociacao));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                }
            }

            if (origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA) && (!origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)
                    && origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA))) {
                corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleCompra", tntControleCompra));
                corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
            }

            if (!origemAdes.contains(CodedValues.ORIGEM_ADE_NOVA)) {
                if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA) && origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleRenegociacao", tntControleRenegociacao));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                } else if (origemAdes.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {
                    corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleCompra", tntControleCompra));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoDestino.adeCodigo = ade.adeCodigo))");
                }
            }
        }
    }

    /**
     * filtra os contratos liquidados de acordo com a natureza do encerramento (se foi renegociação, venda, conclusão, cancelamento ou
     * liquidação antecipada).
     * @param corpoBuilder
     */
    protected void filtroTerminoAde(StringBuilder corpoBuilder, String tntControleRenegociacao, String tntControleCompra, List<String> tntCodigos, List<String> sadConcluidoCancelado, String sadLiquidada, String sadConcluido, String sadCancelada, List<String> sadNaoConcluido, List<String> sadNaoCancelada) {
        boolean usouRelacionamento = false;

        if (motivoTerminoAdes != null && motivoTerminoAdes.size() > 0) {
            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) && !motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA)) {
                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else {
                    corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleRenegociacao", tntControleRenegociacao));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                }

                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CONCLUSAO)) {
                    corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadNaoConcluido", sadNaoConcluido));
                }

                if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                    corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadNaoCancelada", sadNaoCancelada));
                }
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) && (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)
                    && motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA))) {
                corpoBuilder.append(" AND ((NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleCompra", tntControleCompra));
                corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                usouRelacionamento = true;
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA) &&
                    motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA) &&
                    motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                corpoBuilder.append(" AND (sad.sadCodigo ").append(criaClausulaNomeada("sadLiquidada", sadLiquidada));
                usouRelacionamento = true;
            }

            if (!motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA)) {
                if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA) && motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_RENEGOCIADA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleRenegociacao", tntControleRenegociacao));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_VENDA)) {
                    corpoBuilder.append(" AND ((EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
                    corpoBuilder.append(" where rad.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntControleCompra", tntControleCompra));
                    corpoBuilder.append(" AND rad.autDescontoByAdeCodigoOrigem.adeCodigo = ade.adeCodigo))");

                    usouRelacionamento = true;
                }
            }

            if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CONCLUSAO)) {
                if (usouRelacionamento) {
                    corpoBuilder.append(" OR ");
                } else {
                    corpoBuilder.append(" AND ");
                }

                if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                    corpoBuilder.append(" sad.sadCodigo ").append(criaClausulaNomeada("sadConcluidoCancelado", sadConcluidoCancelado));
                } else {
                    corpoBuilder.append(" sad.sadCodigo ").append(criaClausulaNomeada("sadConcluido", sadConcluido));
                }
            } else if (motivoTerminoAdes.contains(CodedValues.TERMINO_ADE_CANCELADA)) {
                if (usouRelacionamento) {
                    corpoBuilder.append(" OR ");
                } else {
                    corpoBuilder.append(" AND ");
                }
                corpoBuilder.append(" sad.sadCodigo ").append(criaClausulaNomeada("sadCancelada", sadCancelada));
            }

            if (usouRelacionamento) {
                corpoBuilder.append(")");
            }
        }
    }
    
    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.USU_LOGIN,
                "servidor",
                Columns.SRS_DESCRICAO,
                Columns.TOC_DESCRICAO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_VLR_LIQUIDO,
                "observacao",
                Columns.TMO_DESCRICAO,
                Columns.OCA_DATA,
                Columns.ADE_NUMERO,
                Columns.SVC_DESCRICAO,
                Columns.SAD_DESCRICAO,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                "cpf",
                "matricula",               
                Columns.CSA_CODIGO,
                Columns.CSE_CODIGO,
                Columns.COR_CODIGO,
                Columns.ORG_CODIGO,
                Columns.SER_CODIGO,
                "sup_cse_codigo",
                "nome",
                Columns.OUS_IP_ACESSO,
                Columns.EST_NOME
        };
    }
}
