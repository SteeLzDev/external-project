package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: PermissionarioHome</p>
 * <p>Description: Classe Home para a entidade Permissionario</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PermissionarioHome extends AbstractEntityHome {

    public static Permissionario findByPrimaryKey(String prmCodigo) throws FindException {
        Permissionario permissionario = new Permissionario();
        permissionario.setPrmCodigo(prmCodigo);
        return find(permissionario, prmCodigo);
    }

    public static Permissionario findByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM Permissionario prm WHERE prm.registroServidor.rseCodigo = :rseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        List<Permissionario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Permissionario findAtivoByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM Permissionario prm WHERE prm.registroServidor.rseCodigo = :rseCodigo AND prm.prmAtivo = :ativo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("ativo", CodedValues.STS_ATIVO.toString());

        List<Permissionario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Collection<Permissionario> findAtivoByRseCodigoAndCsaCodigo(String rseCodigo, String csaCodigo) throws FindException {
        String query = "FROM Permissionario prm WHERE prm.registroServidor.rseCodigo = :rseCodigo AND prm.prmAtivo = :ativo";
        query += " AND prm.consignataria.csaCodigo = :csaCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("ativo", CodedValues.STS_ATIVO.toString());
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static Permissionario create(String rseCodigo, String csaCodigo, String echCodigo, String prmTelefone, String prmEmail,
                                        String prmComplEndereco, Date prmDataOcupacao, Date prmDataDesocupacao, String prmEmTransferencia) throws CreateException {

        Session session = SessionUtil.getSession();
        Permissionario bean = new Permissionario();

        try {
            String objectId = DBHelper.getNextId();
            bean.setPrmCodigo(objectId);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setEnderecoConjHabitacional(session.getReference(EnderecoConjHabitacional.class, echCodigo));
            bean.setPrmTelefone(prmTelefone);
            bean.setPrmEmail(prmEmail);
            bean.setPrmComplEndereco(prmComplEndereco);
            bean.setPrmDataCadastro(DateHelper.getSystemDatetime());
            bean.setPrmDataOcupacao(prmDataOcupacao);
            bean.setPrmDataDesocupacao(prmDataDesocupacao);
            bean.setPrmEmTransferencia(prmEmTransferencia);
            bean.setPrmAtivo(CodedValues.STS_ATIVO);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM Permissionario prm WHERE prm.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
