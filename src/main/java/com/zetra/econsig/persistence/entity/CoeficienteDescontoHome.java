package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
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
 * <p>Title: CoeficienteDescontoHome</p>
 * <p>Description: Classe Home para a entidade CoeficienteDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteDescontoHome extends AbstractEntityHome {

    public static CoeficienteDesconto findByPrimaryKey(String cdeCodigo) throws FindException {
        CoeficienteDesconto coeficienteDesconto = new CoeficienteDesconto();
        coeficienteDesconto.setCdeCodigo(cdeCodigo);
        return find(coeficienteDesconto, cdeCodigo);
    }

    public static CoeficienteDesconto findByAdeCodigo(String adeCodigo) throws FindException {
        String query = "FROM CoeficienteDesconto cde WHERE cde.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<CoeficienteDesconto> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static CoeficienteDesconto findArquivadoByAdeCodigo(String adeCodigo) throws FindException {
        String query = "FROM HtCoeficienteDesconto cde WHERE cde.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<HtCoeficienteDesconto> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return new CoeficienteDesconto(result.get(0));
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static CoeficienteDesconto create(java.lang.String adeCodigo, java.lang.String cftCodigo, java.math.BigDecimal cdeVlrLiberado, java.math.BigDecimal cdeVlrLiberadoCalc, java.lang.String cdeTxtContato, java.lang.Short cdeRanking,
            BigDecimal cdeVlrTac, BigDecimal cdeVlrIof) throws CreateException {

        Session session = SessionUtil.getSession();
        CoeficienteDesconto bean = new CoeficienteDesconto();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCdeCodigo(objectId);
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setCoeficiente(session.getReference(Coeficiente.class, cftCodigo));
            bean.setCdeVlrLiberado(cdeVlrLiberado);
            bean.setCdeVlrLiberadoCalc(cdeVlrLiberadoCalc);
            bean.setCdeTxtContato(cdeTxtContato);
            bean.setCdeRanking(cdeRanking);
            bean.setCdeVlrTac(cdeVlrTac);
            bean.setCdeVlrIof(cdeVlrIof);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
