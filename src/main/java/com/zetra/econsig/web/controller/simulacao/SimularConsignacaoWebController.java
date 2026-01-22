package com.zetra.econsig.web.controller.simulacao;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.dto.web.ServicoSolicitacaoServidor;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.margem.TextoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.InserirSolicitacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.webservice.rest.request.CsaListInfoRequest;
import com.zetra.econsig.webservice.rest.request.PDFRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SimularConsignacaoWebController</p>
 * <p>Description: Web controller para caso de uso de simulação de reserva</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/simularConsignacao" })
public class SimularConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimularConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private InserirSolicitacaoController inserirSolicitacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=iniciarSimulacao" })
    public String iniciarSimulacao(@RequestParam(value = "SVC_CODIGO", required = true, defaultValue = "") String svcCodigo, @RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "titulo", required = true, defaultValue = "") String titulo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean origem = Boolean.parseBoolean(request.getParameter("origem"));
        final boolean tpcSolicitarPortabilidadeRanking = Boolean.parseBoolean(request.getParameter("tpcSolicitarPortabilidadeRanking"));
        final String adeCodigo = request.getParameter("ADE_CODIGO");

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        if (tpcSolicitarPortabilidadeRanking) {
            responsavel.setFunCodigo(CodedValues.FUN_SOLICITAR_PORTABILIDADE);
        }

        SynchronizerToken.saveToken(request);
        model.addAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));
        model.addAttribute("_skip_history_", Boolean.TRUE);

        if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo) || TextHelper.isNull(titulo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("SVC_CODIGO", svcCodigo);
        model.addAttribute("RSE_CODIGO", rseCodigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("origem", origem);
        model.addAttribute("tpcSolicitarPortabilidadeRanking", tpcSolicitarPortabilidadeRanking);
        model.addAttribute("ADE_CODIGO", adeCodigo);

        // Parâmetros de serviço
        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        /*******************************************Dados para Simulação*******************************************************/
        // Verifica se pode mostrar margem
        final boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString());
        MargemDisponivel margemDisponivel = null;
        try {
            final Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
            final String tipoVlrMargemDisponivel = ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr());
            model.addAttribute("tipoVlrMargemDisponivel", tipoVlrMargemDisponivel);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String adeVlrPadrao = null;
        try {
            adeVlrPadrao = ((paramSvcCse.getTpsAdeVlr() != null) && !"".equals(paramSvcCse.getTpsAdeVlr())) ? NumberHelper.reformat(paramSvcCse.getTpsAdeVlr(), "en", NumberHelper.getLang()) : "";
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } // Valor da prestação fixo para o serviço
        final boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido

        final ExibeMargem exibeMargem = margemDisponivel.getExibeMargem();
        final boolean podeMostrarMargem = exibeMargem.isExibeValor();

        final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();
        final String margemConsignavel = rseMargemRest.toString();

        if (responsavel.isSer()) {
            // Parâmetro de sistema para exibir a margem do servidor na tela
            boolean exigeCaptcha = false;
            boolean exibeCaptcha = false;
            boolean exibeCaptchaAvancado = false;
            boolean exibeCaptchaDeficiente = false;
            final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaSimular")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptchaSimular") : "N";
            final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

            final boolean defVisual = responsavel.isDeficienteVisual();
            if (!defVisual) {
                exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                exibeCaptcha = !exibeCaptchaAvancado;
            } else {
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            }
            if (!podeConsultar && "S".equals(validaRecaptcha)) {
                if (!defVisual) {
                    if (exibeCaptcha) {
                        if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, "codigoCapSimular"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            exigeCaptcha = true;
                        } else {
                            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                            exigeCaptcha = false;
                        }
                    } else if (exibeCaptchaAvancado) {
                        final String remoteAddr = request.getRemoteAddr();

                        if (!isValidCaptcha(request.getParameter("g-recaptcha-response_leilaoR"), remoteAddr, responsavel)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            exigeCaptcha = true;
                        } else {
                            exigeCaptcha = false;
                        }
                    }
                } else {
                    final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    if (exigeCaptchaDeficiente) {
                        final String captchaAnswer = request.getParameter("codigoCapSimular");

                        if (captchaAnswer == null) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            exigeCaptcha = true;
                        }

                        final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                        if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                            exigeCaptcha = true;
                        } else {
                            session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                            exigeCaptcha = false;
                        }
                    }
                }
            } else if (podeConsultar) {
                exigeCaptcha = false;
            } else {
                exigeCaptcha = true;
            }
            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
            model.addAttribute("exigeCaptcha", exigeCaptcha);
            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        }

        final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
        final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);
        final boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

        // Dados do servidor / registro servidor / órgão / estabelecimento
        CustomTransferObject servidor = null;
        String orgCodigo = null;
        try {
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();

            model.addAttribute("servidor", servidor);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("mensagemDataMargem", TextoMargem.getMensagemDataMargem(servidor, responsavel));
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Se não escolheu prazo ainda, então está na tela inicial da simulação
        final String przVlr = request.getParameter("PRZ_VLR");
        if (TextHelper.isNull(przVlr)) {

            try {
                // Seleciona prazos ativos
                List<TransferObject> prazos = null;
                final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel);
                final int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                Short prazoSimulacao = 0;
                if (!simuladorAgrupadoPorNaturezaServico) {
                    prazos = simulacaoController.getPrazoCoeficiente(svcCodigo, null, orgCodigo, dia, responsavel);
                } else {
                    prazos = simulacaoController.getPrazoCoeficienteEmprestimo(orgCodigo, dia, responsavel);
                }
                if ((prazos == null) || prazos.isEmpty()) {
                    throw new SimulacaoControllerException("mensagem.erro.prazo.com.taxa.inexistente", responsavel);
                } else {
                    final Set<Integer> prazosPossiveisMensal = new TreeSet<>();
                    prazos.forEach(p -> prazosPossiveisMensal.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                    model.addAttribute("prazosPossiveisMensal", prazosPossiveisMensal);
                    prazoSimulacao = Short.valueOf(prazos.get(prazos.size() - 1).getAttribute(Columns.PRZ_VLR).toString());
                    if (!PeriodoHelper.folhaMensal(responsavel)) {
                        final Set<Integer> prazosPossiveisPeriodicidadeFolha = PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel);
                        model.addAttribute("prazosPossiveisPeriodicidadeFolha", prazosPossiveisPeriodicidadeFolha);
                        final LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>(prazosPossiveisPeriodicidadeFolha);
                        prazoSimulacao = linkedHashSet.stream().reduce((first, second) -> second).orElse(0).shortValue();
                    }
                }

                model.addAttribute("prazos", prazos);

                // DESENV-14152
                if ((rseMargemRest != null) && (rseMargemRest.signum() > 0)) {
                    List<TransferObject> simulacoes = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, rseMargemRest, null, prazoSimulacao, null, true, adePeriodicidade, responsavel);
                    final int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
                    simulacoes = simulacaoController.selecionarLinhasSimulacao(simulacoes, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);
                    for (final TransferObject simu : simulacoes) {
                        if ((Boolean) simu.getAttribute("OK")) {
                            model.addAttribute("vlrSolicitadoCalculado", simu.getAttribute("VLR_LIBERADO").toString());
                            break;
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Exibe Texto Explicativo
        String txtExplicativo = "";
        if (responsavel.isSer()) {
            txtExplicativo = TextHelper.isNull(paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao()) ? "" : paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao();
        }

        model.addAttribute("exibeMargem", exibeMargem);
        model.addAttribute("podeMostrarMargem", podeMostrarMargem);
        model.addAttribute("rseMargemRest", rseMargemRest);
        model.addAttribute("margemConsignavel", margemConsignavel);
        model.addAttribute("margemDisponivel", margemDisponivel);
        model.addAttribute("adePeriodicidade", adePeriodicidade);
        model.addAttribute("permiteEscolherPeriodicidade", permiteEscolherPeriodicidade);
        model.addAttribute("permiteSimularSemMargem", permiteSimularSemMargem);
        model.addAttribute("alteraAdeVlr", alteraAdeVlr);
        model.addAttribute("adeVlrPadrao", adeVlrPadrao);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("txtExplicativo", txtExplicativo);
        /*****************************************************************************************************/
        return viewRedirect("jsp/simularConsignacao/simularConsignacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=simular" })
    public String simular(@RequestParam(value = "PRZ_VLR", required = true, defaultValue = "") String przVlr, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean paramTipoSimuPorOperacao = (ParamSist.getInstance().getParam(CodedValues.TPC_SIMULADOR_COM_CET_TIPO_OPERACAO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_SIMULADOR_COM_CET_TIPO_OPERACAO, responsavel));
        final boolean vlrTotalPg = (ParamSist.getInstance().getParam(CodedValues.TPC_EXIBIR_COLUNA_VLR_TOTAL_PAGAMENTO_NA_SIMULACAO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_EXIBIR_COLUNA_VLR_TOTAL_PAGAMENTO_NA_SIMULACAO, responsavel)) && responsavel.isSer();

        final String svcCodigo = request.getParameter("SVC_CODIGO");
        final String rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : request.getParameter("RSE_CODIGO"));
        String titulo = request.getParameter("titulo");
        String adeVlr = request.getParameter("ADE_VLR");
        String vlrLiberado = request.getParameter("VLR_LIBERADO");
        final boolean tpcSolicitarPortabilidadeRanking = Boolean.parseBoolean(request.getParameter("tpcSolicitarPortabilidadeRanking"));
        final String adeCodigo = request.getParameter("ADE_CODIGO");

        iniciarSimulacao(svcCodigo, rseCodigo, titulo, request, response, session, model);

        // Tenta realizar o decode para casos onde é possivel que possua XSS
        try {
			titulo = TextHelper.decode64(titulo);
		} catch (final Exception ex) {
            LOG.info(ex.getMessage(), ex);
		}

        model.addAttribute("titulo", titulo);

        if (!TextHelper.isNull(adeVlr)) {
            try {
                adeVlr = NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en");
            } catch (final java.text.ParseException ex) {
                adeVlr = "";
            }
        }
        if (!TextHelper.isNull(vlrLiberado)) {
            try {
                vlrLiberado = NumberHelper.reformat(vlrLiberado, NumberHelper.getLang(), "en");
            } catch (final java.text.ParseException ex) {
                vlrLiberado = "";
            }
        }

        if (tpcSolicitarPortabilidadeRanking) {
            responsavel.setFunCodigo(CodedValues.FUN_SOLICITAR_PORTABILIDADE);
        }

        List<TransferObject> simulacao = null;
        try {
            simulacao = ranking(adeVlr, vlrLiberado, request, response, session, model);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/simularConsignacao/simularConsignacao", request, session, model, responsavel);
        }

        final String qtdeColunasSimulacao = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel))
                ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel).toString()
                : "4";
        final float floatQtdeColunasSimulacao = Float.parseFloat(qtdeColunasSimulacao);

        boolean taxaJurosManCsa = false;
        for (final TransferObject simulacaoAnalise : simulacao) {
            final BigDecimal cftVlrRef = !TextHelper.isNull(simulacaoAnalise.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(simulacaoAnalise.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
            if (!TextHelper.isNull(cftVlrRef)) {
                taxaJurosManCsa = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, CodedValues.TPC_SIM, responsavel);
                break;
            }
        }

        boolean posibilitaLeilaoReverso = false;
        for (final TransferObject simulacaoLeilaoReverso : simulacao) {
            if ((boolean) simulacaoLeilaoReverso.getAttribute("OK")) {
                //So habilita leilão reverso caso ao menos exista um OK
                posibilitaLeilaoReverso = true;
                break;
            }
        }

        // Parâmetros de serviço
        final boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString());
        ParamSvcTO paramSvcCse = null;
        MargemDisponivel margemDisponivel = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
        } catch (ViewHelperException | ParametroControllerException ex) {
            throw new ZetraException(ex);
        }

        final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

        //Se não tem simulação disponivel e marger é negativa e o parametro de 331 esta desabilitado, gera mensagem de erro
        if (!posibilitaLeilaoReverso && (rseMargemRest.compareTo(new BigDecimal(0)) < 0) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.ranking.csa.servidor.sem.margem", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verificamos quais Consignatárias permitem ser contactadas
        final List<String> csaCodigos = new ArrayList<>();
        for(final TransferObject dadosSimulacao : simulacao) {
            csaCodigos.add((String) dadosSimulacao.getAttribute(Columns.CSA_CODIGO));
        }

        final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
        final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

        for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
            hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
        }

    	try {
    		final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            model.addAttribute("serNome", servidor.getAttribute(Columns.SER_NOME).toString());
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("vlrTotalPg", vlrTotalPg);
        model.addAttribute("posibilitaLeilaoReverso", posibilitaLeilaoReverso);
        model.addAttribute("simulacao", simulacao);
        model.addAttribute("floatQtdeColunasSimulacao", floatQtdeColunasSimulacao);
        model.addAttribute("mensagem", responsavel.isSer() ? ApplicationResourcesHelper.getMessage("mensagem.observacao.taxa.politica.instituicao", responsavel) : "");
        model.addAttribute("temCET", ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel));
        model.addAttribute("simulacaoPorTaxaJuros", ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel));
        model.addAttribute("simulacaoMetodoMexicano", ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel));
        model.addAttribute("simulacaoMetodoBrasileiro", ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel));
        model.addAttribute("SIMULACAO_POR_ADE_VLR", !TextHelper.isNull(adeVlr));
        model.addAttribute("podeSolicitar", responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO));
        model.addAttribute("keyPair", LoginHelper.getRSAKeyPair(request));
        model.addAttribute("taxaJurosManCSA", taxaJurosManCsa);
        model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);
        model.addAttribute("tpcSolicitarPortabilidadeRanking", tpcSolicitarPortabilidadeRanking);
        model.addAttribute("ADE_CODIGO", adeCodigo);

        if (responsavel.isSer() && paramTipoSimuPorOperacao && !tpcSolicitarPortabilidadeRanking) {
            return viewRedirect("jsp/simularConsignacao/visualizarRankingSimulacaoCET", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/simularConsignacao/visualizarRankingSimulacao", request, session, model, responsavel);
        }
    }

    protected List<TransferObject> ranking(String adeVlr, String vlrLiberado, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

        final String svcCodigo = request.getParameter("SVC_CODIGO");
        final String przVlr = request.getParameter("PRZ_VLR");
        final String rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : request.getParameter("RSE_CODIGO"));
        final String orgCodigo = request.getParameter("ORG_CODIGO");
        final String adePeriodicidade = request.getParameter("adePeriodicidade");
        final boolean tpcSolicitarPortabilidadeRanking = Boolean.parseBoolean(request.getParameter("tpcSolicitarPortabilidadeRanking"));

        // Parâmetros de serviço
        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            throw ex;
        }

        final int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
        final boolean tpsExigenciaConfirmacaoLeituraServidor = paramSvcCse.isTpsExigenciaConfirmacaoLeituraServidor();

        final boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString());
        MargemDisponivel margemDisponivel = null;
        try {
            final Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
        } catch (final ViewHelperException ex) {
            throw new ZetraException(ex);
        }

        final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

        boolean vlrOk = true;
        if (!TextHelper.isNull(adeVlr)) {
            vlrOk = (rseMargemRest.compareTo(new BigDecimal(adeVlr)) >= 0) || permiteSimularSemMargem;
            if (!vlrOk) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.margem.valor.prestacao.maior.margem.disponivel", responsavel));
            }
        }

        List<TransferObject> simulacao = null;
        Short numParcelas = null;
        try {
            numParcelas = Short.parseShort(przVlr);
            if (numParcelas <= 0) {
                throw new ZetraException("mensagem.informe.ade.prazo", responsavel);
            }
        } catch (final NumberFormatException ex) {
            throw new ZetraException("mensagem.informe.ade.prazo", responsavel, ex);
        }

        try {
            if (!TextHelper.isNull(przVlr) && ((!TextHelper.isNull(adeVlr) && vlrOk) || !TextHelper.isNull(vlrLiberado))) {
                simulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, "".equals(adeVlr) ? null : new BigDecimal(adeVlr), "".equals(vlrLiberado) ? null : new BigDecimal(vlrLiberado), numParcelas, null, true, adePeriodicidade, responsavel);
                if (tpcSolicitarPortabilidadeRanking) {
                    final BigDecimal valorContrato = !TextHelper.isNull(adeVlr) ? new BigDecimal(adeVlr) : new BigDecimal(vlrLiberado);

                    final String adeCodigo = request.getParameter("ADE_CODIGO");
                    final TransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    final String csaCodigo = (String) autdes.getAttribute(Columns.CSA_CODIGO);

                    simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, valorContrato, qtdeConsignatariasSimulacao, false, true, csaCodigo, CodedValues.FUN_SOLICITAR_PORTABILIDADE, responsavel);
                } else {
                    simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);
                }
            } else {
                // Evita NPE nas interfaces de listagem do ranking
                simulacao = new ArrayList<>();
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }

        String adeVlrFormatado = "";
        try {
            if (!TextHelper.isNull(adeVlr)) {
                adeVlrFormatado = NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang());
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        String vlrLiberadoFormatado = "";
        try {
            if (!TextHelper.isNull(vlrLiberado)) {
                vlrLiberadoFormatado = NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang());
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("ADE_VLR_FORMATADO", adeVlrFormatado);
        model.addAttribute("VLR_LIBERADO_FORMATADO", vlrLiberadoFormatado);

        final boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        final boolean teveValorParcelaForaMargem = exibeCETMinMax ? simulacao.stream().anyMatch(coeficiente -> coeficiente.getAttribute("VLR_PARCELA_FORA_MARGEM").equals(true)) : false;
        final boolean vlrLiberadoOk = exibeCETMinMax ? !teveValorParcelaForaMargem : true;

        model.addAttribute("SVC_CODIGO", svcCodigo);
        model.addAttribute("ADE_VLR", adeVlr);
        model.addAttribute("PRZ_VLR", przVlr);
        model.addAttribute("VLR_LIBERADO", vlrLiberado);
        model.addAttribute("RSE_CODIGO", rseCodigo);
        model.addAttribute("ORG_CODIGO", orgCodigo);
        model.addAttribute("adePeriodicidade", adePeriodicidade);
        model.addAttribute("numParcelas", numParcelas);
        model.addAttribute("qtdeConsignatariasSimulacao", qtdeConsignatariasSimulacao);
        model.addAttribute("vlrOk", vlrOk);
        model.addAttribute("tpsExigenciaConfirmacaoLeituraServidor", tpsExigenciaConfirmacaoLeituraServidor);
        model.addAttribute("exibeCETMinMax", exibeCETMinMax);
        model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);

        return simulacao;
    }

    @Override
    protected String executarFuncaoAposDuplicidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final String svcCodigo = request.getParameter("SVC_CODIGO");
        final String csaCodigo = request.getParameter("CSA_CODIGO");
        return confirmar(svcCodigo, csaCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmar(@RequestParam(value = "SVC_CODIGO", required = true, defaultValue = "") String svcCodigo, @RequestParam(value = "CSA_CODIGO", required = true, defaultValue = "") String csaCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String adeVlr = request.getParameter("ADE_VLR");
        final String adeVlrTac = request.getParameter("ADE_VLR_TAC");
        final String adeVlrIof = request.getParameter("ADE_VLR_IOF");
        final String ade_vlr_cat = request.getParameter("ADE_VLR_CAT"); // simulacaoMetodoMexicano
        final String adeVlrIva = request.getParameter("ADE_VLR_IVA"); // simulacaoMetodoMexicano
        final String cftCodigo = request.getParameter("CFT_CODIGO");
        final String dtjCodigo = request.getParameter("DTJ_CODIGO");
        final String vlrLiberado = request.getParameter("VLR_LIBERADO");
        final String przVlr = request.getParameter("PRZ_VLR");
        final String svcCodigoOrigem = request.getParameter("SVC_CODIGO_ORIGEM");

        final boolean simulacaoPorAdeVlr = Boolean.parseBoolean(request.getParameter("SIMULACAO_POR_ADE_VLR"));
        model.addAttribute("SIMULACAO_POR_ADE_VLR", simulacaoPorAdeVlr);
        final boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("vlrLiberadoOk"));
        model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);
        return confirmar(svcCodigo, svcCodigoOrigem, csaCodigo, adeVlr, adeVlrTac, adeVlrIof, ade_vlr_cat, adeVlrIva, cftCodigo, dtjCodigo, vlrLiberado, przVlr, false, request, response, session, model);
    }

    protected String confirmar(String svcCodigo, String svcCodigoOrigem, String csaCodigo, String adeVlr, String adeVlrTac, String adeVlrIof, String ade_vlr_cat, String adeVlrIva, String cftCodigo, String dtjCodigo, String vlrLiberado, String przVlr, boolean escolherOutroSvc, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String svcIdentificador = request.getParameter("SVC_IDENTIFICADOR");
            final String adePeriodicidade = request.getParameter("ADE_PERIODICIDADE"); // simulacaoMetodoMexicano
            final String tipo = request.getParameter("tipo");

            final boolean leilao = ((responsavel.getFunCodigo() != null) &&
                    (CodedValues.FUN_SOLICITAR_LEILAO_REVERSO.equals(responsavel.getFunCodigo()) ||
                            CodedValues.FUN_SOLICITAR_PORTABILIDADE.equals(responsavel.getFunCodigo())) && !ParamSist.paramEquals(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel));

            if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!validarDadosSimulacao(request, null, adeVlr, vlrLiberado, przVlr, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseCodigo = responsavel.getRseCodigo();
            String serCodigo = responsavel.getSerCodigo();
            String orgCodigo = responsavel.getOrgCodigo();

            if (!responsavel.isSer()) {
                rseCodigo = request.getParameter("RSE_CODIGO");
                CustomTransferObject servidor = null;
                try {
                    servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                    serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                    orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
                } catch (final ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            ServidorTransferObject servidor = null;
            try {
                servidor = servidorController.findServidor(serCodigo, responsavel);
                model.addAttribute("servidor", servidor);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Seleciona texto de instrução para o servidor cadastrado pela consignatária
            final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
            final String csaIdentificador = consignataria.getCsaIdentificador();

            final Integer prazo = (!TextHelper.isNull(przVlr) ? Integer.valueOf(przVlr) : null);
            BigDecimal valor = null;
            BigDecimal liberado = null;
            try {
                valor = (!TextHelper.isNull(adeVlr) ? new BigDecimal(NumberHelper.parse(adeVlr, "en")) : null);
                liberado = (!TextHelper.isNull(vlrLiberado) ? new BigDecimal(NumberHelper.parse(vlrLiberado, "en")) : null);
            } catch (final ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Cria lista com ade_codigo para portabilidade para que no podeReservarMargem, seja considerada a operação de portabilidade
            List<String> adeCodigosRenegociacao = null;
            if (request.getAttribute("adePortabilidade") != null) {
                final TransferObject adePortabilidade = (TransferObject) request.getAttribute("adePortabilidade");
                adeCodigosRenegociacao = new ArrayList<>(1);
                adeCodigosRenegociacao.add(adePortabilidade.getAttribute(Columns.ADE_CODIGO).toString());
            }

            CustomTransferObject convenio = null;
            String cnvCodigo = null;
            try {
                // Busca os dados do convênio
                convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                final String acao = ("simula_renegociacao".equals(tipo) ? "RENEGOCIAR" : "RESERVAR");

                final boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
                // Verifica se as entidades não estão bloqueadas
                autorizacaoController.podeReservarMargem(cnvCodigo, null, rseCodigo, true, true, true, adeCodigosRenegociacao, valor, liberado, prazo, 0, adePeriodicidade, null, null, acao, true, telaConfirmacaoDuplicidade, responsavel);

            } catch (final AutorizacaoControllerException ex) {
                final String messageKey = ex.getMessageKey();
                if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                    return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "confirmar", ex);
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String svcDescricao = (String) convenio.getAttribute(Columns.SVC_DESCRICAO);
            final String csaNome = (String) convenio.getAttribute(Columns.CSA_NOME);

            if (!"".equals(vlrLiberado)) {
                try {
                    vlrLiberado = NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang());
                } catch (final ParseException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            /****************************************************************************************************************/
            // Busca os parâmetros do serviço
            final List<String> tpsCsaCodigos = new ArrayList<>();
            tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
            tpsCsaCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

            ParamSvcTO paramSvcCse = null;
            List<TransferObject> paramSvcCsa = null;
            try {
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCsaCodigos, false, responsavel);
            } catch (final ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ParamSvcTO paramSvcCseOrigem = null;
            try {
                paramSvcCseOrigem = parametroController.getParamSvcCseTO(svcCodigoOrigem, responsavel);
            } catch (final ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean tpsExigenciaConfirmacaoLeituraServidor = paramSvcCseOrigem.isTpsExigenciaConfirmacaoLeituraServidor();
            if (tpsExigenciaConfirmacaoLeituraServidor) {
                final String exigenciaConfirmacao = request.getParameter("exigenciaConfirmacaoLeitura");
                model.addAttribute("exigenciaConfirmacaoLeitura", exigenciaConfirmacao);
                if (!"true".equals(exigenciaConfirmacao)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.simulacao.informar.confirmacao.leitura", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            final int carenciaMinCse = ((paramSvcCse.getTpsCarenciaMinima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMinima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
            final int carenciaMaxCse = ((paramSvcCse.getTpsCarenciaMaxima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMaxima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
            final boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic() && !leilao;
            final boolean exibirTabelaPrice = paramSvcCse.isTpsExibeTabelaPrice();
            String paramExibeCampoCidade = paramSvcCse.getTpsExibeCidadeConfirmacaoSolicitacao();
            if (TextHelper.isNull(paramExibeCampoCidade)) {
                paramExibeCampoCidade = CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO;
            }
            final boolean campoCidadeObrigatorio = (!leilao && CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO.equals(paramExibeCampoCidade)) ||
                    (leilao && CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO.equals(paramExibeCampoCidade));

            // Parâmetros de convênio
            final int carenciaMinima = ((convenio.getAttribute("CARENCIA_MINIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MINIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
            final int carenciaMaxima = ((convenio.getAttribute("CARENCIA_MAXIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MAXIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

            // Define os valores de carência mínimo e máximo
            final int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
            int carenciaMinPermitida = carenciaPermitida[0];

            final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);
            final boolean quinzenal = simulacaoMetodoMexicano && CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePeriodicidade);

            boolean exigeAssinaturaDigital = false;
            for (final TransferObject vo : paramSvcCsa) {
                if (CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                    exigeAssinaturaDigital = "S".equals(vo.getAttribute(Columns.PSC_VLR));
                }
                // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
                final CustomTransferObject param = (CustomTransferObject) vo;
                if (((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) && CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))){
				    final String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
				    switch (pscVlr) {
					case "E":
						model.addAttribute("enderecoObrigatorio", true);
						break;
					case "C":
						model.addAttribute("celularObrigatorio", true);
						break;
					case "EC":
						model.addAttribute("enderecoCelularObrigatorio", true);
						break;
					case null:
					default:
						break;
					}
				}
            }

            // Calcula a data inicial e final do contrato
            Date adeAnoMesIni = null;
            Date adeAnoMesFim = null;

            carenciaMinPermitida = parametroController.calcularAdeCarenciaDiaCorteCsa(carenciaMinPermitida, csaCodigo, orgCodigo, responsavel);
            try {
                adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, carenciaMinPermitida, adePeriodicidade, responsavel);

                final java.sql.Date dataInicioFimAde = autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, new java.sql.Date(adeAnoMesIni.getTime()), paramSvcCse.getTpsIncideMargem(), true, false, responsavel);
                boolean mensagemAlertaAlteracaoDataInicio = false;
                if((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(adeAnoMesIni) > 0)) {
                    adeAnoMesIni = dataInicioFimAde;
                    carenciaMinPermitida = 0;
                    mensagemAlertaAlteracaoDataInicio = true;
                }

                adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, Integer.valueOf(przVlr), adePeriodicidade, responsavel);
                autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, new java.sql.Date(adeAnoMesFim.getTime()), paramSvcCse.getTpsIncideMargem(), false, true, responsavel);

                if(mensagemAlertaAlteracaoDataInicio) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
                }
            } catch (final PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String dataIni = (adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "");
            final String dataFim = (adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "");

            final boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
            final boolean exigeMunicipioLotacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            // Verifica se permite servidor escolher correspondentes
            List<TransferObject> lstCorrespondentes = null;
            String permiteEscolherCorresp;
            try {
                permiteEscolherCorresp = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_SERVIDOR_ESCOLHER_COR_SIMULACAO, responsavel);
                if (!TextHelper.isNull(permiteEscolherCorresp) && "S".equalsIgnoreCase(permiteEscolherCorresp)) {
                    final CorrespondenteTransferObject cor = new CorrespondenteTransferObject();
                    cor.setCsaCodigo(csaCodigo);
                    cor.setCorAtivo(CodedValues.STS_ATIVO);
                    lstCorrespondentes = consignatariaController.lstCorrespondentes(cor, responsavel);
                }
            } catch (final ZetraException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject cft = null;
            String cftVlr = null;
            if (!TextHelper.isNull(dtjCodigo)) {
                try {
                    cft = simulacaoController.getDefinicaoTaxaJuros(dtjCodigo);
                    cftVlr = NumberHelper.format(((BigDecimal) cft.getAttribute(Columns.CFT_VLR)).doubleValue(), LocaleHelper.getLanguage());
                } catch (final SimulacaoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } else if (!TextHelper.isNull(cftCodigo)) {
                try {
                    cft = simulacaoController.getCoeficienteAtivo(cftCodigo);
                    cftVlr = NumberHelper.format(((BigDecimal) cft.getAttribute(Columns.CFT_VLR)).doubleValue(), LocaleHelper.getLanguage());
                } catch (final SimulacaoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            if (exibirTabelaPrice && !TextHelper.isNull(liberado) && !TextHelper.isNull(valor) && !TextHelper.isNull(prazo) && (cft != null)) {
                final CustomTransferObject autdes = new CustomTransferObject();
                autdes.setAttribute(Columns.ADE_CODIGO, "");
                autdes.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                autdes.setAttribute(Columns.ORG_CODIGO, orgCodigo);
                autdes.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                autdes.setAttribute(Columns.SVC_CODIGO, svcCodigo);
                autdes.setAttribute(Columns.CFT_CODIGO, cftCodigo);
                autdes.setAttribute(Columns.DTJ_CODIGO, dtjCodigo);
                autdes.setAttribute(Columns.ADE_VLR, valor);
                autdes.setAttribute(Columns.ADE_VLR_LIQUIDO, liberado);
                autdes.setAttribute(Columns.ADE_PRAZO, prazo);
                autdes.setAttribute(Columns.ADE_DATA, Calendar.getInstance().getTime());
                autdes.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
                autdes.setAttribute(Columns.ADE_ANO_MES_FIM, adeAnoMesFim);
                autdes.setAttribute(Columns.CFT_VLR, cft.getAttribute(Columns.CFT_VLR));

                model.addAttribute("autdes", autdes);
            }

            if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel)) {
                final int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                final TransferObject paramMensagemSolicitacao = simulacaoController.getParamSvcCsaMensagemSolicitacaoOutroSvc(svcCodigo, csaCodigo, prazo.shortValue(), (short) dia, responsavel);
                if (paramMensagemSolicitacao != null) {
                    model.addAttribute("mensagemSolicitacaoOutroSvc", paramMensagemSolicitacao.getAttribute(Columns.PSC_VLR));
                    model.addAttribute("nomeOutroSvc", paramMensagemSolicitacao.getAttribute(Columns.SVC_DESCRICAO));
                    model.addAttribute("novoCftCodigo", paramMensagemSolicitacao.getAttribute(Columns.CFT_CODIGO));
                    model.addAttribute("keyPair", LoginHelper.getRSAKeyPair(request));
                }
            }

            if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("termoConsentimentoDadosServidor", montarTermoConsentimentoDadosServidor(responsavel));
            }

            try {
                final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
                model.addAttribute("tdaList", tdaList);

                if ((tdaList != null) && !tdaList.isEmpty()) {
                    final Map<String, String> dadosAutorizacao = new HashMap<>();
                    for (final TransferObject tda : tdaList) {
                        final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                        final String tdaValor = autorizacaoController.getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
                        dadosAutorizacao.put(tdaCodigo, tdaValor);
                    }
                    model.addAttribute("dadosAutorizacao", dadosAutorizacao);
                }
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            int maxQtdArquivos;

            try {
                maxQtdArquivos = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel));
            } catch (final NumberFormatException e) {
                maxQtdArquivos = 15;
            }

            final String usuToken = usuarioController.gerarChaveSessaoUsuario(responsavel.getUsuCodigo(), responsavel); // UsuarioChaveSessaoHome.findByPrimaryKey(responsavel.getUsuCodigo());
            final boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

            final boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("vlrLiberadoOk"));
            if(!vlrLiberadoOk) {
	            final boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString());
	            MargemDisponivel margemDisponivel = null;
	            try {
	                final Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
	                margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
	                final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();
	                final List<TransferObject> simulacao = simulacaoController.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, rseMargemRest, null, Short.parseShort(przVlr), null, true, false, adePeriodicidade, responsavel);
	                for (final TransferObject simu : simulacao) {
                    	vlrLiberado = simu.getAttribute("VLR_LIBERADO").toString();
                    	adeVlr = simu.getAttribute("VLR_PARCELA").toString();
                        break;
	                }
	            } catch (final ViewHelperException ex) {
	                throw new ZetraException(ex);
	            }
            }

            model.addAttribute("usuToken", usuToken);
            model.addAttribute("exigeTelefone", exigeTelefone);
            model.addAttribute("exigeMunicipioLotacao", exigeMunicipioLotacao);
            model.addAttribute("campoCidadeObrigatorio", campoCidadeObrigatorio);
            model.addAttribute("exigeCodAutSolicitacao", exigeCodAutSolicitacao);
            model.addAttribute("tipo", tipo);
            model.addAttribute("exigeAssinaturaDigital", exigeAssinaturaDigital);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("vlrLiberado", vlrLiberado);
            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("przVlr", przVlr);
            model.addAttribute("cftVlr", cftVlr);
            model.addAttribute("carenciaMinPermitida", carenciaMinPermitida);
            model.addAttribute("dataIni", dataIni);
            model.addAttribute("dataFim", dataFim);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
            model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
            model.addAttribute("quinzenal", quinzenal);
            model.addAttribute("lstCorrespondentes", lstCorrespondentes);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("cftCodigo", cftCodigo);
            model.addAttribute("dtjCodigo", dtjCodigo);
            model.addAttribute("ade_vlr_cat", ade_vlr_cat);
            model.addAttribute("adeVlrIva", adeVlrIva);
            model.addAttribute("adeVlrTac", adeVlrTac);
            model.addAttribute("adeVlrIof", adeVlrIof);
            model.addAttribute("adePeriodicidade", adePeriodicidade);
            model.addAttribute("qtdMaximaArquivos", maxQtdArquivos);
            model.addAttribute("svcCodigoOrigem", svcCodigoOrigem);
            model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);

            final String chaveSeguranca = JspHelper.verificaVarQryStr(request, "chaveSeguranca");
            if (!TextHelper.isNull(chaveSeguranca)) {
                model.addAttribute("chaveSeguranca", chaveSeguranca);
            }

            //Envia o código de autorização enviado por SMS ao Servidor.
            final boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
            if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
            	usuarioController.enviarCodigoAutorizacaoSms(rseCodigo, responsavel);
            }

            if (escolherOutroSvc) {
                // Exibir também uma mensagem de alerta ressaltando que os valores da solicitação foram atualizados para contemplar o serviço contratado.
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.solicitar.consignacao.valores.atualizados", responsavel));
            }

            try {
                if(parametroController.isExigeReconhecimentoFacialServidor(svcCodigo, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reconhecimentoFacialServidorSimulacao"))
                        && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "exigeReconhecimentoFacil"))) {
                    model.addAttribute("exigeReconhecimentoFacil", "true");
                }

                if(parametroController.isSimularConsignacaoComReconhecimentoFacialELiveness(svcCodigo, responsavel)){
                    model.addAttribute("simularConsignacaoComReconhecimentoFacialELiveness", "true");
                }
            } catch (final ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verificamos quais Consignatárias permitem ser contactadas
            final List<String> csaCodigos = new ArrayList<>();
            csaCodigos.add(csaCodigo);

            final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
            final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

            for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
                hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
            }
            model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);
            model.addAttribute("anexoObrigatorio", parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel));
            model.addAttribute("qtdeMinAnexos", paramSvcCse.getTpsQuantidadeMinimaInclusaoContratos());

            return viewRedirect("jsp/simularConsignacao/confirmarSimulacao", request, session, model, responsavel);
        } catch (NumberFormatException | ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    @RequestMapping(params = { "acao=emitirBoleto" })
    public String emitirBoleto(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reconhecimentoFacial"))) {
            model.addAttribute("exigeReconhecimentoFacil", "true");
        }

        final UploadHelper uploadHelper = new UploadHelper();
        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel);
        final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200);

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
        } catch (final Throwable ex) {
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        Boolean aceitoTermoUsoColetaDados = null;
        if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            if (!"S".equals(JspHelper.verificaVarQryStr(request, uploadHelper, "aceitoTermoUsoColetaDados"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.termo.de.consentimento.coleta.dados.servidor.alerta", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            aceitoTermoUsoColetaDados = true;
        }

        final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
        final boolean simulacaoMetodoIndiano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_INDIANO, responsavel);

        final String csaCodigo = uploadHelper.getValorCampoFormulario("CSA_CODIGO");
        final String corCodigo = uploadHelper.getValorCampoFormulario("COR_CODIGO");
        final String svcCodigo = uploadHelper.getValorCampoFormulario("SVC_CODIGO");
        final String titulo = uploadHelper.getValorCampoFormulario("titulo");

        String rseCodigo = responsavel.getRseCodigo();
        String serCodigo = responsavel.getSerCodigo();
        String orgCodigo = null;
        final String cftCodigo = uploadHelper.getValorCampoFormulario("CFT_CODIGO");
        final String dtjCodigo = uploadHelper.getValorCampoFormulario("DTJ_CODIGO");
        final String strVlrLiberado = uploadHelper.getValorCampoFormulario("VLR_LIBERADO");
        final String strRanking = uploadHelper.getValorCampoFormulario("RANKING");

        BigDecimal vlrLiberado = null;
        BigDecimal vlrTac = null;
        BigDecimal vlrIof = null;
        final String adeVlr = uploadHelper.getValorCampoFormulario("ADE_VLR");
        final String adeVlrTac = uploadHelper.getValorCampoFormulario("ADE_VLR_TAC");
        final String adeVlrIof = uploadHelper.getValorCampoFormulario("ADE_VLR_IOF");
        final String adeCarencia = uploadHelper.getValorCampoFormulario("ADE_CARENCIA");
        final String przVlr = uploadHelper.getValorCampoFormulario("PRZ_VLR");
        final String adePeriodicidade = uploadHelper.getValorCampoFormulario("ADE_PERIODICIDADE");
        Short ranking = null;

        final String svcCodigoOrigem = uploadHelper.getValorCampoFormulario("SVC_CODIGO_ORIGEM");
        final String adeCodigoPortabilidade = uploadHelper.getValorCampoFormulario("ADE_CODIGO_PORTABILIDADE");
        List<String> adeCodigosRenegociacao = null;
        if (!TextHelper.isNull(adeCodigoPortabilidade)) {
            adeCodigosRenegociacao = new ArrayList<>(1);
            adeCodigosRenegociacao.add(adeCodigoPortabilidade);
        }

        java.sql.Date dataNascimento = null;
        java.sql.Date dataEmissaoIdt = null;
        java.sql.Date dataAdmissaoSql = null;
        Timestamp dataAdmissao = null;
        try {
            dataNascimento = !TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_DATA_NASC")) ? DateHelper.toSQLDate(DateHelper.parse(uploadHelper.getValorCampoFormulario("SER_DATA_NASC"), LocaleHelper.getDatePattern())) : null;
            dataEmissaoIdt = !TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_DATA_IDT")) ? DateHelper.toSQLDate(DateHelper.parse(uploadHelper.getValorCampoFormulario("SER_DATA_IDT"), LocaleHelper.getDatePattern())) : null;
            dataAdmissaoSql = !TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_DATA_ADMISSAO")) ? DateHelper.toSQLDate(DateHelper.parse(uploadHelper.getValorCampoFormulario("SER_DATA_ADMISSAO"), LocaleHelper.getDatePattern())) : null;

            if (dataAdmissaoSql != null) {
                dataAdmissao = new Timestamp(dataAdmissaoSql.getTime());
            }
        } catch (final ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!responsavel.isSer()) {
            rseCodigo = uploadHelper.getValorCampoFormulario("RSE_CODIGO");
        }

        try {
            if (responsavel.isSer()) {
                validaInformacoesServidorObrigatorias(uploadHelper, svcCodigoOrigem, csaCodigo, responsavel);
            }
        } catch (final SimulacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo) || TextHelper.isNull(titulo) || TextHelper.isNull(strVlrLiberado) || TextHelper.isNull(strRanking)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else {
            	vlrLiberado = new BigDecimal(NumberHelper.reformat(strVlrLiberado, NumberHelper.getLang(), "en"));
            	vlrTac = new BigDecimal(((adeVlrTac != null) && !"".equals(adeVlrTac)) ? NumberHelper.reformat(adeVlrTac, NumberHelper.getLang(), "en") : "0.00");
                vlrIof = new BigDecimal(((adeVlrIof != null) && !"".equals(adeVlrIof)) ? NumberHelper.reformat(adeVlrIof, NumberHelper.getLang(), "en") : "0.00");
                ranking = Short.valueOf(strRanking);
            }

            if (!validarDadosSimulacao(request, uploadHelper, adeVlr, vlrLiberado.toPlainString(), przVlr, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final CustomTransferObject servidorTO = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            serCodigo = servidorTO.getAttribute(Columns.SER_CODIGO).toString();
            orgCodigo = servidorTO.getAttribute(Columns.ORG_CODIGO).toString();
            boolean leilao = "leilao".equals(uploadHelper.getValorCampoFormulario("tipo"));

            final boolean exigeAssinaturaDigital = Boolean.parseBoolean(uploadHelper.getValorCampoFormulario("exigeAssinaturaDigital"));
            final CustomTransferObject paramSvcQntdeMinAnexos = autorizacaoController.getParametroSvc(CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS, svcCodigo, svcCodigo, false, null);
            final String strQntdadeMinAnexos = (String) paramSvcQntdeMinAnexos.getAttribute(Columns.PSE_VLR);
            final Integer qntdadeMinAnexos = !TextHelper.isNull(strQntdadeMinAnexos) ? Integer.valueOf(strQntdadeMinAnexos) : 0;
            final boolean exigeAnexoServidor = parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel) && (qntdadeMinAnexos > 0);

            // Salva os dados do servidor
            // Insere a reserva
            // Ok para inclusão da reserva.
            if (uploadHelper.getValorCampoFormulario("SER_CODIGO") != null) {
                final boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
                if (exigeTelefone && TextHelper.isNull(uploadHelper.getValorCampoFormulario("TDA_25"))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone.solicitacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                boolean comSerSenha = false;
                String senhaAberta = null;

                if (ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) {
                    final boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

                    try {
                        // Se valida senha do servidor, recupera e seta na sessão para ser validada posteriormente
                        final String senha = uploadHelper.getValorCampoFormulario(uploadHelper.getValorCampoFormulario("cryptedPasswordFieldName"));
                        final String serLogin = uploadHelper.getValorCampoFormulario("serLogin");

                        if (!TextHelper.isNull(senha)) {
                            session.setAttribute("serAutorizacao", senha);
                        }
                        if (!TextHelper.isNull(serLogin)) {
                            session.setAttribute("serLogin", serLogin);
                        }

                        SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, serSenhaObrigatoria, true, responsavel);
                        comSerSenha = true;
                        senhaAberta = (String) request.getAttribute("senhaServidorOK");
                    } catch (final ViewHelperException ex) {
                        // Paraná: ao receber 'senha expirada' a CSA poderá ativar a senha.
                        if (ex.getMessageKey().indexOf("mensagem.erro.senha.expirada.certifique.ativacao") != -1) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.expirada.ativar", responsavel));
                            // Redireciona para JSP específico de ativação de senha eConsig PR
                            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/ativarSenhaServidor?acao=iniciar", request)));
                            return "jsp/redirecionador/redirecionar";
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                // Monta dados do servidor para ser alterado
                final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)) {
                    servidorUpd.setSerEnd(uploadHelper.getValorCampoFormulario("SER_END"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
                    servidorUpd.setSerTel(uploadHelper.getValorCampoFormulario("SER_TEL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)) {
                    servidorUpd.setSerCompl(uploadHelper.getValorCampoFormulario("SER_COMPL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)) {
                    servidorUpd.setSerBairro(uploadHelper.getValorCampoFormulario("SER_BAIRRO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)) {
                    servidorUpd.setSerCidade(uploadHelper.getValorCampoFormulario("SER_CIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)) {
                    servidorUpd.setSerUf(uploadHelper.getValorCampoFormulario("SER_UF"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)) {
                    servidorUpd.setSerCep(uploadHelper.getValorCampoFormulario("SER_CEP"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)) {
                    servidorUpd.setSerDataNasc(dataNascimento);
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                    servidorUpd.setSerSexo(uploadHelper.getValorCampoFormulario("SER_SEXO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
                    servidorUpd.setSerNroIdt(uploadHelper.getValorCampoFormulario("SER_NRO_IDT"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
                    servidorUpd.setSerDataIdt(dataEmissaoIdt);
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
                    servidorUpd.setSerCelular(uploadHelper.getValorCampoFormulario("SER_CEL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
                    servidorUpd.setSerNacionalidade(uploadHelper.getValorCampoFormulario("SER_NACIONALIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
                    servidorUpd.setSerCidNasc(uploadHelper.getValorCampoFormulario("SER_NATURALIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)) {
                    servidorUpd.setSerUfNasc(uploadHelper.getValorCampoFormulario("SER_UF_NASCIMENTO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)) {
                    servidorUpd.setSerNro(uploadHelper.getValorCampoFormulario("SER_NRO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel)) {
                    servidorUpd.setSerEmail(uploadHelper.getValorCampoFormulario("SER_EMAIL"));
                }

                // Monta dados do registro servidor para ser alterado
                final RegistroServidorTO registroServidorUpd = new RegistroServidorTO(rseCodigo);
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
                    registroServidorUpd.setRseAgenciaSalAlternativa(uploadHelper.getValorCampoFormulario("SER_IBAN"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
                    registroServidorUpd.setRseSalario(new BigDecimal((!TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_SALARIO")) ? NumberHelper.reformat(uploadHelper.getValorCampoFormulario("SER_SALARIO").toString(), NumberHelper.getLang(), "en") : "0.00")));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                    registroServidorUpd.setRseDataAdmissao(dataAdmissao);
                }
                registroServidorUpd.setRseMunicipioLotacao(uploadHelper.getValorCampoFormulario("RSE_MUNICIPIO_LOTACAO"));

                // Valida a reserva de Margem
                AutorizacaoHelper.validarValorAutorizacao(new BigDecimal(adeVlr), svcCodigo, csaCodigo, responsavel);

                final CustomTransferObject convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();

                // Dados da consignação
                final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
                final Map<String, String> dadosAutorizacao = new HashMap<>();
                for (final TransferObject tda : tdaList) {
                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    dadosAutorizacao.put(tdaCodigo, JspHelper.parseValor(request, uploadHelper, "TDA_" + tdaCodigo, (String) tda.getAttribute(Columns.TDA_DOMINIO)));
                }

                // Objeto de parâmetro de reserva de margem
                final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

                reservaParam.setRseCodigo(rseCodigo);
                reservaParam.setAdeVlr(new BigDecimal(adeVlr));
                reservaParam.setAdePrazo(Integer.valueOf(przVlr));
                reservaParam.setAdeCarencia(Integer.valueOf(adeCarencia));
                reservaParam.setAdeIdentificador(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel));
                reservaParam.setCnvCodigo(cnvCodigo);
                reservaParam.setCorCodigo(!TextHelper.isNull(corCodigo) ? corCodigo : null);
                reservaParam.setSerSenha(senhaAberta);
                reservaParam.setComSerSenha(comSerSenha);
                if (!simulacaoMetodoMexicano && !simulacaoMetodoIndiano) {
                    reservaParam.setAdeVlrTac(vlrTac);
                    reservaParam.setAdeVlrIof(vlrIof);
                }
                reservaParam.setAdeVlrLiquido(vlrLiberado);
                reservaParam.setValidar(Boolean.FALSE);
                reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                reservaParam.setSerAtivo(Boolean.TRUE);
                reservaParam.setCnvAtivo(Boolean.TRUE);
                reservaParam.setSerCnvAtivo(Boolean.TRUE);
                reservaParam.setSvcAtivo(Boolean.TRUE);
                reservaParam.setSvcCodigoOrigem(svcCodigoOrigem);
                reservaParam.setCsaAtivo(Boolean.TRUE);
                reservaParam.setOrgAtivo(Boolean.TRUE);
                reservaParam.setEstAtivo(Boolean.TRUE);
                reservaParam.setCseAtivo(Boolean.TRUE);
                reservaParam.setAcao("RESERVAR");
                reservaParam.setCftCodigo(cftCodigo);
                reservaParam.setDtjCodigo(dtjCodigo);
                reservaParam.setCdeVlrLiberado(vlrLiberado);
                reservaParam.setCdeRanking(ranking);
                reservaParam.setCdeTxtContato(uploadHelper.getValorCampoFormulario("CDE_TXT_CONTATO"));
                reservaParam.setAdePeriodicidade(adePeriodicidade);
                // Seta os dados genéricos que o responsável tem permissão de alterar
                reservaParam.setDadosAutorizacaoMap(dadosAutorizacao);
                // telefone informado na solicitação deve ser salvo como dado de autorização TDA_SOLICITACAO_TEL_SERVIDOR
                reservaParam.setTdaTelSolicitacaoSer(uploadHelper.getValorCampoFormulario("TDA_25"));

                final String exigenciaConfirmacaoLeitura = uploadHelper.getValorCampoFormulario("exigenciaConfirmacaoLeitura");
                if (exigenciaConfirmacaoLeitura != null) {
                    reservaParam.setExigenciaConfirmacaoLeitura(exigenciaConfirmacaoLeitura);
                }

                //Seta o parâmetro para leilão reverso.
                final String termoAceite = uploadHelper.getValorCampoFormulario("TERMO_ACEITE_LEILAO");
                if ("SIM".equals(termoAceite)) {
                    final Boolean simulacaoPorAdeVlr = Boolean.parseBoolean(uploadHelper.getValorCampoFormulario("SIMULACAO_POR_ADE_VLR"));
                    reservaParam.setSimulacaoPorAdeVlr(simulacaoPorAdeVlr);
                    reservaParam.setIniciarLeilaoReverso(true);
                    leilao = true;

                } else {
                    reservaParam.setIniciarLeilaoReverso(false);
                }
                // Cidade para efetivação/assinatura do contrato
                final String cidNome = uploadHelper.getValorCampoFormulario("CID_NOME");
                final String cidCodigo = uploadHelper.getValorCampoFormulario("CID_CODIGO");
                if (!TextHelper.isNull(cidNome) && !TextHelper.isNull(cidCodigo)) {
                    reservaParam.setCidCodigo(cidCodigo);
                }

                reservaParam.setTelaConfirmacaoDuplicidade("S".equals(uploadHelper.getValorCampoFormulario("telaConfirmacaoDuplicidade")));
                reservaParam.setChkConfirmarDuplicidade(!TextHelper.isNull(uploadHelper.getValorCampoFormulario("chkConfirmarDuplicidade")));
                reservaParam.setMotivoOperacaoCodigoDuplicidade(uploadHelper.getValorCampoFormulario("TMO_CODIGO"));
                reservaParam.setMotivoOperacaoObsDuplicidade(uploadHelper.getValorCampoFormulario("ADE_OBS"));
                reservaParam.setAceitoTermoUsoColetaDados(aceitoTermoUsoColetaDados);
                reservaParam.setAdeCodigosRenegociacao(adeCodigosRenegociacao);

                //Valida o código de autorização enviado por SMS ao Servidor.
                final boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
                if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
                    final String codAut = uploadHelper.getValorCampoFormulario("codAutorizacao");
                    usuarioController.validarCodigoAutorizacaoSms(codAut, responsavel);
                }

                if (exigeAssinaturaDigital && !leilao) {
                    final List<String> nomeArquivos = uploadHelper.getNomeCamposArquivos();

                    for (int i = 0; i < nomeArquivos.size(); i++) {
                        final String nomeArquivo = uploadHelper.getFileName(i);

                        if (TextHelper.isNull(nomeArquivo)) {
                            continue;
                        }

                        boolean extensaoValida = false;
                        for (final String extensoesArquivoPermitida : UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO) {
                            if (nomeArquivo.toLowerCase().endsWith(extensoesArquivoPermitida.toLowerCase())) {
                                extensaoValida = true;
                                break;
                            }
                        }

                        if (!extensaoValida) {
                            throw new ZetraException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO, ", "));
                        }

                    }
                }

                // Insere solicitação
                adeCodigo = inserirSolicitacaoController.solicitarReservaMargem(reservaParam, servidorUpd, registroServidorUpd, svcCodigo, responsavel);

                // Invalidar o token enviado via SMS
                if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
                    final TransferObject usuario = new CustomTransferObject();
                    usuario.setAttribute(Columns.USU_CODIGO, responsavel.getUsuCodigo());
                    usuarioController.limparDadosOTP(usuario , responsavel);
                }
            }

            //Salva anexos
            if (exigeAssinaturaDigital && !leilao) {
                Map<String, File> novosAnexos = null;
                final String path = "anexo" + File.separatorChar + DateHelper.format(Calendar.getInstance().getTime(), "yyyyMMdd") + File.separatorChar + adeCodigo;
                novosAnexos = uploadHelper.salvarArquivos(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO, null);

                if (novosAnexos == null) {
                    throw new ZetraException("mensagem.erro.upload.arquivo.assinatura.digital.selecione.arquivo", responsavel);
                }

                for (final File anexo : novosAnexos.values()) {
                    if ((anexo != null) && anexo.exists()) {
                        try {
                            final String aadDescricao = anexo.getName();
                            final TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);
                            final Date aadPeriodo = (Date) ade.getAttribute(Columns.ADE_ANO_MES_INI);
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, aadPeriodo != null ? new java.sql.Date(aadPeriodo.getTime()) : null, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO, responsavel);
                        } catch (final Exception ex) {
                            anexo.delete();
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.assinatura.digital.selecione.arquivo", responsavel));
                    }
                }

                // Cria solicitação do tipo crédito eletrônico
                if (!inserirSolicitacaoController.temSolicitacaoAutorizacao(adeCodigo, TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO, StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS, responsavel)) {
                	inserirSolicitacaoController.incluirSolicitacaoCreditoEletronico(adeCodigo, responsavel);
                }
            }

            // Anexo Servidor Obrigatório
            final String nomeAnexo = uploadHelper.getValorCampoFormulario("FILE1");
            String aadDescricao = uploadHelper.getValorCampoFormulario("AAD_DESCRICAO");
            if (exigeAnexoServidor && !TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(adeCodigo) && (qntdadeMinAnexos > 0)) {
                String [] anexosName;
                File anexo = null;
                anexosName = nomeAnexo.split(";");

                if((qntdadeMinAnexos > 0) && (anexosName.length < qntdadeMinAnexos)) {
                    throw new AutorizacaoControllerException("mensagem.erro.upload.arquivo.qunt.min", responsavel, strQntdadeMinAnexos);
                }
                final TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);
                final Date aadPeriodo = (Date) ade.getAttribute(Columns.ADE_ANO_MES_INI);
                for (final String nomeAnexoCorrente : anexosName) {
                    anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigo, session.getId(), responsavel);
                    if ((anexo != null) && anexo.exists()) {
                        aadDescricao = (!TextHelper.isNull(aadDescricao) && (aadDescricao.length() <= 255)) ? aadDescricao : anexo.getName();
                        editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, aadPeriodo != null ? new java.sql.Date(aadPeriodo.getTime()) : null, TipoArquivoEnum.ARQUIVO_ANEXO_CONTRATO, responsavel);
                    }
                }
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            model.addAttribute("botaoVoltarPaginaInicial", Boolean.TRUE);
            if (responsavel.isSer() && leilao) {
                model.addAttribute("tipo", "servidor");
                model.addAttribute("_skip_history_", "true");
                final ParamSession paramSession = ParamSession.getParamSession(session);
                paramSession.backToFirst();
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));

                if ((responsavel.getFunCodigo() != null) && CodedValues.FUN_SOLICITAR_PORTABILIDADE.equals(responsavel.getFunCodigo())) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.solicitar.portabilidade", responsavel));
                } else {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.solicitacao.incluida", responsavel));
                }

                return detalharConsignacao(TextHelper.forHtmlAttribute(adeCodigo), request, response, session, model);
            }

            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reconhecimentoFacial"))) {
                model.addAttribute("exigeReconhecimentoFacil", "false");
            }

            return super.emitirBoleto(adeCodigo, request, response, session, model);
        } catch (ZetraException | ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    @RequestMapping(params = { "acao=emitirBoletoExterno" })
    public String emitirBoletoExterno(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            model.addAttribute("botaoVoltarPaginaInicial", Boolean.TRUE);
            return super.emitirBoletoExterno(adeCodigo, request, response, session, model);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarServicos" })
    public String listarServicos(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Lista os serviços que possuem prazos ativos com coeficientes cadastrados
        List<TransferObject> servicos = null;
        final String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
        try {
            // Pega o código do órgão do servidor
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            if(!TextHelper.isNull(corCodigo)) {
                servicos = simulacaoController.lstServicosSimulacao(null, null, servidor.getAttribute(Columns.ORG_CODIGO).toString(), dia, corCodigo, responsavel);
            } else {
                servicos = simulacaoController.lstServicosSimulacao(null, servidor.getAttribute(Columns.ORG_CODIGO).toString(), dia, responsavel);
            }
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (servicos == null) {
            servicos = new ArrayList<>();
        } else if (servicos.size() == 1) {
            // Se encontrou apenas um serviço, redireciona para tela de simulação
            final CustomTransferObject servico = (CustomTransferObject) servicos.get(0);
            final String svcCodigo = (String) servico.getAttribute(Columns.SVC_CODIGO);
            final String svcDescricao = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
            return iniciarSimulacao(svcCodigo, rseCodigo, svcDescricao, request, response, session, model);
        }

        model.addAttribute("servicos", servicos);
        model.addAttribute("RSE_CODIGO", rseCodigo);

        return viewRedirect("jsp/simularConsignacao/listarServicosSimulacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=listarServicosServidor" })
    public String listarServicosServidor(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String orgCodigo = responsavel.getOrgCodigo();
        List<TransferObject> servicosReserva = null;

        // Busca Lista de serviços disponíveis para solicitação pelo servidor
        try {
            final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
            final boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
            final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
            servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);
        } catch (final ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("servicosReserva", definirServicosSolicitacao(servicosReserva, request));

        return viewRedirect("jsp/simularConsignacao/listarServicosSimulacaoServidor", request, session, model, responsavel);

    }

    protected List<ServicoSolicitacaoServidor> definirServicosSolicitacao(List<TransferObject> servicosReserva, HttpServletRequest request) {

        final List<ServicoSolicitacaoServidor> servicos = new ArrayList<>();

        final Iterator<TransferObject> it = servicosReserva.iterator();
        TransferObject next = null;
        String link = null;
        String label = null;

        while (it.hasNext()) {
            next = it.next();
            link = SynchronizerToken.updateTokenInURL(next.getAttribute("link").toString(), request);
            label = next.getAttribute("label").toString().toUpperCase();
            servicos.add(new ServicoSolicitacaoServidor(link, label));
        }

        return servicos;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("isIe", (request.getHeader("user-agent").indexOf("MSIE") > 0));
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.simulacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/simularConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("omitirAdeNumero", true);
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarServicos(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "listarServicos";
    }

    private boolean validarDadosSimulacao(HttpServletRequest request, UploadHelper uploadHelper, String adeVlr, String adeVlrLiberado, String adePrazo, AcessoSistema responsavel) {
        final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
        String chaveSegurancaAberta = null;
        try {
            String chaveSeguranca = (String) request.getAttribute("chaveSeguranca");
            if (TextHelper.isNull(chaveSeguranca)) {
                chaveSeguranca = JspHelper.verificaVarQryStr(request, uploadHelper, "chaveSeguranca");
            }
            chaveSegurancaAberta = RSA.decrypt(chaveSeguranca, keyPair.getPrivate());
        } catch (BadPaddingException | NullPointerException ex) {
            LOG.warn(ex);
        }
        if (chaveSegurancaAberta != null) {
        	final boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        	if(!exibeCETMinMax) {
        		final String[] dadosSimulacao = chaveSegurancaAberta.split("\\|");
                if ((dadosSimulacao.length == 3) &&
                        !TextHelper.isNull(adeVlr) && (adeVlr.equals(dadosSimulacao[0]) || TextHelper.isNull(dadosSimulacao[0])) &&
                        !TextHelper.isNull(adeVlrLiberado) && (adeVlrLiberado.equals(dadosSimulacao[1]) || TextHelper.isNull(dadosSimulacao[1])) &&
                        !TextHelper.isNull(adePrazo) && adePrazo.equals(dadosSimulacao[2])) {
                    return true;
                }
        	} else {
        		return true;
        	}

        }
        try {
            // Gerar log de auditoria de erro de segurança
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.seguranca.alteracao.valores.simulacao", responsavel)));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return false;
    }

    @RequestMapping(params = { "acao=escolherOutroSvc" })
    public String escolherOutroSvc(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
        final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

        final String adeVlr = request.getParameter("ADE_VLR");
        final String vlrLiberado = request.getParameter("VLR_LIBERADO");
        final String novoCftCodigo = request.getParameter("NOVO_CFT_CODIGO");
        final String przVlr = request.getParameter("PRZ_VLR");
        final String svcCodigoOrigem = request.getParameter("SVC_CODIGO_ORIGEM");

        List<TransferObject> simulacao = null;
        try {
            simulacao = ranking(adeVlr, vlrLiberado, request, response, session, model);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if ((simulacao == null) || simulacao.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Boolean vlrOk = false;
        String csa_codigo = "", csa_nome = "", cft_codigo = "", ranking = "", svcCodigoItem = "", dtjCodigo = "";
        String tac = "", iof = "";
        String cat = "", iva = ""; // simulacaoMetodoMexicano
        String vlr_ade = "", vlrLiberado_param = "";
        for (final TransferObject coeficiente : simulacao) {
            vlrOk = Boolean.parseBoolean(coeficiente.getAttribute("OK").toString());
            cft_codigo = (String) coeficiente.getAttribute(Columns.CFT_CODIGO);

            if (!vlrOk || !cft_codigo.equals(novoCftCodigo)) {
                continue;
            }

            csa_codigo = (String) coeficiente.getAttribute(Columns.CSA_CODIGO);
            svcCodigoItem = (String) coeficiente.getAttribute(Columns.SVC_CODIGO);
            csa_nome = (String) coeficiente.getAttribute("TITULO");
            dtjCodigo = (String) coeficiente.getAttribute(Columns.DTJ_CODIGO);
            vlr_ade = coeficiente.getAttribute("VLR_PARCELA").toString();
            vlrLiberado_param = coeficiente.getAttribute("VLR_LIBERADO").toString();
            ranking = (String) coeficiente.getAttribute("RANKING");

            try {
                if (simulacaoPorTaxaJuros) {
                    if (simulacaoMetodoMexicano) {
                        cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    } else if (simulacaoMetodoBrasileiro) {
                        tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    }
                }
            } catch (final ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            break;
        }

        if (!vlrOk) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.ranking.csa.nao.disponivel", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("CSA_CODIGO", csa_codigo);
        model.addAttribute("CSA_NOME", csa_nome);
        model.addAttribute("CFT_CODIGO", cft_codigo);
        model.addAttribute("ADE_VLR", vlr_ade);
        model.addAttribute("VLR_LIBERADO", vlrLiberado_param);
        model.addAttribute("RANKING", ranking);
        model.addAttribute("SVC_CODIGO", svcCodigoItem);
        if (simulacaoMetodoMexicano) {
            model.addAttribute("ADE_VLR_CAT", cat);
            model.addAttribute("ADE_VLR_IVA", iva);
        } else if (simulacaoMetodoBrasileiro) {
            model.addAttribute("ADE_VLR_TAC", tac);
            model.addAttribute("ADE_VLR_IOF", iof);
        }
        model.addAttribute("SIMULACAO_POR_ADE_VLR", !TextHelper.isNull(adeVlr));

        return confirmar(svcCodigoItem, svcCodigoOrigem, csa_codigo, vlr_ade, tac, iof, cat, iva, cft_codigo, dtjCodigo, vlrLiberado_param, przVlr, true, request, response, session, model);
    }

    private String montarTermoConsentimentoDadosServidor(AcessoSistema responsavel) {
        String termoConsentimento = "";
        if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "termo_de_uso" + File.separatorChar;
            absolutePath += CodedNames.TEMPLATE_TERMO_CONSENTIMENTO_DADOS_SERVIDOR;

            final File file = new File(absolutePath);
            if ((file != null) && file.isFile() && file.exists()) {
                termoConsentimento = FileHelper.readAll(absolutePath).replaceAll("\\r\\n|\\r|\\n", "");
            }
        }
        return termoConsentimento;
    }

    private void validaInformacoesServidorObrigatorias(UploadHelper uploadHelper ,String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        boolean celularObrigatorio = false;
        boolean enderecoObrigatorio = false;
        boolean enderecoCelularObrigatorio = false;

        final List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

        List<TransferObject> paramSvcCsa;
        try {
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject param2 : paramSvcCsa) {
                final CustomTransferObject param = (CustomTransferObject) param2;
                if (((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) && CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))){
				    final String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
				    switch (pscVlr) {
					case "E":
						enderecoObrigatorio = true;
						break;
					case "C":
						celularObrigatorio = true;
						break;
					case "EC":
						enderecoCelularObrigatorio = true;
						break;
					case null:
					default:
						break;
					}
				}
            }

            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) && (celularObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_TEL"))) {
               throw new ViewHelperException("mensagem.informe.servidor.telefone",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_END"))) {
                throw new ViewHelperException("mensagem.informe.servidor.logradouro",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_NRO"))) {
                throw new ViewHelperException("mensagem.informe.servidor.numero",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_COMPL"))) {
                throw new ViewHelperException("mensagem.informe.servidor.complemento",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_BAIRRO"))) {
                throw new ViewHelperException("mensagem.informe.servidor.bairro",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_CIDADE"))) {
                throw new ViewHelperException("mensagem.informe.servidor.cidade",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_CEP"))) {
                throw new ViewHelperException("mensagem.informe.servidor.cep",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_UF"))) {
                throw new ViewHelperException("mensagem.informe.servidor.estado",responsavel);
            }
            if ((ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)&& (celularObrigatorio|| enderecoCelularObrigatorio))) && TextHelper.isNull(uploadHelper.getValorCampoFormulario("SER_CEL"))) {
                throw new ViewHelperException("mensagem.informe.servidor.celular",responsavel);
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex.getMessageKey(), responsavel);
        }
    }

    @RequestMapping(params = { "acao=validaOtpServidorSimularConsignacao" })
    @ResponseBody
    public ResponseEntity<String> validaOtpServidorSimularConsignacao(@RequestBody(required = true) Map<String, Object> body, HttpServletRequest request, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String rseCodigo = String.valueOf(body.get("RSE_CODIGO"));
        final String svcCodigo = String.valueOf(body.get("SVC_CODIGO"));
        final String csaCodigo = String.valueOf(body.get("CSA_CODIGO"));
        final String senha = String.valueOf(body.get("SENHA"));
        final String serLogin = String.valueOf(body.get("SER_LOGIN"));
        try {
            if (ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) {
                final boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

                if (!TextHelper.isNull(senha)) {
                    session.setAttribute("serAutorizacao", senha);
                }
                if (!TextHelper.isNull(serLogin)) {
                    session.setAttribute("serLogin", serLogin);
                }

                SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, serSenhaObrigatoria, true, responsavel);
            }
        } catch (ParametroControllerException | ViewHelperException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.valida.otp.servidor.simular.consignacao", responsavel));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=erroValidaOtpServidorSimularConsignacao" })
    public String erroValidaOtpServidorSimularConsignacao(HttpServletRequest request, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String mensagem = JspHelper.verificaVarQryStr (request, "mensagem");
        if((TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO)) || TextHelper.isNull(session.getAttribute(CodedValues.MSG_ALERT))) && !TextHelper.isNull(mensagem)) {
            session.setAttribute(CodedValues.MSG_ERRO, mensagem);
        }
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    @RequestMapping(method = {RequestMethod.POST}, params = {"acao=enviaEmailConsignacao"})
    public ResponseEntity<String>  enviaEmailSimulacao(@RequestBody PDFRequest requestData, HttpServletRequest request) throws ConsignanteControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

        try {
            final List<CsaListInfoRequest> inforCsas = requestData.getInforCsas();
            final List<String> chunks = requestData.getChunks();
            if ((chunks == null) || chunks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            final StringBuilder sb = new StringBuilder();
            for (final String chunk : chunks) {
                sb.append(chunk);
            }

            String base64String = sb.toString();
            if (base64String.contains("data:application/pdf")) {
                base64String = base64String.split(",")[1];
            }

            final byte[] pdfBytes = Base64.getDecoder().decode(base64String);

            final Path tempPdfPath = Files.createTempFile("documento", ".pdf");
            Files.write(tempPdfPath, pdfBytes);

            final Path tempDir = Files.createTempDirectory("tempDir");
            final String zipFileName = "resultado_simulacao " + DateHelper.getSystemDatetime() + ".zip";
            final Path zipFilePath = tempDir.resolve(zipFileName);

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
                final String pdfFileNameInZip = "resultado_simulacao " + DateHelper.getSystemDatetime() +".pdf";
                zos.putNextEntry(new ZipEntry(pdfFileNameInZip));
                zos.write(Files.readAllBytes(tempPdfPath));
                zos.closeEntry();
            }

            EnviaEmailHelper.notificaSerSimulacaoConsignacao(inforCsas, requestData.getSerEmail(), responsavel.getUsuNome(), cse.getCseNome(), zipFilePath.toString(), responsavel);
            return ResponseEntity.ok("{\"message\": \"Email enviado com sucesso.\"}");
        } catch (final Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar email.");
        }
    }
}
