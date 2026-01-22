package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AcessoTransferObject</p>
 * <p>Description: Transfer Object de Acesso (Informações para os comandos do centralizador de login).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcessoTransferObject extends CustomTransferObject {

    private static final long serialVersionUID = 6671148779126662485L;

    protected static final String CHAVE_ACESSO_COMANDO = "acesso.comando";
    protected static final String CHAVE_URI_ACESSO = "uri.acesso";
    protected static final String CHAVE_VERSAO = "acesso.versao";
    protected static final String CHAVE_TIMESTAMP_CENTRALIZADOR = "timestamp.centralizador";
    protected static final String CHAVE_TIMESTAMP_ECONSIG = "timestamp.econsig";
    protected static final String CHAVE_RESULTADO = "acesso.resultado";
    protected static final String CHAVE_REMOTE_PORT_ACESSO = "remote.port.acesso";

    public final String VERSAO = "2.0";

    public AcessoTransferObject() {
        super();
        setAttribute(CHAVE_VERSAO, VERSAO);
    }

    public AcessoTransferObject(String login, String senha) {
        this();
        setAttribute(Columns.USU_LOGIN, login);
        setAttribute(Columns.USU_SENHA, senha);
    }

    public AcessoTransferObject(AcessoTransferObject acesso) {
        this();
        setAtributos(acesso.getAtributos());
    }

    // Getter
    public String getLogin() {
        return (String) getAttribute(Columns.USU_LOGIN);
    }

    public String getSenha() {
        return (String) getAttribute(Columns.USU_SENHA);
    }

    public String getComando() {
        return (String) getAttribute(CHAVE_ACESSO_COMANDO);
    }

    public String getConsignataria() {
        return (String) getAttribute(Columns.CSA_IDENTIFICADOR_INTERNO);
    }

    public String getURIAcesso () {
        return (String) getAttribute(CHAVE_URI_ACESSO);
    }
    
    public Integer getRemotePortAcesso () {
        return (Integer) getAttribute(CHAVE_REMOTE_PORT_ACESSO);
    }
    
    public String getVersao() {
        return (String) getAttribute(CHAVE_VERSAO);
    }

    public String getTimestampCentralizador() {
        return (String) getAttribute(CHAVE_TIMESTAMP_CENTRALIZADOR);
    }

    public String getTimestampEconsig() {
        return (String) getAttribute(CHAVE_TIMESTAMP_ECONSIG);
    }

    public String getResultado() {
        return (String) getAttribute(CHAVE_RESULTADO);
    }

    // Setter
    public void setComando(String comando) {
        setAttribute(CHAVE_ACESSO_COMANDO, comando);
    }

    public void setSenha(String senha) {
        setAttribute(Columns.USU_SENHA, senha);
    }

    public void setConsignataria(String consignataria) {
        setAttribute(Columns.CSA_IDENTIFICADOR_INTERNO, consignataria);
    }

    public void setURIAcesso(String uri) {
        setAttribute(CHAVE_URI_ACESSO, uri);
    }
    
    public void setRemotePortAcesso(Integer port) {
        setAttribute(CHAVE_REMOTE_PORT_ACESSO, port);
    }

    public void setVersao(String versao) {
        setAttribute(CHAVE_VERSAO, versao);
    }

    public void setTimestampCentralizador(String timestamp) {
        setAttribute(CHAVE_TIMESTAMP_CENTRALIZADOR, timestamp);
    }

    public void setTimestampEconsig(String timestamp) {
        setAttribute(CHAVE_TIMESTAMP_ECONSIG, timestamp);
    }

    public void setResultado(String resultado) {
        setAttribute(CHAVE_RESULTADO, resultado);
    }

    // Equals
    @Override
    public boolean equals(Object acesso) {
        try {
            AcessoTransferObject acessoTO = (AcessoTransferObject) acesso;
            if (acessoTO != null &&
                acessoTO.getLogin().equals(getLogin()) &&
                acessoTO.getSenha().equals(getSenha()) &&
                acessoTO.getComando().equals(getComando())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 37 * result + ( getLogin() == null ? 0 : getLogin().hashCode() );
        result = 37 * result + ( getSenha() == null ? 0 : getSenha().hashCode() );
        result = 37 * result + ( getComando() == null ? 0 : getComando().hashCode() );
        return result;
    }
}
