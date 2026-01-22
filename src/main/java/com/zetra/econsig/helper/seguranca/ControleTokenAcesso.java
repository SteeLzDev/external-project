package com.zetra.econsig.helper.seguranca;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ControleTokenAcesso</p>
 * <p>Description: Faz a gestão do tokens de acesso.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ControleTokenAcesso {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleTokenAcesso.class);

    private long ultimaLimpeza = Calendar.getInstance().getTimeInMillis();
    // Intervalo de tempo, em milisegundos, para realizar limpeza do cache
    private static final long intervaloLimpeza = 1000 * 60 * 60;
    private static final int QTDE_CONSULTAS_TOKEN = 2;

    private static ControleTokenAcesso singleton;
    private final Map<String, Token> cache;
    private final KeyPair keyPair;

    static {
        singleton = new ControleTokenAcesso();
    }

    private ControleTokenAcesso() {
        cache = new HashMap<String, Token>();
        keyPair = RSA.generateKeyPair(CodedValues.RSA_KEY_SIZE);
    }

    public static ControleTokenAcesso getInstance() {
        return singleton;
    }

    public synchronized String getToken(String rseCodigo, List<String> csaCodigos, String ip) {
        long agora = Calendar.getInstance().getTimeInMillis();

        if (agora > (ultimaLimpeza + intervaloLimpeza)) {
            ultimaLimpeza = agora;
            limpaCache();
        }

        excluiTokenUsuario(rseCodigo);

        // Token deve ser criptografado.
        String token = rseCodigo.concat(String.valueOf(agora));
        token = RSA.encrypt(token, keyPair.getPublic());
        token = token.replaceAll("\n", "").replaceAll("\r", "");

        Token t = new Token(rseCodigo, csaCodigos, token, ip);
        cache.put(token, t);

        return token;
    }

    private synchronized void excluiTokenUsuario(String rseCodigo) {
        Iterator<Token> ite = cache.values().iterator();
        while (ite.hasNext()) {
            Token o = ite.next();
            if (o.getRseCodigo().equals(rseCodigo)) {
                removeToken(o);
            }
        }
    }

    public synchronized void validarToken(String rseCodigo, String csaCodigo, String token) throws ZetraException {
        Token t = cache.get(token);
        if (t == null || !t.getRseCodigo().equals(rseCodigo)) {
            ZetraException ex = new ZetraException("mensagem.tokenInvalido", (AcessoSistema) null);
            throw ex;
        } else if (!t.getCsaCodigos().contains(csaCodigo)) {
            ZetraException ex = new ZetraException("mensagem.tokenNaoPertenceCsa", (AcessoSistema) null);
            throw ex;
        } else if (t.expirado()) {
            ZetraException ex = new ZetraException("mensagem.tokenExpirado", (AcessoSistema) null);
            throw ex;
        }
    }

    public synchronized void incrementarUtilizacaoToken(String token) {
        Token t = cache.get(token);
        if (t != null) {
            t.incrementaUtilizacao();
        }
    }

    public synchronized void invalidarToken(String token) {
        Token t = cache.get(token);
        if (t != null) {
            t.invalidar();
        }
    }

    private synchronized void limpaCache() {
        LOG.info("Iniciando limpeza do cache de tokens expirados.");
        List<Token> expirado = new ArrayList<Token>();

        Iterator<Token> ite = cache.values().iterator();
        while (ite.hasNext()) {
            Token t = ite.next();
            if (t.expirado()) {
                expirado.add(t);
            }
        }

        ite = expirado.iterator();
        while (ite.hasNext()) {
            // Remove token expirado do cache
            String chave  = ite.next().getToken();
            removeToken(chave);
        }
        LOG.info("Finalizando limpeza do cache de tokens expirados.");
    }

    private synchronized Token removeToken(Object chave) {
        if (chave instanceof Token) {
            return cache.remove(((Token)chave).getToken());
        }
        return cache.remove(chave);
    }

    class Token {
        private final String rseCodigo;
        private final List<String> csaCodigos;
        private final String token;
        private final String ip;
        private final Date dataCriacao;
        private int qtdeUtilizacao;

        public Token(String rseCodigo, List<String> csaCodigos, String token, String ip) {
            this.rseCodigo = rseCodigo;
            this.csaCodigos = csaCodigos;
            this.token = token;
            this.ip = ip;
            dataCriacao = DateHelper.getSystemDatetime();
            qtdeUtilizacao = 0;
        }

        public List<String> getCsaCodigos() {
            return csaCodigos;
        }

        public String getToken() {
            return token;
        }

        public String getRseCodigo() {
            return rseCodigo;
        }

        public Date getDataCriacao() {
            return dataCriacao;
        }

        public String getIp() {
            return ip;
        }

        public int getQtdeUtilizacao() {
            return qtdeUtilizacao;
        }

        public void incrementaUtilizacao() {
            qtdeUtilizacao++;
        }

        public void invalidar() {
            qtdeUtilizacao = QTDE_CONSULTAS_TOKEN;
        }

        public boolean expirado() {
            long dataCriacao = this.dataCriacao.getTime();
            long agora = Calendar.getInstance().getTimeInMillis();

            // Se o tempo da criação somado ao intervalo de limpeza for menor que agora
            // ou a quantidade de utilizações do mesmo for maior que o permitido o token está expirado
            if ((agora > (dataCriacao + intervaloLimpeza)) || (qtdeUtilizacao >= QTDE_CONSULTAS_TOKEN)) {
                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((csaCodigos == null) ? 0 : csaCodigos.hashCode());
            result = prime * result + ((dataCriacao == null) ? 0 : dataCriacao.hashCode());
            result = prime * result + ((ip == null) ? 0 : ip.hashCode());
            result = prime * result + qtdeUtilizacao;
            result = prime * result + ((rseCodigo == null) ? 0 : rseCodigo.hashCode());
            result = prime * result + ((token == null) ? 0 : token.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Token) {
                Token outro = (Token) obj;
                if (rseCodigo.equals(outro.getRseCodigo()) && csaCodigos.equals(outro.getCsaCodigos())) {
                    return true;
                }
            }

            return false;
        }

        private ControleTokenAcesso getOuterType() {
            return ControleTokenAcesso.this;
        }
    }
}
