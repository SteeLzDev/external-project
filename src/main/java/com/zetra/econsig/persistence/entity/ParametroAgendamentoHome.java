package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParametroAgendamentoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade ParametroAgendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroAgendamentoHome extends AbstractEntityHome {

    public static List<ParametroAgendamento> findByAgdCodigoPagNome(String agdCodigo, String pagNome) throws FindException {
        String query = "FROM ParametroAgendamento pag WHERE pag.agendamento.agdCodigo = :agdCodigo and pag.pagNome = :pagNome";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("agdCodigo", agdCodigo);
        parameters.put("pagNome", pagNome);

        List<ParametroAgendamento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParametroAgendamento findByAgdCodigoPagNomePagValor(String agdCodigo, String pagNome, String pagValor) throws FindException {
        String query = "FROM ParametroAgendamento pag WHERE pag.agendamento.agdCodigo = :agdCodigo and pag.pagNome = :pagNome and text_to_string(pag.pagValor) = :pagValor";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("agdCodigo", agdCodigo);
        parameters.put("pagNome", pagNome);
        parameters.put("pagValor", pagValor);

        List<ParametroAgendamento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParametroAgendamento create(String agdCodigo, String nome, String valor) throws CreateException {

        Session session = SessionUtil.getSession();
        ParametroAgendamento bean = new ParametroAgendamento();
        try {
            bean.setAgendamento((Agendamento) session.getReference(Agendamento.class, agdCodigo));
            bean.setPagNome(nome);
            bean.setPagValor(valor);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static ParametroAgendamento findByPrimaryKey(Integer pagCodigo) throws FindException {
        ParametroAgendamento parametro = new ParametroAgendamento();
        parametro.setPagCodigo(pagCodigo);

        return find(parametro, pagCodigo);
    }
}
