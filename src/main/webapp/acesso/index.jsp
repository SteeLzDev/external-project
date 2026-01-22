<%@ page import="java.security.Key" %>
<%@ page import="java.security.KeyPair" %>
<%@ page import="com.zetra.econsig.exception.ZetraException" %>
<%@ page import="com.zetra.econsig.dto.entidade.AcessoTransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.ConsignanteTransferObject" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.acesso.AcessoHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.config.SysConfig" %>
<%@ page import="com.zetra.econsig.helper.criptografia.RSA" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="java.security.cert.X509Certificate" %>
<%@ include file="../geral/env_navegacao.jsp" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="cseDelegate" scope="page" class="com.zetra.econsig.delegate.ConsignanteDelegate"/>
<%
LOG.warn("20 - index.jsp");
String comando = request.getParameter(AcessoHelper.PARAMETRO_COMANDO);
LOG.warn("22 - comando: " + comando);
String certificado = request.getParameter(AcessoHelper.PARAMETRO_CERTIFICADO);
String versaoProtocolo = request.getParameter(AcessoHelper.PARAMETRO_VERSAO_PROTOCOLO);
String urlCentral = request.getParameter("urlCentralizador") != null ? request.getParameter("urlCentralizador") : ""; 
String attrSessionAcessoUrl = request.getParameter("attrSessionAcessoUrl") != null ? request.getParameter("attrSessionAcessoUrl") : "";
String parametrosCentral = request.getParameter("parametrosCentral") != null ? request.getParameter("parametrosCentral") : "";

String aliasEconsig = CodedValues.PROTOCOLO_KEYSTORE_ALIAS_ECONSIG_PROPERTY;

