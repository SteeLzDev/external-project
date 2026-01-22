package com.zetra.econsig.helper.criptografia;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>Title: RSA</p>
 * <p>Description: Implementação do algoritimo RSA</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class RSA {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RSA.class);

    private static final int CHUNK_SIZE = 32;
    private static final String CIPHER_DELIMITER = ";";
    private static final String XFORM = "RSA/NONE/PKCS1PADDING";
    private static final String RSA = "RSA";

    /**
     * Gera uma nova dupla de chaves (Pública - Privada)
     * @param keySize
     * @return
     */
    public static KeyPair generateKeyPair(int keySize) {

        KeyPairGenerator kpg = SecurityProvider.getInstance().getKeyPairGenerator(RSA);
        kpg.initialize(keySize);
        return kpg.generateKeyPair();

    }

    /**
     * Cria uma chave pública com os parâmetros informados.
     * @param modulus
     * @param publicExponent
     * @return
     */
    public static PublicKey generatePublicKey(String modulus, String publicExponent) {
        try {
            BigInteger modulusBigInt = new BigInteger(modulus, 16);
            BigInteger publicExponentBigInt = new BigInteger(publicExponent, 16);
            KeyFactory keyFactory = SecurityProvider.getInstance().getKeyFactory(RSA);
            return keyFactory.generatePublic(new RSAPublicKeySpec(modulusBigInt, publicExponentBigInt));
        } catch (InvalidKeySpecException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Cria uma chave privada com os parâmetros informados
     * @param modulus
     * @param privateExponent
     * @return
     */
    public static PrivateKey generatePrivateKey(String modulus, String privateExponent) {
        try {
            BigInteger modulusBigInt = new BigInteger(modulus, 16);
            BigInteger privateExponentBigInt = new BigInteger(privateExponent, 16);
            KeyFactory keyFactory = SecurityProvider.getInstance().getKeyFactory(RSA);
            return keyFactory.generatePrivate(new RSAPrivateKeySpec(modulusBigInt, privateExponentBigInt));
        } catch (InvalidKeySpecException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Criptografa a entrada com a chave fornecida
     * @param input
     * @param key
     * @return
     */
    public static String encrypt(String input, Key key) {
        try {
            StringBuilder output = new StringBuilder();
            int j = 0;

            String msg = null;
            while (j + CHUNK_SIZE < input.length()) {
                msg = encodeBASE64(encrypt(input.substring(j, j + CHUNK_SIZE).getBytes(), key));
                output.append(msg).append(CIPHER_DELIMITER);
                j += CHUNK_SIZE;
            }
            if (j < input.length()) {
                msg = encodeBASE64(encrypt(input.substring(j).getBytes(), key));
                output.append(msg);
            }
            return output.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Decriptograda a entrada com a chave fornecida
     * @param input
     * @param key
     * @return
     */
    public static String decrypt(String input, Key key) throws BadPaddingException {
        try {
            String[] inputs = input.split(CIPHER_DELIMITER);
            StringBuilder output = new StringBuilder();
            for (String input2 : inputs) {
                output.append(new String(decrypt(decodeBASE64(input2), key)));
            }
            return output.toString();
        } catch (BadPaddingException bpe) {
            LOG.error("Erro ao decriptografar. É possível que a chave não corresponda à utilizada para criptografar. Este erro também acontece ao acessar o eConsig por http com a opção de secure cookie habilitada no web.xml; Neste caso o eConsig só pode ser acessado por https.");
            throw bpe;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private static byte[] encrypt(byte[] inpBytes, Key key) throws Exception {
        Cipher cipher = SecurityProvider.getInstance().getCypher(XFORM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    private static byte[] decrypt(byte[] inpBytes, Key key) throws Exception {
        Cipher cipher = SecurityProvider.getInstance().getCypher(XFORM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    /**
     * Encode bytes array to BASE64 string
     * @param bytes
     * @return Encoded string
     */
    public static String encodeBASE64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * Decode BASE64 encoded string to bytes array
     * @param text The string
     * @return Bytes array
     * @throws IOException
     */
    public static byte[] decodeBASE64(String text) throws IOException {
        return Base64.decodeBase64(text.getBytes());
    }
}
