package com.zetra.econsig.web.controller.servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: OcultarRegistroServidorCsaWebController</p>
 * <p>Description: Controlador Web para o caso de uso ocultar cadastro de servidor para consignatárias.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/ocultarRegistroSerCsa" })
public class OcultarRegistroServidorCsaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OcultarRegistroServidorCsaWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request) || !responsavel.isCseSup()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);

            if (servidor == null || registroServidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<String> csaOculta = servidorController.listCsaOcultasRse(rseCodigo, responsavel);

            // Obtem os valores dos bloqueios por natureza de serviço
            List<TransferObject> consignatarias = null;
            try {
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ORG_CODIGO, registroServidor.getOrgCodigo());
                consignatarias = convenioController.getCsaCnvAtivo(null, registroServidor.getOrgCodigo(), responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            StringBuilder possuiContratoCsa = new StringBuilder();
            try {
                List<TransferObject> totalContratosCsa = pesquisarConsignacaoController.obtemTotalConsignacaoPorCsa(rseCodigo, responsavel);
                if (totalContratosCsa != null && !totalContratosCsa.isEmpty()) {
                    for (TransferObject csa : totalContratosCsa) {
                        possuiContratoCsa.append("{");
                        possuiContratoCsa.append("codigo: \"").append(csa.getAttribute(Columns.CSA_CODIGO).toString()).append("\", ");
                        possuiContratoCsa.append("nome: \"").append(csa.getAttribute(Columns.CSA_NOME).toString()).append("\"");
                        possuiContratoCsa.append("}, ");
                    }

                    if (possuiContratoCsa.lastIndexOf(", ") > 0) {
                        possuiContratoCsa.replace(possuiContratoCsa.lastIndexOf(", "), possuiContratoCsa.length(), "");
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("servidor", servidor);
            model.addAttribute("registroServidor", registroServidor);
            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("possuiContratoCsa", possuiContratoCsa.toString());
            model.addAttribute("csaOculta", csaOculta);

            return viewRedirect("jsp/editarServidor/ocultarRegistroServidorCsa", request, session, model, responsavel);

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request) || !responsavel.isCseSup()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tmoCodigo = request.getParameter("TMO_CODIGO");
            String orsObs = request.getParameter("ADE_OBS");

            // Se for servidor, não exige motivo para operação
            if (!responsavel.isSer() && TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_OCULTAR_RSE_PARA_CSA, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return iniciar(request, response, session, model);
            }

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String[] codigos = request.getParameterValues("selecionarCheckBox");

            List<String> csaCodigos = new ArrayList<>();
            if (codigos != null) {
                csaCodigos = Arrays.asList(codigos);
            }

            // Salva os bloqueios de servidor
            servidorController.createRegistroServidorOcultoCsa(rseCodigo, csaCodigos, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.ocultado.csa.sucesso", responsavel));

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
}
