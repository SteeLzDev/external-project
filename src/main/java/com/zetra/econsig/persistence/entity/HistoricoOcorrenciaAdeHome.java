package com.zetra.econsig.persistence.entity;


import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricoOcorrenciaAdeHome extends AbstractEntityHome {

    public static List<HistoricoOcorrenciaAde> findHistoricoOcorrenciaAde(String ocaCodigo) throws FindException {
        String query = "FROM HistoricoOcorrenciaAde hoa WHERE hoa.ocaCodigo = :ocaCodigo order by hoa.hoaData desc";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ocaCodigo", ocaCodigo);
        return findByQuery(query, parameters);
    }

    public static HistoricoOcorrenciaAde create(String ocaCodigo, String hoaObs, AcessoSistema responsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        HistoricoOcorrenciaAde bean = new HistoricoOcorrenciaAde();

        try {
            bean.setHoaCodigo(DBHelper.getNextId());
            bean.setOcaCodigo(ocaCodigo);
            bean.setHoaIpAcesso(responsavel.getIpUsuario());
            bean.setHoaData(DateHelper.getSystemDatetime());
            bean.setUsuCodigo(responsavel.getUsuCodigo());
            bean.setHoaObs(hoaObs);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
