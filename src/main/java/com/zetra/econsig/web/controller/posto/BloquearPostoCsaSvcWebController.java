package com.zetra.econsig.web.controller.posto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;


@Controller
@RequestMapping(value = { "/v3/bloquearPostoCsaSvc" }, method = { RequestMethod.POST })
public class BloquearPostoCsaSvcWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloquearPostoCsaSvcWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    private String listarPostos(@RequestParam(value = "svc", required = true, defaultValue = "")
        String svcCodigo, @RequestParam(value = "csa", required = true, defaultValue = "")
        String csaCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Busca os dados da consignatária passada
            ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", !TextHelper.isNull(csa.getCsaNomeAbreviado()) ? csa.getCsaNomeAbreviado() : csa.getCsaNome());

            ServicoTransferObject svc = convenioController.findServico(svcCodigo, responsavel);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("svcDescricao", svc.getSvcDescricao());

            // Lista os postos trazendo junto os bloqueios, caso existam, dos postos
            List<TransferObject> lstPostos = postoRegistroServidorController.lstBloqueioPostoPorCsaSvc(csaCodigo, svcCodigo, responsavel);
            model.addAttribute("lstPostos", lstPostos);

            return viewRedirect("jsp/bloquearPostoCsaSvc/listarPosto", request, session, model, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    private String salvar(@RequestParam(value = "svc", required = true, defaultValue = "")
        String svcCodigo, @RequestParam(value = "csa", required = true, defaultValue = "")
        String csaCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Percorre a listagem de postos obtendo da requisição os dados marcados no formulário
            Map<String, Boolean> bloqueiosSolicitacao = new HashMap<>();
            Map<String, Boolean> bloqueiosReserva = new HashMap<>();
            List<TransferObject> lstPostos = postoRegistroServidorController.lstBloqueioPostoPorCsaSvc(csaCodigo, svcCodigo, responsavel);
            for (TransferObject posto : lstPostos) {
                String posCodigo = posto.getAttribute(Columns.POS_CODIGO).toString();
                boolean bloqSolicitacao = "S".equals(request.getParameter("solicitacao_" + posCodigo));
                boolean bloqReserva = "S".equals(request.getParameter("reserva_" + posCodigo));
                bloqueiosSolicitacao.put(posCodigo, bloqSolicitacao);
                bloqueiosReserva.put(posCodigo, bloqReserva);
            }

            // Persiste as alterações
            postoRegistroServidorController.salvarBloqueioPostoPorCsaSvc(csaCodigo, svcCodigo, bloqueiosSolicitacao, bloqueiosReserva, responsavel);
            // Define mensagem de sucesso
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.bloquear.posto.csa.svc", responsavel));

            // Redireciona para o recurso anterior
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();
            model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
