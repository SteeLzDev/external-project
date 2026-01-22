package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: MensagemHome</p>
 * <p>Description: Classe Home para a entidade Mensagem</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MensagemHome extends AbstractEntityHome {

    public static Mensagem findByPrimaryKey(String menCodigo) throws FindException {
        Mensagem mensagem = new Mensagem();
        mensagem.setMenCodigo(menCodigo);
        return find(mensagem, menCodigo);
    }

    public static Mensagem create(String usuCodigo, String funCodigo, String menTitulo, String menTexto, Date menData, Short menSequencia, String menExibeCse,
            String menExibeOrg, String menExibeCsa, String menExibeCor, String menExibeSer, String menExibeSup, String menExigeLeitura,
            String menPermiteLerDepois, String menNotificarCseLeitura, String menBloqCsaSemLeitura, String menHtml, String menPublica, String menLidaIndividualmente, String menPushNotificationSer) throws CreateException {

        Session session = SessionUtil.getSession();
        Mensagem bean = new Mensagem();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setMenCodigo(objectId);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));

            if (!TextHelper.isNull(funCodigo)) {
                bean.setFuncao(session.getReference(Funcao.class, funCodigo));
            }

            bean.setMenTitulo(menTitulo);
            bean.setMenTexto(menTexto);
            bean.setMenData(menData);
            bean.setMenSequencia(menSequencia);
            bean.setMenExibeCse(menExibeCse);
            bean.setMenExibeOrg(menExibeOrg);
            bean.setMenExibeCsa(menExibeCsa);
            bean.setMenExibeCor(menExibeCor);
            bean.setMenExibeSer(menExibeSer);
            bean.setMenExibeSup(menExibeSup);
            bean.setMenExigeLeitura(menExigeLeitura);
            if (!TextHelper.isNull(menHtml)) {
                bean.setMenHtml(menHtml);
            } else {
                bean.setMenHtml("N");
            }

            if (!TextHelper.isNull(menPermiteLerDepois)) {
                bean.setMenPermiteLerDepois(menPermiteLerDepois);
            } else {
                bean.setMenPermiteLerDepois("S");
            }

            if (!TextHelper.isNull(menNotificarCseLeitura)) {
                bean.setMenNotificarCseLeitura(menNotificarCseLeitura);
            } else {
                bean.setMenNotificarCseLeitura("N");
            }

            if (!TextHelper.isNull(menBloqCsaSemLeitura)) {
                bean.setMenBloqCsaSemLeitura(menBloqCsaSemLeitura);
            } else {
                bean.setMenBloqCsaSemLeitura("N");
            }
            
            if (!TextHelper.isNull(menPublica)) {
                bean.setMenPublica(menPublica);
            } else {
                bean.setMenPublica("N");
            }
            
            if(!TextHelper.isNull(menLidaIndividualmente)) {
            	bean.setMenLidaIndividualmente(menLidaIndividualmente);
            } else {
            	bean.setMenLidaIndividualmente("N");
            }

            if(!TextHelper.isNull(menPushNotificationSer)) {
            	bean.setMenPushNotificationSer(menPushNotificationSer);
            } else {
            	bean.setMenPushNotificationSer("N");
            }

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
