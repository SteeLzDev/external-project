package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CanalEnum;

public class HistoricoLoginHome extends AbstractEntityHome{


    public static HistoricoLogin findByPrimaryKey(Long hloCodigo) throws FindException {
        HistoricoLogin historicoLogin = new HistoricoLogin();
        historicoLogin.setHloCodigo(hloCodigo);
        return find(historicoLogin, hloCodigo);
    }


    public static HistoricoLogin create(String usuCodigo, Date hloData, CanalEnum hloCanal) throws CreateException {

        Session session = SessionUtil.getSession();
        HistoricoLogin bean = new HistoricoLogin();

        try {
            if(hloData == null) {
                hloData = DateHelper.getSystemDatetime();
            }

            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setHloData(hloData);

            if (hloCanal != null) {
                bean.setHloCanal(hloCanal.getCodigo());
            } else {
                bean.setHloCanal(CanalEnum.WEB.getCodigo());
            }

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
