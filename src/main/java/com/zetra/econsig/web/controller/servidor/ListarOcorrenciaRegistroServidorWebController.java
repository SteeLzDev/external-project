package com.zetra.econsig.web.controller.servidor;

import java.util.Arrays;
import java.util.List;

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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarOcorrenciaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Ocorrencia Registro Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarOcorrenciaRegistroServidor" })
public class ListarOcorrenciaRegistroServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarOcorrenciaRegistroServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            //Valida o token de sessão para evitar a chamada direta da operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            ParamSession paramSession = ParamSession.getParamSession(session);

            String rse_codigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");
            String linkRetorno = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            if (TextHelper.isNull(linkRet)) {
                linkRet = linkRetorno.replace('?', '$').replace('&', '|').replace('=', '(');
            }

            //Busca os dados e as margens do servidor
            CustomTransferObject servidor = null;
            try {
                // Busca os dados do servidor
                servidor = pesquisarServidorController.buscaServidor(rse_codigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> lstOcorrencias = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ORS_RSE_CODIGO, rse_codigo);
                criterio.setAttribute(Columns.ORS_TOC_CODIGO, CodedValues.TOC_RSE_ALTERACAO_MARGEM);

                int total = servidorController.countOrsRegistroServidor(criterio, responsavel);
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                lstOcorrencias = servidorController.lstOrsRegistroServidor(criterio, offset, size, responsavel);

                List<String> listParams = Arrays.asList(new String[] { "RSE_CODIGO" });
                String linkListagem = "../v3/listarOcorrenciaRegistroServidor?acao=iniciar";
                configurarPaginador(linkListagem, "rotulo.paginacao.titulo.mensagem", total, JspHelper.LIMITE, listParams, false, request, model);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("servidor", servidor);
            model.addAttribute("lstOcorrencias", lstOcorrencias);
            model.addAttribute("linkRetorno", linkRetorno);
            model.addAttribute("rse_codigo", rse_codigo);
            model.addAttribute("paramSession", paramSession);

            return viewRedirect("jsp/editarServidor/listarOcorrenciaRegistroServidor", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
