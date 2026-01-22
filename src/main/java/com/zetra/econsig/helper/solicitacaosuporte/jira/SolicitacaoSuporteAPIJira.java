package com.zetra.econsig.helper.solicitacaosuporte.jira;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SolicitacaoSuporteAPIException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.SolicitacaoSuporteAPI;
import com.zetra.econsig.helper.solicitacaosuporte.SolicitacaoSuporteConfig;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
//import com.sun.jersey.api.client.ClientHandlerException;
import com.zetra.jira.JiraEConsig;
import com.zetra.jira.bean.Issue;
import com.zetra.jira.exception.JiraException;

/**
 * <p> Title: SolicitacaoSuporteAPIJira</p>
 * <p> Description: Integração com o Jira para abertura de chamados</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoSuporteAPIJira implements SolicitacaoSuporteAPI {

    private static final String ID_KEY = "id";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoSuporteAPIJira.class);

    @Override
    public Map<String, String> getValoresCampos(String campo) throws SolicitacaoSuporteAPIException, IOException {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.erro.interno", (AcessoSistema) null, ex);
        }

        final JiraEConsig jiraEConsig = JiraEConsig.getInstance(ssc.getBaseurl(), ssc.getBaseHttpPort(), ssc.getAuthUser(), ssc.getAuthPassword(), ssc.getVersaoJira());
        final Map<String, String> retorno = new LinkedHashMap<>();

        if (TextHelper.isNull(campo)) {
            return null;
        }

        try {
            if (campo.equals(Columns.SOS_SISTEMA_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.sistemaIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_PAPEL_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.papelIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_MOTIVO_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.motivoIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                retorno.remove("id");
                retorno.remove("value");

                return retorno;
            } else if (campo.equals(Columns.SOS_ATENDIMENTO_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.assuntoIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_TIPO_ID_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.tipoIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_PRIORIDADE_ID_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.prioridadeIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_PROJETO_ID_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.projetoIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                return retorno;
            } else if (campo.equals(Columns.SOS_SERVICO_TRANSIENTE)) {
                final List<Map<String, String>> lstMap = jiraEConsig.servicoIdLookUp();

                for (final Map<String, String> mapaCorrente: lstMap) {
                    retorno.putAll(mapaCorrente);
                }

                retorno.remove("id");
                retorno.remove("value");

                return retorno;
            }

        } catch (final AuthenticationException e) {
            LOG.error("ERRO AuthenticationException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.comunicacao.sistema", (AcessoSistema) null, e);
        } catch (final JiraException e) {
            LOG.error("ERRO JiraException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.erro.interno", (AcessoSistema) null, e);
        }
        return null;
    }

    @Override
    public String criarSolicitacaoSuporte(TransferObject campos) throws SolicitacaoSuporteAPIException, IOException {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.erro.interno", (AcessoSistema) null, ex);
        }

        final JiraEConsig jiraEConsig = JiraEConsig.getInstance(ssc.getBaseurl(), ssc.getBaseHttpPort(), ssc.getAuthUser(), ssc.getAuthPassword(), ssc.getVersaoJira());

        try {
            final Issue solicitacao = jiraEConsig.createTaskSuporte((String) campos.getAttribute(Columns.SOS_CLIENTE_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_LOGIN_ECONSIG_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_SISTEMA_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_PAPEL_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_SERVICO_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_ATENDIMENTO_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_SUMARIO),
                    (String) campos.getAttribute(Columns.SOS_DESCRICAO_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_EMAIL_TRANSIENTE),
                    (String) campos.getAttribute(Columns.SOS_DESCSA_TRANSIENTE),
                    (String) campos.getAttribute(Columns.USU_CPF),
                    TextHelper.isNull(campos.getAttribute(Columns.SOS_TELEFONE_TRANSIENTE)) ? null : (String) campos.getAttribute(Columns.SOS_TELEFONE_TRANSIENTE),
                    TextHelper.isNull(campos.getAttribute(Columns.SOS_ARQUIVO)) ? null : (File) campos.getAttribute(Columns.SOS_ARQUIVO),
                    TextHelper.isNull(campos.getAttribute(Columns.SOS_EMAIL_USUARIO_SUPORTE)) ? null : (String) campos.getAttribute(Columns.SOS_EMAIL_USUARIO_SUPORTE),
                    !TextHelper.isNull(campos.getAttribute(Columns.SOS_TOTEM)));

            return solicitacao.getKey();
        } catch (final AuthenticationException e) {
            LOG.error("ERRO AuthenticationException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.criar.solicitacao", (AcessoSistema) null, e);
        } catch (final JiraException e) {
            LOG.error("ERRO JiraException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException(e);
        }
    }

    @Override
    public TransferObject findByChave(String chave) throws SolicitacaoSuporteAPIException, IOException {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.erro.interno", (AcessoSistema) null, ex);
        }

        final JiraEConsig jiraEConsig = JiraEConsig.getInstance(ssc.getBaseurl(), ssc.getBaseHttpPort(), ssc.getAuthUser(), ssc.getAuthPassword(), ssc.getVersaoJira());

        Issue issue = null;
        try {
            issue = jiraEConsig.getIssue(chave);
        } catch (final ParseException e) {
            LOG.error("ERRO ParseException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.recuperar.solicitacao", (AcessoSistema) null, e);
        }

        final TransferObject solicitacao = new CustomTransferObject();

        solicitacao.setAttribute(Columns.SOS_CLIENTE_TRANSIENTE, issue.getFields().getCliente());
        for (final Map<String, Object> sistMap: issue.getFields().getSistemaId()) {
            if (sistMap.containsKey(ID_KEY)) {
                solicitacao.setAttribute(Columns.SOS_SISTEMA_TRANSIENTE, sistMap.get(ID_KEY));
                break;
            }
        }
        solicitacao.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, issue.getFields().getPapelId().get("value"));
        solicitacao.setAttribute(Columns.SOS_SERVICO_TRANSIENTE, issue.getFields().getServicoId().get("value"));
        solicitacao.setAttribute(Columns.SOS_ATENDIMENTO_TRANSIENTE, issue.getFields().getAtendimentoId().get(ID_KEY));
        solicitacao.setAttribute(Columns.SOS_TIPO_ID_TRANSIENTE, issue.getFields().getTipoId().get(ID_KEY));
        solicitacao.setAttribute(Columns.SOS_PRIORIDADE_ID_TRANSIENTE, issue.getFields().getPrioridadeId().get("name"));
        solicitacao.setAttribute(Columns.SOS_PROJETO_ID_TRANSIENTE, issue.getFields().getProjetoId().get(ID_KEY));
        solicitacao.setAttribute(Columns.SOS_SUMARIO, issue.getFields().getSumario());
        solicitacao.setAttribute(Columns.SOS_DESCRICAO_TRANSIENTE, issue.getFields().getDescricao());
        solicitacao.setAttribute(Columns.SOS_EMAIL_TRANSIENTE, issue.getFields().getEmail());
        solicitacao.setAttribute(Columns.SOS_DESCSA_TRANSIENTE, issue.getFields().getDesCsa());
        solicitacao.setAttribute(Columns.SOS_LOGIN_ECONSIG_TRANSIENTE, issue.getFields().getLoginEConsig());
        solicitacao.setAttribute(Columns.SOS_DATA_CADASTRO, issue.getFields().getDataCriacao());
        solicitacao.setAttribute(Columns.SOS_DATA_ATUALIZACAO_TRANSIENTE, issue.getFields().getDataUpdate());
        solicitacao.setAttribute(Columns.SOS_DATA_RESOLUCAO_TRANSIENTE, issue.getFields().getDataResolucao());
        solicitacao.setAttribute(Columns.SOS_SOLUCAO_TRANSIENTE, issue.getFields().getSolucao());
        solicitacao.setAttribute(Columns.SOS_CHAVE, chave);
        final LinkedHashMap<String, String> situacao = issue.getFields().getSituacao();
        if (situacao != null) {
            solicitacao.setAttribute(Columns.SOS_STATUS_TRANSIENTE, situacao.get("name"));
        }
        if (issue.getFields().getResponsavel() != null) {
            solicitacao.setAttribute(Columns.SOS_RESPONSAVEL_TRANSIENTE, issue.getFields().getResponsavel().get("displayName"));
        }
        try {
            solicitacao.setAttribute(Columns.SOS_COMENTARIO_TRANSIENTE, issue.getFields().getComentarios());
        } catch (final java.text.ParseException e) {
            LOG.error("ERRO ParseException NO JIRA: "+e.getMessage());
            throw new SolicitacaoSuporteAPIException("mensagem.erro.jira.recuperar.solicitacao", (AcessoSistema) null, e);
        }
        solicitacao.setAttribute(Columns.SOS_SLA_INDICATOR, issue.getFields().getSlaIdIndicator());

        return solicitacao;
    }


    /**
     * Método que conecta no Jira e recupera uma lista de tarefas filtrando por login.
     * @throws ParseException
     */
    @Override
    public List<Issue> getIssueListByLogin(String valor, String paramIniPeriodo, String paramFimPeriodo, String cseIdInterno) throws IOException, AuthenticationException, JiraException {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            throw new IOException(ex);
        }

        final JiraEConsig jiraEConsig = JiraEConsig.getInstance(ssc.getBaseurl(), ssc.getBaseHttpPort(), ssc.getAuthUser(), ssc.getAuthPassword(), ssc.getVersaoJira());
        return jiraEConsig.getIssueListByLoginCse(valor, paramIniPeriodo, paramFimPeriodo, cseIdInterno);
    }

    /**
     * Método que conecta no Jira e recupera uma lista de tarefas produto "eConsig"
     * do tipo de serviço "Requisição de serviço" (customfield_12931) e que o "AA Sistema eConsig" corresponda à CSE do sistema (cseIdInterno).
     * @throws ParseException
     */
    @Override
    public List<Issue> getIssueList(String grupo, String paramIniPeriodo, String paramFimPeriodo, String cseIdInterno) throws IOException, AuthenticationException, JiraException {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
        } catch (final Exception ex) {
            LOG.error("ERRO JiraException NO JIRA: " + ex.getMessage());
            throw new IOException(ex);
        }

        final JiraEConsig jiraEConsig = JiraEConsig.getInstance(ssc.getBaseurl(), ssc.getBaseHttpPort(), ssc.getAuthUser(), ssc.getAuthPassword(), ssc.getVersaoJira());
        return jiraEConsig.getIssueListSistemaGrupo(grupo, paramIniPeriodo, paramFimPeriodo, cseIdInterno);
    }
}
