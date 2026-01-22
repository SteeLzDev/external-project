package com.zetra.econsig.web.controller.leilao;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.simulacao.SimularConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SolicitarLeilaoWebController</p>
 * <p>Description: Web controller para caso de uso de simulação de reserva</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/solicitarLeilao" })
public class SolicitarLeilaoWebController extends SimularConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitarLeilaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Override
    @RequestMapping(params = { "acao=listarServicos" })
    public String listarServicos(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        List<TransferObject> servicosReserva = null;
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        final boolean origem = true;

        // Se é usuário servidor ...
        if (responsavel.isSer()) {
            final String orgCodigo = responsavel.getOrgCodigo();
            // Busca Lista de serviços disponíveis para solicitação pelo servidor
            try {
                final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                final boolean temPermissaoReserva = false; // Não inclui serviços de natureza que não seja empréstimo
                final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Valida leilao reverso
            if ((servicosReserva.size() == 1) || ((servicosReserva.size() > 1) && ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel))) {
                // Se encontrou apenas um serviço, ou são vários mas agrupado pela natureza, redireciona para tela de simulação
                final CustomTransferObject servico = (CustomTransferObject) servicosReserva.get(0);
                final String svc_codigo = (String) servico.getAttribute(Columns.SVC_CODIGO);
                final String svc_descricao = (String) servico.getAttribute(Columns.SVC_DESCRICAO);

                final ParamSession paramSession = ParamSession.getParamSession(session);
                paramSession.halfBack();

                model.addAttribute("origem", true);
                final String link = "../v3/solicitarLeilao?acao=iniciarSimulacao&SVC_CODIGO=" + svc_codigo + "&titulo=" + TextHelper.forHtmlContent(svc_descricao) + "&flow=start" + "&origem=true"; // + "&" + SynchronizerToken.generateToken4URL(request);
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                return "jsp/redirecionador/redirecionar";
            }
        }

        if (CodedValues.FUN_SOLICITAR_LEILAO_REVERSO.equals(responsavel.getFunCodigo())) {
            try {
                autorizacaoController.verificaBloqueioFuncao(rseCodigo, "LEILAO", responsavel);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem?tipo=principal", request, session, model, responsavel);
            }
        }

        model.addAttribute("servicosReserva", servicosReserva);
        model.addAttribute("origem", origem);

        return viewRedirect("jsp/simularConsignacao/listarServicosLeilaoReverso", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarLeilao" })
    public String iniciarLeilao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
        final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

        final String adeVlr = request.getParameter("ADE_VLR");
        final String vlrLiberado = request.getParameter("VLR_LIBERADO");
        final String przVlr = request.getParameter("PRZ_VLR");
        final String svcCodigoOrigem = request.getParameter("SVC_CODIGO_ORIGEM");

        List<TransferObject> simulacao = null;
        try {
            simulacao = ranking(adeVlr, vlrLiberado, request, response, session, model);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if ((simulacao == null) || simulacao.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Boolean vlrOk = false;
        String csa_codigo = "", csa_nome = "", cft_codigo = "", ranking = "", svcCodigoItem = "", dtjCodigo = "";
        String tac = "", iof = "";
        String cat = "", iva = ""; // simulacaoMetodoMexicano
        String vlr_ade = "", vlrLiberado_param = "";
        for (final TransferObject coeficiente : simulacao) {
            vlrOk = Boolean.parseBoolean(coeficiente.getAttribute("OK").toString());

            if (!vlrOk) {
                continue;
            }

            csa_codigo = (String)coeficiente.getAttribute(Columns.CSA_CODIGO);
            svcCodigoItem = (String)coeficiente.getAttribute(Columns.SVC_CODIGO);
            csa_nome = (String)coeficiente.getAttribute("TITULO");
            cft_codigo = (String)coeficiente.getAttribute(Columns.CFT_CODIGO);
            dtjCodigo = (String)coeficiente.getAttribute(Columns.DTJ_CODIGO);
            vlr_ade = coeficiente.getAttribute("VLR_PARCELA").toString();
            vlrLiberado_param = coeficiente.getAttribute("VLR_LIBERADO").toString();
            ranking = (String)coeficiente.getAttribute("RANKING");

            try {
                if (simulacaoPorTaxaJuros) {
                    if (simulacaoMetodoMexicano) {
                        cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    } else if (simulacaoMetodoBrasileiro) {
                        tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    }
                }
            } catch (final ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            break;
        }

        if (!vlrOk) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.ranking.csa.nao.disponivel", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("CSA_CODIGO", csa_codigo);
        model.addAttribute("CSA_NOME", csa_nome);
        model.addAttribute("CFT_CODIGO", cft_codigo);
        model.addAttribute("ADE_VLR", vlr_ade);
        model.addAttribute("VLR_LIBERADO", vlrLiberado_param);
        model.addAttribute("RANKING", ranking);
        model.addAttribute("SVC_CODIGO", svcCodigoItem);
        if (simulacaoMetodoMexicano) {
            model.addAttribute("ADE_VLR_CAT", cat);
            model.addAttribute("ADE_VLR_IVA", iva);
        } else if (simulacaoMetodoBrasileiro) {
            model.addAttribute("ADE_VLR_TAC", tac);
            model.addAttribute("ADE_VLR_IOF", iof);
        }
        model.addAttribute("SIMULACAO_POR_ADE_VLR", !TextHelper.isNull(adeVlr));
		final boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("VLR_LIBERADO_OK"));
        model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);

        return confirmar(svcCodigoItem, svcCodigoOrigem, csa_codigo, vlr_ade, tac, iof, cat, iva, cft_codigo, dtjCodigo, vlrLiberado_param, przVlr, false, request, response, session, model);
    }

    @Override
    protected String confirmar(String svcCodigo, String svcCodigoOrigem, String csaCodigo, String adeVlr, String adeVlrTac, String adeVlrIof, String ade_vlr_cat, String adeVlrIva, String cftCodigo, String dtjCodigo, String vlrLiberado, String przVlr, boolean escolherOutroSvc, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if(ParamSist.paramEquals(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_NAO, responsavel)) {
            return super.confirmar(svcCodigo, svcCodigoOrigem, csaCodigo, adeVlr, adeVlrTac, adeVlrIof, ade_vlr_cat, adeVlrIva, cftCodigo, dtjCodigo, vlrLiberado, przVlr, false, request, response, session, model);
        }

        final String rseCodigo = responsavel.getRseCodigo();

        final String minutosEncerramentoLeilao = (ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel).toString() : "N/A");

        String dataPrevistaEncerramentoLeilao = null;
        try {
            if (!"N/A".equals(minutosEncerramentoLeilao)) {
                dataPrevistaEncerramentoLeilao = DateHelper.toDateTimeString(leilaoSolicitacaoController.calcularDataValidadeLeilao(Integer.parseInt(minutosEncerramentoLeilao), responsavel));
            }
        } catch (final NumberFormatException | CalendarioControllerException ex) {
            LOG.error(ex.getMessage());
        }

        boolean temBloqueioLeilao = false;

        try {
            autorizacaoController.verificaBloqueioFuncao(rseCodigo, "LEILAO", responsavel);
        } catch (final AutorizacaoControllerException ex) {
            temBloqueioLeilao = true;
        }

        String mensagemTermoAceite = null;
        if ((responsavel.getFunCodigo() != null) && CodedValues.FUN_SOLICITAR_PORTABILIDADE.equals(responsavel.getFunCodigo())) {
            mensagemTermoAceite = ApplicationResourcesHelper.getMessage("mensagem.leilao.portabilidade.termoaceite", responsavel, String.valueOf(Integer.valueOf(minutosEncerramentoLeilao) / 60));
        } else {
            mensagemTermoAceite = ApplicationResourcesHelper.getMessage("mensagem.leilao.reverso.termoaceite", responsavel, String.valueOf(Integer.valueOf(minutosEncerramentoLeilao) / 60));
        }

        model.addAttribute("dataPrevistaEncerramentoLeilao", dataPrevistaEncerramentoLeilao);
        model.addAttribute("mensagemTermoAceite", mensagemTermoAceite);
        model.addAttribute("temBloqueioLeilao", temBloqueioLeilao);

        return super.confirmar(svcCodigo, svcCodigoOrigem, csaCodigo, adeVlr, adeVlrTac, adeVlrIof, ade_vlr_cat, adeVlrIva, cftCodigo, dtjCodigo, vlrLiberado, przVlr, false, request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.menu.leilao.reverso", responsavel));
        model.addAttribute("acaoFormulario", "../v3/solicitarLeilao");
        model.addAttribute("leilaoReverso", Boolean.TRUE);
    }
}
