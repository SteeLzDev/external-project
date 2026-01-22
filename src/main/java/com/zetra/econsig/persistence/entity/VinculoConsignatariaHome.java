package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VinculoConsignatariaHome extends AbstractEntityHome {

    public static VinculoConsignataria find(String vcsCodigo) throws FindException {
        VinculoConsignataria vinculoConsignataria = new VinculoConsignataria();
        vinculoConsignataria.setVcsCodigo(vcsCodigo);
        return find(vinculoConsignataria, vcsCodigo);
    }

    public static VinculoConsignataria create(VinculoConsignataria bean) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            bean.setVcsCodigo(DBHelper.getNextId());
            create(bean, session);
            return bean;
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

    }

    public static List<VinculoConsignataria> listVinculosCsa(String csaCodigo) throws FindException {
        String query = "FROM VinculoConsignataria csaVin WHERE csaVin.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static int verifyDuplic(String descricao, String identificador, String csaCodigo) throws FindException {
        String queryDes = "FROM VinculoConsignataria csaVin WHERE csaVin.vcsDescricao = :vcsDescricao and csaVin.csaCodigo = :csaCodigo";
        String queryIden = "FROM VinculoConsignataria csaVin WHERE csaVin.vcsIdentificador = :vcsIdentificador and csaVin.csaCodigo = :csaCodigo";

        int verifyCode = 0;
        if (!descricao.isEmpty()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("csaCodigo", csaCodigo);
            parameters.put("vcsDescricao", descricao);
            List<VinculoConsignataria> result = findByQuery(queryDes, parameters);
            if (!result.isEmpty()) {
                verifyCode = 1;
            }
        }

        if (!identificador.isEmpty()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("csaCodigo", csaCodigo);
            parameters.put("vcsIdentificador", identificador);
            List<VinculoConsignataria> result = findByQuery(queryIden, parameters);
            if (!result.isEmpty()) {
                verifyCode = 2;
            }
        }

        return verifyCode;
    }

    public static List<VinculoConsignataria> findByVrsAndCsa(String csaCodigo, String vrsCodigo) throws FindException  {
        String query = "FROM VinculoConsignataria tvc INNER JOIN VinculoCsaRse tvcr ON tvcr.vcsCodigo = tvc.vcsCodigo WHERE tvc.csaCodigo = :csaCodigo AND tvcr.vrsCodigo = :vrsCodigo AND tvc.vcsAtivo = :vcsAtivo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("vrsCodigo", vrsCodigo);
        parameters.put("vcsAtivo",  CodedValues.VCS_ATIVO);

        return findByQuery(query, parameters);
    }



}
