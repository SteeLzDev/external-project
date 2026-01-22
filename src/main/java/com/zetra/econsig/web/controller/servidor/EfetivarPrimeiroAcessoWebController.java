package com.zetra.econsig.web.controller.servidor;

import java.security.KeyPair;
import java.util.Map;

import javax.crypto.BadPaddingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>Title: EfetivarPrimeiroAcessoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Primeiro Acesso.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/efetivarPrimeiroAcesso" })
public class EfetivarPrimeiroAcessoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EfetivarPrimeiroAcessoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar", "usu=servidor" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        responsavel.setUsuCodigo(CodedValues.USU_CODIGO_SISTEMA);

        try {
            // Valores retornados pelo primeiro acesso e são utilizados nos passos seguintes
            String usuCodigo = "";
            String token = "";

            // Valores para login
            String estCodigo = "";
            String estIdentificador = "";
            String orgCodigo = "";
            String orgIdentificador = "";
            String matricula = "";

            boolean isSer = JspHelper.verificaVarQryStr(request, "usu").equals("servidor");
            String passo = JspHelper.verificaVarQryStr(request, "passo");
            if (TextHelper.isNull(passo)) {
                passo = "passo1";
            }

            if (JspHelper.verificaVarQryStr(request, "usu").isEmpty()) {
                String telaLogin = LoginHelper.getPaginaLoginPeloPapel(request, responsavel) + "?t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
                response.sendRedirect(telaLogin);
                return viewRedirect("jsp/editarServidor/listarOcorrenciaServidor", request, session, model, responsavel);
            } else if (isSer) {
                response.addCookie(new Cookie("LOGIN", "SERVIDOR"));
            } else {
                response.addCookie(new Cookie("LOGIN", ""));

                //com as novas interfaces do spring os cookies são gravados com domínios diferentes
                JspHelper.setaCookieLogin(response, request.getContextPath());
            }

            boolean primeiroAcessoViaOTP = ParamSist.paramEquals(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, CodedValues.TPC_SIM, responsavel);

            if (!isSer || !primeiroAcessoViaOTP) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            int intpwdStrength = 0;
            int pwdStrengthLevel = 0;
            String strpwdStrengthLevel = "";
            String pwdStrength = "3";
            String strMensagemSenha = "";
            String strMensagemSenha1 ="";
            String strMensagemSenha2 = "";
            String strMensagemSenha3 = "";
            String strMensagemErroSenha = "";
            int tamMinSenhaServidor = 6;
            int tamMaxSenhaServidor = 8;
            boolean ignoraSeveridade = false;

            try {
                if (passo.equals("passo2") || passo.equals("passo3") || passo.equals("passo4")) {
                    usuCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuCodigo")) ? JspHelper.verificaVarQryStr(request, "usuCodigo").toString() : "";
                    String serCpf = JspHelper.verificaVarQryStr(request, "CPF");
                    String otp = JspHelper.verificaVarQryStr(request, "OTP");
                    String serEmail = JspHelper.verificaVarQryStr(request, "SER_EMAIL");
                    String serDddCelular = JspHelper.verificaVarQryStr(request, "SER_DDD_CELULAR");
                    String serCelular = JspHelper.verificaVarQryStr(request, "SER_CELULAR");

                    // Valida o captcha informado
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                            && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                        throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    }
                    if (!passo.equals("passo4")) {
                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                    }

                    if (passo.equals("passo2")) {
                        Map<String, Object> retorno = usuarioController.primeiroAcesso(serCpf, null, otp, responsavel);
                        usuCodigo = !TextHelper.isNull(retorno.get("usuCodigo")) ? retorno.get("usuCodigo").toString() : "";
                        token = !TextHelper.isNull(retorno.get("token")) ? retorno.get("token").toString() : "";

                    } else if (passo.equals("passo3")) {
                        token = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "token")) ? JspHelper.verificaVarQryStr(request, "token").toString() : "";
                        usuarioController.enviaOTPServidor(usuCodigo, token, serEmail, serDddCelular + serCelular, responsavel);

                    } else if (passo.equals("passo4")) {
                        String usuSenha = "";
                        String usuSenhaCrypt = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
                        try {
                            KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                            usuSenha = RSA.decrypt(usuSenhaCrypt, keyPair.getPrivate());
                        } catch (BadPaddingException e) {
                            throw new UsuarioControllerException(LoginHelper.getMensagemErroLoginServidor(), responsavel);
                        }

                        usuarioController.validaOTPServidor(usuCodigo, usuSenha, false, otp, serEmail, serDddCelular + serCelular, false, responsavel);

                        // Faz login do servidor
                        return "forward:/v3/autenticar?acao=autenticar";
                    }
                }

                if (JspHelper.verificaVarQryStr(request, "SEM_OTP").equals("S")) {
                    passo = "passo3";
                }

                if (passo.equals("passo3")) {
                    // Save Token
                    SynchronizerToken.saveToken(request);

                    pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";

                    try {
                        tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
                    } catch (Exception ex) {
                        tamMinSenhaServidor = 6;
                    }
                    try {
                        tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
                    } catch (Exception ex) {
                        tamMaxSenhaServidor = 8;
                    }

                    // Transforma o parâmetro em um número inteiro
                    try {
                        intpwdStrength = Integer.parseInt(pwdStrength);
                    } catch (NumberFormatException ex) {
                        intpwdStrength = 3;
                    }

                    ignoraSeveridade = intpwdStrength == 0;
                    pwdStrengthLevel = 1; // very weak
                    strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.muito.baixo", responsavel);
                    String nivel = "muito.baixo";
                    if (intpwdStrength == 2) { // weak
                        pwdStrengthLevel = 16;
                        strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.baixo", responsavel);
                        nivel = "baixo";
                    } else if (intpwdStrength == 3) { // mediocre
                        pwdStrengthLevel = 25;
                        strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.medio", responsavel);
                        nivel = "medio";
                    } else if (intpwdStrength >= 4) { // strong
                        pwdStrengthLevel = 35;
                        strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.alto", responsavel);
                        nivel = "alto";
                    }
                    String chave = "rotulo.ajuda.alteracaoSenha." + nivel;
                    if (!isSer) {
                        chave += ".geral";
                    } else {
                        chave += ".servidor";
                        boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
                        if (senhaServidorNumerica) {
                            chave += ".numerica";
                        }
                    }
                    strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
                    strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
                    strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
                    strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
                    strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);

                    // Procura usuário pelo código
                    UsuarioTransferObject usuarioTO = usuarioController.findUsuario(usuCodigo, responsavel);
                    String usuLogin = usuarioTO.getUsuLogin();
                    matricula = usuLogin.substring(usuLogin.lastIndexOf("-") + 1, usuLogin.length());
                    String[] arrLogin = usuLogin.split("-");

                    int i = 0;
                    while (!arrLogin[i].equalsIgnoreCase(matricula)) {
                        if (TextHelper.isNull(estIdentificador)) {
                            estIdentificador = arrLogin[i++];
                        } else if (TextHelper.isNull(orgIdentificador)) {
                            orgIdentificador = arrLogin[i++];
                        }
                    }

                    if (!TextHelper.isNull(estIdentificador)) {
                        EstabelecimentoTransferObject est = consignanteController.findEstabelecimentoByIdn(estIdentificador, responsavel);
                        estCodigo = est.getEstCodigo();
                    }

                    if (!TextHelper.isNull(orgIdentificador)) {
                        OrgaoTransferObject org = consignanteController.findOrgaoByIdn(orgIdentificador, estCodigo, responsavel);
                        orgCodigo = org.getOrgCodigo();
                    }
                }
            } catch (Exception ex) {
                // Exibe mensagem de erro ao usuário
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                // Volta ao passo anterior
                passo = (passo.equals("passo2") ? "passo1" : passo.equals("passo3") ? "passo2" : passo.equals("passo4") ? "passo3" : "passo1");
            }

            model.addAttribute("intpwdStrength", intpwdStrength);
            model.addAttribute("pwdStrengthLevel", pwdStrengthLevel);
            model.addAttribute("strpwdStrengthLevel", strpwdStrengthLevel);
            model.addAttribute("pwdStrength", pwdStrength);
            model.addAttribute("tamMinSenhaServidor", tamMinSenhaServidor);
            model.addAttribute("tamMaxSenhaServidor", tamMaxSenhaServidor);
            model.addAttribute("ignoraSeveridade", ignoraSeveridade);
            model.addAttribute("strMensagemSenha", strMensagemSenha);
            model.addAttribute("strMensagemSenha1", strMensagemSenha1);
            model.addAttribute("strMensagemSenha2", strMensagemSenha2);
            model.addAttribute("strMensagemSenha3", strMensagemSenha3);
            model.addAttribute("strMensagemErroSenha", strMensagemErroSenha);
            model.addAttribute("passo", passo);
            model.addAttribute("usuCodigo", usuCodigo);
            model.addAttribute("token", token);
            model.addAttribute("matricula", matricula);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("orgIdentificador", orgIdentificador);
            model.addAttribute("estCodigo", estCodigo);
            model.addAttribute("estIdentificador", estIdentificador);

            return viewRedirect("jsp/editarServidor/efetivarPrimeiroAcesso", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
