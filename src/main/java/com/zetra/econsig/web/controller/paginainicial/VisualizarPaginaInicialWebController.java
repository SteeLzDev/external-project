package com.zetra.econsig.web.controller.paginainicial;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.web.ServicoSolicitacaoServidor;
import com.zetra.econsig.exception.BannerPublicidadeControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.service.banner.BannerPublicidadeController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.bannercalculadora.margem.BannerCalculadoraMargemBase;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoModulosSistema;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoServicoCancelamentoAutomatico;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoServicoCompraContrato;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoServicoModuloAvancadoCompra;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoServicoRenegociacaoContrato;
import com.zetra.econsig.web.tag.ConfigSistemaTag.ConfiguracaoTaxa;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VisualizarPaginaInicialWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar página inicial dos papeis de servidor, consignatária e consignante</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $
 * $
 * $
 */
@Controller
public class VisualizarPaginaInicialWebController extends AbstractWebController {
    public static final int NUM_ITENS_PG_CAROUSEL_BANNERS = 1;

    public static final int NUM_ITENS_PG_CAROUSEL_NSE = 3;

    public static final Character NAO_EXIBE = '0';

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarPaginaInicialWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private BannerPublicidadeController bannerPublicidadeController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @RequestMapping(value = { "/v3/iniciarFsConsignataria" })
    public String iniciarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        return viewRedirect("jsp/visualizarPaginaInicial/visualizarConsignatariaFs", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/iniciarFsConsignante" })
    public String iniciarConsignante(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        return viewRedirect("jsp/visualizarPaginaInicial/visualizarConsignanteFs", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/iniciarFsServidor" })
    public String iniciarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        return viewRedirect("jsp/visualizarPaginaInicial/visualizarServidorFs", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/exibirMensagens" })
    public String exibirMensagens(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws MenuControllerException, FindException, UpdateException {
        return carregar(true, request, response, session, model);
    }

    @RequestMapping(value = { "/v3/carregarPrincipal" })
    public String carregarPrincipal(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws MenuControllerException, FindException, UpdateException {
        return carregar(false, request, response, session, model);
    }

    private String carregar(boolean menuMensagem, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws MenuControllerException, FindException, UpdateException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        verificarCargaMargens(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("mensagem.principal.titulo", responsavel));

        if (responsavel.isSer()) {
            // Parâmetro de sistema para exibir a margem do servidor na tela inicial do módulo do servidor
            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL, CodedValues.TPC_SIM, responsavel)) {
                try {
                    boolean exigeCaptcha = false;
                    boolean exibeCaptcha = false;
                    boolean exibeCaptchaAvancado = false;
                    boolean exibeCaptchaDeficiente = false;
                    final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptcha")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptcha") : "N";
                    final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

                    final boolean defVisual = responsavel.isDeficienteVisual();
                    if (!defVisual) {
                        exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        exibeCaptcha = !exibeCaptchaAvancado;
                    } else {
                        exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    }
                    if(!podeConsultar && "S".equals(validaRecaptcha)) {
                        if (!defVisual) {
                            if (exibeCaptcha) {
                                if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request,"codigoCap"))) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                    exigeCaptcha = true;
                                } else {
                                    session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                    final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                    model.addAttribute("margensServidor", margens);
                                    ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                }
                            } else if (exibeCaptchaAvancado) {
                                final String remoteAddr = request.getRemoteAddr();

                                if (!isValidCaptcha(request.getParameter("g-recaptcha-response_principal"), remoteAddr, responsavel)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                    exigeCaptcha = true;
                                } else {
                                    final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                    model.addAttribute("margensServidor", margens);
                                    ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                }
                            }
                        } else {
                            final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                            if (exigeCaptchaDeficiente) {
                                final String captchaAnswer = JspHelper.verificaVarQryStr(request,"codigoCap");

                                if (captchaAnswer == null) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                    exigeCaptcha = true;
                                }

                                final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                    exigeCaptcha = true;
                                } else {
                                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                model.addAttribute("margensServidor", margens);
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                }
                            }
                        }
                    } else if (podeConsultar) {
                        final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                        model.addAttribute("margensServidor", margens);
                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                    } else {
                        exigeCaptcha = true;
                    }
                    model.addAttribute("exigeCaptcha", exigeCaptcha);
                    model.addAttribute("exibeCaptcha", exibeCaptcha);
                    model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                    model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
                } catch (final ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            // DESENV-16136 : não exibe a lista de serviços quando mostra os cards do portal de benefícios.
            if (!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel) ||
                !responsavel.temPermissao(CodedValues.FUN_FLUXO_BENEFICIOS_PORTAL_PUBLICO)) {
                try {
                    if (session.getAttribute("servicosReserva") == null) {
                        // Busca Lista de serviços disponíveis para solicitação pelo servidor
                        final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                        final boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                        final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                        final List<TransferObject> servicosReserva = SolicitacaoServidorHelper.lstServicos(responsavel.getOrgCodigo(), null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);
                        // Seta a lista de serviços na sessão, pois o menu é recarregado a cada nova página
                        session.setAttribute("servicosReserva", servicosReserva);
                    }
                } catch (final ViewHelperException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
            boolean registroServidorBloqueado = false;
            try {
                final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(responsavel.getRseCodigo(), true, responsavel);
                registroServidorBloqueado = registroServidor.isBloqueado();
            } catch (final ServidorControllerException ex) {
                // TODO Auto-generated catch block
                LOG.error(ex.getMessage(), ex);
            }
            model.addAttribute("registroServidorBloqueado", registroServidorBloqueado);
        }

        SynchronizerToken.saveToken(request);

        // Variável utilizada para fazer preview de telas de mensagem das outras entidades
        final AcessoSistema previewEntidade = new AcessoSistema(null, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));

