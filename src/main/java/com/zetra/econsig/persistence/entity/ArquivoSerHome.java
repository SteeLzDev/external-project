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
 * <p>Title: ArquivoSerHome</p>
 * <p>Description: Classe Home para a entidade ArquivoSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoSerHome extends AbstractEntityHome {

    public static ArquivoSer findByPrimaryKey(ArquivoSerId id) throws FindException {
        ArquivoSer arquivoSer = new ArquivoSer();
        arquivoSer.setId(id);
        return find(arquivoSer, id);
    }

    public static ArquivoSer findBySerCodigo(String serCodigo) throws FindException {
        String query = "FROM ArquivoSer arqSer WHERE arqSer.servidor.serCodigo = :serCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("serCodigo", serCodigo);

        List<ArquivoSer> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<ArquivoSer> listBySerCodigo(String serCodigo) throws FindException {
        String query = "FROM ArquivoSer arqSer WHERE arqSer.servidor.serCodigo = :serCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static ArquivoSer create(String arqCodigo, String serCodigo, Serializable usuCodigo, String aseNome, String aseIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        ArquivoSer bean = new ArquivoSer();
        Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());

        ArquivoSerId id = new ArquivoSerId();
        id.setArqCodigo(arqCodigo);
        id.setSerCodigo(serCodigo);
        bean.setId(id);
        bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
        bean.setAseDataCriacao(agora);
        bean.setAseNome(aseNome);
        bean.setAseIpAcesso(aseIpAcesso);

        create(bean);
        return bean;
    }
}
