package com.zetra.econsig.web.controller.servidor;

import java.text.ParseException;

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
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.RiscoRegistroServidorEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CadastrarAnaliseRiscoServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Cadastrar Análise de Risco do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cadastrarAnaliseRiscoServidor" })
public class CadastrarAnaliseRiscoServidorWebController extends AbstractWebController {

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Verifica se está habilitado o cadastro de análise de risco e se o usuário tem permissão
        if (!ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel) || !responsavel.isCsa() || !responsavel.temPermissao(CodedValues.FUN_CADASTRO_RISCO_SERVIDOR_CSA) || TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            TransferObject risco = leilaoSolicitacaoController.obterAnaliseDeRiscoRegistroServidor(rseCodigo, responsavel);
            String arrRisco = (String) (!TextHelper.isNull(risco) ? risco.getAttribute(Columns.ARR_RISCO) : "");
            String arrData = !TextHelper.isNull(risco) ? DateHelper.reformat(risco.getAttribute(Columns.ARR_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
            String riscoTexto = RiscoRegistroServidorEnum.recuperaDescricaoRisco(arrRisco, responsavel);

            CustomTransferObject servInfo = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            model.addAttribute("servidor", servInfo);
            model.addAttribute("arrRisco", arrRisco);
            model.addAttribute("arrData", arrData);
            model.addAttribute("riscoTexto", riscoTexto);
            model.addAttribute("rseCodigo", rseCodigo);

            return viewRedirect("jsp/cadastrarAnaliseRiscoServidor/cadastrarAnaliseRiscoServidor", request, session, model, responsavel);
        } catch (LeilaoSolicitacaoControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            String csaCodigo = responsavel.getCsaCodigo();
            String risco = JspHelper.verificaVarQryStr(request, "ARR_RISCO");

            // Salva a análise para o servidor
            leilaoSolicitacaoController.informarAnaliseDeRisco(rseCodigo, csaCodigo, risco, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cadastrar.analise.risco.servidor.sucesso", responsavel));

            // Volta para o acompanhamento de leilão
            paramSession.halfBack();

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (LeilaoSolicitacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
