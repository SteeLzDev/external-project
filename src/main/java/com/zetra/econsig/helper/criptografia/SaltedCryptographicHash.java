package com.zetra.econsig.helper.criptografia;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/**
 * <p>Title: SaltedCryptographicHash</p>
 * <p>Description: Classe para geração e verificação de senhas com múltiplas
 * implementações de Hash com salt dinâmico de 4 bytes</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaltedCryptographicHash {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SaltedCryptographicHash.class);

    public static final int SALT_SIZE = 8;

    private final Algorithm algorithm;

    public SaltedCryptographicHash(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Boolean check(String plain, String encrypted) {
        final String salt = encrypted.substring(algorithm.hashSize);
        final String crypt = crypt(salt, plain);
        final String cryptUCase = crypt(salt, plain.toUpperCase());
        final String cryptLCase = crypt(salt, plain.toLowerCase());

        return !crypt.equals("") && crypt.equals(encrypted) ||
               !cryptUCase.equals("") && cryptUCase.equals(encrypted) ||
               !cryptLCase.equals("") && cryptLCase.equals(encrypted);
    }

    public String crypt(String passwordToHash) {
        return crypt(null, passwordToHash, 1);
    }

    public String crypt(String salt, String passwordToHash) {
        return crypt(salt, passwordToHash, 1);
    }

    private String crypt(String salt, String passwordToHash, int iteration) {
        String generatedPassword = null;

        try {
            if (salt == null) {
                salt = getSalt(SALT_SIZE + algorithm.iterations);
            }

            if (Algorithm.ARGON2ID.equals(algorithm)) {
                final Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                                                                       .withVersion(algorithm.version)
                                                                       .withIterations(algorithm.iterations)
                                                                       .withMemoryAsKB(algorithm.memory)
                                                                       .withParallelism(algorithm.parallelism)
                                                                       .withSalt(Strings.toByteArray(salt));
                //
                // Set the password.
                //
                final Argon2BytesGenerator gen = new Argon2BytesGenerator();
                gen.init(builder.build());
                final byte[] result = new byte[48];
                gen.generateBytes(passwordToHash.toCharArray(), result, 0, result.length);
                generatedPassword = Base64.getEncoder().encodeToString(result) + salt;
                Arrays.clear(result);

            } else {
                // Put bouncycastle as SecurityProvider
                SecurityProvider.getInstance();
                // Create MessageDigest instance
                final MessageDigest md = MessageDigest.getInstance(algorithm.name);
                // Add password bytes to digest
                md.update(salt.getBytes());
                // Get the hash's bytes
                final byte[] bytes = md.digest(passwordToHash.getBytes());
                // This bytes[] has bytes in decimal format;
                // Convert it to hexadecimal format
                final StringBuilder sb = new StringBuilder();
                for (final byte b : bytes) {
                    sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                }
                sb.append(salt);
                // Get complete hashed password in hex format
                // Hash the result SALT_SIZE times
                generatedPassword = iteration < algorithm.iterations ? crypt(salt, sb.toString(), iteration + 1) : sb.toString();
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return generatedPassword;
    }

    private static String getSalt(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
        // Always use a SecureRandom generator
        final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        // Create array for salt
        final byte[] array = new byte[size / 2];
        // Get a random array
        sr.nextBytes(array);
        final BigInteger bi = new BigInteger(1, array);
        String salt = bi.toString(16);
        // pad 0 a esquerda (exemplo, se o byte vale 4, em hex=04, menos se estiver na primeira
        // posicao do array
        if (salt.length() % 2 == 1) {
            salt = "0" + salt;
        }
        // Algumas vezes o array não vem completo o que gera um salt menor
        // como solucao eu chamo recursivamente
        if (salt.length() < 2 * array.length) {
            return getSalt(size);
        } else {
            return salt;
        }
    }

    public static Algorithm getAlgorithm(int passwordSize) {
        for (final Algorithm algorithm : Algorithm.values()) {
            final int diff = passwordSize - (algorithm.hashSize + SALT_SIZE);
            if (diff == 0 || diff == algorithm.iterations) {
                return algorithm;
            }
        }
        return null;
    }

    public enum Algorithm {
        MD5("MD5", 32, 0, 0, 0, 0),
        //SHA3_256("SHA3-256", 64),
        //SHA3_384("SHA3-384", 96),
        //SHA3_512("SHA3-512", 128),
        KECCAK_512("KECCAK-512", 128, 0, 0, 0, 0),
        KECCAK_512_RECURSIVE("KECCAK-512", 128, 0, 8, 0, 0),
        //Skein_1024("Skein-1024-1024", 256),
        ARGON2ID("ARGON2ID", 64, Argon2Parameters.ARGON2_VERSION_13, 4, 32, 2)
        ;

        /*
        --> https://github.com/rtyley/spongycastle/issues/23
        So, here is the full story,
        There are two versions of SHA3:
        SHA3: The 1st implementation made by the Keccak team's by 2012
        SHA3: The 2nd version which is an updated made by NIST by 2014 and turned to be the official standard till nowadays.
        Here is the link to the announcement of the National Institute of Standards and Technology:
        https://www.federalregister.gov/documents/2014/05/28/2014-12336/announcing-draft-federal-information-processing-standard-fips-202-sha-3-standard-permutation-based

        Bouncy castle followed the standard and updated it implementation to the FIST202 in the version 53, the previous implementation was kept but callable via the "KECCAK-256"
        algorithm, here his how to call it from the BouncyCastle/SpongyCastle 53 and above:
        */

        private String name;
        private int hashSize;
        private int version;
        private int iterations;
        private int memory;
        private int parallelism;

        private Algorithm(String name, int hashSize, int version, int iterations, int memory, int parallelism) {
            this.name =  name;
            this.hashSize = hashSize;
            this.version = version;
            this.iterations = iterations;
            this.memory = memory;
            this.parallelism = parallelism;
        }
    }
}
