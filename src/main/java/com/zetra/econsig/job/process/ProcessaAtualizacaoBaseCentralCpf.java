package com.zetra.econsig.job.process;

import java.io.IOException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.acesso.AcessoHelper;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: ProcessaAtualizacaoBaseCentralCpf</p>
 * <p> Description: Classe para processamento da rotina de atualização de CPFs na base do Centralizador</p>
 * <p> Copyright: Copyright (c) 2002-2017</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaAtualizacaoBaseCentralCpf extends ProcessoAgendadoEventual {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaAtualizacaoBaseCentralCpf.class);

    public ProcessaAtualizacaoBaseCentralCpf(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        final String mensagemErro = atualizarBaseCentralCpf(getResponsavel());

        // Se tem mensagem de erro, define resultado como erro e seta mensagem para gravação na ocorrência
        if (!TextHelper.isNull(mensagemErro)) {
            codigoRetorno = ERRO;
            mensagem = mensagemErro;
        }
    }

    private static String atualizarBaseCentralCpf(AcessoSistema responsavel) {
        try {
            // Recupera a lista de CPFs ou e-mails do sistema
            final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
            final ServidorDelegate serDelegate = new ServidorDelegate();
            List<String> listaId = new ArrayList<>();

            if(omiteCpf) {
                listaId = serDelegate.listarEmailServidoresAtivos(responsavel);
            } else {
                listaId = serDelegate.listarCpfServidoresAtivos(responsavel);
            }

            String conteudo = TextHelper.join(listaId, System.lineSeparator());
            LOG.debug("Quantidade de "+ (omiteCpf ? "E-mails" : "CPFs" )+" a serem enviados para Centralizador: " + listaId.size());

            // Calcula o md5 do conteúdo original
            final String md5 = TextHelper.md5(conteudo);

            // Criptografa o md5 gerado com chaves privada do eConsig e pública do Centralizador
            final String aliasEconsig = CodedValues.PROTOCOLO_KEYSTORE_ALIAS_ECONSIG_PROPERTY;

            // Busca os dados do consignante
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            // Obtém o certificado do Centralizador
            String certificado = null;
            final Response respostaCertificado = enviarRequisicao("/rest/acesso/certificado", null, responsavel);
            if (respostaCertificado.getStatus() == Response.Status.OK.getStatusCode()) {
                final Map<String, Object> map = (Map<String, Object>) new JSONParser().parse(respostaCertificado.readEntity(String.class));
                certificado = (String) map.get("mensagem");
                // Valida o certificado, e atualiza o banco de dados caso seja mais recente
                AcessoHelper.validaCertificadoCentralizador(certificado, true);
            } else {
                // Usa o certificado cadastrado na base de dados
                certificado = consignante.getCseCertificadoCentralMobile();
            }

            // Converte o certificado do Centralizador e obtém a chave pública
            final X509Certificate X509CertCentralizador = AcessoHelper.convertToX509(certificado);
            final Key publicKeyCentralizador = X509CertCentralizador.getPublicKey();

            // Obtém a chave privada do eConsig e criptografa a mensagem
            final Key privateKeyEConsig = AcessoHelper.getPrivateKey(aliasEconsig, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
            final String md5Crypt = Base64.encodeBase64String(RSA.encrypt(RSA.encrypt(md5, privateKeyEConsig), publicKeyCentralizador).getBytes());

            final X509Certificate X509CertEConsig = AcessoHelper.getCertificate(aliasEconsig, CodedValues.PROTOCOLO_KEYSTORE_PATH_PROPERTY, CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY);
            final String certificadoEConsig = Base64.encodeBase64String(X509CertEConsig.getEncoded());

            // Compacta o conteúdo do arquivo
            final byte[] conteudoZip = FileHelper.gzipString(conteudo);
            // Aplica Base64 para envio via REST
            conteudo = Base64.encodeBase64String(conteudoZip);

            // Cria conjunto de parâmetros para chamada REST
            final Map<String, Object> data = new HashMap<>();
            data.put("id", consignante.getIdentificadorInterno());
            data.put("arquivo", conteudo);
            data.put("certificado", certificadoEConsig);
            data.put("chave", md5Crypt);
            data.put("cseNome", TextHelper.removeAccentCharsetArbitrario(consignante.getCseNome()));

            // Realiza a chamada REST
            final Response resposta = enviarRequisicao("/rest/servidor/importar", data, responsavel);

            // Se o resultado foi diferente de sucesso retorna mensagem de erro
            String mensagemErro = null;
            if (resposta.getStatus() != Response.Status.OK.getStatusCode()) {
                final Map<String, Object> map = (Map<String, Object>) new JSONParser().parse(resposta.readEntity(String.class));
                mensagemErro = (String) map.get("mensagem");
            }

            resposta.close();
            return mensagemErro;

        } catch (CertificateException | KeyManagementException | NoSuchAlgorithmException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return ApplicationResourcesHelper.getMessage("mensagem.erro.processo.atualizacao.base.cpf.preparacao", responsavel);
        } catch (final ProcessingException ex) {
            LOG.error(ex.getMessage(), ex);
            return ApplicationResourcesHelper.getMessage("mensagem.erro.processo.atualizacao.base.cpf.execucao", responsavel);
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return ApplicationResourcesHelper.getMessage("mensagem.erro.processo.atualizacao.base.cpf.finalizacao", responsavel);
        } catch (ServidorControllerException | ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    private static Response enviarRequisicao(String urlRest, Map<String, Object> parametros, AcessoSistema responsavel) throws NoSuchAlgorithmException, KeyManagementException {
        final String urlCentralizador = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR_MOBILE, responsavel);
        final String url = urlCentralizador + urlRest;

        final SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0];
                }
        }}, new java.security.SecureRandom());

        final HostnameVerifier allowAll = (hostname, session) -> true;

        final Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier(allowAll).build();
        final WebTarget webResource = client.target(url);

        Entity<Map<String, Object>> json = null;
        if (parametros != null) {
            json = Entity.json(parametros);
        }

        // Realiza a chamada REST
        return webResource.request(MediaType.APPLICATION_JSON).accept("application/json").post(json);
    }
}
