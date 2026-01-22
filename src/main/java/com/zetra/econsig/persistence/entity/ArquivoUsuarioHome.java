package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoUsuarioHome</p>
 * <p>Description: Classe Home para a entidade ArquivoUsuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoUsuarioHome extends AbstractEntityHome {

    public static ArquivoUsuario findByPrimaryKey(String codigo) throws FindException {
        ArquivoUsuario agendamento = new ArquivoUsuario();
        agendamento.setAusCodigo(codigo);
        return find(agendamento, codigo);
    }

    public static Collection<ArquivoUsuario> findByUsuCodigoTipoArquivo(String usuCodigo, String tarCodigo) throws FindException {
        StringBuilder query = new StringBuilder("FROM ArquivoUsuario aus WHERE aus.usuario.usuCodigo = :usuCodigo");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        if (!TextHelper.isNull(tarCodigo)) {
            query.append(" and aus.tipoArquivo.tarCodigo = :tarCodigo");
            parameters.put("tarCodigo", tarCodigo);
        }

        return findByQuery(query.toString(), parameters);
    }

    public static ArquivoUsuario create(TipoArquivo tipoArquivo, String usuCodigo, byte [] ausConteudo) throws CreateException {

        Session session = SessionUtil.getSession();
        ArquivoUsuario bean = new ArquivoUsuario();

        try {
            String objectId = DBHelper.getNextId();
            bean.setAusCodigo(objectId);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setAusDataCriacao(DateHelper.getSystemDatetime());
            bean.setTipoArquivo(tipoArquivo);
            bean.setAusConteudo(ausConteudo);

            create(bean, session);

        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
