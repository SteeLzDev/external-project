package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.geradoradenumero.AdeNumeroHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AutDescontoHome</p>
 * <p>Description: Classe Home para a entidade AutDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AutDescontoHome extends AbstractEntityHome {

    public static AutDesconto findByPrimaryKey(String adeCodigo) throws FindException {
        final AutDesconto autDesconto = new AutDesconto();
        autDesconto.setAdeCodigo(adeCodigo);
        return find(autDesconto, adeCodigo);
    }

    public static AutDesconto findArquivadoByPrimaryKey(String adeCodigo) throws FindException {
        final HtAutDesconto autDesconto = new HtAutDesconto();
        autDesconto.setAdeCodigo(adeCodigo);
        return new AutDesconto(find(autDesconto, adeCodigo));
    }

    public static AutDesconto findByPrimaryKeyForUpdate(String adeCodigo) throws FindException {
        final AutDesconto autDesconto = new AutDesconto();
        autDesconto.setAdeCodigo(adeCodigo);
        return find(autDesconto, adeCodigo, true);
    }

    public static AutDesconto findByAdeNumero(Long adeNumero) throws FindException {
        final String query = "FROM AutDesconto ade WHERE ade.adeNumero = :adeNumero";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeNumero", adeNumero);

        final List<AutDesconto> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<AutDesconto> findByAdeIdentificador(String adeIdentificador) throws FindException {
        final String query = "FROM AutDesconto ade WHERE ade.adeIdentificador = :adeIdentificador";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeIdentificador", adeIdentificador);

        return findByQuery(query, parameters);
    }

    public static List<AutDesconto> lstAdesNaoExportados() throws FindException {
        final String query = "FROM AutDesconto ade WHERE ade.tipoMotivoNaoExportacao.mneCodigo IS NOT NULL";

        return findByQuery(query, null);
    }

    public static List<AutDesconto> findByRseCodigo(String rseCodigo) throws FindException {
        final String query = "FROM AutDesconto ade WHERE ade.registroServidor.rseCodigo = :rseCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    public static List<HtAutDesconto> findArquivadasByRseCodigo(String rseCodigo) throws FindException {
        final String query = "FROM HtAutDesconto ade WHERE ade.registroServidor.rseCodigo = :rseCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    /**
     * Procura todos os servidores que pertencem a um serviço
     * Metodo utilizado basicamente na migração do beneficios da dados autorização desconto para as tabelas novas
     * @return
     * @throws FindException
     */
    public static List<AutDesconto> listaAutDescontoPorServicos(List<String> listaLikeServicosBeneficios, Servidor servidor) throws FindException {
        final Map<String, Object> parameters = new HashMap<>();

        String query = "SELECT DISTINCT ade FROM AutDesconto ade "
                + "JOIN FETCH ade.registroServidor rse "
                + "JOIN FETCH rse.servidor ser "
                + "JOIN FETCH ade.dadosAutorizacaoDescontoSet dad "
                + "JOIN FETCH dad.tipoDadoAdicional tda "
                + "JOIN FETCH ade.verbaConvenio vco "
                + "JOIN FETCH vco.convenio cnv "
                + "JOIN FETCH cnv.servico svc "
                + "JOIN FETCH cnv.consignataria csa "
                + "LEFT JOIN FETCH ade.contratoBeneficio cbe "
                + "LEFT JOIN FETCH cbe.beneficiario bfc "
                + "WHERE 1 = 1 ";

        query +="AND ser.serCodigo = :sercodigo AND ( ";
        parameters.put("sercodigo", servidor.getSerCodigo());

        for (final String svcIdentificador : listaLikeServicosBeneficios) {
            query += "svc.svcIdentificador like :"+svcIdentificador+" OR ";
            parameters.put(svcIdentificador, svcIdentificador.concat("%"));
        }
        query = query.substring(0, query.length()-4);
        query += " ) ORDER BY svc.svcIdentificador ASC, ade.adeData DESC, dad.tipoDadoAdicional DESC";

        return findByQuery(query, parameters);
    }

    public static AutDesconto create(String sadCodigo, String vcoCodigo, String rseCodigo, String corCodigo, String usuCodigo,
            String adeIdentificador, String adeIndice, String adeCodReg, Integer adePrazo, Integer adePrdPagas,
            Date adeAnoMesIni, Date adeAnoMesFim, Date adeAnoMesIniRef, Date adeAnoMesFimRef,
            BigDecimal adeVlr, BigDecimal adeVlrTac, BigDecimal adeVlrIof, BigDecimal adeVlrLiquido,
            BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, BigDecimal adeVlrSegPrestamista,
            String adeTipoVlr, Short adeIntFolha, Short adeIncMargem, Integer adeCarencia,
            Short adeCarenciaFinal, Timestamp adeDtHrOcorrencia, BigDecimal adeVlrSdoMov,
            BigDecimal adeVlrSdoRet, String adeBanco, String adeAgencia, String adeConta,
            String adeTipoTaxa, BigDecimal adeVlrPercentual, String adePeriodicidade, String cidCodigo) throws CreateException {

        return create(sadCodigo, vcoCodigo, rseCodigo, corCodigo, usuCodigo, adeIdentificador, adeIndice, adeCodReg, adePrazo,
                adePrdPagas, adeAnoMesIni, adeAnoMesFim, adeAnoMesIniRef, adeAnoMesFimRef, adeVlr, adeVlrTac, adeVlrIof, adeVlrLiquido,
                adeVlrMensVinc, adeTaxaJuros, adeVlrSegPrestamista, adeTipoVlr, adeIntFolha, adeIncMargem, adeCarencia, adeCarenciaFinal,
                adeDtHrOcorrencia, adeVlrSdoMov, adeVlrSdoRet, adeBanco, adeAgencia, adeConta, adeTipoTaxa, adeVlrPercentual, adePeriodicidade,
                cidCodigo, null, null);
    }

    public static AutDesconto create(String sadCodigo, String vcoCodigo, String rseCodigo, String corCodigo, String usuCodigo,
            String adeIdentificador, String adeIndice, String adeCodReg, Integer adePrazo, Integer adePrdPagas,
            Date adeAnoMesIni, Date adeAnoMesFim, Date adeAnoMesIniRef, Date adeAnoMesFimRef,
            BigDecimal adeVlr, BigDecimal adeVlrTac, BigDecimal adeVlrIof, BigDecimal adeVlrLiquido,
            BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, BigDecimal adeVlrSegPrestamista,
            String adeTipoVlr, Short adeIntFolha, Short adeIncMargem, Integer adeCarencia,
            Short adeCarenciaFinal, Timestamp adeDtHrOcorrencia, BigDecimal adeVlrSdoMov,
            BigDecimal adeVlrSdoRet, String adeBanco, String adeAgencia, String adeConta,
            String adeTipoTaxa, BigDecimal adeVlrPercentual, String adePeriodicidade, String cidCodigo, String cbeCodigo,
            String tlaCodigo) throws CreateException {
        final Session session = SessionUtil.getSession();
        final AutDesconto bean = new AutDesconto();

        final Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());
        try {
            bean.setAdeCodigo(DBHelper.getNextId());
            bean.setStatusAutorizacaoDesconto(session.getReference(StatusAutorizacaoDesconto.class, sadCodigo));
            bean.setVerbaConvenio(session.getReference(VerbaConvenio.class, vcoCodigo));
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            if (!TextHelper.isNull(corCodigo)) {
                bean.setCorrespondente(session.getReference(Correspondente.class, corCodigo));
            }
            bean.setAdeData(agora);
            bean.setAdeDataRef(agora);
            bean.setAdeVlr(adeVlr);
            bean.setAdeVlrRef(adeVlr);
            bean.setAdePrazo(adePrazo);
            bean.setAdePrazoRef(adePrazo);
            bean.setAdePrdPagas(adePrdPagas);
            bean.setAdePrdPagasTotal(adePrdPagas);
            bean.setAdeAnoMesIni(adeAnoMesIni);
            bean.setAdeAnoMesFim(adeAnoMesFim);
            bean.setAdeIdentificador(adeIdentificador);
            bean.setAdeTipoVlr(adeTipoVlr);
            bean.setAdeIntFolha(adeIntFolha);
            bean.setAdeIncMargem(adeIncMargem);
            bean.setAdeIndice(adeIndice);
            bean.setAdeVlrTac(adeVlrTac);
            bean.setAdeVlrIof(adeVlrIof);
            bean.setAdeVlrLiquido(adeVlrLiquido);
            bean.setAdeVlrMensVinc(adeVlrMensVinc);
            bean.setAdeVlrSegPrestamista(adeVlrSegPrestamista);
            bean.setAdeTaxaJuros(adeTaxaJuros);
            bean.setAdeDataHoraOcorrencia(adeDtHrOcorrencia);
            bean.setAdeCodReg(adeCodReg);
            bean.setAdeCarencia(adeCarencia);
            bean.setAdeCarenciaFinal(adeCarenciaFinal);
            bean.setAdePaga("N");
            bean.setAdeExportacao("S");
            bean.setAdeVlrSdoMov(adeVlrSdoMov);
            bean.setAdeVlrSdoRet(adeVlrSdoRet);
            bean.setAdeBanco(adeBanco);
            bean.setAdeAgencia(adeAgencia);
            bean.setAdeConta(adeConta);
            bean.setAdeTipoTaxa(adeTipoTaxa);
            bean.setAdeVlrPercentual(adeVlrPercentual);
            bean.setAdePeriodicidade(adePeriodicidade);
            if (cbeCodigo != null) {
                bean.setContratoBeneficio(session.getReference(ContratoBeneficio.class, cbeCodigo));
            }
            if (tlaCodigo != null) {
                bean.setTipoLancamento(session.getReference(TipoLancamento.class, tlaCodigo));
            }

            if (adeAnoMesIniRef != null) {
                bean.setAdeAnoMesIniRef(adeAnoMesIniRef);
                bean.setAdeAnoMesFimRef(adeAnoMesFimRef);
            } else {
                bean.setAdeAnoMesIniRef(adeAnoMesIni);
                bean.setAdeAnoMesFimRef(adeAnoMesFim);
            }

            final Long adeNumero = AdeNumeroHelper.getNext(vcoCodigo, adeAnoMesIni);
            if (adeNumero != null) {
                bean.setAdeNumero(adeNumero);
            }

            if (!TextHelper.isNull(cidCodigo)) {
                bean.setCidade(session.getReference(Cidade.class, cidCodigo));
            }

            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<AutDesconto> findByBeneficiarioAndInTntCodigoAndNotInSadCodigo(String bfcCodigo, List<String> tntCodigo, List<String> sadCodigo, AcessoSistema responsavel) throws FindException {
        final String query = "SELECT DISTINCT aut FROM AutDesconto aut "
                + "INNER JOIN aut.tipoLancamento tla "
                + "INNER JOIN tla.tipoNatureza tnt "
                + "INNER JOIN aut.statusAutorizacaoDesconto sad "
                + "INNER JOIN aut.contratoBeneficio cbe "
                + "INNER JOIN cbe.beneficiario bfc "
                + "WHERE sad.sadCodigo not in (:sadCodigo) "
                + "AND bfc.bfcCodigo = :bfcCodigo "
                + "AND tnt.tntCodigo in (:tntCodigo)";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("sadCodigo", sadCodigo);
        parameters.put("bfcCodigo", bfcCodigo);
        parameters.put("tntCodigo", tntCodigo);

        final List<AutDesconto> autDescontos = findByQuery(query, parameters);
        if ((autDescontos == null) || (autDescontos.size() == 0)) {
            return null;
        } else {
            return autDescontos;
        }
    }

    public static void updateAdeVlr(String adeCodigo, BigDecimal adeVlr) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE AutDesconto set adeVlr = :adeVlr WHERE adeCodigo = :adeCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("adeVlr", adeVlr);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateVerbaConvenio(String adeCodigo, String vcoCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE AutDesconto set verbaConvenio.vcoCodigo = :vcoCodigo WHERE adeCodigo = :adeCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("vcoCodigo", vcoCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateVerbaConvenioIncideMargem(String adeCodigo, String vcoCodigo, Short adeIncMargem) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE AutDesconto set verbaConvenio.vcoCodigo = :vcoCodigo, adeIncMargem = :adeIncMargem WHERE adeCodigo = :adeCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("vcoCodigo", vcoCodigo);
            queryUpdate.setParameter("adeIncMargem", adeIncMargem);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateNotificaCse(String adeCodigo, boolean inlcuir) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE AutDesconto set adeDataNotificacaoCse = :adeDataNotificacaoCse WHERE adeCodigo = :adeCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            if (inlcuir) {
                queryUpdate.setParameter("adeDataNotificacaoCse", DateHelper.getSystemDatetime());
            } else {
                queryUpdate.setParameter("adeDataNotificacaoCse", null);
            }
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateRegistraValorLiberadoConsignacao(String adeCodigo, boolean inlcuir) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            String hql;
            if (inlcuir) {
                hql = "UPDATE AutDesconto set adeDataLiberacaoValor = :adeDataLiberacaoValor WHERE adeCodigo = :adeCodigo";
            } else {
                hql = "UPDATE AutDesconto set adeDataLiberacaoValor = :adeDataLiberacaoValor, sadCodigo = :sadCodigo WHERE adeCodigo = :adeCodigo";
            }
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            if (inlcuir) {
                queryUpdate.setParameter("adeDataLiberacaoValor", DateHelper.getSystemDatetime());
            } else {
                queryUpdate.setParameter("adeDataLiberacaoValor", null);
                queryUpdate.setParameter("sadCodigo", CodedValues.SAD_AGUARD_DEFER);
            }
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateImpHistorico(String adeCodigo, String sadCodigo, Long adeNumero, Integer adePrdPagas, Integer adePrazoRef, Short adeIncMargem, java.util.Date adeData, java.util.Date adeDataRef, java.util.Date adeAnoMesIniFolha, java.util.Date adeAnoMesFimFolha, BigDecimal adeVlrFolha, BigDecimal adeVlrParcelaFolha, String adeIndice, String adeIndiceExp) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {

            final StringBuilder sql = new StringBuilder();
            sql.append("UPDATE AutDesconto ade set ");
            sql.append("ade.statusAutorizacaoDesconto.sadCodigo = :sadCodigo ");
            if (adeNumero != null) {
                sql.append(", ade.adeNumero = :adeNumero ");
            }
            if (adePrdPagas != null) {
                sql.append(", ade.adePrdPagas = :adePrdPagas ");
                sql.append(", ade.adePrdPagasTotal = :adePrdPagas ");
            }
            if (adePrazoRef != null) {
                sql.append(", ade.adePrazoRef = :adePrazoRef ");
            }
            if (adeIncMargem != null) {
                sql.append(", ade.adeIncMargem = :adeIncMargem ");
            }
            if (adeData != null) {
                sql.append(", ade.adeData = :adeData ");
            }
            if (adeDataRef != null) {
                sql.append(", ade.adeDataRef = :adeDataRef ");
            }
            if (adeAnoMesIniFolha != null) {
                sql.append(", ade.adeAnoMesIniFolha = :adeAnoMesIniFolha ");
            }
            if (adeAnoMesFimFolha != null) {
                sql.append(", ade.adeAnoMesFimFolha = :adeAnoMesFimFolha ");
            }
            if (adeVlrFolha != null) {
                sql.append(", ade.adeVlrFolha = :adeVlrFolha ");
            }
            if (adeVlrParcelaFolha != null) {
                sql.append(", ade.adeVlrParcelaFolha = :adeVlrParcelaFolha ");
            }
            if (!TextHelper.isNull(adeIndice)) {
                sql.append(", ade.adeIndice = :adeIndice ");
            }
            if (!TextHelper.isNull(adeIndiceExp)) {
                sql.append(", ade.adeIndiceExp = :adeIndiceExp ");
            }
            sql.append("WHERE adeCodigo = :adeCodigo");

            final MutationQuery queryUpdate = session.createMutationQuery(sql.toString());
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("sadCodigo", sadCodigo);

            if (adeNumero != null) {
                queryUpdate.setParameter("adeNumero", adeNumero);
            }
            if (adePrdPagas != null) {
                queryUpdate.setParameter("adePrdPagas", adePrdPagas);
            }
            if (adePrazoRef != null) {
                queryUpdate.setParameter("adePrazoRef", adePrazoRef);
            }
            if (adeIncMargem != null) {
                queryUpdate.setParameter("adeIncMargem", adeIncMargem);
            }
            if (adeData != null) {
                queryUpdate.setParameter("adeData", adeData);
            }
            if (adeDataRef != null) {
                queryUpdate.setParameter("adeDataRef", adeDataRef);
            }
            if (adeAnoMesIniFolha != null) {
                queryUpdate.setParameter("adeAnoMesIniFolha", adeAnoMesIniFolha);
            }
            if (adeAnoMesFimFolha != null) {
                queryUpdate.setParameter("adeAnoMesFimFolha", adeAnoMesFimFolha);
            }
            if (adeVlrFolha != null) {
                queryUpdate.setParameter("adeVlrFolha", adeVlrFolha);
            }
            if (adeVlrParcelaFolha != null) {
                queryUpdate.setParameter("adeVlrParcelaFolha", adeVlrParcelaFolha);
            }
            if (!TextHelper.isNull(adeIndice)) {
                queryUpdate.setParameter("adeIndice", adeIndice);
            }
            if (!TextHelper.isNull(adeIndiceExp)) {
                queryUpdate.setParameter("adeIndiceExp", adeIndiceExp);
            }

            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
    
    public static void updateAdeTaxaJuros(String adeCodigo, BigDecimal adeTaxaJuros) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final String hql = "UPDATE AutDesconto set adeTaxaJuros = :adeTaxaJuros WHERE adeCodigo = :adeCodigo";
            final MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("adeTaxaJuros", adeTaxaJuros);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<AutDesconto> listaAutDescontoByRseCodigoAndTocCodigo(String rseCodigo, String tocCodigo) throws FindException {
        final Map<String, Object> parameters = new HashMap<>();
        final StringBuilder query = new StringBuilder();

        query.append("SELECT DISTINCT ade FROM AutDesconto ade ");
        query.append("JOIN FETCH ade.registroServidor rse ");
        query.append("JOIN FETCH rse.servidor ser ");
        query.append("JOIN FETCH ade.verbaConvenio vco ");
        query.append("JOIN FETCH vco.convenio cnv ");
        query.append("JOIN FETCH cnv.consignataria csa ");
        query.append("INNER JOIN ade.ocorrenciaAutorizacaoSet oca  with oca.tocCodigo = :tocCodigo ");
        query.append("WHERE 1 = 1 ");
        if(!TextHelper.isNull(rseCodigo)) {
            query.append("AND rse.rseCodigo = :rseCodigo");
        }

        if(!TextHelper.isNull(rseCodigo)) {
            parameters.put("rseCodigo", rseCodigo);
        }
        parameters.put("tocCodigo", tocCodigo);
        return findByQuery(query.toString(), parameters);
    }
}
