package com.zetra.econsig.helper.criptografia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AES</p>
 * <p>Description: Rotinas auxiliares para criptografia de arquivos com chave AES + RSA</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AES {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AES.class);

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_CIPHER = "AES/CBC/PKCS5PADDING";
    public static final int AES_KEY_SIZE = 128;
    public static final int INIT_VECTOR_SIZE = 16;

    public static boolean encryptFile(byte[] key, String inputFileName, String outputFileName) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            final byte[] initVector = generateInitVector();
            final IvParameterSpec iv = new IvParameterSpec(initVector);
            final SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGORITHM);

            final Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            in = new BufferedInputStream(new FileInputStream(inputFileName));
            out = new BufferedOutputStream(new CipherOutputStream(new FileOutputStream(outputFileName), cipher));

            copyBytes(in, out);

            in.close();
            out.close();

            saveInitVector(initVector, outputFileName);
            return true;
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    public static String encryptText(byte[] key, byte[] initVector, String strToEncrypt) {
        try {
            final IvParameterSpec iv = new IvParameterSpec(initVector);
            final SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGORITHM);

            final Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static boolean decryptFile(byte[] key, String inputFileName, String outputFileName) {
        try {
            final byte[] initVector = new byte[INIT_VECTOR_SIZE];

            // Abre o arquivo criptografado para recuperar o initVector
            final RandomAccessFile randFile = new RandomAccessFile(inputFileName, "rw");
            final long fileSize = randFile.length();
            randFile.seek(fileSize - INIT_VECTOR_SIZE);
            randFile.read(initVector);
            // Apaga o initVector do arquivo pois ele não deve ser lido a descriptografar
            final FileChannel channel = randFile.getChannel();
            channel.truncate(fileSize - INIT_VECTOR_SIZE);
            channel.close();
            randFile.close();

            final IvParameterSpec iv = new IvParameterSpec(initVector);
            final SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGORITHM);

            final Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            final BufferedInputStream in = new BufferedInputStream(new CipherInputStream(new FileInputStream(inputFileName), cipher));
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName));

            copyBytes(in, out);

            in.close();
            out.close();

            // Salva novamente o initVector no arquivo criptografado
            saveInitVector(initVector, inputFileName);

            return true;
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    public static String decryptText(byte[] key, byte[] initVector, String strToDecrypt) {
        try {
            final IvParameterSpec iv = new IvParameterSpec(initVector);
            final SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGORITHM);

            final Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    private static void copyBytes(InputStream is, OutputStream os) throws IOException {
        int i;
        final byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }

    private static void saveInitVector(byte[] initVector, String fileName) throws IOException {
        final RandomAccessFile randFile = new RandomAccessFile(fileName, "rw");
        randFile.seek(randFile.length());
        randFile.write(initVector);
        randFile.close();
    }

    public static byte[] generateInitVector() {
        final byte[] ivBytes = new byte[INIT_VECTOR_SIZE];
        final SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        return ivBytes;
    }

    public static byte[] generateNewAESKey() {
        try {
            final KeyGenerator kgen = KeyGenerator.getInstance(AES_ALGORITHM);
            kgen.init(AES_KEY_SIZE);
            final SecretKey aesKey = kgen.generateKey();
            return aesKey.getEncoded();
        } catch (final NoSuchAlgorithmException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    private static Key getRSAKey(boolean publicKey) {
        try {
            final String aliasEconsig = CodedValues.PROTOCOLO_KEYSTORE_ALIAS_ECONSIG_PROPERTY;
            final String storepass = CodedValues.PROTOCOLO_KEYSTORE_PASS_PROPERTY;

            final KeyStore ks = KeyStore.getInstance("JKS");
            final InputStream is = AES.class.getResourceAsStream("/file.jks");
            ks.load(is, storepass.toCharArray());

            if (publicKey) {
                return ((X509Certificate) ks.getCertificate(aliasEconsig)).getPublicKey();
            } else {
                return ks.getKey(aliasEconsig, storepass.toCharArray());
            }
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String encryptKey(byte[] plainKey) {
        // A chave pública RSA é usada para criptografar a chave AES
        final Key publicKey = getRSAKey(true);
        if (publicKey != null) {
            return RSA.encrypt(RSA.encodeBASE64(plainKey), publicKey);
        }
        return null;
    }

    public static byte[] decryptKey(String cryptedKey) {
        try {
            // A chave privada RSA é usada para descriptografar a chave AES.
            final Key privateKey = getRSAKey(false);
            if (privateKey != null) {
                return RSA.decodeBASE64(RSA.decrypt(cryptedKey, privateKey));
            }
        } catch (BadPaddingException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }
}
