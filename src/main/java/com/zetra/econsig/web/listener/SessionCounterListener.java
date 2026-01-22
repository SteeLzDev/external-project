package com.zetra.econsig.web.listener;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.cache.ExternalSet;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: SessionCounterListener</p>
 * <p>Description: Listener para contabilização de sessões ativas no sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class SessionCounterListener {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SessionCounterListener.class);

    @Value("${spring.session.store-type}")
    private String storeType;

    private final Set<String> sessionsIds; 
    private final Map<String, String> mapUserToSessionId;

    public SessionCounterListener() {
        if ("none".equalsIgnoreCase(storeType)) {
            sessionsIds = Collections.synchronizedSet(new HashSet<>()); 
            mapUserToSessionId = new Hashtable<>();
        } else {
            final String prefix = getClass().getSimpleName();
            sessionsIds = new ExternalSet<>(prefix + "-sessions", false);
            mapUserToSessionId = new ExternalMap<>(prefix + "-usermap", null, false);
        }
    }

    private Date lastDestroyedSession = new Date();

    @Autowired
    private SessionRepository<Session> sessionRepository;

    @EventListener
    public void sessionCreatedListener(SessionCreatedEvent event) {
        registerNewSession(event.getSession());
    }

    private void registerNewSession(Session session) {
        LOG.debug("New session created");
        sessionsIds.add(session.getId());
    }

    @EventListener
    public void sessionDestroyedListener(SessionDestroyedEvent event) {
        registerSessionDelete(event.getSessionId());
    }

    private void registerSessionDelete(String sessionId) {
        LOG.debug("Session deleted");
        lastDestroyedSession = new Date();
        sessionsIds.remove(sessionId);
    }

    public int getActiveSessions() {
        return mapUserToSessionId.size();
    }

    public int getSecondsWithoutUsers() {
        if (mapUserToSessionId.size() == 0 && lastDestroyedSession != null) {
            Date now = new Date();
            return (int) (now.getTime() - lastDestroyedSession.getTime()) / 1000;
        }
        return 0;
    }

    public List<AcessoSistema> getLoggedUsers(AcessoSistema responsavel) {
        List<AcessoSistema> usuariosLogados = new ArrayList<>();
        for (String sessionId : sessionsIds) {
            Session session = getSessionById(sessionId);
            if (!isSessionExpired(session)) {
                AcessoSistema usuario = getUserInSession(session);
                if (usuario != null && usuario.isSessaoValida() && session.getAttribute(CodedValues.SESSAO_INVALIDA) == null) {
                    if (responsavel.isSup() ||
                            (responsavel.isCse() && (usuario.isCseOrg() || usuario.isCsaCor() || usuario.isSer())) ||
                            (responsavel.isOrg() && usuario.isOrg() && responsavel.getOrgCodigo().equals(usuario.getOrgCodigo())) ||
                            (responsavel.isCsa() && usuario.isCsaCor() && responsavel.getCsaCodigo().equals(usuario.getCsaCodigo())) ||
                            (responsavel.isCor() && usuario.isCor() && responsavel.getCorCodigo().equals(usuario.getCorCodigo()))) {
                        usuario.setSessionId(session.getId());
                        usuario.setDataUltimaRequisicao(Date.from(session.getLastAccessedTime()));
                        usuariosLogados.add(usuario);
                    }
                }
            }
        }

        return usuariosLogados;
    }

    public void logoutSession(String sessionId, AcessoSistema responsavel) {
        Session session = getSessionById(sessionId);
        if (session != null) {
            AcessoSistema usuario = getUserInSession(session);
            if (usuario != null && usuario.isSessaoValida()) {
                invalidateSession(session, "mensagem.aviso.sessao.usuario.encerrada.manualmente");

                try {
                    // Gera log de logout, indicando que foi encerrada por outro usuário
                    final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_AVISO);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.log.sessao.usuario.encerrada.por.administrador", responsavel));
                    log.setUsuario(usuario.getUsuCodigo());
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private Session getSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    private AcessoSistema getUserInSession(Session session) {
        return session != null ? (AcessoSistema) session.getAttribute(AcessoSistema.SESSION_ATTR_NAME) : null;
    }

    private void invalidateSession(Session session, String messageKey) {
        session.setAttribute(CodedValues.SESSAO_INVALIDA, Boolean.TRUE);
        session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage(messageKey, AcessoSistema.getAcessoUsuarioSistema()));
        sessionRepository.save(session);
    }

    private boolean isSessionExpired(Session session) {
        if (session == null || session.isExpired()) {
            return true;
        }

        // If not expired, check time-to-live limit
        final Duration sessionTimeToLiveHours = Duration.ofHours(SysConfig.get().getSessionTimeToLiveHours());

        // If session was created more than TTL hours, then expires session
        if (session.getCreationTime().plus(sessionTimeToLiveHours).isBefore(Instant.now())) {
            invalidateSession(session, "mensagem.informacao.sessao.expirada");
            return true;
        }

        // If last access plus max inactive inverval is before now, then expires session
        if (session.getLastAccessedTime().plus(session.getMaxInactiveInterval()).isBefore(Instant.now())) {
            invalidateSession(session, "mensagem.informacao.sessao.expirada");
            return true;
        }

        return false;
    }

    public void validateNewSession(String usuCodigo, String id) {
        if (ParamSist.paramEquals(CodedValues.TPC_BLOQ_ACESSO_SIMULTANEO_USUARIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            final String anotherId = mapUserToSessionId.get(usuCodigo);
            if (anotherId != null && !anotherId.equals(id)) {
                final Session session = getSessionById(anotherId);
                if (session != null) {
                    invalidateSession(session, "mensagem.erro.acesso.simultaneo.invalido");
                }
            }
        }

        mapUserToSessionId.put(usuCodigo, id);
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void clearCache() {
        LOG.info("Clear session cache");
        final Set<String> idToRemove = new HashSet<>();
        final Set<String> userToRemove = new HashSet<>();

        if (sessionsIds.isEmpty()) {
            return;
        }

        // From users mapping, identity expired sessions
        for (String usuCodigo : mapUserToSessionId.keySet()) {
            String sessionId = mapUserToSessionId.get(usuCodigo);
            if (isSessionExpired(getSessionById(sessionId))) {
                idToRemove.add(sessionId);
                userToRemove.add(usuCodigo);
            }
        }

        // From sessions, identity and expired sessions
        for (String sessionId : sessionsIds) {
            if (isSessionExpired(getSessionById(sessionId))) {
                idToRemove.add(sessionId);
            }
        }

        for (String usuCodigo : userToRemove) {
            mapUserToSessionId.remove(usuCodigo);
        }
        for (String sessionId : idToRemove) {
            registerSessionDelete(sessionId);
        }
    }
}
