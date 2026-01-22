package com.zetra.econsig.helper.usuario;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.ParametroTipo;
import com.zetra.econsig.parser.config.RegistroTipo;

/**
 * <p>Title: CertificadoDigital</p>
 * <p>Description: Classe que define as propriedades do Certificado Digital e realiza a validacao.</p>
 * <p>Copyright: Copyright (c) 2009-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class CertificadoDigital {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CertificadoDigital.class);

    public static final String GERAL = "GERAL";

    // INFORMACOES DO CERTIFICADO
    public static final String CPF = "CPF";
    public static final String NOME = "NOME";
    public static final String MATRICULA = "MATRICULA";
    public static final String DATA_NASCIMENTO = "DATA_NASCIMENTO";
    public static final String NIS = "NIS";
    public static final String RG = "RG";
    public static final String EMISSOR_RG = "EMISSOR_RG";
    public static final String INSS = "INSS";
    public static final String TITULO_ELEITOR = "TITULO_ELEITOR";
    public static final String ZONA_ELEITORAL = "ZONA_ELEITORAL";
    public static final String SECAO_ELEITORAL = "SECAO_ELEITORAL";
    public static final String MUNICIPIO = "MUNICIPIO";
    public static final String CNPJ = "CNPJ";
    public static final String PJ_RESPONSAVEL = "PJ_RESPONSAVEL";
    public static final String PJ_INSS = "PJ_INSS";
    public static final String PJ_NOME_EMPRESARIAL = "PJ_NOME_EMPRESARIAL";
    public static final String UNIDADE_ORGANIZACIONAL = "UNIDADE_ORGANIZACIONAL";

    // CAMPOS DO DISTINGUISHED NAME (DN)
    public static final String COMMON_NAME = "CN";
    public static final String ORGANIZATIONAL_UNIT = "OU";
    public static final String ORGANIZATION = "O";
    public static final String LOCALITY = "L";
    public static final String STATE = "ST";
    public static final String COUNTRY = "C";

    // OID especificados pela ICP Brasil
    public static final ASN1ObjectIdentifier OID_PF_DADOS_TITULAR     = new ASN1ObjectIdentifier("2.16.76.1.3.1");
    public static final ASN1ObjectIdentifier OID_PJ_RESPONSAVEL       = new ASN1ObjectIdentifier("2.16.76.1.3.2");
    public static final ASN1ObjectIdentifier OID_PJ_CNPJ              = new ASN1ObjectIdentifier("2.16.76.1.3.3");
    public static final ASN1ObjectIdentifier OID_PJ_DADOS_RESPONSAVEL = new ASN1ObjectIdentifier("2.16.76.1.3.4");
    public static final ASN1ObjectIdentifier OID_PF_ELEITORAL         = new ASN1ObjectIdentifier("2.16.76.1.3.5");
    public static final ASN1ObjectIdentifier OID_PF_INSS              = new ASN1ObjectIdentifier("2.16.76.1.3.6");
    public static final ASN1ObjectIdentifier OID_PJ_INSS              = new ASN1ObjectIdentifier("2.16.76.1.3.7");
    public static final ASN1ObjectIdentifier OID_PJ_NOME_EMPRESARIAL  = new ASN1ObjectIdentifier("2.16.76.1.3.8");

    public static final String ARQ_CERTIFICADO_DIGITAL = "certificado_digital.xml";

    private final Map<String, Map<String, String>> cacheFormatos;

    private static class SingletonHelper {
        private static final CertificadoDigital instance = new CertificadoDigital();
    }

    public static CertificadoDigital getInstance() {
        return SingletonHelper.instance;
    }

    private CertificadoDigital() {
        if (ExternalCacheHelper.hasExternal()) {
            cacheFormatos = new ExternalMap<>();
        } else {
            cacheFormatos = new HashMap<>();
        }
        configurar();
    }

    public void reset() {
        cacheFormatos.clear();
    }

    /**
     * Le configuracoes do XML ARQ_CERTIFICADO_DIGITAL e salva em cacheFormatos. Se nao existe XML, o valor default é: CN=CPF.
     * Ex: <Documento>
     *       <Registro Nome="GERAL">
     *          <Atributo Nome="CN" Valor="CPF"/>
     *       </Registro>
     *       <Registro Nome="OU=RFB e-CPF A3">
     *          <Atributo Nome="CN" Valor="NOME,CPF"/>
     *       </Registro>
     *     </Documento>
     */
    private void configurar() {
        try {
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathXml = absolutePath + File.separatorChar + "conf";
            final String arqConf = pathXml + File.separatorChar + ARQ_CERTIFICADO_DIGITAL;
            if ((new File(arqConf)).exists()) {
                final DocumentoTipo doc = XmlHelper.unmarshal(new FileInputStream(arqConf));
                final List<RegistroTipo> registros = doc.getRegistro();
                final Map<String, Map<String, String>> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cacheFormatos;
                for (final RegistroTipo registro : registros) {
                    final List<ParametroTipo> atributos = registro.getAtributo();
                    final Map<String, String> cacheCampos = new HashMap<>();
                    final Map<String, Object> parametrosMap = XmlHelper.parametrosToMap(atributos);
                    parametrosMap.keySet().stream().filter(key -> parametrosMap.get(key) != null).forEach(key -> cacheCampos.put(key, parametrosMap.get(key).toString()));
                    mapForLoad.put((!TextHelper.isNull(registro.getNome())? registro.getNome(): GERAL) , cacheCampos);
                }
                if (ExternalCacheHelper.hasExternal() && cacheFormatos.isEmpty()) {
                    cacheFormatos.putAll(mapForLoad);
                }
            } else {
                final Map<String, String> cacheCampos = new HashMap<>();
                cacheCampos.put(CPF, CPF);
                cacheFormatos.put(GERAL, cacheCampos);
            }
        } catch (final Exception ex) {
            LOG.error("Não foi possivel ler as configurações do arquivo " + ARQ_CERTIFICADO_DIGITAL + ": " + ex.getMessage(), ex);
            final Map<String, String> cacheCampos = new HashMap<>();
            cacheCampos.put(CPF, CPF);
            cacheFormatos.put(GERAL, cacheCampos);
        }
    }

    /**
     * Recupera o mapeamento de cacheFormatos. Se estiver vazio (reset), entao le novamente as configuracoes.
     * @return cacheFormatos.
     */
    public Map<String, Map<String, String>> getFormatos() {
        if (cacheFormatos.isEmpty()) {
            synchronized (this) {
                if (cacheFormatos.isEmpty()) {
                    configurar();
                }
            }
        }
        return cacheFormatos;
    }

    /**
     * Realiza a validacao das informacoes recebidas no Certificado Digital de acordo com as
     * configuracoes feitas no arquivo certificado_digital.xml.
     * @param assunto Conteudo do Certificado.
     * @param usuCodigo Codigo do Usuario.
     * @param csaCodigo Codigo da Consignataria.
     * @param responsavel Responsavel pela operacao.
     * @return Verdadeiro, se os valores forem compativeis. Falso, se forem incompativeis.
     *         Nulo, se nao ocorreu nenhuma validacao (configuracao errada no xml).
     * @throws UsuarioControllerException
     * @throws ConsignatariaControllerException
     */
    public Boolean validarCertificado(X509Certificate certificado, AcessoSistema responsavel) throws UsuarioControllerException, ConsignatariaControllerException {
        final Map<String, Boolean> mapValidacoes = new HashMap<>();

        final X500Principal assunto = certificado.getSubjectX500Principal();

        // Recupera informacoes do usuario e consignataria
        final UsuarioDelegate usuDelegate = new UsuarioDelegate();
        final UsuarioTransferObject usuario = usuDelegate.findUsuario(responsavel.getUsuCodigo(), responsavel);
        ConsignatariaTransferObject consignataria = null;
        if (responsavel.isCsaCor()) {
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            consignataria = csaDelegate.findConsignataria(responsavel.getCsaCodigo(), responsavel);
        }

        // Recupera informacoes dos formatos configurados no campo <Registro> do ARQ_CERTIFICADO_DIGITAL
        final Map<String, Map<String, String>> formatos = getFormatos();

        // Cria um map com as informacoes do Certificado, ja que alguns campos podem vir repetidos
        final Map<String, String> mapTokens = getTokens(assunto.toString(), responsavel);

        // Adiciona ao mapa as informações de extensão do certificado
        parseCertificate(certificado, mapTokens);

        // Realiza a iteracao sobre os possiveis formatos de certificado configurados
        for (final String chaveFormato : getFormatosPossiveis(formatos.keySet(), mapTokens)) {
            Boolean campoValido = null;

            // Realiza a iteracao sobre as informacoes enviadas no certificado
            for (final Map.Entry<String, String> mapTokensEntry : mapTokens.entrySet()) {
                // Verifica se a informacao esta entre as que devem ser validadas
                final Map<String, String> formato = formatos.get(chaveFormato);
                final String campo = (formato != null)? (String) formato.get(mapTokensEntry.getKey()): null;
                if (campo != null) {
                    campoValido = validarCampo(campo, mapTokensEntry.getValue(), usuario, consignataria);
                    if ((campoValido != null) && campoValido.equals(Boolean.FALSE)) {
                        break;
                    }
                }
            }
            // Map das validacoes
            mapValidacoes.put(chaveFormato, campoValido);
        }

        final boolean valido = mapValidacoes.containsValue(Boolean.TRUE);

        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.CERTIFICADO, Log.VALIDAR_CERTIFICADO, (valido ? Log.LOG_INFORMACAO : Log.LOG_ERRO));
            log.setUsuario(responsavel.getUsuCodigo());
            log.add(ApplicationResourcesHelper.getMessage("rotulo.certificado.digital.arg0", responsavel, assunto.toString()));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return valido;
    }

    /**
     * Realiza a validacao das informacoes recebidas no Certificado Digital de acordo com as
     * configuracoes feitas no arquivo certificado_digital.xml.
     * @param assunto Certificado
     * @param responsavel Responsavel pela operacao.
     * @return Verdadeiro, se os valores forem compativeis. Falso, se forem incompativeis.
     *         Nulo, se nao ocorreu nenhuma validacao (configuracao errada no xml).
     * @throws UsuarioControllerException
     * @throws ConsignatariaControllerException
     * @throws ParseException 
     */
    public Boolean validarCertificado(String assunto, AcessoSistema responsavel) throws UsuarioControllerException, ConsignatariaControllerException, ParseException {
        JSONParser parser = new JSONParser();
        final JSONObject jsonObject = (JSONObject) parser.parse(assunto);
        Map<String, String> mapTokens = convertToMap(jsonObject);

        final Map<String, Boolean> mapValidacoes = new HashMap<>();
        
        // Recupera informacoes do usuario e consignataria
        final UsuarioDelegate usuDelegate = new UsuarioDelegate();
        final UsuarioTransferObject usuario = usuDelegate.findUsuario(responsavel.getUsuCodigo(), responsavel);
        ConsignatariaTransferObject consignataria = null;
        if (responsavel.isCsaCor()) {
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            consignataria = csaDelegate.findConsignataria(responsavel.getCsaCodigo(), responsavel);
        }

        // Recupera informacoes dos formatos configurados no campo <Registro> do ARQ_CERTIFICADO_DIGITAL
        final Map<String, Map<String, String>> formatos = getFormatos();

        // Realiza a iteracao sobre os possiveis formatos de certificado configurados
        for (final String chaveFormato : getFormatosPossiveis(formatos.keySet(), mapTokens)) {
            Boolean campoValido = null;

            // Realiza a iteracao sobre as informacoes enviadas no certificado
            for (final Map.Entry<String, String> mapTokensEntry : mapTokens.entrySet()) {
                // Verifica se a informacao esta entre as que devem ser validadas
                final Map<String, String> formato = formatos.get(chaveFormato);
                final String campo = (formato != null)? (String) formato.get(mapTokensEntry.getKey()): null;
                if (campo != null) {
                    campoValido = validarCampo(campo, mapTokensEntry.getValue(), usuario, consignataria);
                    if ((campoValido != null) && campoValido.equals(Boolean.FALSE)) {
                        break;
                    }
                }
            }
            // Map das validacoes
            mapValidacoes.put(chaveFormato, campoValido);
        }

        final boolean valido = mapValidacoes.containsValue(Boolean.TRUE);

        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.CERTIFICADO, Log.VALIDAR_CERTIFICADO, (valido ? Log.LOG_INFORMACAO : Log.LOG_ERRO));
            log.setUsuario(responsavel.getUsuCodigo());
            log.add(ApplicationResourcesHelper.getMessage("rotulo.certificado.digital.arg0", responsavel, assunto.toString()));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return valido;
    }

    private static Map<String, String> convertToMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();

        for (Object key : jsonObject.keySet()) {
            String value = (String) jsonObject.get(key);
            map.put((String) key, value);
        }

        return map;
    }

    /**
     * Obtem um mapeamento das informacoes recebidas no certificado digital do usuario.
     * @param assunto Informacoes do certificado do usuario.
     * @param responsavel Responsavel pela operacao.
     * @return Mapeamento das informacoes.
     */
    @SuppressWarnings("java:S2259")
    private Map<String, String> getTokens(String assunto, AcessoSistema responsavel) {
        final Map<String, String> mapTokens = new HashMap<>();

        final StringTokenizer nameTokenizer = new StringTokenizer(assunto, ",");
        while (nameTokenizer.hasMoreTokens()) {
            String token = nameTokenizer.nextToken().trim();
            String dn = null;
            if (token.toUpperCase().startsWith(COMMON_NAME.concat("="))) {
                dn = COMMON_NAME;
            } else if (responsavel.isCsaCor()) {
                dn = getDN(token);
            }
            if (!TextHelper.isNull(dn)) {
                token = token.substring(dn.length() + 1, token.length());
                if (TextHelper.isNull(mapTokens.get(dn))) {
                    mapTokens.put(dn, token);
                } else {
                    // Concatena valores de campos repetidos, se existirem
                    mapTokens.put(dn, mapTokens.get(dn).concat(":").concat(token));
                }
            }
        }
        return mapTokens;
    }

    /**
     * Verifica quais formatos podem ser validados de acordo com o certificado do usuario. Se não existir nenhum
     * certificado específico configurado no certificado do usuario, então pode usar qualquer um.
     * @param formatos Conjunto de identificadores de formatos.
     * @param mapTokens Mapeamentos do certificado do usuario.
     * @return Conjunto de identificadores de formatos possiveis.
     */
    private Set<String> getFormatosPossiveis(Set<String> formatos, Map<String,String> mapTokens) {
        final List<String> possiveis = new ArrayList<>();
        for (final String formato : formatos) {
            final String dn = getDN(formato);
            if (mapTokens.get(dn) != null) {
                final String registro = (formato.indexOf("=") != -1)? formato.substring(dn.length() + 1): formato.substring(dn.length());
                if (mapTokens.get(dn).toUpperCase().indexOf(registro.toUpperCase()) != -1) {
                    possiveis.add(formato);
                }
            }
        }
        if (possiveis.isEmpty()) {
            return formatos;
        }
        return new HashSet<>(possiveis);
    }

    /**
     * Realiza efetivamente a validacao da informacao do certificado digital.
     * @param campo Campo a ser validado.
     * @param token Informacao do certificado.
     * @param usuario Informacoes do usuario no sistema.
     * @param consignataria Informacoes da consignataria no sistema.
     * @return Resultado da validacao.
     */
    private Boolean validarCampo(String campo, String token, UsuarioTransferObject usuario, ConsignatariaTransferObject consignataria) {
        Boolean campoValido = null;
        final String[] dadosCampo = TextHelper.split(campo, ",");

        // Para cada dado a ser validado, realiza a comparacao dos dados entre certificado e banco
        for (final String element : dadosCampo) {
            if (!TextHelper.isNull(element)) {
                String valor = null;
                if (element.trim().equalsIgnoreCase(CPF)) {
                    valor = usuario.getUsuCPF() != null ? TextHelper.dropSeparator(usuario.getUsuCPF()) : null;
                } else if (element.trim().equalsIgnoreCase(NOME)) {
                    valor = usuario.getUsuNome() != null ? TextHelper.removeAccent(usuario.getUsuNome()) : null;
                } else if (element.trim().equalsIgnoreCase(MATRICULA)) {
                    valor = usuario.getUsuMatriculaInst() != null ? TextHelper.dropSeparator(usuario.getUsuMatriculaInst()) : null;
                } else if (element.trim().equalsIgnoreCase(UNIDADE_ORGANIZACIONAL)) {
                    valor = (consignataria != null) && (consignataria.getCsaUnidadeOrganizacional() != null) ? TextHelper.removeAccent(consignataria.getCsaUnidadeOrganizacional()) : null;
                } else {
                    continue;
                }

                // Compara informacao do certificado com valor no banco
                if (!TextHelper.isNull(token) && !TextHelper.isNull(valor) && (TextHelper.removeAccent(token).toLowerCase().indexOf(valor.toLowerCase()) != -1)) {
                   campoValido = Boolean.TRUE;
                } else {
                   campoValido = Boolean.FALSE;
                   break;
                }
            }
        }
        return campoValido;
    }

    /**
     * Verifica qual campo do DISTINGUISHED NAME (DN) corresponde ao registro recebido como parametro.
     * @param registro Informacao a ser analisada.
     * @return Campo do DISTINGUISHED NAME (DN).
     */
    private String getDN(String registro) {
        if (registro.startsWith(COMMON_NAME.concat("="))) {
            return COMMON_NAME;
        } else if (registro.startsWith(ORGANIZATIONAL_UNIT.concat("="))) {
            return ORGANIZATIONAL_UNIT;
        } else if (registro.startsWith(ORGANIZATION.concat("="))) {
            return ORGANIZATION;
        } else if (registro.startsWith(LOCALITY.concat("="))) {
            return LOCALITY;
        } else if (registro.startsWith(STATE.concat("="))) {
            return STATE;
        } else if (registro.startsWith(COUNTRY.concat("="))) {
            return COUNTRY;
        }
        return "";
    }

    /**
     * Retorna TRUE caso na lista de certificados válidos, algum deles aceite a matrícula institucional
     * como campo para validação.
     * @return
     */
    public boolean aceitaCertificadoPelaMatriculaInst() {
        // getFormatos() recupera informacoes dos formatos configurados no campo <Registro> do ARQ_CERTIFICADO_DIGITAL
        // Realiza a iteracao sobre os possiveis formatos de certificado configurados
        for (final Map.Entry<String, Map<String, String>> formatosEntry : getFormatos().entrySet()) {
            // Verifica se algum dos campos possui a matrícula
            for (final String campoFormato : formatosEntry.getValue().values()) {
                if (campoFormato.contains(MATRICULA)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Realiza o "parse" do certificado informado por parâmetro, adicionando os campos
     * presentes na extensão "SubjectAlternativeNames" no mapa de tokens do certificado.
     * @param cert
     * @param mapTokens
     */
    private void parseCertificate(X509Certificate cert, Map<String, String> mapTokens) {
        try {
            final Collection<List<?>> col = cert.getSubjectAlternativeNames();
            if (col != null) {
                for (final List<?> lst : col) {
                    Object value = lst.get(1);

                    if (value instanceof final byte[] valueByteA) {
                        value = toDERObject(valueByteA);
                    }

                    if (value instanceof final DERSequence seq) {
                        /**
                         * DER Sequence
                         *      ObjectIdentifier
                         *      Tagged
                         *          DER Octet String
                         */
                        final ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) seq.getObjectAt(0);
                        DERTaggedObject tagged = (DERTaggedObject) seq.getObjectAt(1);
                        String info = null;

                        if (tagged.getBaseObject() instanceof final DERTaggedObject taggedObject) {
                            tagged = taggedObject;
                        }

                        final ASN1Primitive derObj = tagged.toASN1Primitive();

                        if ((derObj instanceof final DEROctetString octetString)) {
                            info = new String(octetString.getOctets());
                        } else if ((derObj instanceof final DERPrintableString octetPrintableString)) {
                            info = new String(octetPrintableString.getOctets());
                        } else if (derObj instanceof final DERUTF8String str) {
                            info = str.getString();
                        }

                        if ((info != null) && !info.isEmpty()) {
                            if (oid.equals(OID_PF_DADOS_TITULAR) || oid.equals(OID_PJ_DADOS_RESPONSAVEL)) {
                                final String nascimento = info.substring(0, 8);
                                mapTokens.put(DATA_NASCIMENTO, nascimento);
                                final String cpf = info.substring(8, 19);
                                mapTokens.put(CPF, cpf);
                                final String nis = info.substring(19, 30);
                                mapTokens.put(NIS, nis);
                                final String rg = info.substring(30, 45);
                                mapTokens.put(RG, rg);
                                if (!rg.equals("000000000000000")) {
                                    final String ufExp = info.substring(45, info.length());
                                    mapTokens.put(EMISSOR_RG, ufExp);
                                }
                            } else if (oid.equals(OID_PF_INSS)) {
                                final String inss = info.substring(0, 12);
                                mapTokens.put(INSS, inss);
                            } else if (oid.equals(OID_PF_ELEITORAL)) {
                                final String titulo = info.substring(0, 12);
                                mapTokens.put(TITULO_ELEITOR, titulo);
                                final String zona = info.substring(12, 15);
                                mapTokens.put(ZONA_ELEITORAL, zona);
                                final String secao = info.substring(15, 19);
                                mapTokens.put(SECAO_ELEITORAL, secao);
                                if (!titulo.equals("000000000000")) {
                                    final String municipio = info.substring(19, info.length());
                                    mapTokens.put(MUNICIPIO, municipio);
                                }
                            } else if (oid.equals(OID_PJ_RESPONSAVEL)) {
                                mapTokens.put(PJ_RESPONSAVEL, info);
                            } else if (oid.equals(OID_PJ_CNPJ)) {
                                mapTokens.put(CNPJ, info);
                            } else if (oid.equals(OID_PJ_INSS)) {
                                mapTokens.put(PJ_INSS, info);
                            } else if (oid.equals(OID_PJ_NOME_EMPRESARIAL)) {
                                mapTokens.put(PJ_NOME_EMPRESARIAL, info);
                            }
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private ASN1Primitive toDERObject(byte[] data) throws IOException {
        try (ASN1InputStream asnInputStream = new ASN1InputStream(new ByteArrayInputStream(data))) {
            return asnInputStream.readObject();
        }
    }
}
