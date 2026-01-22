package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
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

/**
 * <p>Title: ParamTarifConsignanteHome</p>
 * <p>Description: Classe Home para a entidade ParamTarifConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamTarifConsignanteHome extends AbstractEntityHome {

    public static List<ParamTarifConsignante> findByServico(String svcCodigo) throws FindException {
        String query = "FROM ParamTarifConsignante AS p WHERE p.servico.svcCodigo = :svcCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);

        List<ParamTarifConsignante> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParamTarifConsignante findByPrimaryKey(String pcvCodigo) throws FindException {
        ParamTarifConsignante paramTarifCse = new ParamTarifConsignante();
        paramTarifCse.setPcvCodigo(pcvCodigo);
        return find(paramTarifCse, pcvCodigo);
    }

    public static ParamTarifConsignante create(String svcCodigo, String tptCodigo, Date pcvDataIniVig, Date pcvDataFimVig, Short pcvAtivo, BigDecimal pcvVlr, Integer pcvBaseCalc, Integer pcvFormaCalc, Integer pcvDecimais,
            BigDecimal pcvVlrIni, BigDecimal pcvVlrFim, String cseCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        ParamTarifConsignante bean = new ParamTarifConsignante();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPcvCodigo(objectId);
            bean.setConsignante(session.getReference(Consignante.class, cseCodigo));
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setTipoParamTarifCse(session.getReference(TipoParamTarifCse.class, tptCodigo));
            bean.setPcvDataIniVig(pcvDataIniVig);
            bean.setPcvDataFimVig(pcvDataFimVig);
            bean.setPcvAtivo(pcvAtivo);
            bean.setPcvVlr(pcvVlr);
            bean.setPcvBaseCalc(pcvBaseCalc);
            bean.setPcvFormaCalc(pcvFormaCalc);
            bean.setPcvDecimais(pcvDecimais);
            bean.setPcvVlrIni(pcvVlrIni);
            bean.setPcvVlrFim(pcvVlrFim);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }


}
