package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SenhaAutorizacaoServidorHome</p>
 * <p>Description: Classe Home para a entidade SenhaAutorizacaoServidor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SenhaAutorizacaoServidorHome extends AbstractEntityHome {

    public static SenhaAutorizacaoServidor findByPrimaryKey(SenhaAutorizacaoServidorId pk) throws FindException {
        SenhaAutorizacaoServidor senhaAutorizacaoServidor = new SenhaAutorizacaoServidor();
        senhaAutorizacaoServidor.setId(pk);
        return find(senhaAutorizacaoServidor, pk);
    }

    public static SenhaAutorizacaoServidor findPrimeiroByUsuarioSenha(String usuCodigo, String sasSenha) throws FindException {
        String query = "FROM SenhaAutorizacaoServidor sas WHERE sas.id.usuCodigo = :usuCodigo AND sas.sasSenha = :sasSenha ORDER BY sas.sasDataExpiracao";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("sasSenha", sasSenha);

        List<SenhaAutorizacaoServidor> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<SenhaAutorizacaoServidor> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM SenhaAutorizacaoServidor sas WHERE sas.id.usuCodigo = :usuCodigo ORDER BY sas.sasDataExpiracao";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static SenhaAutorizacaoServidor create(String usuCodigo, String sasSenha, Date sasDataExpiracao, Short sasQtdOperacoes) throws CreateException {

        Session session = SessionUtil.getSession();
        SenhaAutorizacaoServidor bean = new SenhaAutorizacaoServidor();
        try {
            Date sasDataCriacao = DateHelper.getSystemDatetime();
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setSasDataCriacao(sasDataCriacao);
            bean.setSasDataExpiracao(sasDataExpiracao);
            bean.setSasQtdOperacoes(sasQtdOperacoes);
            bean.setSasSenha(sasSenha);

            SenhaAutorizacaoServidorId id = new SenhaAutorizacaoServidorId();
            id.setUsuCodigo(usuCodigo);
            id.setSasDataCriacao(sasDataCriacao);
            bean.setId(id);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
