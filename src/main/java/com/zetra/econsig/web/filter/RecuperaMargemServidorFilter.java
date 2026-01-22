package com.zetra.econsig.web.filter;

import static com.zetra.econsig.web.controller.AbstractWebController.isValidCaptcha;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: RecuperaMargemServidorFilter</p>
 * <p>Description: Filtro para recuperar a margem do servidor e atualizar na sessão do usuário.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class RecuperaMargemServidorFilter extends EConsigFilter {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperaMargemServidorFilter.class);

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (prosseguirSemExecutar(request)) {
            chain.doFilter(request, response);
            return;
        }
        final AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpSession session = httpRequest.getSession();
        final ParamSist paramSist = ParamSist.getInstance();
        boolean podeConsultar = false;
        boolean exigeCaptcha = false;
        boolean exibeCaptcha = false;
        boolean exibeMargemTopo = true;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;
        final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr((HttpServletRequest) request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr((HttpServletRequest) request, "validaCaptchaTopo") : "N";

        try {
            podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());
            // Verifica se deve exibir a margem do servidor no portal
            if (responsavel.isSer()
                    && responsavel.isSessaoValida()
                    && ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel)
                    && ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL, responsavel)) {

                // recupera o tempo (em segundos) de atualização da margem na sessão
                int tempoAtualizacao = CodedValues.TEMPO_DEFAULT_ATUALIZACAO_MARGEM_SESSAO_SER;
                final Object objTempoAtualizacao = paramSist.getParam(CodedValues.TPC_TEMPO_ATUALIZACAO_MARGEM_SESSAO_SER, AcessoSistema.getAcessoUsuarioSistema());
                try {
                    tempoAtualizacao = objTempoAtualizacao != null ? Integer.parseInt(objTempoAtualizacao.toString()) : tempoAtualizacao;
                } catch (final NumberFormatException ex) {
                    // caso o parâmetro esteja preenchido incorretamente, usa o valor default
                    tempoAtualizacao = CodedValues.TEMPO_DEFAULT_ATUALIZACAO_MARGEM_SESSAO_SER;
                }

                boolean atualizarDadosSessao = false;
                // verifica se passou o tempo necessário para atualizar as informações de margem na sessão
                if (session.getAttribute("dataAtualizacaoMargensRse") == null) {
                    // atualiza os dados caso ainda não tenham sido carregados na sessão
                    atualizarDadosSessao = true;
                } else {
                    // recupera a última atualização de margem do usuário servidor na sessão
                    Date dataUltAtualizacao = (Date) session.getAttribute("dataAtualizacaoMargensRse");
                    dataUltAtualizacao = DateHelper.addSeconds(dataUltAtualizacao, tempoAtualizacao);
                    final Date agora =  DateHelper.getSystemDatetime();
                    // int segundosUltAtualizacao = DateHelper.minDiff(dataUltAtualizacao) * 60;
                    if ((agora.compareTo(dataUltAtualizacao) > 0) || !podeConsultar) {
                        // atualiza os dados caso já tenha atingido o tempo de atualização
                        atualizarDadosSessao = true;
                    }
                }

                if (atualizarDadosSessao) {
                    // recupera dados das margens do servidor e atualiza na sessão
                    try {
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
                                    final Object captchaValido = session.getAttribute("validado") != null ? session.getAttribute("validado") : "N";

                                    if("N".equals(captchaValido)) {
                                        if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                                && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr((HttpServletRequest) request, "codigoCapTopo"))) {
                                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                            exigeCaptcha = true;
                                            exibeMargemTopo = false;
                                            session.setAttribute("validado", "N");
                                        } else {
                                            final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                            if (!margens.isEmpty()) {
                                                session.setAttribute("margensRse", margens);
                                                session.setAttribute("dataAtualizacaoMargensRse", DateHelper.getSystemDatetime());
                                            }
                                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                            session.setAttribute("validado", "S");
                                        }
                                    }
                                } else if (exibeCaptchaAvancado) {
                                    final String remoteAddr = request.getRemoteAddr();
                                    final Object captchaValido = session.getAttribute("validado") != null ? session.getAttribute("validado") : "N";

                                    if("N".equals(captchaValido)){
                                        if (!isValidCaptcha(request.getParameter("g-recaptcha-response_topo"),  remoteAddr, responsavel)) {
                                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                            exigeCaptcha = true;
                                            exibeMargemTopo = false;
                                            session.setAttribute("validado", "N");
                                        } else {
                                            final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                            if (!margens.isEmpty()) {
                                                session.setAttribute("margensRse", margens);
                                                session.setAttribute("dataAtualizacaoMargensRse", DateHelper.getSystemDatetime());
                                            }
                                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                            session.setAttribute("validado", "S");
                                        }
                                    }
                                }
                            } else {
                                final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                                if (exigeCaptchaDeficiente) {
                                    final String captchaAnswer = JspHelper.verificaVarQryStr((HttpServletRequest) request, "codigoCapTopo");

                                    if (captchaAnswer == null) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                        exibeMargemTopo = false;
                                    }

                                    final Object captchaValido = session.getAttribute("validado") != null ? session.getAttribute("validado") : "N";

                                    if ("N".equals(captchaValido)) {
                                        final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                        if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                            exigeCaptcha = true;
                                            exibeMargemTopo = false;
                                            session.setAttribute("validado", "N");
                                        } else {
                                            final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                                            if (!margens.isEmpty()) {
                                                session.setAttribute("margensRse", margens);
                                                session.setAttribute("dataAtualizacaoMargensRse", DateHelper.getSystemDatetime());
                                            }
                                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                            session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                            session.setAttribute("validado", "S");
                                        }
                                    }
                                }
                            }
                        } else if (podeConsultar) {
                            final List<MargemTO> margens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, null, null, true, true, responsavel);
                            if (!margens.isEmpty()) {
                                session.setAttribute("margensRse", margens);
                                session.setAttribute("dataAtualizacaoMargensRse", DateHelper.getSystemDatetime());
                            }
                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                        } else {
                            exigeCaptcha = true;
                            exibeMargemTopo = false;
                        }


                    } catch (final ServidorControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        exibeMargemTopo = false;
                    }
                }
                session.setAttribute("exigeCaptchaTopo", exigeCaptcha);
                session.setAttribute("exibeCaptcha", exibeCaptcha);
                session.setAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                session.setAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
                session.setAttribute("exibeMargemTopo", exibeMargemTopo);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}