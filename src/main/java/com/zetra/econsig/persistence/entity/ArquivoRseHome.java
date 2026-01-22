package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoRseHome</p>
 * <p>Description: Classe Home para a entidade ArquivoRse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoRseHome extends AbstractEntityHome {

    public static ArquivoRse findByPrimaryKey(ArquivoRseId id) throws FindException {
        ArquivoRse arquivoRse = new ArquivoRse();
        arquivoRse.setId(id);
        return find(arquivoRse, id);
    }

    public static ArquivoRse findByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM ArquivoRse arqRse WHERE arqRse.registroServidor.rseCodigo = :rseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        List<ArquivoRse> result = findByQuery(query, parameters);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<ArquivoRse> listByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM ArquivoRse arqRse WHERE arqRse.registroServidor.rseCodigo = :rseCodigo ORDER BY arqRse.arsDataCriacao DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    public static ArquivoRse create(String arqCodigo, String rseCodigo, Serializable usuCodigo, String arsNome, String arsIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        ArquivoRse bean = new ArquivoRse();
        Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());

        ArquivoRseId id = new ArquivoRseId();
        id.setArqCodigo(arqCodigo);
        id.setRseCodigo(rseCodigo);
        bean.setId(id);
        bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
        bean.setArsDataCriacao(agora);
        bean.setArsNome(arsNome);
        bean.setArsIpAcesso(arsIpAcesso);

        create(bean);
        return bean;
    }
}
