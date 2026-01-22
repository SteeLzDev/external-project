package com.zetra.econsig.persistence.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: BancoHome</p>
 * <p>Description: Classe Home para a entidade Banco</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BancoHome extends AbstractEntityHome {

    public static Banco findByPrimaryKey(Short bcoCodigo) throws FindException {
        final Banco banco = new Banco();
        banco.setBcoCodigo(bcoCodigo);
        return find(banco, bcoCodigo);
    }

    public static void updateBcoCodigo(List<String> bcoCodigos) throws UpdateException{
        final List<Short> bcoCodigosShort = bcoCodigos.stream().map(Short::parseShort).collect(Collectors.toList());
        final Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            if(bcoCodigosShort.isEmpty()) {
                hql.append("UPDATE Banco bco SET bco.bcoFolha = 'N'");
                MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
                queryUpdate.executeUpdate();
                session.flush();
            }else {
                hql.append("UPDATE Banco bco SET bco.bcoFolha = 'N'");
                MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
                queryUpdate.executeUpdate();

                hql = new StringBuilder();
                hql.append("UPDATE Banco bco SET bco.bcoFolha = 'S' WHERE bco.bcoCodigo in (:bcoCodigosShort) ");
                queryUpdate = session.createMutationQuery(hql.toString());
                queryUpdate.setParameter("bcoCodigosShort", bcoCodigosShort);
                queryUpdate.executeUpdate();
                session.flush();
            }
        }catch (final Exception ex) {
            throw new UpdateException(ex);
        }finally{
            SessionUtil.closeSession(session);
        }

    }

    public static List<Banco> findAll() throws FindException {
        final String query = "FROM Banco banco";

        return findByQuery(query, null);
    }

    public static List<Banco> findAllActive() throws FindException {
        final String query = "FROM Banco banco where bcoAtivo = true";

        return findByQuery(query, null);
    }

    public static List<Banco> findAllFolha() throws FindException {
        final String query = "FROM Banco banco where bcoAtivo = true and bcoFolha = 'S'";

        return findByQuery(query, null);
    }

    public static Banco create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
