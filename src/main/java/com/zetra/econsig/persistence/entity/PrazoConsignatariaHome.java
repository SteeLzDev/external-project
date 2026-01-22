package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: PrazoConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade PrazoConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PrazoConsignatariaHome extends AbstractEntityHome {

    public static PrazoConsignataria findByPrimaryKey(String przCsaCodigo) throws FindException {
        PrazoConsignataria prazoConsignataria = new PrazoConsignataria();
        prazoConsignataria.setPrzCsaCodigo(przCsaCodigo);
        return find(prazoConsignataria, przCsaCodigo);
    }

    public static PrazoConsignataria findByCsaPrazo(String csaCodigo, String przCodigo) throws FindException {
        String query = "FROM PrazoConsignataria pzc WHERE pzc.consignataria.csaCodigo = :csaCodigo AND pzc.prazo.przCodigo = :przCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("przCodigo", przCodigo);

        List<PrazoConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<PrazoConsignataria> findByCsaServico(String csaCodigo, String svcCodigo) throws FindException {
        String query = "FROM PrazoConsignataria pzc WHERE pzc.consignataria.csaCodigo = :csaCodigo AND EXISTS (SELECT 1 FROM pzc.prazo prz WHERE prz.servico.svcCodigo = :svcCodigo)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static PrazoConsignataria create(String csaCodigo, String przCodigo, Short przCsaAtivo) throws CreateException {

        Session session = SessionUtil.getSession();
        PrazoConsignataria bean = new PrazoConsignataria();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPrzCsaCodigo(objectId);
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setPrazo((Prazo) session.getReference(Prazo.class, przCodigo));
            bean.setPrzCsaAtivo(przCsaAtivo);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static PrazoConsignataria create(String csaCodigo, String przCodigo) throws CreateException {
        return create(csaCodigo, przCodigo, CodedValues.STS_ATIVO);
    }
}
