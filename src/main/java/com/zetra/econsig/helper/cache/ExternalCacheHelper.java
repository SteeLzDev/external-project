package com.zetra.econsig.helper.cache;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Title: ExternalCacheHelper</p>
 * <p>Description: Classe auxiliar para geração da chave única para o sistema.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 */
@Getter
@Setter
public class ExternalCacheHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExternalCacheHelper.class);

    private static final Set<String> ignoredClasses = new HashSet<>();

    private static final ExternalCacheConfig externalCacheConfig;

    @SuppressWarnings("java:S115")
    private static final String databaseUrl;

    @SuppressWarnings("java:S115")
    private static final String releaseTag;

    private boolean enabled;

    private boolean paramsistEnabled;

    private boolean paramsvcEnabled;

    private boolean controleConsultaEnabled;

    private boolean controleComunicacaoPermitidaEnabled;

    private boolean controleTipoEntidadeEnabled;

    private boolean acessoRecursoEnabled;

    private boolean margemEnabled;

    private boolean naturezaRelSvcEnabled;

    private boolean certificadoDigitalEnabled;

    private boolean funcaoExigeMotivoEnabled;

    private boolean configRelatorioEnabled;

    private boolean controleRestricaoAcessoEnabled;

    private boolean casamentoMargemEnabled;

    private boolean statusAutorizacaoDescontoEnabled;

    private boolean periodoEnabled;

    private boolean repasseEnabled;

    private boolean controleAcessoSegurancaEnabled;

    private boolean applicationResourcesEnabled;

    private boolean paramSenhaExternaHelperEnabled;

    private boolean senhaExternaEnabled;

    private boolean controleEnvioEmailEnabled;

    private boolean showFieldEnabled;

    private boolean viewImageEnabled;

    private boolean ipWatchdogEnabled;

    private boolean recursoSistemaEnabled;

    static {
        ignoredClasses.add(Thread.class.getName());
        ignoredClasses.add(ExternalCacheHelper.class.getName());
        ignoredClasses.add(ExternalMap.class.getName());
        ignoredClasses.add(ExternalSet.class.getName());

        externalCacheConfig = ApplicationContextProvider.getApplicationContext().getBean(ExternalCacheConfig.class);

        String url = "";
        try {
            final DataSource dataSource = ApplicationContextProvider.getApplicationContext().getBean("dataSource", DataSource.class);
            final String cleanURI = dataSource.getConnection().getMetaData().getURL().substring(5);
            final URI uri = URI.create(cleanURI);
            url = uri.getPath();
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        databaseUrl = url;

        String version = "";
        try {
            final Properties file = new Properties();
            file.load(ApplicationResourcesHelper.class.getClassLoader().getResourceAsStream("ApplicationResources.properties"));

            version = file.get("release.tag").toString();
        } catch (final IOException | NullPointerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        releaseTag = version;
    }

    /**
     *
     * @return
     */
    public static boolean hasExternal() {
        final boolean hasExternal = externalCacheConfig.isEnabled() && (buildKey("enabled") != null);
        if (hasExternal) {
            final Boolean enabledByClass = externalCacheConfig.getEnabledByClass(getCallerName());
            if (enabledByClass != null) {
                return enabledByClass;
            }
        }

        return hasExternal;
    }

    protected static String getCallerName() {
        final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (final StackTraceElement ste : stElements) {
            if (!ignoredClasses.contains(ste.getClassName())) {
                return ste.getClassName().substring(ste.getClassName().lastIndexOf('.') + 1);
            }
        }
        String name = null;
        if (stElements.length == 1) {
            name = stElements[0].getClassName().substring(stElements[2].getClassName().lastIndexOf('.') + 1);
        } else if (stElements.length > 1) {
            name = stElements[1].getClassName().substring(stElements[2].getClassName().lastIndexOf('.') + 1);
        } else {
            name = UUID.randomUUID().toString();
        }
        return name;
    }

    /**
    *
    * @param name
    * @return
    * @throws ZetraException
    */
    public static String buildKey(String name) {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
            final ParametroDelegate psiDelegate = new ParametroDelegate();
            final String urlSistema = psiDelegate.findParamSistCse(CodedValues.TPC_LINK_ACESSO_SISTEMA, CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());

            final StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(name);
            keyBuilder.append("@");
            keyBuilder.append(cse.getIdentificadorInterno());
            keyBuilder.append("#");
            final StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append(cse.getIdentificadorInterno());
            signatureBuilder.append("|");
            signatureBuilder.append(cse.getCseNome());
            signatureBuilder.append("|");
            signatureBuilder.append(databaseUrl);
            signatureBuilder.append("|");
            signatureBuilder.append(releaseTag);
            if (!TextHelper.isNull(urlSistema)) {
                signatureBuilder.append("|");
                signatureBuilder.append(urlSistema);
            }
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(signatureBuilder.toString().getBytes());
            final byte[] digest = md.digest();
            for (final byte element : digest) {
                keyBuilder.append(String.format("%02X", element));
            }
            return keyBuilder.toString();
        } catch (final ConsignanteControllerException | NoSuchAlgorithmException | ParametroControllerException ex) {
            LOG.error(ex);
            LOG.warn("Disabling external cache");
            externalCacheConfig.disable();
        }
        return null;
    }
}
