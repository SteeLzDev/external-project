package com.zetra.econsig.web.controller.margem;

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
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: ConsultarExtratoMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Extrato de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarExtratoMargem" })
public class ConsultarExtratoMargemWebController extends AbstractConsultarConsignacaoWebController {

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        if (TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CustomTransferObject servidor = null;
        List<MargemTO> margensServidor = null;
        List<TransferObject> extrato = null;

        try {
            // Pesquisa os dados do servidor
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, false, responsavel);
            String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();

            // Busca as margens do servidor
            margensServidor = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, true, responsavel);

            // Pesquisa os dados do extrato da margem
            extrato = consultarMargemController.lstExtratoMargemRse(rseCodigo, orgCodigo, responsavel);
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String destinoBotaoVoltar = null;

        if (responsavel.isSer()) {
            destinoBotaoVoltar = "../v3/carregarPrincipal";
        } else {
            ParamSession paramSession = ParamSession.getParamSession(session);
            destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
            if (destinoBotaoVoltar.contains("consultarMargem")) {
                destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
            } else {
                destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
            }
        }

        // Armazena os objetos no contexto de página para acesso nas tags
        model.addAttribute("servidor", servidor);
        model.addAttribute("lstMargens", margensServidor);
        model.addAttribute("lstExtrato", extrato);
        model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);

        return viewRedirect("jsp/consultarExtratoMargem/consultarExtrato", request, session, model, responsavel);
    }
}
