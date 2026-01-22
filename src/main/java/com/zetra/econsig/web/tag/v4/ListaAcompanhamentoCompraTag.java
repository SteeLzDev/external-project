package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.compra.MontaCriterioAcompanhamentoCompra;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ListaAcompanhamentoCompraTag</p>
 * <p>Description: Tag para listagem de contratos no acompanhamento de compra no leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAcompanhamentoCompraTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaAcompanhamentoCompraTag.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private CalendarioController calendarioControler;

    private String csaCodigo;

    private String orgCodigo;

    private String corCodigo;

    private String pesquisar;

    private String filtroConfiguravel;

    private CustomTransferObject criteriosSelecionadosPesquisa;

    private String link = "../v3/acompanharPortabilidade?acao=iniciar";

    private String reusePageToken;

    private String linkPaginacao;

    private int offset;

    public String getLinkPaginacao() {
        return linkPaginacao;
    }

    public void setLinkPaginacao(String linkPaginacao) {
        this.linkPaginacao = linkPaginacao;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.jsp.tagext.Tag#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            imprimeHTMLTag();
        } catch (IOException | ServletException ex) {
            throw new JspException(ex);
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    /**
     * Imprime o HTML da tag.
     * @throws IOException
     * @throws ServletException
     */
    private void imprimeHTMLTag() throws IOException, ServletException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());

        // Faz a pesquisa dos contratos em situação de compra
        if (pesquisar != null && pesquisar.equals("true")) {
            // Gera o código das funções javascript
            String codigoJavascript = geraJavaScript(responsavel);
            pageContext.getOut().print(codigoJavascript);

            if (filtroConfiguravel != null && filtroConfiguravel.equals("0")) {
                CustomTransferObject criteriosPesquisa = criteriosSelecionadosPesquisa;
                imprimeGridListaAcompanhamento(criteriosPesquisa, "filtroConfiguravel", ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.resultado.pesquisa", responsavel), responsavel);
            } else if (filtroConfiguravel != null && filtroConfiguravel.equals("1")) {
                CustomTransferObject criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaInfoSaldoDevedor(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "infoSaldo", ApplicationResourcesHelper.getMessage("rotulo.pendencia.informacao.saldo.devedor", responsavel), responsavel);

                if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                    criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(criteriosSelecionadosPesquisa);
                    imprimeGridListaAcompanhamento(criteriosPesquisa, "aprovacaoSaldo", ApplicationResourcesHelper.getMessage("rotulo.pendencia.aprovacao.saldo.devedor", responsavel), responsavel);
                }

                criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaPagtoSaldoDevedor(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "pagtoSaldo", ApplicationResourcesHelper.getMessage("rotulo.pendencia.pagamento.saldo.devedor", responsavel), responsavel);

                criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaLiquidacao(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "liquidacao", ApplicationResourcesHelper.getMessage("rotulo.pendencia.liquidacao.contrato", responsavel), responsavel);

            } else if (filtroConfiguravel != null && filtroConfiguravel.equals("2")) {
                CustomTransferObject criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioInfoSaldoDevedor(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "bloqueioInfoSaldo", ApplicationResourcesHelper.getMessage("rotulo.bloqueio.nao.informacao.saldo.devedor", responsavel), responsavel);

                if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                    criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioAprovacaoSaldoDevedor(criteriosSelecionadosPesquisa);
                    imprimeGridListaAcompanhamento(criteriosPesquisa, "bloqueioAprovacaoSaldo", ApplicationResourcesHelper.getMessage("rotulo.bloqueio.nao.aprovacao.saldo.devedor", responsavel), responsavel);
                }

                criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioPagtoSaldoDevedor(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "bloqueioPagtoSaldo", ApplicationResourcesHelper.getMessage("rotulo.bloqueio.nao.pagamento.saldo.devedor", responsavel), responsavel);

                criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioLiquidacao(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "bloqueioLiquidacao", ApplicationResourcesHelper.getMessage("rotulo.bloqueio.nao.liquidacao.contrato", responsavel), responsavel);

            } else if (filtroConfiguravel != null && filtroConfiguravel.equals("3") && responsavel.isSer()) {
                CustomTransferObject criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(criteriosSelecionadosPesquisa);
                imprimeGridListaAcompanhamento(criteriosPesquisa, "aprovacaoSaldo", ApplicationResourcesHelper.getMessage("rotulo.pendencia.aprovacao.saldo.devedor", responsavel), responsavel);
            }
        }
    }

    /**
     * Imprime o HTML correspondente à lista de contratos.
     * @param criteriosPesquisa Critérios para a pesquisa de contratos
     * @param idGrid Identificador do grid para tratamento de paginação
     * @param tituloGrid Título do grid de exibição do resultado
     * @param responsavel
     * @throws IOException
     * @throws ServletException
     */
    private void imprimeGridListaAcompanhamento(CustomTransferObject criteriosPesquisa, String idGrid, String tituloGrid, AcessoSistema responsavel) throws IOException, ServletException {
        boolean possuiEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean sistExibeHistLiqAntecipadas = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS, responsavel);
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        // Monta lista de parâmetros para serem concatenados na URL de
        // include da barra de navegação. Contém os parâmetros do formulário
        String name = null;
        Iterator<String> itCriterios = criteriosPesquisa.getAtributos().keySet().iterator();
        String parametros = "$pesquisar(true|filtroConfiguravel(" + filtroConfiguravel + "|indice(" + idGrid;
        while (itCriterios.hasNext()) {
            name = itCriterios.next();
            parametros += "|" + name + "(" + (criteriosPesquisa.getAttribute(name) != null ? criteriosPesquisa.getAttribute(name).toString() : "");
        }
        if (responsavel.isCseSup()) {
            parametros += "|CSA_CODIGO(" + csaCodigo;
        }
        // Reformata os parâmetros para serem utilizados nas funções de javascript
        parametros = parametros.replace('$', '?').replace('|', '&').replace('(', '=');

        List<TransferObject> listaContratos = null;
        int qtdPorPagina = JspHelper.LIMITE;

        try {
            // Reformata os parâmetros para serem utilizados nas funções de javascript
            parametros = parametros.replace('$', '?').replace('|', '&').replace('(', '=');

            listaContratos = pesquisarConsignacaoController.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, corCodigo, orgCodigo, responsavel);
            linkPaginacao += parametros.replace("$", "|");
            linkPaginacao += "&total" + idGrid + "=" + listaContratos.size();
            linkPaginacao += "&indice=" + idGrid;
            if (((HttpServletRequest) pageContext.getRequest()).getQueryString() != null && !((HttpServletRequest) pageContext.getRequest()).getQueryString().equals("")) {
                linkPaginacao += "&" + ((HttpServletRequest) pageContext.getRequest()).getQueryString();
            }

        } catch (Exception ex) {
            pageContext.getSession().setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        if (listaContratos == null) {
            return;
        }
        String tituloPaginacao = ApplicationResourcesHelper.getMessage("rotulo.paginacao.acompanhamento.compra", responsavel);

        // Início da geração do HTML de detalhe de compra
        StringBuilder html = new StringBuilder();

        html.append("<div class=\"card\">\n");
        html.append("  <div class=\"card-header\">");
        html.append("    <h2 class=\"card-header-title\">" + tituloGrid + "</h2>");
        html.append("  </div>");
        if (responsavel.isSer()) {
            html.append("<div class=\"card-body pb-0 mb-0\">");
            html.append("  <div class='alert alert-warning mb-0' role='alert'>");
            html.append("    <p class=\"mb-0\">").append(ApplicationResourcesHelper.getMessage("mensagem.concorde.saldo.devedor.processo.compra.v4", responsavel)).append("</p>");
            html.append("    <p class=\"mb-0\">").append(ApplicationResourcesHelper.getMessage("mensagem.nao.concorde.saldo.devedor.processo.compra.v4", responsavel)).append("</p>");
            html.append("    <p class=\"mb-0\">").append(ApplicationResourcesHelper.getMessage("mensagem.nao.concorde.saldo.devedor.cancelar.processo.compra.v4", responsavel)).append("</p>");
            html.append("  </div>");
            html.append("</div>");
        }
        html.append("  <div class=\"card-body table-responsive p-0\">");
        html.append("    <table class=\"table table-striped table-hover \">\n");

        if (!TextHelper.isNull(linkPaginacao)) {
            int total = listaContratos.size();
            linkPaginacao += "&indice=2&total2=" + total;
        }
        html.append("      <thead>\n");
        html.append("        <tr>\n");

        String origem = (String) criteriosPesquisa.getAttribute("origem");
        if (TextHelper.isNull(csaCodigo) || (origem != null && origem.equals("1"))) {
            html.append("      <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.origem", responsavel)).append("</th>\n");
        }
        if (TextHelper.isNull(csaCodigo) || (origem != null && origem.equals("0"))) {
            html.append("      <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.destino", responsavel)).append("</th>\n");
        }
        html.append("          <th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.compulsorios.numero.ade", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.compulsorios.inclusao", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.compulsorios.valor.prestacao", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.compulsorios.numero.prestacao", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.data.compra.abreviado", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.data.abreviado", responsavel)).append("</th>\n");
        if (responsavel.isSer()) {
            html.append("      <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor.abreviado", responsavel)).append("</th>\n");
        } else {
            if (possuiEtapaAprovacaoSaldo) {
                html.append("  <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.data.aprovacao.abreviado", responsavel)).append("</th>\n");
            }
            html.append("      <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.data.pagamento.abreviado", responsavel)).append("</th>\n");
        }
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.proposta.vencimento", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.status", responsavel)).append("</th>\n");
        html.append("          <th scope=\"col\" width=\"1%\">").append(ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)).append("</th>\n");
        html.append("        </tr>\n");
        html.append("      </thead>\n");
        html.append("    <tbody>\n");

        CustomTransferObject ade = null;
        String adeCodigo = null;
        String sadCodigo = null;
        String stcDescricao = null;
        String adeNumero = null;
        String adePrazo = null;
        String adeData = null;
        String adeVlr = null;
        String adeTipoVlr = null;
        String servidor = null;
        String dataCompra = null;
        String dataInfSdv = null;
        String dataAprSdv = null;
        String dataPagSdv = null;
        String adeDest = null;
        String csaCodigoOrigem = null;
        String csaCodigoDestino = null;
        String consignatariaOrigem = null;
        String consignatariaDestino = null;
        String sdvValor = null;
        String destinatarioNotificacao = null;
        boolean exibeVencimento = false;
        boolean diasUteisNoControle = false;
        String statusCompra = null;
        String vencimento = null;
        Date vencimentoProxDiaUtil = null;

        try {
            offset = Integer.parseInt(pageContext.getRequest().getParameter("offset" + idGrid));
        } catch (Exception ex) {
        }
        if (listaContratos.size() > 0) {
            int i = 0;
            while (i < qtdPorPagina && offset + i < listaContratos.size()) {
                ade = (CustomTransferObject) listaContratos.get(offset + i);
                i++;
                ParamSvcTO paramSvcCse = null;

                try {
                    adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                    sadCodigo = ade.getAttribute(Columns.SAD_CODIGO).toString();
                    stcDescricao = ade.getAttribute(Columns.STC_DESCRICAO).toString();
                    adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                    adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado.singular", responsavel);
                    adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                    servidor = ade.getAttribute(Columns.RSE_MATRICULA) + " - " + ade.getAttribute(Columns.SER_NOME) + " - " + ade.getAttribute(Columns.SER_CPF);
                    dataCompra = ade.getAttribute(Columns.RAD_DATA) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    dataInfSdv = ade.getAttribute(Columns.RAD_DATA_INF_SALDO) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_INF_SALDO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    dataAprSdv = ade.getAttribute(Columns.RAD_DATA_APR_SALDO) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_APR_SALDO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    dataPagSdv = ade.getAttribute(Columns.RAD_DATA_PGT_SALDO) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_PGT_SALDO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    adeDest = ade.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO).toString();
                    consignatariaOrigem = !TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString();
                    consignatariaDestino = !TextHelper.isNull(ade.getAttribute("CSA_NOME_ABREV_DESTINO")) ? ade.getAttribute("CSA_NOME_ABREV_DESTINO").toString() : ade.getAttribute("CSA_NOME_DESTINO").toString();
                    csaCodigoOrigem = (String) ade.getAttribute("CSA_CODIGO_ORIGEM");
                    csaCodigoDestino = (String) ade.getAttribute("CSA_CODIGO_DESTINO");
                    destinatarioNotificacao = compraContratoController.emailDestinatarioMsgCsaDestinoPortabilidade(adeCodigo, csaCodigoOrigem, responsavel);
                    statusCompra = ade.getAttribute(Columns.STC_CODIGO) != null ? ade.getAttribute(Columns.STC_CODIGO).toString() : "0";


                    try {
                        // Parâmetros de serviço
                        paramSvcCse = parametroController.getParamSvcCseTO((String) ade.getAttribute("SVC_CODIGO_DESTINO"), responsavel);
                    } catch (ParametroControllerException ex) {
                        pageContext.getSession().setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        LOG.error(ex.getMessage(), ex);
                        return;
                    }


                    // Tratar Data de vencimento
                    diasUteisNoControle = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar cal = Calendar.getInstance();
                    Date date = dateFormat.parse(dataCompra);

                    String aguardeInfSld = !Objects.equals(paramSvcCse.getTpsDiasInfSaldoDvControleCompra(), "") ? paramSvcCse.getTpsDiasInfSaldoDvControleCompra() : CodedValues.PSC_BOOLEANO_NAO;
                    String aguardeInfPg = !Objects.equals(paramSvcCse.getTpsDiasInfPgtSaldoControleCompra(), "") ? paramSvcCse.getTpsDiasInfPgtSaldoControleCompra() : CodedValues.PSC_BOOLEANO_NAO;
                    String aguardeLiqui = !Objects.equals(paramSvcCse.getTpsDiasLiquidacaoAdeControleCompra(), "") ? paramSvcCse.getTpsDiasLiquidacaoAdeControleCompra() : CodedValues.PSC_BOOLEANO_NAO;

                    if(diasUteisNoControle){
                        if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo())) {
                            if(aguardeInfSld.equals(CodedValues.PSC_BOOLEANO_NAO)) {
                                exibeVencimento = false;
                            } else {
                                vencimentoProxDiaUtil = calendarioControler.findProximoDiaUtil(date, Integer.parseInt(aguardeInfSld) -1);
                                cal.setTime(vencimentoProxDiaUtil);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        } else if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo())) {
                            if (aguardeInfPg.equals(CodedValues.PSC_BOOLEANO_NAO)){
                                exibeVencimento = false;
                            } else {
                                vencimentoProxDiaUtil = calendarioControler.findProximoDiaUtil(date, Integer.parseInt(aguardeInfPg) -1);
                                cal.setTime(vencimentoProxDiaUtil);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        } else if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo())) {
                            if(aguardeLiqui.equals(CodedValues.PSC_BOOLEANO_NAO)){
                                exibeVencimento = false;
                            } else {
                                vencimentoProxDiaUtil = calendarioControler.findProximoDiaUtil(date, Integer.parseInt(aguardeLiqui) -1);
                                cal.setTime(vencimentoProxDiaUtil);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        }
                    } else {
                        cal.setTime(date);

                        if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo())) {
                            if(aguardeInfSld.equals(CodedValues.PSC_BOOLEANO_NAO)) {
                                exibeVencimento = false;
                            } else {
                                cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(aguardeInfSld) - 1);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        } else if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo())) {
                            if (aguardeInfPg.equals(CodedValues.PSC_BOOLEANO_NAO)){
                                exibeVencimento = false;
                            } else {
                                cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(aguardeInfPg) - 1);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        } else if (statusCompra.equals(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo())) {
                            if(aguardeLiqui.equals(CodedValues.PSC_BOOLEANO_NAO)){
                                exibeVencimento = false;
                            } else {
                                cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(aguardeLiqui) - 1);
                                vencimento = dateFormat.format(cal.getTime());
                                exibeVencimento = true;
                            }
                        }
                    }

                    adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                    if (!adeVlr.equals("")) {
                        adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                        adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
                    }

                    sdvValor = ade.getAttribute(Columns.SDV_VALOR) != null ? ade.getAttribute(Columns.SDV_VALOR).toString() : "";
                    if (!sdvValor.equals("")) {
                        sdvValor = NumberHelper.format(Double.valueOf(sdvValor).doubleValue(), NumberHelper.getLang());
                    }
                } catch (ParseException | CompraContratoControllerException ex) {
                    pageContext.getSession().setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                    return;
                } catch (CalendarioControllerException e) {
                    throw new RuntimeException(e);
                }

                html.append("        <tr>\n");

                if (TextHelper.isNull(csaCodigo) || (origem != null && origem.equals("1"))) {
                    html.append("<td>").append(TextHelper.forHtmlContent(consignatariaOrigem)).append("</td>\n");
                }
                if (TextHelper.isNull(csaCodigo) || (origem != null && origem.equals("0"))) {
                    html.append("<td>").append(TextHelper.forHtmlContent(consignatariaDestino)).append("</td>\n");
                }
                html.append("<td>").append(TextHelper.forHtmlContent(servidor)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(adeNumero)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(adeData)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr) + " " + adeVlr)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(adePrazo)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(dataCompra)).append("</td>\n");
                html.append("<td>").append(TextHelper.forHtmlContent(dataInfSdv)).append("</td>\n");

                if (responsavel.isSer()) {
                    html.append("<td>").append(TextHelper.forHtmlContent("R$ " + sdvValor)).append("</td>\n");
                } else {
                    if (possuiEtapaAprovacaoSaldo) {
                        html.append("<td>").append(TextHelper.forHtmlContent(dataAprSdv)).append("</td>\n");
                    }
                    html.append("<td>").append(TextHelper.forHtmlContent(dataPagSdv)).append("</td>\n");
                }
                if (exibeVencimento) {
                    if (diasUteisNoControle){
                        html.append("<td>").append(TextHelper.forHtmlContent(vencimento)).append("</td>\n");
                    } else {
                        html.append("<td>").append(TextHelper.forHtmlContent(vencimento)).append("</td>\n");
                    }
                } else {
                    html.append("<td>").append(TextHelper.forHtmlContent("-")).append("</td>\n");
                }
                html.append("<td>").append(TextHelper.forHtmlContent(stcDescricao)).append("</td>\n");
                html.append("  <td>");
                html.append("    <div class=\"actions\">");
                html.append("      <div class=\"dropdown\">");
                html.append("        <a class=\"dropdown-toggle ico-action\" href=\"#\" role=\"button\" id=\"userMenu\" data-bs-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">");
                html.append("          <div class=\"form-inline\">");
                html.append("            <span class=\"mr-1\" data-bs-toggle=\"tooltip\" title=\"\" data-original-title=\"" + ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel) + "\" aria-label=\"" + ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel) + "\">");
                html.append("               <svg><use xlink:href=\"#i-engrenagem\"></use></svg>");
                html.append("            </span>" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel));
                html.append("          </div>");
                html.append("        </a>");
                html.append("        <div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"userMenu\">");

                if (responsavel.isCseSupOrg()) {
                    html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('e', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel) + "</a>");
                    if (origem != null && origem.equals("0") && responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR)) {
                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('s', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.saldo.devedor", responsavel) + "</a>");
                    }
                } else if (responsavel.isSer()) {
                    html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('e', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel) + "</a>");
                    if (possuiEtapaAprovacaoSaldo && responsavel.temPermissao(CodedValues.FUN_APROVAR_SALDO_DEVEDOR)) {
                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('asd', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar.saldo.devedor", responsavel) + "</a>");
                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('rsd', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.rejeitar.saldo.devedor", responsavel) + "</a>");
                    }
                    if (responsavel.temPermissao(CodedValues.FUN_CANC_COMPRA)) {
                        // Cancelamento deve ser feito pelo destino (adeDest)
                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('cc', '" + TextHelper.forJavaScriptAttribute(adeDest) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.compra", responsavel) + "</a>");
                    }

                } else {
                    // Determina se o sistema deve obrigar que o ciclo de vida do processo de compra seja seguido passo-a-passo,
                    // não podendo o saldo ser informado como pago antes de ter sido cadastrado, por exemplo.
                    boolean cicloVidaFixo = ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel);
                    boolean permiteLiquidarEmQualquerEtapa = false;
                    if (responsavel.isCseSupOrg()) {
                        permiteLiquidarEmQualquerEtapa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel);
                    } else if (responsavel.isCsaCor()) {
                        permiteLiquidarEmQualquerEtapa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSA_COR, CodedValues.TPC_SIM, responsavel);
                    }

                    // Verifica se possui saldo devedor informado e pago
                    boolean possuiInfSaldoDevedor = (ade.getAttribute(Columns.RAD_DATA_INF_SALDO) != null);
                    boolean possuiAprSaldoDevedor = (ade.getAttribute(Columns.RAD_DATA_APR_SALDO) != null);
                    boolean possuiPgtSaldoDevedor = (ade.getAttribute(Columns.RAD_DATA_PGT_SALDO) != null);

                    if (origem != null && origem.equals("0")) {
                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('e', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel) + "</a>");
                        if (responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR)) {
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else if (cicloVidaFixo && possuiPgtSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" src=\"../img/icones/saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else if (cicloVidaFixo && possuiEtapaAprovacaoSaldo && possuiAprSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.aprovado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.aprovado", responsavel)).append("\" src=\"../img/icones/saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('s', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.saldo.devedor", responsavel) + "</a>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_REJEITAR_PGT_SALDO_DEVEDOR)) {
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/rejeitar_pgt_saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else if (!possuiPgtSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.nao.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.nao.informado", responsavel)).append("\" src=\"../img/icones/rejeitar_pgt_saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('rp', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.rejeitar.pagamento.saldo.devedor", responsavel) + "</a>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO)) {
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/liquidar_contrato_des.gif\" border=\"0\"/></td>");
                            } else if (cicloVidaFixo && !permiteLiquidarEmQualquerEtapa && !possuiPgtSaldoDevedor && !csaCodigoOrigem.equals(csaCodigoDestino)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.nao.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.nao.informado", responsavel)).append("\" src=\"../img/icones/liquidar_contrato_des.gif\" border=\"0\"/></td>");
                            } else {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('l', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar", responsavel) + "</a>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_ENVIAR_MSG_PORTABILIDADE_CSA_COR) && responsavel.isCsaCor() && !TextHelper.isNull(destinatarioNotificacao)) {
                            html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('MSG_CSA_PORTABILIDADE', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("mensagem.acao.enviar.mensagem.csa.destino", responsavel) + "</a>");
                        }

                    } else {
                        html.append("<a  class=\"dropdown-item\" href=\"#no-back\" onClick=\"cntrTerceiro('" + TextHelper.forJavaScriptAttribute(adeCodigo) + "','" + TextHelper.forJavaScriptAttribute(adeDest) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel) + "</a>");
                        if (responsavel.temPermissao(CodedValues.FUN_ATUALIZAR_PROCESSO_COMPRA)) {
                            if (possuiPgtSaldoDevedor || sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" src=\"../img/icones/editar_des.gif\" border=\"0\"/></td>");
                            } else if (possuiEtapaAprovacaoSaldo && possuiAprSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.aprovado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.aprovado", responsavel)).append("\" src=\"../img/icones/editar_des.gif\" border=\"0\"/></td>");
                            } else if (possuiPgtSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" src=\"../img/icones/editar_des.gif\" border=\"0\"/></td>");
                            } else {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('at', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.atualizar.saldo.devedor", responsavel) + "</a>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_SOL_RECALCULO_SALDO_DEVEDOR)) {
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/consultar_saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else if (cicloVidaFixo && possuiPgtSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.saldo.devedor.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.pagamento.saldo.devedor.informado", responsavel)).append("\" src=\"../img/icones/consultar_saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else if (!possuiInfSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" src=\"../img/icones/consultar_saldo_devedor_des.gif\" border=\"0\"/></td>");
                            } else {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('sr', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.solicitar.recalculo.saldo.devedor", responsavel) + "</a>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_INFORMAR_PGT_SALDO_DEVEDOR)) {
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/pgt_saldo_devedor_des.gif\" border=\"0\"/></td>");
                                if (sistExibeHistLiqAntecipadas) {
                                    // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/historico_margem_des.gif\" border=\"0\"/></td>");
                                }
                            } else if (cicloVidaFixo && !possuiInfSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" src=\"../img/icones/pgt_saldo_devedor_des.gif\" border=\"0\"/></td>");
                                if (sistExibeHistLiqAntecipadas) {
                                    // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.informado", responsavel)).append("\" src=\"../img/icones/historico_margem_des.gif\" border=\"0\"/></td>");
                                }
                            } else if (cicloVidaFixo && possuiEtapaAprovacaoSaldo && !possuiAprSaldoDevedor) {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.aprovado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.aprovado", responsavel)).append("\" src=\"../img/icones/pgt_saldo_devedor_des.gif\" border=\"0\"/></td>");
                                if (sistExibeHistLiqAntecipadas) {
                                    // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.aprovado", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.nao.aprovado", responsavel)).append("\" src=\"../img/icones/historico_margem_des.gif\" border=\"0\"/></td>");
                                }
                            } else {
                                if (responsavel.temPermissao(CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO)) {
                                    html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('apc', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.informar.pagamento.saldo.devedor", responsavel) + "</a>");
                                } else {
                                    html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('i', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.informar.pagamento.saldo.devedor", responsavel) + "</a>");
                                }
                                if (sistExibeHistLiqAntecipadas) {
                                    int numAdeHistLiqAntecipadas = (paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas() != null && !paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas().equals("")) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) : 0;

                                    if (numAdeHistLiqAntecipadas > 0) {
                                        String paramHistLiq = "?RSE_MATRICULA=" + ade.getAttribute(Columns.RSE_MATRICULA) + "&RSE_CODIGO=" + ade.getAttribute(Columns.RSE_CODIGO) + "&acao=listarHistLiquidacoesAntecipadas&SVC_CODIGO=" + ade.getAttribute("SVC_CODIGO_DESTINO") + "&SER_NOME=" + TextHelper.encode64((String) ade.getAttribute(Columns.SER_NOME));
                                        html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('hla', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + TextHelper.forJavaScriptAttribute(paramHistLiq) + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar.historico.liquidacoes.antecipadas", responsavel) + "</a>");
                                    } else {
                                        // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.sem.historico.liquidacoes.antecipadas", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.sem.historico.liquidacoes.antecipadas", responsavel)).append("\" src=\"../img/icones/historico_margem_des.gif\" border=\"0\"/></td>");
                                    }
                                }
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_RETIRAR_CONTRATO_COMPRA)) {
                            if (!sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('rcc', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.retirar.contrato.compra", responsavel) + "</a>");
                            } else {
                                // html.append("<td align=\"center\"><img alt=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.consignacao.liquidada", responsavel)).append("\" src=\"../img/icones/retirar_contrato_compra_des.png\" border=\"0\"/></td>");
                            }
                        }

                        if (responsavel.temPermissao(CodedValues.FUN_ENVIAR_MSG_PORTABILIDADE_CSA_COR) && responsavel.isCsaCor() && !TextHelper.isNull(destinatarioNotificacao)) {
                            html.append("<a class=\"dropdown-item\" href=\"#no-back\" onClick=\"doIt('MSG_CSA_PORTABILIDADE', '" + TextHelper.forJavaScriptAttribute(adeCodigo) + "', '" + parametros + "');\">" + ApplicationResourcesHelper.getMessage("mensagem.acao.enviar.mensagem.csa.destino", responsavel) + "</a>");
                        }
                    }
                }

                html.append("        </div>");
                html.append("      </div>");
                html.append("    </div>");
                html.append("  </td>");
                html.append("</tr>");
            }
        }
        // Controles de paginação
        int totalRegistros = listaContratos.size();
        int qtdPagina = (totalRegistros / qtdPorPagina) + ((totalRegistros % qtdPorPagina) == 0 ? 0 : 1);
        int first = (totalRegistros > 0) ? offset + 1 : 0;
        int last = Math.min(offset + qtdPorPagina, totalRegistros);
        int paginaAtual = (last / qtdPorPagina) + ((last % qtdPorPagina) == 0 ? 0 : 1);

        html.append("      </tbody>\n");
        html.append("      <tfoot>\n");
        html.append("        <tr>\n");
        html.append("          <td colspan=\"5\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.acompanhamento.compra", responsavel));
        html.append("          <span class=\"font-italic\"> - ");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.registros.sem.estilo", responsavel, String.valueOf(first), String.valueOf(last), String.valueOf(totalRegistros)));
        html.append("          </span></td>\n");
        html.append("        </tr>\n");
        html.append("      </tfoot>\n");
        html.append("    </table>\n");
        html.append("  </div>");
        html.append("</div>");

        html.append("  <div class=\"card-footer\">\n");
        html.append("    <nav aria-label=").append(TextHelper.forHtmlAttribute(tituloPaginacao)).append("\">\n");
        html.append("      <ul class=\"pagination justify-content-end\">\n");
        html.append("      <li class=\"page-item ").append((paginaAtual > 1) ? "" : "disabled").append("\">");
        html.append("      <a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&total=" + totalRegistros + "&offset2=0", request))).append("')\" aria-label='");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.anterior", responsavel)).append("'>«</a>");
        html.append("      </li>\n");
        if (paginaAtual - 10 > 1) {
            String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&total=" + totalRegistros + "&offset2=" + 0, request);
            html.append("  <li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">1</a></li>");
            html.append("  <li class=\"page-item disabled\"><a class=\"page-link\" href=\"#no-back\">...</a></li>\n");
        }
        for (int contador = Math.max(1, paginaAtual - 10); contador <= Math.min(qtdPagina, paginaAtual + 10); contador++) {
            String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&total=" + totalRegistros + "&offset2=" + ((contador - 1) * qtdPorPagina), request);
            if (contador == paginaAtual) {
                html.append("<li class=\"page-item active\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(contador);
                html.append("<span class=\"sr-only\"> (").append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.atual", responsavel)).append(")</span></a></li>\n");
            } else {
                html.append("<li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(contador).append("</a></li>\n");
            }
        }
        if (qtdPagina > paginaAtual + 10) {
            String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&total=" + totalRegistros + "&offset2=" + ((qtdPagina - 1) * qtdPorPagina), request);
            html.append("  <li class=\"page-item disabled\"><a class=\"page-link\" href=\"#no-back\">...</a></li>\n");
            html.append("  <li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(qtdPagina).append("</a></li>");
        }
        html.append("      <li class=\"page-item ").append((paginaAtual < qtdPagina) ? "" : "disabled").append("\">");
        html.append("      <a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&total=" + totalRegistros + "&offset2=" + (paginaAtual * qtdPorPagina), request))).append("')\" ");
        html.append("      aria-label='").append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.proxima", responsavel)).append("'>»</a>");
        html.append("      </li>\n");
        html.append("    </ul>\n");
        html.append("  </nav>\n");
        html.append("</div>\n");

        if (!responsavel.isSer()) {
            // Exibe o botão de detalhamento quando houver algum contrato.
            if (listaContratos.size() > 0) {
                html.append("<div class=\"btn-action\">");
                html.append("  <a class=\"btn btn-primary\" id=\"btnDetalhar\" href=\"#no-back\" onClick=\"doIt('d', '', '" + parametros + "');\"><svg width=\"20\"><use xlink:href=\"../img/sprite.svg#i-consultar\"></use></svg>" + ApplicationResourcesHelper.getMessage("rotulo.botao.detalhar", responsavel) + "</a>");
                html.append("</div>");
            }
        }
        pageContext.getOut().print(html.toString());
        html.setLength(0);

        html.append("<div class=\"modal fade\" id=\"confirmarMensagem\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"modalTitulo\" aria-hidden=\"true\" style=\"display: none;\">");
        html.append("  <div class=\"modal-dialog\" role=\"document\">");
        html.append("    <div class=\"modal-content\">");
        html.append("      <div class=\"modal-header\">");
        html.append("        <h5 class=\"modal-title about-title mb-0\" id=\"modalTitulo\">" + ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.observacao", responsavel) + "</h5>");
        html.append("        <button type=\"button\" class=\"logout mr-3\" data-bs-dismiss=\"modal\" aria-label='" + ApplicationResourcesHelper.getMessage("rotulo.botao.fechar", responsavel) + "'>");
        html.append("          <span aria-hidden=\"true\"></span>");
        html.append("        </button>");
        html.append("      </div>");
        html.append("      <div class=\"form-group modal-body m-0\">");
        html.append("        <span id=\"sde_msg\"></span>");
        html.append("      </div>");
        html.append("      <div class=\"form-group modal-body m-0\">");
        html.append("        <span>" + ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.informe.observacao", responsavel) + "</span>");
        html.append("      </div>");
        html.append("      <div class=\"form-group modal-body m-0\">");
        html.append("        <label for=\"editfield\">" + ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.observacao", responsavel) + "</label>");
        html.append("        <textarea class=\"form-control\" id=\"editfield\" name=\"editfield\" rows=\"3\" cols=\"28\"></textarea>");
        html.append("      </div>");
        html.append("      <div class=\"modal-footer pt-0\">");
        html.append("        <div class=\"btn-action mt-2 mb-0\">");
        html.append("          <a class=\"btn btn-outline-danger\" data-bs-dismiss=\"modal\" aria-label='" + ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel) + "' href=\"#\">" + ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel) + "</a>");
        html.append("          <input hidden=\"true\"  id=\"sde_url\" value=\"\">");
        html.append("          <input hidden=\"true\"  id=\"sde_param\" value=\"\">");
        html.append("          <input hidden=\"true\"  id=\"sde_msg\" value=\"\">");
        html.append("          <a class=\"btn btn-primary\" data-bs-dismiss=\"modal\" aria-label='" + ApplicationResourcesHelper.getMessage("rotulo.botao.confirmar", responsavel) + "' onclick=\"show_setObservacao();\" href=\"#\">" + ApplicationResourcesHelper.getMessage("rotulo.botao.confirmar", responsavel) + "</a>");
        html.append("        </div>");
        html.append("      </div>");
        html.append("    </div>");
        html.append("  </div>");
        html.append("</div>");
        pageContext.getOut().print(html.toString());
    }

    /**
     * Gera o javascript necessário para os botões da listagem.
     * @return
     */
    private String geraJavaScript(AcessoSistema responsavel) {

        // Verifica se o motivo da operação é exigido.
        boolean exigeMotivoOperacao = ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel);

        StringBuilder html = new StringBuilder();

        html.append("<script language=\"JavaScript\" type=\"text/JavaScript\">");
        html.append("function doIt(opt, ade, parametros) {");
        html.append("    var msg = '';");
        html.append("    var qs = parametros;");

        if (TextHelper.isNull(reusePageToken) || !reusePageToken.equalsIgnoreCase("true")) {
            SynchronizerToken.saveToken((HttpServletRequest) pageContext.getRequest());
        }
        html.append("    qs = qs + '&").append(SynchronizerToken.generateToken4URL((HttpServletRequest) pageContext.getRequest())).append("';");

        html.append("    if (ade != '') {");
        html.append("      qs += '&ADE_CODIGO=' + ade;");
        html.append("    }");

        html.append("    if (opt == 'e') {");
        if (responsavel.isSer()) {
            html.append("      qs += '&acao=detalharConsignacao';");
            html.append("      j = '../v3/consultarConsignacao';");
        } else {
            html.append("      qs += '&acao=detalharConsignacao';");
            html.append("      j = '../v3/acompanharPortabilidade';");
        }
        html.append("    } else if (opt == 'rcc') {");
        html.append("      qs += '&acao=efetivarAcao&opt=rcc&tipo=retirar_contrato_compra';");
        html.append("      j = '../v3/retirarConsignacaoCompra';");
        if (!exigeMotivoOperacao || !FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_RETIRAR_CONTRATO_COMPRA, responsavel)) {
            html.append("  msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.exclusao.contrato.compra", responsavel)).append("';");
        }

        html.append("    } else if (opt == 'l') {");

        if (exigeMotivoOperacao && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_LIQ_CONTRATO, responsavel)) {
            html.append("  qs += '&acao=efetivarAcao';");
            html.append("  j = '../v3/liquidarConsignacao';");
        } else {
            html.append("  msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel)).append("';");
            html.append("  qs += '&acao=efetivarAcao';");
            html.append("  j = '../v3/liquidarConsignacao';");
        }

        html.append("    } else if (opt == 's') {");
        html.append("      j = '../v3/editarSaldoDevedor';");
        html.append("      qs += '&acao=iniciar&tipo=compra';");
        html.append("    } else if (opt == 'at') {");
        html.append("      j = '../v3/atualizarProcessoPortabilidade';");
        html.append("      qs += '&acao=iniciar';");
        html.append("    } else if (opt == 'sr') {");
        html.append("      msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitacao.recalculo.saldo.devedor", responsavel)).append("';");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&acao=solicitarRecalcSdv';");
        html.append("    } else if (opt == 'rp') {");
        html.append("      msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.rejeicao.pagamento.saldo.devedor", responsavel)).append("';");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&acao=rejeitarPgtoSdv';");
        html.append("    } else if (opt == 'i') {");
        html.append("      msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.pagamento.saldo.devedor", responsavel)).append("';");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&acao=informarPgtSdv';");
        html.append("    } else if (opt == 'apc') {");
        html.append("      j   = '../v3/anexarPagamentoConsignacao?acao=iniciar&ADE_CODIGO=' +ade;");
        html.append("    } else if (opt == 'd') {");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&acao=detalharPesquisa';");
        html.append("    } else if (opt == 'asd') {");
        html.append("      msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.aprovacao.saldo.devedor", responsavel)).append("';");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&aprovado=S&acao=aprovarSaldoDevedor';");
        html.append("    } else if (opt == 'rsd') {");
        html.append("      msg = '").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.rejeicao.saldo.devedor", responsavel)).append("';");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&aprovado=N&acao=aprovarSaldoDevedor';");
        html.append("    } else if (opt == 'cc') {");
        html.append("      msg = '" + ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.processo.compra.contratos", responsavel) + "';");
        html.append("      j = '../v3/retirarConsignacaoCompra';");
        html.append("      qs += '&acao=efetivarAcao&tipo=cancelar_compra&opt=cc';");
        html.append("    } else if (opt == 'hla') {");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&_skip_history_=true';");
        html.append("      openModalSubAcesso(j + qs);");
        html.append("      return;");
        html.append("    } else if (opt == 'MSG_CSA_PORTABILIDADE') {");
        html.append("      j = '../v3/acompanharPortabilidade';");
        html.append("      qs += '&acao=editarMsgCsaPortabilidade&ADE_CODIGO_MSG=' +ade;");
        html.append("    }");

        html.append("    if (opt == 'i' || opt == 'sr' || opt == 'rp') {");
        html.append("      show_observacao(j, qs, msg);");
        html.append("    } else if (msg == '' || confirm(msg)) {");
        html.append("      postData(j + qs);");
        html.append("    }");
        html.append("  }");

        html.append("  function cntrTerceiro(ade, adeDest, parametros) {");
        html.append("    var msg = '';");
        html.append("    var qs = parametros;");

        html.append("    if (ade != '' && adeDest != '') {");
        html.append("      qs += '&ade=' + ade + '&adeDest=' + adeDest + '&barraAcoes=false&isOrigem=1&acao=detalharConsignacao'; ");
        html.append("    }");

        html.append("    j = '../v3/acompanharPortabilidade';");
        html.append("    qs = qs + '&").append(SynchronizerToken.generateToken4URL((HttpServletRequest) pageContext.getRequest())).append("';");
        html.append("    postData(j + qs);");
        html.append("  }");

        html.append("  function show_setObservacao() {");
        html.append("    urlSolicitacao = $(\"#sde_url\").val();");
        html.append("    urlSolicitacao = urlSolicitacao + $(\"#sde_param\").val();");
        html.append("    postData(urlSolicitacao + '&obs=' + $('#editfield').val());");
        html.append("  }");

        html.append("  function show_observacao(sde_url, sde_param, sde_msg) {");
        html.append("    $('#confirmarMensagem').modal('show');");
        html.append("    $(\"#sde_url\").val(sde_url);");
        html.append("    $(\"#sde_param\").val(sde_param);");
        html.append("    $(\"#sde_msg\").text(sde_msg);");
        html.append("    $('#editfield').val(sde_motivo);");
        html.append("  }");

        html.append("</script>");

        return html.toString();
    }

    public String getCorCodigo() {
        return corCodigo;
    }

    public void setCorCodigo(String corCodigo) {
        this.corCodigo = corCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public String getPesquisar() {
        return pesquisar;
    }

    public void setPesquisar(String pesquisar) {
        this.pesquisar = pesquisar;
    }

    public String getFiltroConfiguravel() {
        return filtroConfiguravel;
    }

    public void setFiltroConfiguravel(String filtroConfiguravel) {
        this.filtroConfiguravel = filtroConfiguravel;
    }

    public CustomTransferObject getCriteriosPesquisa() {
        return criteriosSelecionadosPesquisa;
    }

    public void setCriteriosPesquisa(CustomTransferObject criteriosPesquisa) {
        criteriosSelecionadosPesquisa = criteriosPesquisa;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String isReusePageToken() {
        return reusePageToken;
    }

    public void setReusePageToken(String reusePageToken) {
        this.reusePageToken = reusePageToken;
    }
}
