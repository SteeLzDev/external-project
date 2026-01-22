package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RelacionamentoServicoBeneficioHome</p>
 * <p>Description: Classe home da entidade RelacionamentoServicoBeneficio</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelacionamentoBeneficioServicoHome extends AbstractEntityHome {

    public static List<BeneficioServico> findByBenCodigo(String benCodigo) throws FindException {
        String query = "FROM BeneficioServico bse WHERE bse.beneficio.benCodigo = :benCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("benCodigo", benCodigo);

        return findByQuery(query, parameters);
    }

    public static List<BeneficioServico> findByBenCodigoTibCodigo(String benCodigo, String tibCodigo) throws FindException {
        String query = "FROM BeneficioServico bse "
                     + "WHERE bse.beneficio.benCodigo = :benCodigo "
                     + "AND bse.tipoBeneficiario.tibCodigo = :tibCodigo "
                     + "ORDER BY bse.bseOrdem ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("benCodigo", benCodigo);
        parameters.put("tibCodigo", tibCodigo);

        return findByQuery(query, parameters);
    }

    public static BeneficioServico createByObject(BeneficioServico relacionamentoBeneficioServico) throws CreateException {
        BeneficioServico relBenSvc = create(relacionamentoBeneficioServico);
        return relBenSvc;
    }

    public static void deleteRelacionamentoBeneficioServicoByBenCodigo(String benCodigo) {
        Session session = SessionUtil.getSession();
        String queryDelete = "DELETE FROM BeneficioServico rel " + "WHERE rel.beneficio.benCodigo = :benCodigo";
        MutationQuery query = session.createMutationQuery(queryDelete);
        query.setParameter("benCodigo", benCodigo);
        query.executeUpdate();
        SessionUtil.closeSession(session);
    }

    public static BeneficioServico findByIdn(String benCodigo, String svcCodigo, String tibCodigo) throws FindException {
        String query = "FROM BeneficioServico rel "
                + "WHERE rel.beneficio.benCodigo = :benCodigo "
                + "AND rel.tipoBeneficiario.tibCodigo = :tibCodigo "
                + "AND rel.servico.svcCodigo = :svcCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("benCodigo", benCodigo);
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("tibCodigo", tibCodigo);

        List<BeneficioServico> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
   }


}
