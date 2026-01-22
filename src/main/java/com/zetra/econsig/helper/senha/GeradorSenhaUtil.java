package com.zetra.econsig.helper.senha;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PasswordUtil</p>
 * <p>Description: Métodos utilitários para senha</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class GeradorSenhaUtil {
    private static final char [] CARACTERES = {'0','1','2','3','4','5','6','7','8','9',
            'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','x','z','y','w',
            '!','?','@','#','$','%','(',')','[',']','{','}','<','>','&','*','_','-',':',';','/','|','\\'};

    /**
     * 
     * @param passwordSize
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     * Obs.: A chamada de instância "SecureRandom.getInstance("SHA1PRNG","SUN");", apesar de já estar obsoleta e usar o "SHA1",
     * não caracteriza uma vulnerabilidade porque é usada apenas para gerar um "aleatório" para pegar um "char" dentro de um "array",
     * e não criptografa nada.
     */
    public static String getPassword(int passwordSize, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            passwordSize = Math.min(passwordSize, 12);

            final boolean withSymbols = SenhaHelper.getPwdStrength(tipoEntidade, responsavel) > 3;
            final int numMaxSymbols = (withSymbols ? 1 : 0);
            final int numMaxConsoantes = Math.round(passwordSize * 0.4f);
            final int numMaxNumeros = passwordSize - numMaxConsoantes - numMaxSymbols;
            int consoantes = 0;
            int numeros = 0;
            int symbols = 0;
            String password = "";
            char caractere = ' ';
            final Pattern patternNumerico = Pattern.compile(SenhaHelper.numericSequencePattern, Pattern.CASE_INSENSITIVE);
            final Pattern patternAlfabetico = Pattern.compile(SenhaHelper.alphabeticSequencePattern, Pattern.CASE_INSENSITIVE);
            SecureRandom aleatorio = SecureRandom.getInstance("SHA1PRNG","SUN");
            while (passwordSize > 0) {
                caractere = getRandomChar(aleatorio);
                if (Character.isLetter(caractere)) {
                    consoantes++;
                    if (consoantes <= numMaxConsoantes) {
                        if (password.matches(".*[a-zA-Z]{2}$")) {
                            Matcher matcher = patternAlfabetico.matcher(password + caractere);
                            if (matcher.find() || ((password + caractere).matches(".*(.)\\1{2}"))) {
                                caractere = getAnother(caractere, aleatorio);
                            }
                        }
                        password = password + (withSymbols && consoantes == 1 ? Character.toUpperCase(caractere) : caractere);
                        passwordSize--;
                    }
                } else if (Character.isDigit(caractere)) {
                    numeros++;
                    if (numeros <= numMaxNumeros) {
                        if (password.matches(".*\\d{2}")) {
                            Matcher matcher = patternNumerico.matcher(password + caractere);
                            if (matcher.find() || ((password + caractere).matches(".*(.)\\1{2}"))) {
                                caractere = getAnother(caractere, aleatorio);
                            }
                        }
                        password = password + caractere;
                        passwordSize--;
                    }
                } else if (withSymbols) {
                    symbols++;
                    if (symbols <= numMaxSymbols) {
                        password = password + caractere;
                        passwordSize--;
                    }
                }
            }
            return password;
        } catch (Exception ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    public static String getPasswordNumber(int passwordSize, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final StringBuilder password = new StringBuilder();
            final SecureRandom aleatorio = SecureRandom.getInstance("SHA1PRNG","SUN");
            while (passwordSize > 0) {
                password.append(getRandomNumber(aleatorio));
                passwordSize--;
            }

            return password.toString();
        } catch (Exception ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    private static char getRandomChar(SecureRandom aleatorio) throws UsuarioControllerException {
        try {
            final int posicao = aleatorio.nextInt(CARACTERES.length);
            return CARACTERES[posicao];
        } catch (Exception ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    private static int getRandomNumber(SecureRandom aleatorio) throws UsuarioControllerException {
        try {
            return aleatorio.nextInt(10);
        } catch (Exception ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    private static char getAnother(char caractere, SecureRandom aleatorio) throws UsuarioControllerException{
        char novoCaractere;
        do {
            novoCaractere = getRandomChar(aleatorio);
        }
        while ((Character.getType(caractere) != Character.getType(novoCaractere)) || (novoCaractere == caractere));
        return novoCaractere;
    }
}
