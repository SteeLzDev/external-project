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
 * <p>Title: EnderecoAcessoFuncaoHome</p>
 * <p>Description: Classe Home para a entidade EnderecoAcessoFuncao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnderecoAcessoFuncaoHome extends AbstractEntityHome {

    public static EnderecoAcessoFuncao findByPrimaryKey(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM EnderecoAcessoFuncao eaf WHERE eaf.id.usuCodigo = :usuCodigo and eaf.id.funCodigo = :funCodigo";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("usuCodigo", usuCodigo);
        parametros.put("funCodigo", funCodigo);

        List<EnderecoAcessoFuncao> result = findByQuery(query, parametros);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static EnderecoAcessoFuncao create(EnderecoAcessoFuncao t) throws CreateException {
        Session session = SessionUtil.getSession();
        try {
            if (t.getFuncao() == null) {
                t.setFuncao(session.getReference(Funcao.class, t.getFunCodigo()));
            }
            create(t, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return t;
    }

}
