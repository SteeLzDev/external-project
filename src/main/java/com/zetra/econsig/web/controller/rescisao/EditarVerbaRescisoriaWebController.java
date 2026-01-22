package com.zetra.econsig.web.controller.rescisao;

import java.math.BigDecimal;
import java.text.ParseException;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EditarVerbaRescisoriaWebController</p>
 * <p>Description: Editar e reter a verba rescisória do colaborador para abater no pagamento de contratos de empréstimo em aberto</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarVerbaRescisoria" })
public class EditarVerbaRescisoriaWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarVerbaRescisoriaWebController.class);

    @Autowired
    VerbaRescisoriaController verbaRescisoriaController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarColaboradoresRescisao(@RequestParam(value = "VRR_CODIGO", required = true, defaultValue = "") String vrrCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
                    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // Recupera os contratos que podem participar da retenção de verba do colaborador obedecendo a ordem de prioridade para pagamento
            List<TransferObject> listaContratosRetencao = verbaRescisoriaController.listarContratosReterVerbaRescisoria(vrrCodigo, responsavel);

            // Seta atributos no model
            model.addAttribute("VRR_CODIGO", vrrCodigo);
            model.addAttribute("listaContratosRetencao", listaContratosRetencao);

            return viewRedirect("jsp/rescisao/editarVerbaRescisoria", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmarRetencaoVerbaRescisoria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Recupera código e valor da verba rescisória
            String vrrCodigo = JspHelper.verificaVarQryStr(request, "vrrCodigo");
            BigDecimal vrrValor = new BigDecimal(0.00);

            if (!JspHelper.verificaVarQryStr(request, "vrrValor").isEmpty()) {
                vrrValor = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "vrrValor"), NumberHelper.getLang(), "en"));
            }
            // Confirmar retenção de verba rescisória
            verbaRescisoriaController.confirmarVerbaRescisoria(vrrCodigo, vrrValor, responsavel);
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // redireciona para a lista de colaboradores candidatos à rescisão contratual
        return "forward:/v3/listarColaboradoresVerbaRescisoria?acao=iniciar&_skip_history_=true";
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizarVerbaRescisoria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String vrrCodigo = JspHelper.verificaVarQryStr(request, "vrrCodigo");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");

            CustomTransferObject servidor = null;
            // Recupera os dados do colaborador
            if (!TextHelper.isNull(rseCodigo)) {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            }

            // Recupera os contratos do colaborador que foram criados para reter valor de verba rescisória
            List<TransferObject> detalheRetencaoVerbaRescisoria = verbaRescisoriaController.listarContratosVerbaRescisoriaConcluida(vrrCodigo, responsavel);

            // Seta atributos no model
            model.addAttribute("servidor", servidor);
            model.addAttribute("detalheRetencaoVerbaRescisoria", detalheRetencaoVerbaRescisoria);

            return viewRedirect("jsp/rescisao/visualizarVerbaRescisoria", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=visualizarComunicado" })
    public String visualizarComunicadoRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws VerbaRescisoriaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String vrrCodigo = JspHelper.verificaVarQryStr(request, "vrrCodigo");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");

            CustomTransferObject servidor = null;
            // Recupera os dados do colaborador
            if (!TextHelper.isNull(rseCodigo)) {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            }

            // Recupera os contratos do colaborador que continuaram com saldo devedor após aplicação da retenção de verba rescisória
            List<TransferObject> contratosRescisaoSaldoPendente = verbaRescisoriaController.listarContratosSaldoDevedorPendente(vrrCodigo, responsavel);

            model.addAttribute("ser_nome", servidor.getAttribute(Columns.SER_NOME));
            model.addAttribute("contratosRescisaoSaldoPendente", contratosRescisaoSaldoPendente);

            return viewRedirect("jsp/rescisao/visualizarComunicadoRescisao", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
