package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AtendimentoHome</p>
 * <p>Description: Classe Home para Entidade Atendimento</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtendimentoHome extends AbstractEntityHome {

    public static Atendimento findByPrimaryKey(String ateCodigo) throws FindException {
        Atendimento atendimento = new Atendimento();
        atendimento.setAteCodigo(ateCodigo);
        return find(atendimento, ateCodigo);
    }

    public static List<Atendimento> findByAteEmailUsuarioAndAteIdSessao(String ateEmailUsuario, String ateIdSessao) throws FindException {
        StringBuilder query = new StringBuilder("FROM Atendimento ate WHERE 1 = 1");

        if (!TextHelper.isNull(ateEmailUsuario) && !TextHelper.isNull(ateIdSessao)) {
            query.append(" AND ate.ateEmailUsuario = :ateEmailUsuario AND ate.ateIdSessao = :ateIdSessao");
        } else if (!TextHelper.isNull(ateEmailUsuario) && TextHelper.isNull(ateIdSessao)) {
                query.append(" AND ate.ateEmailUsuario = :ateEmailUsuario");
        } else if (TextHelper.isNull(ateEmailUsuario) && !TextHelper.isNull(ateIdSessao)) {
            query.append(" AND ate.ateIdSessao = :ateIdSessao");
        } else {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
        }

        Map<String, Object> parameters = new HashMap<>();
        if (!TextHelper.isNull(ateEmailUsuario)) {
            parameters.put("ateEmailUsuario", ateEmailUsuario);
        }
        if (!TextHelper.isNull(ateIdSessao)) {
            parameters.put("ateIdSessao", ateIdSessao);
        }

        return findByQuery(query.toString(), parameters);
    }

    public static Atendimento create(Atendimento atendimento) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            String objectId = DBHelper.getNextId();
            atendimento.setAteCodigo(objectId);

            if (atendimento.getUsuario() != null) {
                atendimento.setUsuario(session.getReference(Usuario.class, atendimento.getUsuario().getUsuCodigo()));
            }

            atendimento = create(atendimento, session);

        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }

        return atendimento;
    }

}
