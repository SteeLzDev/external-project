package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AtendimentoHome</p>
 * <p>Description: Classe Home para Entidade Atendimento Mensagem</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtendimentoMensagemHome extends AbstractEntityHome {

    public static List<AtendimentoMensagem> findByAteCodigoOrderByAmeSequencia(String ateCodigo) throws FindException {
        StringBuilder query = new StringBuilder("FROM AtendimentoMensagem ame WHERE ame.id.ateCodigo = :ateCodigo ORDER BY ame.id.ameSequencia");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ateCodigo", ateCodigo);

        return findByQuery(query.toString(), parameters);
    }

    public static Integer selectMaxAmeSequenciaByAteCodigo(String ateCodigo) throws FindException {
        StringBuilder query = new StringBuilder("FROM AtendimentoMensagem ame WHERE ame.id.ateCodigo = :ateCodigo ORDER BY ame.id.ameSequencia DESC");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ateCodigo", ateCodigo);

        List<AtendimentoMensagem> mensagens = findByQuery(query.toString(), parameters, 1, 0);
        if (mensagens != null && !mensagens.isEmpty()) {
            return mensagens.get(0).getAmeSequencia();
        }

        return 0;
    }

    public static AtendimentoMensagem create(AtendimentoMensagem mensagem) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            mensagem = create(mensagem, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return mensagem;
    }
}
