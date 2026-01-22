package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ContratoBeneficioHome</p>
 * <p>Description: Classe Home da entidade ContratoBeneficio</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ContratoBeneficioHome extends AbstractEntityHome {

    public static ContratoBeneficio findByPrimaryKey(String cbeCodigo) throws FindException {
        ContratoBeneficio contratoBeneficio = new ContratoBeneficio();
        contratoBeneficio.setCbeCodigo(cbeCodigo);

        return find(contratoBeneficio, cbeCodigo);
    }

    public static List<ContratoBeneficio> findByBfcCodigoAndTntCodigoAndSadCodigo(String bfcCodigo, List<String> tntCodigo, List<String> sadCodigo) throws FindException {
        String query = "SELECT cbe "
                + "FROM AutDesconto ade "
                + "INNER JOIN ade.statusAutorizacaoDesconto sad "
                + "INNER JOIN ade.tipoLancamento tla "
                + "INNER JOIN tla.tipoNatureza tnt "
                + "INNER JOIN ade.contratoBeneficio cbe "
                + "INNER JOIN cbe.beneficiario bfc "
                + "WHERE bfc.bfcCodigo = :codigo "
                + "AND tnt.tntCodigo in (:tntCodigo) "
                + "AND sad.sadCodigo in (:sadCodigo) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("codigo", bfcCodigo);
        parameters.put("tntCodigo", tntCodigo);
        parameters.put("sadCodigo", sadCodigo);

        List<ContratoBeneficio> contratoBeneficios = findByQuery(query, parameters);
        if (contratoBeneficios == null || contratoBeneficios.size() == 0) {
            return null;
        } else {
          return contratoBeneficios;
        }
    }

    /**
     * Procura na ta_contrato_beneficio todos os contratos menor que a data infomada
     * Tem o @SuppressWarnings porque o forRevisionsOfEntity pode voltar tanto o Objeto ContratoBeneficio ou da RevisaoAuditoria.
     * @param cbeCodigo
     * @param dataLimiteRevision
     * @return
     */
    public static List<ContratoBeneficio> findRevisionLessThenDate(String cbeCodigo, Date dataLimiteRevision) {
        AuditReader auditReader = AuditReaderFactory.get(SessionUtil.getSession());

        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(ContratoBeneficio.class, true, true)
                .add(AuditEntity.revisionProperty("revData").lt(dataLimiteRevision))
                .add(AuditEntity.id().eq(cbeCodigo))
                .addOrder(AuditEntity.revisionProperty("revData").desc());


        return Collections.checkedList(auditQuery.getResultList(), ContratoBeneficio.class);
    }

    public static ContratoBeneficio create(Beneficiario beneficiario, Beneficio beneficio,
            String numero, Date dataInclusao, Date dataInicioVigencia, String itemLote,
            String numeroLote, BigDecimal valorTotal, BigDecimal valorSubsidio, StatusContratoBeneficio statusContratoBeneficio, Date dataCancelamento) throws CreateException {
        ContratoBeneficio contratoBeneficio = new ContratoBeneficio();

        try {
            contratoBeneficio.setCbeCodigo(DBHelper.getNextId());
            contratoBeneficio.setBeneficiario(beneficiario);
            contratoBeneficio.setBeneficio(beneficio);
            contratoBeneficio.setCbeNumero(numero);
            contratoBeneficio.setCbeDataInclusao(dataInclusao);
            contratoBeneficio.setCbeDataInicioVigencia(dataInicioVigencia);
            contratoBeneficio.setCbeItemLote(itemLote);
            contratoBeneficio.setCbeNumeroLote(numeroLote);
            contratoBeneficio.setCbeValorTotal(valorTotal);
            contratoBeneficio.setCbeValorSubsidio(valorSubsidio);
            contratoBeneficio.setStatusContratoBeneficio(statusContratoBeneficio);
            contratoBeneficio.setCbeDataCancelamento(dataCancelamento);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(contratoBeneficio);

        return contratoBeneficio;
    }

    public static void removeByBeneficiario(String bfcCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ContratoBeneficio cbe WHERE cbe.beneficiario.bfcCodigo = :bfcCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("bfcCodigo", bfcCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<ContratoBeneficio> findByBfcCodigoAndSadCodigo(String bfcCodigo, List<String> sadCodigo) throws FindException {
        String query = "SELECT cbe "
                + "FROM AutDesconto ade "
                + "INNER JOIN ade.statusAutorizacaoDesconto sad "
                + "INNER JOIN ade.contratoBeneficio cbe "
                + "INNER JOIN cbe.beneficiario bfc "
                + "WHERE bfc.bfcCodigo = :codigo "
                + "AND sad.sadCodigo in (:sadCodigo) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("codigo", bfcCodigo);
        parameters.put("sadCodigo", sadCodigo);

        List<ContratoBeneficio> contratoBeneficios = findByQuery(query, parameters);
        if (contratoBeneficios == null || contratoBeneficios.isEmpty()) {
            return Collections.emptyList();
        } else {
          return contratoBeneficios;
        }
    }

    public static List<ContratoBeneficio> findByBfcCodigoAndSadCodigoBenCodigo(String bfcCodigo, List<String> sadCodigo, String benCodigo) throws FindException {
        String query = "SELECT cbe "
                + "FROM AutDesconto ade "
                + "INNER JOIN ade.statusAutorizacaoDesconto sad "
                + "INNER JOIN ade.contratoBeneficio cbe "
                + "INNER JOIN cbe.beneficiario bfc "
                + "WHERE bfc.bfcCodigo = :codigo "
                + "AND sad.sadCodigo in (:sadCodigo) "
                + "AND cbe.benCodigo in (:benCodigo) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("codigo", bfcCodigo);
        parameters.put("sadCodigo", sadCodigo);
        parameters.put("benCodigo", benCodigo);

        List<ContratoBeneficio> contratoBeneficios = findByQuery(query, parameters);
        if (contratoBeneficios == null || contratoBeneficios.isEmpty()) {
            return Collections.emptyList();
        } else {
          return contratoBeneficios;
        }
    }
}
