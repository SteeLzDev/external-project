package com.zetra.econsig.web.controller.gruposervico;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.GrupoServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ManterGrupoServicoWebController</p>
 * <p>Description: Web Controller para manutenção de grupo de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/manterGrupoServico" }, method = { RequestMethod.POST })
public class ManterGrupoServicoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterGrupoServicoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @RequestMapping(params = { "acao=iniciar" })
    private String listarGrupoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        try {
            List<TransferObject> grupo = convenioController.lstGrupoServicos(true, responsavel);
            model.addAttribute("grupo", grupo);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterGrupoServico/listarGrupoServico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    private String excluirGrupoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tgsCodigo = JspHelper.verificaVarQryStr(request, "codigo");
            GrupoServicoTransferObject grupoSvcRem = new GrupoServicoTransferObject(tgsCodigo);
            convenioController.removeGrupoServico(grupoSvcRem, responsavel);
            session.setAttribute(CodedValues.MSG_INFO,ApplicationResourcesHelper.getMessage("mensagem.excluir.grupo.servico.sucesso", responsavel));
        } catch (ConvenioControllerException e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listarGrupoServico(request, response, session, model);
    }

    @RequestMapping(params = { "acao=inserir"})
    private String inserirGrupoServico (HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return viewRedirect("jsp/manterGrupoServico/inserirGrupoServico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar"})
    private String salvarGrupoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        boolean criarNovoRegistro = false;
        String tgsCodigo = "";

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // Verifica se os campos foram preenchidos
            String reqColumnsStr = "tgsIdentificador|tgsGrupo";
            String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
            if (!TextHelper.isNull(msgErro)) {
                return inserirGrupoServico(request, response, session, model);
            }

            Integer qtd = null;
            Integer qtdPorCsa = null;
            try {
                qtd = !JspHelper.verificaVarQryStr(request, "tgsQuantidade").equals("") ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "tgsQuantidade")) : null;
            } catch (NumberFormatException ex) {
                qtd = null;
            }
            try {
                qtdPorCsa = !JspHelper.verificaVarQryStr(request, "tgsQuantidadePorCsa").equals("") ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "tgsQuantidadePorCsa")) : null;
            } catch (NumberFormatException ex) {
                qtdPorCsa = null;
            }

            tgsCodigo = JspHelper.verificaVarQryStr(request, "tgsCodigo");
            criarNovoRegistro = TextHelper.isNull(tgsCodigo);

            if (criarNovoRegistro) {
                // cria novo grupo de serviço
                GrupoServicoTransferObject grupoSvc = new GrupoServicoTransferObject();
                grupoSvc.setGrupoSvcIdentificador(JspHelper.verificaVarQryStr(request, "tgsIdentificador"));
                grupoSvc.setGrupoSvcGrupo(JspHelper.verificaVarQryStr(request, "tgsGrupo"));
                grupoSvc.setGrupoSvcQuantidade(qtd);
                grupoSvc.setGrupoSvcQuantidadePorCsa(qtdPorCsa);
                tgsCodigo = convenioController.createGrupoServico(grupoSvc, responsavel);
            } else {
                // atualiza o grupo de serviço
                GrupoServicoTransferObject grupoSvc = new GrupoServicoTransferObject(tgsCodigo);
                grupoSvc.setGrupoSvcGrupo(JspHelper.verificaVarQryStr(request, "tgsGrupo"));
                grupoSvc.setGrupoSvcIdentificador(JspHelper.verificaVarQryStr(request, "tgsIdentificador"));
                grupoSvc.setGrupoSvcQuantidade(qtd);
                grupoSvc.setGrupoSvcQuantidadePorCsa(qtdPorCsa);
                convenioController.updateGrupoServico(grupoSvc, responsavel);
            }
            model.addAttribute("tgsCodigo", tgsCodigo);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.salvar.grupo.servico.sucesso", responsavel));
            return carregarDadosParaVisualizacao(request, session, model, tgsCodigo, responsavel);
        } catch (ConvenioControllerException e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            if (criarNovoRegistro) {
                return viewRedirect("jsp/manterGrupoServico/inserirGrupoServico", request, session, model, responsavel);
            } else {
                // return editarGrupoServico(request, response, session, model);
                return carregarDadosParaVisualizacao(request, session, model, tgsCodigo, responsavel);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/manterGrupoServico/inserirGrupoServico", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar"})
    private String editarGrupoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tgsCodigo = JspHelper.verificaVarQryStr(request, "tgsCodigo");
        return carregarDadosParaVisualizacao(request, session, model, tgsCodigo, responsavel);
    }


    private String carregarDadosParaVisualizacao(HttpServletRequest request, HttpSession session, Model model, String tgsCodigo, AcessoSistema responsavel)  {
        try {
            // Carrega os dados do grupo de serviço para exibir na tela
            GrupoServicoTransferObject grupoServico = convenioController.findGrupoServico(tgsCodigo, responsavel);
            model.addAttribute("grupoServico", grupoServico);
        } catch (ConvenioControllerException e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterGrupoServico/editarGrupoServico", request, session, model, responsavel);
    }
}
