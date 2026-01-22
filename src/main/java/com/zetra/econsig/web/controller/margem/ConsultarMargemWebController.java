package com.zetra.econsig.web.controller.margem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.TextoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.pdf.PDFHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroSer;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.RiscoRegistroServidorEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.glxn.qrgen.QRCode;

/**
 * <p>Title: ConsultarMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarMargem" })
public class ConsultarMargemWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarMargemWebController.class);

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Autowired
    private MargemController margemController;

    @Override
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");

        boolean exibeCaptcha = false;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;

        // DESENV-7221 - Verifica se aparecerá o captcha
        if (responsavel.isCsaCor()) {

            final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptcha(responsavel.getUsuCodigo());

            if (!podeConsultar) {
                final boolean defVisual = responsavel.isDeficienteVisual();
                if (!defVisual) {
                    exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                } else {
                    exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                }
            }
        }

        // quando a chamada não vem da tela inicial de pesquisa de margem
        String linkRet = JspHelper.verificaVarQryStr(request, "linkRetHistoricoFluxo");

        if (TextHelper.isNull(linkRet)) {
            // Se não tem link de retorno, o padrão é voltar para a tela inicial
            if (model.containsAttribute("RSE_CODIGO")) {
                linkRet = "../v3/consultarMargem?acao=iniciar";
            } else {
                linkRet = "../v3/carregarPrincipal";
            }
        } else {
            linkRet = linkRet.replace('$', '?').replace('(', '=').replace('|', '&');
            linkRet += (linkRet.indexOf("?") > -1 ? "&" : "?") + SynchronizerToken.generateToken4URL(request);
        }

        final boolean boolTpcPmtCompMargem = ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, responsavel);

        // Parametro para exibição de análise de risco cadastrado pela CSA
        final boolean temRiscoPelaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, responsavel);

        // Parâmetro para exibição de variação de margem
        final boolean possuiVariacaoMargem = (responsavel.isCseSupOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSE_ORG, responsavel)) || (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSA_COR, responsavel));

        //Parâmetro para exibir filtro de vínculo
        final boolean exibeFiltroVinculo = !responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_FILTRO_VINCULO_CONSULTA_MARGEM, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM);

        List<TransferObject> listaVincRegSer = null;

        try {
            listaVincRegSer = servidorController.selectVincRegistroServidor(true, responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        try {
            if(!TextHelper.isNull(rseCodigo)) {
                final CustomTransferObject registroServidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                if (!model.containsAttribute("RSE_CODIGO") && ((!TextHelper.isNull(rseMatricula) && (registroServidor != null) && registroServidor.getAttribute(Columns.RSE_MATRICULA).equals(rseMatricula)) || TextHelper.isNull(rseMatricula))) {
                    model.addAttribute("RSE_CODIGO",rseCodigo);
                } else if(!TextHelper.isNull(rseMatricula) && (registroServidor != null) && !registroServidor.getAttribute(Columns.RSE_MATRICULA).equals(rseMatricula)) {
                    rseCodigo = null;
                }
            }
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("linkRetHistoricoFluxo", linkRet);
        model.addAttribute("boolTpcPmtCompMargem", boolTpcPmtCompMargem);
        model.addAttribute("temRiscoPelaCsa", temRiscoPelaCsa);
        model.addAttribute("possuiVariacaoMargem", possuiVariacaoMargem);

        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("tipo", tipo);
        model.addAttribute("acao", "pesquisarServidor");
        model.addAttribute("exibeFiltroVinculo", exibeFiltroVinculo);
        model.addAttribute("listaVincRegSer", listaVincRegSer);
        model.addAttribute("rseMatricula",rseMatricula);

        try {
            carregarExibicaoMargem(responsavel, request, session, model);
        } catch (InstantiationException | IllegalAccessException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        super.iniciar(request, response, session, model);

        return viewRedirect("jsp/consultarMargem/consultarMargem", request, session, model, responsavel);
    }

    @Override
    public String pesquisarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParseException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // DESENV-7221 - Criação de novo parâmetro de sistema (captcha na consulta de margens)
        final String usuCodigo = responsavel.getUsuCodigo();
        final boolean validaAutorizacaoSemSenha = !TextHelper.isNull(session.getAttribute("valida_autorizacao"));

        if (responsavel.isCsaCor() && !validaAutorizacaoSemSenha) {

            final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptcha(usuCodigo);

            if (!podeConsultar) {

                final boolean defVisual = responsavel.isDeficienteVisual();

                if (!defVisual) {
                    if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                        if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            return iniciar(request, response, session, model);
                        }
                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                    } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                        final String remoteAddr = request.getRemoteAddr();

                        if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            return iniciar(request, response, session, model);
                        }
                    }
                } else {
                    final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    if (exigeCaptchaDeficiente) {
                        final String captchaAnswer = request.getParameter("captcha");

                        if (captchaAnswer == null) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            return iniciar(request, response, session, model);
                        }

                        final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                        if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            return iniciar(request, response, session, model);
                        }
                        session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    }
                }
            }
        }

        ControleConsulta.getInstance().somarValorCaptcha(usuCodigo);

        return super.pesquisarServidor(request, response, session, model);

    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("RSE_CODIGO", rseCodigo);
        iniciar(request, response, session, model);

        final boolean naoExigeVlrParcelaCseOrg = responsavel.isCseOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_SEMPRE_EXIBIR_VALOR_MARGEM_CSE_ORG, responsavel);
        if (!TextHelper.isNull(rseCodigo)) {
            try {
                final String valorParcela = request.getParameter("ADE_VLR");
                if (!naoExigeVlrParcelaCseOrg && (TextHelper.isNull(valorParcela) && !model.containsAttribute("podeMostrarMargem"))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.erro.valor.ausente", responsavel));
                } else {
                    // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
                    if (!validarSenhaServidor(rseCodigo, false, request, session, responsavel)) {
                        model.addAttribute("RSE_CODIGO", "");
                        return iniciar(request, response, session, model);
                    }

                    if (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)) {
                        final TransferObject risco = leilaoSolicitacaoController.obterAnaliseDeRiscoRegistroServidor(rseCodigo, responsavel);
                        String riscoCsa = (String) (!TextHelper.isNull(risco) ? risco.getAttribute(Columns.ARR_RISCO) : "");

                        riscoCsa = RiscoRegistroServidorEnum.recuperaDescricaoRisco(riscoCsa, responsavel);
                        model.addAttribute("ARR_RISCO", riscoCsa);
                    }

                    // DESENV-16777 - Cascavel - Criar botão para bloquear servidor para uma determinada CSA
                    final boolean podeBloquearDesbloquearConvenios = !responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR);
                    model.addAttribute("podeBloquearDesbloquearConvenios", podeBloquearDesbloquearConvenios);
                    if (podeBloquearDesbloquearConvenios) {
                        final String csaCodigo = responsavel.getCsaCodigo();
                        CustomTransferObject bloqueioServidor = null;
                        try {
                            bloqueioServidor = parametroController.getBloqueioCnvRegistroServidor(rseCodigo, csaCodigo, null, Boolean.TRUE, responsavel);
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        }

                        int cnvBloqueados = 0;
                        if (bloqueioServidor != null) {
                            cnvBloqueados = bloqueioServidor.getAttribute("B") != null ? (Integer) bloqueioServidor.getAttribute("B") : 0;
                        }
                        String msgBloquearDesbloquearConvenios = "";
                        if (cnvBloqueados == 0) {
                            msgBloquearDesbloquearConvenios = "rotulo.acao.bloquear.convenios.servidor";
                        } else {
                            msgBloquearDesbloquearConvenios = "rotulo.acao.desbloquear.convenios.servidor";
                        }
                        model.addAttribute("msgBloquearDesbloquearConvenios", msgBloquearDesbloquearConvenios);
                    }

                    final String senhaServidor = (String) request.getAttribute("senhaServidorOK");
                    final BigDecimal adeVlr = !TextHelper.isNull(valorParcela) ? new BigDecimal(NumberHelper.parse(valorParcela, NumberHelper.getLang())) : null;

                    final TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                    model.addAttribute("servidor", servidor);
                    model.addAttribute("serNome", servidor.getAttribute(Columns.SER_NOME));

                    if (responsavel.isCsaCor()) {
                        final String csaCodigo = responsavel.isCsa() ? responsavel.getCsaCodigo() : responsavel.getCodigoEntidadePai();
                        autorizacaoController.verificaBloqueioVinculoCnvAlertaSessao(session, csaCodigo, null, (String) servidor.getAttribute(Columns.RSE_VRS_CODIGO), responsavel);
                    }
                    verificarServidorVisualizarMargem(responsavel, servidor, rseCodigo, model);
                    carregarMargem(rseCodigo, senhaServidor, adeVlr, servidor, responsavel, session, model);
                    carregarHistoricoLiqAnt(responsavel, session, model);
                    carregarBloqueios(rseCodigo, responsavel, session, model);
                    carregarFotoServidor(servidor, responsavel, model);
                }
            } catch (ServidorControllerException | LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                final String[] vinculo = ex.getMessage().split(":");
                final String mensagemErro = ApplicationResourcesHelper.getMessage("mensagem.vinculoNaoPermiteConsultarMargem", responsavel, vinculo.length > 1 ? vinculo[1] : "");
                session.setAttribute(CodedValues.MSG_ERRO, mensagemErro);
            }
        }

        session.removeAttribute("valida_autorizacao_novamente");
        return viewRedirect("jsp/consultarMargem/consultarMargem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validarDigital" })
    public String validarDigital(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        iniciar(request, response, session, model);

        model.addAttribute("RSE_CODIGO", rseCodigo);
        model.addAttribute("exigeSenhaConsultaMargem", true);
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("acao", "consultar");

        return viewRedirect("jsp/consultarMargem/consultarMargem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validarOtp" })
    public String validarOtp(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ServicoControllerException, ParametroControllerException, InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        iniciar(request, response, session, model);

        model.addAttribute("RSE_CODIGO", rseCodigo);

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("eConsig.page.token");
        params.remove("acao");
        params.remove("back");
        params.remove("tokenLeitor");
        params.remove("RSE_CODIGO");
        params.remove("RSE_MATRICULA");
        params.remove("SER_CPF");
        params.remove("ADE_NUMERO");
        params.remove("_skip_history_");
        params.remove("senha");
        params.remove("serAutorizacao");

        final List<String> requestParams = new ArrayList<>(params);
        model.addAttribute("requestParams", requestParams);

        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("proximaOperacao", "consultar");
        model.addAttribute("exibirCampoSenha", true);
        model.addAttribute("_skip_history_", true);

        final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
        if (geraSenhaAutOtp || (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor())) {
            // Em caso de OTP, a senha não foi exibida no primeiro passo da consulta, pois não é possível gerar OTP sem saber
            // para qual servidor a senha foi enviada. Não sendo OTP, mas sendo a senha obrigatória para exibir dados cadastrais
            // para a CSA/COR, então esta também será obrigatória
            model.addAttribute("exigeSenhaConsultaMargem", Boolean.TRUE);
            model.addAttribute("senhaObrigatoriaConsulta", Boolean.TRUE);
        }

        // Opções de inclusão avançada
        model.addAttribute("desabilitaOpcoesAvancadas", Boolean.TRUE);

        return viewRedirect("jsp/consultarServidor/validarDigitalServidor", request, session, model, responsavel);
    }

    // DESENV-16085: Mensagem de aviso que pode realizar novas reservas apesar de estar bloqueado por o sistema pertencente ao papel estar habilitado.
    private String verificarServidorVisualizarMargem(AcessoSistema responsavel, TransferObject servidor,String rseCodigo, Model model) throws ServidorControllerException, ParametroControllerException {
        String infoMotivoBloqueio = null;
        String msgPertenceCategoria = null;
        String motivoObservacaoFormatado = null;
        if (CodedValues.SRS_BLOQUEADO.equals(servidor.getAttribute(Columns.SRS_CODIGO)) &&
                ((responsavel.isCseOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSE_ORG, CodedValues.TPC_SIM, responsavel)) ||
                (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSA_COR, CodedValues.TPC_SIM, responsavel)) ||
                (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SUP, CodedValues.TPC_SIM, responsavel)) ||
                (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER, CodedValues.TPC_SIM, responsavel)))) {
            model.addAttribute("permissaoVisualizarMargemServidoresBloqueados", true);

            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
            final List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_RSE_BLOQUEIO_STATUS_MANUAL);

            final String[] motivoBloqueio = !TextHelper.isNull(registroServidor.getRseMotivoBloqueio()) ? registroServidor.getRseMotivoBloqueio().split("<br>") : null;
            final OcorrenciaRegistroSer ocaRse = servidorController.obtemUltimaOcorrenciaRegistroServidor(rseCodigo, tocCodigos, responsavel);

            if((motivoBloqueio !=null) && (ocaRse != null)) {
                String motivoObservacao = "";
                if(motivoBloqueio.length > 1) {
                    motivoObservacao = !TextHelper.isNull(motivoBloqueio[0]) && !TextHelper.isNull(motivoBloqueio[1]) ? motivoBloqueio[1] + ". " + motivoBloqueio[2] : motivoBloqueio[1];
                    motivoObservacaoFormatado = motivoObservacao.replaceAll("[\"\\n]+", " ");
                } else {
                    motivoObservacao = !TextHelper.isNull(motivoBloqueio[0]) ? motivoBloqueio[0] : "";
                    motivoObservacaoFormatado = motivoObservacao.replaceAll("[\"\\n]+", " ");
                }
                final String dataOcorrencia = DateHelper.toDateString(ocaRse.getOrsData());
                infoMotivoBloqueio = ApplicationResourcesHelper.getMessage("mensagem.informacao.motivo.bloqueio.registro.servidor", responsavel, motivoObservacaoFormatado,dataOcorrencia);
                model.addAttribute("infoMotivoBloqueio",infoMotivoBloqueio);
            }
        } else {
            model.addAttribute("permissaoVisualizarMargemServidoresBloqueados", false);
        }

        final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
        String paramCsa = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CATEGORIAS_PARA_EXIBIR_MENSAGEM_RESERVA_CONSULTA_MARGEM, responsavel);
        if(!TextHelper.isNull(paramCsa) && paramCsa.contains(registroServidor.getRseTipo())) {
		    msgPertenceCategoria = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_MENSAGEM_EXIBIDA_CSA_COR, responsavel);
		    model.addAttribute("msgPertenceCategoria", msgPertenceCategoria);
		}

        return infoMotivoBloqueio;
    }

    private void carregarMargem(String rseCodigo, String senhaServidor, BigDecimal adeVlr, TransferObject servidor, AcessoSistema responsavel, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServidorControllerException {
        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, adeVlr, null, responsavel.getCsaCodigo(), !TextHelper.isNull(senhaServidor), senhaServidor, true, null, responsavel);
        final Short codMargemLimitePorCsa = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel)) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : 0;
        if ((codMargemLimitePorCsa != null) && !codMargemLimitePorCsa.equals(CodedValues.INCIDE_MARGEM_NAO) && responsavel.isCsaCor()) {
            final MargemTO margemLimiteDisponivel = consultarMargemController.consultarMargemLimitePorCsa(rseCodigo, responsavel.getCsaCodigo(), codMargemLimitePorCsa, null, responsavel);
            if (margemLimiteDisponivel != null) {
                margemLimiteDisponivel.setMarDescricao(ApplicationResourcesHelper.getMessage("rotulo.reservar.margem.margem.limite.por.csa.disponivel", responsavel));
                margens.add(margemLimiteDisponivel);
            }
        }

        final TextoMargem textoMargem = new TextoMargem(servidor, margens, responsavel, model);
        if (!textoMargem.isVazio()) {
            session.setAttribute(textoMargem.getTipoMsg(), textoMargem.getTexto());
        }

        model.addAttribute("lstMargens", margens);
    }

    private void carregarBloqueios(String rseCodigo, AcessoSistema responsavel, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServidorControllerException, ParametroControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_MOSTRA_CNV_BLOQ_CONSULTA_MARGEM, CodedValues.TPC_SIM, responsavel)) {
            boolean temConvenioBloqueado = servidorController.countConvenioBloqueados(rseCodigo, responsavel.getOrgCodigo(), responsavel.getCsaCodigo(), responsavel) > 0;
            if (!temConvenioBloqueado) {
                final Map<String, Long> svcBloqueios = parametroController.getBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);
                temConvenioBloqueado = (svcBloqueios.get("B") != null) && (svcBloqueios.get("B").intValue() > 0);
            }
            if (!temConvenioBloqueado) {
                final Map<String, Long> nseBloqueios = parametroController.getBloqueioNseRegistroServidor(rseCodigo, null, responsavel);
                temConvenioBloqueado = (nseBloqueios.get("B") != null) && (nseBloqueios.get("B").intValue() > 0);
            }
            if (temConvenioBloqueado) {
                model.addAttribute("temConvenioBloqueado", Boolean.TRUE);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.servidor.possui.convenios.bloqueados", responsavel));
            }
        }
    }

    private void carregarExibicaoMargem(AcessoSistema responsavel, HttpServletRequest request, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParametroControllerException {
        final List<MargemTO> margensIncidentes = parametroController.lstMargensIncidentes(null, responsavel.getCsaCodigo(), responsavel.getOrgCodigo(), null, null, responsavel);
        final Iterator<MargemTO> itMargensIncidentes = margensIncidentes.iterator();
        boolean podeMostrarMargem = itMargensIncidentes.hasNext();
        boolean exibeAlgumaMargem = false;
        boolean exigeSenha = false;
        while (itMargensIncidentes.hasNext()) {
            final MargemTO margem = itMargensIncidentes.next();
            final boolean exibeValor = new ExibeMargem(margem, responsavel).isExibeValor();
            podeMostrarMargem &= exibeValor;
            exibeAlgumaMargem |= exibeValor;
        }
        if (podeMostrarMargem) {
            model.addAttribute("podeMostrarMargem", Boolean.TRUE);
        }
        if (exibeAlgumaMargem) {
            model.addAttribute("exibeAlgumaMargem", Boolean.TRUE);
        }

        // Parâmetro de obrigatoriedade de CPF e Matrícula
        final boolean requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
        if (requerMatriculaCpf) {
            model.addAttribute("requerMatriculaCpf", Boolean.TRUE);
        }

        // Se é validação de digital ou gera senha OTP, realiza a validação após selecionar o servidor, então não deve pedir senha
        // Se não tem validação de digital, e a senha é obrigatória para consultar margem, então deve exibir campo de senha
		if (!ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel) && parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel)) {
		    model.addAttribute("exigeSenhaConsultaMargem", Boolean.TRUE);
		    exigeSenha = true;
		}

        // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
            model.addAttribute("exigeSenhaConsultaMargem", Boolean.TRUE);
            model.addAttribute("senhaObrigatoriaConsulta", Boolean.TRUE);
            exigeSenha = true;
        }

        final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
        if (geraSenhaAutOtp) {
            model.addAttribute("geraSenhaAutOtp", Boolean.TRUE);
            exigeSenha = true;
        }

        if (exigeSenha && responsavel.isCsa() && (ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel) > 0)) {
            model.addAttribute("verificaAutorizacaoSemSenha", Boolean.TRUE);
        }

        final String chaveTextoAjuda = JspHelper.getRotuloAjudaPesquisaServidor(requerMatriculaCpf, false, !podeMostrarMargem, responsavel);
        model.addAttribute("chaveTextoAjuda", chaveTextoAjuda);
    }

    private void carregarHistoricoLiqAnt(AcessoSistema responsavel, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServicoControllerException, ParametroControllerException {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS, responsavel)) {
            final String svcCodigo = servicoController.selectServicoMaxParametro(CodedValues.TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS, CodedValues.NSE_EMPRESTIMO, true);
            if (!TextHelper.isNull(svcCodigo)) {
                model.addAttribute("svcCodigo", svcCodigo);

                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final int numAdeHistLiqAntecipadas = !TextHelper.isNull(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) : 0;
                if (numAdeHistLiqAntecipadas > 0) {
                    model.addAttribute("exibeHistLiqAntecipadas", Boolean.TRUE);
                }
            }
        }
    }

    private void carregarFotoServidor(TransferObject servidor, AcessoSistema responsavel, Model model) throws ParametroControllerException {
        final String arquivoFotoServidor = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), servidor.getAttribute(Columns.RSE_CODIGO).toString(), responsavel);
        if (!TextHelper.isNull(arquivoFotoServidor)) {
            model.addAttribute("arquivoFotoServidor", arquivoFotoServidor);
        }
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if (parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {
                return validarOtp(rseCodigo, request, response, session, model);
            } else if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                return validarDigital(rseCodigo, request, response, session, model);
            } else {
                return consultar(rseCodigo, request, response, session, model);
            }
        } catch (ServicoControllerException | ParametroControllerException | InstantiationException | IllegalAccessException | ServletException | IOException | ParseException e) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws com.zetra.econsig.exception.ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarMargem");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        try {
            if (parametroController.senhaServidorObrigatoriaConsultaMargem(JspHelper.verificaVarQryStr(request, "RSE_CODIGO"), responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {
                return "validarOtp";
            } else if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                return "validarDigital";
            } else {
                return "consultar";
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return "consultar";
        }
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    protected String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }

    @RequestMapping(params = { "acao=gerarPdf" }, method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> gerarPdf(@RequestBody(required = true) Map<String,Object> corpo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException, ServidorControllerException, ParametroControllerException, MargemControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean geraCodigoValidacao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_CONTROLE_DOCUMENTO_MARGEM, responsavel);
        final String ulrSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        File qrFile = null;
        String chaveValidacao = null;
        if  (geraCodigoValidacao) {
            chaveValidacao = TextHelper.generateRandomString(6);
            qrFile = QRCode.from(ulrSistema + "/v3/validaPdf?acao=consultar")
                    .withSize(150,150)
                    .file("qrcode.pgn");
        }

        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final String rseCodigo = String.valueOf(corpo.get("rseCodigo"));

        if (TextHelper.isNull(rseCodigo)) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TransferObject servidor = null;
        try {
            // Verifica se existe servidor informado
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(corpo.get("html"));

        // Exibir no relatório data da última atualização de margem
        final Date dataCargaMargem = (Date) servidor.getAttribute(Columns.RSE_DATA_CARGA);
        if (dataCargaMargem != null) {
            htmlBuilder.append("<h3><strong>").append(ApplicationResourcesHelper.getMessage("rotulo.margem.dataCarga", responsavel, "")).append("</strong></h3>\n");
            htmlBuilder.append("<div class=\"col-6\">").append(DateHelper.toDateString(dataCargaMargem)).append("</div><br>");
        }

        String html = htmlBuilder.toString();
        html = html.replace("<dl", "<div");
        html = html.replace("<dt class=\"col-6\">", "<h3><strong>");
        html = html.replace("<dd", "<div");
        html = html.replace("</dl>", "</div>");
        html = html.replace("</dt>", "</strong></h3>");
        html = html.replace("</dd>", "</div><br>");
        html = html.replace("</h2>", "</h2><br>");

        // Remove imagem da geração do relatório
        final String pattern1 = "<img ";
        final String pattern2 = ">";
        final StringBuilder textoSubstituir = new StringBuilder(pattern1);

        final Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        final Matcher m = p.matcher(html);
        while (m.find()) {
            textoSubstituir.append(m.group(1));
        }
        textoSubstituir.append(pattern2);

        // Adiciona logo do sistema
        final String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        final StringBuilder logoSistema = new StringBuilder();
        if (!TextHelper.isNull(urlSistema)) {
            String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
        	String imgLogo = "v4".equals(versaoLeiaute) ? "img/logo_sistema.png" : "img/logo_sistema_v5.png";
            final String urlLogoSistema = urlSistema + (urlSistema.endsWith("/") ? "" : "/") + imgLogo;
            logoSistema.append("<div>");
            logoSistema.append("<img src=\"").append(urlLogoSistema).append("\" align=\"left\" title=\"logo\" alt=\"titulo.logo\">");
            logoSistema.append("</div><br>");
        }

        final StringBuilder rodape = new StringBuilder();

        // Exibir no relatório data da consulta
        final String dataGeracao = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        rodape.append("<h3><strong>").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.data.consulta", responsavel)).append(":</strong></h3>\n");
        rodape.append("<div class=\"col-6\">").append(dataGeracao).append("</div><br>");

        // Exibir no relatório url do sistema
        if (!TextHelper.isNull(urlSistema)) {
            rodape.append("<h3><strong>").append(ApplicationResourcesHelper.getMessage("rotulo.link.acesso", responsavel)).append(":</strong></h3>\n");
            rodape.append("<div class=\"col-6\">").append(urlSistema).append("</div><br>");
        }

     // Exibir no relatório informações do bloqueio
     		final String temBloqueio = "span name=\"bloqueio\"";
     		final boolean temDecisaoJudicial = html.contains(temBloqueio);
     		if (temDecisaoJudicial) {
     			final String regex = "<span\\s+([^>]*)name=\"bloqueio\"([^>]*)>(.*?)</span>";
     			final Pattern pattern = Pattern.compile(regex);
     			final Matcher matcher = pattern.matcher(html);
     			String tagSpan = "";
     			rodape.append("<h3><strong>")
     					.append(ApplicationResourcesHelper.getMessage("rotulo.informacoes.pdf", responsavel))
     					.append(":</strong></h3>\n");
     			while (matcher.find()) {
     				tagSpan = matcher.group(0);
     				rodape.append("<div class=\"col-6\">").append(tagSpan).append("</div><br>");
     				html = html.replace(tagSpan, "");
     			}
     		}

        html = logoSistema.toString() + html.replaceAll(textoSubstituir.toString(), "") + rodape.toString();

        final String hoje = DateHelper.format(DateHelper.getSystemDate(), "yyyyMMdd");
        final String dataHora = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
        final String dirRaiz = ParamSist.getDiretorioRaizArquivos();
        final Paragraph paragrafo = new Paragraph(" ");

        final File hojeDir = new File(dirRaiz + File.separatorChar + "temp" + File.separatorChar + "margem" + File.separatorChar + rseCodigo  + File.separatorChar + hoje);
        if (!hojeDir.exists() && !hojeDir.mkdirs()) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.criacao.diretorio", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());

        final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
        final String nomeArquivoDestino = rseMatricula + "_" + dataHora + ".pdf";
        final String arquivoConsultaMargem = hojeDir.getAbsolutePath() + File.separatorChar + nomeArquivoDestino;

        OutputStream file = null;
        PdfWriter writer = null;
        try {
            file = new FileOutputStream(new File(arquivoConsultaMargem));

            writer = PdfWriter.getInstance(document, file);
            document.open();
            document.add(paragrafo);

            if (qrFile != null) {
                final File finalQrFile = qrFile;
                final String finalChaveValidacao = "Chave: " + chaveValidacao;
                writer.setPageEvent(new PdfPageEventHelper() {
                    @Override
                    public void onEndPage(PdfWriter writer, Document document) {
                        if (writer.getPageNumber() == writer.getPageNumber()) {
                            try {
                                final Image qrImage = Image.getInstance(finalQrFile.getAbsolutePath());
                                qrImage.scaleToFit(80, 80);
                                qrImage.setAbsolutePosition(document.getPageSize().getWidth() - document.rightMargin() - qrImage.getScaledWidth(),
                                        document.bottomMargin()); // Ajustar a posição
                                final PdfContentByte canvas = writer.getDirectContent();
                                canvas.addImage(qrImage);
                                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(finalChaveValidacao),
                                        document.getPageSize().getWidth() - document.rightMargin() - qrImage.getScaledWidth() - 100,
                                        (document.bottomMargin() + (qrImage.getScaledHeight() / 2)) - 26.7f, 0);
                            } catch (final Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    }
                });

            }
            PDFHelper.addHTMLToPDF(document, html);
        } catch (final FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.arquivo.nao.encontrado", responsavel), HttpStatus.CONFLICT);
        } catch (DocumentException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            document.close();

            if (geraCodigoValidacao) {
                margemController.createControleDocumentoMargem(rseCodigo, arquivoConsultaMargem, chaveValidacao, responsavel);
            }

            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (file != null) {
                try {
                    file.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        final File arquivo = new File(arquivoConsultaMargem);

        // Gera log de download de arquivo
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
        }

        final byte[] contents = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(arquivo));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(nomeArquivoDestino, nomeArquivoDestino);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

}
