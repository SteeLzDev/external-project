package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.margem.ReservarMargemWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/solicitarReservaCartao" })
public class SolicitarReservaCartaoWebController extends ReservarMargemWebController {

    private static final Integer PRAZO_CARTAO = 0;

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("acaoFormulario", "../v3/solicitarReservaCartao");
    }

    @Override
    @PostMapping(params = { "acao=selecionarCsa" })
    public String selecionarCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String rseCodigo = responsavel.getRseCodigo();
        final String orgCodigo = responsavel.getOrgCodigo();
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        final String paginaRetorno = super.selecionarCsa(request, response, session, model);
        final List<TransferObject> consignatarias = (List<TransferObject>) model.getAttribute("listaConsignataria");

        if (consignatarias != null && !consignatarias.isEmpty()) {
            try {

                final List<TransferObject> consignatariasComTaxa = simulacaoController.buscarTaxasParaConsignatarias(responsavel, rseCodigo, orgCodigo, svcCodigo, consignatarias.toArray(TransferObject[]::new));

                if (!consignatariasComTaxa.isEmpty()) {
                    // Ordena por taxa de forma crescente
                    consignatariasComTaxa.sort((TransferObject obj1, TransferObject obj2) -> ((BigDecimal) obj1.getAttribute(Columns.CFT_VLR)).compareTo((BigDecimal) obj2.getAttribute(Columns.CFT_VLR)));

                    // Sobrescreve a lista de consignatárias com as que possuem taxas
                    model.addAttribute("listaConsignataria", consignatariasComTaxa);
                    model.addAttribute("exibeTaxaJuros", Boolean.TRUE);
                    model.addAttribute("temCET", ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel));
                }
            } catch (final ZetraException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        return paginaRetorno;
    }

    

    @Override
    protected BigDecimal buscarTaxaCadastrada(Integer adePrazo, AcessoSistema responsavel, HttpServletRequest request) throws SimulacaoControllerException {
        if (responsavel.isSer()) {
            final String rseCodigo = responsavel.getRseCodigo();
            final String orgCodigo = responsavel.getOrgCodigo();
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            // Prazo será Zero que corresponde ao prazo indeterminado
            adePrazo = PRAZO_CARTAO;

            // Busca a taxa para a CSA informada para exibição na interface
            final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            final List<TransferObject> taxasSimulacao = simulacaoController.getCoeficienteAtivo(csaCodigo, svcCodigo, orgCodigo, rseCodigo, adePrazo.shortValue(), dia, true, null, null, responsavel);
            if (taxasSimulacao != null && !taxasSimulacao.isEmpty()) {
                return (BigDecimal) taxasSimulacao.get(0).getAttribute(Columns.CFT_VLR);
            }
        }

        return null;
    }

    @Override
    @PostMapping(params = { "acao=reservarMargem" })
    public String reservarMargem(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final BigDecimal taxaCadastrada = buscarTaxaCadastrada(PRAZO_CARTAO, responsavel, request);
            if (taxaCadastrada != null) {
                model.addAttribute("taxaCadastrada", NumberHelper.format(taxaCadastrada.doubleValue(), NumberHelper.getLang(), 2, 8));
            }
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return super.reservarMargem(rseCodigo, request, response, session, model);
    }

    @Override
    @PostMapping(params = { "acao=autorizarReserva" })
    public String autorizarReserva(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final BigDecimal taxaCadastrada = buscarTaxaCadastrada(PRAZO_CARTAO, responsavel, request);
            if (taxaCadastrada != null) {
                model.addAttribute("taxaCadastrada", NumberHelper.format(taxaCadastrada.doubleValue(), NumberHelper.getLang(), 2, 8));
            }
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return super.autorizarReserva(rseCodigo, request, response, session, model);
    }
}
