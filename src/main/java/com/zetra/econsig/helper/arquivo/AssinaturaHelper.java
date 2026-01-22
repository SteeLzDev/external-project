package com.zetra.econsig.helper.arquivo;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import eu.medsea.mimeutil.MimeType;
import jakarta.json.Json;
import jakarta.json.JsonReader;

/**
 * <p>Title: AssinaturaHelper</p>
 * <p>Description: Helper Class para Operação de assinatura digital de documentos</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AssinaturaHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AssinaturaHelper.class);

    private final String accessToken;
    private final String endPoint;

    private static final AssinaturaHelper instance = new AssinaturaHelper();

    public AssinaturaHelper() {
        accessToken = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_ASSINATURA_DIGITAL, AcessoSistema.getAcessoUsuarioSistema());
        endPoint = (String) ParamSist.getInstance().getParam(CodedValues.TPC_END_POINT_ASSINATURA_DIGITAL, AcessoSistema.getAcessoUsuarioSistema());
    }

    /**
     * Retorna um objeto AssinaturaHelper já criado
     * @return
     */
    public static AssinaturaHelper getInstance() {
        return instance;
    }

    /**
     * Recupera um documento a partir de sua chave
     * @param chave
     * @return
     */
    public Documento recuperarDocumento(String chave) throws Exception {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        CloseableHttpClient httpClient = null;

        try {
            final String url = endPoint + "/api/v1/documents/" + chave + "?access_token=" + accessToken;

            httpClient = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            final HttpResponse result = httpClient.execute(request);
            final String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            final JsonReader jsonReader = Json.createReader(new StringReader(json));
            final jakarta.json.JsonObject document = jsonReader.readObject();

            if ((document == null) || document.containsKey("errors")) {
                if (document != null) {
                    LOG.debug(document.get("errors"));
                }
                throw new ZetraException("mensagem.erro.documento.nao.encontrado", responsavel);
            }

            final int statusCode = result.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.documento.nao.encontrado", responsavel));
                throw new ZetraException("mensagem.erro.documento.nao.encontrado", responsavel);
            }

            final Documento documento = criaDocumento(document);
            return documento;
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ZetraException("mensagem.erro.localizar.documento.para.assinatura.digital", responsavel, e);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    /**
     * Envia um arquivo para ser assinado.
     * OBS: As pessoas da lista terão que assinar o documento usando a interface web do Clicksign.
     * Um email é enviado para cada pessoa da lista.
     * @param arquivo Arquivo
     * @param assinaturas Lista de emails das pessoas a assinar o documento
     * @param mensagem Mensagem para ser enviada para as pessoas que irão assinar o documento
     */
    public Documento enviarArquivo(File arquivo, List<String> assinaturas, List<TransferObject> assinaturasComCPF, String mensagem) throws Exception {
        return enviarArquivo(arquivo, assinaturas, assinaturasComCPF, mensagem, false);
    }

    /**
     * Envia um arquivo para ser assinado.
     * OBS: As pessoas da lista terão que assinar o documento usando a interface web do Clicksign.
     * @param arquivo Arquivo
     * @param assinaturas Lista de emails das pessoas a assinar o documento
     * @param mensagem Mensagem para ser enviada para as pessoas que irão assinar o documento
     * @param skipEmail Informa se é necessário não enviar email para os proprietários das assinaturas
     */
    public Documento enviarArquivo(File arquivo, List<String> assinaturas, List<TransferObject> assinaturasComCPF, String mensagem, Boolean skipEmail) throws Exception {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final CloseableHttpClient httpClient = null;

        try (httpClient) {
            final String url = endPoint + "/api/v1/documents?access_token=" + accessToken;
            final String strJsonBody = gerarJsonCriacaoDocumento(arquivo, assinaturas, assinaturasComCPF, mensagem, skipEmail);

            final RestTemplate restTemplate = new RestTemplate();
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            final HttpEntity<String> requestEntity = new HttpEntity<>(strJsonBody, headers);
            final ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            final JsonReader jsonReader = Json.createReader(new StringReader(responseEntity.getBody()));
            final jakarta.json.JsonObject document = jsonReader.readObject();

            if ((document == null) || document.containsKey("errors")) {
                if (document != null) {
                    LOG.debug(document.get("errors"));
                }
                throw new ZetraException("mensagem.erro.documento.nao.encontrado", responsavel);
            }

            final int statusCode = responseEntity.getStatusCode().value();
            if (statusCode != HttpStatus.SC_CREATED) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.criar.documento", responsavel));
                throw new ZetraException("mensagem.erro.criar.documento", responsavel);
            }

            final Documento documento = criaDocumento(document);

            return documento;

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ZetraException("mensagem.erro.enviar.documento.para.assinatura.digital", responsavel, e);
        }
    }

    public static String gerarJsonCriacaoDocumento(File arquivo, List<String> assinantes, List<TransferObject> assinaturasComCPF, String mensagem, Boolean skipEmail) throws IOException, ZetraException {
        final ObjectMapper mapper = new ObjectMapper();

        final String nameDocument = arquivo.getName();
        final String id = mapper.writeValueAsString("/" + nameDocument);
        final String fileBase64 = Base64.encodeBase64String(FileUtils.readFileToByteArray(arquivo));

        // Detecta o mime type do arquivo a ser enviado
        final Set<MimeType> mimeCollection = FileHelper.detectContentType(arquivo);
        final String contentType = (mimeCollection != null) && (mimeCollection.size() > 0) ? mimeCollection.toArray()[0].toString() : "application/pdf";
        final String content = mapper.writeValueAsString("data:" + contentType + ";base64," + fileBase64);

        final String strLocale = LocaleHelper.getLocale();
        final String locale =  (strLocale.equals(LocaleHelper.BRASIL) || strLocale.equals(LocaleHelper.EUA)) ? strLocale : LocaleHelper.EUA;

        StringBuilder strJsonBody = new StringBuilder("{").append("\"document\": ").append("{").append("\"path\": ").append(id).append("," // Sempre iniciar com barra
).append("\"content_base64\": ").append(content).append("," // Formato "data:application/pdf;base64,data"
).append("\"auto_close\": ").append(true).append(",").append("\"locale\": ").append(mapper.writeValueAsString(locale)).append(",");

        if ((assinaturasComCPF != null) && !assinaturasComCPF.isEmpty()) {
            strJsonBody.append("").append("\"signers\": [");
            for (final TransferObject assinante : assinaturasComCPF) {
                strJsonBody.append("").append("{").append("\"documentation\": ").append(mapper.writeValueAsString(assinante.getAttribute(Columns.SER_CPF))).append(",").append("\"email\": ").append(mapper.writeValueAsString(assinante.getAttribute(Columns.SER_EMAIL))).append(",").append("\"sign_as\": ").append(mapper.writeValueAsString("sign")).append(",").append("\"auths\": [").append("\"icp_brasil\"").append("]").append(",").append("\"has_documentation\": ").append(true).append(",").append("\"send_email\": ").append(mapper.writeValueAsString(!skipEmail)).append(",").append("\"message\": ").append(mapper.writeValueAsString(mensagem)).append("}");
            }
            strJsonBody.append("").append("]");

        } else if (assinantes != null) {
            strJsonBody.append("").append("\"signers\": [");
            for (final String assinante : assinantes) {
                strJsonBody.append("").append("{").append("\"email\": ").append(mapper.writeValueAsString(assinante)).append(",").append("\"sign_as\": ").append(mapper.writeValueAsString("sign")).append(",").append("\"auths\": [").append("\"email\"").append("]").append(",").append("\"has_documentation\": ").append(false).append(",").append("\"send_email\": ").append(mapper.writeValueAsString(!skipEmail)).append(",").append("\"message\": ").append(mapper.writeValueAsString(mensagem)).append("}");
            }
            strJsonBody.append("").append("]");
        }

        strJsonBody.append("").append("}").append("}");
        return strJsonBody.toString();
    }

    private Documento criaDocumento(jakarta.json.JsonObject json) {
        final jakarta.json.JsonObject document = json.getJsonObject("document");

        final Documento doc = new Documento();
        doc.chave = document.getString("key");
        doc.assinado = !TextHelper.isNull(document.getString("status")) ? document.getString("status").equals("closed") : false;
        doc.nome = document.getString("filename");

        final String dataCriacao = document.getString("uploaded_at");
        try {
            doc.dataCriacao = DateHelper.parse(dataCriacao, "yyyy-MM-dd'T'HH:mm:ss.SS");
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            LOG.error("Não foi possível realizar o parser da data de criação.");
        }

        final String dataModificacao = document.getString("updated_at");
        try {
            doc.dataModificacao = DateHelper.parse(dataModificacao, "yyyy-MM-dd'T'HH:mm:ss.SS");
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            LOG.error("Não foi possível realizar o parser da data de modificação.");
        }

        return doc;
    }

    public class Documento {
        public String chave;
        public String nome;
        public Boolean assinado;
        public Date dataCriacao;
        public Date dataModificacao;
    }
}
