package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: VerbaConvenioHome</p>
 * <p>Description: Classe Home para a entidade VerbaConvenio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerbaConvenioHome extends AbstractEntityHome {

    public static VerbaConvenio findByPrimaryKey(String vcoCodigo) throws FindException {
        VerbaConvenio verbaConvenio = new VerbaConvenio();
        verbaConvenio.setVcoCodigo(vcoCodigo);
        return find(verbaConvenio, vcoCodigo);
    }

    public static VerbaConvenio findAtivoByConvenio(String cnvCodigo) throws FindException {
        String query = "FROM VerbaConvenio vco WHERE vco.convenio.cnvCodigo = :cnvCodigo AND vco.vcoAtivo = 1";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cnvCodigo", cnvCodigo);

        List<VerbaConvenio> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static VerbaConvenio create(String cnvCodigo, Short vcoAtivo, BigDecimal vcoVlrVerba) throws CreateException {

        Session session = SessionUtil.getSession();
        VerbaConvenio bean = new VerbaConvenio();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setVcoCodigo(objectId);
            bean.setConvenio((Convenio) session.getReference(Convenio.class, cnvCodigo));
            bean.setVcoAtivo(vcoAtivo);
            bean.setVcoVlrVerba(vcoVlrVerba);
            bean.setVcoVlrVerbaRest(vcoVlrVerba);
            bean.setVcoDataIni(DateHelper.toSQLDate(DateHelper.getSystemDatetime()));
            bean.setVcoDataFim(new Date(0));
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
