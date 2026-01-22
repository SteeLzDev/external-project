package com.zetra.econsig.web.controller.grupoConsignataria;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.GrupoConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterGrupoConsignataria" })
public class ManterGrupoConsignatariaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterGrupoConsignatariaWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {
        SynchronizerToken.saveToken(request);

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<?> grupo = consignatariaController.lstGrupoConsignataria(null, responsavel);
        boolean podeCriarGrpCsa = responsavel.temPermissao(CodedValues.FUN_CRIAR_GRUPO_CONSIGNATARIA);

        model.addAttribute("grupo", grupo);
        model.addAttribute("podeCriarGrpCsa", podeCriarGrpCsa);

        return viewRedirect("jsp/manterGrupoConsignataria/listarGrupoConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeCriarGrpCsa = responsavel.temPermissao(CodedValues.FUN_CRIAR_GRUPO_CONSIGNATARIA);
        String tgcCodigo = request.getParameter("tgcCodigo");
        String tgcIdentificador = "";
        String tgcDescricao = "";
        if (!TextHelper.isNull(tgcCodigo)) {
            List<?> lstTgc = consignatariaController.lstGrupoConsignataria(tgcCodigo, responsavel);
            CustomTransferObject ctoGrupoConsignataria = (CustomTransferObject) lstTgc.get(0);
            tgcIdentificador = (String) ctoGrupoConsignataria.getAttribute(Columns.TGC_IDENTIFICADOR);
            tgcDescricao = (String) ctoGrupoConsignataria.getAttribute(Columns.TGC_DESCRICAO);

        }

        model.addAttribute("podeCriarGrpCsa", podeCriarGrpCsa);
        model.addAttribute("tgcCodigo", tgcCodigo);
        model.addAttribute("tgcIdentificador", tgcIdentificador);
        model.addAttribute("tgcDescricao", tgcDescricao);

        return viewRedirect("jsp/manterGrupoConsignataria/editarGrupoConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tgcCodigo = request.getParameter("tgcCodigo");
        String tgcIdentificador = request.getParameter("tgcIdentificador");
        String tgcDescricao = request.getParameter("tgcDescricao");

        if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tgcDescricao")) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tgcIdentificador"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String operacao = request.getParameter("operacao");

            //Cria novo registro de grupo de CSA.
        if (operacao.equals("inserir")) {
            try {
                tgcCodigo = consignatariaController.insGrupoConsignataria(tgcIdentificador, tgcDescricao, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.criar.grupo.consignataria.existe.outro.mesmo.codigo", responsavel));
            }
        } else if (operacao.equals("modificar")){
            //Edita registro do grupo CSA.
            try {
                GrupoConsignatariaTransferObject gcto = new GrupoConsignatariaTransferObject(tgcCodigo);
                gcto.setGrupoCsaIdentificador(tgcIdentificador);
                gcto.setGrupoCsaDescricao(tgcDescricao);
                consignatariaController.edtGrupoConsignataria(gcto, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.criar.grupo.consignataria.existe.outro.mesmo.codigo", responsavel));

            }
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String tgcCodigo = request.getParameter("tgcCodigo");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            consignatariaController.renGrupoConsignataria(tgcCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.grupo.csa.sucesso", responsavel));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return iniciar(request, response, session, model);
    }
}
