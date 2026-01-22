package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioCorHome</p>
 * <p>Description: Classe Home para a entidade UsuarioCor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCorHome extends AbstractEntityHome {

    public static UsuarioCor findByPrimaryKey(UsuarioCorId id) throws FindException {
        UsuarioCor usuarioCor = new UsuarioCor();
        usuarioCor.setId(id);
        return find(usuarioCor, id);
    }

    public static UsuarioCor findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioCor usuCor WHERE usuCor.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioCor> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static UsuarioCor create(String corCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioCor bean = new UsuarioCor();

        UsuarioCorId id = new UsuarioCorId();
        id.setCorCodigo(corCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
