package com.zetra.econsig.helper.acesso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.net.ssl.SSLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.AcessoTransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
/**
 * <p>Title: AcessoHelper</p>
 * <p>Description: Helper Class para operações de login (via centralizador).</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcessoHelper {
    private static final String USUARIO_CSE = "ZETRASOFT";

    private static final int PERFIL_INATIVO = 0;

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AcessoHelper.class);

    // Parametro com o comando a ser executado
    public static final String PARAMETRO_COMANDO = "comando";

    // Parametro com o certificado publico do sender do pacote
    public static final String PARAMETRO_CERTIFICADO = "certificado";

    // Parametro com a versão do protocolo
    public static final String PARAMETRO_VERSAO_PROTOCOLO = "protocolo";

    // Versões de protocolo
    public static final String VERSAO_PROTOCOLO_ORIGINAL = "0";
    public static final String VERSAO_PROTOCOLO_ANTI_REPLAY = "1";
    public static final String VERSAO_PROTOCOLO_CA = "2";

    // Possíveis comandos a serem enviados ao eConsig.
    public static final String COMANDO_LOGIN         = "login";
    public static final String COMANDO_ALTERAR_SENHA = "alterar senha";
    public static final String COMANDO_VALIDAR_LOGIN = "validar login";
    public static final String COMANDO_SINCRONIZAR_CERTIFICADOS = "sincronizar certificados";

    // Possíveis resultados a serem enviados como resposta.
    public static final String RESPOSTA_OK                       = "OK";
    public static final String RESPOSTA_USUARIO_NAO_ENCONTRADO   = "USUARIO_NAO_CSA";
    public static final String RESPOSTA_USUARIO_NAO_PERTENCE_CSA = "USUARIO_NAO_PERTENCE_CSA";
    public static final String RESPOSTA_USUARIO_BLOQUEADO        = "USUARIO_BLOQUEADO";
    public static final String RESPOSTA_USUARIO_EXCLUIDO         = "USUARIO_EXCLUIDO";
    public static final String RESPOSTA_SENHA_EXPIRADA           = "SENHA_EXPIRADA";
    public static final String RESPOSTA_SENHA_INCORRETA          = "SENHA_INCORRETA";
    public static final String RESPOSTA_ORIGEM_ACESSO_INVALIDA   = "ORIGEM_ACESSO_INVALIDA";
    public static final String RESPOSTA_VERSAO_INVALIDA          = "VERSAO_INVALIDA";
    public static final String RESPOSTA_CPF_OBRIGATORIO          = "CPF_OBRIGATORIO";
    public static final String RESPOSTA_EMAIL_OBRIGATORIO        = "EMAIL_OBRIGATORIO";

    // Determina o método de comunicação
    private boolean useReflection;

    public AcessoHelper() {
        useReflection = false;
    }

    public boolean getUseReflection() {
        return useReflection;
    }
    public AcessoHelper setUseReflection(boolean useReflection) {
        this.useReflection = useReflection;
        return this;
    }

    // ***************************** ROTINAS DE COMUNICAÇÃO *****************************

    /**
     * Criptografa o objeto do tipo {@link AcessoTransferObject}. Faz criptografia dupla:
     * 1. Primeiro criptografa com a chave privada do remetente. Assim garantindo que a
     * criptografia somente por ter sido feita pelo remetente;
     * 2. Depois criptografa com a chave pública do destinatário. Assim garantindo que somente
     * o destinatário poderá descriptografar as informações.
     *
     * @param acessoTO    objeto do tipo {@link AcessoTransferObject} a ser criptografado.
     * @param privateKey  objeto do tipo {@link Key} a usar usado como chave privada do remetente.
     * @param publicKey   objeto do tipo {@link Key} a usar usado como chave pública do destinatário.
     * @return  um string com o objeto do tipo {@link AcessoTransferObject} criptografado.
     */
    public String codificarAcessoTO(AcessoTransferObject acessoTO, Key privateKey, Key publicKey) {
        // Usado na autenticação dos pacotes para evitar replay
        if (TextHelper.isNull(acessoTO.getTimestampCentralizador())) {
            acessoTO.setTimestampCentralizador(String.valueOf(System.currentTimeMillis()));
            acessoTO.setTimestampEconsig(null);
        } else if (TextHelper.isNull(acessoTO.getTimestampEconsig())) {
            // Cria uma entrada de timestamp do cache de comandos e retorna uma chave (desafio para o centralizador)
            acessoTO.setTimestampEconsig(String.valueOf(CacheComandos.INSTANCE.setComando(System.currentTimeMillis())));
        }

        if (!useReflection) {
            String resultado = null;
            final String xml = new Exporter(acessoTO).setIndented(false).toXml();
            if (!TextHelper.isNull(xml)) {
                resultado = RSA.encrypt(RSA.encrypt(RSA.encodeBASE64(xml.getBytes()), privateKey), publicKey);
            }
            return resultado;
        } else {
            return codificarAcessoTOReflection(acessoTO, privateKey, publicKey);
        }
    }

    /**
     * Descriptografa o objeto do tipo {@link AcessoTransferObject}. Desfaz a criptografia dupla:
     * 1. Primeiro descriptografa com a chave privada do destinatário.
     * 2. Depois descriptografa com a chave pública do remetente.
     * Desta forma garante-se que a informação foi enviada pelo remetente somente para o destinatário.
     *
     * @param acesso      um string com o objeto do tipo {@link AcessoTransferObject} criptografado.
     * @param publicKey   objeto do tipo {@link Key} a usar usado como chave pública do remetente.
     * @param privateKey  objeto do tipo {@link Key} a usar usado como chave privada do destinatário.
     * @return  objeto do tipo {@link AcessoTransferObject} descriptografado.
     */
    public AcessoTransferObject decodificarAcessoTO(String acesso, Key publicKey, Key privateKey) {
        if (!useReflection) {
            final ObjectInputStream in = null;
            AcessoTransferObject acessoTO = null;
            try {
                byte[] buf = null;
                buf = RSA.decodeBASE64(RSA.decrypt(RSA.decrypt(acesso, privateKey), publicKey));
                acessoTO = new Parser().toTransferObject(buf);
            } catch (final BadPaddingException e) {
                // logado na console pelo RSA.decodeBASE64
            } catch (IOException | ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
            return acessoTO;
        } else {
            return decodificarAcessoTOReflection(acesso, publicKey, privateKey);
        }
    }
    /**
     * Criptografa o objeto do tipo {@link AcessoTransferObject}. Faz criptografia dupla:
     * 1. Primeiro criptografa com a chave privada do remetente. Assim garantindo que a
     * criptografia somente por ter sido feita pelo remetente;
     * 2. Depois criptografa com a chave pública do destinatário. Assim garantindo que somente
     * o destinatário poderá descriptografar as informações.
     *
     * @param acessoTO    objeto do tipo {@link AcessoTransferObject} a ser criptografado.
     * @param privateKey  objeto do tipo {@link Key} a usar usado como chave privada do remetente.
     * @param publicKey   objeto do tipo {@link Key} a usar usado como chave pública do destinatário.
     * @return  um string com o objeto do tipo {@link AcessoTransferObject} criptografado.
     */
    private String codificarAcessoTOReflection(AcessoTransferObject acessoTO, Key privateKey, Key publicKey) {
        // Serialize to a byte array
        ByteArrayOutputStream bos = null;
        String resultado = null;
        try {
            bos = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(bos) ;
            out.writeObject(acessoTO);
            out.close();
            // Get the bytes of the serialized object
            final byte[] buf = bos.toByteArray();
            bos.close();

            resultado = RSA.encrypt(RSA.encrypt(RSA.encodeBASE64(buf), privateKey), publicKey);
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return resultado;
    }

    /**
     * Descriptografa o objeto do tipo {@link AcessoTransferObject}. Desfaz a criptografia dupla:
     * 1. Primeiro descriptografa com a chave privada do destinatário.
     * 2. Depois descriptografa com a chave pública do remetente.
     * Desta forma garante-se que a informação foi enviada pelo remetente somente para o destinatário.
     *
     * @param acesso      um string com o objeto do tipo {@link AcessoTransferObject} criptografado.
     * @param publicKey   objeto do tipo {@link Key} a usar usado como chave pública do remetente.
     * @param privateKey  objeto do tipo {@link Key} a usar usado como chave privada do destinatário.
     * @return  objeto do tipo {@link AcessoTransferObject} descriptografado.
     */
    private AcessoTransferObject decodificarAcessoTOReflection(String acesso, Key publicKey, Key privateKey) {
        ObjectInputStream in = null;
        AcessoTransferObject acessoTO = null;
        try {
            byte[] buf;
            try {
                buf = RSA.decodeBASE64(RSA.decrypt(RSA.decrypt(acesso, privateKey), publicKey));
            } catch (final BadPaddingException ex) {
                // Tentativa de decriptografia com chave inválida.
                LOG.error(ex.getMessage(), ex);
                buf = null;
            }
            in = new ObjectInputStream(new ByteArrayInputStream(buf));
            acessoTO = (AcessoTransferObject) in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return acessoTO;
    }

    /**
     * Envia o comando via {@link HttpClient} do centralizador para o sistema eConsig.
     *
     * @param url           URL do eConsig responsável por receber e processar o comando enviado.
     * @param comando       Objeto do tipo {@link AcessoTransferObject} criptografado.
     * @param keystorePath  Keystore com o certificado do sistema eConsig para acesso via HTTPS.
     * @param keystorePass  Senha de acesso ao keystore informado.
     * @return  a resposta obtida do sistema eConsig.
     * @throws ZetraException   se houver algum erro no envio do comando.
     */
    public String enviarComando(String url, String comando, String keystorePath, String keystorePass) throws ZetraException {
        return enviarComando(url, comando, keystorePath, keystorePass, null, false);
    }

    /**
     * Envia o comando via {@link HttpClient} do centralizador para o sistema eConsig.
     *
     * @param url           URL do eConsig responsável por receber e processar o comando enviado.
     * @param comando       Objeto do tipo {@link AcessoTransferObject} criptografado.
     * @param keystorePath  Keystore com o certificado do sistema eConsig para acesso via HTTPS.
     * @param keystorePass  Senha de acesso ao keystore informado.
     * @param X509certCentralizador Certificado do centralizador usado na criptografia do comando.
     * @param certificadoCA Indica o uso de certificados de CA
     * @return  a resposta obtida do sistema eConsig.
     * @throws ZetraException   se houver algum erro no envio do comando.
     */
    public String enviarComando(String url, String comando, String keystorePath, String keystorePass, X509Certificate X509certCentralizador, boolean certificadoCA) throws ZetraException {
        HttpPost post = null;
        InputStream inputStream = null;
        HttpResponse response = null;

        try {
            final HttpClient client = HttpHelper.getHttpClient(keystorePath, keystorePass);
            post = new HttpPost(url);

            // Define os parâmetros do POST
            final ArrayList<NameValuePair> data = new ArrayList<>(1);
            data.add(new BasicNameValuePair(PARAMETRO_COMANDO, comando));
            if (X509certCentralizador != null) {
                // Certificado usado no protocolo novo
                final String certificado = convertToPem(X509certCentralizador);
                data.add(new BasicNameValuePair(PARAMETRO_CERTIFICADO, certificado));
                // Usado para evitar que o eConsig realize uma tentativa de descodificar usando chaves antigas
                if(certificadoCA) {
                    data.add(new BasicNameValuePair(PARAMETRO_VERSAO_PROTOCOLO, VERSAO_PROTOCOLO_CA));
                }
            }
            post.setEntity(new UrlEncodedFormEntity(data));

            int statusCode = 0;
            Exception cause = null;
            try {
                // Executa o post
                response = client.execute(post);
                statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    String message = null;
                    if (statusCode == HttpStatus.SC_NOT_FOUND) {
                        message = "PAGE NOT FOUND: " + url;
                    } else  if (response.getStatusLine() != null) {
                        message = response.getStatusLine().getReasonPhrase();
                    }
                    if (message == null) {
                        message = "STATUS DO RETORNO DA REQUISIÇÃO: " + statusCode;
                    }
                    LOG.error("Erro ocorreu acessando URL: " + url);
                    throw ZetraException.byMessage("904", new Exception(message));
                }
            } catch (UnknownHostException | NoRouteToHostException | ConnectException | SSLException ex) {
                cause = ex;
            }
            if (cause != null) {
                LOG.error("Erro ocorreu acessando URL: " + url);
                throw ZetraException.byMessage("903", cause);
            }

            final HttpEntity entity = response.getEntity();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            if (entity != null) {
                // Input Stream para receber o resultado da requisição
                inputStream = entity.getContent();
                // Grava o resultado em um buffer
                int c = -1;
                while ((c = inputStream.read()) != -1) {
                    out.write(c);
                }
            }
            return out.toString();

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;

        } catch (final Exception ex) {
        	LOG.error(ex.getMessage(), ex);
            throw ZetraException.byMessage("999", ex);

        } finally {
            try {
                if (post != null) {
                    post.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    // ***************************** COMANDOS *****************************
    /**
     * Verifica se o comando solicitado é válido e processa o mesmo.
     *
     * @param acessoTO  objeto do tipo {@link AcessoTransferObject} a ser processado.
     * @throws ZetraException  se houver algum erro no processamento do comando.
     */
    public static void processarComando(AcessoTransferObject acessoTO, String endereco) throws ZetraException {
        final AcessoSistema responsavel = findResponsavel(acessoTO);
        // !TextHelper.isNull(acessoTO.getTimestampCentralizador()) permite suporte a Centralizador antigo, o que permite fazer a atualização do
        // eConsig antes do centralizador
        if (!TextHelper.isNull(acessoTO.getTimestampCentralizador()) && !CacheComandos.INSTANCE.hasComando(Long.valueOf(acessoTO.getTimestampEconsig()))) {
            throw new ZetraException("mensagem.validacao.comando", responsavel);
        }

        final Object paramUrlCentralizador = ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR, responsavel);
        final String urlCentralizador = (paramUrlCentralizador != null) ? (String) paramUrlCentralizador : null;
        final List<String> urls = Arrays.asList(urlCentralizador.split(";"));
        if ((urlCentralizador != null) && !"".equals(urlCentralizador)) {
            if (!JspHelper.validaUrl(endereco, urls)) {
                throw new ZetraException("mensagem.enderecoAcessoCentralizadorInvalido", responsavel);
            }
        } else {
            throw new ZetraException("mensagem.urlAcessoNaoCadastradaCentralizador", responsavel);
        }

        if (acessoTO != null) {
            if (COMANDO_VALIDAR_LOGIN.equals(acessoTO.getComando()) || COMANDO_LOGIN.equals(acessoTO.getComando())) {
                validarLogin(acessoTO);
            } else if (COMANDO_ALTERAR_SENHA.equals(acessoTO.getComando())) {
                alterarSenha(acessoTO);
            }
        }
    }

    /**
     * Valida o login e a senha informados no objeto do tipo {@link AcessoTransferObject}.
     *
     * @param acessoTO  objeto do tipo {@link AcessoTransferObject} a ser validado.
     * @throws ZetraException  se houver algum erro na validação do login.
     */
    private static void validarLogin(AcessoTransferObject acessoTO) throws ZetraException {
        try {
            final UsuarioDelegate delegate = new UsuarioDelegate();
            final CustomTransferObject usuario = delegate.findTipoUsuario(acessoTO.getLogin(), AcessoSistema.getAcessoUsuarioSistema());
            if (usuario == null) {
                throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_ENCONTRADO);
            } else {
                // Busca os dados do usuário responsável
                final AcessoSistema responsavel = findResponsavel(acessoTO);

                // Verifica a licença do eConsig
                verificaLicencaSistema(usuario);

                // Verifica se o sistema não está bloqueado
                verificaSistemaBloqueado(delegate, usuario);

                // Verifica se o usuário está bloqueado ou excluído
                verificaStatusUsuario(responsavel, delegate, usuario);

                // Verifica se o usuário é da consignatária.
                usuarioPertenceConsignataria(acessoTO, usuario);

                // Verifica a senha do usuário
                verificaSenhaUsuario(acessoTO, usuario);

                try {
                    // Confere se endereço de origem da requisição é permitido ao acesso do usuário corrente
                    final String usu_ip_acesso = (usuario.getAttribute(Columns.USU_IP_ACESSO) != null ? usuario.getAttribute(Columns.USU_IP_ACESSO).toString() : "");
                    final String usu_ddns_acesso = (usuario.getAttribute(Columns.USU_DDNS_ACESSO) != null ? usuario.getAttribute(Columns.USU_DDNS_ACESSO).toString() : "");
                    UsuarioHelper.verificarIpDDNSAcesso(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), acessoTO.getURIAcesso(), usu_ip_acesso, usu_ddns_acesso, usuario.getAttribute(Columns.USU_CODIGO).toString(), responsavel);
                } catch (final ZetraException ze) {
                	LOG.error(ze.getMessage(), ze);
                    throw ZetraException.byMessage(RESPOSTA_ORIGEM_ACESSO_INVALIDA + " " + ze.getMessage(), ze);
                }

                try {
                    // Confere a obrigatoriedade de cpf
                    final String usuCpf = (usuario.getAttribute(Columns.USU_CPF) != null ? usuario.getAttribute(Columns.USU_CPF).toString() : "");
                    UsuarioHelper.verificarCpfUsuario(responsavel.getTipoEntidade(), usuCpf, responsavel);
                } catch (final ZetraException ze) {
                	LOG.error(ze.getMessage(), ze);
                    throw ZetraException.byMessage(RESPOSTA_CPF_OBRIGATORIO + " " + ze.getMessage(), ze);
                }

                try {
                    // Confere a obrigatoriedade de email
                    final String usuEmail = (usuario.getAttribute(Columns.USU_EMAIL) != null ? usuario.getAttribute(Columns.USU_EMAIL).toString() : "");
                    UsuarioHelper.verificarEmailUsuario(responsavel.getTipoEntidade(), usuEmail, responsavel);
                } catch (final ZetraException ze) {
                	LOG.error(ze.getMessage(), ze);
                    throw ZetraException.byMessage(RESPOSTA_EMAIL_OBRIGATORIO + " " + ze.getMessage(), ze);
                }
            }
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    /**
     * Altera a senha do usuário informado no objeto do tipo {@link AcessoTransferObject}.
     *
     * @param acessoTO  objeto do tipo {@link AcessoTransferObject} a ser alterado.
     * @throws ZetraException  se houver algum erro na alteração da senha.
     */
    private static void alterarSenha(AcessoTransferObject acessoTO) throws ZetraException {
        try {
            final UsuarioDelegate delegate = new UsuarioDelegate();
            final CustomTransferObject usuario = delegate.findTipoUsuario(acessoTO.getLogin(), AcessoSistema.getAcessoUsuarioSistema());

            if (usuario == null) {
                throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_ENCONTRADO);
            } else {
                // Verifica se o usuário é da consignatária.
                usuarioPertenceConsignataria(acessoTO, usuario);

                // Busca o usuário responsavel
                final AcessoSistema responsavel = findResponsavel(acessoTO);

                verificaSistemaBloqueado(delegate, usuario);
                verificaLicencaSistema(usuario);
                verificaStatusUsuario(responsavel, delegate, usuario);

                UsuarioHelper.verificarIpDDNSAcesso(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), acessoTO.getURIAcesso(),
                                                    (String) usuario.getAttribute(Columns.USU_IP_ACESSO),
                                                    (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO),
                                                    (String) usuario.getAttribute(Columns.USU_CODIGO),
                                                    responsavel);

                delegate.alterarSenha((String) usuario.getAttribute(Columns.USU_CODIGO), acessoTO.getSenha(), "Alterada via centralizador de login.", false, false, false, null, responsavel);
            }
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    /**
     * Verifica o status do usuário requisitando alterção da senha. Este não pode estar bloqueado, excluído ou pertencente a perfil inválido
     * @param responsavel
     * @param delegate
     * @param usuario
     * @throws ZetraException
     */
    private static void verificaStatusUsuario(AcessoSistema responsavel, UsuarioDelegate delegate, CustomTransferObject usuario) throws ZetraException {
        final String usuStatus = (String) usuario.getAttribute(Columns.USU_STU_CODIGO);

        if (CodedValues.STU_CODIGOS_INATIVOS.contains(usuStatus)) {
            throw ZetraException.byMessage(RESPOSTA_USUARIO_BLOQUEADO);
        } else if (CodedValues.STU_EXCLUIDO.equals(usuStatus)) {
            throw ZetraException.byMessage(RESPOSTA_USUARIO_EXCLUIDO);
        } else if (!CodedValues.STU_ATIVO.equals(usuStatus)) {
            throw ZetraException.byMessage(RESPOSTA_USUARIO_BLOQUEADO);
        } else {
            String perCodigo = (String) usuario.getAttribute(Columns.UPE_PER_CODIGO);
            if (TextHelper.isNull(perCodigo)) {
                final UsuarioDelegate usuDelegate = new UsuarioDelegate();
                perCodigo = usuDelegate.findUsuarioPerfil((String) usuario.getAttribute(Columns.USU_CODIGO), AcessoSistema.getAcessoUsuarioSistema());
            }

            try {
                if (delegate.getPerfilStatus(perCodigo, responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), AcessoSistema.getAcessoUsuarioSistema()) == PERFIL_INATIVO) {
                    throw ZetraException.byMessage(RESPOSTA_USUARIO_BLOQUEADO);
                }
            } catch (final FindException fex) {
                if ((usuario.getAttribute(Columns.USU_CENTRALIZADOR) != null) && "S".equals(usuario.getAttribute(Columns.USU_CENTRALIZADOR).toString())) {
                    throw ZetraException.byMessage("Perfil não encontrado.");
                }
            } catch (final UsuarioControllerException uex) {
            	LOG.error(uex.getMessage(), uex);
                throw ZetraException.byMessage("Erro ao recuperar perfil do usuário.", uex);
            }
        }
    }

    /**
     * Verifica se a senha do usuário, informada no AcessoTO está correta de acordo com os dados
     * retornados na pesquisa de usuário
     * @param acessoTO
     * @param usuario
     * @throws ZetraException
     */
    private static void verificaSenhaUsuario(AcessoTransferObject acessoTO, CustomTransferObject usuario) throws ZetraException {
        // O usuário existe, criptografa a senha passada e compara com a da base de dados
        final String senha = usuario.getAttribute(Columns.USU_SENHA).toString();
        final String expirou = usuario.getAttribute("EXPIROU") != null ? usuario.getAttribute("EXPIROU").toString() : "1";

        if (!JCrypt.verificaSenha(acessoTO.getSenha(), senha)) {
            try {
                JspHelper.alcancouNumMaxTentativasLogin(usuario, AcessoSistema.getAcessoUsuarioSistema());
            } catch (final ZetraException ze) {
            	LOG.error(ze.getMessage(), ze);
                throw ZetraException.byMessage(RESPOSTA_SENHA_INCORRETA + ze.getMessage(), ze);
            }

            throw ZetraException.byMessage(RESPOSTA_SENHA_INCORRETA);
        } else if ("1".equals(expirou)) {
            throw ZetraException.byMessage(RESPOSTA_SENHA_EXPIRADA);
        }
    }

    /**
     * Verifica se o sistema está bloqueado, e caso esteja, se o usuário tem permissão de
     * acessar o sistema bloqueado.
     * @param delegate
     * @param usuario
     * @throws ZetraException
     */
    private static void verificaSistemaBloqueado(UsuarioDelegate delegate, CustomTransferObject usuario) throws ZetraException {
        // Verifica se o sistema não está bloqueado
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        final Short status = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
        final boolean indisponivel = status.equals(CodedValues.STS_INDISP);
        if (indisponivel &&
            !delegate.usuarioTemPermissao((String) usuario.getAttribute(Columns.USU_CODIGO), CodedValues.FUN_EFETUAR_LOGIN_SISTEMA_BLOQUEADO, null, AcessoSistema.getAcessoUsuarioSistema())) {
            throw ZetraException.byMessage(LoginHelper.getMensagemSistemaIndisponivel());
        }
    }

    /**
     * Verifica se a licença do sistema está expirada.
     * @param usuario
     * @throws ZetraException
     */
    private static void verificaLicencaSistema(CustomTransferObject usuario) throws ZetraException {
        // Verifica a licença do eConsig
        final String licenca = (String) usuario.getAttribute(Columns.CSE_LICENCA);
        final String publicKeyCentralizador = (String) usuario.getAttribute(Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR);
        final String modulusCentralizador = (String) usuario.getAttribute(Columns.CSE_RSA_MODULUS_CENTRALIZADOR);
        if (UsuarioHelper.isLicencaExpirada(licenca, publicKeyCentralizador, modulusCentralizador)) {
            // Dá mensagem de erro genérica, com código de erro.
            throw ZetraException.byMessage("Erro interno do sistema (Cod.999)");
        }
    }

    /**
     * Verifica se o usuário pertence à consignatária
     * @param acessoTO
     * @param usuario
     * @throws ZetraException
     */
    private static void usuarioPertenceConsignataria(AcessoTransferObject acessoTO, CustomTransferObject usuario) throws ZetraException {
        if (USUARIO_CSE.equals(acessoTO.getConsignataria())) {
            if ((usuario.getAttribute(Columns.USP_CSE_CODIGO) == null) && (usuario.getAttribute(Columns.UCE_CSE_CODIGO) == null) && (usuario.getAttribute(Columns.UOR_ORG_CODIGO) == null)) {
                throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_PERTENCE_CSA);
            }
        } else {
            String csaCodigo = (String) usuario.getAttribute(Columns.UCA_CSA_CODIGO);

            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            if (usuario.getAttribute(Columns.UCO_COR_CODIGO) != null) {
                final CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(usuario.getAttribute(Columns.UCO_COR_CODIGO).toString(), AcessoSistema.getAcessoUsuarioSistema());
                csaCodigo = cor.getCsaCodigo();
            }
            final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(csaCodigo, AcessoSistema.getAcessoUsuarioSistema());
            if (!acessoTO.getConsignataria().equals(csa.getCsaIdentificadorInterno())) {
                throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_PERTENCE_CSA);
            }
        }
    }

    /**
     * Obtém o objeto responsável de acordo com os dados do usuário
     * @param acessoTO
     * @return
     * @throws ZetraException
     */
    private static AcessoSistema findResponsavel(AcessoTransferObject acessoTO) throws ZetraException {
        AcessoSistema responsavel = null;
        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(acessoTO.getLogin(), acessoTO.getURIAcesso(), null);
        } catch (final ZetraException e) {
            LOG.warn("Login: " + acessoTO.getLogin() + " - " + e.getMessage());
            throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_ENCONTRADO);
        }
        if (responsavel == null) {
            throw ZetraException.byMessage(RESPOSTA_USUARIO_NAO_ENCONTRADO);
        }
        return responsavel;
    }

    public static String convertToPem(X509Certificate cert) throws CertificateEncodingException {
        final Base64 encoder = new Base64(64);
        final byte[] derCert = cert.getEncoded();
        final String pemCert = new String(encoder.encode(derCert));
        return pemCert;
    }

    public static X509Certificate convertToX509(String cert) throws CertificateException {
        //TODO: fazer a validação
        //Validação: retorna null se valida=true e certificado não é válido
        //Certificado não é válido se não dor da CA da Zetra e tiver data anterior ao do último armazenado
        final byte[] certBytes = ("-----BEGIN CERTIFICATE-----\r\n" + cert + "\r\n-----END CERTIFICATE-----").getBytes();
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        final InputStream in = new ByteArrayInputStream(certBytes);
        final X509Certificate X509cert = (X509Certificate) certFactory.generateCertificate(in);
        return X509cert;
    }

    public static X509Certificate getCertificate(String alias, String keystore, String storepass) throws CertificateException {
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
            final Resource keystoreResource = new ClassPathResource(keystore);
            final InputStream is = keystoreResource.getInputStream();
            ks.load(is, storepass.toCharArray());
            return (X509Certificate) ks.getCertificate(alias);
        } catch (final Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new CertificateException("Falha ao recuperar certificado.", e);
        }
    }

    public static Key getPrivateKey(String alias, String keystore, String storepass) throws CertificateException {
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
            final Resource keystoreResource = new ClassPathResource(keystore);
            final InputStream is = keystoreResource.getInputStream();
            ks.load(is, storepass.toCharArray());
            return ks.getKey(alias, storepass.toCharArray());
        } catch (final Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new CertificateException("Falha ao recuperar chave privada.", e);
        }
    }

    public static void validaCertificadoCentralizador(String certificado, boolean mobile) throws ConsignanteControllerException, CertificateException {
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        final ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
        final String certificadoAtual = mobile ? consignante.getCseCertificadoCentralMobile() : consignante.getCseCertificadoCentralizador();

        final X509Certificate certificadoCA = getCertificate(CodedValues.PROTOCOLO_KEYSTORE_ALIAS_CA_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
        final X509Certificate certificadoX509 = convertToX509(certificado);

        try {
            certificadoX509.verify(certificadoCA.getPublicKey());
        } catch (final Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new CertificateException("Falha ao validar certificado com a CA da Zetra.", e);
        }

        if (certificadoAtual != null) {
            final X509Certificate certificadoAtualX509 = convertToX509(certificadoAtual);
            if (certificadoX509.getNotBefore().compareTo(certificadoAtualX509.getNotBefore()) < 0) {
                throw new CertificateException("Certificado mais antigo que o atualmente armazenado no eConsig.");
            } else if (certificadoX509.getNotBefore().compareTo(certificadoAtualX509.getNotBefore()) > 0) {
                // Atualiza o certificado no banco se o enviado for mais novo
                atualizarCertificadoCentralizador(consignante, certificado, mobile);
            }
        } else {
            // Inclui um certificado no sistema caso ainda não exista
            atualizarCertificadoCentralizador(consignante, certificado, mobile);
        }
    }

    private static void atualizarCertificadoCentralizador(ConsignanteTransferObject consignante, String certificado, boolean mobile) throws ConsignanteControllerException {
        if (mobile) {
            consignante.setCseCertificadoCentralMobile(certificado);
        } else {
            consignante.setCseCertificadoCentralizador(certificado);
        }
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        cseDelegate.updateConsignante(consignante, AcessoSistema.getAcessoUsuarioSistema());
    }
}