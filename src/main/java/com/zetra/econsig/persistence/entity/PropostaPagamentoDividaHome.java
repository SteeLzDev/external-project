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
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.StatusPropostaEnum;

/**
 * <p>Title: PropostaPagamentoDividaHome</p>
 * <p>Description: Classe Home para a entidade PropostaPagamentoDivida</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PropostaPagamentoDividaHome extends AbstractEntityHome {

    public static PropostaPagamentoDivida findByPrimaryKey(String ppdCodigo) throws FindException {
        PropostaPagamentoDivida propostaPagamentoDivida = new PropostaPagamentoDivida();
        propostaPagamentoDivida.setPpdCodigo(ppdCodigo);
        return find(propostaPagamentoDivida, ppdCodigo);
    }

    public static List<PropostaPagamentoDivida> findByAdeCsaAusentes(String adeCodigo, String csaCodigo, List<String> ppdCodigo) throws FindException {
        String query = "FROM PropostaPagamentoDivida ppd WHERE ppd.autDesconto.adeCodigo = :adeCodigo AND ppd.consignataria.csaCodigo = :csaCodigo AND ppd.ppdCodigo NOT IN (:ppdCodigo)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("ppdCodigo", ppdCodigo);

        return findByQuery(query, parameters);
    }

    public static List<PropostaPagamentoDivida> findByAdeCsa(String adeCodigo, String csaCodigo) throws FindException {
        String query = "FROM PropostaPagamentoDivida ppd WHERE ppd.autDesconto.adeCodigo = :adeCodigo AND ppd.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static List<PropostaPagamentoDivida> findPendentesByAde(String adeCodigo) throws FindException {
        String query = "FROM PropostaPagamentoDivida ppd WHERE ppd.autDesconto.adeCodigo = :adeCodigo AND ppd.statusProposta.stpCodigo IN (:stpCodigo)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("stpCodigo", StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo());

        return findByQuery(query, parameters);
    }

    public static PropostaPagamentoDivida create(String adeCodigo, String csaCodigo, String usuCodigo, String stpCodigo, Integer ppdNumero,
            BigDecimal ppdValorDivida, BigDecimal ppdValorParcela, Integer ppdPrazo, BigDecimal ppdTaxaJuros, Date ppdDataCadastro, Date ppdDataValidade) throws CreateException {
        Session session = SessionUtil.getSession();
        PropostaPagamentoDivida bean = new PropostaPagamentoDivida();

        try {
            String objectId = DBHelper.getNextId();
            bean.setPpdCodigo(objectId);
            bean.setAutDesconto((AutDesconto) session.getReference(AutDesconto.class, adeCodigo));
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setStatusProposta((StatusProposta) session.getReference(StatusProposta.class, stpCodigo));
            bean.setPpdNumero(ppdNumero);
            bean.setPpdValorDivida(ppdValorDivida);
            bean.setPpdValorParcela(ppdValorParcela);
            bean.setPpdPrazo(ppdPrazo);
            bean.setPpdTaxaJuros(ppdTaxaJuros);
            bean.setPpdDataCadastro((ppdDataCadastro != null) ? ppdDataCadastro : DateHelper.getSystemDatetime());
            bean.setPpdDataValidade(ppdDataValidade);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
