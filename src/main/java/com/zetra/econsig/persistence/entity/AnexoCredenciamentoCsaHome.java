package com.zetra.econsig.persistence.entity;

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
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AnexoComunicacaoHome</p>
 * <p>Description: Classe Home para a entidade AnexoComunicacaoHome</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 */

public class AnexoCredenciamentoCsaHome extends AbstractEntityHome {

    public static AnexoCredenciamentoCsa findByPrimaryKey(String ancCodigo) throws FindException {
    	AnexoCredenciamentoCsa anexoCredenciamentoCsa = new AnexoCredenciamentoCsa();
    	anexoCredenciamentoCsa.setAncCodigo(ancCodigo);
        return find(anexoCredenciamentoCsa, ancCodigo);
    }

    public static List<AnexoCredenciamentoCsa> findByCreCodigo(String creCodigo) throws FindException {
        String query = "FROM AnexoCredenciamentoCsa anc "
        		+ "JOIN FETCH anc.tipoArquivo "
        		+ "JOIN FETCH anc.usuario "
        		+ "WHERE anc.creCodigo = :creCodigo "
        		+ "ORDER BY anc.ancData DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("creCodigo", creCodigo);

        return findByQuery(query, parameters);
    }

    public static AnexoCredenciamentoCsa create(String ancNome, String creCodigo, String tarCodigo, AcessoSistema responsavel) throws CreateException {

        Session session = SessionUtil.getSession();
        AnexoCredenciamentoCsa bean = new AnexoCredenciamentoCsa();

        try {
            bean.setAncCodigo(DBHelper.getNextId());
            bean.setUsuCodigo(responsavel.getUsuCodigo());
            bean.setCreCodigo(creCodigo);
            bean.setTarCodigo(tarCodigo);
            bean.setAncNome(ancNome);
            bean.setAncAtivo(CodedValues.STS_ATIVO);
            bean.setAncData(DateHelper.getSystemDatetime());
            bean.setAncIpAcesso(responsavel.getIpUsuario());
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static List<AnexoCredenciamentoCsa> findByCreCodigoTarCodigo(String creCodigo, String tarCodigo) throws FindException {
        String query = "FROM AnexoCredenciamentoCsa anc "
                + "JOIN FETCH anc.tipoArquivo "
                + "JOIN FETCH anc.usuario "
                + "WHERE anc.creCodigo = :creCodigo "
                + "AND anc.tarCodigo = :tarCodigo "
                + "ORDER BY anc.ancData DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("creCodigo", creCodigo);
        parameters.put("tarCodigo", tarCodigo);

        return findByQuery(query, parameters);
    }
}