package com.zetra.econsig.web.controller.renegociacao;

import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: CancelarRenegociacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso CancelarRenegociacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarRenegociacao" })
public class CancelarRenegociacaoWebController extends AbstractConsultarConsignacaoWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.cancelar.renegociacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/cancelarRenegociacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        boolean tpcCancelaPosCorte = ParamSist.getInstance().getParam(CodedValues.TPC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE, responsavel) != null &&
                ParamSist.getInstance().getParam(CodedValues.TPC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE, responsavel).equals(CodedValues.TPC_SIM);

        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        if (tpcCancelaPosCorte) {
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        }
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/cancelarRenegociacao?acao=exibirCancelamento&opt=cr";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.renegociacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancReneg", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("CANC_RENEGOCIACAO", CodedValues.FUN_CANC_RENEGOCIACAO, descricao, descricaoCompleta, "cancelar.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/cancelarRenegociacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "cancelar_renegociacao");
        return criterio;
    }

    @RequestMapping(params = { "acao=exibirCancelamento" })
    public String exibirCancelamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String adeCodigo;
        if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else {
            adeCodigo = request.getParameter("ADE_CODIGO");
        }

        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        SynchronizerToken.saveToken(request);

        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_CANC_RENEGOCIACAO, responsavel.getUsuCodigo(), svcCodigo)) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca atributos quanto a exigencia de Tipo de motivo da operacao
        Object objMtvOperacao = ParamSist.getInstance().getParam(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, responsavel);
        Boolean exigeMotivo = Boolean.valueOf((objMtvOperacao != null && objMtvOperacao.equals(CodedValues.TPC_SIM) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_CANC_RENEGOCIACAO, responsavel)));

        // Exigência de senha para cancelamento de renegociação
        try {
            Boolean exigeSenhaSerCancel = parametroController.senhaServidorObrigatoriaCancelarReneg(svcCodigo, responsavel);
            model.addAttribute("exigeSenhaSerCancel", exigeSenhaSerCancel);
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("exigeMotivo", exigeMotivo);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("autdes", autdes);
        model.addAttribute("adeCodigo", adeCodigo);
        return viewRedirect("jsp/cancelarRenegociacao/cancelarRenegociacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=cancelarRenegociacao" })
    public String cancelarRenegociacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BadPaddingException, ConsignatariaControllerException, ServletException, IOException, ServidorControllerException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String adeCodigo = request.getParameter("ADE_CODIGO").toString();

        try {
            CustomTransferObject tipoMotivoOperacao = null;
            if (request.getParameter("TMO_CODIGO") != null) {
                tipoMotivoOperacao = new CustomTransferObject();
                tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
            }

            String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");
            if (rseCodigo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean dispensaValidacaoDigital = false;
            if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                ServidorTransferObject servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                dispensaValidacaoDigital = !TextHelper.isNull(servidorTO.getSerDispensaDigital()) && servidorTO.getSerDispensaDigital().equals(CodedValues.TPC_SIM);
            }

            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

            }

            // Exigência de senha para cancelamento de renegociação
            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            Boolean exigeSenhaSerCancel = false;
            try {
                exigeSenhaSerCancel = parametroController.senhaServidorObrigatoriaCancelarReneg(svcCodigo, responsavel);
            } catch (ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String senhaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
            boolean validaDigitais = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !dispensaValidacaoDigital;
            boolean digitalServidorValidada = (session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA) != null && rseCodigo.equals(session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA).toString()));
            boolean senhaValidada = false;

            if (validaDigitais && digitalServidorValidada) {
                // Senha validada
                senhaValidada = true;
            } else if (!TextHelper.isNull(senhaCriptografada)) {
                // Decriptografa a senha informada
                KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                String senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());

                if (!TextHelper.isNull(senhaAberta)) {
                    try {
                        SenhaHelper.validarSenhaServidor(rseCodigo, senhaAberta, JspHelper.getRemoteAddr(request), request.getParameter("serLogin"), null, true, false, responsavel);
                        senhaValidada = true;
                    } catch (UsuarioControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return exibirCancelamento(request, response, session, model);
                    }
                }
            }

            if (!senhaValidada && exigeSenhaSerCancel) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            cancelarConsignacaoController.cancelarRenegociacao(adeCodigo, tipoMotivoOperacao, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.concluido.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

        } catch (AutorizacaoControllerException mae) {
            session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";

    }
}
