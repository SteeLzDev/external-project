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
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.NaturezaPlanoEnum;

/**
 * <p>Title: PlanoHome</p>
 * <p>Description: Classe Home para a entidade Plano</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PlanoHome extends AbstractEntityHome {

    public static Plano findByPrimaryKey(String plaCodigo) throws FindException {
        String query = "FROM Plano pla WHERE pla.plaCodigo = :plaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("plaCodigo", plaCodigo);

        List<Plano> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Plano findByIdn(String plaIdentificador) throws FindException {
        String query = "FROM Plano pla WHERE pla.plaIdentificador = :plaIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("plaIdentificador", plaIdentificador);

        List<Plano> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Plano> findByNatureza(NaturezaPlanoEnum natureza) throws FindException {
        String query = "FROM Plano pla WHERE pla.naturezaPlano.nplCodigo = :nplCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nplCodigo", natureza.getCodigo());

        return findByQuery(query, parameters);
    }

    public static Plano create(String svcCodigo, String csaCodigo, String nplCodigo, String plaIdentificador,
                               String plaDescricao, Short plaAtivo) throws CreateException {

        Session session = SessionUtil.getSession();
        Plano bean = new Plano();

        try {
            bean.setPlaCodigo(DBHelper.getNextId());
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setNaturezaPlano(session.getReference(NaturezaPlano.class, nplCodigo));
            bean.setPlaIdentificador(plaIdentificador);
            bean.setPlaDescricao(plaDescricao);
            bean.setPlaAtivo(plaAtivo);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
