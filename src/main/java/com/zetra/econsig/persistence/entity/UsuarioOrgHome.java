package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioOrgHome</p>
 * <p>Description: Classe Home para a entidade UsuarioOrg</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioOrgHome extends AbstractEntityHome {

    public static UsuarioOrg findByPrimaryKey(UsuarioOrgId id) throws FindException {
        UsuarioOrg usuarioOrg = new UsuarioOrg();
        usuarioOrg.setId(id);
        return find(usuarioOrg, id);
    }

    public static UsuarioOrg findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM UsuarioOrg usuOrg WHERE usuOrg.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        List<UsuarioOrg> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static UsuarioOrg create(String orgCodigo, String usuCodigo, String stuCodigo) throws CreateException {
        UsuarioOrg bean = new UsuarioOrg();

        UsuarioOrgId id = new UsuarioOrgId();
        id.setOrgCodigo(orgCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
