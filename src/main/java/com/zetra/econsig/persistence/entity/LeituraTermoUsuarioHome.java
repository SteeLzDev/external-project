package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;


/**
 * <p>Title: LeituraTermoUsuarioHome</p>
 * <p>Description: Classe Home para a entidade LeituraTermoUsuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeituraTermoUsuarioHome extends AbstractEntityHome {

    public static LeituraTermoUsuario findByPrimaryKey(String pk) throws FindException {
    	final LeituraTermoUsuario bean = new LeituraTermoUsuario();
        bean.setLtuCodigo(pk);
        return find(bean, pk);
    }

    public static LeituraTermoUsuario create(String usuCodigo, String tadCodigo, String ltuTermoAceito, String ltuCanal, String ltuIpAcesso,
    		Integer ltuPorta, String ltuObs, Integer versaoTermo) throws CreateException {
        final Session session = SessionUtil.getSession();
    	final LeituraTermoUsuario bean = new LeituraTermoUsuario();

        final Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String objectId = null;
        try {
			objectId = DBHelper.getNextId();

	        bean.setLtuCodigo(objectId);
			bean.setUsuCodigo(usuCodigo);
			bean.setTadCodigo(tadCodigo);
			bean.setLtuData(agora);
			bean.setLtuTermoAceito(ltuTermoAceito);
			bean.setLtuCanal(ltuCanal);
			bean.setLtuIpAcesso(ltuIpAcesso);
			bean.setLtuPortaLogica(ltuPorta);
			bean.setLtuObs(ltuObs);
            bean.setLtuVersaoTermo(versaoTermo);

			create(bean);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static LeituraTermoUsuario createWithDate( Date date, String usuCodigo, String tadCodigo, String ltuTermoAceito, String ltuCanal, String ltuIpAcesso,
    		Integer ltuPorta, String ltuObs) throws CreateException {
        final Session session = SessionUtil.getSession();
    	final LeituraTermoUsuario bean = new LeituraTermoUsuario();

        String objectId = null;
        try {
			objectId = DBHelper.getNextId();

	        bean.setLtuCodigo(objectId);
			bean.setUsuCodigo(usuCodigo);
			bean.setTadCodigo(tadCodigo);
			bean.setLtuData(date);
			bean.setLtuTermoAceito(ltuTermoAceito);
			bean.setLtuCanal(ltuCanal);
			bean.setLtuIpAcesso(ltuIpAcesso);
			bean.setLtuPortaLogica(ltuPorta);
			bean.setLtuObs(ltuObs);

			create(bean);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
