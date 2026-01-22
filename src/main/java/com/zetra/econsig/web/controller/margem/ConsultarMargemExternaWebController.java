package com.zetra.econsig.web.controller.margem;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;
import com.zetra.econsig.web.controller.taxas.DemonstrarTaxaJurosWebController;
import com.zetra.econsig.webclient.margem.ConsultarMargemServiceClient;

@Controller
@RequestMapping(value = "/v3/consultarMargemExterna")
public class ConsultarMargemExternaWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DemonstrarTaxaJurosWebController.class);

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<MargemTO> margens = new ArrayList<>();

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("RSE_CODIGO", rseCodigo);
        iniciar(request, response, session, model);

        if (!TextHelper.isNull(rseCodigo)) {
            try {
                TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                String rseMatricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
                model.addAttribute("servidor", servidor);
                model.addAttribute("serNome", servidor.getAttribute(Columns.SER_NOME));

                margens = ConsultarMargemServiceClient.consultarMargemExterna(rseMatricula, responsavel);

                model.addAttribute("lstMargens", margens);
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            } catch (UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());            }
        }
        return viewRedirect("jsp/consultarMargem/consultarMargemExterna", request, session, model, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarMargemExterna");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("omitirAdeNumero", true);
    }

    @Override
    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("acaoRetorno", SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal", request));
    }

    @Override
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        // Retorna para operação de pesquisar de servidor
        return super.iniciar(request, response, session, model);
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            return consultar(rseCodigo, request, response, session, model);
        } catch (ServicoControllerException | ParametroControllerException | InstantiationException | IllegalAccessException | ServletException | IOException | ParseException e) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "consultar";
    }
}