if (TextHelper.isNull(comando)) {
    out.clear();
    out.print(ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", null));
    return;
}

ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
AcessoHelper acessoHelper = new AcessoHelper();

String certificadoCentralizador = consignante.getCseCertificadoCentralizador();
// O Objetivo é forçar o eConsig a usar certificado de CA se já existir um armazenado, sem isto,
// seria vulnerável a um ataque de um Centralizador fake usando usando o protocolo antigo.
if (TextHelper.isNull(versaoProtocolo) && !TextHelper.isNull(certificadoCentralizador)) {
    versaoProtocolo = AcessoHelper.VERSAO_PROTOCOLO_CA;
}
LOG.warn("46 - versaoProtocolo: " + versaoProtocolo);

// Valida o certificado e salva na tb_consignante.
// Este pode ser um pacote de sincronismo de certificados ou resposta ao desafio.
try {
	LOG.warn("51 - certificado: " + certificado);
    if (!TextHelper.isNull(certificado)) {
        AcessoHelper.validaCertificadoCentralizador(certificado, false);
    }
    LOG.warn("55 - certificado centralizador validado: ");
} catch (Exception e) {
    out.clear();
    out.print(e.getMessage());
    LOG.warn("59 - certificado centralizador deu erro: " + e.getMessage());
    return;   
}

// Descriptografa com a chave privada do eConsig e a chave pública do centralizador se for pacote 1 dos protocolos sem CA
// ou se for o pacote de desafio quando o Centralizador possui suporte ao protocolo 2 mas ainda não possui um certificado
// de eConsig relativo a este sistema.
String pubKeyCentralizador = consignante.getCseRsaPublicKeyCentralizador();
String modulusCentralizador = consignante.getCseRsaModulusCentralizador();

Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
Key publicKeyCentralizador = RSA.generatePublicKey(modulusCentralizador, pubKeyCentralizador);

LOG.warn("72 - privateKeyEConsig: " + privateKeyEConsig + " - publicKeyCentralizador: " + publicKeyCentralizador);

// No primeiro pacote é usado as chaves fixas do código.
AcessoTransferObject acessoTO = null;
try {
  // O uso do protocolo evita uma tentativa de uso das chaves antigas ao decodificar o pacote de desafio
  // gerando exception no log
  if (versaoProtocolo != null && Integer.parseInt(versaoProtocolo) > 1) {
      // Neste ponto, o certificado usado é o armazenado na validação.
      certificado = consignante.getCseCertificadoCentralizador();
      X509Certificate X509CertCentralizador = AcessoHelper.convertToX509(certificado);
      publicKeyCentralizador = X509CertCentralizador.getPublicKey();
      privateKeyEConsig = acessoHelper.getPrivateKey(aliasEconsig, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
      // Na sincronização, apenas troco certificados, não tenho um comando para descriptografar
      if (comando.equals(AcessoHelper.COMANDO_SINCRONIZAR_CERTIFICADOS)) {
          acessoTO = new AcessoTransferObject();
          acessoTO.setComando(AcessoHelper.COMANDO_SINCRONIZAR_CERTIFICADOS);
      } else {
          //TODO: pegar alias, path keystore e senha do properties
          acessoTO = new AcessoHelper().decodificarAcessoTO(comando, publicKeyCentralizador, privateKeyEConsig);
      }
  } else {
      if (TextHelper.isNull(consignante.getCseCertificadoCentralizador())) {
          acessoTO = new AcessoHelper().decodificarAcessoTO(comando, publicKeyCentralizador, privateKeyEConsig);
      } else {
          out.clear();
          out.print("Certificado de CA do Centralizador já existe no eConsig mas Centralizador não esta usando o protocolo com CA.");
          return;   
      }
  }
} catch (Exception e) {
  out.clear();
  out.print(ApplicationResourcesHelper.getMessage("mensagem.falhaComunicacao", null));
  return;   
}

if (acessoTO == null) {    
    out.clear();
    out.print(ApplicationResourcesHelper.getMessage("mensagem.falhaComunicacao", null));
    return;
}

String resultado = AcessoHelper.RESPOSTA_VERSAO_INVALIDA;

try {
    if ((!TextHelper.isNull(acessoTO.getTimestampCentralizador()) && TextHelper.isNull(acessoTO.getTimestampEconsig())) || 
        comando.equals(AcessoHelper.COMANDO_SINCRONIZAR_CERTIFICADOS)) {
        
        // Quando é a primeira comunicação entre os sistemas com certificados de CA, o pacote de desafio chegou usando as chaves antigas
        // Aqui eu já respondo com as novas chaves.
        // Se for uma ressincronização, já esta com as novas chaves e isto não seria necessário.
        if (!TextHelper.isNull(certificado)) {
            X509Certificate X509CertCentralizador = AcessoHelper.convertToX509(certificado);
            publicKeyCentralizador = X509CertCentralizador.getPublicKey();
            privateKeyEConsig = acessoHelper.getPrivateKey(aliasEconsig, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
        }
        // cria o desafio ou resposta ao pacote de sincronismo de certificados.
        LOG.warn("129 - privateKeyEConsig: " + privateKeyEConsig + " - publicKeyCentralizador: " + publicKeyCentralizador);
        resultado = new AcessoHelper().codificarAcessoTO(acessoTO, privateKeyEConsig, publicKeyCentralizador);
    } else {
        String endereco = JspHelper.getRemoteAddr(request);
        AcessoHelper.processarComando(acessoTO, endereco);
        if (acessoTO != null) {
            // A resposta antes do suporte ao timestamp era texto puro, se existe acessoTO.getTimestampCentralizador()
            // centralizador espera/aceita resposta criptografada.
            if (TextHelper.isNull(acessoTO.getTimestampCentralizador())) {
                resultado = AcessoHelper.RESPOSTA_OK;
            } else {
                acessoTO.setResultado(TextHelper.encode64(AcessoHelper.RESPOSTA_OK));
                // Cria uma nova entrada no cache para uso em comandos posteriores dentro do mesmo fluxo, como inicializar a sessão do usuário.
                acessoTO.setTimestampEconsig(null);

                LOG.warn("144 - privateKeyEConsig: " + privateKeyEConsig + " - publicKeyCentralizador: " + publicKeyCentralizador);
                resultado = new AcessoHelper().codificarAcessoTO(acessoTO, privateKeyEConsig, publicKeyCentralizador);
            }
        }
    }
} catch (Exception ex) {
    if (TextHelper.isNull(acessoTO.getTimestampCentralizador())) {
        resultado = ex.getMessage();
    } else {
        acessoTO.setResultado(TextHelper.encode64(ex.getMessage()));
        LOG.warn("154 - privateKeyEConsig: " + privateKeyEConsig + " - publicKeyCentralizador: " + publicKeyCentralizador);
        resultado = new AcessoHelper().codificarAcessoTO(acessoTO, privateKeyEConsig, publicKeyCentralizador);
    }
}
if (acessoTO == null || !AcessoHelper.COMANDO_LOGIN.equals(acessoTO.getComando())) {
    out.clear();

    // Se o centralizado não suporta novo protocolo com CA, não enviou o certificado.
    if (!TextHelper.isNull(certificado)) {
        // TODO: pegar alias, path keystore e senha do properties
        X509Certificate X509cert = acessoHelper.getCertificate(aliasEconsig, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
        String eConsigCert = AcessoHelper.convertToPem(X509cert);
        resultado = resultado.concat("|" + eConsigCert);
    }

    LOG.warn(resultado);
    out.print(resultado);
    return;
}

KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
String senha = RSA.encrypt(acessoTO.getSenha(), keyPair.getPublic());

session.setAttribute(com.zetra.econsig.values.CodedNames.ATTR_SESSION_CENTRALIZADOR, Boolean.TRUE);
%>
<html>
  <head>
    <%@ include file="../geral/head.jsp"%>
    <script language="javascript" type="text/javascript">
      function pageLoad() {
        document.forms[0].submit();
      }
    </script>
  </head>
  <body onLoad="pageLoad();">
    <form name=enviar method="post" action="../v3/autenticarUsuario">
      <input name="username" type="hidden" value="<%=TextHelper.forHtmlAttribute(acessoTO.getLogin())%>">
      <input name="cryptedPasswordFieldName" type="hidden" value="senhaRSA">
      <input name="acao" type="hidden" value="autenticar">
      <input name="senhaRSA" type="hidden" value="<%=TextHelper.forHtmlAttribute(senha)%>">
      <input name="usuCentralizador" type="hidden" value="S">
      <input name="urlCentralizador" type="hidden" value="<%=urlCentral%>">
      <input name="attrSessionAcessoUrl" type="hidden" value="<%=attrSessionAcessoUrl%>">
      <input name="parametrosCentral" type="hidden" value="<%=parametrosCentral%>">
    </form>
  </body>
</html>