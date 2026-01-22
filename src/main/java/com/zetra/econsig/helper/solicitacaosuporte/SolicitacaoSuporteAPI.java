package com.zetra.econsig.helper.solicitacaosuporte;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SolicitacaoSuporteAPIException;
import com.zetra.jira.bean.Issue;
import com.zetra.jira.exception.JiraException;

public interface SolicitacaoSuporteAPI {

    public Map<String, String> getValoresCampos(String campo) throws SolicitacaoSuporteAPIException, IOException;

    public String criarSolicitacaoSuporte(TransferObject campos) throws SolicitacaoSuporteAPIException, IOException;

    public TransferObject findByChave(String chave) throws SolicitacaoSuporteAPIException, IOException;

    public List<Issue> getIssueListByLogin(String valor, String paramIniPeriodo, String paramFimPeriodo, String cseIdInterno) throws SolicitacaoSuporteAPIException, IOException, AuthenticationException, JiraException, ParseException;

    public List<Issue> getIssueList(String grupo, String paramIniPeriodo, String paramFimPeriodo, String cseIdInterno) throws SolicitacaoSuporteAPIException, IOException, AuthenticationException, JiraException;

}
