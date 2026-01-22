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
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: TipoMotivoReclamacaoHome</p>
 * <p>Description: Classe Home para a entidade TipoMotivoReclamacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoMotivoReclamacaoHome extends AbstractEntityHome {

    public static TipoMotivoReclamacao findByPrimaryKey(String codigo) throws FindException {
        TipoMotivoReclamacao tipoMotivoReclamacao = new TipoMotivoReclamacao();
        tipoMotivoReclamacao.setTmrCodigo(codigo);
        return find(tipoMotivoReclamacao, codigo);
    }

    public static TipoMotivoReclamacao findByDescricao(String tmrDescricao) throws FindException {
        String query = "FROM TipoMotivoReclamacao tmr WHERE lower(tmr.tmrDescricao) = lower(:tmrDescricao)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tmrDescricao", tmrDescricao);

        List<TipoMotivoReclamacao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static TipoMotivoReclamacao create(String descricao) throws CreateException {

        Session session = SessionUtil.getSession();
        TipoMotivoReclamacao bean = new TipoMotivoReclamacao();

        String codigo = null;
        try {
            codigo = DBHelper.getNextId();
            bean.setTmrCodigo(codigo);
            bean.setTmrDescricao(descricao);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
