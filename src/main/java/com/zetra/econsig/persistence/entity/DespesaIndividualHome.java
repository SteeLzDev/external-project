package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DespesaIndividualHome</p>
 * <p>Description: Classe Home para a entidade Despesa Individual</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DespesaIndividualHome extends AbstractEntityHome {

    public static DespesaIndividual findByPrimaryKey(String adeCodigo) throws FindException {
        DespesaIndividual despesaIndividual = new DespesaIndividual();
        despesaIndividual.setAdeCodigo(adeCodigo);
        return find(despesaIndividual, adeCodigo);
    }

    public static DespesaIndividual findByPrmCodigo(String prmCodigo) throws FindException {
        String query = "FROM DespesaIndividual des WHERE des.permissionario.prmCodigo = :prmCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prmCodigo", prmCodigo);

        List<DespesaIndividual> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static DespesaIndividual create(String adeCodigo, String plaCodigo, String prmCodigo, String decCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        DespesaIndividual bean = new DespesaIndividual();

        try {
            bean.setAdeCodigo(adeCodigo);
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setPlano(session.getReference(Plano.class, plaCodigo));
            bean.setPermissionario(session.getReference(Permissionario.class, prmCodigo));
            if (!TextHelper.isNull(decCodigo)) {
                bean.setDespesaComum(session.getReference(DespesaComum.class, decCodigo));
            }
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
