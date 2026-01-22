package com.zetra.econsig.service.parametro;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OcorrenciaParamSistCseTO;
import com.zetra.econsig.dto.entidade.ParamCnvRseTO;
import com.zetra.econsig.dto.entidade.ParamCsaRseTO;
import com.zetra.econsig.dto.entidade.ParamNseRseTO;
import com.zetra.econsig.dto.entidade.ParamSistCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcCsaTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcRseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParamTarifCseTO;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ParamConvenioRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamCsaRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamNseRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamServicoRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParametrosDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AcessoRecurso;
import com.zetra.econsig.persistence.entity.AcessoRecursoHome;
import com.zetra.econsig.persistence.entity.AcessoUsuario;
import com.zetra.econsig.persistence.entity.AcessoUsuarioHome;
import com.zetra.econsig.persistence.entity.AjudaHome;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.NaturezaServicoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParamSistCseHome;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamConsignataria;
import com.zetra.econsig.persistence.entity.ParamConsignatariaHome;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSer;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSerId;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroSer;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroSerId;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamNseRegistroSer;
import com.zetra.econsig.persistence.entity.ParamNseRegistroSerId;
import com.zetra.econsig.persistence.entity.ParamNseRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamOrgao;
import com.zetra.econsig.persistence.entity.ParamOrgaoHome;
import com.zetra.econsig.persistence.entity.ParamServicoRegistroSer;
import com.zetra.econsig.persistence.entity.ParamServicoRegistroSerId;
import com.zetra.econsig.persistence.entity.ParamServicoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamSistConsignante;
import com.zetra.econsig.persistence.entity.ParamSistConsignanteHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignataria;
import com.zetra.econsig.persistence.entity.ParamSvcConsignatariaHome;
import com.zetra.econsig.persistence.entity.ParamTarifConsignante;
import com.zetra.econsig.persistence.entity.ParamTarifConsignanteHome;
import com.zetra.econsig.persistence.entity.ParametroAgendamento;
import com.zetra.econsig.persistence.entity.ParametroAgendamentoHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RegraRestricaoAcesso;
import com.zetra.econsig.persistence.entity.RegraRestricaoAcessoCsa;
import com.zetra.econsig.persistence.entity.RegraRestricaoAcessoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoServico;
import com.zetra.econsig.persistence.entity.RelacionamentoServicoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.TipoNatureza;
import com.zetra.econsig.persistence.entity.TipoNaturezaHome;
import com.zetra.econsig.persistence.entity.TipoParamOrgao;
import com.zetra.econsig.persistence.entity.TipoParamOrgaoHome;
import com.zetra.econsig.persistence.entity.TipoParamSistConsignante;
import com.zetra.econsig.persistence.entity.TipoParamSistConsignanteHome;
import com.zetra.econsig.persistence.entity.TipoParamSvc;
import com.zetra.econsig.persistence.entity.TipoParamSvcHome;
import com.zetra.econsig.persistence.query.admin.ListaAcessoRecursoQuery;
import com.zetra.econsig.persistence.query.admin.ListaRegraRestricaoAcessoQuery;
import com.zetra.econsig.persistence.query.admin.ListaTipoNaturezaQuery;
import com.zetra.econsig.persistence.query.admin.ListaTipoParamSvcQuery;
import com.zetra.econsig.persistence.query.admin.ListaTipoParamSvcSobrepoeQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoPorStatusQuery;
import com.zetra.econsig.persistence.query.convenio.ListaBloqueioCsaServidorQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioVinculoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.convenio.ObtemBloqueioCnvRseQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemIncidenteQuery;
import com.zetra.econsig.persistence.query.parametro.ListaIncidenciasMargemQuery;
import com.zetra.econsig.persistence.query.parametro.ListaOcorrenciaParamSistCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCnvRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCsaQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamOrgaoEditavelQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamOrgaoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSistCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCsaQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcServidorSobrepoeQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamTarifCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentoSvcCorrecaoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.parametro.ListaServicoRelacionamentoSvcQuery;
import com.zetra.econsig.persistence.query.parametro.ListaServicosRelacionadosQuery;
import com.zetra.econsig.persistence.query.parametro.ListaSvcByValorFixoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaTodosParamSvcCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaTodosParamTarifCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListarParamSvcCorQuery;
import com.zetra.econsig.persistence.query.parametro.ObtemMaxMinVlrParamSvcCseNseQuery;
import com.zetra.econsig.persistence.query.sdp.plano.ListaParametroPlanoQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoSerQuery;
import com.zetra.econsig.persistence.query.servico.ListaTipoNaturezaEditavelServicoQuery;
import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoCnvQuery;
import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoNseQuery;
import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoSvcQuery;
import com.zetra.econsig.persistence.query.servidor.ListaBloqueioNaturezaServicoServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaBloqueioServicoServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorCnvCodigosQuery;
import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorEntidadeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioCsaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioNseRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioServicoRegistroServidorQuery;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.values.NaturezaPlanoEnum;

