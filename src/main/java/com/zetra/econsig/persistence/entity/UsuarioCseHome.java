package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioCseHome</p>
 * <p>Description: Classe Home para a entidade UsuarioCse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCseHome extends AbstractEntityHome {

    public static UsuarioCse findByPrimaryKey(UsuarioCseId id) throws FindException {
        UsuarioCse usuarioCse = new UsuarioCse();
        usuarioCse.setId(id);
        return find(usuarioCse, id);
    }

    public static UsuarioCse findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioCse usuCse WHERE usuCse.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioCse> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static UsuarioCse create(String cseCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioCse bean = new UsuarioCse();

        UsuarioCseId id = new UsuarioCseId();
        id.setCseCodigo(cseCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
