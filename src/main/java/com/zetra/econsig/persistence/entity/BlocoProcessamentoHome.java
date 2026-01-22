package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: BlocoProcessamentoHome</p>
 * <p>Description: Classe Home para manutenção de blocos de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BlocoProcessamentoHome extends AbstractEntityHome {

    public static BlocoProcessamento create(StatusBlocoProcessamentoEnum status, TipoBlocoProcessamentoEnum tipo, Date bprPeriodo, Integer bprOrdemExecucao, String bprLinha, Integer bprNumLinha, String bprCampos,
            String cnvCodVerba, String svcIdentificador, String csaIdentificador, String estIdentificador, String orgIdentificador, String rseMatricula, String serCpf,
            Long adeNumero, String adeIndice, String rseCodigo, String cnvCodigo, String estCodigo, String orgCodigo, Session session) throws CreateException {

        BlocoProcessamento bean = new BlocoProcessamento();

        bean.setStatusBlocoProcessamento(session.getReference(StatusBlocoProcessamento.class, status.getCodigo()));
        bean.setTipoBlocoProcessamento(session.getReference(TipoBlocoProcessamento.class, tipo.getCodigo()));
        bean.setBprPeriodo(bprPeriodo);
        bean.setBprDataInclusao(new Date());
        bean.setBprOrdemExecucao(bprOrdemExecucao);
        bean.setBprLinha(bprLinha);
        bean.setBprNumLinha(bprNumLinha);
        bean.setBprCampos(bprCampos);
        bean.setCnvCodVerba(cnvCodVerba);
        bean.setSvcIdentificador(svcIdentificador);
        bean.setCsaIdentificador(csaIdentificador);
        bean.setEstIdentificador(estIdentificador);
        bean.setOrgIdentificador(orgIdentificador);
        bean.setRseMatricula(rseMatricula);
        bean.setSerCpf(serCpf);
        bean.setAdeNumero(adeNumero);
        bean.setAdeIndice(adeIndice);

        if (!TextHelper.isNull(rseCodigo)) {
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
        }
        if (!TextHelper.isNull(cnvCodigo)) {
            bean.setConvenio(session.getReference(Convenio.class, cnvCodigo));
        }
        if (!TextHelper.isNull(estCodigo)) {
            bean.setEstabelecimento(session.getReference(Estabelecimento.class, estCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
        }

        return create(bean, session, false);
    }

    public static BlocoProcessamento create(StatusBlocoProcessamentoEnum status, TipoBlocoProcessamentoEnum tipo, Date bprPeriodo, Integer bprOrdemExecucao, String bprLinha, Integer bprNumLinha, String bprCampos,
            String cnvCodVerba, String svcIdentificador, String csaIdentificador, String estIdentificador, String orgIdentificador, String rseMatricula, String serCpf,
            Long adeNumero, String adeIndice, String rseCodigo, String cnvCodigo, String estCodigo, String orgCodigo) throws CreateException {

        BlocoProcessamento bean = null;
        Session session = SessionUtil.getSession();

        try {
            bean = create(status, tipo, bprPeriodo, bprOrdemExecucao, bprLinha, bprNumLinha, bprCampos,
                    cnvCodVerba, svcIdentificador, csaIdentificador, estIdentificador, orgIdentificador, rseMatricula, serCpf,
                    adeNumero, adeIndice, rseCodigo, cnvCodigo, estCodigo, orgCodigo, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void atualizarStatusBloco(Integer bprCodigo, StatusBlocoProcessamentoEnum status, String bprMensagem) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("UPDATE BlocoProcessamento bpr SET ");
            hql.append("bpr.statusBlocoProcessamento.sbpCodigo = :sbpCodigo ");

            if (status.equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO) || status.equals(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO)) {
                hql.append(", bpr.bprDataProcessamento = current_timestamp ");
            }
            if (!TextHelper.isNull(bprMensagem)) {
                hql.append(", bpr.bprMensagem = :bprMensagem ");
            } else {
                hql.append(", bpr.bprMensagem = null ");
            }

            hql.append("WHERE bpr.bprCodigo = :bprCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("sbpCodigo", status.getCodigo());
            queryUpdate.setParameter("bprCodigo", bprCodigo);
            if (!TextHelper.isNull(bprMensagem)) {
                queryUpdate.setParameter("bprMensagem", bprMensagem);
            }

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void atualizarStatusBlocos(StatusBlocoProcessamentoEnum statusOrigem, StatusBlocoProcessamentoEnum statusDestino, String tipoEntidade, String codigoEntidade) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("UPDATE BlocoProcessamento bpr SET ");
            hql.append("bpr.statusBlocoProcessamento.sbpCodigo = :sbpCodigoDestino ");
            hql.append("WHERE 1=1 ");

            if (statusOrigem != null) {
                hql.append(" AND bpr.statusBlocoProcessamento.sbpCodigo = :sbpCodigoOrigem ");
            }

            if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                    hql.append(" AND bpr.orgao.orgCodigo = :codigoEntidade ");
                } else {
                    hql.append(" AND bpr.estabelecimento.estCodigo = :codigoEntidade ");
                }
            }

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("sbpCodigoDestino", statusDestino.getCodigo());

            if (statusOrigem != null) {
                queryUpdate.setParameter("sbpCodigoOrigem", statusOrigem.getCodigo());
            }

            if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
                queryUpdate.setParameter("codigoEntidade", codigoEntidade);
            }

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removerBlocos(String tipoEntidade, String codigoEntidade) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BlocoProcessamento bpr ");
            hql.append("WHERE 1=1 ");

            if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                    hql.append(" AND bpr.orgao.orgCodigo = :codigoEntidade ");
                } else {
                    hql.append(" AND bpr.estabelecimento.estCodigo = :codigoEntidade ");
                }
            }

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
                queryUpdate.setParameter("codigoEntidade", codigoEntidade);
            }

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removerBlocos(List<String> sbpCodigos) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BlocoProcessamento bpr ");
            hql.append("WHERE bpr.statusBlocoProcessamento.sbpCodigo IN (:sbpCodigos) ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("sbpCodigos", sbpCodigos);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BlocoProcessamento bpr WHERE bpr.registroServidor.rseCodigo = :rseCodigo ");

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
