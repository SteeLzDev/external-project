package com.zetra.econsig.job.process.agendado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaValidaLicenca</p>
 * <p>Description: Revalida a licença do eConsig</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaValidaLicenca extends ProcessoAgendadoPeriodico {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaValidaLicenca.class);

    private static final String URL_VALIDACAO_LICENCA = "/web/validarLicenca.do";

    private int count;

    public ProcessaValidaLicenca(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        count = 0;
    }

    @Override
    protected void executa() {
        // Imprime mensagem de Debug
        Date now = new Date();
        LOG.debug("perform, now: " + now + ", count: " + count++);

        // Faz requisição para revalidação da licença
        String result = fazerRequisicao(now);
        String timestamp = null;
        String certificadoCentralizador = null;

        if (!TextHelper.isNull(result)) {
            try {
                Properties env = new Properties();
                env.load(new ByteArrayInputStream(result.getBytes()));
                timestamp = env.getProperty("licenca");
                certificadoCentralizador = env.getProperty("certificadoCentralizador");
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            // Busca os dados necessários da consignante
            ConsignanteTransferObject consignante = getDadosConsignante();
            if (TextHelper.isNull(consignante) ||
                    TextHelper.isNull(consignante.getCseRsaPublicKeyCentralizador()) ||
                    TextHelper.isNull(consignante.getCseRsaModulusCentralizador())) {
                LOG.error("NÃO FOI POSSÍVEL OBTER OS DADOS DO " + ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", getResponsavel()).toLowerCase() + " PARA VALIDAÇÃO DA LICENÇA.");
            }
            certificadoCentralizador = consignante.getCseCertificadoCentralizador();

            // A licença deve ser considerada expirada porque não foi possível conectar ao validador de licença
            timestamp = null;
        }

        atualizarLicenca(timestamp, certificadoCentralizador);
    }

    private String fazerRequisicao(Date now) {
        HttpPost post = null;
        HttpResponse response = null;
        InputStream inputStream = null;
        String result = null;

        try {
            ParamSist paramSist = ParamSist.getInstance();
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            // Busca a url do centralizador pelo parâmetro de sistema
            String urlCentralizador = (String) paramSist.getParam(CodedValues.TPC_URL_CENTRALIZADOR, responsavel);
            if (TextHelper.isNull(urlCentralizador)) {
                LOG.error("A URL DO CENTRALIZADOR DEVE SER CADASTRADA NO PARÂMETRO DE SISTEMA.");
                return null;
            }

            List<String> urls = Arrays.asList(urlCentralizador.split(";"));
            urlCentralizador = urls.get(0);

            // Busca os dados necessários da consignante
            ConsignanteTransferObject consignante = getDadosConsignante();
            if (TextHelper.isNull(consignante) ||
                    TextHelper.isNull(consignante.getCseRsaPublicKeyCentralizador()) ||
                    TextHelper.isNull(consignante.getCseRsaModulusCentralizador())) {
                LOG.error("NÃO FOI POSSÍVEL OBTER OS DADOS DO " + ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel).toLowerCase() + " PARA VALIDAÇÃO DA LICENÇA.");
                return null;
            }

            // Configura o HTTPClient para utilizar as classes do pacote com.zetra.net
            // na criação dos sockets para https.
            String keystorePath = (String) paramSist.getParam(CodedValues.TPC_KEYSTORE_FILE, responsavel);
            String keystorePass = (String) paramSist.getParam(CodedValues.TPC_KEYSTORE_PASSWORD, responsavel);

            // Busca valor da public key do banco de dados
            String idConsignante = consignante.getCseIdentificador();

            // Criptografa a mensagem a ser enviada
            String message = String.valueOf(now.getTime()) + "-" + idConsignante;

            // Criptografa com a chave privada do eConsig e a chave pública do centralizador
            String pubKeyCentralizador = consignante.getCseRsaPublicKeyCentralizador();
            String modulusCentralizador = consignante.getCseRsaModulusCentralizador();
            Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
            Key publicKeyCentralizador = RSA.generatePublicKey(modulusCentralizador, pubKeyCentralizador);

            message = RSA.encrypt(message, privateKeyEConsig);
            message = RSA.encrypt(message, publicKeyCentralizador);

            // Define os parâmetros do POST
            ArrayList<NameValuePair> data = new ArrayList<>(2);
            data.add(new BasicNameValuePair("licenca", message));
            data.add(new BasicNameValuePair("consignante", idConsignante));

            // Cria o cliente HTTP e o método POST para validação da senha
            HttpClient client = HttpHelper.getHttpClient(keystorePath, keystorePass);

            post = new HttpPost(urlCentralizador + URL_VALIDACAO_LICENCA);
            post.setEntity(new UrlEncodedFormEntity(data));

            // executa o POST
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                if (entity != null) {
                    // Input Stream para receber o resultado da requisição
                    inputStream = entity.getContent();
                    // Grava o resultado em um buffer
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int c = -1;
                    while ((c = inputStream.read()) != -1) {
                        out.write(c);
                    }
                    result = out.toString();
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (post != null) {
                    post.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return result;
    }

    private void atualizarLicenca(String novaLicenca, String certificadoCentralizador) {
        try {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            if (!TextHelper.isNull(novaLicenca)) {
                consignante.setCseLicenca(novaLicenca);
            } else {
                consignante.setCseLicenca(null);
            }
            consignante.setCseCertificadoCentralizador(certificadoCentralizador);

            cseDelegate.updateConsignante(consignante, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private ConsignanteTransferObject getDadosConsignante() {
        try {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            return consignante;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }
}