/**
 * <p>Title: ParametroControllerBean</p>
 * <p>Description: Session Bean para os Parametros (Tarifação, Serviço, ...)</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ParametroControllerBean implements ParametroController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParametroControllerBean.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    // ParamTarifCse
    @Override
    public List<TransferObject> selectParamTarifCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaTodosParamTarifCseQuery query = new ListaTodosParamTarifCseQuery();
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> lstParamTarifCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamTarifCseQuery query = new ListaParamTarifCseQuery();
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private ParamTarifConsignante findParamTarifCseBean(ParamTarifCseTO param) throws ParametroControllerException {
        ParamTarifConsignante paramBean = null;
        if (param.getPcvCodigo() != null) {
            try {
                paramBean = ParamTarifConsignanteHome.findByPrimaryKey(param.getPcvCodigo());
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramBean;
    }

    private ParamTarifCseTO setParamTarifCseValues(ParamTarifConsignante paramBean) {
        final ParamTarifCseTO param = new ParamTarifCseTO(paramBean.getPcvCodigo());
        if (paramBean.getServico() != null) {
            param.setSvcCodigo(paramBean.getServico().getSvcCodigo());
        }
        if (paramBean.getTipoParamTarifCse() != null) {
            param.setTptCodigo(paramBean.getTipoParamTarifCse().getTptCodigo());
        }
        param.setPcvDataIniVig(paramBean.getPcvDataIniVig());
        param.setPcvDataFimVig(paramBean.getPcvDataFimVig());
        param.setPcvAtivo(paramBean.getPcvAtivo());
        param.setPcvVlr(paramBean.getPcvVlr());
        param.setPcvBaseCalc(paramBean.getPcvBaseCalc());
        param.setPcvFormaCalc(paramBean.getPcvFormaCalc());
        param.setPcvDecimais(paramBean.getPcvDecimais());
        param.setPcvVlrIni(paramBean.getPcvVlrIni());
        param.setPcvVlrFim(paramBean.getPcvVlrFim());
        if (paramBean.getConsignante() != null) {
            param.setCseCodigo(paramBean.getConsignante().getCseCodigo());
        }

        return param;
    }

    private String createParamTarifCse(ParamTarifCseTO paramTarifCse, AcessoSistema responsavel) throws ParametroControllerException {
        String pcvCodigo = null;
        if (paramTarifCse.getPcvDataIniVig() == null) {
            paramTarifCse.setPcvDataIniVig(DateHelper.getSystemDatetime());
        }
        if (paramTarifCse.getPcvDataFimVig() == null) {
            paramTarifCse.setPcvDataFimVig(DateHelper.getSystemDatetime());
        }
        try {
            final ParamTarifConsignante paramTarifCseBean = ParamTarifConsignanteHome.create(paramTarifCse.getSvcCodigo(), paramTarifCse.getTptCodigo(), paramTarifCse.getPcvDataIniVig(), paramTarifCse.getPcvDataFimVig(), paramTarifCse.getPcvAtivo(), paramTarifCse.getPcvVlr(), paramTarifCse.getPcvBaseCalc(), paramTarifCse.getPcvFormaCalc(), paramTarifCse.getPcvDecimais(), paramTarifCse.getPcvVlrIni(), paramTarifCse.getPcvVlrFim(), paramTarifCse.getCseCodigo());
            pcvCodigo = paramTarifCseBean.getPcvCodigo();

            // Grava log da criação do parâmetro
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_TARIF_CSE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setParamTarifCse(pcvCodigo);
            log.setTipoParamTarifCse(paramTarifCse.getTptCodigo());
            log.setServico(paramTarifCse.getSvcCodigo());
            log.setConsignante(paramTarifCse.getCseCodigo());
            log.getUpdatedFields(paramTarifCse.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.salvar.parametros.deste.servico.erro.interno", responsavel, ex.getMessage());
        }
        return pcvCodigo;
    }

    @Override
    public void updateParamTarifCse(ParamTarifCseTO paramTarifCse, AcessoSistema responsavel) throws ParametroControllerException {
        // Se o PCV_VLR é vazio ou nulo o parâmetro deve ser excluído.
        if (TextHelper.isNull(paramTarifCse.getPcvVlr())) {
            try {
                if (TextHelper.isNull(paramTarifCse.getPcvCodigo())) {
                    LOG.debug("Parâmetro de tarifação não foi excluído pois não existe.");
                    return;
                }
                removeParamTarifCse(paramTarifCse, responsavel);
            } catch (final ParametroControllerException ex) {
                if ((ex.getCause() != null) && ex.getCause().getClass().equals(FindException.class)) {
                    LOG.debug("Parâmetro de tarifação não foi excluído pois não existe.", ex);
                } else {
                    LOG.error(ex.getMessage(), ex);
                    throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        } else {
            try {
                final ParamTarifConsignante paramTarifCseBean = findParamTarifCseBean(paramTarifCse);
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_TARIF_CSE, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setParamTarifCse(paramTarifCseBean.getPcvCodigo());
                log.setConsignante(paramTarifCseBean.getConsignante().getCseCodigo());
                log.setServico(paramTarifCseBean.getServico().getSvcCodigo());
                log.setTipoParamTarifCse(paramTarifCseBean.getTipoParamTarifCse().getTptCodigo());

                /* Compara a versão do cache com a passada por parâmetro */
                final ParamTarifCseTO paramTarifCseCache = setParamTarifCseValues(paramTarifCseBean);
                final CustomTransferObject merge = log.getUpdatedFields(paramTarifCse.getAtributos(), paramTarifCseCache.getAtributos());
                if (merge.getAtributos().containsKey(Columns.PCV_DATA_INI_VIG)) {
                    paramTarifCseBean.setPcvDataIniVig((Date) merge.getAttribute(Columns.PCV_DATA_INI_VIG));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_DATA_FIM_VIG)) {
                    paramTarifCseBean.setPcvDataFimVig((Date) merge.getAttribute(Columns.PCV_DATA_FIM_VIG));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_ATIVO)) {
                    paramTarifCseBean.setPcvAtivo((Short) merge.getAttribute(Columns.PCV_ATIVO));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_VLR)) {
                    paramTarifCseBean.setPcvVlr((BigDecimal) merge.getAttribute(Columns.PCV_VLR));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_BASE_CALC)) {
                    paramTarifCseBean.setPcvBaseCalc((Integer) merge.getAttribute(Columns.PCV_BASE_CALC));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_FORMA_CALC)) {
                    paramTarifCseBean.setPcvFormaCalc((Integer) merge.getAttribute(Columns.PCV_FORMA_CALC));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_DECIMAIS)) {
                    paramTarifCseBean.setPcvDecimais((Integer) merge.getAttribute(Columns.PCV_DECIMAIS));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_VLR_INI)) {
                    paramTarifCseBean.setPcvVlrIni((BigDecimal) merge.getAttribute(Columns.PCV_VLR_INI));
                }
                if (merge.getAtributos().containsKey(Columns.PCV_VLR_FIM)) {
                    paramTarifCseBean.setPcvVlrFim((BigDecimal) merge.getAttribute(Columns.PCV_VLR_FIM));
                }

                AbstractEntityHome.update(paramTarifCseBean);

                /* Só grava o log se alguma informação do parâmetro foi alterada */
                if ((merge.getAtributos() != null) && !merge.getAtributos().isEmpty()) {
                    log.write();
                }
            } catch (final ParametroControllerException ex) {
                // Se o parâmetro não existir, então cria um novo
                LOG.error("Não foi possível localizar o parâmetro de tarifação consignante para alteração, será feita a inclusão.");
                createParamTarifCse(paramTarifCse, responsavel);
            } catch (final LogControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            } catch (final UpdateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private void removeParamTarifCse(ParamTarifCseTO paramTarifCse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamTarifConsignante paramTarifCseBean = findParamTarifCseBean(paramTarifCse);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_TARIF_CSE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setParamTarifCse(paramTarifCseBean.getPcvCodigo());

            AbstractEntityHome.remove(paramTarifCseBean);

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ParamSvcCse
    @Override
    public ParamSvcCseTO findParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException {
        return setParamSvcCseValues(findParamSvcCseBean(paramSvcCse));

    }

    private ParamSvcConsignante findParamSvcCseBean(ParamSvcCseTO param) throws ParametroControllerException {
        ParamSvcConsignante paramBean = null;
        if (param.getPseCodigo() != null) {
            try {
                paramBean = ParamSvcConsignanteHome.findByPrimaryKey(param.getPseCodigo());
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else if ((param.getTpsCodigo() != null) && (param.getSvcCodigo() != null) && (param.getCseCodigo() != null)) {
            try {
                paramBean = ParamSvcConsignanteHome.findByTipoCseServico(param.getTpsCodigo(), param.getCseCodigo(), param.getSvcCodigo());
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramBean;
    }

    private ParamSvcCseTO setParamSvcCseValues(ParamSvcConsignante paramBean) {
        final ParamSvcCseTO param = new ParamSvcCseTO(paramBean.getPseCodigo());
        param.setCseCodigo(paramBean.getConsignante().getCseCodigo());
        param.setPseVlr(paramBean.getPseVlr());
        param.setPseVlrRef(paramBean.getPseVlrRef());
        param.setSvcCodigo(paramBean.getServico().getSvcCodigo());
        param.setTpsCodigo(paramBean.getTipoParamSvc().getTpsCodigo());

        return param;
    }

    private String createParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException {
        String pseCodigo = null;
        try {
            final ParamSvcConsignante paramSvcCseBean = ParamSvcConsignanteHome.create(paramSvcCse.getSvcCodigo(), paramSvcCse.getTpsCodigo(), paramSvcCse.getCseCodigo(), paramSvcCse.getPseVlr(), paramSvcCse.getPseVlrRef());
            pseCodigo = paramSvcCseBean.getPseCodigo();

            // Grava log da criação do parâmetro
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_CSE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setParamSvcCse(paramSvcCseBean.getPseCodigo());
            log.setTipoParamSvc(paramSvcCseBean.getTipoParamSvc().getTpsCodigo());
            log.setConsignante(paramSvcCse.getCseCodigo());
            log.setServico(paramSvcCse.getSvcCodigo());
            log.getUpdatedFields(paramSvcCse.getAtributos(), null);
            log.write();

        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.criar.parametros.deste.servico.erro.interno", responsavel, ex.getMessage());
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return pseCodigo;
    }

    @Override
    public void updateParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamSvcConsignante paramSvcCseBean = findParamSvcCseBean(paramSvcCse);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_CSE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setParamSvcCse(paramSvcCseBean.getPseCodigo());
            log.setTipoParamSvc(paramSvcCseBean.getTipoParamSvc().getTpsCodigo());
            log.setConsignante(paramSvcCseBean.getConsignante().getCseCodigo());
            log.setServico(paramSvcCseBean.getServico().getSvcCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            final ParamSvcCseTO paramSvcCseCache = setParamSvcCseValues(paramSvcCseBean);
            final CustomTransferObject merge = log.getUpdatedFields(paramSvcCse.getAtributos(), paramSvcCseCache.getAtributos());
            if (merge.getAtributos().containsKey(Columns.PSE_VLR)) {
                paramSvcCseBean.setPseVlr((String) merge.getAttribute(Columns.PSE_VLR));
            }

            if (merge.getAtributos().containsKey(Columns.PSE_VLR_REF)) {
                paramSvcCseBean.setPseVlrRef((String) merge.getAttribute(Columns.PSE_VLR_REF));
            }

            AbstractEntityHome.update(paramSvcCseBean);

            /* Só grava o log se o valor do parâmetro ou o valor de referência foi alterado */
            if (merge.getAtributos().containsKey(Columns.PSE_VLR) || merge.getAtributos().containsKey(Columns.PSE_VLR_REF)) {
                log.write();
            }
        } catch (final ParametroControllerException ex) {
            // Se o parâmetro não existir, então cria um novo
            if ((ex.getCause() != null) && ex.getCause().getClass().equals(FindException.class)) {
                createParamSvcCse(paramSvcCse, responsavel);
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> lstParamSvcCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstParamSvcCse(String tpsCodigo, String pseVlr, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
            query.tpsCodigo = tpsCodigo;
            query.pseVlr = pseVlr;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        if (TextHelper.isNull(svcCodigo)) {
            throw new ParametroControllerException("mensagem.usoIncorretoSistema", responsavel);
        }
        try {
            final ListaTodosParamSvcCseQuery query = new ListaTodosParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCse(String svcCodigo, String responsavelAltera, AcessoSistema responsavel) throws ParametroControllerException {
        if (TextHelper.isNull(svcCodigo)) {
            throw new ParametroControllerException("mensagem.usoIncorretoSistema", responsavel);
        }
        try {
            final ListaTodosParamSvcCseQuery query = new ListaTodosParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.responsavelAltera = responsavelAltera;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public ParamSvcTO getParamSvcCseTO(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);
    }

    @Override
    public List<TransferObject> recuperaIncidenciasMargem(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaIncidenciasMargemQuery query = new ListaIncidenciasMargemQuery();
            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public CustomTransferObject getParamSvcCse(String svcCodigo, String tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        CustomTransferObject result = null;
        try {
            final ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.tpsCodigo = tpsCodigo;
            final List<TransferObject> lstParamSvcCse = query.executarDTO();
            if ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0)) {
                result = (CustomTransferObject) lstParamSvcCse.get(0);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
        return result;
    }

    @Override
    public List<TransferObject> lstTipoNatureza(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaTipoNaturezaQuery query = new ListaTipoNaturezaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ParamSistCse
    @Override
    public List<TransferObject> selectParamSistCse(String tpcCseAltera, String tpcCseConsulta, String tpcSupAltera, String tpcSupConsulta, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSistCseQuery query = new ListaParamSistCseQuery(responsavel);
            query.tpcCseAltera = tpcCseAltera;
            query.tpcCseConsulta = tpcCseConsulta;
            query.tpcSupAltera = tpcSupAltera;
            query.tpcSupConsulta = tpcSupConsulta;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSistCseEditavelPerfil(String papCodigo, String perCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSistCseQuery query = new ListaParamSistCseQuery(responsavel);
            query.perCodigo = perCodigo;
            query.tpcCseAltera = (AcessoSistema.ENTIDADE_CSE.equals(papCodigo) ? CodedValues.TPC_SIM : null);
            query.tpcSupAltera = (AcessoSistema.ENTIDADE_SUP.equals(papCodigo) ? CodedValues.TPC_SIM : null);

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public LinkedList<OcorrenciaParamSistCseTO> selectOcorrenciaParamSistCse(OcorrenciaParamSistCseTO criterio, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaOcorrenciaParamSistCseQuery query = new ListaOcorrenciaParamSistCseQuery();
            query.tpcCodigo = criterio.getTpcCodigo();
            query.tpcDescricao = criterio.getTpcDescricao();
            query.usuLogin = criterio.getUsuLogin();

            final int offset = criterio.getAttribute("offset") != null ? Integer.parseInt(criterio.getAttribute("offset").toString()) : -1;
            final int count = criterio.getAttribute("size") != null ? Integer.parseInt(criterio.getAttribute("size").toString()) : -1;
            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            final LinkedList<OcorrenciaParamSistCseTO> resultado = new LinkedList<>();
            final List<TransferObject> lstResultadoTemporario = query.executarDTO();
            for (final TransferObject resultadoTemporario : lstResultadoTemporario) {
                resultado.add(new OcorrenciaParamSistCseTO(resultadoTemporario));
            }
            return resultado;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaParamSistCse(OcorrenciaParamSistCseTO criterio, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaOcorrenciaParamSistCseQuery query = new ListaOcorrenciaParamSistCseQuery();
            query.tpcCodigo = criterio.getTpcCodigo();
            query.tpcDescricao = criterio.getTpcDescricao();
            query.usuLogin = criterio.getUsuLogin();
            query.count = true;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String findParamSistCse(String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        ParamSistCseTO criterio = new ParamSistCseTO(tpcCodigo);
        criterio.setCseCodigo(cseCodigo);
        criterio = findParamSistCse(criterio, responsavel);
        return criterio.getPsiVlr();
    }

    @Override
    public ParamSistCseTO findParamSistCse(ParamSistCseTO param, AcessoSistema responsavel) throws ParametroControllerException {
        final ParamSistConsignante bean = findParamSistCseBean(param);
        return setParamSistCseValues(bean);
    }

    private ParamSistConsignante findParamSistCseBean(ParamSistCseTO param) throws ParametroControllerException {
        ParamSistConsignante paramBean = null;
        if (param.getTpcCodigo() != null) {
            try {
                paramBean = ParamSistConsignanteHome.findByPrimaryKey(param.getTpcCodigo());
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramBean;
    }

    private ParamSistCseTO setParamSistCseValues(ParamSistConsignante paramBean) {
        final ParamSistCseTO param = new ParamSistCseTO(paramBean.getTipoParamSistConsignante().getTpcCodigo());
        param.setCseCodigo(paramBean.getConsignante().getCseCodigo());
        param.setPsiVlr(paramBean.getPsiVlr());

        return param;
    }

    private void createParamSistCse(ParamSistCseTO paramSistCse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            ParamSistConsignanteHome.create(paramSistCse.getTpcCodigo(), paramSistCse.getCseCodigo(), paramSistCse.getPsiVlr());

            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SIST_CSE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setTipoParamSistCse(paramSistCse.getTpcCodigo());
            log.setConsignante(paramSistCse.getCseCodigo());
            log.getUpdatedFields(paramSistCse.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.criar.parametros.deste.servico.erro.interno", responsavel, ex.getMessage());
        }
    }

    @Override
    public void updateParamSistCse(String psiVlr, String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final ParamSistCseTO paramSistCse = new ParamSistCseTO(tpcCodigo);
        paramSistCse.setCseCodigo(cseCodigo);
        paramSistCse.setPsiVlr(psiVlr);
        updateParamSistCse(paramSistCse, responsavel);
    }

    @Override
    public void updateParamSistCse(ParamSistCseTO paramSistCse, AcessoSistema responsavel) throws ParametroControllerException {
        if (CodedValues.TPC_DIR_RAIZ_ARQUIVOS.equals(paramSistCse.getTpcCodigo())) {
            throw new ParametroControllerException("mensagem.erro.tentativa.editar.parametro.sistema.arg0.que.nao.permite.alteracao.via.sistema", responsavel, CodedValues.TPC_DIR_RAIZ_ARQUIVOS);
        }

        TipoParamSistConsignante tipoParamSistConsignante;
        try {
            tipoParamSistConsignante = TipoParamSistConsignanteHome.findByPrimaryKey(paramSistCse.getTpcCodigo());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        String valorAntigo = tipoParamSistConsignante.getTpcVlrDefault(); // Se ainda não estiver presente na tb_param_sist_consignante
        final String valorNovo = paramSistCse.getPsiVlr();
        String opsObs = "";

        try {
            final ParamSistConsignante paramSistCseBean = findParamSistCseBean(paramSistCse);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SIST_CSE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoParamSistCse(paramSistCseBean.getTipoParamSistConsignante().getTpcCodigo());

            valorAntigo = paramSistCseBean.getPsiVlr();

            // Compara a versão do cache com a passada por parâmetro
            final ParamSistCseTO paramSistCseCache = setParamSistCseValues(paramSistCseBean);
            final CustomTransferObject merge = log.getUpdatedFields(paramSistCse.getAtributos(), paramSistCseCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.PSI_VLR)) {
                paramSistCseBean.setPsiVlr((String) merge.getAttribute(Columns.PSI_VLR));
            }

            AbstractEntityHome.update(paramSistCseBean);

            log.write();

            // Gera descrição da ocorrência
            opsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.parametro.alterado.de.arg0.para.arg1", responsavel, valorAntigo, valorNovo);

        } catch (final ParametroControllerException ex) {
            // Se o parâmetro não existir, então cria um novo
            if ((ex.getCause() != null) && ex.getCause().getClass().equals(FindException.class)) {
                createParamSistCse(paramSistCse, responsavel);

                // Gera descrição da ocorrência
                opsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.parametro.alterado.de.arg0.para.arg1", responsavel, valorAntigo, valorNovo);
            } else {
                LOG.error(ex.getMessage(), ex);
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        if (!TextHelper.isNull(opsObs)) {
            try {
                final String tpcCodigo = paramSistCse.getTpcCodigo();
                final String cseCodigo = paramSistCse.getCseCodigo();
                final String opsIpAcesso = responsavel.getIpUsuario();
                final String tocCodigo = CodedValues.TOC_ALTERACAO_PARAM_SIST_CSE;
                final String usuCodigo = responsavel.getUsuCodigo();
                OcorrenciaParamSistCseHome.create(tocCodigo, usuCodigo, tpcCodigo, cseCodigo, null, opsObs, opsIpAcesso);
            } catch (final com.zetra.econsig.exception.CreateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public TipoParamSistConsignante findTipoParamSistConsignante(String tpcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        TipoParamSistConsignante retorno = null;

        try {
            retorno = TipoParamSistConsignanteHome.findByPrimaryKey(tpcCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    // ParamSvc

    @Override
    public TipoParamSvc findTipoParamServico(String tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        TipoParamSvc retorno = null;

        try {
            retorno = TipoParamSvcHome.findByPrimaryKey(tpsCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    @Override
    public List<TransferObject> lstTipoParamSvc(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaTipoParamSvcQuery query = new ListaTipoParamSvcQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ParamSvcSobrepoe
    @Override
    public List<TransferObject> lstTipoParamSvcSobrepoe(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaTipoParamSvcSobrepoeQuery query = new ListaTipoParamSvcSobrepoeQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcSobrepoe(String svcCodigo, String rseCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcServidorSobrepoeQuery query = new ListaParamSvcServidorSobrepoeQuery();
            query.svcCodigo = svcCodigo;
            query.rseCodigo = rseCodigo;
            query.tpsCodigos = tpsCodigos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public void updateParamSvcSobrepoe(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : parametros) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setRegistroServidor((String) cto.getAttribute(Columns.RSE_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.TPS_CODIGO));
                log.add("PSR_VLR: " + cto.getAttribute(Columns.PSR_VLR));
                log.write();
            }
            dao.updateParamSvcSobrepoe(parametros);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ParamSvcCsa
    private List<TransferObject> lstParamSvcCsa(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final List<String> svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigo);
            final ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
            query.svcCodigos = svcCodigos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCsa(List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException {
        return selectParamSvcCsa((List<String>) null, (List<String>) null, tpsCodigos, false, responsavel);
    }

    @Override
    public List<TransferObject> selectParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        List<String> svcCodigos = null;
        List<String> csaCodigos = null;

        if (!TextHelper.isNull(svcCodigo)) {
            svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigo);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            csaCodigos = new ArrayList<>();
            csaCodigos.add(csaCodigo);
        }

        return selectParamSvcCsa(svcCodigos, csaCodigos, tpsCodigos, ativo, responsavel);
    }

    @Override
    public List<TransferObject> selectParamSvcCsa(List<String> svcCodigos, List<String> csaCodigos, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
            query.svcCodigos = svcCodigos;
            query.csaCodigos = csaCodigos;
            query.tpsCodigos = tpsCodigos;
            query.ativo = ativo;

            List<TransferObject> result = query.executarDTO();

            if (!ativo && (result != null) && result.isEmpty()) {
                query.dataIniVigIndiferente = true;
                result = query.executarDTO();
            }

            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCsa(String csaIdentificadorInterno, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
            query.csaIdentificadorInterno = csaIdentificadorInterno;
            query.tpsCodigos = tpsCodigos;
            query.ativo = ativo;

            List<TransferObject> result = query.executarDTO();

            if (!ativo && (result != null) && result.isEmpty()) {
                query.dataIniVigIndiferente = true;
                result = query.executarDTO();
            }

            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public void updateParamSvcCsa(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : parametros) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_CSA, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignataria((String) cto.getAttribute(Columns.PSC_CSA_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.PSC_SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.TPS_CODIGO));
                log.add("PSC_VLR: " + cto.getAttribute(Columns.PSC_VLR));
                log.write();
            }
            dao.updateParamSvcCsa(parametros);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void deleteParamIgualCse(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : tpsCodigo) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_CSA, Log.DELETE, Log.LOG_INFORMACAO);
                log.setConsignataria((String) cto.getAttribute(Columns.PSC_CSA_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.PSC_SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.TPS_CODIGO));
                log.write();
            }
            dao.deleteParamIgualCse(tpsCodigo);

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void deleteParamIgualCseRse(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : tpsCodigo) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
                log.setRegistroServidor((String) cto.getAttribute(Columns.PSR_RSE_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.PSR_SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.TPS_CODIGO));
                log.write();
            }
            dao.deleteParamIgualCseRse(tpsCodigo);

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void ativaParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();

            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_CSA, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.setServico(svcCodigo);
            if ((tpsCodigos == null) || tpsCodigos.isEmpty()) {
                log.write();
            } else {
                for (final String tpsCodigo : tpsCodigos) {
                    log.setTipoParamSvc(tpsCodigo);
                    log.write();
                }
            }

            dao.ativaParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos);

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, boolean ativos, AcessoSistema responsavel) throws ParametroControllerException {
        return lstServicoServidor(rseCodigo, csaCodigo, null, ativos, responsavel);
    }

    /**
     * Lista serviços que tem convênio com o órgão do servidor
     *
     * @param rseCodigo   - código do registro servidor
     * @param csaCodigo   - código da consignatária
     * @param nseCodigo   - filtro de natureza dos serviços a serem buscados
     * @param ativos      - serviço ativo ou não
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, String nseCodigo, boolean ativos, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaServicoSerQuery query = new ListaServicoSerQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;
            query.ativos = ativos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, String nseCodigo, boolean ativos, int offset, int count, AcessoSistema responsavel) throws ParametroControllerException {
        try {

            final ListaServicoSerQuery query = new ListaServicoSerQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;
            query.ativos = ativos;
            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * BLOQUEIO DE CONVENIO POR REGISTRO SERVIDOR
     **/

    @Override
    public List<TransferObject> lstBloqueioCnvRegistroServidor(String rseCodigo, String csaCodigo, String svcCodigo, Boolean inativosSomenteComBloqueio, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaConvenioRegistroServidorQuery query = new ListaConvenioRegistroServidorQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.inativosSomenteComBloqueio = inativosSomenteComBloqueio;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject getBloqueioCnvRegistroServidor(String rseCodigo, String csaCodigo, String svcCodigo, Boolean inativosSomenteComBloqueio, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ObtemBloqueioCnvRseQuery query = new ObtemBloqueioCnvRseQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.inativosSomenteComBloqueio = inativosSomenteComBloqueio;

            final List<TransferObject> result = query.executarDTO();
            final CustomTransferObject retorno = new CustomTransferObject();

            for (final TransferObject cto : result) {
                retorno.setAttribute(cto.getAttribute("TIPO").toString(), Integer.valueOf(cto.getAttribute("TOTAL").toString()));
            }

            return retorno;

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public void setBloqueioCnvRegistroServidor(List<ParamCnvRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            if ((bloqueios != null) && (bloqueios.size() > 0)) {
                final String rseCodigo = bloqueios.get(0).getRseCodigo();
                // Listas com as 3 possibilidades de alteração para gerar um email detalhando estas para o servidor, se necessário.
                final List<TransferObject> bloqueado = new ArrayList<>();
                final List<TransferObject> desbloqueado = new ArrayList<>();
                final List<TransferObject> alterado = new ArrayList<>();
                String valorAntigo = null;
                String valorAntigoCse = null;
                String valorAntigoCsa = null;
                String valorAntigoSer = null;

                for (final ParamCnvRseTO next : bloqueios) {
                    boolean dadosAlterados = false;
                    int tipo = 3;

                    try {
                        // Busca o parâmetro de convenio por servidor
                        final ParamConvenioRegistroSerId paramConvenioRegistroServidor = new ParamConvenioRegistroSerId();
                        paramConvenioRegistroServidor.setTpsCodigo(next.getTpsCodigo());
                        paramConvenioRegistroServidor.setRseCodigo(next.getRseCodigo());
                        paramConvenioRegistroServidor.setCnvCodigo(next.getCnvCodigo());
                        final ParamConvenioRegistroSer paramCnvRseBean = ParamConvenioRegistroServidorHome.findByPrimaryKey(paramConvenioRegistroServidor);
                        valorAntigoCse = paramCnvRseBean.getPcrVlrCse();
                        valorAntigoCsa = paramCnvRseBean.getPcrVlrCsa();
                        valorAntigoSer = paramCnvRseBean.getPcrVlrSer();
                        valorAntigo = paramCnvRseBean.getPcrVlr();

                        final int pcrVlrSerOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrSer()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrSer()) : Integer.MAX_VALUE;
                        final int pcrVlrCsaOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrCsa()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrCsa()) : Integer.MAX_VALUE;
                        final int pcrVlrCseOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrCse()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrCse()) : Integer.MAX_VALUE;
                        final int pcrVlrOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlr()) ? Integer.parseInt(paramCnvRseBean.getPcrVlr()) : Integer.MAX_VALUE;

                        final int pcrVlrSerNew = responsavel.isSer() && !TextHelper.isNull(next.getPcrVlrSer()) ? Integer.parseInt(next.getPcrVlrSer()) : responsavel.isSer() ? Integer.MAX_VALUE : pcrVlrSerOld;
                        final int pcrVlrCsaNew = responsavel.isCsa() && !TextHelper.isNull(next.getPcrVlrCsa()) ? Integer.parseInt(next.getPcrVlrCsa()) : responsavel.isCsa() ? Integer.MAX_VALUE : pcrVlrCsaOld;
                        final int pcrVlrCseNew = responsavel.isCseSupOrg() && !TextHelper.isNull(next.getPcrVlrCse()) ? Integer.parseInt(next.getPcrVlrCse()) : responsavel.isCseSupOrg() ? Integer.MAX_VALUE : pcrVlrCseOld;
                        final int pcrVlrNew = Math.min(pcrVlrSerNew, Math.min(pcrVlrCsaNew, pcrVlrCseNew));

                        if (pcrVlrSerOld != pcrVlrSerNew) {
                            paramCnvRseBean.setPcrVlrSer(pcrVlrSerNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrSerNew) : null);
                            dadosAlterados = true;
                        }
                        if (pcrVlrCsaOld != pcrVlrCsaNew) {
                            paramCnvRseBean.setPcrVlrCsa(pcrVlrCsaNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrCsaNew) : null);
                            dadosAlterados = true;
                        }
                        if (pcrVlrCseOld != pcrVlrCseNew) {
                            paramCnvRseBean.setPcrVlrCse(pcrVlrCseNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrCseNew) : null);
                            dadosAlterados = true;
                        }
                        if (pcrVlrOld != pcrVlrNew) {
                            paramCnvRseBean.setPcrVlr(pcrVlrNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrNew) : null);
                            dadosAlterados = true;
                        }
                        if (!next.getPcrObs().equals(paramCnvRseBean.getPcrObs())) {
                            paramCnvRseBean.setPcrObs(next.getPcrObs());
                            dadosAlterados = true;
                        }
                        if (dadosAlterados) {
                            if (pcrVlrNew != Integer.MAX_VALUE) {
                                paramCnvRseBean.setPcrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                                AbstractEntityHome.update(paramCnvRseBean);
                                tipo = 1;

                                // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                                if (pcrVlrNew == 0) {
                                    bloqueado.add(createTransferObject(next, valorAntigo, valorAntigoCse, valorAntigoCsa, valorAntigoSer));
                                } else {
                                    alterado.add(createTransferObject(next, valorAntigo, valorAntigoCse, valorAntigoCsa, valorAntigoSer));
                                }
                            } else {
                                // Remove o parâmetro caso o valor mínimo entre os papéis é Integer.MAX_VALUE
                                AbstractEntityHome.remove(paramCnvRseBean);
                                tipo = 2;
                                desbloqueado.add(createTransferObject(next, valorAntigo, valorAntigoCse, valorAntigoCsa, valorAntigoSer));
                            }
                        }
                    } catch (final FindException e) {
                        // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                        if ((next.getPcrVlr() != null) && !"".equals(next.getPcrVlr())) {
                            ParamConvenioRegistroServidorHome.create(next.getTpsCodigo(), next.getRseCodigo(), next.getCnvCodigo(), next.getPcrVlr(), next.getPcrVlrSer(), next.getPcrVlrCsa(), next.getPcrVlrCse(), next.getPcrObs());
                            tipo = 0;
                            // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                            if (Integer.parseInt(next.getPcrVlr()) == 0) {
                                bloqueado.add(createTransferObject(next, null, null, null, null));
                            } else {
                                alterado.add(createTransferObject(next, null, null, null, null));
                            }
                            dadosAlterados = true;
                        }
                    }

                    if (dadosAlterados && (tipo == 0)) {
                        // Grava Log de criação
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setConvenio(next.getCnvCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.bloqueio.convenio.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 1)) {
                        // Grava Log de atualização
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setConvenio(next.getCnvCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atualizando.bloqueio.convenio.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 2)) {
                        // Grava Log de exclusão
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setConvenio(next.getCnvCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.desbloqueio.bloqueio.convenio.log", responsavel));
                        log.write();
                    }
                }

                final String orsObs = (tmoObject != null) && (tmoObject.getAttribute("orsObs") != null) ? tmoObject.getAttribute("orsObs").toString() : "";
                final String tmoCodigo = (tmoObject != null) && (tmoObject.getAttribute("tmoCodigo") != null) ? tmoObject.getAttribute("tmoCodigo").toString() : "";
                // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
                if (!TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel) && TextHelper.isNull(orsObs)) {
                    throw new ParametroControllerException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
                }
                if (!bloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(bloqueado, Columns.CNV_COD_VERBA, Columns.PCR_VLR, orsObs, "mensagem.informacao.bloqueio.convenio.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_VERBA, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!alterado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(alterado, Columns.CNV_COD_VERBA, Columns.PCR_VLR, orsObs, "mensagem.informacao.atualizando.bloqueio.convenio.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_BLOQUEIO_VERBAS, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!desbloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(desbloqueado, Columns.CNV_COD_VERBA, Columns.PCR_VLR, orsObs, "mensagem.informacao.desbloqueio.bloqueio.convenio.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_DESBLOQUEIO_VERBAS, observacaoOrs, tmoCodigo, responsavel);
                }

                // Envia email com as alterações para o servidor, se necessário
                if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_BLOQ_DESBLOQ_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    EnviaEmailHelper.enviarEmailBloqDesbloqServidorVerba(rseCodigo, bloqueado, desbloqueado, alterado, responsavel);
                }
            }
        } catch (com.zetra.econsig.exception.CreateException | UpdateException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    /**
     * Cria o TransfefrObject que será repassado à rotina de envio de emails
     *
     * @param paramSvcRseBean
     * @return
     */
    private TransferObject createTransferObject(ParamCnvRseTO paramCnvRseTO, String pcrVlrOld, String pcrVlrCseOld, String pcrVlrCsaOld, String pcrVlrSerOld) {
        final TransferObject to = new CustomTransferObject();
        try {
            final Convenio cnv = ConvenioHome.findByPrimaryKey(paramCnvRseTO.getCnvCodigo());
            to.setAttribute(Columns.CNV_COD_VERBA, cnv.getCnvCodVerba());
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
        }
        to.setAttribute(Columns.PCR_CNV_CODIGO, paramCnvRseTO.getCnvCodigo());
        to.setAttribute(Columns.PCR_DATA_CADASTRO, paramCnvRseTO.getPcrDataCadastro());
        to.setAttribute(Columns.PCR_OBS, paramCnvRseTO.getPcrObs());
        to.setAttribute(Columns.PCR_RSE_CODIGO, paramCnvRseTO.getRseCodigo());
        to.setAttribute(Columns.PCR_TPS_CODIGO, paramCnvRseTO.getTpsCodigo());

        to.setAttribute(Columns.PCR_VLR, paramCnvRseTO.getPcrVlr());
        to.setAttribute(Columns.PCR_VLR_CSE, paramCnvRseTO.getPcrVlrCse());
        to.setAttribute(Columns.PCR_VLR_CSA, paramCnvRseTO.getPcrVlrCsa());
        to.setAttribute(Columns.PCR_VLR_SER, paramCnvRseTO.getPcrVlrSer());

        to.setAttribute(Columns.PCR_VLR + "_OLD", pcrVlrOld);
        to.setAttribute(Columns.PCR_VLR_CSE + "_OLD", pcrVlrCseOld);
        to.setAttribute(Columns.PCR_VLR_CSA + "_OLD", pcrVlrCsaOld);
        to.setAttribute(Columns.PCR_VLR_SER + "_OLD", pcrVlrSerOld);

        return to;
    }

    @Override
    public void setBloqueioCnvRegistroServidor(ParamCnvRseTO paramCnvRse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            try {
                // Busca o parâmetro de convenio por servidor
                final ParamConvenioRegistroSerId paramConvenioRegistroServidor = new ParamConvenioRegistroSerId();
                paramConvenioRegistroServidor.setTpsCodigo(paramCnvRse.getTpsCodigo());
                paramConvenioRegistroServidor.setRseCodigo(paramCnvRse.getRseCodigo());
                paramConvenioRegistroServidor.setCnvCodigo(paramCnvRse.getCnvCodigo());
                final ParamConvenioRegistroSer paramCnvRseBean = ParamConvenioRegistroServidorHome.findByPrimaryKey(paramConvenioRegistroServidor);
                // Se encontrou o parâmetro, verifica se tem que excluir ou atualizar
                if ((paramCnvRse.getPcrVlr() != null) && !"".equals(paramCnvRse.getPcrVlr())) {
                    // Atualiza o parâmetro e a data de cadastro

                    final int pcrVlrSerOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrSer()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrSer()) : Integer.MAX_VALUE;
                    final int pcrVlrCsaOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrCsa()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrCsa()) : Integer.MAX_VALUE;
                    final int pcrVlrCseOld = !TextHelper.isNull(paramCnvRseBean.getPcrVlrCse()) ? Integer.parseInt(paramCnvRseBean.getPcrVlrCse()) : Integer.MAX_VALUE;

                    final int pcrVlrSerNew = responsavel.isSer() && !TextHelper.isNull(paramCnvRse.getPcrVlrSer()) ? Integer.parseInt(paramCnvRse.getPcrVlrSer()) : responsavel.isSer() ? Integer.MAX_VALUE : pcrVlrSerOld;
                    final int pcrVlrCsaNew = responsavel.isCsa() && !TextHelper.isNull(paramCnvRse.getPcrVlrCsa()) ? Integer.parseInt(paramCnvRse.getPcrVlrCsa()) : responsavel.isCsa() ? Integer.MAX_VALUE : pcrVlrCsaOld;
                    final int pcrVlrCseNew = responsavel.isCseSupOrg() && !TextHelper.isNull(paramCnvRse.getPcrVlrCse()) ? Integer.parseInt(paramCnvRse.getPcrVlrCse()) : responsavel.isCseSupOrg() ? Integer.MAX_VALUE : pcrVlrCseOld;
                    final int pcrVlrNew = Math.min(pcrVlrSerNew, Math.min(pcrVlrCsaNew, pcrVlrCseNew));

                    paramCnvRseBean.setPcrVlr(pcrVlrNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrNew) : null);
                    paramCnvRseBean.setPcrVlrSer(pcrVlrSerNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrSerNew) : null);
                    paramCnvRseBean.setPcrVlrCsa(pcrVlrCsaNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrCsaNew) : null);
                    paramCnvRseBean.setPcrVlrCse(pcrVlrCseNew != Integer.MAX_VALUE ? String.valueOf(pcrVlrCseNew) : null);
                    paramCnvRseBean.setPcrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    paramCnvRseBean.setPcrObs(paramCnvRse.getPcrObs());

                    AbstractEntityHome.update(paramCnvRseBean);
                } else {
                    // remove o parâmetro
                    AbstractEntityHome.remove(paramCnvRseBean);
                    final ConvenioTransferObject convenio = convenioController.findByPrimaryKey(paramCnvRseBean.getCnvCodigo(), responsavel);
                    final CustomTransferObject ser = pesquisarServidorController.buscaServidor(paramCnvRseBean.getRseCodigo(), responsavel);
                    final String ocsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.desbloqueio.bloqueio.convenio.unificado", responsavel, convenio.getCnvDescricao(), (String) ser.getAttribute(Columns.SER_NOME));
                    OcorrenciaRegistroServidorHome.create(paramCnvRseBean.getRseCodigo(), CodedValues.TOC_RSE_DESBLOQUEIO_VERBAS, responsavel.getUsuCodigo(), ocsObs, null, null);
                }
            } catch (final FindException e) {
                // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                if ((paramCnvRse.getPcrVlr() != null) && !"".equals(paramCnvRse.getPcrVlr())) {
                    final ConvenioTransferObject convenio = convenioController.findByPrimaryKey(paramCnvRse.getCnvCodigo(), responsavel);
                    final CustomTransferObject ser = pesquisarServidorController.buscaServidor(paramCnvRse.getRseCodigo(), responsavel);
                    final String obsBloqueio = ApplicationResourcesHelper.getMessage("mensagem.informacao.bloqueio.convenio.unificado", responsavel, convenio.getCnvDescricao(), (String) ser.getAttribute(Columns.SER_NOME));
                    ParamConvenioRegistroServidorHome.create(paramCnvRse.getTpsCodigo(), paramCnvRse.getRseCodigo(), paramCnvRse.getCnvCodigo(), paramCnvRse.getPcrVlr(), paramCnvRse.getPcrVlrSer(), paramCnvRse.getPcrVlrCsa(), paramCnvRse.getPcrVlrCse(), paramCnvRse.getPcrObs());
                    OcorrenciaRegistroServidorHome.create(paramCnvRse.getRseCodigo(), CodedValues.TOC_RSE_BLOQUEIO_VERBA, responsavel.getUsuCodigo(), obsBloqueio, null, null);
                }
            }

            // Grava Log de atualização
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoParamSvc(paramCnvRse.getTpsCodigo());
            log.setRegistroServidor(paramCnvRse.getRseCodigo());
            log.setConvenio(paramCnvRse.getCnvCodigo());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atualizando.bloqueio.convenio", responsavel));
            log.add(paramCnvRse.toString());
            log.write();

        } catch (com.zetra.econsig.exception.CreateException | UpdateException | RemoveException |
                 LogControllerException | ConvenioControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void corrigeBloqueioServidor(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamConvenioRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamConvenioRegistroServidorDAO();
            dao.corrigeBloqueioServidor();
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.corrigindo.bloqueio.convenios", responsavel));
            log.write();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void copiaBloqueioCnv(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamConvenioRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamConvenioRegistroServidorDAO();
            dao.copiaBloqueioCnv(rseCodNovo, rseCodAnt);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CNV_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodNovo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.convenios.antigo.para.novo", responsavel));
            log.write();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean temServidorBloqueadoCnv(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ContaServidorBloqueadoCnvQuery query = new ContaServidorBloqueadoCnvQuery();
            return (query.executarContador() > 0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** FIM BLOQUEIO DE CONVENIO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE SERVICO POR REGISTRO SERVIDOR
     **/

    @Override
    public List<TransferObject> lstBloqueioSvcRegistroServidor(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaBloqueioServicoServidorQuery query = new ListaBloqueioServicoServidorQuery();
            query.rseCodigo = rseCodigo;
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, Long> getBloqueioSvcRegistroServidor(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ObtemBloqueioServicoRegistroServidorQuery query = new ObtemBloqueioServicoRegistroServidorQuery();
            query.rseCodigo = rseCodigo;
            query.svcCodigo = svcCodigo;
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setBloqueioSvcRegistroServidor(ParamSvcRseTO bloqueio, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        final List<ParamSvcRseTO> bloqueios = new ArrayList<>();
        bloqueios.add(bloqueio);
        setBloqueioSvcRegistroServidor(bloqueios, tmoObject, responsavel);
    }

    @Override
    public void setBloqueioSvcRegistroServidor(List<ParamSvcRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            if ((bloqueios != null) && (bloqueios.size() > 0)) {
                String rseCodigo = bloqueios.get(0).getRseCodigo();
                ParamServicoRegistroSer paramSvcRseBean = null;

                // Listas com as 3 possibilidades de alteração para gerar um email detalhando estas para o servidor, se necessário.
                final List<TransferObject> bloqueado = new ArrayList<>();
                final List<TransferObject> desbloqueado = new ArrayList<>();
                final List<TransferObject> alterado = new ArrayList<>();
                String valorAntigo = null;

                for (final ParamSvcRseTO next : bloqueios) {
                    boolean dadosAlterados = false;
                    int tipo = 3;

                    try {
                        // Busca o parâmetro de convenio por servidor
                        final ParamServicoRegistroSerId paramServicoRegistroServidor = new ParamServicoRegistroSerId();
                        paramServicoRegistroServidor.setTpsCodigo(next.getTpsCodigo());
                        paramServicoRegistroServidor.setRseCodigo(next.getRseCodigo());
                        paramServicoRegistroServidor.setSvcCodigo(next.getSvcCodigo());
                        rseCodigo = next.getRseCodigo();
                        paramSvcRseBean = ParamServicoRegistroServidorHome.findByPrimaryKey(paramServicoRegistroServidor);
                        valorAntigo = paramSvcRseBean.getPsrVlr();
                        // Se encontrou o parâmetro, verifica se tem que excluir ou atualizar
                        if ((next.getPsrVlr() != null) && !"".equals(next.getPsrVlr())) {
                            // Atualiza o parâmetro e a data de cadastro, se necessário
                            if (!next.getPsrVlr().equals(paramSvcRseBean.getPsrVlr()) || (TextHelper.isNull(next.getPsrObs()) && !TextHelper.isNull(paramSvcRseBean.getPsrObs())) || (!TextHelper.isNull(next.getPsrObs()) && !next.getPsrObs().equals(paramSvcRseBean.getPsrObs())) || (TextHelper.isNull(next.getPsrAlteradoPeloServidor()) && !TextHelper.isNull(paramSvcRseBean.getPsrAlteradoPeloServidor())) || (!TextHelper.isNull(next.getPsrAlteradoPeloServidor()) && !next.getPsrAlteradoPeloServidor().equals(paramSvcRseBean.getPsrAlteradoPeloServidor()))) {
                                paramSvcRseBean.setPsrVlr(next.getPsrVlr());
                                paramSvcRseBean.setPsrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                                paramSvcRseBean.setPsrObs(next.getPsrObs());
                                paramSvcRseBean.setPsrAlteradoPeloServidor(next.getPsrAlteradoPeloServidor());
                                // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                                if (Integer.parseInt(next.getPsrVlr()) == 0) {
                                    bloqueado.add(createTransferObject(next, valorAntigo));
                                } else {
                                    alterado.add(createTransferObject(next, valorAntigo));
                                }
                                AbstractEntityHome.update(paramSvcRseBean);
                                tipo = 1;
                                dadosAlterados = true;
                            }
                        } else {
                            // remove o parâmetro
                            AbstractEntityHome.remove(paramSvcRseBean);
                            tipo = 2;
                            dadosAlterados = true;
                            desbloqueado.add(createTransferObject(next, valorAntigo));
                        }
                    } catch (final FindException e) {
                        // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                        if ((next.getPsrVlr() != null) && !"".equals(next.getPsrVlr())) {
                            paramSvcRseBean = ParamServicoRegistroServidorHome.create(next.getTpsCodigo(), next.getRseCodigo(), next.getSvcCodigo(), next.getPsrVlr(), next.getPsrObs(), next.getPsrAlteradoPeloServidor());
                            tipo = 0;
                            dadosAlterados = true;
                            // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                            if (Integer.parseInt(next.getPsrVlr()) == 0) {
                                bloqueado.add(createTransferObject(next, null));
                            } else {
                                alterado.add(createTransferObject(next, null));
                            }
                        }
                    }

                    // Grava Log de atualização, se necessário
                    if (dadosAlterados && (tipo == 0)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setServico(next.getSvcCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.bloqueio.servico.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 1)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setServico(next.getSvcCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.alteracao.bloqueio.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 2)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setServico(next.getSvcCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.desbloqueio.log", responsavel));
                        log.write();
                    }

                }

                final String orsObs = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("orsObs")) ? (String) tmoObject.getAttribute("orsObs") : "";
                final String tmoCodigo = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("tmoCodigo")) ? (String) tmoObject.getAttribute("tmoCodigo") : "";
                // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
                if (!TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel) && orsObs.isEmpty()) {
                    throw new ParametroControllerException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
                }
                if (!bloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(bloqueado, Columns.SVC_DESCRICAO, Columns.PSR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.bloqueio.servico.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_SERVICO, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!alterado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(alterado, Columns.SVC_DESCRICAO, Columns.PSR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.alteracao.bloqueio.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_BLOQUEIO_SERVICO_SERVIDOR, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!desbloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(desbloqueado, Columns.SVC_DESCRICAO, Columns.PSR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.desbloqueio.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_DESBLOQUEIO_SERVICO_SERVIDOR, observacaoOrs, tmoCodigo, responsavel);

                }

                // Envia email com as alterações para o servidor, se necessário
                if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_BLOQ_DESBLOQ_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    EnviaEmailHelper.enviarEmailBloqDesbloqServidorServico(rseCodigo, bloqueado, desbloqueado, alterado, responsavel);
                }
            }
        } catch (com.zetra.econsig.exception.CreateException | RemoveException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    private String retornaObservacaoParamOcorrenciaRse(List<TransferObject> lista, String colunaDescricao, String colunaValor, String orsObs, String rotulo, AcessoSistema responsavel) {
        final StringBuilder descricao = new StringBuilder();
        final StringBuilder obs = new StringBuilder();
        for (final TransferObject to : lista) {
            if (descricao.length() > 0) {
                descricao.append(",");
            }
            descricao.append(to.getAttribute(colunaDescricao));

            if (obs.length() > 0) {
                obs.append(",");
            }

            final String novo = !TextHelper.isNull(to.getAttribute(colunaValor)) ? to.getAttribute(colunaValor).toString() : ApplicationResourcesHelper.getMessage("rotulo.nulo.singular", responsavel);
            final String antigo = !TextHelper.isNull(to.getAttribute(colunaValor + "_OLD")) ? to.getAttribute(colunaValor + "_OLD").toString() : ApplicationResourcesHelper.getMessage("rotulo.nulo.singular", responsavel);
            obs.append(antigo).append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.para", responsavel)).append(" ").append(novo);
        }
        obs.append(".");

        String observacao = ApplicationResourcesHelper.getMessage(rotulo, responsavel, descricao.toString(), obs.toString());

        if (!orsObs.isEmpty()) {
            observacao += orsObs;
        }

        return observacao;
    }

    /**
     * Cria o TransfefrObject que será repassado à rotina de envio de emails
     *
     * @param paramSvcRseBean
     * @return
     */
    private TransferObject createTransferObject(ParamSvcRseTO paramSvcRseTO, String psrVlrOld) {
        final TransferObject to = new CustomTransferObject();
        try {
            final TransferObject servico = servicoController.findServico(paramSvcRseTO.getSvcCodigo());
            to.setAttribute(Columns.SVC_DESCRICAO, servico.getAttribute(Columns.SVC_DESCRICAO));
        } catch (final ServicoControllerException e) {
            LOG.error(e.getMessage(), e);
        }
        to.setAttribute(Columns.PSR_VLR, paramSvcRseTO.getPsrVlr());
        to.setAttribute(Columns.PSR_VLR + "_OLD", psrVlrOld);
        to.setAttribute(Columns.PSR_TPS_CODIGO, paramSvcRseTO.getTpsCodigo());
        to.setAttribute(Columns.PSR_RSE_CODIGO, paramSvcRseTO.getRseCodigo());
        to.setAttribute(Columns.PSR_SVC_CODIGO, paramSvcRseTO.getSvcCodigo());

        return to;
    }

    @Override
    public void copiaBloqueioSvc(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamServicoRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamServicoRegistroServidorDAO();
            dao.copiaBloqueioSvc(rseCodNovo, rseCodAnt);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodNovo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.servico.antigo.para.novo", responsavel));
            log.write();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private List<TransferObject> lstParamSvcRse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcRseQuery query = new ListaParamSvcRseQuery();
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void createParamSvcRse(ParamSvcRseTO paramSvcRse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            ParamServicoRegistroServidorHome.create(paramSvcRse.getTpsCodigo(), paramSvcRse.getRseCodigo(), paramSvcRse.getSvcCodigo(), paramSvcRse.getPsrVlr(), paramSvcRse.getPsrObs(), paramSvcRse.getPsrAlteradoPeloServidor());
            // Grava log da criação do parâmetro de bloqueio de serviço/servidor
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_REGISTRO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
            log.setTipoParamSvc(paramSvcRse.getTpsCodigo());
            log.setRegistroServidor(paramSvcRse.getRseCodigo());
            log.setServico(paramSvcRse.getSvcCodigo());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.servico.para.novo.servico", responsavel));
            log.write();

        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.criar.parametros.deste.servico.erro.interno", responsavel, ex.getMessage());
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean temServidorBloqueadoSvc(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ContaServidorBloqueadoSvcQuery query = new ContaServidorBloqueadoSvcQuery();
            return (query.executarContador() > 0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** FIM BLOQUEIO DE SERVICO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE NATUREZA DE SERVICO POR REGISTRO SERVIDOR
     **/
    @Override
    public List<TransferObject> lstBloqueioNseRegistroServidor(String rseCodigo, String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaBloqueioNaturezaServicoServidorQuery query = new ListaBloqueioNaturezaServicoServidorQuery();
            query.rseCodigo = rseCodigo;
            query.nseCodigo = nseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, Long> getBloqueioNseRegistroServidor(String rseCodigo, String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ObtemBloqueioNseRegistroServidorQuery query = new ObtemBloqueioNseRegistroServidorQuery();
            query.rseCodigo = rseCodigo;
            query.nseCodigo = nseCodigo;
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setBloqueioNseRegistroServidor(List<ParamNseRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            if ((bloqueios != null) && (bloqueios.size() > 0)) {
                final String rseCodigo = bloqueios.get(0).getRseCodigo();
                ParamNseRegistroSer paramNseRseBean = null;

                // Listas com as 3 possibilidades de alteração para gerar um email detalhando estas para o servidor, se necessário.
                final List<TransferObject> bloqueado = new ArrayList<>();
                final List<TransferObject> desbloqueado = new ArrayList<>();
                final List<TransferObject> alterado = new ArrayList<>();
                String valorAntigo = null;

                for (final ParamNseRseTO next : bloqueios) {
                    boolean dadosAlterados = false;
                    int tipo = 3;

                    try {
                        // Busca o parâmetro de natureza de serviço por servidor
                        final ParamNseRegistroSerId paramNseRegistroServidor = new ParamNseRegistroSerId();
                        paramNseRegistroServidor.setTpsCodigo(next.getTpsCodigo());
                        paramNseRegistroServidor.setRseCodigo(next.getRseCodigo());
                        paramNseRegistroServidor.setNseCodigo(next.getNseCodigo());
                        paramNseRseBean = ParamNseRegistroServidorHome.findByPrimaryKey(paramNseRegistroServidor);
                        valorAntigo = paramNseRseBean.getPnrVlr();
                        // Se encontrou o parâmetro, verifica se tem que excluir ou atualizar
                        if ((next.getPnrVlr() != null) && !"".equals(next.getPnrVlr())) {
                            // Atualiza o parâmetro e a data de cadastro, se necessário
                            if (!next.getPnrVlr().equals(paramNseRseBean.getPnrVlr()) || !next.getPnrObs().equals(paramNseRseBean.getPnrObs()) || !next.getPnrAlteradoPeloServidor().equals(paramNseRseBean.getPnrAlteradoPeloServidor())) {
                                paramNseRseBean.setPnrVlr(next.getPnrVlr());
                                paramNseRseBean.setPnrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                                paramNseRseBean.setPnrObs(next.getPnrObs());
                                paramNseRseBean.setPnrAlteradoPeloServidor(next.getPnrAlteradoPeloServidor());

                                AbstractEntityHome.update(paramNseRseBean);
                                tipo = 1;
                                dadosAlterados = true;
                                // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                                if (Integer.parseInt(next.getPnrVlr()) == 0) {
                                    bloqueado.add(createTransferObject(next, valorAntigo));
                                } else {
                                    alterado.add(createTransferObject(next, valorAntigo));
                                }
                            }
                        } else {
                            // remove o parâmetro
                            AbstractEntityHome.remove(paramNseRseBean);
                            tipo = 2;
                            dadosAlterados = true;
                            desbloqueado.add(createTransferObject(next, valorAntigo));
                        }
                    } catch (final FindException e) {
                        // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                        if ((next.getPnrVlr() != null) && !"".equals(next.getPnrVlr())) {
                            ParamNseRegistroServidorHome.create(next.getTpsCodigo(), next.getRseCodigo(), next.getNseCodigo(), next.getPnrVlr(), next.getPnrObs(), next.getPnrAlteradoPeloServidor());
                            tipo = 0;
                            dadosAlterados = true;
                            // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                            if (Integer.parseInt(next.getPnrVlr()) == 0) {
                                bloqueado.add(createTransferObject(next, null));
                            } else {
                                alterado.add(createTransferObject(next, null));
                            }
                        }
                    }

                    // Grava Log de atualização
                    if (dadosAlterados && (tipo == 0)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_NSE_REGISTRO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setNaturezaServico(next.getNseCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.bloqueio.servico.por.natureza.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 1)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_NSE_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setNaturezaServico(next.getNseCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.alteracao.bloqueio.servico.por.natureza.log", responsavel));
                        log.write();
                    } else if (dadosAlterados && (tipo == 2)) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_NSE_REGISTRO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
                        log.setTipoParamSvc(next.getTpsCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setNaturezaServico(next.getNseCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.desbloqueio.servico.por.natureza.log", responsavel));
                        log.write();
                    }
                }

                final String orsObs = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("orsObs")) ? (String) tmoObject.getAttribute("orsObs") : "";
                final String tmoCodigo = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("tmoCodigo")) ? (String) tmoObject.getAttribute("tmoCodigo") : "";
                // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
                if (!TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel) && orsObs.isEmpty()) {
                    throw new ParametroControllerException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
                }
                if (!bloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(bloqueado, Columns.NSE_DESCRICAO, Columns.PNR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.bloqueio.servico.por.natureza.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_SERVICO_POR_NATUREZA, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!alterado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(alterado, Columns.NSE_DESCRICAO, Columns.PNR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.alteracao.bloqueio.servico.por.natureza.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_BLOQUEIO_SERVICO_SERVIDOR_NATUREZA, observacaoOrs, tmoCodigo, responsavel);

                }
                if (!desbloqueado.isEmpty()) {
                    final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(desbloqueado, Columns.NSE_DESCRICAO, Columns.PNR_VLR, orsObs, "mensagem.ocorrencia.ors.obs.desbloqueio.servico.por.natureza.unificado", responsavel);
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_DESBLOQUEIO_SERVICO_SERVIDOR_NATUREZA, observacaoOrs, tmoCodigo, responsavel);

                }

                // Envia email com as alterações para o servidor, se necessário
                if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_BLOQ_DESBLOQ_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    EnviaEmailHelper.enviarEmailBloqDesbloqServidorNaturezaServico(rseCodigo, bloqueado, desbloqueado, alterado, responsavel);
                }
            }
        } catch (com.zetra.econsig.exception.CreateException | RemoveException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    /**
     * Cria o TransfefrObject que será repassado à rotina de envio de emails
     *
     * @param paramSvcRseBean
     * @return
     */
    private TransferObject createTransferObject(ParamNseRseTO paramSvcRseTO, String pnrVlrOld) {
        final TransferObject to = new CustomTransferObject();
        try {
            final NaturezaServico natureza = NaturezaServicoHome.findByPrimaryKey(paramSvcRseTO.getNseCodigo());
            to.setAttribute(Columns.NSE_DESCRICAO, natureza.getNseDescricao());
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
        }
        to.setAttribute(Columns.PNR_ALTERADO_PELO_SERVIDOR, paramSvcRseTO.getPnrAlteradoPeloServidor());
        to.setAttribute(Columns.PNR_DATA_CADASTRO, paramSvcRseTO.getPnrDataCadastro());
        to.setAttribute(Columns.PNR_NSE_CODIGO, paramSvcRseTO.getNseCodigo());
        to.setAttribute(Columns.PNR_OBS, paramSvcRseTO.getPnrObs());
        to.setAttribute(Columns.PNR_RSE_CODIGO, paramSvcRseTO.getRseCodigo());
        to.setAttribute(Columns.PNR_TPS_CODIGO, paramSvcRseTO.getTpsCodigo());

        to.setAttribute(Columns.PNR_VLR, paramSvcRseTO.getPnrVlr());
        to.setAttribute(Columns.PNR_VLR + "_OLD", pnrVlrOld);
        return to;
    }

    @Override
    public void setBloqueioNseRegistroServidor(ParamNseRseTO paramNseRse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            try {
                // Busca o parâmetro de natureza de serviço por servidor
                final ParamNseRegistroSerId paramNseRegistroServidor = new ParamNseRegistroSerId();
                paramNseRegistroServidor.setTpsCodigo(paramNseRse.getTpsCodigo());
                paramNseRegistroServidor.setRseCodigo(paramNseRse.getRseCodigo());
                paramNseRegistroServidor.setNseCodigo(paramNseRse.getNseCodigo());
                final ParamNseRegistroSer paramNseRseBean = ParamNseRegistroServidorHome.findByPrimaryKey(paramNseRegistroServidor);
                // Se encontrou o parâmetro, verifica se tem que excluir ou atualizar
                if ((paramNseRse.getPnrVlr() != null) && !"".equals(paramNseRse.getPnrVlr())) {
                    // Atualiza o parâmetro e a data de cadastro
                    paramNseRseBean.setPnrVlr(paramNseRse.getPnrVlr());
                    paramNseRseBean.setPnrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    paramNseRseBean.setPnrObs(paramNseRse.getPnrObs());
                    paramNseRseBean.setPnrAlteradoPeloServidor(paramNseRse.getPnrAlteradoPeloServidor());

                    AbstractEntityHome.update(paramNseRseBean);
                } else {
                    // remove o parâmetro
                    AbstractEntityHome.remove(paramNseRseBean);
                }
            } catch (final FindException e) {
                // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                if ((paramNseRse.getPnrVlr() != null) && !"".equals(paramNseRse.getPnrVlr())) {
                    ParamNseRegistroServidorHome.create(paramNseRse.getTpsCodigo(), paramNseRse.getRseCodigo(), paramNseRse.getNseCodigo(), paramNseRse.getPnrVlr(), paramNseRse.getPnrObs(), paramNseRse.getPnrAlteradoPeloServidor());
                }
            }

            // Grava Log de atualização
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_NSE_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoParamSvc(paramNseRse.getTpsCodigo());
            log.setRegistroServidor(paramNseRse.getRseCodigo());
            log.setNaturezaServico(paramNseRse.getNseCodigo());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atualizando.bloqueio.natureza.servico", responsavel));
            log.add(paramNseRse.toString());
            log.write();
        } catch (com.zetra.econsig.exception.CreateException | UpdateException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void copiaBloqueioNse(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamNseRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamNseRegistroServidorDAO();
            dao.copiaBloqueioNse(rseCodNovo, rseCodAnt);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_NSE_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodNovo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.natureza.servico.antigo.para.novo", responsavel));
            log.write();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean temServidorBloqueadoNse(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ContaServidorBloqueadoNseQuery query = new ContaServidorBloqueadoNseQuery();
            return (query.executarContador() > 0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** FIM BLOQUEIO DE NATUREZA DE SERVICO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE CONSIGNATÁRIA POR REGISTRO SERVIDOR
     **/
    @Override
    public List<TransferObject> lstBloqueioCsaRegistroServidor(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaBloqueioCsaServidorQuery query = new ListaBloqueioCsaServidorQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, Long> getBloqueioCsaRegistroServidor(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ObtemBloqueioCsaRegistroServidorQuery query = new ObtemBloqueioCsaRegistroServidorQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setBloqueioCsaRegistroServidor(ParamCsaRseTO bloqueio, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        final List<ParamCsaRseTO> bloqueios = new ArrayList<>();
        bloqueios.add(bloqueio);
        setBloqueioCsaRegistroServidor(bloqueios, tmoObject, responsavel);
    }

    @Override
    public void setBloqueioCsaRegistroServidor(List<ParamCsaRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Listas com as 3 possibilidades de alteração para gerar um email detalhando estas para o servidor, se necessário.
            final List<TransferObject> bloqueado = new ArrayList<>();
            final List<TransferObject> desbloqueado = new ArrayList<>();
            final List<TransferObject> alterado = new ArrayList<>();
            String rseCodigo = "";

            if ((bloqueios != null) && !bloqueios.isEmpty()) {
                rseCodigo = bloqueios.get(0).getRseCodigo();
                ParamConsignatariaRegistroSer paramCsaRseBean = null;
                String valorAntigo = null;

                for (final ParamCsaRseTO next : bloqueios) {
                    boolean dadosAlterados = false;

                    try {
                        // Busca o parâmetro de convenio por servidor
                        final ParamConsignatariaRegistroSerId paramConsignatariaRegistroServidor = new ParamConsignatariaRegistroSerId();
                        paramConsignatariaRegistroServidor.setTpaCodigo(next.getTpaCodigo());
                        paramConsignatariaRegistroServidor.setRseCodigo(next.getRseCodigo());
                        paramConsignatariaRegistroServidor.setCsaCodigo(next.getCsaCodigo());
                        rseCodigo = next.getRseCodigo();
                        paramCsaRseBean = ParamConsignatariaRegistroServidorHome.findByPrimaryKey(paramConsignatariaRegistroServidor);
                        valorAntigo = paramCsaRseBean.getPrcVlr();
                        // Se encontrou o parâmetro, verifica se tem que excluir ou atualizar
                        if ((next.getPrcVlr() != null) && !"".equals(next.getPrcVlr())) {
                            // Atualiza o parâmetro e a data de cadastro, se necessário
                            if (!next.getPrcVlr().equals(paramCsaRseBean.getPrcVlr()) || (TextHelper.isNull(next.getPrcObs()) && !TextHelper.isNull(paramCsaRseBean.getPrcObs())) || (!TextHelper.isNull(next.getPrcObs()) && !next.getPrcObs().equals(paramCsaRseBean.getPrcObs()))) {
                                paramCsaRseBean.setPrcVlr(next.getPrcVlr());
                                paramCsaRseBean.setPrcDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                                paramCsaRseBean.setPrcObs(next.getPrcObs());
                                // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                                if (Integer.parseInt(next.getPrcVlr()) == 0) {
                                    bloqueado.add(createTransferObject(next, valorAntigo, responsavel));
                                } else {
                                    alterado.add(createTransferObject(next, valorAntigo, responsavel));
                                }
                                AbstractEntityHome.update(paramCsaRseBean);
                                dadosAlterados = true;
                            }
                        } else {
                            // remove o parâmetro
                            AbstractEntityHome.remove(paramCsaRseBean);
                            dadosAlterados = true;
                            desbloqueado.add(createTransferObject(next, valorAntigo, responsavel));
                        }
                    } catch (final FindException e) {
                        // Não existe o parâmetro, então cria o parâmetro caso não seja vazio
                        if ((next.getPrcVlr() != null) && !"".equals(next.getPrcVlr())) {
                            paramCsaRseBean = ParamConsignatariaRegistroServidorHome.create(next.getTpaCodigo(), next.getRseCodigo(), next.getCsaCodigo(), next.getPrcVlr(), next.getPrcObs());
                            dadosAlterados = true;
                            // Verifica qual foi a alteração para poder criar a mensagem correta para o servidor
                            if (Integer.parseInt(next.getPrcVlr()) == 0) {
                                bloqueado.add(createTransferObject(next, null, responsavel));
                            } else {
                                alterado.add(createTransferObject(next, null, responsavel));
                            }
                        }
                    }

                    // Grava Log de atualização, se necessário
                    if (dadosAlterados) {
                        final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CSA_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.setTipoParamCsa(next.getTpaCodigo());
                        log.setRegistroServidor(next.getRseCodigo());
                        log.setConsignataria(next.getCsaCodigo());
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atualizando.bloqueio.consignataria", responsavel));
                        log.write();
                    }
                }
            }

            final String orsObs = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("orsObs")) ? (String) tmoObject.getAttribute("orsObs") : "";
            final String tmoCodigo = (tmoObject != null) && !TextHelper.isNull(tmoObject.getAttribute("tmoCodigo")) ? (String) tmoObject.getAttribute("tmoCodigo") : "";
            // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
            if (!TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel) && orsObs.isEmpty()) {
                throw new ParametroControllerException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
            }

            if (!bloqueado.isEmpty()) {
                final String observacaoOrs = retornaObservacaoParamOcorrenciaRse(bloqueado, Columns.CSA_IDENTIFICADOR, Columns.PRC_VLR, orsObs, "mensagem.ocorrencia.ors.obs.bloqueio.consignataria.unificado", responsavel);
                servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_CONSIGNATARIA, observacaoOrs, tmoCodigo, responsavel);
            }

            // Envia email com as alterações para o servidor, se necessário
            if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_BLOQ_DESBLOQ_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                EnviaEmailHelper.enviarEmailBloqDesbloqServidorCsa(rseCodigo, bloqueado, desbloqueado, alterado, responsavel);
            }

        } catch (com.zetra.econsig.exception.CreateException | RemoveException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    /**
     * Cria o TransfefrObject que será repassado à rotina de envio de emails
     *
     * @param paramSvcRseBean
     * @return
     */
    private TransferObject createTransferObject(ParamCsaRseTO paramCsaRseTO, String prcVlrOld, AcessoSistema responsavel) {
        final TransferObject to = new CustomTransferObject();
        try {
            final TransferObject consignataria = consignatariaController.findConsignataria(paramCsaRseTO.getCsaCodigo(), responsavel);
            to.setAttribute(Columns.CSA_NOME, consignataria.getAttribute(Columns.CSA_NOME));
            to.setAttribute(Columns.CSA_IDENTIFICADOR, consignataria.getAttribute(Columns.CSA_IDENTIFICADOR));
        } catch (final ConsignatariaControllerException e) {
            LOG.error(e.getMessage(), e);
        }
        to.setAttribute(Columns.PRC_CSA_CODIGO, paramCsaRseTO.getCsaCodigo());
        to.setAttribute(Columns.PRC_DATA_CADASTRO, paramCsaRseTO.getPrcDataCadastro());
        to.setAttribute(Columns.PRC_OBS, paramCsaRseTO.getPrcObs());
        to.setAttribute(Columns.PRC_RSE_CODIGO, paramCsaRseTO.getRseCodigo());
        to.setAttribute(Columns.PRC_TPA_CODIGO, paramCsaRseTO.getTpaCodigo());

        to.setAttribute(Columns.PRC_VLR, paramCsaRseTO.getPrcVlr());
        to.setAttribute(Columns.PRC_VLR + "_OLD", prcVlrOld);
        return to;
    }

    @Override
    public void copiaBloqueioCsa(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamCsaRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamCsaRegistroServidorDAO();
            dao.copiaBloqueioCsa(rseCodNovo, rseCodAnt);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_CSA_REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodNovo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.copiando.bloqueio.consignataria.antigo.para.novo", responsavel));
            log.write();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean temServidorBloqueadoCsa(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ContaServidorBloqueadoNseQuery query = new ContaServidorBloqueadoNseQuery();
            return (query.executarContador() > 0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** FIM BLOQUEIO DE CONSIGNATÁRIA POR REGISTRO SERVIDOR **/

    /**
     * Determina se a senha de servidor é obrigatória ou não, de acordo com os parâmetros
     * de serviço TPS_SER_SENHA_OBRIGATORIA_CSE e TPS_SER_SENHA_OBRIGATORIA_CSE.
     * Se o usuário for CSA/COR, verifica também o parâmetro de SVC/CSA, cadastrado
     * para a consignatária informada por parâmetro.
     *
     * @param rseCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean senhaServidorObrigatoriaReserva(String rseCodigo, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        boolean senhaObrigatoria = true;
        boolean verificaProxNivelParam = true;
        // Determina o Tipo do parâmetro a ser verificado
        String tpsCodigo = CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA;

        if (responsavel.isSer()) {
            // Se é usuário servidor, então a senha não é obrigatória
            tpsCodigo = CodedValues.TPS_SER_SENHA_OBRIGATORIA_SER;
        } else if (responsavel.isCseSupOrg()) {
            // Se CSE/ORG busca parâmetro específico, a nível de SVC apenas
            tpsCodigo = CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE;
        } else if (responsavel.isCsaCor()) {
            tpsCodigo = CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA;

            if (CanalEnum.SOAP.equals(responsavel.getCanal())) {
                try {
                    final CustomTransferObject paramCsaTO = new CustomTransferObject();
                    paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, csaCodigo);
                    if (responsavel.isCsa()) {
                        paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, CodedValues.TPA_SENHA_SER_RESERVAR_MARGEM_HOST_A_HOST_CSA);
                    } else {
                        paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, CodedValues.TPA_SENHA_SER_RESERVAR_MARGEM_HOST_A_HOST_COR);
                    }
                    final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                    // Se o parâmetro existe, retorna seja positivo ou negativo
                    if ((paramCsaBean != null) && (paramCsaBean.getPcsVlr() != null)) {
                        senhaObrigatoria = (!"N".equalsIgnoreCase(paramCsaBean.getPcsVlr()));
                        // Não precisa mais procurar outro nível de configuração, seja SVC/CSA ou SVC
                        verificaProxNivelParam = false;
                    }
                } catch (final ParametroControllerException ex) {
                    // Se o parâmetro não existe então não sobrepõe o de serviço e sistema
                    senhaObrigatoria = true;
                }
            } else if (!TextHelper.isNull(responsavel.getFunCodigo()) && CodedValues.FUN_INCLUSAO_VIA_LOTE.equals(responsavel.getFunCodigo())) {
                try {
                    /*
                     * Se não há um parâmetro de serviço de CSA para a CSA do serviço em questão, exigir ou não a senha conforme o parâmetro de serviço de CSE.
                     * Se há um parâmetro de serviço de CSA para a CSA do serviço em questão, exigir ou não a senha conforme o parâmetro de serviço de CSA.
                     * Se não há um parâmetro de serviço de CSA e nem para CSE, exigir ou não a senha conforme o parâmetro de "Serviço (012) - Senha do servidor obrigatória para a consignatária na reserva de margem".
                     * Nos casos em que se exige senha, a senha exigida deve ser conforme o parâmetro de "Sistema (268) - Utiliza senha de autorização de desconto", ou seja, se ele estiver habilitado, exigir o token, caso contrário, exigir a senha do portal.
                     */

                    // Busca o parâmetro de SVC/CSA
                    final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE);
                    senhaObrigatoria = ((psc.getPscVlr() == null) || !"N".equals(psc.getPscVlr()));
                    // Se existe o parâmetro para SVC/CSA, então não verifica o parâmetro geral de serviço
                    verificaProxNivelParam = false;
                } catch (final FindException ex) {
                    // Não existe o parâmetro para SVC/CSA, então verifica para o serviço
                    try {
                        final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                        senhaObrigatoria = ((pse.getPseVlr() == null) || !CodedValues.PSE_SER_SENHA_OPCIONAL.equals(pse.getPseVlr()));
                        verificaProxNivelParam = false;
                    } catch (final FindException e) {
                        verificaProxNivelParam = true;
                    }
                }
            }

            if (verificaProxNivelParam) {
                try {
                    // Busca o parâmetro de SVC/CSA
                    final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, tpsCodigo);
                    senhaObrigatoria = ((psc.getPscVlr() == null) || !"N".equals(psc.getPscVlr()));
                    // Se existe o parâmetro para SVC/CSA, então não verifica o parâmetro geral de serviço
                    verificaProxNivelParam = false;
                } catch (final FindException ex) {
                    // Não existe o parâmetro para SVC/CSA, então verifica o geral
                    senhaObrigatoria = true;
                }
            }
        }
        if (verificaProxNivelParam) {
            try {
                final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(tpsCodigo, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                senhaObrigatoria = ((pse.getPseVlr() == null) || !CodedValues.PSE_SER_SENHA_OPCIONAL.equals(pse.getPseVlr()));
            } catch (final FindException ex) {
                // Não existe o parâmetro para serviço, então retorna o valor padrão
                if (responsavel.isSer()) {
                    // Para servidor, o padrão é não exigir senha
                    senhaObrigatoria = false;
                } else {
                    senhaObrigatoria = true;
                }
            }
        }
        if (senhaObrigatoria && !TextHelper.isNull(rseCodigo)) {
            ParamSvcConsignante pse = null;
            RegistroServidor rse = null;
            if (responsavel.isCsaCor()) {
                // Se a senha é obrigatória, verifica se o parâmetro de serviço diz que é opcional caso
                // a consignatária possua consignações ativas com o servidor
                try {
                    pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_SER_SENHA_OPCIONAL_RESERVA_MARGEM_TEM_ADE, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                    if ((pse != null) && (pse.getPseVlr() != null) && "1".equals(pse.getPseVlr())) {
                        // Verifica se a consignatária possui consignações ativas com o servidor
                        try {
                            final ObtemTotalConsignacaoPorStatusQuery query = new ObtemTotalConsignacaoPorStatusQuery();
                            query.rseCodigo = rseCodigo;
                            query.csaCodigo = responsavel.getCsaCodigo();
                            query.corCodigo = responsavel.getCorCodigo();
                            query.sadCodigos = Arrays.asList(CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_AGUARD_DEFER, CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_SUSPENSA, CodedValues.SAD_SUSPENSA_CSE, CodedValues.SAD_AGUARD_LIQUIDACAO, CodedValues.SAD_AGUARD_LIQUI_COMPRA, CodedValues.SAD_ESTOQUE, CodedValues.SAD_ESTOQUE_MENSAL);
                            senhaObrigatoria = (query.executarContador() == 0);
                        } catch (final HQueryException ex) {
                            LOG.error(ex.getMessage(), ex);
                            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
                        }
                    }
                } catch (final FindException ex) {
                    // Não existe o parâmetro, então ignora o erro e retorna o valor já determinado do parâmetro
                }
            }
            // DESENV-17057: Permitir inclusão de consignação para servidores bloqueados sem senha é preciso que o parâmetro de serviço permita inclusão de servidores bloqueados e o sistema exija senha
            // só então com estas condições devemos verificar os parâmetros de serviços que permite inclusão com senha a nivel de SVC/CSA
            try {
                rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            } catch (final FindException e) {
                // Não existe o parâmetro, então ignora o erro e não terá a validação da senha para servidor bloqueado.
            }

            if (senhaObrigatoria && (rse != null) && (rse.getSrsCodigo() != null) && CodedValues.SRS_BLOQUEADO.equals(rse.getSrsCodigo()) && (pse != null) && (pse.getPseVlr() != null) && CodedValues.PSE_BOOLEANO_SIM.equals(pse.getPseVlr())) {
                try {
                    final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA);
                    senhaObrigatoria = ((psc.getPscVlr() != null) && CodedValues.PSE_BOOLEANO_NAO.equals(psc.getPscVlr()));
                } catch (final FindException ex) {
                    // Não existe o parâmetro para CSA
                    senhaObrigatoria = true;
                }
            }
        }

        return verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, senhaObrigatoria, null, responsavel);
    }

    /**
     * Determina se a senha de servidor é obrigatória para confirmação de solicitação, de
     * acordo com os parâmetro de serviço TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO.
     *
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean senhaServidorObrigatoriaConfSolicitacao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            return ((pse.getPseVlr() != null) && "1".equals(pse.getPseVlr().trim()));
        } catch (final FindException ex) {
            // Não existe o parâmetro para serviço, então retorna o valor padrão
            return false;
        }
    }

    /**
     * Determina se a senha de servidor é obrigatória para cancelar renegociação, de
     * acordo com os parâmetro de serviço TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO e parâmetros
     * de consignatária TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_CSA e TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_COR.
     *
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean senhaServidorObrigatoriaCancelarReneg(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        boolean exigeSenha = false;

        try {
            final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            exigeSenha = ((pse.getPseVlr() != null) && "1".equals(pse.getPseVlr().trim()));
        } catch (final FindException ex) {
            // Não existe o parâmetro para serviço, então usa o valor padrão
            exigeSenha = false;
        }

        if (responsavel.isCsaCor() && CanalEnum.SOAP.equals(responsavel.getCanal())) {
            final String tpaCodigo = (responsavel.isCsa() ? CodedValues.TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_CSA : CodedValues.TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_COR);

            // Verifica se o parâmetro de consignatária existe para sobrepor o de serviço
            try {
                final CustomTransferObject paramCsaTO = new CustomTransferObject();
                paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, responsavel.getCsaCodigo());
                paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, tpaCodigo);
                final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                if ((paramCsaBean != null) && !TextHelper.isNull(paramCsaBean.getPcsVlr())) {
                    exigeSenha = ("S".equalsIgnoreCase(paramCsaBean.getPcsVlr()));
                }
            } catch (final ParametroControllerException ex) {
                // Se o parâmetro não existe então não sobrepõe o de serviço
            }
        }

        return exigeSenha;
    }

    /**
     * Determina se a senha do servidor é obrigatória para a consulta de margem.
     * Verifica os parâmetros de sistema TPC_SENHA_SER_ACESSAR_CONS_MARGEM_CSE e
     * TPC_SENHA_SER_ACESSAR_CONS_MARGEM, e caso seja CSA/COR verifica o parâmetro
     * de consignatária TPA_SENHA_SER_CONSULTAR_MARGEM
     * @param rseCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean senhaServidorObrigatoriaConsultaMargem(String rseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        boolean exigeSenha = false;

        if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_ACESSAR_CONS_MARGEM_CSE, CodedValues.TPC_SIM, responsavel)) {
            exigeSenha = true;
        } else if (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_ACESSAR_CONS_MARGEM, CodedValues.TPC_SIM, responsavel)) {
            final String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
            String tpaCodigo = CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM;
            if (CanalEnum.SOAP.equals(responsavel.getCanal())) {
                if (responsavel.isCsa()) {
                    tpaCodigo = CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_CSA;
                } else {
                    tpaCodigo = CodedValues.TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_COR;
                }
            }
            // Se o parâmetro de sistema está igual a SIM, verifica o parâmetro
            // de consignatária que sobrepõe este parâmetro
            try {
                final CustomTransferObject paramCsaTO = new CustomTransferObject();
                paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, csaCodigo);
                paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, tpaCodigo);
                final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                if (((paramCsaBean == null) || (paramCsaBean.getPcsVlr() == null) || !"N".equalsIgnoreCase(paramCsaBean.getPcsVlr()))) {
                    exigeSenha = true;
                }
            } catch (final ParametroControllerException ex) {
                // Se o parâmetro não existe então não sobrepõe o de sistema
                exigeSenha = true;
            }
            // Se a senha é obrigatória, e o parâmetro de sistema informa que é opcional
            // para servidores que possuam consignação ativa com a CSA/COR, então verifica
            // se há contratos ativos
            if (exigeSenha && !TextHelper.isNull(rseCodigo) && ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OPCIONAL_CONS_MARGEM_TEM_ADE, CodedValues.TPC_SIM, responsavel)) {
                try {
                    final ObtemTotalConsignacaoPorStatusQuery query = new ObtemTotalConsignacaoPorStatusQuery();
                    query.rseCodigo = rseCodigo;
                    query.csaCodigo = responsavel.getCsaCodigo();
                    query.corCodigo = responsavel.getCorCodigo();
                    query.sadCodigos = Arrays.asList(CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_AGUARD_DEFER, CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_SUSPENSA, CodedValues.SAD_SUSPENSA_CSE, CodedValues.SAD_AGUARD_LIQUIDACAO, CodedValues.SAD_AGUARD_LIQUI_COMPRA, CodedValues.SAD_ESTOQUE, CodedValues.SAD_ESTOQUE_MENSAL);
                    exigeSenha = (query.executarContador() == 0);
                } catch (final HQueryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        }

        return verificaAutorizacaoReservaSemSenha(rseCodigo, null, exigeSenha, null, responsavel);
    }

    @Override
    public InformacaoSerCompraEnum senhaServidorObrigatoriaCompra(AcessoSistema responsavel) throws ParametroControllerException {
        return senhaServidorObrigatoriaCompra(null, null, responsavel);
    }

    /**
     * Determina se a senha do servidor ou conta bancária é obrigatória para listar
     * os contratos de outras consignatárias na compra de contratos.
     * Verifica o parâmetro de sistema TPC_SENHA_SER_ACESSAR_CONT_CSAS e
     * e caso seja CSA/COR verifica o parâmetro de consignatária
     * TPA_SENHA_SER_ACESSAR_CONT_CSAS ou TPA_SENHA_SER_ACESSAR_CONT_CSAS_HOST_A_HOST
     * caso o acesso seja via host a host.
     *
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public InformacaoSerCompraEnum senhaServidorObrigatoriaCompra(String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        boolean exigeSenha = !ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_ACESSAR_CONT_CSAS, CodedValues.TPC_NAO, responsavel);
        boolean exigeContaBancaria = !exigeSenha;

        if (responsavel.isCsaCor()) {
            final String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
            String tpaCodigo = CodedValues.TPA_SENHA_SER_ACESSAR_CONT_CSAS;
            if (CanalEnum.SOAP.equals(responsavel.getCanal())) {
                tpaCodigo = CodedValues.TPA_SENHA_SER_ACESSAR_CONT_CSAS_HOST_A_HOST;
            }
            // Verifica o parâmetro de consignatária que sobrepõe o de sistema
            try {
                final CustomTransferObject paramCsaTO = new CustomTransferObject();
                paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, csaCodigo);
                paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, tpaCodigo);
                final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                if ((paramCsaBean != null) && (paramCsaBean.getPcsVlr() != null)) {
                    if (InformacaoSerCompraEnum.SENHA.getCodigo().equals(paramCsaBean.getPcsVlr())) {
                        exigeSenha = true;
                        exigeContaBancaria = false;
                    } else if (InformacaoSerCompraEnum.CONTA_BANCARIA.getCodigo().equals(paramCsaBean.getPcsVlr())) {
                        exigeSenha = false;
                        exigeContaBancaria = true;
                    } else if (InformacaoSerCompraEnum.NADA.getCodigo().equals(paramCsaBean.getPcsVlr())) {
                        exigeSenha = false;
                        exigeContaBancaria = false;
                    }
                }
            } catch (final ParametroControllerException ex) {
                // Se o parâmetro não existe então não sobrepõe o de sistema
            }
        }

        if ((exigeSenha || exigeContaBancaria) && !TextHelper.isNull(svcCodigo) && !TextHelper.isNull(rseCodigo)
                && !verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, true, null, responsavel)) {
            return InformacaoSerCompraEnum.NADA;
        }

        return (exigeSenha ? InformacaoSerCompraEnum.SENHA : (exigeContaBancaria ? InformacaoSerCompraEnum.CONTA_BANCARIA : InformacaoSerCompraEnum.NADA));
    }

    /**
     * Determina se a matrícula e o CPF do servidor são obrigatórios para as operações
     * que pesquisam servidores, como consulta de margem, reserva, renegociação, etc.
     * Verifica os parâmetros de sistema TPC_REQUER_MATRICULA_E_CPF_CSE e
     * TPC_REQUER_MATRICULA_E_CPF_CSA, e caso seja CSA/COR verifica o parâmetro
     * de consignatária TPC_REQUER_MATRICULA_E_CPF
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean requerMatriculaCpf(AcessoSistema responsavel) throws ParametroControllerException {
        return requerMatriculaCpf(false, responsavel);
    }

    /**
     * Determina se a matrícula e o CPF do servidor são obrigatórios para as operações
     * que pesquisam servidores, como consulta de margem, reserva, renegociação, etc.
     * Verifica os parâmetros de sistema TPC_REQUER_MATRICULA_E_CPF_CSE e
     * TPC_REQUER_MATRICULA_E_CPF_CSA, e caso seja CSA/COR verifica o parâmetro
     * de consignatária TPC_REQUER_MATRICULA_E_CPF
     *
     * @param lote        indica se é um processamento de lote
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean requerMatriculaCpf(boolean lote, AcessoSistema responsavel) throws ParametroControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, CodedValues.TPC_NAO, responsavel) || ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            return false;
        }

        boolean requerMatriculaCpf = false;

        if (responsavel.isSer()) {
            requerMatriculaCpf = false;
        } else if (responsavel.isCseSupOrg()) {
            requerMatriculaCpf = ParamSist.paramEquals(CodedValues.TPC_REQUER_MATRICULA_E_CPF_CSE, CodedValues.TPC_SIM, responsavel);
        } else if (responsavel.isCsaCor()) {
            if (lote) {
                requerMatriculaCpf = ParamSist.paramEquals(CodedValues.TPC_REQUER_MATRICULA_E_CPF_CSA_LOTE, CodedValues.TPC_SIM, responsavel);
            } else {
                requerMatriculaCpf = ParamSist.paramEquals(CodedValues.TPC_REQUER_MATRICULA_E_CPF_CSA, CodedValues.TPC_SIM, responsavel);
            }

            if (requerMatriculaCpf) {
                // Verifica se existe parâmetro de consignatária que sobreponha o de sistema
                try {
                    final CustomTransferObject paramCsaTO = new CustomTransferObject();
                    paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, responsavel.getCsaCodigo());
                    paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, CodedValues.TPA_REQUER_MATRICULA_E_CPF);
                    final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                    if ((paramCsaBean != null) && !TextHelper.isNull(paramCsaBean.getPcsVlr())) {
                        requerMatriculaCpf = "S".equalsIgnoreCase(paramCsaBean.getPcsVlr());
                    }
                } catch (final ParametroControllerException ex) {
                    // Se o parâmetro não existe então não sobrepõe o de sistema
                }
            }

            if (requerMatriculaCpf && CanalEnum.SOAP.equals(responsavel.getCanal())) {
                // Verifica se existe parâmetro de consignatária que sobreponha o de sistema para acesso Host-Host
                try {
                    final CustomTransferObject paramCsaTO = new CustomTransferObject();
                    paramCsaTO.setAttribute(Columns.PCS_CSA_CODIGO, responsavel.getCsaCodigo());
                    paramCsaTO.setAttribute(Columns.PCS_TPA_CODIGO, CodedValues.TPA_REQUER_MATRICULA_E_CPF_HOST_A_HOST);
                    final ParamConsignataria paramCsaBean = findParamCsaBean(paramCsaTO);
                    if ((paramCsaBean != null) && !TextHelper.isNull(paramCsaBean.getPcsVlr())) {
                        requerMatriculaCpf = "S".equalsIgnoreCase(paramCsaBean.getPcsVlr());
                    }
                } catch (final ParametroControllerException ex) {
                    // Se o parâmetro não existe então não sobrepõe o de sistema
                }
            }
        }

        return requerMatriculaCpf;
    }


    @Override
    public boolean requerDataNascimento(AcessoSistema responsavel) throws ParametroControllerException {
        return responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_REQUER_DATA_NASC_CONS_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel);
    }

    /**
     * Verifica parâmetro de serviço por consignatária para determinar se permite
     * contratos com valor negativo
     *
     * @param csaCodigo
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean permiteContratoValorNegativo(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(svcCodigo)) {
                // Busca o parâmetro de SVC/CSA
                final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO);
                return ((psc.getPscVlr() != null) && "S".equals(psc.getPscVlr()));
            }
        } catch (final FindException ex) {
            // Não existe o parâmetro para SVC/CSA, Default: false
        }
        return false;
    }

    @Override
    public List<MargemTO> lstMargensIncidentes(String svcCodigo, String csaCodigo, String orgCodigo, String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaMargemIncidenteQuery margensQuery = new ListaMargemIncidenteQuery();
            margensQuery.csaCodigo = csaCodigo;
            margensQuery.orgCodigo = orgCodigo;
            margensQuery.rseCodigo = rseCodigo;
            margensQuery.svcCodigo = svcCodigo;
            margensQuery.marCodigo = marCodigo;

            return margensQuery.executarDTO(MargemTO.class);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Short getSvcIncMargem(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            Short incideMargem = CodedValues.INCIDE_MARGEM_SIM;
            if (svcCodigo != null) {
                TransferObject param = null;
                final ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
                query.svcCodigo = svcCodigo;
                query.tpsCodigo = CodedValues.TPS_INCIDE_MARGEM;
                final List<TransferObject> lstParamSvcCse = query.executarDTO();
                if ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0)) {
                    param = lstParamSvcCse.get(0);
                }
                if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null) && !"".equals(param.getAttribute(Columns.PSE_VLR).toString())) {
                    incideMargem = Short.valueOf(param.getAttribute(Columns.PSE_VLR).toString());
                }
            }
            return incideMargem;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void inserirRelacionamento(String tntCodigo, String svcCodigo, List<String> svcDestino, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Lista os relacionamentos que serão deletados
            final ListaRelacionamentosServicoQuery query = new ListaRelacionamentosServicoQuery();
            svcDestino.add(CodedValues.NOT_EQUAL_KEY);
            query.svcCodigoDestino = svcDestino;
            query.svcCodigoOrigem = svcCodigo;
            query.tntCodigo = tntCodigo;
            final List<TransferObject> relSvcADeletar = query.executarDTO();

            // Lista os relacionamentos novos que serão criados
            svcDestino.remove(CodedValues.NOT_EQUAL_KEY);
            query.svcCodigoDestino = svcDestino;
            query.svcCodigoOrigem = svcCodigo;
            query.tntCodigo = tntCodigo;
            final List<TransferObject> svcJaPresentes = query.executarDTO();

            final List<String> svcCodigosInclusao = new ArrayList<>(svcDestino);
            for (final TransferObject relServico : svcJaPresentes) {
                if (svcDestino.contains(relServico.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO))) {
                    svcCodigosInclusao.remove(relServico.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO));
                }
            }

            // Realiza a exclusão e gera logs dos rel. de serviço que foram deletados
            if (!relSvcADeletar.isEmpty()) {
                for (final TransferObject relServico : relSvcADeletar) {
                    final RelacionamentoServico rsv = new RelacionamentoServico();
                    rsv.setRelSvcCodigo(relServico.getAttribute(Columns.RSV_CODIGO).toString());
                    AbstractEntityHome.remove(rsv);

                    final LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_SERVICO, Log.DELETE, Log.LOG_INFORMACAO);
                    log.setServicoOrigem(svcCodigo);
                    log.setServicoDestino(relServico.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString());
                    log.setTipoNatureza(tntCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.exclusao.relacionamento.servico", responsavel));
                    log.write();
                }
            }

            // Realiza a inclusão e gera logs dos rel. de serviço que foram inseridos
            if (!svcCodigosInclusao.isEmpty()) {
                for (final String svcCodigoDestino : svcCodigosInclusao) {
                    RelacionamentoServicoHome.create(svcCodigo, svcCodigoDestino, tntCodigo);

                    final LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_SERVICO, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setServicoOrigem(svcCodigo);
                    log.setServicoDestino(svcCodigoDestino);
                    log.setTipoNatureza(tntCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.inclusao.relacionamento.servico", responsavel));
                    log.write();
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRelacionamento(String tntCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaServicosRelacionadosQuery query = new ListaServicosRelacionadosQuery();
            query.tntCodigo = tntCodigo;
            query.svcCodigoOrigem = svcCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    /**
     * Retorna a lista de CustomTransferObject contendo os códigos de serviços relacionados.
     *
     * @param tntCodigo
     * @param svcCodigoOrigem
     * @param svcCodigoDestino
     * @param responsavel
     * @return
     * @throws HQueryException
     */
    @Override
    public List<TransferObject> getRelacionamentoSvc(String tntCodigo, String svcCodigoOrigem, String svcCodigoDestino, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaRelacionamentosQuery query = new ListaRelacionamentosQuery();
            query.tntCodigo = tntCodigo;
            query.svcCodigoOrigem = svcCodigoOrigem;
            query.svcCodigoDestino = svcCodigoDestino;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> filtrarServicosSemRelacionamentoAlongamento(List<TransferObject> servicos, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                // Se tem alongamento, então recupera os relacionamentos de serviço de alongamento
                final ListaRelacionamentosQuery query = new ListaRelacionamentosQuery();
                query.tntCodigo = CodedValues.TNT_ALONGAMENTO;
                final List<TransferObject> lstRelacionamentoAlongamento = query.executarDTO();
                // Se existe algum relacionamento de serviço de alongamento
                if ((lstRelacionamentoAlongamento != null) && !lstRelacionamentoAlongamento.isEmpty()) {
                    // Recupera o conjunto de códigos de serviço de alongamento distintos
                    final Set<String> svcCodigosAlongamento = lstRelacionamentoAlongamento.stream().map(to -> to.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString()).distinct().collect(Collectors.toSet());
                    // Itera sobre a relação original excluindo os serviços que são de origem de alongamento
                    final List<TransferObject> servicosSemAlongamento = new ArrayList<>();
                    for (final TransferObject servico : servicos) {
                        final String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                        if (!svcCodigosAlongamento.contains(svcCodigo)) {
                            servicosSemAlongamento.add(servico);
                        }
                    }
                    return servicosSemAlongamento;
                }
            }
            return servicos;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Seleciona parametros relacionados a consignataria, como o parametro de
     * exibir margem, utilizado na pagina edt_param_consignataria.jsp
     *
     * @param csaCodigo
     * @param tpaCodigo
     * @param tpaCseAltera
     * @param tpaCsaAltera
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCodigo, String tpaCseAltera, String tpaCsaAltera, String tpaSupAltera, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamCsaQuery query = new ListaParamCsaQuery();
            query.csaCodigo = csaCodigo;
            query.tpaCodigo = tpaCodigo;
            query.tpaCseAltera = tpaCseAltera;
            query.tpaCsaAltera = tpaCsaAltera;
            query.tpaSupAltera = tpaSupAltera;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCseAltera, String tpaCsaAltera, String tpaSupAltera, AcessoSistema responsavel) throws ParametroControllerException {
        return selectParamCsa(csaCodigo, null, tpaCseAltera, tpaCsaAltera, tpaSupAltera, responsavel);
    }

    @Override
    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return selectParamCsa(csaCodigo, tpaCodigo, null, null, null, responsavel);
    }

    @Override
    public List<TransferObject> selectParamCsaNaoNulo(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamCsaQuery query = new ListaParamCsaQuery();
            query.naoNulo = true;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Obtém o valor de um parâmetro de consignatária
     *
     * @param csaCodigo
     * @param tpaCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public String getParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        // Retorna do cache de parâmetros
        return ParamCsa.getParamCsa(csaCodigo, tpaCodigo, responsavel);
        /*
        try {
            final ParamConsignataria paramConsignataria = ParamConsignatariaHome.findParamCsa(tpaCodigo, csaCodigo);
            return paramConsignataria.getPcsVlr();
        } catch (final FindException ex) {
            // Parâmetro não existe, retorna null
            return null;
        }
        */
    }

    @Override
    public void updateParamCsa(CustomTransferObject cto, AcessoSistema responsavel) throws ParametroControllerException {
        ParamConsignataria paramCsaBean = null;
        try {
            paramCsaBean = findParamCsaBean(cto);
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PARAM_SIST_CSA, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setConsignataria(cto.getAttribute(Columns.PCS_CSA_CODIGO).toString());
            logDelegate.setTipoParamSistCsa(cto.getAttribute(Columns.PCS_TPA_CODIGO).toString());
            logDelegate.getUpdatedFields(cto.getAtributos(), null);
            logDelegate.write();
            // Compara a versão do cache com a passada por parâmetro
            if ((cto.getAttribute(Columns.PCS_VLR) != null) && (paramCsaBean != null) && ((paramCsaBean.getPcsVlr() != null) && !cto.getAttribute(Columns.PCS_VLR).equals(paramCsaBean.getPcsVlr()))) {
                paramCsaBean.setPcsVlr((String) cto.getAttribute(Columns.PCS_VLR));
            }

            AbstractEntityHome.update(paramCsaBean);

        } catch (final ParametroControllerException ex) {
            LOG.debug(ex.getMessage());
            // Se o parâmetro não existir, então cria um novo
            if ((ex.getCause() != null) && ex.getCause().getClass().equals(FindException.class)) {
                try {
                    paramCsaBean = createParamCsa(cto, responsavel);
                } catch (final ParametroControllerException ex1) {
                    LOG.debug("erro ao tentar criar: " + ex1.getMessage());
                    throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex1);
                }
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        if (paramCsaBean != null) {
            // Atualiza o cache de parâmetros
            ParamCsa.setParamCsa(paramCsaBean.getCsaCodigo(), paramCsaBean.getTpaCodigo(), paramCsaBean.getPcsVlr(), responsavel);
        }
    }

    private ParamConsignataria findParamCsaBean(CustomTransferObject param) throws ParametroControllerException {
        ParamConsignataria paramBean = null;
        if ((param.getAttribute(Columns.PCS_TPA_CODIGO) != null) && (param.getAttribute(Columns.PCS_CSA_CODIGO) != null)) {
            try {
                paramBean = ParamConsignatariaHome.findParamCsa(param.getAttribute(Columns.PCS_TPA_CODIGO).toString(), param.getAttribute(Columns.PCS_CSA_CODIGO).toString());
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramBean;
    }

    private ParamConsignataria createParamCsa(CustomTransferObject paramSistCse, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParamConsignataria paramCsaBean = ParamConsignatariaHome.create(paramSistCse.getAttribute(Columns.PCS_TPA_CODIGO).toString(), paramSistCse.getAttribute(Columns.PCS_CSA_CODIGO).toString(), paramSistCse.getAttribute(Columns.PCS_VLR).toString());

            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SIST_CSA, Log.CREATE, Log.LOG_INFORMACAO);
            log.setConsignataria(paramSistCse.getAttribute(Columns.PCS_CSA_CODIGO).toString());
            log.setTipoParamSistCsa(paramSistCse.getAttribute(Columns.PCS_TPA_CODIGO).toString());
            log.getUpdatedFields(paramSistCse.getAtributos(), null);
            log.write();

            return paramCsaBean;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.criar.parametros.deste.servico.erro.interno", responsavel, ex.getMessage());
        }
    }

    @Override
    public List<TransferObject> lstRelacionamentoSvcCorrecao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaRelacionamentoSvcCorrecaoQuery query = new ListaRelacionamentoSvcCorrecaoQuery();
            query.svcCodigoOrigem = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public boolean hasValidacaoDataNasc(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Se houver pelo menos 1 serviço com validação de data de nascimento,
            // então deve-se bloquear a visualização da data de nascimento no sistema
            final ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
            query.pseVlr = "1";
            query.tpsCodigo = CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA;
            final List<TransferObject> lstParamSvcCse = query.executarDTO();
            return ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0));
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Correção de Saldo
    @Override
    public CustomTransferObject getServicoCorrecao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaServicoRelacionamentoSvcQuery query = new ListaServicoRelacionamentoSvcQuery();
            query.tntCodigo = CodedValues.TNT_CORRECAO_SALDO;
            query.svcCodigoOrigem = svcCodigo;
            final List<TransferObject> params = query.executarDTO();

            if ((params != null) && (params.size() > 0)) {
                return (CustomTransferObject) params.get(0);
            } else {
                return null;
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void copiaParamSvc(String svcCodOrigem, String svcCodDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Somente devem ser copiados os parâmetros caso os serviços sejam distintos
            if (svcCodOrigem.equals(svcCodDestino)) {
                return;
            }
            /* Cópia do grupo de serviço, prioridade de desconto e parâmetro de serviço ativo */
            final Servico svcDest = ServicoHome.findByPrimaryKey(svcCodDestino);
            final Servico svcOrig = ServicoHome.findByPrimaryKey(svcCodOrigem);
            svcDest.setSvcAtivo(svcOrig.getSvcAtivo());
            svcDest.setSvcPrioridade(svcOrig.getSvcPrioridade());
            if (svcOrig.getTipoGrupoSvc() != null) {
                svcDest.setTipoGrupoSvc(svcOrig.getTipoGrupoSvc());
            }

            /* Cópia dos parâmetros de tarifação */
            // Caso existam, deleta os parâmetros de tarifação antigos do servidor destino
            List<ParamTarifConsignante> lstParTarifCse;
            try {
                lstParTarifCse = ParamTarifConsignanteHome.findByServico(svcCodDestino);
            } catch (final FindException e) {
                LOG.debug("Não existem parâmetros antigos de tarifação para serem excluídos.");
                lstParTarifCse = new ArrayList<>();
            }
            for (final ParamTarifConsignante parTarifCseBean : lstParTarifCse) {
                AbstractEntityHome.remove(parTarifCseBean);
            }
            final ParamTarifCseTO paramTarCseTO = new ParamTarifCseTO();
            List<TransferObject> param = lstParamTarifCse(svcOrig.getSvcCodigo(), responsavel);

            for (final TransferObject cTO : param) {
                if (cTO.getAttribute(Columns.PCV_CODIGO) != null) {
                    if (cTO.getAttribute(Columns.PCV_CSE_CODIGO) != null) {
                        paramTarCseTO.setCseCodigo(cTO.getAttribute(Columns.PCV_CSE_CODIGO).toString());
                    }
                    if (cTO.getAttribute(Columns.PCV_ATIVO) != null) {
                        paramTarCseTO.setPcvAtivo(Short.valueOf(cTO.getAttribute(Columns.PCV_ATIVO).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_BASE_CALC) != null) {
                        paramTarCseTO.setPcvBaseCalc(Integer.valueOf(cTO.getAttribute(Columns.PCV_BASE_CALC).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_DATA_FIM_VIG) != null) {
                        paramTarCseTO.setPcvDataFimVig(DateHelper.parse(cTO.getAttribute(Columns.PCV_DATA_FIM_VIG).toString(), "yyyy-MM-dd"));
                    }
                    if (cTO.getAttribute(Columns.PCV_DATA_INI_VIG) != null) {
                        paramTarCseTO.setPcvDataIniVig(DateHelper.parse(cTO.getAttribute(Columns.PCV_DATA_INI_VIG).toString(), "yyyy-MM-dd"));
                    }
                    if (cTO.getAttribute(Columns.PCV_DECIMAIS) != null) {
                        paramTarCseTO.setPcvDecimais(Integer.valueOf(cTO.getAttribute(Columns.PCV_DECIMAIS).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_FORMA_CALC) != null) {
                        paramTarCseTO.setPcvFormaCalc(Integer.valueOf(cTO.getAttribute(Columns.PCV_FORMA_CALC).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_VLR) != null) {
                        paramTarCseTO.setPcvVlr(new BigDecimal(cTO.getAttribute(Columns.PCV_VLR).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_VLR_FIM) != null) {
                        paramTarCseTO.setPcvVlrFim(new BigDecimal(cTO.getAttribute(Columns.PCV_VLR_FIM).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_VLR_INI) != null) {
                        paramTarCseTO.setPcvVlrIni(new BigDecimal(cTO.getAttribute(Columns.PCV_VLR_INI).toString()));
                    }
                    if (cTO.getAttribute(Columns.PCV_TPT_CODIGO) != null) {
                        paramTarCseTO.setTptCodigo(cTO.getAttribute(Columns.PCV_TPT_CODIGO).toString());
                    }
                    paramTarCseTO.setSvcCodigo(svcDest.getSvcCodigo());

                    // Cria parâmetro de tarifação com código do novo serviço
                    createParamTarifCse(paramTarCseTO, responsavel);
                }
                batman.iterate();
            }
            param.clear();
            /* Cópia dos parâmetros de serviço de cse*/
            param = lstParamSvcCse(svcOrig.getSvcCodigo(), responsavel);
            for (final TransferObject cTO : param) {
                if (cTO.getAttribute(Columns.PSE_CODIGO) != null) {
                    final ParamSvcCseTO paramSvcCseTO = new ParamSvcCseTO();
                    if (cTO.getAttribute(Columns.PSE_CSE_CODIGO) != null) {
                        paramSvcCseTO.setCseCodigo(cTO.getAttribute(Columns.PSE_CSE_CODIGO).toString());
                    }
                    if (cTO.getAttribute(Columns.PSE_VLR) != null) {
                        paramSvcCseTO.setPseVlr(cTO.getAttribute(Columns.PSE_VLR).toString());
                    }
                    if (cTO.getAttribute(Columns.PSE_VLR_REF) != null) {
                        paramSvcCseTO.setPseVlrRef(cTO.getAttribute(Columns.PSE_VLR_REF).toString());
                    }
                    if (cTO.getAttribute(Columns.PSE_TPS_CODIGO) != null) {
                        paramSvcCseTO.setTpsCodigo(cTO.getAttribute(Columns.PSE_TPS_CODIGO).toString());
                    }
                    paramSvcCseTO.setSvcCodigo(svcDest.getSvcCodigo());
                    // Cria parâmetro de serviço de cse com o código do serviço criado
                    updateParamSvcCse(paramSvcCseTO, responsavel);
                }
                batman.iterate();
            }
            param.clear();

            /* Cópia de relacionamentos */
            // Somente sera copiado os relacionamentos que o tipo de natureza do serviço destino aceite receber.
            final List<TransferObject> tntCodigos = selectTipoNaturezaEditavelServico(svcDest.getNaturezaServico().getNseCodigo(), responsavel);
            for (final TransferObject tipoNatureza : tntCodigos) {
                // Mesmo com o controle de fazer a interação sobre a natureza de serviço que o destio aceite estamos validando novamente a permissão.
                final TipoNatureza natureza = TipoNaturezaHome.findByPrimaryKey(tipoNatureza.getAttribute(Columns.TNT_CODIGO).toString());
                boolean podeAlterar = true;
                if (responsavel.isCse()) {
                    podeAlterar = natureza.getTntCseAltera() != null ? !CodedValues.TPC_NAO.equals(natureza.getTntCseAltera()) : false;
                } else {
                    podeAlterar = natureza.getTntSupAltera() != null ? !CodedValues.TPC_NAO.equals(natureza.getTntSupAltera()) : false;
                }

                // Se o responsavel atual não tem permissão de alteração não vamos copiar o relacionamento
                // passamos para o proximo tipo de natureza
                if (!podeAlterar) {
                    continue;
                }

                final ListaRelacionamentosQuery query = new ListaRelacionamentosQuery();
                query.tntCodigo = tipoNatureza.getAttribute(Columns.TNT_CODIGO).toString();
                query.svcCodigoOrigem = svcOrig.getSvcCodigo();
                param = query.executarDTO();
                final List<String> lstSvcCod = new ArrayList<>();
                for (final TransferObject cTO : param) {
                    lstSvcCod.add(cTO.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString());
                }
                // Insere os relacionamentos do serviço escolhido para o serviço criado
                inserirRelacionamento(natureza.getTntCodigo(), svcDest.getSvcCodigo(), lstSvcCod, responsavel);
                lstSvcCod.clear();
                param.clear();
                batman.iterate();
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.servico.a.ser.copiado.nao.encontrado", responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void copiaParamSvcCsa(String svcCodOrigem, String svcCodDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Somente devem ser copiados os parâmetros caso os serviços sejam distintos
            if (svcCodOrigem.equals(svcCodDestino)) {
                return;
            }

            /* Cópia dos parâmetros de serviço de csa*/
            final List<TransferObject> parametros = new ArrayList<>();
            final List<TransferObject> param = lstParamSvcCsa(svcCodOrigem, responsavel);
            for (final TransferObject cTO : param) {
                if (cTO.getAttribute(Columns.PSC_CODIGO) != null) {
                    final ParamSvcCsaTO paramSvcCsaTO = new ParamSvcCsaTO();
                    if (cTO.getAttribute(Columns.PSC_CSA_CODIGO) != null) {
                        paramSvcCsaTO.setCsaCodigo(cTO.getAttribute(Columns.PSC_CSA_CODIGO).toString());
                    }
                    if (cTO.getAttribute(Columns.PSC_VLR) != null) {
                        paramSvcCsaTO.setPscVlr(cTO.getAttribute(Columns.PSC_VLR).toString());
                    }
                    if (cTO.getAttribute(Columns.PSC_VLR_REF) != null) {
                        paramSvcCsaTO.setPscVlrRef(cTO.getAttribute(Columns.PSC_VLR_REF).toString());
                    }
                    if (cTO.getAttribute(Columns.TPS_CODIGO) != null) {
                        paramSvcCsaTO.setTpsCodigo(cTO.getAttribute(Columns.TPS_CODIGO).toString());
                    }
                    if (cTO.getAttribute(Columns.PSC_DATA_INI_VIG) != null) {
                        paramSvcCsaTO.setPscDataIniVig(cTO.getAttribute(Columns.PSC_DATA_INI_VIG).toString());
                    }
                    if (cTO.getAttribute(Columns.PSC_DATA_FIM_VIG) != null) {
                        paramSvcCsaTO.setPscDataFimVig(cTO.getAttribute(Columns.PSC_DATA_FIM_VIG).toString());
                    }

                    paramSvcCsaTO.setSvcCodigo(svcCodDestino);
                    parametros.add(paramSvcCsaTO);
                }
                batman.iterate();
            }
            if (!parametros.isEmpty()) {
                // Cria parâmetro de serviço de csa com o código do serviço criado
                updateParamSvcCsa(parametros, responsavel);
            }
            param.clear();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void copiaParamSvcRse(String svcCodOrigem, String svcCodDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Somente devem ser copiados os parâmetros caso os serviços sejam distintos
            if (svcCodOrigem.equals(svcCodDestino)) {
                return;
            }

            /* Cópia dos parâmetros de serviço de rse*/
            final List<TransferObject> param = lstParamSvcRse(svcCodOrigem, responsavel);
            for (final TransferObject cTO : param) {
                if (cTO.getAttribute(Columns.PSR_VLR) != null) {
                    final ParamSvcRseTO paramSvcRseTO = new ParamSvcRseTO();
                    if (cTO.getAttribute(Columns.PSR_RSE_CODIGO) != null) {
                        paramSvcRseTO.setRseCodigo(cTO.getAttribute(Columns.PSR_RSE_CODIGO).toString());
                    }
                    if (cTO.getAttribute(Columns.PSR_VLR) != null) {
                        paramSvcRseTO.setPsrVlr(cTO.getAttribute(Columns.PSR_VLR).toString());
                    }
                    if (cTO.getAttribute(Columns.PSR_OBS) != null) {
                        paramSvcRseTO.setPsrObs(cTO.getAttribute(Columns.PSR_OBS).toString());
                    }
                    if (cTO.getAttribute(Columns.PSR_ALTERADO_PELO_SERVIDOR) != null) {
                        paramSvcRseTO.setPsrAlteradoPeloServidor(cTO.getAttribute(Columns.PSR_ALTERADO_PELO_SERVIDOR).toString());
                    }
                    paramSvcRseTO.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO);
                    paramSvcRseTO.setSvcCodigo(svcCodDestino);
                    paramSvcRseTO.setPsrDataCadastro(DateHelper.getSystemDatetime());
                    // Cria parâmetro de serviço de rse
                    createParamSvcRse(paramSvcRseTO, responsavel);
                }
                batman.iterate();
            }
            param.clear();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void copiaParamCnvRse(String cnvCodOrigem, String cnvCodDestino, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            // Somente devem ser copiados os parâmetros caso os convênios sejam distintos
            if (cnvCodOrigem.equals(cnvCodDestino)) {
                return;
            }

            final ListaParamCnvRseQuery query = new ListaParamCnvRseQuery();
            query.count = true;
            query.cnvCodigo = cnvCodOrigem;
            final int total = query.executarContador();

            if (total > 0) {
                final ParamConvenioRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamConvenioRegistroServidorDAO();
                dao.copiaBloqueioCnvPorConvenio(cnvCodOrigem, cnvCodDestino, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstFuncoesAcessoRecurso(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaAcessoRecursoQuery query = new ListaAcessoRecursoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject getAcessoUsuario(String acrCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final String usuCodigo = responsavel.getUsuCodigo();
        try {
            final TransferObject to = new CustomTransferObject();

            if (!TextHelper.isNull(usuCodigo)) {
                final AcessoUsuario acu = AcessoUsuarioHome.findById(acrCodigo, usuCodigo);
                to.setAttribute(Columns.ACU_ACR_CODIGO, acrCodigo);
                to.setAttribute(Columns.ACU_USU_CODIGO, usuCodigo);
                to.setAttribute(Columns.ACU_USU_NUMERO_ACESSO, acu.getAcuNroAcesso());
            }

            return to;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void saveAcessoUsuario(String acrCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final String usuCodigo = responsavel.getUsuCodigo();
        if (!TextHelper.isNull(usuCodigo)) {
            try {
                AcessoUsuarioHome.saveOrUpdate(acrCodigo, usuCodigo);
            } catch (CreateException | UpdateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public ParametroAgendamento findParamAgendamento(String agdCodigo, String pagNome, String pagValor, AcessoSistema responsavel) throws ParametroControllerException {
        ParametroAgendamento paramAgd = null;
        if (!TextHelper.isNull(agdCodigo)) {
            try {
                paramAgd = ParametroAgendamentoHome.findByAgdCodigoPagNomePagValor(agdCodigo, pagNome, pagValor);
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramAgd;
    }

    @Override
    public List<ParametroAgendamento> findParamAgendamento(String agdCodigo, String pagNome, AcessoSistema responsavel) throws ParametroControllerException {
        List<ParametroAgendamento> paramAgd = null;
        if (!TextHelper.isNull(agdCodigo)) {
            try {
                paramAgd = ParametroAgendamentoHome.findByAgdCodigoPagNome(agdCodigo, pagNome);
            } catch (final FindException ex) {
                throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
            }
        } else {
            throw new ParametroControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null);
        }
        return paramAgd;
    }

    @Override
    public void atualizaParamAgendamento(String agdCodigo, String nome, String valor, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final List<ParametroAgendamento> lstParamAgd = findParamAgendamento(agdCodigo, nome, responsavel);
            for (final ParametroAgendamento paramAgd : lstParamAgd) {
                AbstractEntityHome.remove(paramAgd);
            }
        } catch (final ParametroControllerException e) {
            LOG.warn("Não existe parâmetro de agendamento para ser atualizado.");
        } catch (final RemoveException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        try {
            ParametroAgendamentoHome.create(agdCodigo, nome, valor);
            final LogDelegate log = new LogDelegate(responsavel, Log.PARAMETRO_AGENDAMENTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setAgendamento(agdCodigo);
            log.write();
        } catch (final com.zetra.econsig.exception.CreateException e) {
            LOG.warn("Não foi possível atualizar o parâmetro de agendamento.", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.atualizar.parametro.agendamento", responsavel);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createAcessoRecurso(String funCodigo, String papCodigo, String acrRecurso, String acrParametro, String acrOperacao, String acrSessao, String acrBloqueio, Short acrAtivo, String acrFimFluxo, String itmCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final AcessoRecurso acesso = AcessoRecursoHome.create(funCodigo, papCodigo, acrRecurso, acrParametro, acrOperacao, acrSessao, acrBloqueio, acrAtivo, acrFimFluxo, itmCodigo);
            final String acrCodigo = acesso.getAcrCodigo();

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ACESSO_RECURSO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setAcessoRecurso(acrCodigo);
            logDelegate.setFuncao(funCodigo);
            logDelegate.setPapel(papCodigo);
            logDelegate.setItemMenu(itmCodigo);
            logDelegate.write();

            return acrCodigo;
        } catch (final com.zetra.econsig.exception.CreateException e) {
            LOG.error("Não foi possível criar o acesso recurso.", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.nao.possivel.criar.acesso.recurso", responsavel);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeAcessoRecursoByFunCodigo(String funCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final Collection<AcessoRecurso> acessosRecursos = AcessoRecursoHome.findByFunCodigo(funCodigo);
            for (final AcessoRecurso acessoRecurso : acessosRecursos) {
                removeAcessoRecurso(acessoRecurso, responsavel);
            }
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private void removeAcessoRecurso(AcessoRecurso acessoRecurso, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            AjudaHome.removeByAcessoRescurso(acessoRecurso.getAcrCodigo());
            AcessoUsuarioHome.removeByAcessoRescurso(acessoRecurso.getAcrCodigo());
            AbstractEntityHome.remove(acessoRecurso);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ACESSO_RECURSO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setAcessoRecurso(acessoRecurso.getAcrCodigo());
            logDelegate.write();

        } catch (final RemoveException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.warn("Não foi possível excluir o acesso recurso.");
            throw new ParametroControllerException("mensagem.erro.nao.possivel.excluir.acesso.recurso", responsavel, e);
        } catch (final LogControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstRestricoesAcesso(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaRegraRestricaoAcessoQuery query = new ListaRegraRestricaoAcessoQuery();
            query.csaCodigo = csaCodigo;
            query.todos = false;

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTodasRestricoesAcesso(AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaRegraRestricaoAcessoQuery query = new ListaRegraRestricaoAcessoQuery();
            query.todos = true;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countRestricoesAcesso(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaRegraRestricaoAcessoQuery query = new ListaRegraRestricaoAcessoQuery();
            query.csaCodigo = csaCodigo;
            query.count = true;
            query.todos = false;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createRestricaoAcesso(TransferObject restricaoAcessoTO, AcessoSistema responsavel) throws ParametroControllerException {
        final Time rraHoraIni = (Time) restricaoAcessoTO.getAttribute(Columns.RRA_HORA_INICIO);
        final Time rraHoraFim = (Time) restricaoAcessoTO.getAttribute(Columns.RRA_HORA_FIM);
        final String rraDescricao = (String) restricaoAcessoTO.getAttribute(Columns.RRA_DESCRICAO);
        final Short rraDiaSemana = (Short) restricaoAcessoTO.getAttribute(Columns.RRA_DIA_SEMANA);
        final Date rraData = (Date) restricaoAcessoTO.getAttribute(Columns.RRA_DATA);
        final String papCodigo = (String) restricaoAcessoTO.getAttribute(Columns.RRA_PAP_CODIGO);
        final String rraDiasUteis = (String) restricaoAcessoTO.getAttribute(Columns.RRA_DIAS_UTEIS);
        final String funCodigo = (String) restricaoAcessoTO.getAttribute(Columns.RRA_FUN_CODIGO);
        final String csaCodigo = (String) restricaoAcessoTO.getAttribute(Columns.RCA_CSA_CODIGO);

        try {
            final RegraRestricaoAcesso regra = RegraRestricaoAcessoHome.create(rraHoraIni, rraHoraFim, rraDescricao, rraData, rraDiaSemana, rraDiasUteis, funCodigo, csaCodigo, papCodigo);

            // Grava log da criação do parâmetro
            final LogDelegate log = new LogDelegate(responsavel, Log.RESTRICAO_ACESSO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setRegraRestricaoAcesso(regra.getRraCodigo());
            log.setFuncao(funCodigo);
            log.setConsignataria(csaCodigo);
            log.setPapel(papCodigo);
            log.getUpdatedFields(restricaoAcessoTO.getAtributos(), null);
            log.write();

            //reconstrói o cache de restrições de acesso
            try {
                ControleRestricaoAcesso.addRestricao(restricaoAcessoTO, responsavel);
                ControleRestricaoAcesso.resetCacheRestricoes();
            } catch (final ZetraException ex) {
                LOG.warn("Erro ao inserir nova regra de restrição de acesso ao cache.", ex);
            }

            return regra.getRraCodigo();
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void excluirRegraRestricaoAcesso(String rraCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            try {
                final RegraRestricaoAcessoCsa rac = RegraRestricaoAcessoHome.findRestricaoAcessoCsaByEntPai(rraCodigo);
                AbstractEntityHome.remove(rac);
            } catch (final FindException ex) {
                //se não encontrou, não é uma restrição criada pela consignatária.
            }

            final RegraRestricaoAcesso regra = RegraRestricaoAcessoHome.findByPrimaryKey(rraCodigo);
            AbstractEntityHome.remove(regra);

            //reconstrói o cache de restrições de acesso
            ControleRestricaoAcesso.resetCacheRestricoes();

        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erro.regra.restricao.acesso.nao.encontrada", responsavel, ex);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erro.regra.restricao.acesso.nao.pode.ser.removida", responsavel, ex);
        }
    }

    /**
     * MÉTODOS PARA GERENCIAMENTO DE PARÂMETROS DO SDP
     */

    @Override
    public List<TransferObject> selectParamPlano(String plaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParametroPlanoQuery query = new ListaParametroPlanoQuery();
            query.plaCodigo = plaCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, String> getParamPlano(String plaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final Map<String, String> parametrosPlano = new HashMap<>();
        final List<TransferObject> lstParamPlano = selectParamPlano(plaCodigo, responsavel);

        for (final TransferObject ppl : lstParamPlano) {
            final String paramName = (String) ppl.getAttribute(Columns.TPP_CODIGO);
            final String paramVlr = (String) ppl.getAttribute(Columns.PPL_VALOR);

            parametrosPlano.put(paramName, paramVlr);
        }
        return parametrosPlano;
    }

    @Override
    public void validaParametrosPlanoDesconto(String plaCodigo, String svcCodigo, BigDecimal adeVlr, Integer adePrazo, String adeIndice, AcessoSistema responsavel) throws ParametroControllerException {
        //Lista dos parâmetros de plano necessários
        final Map<String, String> parametrosPlano = new HashMap<>();
        final List<TransferObject> lstParamPlano = selectParamPlano(plaCodigo, responsavel);

        for (final TransferObject ppl : lstParamPlano) {
            final String paramName = (String) ppl.getAttribute(Columns.TPP_CODIGO);
            final String paramVlr = (String) ppl.getAttribute(Columns.PPL_VALOR);

            parametrosPlano.put(paramName, paramVlr);
        }

        final ParamSvcTO paramSvcCse = getParamSvcCseTO(svcCodigo, responsavel);

        // Se o valor é fixo
        boolean adeVlrFixo = !paramSvcCse.isTpsAlteraAdeVlr();
        if (!adeVlrFixo && parametrosPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO)) {
            adeVlrFixo = CodedValues.PLANO_VALOR_PRE_DETERMINADO.equals(parametrosPlano.get(CodedValues.TPP_VLR_FIXO_PLANO)); // Habilita ou nao campo de valor da reserva dependendo da configuração do plano
        }

        // Se o ade_vlr padrão
        BigDecimal adeVlrPadrao = ((paramSvcCse.getTpsAdeVlr() != null) && !"".equals(paramSvcCse.getTpsAdeVlr())) ? NumberHelper.parseDecimal(paramSvcCse.getTpsAdeVlr()) : BigDecimal.ZERO; // Valor da prestação fixo para o serviço
        if (paramSvcCse.isTpsAlteraAdeVlr() && parametrosPlano.containsKey(CodedValues.TPP_VLR_PLANO)) {
            final String valorPlano = parametrosPlano.get(CodedValues.TPP_VLR_PLANO);
            adeVlrPadrao = !TextHelper.isNull(valorPlano) ? NumberHelper.parseDecimal(valorPlano) : adeVlrPadrao; // Valor da prestação fixo para o plano
        }

        // Se o prazo é fixo
        boolean prazoFixo = paramSvcCse.isTpsPrazoFixo();
        if (!prazoFixo && parametrosPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO)) {
            prazoFixo = CodedValues.PLANO_PRAZO_FIXO_SIM.equals(parametrosPlano.get(CodedValues.TPP_PRAZO_FIXO_PLANO));
        }

        // Se o prazo padrão
        Integer maxPrazo = ((paramSvcCse.getTpsMaxPrazo() != null) && !"".equals(paramSvcCse.getTpsMaxPrazo())) ? Integer.valueOf(paramSvcCse.getTpsMaxPrazo()) : null;
        if ((maxPrazo == null) || (!paramSvcCse.isTpsPrazoFixo() && prazoFixo)) {
            maxPrazo = parametrosPlano.containsKey(CodedValues.TPP_PRAZO_MAX_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_PRAZO_MAX_PLANO)) ? Integer.valueOf(parametrosPlano.get(CodedValues.TPP_PRAZO_MAX_PLANO)) : null;
        }

        // Cadastro de indices
        final String indicePadrao = parametrosPlano.containsKey(CodedValues.TPP_INDICE_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_INDICE_PLANO)) ? parametrosPlano.get(CodedValues.TPP_INDICE_PLANO).toString() : "";

        final boolean validaValor = ((!parametrosPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO) || !CodedValues.PLANO_VALOR_PRE_DETERMINADO.equals(parametrosPlano.get(CodedValues.TPP_VLR_FIXO_PLANO))) || (!parametrosPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) || CodedValues.PLANO_SEM_RATEIO.equals(parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO))));

        if (validaValor && adeVlrFixo && (adeVlrPadrao.compareTo(adeVlr) != 0)) {
            throw new ParametroControllerException("mensagem.informacao.valor.parcela.deve.ser.arg0", responsavel, NumberHelper.format(adeVlrPadrao.doubleValue(), NumberHelper.getLang()));
        }

        if (prazoFixo && (maxPrazo != null) && ((adePrazo == null) || (maxPrazo.compareTo(adePrazo) != 0))) {
            throw new ParametroControllerException("mensagem.erro.prazo.informado.invalido.para.este.plano", responsavel);
        }

        if ((maxPrazo != null) && (maxPrazo.compareTo(0) > 0) && ((adePrazo == null) || (maxPrazo.compareTo(adePrazo) < 0))) {
            throw new ParametroControllerException("mensagem.erro.quantidade.parcelas.maior.que.permitido.para.este.plano.quantidade.maxima.permitida.arg0", responsavel, maxPrazo.toString());
        }

        // Valida o índice pelo padrão do plano
        if (!TextHelper.isNull(indicePadrao) && (TextHelper.isNull(adeIndice) || (indicePadrao.compareTo(adeIndice) != 0))) {
            try {
                // Se for diferente, verifica se não é o indice padrão de taxa de uso proporcional antes de lançar erro
                final Plano plano = PlanoHome.findByPrimaryKey(plaCodigo);
                if ((!plano.getNaturezaPlano().getNplCodigo().equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()) || !CodedValues.INDICE_PADRAO_TAXA_USO_PROPORCIONAL.equals(adeIndice))) {
                    throw new ParametroControllerException("mensagem.erro.indice.informado.invalido.para.este.plano", responsavel);
                }
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Recupera a lista de tipo de natureza a partir da natureza do serviço
     *
     * @param nseCodigo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public List<TransferObject> selectTipoNaturezaEditavelServico(String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaTipoNaturezaEditavelServicoQuery query = new ListaTipoNaturezaEditavelServicoQuery();
            query.nseCodigo = nseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna uma lista com os limites máximos ou mínimos dos parâmetros de serviço por cse dados para uma natureza de serviço
     *
     * @param tpsCodigos   - lista de parâmetros de serviço por cse
     * @param nseCodigo    - código de uma natureza de serviço
     * @param limiteMinimo - se irá buscar o limite mínimo
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public List<TransferObject> listaLimitesMaxMinParamSvcCseNse(List<String> tpsCodigos, String nseCodigo, boolean limiteMinimo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ObtemMaxMinVlrParamSvcCseNseQuery maxMinVlrsParamSvcCse = new ObtemMaxMinVlrParamSvcCseNseQuery();
            maxMinVlrsParamSvcCse.tpsCodigos = tpsCodigos;
            maxMinVlrsParamSvcCse.nseCodigo = nseCodigo;
            maxMinVlrsParamSvcCse.buscaMin = limiteMinimo;
            return maxMinVlrsParamSvcCse.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ParamOrgao
    @Override
    public List<TransferObject> selectParamOrgaoEditavel(String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamOrgaoEditavelQuery query = new ListaParamOrgaoEditavelQuery(orgCodigo, responsavel);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateParamOrgao(String paoVlr, String taoCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final TipoParamOrgao tipoParamOrgao = TipoParamOrgaoHome.findByPrimaryKey(taoCodigo);
            String paoVlrOld = tipoParamOrgao.getTaoVlrDefault();

            if ((responsavel.isSup() && !"S".equals(tipoParamOrgao.getTaoSupAltera())) || (responsavel.isCse() && !"S".equals(tipoParamOrgao.getTaoCseAltera())) || (responsavel.isOrg() && !"S".equals(tipoParamOrgao.getTaoOrgAltera()))) {
                throw new ParametroControllerException("mensagem.alterar.parametro.orgao.erro.permissao", responsavel);
            }

            try {
                final ParamOrgao paramOrgao = ParamOrgaoHome.findByPrimaryKey(orgCodigo, taoCodigo);
                paoVlrOld = paramOrgao.getPaoVlr();
                paramOrgao.setPaoVlr(paoVlr);
                AbstractEntityHome.update(paramOrgao);
            } catch (final FindException ex) {
                // Se o parâmetro não existe, então devemos criá-lo
                ParamOrgaoHome.create(orgCodigo, taoCodigo, paoVlr);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_ORGAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setOrgao(orgCodigo);
            log.setTipoParamOrgao(taoCodigo);
            log.addChangedField(Columns.PAO_VLR, paoVlr, paoVlrOld);
            log.write();
        } catch (FindException | UpdateException | com.zetra.econsig.exception.CreateException |
                 LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public ParamOrgao findParamOrgaoByOrgCodigoAndTaoCodigo(String orgCodigo, String taoCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            return ParamOrgaoHome.findByPrimaryKey(orgCodigo, taoCodigo);
        } catch (final FindException ex) {
            return null;
        }
    }

    @Override
    public Integer buscarQuantidadeParamOrgao(String taoCodigo, String estCodigo, String orgCodigo, String paoVlr, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamOrgaoQuery query = new ListaParamOrgaoQuery();
            query.taoCodigo = taoCodigo;
            query.estCodigo = estCodigo;
            query.orgCodigo = orgCodigo;
            query.paoVlr = paoVlr;
            query.count = true;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCor(String svcCodigo, String corCodigo, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        List<String> svcCodigos = null;
        List<String> corCodigos = null;

        if (!TextHelper.isNull(svcCodigo)) {
            svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigo);
        }

        if (!TextHelper.isNull(corCodigo)) {
            corCodigos = new ArrayList<>();
            corCodigos.add(corCodigo);
        }

        return selectParamSvcCor(svcCodigos, corCodigos, tpsCodigos, ativo, responsavel);
    }

    @Override
    public List<TransferObject> selectParamSvcCor(List<String> svcCodigos, List<String> corCodigos, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListarParamSvcCorQuery query = new ListarParamSvcCorQuery();
            query.svcCodigos = svcCodigos;
            query.corCodigos = corCodigos;
            query.tpsCodigos = tpsCodigos;
            query.ativo = ativo;

            List<TransferObject> result = query.executarDTO();

            if (!ativo && (result != null) && result.isEmpty()) {
                query.dataIniVigIndiferente = true;
                result = query.executarDTO();
            }

            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public void updateParamSvcCor(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : parametros) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_COR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setCorrespondente((String) cto.getAttribute(Columns.PSO_COR_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.PSO_SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.PSO_TPS_CODIGO));
                log.add("PSO_VLR: " + cto.getAttribute(Columns.PSO_VLR));
                log.write();
            }
            dao.updateParamSvcCor(parametros);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void deleteParamIgualCsa(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ParametrosDAO dao = DAOFactory.getDAOFactory().getParametrosDAO();
            for (final TransferObject cto : tpsCodigo) {
                final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_COR, Log.DELETE, Log.LOG_INFORMACAO);
                log.setCorrespondente((String) cto.getAttribute(Columns.PSO_COR_CODIGO));
                log.setServico((String) cto.getAttribute(Columns.PSO_SVC_CODIGO));
                log.setTipoParamSvc((String) cto.getAttribute(Columns.PSO_TPS_CODIGO));
                log.write();
            }
            dao.deleteParamIgualCsa(tpsCodigo);

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int calcularAdeCarenciaDiaCorteCsa(int adeCarencia, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException {

        try {
            final String strDiaCorteCsa = getParamCsa(csaCodigo, CodedValues.TPA_DIA_CORTE, responsavel);
            final int diaCorteCsa = !TextHelper.isNull(strDiaCorteCsa) ? Integer.parseInt(strDiaCorteCsa) : 0;
            final int diaCorteSistema = PeriodoHelper.getInstance().getProximoDiaCorte(orgCodigo, responsavel);
            final boolean calcularCarencia = (diaCorteCsa > 0) && (diaCorteCsa != diaCorteSistema);

            if (calcularCarencia && (DateHelper.getDay(DateHelper.getSystemDate()) > diaCorteCsa) && (DateHelper.getDay(DateHelper.getSystemDate()) <= diaCorteSistema)) {
                return adeCarencia + 1;
            }

            return adeCarencia;
        } catch (ParametroControllerException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> filtraAdeRestringePortabilidade(List<TransferObject> lstConsignacao, String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            MargemDisponivel margemDisponivel = null;
            final List<TransferObject> lstConsignacaoResult = new ArrayList<>();
            CustomTransferObject paramSvcCondicionaPortabilidade = null;
            try {
                paramSvcCondicionaPortabilidade = getParamSvcCse(svcCodigo, CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE, responsavel);
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
            final String condicionaPortabilidade = paramSvcCondicionaPortabilidade != null ? (String) paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR) : "N";

            if (!CodedValues.TPC_NAO.equals(condicionaPortabilidade) && !condicionaPortabilidade.isEmpty()) {
                for (final TransferObject consignacao : lstConsignacao) {
                    final String csaCodigo = (String) consignacao.getAttribute(Columns.CSA_CODIGO);
                    final String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                    margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, null, responsavel);
                    final List<ParcelaDescontoTO> parcelas = parcelaController.findParcelas(adeCodigo, null, responsavel);
                    final int ultimaParcela = parcelas != null ? parcelas.size() - 1 : 0;
                    final boolean margemNegativa = margemDisponivel.getMargemRestante().compareTo(BigDecimal.ZERO) < 0;
                    final boolean rejeitado = (parcelas != null) && !parcelas.isEmpty() && (CodedValues.SPD_REJEITADAFOLHA.equals(parcelas.get(ultimaParcela).getSpdCodigo()) || CodedValues.SPD_SEM_RETORNO.equals(parcelas.get(ultimaParcela).getSpdCodigo()));

                    if ((CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA.equals(condicionaPortabilidade) && margemNegativa) || (CodedValues.RESTRINGE_PORTABILIDADE_PARCELA_REJEITADA.equals(condicionaPortabilidade) && rejeitado)) {
                        lstConsignacaoResult.add(consignacao);
                    } else if (CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_E_PARCELA_REJEITADA.equals(condicionaPortabilidade) && margemNegativa && rejeitado) {
                        lstConsignacaoResult.add(consignacao);
                    } else if (CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_OU_PARCELA_REJEITADA.equals(condicionaPortabilidade) && (margemNegativa || rejeitado)) {
                        lstConsignacaoResult.add(consignacao);
                    }
                }

                return lstConsignacaoResult;
            } else {
                return lstConsignacao;
            }
        } catch (ViewHelperException | ParcelaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean isReservaSaudeSemModulo(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final boolean moduloBeneficioSaude = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
            final boolean permiteReservaSaudeSemModulo = ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel);

            if (moduloBeneficioSaude && permiteReservaSaudeSemModulo) {
                final Servico servico = ServicoHome.findByPrimaryKey(svcCodigo);
                final String nseCodigo = servico.getNaturezaServico() != null ? servico.getNaturezaServico().getNseCodigo() : null;

                if ((nseCodigo != null) && (CodedValues.NSE_PLANO_DE_SAUDE.equals(nseCodigo) || CodedValues.NSE_PLANO_ODONTOLOGICO.equals(nseCodigo))) {
                    return true;
                }
            }
            return false;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectParamSvcCsaDiferente(List<String> svcCodigos, List<String> csaCodigos, List<String> tpsCodigos, String pscVlrDiferente, String pscVlrRefDiferentes, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
            query.svcCodigos = svcCodigos;
            query.csaCodigos = csaCodigos;
            query.tpsCodigos = tpsCodigos;
            query.pscVlrDiferente = pscVlrDiferente;
            query.pscVlrRefDiferente = pscVlrRefDiferentes;
            query.ativo = ativo;

            List<TransferObject> result = query.executarDTO();

            if (!ativo && (result != null) && result.isEmpty()) {
                query.dataIniVigIndiferente = true;
                result = query.executarDTO();
            }

            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> selectSvcByValorFixo(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaSvcByValorFixoQuery query = new ListaSvcByValorFixoQuery();
            query.csaCodigo = csaCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException(ex);
        }
    }

    @Override
    public boolean isExigeReconhecimentoFacialServidor(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        if (!responsavel.isSer()) {
            return false;
        }
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                final ParamSvcTO paramSvcCse = getParamSvcCseTO(svcCodigo, responsavel);
                return paramSvcCse.isTpsRequerReconhecimentoFacilServidor();
            }
            return false;
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean isSimularConsignacaoComReconhecimentoFacialELiveness(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        if (!responsavel.isSer()) {
            return false;
        }
        final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
        final boolean reconhecimentoFacialComLiveness = ParamSist.paramEquals(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        if (geraSenhaAutOtp && reconhecimentoFacialComLiveness) {
            final ParamSvcTO paramSvcCse = getParamSvcCseTO(svcCodigo, responsavel);
            return paramSvcCse.isTpsRequerReconhecimentoFacilServidor();
        }

        return false;
    }

    @Override
    public void invertVinculoParam(int invertCode, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioVinculoRegistroServidorQuery query = new ListaConvenioVinculoRegistroServidorQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> result = query.executarDTO();

            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CNV_CSA_CODIGO, csaCodigo);

            final List<String> vrsCodigos = new ArrayList<>();
            final List<TransferObject> vrs = servidorController.selectVincRegistroServidor(true, responsavel);
            final List<TransferObject> servicos = convenioController.listCnvScvCodigo(criterio, responsavel);

            for (final TransferObject vr : invertCode == 1 ? vrs : result) {
                final String vrsCodigo = (String) vr.getAttribute(invertCode == 1 ? Columns.VRS_CODIGO : Columns.CVR_VRS_CODIGO);
                vrsCodigos.add(vrsCodigo);
            }

            for (final TransferObject ser : servicos) {
                final String svcCodigo = (String) ser.getAttribute(Columns.SVC_CODIGO);
                servidorController.updateCnvVincCsaSvc(csaCodigo, svcCodigo, vrsCodigos, responsavel);
            }

        } catch (HQueryException | ServidorControllerException | ConvenioControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new ConvenioControllerException(e);
        }
    }

    @Override
    public boolean isObrigatorioAnexoInclusao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final ParamSvcTO paramSvcCse = getParamSvcCseTO(svcCodigo, responsavel);
        return (paramSvcCse.isTpsAnexoInclusaoContratosObrigatorioCseOrgSup() && responsavel.isCseSupOrg())
                || (paramSvcCse.isTpsAnexoInclusaoContratosObrigatorioCsaOrg() && responsavel.isCsaCor())
                || (paramSvcCse.isTpsAnexoInclusaoContratosObrigatorioSer() && responsavel.isSer());
    }

    @Override
    public List<TransferObject> lstBloqueioCnvRegistroServidorEntidade(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaConvenioRegistroServidorEntidadeQuery query = new ListaConvenioRegistroServidorEntidadeQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstBloqueioCnvRegistroServidorCnvCodigos(List<String> cnvCodigos, AcessoSistema responsavel) throws ParametroControllerException {
        try {
            final ListaConvenioRegistroServidorCnvCodigosQuery query = new ListaConvenioRegistroServidorCnvCodigosQuery();
            query.cnvCodigos = cnvCodigos;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se é possível reservar margem sem senha pelo papel de consignatária de acordo com a permissão
     * que o servidor pode efetuar para determinada consignatária, durante determinado tempo.
     *
     * @param rseCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param senhaObrigatoria
     * @param responsavel
     * @return
     * @throws ParametroControllerException
     */
    @Override
    public boolean verificaAutorizacaoReservaSemSenha(String rseCodigo, String svcCodigo, boolean senhaObrigatoria, String adeNumero, AcessoSistema responsavel) throws ParametroControllerException {
        final List<String> funcoesAutorizacaoSemSenha = new ArrayList<>();
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_RES_MARGEM);
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_AUT_RESERVA);
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_INCLUSAO_VIA_LOTE);
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_COMP_CONTRATO);
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_CONS_MARGEM);
        funcoesAutorizacaoSemSenha.add(CodedValues.FUN_RENE_CONTRATO);

        if (funcoesAutorizacaoSemSenha.contains(responsavel.getFunCodigo()) &&
                (ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel) > 0) && responsavel.isCsa() && senhaObrigatoria && !TextHelper.isNull(rseCodigo)) {
            try {
                boolean svcEmprestimoCartao = false;
                if (!TextHelper.isNull(adeNumero)) {
                    final Servico servico = ServicoHome.findByAdeNumero(adeNumero);
                    svcEmprestimoCartao = (servico != null) && CodedValues.NSE_EMPRESTIMO.equals(servico.getNseCodigo());
                } else if (!TextHelper.isNull(svcCodigo)) {
                    final CustomTransferObject naturezaServico = servicoController.findNaturezaServico(svcCodigo, responsavel);
                    final String nseCodigo = (String) naturezaServico.getAttribute(Columns.NSE_CODIGO);
                    svcEmprestimoCartao = !TextHelper.isNull(nseCodigo) && (CodedValues.NSE_EMPRESTIMO.equals(nseCodigo) || CodedValues.NSE_CARTAO.equals(nseCodigo));
                }

                if (svcEmprestimoCartao || CodedValues.FUN_CONS_MARGEM.equals(responsavel.getFunCodigo())) {
                    final List<ConsultaMargemSemSenha> lstConsultaMargemSemSenhas = consignatariaController.listaConsignatariaConsultaMargemSemSenhaByRseCodigoByCsaCodigo(rseCodigo, responsavel.getCsaCodigo(), responsavel);

                    if ((lstConsultaMargemSemSenhas != null) && !lstConsultaMargemSemSenhas.isEmpty()) {
                        senhaObrigatoria = false;
                    }
                }
            } catch (final ServicoControllerException | ConsignatariaControllerException | FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParametroControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return senhaObrigatoria;
    }
}