package com.zetra.econsig.web.controller.funcao;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterFuncaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de manutenção de funções</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 **/
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterFuncao" })
public class ManterFuncaoWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterFuncaoWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;
            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            if (!TextHelper.isNull(filtro)) {
                try {
                    filtro = java.net.URLDecoder.decode(filtro, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            TransferObject criterio = new CustomTransferObject();
            if (filtro_tipo == 2) {
                criterio.setAttribute(Columns.FUN_DESCRICAO, filtro);
            } else if (filtro_tipo == 3) {
                criterio.setAttribute(Columns.GRF_DESCRICAO, filtro);
            }

            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            int total = usuarioController.countFuncao(criterio, responsavel);

            List<TransferObject> funcoes = usuarioController.listFuncao(criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("acao");
            params.remove("FILTRO");

            List<String> requestParams = new ArrayList<>(params);

            String link = "../v3/manterFuncao?acao=listar";

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO"))) {
                String filtroDecode = java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "FILTRO"), "UTF-8");
                link += "&FILTRO=" + java.net.URLEncoder.encode(filtroDecode, "UTF-8");
            }

            configurarPaginador(link, "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);

            model.addAttribute("funcoes", funcoes);
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtro_tipo", filtro_tipo);

            return viewRedirect("jsp/manterFuncao/listarFuncao", request, session, model, responsavel);

        } catch (UnsupportedEncodingException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String funCodigo = JspHelper.verificaVarQryStr(request, "funCodigo");

            TransferObject funcao = usuarioController.getFuncao(funCodigo, responsavel);

            model.addAttribute("funcao", funcao);

            return viewRedirect("jsp/manterFuncao/editarFuncao", request, session, model, responsavel);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            ParamSession paramSession = ParamSession.getParamSession(session);

            String funCodigo = JspHelper.verificaVarQryStr(request, "funCodigo");
            String funDescricao = JspHelper.verificaVarQryStr(request, "funDescricao");
            String funExigeTmo = JspHelper.verificaVarQryStr(request, "funExigeTmo");
            String funExigeSegundaSenhaCse = JspHelper.verificaVarQryStr(request, "funExigeSegundaSenhaCse");
            String funExigeSegundaSenhaOrg = JspHelper.verificaVarQryStr(request, "funExigeSegundaSenhaOrg");
            String funExigeSegundaSenhaSup = JspHelper.verificaVarQryStr(request, "funExigeSegundaSenhaSup");
            String funExigeSegundaSenhaCsa = JspHelper.verificaVarQryStr(request, "funExigeSegundaSenhaCsa");
            String funExigeSegundaSenhaCor = JspHelper.verificaVarQryStr(request, "funExigeSegundaSenhaCor");

            TransferObject funcao = new CustomTransferObject();
            funcao.setAttribute(Columns.FUN_CODIGO, funCodigo);
            funcao.setAttribute(Columns.FUN_DESCRICAO, funDescricao);
            funcao.setAttribute(Columns.FUN_EXIGE_TMO, funExigeTmo);
            funcao.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE, funExigeSegundaSenhaCse);
            funcao.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG, funExigeSegundaSenhaOrg);
            funcao.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP, funExigeSegundaSenhaSup);
            funcao.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA, funExigeSegundaSenhaCsa);
            funcao.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR, funExigeSegundaSenhaCor);

            // Altera função
            usuarioController.updateFuncao(funcao, null, responsavel);

            // Limpar cache
            JspHelper.limparCacheParametros();

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.funcao.alterado.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.funcao.editar.titulo", responsavel));
    }

}
