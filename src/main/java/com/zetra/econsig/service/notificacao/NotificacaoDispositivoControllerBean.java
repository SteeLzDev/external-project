package com.zetra.econsig.service.notificacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.NotificacaoDispositivoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.notificacao.NotificacaoDispositivoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.NotificacaoDispositivo;
import com.zetra.econsig.persistence.entity.NotificacaoDispositivoHome;
import com.zetra.econsig.persistence.entity.UsuarioChaveDispositivo;
import com.zetra.econsig.persistence.entity.UsuarioChaveDispositivoHome;
import com.zetra.econsig.persistence.query.notificacao.ListaNotificacaoQuery;
import com.zetra.econsig.persistence.query.notificacao.ListaNotificacoesInativasQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: NotificacaoDispositivoControllerBean</p>
 * <p>Description: Implementação session bean de Notificações push a dispositivos.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class NotificacaoDispositivoControllerBean implements NotificacaoDispositivoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NotificacaoDispositivoControllerBean.class);

    @Override
    public List<TransferObject> lstNotificacoes(String funCodigo, String tnoCodigo, boolean ndiAtivo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        final ListaNotificacaoQuery lstNdiQuery = new ListaNotificacaoQuery();

        lstNdiQuery.funCodigo = funCodigo;
        lstNdiQuery.tnoCodigo = tnoCodigo;
        lstNdiQuery.ndiAtivo = ndiAtivo;

        try {
            return lstNdiQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new NotificacaoDispositivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createNotificacaoDispositivo(String usuCodigoOperador, String usuCodigoDestinatario, String funCodigo, String ndiTexto, Date ndiData, Date ndiDataEnvio, Short ndiStatus, String tnoCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        try {
            final NotificacaoDispositivo notificacao = NotificacaoDispositivoHome.create(usuCodigoOperador, usuCodigoDestinatario, funCodigo, ndiTexto, ndiData, ndiDataEnvio, ndiStatus, tnoCodigo);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.NOTIFICACAO_DISPOSITIVO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setNotificacaoDispostivo(notificacao.getNdiCodigo());
            logDelegate.write();

            return notificacao.getNdiCodigo();

        } catch (final LogControllerException ex) {
            throw new NotificacaoDispositivoControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new NotificacaoDispositivoControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     *  envia as notificações filtradas pelos parâmetros abaixo para os respectivos dispositivos
     * @param ndiCodigo
     * @param deviceToken - token único que identifica dispositivo móvel recipiente
     * @param ndiTexto - JSON com dados da entidade a ser tratado pelo código cliente
     * @param mensagemNdi - Corpo da notificação
     * @param tituloNdi - título da notificação
     * @param usuCodigoDestinatario
     * @param tnoCodigo - tipo de notificação
     * @param ndiAtivo
     * @param collapseId
     * @param responsavel
     * @throws NotificacaoDispositivoControllerException
     */
    @Override
    public void enviarNotificacao(String ndiCodigo, String deviceToken, String ndiTexto, String mensagemNdi, String tituloNdi, String usuCodigoDestinatario, String tnoCodigo, boolean ndiAtivo, String collapseId, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        try {
            final List<String> lstTokens = new ArrayList<>();
            lstTokens.add(deviceToken);
            if (NotificacaoDispositivoHelper.enviarNotificacao(lstTokens, ndiTexto, mensagemNdi, tituloNdi, tnoCodigo, collapseId, responsavel)) {
                registrarEnvioNotificacao(ndiCodigo, responsavel);
            }
        } catch (final FindException ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
            throw new NotificacaoDispositivoControllerException("mensagem.erro.notificacao.dispositivo.nao.encontrada", responsavel);
        } catch (final ZetraException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new NotificacaoDispositivoControllerException(e.getMessageKey(), responsavel, e);
        }
    }

    @Override
    public boolean enviarNotificacao(String serCodigo, String titulo, String texto, TipoNotificacaoEnum tipoNotificacao, String funCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        try {
            // Pesquisa o usuário servidor pelo código do servidor
            final ObtemUsuarioServidorQuery usuSerQry = new ObtemUsuarioServidorQuery();
            usuSerQry.serCodigo = serCodigo;
            final List<TransferObject> lstUsuarioSer = usuSerQry.executarDTO();

            for (final TransferObject usuarioSer : lstUsuarioSer) {
                final String usuCodigo = usuarioSer.getAttribute(Columns.USU_CODIGO).toString();
                String chaveDispositivo = null;

                // Verifica se o usuário servidor tem código do dispositivo (deviceToken) habilitado
                try {
                    final UsuarioChaveDispositivo ucd = UsuarioChaveDispositivoHome.findByPrimaryKey(usuCodigo);
                    chaveDispositivo = ucd.getUcdToken();
                } catch (final FindException ex) {
                    // Usuário não tem chave dispositivo, como não é obrigatório não precisa retornar erro
                }

                if (!TextHelper.isNull(chaveDispositivo)) {
                    final String strJsonBody = "{"
                                       + "\"tno_codigo\": \"" + tipoNotificacao.getCodigo() + "\""
                                       + "}";

                    // Cria a notificação
                    final String ndiCodigo = createNotificacaoDispositivo(responsavel.getUsuCodigo(), usuCodigo, funCodigo, strJsonBody,
                            DateHelper.getSystemDatetime(), null, CodedValues.NDI_ATIVO,
                            tipoNotificacao.getCodigo(), responsavel);

                    // Envia a notificação
                    final List<String> lstTokens = new ArrayList<>();
                    lstTokens.add(chaveDispositivo);
                    if (NotificacaoDispositivoHelper.enviarNotificacao(lstTokens, strJsonBody, texto, titulo, tipoNotificacao.getCodigo(), "", responsavel)) {
                        // Registra o envio da notificação
                        registrarEnvioNotificacao(ndiCodigo, responsavel);

                        // Se conseguiu enviar para um dispositivo do servidor, então interrompe o laço
                        return true;
                    }
                }
            }
        } catch (final NotificacaoDispositivoControllerException ex) {
            throw ex;
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new NotificacaoDispositivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return false;
    }

    @Override
    public void enviarNotificacaoMultipla(String tituloPush, String textoPush, String strJsonBody, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {

        if (!responsavel.temPermissao(CodedValues.FUN_ENVIAR_MENSAGEM_MASSA_PUSH_NOTIFICATION)) {
            throw new NotificacaoDispositivoControllerException("mensagem.semPermissaoEnvioMassa", responsavel);
        }

        Date ultimaData = new Date(0);
        final boolean temRegistros = true;

        try {
            while (temRegistros) {
                final List <UsuarioChaveDispositivo> listaUcd = UsuarioChaveDispositivoHome.buscarLote(ultimaData);
                if ((listaUcd == null) || listaUcd.isEmpty()) {
                    break;
                }
                final List<String> lstTokens = new ArrayList<>();
                for (final UsuarioChaveDispositivo ucd : listaUcd) {
                    if((ucd.getUcdToken() != null) && !ucd.getUcdToken().trim().isEmpty()) {
                        lstTokens.add(ucd.getUcdToken());
                    }
                }
                if (!lstTokens.isEmpty()) {
                    NotificacaoDispositivoHelper.enviarNotificacao(lstTokens, strJsonBody, textoPush, tituloPush, TipoNotificacaoEnum.PUSH_NOTIFICATION_MENSAGEM_SERVIDOR.getCodigo(), null, responsavel);
                    LOG.info("Push enviado para " + lstTokens.size() + " dispositivos");
                } else {
                    LOG.warn("Nenhum token válido encontrado!");
                }
                if (!listaUcd.isEmpty()) {
                    ultimaData = listaUcd.get(listaUcd.size() - 1).getUcdDataCriacao();
                }
            }
        } catch (final NotificacaoDispositivoControllerException ex) {
            throw ex;
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            // throw new NotificacaoDispositivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * atualiza a data de envio da notificação ao dispositivo
     * @param ndiCodigo
     * @param responsavel
     * @throws NotificacaoDispositivoControllerException
     */
    @Override
    public void registrarEnvioNotificacao(String ndiCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        try {
            final NotificacaoDispositivo notificacao = NotificacaoDispositivoHome.findByPrimaryKey(ndiCodigo);
            notificacao.setNdiDataEnvio(DateHelper.getSystemDatetime());

            AbstractEntityHome.update(notificacao);
        } catch (final FindException ex) {
            throw new NotificacaoDispositivoControllerException("mensagem.erro.notificacao.dispositivo.nao.encontrada", responsavel, ex);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new NotificacaoDispositivoControllerException("mensagem.erro.notificacao.dispositivo.falha.atualizar", responsavel, ex);
        }
    }

    @Override
    public void registrarEnvioNotificacoesInativas(String ndiCodigo, String funCodigo, String usuCodigoDestinatario, String tnoCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException {
        try {
            final ListaNotificacoesInativasQuery lstNdiQuery = new ListaNotificacoesInativasQuery();
            lstNdiQuery.funCodigo = funCodigo;
            lstNdiQuery.usuCodigoDestinatario = usuCodigoDestinatario;
            lstNdiQuery.tnoCodigo = tnoCodigo;
            lstNdiQuery.ndiCodigo = ndiCodigo;

            final List<String> lstNdiAcancelar = lstNdiQuery.executarLista();

            for (final String ndi : lstNdiAcancelar) {
                registrarEnvioNotificacao(ndi, responsavel);
            }
        } catch (final HQueryException ex) {
            throw new NotificacaoDispositivoControllerException("mensagem.erro.notificacao.dispositivo.falha.atualizar", responsavel, ex);
        }
    }
}
