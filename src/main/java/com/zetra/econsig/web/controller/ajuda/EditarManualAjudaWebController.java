package com.zetra.econsig.web.controller.ajuda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AcessoRecurso;
import com.zetra.econsig.persistence.entity.Ajuda;
import com.zetra.econsig.service.sistema.AjudaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EditarManualAjudaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Ajuda.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 29455 $
 * $Date: 2020-05-08 15:58:16 -0300 (Sex, 08 mai 2020) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarManualAjuda" })
public class EditarManualAjudaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarManualAjudaWebController.class);

    @Autowired
    private AjudaController ajudaController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rotuloBotaoVisualizar = ApplicationResourcesHelper.getMessage("mensagem.ajuda.visualizar.clique.aqui", responsavel);
        String rotuloBotaoMarcarTodos = ApplicationResourcesHelper.getMessage("mensagem.ajuda.marcar.todos.clique.aqui", responsavel);
        String rotuloBotaoDesmarcarTodos = ApplicationResourcesHelper.getMessage("mensagem.ajuda.desmarcar.todos.clique.aqui", responsavel);
        String rotuloBotaoEditarTodas = ApplicationResourcesHelper.getMessage("rotulo.botao.ajuda.editar.todas", responsavel);
        String rotuloBotaoExcluirTodas = ApplicationResourcesHelper.getMessage("rotulo.botao.ajuda.excluir.todas", responsavel);
        String rotuloBotaoVisualizarTodas = ApplicationResourcesHelper.getMessage("rotulo.botao.ajuda.listar.todos", responsavel);
        String rotuloBotaoVoltar = ApplicationResourcesHelper.getMessage("rotulo.botao.ajuda.voltar", responsavel);
        String rotuloCheckboxTodos = ApplicationResourcesHelper.getMessage("rotulo.checkbox.listar.ajuda.todos", responsavel);

        String acrCodigo = JspHelper.verificaVarQryStr(request, "acrCodigo");
        String funcao = JspHelper.verificaVarQryStr(request, "funCodigo");
        AcessoRecurso acesso = (AcessoRecurso) session.getAttribute("acesso");
        String acrParametro = JspHelper.verificaVarQryStr(request, "acrParametro");
        String acrOperacao = JspHelper.verificaVarQryStr(request, "acrOperacao");

        if (TextHelper.isNull(acrCodigo) && acesso != null) {
            acrCodigo = acesso.getAcrCodigo();
        }
        if (TextHelper.isNull(acrCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<String> funCodigos = new ArrayList<>();

        String excluiFiltroFuncao = JspHelper.verificaVarQryStr(request, "refresh");
        if (!excluiFiltroFuncao.equals("true") && !TextHelper.isNull(funcao)) {
            funCodigos.add(funcao);
        }

        // Recupera o recurso
        List<String> acrCodigos = new ArrayList<>();
        acrCodigos.add(acrCodigo);
        List<TransferObject> retorno = null;
        try {
            retorno = ajudaController.lstFuncoesPapeisAcessoRecurso(acrCodigos, null, null, null, null, responsavel);
        } catch (Exception ex) {
        }
        if (retorno == null || retorno.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.dados.ajuda", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        String acrRecurso = ((CustomTransferObject) retorno.get(0)).getAttribute(Columns.ACR_RECURSO).toString();

        List<TransferObject> funAcessoRecurso = new ArrayList<>();
        try {
            funAcessoRecurso = ajudaController.lstFuncoesPapeisAcessoRecurso(null, acrRecurso, funCodigos, acrParametro, acrOperacao, responsavel);
        } catch (ConsignanteControllerException e) {
            e.printStackTrace();
        }

        Boolean ajudaPopup = request.getParameter("ajudaPopup") != null ? (Boolean.parseBoolean(request.getParameter("ajudaPopup"))) : false;

        if (funAcessoRecurso == null || funAcessoRecurso.isEmpty() || funAcessoRecurso.size() == 1) {
            return "forward:/v3/editarManualAjuda?acao=editar&acrCodigos=" + acrCodigo + "&ajudaPopup=" + ajudaPopup + "&" + SynchronizerToken.generateToken4URL(request);
        }
        model.addAttribute("rotuloBotaoVisualizar", rotuloBotaoVisualizar);
        model.addAttribute("rotuloBotaoMarcarTodos", rotuloBotaoMarcarTodos);
        model.addAttribute("rotuloBotaoDesmarcarTodos", rotuloBotaoDesmarcarTodos);
        model.addAttribute("rotuloBotaoEditarTodas", rotuloBotaoEditarTodas);
        model.addAttribute("rotuloBotaoExcluirTodas", rotuloBotaoExcluirTodas);
        model.addAttribute("rotuloBotaoVisualizarTodas", rotuloBotaoVisualizarTodas);
        model.addAttribute("rotuloBotaoVoltar", rotuloBotaoVoltar);
        model.addAttribute("rotuloCheckboxTodos", rotuloCheckboxTodos);
        model.addAttribute("funAcessoRecurso", funAcessoRecurso);
        model.addAttribute("acrCodigo", acrCodigo);
        model.addAttribute("funcao", funcao);
        model.addAttribute("acesso", acesso);
        model.addAttribute("acrParametro", acrParametro);
        model.addAttribute("acrOperacao", acrOperacao);
        model.addAttribute("clicouListarTodos", request.getParameter("clicouListarTodos") != null ? (request.getParameter("clicouListarTodos").equals(CodedValues.TPC_SIM) ? true : false) : false);

        return viewRedirect("jsp/editarManualAjuda/listarRecurso", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rotuloBotaoCancelar = ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar.edicao.ajuda", responsavel);
        AcessoRecurso acesso = (AcessoRecurso) session.getAttribute("acesso");
        String acaoPosterior = JspHelper.verificaVarQryStr(request, "acaoPosterior");
        String ajuTitulo = JspHelper.verificaVarQryStr(request, "ajuTitulo");
        String ajuTexto = JspHelper.verificaVarQryStr(request, "innerTemp");
        String[] codigos = request.getParameterValues("acrCodigos");
        List<String> acrCodigos = codigos != null ? Arrays.asList(codigos) : new ArrayList<>();
        String acrCodigoOriginal = request.getParameter("acrCodigoOriginal");

        if (acrCodigos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.dados.ajuda", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Faz as substituições necessárias para que o editor possa ler o que foi salvo no banco de dados
        ajuTexto = ajuTexto.replaceAll("&quot;", "\"");

        String ajuSequencia = JspHelper.verificaVarQryStr(request, "ajuSequencia").equals("") ? "0" : JspHelper.verificaVarQryStr(request, "ajuSequencia");

        try {
            // Exclui as ajudas selecionadas
            if (!TextHelper.isNull(acaoPosterior) && acaoPosterior.equals("excluir")) {
                ajudaController.excluirAjuda(acrCodigos, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.ajuda.excluida.sucesso", responsavel));
                return "forward:/v3/visualizarAjudaContexto?acao=visualizar&" + SynchronizerToken.generateToken4URL(request);

            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Ajuda ajuda = null;
        int qtdeAcessoAjudaCadastrada = 0;
        List<TransferObject> funAcessoRecurso = new ArrayList<>();
        try {
            funAcessoRecurso = ajudaController.lstFuncoesPapeisAcessoRecurso(acrCodigos, null, null, null, null, responsavel);
        } catch (ConsignanteControllerException e) {
            e.printStackTrace();
        }
        Iterator<TransferObject> iteFunAceRec = funAcessoRecurso.iterator();
        while (iteFunAceRec.hasNext()) {
            CustomTransferObject cto = (CustomTransferObject) iteFunAceRec.next();
            String codigo = cto.getAttribute(Columns.ACR_CODIGO).toString();
            boolean possuiAjuda = cto.getAttribute("possui_ajuda") != null && cto.getAttribute("possui_ajuda").toString().equals("1") ? true : false;

            if (possuiAjuda) {
                qtdeAcessoAjudaCadastrada++;
            }

            if (ajuda == null && possuiAjuda) {
                try {
                    ajuda = ajudaController.findAjudaByPrimaryKey(codigo, responsavel);
                    if (ajuda != null && !TextHelper.isNull(ajuda.getAjuHtml()) && ajuda.getAjuHtml().equals("S")) {
                        ajuda.setAjuTexto(Jsoup.parse(ajuda.getAjuTexto()).text());
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.ajuda.texto.desformatado", responsavel));
                    }
                } catch (Exception ex) {
                }
            }
        }
        if (qtdeAcessoAjudaCadastrada > 1) {
            String msgAlerta = (String) session.getAttribute(CodedValues.MSG_ALERT);
            if (!TextHelper.isNull(msgAlerta)) {
                msgAlerta += "<br><br>";
            } else {
                msgAlerta = "";
            }
            session.setAttribute(CodedValues.MSG_ALERT, msgAlerta + ApplicationResourcesHelper.getMessage("mensagem.ajuda.informacoes.sobreescritas", responsavel));
        }

        String titulo = null;
        if (ajuda != null) {
          ajuTitulo = ajuda.getAjuTitulo();
        }
        if (ajuTitulo != null && !ajuTitulo.equals("")) {
            titulo = ApplicationResourcesHelper.getMessage("rotulo.editar.ajuda.titulo", responsavel);
        } else {
            ajuTitulo = "";
            titulo = ApplicationResourcesHelper.getMessage("rotulo.criar.ajuda.titulo", responsavel);
        }

        Boolean ajudaPopup = request.getParameter("ajudaPopup") != null ? (Boolean.parseBoolean(request.getParameter("ajudaPopup"))) : false;

        model.addAttribute("rotuloBotaoCancelar", rotuloBotaoCancelar);
        model.addAttribute("acesso", acesso);
        model.addAttribute("ajuTitulo", ajuTitulo);
        model.addAttribute("tituloPagina", titulo);
        model.addAttribute("ajuTexto", ajuTexto);
        model.addAttribute("acrCodigos", acrCodigos);
        model.addAttribute("ajuSequencia", ajuSequencia);
        model.addAttribute("funAcessoRecurso", funAcessoRecurso);
        model.addAttribute("ajuda", ajuda);
        model.addAttribute("qtdeAcessoAjudaCadastrada", qtdeAcessoAjudaCadastrada);
        model.addAttribute("acrCodigoOriginal", acrCodigoOriginal);
        model.addAttribute("ajudaPopup", ajudaPopup ? CodedValues.TPC_SIM : CodedValues.TPC_NAO);

        return viewRedirect("jsp/editarManualAjuda/editarAjuda", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarEdicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String ajuSequencia = JspHelper.verificaVarQryStr(request, "ajuSequencia").equals("") ? "0" : JspHelper.verificaVarQryStr(request, "ajuSequencia");
        String ajuTitulo = JspHelper.verificaVarQryStr(request, "ajuTitulo");
        String ajuTexto = JspHelper.verificaVarQryStr(request, "innerTemp");
        String[] codigos = request.getParameterValues("acrCodigos");
        List<String> acrCodigos = codigos != null ? Arrays.asList(codigos) : new ArrayList<>();

        try {
            // Para salvar uma edição ou nova mensagem, seta os novos valores.
            String mensagemSucesso = ApplicationResourcesHelper.getMessage("mensagem.ajuda.salva.sucesso", responsavel);
            ajudaController.editarAjuda(acrCodigos, ajuTitulo, ajuTexto, Short.valueOf(ajuSequencia), responsavel);
            session.setAttribute(CodedValues.MSG_INFO, mensagemSucesso);

            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
            return editar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
