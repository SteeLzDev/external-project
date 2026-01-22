package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaDadosAutorizacaoHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaDadosAde</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaDadosAutorizacaoHome extends AbstractEntityHome {

    public static OcorrenciaDadosAde findByPrimaryKey(String odaCodigo) throws FindException {
    	OcorrenciaDadosAde ocorrenciaAutorizacao = new OcorrenciaDadosAde();
    	ocorrenciaAutorizacao.setOdaCodigo(odaCodigo);
        return find(ocorrenciaAutorizacao, odaCodigo);
    }

    public static List<OcorrenciaDadosAde> findByAdeCodigo(String adeCodigo) throws FindException {
        String query = "FROM OcorrenciaDadosAde oda WHERE oda.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);

        return findByQuery(query, parameters);
    }


    public static List<OcorrenciaDadosAde> findByAdeTocCodigo(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaDadosAde oda WHERE oda.autDesconto.adeCodigo = :adeCodigo AND oda.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaDadosAde> findByAdeTocCodigoOrdenado(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaDadosAde oda WHERE oda.autDesconto.adeCodigo = :adeCodigo AND oda.tipoOcorrencia.tocCodigo = :tocCodigo order by oda.odaData desc";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaDadosAde> findByAdeTocCodigo(String adeCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaDadosAde oda WHERE oda.autDesconto.adeCodigo = :adeCodigo AND oda.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaDadosAde> findByAdeTocCodigoOrdenado(String adeCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaDadosAde oda WHERE oda.autDesconto.adeCodigo = :adeCodigo AND oda.tipoOcorrencia.tocCodigo IN (:tocCodigos) order by oda.odaData desc";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaDadosAde create(String adeCodigo, String tocCodigo, String usuCodigo, String tdaCodigo, Date odaData, String odaObs, String odaValorAnt, String odaValorNovo, String odaIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaDadosAde bean = new OcorrenciaDadosAde();

        try {
            String objectId = DBHelper.getNextId();
            bean.setOdaCodigo(objectId);
            bean.setAutDesconto((AutDesconto) session.getReference(AutDesconto.class, adeCodigo));
            bean.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setTipoDadoAdicional((TipoDadoAdicional) session.getReference(TipoDadoAdicional.class, tdaCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            if (odaData == null) {
                bean.setOdaData(Calendar.getInstance().getTime());
            } else {
                bean.setOdaData(odaData);
            }
            bean.setOdaObs(odaObs);
            bean.setOdaValorAnt(odaValorAnt);
            bean.setOdaValorNovo(odaValorNovo);
            bean.setOdaIpAcesso(odaIpAcesso);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
