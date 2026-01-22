package com.zetra.econsig.web.controller.rescisao;

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
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaInclusaoColaboradorRescisao;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarColaboradoresRescisaoWebController</p>
 * <p>Description: Listar candidatos e confirmar a inclusão de colaboradores no processo de rescisão contratual</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarColaboradoresRescisao" })
public class ListarColaboradoresRescisaoWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarColaboradoresRescisaoWebController.class);

    @Autowired
    VerbaRescisoriaController verbaRescisoriaController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.colaborador.rescisao.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String listarColaboradoresRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            List<TransferObject> listaRseCandidatoRescisao = new ArrayList<>();
            CustomTransferObject criterio = new CustomTransferObject();

            List<String> svrCodigos = new ArrayList<>();
            svrCodigos.add(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());
            int total = verbaRescisoriaController.countVerbaRescisoriaRse(svrCodigos, responsavel);

            int size = JspHelper.LIMITE;
            int offset = 0;

            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(ProcessaInclusaoColaboradorRescisao.CHAVE, session);
            if (temProcessoRodando) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.inclusao.colaborador.rescisao.processo.em.execucao", responsavel));
            } else {
                criterio.setAttribute(Columns.VRR_SVR_CODIGO, svrCodigos);
                listaRseCandidatoRescisao = verbaRescisoriaController.listarVerbaRescisoriaRse(criterio, offset, size, responsavel);

                // Monta lista de parâmetros e link de paginação
                Set<String> params = new HashSet<>(request.getParameterMap().keySet());
                params.remove("offset");

                List<String> requestParams = new ArrayList<>(params);
                configurarPaginador(getLinkAction(), "rotulo.listar.colaborador.rescisao.paginacao.titulo", total, size, requestParams, false, request, model);
            }

            // Seta atributos no model
            model.addAttribute("listaRseCandidatoRescisao", listaRseCandidatoRescisao);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            return viewRedirect("jsp/rescisao/listarColaboradoresRescisao", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirColaboradorListaRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // Código da verba rescisória do registro servidor que será excluído da lista
            String vrrCodigo = JspHelper.verificaVarQryStr(request, Columns.VRR_CODIGO);
            verbaRescisoriaController.removerVerbaRescisoriaRse(vrrCodigo, responsavel);

            return listarColaboradoresRescisao(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmarListaRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Verifica se tem pelo menos um candidato à rescisão para prosseguir
            List<String> svrCodigos = new ArrayList<>();
            svrCodigos.add(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());
            int total = verbaRescisoriaController.countVerbaRescisoriaRse(svrCodigos, responsavel);
            if (total > 0) {
                // Inicia o processo de confirmação dos colaboradores candidatos no processo de rescisão contratual
                ProcessaInclusaoColaboradorRescisao processo = new ProcessaInclusaoColaboradorRescisao(responsavel);
                processo.start();
                ControladorProcessos.getInstance().incluir(ProcessaInclusaoColaboradorRescisao.CHAVE, processo);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.inclusao.colaborador.rescisao.processo.em.execucao", responsavel));
            } else {
                // Nenhum colaborador selecionado
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.confirmar.colaborador.rescisao.nenhum.selecionado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (VerbaRescisoriaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listarColaboradoresRescisao(request, response, session, model);
    }

    private String getLinkAction() {
        return "../v3/listarColaboradoresRescisao?acao=iniciar";
    }
}
