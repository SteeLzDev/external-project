package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioSupHome</p>
 * <p>Description: Classe Home para a entidade UsuarioSup</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioSupHome extends AbstractEntityHome {

    public static UsuarioSup findByPrimaryKey(UsuarioSupId id) throws FindException {
        UsuarioSup usuarioSup = new UsuarioSup();
        usuarioSup.setId(id);
        return find(usuarioSup, id);
    }

    public static UsuarioSup findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioSup usuCse WHERE usuCse.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioSup> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static UsuarioSup create(String cseCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioSup bean = new UsuarioSup();

        UsuarioSupId id = new UsuarioSupId();
        id.setCseCodigo(cseCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
