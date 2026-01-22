package com.zetra.econsig.web.controller.funcao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.DestinatarioEmailSer;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarFuncoesEnvioEmailSerWebController</p>
 * <p>Description: Controlador Web para o casos de uso de editar funções para envio de e-mail do servidor.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Alexandre Fernandes
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarFuncoesEnvioEmailSer" })
public class EditarFuncoesEnvioEmailSerWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarFuncoesEnvioEmailSerWebController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String serCodigo = responsavel.isSer() ? responsavel.getSerCodigo() : request.getParameter("SER_CODIGO");

        if (!SynchronizerToken.isTokenValid(request) || (TextHelper.isNull(serCodigo) && !responsavel.isSer())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
    		ServidorTransferObject ser = servidorController.findServidor(serCodigo, responsavel);

            // Carrega lista de funções que estão habilitadas para envio de e-mail ao servidor
            List<TransferObject> funcoes = servidorController.lstFuncoesEnvioEmailSer(serCodigo, responsavel);

            model.addAttribute("funcoes", funcoes);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("serNome", ser.getSerNome());
            model.addAttribute("serEmail", ser.getSerEmail());
            model.addAttribute("readOnly", !responsavel.temPermissao(CodedValues.FUN_EDITAR_FUNCOES_ENVIO_EMAIL));

            return viewRedirect("jsp/editarFuncoesEnvioEmail/editarFuncoesEnvioEmailSer", request, session, model, responsavel);

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String serCodigo = responsavel.isSer() ? responsavel.getSerCodigo() : request.getParameter("SER_CODIGO");

        if (!SynchronizerToken.isTokenValid(request) || (TextHelper.isNull(serCodigo) && !responsavel.isSer())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
    		List<DestinatarioEmailSer> listaInc = new ArrayList<>();
    		List<DestinatarioEmailSer> listaExc = new ArrayList<>();

    		List<TransferObject> funcoes = servidorController.lstFuncoesEnvioEmailSer(serCodigo, responsavel);
    		for (TransferObject funcao : funcoes) {
    			String funCodigo = (String) funcao.getAttribute(Columns.FUN_CODIGO);
    			String papCodigo = (String) funcao.getAttribute(Columns.PAP_CODIGO);
    			String chaveCampo = funCodigo + "_" + papCodigo;

    			boolean receberOld = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DES_RECEBER));
    			boolean receberNew = "S".equals(request.getParameter("receber_" + chaveCampo));

    			boolean existe = !TextHelper.isNull(funcao.getAttribute(Columns.DES_RECEBER));

    			// Se teve alteração, adiciona à lista de alterações a serem salvas
    			if (receberOld != receberNew) {
    				DestinatarioEmailSer dem = new DestinatarioEmailSer();
    				dem.setFunCodigo(funCodigo);
    				dem.setPapCodigo(papCodigo);
    				dem.setSerCodigo(serCodigo);
    				dem.setDesReceber(receberNew ? "S" : "N");

    				if (existe) {
    					// Se existe e deve receber, então remove o registro
    					listaExc.add(dem);
    				} else {
    					// Se não é para receber, então salva o registro
    					listaInc.add(dem);
    				}
    			}
    		}

    		if (!listaInc.isEmpty() || !listaExc.isEmpty()) {
    			// Salva as alterações
    			servidorController.salvarFuncoesEnvioEmailSer(listaInc, listaExc, responsavel);

    			// Adiciona mensagem de sucesso na sessão
    			session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.funcoes.envio.email.sucesso", responsavel));
    		}

            ParamSession paramSession = ParamSession.getParamSession(session);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.funcao.alterado.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage(responsavel.temPermissao(CodedValues.FUN_EDITAR_FUNCOES_ENVIO_EMAIL) ? "rotulo.editar.funcoes.envio.email.titulo" : "rotulo.consultar.funcoes.envio.email.titulo", responsavel));
    }
}
