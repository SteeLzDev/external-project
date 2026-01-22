package com.zetra.econsig.service.notificacao;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.NotificacaoEmailControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.NotificacaoEmail;
import com.zetra.econsig.persistence.entity.NotificacaoEmailHome;
import com.zetra.econsig.persistence.query.notificacao.ListaNotificacaoEmailQuery;
import com.zetra.econsig.persistence.query.notificacao.ListaTipoNotificacaoQuery;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: NotificacaoEmailControllerBean</p>
 * <p>Description: Implementação session bean de NotificacaoEmail.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class NotificacaoEmailControllerBean implements NotificacaoEmailController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NotificacaoEmailControllerBean.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Override
    public List<TransferObject> lstNotificacoes(TransferObject criterio, AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        try {
            ListaNotificacaoEmailQuery query = new ListaNotificacaoEmailQuery(criterio);
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new NotificacaoEmailControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String criarNotificacao(String tnoCodigo, String nemDestinatario, String nemTitulo, String nemTexto, Date nemData, Date nemDataEnvio, AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        try {
            String funCodigo = responsavel.getFunCodigo();
            String usuCodigo = responsavel.getUsuCodigo();
            NotificacaoEmail notificacao = NotificacaoEmailHome.create(usuCodigo, funCodigo, tnoCodigo, nemDestinatario, nemTitulo, nemTexto, nemData, nemDataEnvio);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.NOTIFICACAO_EMAIL, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setNotificacaoEmail(notificacao.getNemCodigo());
            logDelegate.setUsuario(usuCodigo);
            logDelegate.setFuncao(funCodigo);
            logDelegate.setTipoNotificacao(tnoCodigo);
            logDelegate.write();

            return notificacao.getNemCodigo();

        } catch (LogControllerException ex) {
            throw new NotificacaoEmailControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (com.zetra.econsig.exception.CreateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new NotificacaoEmailControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     * Envia as notificações filtradas pelos parâmetros abaixo para os respectivos dispositivos
     * @param ndiCodigo
     * @param funCodigo
     * @param usuCodigoDestinatario
     * @param tnoCodigo
     * @param ndiAtivo
     * @param responsavel
     * @throws NotificacaoEmailControllerException
     */
    @Override
    public void enviarNotificacao(AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        try {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.NEM_DATA_ENVIO, CodedValues.IS_NULL_KEY);
            List<TransferObject> emails = lstNotificacoes(criterio, responsavel);

            // No email diário consolidar as mensagens por tipo de notificação e destinatário
            Map<String, List<TransferObject>> map = agrupaEmails(emails);
            List<String> nemCodigos = null;

            if (map != null) {
                MailHelper sender = new MailHelper();

                // Titulo Será nome do sistema e nome do consignante
                String cseNome = getCseNome(responsavel);
                String subject = JspHelper.getNomeSistema(responsavel) + " - " + cseNome;

                List<String> conteudos = null;

                for (String key : map.keySet()) {
                    List<TransferObject> valor = map.get(key);
                    String to = null;
                    String cc = null;
                    String bcc = null;
                    StringBuilder conteudo = new StringBuilder("");
                    List<String> anexos = null;
                    Map<String, String> customHeaders = null;
                    String nemTexto = null;

                    nemCodigos = new ArrayList<>();
                    conteudos = new ArrayList<>();
                    for (TransferObject email : valor) {
                        nemCodigos.add(email.getAttribute(Columns.NEM_CODIGO).toString());
                        to = email.getAttribute(Columns.NEM_DESTINATARIO).toString();
                        nemTexto = email.getAttribute(Columns.NEM_TEXTO).toString();

                        // Comparar o texto do corpo para evitar duplicidade
                        if (conteudos.contains(nemTexto)) {
                            continue;
                        }

                        conteudos.add(nemTexto);

                        conteudo.append("<br><br><br>");
                        conteudo.append(email.getAttribute(Columns.NEM_TITULO).toString());
                        conteudo.append("<br><br>");
                        conteudo.append(nemTexto);
                    }

                    try {
                        // O processo automático dispara o envio das mensagens e registra a data-hora de envio
                        sender.send(to, cc, bcc, subject, conteudo.toString(), anexos, customHeaders);
                        registrarEnvioNotificacoes(nemCodigos, responsavel);
                    } catch (MessagingException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }

        } catch (ZetraException e) {
            LOG.error(e.getMessage(), e);
            throw new NotificacaoEmailControllerException(e.getMessageKey(), responsavel, e);
        }

    }

    /**
     * Atualiza a data de envio da notificação de email
     * @param nemCodigo
     * @param responsavel
     * @throws NotificacaoEmailControllerException
     */
    @Override
    public void registrarEnvioNotificacao(String nemCodigo, AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        try {
            NotificacaoEmail notificacao = NotificacaoEmailHome.findByPrimaryKey(nemCodigo);
            notificacao.setNemDataEnvio(DateHelper.getSystemDatetime());

            NotificacaoEmailHome.update(notificacao);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.NOTIFICACAO_EMAIL, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setNotificacaoDispostivo(notificacao.getNemCodigo());
            logDelegate.write();

        } catch (LogControllerException ex) {
            throw new NotificacaoEmailControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (FindException ex) {
            throw new NotificacaoEmailControllerException("mensagem.erro.notificacao.dispositivo.nao.encontrada", responsavel, ex);
        } catch (UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new NotificacaoEmailControllerException("mensagem.erro.notificacao.dispositivo.falha.atualizar", responsavel, ex);
        }
    }

    /**
     * Atualiza a data de envio das notificações de email
     * @param nemCodigos
     * @param responsavel
     * @throws NotificacaoEmailControllerException
     */
    private void registrarEnvioNotificacoes(List<String> nemCodigos, AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        for (String nemCodigo : nemCodigos) {
            registrarEnvioNotificacao(nemCodigo, responsavel);
        }
    }


    /**
     * Lista tipos de notificação
     * @param responsavel
     * @return Retorna uma lista de tipos de notificação
     * @throws NotificacaoEmailControllerException
     */
    @Override
    public List<TransferObject> lstTipoNotificacao(AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        try {
            ListaTipoNotificacaoQuery query = new ListaTipoNotificacaoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new NotificacaoEmailControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Obtém o nome do consignante para envio de e-mail
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    private final String getCseNome(AcessoSistema responsavel) throws ViewHelperException {
        try {
            ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            return cse.getCseNome();
        } catch (ConsignanteControllerException e) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Agrupa por emails por tipo de notificação e destinatário
     * @param emails
     * @return
     */
    protected Map<String, List<TransferObject>> agrupaEmails(List<TransferObject> emails) {
        Map<String, List<TransferObject>> map = null;
        if (emails != null && !emails.isEmpty()) {
            map = new HashMap<>();
            String chave = null;
            for (TransferObject email : emails) {
                chave = email.getAttribute(Columns.NEM_TNO_CODIGO).toString() + ";" + email.getAttribute(Columns.NEM_DESTINATARIO).toString();

                List<TransferObject> valor = map.get(chave);
                if (valor == null) {
                    valor = new ArrayList<>();
                    map.put(chave, valor);
                }

                valor.add(email);
            }
        }
        return map;
    }

}
