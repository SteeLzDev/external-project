package com.zetra.econsig.web.controller.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: CancelarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso CancelarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarConsignacao" })
public class CancelarConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarReservaWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.cancelar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/cancelarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
        CustomTransferObject autDesconto = null;
        boolean exigeSenhaServidor = false;

        try {
            if(!TextHelper.isNull(adeCodigo)) {
                autDesconto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO((String) autDesconto.getAttribute(Columns.SVC_CODIGO), responsavel);
                exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerCancelarConsignacao();
            }
        } catch (AutorizacaoControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("exigeSenhaServidor", responsavel.isCsaCor() && exigeSenhaServidor);

        String urlDestino = "../v3/cancelarConsignacao?acao=cancelar";
        String funCodigo = CodedValues.FUN_CANC_CONSIGNACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        if (responsavel.isCseSupOrg()) {
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        }
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/cancelarConsignacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel);

        acoes.add(new AcaoConsignacao("CANC_CONSIGNACAO", CodedValues.FUN_CANC_CONSIGNACAO, descricao, descricaoCompleta, "cancelar.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,null));

        // Adiciona o editar consignação
        link = "../v3/cancelarConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "cancelar-ade");
        return criterio;
    }

    @RequestMapping(params = { "acao=cancelar" })
    public String cancelar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            // Valida o token de sesssão para evitar a chamada direta da operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (request.getParameter("ADE_CODIGO") == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String adeCodigo = request.getParameter("ADE_CODIGO").toString();

            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();

            ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            boolean exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerCancelarConsignacao();

            if (responsavel.isCsaCor() && exigeSenhaServidor) {
                if (TextHelper.isNull(request.getParameter("serAutorizacao")) && TextHelper.isNull(request.getParameter("tokenOAuth2"))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else {
                    try {
                        SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, false, true, responsavel);
                    } catch (ZetraException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

            try {
                CustomTransferObject tipoMotivoOperacao = null;
                if (request.getParameter("TMO_CODIGO") != null) {
                    tipoMotivoOperacao = new CustomTransferObject();
                    tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                }

                cancelarConsignacaoController.cancelar(adeCodigo, tipoMotivoOperacao, responsavel);
                consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.concluido.sucesso", responsavel));

                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                }

            } catch (AutorizacaoControllerException mae) {
                session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
                LOG.error(mae);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ConsignatariaControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
