package com.zetra.econsig.web.controller.consignacao;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
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
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.AutorizarConsignacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutorizarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso AutorizarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/autorizarConsignacao" })
public class AutorizarConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("deferirConsignacaoController")
    private DeferirConsignacaoController deferirConsignacaoController;

    @Autowired
    private AutorizarConsignacaoController autorizarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.autorizar.reserva.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/autorizarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/autorizarConsignacao?acao=confirmarReserva";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.autorizar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.autorizar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.autorizar.reserva.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel);

        acoes.add(new AcaoConsignacao("AUT_RESERVA", CodedValues.FUN_AUT_RESERVA, descricao, descricaoCompleta, "autorizar_reserva.gif", "btnAutorizarReserva", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/autorizarConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "autorizar");
        return criterio;
    }

    @RequestMapping(params = { "acao=confirmarReserva" })
    public String confirmarReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = com.zetra.econsig.helper.web.JspHelper.getAcessoSistema(request);
        try {
            String adeCodigo = request.getParameter("ADE_CODIGO").toString();

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request) || (request.getParameter("ADE_CODIGO") == null)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            adeCodigo = request.getParameter("ADE_CODIGO").toString();

            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_AUT_RESERVA, responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final StringBuilder linkAcao = new StringBuilder("../v3/autorizarConsignacao?acao=autorizarReserva");
            if (!TextHelper.isNull(com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "TMO_CODIGO"))) {
                final String tmoCodigo = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
                final String adeObs = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "ADE_OBS");
                linkAcao.append("&TMO_CODIGO=").append(tmoCodigo).append("&ADE_OBS=").append(adeObs);
            }
            linkAcao.append("&").append(SynchronizerToken.generateToken4URL(request));

            // Busca atributos quanto a exigencia de Tipo de motivo da operacao
            final Object objMtvCancelamento = ParamSist.getInstance().getParam(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, responsavel);
            final boolean exigeMotivo = (CodedValues.TPC_SIM.equals(objMtvCancelamento) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_AUT_RESERVA, responsavel));

            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("autdes", autdes);
            model.addAttribute("linkAcao", linkAcao.toString());
            model.addAttribute("adeCodigo", adeCodigo);

            if ((ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel) > 0) && responsavel.isCsa()) {
                final boolean exigeSenha = parametroController.verificaAutorizacaoReservaSemSenha(autdes.getAttribute(Columns.RSE_CODIGO).toString(), svcCodigo, true, null, responsavel);
                if (!exigeSenha) {
                    model.addAttribute("exigeSenhaServidor", exigeSenha);
                }
            }

            return viewRedirect("jsp/autorizarReserva/autorizarConsignacao", request, session, model, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = "acao=autorizarReserva")
    public String autorizarReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = com.zetra.econsig.helper.web.JspHelper.getAcessoSistema(request);
        try {

            final ParamSession paramSession = ParamSession.getParamSession(session);
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final boolean bloqueiaDeferimentoContratoSemPrioridade = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_DEFERIMENTO_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel);

            final String senhaCriptografada = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
            String senhaAberta = null;
            String funcao = CodedValues.FUN_DEF_CONSIGNACAO;

            // Atualmente (10/05/2018) a operação que chama este arquivo com senha é a 'Autorizar Consignação' (/v3/autorizarConsignacao).
            if (!"".equals(senhaCriptografada)) {
                final String rseCodigo = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "rseCodigo");
                if ("".equals(rseCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                final boolean digitalServidorValidada = ((session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA) != null) && rseCodigo.equals(session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA).toString()));
                if (!digitalServidorValidada) {
                    // Decriptografa a senha informada
                    final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                    senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());

                    try {
                        SenhaHelper.validarSenhaServidor(rseCodigo, senhaAberta, com.zetra.econsig.helper.web.JspHelper.getRemoteAddr(request), request.getParameter("serLogin"), null, true, false, responsavel);
                    } catch (final UsuarioControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
                funcao = CodedValues.FUN_AUT_RESERVA;
            } else if (!TextHelper.isNull(request.getParameterMap().containsKey("rfcOAuth2"))) {
                final String tokenObrigatorio = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "rfcOAuth2");
                final String rseCodigo = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "rseCodigo");
                try {
                    SenhaHelper.validarSenha(request, rseCodigo, null, "51M".equals(tokenObrigatorio), true, responsavel);
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            boolean podeIndeferir = false;
            if (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
                podeIndeferir = true;
            }
            final String cor_codigo = !responsavel.isCor() ? null : responsavel.getCodigoEntidade();
            String msg = "";
            String[] adeCodigosDef = null;
            String[] adeCodigosIndef = null;

            final boolean deferirTodos = CodedValues.TPC_SIM.equals(com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "APLICAR_TODOS_DEF"));
            final boolean indeferirTodos = CodedValues.TPC_SIM.equals(com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "APLICAR_TODOS_INDEF"));

            if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
                if ((request.getParameterValues("chkDeferir") == null) && (!podeIndeferir || (request.getParameterValues("chkIndeferir") == null)) && (request.getParameterValues("adesDeferir") == null)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                // Se for para deferir todos, utiliza a lista de contratos encontrados na busca realizada anteriormente
                if (deferirTodos) {
                    adeCodigosDef = request.getParameterValues("adesDeferir");
                } else {
                    adeCodigosDef = request.getParameterValues("chkDeferir");
                }
                // ordena a lista de contratos para deferir por data crescente
                if (bloqueiaDeferimentoContratoSemPrioridade && (adeCodigosDef != null) && (adeCodigosDef.length > 0)) {
                    adeCodigosDef = pesquisarConsignacaoController.ordenarContratosPorDataCrescente(Arrays.asList(adeCodigosDef), responsavel).toArray(new String[] {});
                }

                if (podeIndeferir) {
                    // Se for para indeferir todos, utiliza a lista de contratos encontrados na busca realizada anteriormente
                    if (indeferirTodos) {
                        adeCodigosIndef = request.getParameterValues("adesDeferir");
                    } else {
                        adeCodigosIndef = request.getParameterValues("chkIndeferir");
                    }
                    // ordena a lista de contratos para indeferir por data crescente
                    if (bloqueiaDeferimentoContratoSemPrioridade && (adeCodigosIndef != null) && (adeCodigosIndef.length > 0)) {
                        adeCodigosIndef = pesquisarConsignacaoController.ordenarContratosPorDataCrescente(Arrays.asList(adeCodigosIndef), responsavel).toArray(new String[] {});
                    }
                }
            } else {
                adeCodigosDef = new String[1];
                adeCodigosDef[0] = request.getParameter("ADE_CODIGO").toString();
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.deferir.consignacao.concluido.sucesso", responsavel));

            if (CodedValues.FUN_AUT_RESERVA.equals(funcao)) {
                // Autorizar consignação
                try {
                    CustomTransferObject tmo = null;
                    if (request.getParameter("TMO_CODIGO") != null) {
                        tmo = new CustomTransferObject();
                        tmo.setAttribute(Columns.ADE_CODIGO, adeCodigosDef[0]);
                        tmo.setAttribute(Columns.TMO_CODIGO, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                        tmo.setAttribute(Columns.OCA_OBS, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                    }
                    autorizarConsignacaoController.autorizar(adeCodigosDef[0], cor_codigo, senhaAberta, tmo, responsavel);
                    if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigosDef[0], CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    }
                } catch (final AutorizacaoControllerException ex) {
                    msg += ex.getMessage() + "<BR>";
                    session.removeAttribute(CodedValues.MSG_INFO);
                }
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            } else {
                // Deferir consignações
                if ((adeCodigosDef != null) && (adeCodigosDef.length > 0)) {
                    for (final String element : adeCodigosDef) {
                        try {
                            CustomTransferObject tmo = null;
                            if ((request.getParameter("TMO_CODIGO") != null) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_DEF_CONSIGNACAO, responsavel)) {
                                tmo = new CustomTransferObject();
                                tmo.setAttribute(Columns.ADE_CODIGO, element);
                                tmo.setAttribute(Columns.TMO_CODIGO, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                                tmo.setAttribute(Columns.OCA_OBS, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                            }
                            deferirConsignacaoController.deferir(element, tmo, responsavel);
                            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                                autorizacaoController.criaOcorrenciaADE(element, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                            }
                        } catch (final AutorizacaoControllerException ex) {
                            msg += ex.getMessage() + "<BR>";
                            session.removeAttribute(CodedValues.MSG_INFO);
                        }
                    }
                }
                // Indeferir consignações
                if ((adeCodigosIndef != null) && (adeCodigosIndef.length > 0)) {
                    for (final String element : adeCodigosIndef) {
                        try {
                            CustomTransferObject tmo = null;
                            if ((request.getParameter("TMO_CODIGO") != null) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_INDF_CONSIGNACAO, responsavel)) {
                                tmo = new CustomTransferObject();
                                tmo.setAttribute(Columns.ADE_CODIGO, element);
                                tmo.setAttribute(Columns.TMO_CODIGO, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                                tmo.setAttribute(Columns.OCA_OBS, com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                            }
                            deferirConsignacaoController.indeferir(element, tmo, responsavel);
                            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                                autorizacaoController.criaOcorrenciaADE(element, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                            }
                        } catch (final AutorizacaoControllerException ex) {
                            msg += ex.getMessage() + "<BR>";
                            session.removeAttribute(CodedValues.MSG_INFO);
                        }
                    }
                }
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (AutorizacaoControllerException | BadPaddingException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
