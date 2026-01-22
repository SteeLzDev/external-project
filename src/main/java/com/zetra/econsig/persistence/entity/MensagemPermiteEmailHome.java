package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: MensagemPermiteEmailHome</p>
 * <p>Description: Home do bean class MensagemPermiteEmail.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MensagemPermiteEmailHome extends AbstractEntityHome {

    public static MensagemPermiteEmail create(String papCodigoRemetente, String papCodigoDestinatario) throws CreateException {
        MensagemPermiteEmailId id = new MensagemPermiteEmailId(papCodigoRemetente, papCodigoDestinatario);

        MensagemPermiteEmail bean = new MensagemPermiteEmail();
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static List<MensagemPermiteEmail> findByPapCodigoRemetente(String papelRemetente) throws FindException {
        String query = "FROM MensagemPermiteEmail mpe WHERE mpe.id.papCodigoRemetente = :papelRemetente";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("papelRemetente", papelRemetente);

        List<MensagemPermiteEmail> result = findByQuery(query, parameters);
        if (result == null || result.isEmpty()) {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
        }

        return result;
    }

    public static Collection<MensagemPermiteEmail> listMensagemPermiteEmail() throws FindException {
        String query = "FROM MensagemPermiteEmail mpe";

        Map<String, Object> parameters = new HashMap<String, Object>();

        return findByQuery(query, parameters);
    }
}
