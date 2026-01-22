package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade RegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroServidorHome extends AbstractEntityHome {

    public static RegistroServidor findByPrimaryKey(String rseCodigo) throws FindException {
        final RegistroServidor registroServidor = new RegistroServidor();
        registroServidor.setRseCodigo(rseCodigo);
        return find(registroServidor, rseCodigo);
    }

    public static RegistroServidor findByPrimaryKeyForUpdate(String rseCodigo) throws FindException {
        final RegistroServidor registroServidor = new RegistroServidor();
        registroServidor.setRseCodigo(rseCodigo);
        return find(registroServidor, rseCodigo, true);
    }

    public static List<RegistroServidor> findBySerCodigo(String serCodigo) throws FindException {
        final String query = "FROM RegistroServidor rse WHERE rse.servidor.serCodigo = :serCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static List<RegistroServidor> findByMatricula(String rseMatricula) throws FindException {
        final String query = "FROM RegistroServidor rse WHERE rse.rseMatricula = :rseMatricula";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseMatricula", rseMatricula);

        return findByQuery(query, parameters);
    }

    public static RegistroServidor findByMatriculaOrgao(String rseMatricula, String orgCodigo) throws FindException {
        final String query = "FROM RegistroServidor rse WHERE rse.rseMatricula = :rseMatricula AND rse.orgao.orgCodigo = :orgCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseMatricula", rseMatricula);
        parameters.put("orgCodigo", orgCodigo);

        final List<RegistroServidor> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static RegistroServidor findByMatriculaOrgaoFetchAutDesconto(String rseMatricula, String orgCodigo) throws FindException {
        final String query = "FROM RegistroServidor rse LEFT JOIN FETCH rse.autDescontoSet ade WHERE rse.rseMatricula = :rseMatricula AND rse.orgao.orgCodigo = :orgCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseMatricula", rseMatricula);
        parameters.put("orgCodigo", orgCodigo);

        final List<RegistroServidor> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static RegistroServidor findByAutDesconto(String adeCodigo) throws FindException {
        final String query = "SELECT rse FROM RegistroServidor rse INNER JOIN rse.autDescontoSet ade WHERE ade.adeCodigo = :adeCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        final List<RegistroServidor> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static RegistroServidor create(String serCodigo, String orgCodigo, String srsCodigo, String rseMatricula,
            BigDecimal rseMargem, BigDecimal rseMargemRest, BigDecimal rseMargemUsada,
            BigDecimal rseMargem2, BigDecimal rseMargemRest2, BigDecimal rseMargemUsada2,
            BigDecimal rseMargem3, BigDecimal rseMargemRest3, BigDecimal rseMargemUsada3,
            String rseTipo, Integer rsePrazo, Date rseDataAdmissao, String rseCLT,
            Short rseParamQtdAdeDefault, Short bcoCodigo, String rseObs, String rseAssociado,
            String rseEstabilizado, Date rseDataCarga, Date rseDataFimEngajamento, Date rseDataLimitePermanencia,
            String rseBancoSal, String rseAgenciaSal, String rseAgenciaDvSal, String rseContaSal, String rseContaDvSal,
            String rseBancoSal2, String rseAgenciaSal2, String rseAgenciaDvSal2, String rseContaSal2, String rseContaDvSal2,
            BigDecimal rseSalario, BigDecimal rseProventos, BigDecimal rseDescontosComp, BigDecimal rseDescontosFacu, BigDecimal rseOutrosDescontos,
            String crsCodigo, String prsCodigo, String sboCodigo, String uniCodigo, String vrsCodigo, String posCodigo, String trsCodigo, String capCodigo,
            String rsePraca, String rseBeneficiarioFinanDvCart, String rseMunicipioLotacao, String rseMatriculaInst, Date rseDataCtc, BigDecimal rseBaseCalculo,
                                          String rsePedidoDemissao, Date rseDataSaida, Date rseDataUltSalario, Date rseDataRetorno, String rseMotivoFaltaMargem) throws CreateException {

        final Session session = SessionUtil.getSession();

        final RegistroServidor bean = new RegistroServidor();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setRseCodigo(objectId);
            bean.setRseAuditoriaTotal("N"); // Valor Default
            bean.setServidor(session.getReference(Servidor.class, serCodigo));
            bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
            bean.setStatusRegistroServidor(session.getReference(StatusRegistroServidor.class, srsCodigo));
            bean.setRseMatricula(rseMatricula);
            bean.setRseMargem(rseMargem);
            bean.setRseMargemRest(rseMargemRest);
            bean.setRseMargemUsada(rseMargemUsada);
            bean.setRseMargem2(rseMargem2);
            bean.setRseMargemRest2(rseMargemRest2);
            bean.setRseMargemUsada2(rseMargemUsada2);
            bean.setRseMargem3(rseMargem3);
            bean.setRseMargemRest3(rseMargemRest3);
            bean.setRseMargemUsada3(rseMargemUsada3);
            bean.setRseTipo(rseTipo);
            bean.setRsePrazo(rsePrazo);
            bean.setRseDataAdmissao(rseDataAdmissao);
            bean.setRseClt(rseCLT);
            bean.setRseParamQtdAdeDefault(rseParamQtdAdeDefault);
            bean.setRseObs(rseObs);
            bean.setRseAssociado(rseAssociado);
            bean.setRseEstabilizado(rseEstabilizado);
            bean.setRseDataCarga(rseDataCarga);
            bean.setRseDataFimEngajamento(rseDataFimEngajamento);
            bean.setRseDataLimitePermanencia(rseDataLimitePermanencia);
            bean.setRseBancoSal(rseBancoSal);
            bean.setRseAgenciaSal(rseAgenciaSal);
            bean.setRseAgenciaDvSal(rseAgenciaDvSal);
            bean.setRseContaSal(rseContaSal);
            bean.setRseContaDvSal(rseContaDvSal);
            bean.setRseBancoSal2(rseBancoSal2);
            bean.setRseAgenciaSal2(rseAgenciaSal2);
            bean.setRseAgenciaDvSal2(rseAgenciaDvSal2);
            bean.setRseContaSal2(rseContaSal2);
            bean.setRseContaDvSal2(rseContaDvSal2);
            bean.setRseSalario(rseSalario);
            bean.setRseProventos(rseProventos);
            bean.setRseDescontosComp(rseDescontosComp);
            bean.setRseDescontosFacu(rseDescontosFacu);
            bean.setRseOutrosDescontos(rseOutrosDescontos);
            bean.setRsePraca(rsePraca);
            bean.setRseBeneficiarioFinanDvCart(rseBeneficiarioFinanDvCart);
            bean.setRseMunicipioLotacao(rseMunicipioLotacao);
            bean.setRseMatriculaInst(rseMatriculaInst);
            bean.setRseDataCtc(rseDataCtc);
            bean.setRseBaseCalculo(rseBaseCalculo);
            bean.setRsePedidoDemissao(rsePedidoDemissao);
            bean.setRseDataSaida(rseDataSaida);
            bean.setRseDataUltSalario(rseDataUltSalario);
            bean.setRseDataRetorno(rseDataRetorno);

            if (bcoCodigo != null) {
                bean.setBanco(session.getReference(Banco.class, bcoCodigo));
            }
            if (crsCodigo != null) {
                bean.setCargoRegistroServidor(session.getReference(CargoRegistroServidor.class, crsCodigo));
            }
            if (prsCodigo != null) {
                bean.setPadraoRegistroServidor(session.getReference(PadraoRegistroServidor.class, prsCodigo));
            }
            if (sboCodigo != null) {
                bean.setSubOrgao(session.getReference(SubOrgao.class, sboCodigo));
            }
            if (uniCodigo != null) {
                bean.setUnidade(session.getReference(Unidade.class, uniCodigo));
            }
            if (vrsCodigo != null) {
                bean.setVinculoRegistroServidor(session.getReference(VinculoRegistroServidor.class, vrsCodigo));
            }
            if (posCodigo != null) {
                bean.setPostoRegistroServidor(session.getReference(PostoRegistroServidor.class, posCodigo));
            }
            if (trsCodigo != null) {
                bean.setTipoRegistroServidor(session.getReference(TipoRegistroServidor.class, trsCodigo));
            }
            if (capCodigo != null) {
                bean.setCapacidadeRegistroSer(session.getReference(CapacidadeRegistroSer.class, capCodigo));
            }

            bean.setRseMotivoFaltaMargem(rseMotivoFaltaMargem);
            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static RegistroServidor create(String serCodigo, String orgCodigo, String srsCodigo, String rseMatricula,
            BigDecimal rseMargem, BigDecimal rseMargemRest, BigDecimal rseMargemUsada) throws CreateException {

        final Session session = SessionUtil.getSession();
        final RegistroServidor bean = new RegistroServidor();

        try {
            final String objectId = DBHelper.getNextId();
            bean.setRseCodigo(objectId);
            bean.setRseAuditoriaTotal("N"); // Valor Default
            bean.setServidor(session.getReference(Servidor.class, serCodigo));
            bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
            bean.setStatusRegistroServidor(session.getReference(StatusRegistroServidor.class, srsCodigo));
            bean.setRseMatricula(rseMatricula);
            bean.setRseMargem(rseMargem);
            bean.setRseMargemRest(rseMargemRest);
            bean.setRseMargemUsada(rseMargemUsada);
            create(bean, session);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void excluirRegistroServidor(String rseCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("UPDATE RegistroServidor rse SET ");
            hql.append("rse.statusRegistroServidor.srsCodigo = :srsCodigo, ");
            hql.append("rse.rseDataCarga = NULL ");

            if (!ParamSist.paramEquals(CodedValues.TPC_ZERAR_MARGEM_AO_EXCLUIR_SERVIDOR, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
                hql.append(", rse.rseMargem  = 0, rse.rseMargemRest  = 0 - rse.rseMargemUsada  ");
                hql.append(", rse.rseMargem2 = 0, rse.rseMargemRest2 = 0 - rse.rseMargemUsada2 ");
                hql.append(", rse.rseMargem3 = 0, rse.rseMargemRest3 = 0 - rse.rseMargemUsada3 ");
            }

            hql.append("WHERE rse.rseCodigo = :rseCodigo");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("rseCodigo", rseCodigo);
            queryUpdate.setParameter("srsCodigo", CodedValues.SRS_EXCLUIDO);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void alterarStatusRegistroServidor(String rseCodigo, String srsCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("UPDATE RegistroServidor rse ");
            hql.append("SET rse.statusRegistroServidor.srsCodigo = :srsCodigo ");
            hql.append("WHERE rse.rseCodigo = :rseCodigo");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("rseCodigo", rseCodigo);
            queryUpdate.setParameter("srsCodigo", srsCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void bloquearRegistroServidor(String rseCodigo) throws UpdateException {
        alterarStatusRegistroServidor(rseCodigo, CodedValues.SRS_BLOQUEADO);
    }

    public static void bloquearRegistroServidorPorSeguranca(String rseCodigo) throws UpdateException {
        alterarStatusRegistroServidor(rseCodigo, CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
    }

    public static void alterarPontuacaoRegistroServidor(String rseCodigo, Integer rsePontuacao) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final MutationQuery queryUpdate = session.createMutationQuery("UPDATE RegistroServidor rse SET rse.rsePontuacao = :rsePontuacao WHERE rse.rseCodigo = :rseCodigo");
            queryUpdate.setParameter("rseCodigo", rseCodigo);
            queryUpdate.setParameter("rsePontuacao", rsePontuacao);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
