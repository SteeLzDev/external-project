package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioCsaHome</p>
 * <p>Description: Classe Home para a entidade UsuarioCsa</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCsaHome extends AbstractEntityHome {

    public static UsuarioCsa findByPrimaryKey(UsuarioCsaId id) throws FindException {
        UsuarioCsa usuarioCsa = new UsuarioCsa();
        usuarioCsa.setId(id);
        return find(usuarioCsa, id);
    }

    public static UsuarioCsa findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioCsa usuCsa WHERE usuCsa.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioCsa> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static UsuarioCsa create(String csaCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioCsa bean = new UsuarioCsa();

        UsuarioCsaId id = new UsuarioCsaId();
        id.setCsaCodigo(csaCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
