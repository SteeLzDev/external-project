package com.zetra.econsig.web.controller.solicitacaosuporte;

import java.util.Map;

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
import com.zetra.econsig.exception.SolicitacaoSuporteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.solicitacaosuporte.SolicitacaoSuporteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: InserirSolicitacaoSuporteWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso inserir solicitação de suporte.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirSolicitacaoSuporte" })
public class InserirSolicitacaoSuporteWebController extends AbstractWebController {

    @Autowired
    private SolicitacaoSuporteController solicitacaoSuporteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ZetraException  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        Map<String, String> servicoMap = solicitacaoSuporteController.lstValoresCampoSolicitacaoSuporte(Columns.SOS_SERVICO_TRANSIENTE, responsavel);
        model.addAttribute("servicoMap", servicoMap);

        return viewRedirect("jsp/manterSolicitacaoSuporte/inserirSolicitacaoSuporte", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String sosServico = JspHelper.verificaVarQryStr(request, "sosServico");
        String sosSumario = JspHelper.verificaVarQryStr(request, "sosSumario");
        String sosDescricao = JspHelper.verificaVarQryStr(request, "sosDescricao");

        TransferObject sosTO = new CustomTransferObject();
        sosTO.setAttribute(Columns.SOS_SERVICO_TRANSIENTE, sosServico);
        sosTO.setAttribute(Columns.SOS_SUMARIO, sosSumario);
        sosTO.setAttribute(Columns.SOS_DESCRICAO_TRANSIENTE, sosDescricao);

        try {
            solicitacaoSuporteController.criarSolicitacaoSuporte(sosTO, responsavel);
        } catch (SolicitacaoSuporteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return "forward:/v3/listarSolicitacaoSuporte?acao=iniciar";
    }
}
