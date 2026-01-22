package com.zetra.econsig.helper.criptografia;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import jakarta.annotation.Nullable;

/**
 * Exporta os métodos que utilizam o SecurityProvider, de forma que o
 * controle sobre qual provedor está sendo usado para segurança seja
 * realizado de um ponto centrael.
 * @author luis
 *
 */
public class SecurityProvider {

    private static final String PROVIDER = "BC";
    private static final SecurityProvider singleton = new SecurityProvider();
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SecurityProvider.class);

    /**
     * Configura o provedor de segurança
     */
    private SecurityProvider(){
        Security.addProvider(new BouncyCastleProvider());
    }

    public static SecurityProvider getInstance(){
        return singleton;
    }

    @Nullable
    public KeyFactory getKeyFactory(String algorithm) {
        KeyFactory res = null;
        try {
             res = KeyFactory.getInstance(algorithm, PROVIDER);
        } catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return res;
    }

    @Nullable
    public KeyPairGenerator getKeyPairGenerator(String algorithm){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(algorithm, PROVIDER);
        } catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return kpg;
    }

    @Nullable
    public Cipher getCypher(String algorithm){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm, PROVIDER);
        } catch (final NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return cipher;
    }

    @ Nullable
    public KeyStore getKeyStore(){
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (final KeyStoreException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return trustStore;
    }
}
