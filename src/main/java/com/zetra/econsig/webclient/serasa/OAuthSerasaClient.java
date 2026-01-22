package com.zetra.econsig.webclient.serasa;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ConsentRequestDTO;
import com.zetra.econsig.dto.web.SerasaToken;
import com.zetra.econsig.exception.SerasaException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.query.termoAdesao.ObterTermoAdesaoComLeituraQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webclient.sso.OAuth2SSOClient;
import com.zetra.econsig.webservice.rest.request.AuditTrail;
import com.zetra.econsig.webservice.rest.request.Authentication;
import com.zetra.econsig.webservice.rest.request.Consent;
import com.zetra.econsig.webservice.rest.request.Document;
import com.zetra.econsig.webservice.rest.request.UsagePurpose;

/**
 * <p>Title: OAuth2SSOClient</p>
 * <p>Description: Implementação OAuth2 de SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Fagner Luiz, Leonel Martins
 */
@Component
public class OAuthSerasaClient implements SerasaClient {

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OAuth2SSOClient.class);

    private static final String PROVEDOR = "econsig";

    @Override    
    public ResponseEntity<SerasaToken> autenticar(String serasaTokenUrl, String clientId, String clientSecret, AcessoSistema responsavel) throws SerasaException {
        RestTemplate restTemplate = new RestTemplate();
        try{
            if (TextHelper.isNull(clientId) || TextHelper.isNull(clientSecret) || TextHelper.isNull(serasaTokenUrl)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.parametros.serasa.invalidos", responsavel));
                throw new ZetraException("mensagem.erro.parametros.serasa.invalidos", responsavel);
            }

            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + encodedAuth;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(null, headers);
            final ResponseEntity<SerasaToken> responseEntity = restTemplate.postForEntity(serasaTokenUrl, request, SerasaToken.class);
           
            return responseEntity;

        } catch(Exception ex){
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.parametros.serasa.invalidos", responsavel));
            LOG.error(ex.getMessage(), ex);
            throw new SerasaException("mensagem.erro.autenticacao.serasa", responsavel);
        }
    } 

    public boolean enviarConsentimento(ConsentRequestDTO consentData, AcessoSistema responsavel, SerasaToken token) throws SerasaException {
        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_CONSENT_SERASA, responsavel);
        
        if (TextHelper.isNull(urlBase)) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token.accessToken);
            String json = new ObjectMapper().writeValueAsString(consentData);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(urlBase, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
             LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.enviar.consentimento.serasa", AcessoSistema.getAcessoUsuarioSistema()));
             LOG.error(e.getMessage());
            return false;
        }
    }

    public ConsentRequestDTO montarConsentimento(String cpf, String tadCodigo, String usuCodigo, SerasaToken token, AcessoSistema responsavel, String ltuCodigo) throws SerasaException {
        try {
            ObterTermoAdesaoComLeituraQuery query = new ObterTermoAdesaoComLeituraQuery();
            query.tadCodigo = tadCodigo;
            query.usuCodigo = usuCodigo;
            query.ltuCodigo = ltuCodigo;
            List<TransferObject> resultado = query.executarDTO();

            Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);

            if (resultado == null || resultado.isEmpty()) {
                return null;
            }

            TransferObject row = resultado.get(0);
            String dataSet = (String) row.getAttribute(Columns.TAD_ENVIA_API_CONSENTIMENTO);
            Integer versaoTermo = (Integer) row.getAttribute(Columns.TAD_VERSAO_TERMO);
            String hashConnection = String.valueOf(row.getAttribute(Columns.LTU_CODIGO));
            Timestamp ts = (Timestamp) row.getAttribute(Columns.LTU_DATA);
            LocalDateTime consentDate = ts.toLocalDateTime();

            ConsentRequestDTO data = new ConsentRequestDTO();
            data.data_set = dataSet;
            data.provider = PROVEDOR;
            data.source = cse.getCseNome();
            data.channel = "web";
            data.hash_connection = hashConnection;
            data.consent_date = consentDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Consent consent = new Consent();
            consent.term_version = versaoTermo;

            consent.usage_purpose = Arrays.asList(
                new UsagePurpose("SALARY_VERIFICATION", true)              
            );
            consent.consent_url = "";
            data.consent = consent;

            Authentication auth = new Authentication();
            auth.method = "jwt";
            auth.token = token.accessToken;
            auth.service = "platform";
            data.authentication = auth;
            
            Document doc = new Document();
            doc.num_document = cpf.replaceAll("\\D","");
            doc.type_document = 2;
            data.document = doc;
            
            AuditTrail audit = new AuditTrail();
            audit.trail = "00-" + hashConnection;
            data.audit_trail = audit;
            return data;
        } catch (Exception e) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.interno", AcessoSistema.getAcessoUsuarioSistema()));
            LOG.error(e.getMessage());
            throw new SerasaException("mensagem.erro.processamento.interno",responsavel, e);
        }
    }

}