package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: InformacaoCsaServidorHome</p>
 * <p>Description: Home de informacao csa servidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class InformacaoCsaServidorHome extends AbstractEntityHome{
    
    public static InformacaoCsaServidor findInformacaoCsaServidorByIcsCodigo(String icsCodigo, AcessoSistema responsavel) throws FindException{
        String query = "FROM InformacaoCsaServidor ics WHERE ics.icsCodigo = :icsCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("icsCodigo", icsCodigo);
        
        List<InformacaoCsaServidor> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) responsavel);
    }
    
    public static InformacaoCsaServidor create(String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws CreateException, MissingPrimaryKeyException {
        Session session = SessionUtil.getSession();
        InformacaoCsaServidor bean = new InformacaoCsaServidor();

        try {
            bean.setIcsCodigo(DBHelper.getNextId());
            bean.setIcsValor(icsValor);
            bean.setIcsData(DateHelper.getSystemDatetime());
            bean.setUsuCodigo(responsavel.getUsuCodigo());
            bean.setSerCodigo(serCodigo);
            bean.setCsaCodigo(csaCodigo);
            bean.setIcsIpAcesso(responsavel.getIpUsuario());
            
            create(bean, session);

        } catch (CreateException | MissingPrimaryKeyException ex) {
            throw new CreateException("mensagem.informacao.csa.servidor.erro.criar.novo", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
    
    public static InformacaoCsaServidor update(String icsCodigo, String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws UpdateException {
        Session session = SessionUtil.getSession();
        InformacaoCsaServidor bean = new InformacaoCsaServidor();

        try {
            bean.setIcsCodigo(icsCodigo);
            bean.setIcsValor(icsValor);
            bean.setIcsData(DateHelper.getSystemDatetime());
            bean.setUsuCodigo(responsavel.getUsuCodigo());
            bean.setSerCodigo(serCodigo);
            bean.setCsaCodigo(csaCodigo);
            bean.setIcsIpAcesso(responsavel.getIpUsuario());
            
            update(bean);

        } catch (UpdateException ex) {
            throw new UpdateException("mensagem.informacao.csa.servidor.erro.editar", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
