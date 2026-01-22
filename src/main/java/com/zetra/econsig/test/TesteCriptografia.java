package com.zetra.econsig.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;

import com.zetra.econsig.helper.arquivo.FileHelper;

/**
 * <p>Title: TesteCriptografia</p>
 * <p>Description: Teste de criptografia de arquivo para análise da DESENV-7398. Adaptado de http://www.macs.hw.ac.uk/~ml355/lore/pkencryption.htm.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@SuppressWarnings("all")
public class TesteCriptografia {

    public static final int AES_Key_Size = 256;

    Cipher pkCipher, aesCipher;

    byte[] aesKey;

    SecretKeySpec aeskeySpec;

    /**
     * Constructor: creates ciphers
     */
    public TesteCriptografia() throws GeneralSecurityException {
        // create RSA public key cipher
        pkCipher = Cipher.getInstance("RSA");
        // create AES shared key cipher
        aesCipher = Cipher.getInstance("AES");
    }

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int) privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

//        // read AES key
//        pkCipher.init(Cipher.DECRYPT_MODE, pk);
//        aesKey = new byte[AES_Key_Size / 8];
//        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
//        is.read(aesKey);
//        aeskeySpec = new SecretKeySpec(aesKey, "AES");

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size / 8];
        CipherInputStream is = new CipherInputStream(new ByteArrayInputStream(Base64.decodeBase64(FileHelper.readAll(in.getAbsolutePath()))), pkCipher);
        is.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
}

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int) publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new Base64OutputStream(new FileOutputStream(out)), pkCipher);
        os.write(aesKey);
        os.close();
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }

    public static byte[] getRandomBytes(int n) {
        char[] pw = new char[n];
        int c  = 'A';
        int  r1 = 0;
        for (int i=0; i < n; i++) {
            r1 = (int)(Math.random() * 3);
            switch(r1) {
                case 0: c = '0' +  (int)(Math.random() * 10); break;
                case 1: c = 'a' +  (int)(Math.random() * 26); break;
                case 2: c = 'A' +  (int)(Math.random() * 26); break;
            }
            pw[i] = (char)c;
        }

        return new String(pw).getBytes();
    }

    public static void main(String[] args) {
        File dir = new File("/home/eConsig/arquivos/temp/");
        File encryptedKeyFile64 = new File(dir, "chave.e64");
        File publicKeyFile = new File(dir, "public.der");
        File privateKeyFile = new File(dir, "private.der");

        int size = 1024;
        int runs = 10;
        int n = 1;
        try {
            TesteCriptografia secure = new TesteCriptografia();
            for (int i=1; i<=20; i++) {
                long timeMakeKey = 0;
                long timeSaveKey = 0;
                long timeEncrypt = 0;
                long timeLoadKey = 0;
                long timeDecrypt = 0;

                System.out.println("Teste: " + i);
                String fileName = "teste-" + (i<10? "0" : "") + i;
                File fileToEncrypt = new File(dir, fileName + ".txt");
                File encryptedFile = new File(dir, fileName + ".txt.cry");
                File unencryptedFile = new File(dir, fileName + ".txt.plain");

                long beginFileCreation = Calendar.getInstance().getTimeInMillis();
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileToEncrypt))) {
                    for (int k=0; k<n; k++) {
                        out.write(getRandomBytes(size), 0, size);
                        out.write(10);
                    }
                    n *= 2;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                long endFileCreation = Calendar.getInstance().getTimeInMillis();
                long timeFileCreation = endFileCreation - beginFileCreation;
                long encryptedFileSize = 0;
                for (int j=0; j < runs; j++) {
                    System.out.print("-");
                    // to encrypt a file
                    long beginMakeKey = Calendar.getInstance().getTimeInMillis();
                    secure.makeKey();
                    long endMakeKey = Calendar.getInstance().getTimeInMillis();
                    long beginSaveKey = Calendar.getInstance().getTimeInMillis();
                    secure.saveKey(encryptedKeyFile64, publicKeyFile);
                    long endSaveKey = Calendar.getInstance().getTimeInMillis();
                    long beginEncrypt = Calendar.getInstance().getTimeInMillis();
                    secure.encrypt(fileToEncrypt, encryptedFile);
                    long endEncrypt = Calendar.getInstance().getTimeInMillis();

                    // to decrypt it again
                    long beginLoadKey = Calendar.getInstance().getTimeInMillis();
                    secure.loadKey(encryptedKeyFile64, privateKeyFile);
                    long endLoadKey = Calendar.getInstance().getTimeInMillis();
                    long beginDecrypt = Calendar.getInstance().getTimeInMillis();
                    secure.decrypt(encryptedFile, unencryptedFile);
                    long endDecrypt = Calendar.getInstance().getTimeInMillis();

                    timeMakeKey += endMakeKey - beginMakeKey;
                    timeSaveKey += endSaveKey - beginSaveKey;
                    timeEncrypt += endEncrypt - beginEncrypt;
                    timeLoadKey += endLoadKey - beginLoadKey;
                    timeDecrypt += endDecrypt - beginDecrypt;
                    encryptedFileSize += encryptedFile.length();
                }
                encryptedFileSize = encryptedFileSize / runs;
                long overhead = encryptedFileSize - fileToEncrypt.length();
                System.out.println("");
                System.out.println("Arquivo..................: " + fileToEncrypt.getName());
                System.out.println("Tamnho do arquivo original (B/MB): " + fileToEncrypt.length() + " / " + (double)(fileToEncrypt.length() / 1048576));
                System.out.println("Tamnho do arquivo criptografado (B/MB): " + encryptedFileSize + " / " + (double)(encryptedFileSize / 1048576));
                System.out.println("Tempo de criação do arquivo (ms): " + timeFileCreation);
                System.out.println("Número de execuções...................: " + runs);
                System.out.println("Overhead (diferença) no tamanho (B/MB): " + overhead + " / " + (double)(overhead / 1048576));
                System.out.println("Tempo médio de criação da chave (ms)..: " + timeMakeKey / runs);
                System.out.println("Tempo médio de salvar a chave (ms)....: " + timeSaveKey / runs);
                System.out.println("Tempo médio de criptografar (ms)......: " + timeEncrypt / runs);
                System.out.println("Tempo médio de leitura da chave (ms)..: " + timeLoadKey / runs);
                System.out.println("Tempo médio de descriptografar (ms)...: " + timeDecrypt / runs);
                System.out.println("");
            }
        } catch (GeneralSecurityException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
