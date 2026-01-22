package com.zetra.econsig.web.controller.consignacao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: SolicitarSaldoDevedorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Solicitar Saldo Devedor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/solicitarSaldoDevedor" })
public class SolicitarSaldoDevedorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitarSaldoDevedorWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return executar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=solicitar" })
    public String solicitar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return executar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=solicitar_liquidacao" })
    public String solicitarSaldoLiquidacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return executar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=solicitar_saldo_exclusao_ser" })
    public String solicitarSaldoExclusao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return executar(request, response, session, model);
    }

    private String executar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String acao = JspHelper.verificaVarQryStr(request, "acao");

            if (!(responsavel.isSer() || responsavel.isCseSupOrg()) || (acao.equalsIgnoreCase("solicitar_saldo_exclusao_ser") && TextHelper.isNull(rseCodigo)) || (!acao.equalsIgnoreCase("solicitar_saldo_exclusao_ser") && TextHelper.isNull(adeCodigo))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean habilitaSaldoDevedorExclusaoServidor = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            if (acao.equalsIgnoreCase("solicitar_saldo_exclusao_ser") && (!habilitaSaldoDevedorExclusaoServidor || !responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEV_EXCLUSAO_SER))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean acaoSimularSaldo = acao.equalsIgnoreCase("consultar");
            int qtdParcelas = 0;
            if (acaoSimularSaldo && ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULACAO_PARCIAL_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel)) {
                boolean simulacaoParcial = "true".equals(request.getParameter("parcial"));
                if (simulacaoParcial) {
                    qtdParcelas = TextHelper.isNum(request.getParameter("qtdParcelas")) ? Integer.valueOf(request.getParameter("qtdParcelas")) : 0;
                }
            }

            try {
                String ocaObs = "";
                String sdvTelefone = JspHelper.verificaVarQryStr(request, "sdv_telefone");

                String requerTelSer = (String) ParamSist.getInstance().getParam(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, responsavel);

                if(!TextHelper.isNull(requerTelSer) && requerTelSer.equalsIgnoreCase(CodedValues.TEL_SER_SOLIC_SALDO_DEVEDOR_OBRIGATORIO) && TextHelper.isNull(sdvTelefone)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitar.saldo.devedor.telefone.obrigatorio", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!sdvTelefone.equals("")) {
                    ocaObs = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.ocorrencia.obs.telefone", responsavel, responsavel.getUsuNome(), sdvTelefone);
                    autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_TEL_SERVIDOR, sdvTelefone, responsavel);
                } else {
                    ocaObs = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.ocorrencia.obs", responsavel, responsavel.getUsuNome());
                }
                String msg = "";
                if (acao.equalsIgnoreCase("solicitar_saldo_exclusao_ser")) {
                    msg = saldoDevedorController.solicitarSaldoDevedorExclusaoServidor(rseCodigo, ocaObs, responsavel);
                } else {
                    msg = saldoDevedorController.solicitarSaldoDevedor(adeCodigo, ocaObs, !acaoSimularSaldo, acao.equalsIgnoreCase("solicitar_liquidacao"), qtdParcelas, responsavel);
                }
                session.setAttribute(CodedValues.MSG_INFO, msg);

                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                }
            } catch (SaldoDevedorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
