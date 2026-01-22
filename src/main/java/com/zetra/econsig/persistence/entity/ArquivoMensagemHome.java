package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoMensagemHome</p>
 * <p>Description: Classe Home para a entidade ArquivoMensagem</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoMensagemHome extends AbstractEntityHome {

    public static ArquivoMensagem findByPrimaryKey(ArquivoMensagemId id) throws FindException {
        ArquivoMensagem arquivo = new ArquivoMensagem();
        arquivo.setId(id);
        return find(arquivo, id);
    }

    public static ArquivoMensagem findByMenCodigo(String menCodigo) throws FindException {
        String query = "FROM ArquivoMensagem arqMen WHERE arqMen.mensagem.menCodigo = :menCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("menCodigo", menCodigo);

        List<ArquivoMensagem> result = findByQuery(query, parameters);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ArquivoMensagem create(String menCodigo, String arqCodigo, Serializable usuCodigo, String amnNome, String amnIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        ArquivoMensagem bean = new ArquivoMensagem();
        Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());

            ArquivoMensagemId id = new ArquivoMensagemId();
            id.setMenCodigo(menCodigo);
            id.setArqCodigo(arqCodigo);
            bean.setId(id);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setAmnDataCriacao(agora);
            bean.setAmnNome(amnNome);
            bean.setAmnIpAcesso(amnIpAcesso);

            create(bean);
            SessionUtil.closeSession(session);
        return bean;
    }
}