        // Só exibe combo para preview de msgs se o usuario for cse, com permissao para editar msgs e tiver clicado no menu Mensagens
        final boolean mostraComboPreview = responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDITAR_MENSAGEM) && menuMensagem;

        // Mensagem na tela inicial só é mostrada depois do login.
        boolean mostraMensagem = "true".equalsIgnoreCase(JspHelper.verificaVarQryStr(request, "mostraMensagem"));

        if (menuMensagem) {
            mostraMensagem = true;
        }

        // Todas as mensagens são listadas ou apenas uma quantidade X delas
        final boolean limitaMsg = "true".equalsIgnoreCase(JspHelper.verificaVarQryStr(request, "limitaMsg"));

        // Se o usuário for de consignante e tiver permissão de edição de msgs, inicializa as variáveis para combo de preview de mensagens de outras entidades
        int filtro = responsavel.isSup() ? 6 : 1;
        if (mostraComboPreview) {
            try {
                if (TextHelper.isNum(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"))) {
                    filtro = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
                }
            } catch (final NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            switch (filtro) {
                case 1:
                    previewEntidade.setTipoEntidade("CSE");
                    break;
                case 2:
                    previewEntidade.setTipoEntidade("CSA");
                    break;
                case 3:
                    previewEntidade.setTipoEntidade("COR");
                    break;
                case 4:
                    previewEntidade.setTipoEntidade("ORG");
                    break;
                case 5:
                    previewEntidade.setTipoEntidade("SER");
                    break;
                case 6:
                    previewEntidade.setTipoEntidade("SUP");
                    break;
                default:
                    previewEntidade.setTipoEntidade("CSE");
                    break;
            }
            // Consignatária escolhida para ver as mensagens permitidas à ela
            final String csa = JspHelper.verificaVarQryStr(request, "CSA_CODIGO_AUX");
            if ((csa != null) && !"".equals(csa)) {
                previewEntidade.setCodigoEntidade(csa);
            }
        }
        model.addAttribute("filtro", filtro);
        model.addAttribute("previewEntidade", previewEntidade);

        // Constante que indica qtas mensagens devem ser mostradas
        // Se o parâmetro nao existir, valor default para limite de mensagens é setado
        int numMaxMsg = 5;
        try {
            final Object objParamMsg = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MENSAGEM, responsavel);
            numMaxMsg = objParamMsg != null ? Integer.parseInt(objParamMsg.toString()) : 5;
        } catch (final Exception ex) {
            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.parametro.limite.mensagens.nulo", responsavel));
        }

        // Se não há limite para o número de msgs a serem exibidas, numMaxMsg = 0 faz com que todas sejam listadas
        if (!limitaMsg) {
            numMaxMsg = 0;
        }
        model.addAttribute("numMaxMsg", numMaxMsg);

        List<TransferObject> mensagens = new ArrayList<>();
        int total = 0;
        if (mostraMensagem) {
            try {
                // Se o usuario for de CSE ou SUP, a variável de acesso ao sistema tem que ser diferente para tratar o combo de entidade
                if (mostraComboPreview) {
                    total = mensagemController.countPesquisaMensagem(previewEntidade);
                    mensagens = mensagemController.pesquisaMensagem(previewEntidade, numMaxMsg, false);
                } else {
                    total = mensagemController.countPesquisaMensagem(responsavel);
                    mensagens = mensagemController.pesquisaMensagem(responsavel, numMaxMsg, false);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        model.addAttribute("total", total);
        model.addAttribute("mensagens", mensagens);

        boolean exibeBanner = false;
        boolean haBannerNoSistema = false;
        String bannerName = "";

        // Se é usuário servidor ...
        if (responsavel.isSer()) {
            List<TransferObject> servicosReserva = null;

            final String rseCodigo = responsavel.getRseCodigo();
            final String orgCodigo = responsavel.getOrgCodigo();
            CustomTransferObject servidor = null;
            try {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Busca Lista de serviços disponíveis para solicitação pelo servidor
            try {
                final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                final boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);
            } catch (final ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            //define se exibe banner de propaganda de CSA
            final Object paramExibeBanner = ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_BANNER_SERVIDOR, responsavel);
            exibeBanner = ("S".equals(paramExibeBanner));
            final boolean bannerJaExibido = (session.getAttribute("BANNER_EXIBIDO") != null);
            exibeBanner = exibeBanner && !bannerJaExibido;

            if (exibeBanner) {
                //File diretorioBanner = new File(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner");
                final List<String> bannersList = FileHelper.getFilesInDir(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner");

                if (!bannersList.isEmpty()) {
                    final SecureRandom escolheBanner = new SecureRandom();
                    int tentativas = 0;
                    do {
                        if (tentativas > 100) {
                            break;
                        }
                        final int posicao = escolheBanner.nextInt(bannersList.size());
                        bannerName = bannersList.get(posicao);
                        tentativas++;
                    } while (!bannerName.toLowerCase().endsWith(".gif") && !bannerName.toLowerCase().endsWith(".jpg"));

                    haBannerNoSistema = (bannerName.toLowerCase().endsWith(".gif") || bannerName.toLowerCase().endsWith(".jpg"));
                }
            }

            // DESENV-16136 : não exibe a lista de serviços quando mostra os cards do portal de benefícios.
            if (!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel) ||
                !responsavel.temPermissao(CodedValues.FUN_FLUXO_BENEFICIOS_PORTAL_PUBLICO)) {
                model.addAttribute("servicosReserva", definirServicosSolicitacao(servicosReserva, request));
            }

            //DESENV-15640
            if (ParamSist.getBoolParamSist(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel)) {
                try {
                    final List<TransferObject> itensBannersCarousel = bannerPublicidadeController.listarBannerPublicidade(null, -1, -1, responsavel);

                    if ((itensBannersCarousel != null) && !itensBannersCarousel.isEmpty()) {
                        Collections.sort(itensBannersCarousel, (o1, o2) -> ((Short) o1.getAttribute(Columns.BPU_ORDEM))
                                .compareTo((Short) o2.getAttribute(Columns.BPU_ORDEM)));

                        int numPgCarouselBanners = Float.valueOf(itensBannersCarousel.size() / NUM_ITENS_PG_CAROUSEL_BANNERS).intValue();
                        numPgCarouselBanners = (numPgCarouselBanners == 0) ? 1 : ((itensBannersCarousel.size() - (numPgCarouselBanners * NUM_ITENS_PG_CAROUSEL_BANNERS)) > 0) ? numPgCarouselBanners + 1 : numPgCarouselBanners;
                        model.addAttribute("itensBannersCarousel", itensBannersCarousel);
                        model.addAttribute("numPgCarouselBanners", numPgCarouselBanners);
                        model.addAttribute("itensPorPgCarouselBanners", NUM_ITENS_PG_CAROUSEL_BANNERS);
                    }
                } catch (final BannerPublicidadeControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            // DESENV-11902
            if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel) && responsavel.temPermissao(CodedValues.FUN_FLUXO_BENEFICIOS_PORTAL_PUBLICO)) {
                try {
                    final List<TransferObject> itensNseCarousel = servicoController.lstNaturezasServicos(responsavel.getOrgCodigo(), true, true);

                    if ((itensNseCarousel != null) && !itensNseCarousel.isEmpty()) {
                        Collections.sort(itensNseCarousel, (o1, o2) -> {
                            final Short ordem1 = (o1.getAttribute(Columns.NSE_ORDEM_BENEFICIO) != null ? (Short) o1.getAttribute(Columns.NSE_ORDEM_BENEFICIO) : Short.MAX_VALUE);
                            final Short ordem2 = (o2.getAttribute(Columns.NSE_ORDEM_BENEFICIO) != null ? (Short) o2.getAttribute(Columns.NSE_ORDEM_BENEFICIO) : Short.MAX_VALUE);

                            return ordem1.compareTo(ordem2);
                        });

                        int numPgCarouselNse = Float.valueOf(itensNseCarousel.size() / NUM_ITENS_PG_CAROUSEL_NSE).intValue();
                        numPgCarouselNse = (numPgCarouselNse == 0) ? 1 : ((itensNseCarousel.size() - (numPgCarouselNse * NUM_ITENS_PG_CAROUSEL_NSE)) > 0) ? numPgCarouselNse + 1 : numPgCarouselNse;
                        model.addAttribute("itensNseCarousel", itensNseCarousel);
                        model.addAttribute("numPgCarouselNse", numPgCarouselNse);
                        model.addAttribute("itensPorPgCarouselNse", NUM_ITENS_PG_CAROUSEL_NSE);
                    }
                    model.addAttribute("tituloConhecaBeneficios", ApplicationResourcesHelper.getMessage("rotulo.beneficio.conheca.beneficios.disponiveis", responsavel));
                } catch (final ServicoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            // DESENV-16085: Alerta para caso o servidor esteja bloqueado e o parâmetro de sistema para permissão de exibição de margem para servidores bloqueados esteja habilitado
            if (CodedValues.SRS_BLOQUEADO.equals(servidor.getAttribute(Columns.SRS_CODIGO)) && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("servidorBloqueadoPermissaoVisualizarMargem", true);

                // DESENV-16129 - Rio de Janeiro - Mostrar Motivo de Bloqueio do Servidor
                try {
                    final String rseMotivoBloqueio = servidorController.findRegistroServidor(rseCodigo, responsavel).getRseMotivoBloqueio();
                    if (!TextHelper.isNull(rseMotivoBloqueio)) {
                        model.addAttribute("servidorMotivoBloqueio", rseMotivoBloqueio);
                    }
                } catch (final ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            } else {
                model.addAttribute("servidorBloqueadoPermissaoVisualizarMargem", false);
            }
        } else {
            model.addAttribute("servicosReserva", null);

            // DESENV-12754: alerta de operações sensíveis na fila de autorização
            if (responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_OP_FILA_AUTORIZACAO)) {
                try {
                    final int numOpFilaAutorizacao = sistemaController.countOperacoesFilaAutorizacao(responsavel);
                    if (numOpFilaAutorizacao > 0) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.fila.op.existentes", responsavel));
                    }
                } catch (final ConsignanteControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }
        }

        if(responsavel.isCsa()) {
            try {
                List<Correspondente> correspondentes = null;

                correspondentes = consignatariaController.findCorrespondenteByCsaCodigo(responsavel.getCodigoEntidade(), responsavel);
                // Essa coluna deve ser exibida apenas para usuários de consignatárias que possuem correspondentes cadastrados
                model.addAttribute("csaTemCorrespondentes", (correspondentes != null) && !correspondentes.isEmpty());
            } catch (final ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        if (menuMensagem) {
            model.addAttribute("menuMensagem", Boolean.TRUE);
        }

        model.addAttribute("exibeBanner", exibeBanner);
        model.addAttribute("haBannerNoSistema", haBannerNoSistema);
        model.addAttribute("bannerName", bannerName);
        carregarListaConsignataria(request, session, model, responsavel);

        // Se não é usuário de suporte e tem permissão de ler comunicação, verifica se existem comunicações
        // não lidas para incluir alerta na página principal do sistema. Papel suporte não tem comunicação
        // específica para ele, então o alerta seria exibido sempre que comunicações não lidas de outros
        // papéis existissem, o que tornaria o alerta inútil.
        if (!responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_LER_COMUNICACAO)) {
            final CustomTransferObject criteriosPesquisa = new CustomTransferObject();
            criteriosPesquisa.setAttribute("APENAS_CMN_PAI", Boolean.TRUE);
            criteriosPesquisa.setAttribute(Columns.CSE_CODIGO, responsavel.getCseCodigo());
            criteriosPesquisa.setAttribute(Columns.EST_CODIGO, responsavel.getEstCodigo());
            criteriosPesquisa.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
            criteriosPesquisa.setAttribute(Columns.SER_CODIGO, responsavel.getSerCodigo());
            criteriosPesquisa.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            criteriosPesquisa.setAttribute(Columns.COR_CODIGO, responsavel.getCorCodigo());
            criteriosPesquisa.setAttribute("CMN_LIDA", "0");
            criteriosPesquisa.setAttribute("exibeSomenteCse", responsavel.isCse() ? "1" : "0");

            try {
                final int qtdCmnNaoLida = comunicacaoController.countComunicacoes(criteriosPesquisa, responsavel);
                if (qtdCmnNaoLida > 0) {
                    model.addAttribute("existeComunicacao", Boolean.TRUE);
                }
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        if (responsavel.isSer()) {
            final String exibeCalculoMargemPortalServidor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_CALCULO_MARGEM_PORTAL_SERVIDOR, responsavel);
            if (!TextHelper.isNull(exibeCalculoMargemPortalServidor)) {
            	BannerCalculadoraMargemBase bannerCalculadoraMargem;
				try {
					bannerCalculadoraMargem = (BannerCalculadoraMargemBase) Class.forName(exibeCalculoMargemPortalServidor).getDeclaredConstructor().newInstance();
					model.addAttribute("bannerCalculadoraMargem", bannerCalculadoraMargem.montarBannerCalculadoraMargem(responsavel.getRseCodigo(), responsavel));
				} catch (final NoSuchMethodException | ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ZetraException ex) {
		            LOG.error(ex.getMessage(), ex);
		        }
            }
            return viewRedirect("jsp/visualizarPaginaInicial/visualizarPrincipalServidor", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/visualizarPaginaInicial/visualizarPrincipalGeral", request, session, model, responsavel);
        }
    }

    @RequestMapping(value = "/v3/atualizarExtratoDiaAjax", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> atualizarExtratoDiaAjax(HttpServletRequest request, Model model) throws SQLException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final List<TransferObject> transacoesDia = incluirExtratoTransacoesDia(request.getSession(), model, responsavel);

        final JsonArrayBuilder array = Json.createArrayBuilder();


        for (final TransferObject transacao: transacoesDia) {
            final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

            arrayInner.add(TextHelper.forHtml(transacao.getAttribute(Columns.ADE_NUMERO)));
            if(responsavel.isCsa()) {
                try {
                    List<Correspondente> correspondentes = null;

                    correspondentes = consignatariaController.findCorrespondenteByCsaCodigo(responsavel.getCodigoEntidade(), responsavel);
                    // Essa coluna deve ser exibida apenas para usuários de consignatárias que possuem correspondentes cadastrados
                    if ((correspondentes != null) && !correspondentes.isEmpty()) {
                        final String correspondente = (String) transacao.getAttribute(Columns.COR_NOME);
                        arrayInner.add(!TextHelper.isNull(correspondente) ? TextHelper.forHtml(correspondente) : "");
                    }
                } catch (final ConsignatariaControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            arrayInner.add(TextHelper.forHtml(DateHelper.format((Date) transacao.getAttribute(Columns.OCA_DATA), LocaleHelper.getDateTimePattern())));
            try {
                String adeVlr = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + " " + NumberHelper.reformat(((BigDecimal) transacao.getAttribute(Columns.ADE_VLR)).toString(), "en", NumberHelper.getLang());

                final String tocCodigo = (String) transacao.getAttribute(Columns.TOC_CODIGO);
                if (CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO.equals(tocCodigo) || CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA.equals(tocCodigo)) {
                    adeVlr = "<span class='rotulo-pendente'>" + "- " + TextHelper.forHtml(adeVlr)+ "</span>";
                }

                arrayInner.add(adeVlr);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            array.add(arrayInner);
        }

        final JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("tableDataSrc", array);
        if (model.asMap().get("somaTransDia") != null) {
            result.add("somaTransDia", (String) model.asMap().get("somaTransDia"));
        } else {
            try {
                result.add("somaTransDia", NumberHelper.reformat("0", "en", NumberHelper.getLang()));
            } catch (final ParseException e) {
                result.add("somaTransDia", "0,00");
            }
        }

        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }

    private List<TransferObject> incluirExtratoTransacoesDia(HttpSession session, Model model, AcessoSistema responsavel) {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_EXTRATO_CSA_COR, responsavel) && responsavel.isCsaCor()) {
            try {
                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
                tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
                tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);
                tocCodigos.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);

                final Date systemDate = DateHelper.getSystemDatetime();

                final Date dataIni = DateHelper.clearHourTime(systemDate);
                final Date dataFim = DateHelper.getDate(DateHelper.getYear(systemDate), DateHelper.getMonth(systemDate), DateHelper.getDay(systemDate), 23, 59);

                String csaCodigo = null;
                String corCodigo = null;

                if (responsavel.isCsa()) {
                    csaCodigo = responsavel.getCodigoEntidade();
                } else if (responsavel.isCor()) {
                    corCodigo = responsavel.getCodigoEntidade();
                }

                final List<TransferObject> transacoesDia = consignatariaController.lstContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigos, dataIni, dataFim, null, -1, -1, responsavel);

                if ((transacoesDia != null) && !transacoesDia.isEmpty()) {
                    BigDecimal somaTransDia = BigDecimal.ZERO;

                    final List<String> tocCodigosInclusao = new ArrayList<>();
                    tocCodigosInclusao.add(CodedValues.TOC_TARIF_RESERVA);
                    tocCodigosInclusao.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);

                    for (final TransferObject trans : transacoesDia) {
                        final BigDecimal adeVlr = (BigDecimal) trans.getAttribute(Columns.ADE_VLR);
                        if (adeVlr != null) {
                            if (tocCodigosInclusao.contains(trans.getAttribute(Columns.TOC_CODIGO))) {
                                somaTransDia = somaTransDia.add(adeVlr);
                            } else {
                                somaTransDia = somaTransDia.subtract(adeVlr);
                            }
                        }
                    }

                    model.addAttribute("somaTransDia", NumberHelper.reformat(somaTransDia.setScale(2, java.math.RoundingMode.HALF_UP).toString(), "en", NumberHelper.getLang()));
                }

                return transacoesDia;
            } catch (ConsignatariaControllerException | ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        return null;
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

    @RequestMapping(value = "/v3/listarCadastroTaxas", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoCadastroTaxas(HttpServletRequest request, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Recupera a configuração que informa se o sistema opera com taxas de juros.
        final boolean cadastraTaxas = ParamSist.paramEquals(CodedValues.TPC_PER_CAD_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        if (!cadastraTaxas) {
            // Se não há cadastro de taxas, limpas as configurações carregadas anteriormente e retorna.
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        final JsonArrayBuilder array = Json.createArrayBuilder();

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços que validam as taxas de juros
            final List<TransferObject> servicos = servicoController.selectServicosComParametro(CodedValues.TPS_VALIDAR_TAXA_JUROS, orgCodigo, csaCodigo, "1", false, CodedValues.NSE_EMPRESTIMO, responsavel);

            final List<ConfiguracaoTaxa> taxasServicos = new ArrayList<>();

            if (servicos != null) {
                String dataLimite = null;
                String dataAbertura = null;
                String tipoDataAbertura = null;
                List<TransferObject> limites = null;

                // Para cada serviço.
                for (final TransferObject servico : servicos) {
                    // Recupera os parâmetros do serviço referentes a taxa de juros.
                    final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO((String) servico.getAttribute(Columns.SVC_CODIGO), responsavel);

                    dataLimite = paramSvcCse.getTpsDataLimiteDigitTaxa();
                    dataAbertura = paramSvcCse.getTpsDataAberturaTaxa();
                    tipoDataAbertura = paramSvcCse.getTpsDataAberturaTaxaRef();

                    // Armazena o conjunto de configurações de parâmetros para o serviço.
                    final ConfiguracaoTaxa confTaxa = new ConfiguracaoTaxa((String) servico.getAttribute(Columns.SVC_DESCRICAO), dataLimite, dataAbertura, tipoDataAbertura);

                    // Recupera os limites de taxa de juros do serviço
                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.LTJ_SVC_CODIGO, servico.getAttribute(Columns.SVC_CODIGO));

                    final int total = limiteTaxaJurosController.countLimiteTaxaJuros(criterio, responsavel);
                    limites = limiteTaxaJurosController.listaLimiteTaxaJuros(criterio, 0, total, responsavel);

                    if ((limites != null) && (limites.size() > 0)) {
                        for (final TransferObject limite : limites) {
                            confTaxa.incluirLimiteTaxa(limite.getAttribute(Columns.LTJ_JUROS_MAX).toString(), limite.getAttribute(Columns.LTJ_PRAZO_REF).toString());
                        }
                    }

                    taxasServicos.add(confTaxa);
                }
            }

			for (final ConfiguracaoTaxa confTaxa : taxasServicos) {
				final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

				if ((confTaxa.getLimiteTaxa() != null) && (confTaxa.getLimiteTaxa().size() > 0)) {
					for (final String textoLimite : confTaxa.getLimiteTaxa()) {

						arrayInner.add(TextHelper.forHtmlContent(confTaxa.getNomeServico() != null ? TextHelper.forHtmlContent(confTaxa.getNomeServico()) : ""));
						arrayInner.add(confTaxa.getConfiguracoesDatas() != null ? confTaxa.getConfiguracoesDatas() : "&nbsp;");

						arrayInner.add(!TextHelper.isNull(textoLimite) ? textoLimite : "&nbsp;");
						array.add(arrayInner);
					}
				} else {
					arrayInner.add(TextHelper.forHtmlContent(confTaxa.getNomeServico() != null ? TextHelper.forHtmlContent(confTaxa.getNomeServico()) : ""));
					arrayInner.add(confTaxa.getConfiguracoesDatas() != null ? confTaxa.getConfiguracoesDatas() : "&nbsp;");

					arrayInner.add("&nbsp;");
                    array.add(arrayInner);
				}

			}

            final JsonObjectBuilder result = Json.createObjectBuilder();
            result.add("tableDataSrc", array);

            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);

        } catch (ServicoControllerException | ParametroControllerException | LimiteTaxaJurosControllerException ex) {
            throw new ZetraException(ex);
        }
    }

    @RequestMapping(value = "/v3/listarModulosSistema", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoModulosSistema(HttpServletRequest request) throws UsuarioControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final JsonArrayBuilder array = Json.createArrayBuilder();
        final List<ConfiguracaoModulosSistema> configuracaoModulosSistema = new ArrayList<>();

        if (ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel)) {
            final ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.simulador", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, responsavel)) {
            final ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.portal.servidor", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel)) {
            final ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.portabilidade.margem.consignavel", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel)) {
            final ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.leilao.reverso", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        final List<String> funcoesPerfilServidor = usuarioController.getFuncaoPerfil(AcessoSistema.ENTIDADE_SER, null, "PERFIL-SERVIDOR", responsavel);

        for (final String funcao : funcoesPerfilServidor) {
            if (CodedValues.FUN_CRIAR_COMUNICACAO.equals(funcao)) {
                final ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.comunicacao", responsavel));
                configuracaoModulosSistema.add(configuracao);
                break;
            }
        }

        for (final ConfiguracaoModulosSistema configuracao: configuracaoModulosSistema) {
            final JsonArrayBuilder arrayInner = Json.createArrayBuilder();
            arrayInner.add(TextHelper.forHtmlContent(configuracao.getNomeModulo()) != null ? TextHelper.forHtmlContent(configuracao.getNomeModulo()) : "");
            array.add(arrayInner);
        }

        final JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("tableDataSrc", array);

        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/v3/listarServicosCancelamentoAutomatico", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoServicosCancelamentoAutomatico(HttpServletRequest request, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Verifica se o sistema tem módulo de compra.
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);

        try {
            final JsonArrayBuilder array = Json.createArrayBuilder();

            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços de empréstimo que possuem parâmetro de cancelamento automático.
            final List<TransferObject> servicosCancelamentoAutomatico = servicoController.selectServicosCancelamentoAutomatico(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            final List<ConfiguracaoServicoCancelamentoAutomatico> configuracaoServicosCancelamentoAutomatico = new ArrayList<>();

            if ((servicosCancelamentoAutomatico != null) && (servicosCancelamentoAutomatico.size() > 0)) {
                for (final TransferObject servico : servicosCancelamentoAutomatico) {
                    final String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    final String prazoConfirmacaoSolicitacao = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_SOLICITACAO");
                    final String prazoConfirmacaoReserva = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_RESERVA");
                    final String prazoConfirmacaoCompra = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_COMPRA");

                    final ConfiguracaoServicoCancelamentoAutomatico configuracao = new ConfiguracaoServicoCancelamentoAutomatico(nomeServico, prazoConfirmacaoSolicitacao, prazoConfirmacaoReserva, prazoConfirmacaoCompra);

                    configuracaoServicosCancelamentoAutomatico.add(configuracao);
                }
            }

            for (final ConfiguracaoServicoCancelamentoAutomatico configuracaoServico: configuracaoServicosCancelamentoAutomatico) {
                final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

                arrayInner.add(TextHelper.forHtmlContent(configuracaoServico.getNomeServico() != null ? TextHelper.forHtmlContent(configuracaoServico.getNomeServico()) : ""));
                arrayInner.add(!TextHelper.isNull(configuracaoServico.getPrazoConfirmacaoSolicitacoes()) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoConfirmacaoSolicitacoes()) : "");
                arrayInner.add(!TextHelper.isNull(configuracaoServico.getPrazoConfirmacaoReservas()) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoConfirmacaoReservas()) : "");
                if (temModuloCompra) {
                    arrayInner.add((!TextHelper.isNull(configuracaoServico.getPrazoConfirmacaoCompras())) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoConfirmacaoCompras()) : "");
                }

                array.add(arrayInner);
            }

            final JsonObjectBuilder result = Json.createObjectBuilder();
            result.add("tableDataSrc", array);

            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);

        } catch (final ServicoControllerException e) {
            throw new ZetraException(e);
        }
    }

    @RequestMapping(value = "/v3/listarServicosModuloAvancadoCompra", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoServicosModuloAvancadoCompra(HttpServletRequest request, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Verifica se o sistema tem módulo de compra.
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema tem módulo avançado de compras habilitado.
        final boolean temModuloAvancadoCompras = temModuloCompra && ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel);

        List<ConfiguracaoServicoModuloAvancadoCompra> configuracaoServicosModuloAvancadoCompra = null;

        // Se não há módulo avançado, limpa as configurações anteriormente carregadas e retorna.
        if (!temModuloAvancadoCompras) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        final JsonArrayBuilder array = Json.createArrayBuilder();

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços que validam as taxas de juros
            final List<TransferObject> servicosModuloCompra = servicoController.selectServicosModuloAvancadoCompras(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            configuracaoServicosModuloAvancadoCompra = new ArrayList<>();

            // Se há serviços configurados para o módulo avançado de compra
            if ((servicosModuloCompra != null) && (servicosModuloCompra.size() > 0)) {
                for (final TransferObject servico : servicosModuloCompra) {
                    final String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    final String prazoInformarSaldo = (String) servico.getAttribute("VLR_PRAZO_INFORMAR_SALDO");
                    final String prazoEfetuarPagamento = (String) servico.getAttribute("VLR_PRAZO_EFETUAR_PAGAMENTO");
                    final String prazoLiquidarContrato = (String) servico.getAttribute("VLR_PRAZO_LIQUIDAR_CONTRATO");

                    final ConfiguracaoServicoModuloAvancadoCompra configuracao = new ConfiguracaoServicoModuloAvancadoCompra(nomeServico, prazoInformarSaldo, prazoEfetuarPagamento, prazoLiquidarContrato);

                    configuracaoServicosModuloAvancadoCompra.add(configuracao);
                }
            }
        } catch (final ServicoControllerException e) {
            throw new ZetraException(e);
        }

        for (final ConfiguracaoServicoModuloAvancadoCompra configuracaoServico: configuracaoServicosModuloAvancadoCompra) {
            final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

            arrayInner.add(TextHelper.forHtmlContent(configuracaoServico.getNomeServico()) != null ? TextHelper.forHtmlContent(configuracaoServico.getNomeServico()) : "");
            arrayInner.add(!TextHelper.isNull(configuracaoServico.getPrazoInformarSaldo()) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoInformarSaldo()) : "&nbsp;");
            arrayInner.add(!TextHelper.isNull(configuracaoServico.getPrazoEfetuarPagamento()) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoEfetuarPagamento()) : "&nbsp;");
            arrayInner.add(!TextHelper.isNull(configuracaoServico.getPrazoLiquidarContrato()) ? TextHelper.forHtmlContent(configuracaoServico.getPrazoLiquidarContrato()) : "&nbsp;");

            array.add(arrayInner);
        }

        final JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("tableDataSrc", array);

        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/v3/listarServicosCompraContrato", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoServicosCompraContrato(HttpServletRequest request, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Verifica se o sistema tem módulo de compra.
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);

        // Se não há módulo de compra, limpa as configurações anteriormente carregadas e retorna.
        if (!temModuloCompra) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        final JsonArrayBuilder array = Json.createArrayBuilder();

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços e seus parâmetros
            final List<TransferObject> servicosParametroCompra = servicoController.selectServicosParametroCompra(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            final List<ConfiguracaoServicoCompraContrato> configuracaoServicosCompraContrato = new ArrayList<>();

            // Se há serviços configurados de parametros para compra de contratos
            if ((servicosParametroCompra != null) && (servicosParametroCompra.size() > 0)) {

                for (final TransferObject servico : servicosParametroCompra) {

                    final String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    final String quantidadeMinParcelaPaga = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_PARCELA_PAGA");
                    final String percentualMinParcelaPaga = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_PARCELA_PAGA");
                    final String quantidadeMinVigencia = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_VIGENCIA");
                    final String percentualMinVigencia = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_VIGENCIA");

                    final ConfiguracaoServicoCompraContrato configuracao = new ConfiguracaoServicoCompraContrato(nomeServico, quantidadeMinParcelaPaga, percentualMinParcelaPaga, quantidadeMinVigencia, percentualMinVigencia);

                    configuracaoServicosCompraContrato.add(configuracao);

                }
            }

            for (final ConfiguracaoServicoCompraContrato configuracaoServico: configuracaoServicosCompraContrato) {
                final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

                arrayInner.add(TextHelper.forHtmlContent(configuracaoServico.getNomeServico() != null ? TextHelper.forHtmlContent(configuracaoServico.getNomeServico()) : ""));

                arrayInner.add((!TextHelper.isNull(configuracaoServico.getQuantidadeMinParcelaPaga()) ? TextHelper.forHtmlContent(configuracaoServico.getQuantidadeMinParcelaPaga()) : "&nbsp;") +
                                (!TextHelper.isNull(configuracaoServico.getPercentualMinParcelaPaga()) ? " (" + TextHelper.forHtmlContent(configuracaoServico.getPercentualMinParcelaPaga()) + "%)" : "&nbsp;"));

                arrayInner.add((!TextHelper.isNull(configuracaoServico.getQuantidadeMinVigencia()) ? TextHelper.forHtmlContent(configuracaoServico.getQuantidadeMinVigencia()) : "&nbsp;") +
                                (!TextHelper.isNull(configuracaoServico.getPercentualMinVigencia()) ? " (" + TextHelper.forHtmlContent(configuracaoServico.getPercentualMinVigencia()) + "%)" : "&nbsp;"));

                array.add(arrayInner);
            }

            final JsonObjectBuilder result = Json.createObjectBuilder();
            result.add("tableDataSrc", array);

            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);

        } catch (final ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    @RequestMapping(value = "/v3/listarServicosRenegociacaoContrato", params = { "acao=listar" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> configuracaoServicosRenegociacaoContrato(HttpServletRequest request, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final JsonArrayBuilder array = Json.createArrayBuilder();

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços e seus parâmetros de renegociacao
            final List<TransferObject> servicosParametroRenegociacao = servicoController.selectServicosParametroRenegociacao(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            final List<ConfiguracaoServicoRenegociacaoContrato> configuracaoServicosRenegociacaoContrato = new ArrayList<>();

            // Se há serviços configurados de parametros para renegociacao de contratos
            if ((servicosParametroRenegociacao != null) && (servicosParametroRenegociacao.size() > 0)) {

                for (final TransferObject servico : servicosParametroRenegociacao) {

                    final String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    final String quantidadeMinParcelaPaga = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_PARCELA_PAGA");
                    final String percentualMinParcelaPaga = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_PARCELA_PAGA");
                    final String quantidadeMinVigencia = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_VIGENCIA");
                    final String percentualMinVigencia = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_VIGENCIA");

                    final ConfiguracaoServicoRenegociacaoContrato configuracao = new ConfiguracaoServicoRenegociacaoContrato(nomeServico, quantidadeMinParcelaPaga, percentualMinParcelaPaga, quantidadeMinVigencia, percentualMinVigencia);

                    configuracaoServicosRenegociacaoContrato.add(configuracao);

                }
            }

            for (final ConfiguracaoServicoRenegociacaoContrato configuracaoServico: configuracaoServicosRenegociacaoContrato) {
                final JsonArrayBuilder arrayInner = Json.createArrayBuilder();

                arrayInner.add(TextHelper.forHtmlContent(configuracaoServico.getNomeServico() != null ? TextHelper.forHtmlContent(configuracaoServico.getNomeServico()) : ""));

                arrayInner.add((!TextHelper.isNull(configuracaoServico.getQuantidadeMinParcelaPaga()) ? TextHelper.forHtmlContent(configuracaoServico.getQuantidadeMinParcelaPaga()) : "&nbsp;") +
                                (!TextHelper.isNull(configuracaoServico.getPercentualMinParcelaPaga()) ? " (" + TextHelper.forHtmlContent(configuracaoServico.getPercentualMinParcelaPaga()) + "%)" : "&nbsp;"));

                arrayInner.add((!TextHelper.isNull(configuracaoServico.getQuantidadeMinVigencia()) ? TextHelper.forHtmlContent(configuracaoServico.getQuantidadeMinVigencia()) : "&nbsp;") +
                                (!TextHelper.isNull(configuracaoServico.getPercentualMinVigencia()) ? " (" + TextHelper.forHtmlContent(configuracaoServico.getPercentualMinVigencia()) + "%)" : "&nbsp;"));

                array.add(arrayInner);
            }

            final JsonObjectBuilder result = Json.createObjectBuilder();
            result.add("tableDataSrc", array);

            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);

        } catch (final ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }
}
