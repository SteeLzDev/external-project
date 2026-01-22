package com.zetra.econsig.web.controller.renegociacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SimularRenegociacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso SimularRenegociacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/simularRenegociacao" })
public class SimularRenegociacaoWebController extends RenegociarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimularRenegociacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        final String orgCodigo = responsavel.getOrgCodigo();

        // listar serviços renegociáveis
        List<TransferObject> svcRenegociacao = new ArrayList<>();
        try {
            svcRenegociacao = SolicitacaoServidorHelper.lstServicosRenegociaveis(null, orgCodigo, null, null, responsavel);
        } catch (final ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("svcRenegociacao", svcRenegociacao);

        return viewRedirect("jsp/simularRenegociacao/listarServicosRenegociaveis", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=listarCsaRenegociacao" })
    public String listarCsaRenegociacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String rseCodigo = responsavel.getRseCodigo();
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
        final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        final String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");

        // listar consignatárias com ADEs renegociáveis
        List<TransferObject> csaList = new ArrayList<>();
        try {
            csaList = consignatariaController.lstConsignatariasComAdeRenegociaveis(rseCodigo, svcCodigo, orgCodigo, responsavel);
        } catch (final ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("svcIdentificador", svcIdentificador);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("csaList", csaList);

        return viewRedirect("jsp/simularRenegociacao/listarConsignatariasRenegociacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=simular" })
    public String simular(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Verifica os contratos selecionados para renegociação
            final String[] chkAde = request.getParameterValues("chkADE");

            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
            final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
            final String csaIdentificador = JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR");
            final String csaNome = JspHelper.verificaVarQryStr(request, "CSA_NOME");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            final String rseCodigo = (responsavel.isSer()) ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            // Verifica obrigatóriedade do código do serviço e código do registro servidor
            if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Parâmetros de sistema
            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            final boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);
            final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);
            if (!simulacaoMetodoBrasileiro && (temCET || !simulacaoPorTaxaJuros)) {
              session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.parametrizacao.taxa.iva", responsavel));
              return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String qtdeColunasSimulacao = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel))
                                        ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel).toString()
                                        : "4";
            float floatQtdeColunasSimulacao = Float.parseFloat(qtdeColunasSimulacao);
            if (floatQtdeColunasSimulacao < 1) {
                floatQtdeColunasSimulacao = 1;
            } else if (floatQtdeColunasSimulacao > 4) {
                floatQtdeColunasSimulacao = 4;
            }

            // Parâmetros de serviço
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final Short incMargem  = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            final String tipoVlr   = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
            final boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
            String adeVlrPadrao = ((paramSvcCse.getTpsAdeVlr() != null) && !"".equals(paramSvcCse.getTpsAdeVlr())) ? NumberHelper.reformat(paramSvcCse.getTpsAdeVlr(), "en", NumberHelper.getLang()) : ""; // Valor da prestação fixo para o serviço

            // Dados do servidor
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            // Verifica se pode mostrar margem
            final MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, responsavel);
            final ExibeMargem exibeMargem = margemDisponivel.getExibeMargem();
            final boolean podeMostrarMargem = exibeMargem.isExibeValor();
            BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

            if (responsavel.isSer()) {
                // Parâmetro de sistema para exibir a margem do servidor na tela
                boolean exigeCaptcha = false;
                boolean exibeCaptcha = false;
                boolean exibeCaptchaAvancado = false;
                boolean exibeCaptchaDeficiente = false;
                final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaRenegociacao")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptchaRenegociacao") : "N";
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
                                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request,"codigoCapRenegociacao"))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            } else {
                                session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                exigeCaptcha = false;
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        } else if (exibeCaptchaAvancado) {
                            final String remoteAddr = request.getRemoteAddr();

                            if (!isValidCaptcha(request.getParameter("g-recaptcha-response_renegociacao"), remoteAddr, responsavel)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            } else {
                                exigeCaptcha = false;
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        }
                    } else {
                        final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        if (exigeCaptchaDeficiente) {
                            final String captchaAnswer = request.getParameter("codigoCapRenegociacao");

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
                                exigeCaptcha = false;
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        }
                    }
                } else if (podeConsultar) {
                    exigeCaptcha = false;
                    ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                } else {
                    exigeCaptcha = true;
                }
                model.addAttribute("exigeCaptcha", exigeCaptcha);
                model.addAttribute("exibeCaptcha", exibeCaptcha);
                model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
            }

            // Ades selecionadas para renengociação
            List<String> adesReneg = null;
            if ((chkAde != null) && (chkAde.length > 0)) {
                adesReneg = Arrays.asList(chkAde);
            }
            final List<CustomTransferObject> autdesList = new ArrayList<>();
            try {
                // Se a renegociação vem pela operação de renegociação, então
                // é esperado uma lista de códigos de contratos (mesmo que seja apenas 1)
                for (final String string : adesReneg) {
                    final CustomTransferObject adeTO = pesquisarConsignacaoController.buscaAutorizacao(string, responsavel);
                    autdesList.add(adeTO);
                }
            } catch (final AutorizacaoControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                LOG.error(e.getMessage(), e);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            for (final CustomTransferObject ctoAde : autdesList) {
                if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(incMargem, (Short) ctoAde.getAttribute(Columns.ADE_INC_MARGEM), responsavel)) {
                    rseMargemRest = rseMargemRest.add(new BigDecimal(ctoAde.getAttribute(Columns.ADE_VLR).toString()));
                }
            }
            final String margemConsignavel = rseMargemRest.toString();

            // Se tipo valor igual a margem total, coloca no campo de adeVlr o
            // valor da margem disponível para o serviço
            if (CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(tipoVlr)) {
                adeVlrPadrao = NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang());
            }

            String adePeriodicidade = JspHelper.verificaVarQryStr(request, "adePeriodicidade");
            adePeriodicidade = TextHelper.isNull(adePeriodicidade) ? PeriodoHelper.getPeriodicidadeFolha(responsavel) : adePeriodicidade;
            final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);

            // Dados para simulação
            final String adeVlr = JspHelper.verificaVarQryStr(request, "ADE_VLR");
            final String vlrLiberado = JspHelper.verificaVarQryStr(request, "VLR_LIBERADO");

            model.addAttribute("chkAde", chkAde);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("rseCodigo", rseCodigo);

            // Parâmetros de sistema
            model.addAttribute("permiteSimularSemMargem", permiteSimularSemMargem);
            model.addAttribute("floatQtdeColunasSimulacao", floatQtdeColunasSimulacao);

            // Parâmetros de serviço
            model.addAttribute("alteraAdeVlr", alteraAdeVlr);
            model.addAttribute("adeVlrPadrao", adeVlrPadrao);

            // Dados do servidor
            model.addAttribute("servidor", servidor);

            // Margem
            model.addAttribute("margemDisponivel", margemDisponivel);
            model.addAttribute("exibeMargem", exibeMargem);
            model.addAttribute("podeMostrarMargem", podeMostrarMargem);
            model.addAttribute("rseMargemRest", rseMargemRest);

            model.addAttribute("autdesList", autdesList);
            model.addAttribute("margemConsignavel", margemConsignavel);
            model.addAttribute("adePeriodicidade", adePeriodicidade);
            model.addAttribute("permiteEscolherPeriodicidade", permiteEscolherPeriodicidade);

            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("vlrLiberado", vlrLiberado);

        } catch (ParametroControllerException | ServidorControllerException | NumberFormatException | ViewHelperException | ParseException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/simularRenegociacao/simularRenegociacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=visualizarRanking" })
    public String visualizarRanking(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Verifica os contratos selecionados para renegociação
            final String[] chkAde = request.getParameterValues("chkADE");

            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
            final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
            final String csaIdentificador = JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR");
            final String csaNome = JspHelper.verificaVarQryStr(request, "CSA_NOME");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            final String rseCodigo = (responsavel.isSer()) ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            // Verifica obrigatóriedade do código do serviço e código do registro servidor
            if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Parâmetros de sistema
            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            final boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);
            final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);
            if (!simulacaoMetodoBrasileiro && (temCET || !simulacaoPorTaxaJuros)) {
              session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.parametrizacao.taxa.iva", responsavel));
              return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String qtdeColunasSimulacao = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel))
                                        ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel).toString()
                                        : "4";
            float floatQtdeColunasSimulacao = Float.parseFloat(qtdeColunasSimulacao);
            if (floatQtdeColunasSimulacao < 1) {
                floatQtdeColunasSimulacao = 1;
            } else if (floatQtdeColunasSimulacao > 4) {
                floatQtdeColunasSimulacao = 4;
            }

            // Parâmetros de serviço
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final Short incMargem  = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            final int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();

            // Dados do servidor
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();

            // Verifica se pode mostrar margem
            final MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, responsavel);
            BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

            // Ades selecionadas para renengociação
            List<String> adesReneg = null;
            if ((chkAde != null) && (chkAde.length > 0)) {
                adesReneg = Arrays.asList(chkAde);
            }
            final List<CustomTransferObject> autdesList = new ArrayList<>();
            try {
                // Se a renegociação vem pela operação de renegociação, então
                // é esperado uma lista de códigos de contratos (mesmo que seja apenas 1)
                for (final String string : adesReneg) {
                    final CustomTransferObject adeTO = pesquisarConsignacaoController.buscaAutorizacao(string, responsavel);
                    autdesList.add(adeTO);
                }
            } catch (final AutorizacaoControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                LOG.error(e.getMessage(), e);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            for (final CustomTransferObject ctoAde : autdesList) {
                if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(incMargem, (Short) ctoAde.getAttribute(Columns.ADE_INC_MARGEM), responsavel)) {
                    rseMargemRest = rseMargemRest.add(new BigDecimal(ctoAde.getAttribute(Columns.ADE_VLR).toString()));
                }
            }

            String adePeriodicidade = JspHelper.verificaVarQryStr(request, "adePeriodicidade");
            adePeriodicidade = TextHelper.isNull(adePeriodicidade) ? PeriodoHelper.getPeriodicidadeFolha(responsavel) : adePeriodicidade;

            // Dados para simulação
            String adeVlr = JspHelper.verificaVarQryStr(request, "ADE_VLR");
            String vlrLiberado = JspHelper.verificaVarQryStr(request, "VLR_LIBERADO");

            boolean vlrOk = true;
            if (!"".equals(adeVlr)) {
                try {
                    adeVlr = NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en");
                    vlrOk = (rseMargemRest.compareTo(new BigDecimal(adeVlr)) >= 0) || permiteSimularSemMargem;
                    if (!vlrOk) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.valor.prestacao.maior.margem", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                } catch (final java.text.ParseException ex) {
                    adeVlr = "";
                }
            }
            if (!"".equals(vlrLiberado)) {
                try {
                    vlrLiberado = NumberHelper.reformat(vlrLiberado, NumberHelper.getLang(), "en");
                } catch (final java.text.ParseException ex) {
                    vlrLiberado = "";
                }
            }
            if (TextHelper.isNull(adeVlr) && TextHelper.isNull(vlrLiberado)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.simulacao.valor.prestacao.valor.solicitado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Simular renegociação
            List<TransferObject> simulacao = simulacaoController.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, "".equals(adeVlr) ? null : new BigDecimal(adeVlr),
                    "".equals(vlrLiberado) ? null : new BigDecimal(vlrLiberado), Short.parseShort("0"), null, true, false, adePeriodicidade, responsavel);
            simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);

            boolean taxaJurosManCsa = false;
            for (final TransferObject simulacaoAnalise : simulacao) {
                final BigDecimal cftVlrRef = !TextHelper.isNull(simulacaoAnalise.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(simulacaoAnalise.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
                if (!TextHelper.isNull(cftVlrRef)) {
                    taxaJurosManCsa = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, CodedValues.TPC_SIM, responsavel);
                    break;
                }
            }
            boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
            boolean teveValorParcelaForaMargem = exibeCETMinMax ? simulacao.stream().anyMatch(coeficiente -> coeficiente.getAttribute("VLR_PARCELA_FORA_MARGEM_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO).equals(true)) : false;
            boolean vlrLiberadoOk = exibeCETMinMax ? !teveValorParcelaForaMargem : true;

            model.addAttribute("chkAde", chkAde);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("rseCodigo", rseCodigo);

            // Parâmetros de sistema
            model.addAttribute("simulacaoPorTaxaJuros", simulacaoPorTaxaJuros);
            model.addAttribute("temCET", temCET);
            model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
            model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
            model.addAttribute("floatQtdeColunasSimulacao", floatQtdeColunasSimulacao);
            model.addAttribute("exibeCETMinMax", exibeCETMinMax);

            // Parâmetros de serviço
            model.addAttribute("qtdeConsignatariasSimulacao", qtdeConsignatariasSimulacao);

            // Dados do servidor
            model.addAttribute("servidor", servidor);
            model.addAttribute("orgCodigo", orgCodigo);

            // Margem
            model.addAttribute("adePeriodicidade", adePeriodicidade);

            // Simulação
            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("vlrLiberado", vlrLiberado);
            model.addAttribute("simulacao", simulacao);
            model.addAttribute("taxaJurosManCSA", taxaJurosManCsa);
            model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);

        } catch (ParametroControllerException | ServidorControllerException | SimulacaoControllerException | NumberFormatException | ViewHelperException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/simularRenegociacao/visualizarRankingRenegociacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
            final String csaIdentificador = JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR");
            final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
            final boolean leilaoReverso = ((responsavel.getFunCodigo() != null) && CodedValues.FUN_SOLICITAR_LEILAO_REVERSO.equals(responsavel.getFunCodigo()));
            final String horasEncerramentoLeilao = (ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel).toString() : "N/A");

            if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String[] chkAde = request.getParameterValues("chkADE");

            // Ades selecionadas para renengociação
            List<String> adesReneg = null;
            if ((chkAde != null) && (chkAde.length > 0)) {
                adesReneg = Arrays.asList(chkAde);
            }
            List<TransferObject> autdesList = new ArrayList<>();
            try {
                autdesList = pesquisarConsignacaoController.pesquisarAutorizacoes(adesReneg, responsavel.getTipoEntidade(), responsavel);
                if ((autdesList == null) || autdesList.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.contrato.nao.encontrado", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca dados do servidor
            String rseCodigo = responsavel.getRseCodigo();
            String serCodigo = responsavel.getSerCodigo();
            String orgCodigo = responsavel.getOrgCodigo();

            if (!responsavel.isSer()) {
                rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
                CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
            }

            // Dados da ADE
            String adeVlr = JspHelper.verificaVarQryStr(request, "ADE_VLR");
            final String adeVlrTac = JspHelper.verificaVarQryStr(request, "ADE_VLR_TAC");
            final String adeVlrIof = JspHelper.verificaVarQryStr(request, "ADE_VLR_IOF");
            final String adeVlrCat = JspHelper.verificaVarQryStr(request, "ADE_VLR_CAT"); // simulacaoMetodoMexicano
            final String adeVlrIva = JspHelper.verificaVarQryStr(request, "ADE_VLR_IVA"); // simulacaoMetodoMexicano
            final String adePeriodicidade = JspHelper.verificaVarQryStr(request, "ADE_PERIODICIDADE"); // simulacaoMetodoMexicano
            final String cftCodigo = JspHelper.verificaVarQryStr(request, "CFT_CODIGO");
            final String dtjCodigo = JspHelper.verificaVarQryStr(request, "DTJ_CODIGO");
            final String przVlr = JspHelper.verificaVarQryStr(request, "PRZ_VLR");
            String vlrLiberado = JspHelper.verificaVarQryStr(request, "VLR_LIBERADO");
            boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("vlrLiberadoOk"));
            final Integer prazo = (!TextHelper.isNull(przVlr) ? Integer.valueOf(przVlr) : null);
            final BigDecimal valor = (!TextHelper.isNull(adeVlr) ? new BigDecimal(NumberHelper.parse(adeVlr, "en")) : null);
            final BigDecimal liberado = (!TextHelper.isNull(vlrLiberado) ? new BigDecimal(NumberHelper.parse(vlrLiberado, "en")) : null);

            if (!"".equals(adeVlr)) {
                adeVlr = NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang());
            }

            if (!"".equals(vlrLiberado)) {
                vlrLiberado = NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang());
            }

            // Busca os dados do convênio
            CustomTransferObject convenio = null;
            try {
                convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                final String acao = ((tipo != null) && "simula_renegociacao".equals(tipo) ? "RENEGOCIAR" : "RESERVAR");

                // Verifica se as entidades não estão bloqueadas
                autorizacaoController.podeReservarMargem(cnvCodigo, null, rseCodigo, true, true, true, adesReneg, valor, liberado, prazo, 0, adePeriodicidade, null, null, acao, true, false, responsavel);

            } catch (final Exception e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                LOG.error(e.getMessage(), e);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os parâmetros do serviço
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final int carenciaMinCse = ((paramSvcCse.getTpsCarenciaMinima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMinima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
            final int carenciaMaxCse = ((paramSvcCse.getTpsCarenciaMaxima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMaxima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
            final boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();
            final boolean exibirTabelaPrice = paramSvcCse.isTpsExibeTabelaPrice();
            String paramExibeCampoCidade = paramSvcCse.getTpsExibeCidadeConfirmacaoSolicitacao();
            if (TextHelper.isNull(paramExibeCampoCidade)) {
                paramExibeCampoCidade = CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO;
            }
            final boolean campoCidadeObrigatorio = CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO.equals(paramExibeCampoCidade);

            // Parâmetros de convênio
            final int carenciaMinima = ((convenio.getAttribute("CARENCIA_MINIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MINIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
            final int carenciaMaxima = ((convenio.getAttribute("CARENCIA_MAXIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MAXIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

            // Define os valores de carência mínimo e máximo
            final int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
            final int carenciaMinPermitida = carenciaPermitida[0];

            // Busca parâmetros de sistema
            final boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);
            final boolean quinzenal = simulacaoMetodoMexicano && CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePeriodicidade);

            final Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, carenciaMinPermitida, adePeriodicidade, responsavel);
            final Date adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, Integer.valueOf(przVlr), adePeriodicidade, responsavel);

            final String dataIni = (adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "");
            final String dataFim = (adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "");

            final boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exigeMunicipioLotacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

            // Verifica se permite servidor escolher correspondentes
            List<TransferObject> lstCorrespondentes = null;
            final String permiteEscolherCorresp = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_SERVIDOR_ESCOLHER_COR_SIMULACAO, responsavel);
            if (!TextHelper.isNull(permiteEscolherCorresp) && "S".equalsIgnoreCase(permiteEscolherCorresp)) {
                final CorrespondenteTransferObject cor = new CorrespondenteTransferObject();
                cor.setCsaCodigo(csaCodigo);
                cor.setCorAtivo(CodedValues.STS_ATIVO);
                lstCorrespondentes = consignatariaController.lstCorrespondentes(cor, responsavel);
            }

            boolean temBloqueioLeilao = false;
            try {
                autorizacaoController.verificaBloqueioFuncao(rseCodigo, "LEILAO", responsavel);
            } catch (final AutorizacaoControllerException ex) {
                temBloqueioLeilao = true;
            }

            final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);

            if (exibirTabelaPrice &&
                    !TextHelper.isNull(liberado) &&
                    !TextHelper.isNull(valor) &&
                    !TextHelper.isNull(prazo) &&
                    !TextHelper.isNull(cftCodigo)) {

                try {
                    final TransferObject cft = simulacaoController.getCoeficienteAtivo(cftCodigo);

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
                } catch (final SimulacaoControllerException e) {
                    LOG.error(e.getMessage(), e);
                    session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("tipo", tipo);
            model.addAttribute("leilaoReverso", leilaoReverso);
            model.addAttribute("horasEncerramentoLeilao", !TextHelper.isNull(horasEncerramentoLeilao) ? String.valueOf(Integer.valueOf(horasEncerramentoLeilao) / 60) : horasEncerramentoLeilao);

            model.addAttribute("chkAde", chkAde);
            model.addAttribute("autdesList", autdesList);
            model.addAttribute("convenio", convenio);

            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("orgCodigo", orgCodigo);

            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("adeVlrTac",adeVlrTac);
            model.addAttribute("adeVlrIof", adeVlrIof);
            model.addAttribute("adeVlrCat", adeVlrCat);
            model.addAttribute("adeVvlrIva", adeVlrIva);
            model.addAttribute("adePeriodicidade", adePeriodicidade);
            model.addAttribute("cftCodigo", cftCodigo);
            model.addAttribute("dtjCodigo", dtjCodigo);
            model.addAttribute("przVlr", przVlr);
            model.addAttribute("vlrLiberado", vlrLiberado);
            model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);

            model.addAttribute("carenciaMinCse", carenciaMinCse);
            model.addAttribute("carenciaMaxCse", carenciaMaxCse);
            model.addAttribute("exigeCodAutSolicitacao", exigeCodAutSolicitacao);
            model.addAttribute("exibirTabelaPrice", exibirTabelaPrice);
            model.addAttribute("campoCidadeObrigatorio", campoCidadeObrigatorio);
            model.addAttribute("carenciaMinima", carenciaMinima);
            model.addAttribute("carenciaMaxima", carenciaMaxima);
            model.addAttribute("carenciaMinPermitida", carenciaMinPermitida);

            model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
            model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
            model.addAttribute("quinzenal", quinzenal);

            model.addAttribute("adeAnoMesIni", adeAnoMesIni);
            model.addAttribute("adeAnoMesFim", adeAnoMesFim);
            model.addAttribute("dataIni", dataIni);
            model.addAttribute("dataFim", dataFim);

            model.addAttribute("exigeTelefone", exigeTelefone);
            model.addAttribute("exigeMunicipioLotacao", exigeMunicipioLotacao);

            model.addAttribute("temBloqueioLeilao", temBloqueioLeilao);
            model.addAttribute("lstCorrespondentes", lstCorrespondentes);

            model.addAttribute("tdaList", tdaList);

            model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);

        } catch (AutorizacaoControllerException | ServidorControllerException | ParametroControllerException | ConsignatariaControllerException | PeriodoException | NumberFormatException | ParseException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/simularRenegociacao/confirmarRenegociacao", request, session, model, responsavel);
    }


    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.simular.renegociacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/simularRenegociacao");
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para selecionar consignação para renegociação
        final String link = "../v3/simularRenegociacao?acao=simular";
        final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        final String msgAlternativa = "";
        final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.simular.renegociacao", responsavel);
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("RENE_CONTRATO", CodedValues.FUN_RENE_CONTRATO, descricao, "renegociar_contrato.gif", "btnRenegociarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, "chkADE"));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "simular_renegociacao");

        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        return criterio;
    }

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_SIMULAR_RENEGOCIACAO;
    }
}
