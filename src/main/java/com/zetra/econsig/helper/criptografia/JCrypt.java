package com.zetra.econsig.helper.criptografia;

import com.zetra.econsig.helper.criptografia.SaltedCryptographicHash.Algorithm;

/**
 * <p>Title: JCrypt</p>
 * <p>Description: Classe para centralizar a verificação de hash de senhas</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class JCrypt {

    public static String crypt(String passwordToHash) {
        return crypt(null, passwordToHash);
    }

    public static String crypt(String salt, String passwordToHash) {
        // Para mudar o algoritmo padrão é necessário revisar o tamanho dos campos de senha, senha 2, senha nova,
        // código OTP, tabela de senhas de autorização, tabela de senhas anteriores, para comportar o código HASH
        return new SaltedCryptographicHash(Algorithm.ARGON2ID).crypt(salt, passwordToHash);
    }

    public static Boolean verificaSenha(String senhaAberta, String senha) {
        return verificaSenha(senhaAberta, senha, null);
    }

    public static Boolean verificaSenha(String senhaAberta, String senha, String salto) {
        if (senha == null || senhaAberta == null) {
            return false;
        }

        if (senha.length() == 13) { // crypt
            if (salto == null) {
                salto = senha.substring(0, 2);
            }
            final String senhaCrypt = JCryptOld.crypt(salto, senhaAberta);
            final String senhaCryptUCase = JCryptOld.crypt(salto, senhaAberta.toUpperCase());
            final String senhaCryptLCase = JCryptOld.crypt(salto, senhaAberta.toLowerCase());
            return !senhaCrypt.equals("") && senhaCrypt.equals(senha) ||
                   !senhaCryptUCase.equals("") && senhaCryptUCase.equals(senha) ||
                   !senhaCryptLCase.equals("") && senhaCryptLCase.equals(senha);

        } else {
            final Algorithm algorithm = SaltedCryptographicHash.getAlgorithm(senha.length());
            if (algorithm != null) {
                return new SaltedCryptographicHash(algorithm).check(senhaAberta, senha);
            }
        }
        return false;
    }
}
