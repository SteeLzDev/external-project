package com.zetra.econsig.helper.solicitacaosuporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoSuporteConfig {

    @Value("${suporte.solicitacao.sistema}")
    private String sistemaSolicitacao;

    @Value("${suporte.solicitacao.auth.user}")
    private String authUser;

    @Value("${suporte.solicitacao.auth.password}")
    private String authPassword;

    @Value("${suporte.solicitacao.base.url}")
    private String baseurl;

    @Value("${suporte.solicitacao.base.http.port}")
    private String baseHttpPort;

    @Value("${suporte.solicitacao.mobile.sosServico}")
    private String mobileSosServico;

    @Value("${suporte.solicitacao.versao.jira}")
    private String versaoJira;

    public String getSistemaSolicitacao() {
        return sistemaSolicitacao;
    }

    public String getAuthUser() {
        return authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public String getBaseHttpPort() {
        return baseHttpPort;
    }

    public String getMobileSosServico() {
        return mobileSosServico;
    }

    public String getVersaoJira() {
        return versaoJira;
    }
}
