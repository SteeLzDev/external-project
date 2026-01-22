package com.zetra.econsig.web.controller.servidor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.exception.ArquivoDirfControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.dirf.ArquivoDirfController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ConsultarDirfServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar DIRF do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarDirfServidor" })
public class ConsultarDirfServidorWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarDirfServidorWebController.class);

    @Autowired
    private ArquivoDirfController arquivoDirfController;

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Não valida o per-page-token se for servidor pois este acessa via menu que não repassa o token
            if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String serCodigo = responsavel.getSerCodigo();
            String serNome = responsavel.getUsuNome();
            String rseMatricula = responsavel.getRseMatricula();

            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNome", serNome);

            List<Short> anosDisponiveis = arquivoDirfController.listarAnoCalendarioDirf(serCodigo, responsavel);
            model.addAttribute("anosDisponiveis", anosDisponiveis);

            String link = "../v3/consultarDirfServidor?acao=consultar&" + SynchronizerToken.generateToken4URL(request);
            model.addAttribute("link", link);

            // Recupera o ano que está sendo consultado
            Short anoAtual = null;
            if (TextHelper.isNum(request.getParameter("ANO"))) {
                anoAtual = Short.valueOf(request.getParameter("ANO"));
            } else if (anosDisponiveis != null && !anosDisponiveis.isEmpty()) {
                // Se não está consultando um ano em específico, pega o último disponível, caso exista
                anoAtual = anosDisponiveis.get(anosDisponiveis.size() - 1);
            } else {
                // Se não foi passado, e também não possui registros, pega o ano anterior
                anoAtual = (short) (DateHelper.getYear(DateHelper.getSystemDate()) - 1);
            }
            model.addAttribute("anoAtual", anoAtual);
            model.addAttribute("anoAnterior", (short) (anoAtual - 1));
            model.addAttribute("anoProximo", (short) (anoAtual + 1));
            model.addAttribute("exibirPdf", (anosDisponiveis != null && anosDisponiveis.contains(anoAtual)));

            return viewRedirect("jsp/consultarDirfServidor/consultarDirfServidor", request, session, model, responsavel);

        } catch (ArquivoDirfControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.GET }, params = { "acao=exibir" })
    public void exibir(@RequestParam(value = "ANO", required = true) Short anoCalendario, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String serCodigo = responsavel.getSerCodigo();

            // Obtém o conteúdo da DIRF que estará em Base64
            String conteudoDirfBase64 = arquivoDirfController.obterConteudoArquivoDirf(serCodigo, anoCalendario, responsavel);
            byte[] conteudoDirf = Base64.getDecoder().decode(conteudoDirfBase64);


            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"DIRF.pdf\"");
            response.setContentLength(conteudoDirf.length);

            BufferedInputStream entrada = new BufferedInputStream(new ByteArrayInputStream(conteudoDirf));
            org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
            response.flushBuffer();
            entrada.close();

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        } catch (ArquivoDirfControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
    }
}
