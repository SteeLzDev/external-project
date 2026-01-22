package com.zetra.econsig.helper.usuario;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.UUID;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: LoginHelper</p>
 * <p>Description: Helper utilizado pelas telas de login do sistema</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LoginHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LoginHelper.class);

    /**
     * Constante para guardar nome do consignante no param sist
     */
    private static final String NOME_CONSIGNANTE = "NOME_CONSIGNANTE";

    /**
     * Retorna o nome do consignante para ser exibido na tela de login,
     * caso o parâmetro TPC_MOSTRA_NOME_CSE_LOGIN esteja configurado
     * corretamente.
     * @param request
     * @param responsavel
     * @return
     * @throws Exception
     */
    public static String getCseNome(AcessoSistema responsavel) {
        String nomeCse = (String) ParamSist.getInstance().getParam(NOME_CONSIGNANTE, responsavel);
        if (nomeCse == null) {
            synchronized (LoginHelper.class) {
                if (nomeCse == null) {
                    try {
                        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                        ParamSist.getInstance().setParam(NOME_CONSIGNANTE, consignante.getCseNome());
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }

        nomeCse = "";
        String mostraNomeCse = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MOSTRA_NOME_CSE_LOGIN, responsavel);

        if (mostraNomeCse == null || !mostraNomeCse.equals(CodedValues.TPC_NAO)) {
            nomeCse = (String) ParamSist.getInstance().getParam(NOME_CONSIGNANTE, responsavel);

        }

        return nomeCse;
    }

    public static void setCseNome(String cseNome) {
    	ParamSist.getInstance().setParam(NOME_CONSIGNANTE, cseNome);
    }

    /**
     * Retorna mensagem para ser exibida ao usuário quando este estiver bloqueado.
     * Verifica parâmetro de sistema TPC_MSG_USUARIO_BLOQUEADO sobre o uso da mensagem
     * específica.
     * @param request
     * @param responsavel
     * @return
     */
    public static String getMsgUsuarioBloq(HttpServletRequest request, AcessoSistema responsavel) {
        HttpSession session = request.getSession();
        ServletContext application = session.getServletContext();

        String msgUsuBloqueado = null;
        boolean usaMsgBloqueioUsu = ParamSist.getBoolParamSist(CodedValues.TPC_MSG_USUARIO_BLOQUEADO, responsavel);

        if (usaMsgBloqueioUsu) {
            if (application.getAttribute("msgUsuBloqueado") == null) {
                synchronized (LoginHelper.class) {
                    if (application.getAttribute("msgUsuBloqueado") == null) {

                        String absolutePath = ParamSist.getDiretorioRaizArquivos()
                                            + File.separatorChar + "txt"
                                            + File.separatorChar + "usuario";

                        String fileName = absolutePath + File.separatorChar + "usuario_bloqueado.txt";
                        File arquivo = new File(fileName);

                        if (arquivo.exists()) {
                            try {
                                msgUsuBloqueado = FileHelper.readAll(arquivo.getAbsolutePath());
                                if (msgUsuBloqueado.endsWith("\n")) {
                                    msgUsuBloqueado = msgUsuBloqueado.substring(0, msgUsuBloqueado.length()-1);
                                }
                            } catch(Exception ex) {
                                LOG.debug(ex.getMessage());
                            }
                        }

                        application.setAttribute("msgUsuBloqueado", msgUsuBloqueado);
                    }
                }
            }
            msgUsuBloqueado = (String) application.getAttribute("msgUsuBloqueado");
        }

        return msgUsuBloqueado;
    }

    /**
     * Retorna o salt dinamico para ser utilizado na validação de senha
     * durante o login de usuário
     * @param request
     * @param responsavel
     * @return
     */
    public static String getSaltDinamico(HttpServletRequest request, AcessoSistema responsavel) {
        HttpSession session = request.getSession();

        StringBuilder saltB = new StringBuilder();
        saltB.append((char)(65 + Math.round(Math.random() * 25)));
        saltB.append((char)(65 + Math.round(Math.random() * 25)));
        String saltoDinamico = saltB.toString();
        session.setAttribute("usuario_senha", saltoDinamico);

        return saltoDinamico;
    }

    /**
     * Retorna a mensagem a ser exibida aos usuários quando o sistema está indisponível
     * @return
     */
    public static String getMensagemSistemaIndisponivel() {
        Object rotuloSistemaIndisp = ParamSist.getInstance().getParam(CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
        return (!TextHelper.isNull(rotuloSistemaIndisp)? rotuloSistemaIndisp.toString() :
            ApplicationResourcesHelper.getMessage("rotulo.sistema.indisponivel", null));
    }

    /**
     * Retorna a mensagem a ser exibida aos usuários caso usuário/senha esteja inválido no login
     * @return
     */
    public static String getMensagemErroLogin() {
        Object msgErroLogin = ParamSist.getInstance().getParam(CodedValues.TPC_MENSAGEM_ERRO_LOGIN, AcessoSistema.getAcessoUsuarioSistema());
        return (!TextHelper.isNull(msgErroLogin)? msgErroLogin.toString() :
            ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", null));
    }

    /**
     * Retorna a mensagem a ser exibida aos usuários servidores caso usuário/senha esteja inválido no login
     * @return
     */
    public static String getMensagemErroLoginServidor() {
        Object msgErroLogin = ParamSist.getInstance().getParam(CodedValues.TPC_MENSAGEM_ERRO_LOGIN_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());
        return (!TextHelper.isNull(msgErroLogin)? msgErroLogin.toString() :
            ApplicationResourcesHelper.getMessage("mensagem.erroLoginServidor", null));
    }

    /**
     * Retorna a máscara a ser usada nos campos de login de servidor
     * @return
     */
    public static String getMascaraLoginServidor() {
        Object mascaraLoginServidor = ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());
        if (!TextHelper.isNull(mascaraLoginServidor)) {
            // Se tem máscara para o campo de login, então retorna o valor do parâmetro
            return mascaraLoginServidor.toString();
        } else {
            // Se não tem máscara para o campo de login de servidor, então vê se a matrícula é numérica
            boolean isMatriculaNumerica = ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean loginComCfp = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_CPF, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            String mask = "";
            if (loginComCfp) {
                mask = LocaleHelper.getCpfMask();
            } else if (isMatriculaNumerica) {
                mask = "#D20";
            } else {
                mask = "#L32";
            }

            return mask;
        }
    }

    /**
     * Retorna a máscara do campo matrícula, quando esse não representa o login servidor
     * @return
     */
    public static String getMascaraMatriculaServidor() {
        // Se não tem máscara para o campo de login de servidor, então vê se a matrícula é numérica
        boolean isMatriculaNumerica = ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // Tamanho máximo para a matrícula
        Object paramMaxMatricula = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, AcessoSistema.getAcessoUsuarioSistema());
        int tamMaxMatricula = TextHelper.isNum(paramMaxMatricula) ? (Integer.parseInt(paramMaxMatricula.toString()) == 0 ? 20 : Integer.parseInt(paramMaxMatricula.toString())) : 20;

        return (isMatriculaNumerica ? "#D" : "#L") + String.valueOf(Math.min(tamMaxMatricula, 20));
    }

    /**
     * Retorna a máscara de login externo a ser usada nos campos de login de servidor
     * @return
     */
    public static String getMascaraLoginExternoServidor() {
        Object mascaraLoginExternoServidor = ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());
        if (!TextHelper.isNull(mascaraLoginExternoServidor)) {
            // Se tem máscara para o campo de login, então retorna o valor do parâmetro
            return mascaraLoginExternoServidor.toString();
        } else {
            // Retorna uma máscara padrão bloqueando o acesso pois o sistema não está configurado de acordo.
            return "#*0";
        }
    }

    /**
     * Retorna a url da página de login de usuários, de acordo com o parâmetro de sistema correspondente
     * @return
     * OBS: Refatorado para diminuir o impacto no código pois, na verdade, não era mais preciso ter este método.
     */
    public static String getPaginaLogin() {
        return "../v3/autenticarUsuario";
    }

    /**
     * Retorna a url da página de login de servidor, de acordo com o parâmetro de sistema correspondente
     * @return
     * OBS: Refatorado para diminuir o impacto no código pois, na verdade, não era mais preciso ter este método.
     */
    public static String getPaginaLoginServidor() {
        return "../v3/autenticar";
    }

    /**
     * Retorna a url da página de login de acordo com o papel do usuário, ou de acordo com os
     * cookies, caso não tenha sessão de usuário
     * @param request
     * @param responsavel
     * @return
     */
    public static String getPaginaLoginPeloPapel(HttpServletRequest request, AcessoSistema responsavel) {
        String paginaLogin = null;
        if (responsavel.isSer()) {
            paginaLogin = getPaginaLoginServidor();
        } else if (responsavel.isCseSupOrg() || responsavel.isCsaCor()) {
            paginaLogin = getPaginaLogin();
        } else // Se não é servidor e nenhuma outra entidade, então significa
        // que a sessão expirou e não tenho qual o tipo de entidade.
        // Verifica então pelo cookie
        if (temCookieAcessoServidor(request)) {
            paginaLogin = getPaginaLoginServidor();
        }

        // Se não conseguiu definir a telaLogin, usa a tela padrão
        if (TextHelper.isNull(paginaLogin)) {
            paginaLogin = getPaginaLogin();
        }

        return paginaLogin;
    }

    /**
     * Retorna a url da página de login de acordo com o papel do usuário, ou de acordo com os
     * cookies, caso não tenha sessão de usuário
     * @param request
     * @param responsavel
     * @return
     */
    public static String getPaginaExpiracaoServidor(HttpServletRequest request, AcessoSistema responsavel) {
        String urlExternaSairSistemaSer = null;
        if (responsavel.isSer() || temCookieAcessoServidor(request)) {
            urlExternaSairSistemaSer = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_EXTERNA_SAIR_SISTEMA_SERVIDOR, responsavel);
        }

        return urlExternaSairSistemaSer;
    }

    public static boolean temCookieAcessoServidor(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("LOGIN") && cookie.getValue().equals("SERVIDOR")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recupera o par de chaves RSA da sessão do usuário.
     * @param request
     * @return
     */
    public static KeyPair getRSAKeyPair(HttpServletRequest request) {
        KeyPair keyPair = (request == null) ? null : (KeyPair) request.getSession().getAttribute(CodedValues.RSA_KEY_PAIR_SESSION_ATTR_NAME);

        // Se o par não foi encontrado na sessão, cria um novo e o armazena.
        if (keyPair == null) {
            keyPair = RSA.generateKeyPair(CodedValues.RSA_KEY_SIZE);
            request.getSession().setAttribute(CodedValues.RSA_KEY_PAIR_SESSION_ATTR_NAME, keyPair);
        }

        return keyPair;
    }

    /**
     * Recupera o par de chaves RSA da sessão do usuário com chave definida pela operação
     * @param request
     * @param keyPairName
     * @return
     */
    public static KeyPair getRSAKeyPair(HttpServletRequest request, String keyPairName) {
        KeyPair keyPair = (request == null) ? null : (KeyPair) request.getSession().getAttribute(keyPairName);

        // Se o par não foi encontrado na sessão, cria um novo e o armazena.
        if (keyPair == null) {
            keyPair = RSA.generateKeyPair(CodedValues.RSA_KEY_SIZE);
            request.getSession().setAttribute(keyPairName, keyPair);
        }

        return keyPair;
    }

    /**
     * Recupera a mensagem que será exibida no login de duas etapas de acordo com o parâmetro TPC_MASCARA_NOME_LOGIN
     * @param responsavel
     * @return
     */
    public static String getMensagemMascaraNomeLogin(AcessoSistema responsavel) {
        /*
         * 0 - Exibir nome completo (default)
         * 1 - Exibir apenas o primeiro e segundo nome
         * 2 - Exibir apenas o primeiro e último nome
         * 3 - Exibir apenas as iniciais, seguida de ponto, de cada parte do nome
         * 4 - Exibir apenas o primeiro nome e as iniciais das demais partes
         */

        // O padrão é a mensagem do valor 0
        String retorno = ApplicationResourcesHelper.getMessage("mensagem.login.outro.username", responsavel);

        if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "1", responsavel)) {
            retorno = ApplicationResourcesHelper.getMessage("mensagem.login.outro.username.mascara.1", responsavel);
        } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "2", responsavel)) {
            retorno = ApplicationResourcesHelper.getMessage("mensagem.login.outro.username.mascara.2", responsavel);
        } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "3", responsavel)) {
            retorno = ApplicationResourcesHelper.getMessage("mensagem.login.outro.username.mascara.3", responsavel);
        } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "4", responsavel)) {
            retorno = ApplicationResourcesHelper.getMessage("mensagem.login.outro.username.mascara.4", responsavel);
        }
        return retorno;
    }

    public static String getOAuth2UriAuthentication(String acao, AcessoSistema responsavel) {
        /*
        ?response_type=code
        &scope=...
        &state=...
        &client_id=...
        &redirect_uri=...
        &force_login=true // necessário para que seja possível fazer o logout do eConsig.
      */
      String oAuth2UriAuthentication = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_1_CODE.getValor();
      String oAuth2ClientId = ParamSenhaExternaEnum.OAUTH2_CLIENT_ID.getValor();
      String oAuth2scopeUri = !TextHelper.isNull(ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_SCOPE.getValor()) ? "&scope="+ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_SCOPE.getValor() : "";

      String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
      String urlAutenticacao = urlSistema + (urlSistema.endsWith("/") ? "v3" : "/v3") + "/autenticarOAuth2";

      try {
          urlAutenticacao = URLEncoder.encode(urlAutenticacao, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException ex) {
          LOG.error(ex.getMessage(), ex);
      }

      return oAuth2UriAuthentication
              + (oAuth2UriAuthentication.indexOf('?') == -1 ? "?" : "&")
              + "client_id=" + oAuth2ClientId
              + "&redirect_uri=" + urlAutenticacao
              + "&force_login=true"
              + oAuth2scopeUri
              + "&nonce=" + UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    public static String getLoginServidor(String matricula, String codigoEntidade, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel)) {
            final OrgaoTransferObject orgao = consignanteController.findOrgao(codigoEntidade, responsavel);
            final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimento(orgao.getEstCodigo(), responsavel);
            final String estIdentificador = estabelecimento.getEstIdentificador();
            final String orgIdentificador = orgao.getOrgIdentificador();
            return estIdentificador + "-" + orgIdentificador + "-" + matricula;

        } else {
            final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimento(codigoEntidade, responsavel);
            final String estIdentificador = estabelecimento.getEstIdentificador();
            return estIdentificador + "-" + matricula;
        }
    }

    public static String getMatriculaRequisicao(HttpServletRequest request, String nomeCampoMatricula, AcessoSistema responsavel) {
        String matricula = JspHelper.verificaVarQryStr(request, nomeCampoMatricula);

        if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(matricula)) {
            try {
                matricula = Long.valueOf(matricula).toString();
            } catch (final NumberFormatException ex) {
            }
        }

        return matricula;
    }
}
