package com.zetra.econsig.service.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.LogDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.CorrespondenteHome;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.GrupoFuncaoHome;
import com.zetra.econsig.persistence.entity.LogHome;
import com.zetra.econsig.persistence.entity.MargemHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.PapelHome;
import com.zetra.econsig.persistence.entity.PerfilHome;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.StatusParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.StatusRegistroServidorHome;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.persistence.entity.TipoEntidadeHome;
import com.zetra.econsig.persistence.entity.TipoLog;
import com.zetra.econsig.persistence.entity.TipoOcorrenciaHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidorHome;
import com.zetra.econsig.persistence.query.admin.ListaTipoEntidadeQuery;
import com.zetra.econsig.persistence.query.log.ListaHistArqLogPorPeriodoQuery;
import com.zetra.econsig.persistence.query.log.ListaTipoEntidadeLogQuery;
import com.zetra.econsig.persistence.query.log.ListaTipoLogQuery;
import com.zetra.econsig.persistence.query.log.ObtemLogDataAtualQuery;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
/**
 * <p>Title: LogControllerBean</p>
 * <p>Description: Session Bean para o Log.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class LogControllerBean implements LogController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LogControllerBean.class);

    @Override
    public void gravarLog(String line, String usuCodigo, String ipUsuario, Integer portaLogica, String tipo, String entidade, String funCodigo,
                          String codigoEntidade00, String codigoEntidade01, String codigoEntidade02, String codigoEntidade03,
                          String codigoEntidade04, String codigoEntidade05, String codigoEntidade06, String codigoEntidade07,
                          String codigoEntidade08, String codigoEntidade09, String codigoEntidade10, CanalEnum logCanal) throws LogControllerException {
        if (line == null) {
            throw new LogControllerException("mensagem.erro.nao.possivel.gravar.log.parametros.nao.inicializados", (AcessoSistema) null);
        }

        Session session = SessionUtil.getSession();
        try {
            Usuario usuario = null;
            TipoLog tipoLog = null;
            TipoEntidade tipoEntidade = null;
            Funcao funcao = null;

            if (tipo != null) {
                tipoLog = session.getReference(TipoLog.class, tipo);
            }
            if (usuCodigo != null) {
                usuario = session.getReference(Usuario.class, usuCodigo);
            }
            if (entidade != null) {
                tipoEntidade = session.getReference(TipoEntidade.class, entidade);
            }
            if (funCodigo != null) {
                funcao = session.getReference(Funcao.class, funCodigo);
            }

            LogHome.create(codigoEntidade00, codigoEntidade01, codigoEntidade02, codigoEntidade03, codigoEntidade04, codigoEntidade05,
                           codigoEntidade06, codigoEntidade07, codigoEntidade08, codigoEntidade09, codigoEntidade10,
                           new Date(), line, ipUsuario, portaLogica, usuario, tipoEntidade, tipoLog, funcao, logCanal);
        } catch (CreateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new LogControllerException(e);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    @Override
    public List<TransferObject> lstTiposLog() throws LogControllerException {
        try {
            ListaTipoLogQuery query = new ListaTipoLogQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> getLogDataAtual() throws LogControllerException {
        try {
            ObtemLogDataAtualQuery query = new ObtemLogDataAtualQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> lstTiposEntidadesAuditoria() throws LogControllerException {
        try {
            ListaTipoEntidadeLogQuery query = new ListaTipoEntidadeLogQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoEntidade(List<String> tenCodigos) throws LogControllerException {
        try {
            ListaTipoEntidadeQuery query = new ListaTipoEntidadeQuery();
            query.tipoEntidadeCodigo = tenCodigos;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TipoEntidade> getTiposEntidade(AcessoSistema responsavel) throws LogControllerException {
        try {
            return TipoEntidadeHome.getTiposEntidade();
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista as tabelas de rotacionamento de log criadas de acordo com a data de início e data fim passadas.
     *
     * @param dataInicio
     * @param dataFim
     * @return
     * @throws HQueryException
     */
    @Override
    public List<TransferObject> lstHistoricoArqLog(Date dataInicio, Date dataFim) throws LogControllerException {
        try {
            ListaHistArqLogPorPeriodoQuery query = new ListaHistArqLogPorPeriodoQuery();
            query.dataIni = dataInicio;
            query.dataFim = dataFim;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void geraHistoricoLog(AcessoSistema responsavel) throws LogControllerException {
        try {
            LogDAO logDAO = DAOFactory.getDAOFactory().getLogDAO();
            logDAO.geraHistoricoLog(responsavel);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new LogControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> recuperaDescricoes(Class<? extends AbstractEntityHome> clazz, List<String> codigos) throws LogControllerException {
        try {
            if (codigos == null || codigos.isEmpty()) {
                return null;
            }

            List<String> retorno = new ArrayList<>();

            Iterator<String> ite = codigos.iterator();
            while (ite.hasNext()) {
                String codigo = ite.next();

                if (codigo == null || codigo.equals(CodedValues.NOT_EQUAL_KEY)) {
                    continue;
                }

                if (ConsignanteHome.class.isAssignableFrom(clazz)) {
                    retorno.add(ConsignanteHome.findByPrimaryKey(codigo).getCseNome());
                } else if (ConsignatariaHome.class.isAssignableFrom(clazz)) {
                    retorno.add(ConsignatariaHome.findByPrimaryKey(codigo).getCsaNome());
                } else if (CorrespondenteHome.class.isAssignableFrom(clazz)) {
                    retorno.add(CorrespondenteHome.findByPrimaryKey(codigo).getCorNome());
                } else if (OrgaoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(OrgaoHome.findByPrimaryKey(codigo).getOrgNome());
                } else if (EstabelecimentoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(EstabelecimentoHome.findByPrimaryKey(codigo).getEstNome());
                } else if (ServidorHome.class.isAssignableFrom(clazz)) {
                    retorno.add(ServidorHome.findByPrimaryKey(codigo).getSerNome());
                } else if (StatusAutorizacaoDescontoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(StatusAutorizacaoDescontoHome.findByPrimaryKey(codigo).getSadDescricao());
                } else if (StatusParcelaDescontoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(StatusParcelaDescontoHome.findByPrimaryKey(codigo).getSpdDescricao());
                } else if (StatusRegistroServidorHome.class.isAssignableFrom(clazz)) {
                    retorno.add(StatusRegistroServidorHome.findByPrimaryKey(codigo).getSrsDescricao());
                } else if (VinculoRegistroServidorHome.class.isAssignableFrom(clazz)) {
                    retorno.add(VinculoRegistroServidorHome.findByPrimaryKey(codigo).getVrsDescricao());
                } else if (AutDescontoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(AutDescontoHome.findByPrimaryKey(codigo).getAdeNumero().toString());
                } else if (ConvenioHome.class.isAssignableFrom(clazz)) {
                    retorno.add(ConvenioHome.findByPrimaryKey(codigo).getCnvCodVerba());
                } else if (FuncaoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(FuncaoHome.findByPrimaryKey(codigo).getFunDescricao());
                } else if (GrupoFuncaoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(GrupoFuncaoHome.findByPrimaryKey(codigo).getGrfDescricao());
                } else if (MargemHome.class.isAssignableFrom(clazz)) {
                    retorno.add(MargemHome.findByPrimaryKey(Short.valueOf(codigo)).getMarDescricao());
                } else if (PapelHome.class.isAssignableFrom(clazz)) {
                    retorno.add(PapelHome.findByPrimaryKey(codigo).getPapDescricao());
                } else if (PerfilHome.class.isAssignableFrom(clazz)) {
                    retorno.add(PerfilHome.findByPrimaryKey(codigo).getPerDescricao());
                } else if (RegistroServidorHome.class.isAssignableFrom(clazz)) {
                    retorno.add(RegistroServidorHome.findByPrimaryKey(codigo).getRseMatricula());
                } else if (OrgaoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(OrgaoHome.findByPrimaryKey(codigo).getOrgNome());
                } else if (ServicoHome.class.isAssignableFrom(clazz)) {
                    retorno.add(ServicoHome.findByPrimaryKey(codigo).getSvcDescricao());
                } else if (TipoOcorrenciaHome.class.isAssignableFrom(clazz)) {
                    retorno.add(TipoOcorrenciaHome.findByPrimaryKey(codigo).getTocDescricao());
                } else if (UsuarioHome.class.isAssignableFrom(clazz)) {
                    retorno.add(UsuarioHome.findByPrimaryKey(codigo).getUsuLogin());
                }
            }

            return retorno;

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LogControllerException("mensagem.erro.nao.possivel.recuperar.descricoes.entidade", (AcessoSistema) null);
        }
    }
}
