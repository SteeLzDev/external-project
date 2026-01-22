package com.zetra.econsig.service.convenio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.GrupoServicoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamTarifCseTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenio;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenioHome;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenioId;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.NaturezaServicoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaConvenioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaServicoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.StatusConvenio;
import com.zetra.econsig.persistence.entity.StatusConvenioHome;
import com.zetra.econsig.persistence.entity.TipoGrupoSvc;
import com.zetra.econsig.persistence.entity.TipoGrupoSvcHome;
import com.zetra.econsig.persistence.entity.TipoNatureza;
import com.zetra.econsig.persistence.entity.TipoNaturezaHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ObtemConsignatariaCnvAtivoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCnvsAtualizarVerbaByCnvQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCnvsAtualizarVerbaBySvcQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodVerbaConvenioAtivoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodVerbaConvenioInativoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaByCsaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaCsaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConsignatariaMesmaVerbaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCodVerbaFeriasQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCodVerbaRefQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioConsignatariaBloquearQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioConsolidaDescontosQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteBloquearQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteByCsaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteDesbloquearQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteIncluirQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioEntidadeQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioPelosIdentificadoresQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioRelIntegracaoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioSvcQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosComAdeAtivosQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosIncMargemCartaoReservaLancamentoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosParaAlteracaoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCorrespondenteConvenioOrgaoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaEntidadesCnvNotInListQuery;
import com.zetra.econsig.persistence.query.convenio.ListaOcorrenciaConvenioQuery;
import com.zetra.econsig.persistence.query.convenio.ListaOrgaoConvenioAtivoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaOrgaoConvenioCorrespondenteQuery;
import com.zetra.econsig.persistence.query.convenio.ListaParamConvenioExpiradoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaParametrosConvenioQuery;
import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioCorrespondenteByCsaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioCorrespondenteQuery;
import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioQuery;
import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioServicoQuery;
import com.zetra.econsig.persistence.query.convenio.RecuperaCodVerbasCsaQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOrgaoQuery;
import com.zetra.econsig.persistence.query.orgao.ObtemOrgaoMaxCnvAtivoQuery;
import com.zetra.econsig.persistence.query.servico.ListaGrupoServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaTipoNaturezaEditavelServicoQuery;
import com.zetra.econsig.persistence.query.servico.ObtemServicoByCodVerbaSvcIdentificadorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPorCnvQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConvenioControllerBean</p>
 * <p>Description: Session Bean para manipulacao de convênios.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConvenioControllerBean implements ConvenioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConvenioControllerBean.class);

    private static final int NUM_MAX_COD_VERBAS_A_MOSTRAR = 5;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Override
    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, AcessoSistema responsavel) throws ConvenioControllerException {
        return createConvenio(svcCodigo, csaCodigo, orgCodigo, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, null, responsavel);
    }

    @Override
    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, AcessoSistema responsavel) throws ConvenioControllerException {
        return createConvenio(svcCodigo, csaCodigo, orgCodigo, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, vrbConvenioDirf, null, null, responsavel);
    }

    private List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, String tmoCodigo, String ocoObs, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            if (TextHelper.isNull(svcCodigo)) {
                throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel);
            }

            final ListaOrgaoQuery lstOrgaoQuery = new ListaOrgaoQuery();
            if (!TextHelper.isNull(orgCodigo)) {
                lstOrgaoQuery.orgCodigo = orgCodigo;
            }

            final ListaConsignatariaQuery lstConsignatariaQuery = new ListaConsignatariaQuery();
            if (!TextHelper.isNull(csaCodigo)) {
                lstConsignatariaQuery.csaCodigo = csaCodigo;
            }

            final ListaServicoQuery lstServicoQuery = new ListaServicoQuery();
            if (!TextHelper.isNull(svcCodigo)) {
                lstServicoQuery.svcCodigo = svcCodigo;
            }

            final List<TransferObject> orgaos = lstOrgaoQuery.executarDTO();
            final List<TransferObject> consignatarias = lstConsignatariaQuery.executarDTO();
            final List<TransferObject> servicos = lstServicoQuery.executarDTO();

            if (orgaos.size() == 0) {
                throw new ConvenioControllerException("mensagem.erro.nenhum.orgao.cadastrado.antes.desbloquear.cadastre", responsavel);
            }
            if (consignatarias.size() == 0) {
                throw new ConvenioControllerException("mensagem.erro.nenhuma.consignataria.cadastrado.antes.desbloquear.cadastre", responsavel);
            }
            if (servicos.size() != 1) {
                throw new ConvenioControllerException("mensagem.erro.servico.deve.ser.especificado.para.criacao.convenio", responsavel);
            }

            final boolean exigeMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CONVENIOS, responsavel);
            if (exigeMotivoOperacao && TextHelper.isNull(tmoCodigo) && ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_NAO, responsavel)) {
                throw new ConvenioControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            String tipoOcorrencia = null;
            String observacao = null;
            if (exigeMotivoOperacao) {
                tipoOcorrencia = CodedValues.TOC_DESBLOQUEIO_CONVENIO;
                observacao = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oco.obs.alteracao.convenio", responsavel);
                if (!TextHelper.isNull(ocoObs)) {
                    observacao += " " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, ocoObs);
                }
            }

            final TransferObject servico = servicos.get(0);
            TransferObject consignataria = null;
            TransferObject orgao = null;

            final String svcIdentificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
            String csaIdentificador = null;
            String orgIdentificador = null;

            String cnvIdentificador = "", cnvDescricao = "";

            final Iterator<TransferObject> it = consignatarias.iterator();

            final List<String> convenios = new ArrayList<>();

            while (it.hasNext()) {
                consignataria = it.next();
                csaIdentificador = consignataria.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                csaCodigo = consignataria.getAttribute(Columns.CSA_CODIGO).toString();

                final Iterator<TransferObject> it2 = orgaos.iterator();
                while (it2.hasNext()) {
                    orgao = it2.next();
                    orgIdentificador = orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                    orgCodigo = orgao.getAttribute(Columns.ORG_CODIGO).toString();
                    cnvIdentificador = cnvDescricao = criaCnvIdentificador(orgIdentificador, svcIdentificador, csaIdentificador);
                    vrbConvenio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_SIM, responsavel) ? orgIdentificador + csaIdentificador + svcIdentificador : vrbConvenio;
                    final Convenio convenio = criaConvenio(orgCodigo, svcCodigo, csaCodigo, cnvIdentificador, cnvDescricao, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, vrbConvenioDirf, tipoOcorrencia, tmoCodigo, observacao, responsavel);
                    convenios.add(convenio.getCnvCodigo());
                }
            }

            return convenios;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    /**
     * Atualiza a tabela de convênios bloqueados de acordo com valor default definido
     * @param convenios
     * @throws ConvenioControllerException
     */
    @Override
    public void setParamQuantidadeDefault(List<String> convenios) throws ConvenioControllerException {
        try {
            servidorController.setRseQtdAdeDefault(convenios);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.atualizar.bloqueios.convenio", (AcessoSistema) null, ex);
        }
    }

    private Convenio criaConvenio(String orgCodigo, String svcCodigo, String csaCodigo, String cnvIdentificador, String cnvDescricao, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, AcessoSistema responsavel) throws ConvenioControllerException {
        return criaConvenio(orgCodigo, svcCodigo, csaCodigo, cnvIdentificador, cnvDescricao, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, vrbConvenioDirf, null, null, null, responsavel);
    }

    private Convenio criaConvenio(String orgCodigo, String svcCodigo, String csaCodigo, String cnvIdentificador, String cnvDescricao, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, String tocCodigo, String tmoCodigo, String ocoObs, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            boolean criarOcorrencia = false;
            Convenio convenio;

            final String vce_codigo = "1";
            try {
                // Procura o convênio e caso exista altera as informações necessárias
                convenio = ConvenioHome.findByChave(svcCodigo, csaCodigo, orgCodigo);

                final LogDelegate log = new LogDelegate(responsavel, Log.CONVENIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConvenio(convenio.getCnvCodigo());
                if (!CodedValues.SCV_ATIVO.equals(convenio.getStatusConvenio().getScvCodigo())) {
                    log.setStatusConvenio(CodedValues.SCV_ATIVO);
                    criarOcorrencia = true;
                    convenio.setStatusConvenio(StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_ATIVO));
                }
                if ((convenio.getCnvCodVerba() == null) && (vrbConvenio != null)) {
                    log.addChangedField(Columns.CNV_COD_VERBA, vrbConvenio);
                    convenio.setCnvCodVerba(vrbConvenio);
                } else if ((convenio.getCnvCodVerba() != null) && !convenio.getCnvCodVerba().equals(vrbConvenio)) {
                    log.addChangedField(Columns.CNV_COD_VERBA, vrbConvenio, convenio.getCnvCodVerba());
                    convenio.setCnvCodVerba(vrbConvenio);
                }
                if ((convenio.getCnvCodVerbaRef() == null) && (vrbConvenioRef != null)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_REF, vrbConvenioRef);
                    convenio.setCnvCodVerbaRef(vrbConvenioRef);
                } else if ((convenio.getCnvCodVerbaRef() != null) && !convenio.getCnvCodVerbaRef().equals(vrbConvenioRef)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_REF, vrbConvenioRef, convenio.getCnvCodVerbaRef());
                    convenio.setCnvCodVerbaRef(vrbConvenioRef);
                }
                if ((convenio.getCnvCodVerbaFerias() == null) && (vrbConvenioFerias != null)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_FERIAS, vrbConvenioFerias);
                    convenio.setCnvCodVerbaFerias(vrbConvenioFerias);
                } else if ((convenio.getCnvCodVerbaFerias() != null) && !convenio.getCnvCodVerbaFerias().equals(vrbConvenioFerias)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_FERIAS, vrbConvenioFerias, convenio.getCnvCodVerbaFerias());
                    convenio.setCnvCodVerbaFerias(vrbConvenioFerias);
                }
                if (TextHelper.isNull(convenio.getCnvCodVerbaDirf()) && !TextHelper.isNull(vrbConvenioDirf)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_DIRF, vrbConvenioDirf);
                    convenio.setCnvCodVerbaDirf(vrbConvenioDirf);
                } else if (!TextHelper.isNull(convenio.getCnvCodVerbaDirf()) && !convenio.getCnvCodVerbaDirf().equals(vrbConvenioDirf)) {
                    log.addChangedField(Columns.CNV_COD_VERBA_DIRF, vrbConvenioDirf, convenio.getCnvCodVerbaDirf());
                    convenio.setCnvCodVerbaDirf(vrbConvenioDirf);
                }

                AbstractEntityHome.update(convenio);
                log.write();
            } catch (final FindException ex) {
                // Somente cria convenio se ele não existe
                convenio = ConvenioHome.create(orgCodigo, CodedValues.SCV_ATIVO, svcCodigo, csaCodigo, vce_codigo, cnvIdentificador, cnvDescricao, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, vrbConvenioDirf);
                criarOcorrencia = true;
                final LogDelegate log = new LogDelegate(responsavel, Log.CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setConvenio(convenio.getCnvCodigo());
                log.setOrgao(orgCodigo);
                log.setConsignataria(csaCodigo);
                log.setServico(svcCodigo);
                log.setStatusConvenio(CodedValues.SCV_ATIVO);
                log.addChangedField(Columns.CNV_VCE_CODIGO, vce_codigo);
                log.addChangedField(Columns.CNV_IDENTIFICADOR, cnvIdentificador);
                log.addChangedField(Columns.CNV_DESCRICAO, cnvDescricao);
                log.addChangedField(Columns.CNV_COD_VERBA, vrbConvenio);
                log.addChangedField(Columns.CNV_COD_VERBA_REF, vrbConvenioRef);
                log.addChangedField(Columns.CNV_COD_VERBA_FERIAS, vrbConvenioFerias);
                log.addChangedField(Columns.CNV_COD_VERBA_DIRF, vrbConvenioDirf);
                log.write();
            }

            try {
                // Procura verba_convenio ativa
                VerbaConvenioHome.findAtivoByConvenio(convenio.getCnvCodigo());
            } catch (final FindException ex1) {
                // Somente cria verba_convenio se não existe uma ativa
                final VerbaConvenio verbaConvenio = VerbaConvenioHome.create(convenio.getCnvCodigo(), CodedValues.STS_ATIVO, CodedValues.VLR_INI_VCO);
                final LogDelegate log = new LogDelegate(responsavel, Log.VERBA_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setVerbaConvenio(verbaConvenio.getVcoCodigo());
                log.setConvenio(convenio.getCnvCodigo());
                log.addChangedField(Columns.VCO_ATIVO, CodedValues.STS_ATIVO.toString());
                log.addChangedField(Columns.VCO_VLR_VERBA, CodedValues.VLR_INI_VCO.toString());
                log.write();
            }

            // Criar ocorrência convênio
            if (criarOcorrencia && !TextHelper.isNull(tocCodigo)) {
                OcorrenciaConvenioHome.create(convenio.getCnvCodigo(), tocCodigo, tmoCodigo, ocoObs, responsavel);
            }

            return convenio;

        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.convenio.erro.interno", responsavel, ex.getMessage());
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String criaCnvIdentificador(String orgIdentificador, String svcIdentificador, String csaIdentificador) {
        return "cnv_" + orgIdentificador + "_" + svcIdentificador + "_" + csaIdentificador;
    }

    @Override
    public List<TransferObject> getCnvConsolidaDescontos(String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioConsolidaDescontosQuery query = new ListaConvenioConsolidaDescontosQuery();
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setCnvConsolidaDescontos(String svcCodigo, String orgCodigo, String cnvConsolidaDescontos, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            Convenio cnvBean = null;
            final List<Convenio> cnvBeans = ConvenioHome.findByOrgSvc(orgCodigo, svcCodigo);
            final Iterator<Convenio> it = cnvBeans.iterator();
            while (it.hasNext()) {
                // Edita o parâmetro de consolidação de descontos
                cnvBean = it.next();
                cnvBean.setCnvConsolidaDescontos(cnvConsolidaDescontos);
                AbstractEntityHome.update(cnvBean);

                // Grava log de alteração
                final LogDelegate log = new LogDelegate(responsavel, Log.CONVENIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConvenio(cnvBean.getCnvCodigo());
                log.addChangedField(Columns.CNV_CONSOLIDA_DESCONTOS, cnvConsolidaDescontos);
                log.write();
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.convenioNaoEncontrado", responsavel, ex);
        } catch (UpdateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getSvcByCodVerbaSvcIdentificador(String svcIdentificador, String cnvCodVerba, String orgCodigo, String csaCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {
        List<String> orgCodigos = null;
        if (!TextHelper.isNull(orgCodigo)) {
            orgCodigos = new ArrayList<>();
            orgCodigos.add(orgCodigo);
        }

        final ObtemServicoByCodVerbaSvcIdentificadorQuery svcByCodVerbaIdnt = new ObtemServicoByCodVerbaSvcIdentificadorQuery();
        svcByCodVerbaIdnt.cnvCodVerba = cnvCodVerba;
        svcByCodVerbaIdnt.orgCodigos = orgCodigos;
        svcByCodVerbaIdnt.csaCodigo = csaCodigo;
        svcByCodVerbaIdnt.svcIdentificador = svcIdentificador;
        svcByCodVerbaIdnt.ativo = ativo;

        try {
            return svcByCodVerbaIdnt.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> getCnvCodVerba(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaCodVerbaConvenioAtivoQuery query = new ListaCodVerbaConvenioAtivoQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCnvCodVerbaInativo(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaCodVerbaConvenioInativoQuery query = new ListaCodVerbaConvenioInativoQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setCnvPrioridade(String cnvCodVerba, String cnvPrioridade, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            // Trata o parâmetro de entrada
            Integer prioridade = null;
            try {
                prioridade = Integer.valueOf(cnvPrioridade);
            } catch (final NumberFormatException e) {
                prioridade = null;
            }

            Convenio cnvBean = null;
            final List<Convenio> cnvBeans = ConvenioHome.findByCodVerba(cnvCodVerba);
            final Iterator<Convenio> it = cnvBeans.iterator();
            while (it.hasNext()) {
                // Edita o parâmetro de consolidação de descontos
                cnvBean = it.next();
                cnvBean.setCnvPrioridade(prioridade);
                AbstractEntityHome.update(cnvBean);

                // Grava log de alteração
                final LogDelegate log = new LogDelegate(responsavel, Log.CONVENIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConvenio(cnvBean.getCnvCodigo());
                log.addChangedField(Columns.CNV_PRIORIDADE, prioridade);
                log.write();
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.convenioNaoEncontrado", responsavel, ex);
        } catch (UpdateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return this.getCsaCnvAtivo(svcCodigo, orgCodigo, false, false, responsavel);

    }

    @Override
    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, AcessoSistema responsavel) throws ConvenioControllerException {
        return this.getCsaCnvAtivo(svcCodigo, orgCodigo, false, false, responsavel);
    }

    @Override
    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, boolean listagemReserva, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ObtemConsignatariaCnvAtivoQuery query = new ObtemConsignatariaCnvAtivoQuery();
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.csaDeveSerAtiva = csaDeveSerAtiva;
            query.listagemReserva = listagemReserva;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> getOrgCnvAtivo(String csaCodigo, String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaOrgaoConvenioAtivoQuery query = new ListaOrgaoConvenioAtivoQuery();
            query.csaCodigo = csaCodigo;
            query.corCodigo = corCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> getCnvScvCodigo(String svcCodigo, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaStatusConvenioServicoQuery query = new ListaStatusConvenioServicoQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public void setCnvScvCodigo(String svcCodigo, String csaCodigo, String orgCodigo, String scvCodigo, boolean limpaVerba, List<String> codigos, AcessoSistema responsavel) throws ConvenioControllerException {
        setCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, scvCodigo, limpaVerba, codigos, null, null, responsavel);
    }

    private void setCnvScvCodigo(String svcCodigo, String csaCodigo, String orgCodigo, String scvCodigo, boolean limpaVerba, List<String> codigos, String tmoCodigo, String ocoObs, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            StatusConvenio status = null;
            try {
                status = StatusConvenioHome.findByPrimaryKey(scvCodigo);
            } catch (final FindException e) {
                LOG.error("Status '" + scvCodigo + "' inválido para o convênio. " + e.getMessage(), e);
                throw new ConvenioControllerException("mensagem.erro.status.invalido.convenio", responsavel, e);
            }

            final boolean exigeMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CONVENIOS, responsavel) && !responsavel.isSistema();
            if (exigeMotivoOperacao && TextHelper.isNull(tmoCodigo)) {
                throw new ConvenioControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            final String tipoOcorrencia = (CodedValues.SCV_INATIVO.equals(scvCodigo) ? CodedValues.TOC_BLOQUEIO_CONVENIO : CodedValues.TOC_DESBLOQUEIO_CONVENIO);
            String observacao = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oco.obs.alteracao.convenio", responsavel);
            if (!TextHelper.isNull(ocoObs)) {
                observacao += " " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, ocoObs);
            }

            final ListaConveniosParaAlteracaoQuery query = new ListaConveniosParaAlteracaoQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.codigos = codigos;

            final List<TransferObject> convenios = query.executarDTO();
            final Iterator<TransferObject> ite = convenios.iterator();
            while (ite.hasNext()) {
                final String cnvCodigo = ite.next().getAttribute(Columns.CNV_CODIGO).toString();
                try {
                    final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                    // Se o convênio não está no status passado, faz a alteração
                    if (!convenio.getStatusConvenio().getScvCodigo().equals(scvCodigo)) {
                        convenio.setStatusConvenio(status);
                        if (CodedValues.SCV_INATIVO.equals(scvCodigo) && limpaVerba) {
                            // Se não houver contrato ativo para essa combinação de consignatária e serviço,
                            // limpa as verbas de todos os convênios desta combinação
                            convenio.setCnvCodVerba(null);
                            convenio.setCnvCodVerbaRef(null);
                            convenio.setCnvCodVerbaFerias(null);
                        }
                        AbstractEntityHome.update(convenio);

                        // Criar ocorrência convênio
                        OcorrenciaConvenioHome.create(convenio.getCnvCodigo(), tipoOcorrencia, tmoCodigo, observacao, responsavel);
                    }
                } catch (final FindException e) {
                    LOG.error("Convênio '" + cnvCodigo + "' não encontrado.", e);
                }
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        } catch (UpdateException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listCnvScvCodigo(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        return listCnvScvCodigo(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> listCnvScvCodigo(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaStatusConvenioQuery query = new ListaStatusConvenioQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.csaCodigo = (String) criterio.getAttribute(Columns.CNV_CSA_CODIGO);
                query.svcDescricao = (String) criterio.getAttribute(Columns.SVC_DESCRICAO);
                query.svcIdentificador = (String) criterio.getAttribute(Columns.SVC_IDENTIFICADOR);
                query.orgCodigo = (String) criterio.getAttribute(Columns.CNV_ORG_CODIGO);
                query.scvCodigo = (String) criterio.getAttribute(Columns.CNV_SCV_CODIGO);
                if (criterio.getAttribute("verificaConvenioPossuiContratos") != null) {
                    query.verificaConvenioPossuiContratos = (boolean) criterio.getAttribute("verificaConvenioPossuiContratos");
                }
                if (!TextHelper.isNull(criterio.getAttribute(CodedValues.SERVICO_TEM_ADE))) {
                    query.temAde = (String) criterio.getAttribute(CodedValues.SERVICO_TEM_ADE);
                }

                if ((criterio.getAttribute("FILTRO_RELATORIO") != null) && (criterio.getAttribute(Columns.CNV_CSA_CODIGO) != null)) {
                    query.filtroCampoSvcRelatorioCsa = true;
                }
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public int countCnvScvCodigo(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaStatusConvenioQuery query = new ListaStatusConvenioQuery();

            if (criterio != null) {
                query.count = true;
                query.csaCodigo = (String) criterio.getAttribute(Columns.CNV_CSA_CODIGO);
                query.svcDescricao = (String) criterio.getAttribute(Columns.SVC_DESCRICAO);
                query.svcIdentificador = (String) criterio.getAttribute(Columns.SVC_IDENTIFICADOR);
                query.orgCodigo = (String) criterio.getAttribute(Columns.CNV_ORG_CODIGO);
                query.scvCodigo = (String) criterio.getAttribute(Columns.CNV_SCV_CODIGO);
                if (!TextHelper.isNull(criterio.getAttribute(CodedValues.SERVICO_TEM_ADE))) {
                    query.temAde = (String) criterio.getAttribute(CodedValues.SERVICO_TEM_ADE);
                }
            }

            final List<String> results = query.executarLista();
            return results.size();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public CustomTransferObject getParamCnv(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getParamCnv(cnvCodigo, true, true, responsavel);
    }

    @Override
    public CustomTransferObject getParamCnv(String cnvCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaParametrosConvenioQuery query = new ListaParametrosConvenioQuery();
            query.cnvCodigo = cnvCodigo;
            query.cnvAtivo = cnvAtivo;
            query.svcAtivo = svcAtivo;

            final List<TransferObject> list = query.executarDTO();

            if ((list != null) && !list.isEmpty()) {
                return (CustomTransferObject) list.get(0);
            } else {
                return null;
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getParamCnv(csaCodigo, orgCodigo, svcCodigo, true, true, responsavel);
    }

    @Override
    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaParametrosConvenioQuery query = new ListaParametrosConvenioQuery();
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.svcCodigo = svcCodigo;
            query.cnvAtivo = cnvAtivo;
            query.svcAtivo = svcAtivo;

            final List<TransferObject> list = query.executarDTO();

            if ((list != null) && !list.isEmpty()) {
                return (CustomTransferObject) list.get(0);
            } else {
                return null;
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstConvenios(String cnvCodVerba, String csaCodigo, String svcCodigo, String orgCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {

        try {
            final ListaConveniosQuery cnvQuery = new ListaConveniosQuery();
            cnvQuery.cnvCodVerba = cnvCodVerba;
            cnvQuery.csaCodigo = csaCodigo;
            cnvQuery.svcCodigo = svcCodigo;
            cnvQuery.orgCodigo = orgCodigo;
            cnvQuery.ativo = ativo;

            return cnvQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    private List<TransferObject> lstConveniosCorrespondente(String cnvCodVerba, String csaCodigo, String svcCodigo, String orgCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {

        try {
            final ListaConveniosQuery cnvQuery = new ListaConveniosQuery();
            cnvQuery.cnvCodVerba = cnvCodVerba;
            cnvQuery.csaCodigo = csaCodigo;
            cnvQuery.svcCodigo = svcCodigo;
            cnvQuery.orgCodigo = orgCodigo;
            cnvQuery.ativo = ativo;
            cnvQuery.correspondenteConvenio = true;

            return cnvQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    //Grupo de Serviços
    private GrupoServicoTransferObject setGrupoSvcValues(TipoGrupoSvc grupoServicoBean) {
        final GrupoServicoTransferObject grupoServico = new GrupoServicoTransferObject(grupoServicoBean.getTgsCodigo());
        grupoServico.setGrupoSvcGrupo(grupoServicoBean.getTgsGrupo());
        grupoServico.setGrupoSvcQuantidade(grupoServicoBean.getTgsQuantidade());
        grupoServico.setGrupoSvcQuantidadePorCsa(grupoServicoBean.getTgsQuantidadePorCsa());
        grupoServico.setGrupoSvcIdentificador(grupoServicoBean.getTgsIdentificador());

        return grupoServico;
    }

    private TipoGrupoSvc findGrupoSvcBean(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException {
        TipoGrupoSvc grupoServicoBean = null;
        if (grupoServico.getGrupoSvcCodigo() != null) {
            try {
                grupoServicoBean = TipoGrupoSvcHome.findByPrimaryKey(grupoServico.getGrupoSvcCodigo());
            } catch (final FindException ex) {
                throw new ConvenioControllerException("mensagem.erro.rotulo.grupo.servico.nao.encontrado", responsavel);
            }
        } else if (grupoServico.getGrupoSvcIdentificador() != null) {
            try {
                grupoServicoBean = TipoGrupoSvcHome.findByIdn(grupoServico.getGrupoSvcIdentificador());
            } catch (final FindException ex) {
                throw new ConvenioControllerException("mensagem.erro.rotulo.grupo.servico.nao.encontrado", responsavel);
            }
        } else {
            throw new ConvenioControllerException("mensagem.erro.rotulo.grupo.servico.nao.encontrado", responsavel);
        }

        return grupoServicoBean;
    }

    @Override
    public GrupoServicoTransferObject findGrupoServico(String tgsCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        final GrupoServicoTransferObject grupo = new GrupoServicoTransferObject(tgsCodigo);
        return findGrupoServico(grupo, responsavel);
    }

    @Override
    public GrupoServicoTransferObject findGrupoServico(GrupoServicoTransferObject grupo, AcessoSistema responsavel) throws ConvenioControllerException {
        return setGrupoSvcValues(findGrupoSvcBean(grupo, responsavel));
    }

    @Override
    public String createGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException {
        String grupoSvcCodigo = null;
        try {
            // Verifica se não existe outro grupo de serviço com o mesmo ID
            final GrupoServicoTransferObject grupoSvcOutro = new GrupoServicoTransferObject();
            grupoSvcOutro.setGrupoSvcIdentificador(grupoServico.getGrupoSvcIdentificador());

            boolean existe = false;
            try {
                findGrupoSvcBean(grupoSvcOutro, responsavel);
                existe = true;
            } catch (final ConvenioControllerException ex) {
            }
            if (existe) {
                throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.grupo.servico.existe.outro.mesmo.codigo", responsavel);
            }

            // Cria o grupo de servico
            final TipoGrupoSvc grupoServicoBean = TipoGrupoSvcHome.create(grupoServico.getGrupoSvcGrupo(), grupoServico.getGrupoSvcQuantidade(), grupoServico.getGrupoSvcQuantidadePorCsa(), grupoServico.getGrupoSvcIdentificador());
            grupoSvcCodigo = grupoServicoBean.getTgsCodigo();

            // Cria log de gravação
            final LogDelegate log = new LogDelegate(responsavel, Log.GRUPO_SERVICO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setGrupoServico(grupoSvcCodigo);
            log.getUpdatedFields(grupoServico.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ConvenioControllerException excecao = new ConvenioControllerException("mensagem.erro.nao.possivel.criar.grupo.servico.erro.interno", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new ConvenioControllerException("mensagem.erro.nao.possivel.criar.grupo.servico.existe.outro.mesmo.codigo", responsavel);
            }
            throw excecao;
        }
        return grupoSvcCodigo;
    }

    @Override
    public void updateGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final TipoGrupoSvc grupoSvcBean = findGrupoSvcBean(grupoServico, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.GRUPO_SERVICO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setGrupoServico(grupoSvcBean.getTgsCodigo());
            log.getUpdatedFields(grupoServico.getAtributos(), null);

            /* Compara a versão do cache com a passada por parâmetro */
            final GrupoServicoTransferObject grupoSvcCache = setGrupoSvcValues(grupoSvcBean);
            final CustomTransferObject merge = log.getUpdatedFields(grupoServico.getAtributos(), grupoSvcCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.TGS_IDENTIFICADOR)) {

                // Verifica se não existe outro serviço com o mesmo ID
                final GrupoServicoTransferObject grupoSvcOutro = new GrupoServicoTransferObject();
                grupoSvcOutro.setGrupoSvcIdentificador((String) merge.getAttribute(Columns.TGS_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findGrupoSvcBean(grupoSvcOutro, responsavel);
                    existe = true;
                } catch (final ConvenioControllerException ex) {
                }
                if (existe) {
                    throw new ConvenioControllerException("mensagem.erro.nao.possivel.alterar.grupo.servico.existe.outro.mesmo.codigo", responsavel);
                }
                grupoSvcBean.setTgsIdentificador((String) merge.getAttribute(Columns.TGS_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.TGS_GRUPO)) {
                grupoSvcBean.setTgsGrupo((String) merge.getAttribute(Columns.TGS_GRUPO));
            }

            if (merge.getAtributos().containsKey(Columns.TGS_QUANTIDADE)) {
                grupoSvcBean.setTgsQuantidade((Integer) merge.getAttribute(Columns.TGS_QUANTIDADE));
            }

            if (merge.getAtributos().containsKey(Columns.TGS_QUANTIDADE_POR_CSA)) {
                grupoSvcBean.setTgsQuantidadePorCsa((Integer) merge.getAttribute(Columns.TGS_QUANTIDADE_POR_CSA));
            }

            AbstractEntityHome.update(grupoSvcBean);

            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Remove o grupo de serviço selecionado e desliga os serviços a ele relacionado.
     * @param grupoServico
     * @param responsavel
     * @throws ConvenioControllerException
     */
    @Override
    public void removeGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final TipoGrupoSvc grupoServicoBean = findGrupoSvcBean(grupoServico, responsavel);
            final String tgsCodigo = grupoServicoBean.getTgsCodigo();

            // Atualiza os serviços ligados a este grupo, para não terem ligação com nenhum grupo
            final List<Servico> servicos = ServicoHome.findByTgsCodigo(tgsCodigo);
            final Iterator<Servico> it = servicos.iterator();
            Servico servicoBean = null;

            while (it.hasNext()) {
                servicoBean = it.next();
                servicoBean.setTipoGrupoSvc(null);

                AbstractEntityHome.update(servicoBean);
            }

            // Remove o grupo de serviço
            AbstractEntityHome.remove(grupoServicoBean);

            final LogDelegate log = new LogDelegate(responsavel, Log.GRUPO_SERVICO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setGrupoServico(tgsCodigo);
            log.getUpdatedFields(grupoServico.getAtributos(), null);
            log.write();

        } catch (LogControllerException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.excluir.grupo.servico.selecionado.pois.possui.dependentes", responsavel);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstGrupoServicos(boolean orderById, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaGrupoServicoQuery query = new ListaGrupoServicoQuery();
            query.orderById = orderById;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Serviço
    @Override
    public ServicoTransferObject findServico(String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        final ServicoTransferObject criterio = new ServicoTransferObject(svcCodigo);
        return findServico(criterio, responsavel);
    }

    @Override
    public ServicoTransferObject findServicoByIdn(String svcIdentificador, AcessoSistema responsavel) throws ConvenioControllerException {
        final ServicoTransferObject criterio = new ServicoTransferObject();
        criterio.setSvcIdentificador(svcIdentificador);
        return findServico(criterio, responsavel);
    }

    @Override
    public ServicoTransferObject findServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        return setServicoValues(findServicoBean(servico, responsavel));
    }

    @Override
    public ServicoTransferObject findServicoByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            return setServicoValues(ServicoHome.findByAdeCodigo(adeCodigo));
        } catch (final FindException ex) {
            throw new ConvenioControllerException("mensagem.erro.servico.nao.encontrado", responsavel);
        }
    }

    private ConvenioTransferObject setConvenioValues(Convenio cnv) {
        final ConvenioTransferObject cnvTo = new ConvenioTransferObject(cnv.getCnvCodigo());
        cnvTo.setCsaCodigo(cnv.getConsignataria().getCsaCodigo());
        cnvTo.setSvcCodigo(cnv.getServico().getSvcCodigo());
        cnvTo.setOrgCodigo(cnv.getOrgao().getOrgCodigo());
        cnvTo.setCnvCodVerba(cnv.getCnvCodVerba());
        cnvTo.setCnvCodVerbaFerias(cnv.getCnvCodVerbaFerias());
        cnvTo.setCnvCodVerbaRef(cnv.getCnvCodVerbaRef());
        cnvTo.setCnvConsolidaDescontos(cnv.getCnvConsolidaDescontos());
        cnvTo.setCnvDescricao(cnv.getCnvDescricao());
        cnvTo.setCnvIdentificador(cnv.getCnvIdentificador());
        if (!TextHelper.isNull(cnv.getCnvPrioridade())) {
            cnvTo.setCnvPrioridade(cnv.getCnvPrioridade().toString());
        }
        return cnvTo;
    }

    @Override
    public ConvenioTransferObject findByUniqueKey(String csaCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            return setConvenioValues(ConvenioHome.findByChave(svcCodigo, csaCodigo, orgCodigo));
        } catch (final FindException e) {
            throw new ConvenioControllerException("mensagem.erro.convenio.nao.encontrado", responsavel);
        }
    }

    @Override
    public ConvenioTransferObject findByPrimaryKey(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            return setConvenioValues(ConvenioHome.findByPrimaryKey(cnvCodigo));
        } catch (final FindException e) {
            throw new ConvenioControllerException("mensagem.erro.convenio.nao.encontrado", responsavel);
        }
    }

    private Servico findServicoBean(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        Servico servicoBean = null;
        if (servico.getSvcCodigo() != null) {
            try {
                servicoBean = ServicoHome.findByPrimaryKey(servico.getSvcCodigo());
            } catch (final FindException ex) {
                throw new ConvenioControllerException("mensagem.erro.servico.nao.encontrado", responsavel);
            }
        } else if (servico.getSvcIdentificador() != null) {
            try {
                servicoBean = ServicoHome.findByIdn(servico.getSvcIdentificador());
            } catch (final FindException ex) {
                throw new ConvenioControllerException("mensagem.erro.servico.nao.encontrado", responsavel);
            }
        } else {
            throw new ConvenioControllerException("mensagem.erro.servico.nao.encontrado", responsavel);
        }
        return servicoBean;
    }

    private ServicoTransferObject setServicoValues(Servico servicoBean) {
        final ServicoTransferObject servico = new ServicoTransferObject(servicoBean.getSvcCodigo());
        servico.setSvcAtivo(servicoBean.getSvcAtivo());
        servico.setSvcDescricao(servicoBean.getSvcDescricao());
        servico.setSvcObs(servicoBean.getSvcObs());
        servico.setSvcIdentificador(servicoBean.getSvcIdentificador());
        if (servicoBean.getTipoGrupoSvc() != null) {
            servico.setSvcTgsCodigo(servicoBean.getTipoGrupoSvc().getTgsCodigo());
        }
        Integer svcPrioridade = null;
        try {
            svcPrioridade = Integer.valueOf(servicoBean.getSvcPrioridade());
        } catch (final NumberFormatException e) {
        }
        servico.setSvcPrioridade(svcPrioridade);
        servico.setSvcNseCodigo(servicoBean.getNaturezaServico().getNseCodigo());
        return servico;
    }

    @Override
    public String createServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        return createServico(servico, null, null, null, null, null, responsavel);
    }

    /**
     * Cria o serviço, se svcCopia != null copia os parametros do serviço selecionado e se cnvCopia != null copias os convenios do serviço selecionado
     * @param servico
     * @param svcCopia
     * @param cnvCopia
     * @param responsavel
     * @return svcCodigo
     * @throws ConvenioControllerException
     */
    @Override
    public String createServico(ServicoTransferObject servico, String svcCopia, String cnvCopia, String paramSvcCsaCopia, String bloqueioCnvCopia, String bloqueioSvcCopia, AcessoSistema responsavel) throws ConvenioControllerException {
        String svcCodigo = null;
        try {
            Servico svcBean = null;
            try {
                // Verifica se já existe serviço com mesmo identificador
                svcBean = ServicoHome.findByIdn(servico.getSvcIdentificador());
            } catch (final FindException ex) {
                LOG.error(ex.getMessage());
            }
            if (svcBean != null) {
                throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.servico.existe.outro.mesmo.codigo", responsavel);
            }

            // Se copia os parâmetros de serviço, verifica se o serviço a ser copiado faz parte de algum grupo de serviço
            if (!TextHelper.isNull(svcCopia)) {
                final Servico copia = ServicoHome.findByPrimaryKey(svcCopia);
                if (copia.getTipoGrupoSvc() != null) {
                    servico.setSvcTgsCodigo(copia.getTipoGrupoSvc().getTgsCodigo());
                } else {
                    servico.setSvcTgsCodigo(null);
                }
            }

            final BatchManager batman = new BatchManager(SessionUtil.getSession());

            String svcPrioridade = null;
            try {
                svcPrioridade = servico.getSvcPrioridade().toString();
            } catch (final RuntimeException e) {
            }

            final String nseCodigo = servico.getSvcNseCodigo();
            if (nseCodigo != null) {
                try {
                    NaturezaServicoHome.findByPrimaryKey(nseCodigo);
                } catch (final FindException ex) {
                    throw new ConvenioControllerException("mensagem.erro.natureza.servico.nao.encontrada", responsavel, ex);
                }
            }

            final Servico servicoBean = ServicoHome.create(servico.getSvcIdentificador(), servico.getSvcDescricao(), servico.getSvcAtivo(), servico.getSvcTgsCodigo(), svcPrioridade, servico.getSvcObs(), nseCodigo);
            svcCodigo = servicoBean.getSvcCodigo();

            // Copia os parâmetros de serviço
            if (!TextHelper.isNull(svcCopia)) {
                parametroController.copiaParamSvc(svcCopia, svcCodigo, batman, responsavel);
            }

            // Copia os convenios do serviço
            if (!TextHelper.isNull(cnvCopia)) {
                try {
                    final List<TransferObject> todosconvenios = lstConvenios(null, null, cnvCopia, null, true, responsavel);
                    final Iterator<TransferObject> it = todosconvenios.iterator();
                    TransferObject convenio = null;
                    while (it.hasNext()) {
                        convenio = it.next();
                        final String orgCodigo = convenio.getAttribute(Columns.CNV_ORG_CODIGO) != null ? convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString() : "";
                        final String csaCodigo = convenio.getAttribute(Columns.CNV_CSA_CODIGO) != null ? convenio.getAttribute(Columns.CNV_CSA_CODIGO).toString() : "";
                        final String cnvIdent = convenio.getAttribute(Columns.CNV_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.CNV_IDENTIFICADOR).toString() : "";
                        final String cnvDescricao = convenio.getAttribute(Columns.CNV_DESCRICAO) != null ? convenio.getAttribute(Columns.CNV_DESCRICAO).toString() : "";
                        final String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                        final String cnvCodVerbaRef = convenio.getAttribute(Columns.CNV_COD_VERBA_REF) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA_REF).toString() : "";
                        final String cnvCodVerbaFerias = convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS).toString() : "";
                        final Convenio convenioCriado = criaConvenio(orgCodigo, svcCodigo, csaCodigo, cnvIdent, cnvDescricao, cnvCodVerba, cnvCodVerbaRef, cnvCodVerbaFerias, cnvCodVerbaFerias, responsavel);
                        // se copia bloqueio de verbas
                        if (!TextHelper.isNull(bloqueioCnvCopia) && CodedValues.TPC_SIM.equals(bloqueioCnvCopia)) {
                            parametroController.copiaParamCnvRse(convenio.getAttribute(Columns.CNV_CODIGO).toString(), convenioCriado.getCnvCodigo(), responsavel);
                        }
                        batman.iterate();
                    }
                    // se copia parâmetros de csa
                    if (!TextHelper.isNull(paramSvcCsaCopia) && CodedValues.TPC_SIM.equals(paramSvcCsaCopia)) {
                        parametroController.copiaParamSvcCsa(cnvCopia, svcCodigo, batman, responsavel);
                    }
                    // se copia bloqueio de serviços
                    if (!TextHelper.isNull(bloqueioSvcCopia) && CodedValues.TPC_SIM.equals(bloqueioSvcCopia)) {
                        parametroController.copiaParamSvcRse(cnvCopia, svcCodigo, batman, responsavel);
                    }
                } catch (final RuntimeException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
            final LogDelegate log = new LogDelegate(responsavel, Log.SERVICO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setServico(svcCodigo);
            if (!TextHelper.isNull(nseCodigo)) {
                log.setNaturezaServico(nseCodigo);
            }
            if (!TextHelper.isNull(servico.getSvcTgsCodigo())) {
                log.setGrupoServico(servico.getSvcTgsCodigo());
            }
            log.getUpdatedFields(servico.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ConvenioControllerException excecao = new ConvenioControllerException("mensagem.erro.nao.possivel.criar.servico.erro.interno", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new ConvenioControllerException("mensagem.erro.nao.possivel.criar.servico.existe.outro.mesmo.codigo", responsavel);
            }
            throw excecao;
        } catch (FindException | ParametroControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.servico.erro.copiar.parametros.servico", responsavel, ex);
        }
        return svcCodigo;
    }

    @Override
    public void updateServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        updateServico(servico, null, null, null, responsavel);
    }

    @Override
    public void updateServico(ServicoTransferObject servico, List<ParamSvcCseTO> listaParamSvcCse, List<ParamTarifCseTO> listaParamTarifCse, Map<String, List<String>> relacionamentos, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final Servico servicoBean = findServicoBean(servico, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.SERVICO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setServico(servicoBean.getSvcCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            final ServicoTransferObject servicoCache = setServicoValues(servicoBean);
            final CustomTransferObject merge = log.getUpdatedFields(servico.getAtributos(), servicoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.SVC_IDENTIFICADOR)) {

                // Verifica se não existe outro serviço com o mesmo ID
                final ServicoTransferObject teste = new ServicoTransferObject();
                teste.setSvcIdentificador((String) merge.getAttribute(Columns.SVC_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findServicoBean(teste, responsavel);
                    existe = true;
                } catch (final ConvenioControllerException ex) {
                }
                if (existe) {
                    throw new ConvenioControllerException("mensagem.erro.nao.possivel.alterar.servico.existe.outro.mesmo.codigo", responsavel);
                }
                servicoBean.setSvcIdentificador((String) merge.getAttribute(Columns.SVC_IDENTIFICADOR));
            }

            if ((responsavel != null) && responsavel.isSup() && merge.getAtributos().containsKey(Columns.SVC_OBS)) {
                servicoBean.setSvcObs((String) merge.getAttribute(Columns.SVC_OBS));
            }
            if (merge.getAtributos().containsKey(Columns.SVC_DESCRICAO)) {
                servicoBean.setSvcDescricao((String) merge.getAttribute(Columns.SVC_DESCRICAO));
            }
            if (merge.getAtributos().containsKey(Columns.SVC_ATIVO)) {
                servicoBean.setSvcAtivo((Short) merge.getAttribute(Columns.SVC_ATIVO));

                if (FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_SERVICOS, responsavel)) {
                    // Se exige motivo de operação no bloqueio/desbloqueio, verifica se foi informado, e grava ocorrência
                    final String tmoCodigo = servico.getTmoCodigo();

                    if (!TextHelper.isNull(tmoCodigo)) {
                        StringBuilder oseObs = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ose.obs.alteracao.servico", responsavel));
                        if (!TextHelper.isNull(servico.getOseObs())) {
                            oseObs.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, servico.getOseObs()));
                        }

                        // Cria ocorrência de bloqueio/desbloqueio de serviço
                        final String tocCodigo = (CodedValues.STS_ATIVO.equals(merge.getAttribute(Columns.SVC_ATIVO)) ? CodedValues.TOC_DESBLOQUEIO_SERVICO : CodedValues.TOC_BLOQUEIO_SERVICO);
                        OcorrenciaServicoHome.create(servicoBean.getSvcCodigo(), tocCodigo, tmoCodigo, oseObs.toString(), responsavel);
                    } else {
                        throw new ConvenioControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
                    }
                }
            }
            if (merge.getAtributos().containsKey(Columns.SVC_TGS_CODIGO)) {
                TipoGrupoSvc grupoSvc = null;
                try {
                    grupoSvc = TipoGrupoSvcHome.findByPrimaryKey((String) merge.getAttribute(Columns.SVC_TGS_CODIGO));
                } catch (final FindException e) {
                }
                servicoBean.setTipoGrupoSvc(grupoSvc);
                if (!TextHelper.isNull(merge.getAttribute(Columns.SVC_TGS_CODIGO))) {
                    log.setGrupoServico((String) merge.getAttribute(Columns.SVC_TGS_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.SVC_NSE_CODIGO)) {
                NaturezaServico naturezaSvc = null;
                try {
                    naturezaSvc = NaturezaServicoHome.findByPrimaryKey((String) merge.getAttribute(Columns.SVC_NSE_CODIGO));
                } catch (final FindException e) {
                }
                servicoBean.setNaturezaServico(naturezaSvc);
                if (!TextHelper.isNull(merge.getAttribute(Columns.SVC_NSE_CODIGO))) {
                    log.setNaturezaServico((String) merge.getAttribute(Columns.SVC_NSE_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.SVC_PRIORIDADE)) {
                String svcPrioridade = null;
                try {
                    svcPrioridade = ((Integer) merge.getAttribute(Columns.SVC_PRIORIDADE)).toString();
                } catch (final RuntimeException e) {
                }
                servicoBean.setSvcPrioridade(svcPrioridade);
            }

            AbstractEntityHome.update(servicoBean);

            // Cria o delegate para atualização dos parâmetros deste serviço
            // Altera parâmetros de serviço consignante
            if ((listaParamSvcCse != null) && !listaParamSvcCse.isEmpty()) {
                for (final ParamSvcCseTO paramSvcCseTO : listaParamSvcCse) {
                    parametroController.updateParamSvcCse(paramSvcCseTO, responsavel);
                }
            }

            // Altera parâmetros de tarifação consignante para o serviço
            if ((listaParamTarifCse != null) && !listaParamTarifCse.isEmpty()) {
                for (final ParamTarifCseTO paramTarifCseTO : listaParamTarifCse) {
                    parametroController.updateParamTarifCse(paramTarifCseTO, responsavel);
                }
            }

            // Insere relacionamentos de serviços
            if ((relacionamentos != null) && !relacionamentos.isEmpty()) {

                final HashSet<String> setTipoNaturezaNaturezaServico = new HashSet<>();
                if (servico.getSvcNseCodigo() != null) {
                    final ListaTipoNaturezaEditavelServicoQuery listaTipoNaturezaEditavelServicoQuery = new ListaTipoNaturezaEditavelServicoQuery();
                    listaTipoNaturezaEditavelServicoQuery.nseCodigo = servico.getSvcNseCodigo();
                    final List<TransferObject> tntSuportadas = listaTipoNaturezaEditavelServicoQuery.executarDTO();
                    for (final TransferObject transferObject : tntSuportadas) {
                        setTipoNaturezaNaturezaServico.add((String) transferObject.getAttribute(Columns.TNT_CODIGO));
                    }
                }

                for (final String tipoNatureza : relacionamentos.keySet()) {
                    final TipoNatureza natureza = TipoNaturezaHome.findByPrimaryKey(tipoNatureza);
                    boolean podeAlterar = true;
                    if (responsavel.isCse()) {
                        podeAlterar = natureza.getTntCseAltera() != null ? !CodedValues.TPC_NAO.equals(natureza.getTntCseAltera()) : false;
                    } else {
                        podeAlterar = natureza.getTntSupAltera() != null ? !CodedValues.TPC_NAO.equals(natureza.getTntSupAltera()) : false;
                    }

                    // Se o responsavel não tem permissão de alterar, vamos buscar no banco de dados a relação atual
                    // Nesse momento tambem verifico se a troca da natureza do serviço é compativel
                    List<String> svcCodigoDestino;
                    if (!podeAlterar && setTipoNaturezaNaturezaServico.contains(tipoNatureza)) {
                        final ListaRelacionamentosServicoQuery listaRelacionamentosServicoQuery = new ListaRelacionamentosServicoQuery();
                        listaRelacionamentosServicoQuery.tntCodigo = tipoNatureza;
                        listaRelacionamentosServicoQuery.svcCodigoOrigem = servicoBean.getSvcCodigo();
                        final List<TransferObject> listaRelacionamentosTntAtual = listaRelacionamentosServicoQuery.executarDTO();
                        svcCodigoDestino = new ArrayList<>();
                        for (final TransferObject transferObject : listaRelacionamentosTntAtual) {
                            svcCodigoDestino.add(transferObject.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString());
                        }
                        podeAlterar = true;
                    } else {
                        svcCodigoDestino = relacionamentos.get(tipoNatureza);
                    }

                    parametroController.inserirRelacionamento(tipoNatureza, servico.getSvcCodigo(), svcCodigoDestino, responsavel);
                }
            }

            log.write();
        } catch (UpdateException | CreateException | FindException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ParametroControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException(ex);
        } catch (final ConvenioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public void removeServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final Servico servicoBean = findServicoBean(servico, responsavel);
            final String svcCodigo = servicoBean.getSvcCodigo();
            AbstractEntityHome.remove(servicoBean);
            final LogDelegate log = new LogDelegate(responsavel, Log.SERVICO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setServico(svcCodigo);
            log.getUpdatedFields(servico.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.excluir.servico.selecionado.pois.possui.dependentes", responsavel);
        }
    }

    @Override
    public void copiaServico(String svcCopia, ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final BatchManager batman = new BatchManager(SessionUtil.getSession());
            parametroController.copiaParamSvc(svcCopia, servico.getSvcCodigo(), batman, responsavel);

            final Servico copia = ServicoHome.findByPrimaryKey(svcCopia);
            final TipoGrupoSvc grupo = copia.getTipoGrupoSvc();

            if (grupo != null) {
                servico.setSvcTgsCodigo(grupo.getTgsCodigo());
            } else {
                servico.setSvcTgsCodigo(null);
            }

            updateServico(servico, responsavel);

        } catch (ParametroControllerException | ConvenioControllerException | FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.copiar.parametros.servico", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicos(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        return lstServicos(criterio, -1, -1, false, responsavel);
    }

    @Override
    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException {
        return lstServicos(criterio, offset, count, false, responsavel);
    }

    @Override
    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, boolean orderByList, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaServicoQuery query = new ListaServicoQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (orderByList) {
                query.orderByList = true;
            }

            if (criterio != null) {
                query.svcAtivo = (Short) criterio.getAttribute(Columns.SVC_ATIVO);
                query.svcDescricao = (String) criterio.getAttribute(Columns.SVC_DESCRICAO);
                query.svcIdentificador = (String) criterio.getAttribute(Columns.SVC_IDENTIFICADOR);
                query.tgsCodigo = (String) criterio.getAttribute(Columns.SVC_TGS_CODIGO);
                query.nseCodigo = (String) criterio.getAttribute(Columns.SVC_NSE_CODIGO);
                final Object paramSvcCodigo = criterio.getAttribute(Columns.SVC_CODIGO);
                if (paramSvcCodigo instanceof String) {
                    query.svcCodigo = (String) paramSvcCodigo;
                } else if (paramSvcCodigo instanceof List<?>) {
                    query.svcCodigos = (List<String>) paramSvcCodigo;
                }
                query.marCodigos = (List<String>) criterio.getAttribute(Columns.MAR_CODIGO);
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        return lstConsignatarias(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstConsignatarias(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConsignatariaQuery query = new ListaConsignatariaQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.csaNome = (String) criterio.getAttribute(Columns.CSA_NOME);
                query.csaIdentificador = (String) criterio.getAttribute(Columns.CSA_IDENTIFICADOR);
                final Object paramCsaCodigo = criterio.getAttribute(Columns.CSA_CODIGO);
                if (paramCsaCodigo instanceof String) {
                    query.csaCodigo = (String) paramCsaCodigo;
                } else if (paramCsaCodigo instanceof List<?>) {
                    query.csaCodigos = (List<String>) paramCsaCodigo;
                }
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public int countServicos(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaServicoQuery query = new ListaServicoQuery();
            query.count = true;

            if (criterio != null) {
                query.svcAtivo = (Short) criterio.getAttribute(Columns.SVC_ATIVO);
                query.svcDescricao = (String) criterio.getAttribute(Columns.SVC_DESCRICAO);
                query.svcIdentificador = (String) criterio.getAttribute(Columns.SVC_IDENTIFICADOR);
                query.tgsCodigo = (String) criterio.getAttribute(Columns.SVC_TGS_CODIGO);
                final Object paramSvcCodigo = criterio.getAttribute(Columns.SVC_CODIGO);
                if (paramSvcCodigo instanceof String) {
                    query.svcCodigo = (String) paramSvcCodigo;
                } else if (paramSvcCodigo instanceof List<?>) {
                    query.svcCodigos = (List<String>) paramSvcCodigo;
                }
            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listCnvCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaStatusConvenioCorrespondenteQuery query = new ListaStatusConvenioCorrespondenteQuery();
            query.corCodigo = corCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listCnvCorrespondenteByCsa(String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaStatusConvenioCorrespondenteByCsaQuery query = new ListaStatusConvenioCorrespondenteByCsaQuery();
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public Map<String, List<String>> getCorrespondentePorCnvCodVerba(String csaCodigo, boolean filtraPorCnvCodVerbaRef, boolean filtraPorCnvCodVerbaFerias, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioCorrespondenteByCsaQuery query = new ListaConvenioCorrespondenteByCsaQuery();
            query.csaCodigo = csaCodigo;
            query.filtraPorCnvCodVerbaRef = filtraPorCnvCodVerbaRef;
            query.filtraPorCnvCodVerbaFerias = filtraPorCnvCodVerbaFerias;

            final List<TransferObject> lista = query.executarDTO();

            final Map<String, List<String>> convenios = new HashMap<>();

            if (lista != null) {
                for (final TransferObject to : lista) {
                    final String cnvCodVerba = to.getAttribute(Columns.CNV_COD_VERBA).toString();

                    List<String> correspondentes = convenios.get(cnvCodVerba);
                    if (correspondentes == null) {
                        correspondentes = new ArrayList<>();
                        convenios.put(cnvCodVerba, correspondentes);
                    }

                    correspondentes.add(to.getAttribute(Columns.COR_CODIGO).toString());
                }
            }

            return convenios;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listOrgCnvCorrespondente(String corCodigo, String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaOrgaoConvenioCorrespondenteQuery query = new ListaOrgaoConvenioCorrespondenteQuery();
            query.corCodigo = corCodigo;
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listCorCnvOrgao(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaCorrespondenteConvenioOrgaoQuery query = new ListaCorrespondenteConvenioOrgaoQuery();
            query.cnvCodigo = cnvCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    /**
     * Cria convênio para o correspondente especificado por parâmetro
     * para os convênios selecionados, presentes na lista cnvCodigos.
     * @param corCodigo
     * @param svcCodigo
     * @param cnvCodigos
     * @param responsavel
     * @throws ConvenioControllerException
     */
    @Override
    public void criaConvenioCorrespondente(String corCodigo, String svcCodigo, List<String> cnvCodigos, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final List<String> cnvCodigosDestino = new ArrayList<>();
            if (cnvCodigos != null) {
                cnvCodigosDestino.addAll(cnvCodigos);
            }

            CorrespondenteConvenio crcBean = null;

            final List<CorrespondenteConvenio> crc = CorrespondenteConvenioHome.findByCorSvcCodigo(corCodigo, svcCodigo);
            final Iterator<CorrespondenteConvenio> it = crc.iterator();
            String cnvCodigo = null;
            final StatusConvenio statusAtivo = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_ATIVO);

            while (it.hasNext()) {
                crcBean = it.next();
                cnvCodigo = crcBean.getConvenio().getCnvCodigo();
                if ((cnvCodigos != null) && cnvCodigos.contains(cnvCodigo)) {
                    // Se existe e deve continuar, então altera o status para ativo
                    crcBean.setStatusConvenio(statusAtivo);
                    // Remove da lista de cnvCodigos
                    cnvCodigosDestino.remove(cnvCodigo);
                } else {
                    // Se não existe na lista, ou a lista é nula, então altera o status para inativo
                    crcBean.setStatusConvenio(StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_INATIVO));
                }
                AbstractEntityHome.update(crcBean);
            }

            // Os códigos de convênio que sobraram na lista cnvCodigos são os que devem ser criados
            if ((cnvCodigosDestino != null) && (cnvCodigosDestino.size() > 0)) {
                final Iterator<String> it2 = cnvCodigosDestino.iterator();
                cnvCodigo = null;
                while (it2.hasNext()) {
                    cnvCodigo = it2.next();
                    CorrespondenteConvenioHome.create(corCodigo, cnvCodigo, CodedValues.SCV_ATIVO);
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCorrespondente(corCodigo);
            if ((cnvCodigos != null) && !cnvCodigos.isEmpty()) {
                log.add(Columns.CNV_COD_VERBA, cnvCodigos, ConvenioHome.class);
            }
            log.write();
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.convenio.erro.interno", responsavel, ex.getMessage());
        } catch (UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Cria convênio para os correspondentes especificados por parâmetro para o convênio informado.
     * @param corCodigos
     * @param cnvCodigo
     * @param responsavel
     * @throws ConvenioControllerException
     */
    @Override
    public void criaConvenioCorrespondente(List<String> corCodigos, String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final List<String> corCodigosDestino = new ArrayList<>();
            if (!TextHelper.isNull(corCodigos)) {
                corCodigosDestino.addAll(corCodigos);
            }

            CorrespondenteConvenio crcBean = null;
            final Collection<CorrespondenteConvenio> crc = CorrespondenteConvenioHome.findByCnvCodigo(cnvCodigo);
            final Iterator<CorrespondenteConvenio> it = crc.iterator();
            String corCodigo = null;
            final StatusConvenio statusAtivo = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_ATIVO);

            while (it.hasNext()) {
                crcBean = it.next();
                corCodigo = crcBean.getCorrespondente().getCorCodigo();
                if ((corCodigos != null) && corCodigos.contains(corCodigo)) {
                    // Se existe e deve continuar, então altera o status para ativo
                    crcBean.setStatusConvenio(statusAtivo);
                    // Remove da lista de corCodigos
                    corCodigosDestino.remove(corCodigo);
                } else {
                    // Se não existe na lista, ou a lista é nula, então altera o status para inativo
                    crcBean.setStatusConvenio(StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_INATIVO));
                }
                AbstractEntityHome.update(crcBean);
            }

            // Os códigos de convênio que sobraram na lista cnvCodigos são os que devem ser criados
            Iterator<String> ite = null;
            if ((corCodigosDestino != null) && (corCodigosDestino.size() > 0)) {
                ite = corCodigosDestino.iterator();
                corCodigo = null;
                while (ite.hasNext()) {
                    corCodigo = ite.next();
                    CorrespondenteConvenioHome.create(corCodigo, cnvCodigo, CodedValues.SCV_ATIVO);
                }
            }

            if ((corCodigos != null) && (corCodigos.size() > 0)) {
                ite = corCodigos.iterator();
                corCodigo = null;
                while (ite.hasNext()) {
                    corCodigo = ite.next();
                    final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setCorrespondente(corCodigo);
                    log.setConvenio(cnvCodigo);
                    log.write();
                }
            }
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.criar.convenio.erro.interno", responsavel, ex.getMessage());
        } catch (UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Cria um convênio para o correspondente especificado por parâmetro usando hibernate.
     * IMPORTANTE: Não verifica se o convênio já existe para o correspondente. Prioriza eficiência.
     * @param corCodigo Correspondente.
     * @param cnvCodigo Convenio.
     * @throws ConvenioControllerException
     */
    private void criaConvenioCorrespondente(String corCodigo, String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            CorrespondenteConvenioHome.create(corCodigo, cnvCodigo, CodedValues.SCV_ATIVO);
            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCorrespondente(corCodigo);
            log.setConvenio(cnvCodigo);
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCsaCodVerba(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaCodigoVerbaCsaQuery query = new ListaCodigoVerbaCsaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * recupera todos convênios distintos de uma consignatária com seus códigos de verba, incluídos
     * os de referência e os de férias
     * @param csaCodigo
     * @param incluiCnvBloqueados - define se recupera também os convênios bloqueados
     * @param responsavel
     * @return
     * @throws ConvenioControllerException
     */
    @Override
    public List<TransferObject> recuperaCsaCodVerba(String csaCodigo, boolean incluiCnvBloqueados, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final RecuperaCodVerbasCsaQuery query = new RecuperaCodVerbasCsaQuery();
            query.incluiCnvBloqueados = incluiCnvBloqueados;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCsaCodVerbaReajuste(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final List<String> pseVlrs = new ArrayList<>();
            pseVlrs.add(CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO);
            pseVlrs.add(CodedValues.PERMITE_AUMENTAR_APENAS_VLR_CONTRATO);

            final ListaCodigoVerbaByCsaQuery query = new ListaCodigoVerbaByCsaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCnvEntidade(String codEntidade, String tipoEntidade, String tipo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioEntidadeQuery query = new ListaConvenioEntidadeQuery();
            query.codigoEntidade = codEntidade;
            query.tipoEntidade = tipoEntidade;
            query.operacao = tipo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countCnvBySvcCodigo(String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioSvcQuery query = new ListaConvenioSvcQuery();
            query.count = true;
            query.svcCodigo = svcCodigo;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCnvBySvcCodigo(String svcCodigo, String cnvCodVerba, int offset, int size, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioSvcQuery query = new ListaConvenioSvcQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (size != -1) {
                query.maxResults = size;
            }
            query.cnvCodVerba = cnvCodVerba;
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna lista com o nome de consignatárias que compartilham o mesmo
     * código de verba dado.
     * @param codVerba
     * @param csaCodigo
     * @param svcCodigo
     * @return
     * @throws ConvenioControllerException
     */
    @Override
    public List<String> csaPorCodVerba(String codVerba, String csaCodigo) throws ConvenioControllerException {
        try {
            final ListaConsignatariaMesmaVerbaQuery query = new ListaConsignatariaMesmaVerbaQuery();
            query.cnvCodVerba = codVerba;
            query.csaCodigo = csaCodigo;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    /**
     * Bloqueia todos os convênios do correspondente que estão ativos,
     * independente do status do convênio da consignatária.
     * @param corCodigo
     * @param responsavel
     * @throws ConvenioControllerException
     */
    @Override
    public void bloqueiaCnvCor(String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioCorrespondenteBloquearQuery queryBloquear = new ListaConvenioCorrespondenteBloquearQuery();
            queryBloquear.corCodigo = corCodigo;
            final List<String> cnvCodigosBloquear = queryBloquear.executarLista();

            if ((cnvCodigosBloquear != null) && (cnvCodigosBloquear.size() > 0)) {
                final Iterator<String> it = cnvCodigosBloquear.iterator();
                String cnvCodigo = null;
                CorrespondenteConvenioId id = null;
                CorrespondenteConvenio crcBean = null;
                final StatusConvenio statusInativo = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_INATIVO);

                while (it.hasNext()) {
                    cnvCodigo = it.next();
                    id = new CorrespondenteConvenioId(corCodigo, cnvCodigo);
                    crcBean = CorrespondenteConvenioHome.findByPrimaryKey(id);
                    crcBean.setStatusConvenio(statusInativo);
                    AbstractEntityHome.update(crcBean);
                }

                final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setCorrespondente(corCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.bloquear.todos.convenios", responsavel));
                log.write();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     * Desbloqueia todos os convênios ativos da consignatária para este correspondente.
     * Inclui os registros que não existem, e altera o status para ativo dos que
     * já existem e estão inativos.
     * @param corCodigo
     * @param responsavel
     * @throws ConvenioControllerException
     */
    @Override
    public void desBloqueiaCnvCor(String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioCorrespondenteIncluirQuery queryInclusao = new ListaConvenioCorrespondenteIncluirQuery();
            queryInclusao.corCodigo = corCodigo;
            final List<String> cnvCodigosInclusao = queryInclusao.executarLista();

            final ListaConvenioCorrespondenteDesbloquearQuery queryDesbloquear = new ListaConvenioCorrespondenteDesbloquearQuery();
            queryDesbloquear.corCodigo = corCodigo;
            final List<String> cnvCodigosDesbloquear = queryDesbloquear.executarLista();

            if ((cnvCodigosInclusao != null) && (cnvCodigosInclusao.size() > 0)) {
                final Iterator<String> it = cnvCodigosInclusao.iterator();
                String cnvCodigo = null;
                while (it.hasNext()) {
                    cnvCodigo = it.next();
                    CorrespondenteConvenioHome.create(corCodigo, cnvCodigo, CodedValues.SCV_ATIVO);
                }
            }

            if ((cnvCodigosDesbloquear != null) && (cnvCodigosDesbloquear.size() > 0)) {
                final Iterator<String> it = cnvCodigosDesbloquear.iterator();
                String cnvCodigo = null;
                CorrespondenteConvenioId id = null;
                CorrespondenteConvenio crcBean = null;
                final StatusConvenio statusAtivo = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_ATIVO);

                while (it.hasNext()) {
                    cnvCodigo = it.next();
                    id = new CorrespondenteConvenioId(corCodigo, cnvCodigo);
                    crcBean = CorrespondenteConvenioHome.findByPrimaryKey(id);
                    crcBean.setStatusConvenio(statusAtivo);
                    AbstractEntityHome.update(crcBean);
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE_CONVENIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCorrespondente(corCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.desbloquear.todos.convenios", responsavel));
            log.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     * A partir da listagem de convênios "todosConvenios" verifica quais devem ser bloqueados,
     * quais devem ser desbloqueados e quais devem ser criados.
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param todosConvenios
     * @param tmoCodigo
     * @param ocoObs
     * @param responsavel
     * @return
     * @throws ConvenioControllerException
     */
    @Override
    public List<List<String>> createConvenios(String svcCodigo, String csaCodigo, String orgCodigo, List<TransferObject> todosConvenios, String tmoCodigo, String ocoObs, AcessoSistema responsavel) throws ConvenioControllerException {
        final List<List<String>> convenios = new ArrayList<>();

        final List<TransferObject> cnvCriados = new ArrayList<>();
        final List<String> codigosBloqueados = new ArrayList<>();
        final List<String> codigosBloqueadosCodVerbaNull = new ArrayList<>();
        final List<Object> conveniosBloqueados = new ArrayList<>();

        if ((todosConvenios != null) && !todosConvenios.isEmpty()) {
            for (final TransferObject customTransferObject : todosConvenios) {
                final TransferObject cnv = customTransferObject;
                final String novoStatus = (String) cnv.getAttribute("NOVO_STATUS");
                final boolean limpaVerba = (TextHelper.isNull(cnv.getAttribute(Columns.CNV_COD_VERBA))) == true;

                if (CodedValues.SCV_ATIVO.equals(novoStatus)) {
                    cnvCriados.add(cnv);
                } else if (CodedValues.SCV_INATIVO.equals(novoStatus) && limpaVerba) {
                    codigosBloqueadosCodVerbaNull.add((String) getCodEntidadeValue(csaCodigo, cnv));
                    conveniosBloqueados.add(cnv.getAttribute(Columns.CNV_CODIGO));
                } else if (CodedValues.SCV_INATIVO.equals(novoStatus) && !limpaVerba) {
                    codigosBloqueados.add((String) getCodEntidadeValue(csaCodigo, cnv));
                    conveniosBloqueados.add(cnv.getAttribute(Columns.CNV_CODIGO));
                }
            }
        }

        // Parâmetro de sistema que define se códigos de verba ligados a contratos ativos podem ser alterados.
        final boolean modificaCodVerbaComAdeAtivo = ParamSist.paramEquals(CodedValues.TPC_ALTERA_COD_VERBA_COM_ADE_ATIVOS, CodedValues.TPC_SIM, responsavel);

        try {
            if (!modificaCodVerbaComAdeAtivo) {
                final List<String> entModificadas = new ArrayList<>();

                final ListaConveniosQuery lstCnvQuery = new ListaConveniosQuery();
                lstCnvQuery.svcCodigo = svcCodigo;
                lstCnvQuery.csaCodigo = csaCodigo;
                lstCnvQuery.orgCodigo = orgCodigo;

                // Compara códigos de verba na base com os entrados pelo usuário
                // separa os que foram modificados em relação à base
                final List<TransferObject> cnvsNaBase = lstCnvQuery.executarDTO();
                outerLoop: for (final TransferObject cnvNaBase : cnvsNaBase) {
                    final String codVerbaBase = (String) cnvNaBase.getAttribute(Columns.CNV_COD_VERBA);

                    for (final TransferObject cnv : todosConvenios) {
                        if (cnvNaBase.getAttribute(Columns.CNV_CODIGO).equals(cnv.getAttribute(Columns.CNV_CODIGO))) {
                            if (((codVerbaBase == null) && (cnv.getAttribute(Columns.CNV_COD_VERBA) != null)) || ((codVerbaBase != null) && !codVerbaBase.equals(cnv.getAttribute(Columns.CNV_COD_VERBA)))) {
                                if (!TextHelper.isNull(csaCodigo)) {
                                    entModificadas.add(cnv.getAttribute(Columns.ORG_CODIGO).toString());
                                } else {
                                    entModificadas.add(cnv.getAttribute(Columns.CSA_CODIGO).toString());
                                }
                            }
                            continue outerLoop;
                        }
                    }
                }

                // Confere se os convênios cujo código de verba foram alterados possuem ou não contratos ativos
                if (!entModificadas.isEmpty()) {
                    final ListaConveniosComAdeAtivosQuery cnvComAdeAtivos = new ListaConveniosComAdeAtivosQuery();
                    cnvComAdeAtivos.csaCodigo = csaCodigo;
                    cnvComAdeAtivos.orgCodigo = orgCodigo;
                    cnvComAdeAtivos.svcCodigo = svcCodigo;
                    cnvComAdeAtivos.codigos = entModificadas;

                    final List<TransferObject> codigosImpedidos = cnvComAdeAtivos.executarDTO();

                    if (!codigosImpedidos.isEmpty()) {
                        throw new ConvenioControllerException("mensagem.erro.nao.possivel.alterar.codigos.verba.ligados.contratos.ativos", responsavel);
                    }
                }
            }

            if (!codigosBloqueadosCodVerbaNull.isEmpty()) {
                List<String> codigosImpedidos = null;
                try {
                    final ListaConveniosComAdeAtivosQuery cnvComAdeAtivos = new ListaConveniosComAdeAtivosQuery();
                    cnvComAdeAtivos.csaCodigo = csaCodigo;
                    cnvComAdeAtivos.orgCodigo = orgCodigo;
                    cnvComAdeAtivos.svcCodigo = svcCodigo;
                    cnvComAdeAtivos.codigos = codigosBloqueadosCodVerbaNull;

                    codigosImpedidos = cnvComAdeAtivos.executarLista();
                } catch (final HQueryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConvenioControllerException(ex);
                }

                if ((codigosImpedidos != null) && !codigosImpedidos.isEmpty()) {
                    final List<Object> entNames = new ArrayList<>();

                    outerLoop: for (final String impCodigo : codigosImpedidos) {
                        for (final TransferObject toBlockCnv : todosConvenios) {
                            if (!TextHelper.isNull(csaCodigo)) {
                                if (((String) toBlockCnv.getAttribute(Columns.ORG_CODIGO)).equals(impCodigo)) {
                                    entNames.add(toBlockCnv.getAttribute(Columns.ORG_NOME));
                                    continue outerLoop;
                                }
                            } else if (!TextHelper.isNull(orgCodigo) && ((String) toBlockCnv.getAttribute(Columns.CSA_CODIGO)).equals(impCodigo)) {
                                entNames.add(toBlockCnv.getAttribute(Columns.CSA_NOME));
                                continue outerLoop;
                            }
                        }
                    }

                    String listaEntidades = new String();
                    if (entNames.size() < NUM_MAX_COD_VERBAS_A_MOSTRAR) {
                        listaEntidades = TextHelper.join(entNames, ",") + ".";
                    } else {
                        final List<Object> showList = new ArrayList<>();
                        for (int i = 0; i < NUM_MAX_COD_VERBAS_A_MOSTRAR; i++) {
                            showList.add(entNames.get(i));
                        }
                        listaEntidades = TextHelper.join(showList, ",") + "...";
                    }
                    throw new ConvenioControllerException((csaCodigo != null) ? "mensagem.erro.codigos.verba.seguintes.orgaos.nao.podem.ser.vazios.pois.ha.contratos.associados" : "mensagem.erro.codigos.verba.seguintes.consignatarias.nao.podem.ser.vazios.pois.ha.contratos.associados", responsavel, listaEntidades);
                }
            }

            // Bloqueia os convenios limpando os códigos de verba
            if (!codigosBloqueadosCodVerbaNull.isEmpty()) {
                setCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, CodedValues.SCV_INATIVO, true, codigosBloqueadosCodVerbaNull, tmoCodigo, ocoObs, responsavel);
            }

            if (!codigosBloqueados.isEmpty()) {
                setCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, CodedValues.SCV_INATIVO, false, codigosBloqueados, tmoCodigo, ocoObs, responsavel);
            }

            if ((todosConvenios == null) || todosConvenios.isEmpty()) {
                setCnvScvCodigo(svcCodigo, csaCodigo, orgCodigo, CodedValues.SCV_INATIVO, false, null, tmoCodigo, ocoObs, responsavel);
            }

            if ((cnvCriados != null) && !cnvCriados.isEmpty()) {
                for (int i = 0; i < cnvCriados.size(); i++) {
                    final String codVerba = (String) (cnvCriados.get(i)).getAttribute(Columns.CNV_COD_VERBA);
                    final String codVerbaRef = (String) (cnvCriados.get(i)).getAttribute(Columns.CNV_COD_VERBA_REF);
                    final String codVerbaFerias = (String) (cnvCriados.get(i)).getAttribute(Columns.CNV_COD_VERBA_FERIAS);
                    final String codVerbaDirf = (String) (cnvCriados.get(i)).getAttribute(Columns.CNV_COD_VERBA_DIRF);

                    if ((csaCodigo != null) && !"".equals(csaCodigo)) {
                        final String orgCodigoTO = (String) (cnvCriados.get(i)).getAttribute(Columns.ORG_CODIGO);
                        convenios.add(createConvenio(svcCodigo, csaCodigo, orgCodigoTO, codVerba, codVerbaRef, codVerbaFerias, codVerbaDirf, tmoCodigo, ocoObs, responsavel));
                    } else if ((orgCodigo != null) && !"".equals(orgCodigo)) {
                        final String csaCodigoTO = (String) (cnvCriados.get(i)).getAttribute(Columns.CSA_CODIGO);
                        convenios.add(createConvenio(svcCodigo, csaCodigoTO, orgCodigo, codVerba, codVerbaRef, codVerbaFerias, codVerbaDirf, tmoCodigo, ocoObs, responsavel));
                    }
                }
            }

            // Os convênios correspondentes dos convênios bloqueados também devem ser bloqueados
            final ListaConvenioConsignatariaBloquearQuery query = new ListaConvenioConsignatariaBloquearQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            final List<TransferObject> cnvCorBloqueio = query.executarDTO();
            if ((cnvCorBloqueio != null) && (cnvCorBloqueio.size() > 0)) {
                final Iterator<TransferObject> itBloqueioCnvCor = cnvCorBloqueio.iterator();
                TransferObject next = null;

                CorrespondenteConvenioId id = null;
                CorrespondenteConvenio crcBean = null;
                final StatusConvenio statusInativo = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_INATIVO);

                while (itBloqueioCnvCor.hasNext()) {
                    next = itBloqueioCnvCor.next();

                    id = new CorrespondenteConvenioId((String) next.getAttribute(Columns.COR_CODIGO), (String) next.getAttribute(Columns.CNV_CODIGO));
                    crcBean = CorrespondenteConvenioHome.findByPrimaryKey(id);
                    crcBean.setStatusConvenio(statusInativo);
                    AbstractEntityHome.update(crcBean);
                }
            }

            // Gera logs de convênios bloqueados
            for (final Object element : conveniosBloqueados) {
                final String cnvCodigo = (String) element;

                final LogDelegate log = new LogDelegate(responsavel, Log.CONVENIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConvenio(cnvCodigo);
                log.setStatusConvenio(CodedValues.SCV_INATIVO);
                log.write();
            }
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.bloquear.convenios.correspondentes.necessarios", responsavel, ex);
        } catch (UpdateException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException logEx) {
            LOG.error(logEx.getMessage(), logEx);
            throw new ConvenioControllerException(logEx);
        }

        return convenios;
    }

    private Object getCodEntidadeValue(String csaCodigo, TransferObject cnv) {
        return (!TextHelper.isNull(csaCodigo)) ? cnv.getAttribute(Columns.ORG_CODIGO) : cnv.getAttribute(Columns.CSA_CODIGO);
    }

    @Override
    public void criaConveniosParaNovoOrgao(String orgCodigo, String estCodigo, String orgCopiado, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            String codOrg = "";
            // Se não foi escolhido nenhum órgão para cópia de seus convênios, escolhe-se um órgão qualquer do mesmo estabelecimento
            if ((orgCopiado == null) || "".equals(orgCopiado)) {
                // Lista o órgão do mesmo estabelecimento com maior número de convênios ativos
                try {
                    final ObtemOrgaoMaxCnvAtivoQuery query = new ObtemOrgaoMaxCnvAtivoQuery();
                    query.estCodigo = estCodigo;
                    final List<?> result = query.executarLista();
                    codOrg = (result != null) && (result.size() > 0) ? (String) result.get(0) : null;
                    if (codOrg == null) {
                        throw new ConvenioControllerException("mensagem.erro.nao.existe.orgao.com.convenio.ativo.cadastrado", responsavel);
                    }
                } catch (final HQueryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConvenioControllerException(ex);
                }
            } else {
                codOrg = orgCopiado;
            }

            // Lista das consignatárias que não permitem a cópia de convênio dos correspondentes
            final List<String> listaCsaNaoPermite = new ArrayList<>();
            final List<TransferObject> paramCsa = parametroController.selectParamCsa(null, CodedValues.TPA_PERMITE_COPIA_CONVENIO_CORRESPONDENTE, responsavel);
            for (final TransferObject cto : paramCsa) {
                if ((cto != null) && (cto.getAttribute(Columns.PCS_VLR) != null) && "N".equals(cto.getAttribute(Columns.PCS_VLR).toString())) {
                    listaCsaNaoPermite.add(cto.getAttribute(Columns.PCS_CSA_CODIGO).toString());
                }
            }

            // Mapeamento dos convênios equivalentes entre o órgão copiado e o órgão recém criado
            final Map<String, String> mapConvenios = new HashMap<>(0);

            // Lista todos os convênios ativos
            final List<TransferObject> convenios = lstConvenios(null, null, null, codOrg, true, responsavel);
            final Iterator<TransferObject> it = convenios.iterator();
            TransferObject convenio = null;
            while (it.hasNext()) {
                convenio = it.next();
                final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO) != null ? convenio.getAttribute(Columns.CNV_CODIGO).toString() : "";
                final String svcCodigo = convenio.getAttribute(Columns.CNV_SVC_CODIGO) != null ? convenio.getAttribute(Columns.CNV_SVC_CODIGO).toString() : "";
                final String csaCodigo = convenio.getAttribute(Columns.CNV_CSA_CODIGO) != null ? convenio.getAttribute(Columns.CNV_CSA_CODIGO).toString() : "";
                final String cnvIdent = convenio.getAttribute(Columns.CNV_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.CNV_IDENTIFICADOR).toString() : "";
                final String cnvDescricao = convenio.getAttribute(Columns.CNV_DESCRICAO) != null ? convenio.getAttribute(Columns.CNV_DESCRICAO).toString() : "";
                final String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                final String cnvCodVerbaRef = convenio.getAttribute(Columns.CNV_COD_VERBA_REF) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA_REF).toString() : "";
                final String cnvCodVerbaFerias = convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS).toString() : "";
                final String cnvCodVerbaDirf = convenio.getAttribute(Columns.CNV_COD_VERBA_DIRF) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA_DIRF).toString() : "";
                final Convenio convenioCriado = criaConvenio(orgCodigo, svcCodigo, csaCodigo, cnvIdent, cnvDescricao, cnvCodVerba, cnvCodVerbaRef, cnvCodVerbaFerias, cnvCodVerbaDirf, responsavel);

                // Convênio do órgão copiado equivalente ao convênio do órgão criado
                mapConvenios.put(cnvCodigo, convenioCriado.getCnvCodigo());
            }

            // Lista todos os convênios ativos de correspondentes
            final List<TransferObject> conveniosCorrespondente = lstConveniosCorrespondente(null, null, null, codOrg, true, responsavel);
            final Iterator<TransferObject> itConveniosCorrespondente = conveniosCorrespondente.iterator();
            TransferObject convenioCorrespondente = null;
            while (itConveniosCorrespondente.hasNext()) {
                convenioCorrespondente = itConveniosCorrespondente.next();
                final String csaCodigo = convenioCorrespondente.getAttribute(Columns.CNV_CSA_CODIGO) != null ? convenioCorrespondente.getAttribute(Columns.CNV_CSA_CODIGO).toString() : "";

                // Verifica se a csa deste convênio não está na lista das que não permitem cópia
                if (!listaCsaNaoPermite.contains(csaCodigo)) {
                    final String cnvCodigo = convenioCorrespondente.getAttribute(Columns.CNV_CODIGO) != null ? convenioCorrespondente.getAttribute(Columns.CNV_CODIGO).toString() : "";
                    final String corCodigo = convenioCorrespondente.getAttribute(Columns.CRC_COR_CODIGO) != null ? convenioCorrespondente.getAttribute(Columns.CRC_COR_CODIGO).toString() : "";

                    // Chama método que cria convênio de correspondente sem verificar se ele já existe
                    criaConvenioCorrespondente(corCodigo, mapConvenios.get(cnvCodigo), responsavel);
                }
            }
        } catch (ConvenioControllerException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstCodEntidadesCnvNotInList(String csaCodigo, String orgCodigo, String svcCodigo, List<String> codigosAtivos, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaEntidadesCnvNotInListQuery entCnvNotInList = new ListaEntidadesCnvNotInListQuery();
            entCnvNotInList.csaCodigo = csaCodigo;
            entCnvNotInList.orgCodigo = orgCodigo;
            entCnvNotInList.svcCodigo = svcCodigo;
            entCnvNotInList.ignoredCodList = codigosAtivos;

            return entCnvNotInList.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public Map<String, String> getMapCnvCodVerbaRef() throws ConvenioControllerException {
        final Map<String, String> mapVerbaRef = new HashMap<>();

        try {
            final ListaConvenioCodVerbaRefQuery hibernate = new ListaConvenioCodVerbaRefQuery();
            final List<TransferObject> convenios = hibernate.executarDTO();
            if ((convenios != null) && (convenios.size() > 0)) {
                final Iterator<TransferObject> it = convenios.iterator();
                String codVerba, codVerbaRef;
                TransferObject next;
                while (it.hasNext()) {
                    next = it.next();
                    codVerba = (String) next.getAttribute(Columns.CNV_COD_VERBA);
                    codVerbaRef = (String) next.getAttribute(Columns.CNV_COD_VERBA_REF);
                    mapVerbaRef.put(codVerbaRef, codVerba);
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }

        return mapVerbaRef;
    }

    @Override
    public List<TransferObject> getCnvByIdentificadores(String csaIdentificador, String estIdentificador, String orgIdentificador, String svcIdentificador) throws ConvenioControllerException {
        try {
            final ListaConvenioPelosIdentificadoresQuery query = new ListaConvenioPelosIdentificadoresQuery();
            query.csaIdentificador = csaIdentificador;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.svcIdentificador = svcIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> getCnvByIdentificadores(String csaIdentificador, String estIdentificador, String orgIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioPelosIdentificadoresQuery query = new ListaConvenioPelosIdentificadoresQuery();
            query.csaIdentificador = csaIdentificador;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.svcIdentificador = svcIdentificador;
            query.cnvCodVerba = cnvCodVerba;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public void updateCnvCodVerba() throws ConvenioControllerException {
        try {
            //Atualiza o código de verba dos convênios que não possuem código cadastrado
            //baseado na verba cadastrada para outro convênio de mesma consignatária e mesmo serviço
            final ListaCnvsAtualizarVerbaByCnvQuery cnvByCnvQuery = new ListaCnvsAtualizarVerbaByCnvQuery();
            final List<TransferObject> cnvByCnv = cnvByCnvQuery.executarDTO();
            for (final TransferObject next : cnvByCnv) {
                final String cnvCodigo = (String) next.getAttribute(Columns.CNV_CODIGO);
                final String cnvCodVerba = (String) next.getAttribute(Columns.CNV_COD_VERBA);
                if (!TextHelper.isNull(cnvCodigo) && !TextHelper.isNull(cnvCodVerba)) {
                    try {
                        final Convenio cnv = ConvenioHome.findByPrimaryKey(cnvCodigo);
                        cnv.setCnvCodVerba(cnvCodVerba);

                        AbstractEntityHome.update(cnv);
                    } catch (final FindException e) {
                        LOG.error("Convênio '" + cnvCodigo + "' não encontrado, código de verba não foi atualizado.", e);
                    }
                }
            }

            //Atualiza o código de verba dos convênios que não possuem código cadastrado
            //baseado no identificador do serviço do convênio
            final ListaCnvsAtualizarVerbaBySvcQuery cnvBySvcQuery = new ListaCnvsAtualizarVerbaBySvcQuery();
            final List<TransferObject> cnvBySvc = cnvBySvcQuery.executarDTO();
            for (final TransferObject next : cnvBySvc) {
                final String cnvCodigo = (String) next.getAttribute(Columns.CNV_CODIGO);
                final String cnvCodVerba = (String) next.getAttribute(Columns.SVC_IDENTIFICADOR);
                if (!TextHelper.isNull(cnvCodigo) && !TextHelper.isNull(cnvCodVerba)) {
                    try {
                        final Convenio cnv = ConvenioHome.findByPrimaryKey(cnvCodigo);
                        cnv.setCnvCodVerba(cnvCodVerba);

                        AbstractEntityHome.update(cnv);
                    } catch (final FindException e) {
                        LOG.error("Convênio '" + cnvCodigo + "' não encontrado, código de verba não foi atualizado.", e);
                    }
                }
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.atualizar.codigos.verba.dos.convenios", (AcessoSistema) null, ex, ex.getMessage());
        } catch (final UpdateException ex) {
            LOG.error("Não foi possível atualizar os códigos de verba dos convênios. " + ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConvenioControllerException("mensagem.erro.nao.possivel.atualizar.codigos.verba.dos.convenios", (AcessoSistema) null, ex, ex.getMessage());
        }

    }

    @Override
    public Map<String, String> getMapCnvCodVerbaFerias() throws ConvenioControllerException {
        final Map<String, String> mapVerbaFerias = new HashMap<>();

        try {
            final ListaConvenioCodVerbaFeriasQuery hibernate = new ListaConvenioCodVerbaFeriasQuery();
            final List<TransferObject> convenios = hibernate.executarDTO();
            if ((convenios != null) && (convenios.size() > 0)) {
                final Iterator<TransferObject> it = convenios.iterator();
                String codVerba, codVerbaFerias;
                TransferObject next;
                while (it.hasNext()) {
                    next = it.next();
                    codVerba = (String) next.getAttribute(Columns.CNV_COD_VERBA);
                    codVerbaFerias = (String) next.getAttribute(Columns.CNV_COD_VERBA_FERIAS);
                    mapVerbaFerias.put(codVerbaFerias, codVerba);
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }

        return mapVerbaFerias;
    }

    @Override
    public List<String> lstConvenioRelIntegracao(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaConvenioRelIntegracaoQuery query = new ListaConvenioRelIntegracaoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;

            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    /**
     * Busca convênios por natureza de serviço e orgão do servidor
     * @param csaCodigo
     * @param nseCodigo
     * @param estIdentificador
     * @param orgIdentificador
     * @param rseMatricula
     * @param serCpf
     * @param responsavel
     * @return
     * @throws ConvenioControllerException
     */
    @Override
    public List<Map<String, Object>> lstConvenioPorNseOrgServidor(String csaCodigo, String nseCodigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCpf, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaServidorPorCnvQuery lstServidorCnvQuery = new ListaServidorPorCnvQuery();
            lstServidorCnvQuery.csaCodigo = csaCodigo;
            lstServidorCnvQuery.nseCodigo = nseCodigo;
            lstServidorCnvQuery.estIdentificador = estIdentificador;
            lstServidorCnvQuery.orgIdentificador = orgIdentificador;
            lstServidorCnvQuery.rseMatricula = rseMatricula;
            lstServidorCnvQuery.serCpf = serCpf;
            lstServidorCnvQuery.serAtivo = true;
            lstServidorCnvQuery.cnvAtivo = true;
            lstServidorCnvQuery.tipo = responsavel.getTipoEntidade();
            lstServidorCnvQuery.tipoCodigo = responsavel.getCodigoEntidade();

            if (CanalEnum.SOAP.equals(responsavel.getCanal()) && responsavel.isCsa()) {
                final String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, responsavel);
                lstServidorCnvQuery.matriculaExata = (param != null) && CodedValues.TPA_SIM.equals(param);
            }

            final List<TransferObject> servidorCnvRegs = lstServidorCnvQuery.executarDTO();
            return servidorCnvRegs.stream().map((Function<? super TransferObject, ? extends Map<String, Object>>) TransferObject::getAtributos).collect(Collectors.toList());

        } catch (HQueryException | ParametroControllerException ex) {
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaConvenio(String svcCodigo, String csaCodigo, String orgCodigo, List<String> tocCodigos, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaOcorrenciaConvenioQuery query = new ListaOcorrenciaConvenioQuery();
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.tocCodigos = tocCodigos;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaConvenio(String svcCodigo, String csaCodigo, String orgCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaOcorrenciaConvenioQuery query = new ListaOcorrenciaConvenioQuery();
            query.count = true;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.tocCodigos = tocCodigos;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void bloquearConveniosExpirados(AcessoSistema responsavel) throws ConvenioControllerException {
        try {
            final ListaParamConvenioExpiradoQuery query = new ListaParamConvenioExpiradoQuery();
            final List<TransferObject> parametros = query.executarDTO();
            final Date dataAtual = DateHelper.getSystemDatetime();
            final String ocoObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oco.obs.convenio.expirado", responsavel);

            for (final TransferObject parametro : parametros) {
                final String csaCodigo = parametro.getAttribute(Columns.CSA_CODIGO).toString();
                final String csaNome = parametro.getAttribute(Columns.CSA_NOME).toString();
                final String csaEmail = parametro.getAttribute(Columns.CSA_EMAIL).toString();
                final String svcCodigo = parametro.getAttribute(Columns.SVC_CODIGO).toString();
                final String svcIdentificador = parametro.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                final String svcDescricao = parametro.getAttribute(Columns.SVC_DESCRICAO).toString();
                final String pscVlr = parametro.getAttribute(Columns.PSC_VLR).toString();

                try {
                    final Date dataExpiracao = DateHelper.parse(pscVlr, LocaleHelper.getDatePattern());
                    if (dataExpiracao.compareTo(dataAtual) < 0) {
                        // Data de expiração menor que a data atual. Desativa os convênios e envia e-mail de alerta
                        setCnvScvCodigo(svcCodigo, csaCodigo, null, CodedValues.SCV_INATIVO, false, null, null, ocoObs, responsavel);
                        EnviaEmailHelper.enviarEmailBloqueioConvenioExpirado(csaNome, csaEmail, svcIdentificador, svcDescricao, pscVlr, responsavel);
                    }
                } catch (final ParseException ex) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.incorreto.param.svc", responsavel, CodedValues.TPS_DATA_EXPIRACAO_CONVENIO, svcDescricao));
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstSvcCnvAtivos(String nseCodigo, String csaCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {

        try {
            final ListaConveniosQuery cnvQuery = new ListaConveniosQuery();
            cnvQuery.nseCodigo = nseCodigo;
            cnvQuery.csaCodigo = csaCodigo;
            cnvQuery.ativo = ativo;

            return cnvQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> ListaConveniosIncMargemCartaoReservaLancamento(Short marCodigo, boolean buscaCnvReservaCartao, AcessoSistema responsavel) throws ConvenioControllerException {

        try {
            final ListaConveniosIncMargemCartaoReservaLancamentoQuery cnvQuery = new ListaConveniosIncMargemCartaoReservaLancamentoQuery();
            cnvQuery.marCodigo = marCodigo;
            cnvQuery.buscaCnvReservaCartao = buscaCnvReservaCartao;

            return cnvQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConvenioControllerException(ex);
        }
    }
}
