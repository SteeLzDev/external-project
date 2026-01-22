package com.zetra.econsig.service.simulacao;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DefinicaoTaxaJurosControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.AssinaturaHelper;
import com.zetra.econsig.helper.arquivo.AssinaturaHelper.Documento;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.financeiro.SimulacaoMetodoBrasileiro;
import com.zetra.econsig.helper.financeiro.SimulacaoMetodoIndiano;
import com.zetra.econsig.helper.financeiro.SimulacaoMetodoMexicano;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.pdf.PDFHelper;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.prazo.PrazoSvcCsa;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteDesconto;
import com.zetra.econsig.persistence.entity.CoeficienteDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoId;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Prazo;
import com.zetra.econsig.persistence.entity.PrazoConsignataria;
import com.zetra.econsig.persistence.entity.PrazoConsignatariaHome;
import com.zetra.econsig.persistence.entity.PrazoHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.StatusSolicitacaoHome;
import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficienteAtivoQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficienteQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaConsignatariaComTaxasQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaConsignatariaTaxasSuperioresLimiteQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicoPrazoAtivoQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicosParaCadastroTaxasQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicosSemPrazoConvenioCsaQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicosSimulacaoQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxaJurosAcimaLimiteQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosComDataFimVigMaiorQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosComDataIniVigMaiorQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosQuery;
import com.zetra.econsig.persistence.query.coeficiente.ObtemTipoCoeficienteAtivoQuery;
import com.zetra.econsig.persistence.query.definicaotaxajuros.BuscarDefinicaoTaxaJurosPorCodigoQuery;
import com.zetra.econsig.persistence.query.definicaotaxajuros.BuscarDefinicaoTaxaJurosQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamMensagemSolicitacaoOutroSvcQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.posto.ListaBloqueioSolicitacaoPorPostoCsaSvcQuery;
import com.zetra.econsig.persistence.query.prazo.ListaLimiteTaxaPorPrazoCsaQuery;
import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteCompartilhadoQuery;
import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteEmprestimoQuery;
import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteQuery;
import com.zetra.econsig.persistence.query.prazo.ListaPrazoConsignatariaQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemDataNascServidorQuery;
import com.zetra.econsig.persistence.query.solicitacao.ListaSolicitacaoQuery;
import com.zetra.econsig.service.coeficiente.CoeficienteAtivoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.juros.DefinicaoTaxaJurosController;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <p>Title: SimulacaoControllerBean</p>
 * <p>Description: Session Bean para manipulacao de simulações.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SimulacaoControllerBean implements SimulacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimulacaoControllerBean.class);

    private static final String CHAVE_HASH_BLOQ_POSTO_CSA_SVC = "[%s;%s]";
    private static final String CHAVE_HASH_DEF_TX_JUROS_CSA_SVC = "[%s;%s]";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private CoeficienteAtivoController coeficienteAtivoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private DefinicaoTaxaJurosController definicaoTaxaJurosController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SimulacaoMetodoBrasileiro simulacaoMetodoBrasileiro;

    @Autowired
    private SimulacaoMetodoIndiano simulacaoMetodoIndiano;

    @Autowired
    private SimulacaoMetodoMexicano simulacaoMetodoMexicano;

    // Prazo
    private Prazo findPrazoBean(PrazoTransferObject prazo) throws SimulacaoControllerException {
        Prazo prazoBean = null;
        if (prazo.getPrzCodigo() != null) {
            try {
                prazoBean = PrazoHome.findByPrimaryKey(prazo.getPrzCodigo());
            } catch (final FindException ex) {
                throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", (AcessoSistema) null);
            }
        } else if ((prazo.getSvcCodigo() != null) && (prazo.getPrzVlr() != null)) {
            try {
                prazoBean = PrazoHome.findBySvcValor(prazo.getSvcCodigo(), prazo.getPrzVlr());
            } catch (final FindException ex) {
                throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", (AcessoSistema) null);
            }
        } else {
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", (AcessoSistema) null);
        }
        return prazoBean;
    }

    private PrazoTransferObject setPrazoValues(Prazo prazoBean) {
        final PrazoTransferObject prazo = new PrazoTransferObject(prazoBean.getPrzCodigo());
        prazo.setPrzAtivo(prazoBean.getPrzAtivo());
        prazo.setPrzVlr(prazoBean.getPrzVlr());
        prazo.setSvcCodigo(prazoBean.getServico().getSvcCodigo());

        return prazo;
    }

    @Override
    public PrazoTransferObject findPrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        return setPrazoValues(findPrazoBean(prazo));
    }

    @Override
    public List<PrazoTransferObject> findPrazoByServico(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        return findPrazoByServico(svcCodigo, null, responsavel);
    }

    @Override
    public List<PrazoTransferObject> findPrazoByServico(String svcCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            List<Prazo> prazosBean = null;
            if (przAtivo != null) {
                prazosBean = PrazoHome.findAtivoByServico(svcCodigo);
            } else {
                prazosBean = PrazoHome.findByServico(svcCodigo);
            }
            final List<PrazoTransferObject> result = new ArrayList<>();
            for (final Prazo prazoBean : prazosBean) {
                result.add(setPrazoValues(prazoBean));
            }

            // Ordena os prazos
            Collections.sort(result, (o1, o2) -> {
                final Short d1 = o1.getPrzVlr();
                final Short d2 = o2.getPrzVlr();
                return d1.compareTo(d2);
            });

            return result;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", (AcessoSistema) null);
        }
    }

    @Override
    public List<PrazoTransferObject> findPrazoAtivoByServico(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        return findPrazoByServico(svcCodigo, CodedValues.STS_ATIVO, responsavel);
    }

    @Override
    public String createPrazo(String svcCodigo, Short przVlr, AcessoSistema responsavel) throws SimulacaoControllerException {
        final PrazoTransferObject prazo = new PrazoTransferObject();
        prazo.setPrzVlr(przVlr);
        prazo.setSvcCodigo(svcCodigo);

        return createPrazo(prazo, responsavel);
    }

    @Override
    public String createPrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final Prazo prazoBean = PrazoHome.create(prazo.getSvcCodigo(), prazo.getPrzVlr());
            final String przCodigo = prazoBean.getPrzCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setPrazo(przCodigo);
            log.setServico(prazo.getSvcCodigo());
            log.getUpdatedFields(prazo.getAtributos(), null);
            log.write();
            return przCodigo;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            SimulacaoControllerException excecao = new SimulacaoControllerException("mensagem.erro.nao.possivel.criar.este.prazo.motivo", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new SimulacaoControllerException("mensagem.erro.nao.possivel.criar.este.prazo.ja.existe.outro", responsavel);
            }
            throw excecao;
        }
    }

    @Override
    public void updatePrazo(String przCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException {
        final PrazoTransferObject prazo = new PrazoTransferObject(przCodigo);
        prazo.setPrzAtivo(przAtivo);
        updatePrazo(prazo, responsavel);
    }

    @Override
    public void updatePrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final Prazo prazoBean = findPrazoBean(prazo);
            final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setPrazo(prazoBean.getPrzCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            final PrazoTransferObject prazoCache = setPrazoValues(prazoBean);
            final CustomTransferObject merge = log.getUpdatedFields(prazo.getAtributos(), prazoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.PRZ_ATIVO)) {
                prazoBean.setPrzAtivo((Short) merge.getAttribute(Columns.PRZ_ATIVO));
            }

            AbstractEntityHome.update(prazoBean);

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Ativa ou desativa todos os prazos cadastrados de um dado serviço
     *
     * @param svcCodigo
     * @param przAtivo
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void ativaDesativaSvcPrazo(String svcCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final List<PrazoTransferObject> prazoList = findPrazoByServico(svcCodigo, null, responsavel);
            for (final PrazoTransferObject prazo : prazoList) {
                prazo.setPrzAtivo(przAtivo);
                updatePrazo(prazo, responsavel);
                final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setPrazo(prazo.getPrzCodigo());
                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Desativa os prazos cadastrado de um serviço por consignatária
     *
     * @param svcCodigo
     * @param csaCodigo
     * @param przAtivo
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void desativaSvcPrazoPorCsa(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final List<PrazoConsignataria> prazoList = PrazoConsignatariaHome.findByCsaServico(csaCodigo, svcCodigo);
            final Short inativo = Short.valueOf("0");

            for (final PrazoConsignataria prazoCsa : prazoList) {
                prazoCsa.setPrzCsaAtivo(inativo);
                AbstractEntityHome.update(prazoCsa);

                final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO_CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setPrazo(prazoCsa.getPrazo().getPrzCodigo());
                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", responsavel);
        }
    }

    /**
     * Ativa os prazos cadastrado de um serviço por consignatária
     *
     * @param svcCodigo
     * @param csaCodigo
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void ativaSvcPrazoPorCsa(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            PrazoConsignataria przCsa = null;

            final List<PrazoTransferObject> prazoList = findPrazoByServico(svcCodigo, null, responsavel);
            final Short ativo = Short.valueOf("1");

            for (final PrazoTransferObject prazo : prazoList) {
                final String przCodigo = prazo.getPrzCodigo();

                try {
                    przCsa = PrazoConsignatariaHome.findByCsaPrazo(csaCodigo, przCodigo);
                } catch (final FindException ex) {
                    PrazoConsignatariaHome.create(csaCodigo, przCodigo, ativo);

                    final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO_CONSIGNATARIA, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setPrazo(przCodigo);
                    log.write();
                    continue;
                }

                if ((przCsa != null) && (przCsa.getPrzCsaAtivo().intValue() == 0)) {
                    przCsa.setPrzCsaAtivo(ativo);
                    AbstractEntityHome.update(przCsa);

                    final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO_CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setPrazo(przCodigo);
                    log.write();
                }
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (CreateException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstPrazoSvcCsa(AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaPrazoConsignatariaQuery query = new ListaPrazoConsignatariaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, AcessoSistema responsavel) throws SimulacaoControllerException {
        return this.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, true, true, false, responsavel);
    }

    @Override
    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, boolean validaPrazoRenegociacao, AcessoSistema responsavel) throws SimulacaoControllerException {
        return this.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, true, true, validaPrazoRenegociacao, responsavel);
    }

    @Override
    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, boolean validaBloqSerCnvCsa, boolean validaLimitePrazo, boolean validaPrazoRenegociacao, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaPrazoCoeficienteQuery query = new ListaPrazoCoeficienteQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.dia = dia;
            query.validaBloqSerCnvCsa = validaBloqSerCnvCsa;
            query.validaLimitePrazo = validaLimitePrazo;
            query.validaPrazoRenegociacao = validaPrazoRenegociacao;

            List<TransferObject> retorno = query.executarDTO();
            if ((retorno == null) || (retorno.size() == 0)) {
                // Verifica se a consignatária está bloqueada, para dar mensagem de erro correta
                if ((csaCodigo != null) && !"".equals(csaCodigo)) {
                    final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                    if (validaBloqSerCnvCsa && !consignataria.getAttribute(Columns.CSA_ATIVO).equals(CodedValues.STS_ATIVO)) {
                        throw new SimulacaoControllerException("rotulo.consignataria.bloqueada.singular", responsavel);
                    }
                }

                // Verifica se o serviço tem validação de taxa e se tiver, não deixa reservar margem
                if ((svcCodigo != null) && !"".equals(svcCodigo)) {
                    CustomTransferObject param = null;
                    final ListaParamSvcCseQuery querySvcCse = new ListaParamSvcCseQuery();
                    querySvcCse.svcCodigo = svcCodigo;
                    querySvcCse.tpsCodigo = CodedValues.TPS_VALIDAR_TAXA_JUROS;
                    final List<TransferObject> lstParamSvcCse = querySvcCse.executarDTO();
                    if ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0)) {
                        param = (CustomTransferObject) lstParamSvcCse.get(0);
                    }
                    if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)
                            && "1".equals(param.getAttribute(Columns.PSE_VLR))) {
                        //verifica antes se o serviço é destino de um relacionamento de compartilhamento de taxas.
                        //se for, procura se o serviço oritem tem taxas ativas
                        final boolean temCompartilhamentoTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);

                        if (temCompartilhamentoTaxas) {
                            final ListaPrazoCoeficienteCompartilhadoQuery cftCompartilhados = new ListaPrazoCoeficienteCompartilhadoQuery();
                            cftCompartilhados.svcCodigoDestino = svcCodigo;
                            cftCompartilhados.csaCodigo = csaCodigo;
                            cftCompartilhados.orgCodigo = orgCodigo;
                            cftCompartilhados.dia = dia;

                            retorno = cftCompartilhados.executarDTO();

                            if ((retorno == null) || retorno.isEmpty()) {
                                throw new SimulacaoControllerException("mensagem.erro.prazo.com.taxa.inexistente", responsavel);
                            }
                        } else {
                            throw new SimulacaoControllerException("mensagem.erro.prazo.com.taxa.inexistente", responsavel);
                        }
                    }
                }
            }
            return retorno;
        } catch (HQueryException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getPrazoCoeficienteEmprestimo(String orgCodigo, int dia, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaPrazoCoeficienteEmprestimoQuery query = new ListaPrazoCoeficienteEmprestimoQuery();
            query.orgCodigo = orgCodigo;
            query.dia = dia;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String orgCodigo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException {
        return lstServicosSimulacao(csaCodigo, null, orgCodigo, dia, responsavel);
    }

    @Override
    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException {
        return lstServicosSimulacao(csaCodigo, null, orgCodigo, dia, null, responsavel);
    }

    @Override
    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, short dia, String corCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaServicosSimulacaoQuery query = new ListaServicosSimulacaoQuery();
            final boolean usaDefinicaoTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_USA_DEFINICAO_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

            query.usaDefinicaoTaxaJuros = usaDefinicaoTaxaJuros;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.dia = dia;
            query.corCodigo = corCodigo;

            final List<TransferObject> servicosCandidatos = query.executarDTO();

            if ((servicosCandidatos != null) && (servicosCandidatos.size() > 0) && ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                return parametroController.filtrarServicosSemRelacionamentoAlongamento(servicosCandidatos, responsavel);
            }

            return servicosCandidatos;
        } catch (HQueryException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariasComTaxasAtivas(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaConsignatariaComTaxasQuery query = new ListaConsignatariaComTaxasQuery();
            query.svcCodigo = svcCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * lista registros da tabela de solicitação de autorização que estejam ligados a este ade de simulação
     *
     * @param adeCodigo   - ade código da simulação
     * @param tisCodigos  - filtro dos tipos de solicitação
     * @param ssoCodigos  - filtro de status de solicitação
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public void aprovarAnexosSolicitacaoAutorizacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        if (TextHelper.isNull(adeCodigo)) {
            return;
        }

        List<SolicitacaoAutorizacao> lstSolicitacoes = null;
        try {
            lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, new String[]{TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo()}, StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
        } catch (final FindException e) {
            throw new SimulacaoControllerException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
        }

        if (lstSolicitacoes != null) {
            try {
                final StatusSolicitacao pendenteAssinaturaDoc = StatusSolicitacaoHome.findByPrimaryKey(StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO.getCodigo());

                for (final SolicitacaoAutorizacao solicitacao : lstSolicitacoes) {
                    solicitacao.setStatusSolicitacao(pendenteAssinaturaDoc);
                    AbstractEntityHome.update(solicitacao);
                    final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.solicitacao.anexos", responsavel, StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO.getCodigo());

                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_APRV_ANEXOS_SOLICITACAO, responsavel.getUsuCodigo(), ocaObs, null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(), null, null);
                }
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            } catch (UpdateException | CreateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            }
        }
    }

    /**
     * lista os registros da tabela de solicitação de autorização com status aguardando validação para que sejam reporvados, cria os ocorrências e envia email ao servidor com
     * o motivo da reprovação destes documentos.
     *
     * @param adeCodigo     - ade código da simulação
     * @param obsReprovacao - filtro dos tipos de solicitação
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void reprovarAnexosSolicitacaoAutorizacao(String adeCodigo, String obsReprovacao, AcessoSistema responsavel) throws SimulacaoControllerException {
        if (TextHelper.isNull(adeCodigo)) {
            return;
        }

        if (TextHelper.isNull(obsReprovacao)) {
            throw new SimulacaoControllerException("mensagem.erro.motivo.reprovacao.documento.credito.eletronico", responsavel);
        }

        List<SolicitacaoAutorizacao> lstSolicitacoes = null;
        try {
            lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, new String[]{TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo()}, StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
        } catch (final FindException e) {
            throw new SimulacaoControllerException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
        }

        if (lstSolicitacoes != null) {
            try {
                final StatusSolicitacao pendenteInformacaoDoc = StatusSolicitacaoHome.findByPrimaryKey(StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo());
                for (final SolicitacaoAutorizacao solicitacao : lstSolicitacoes) {
                    solicitacao.setStatusSolicitacao(pendenteInformacaoDoc);
                    AbstractEntityHome.update(solicitacao);
                    final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.solicitacao.anexos", responsavel, StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo());
                    //Cria ocorrência alteração status para pendente de aprovação de documentos.
                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_INFORMACAO, responsavel.getUsuCodigo(), ocaObs, null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(), null, null);
                    //Cria ocorrência de reprovação de anexo com o motivo informado pela CSA.
                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_REPROVAR_ANEXOS_SOLICITACAO, responsavel.getUsuCodigo(), obsReprovacao, null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(), null, null);
                    //Envio email para o servidor contendo os dados da ADE e o motivo da reprovação dos anexos.
                    try {
                        final TransferObject adeTO = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                        final String serEmail = (String) adeTO.getAttribute(Columns.SER_EMAIL);
                        // Envia e-mail para o servidor com o código de autorização para solicitação, caso o mesmo possua um e-mail cadastrado
                        if (!TextHelper.isNull(serEmail)) {
                            EnviaEmailHelper.enviarEmailReprovacaoDocumentacaoServidor(serEmail, adeCodigo, obsReprovacao, responsavel);
                        }
                    } catch (ViewHelperException | AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            } catch (UpdateException | CreateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            }
        }
    }

    /**
     * lista registros da tabela de solicitação de autorização que estejam no status pendente de informação para serem reenviados pelo servidor.
     *
     * @param adeCodigo - ade código da simulação
     * @throws SimulacaoControllerException
     */
    @Override
    public void informarDocumentacaoCreditoEletronico(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        if (TextHelper.isNull(adeCodigo)) {
            return;
        }

        List<SolicitacaoAutorizacao> lstSolicitacoes = null;
        try {
            lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, new String[]{TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo()}, StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo());
        } catch (final FindException e) {
            throw new SimulacaoControllerException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
        }
        if (lstSolicitacoes != null) {
            try {
                final StatusSolicitacao pendenteValidacaoDoc = StatusSolicitacaoHome.findByPrimaryKey(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());

                for (final SolicitacaoAutorizacao solicitacao : lstSolicitacoes) {
                    solicitacao.setStatusSolicitacao(pendenteValidacaoDoc);
                    AbstractEntityHome.update(solicitacao);
                    final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.solicitacao.anexos", responsavel, StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo(), StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
                    //Cria ocorrência alteração status para pendente de aprovação de documentos.
                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_INFORMACAO, responsavel.getUsuCodigo(), ocaObs, null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(), null, null);
                }
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            } catch (UpdateException | CreateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            }
        }
    }

    /**
     * envia anexos de solicitação para serviço externo de assinatura digital
     *
     * @param adeCodigo   - código da ade da solicitação. Se nulo, lista todas solicitações pendentes de assinatura
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void assinarAnexosSolicitacaoAutorizacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        final Boolean assinaturaSomenteCertificado = ParamSist.getBoolParamSist(CodedValues.TPC_ASSINATURA_DIGITAL_CONSIGNACAO_SOMENTE_CERT_DIGITAL, AcessoSistema.getAcessoUsuarioSistema());

        final List<String> tisCodigos = new ArrayList<>();
        tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
        final List<String> ssoCodigos = new ArrayList<>();
        ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO.getCodigo());

        final List<TransferObject> lstSolicitacoes = lstRegistrosSolicitacaoAutorizacao(adeCodigo, tisCodigos, ssoCodigos, responsavel);

        if ((lstSolicitacoes != null) && !lstSolicitacoes.isEmpty()) {
            for (final TransferObject solicitacao : lstSolicitacoes) {
                final String soaAdeCodigo = (String) solicitacao.getAttribute(Columns.SOA_ADE_CODIGO);
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.AAD_ADE_CODIGO, soaAdeCodigo);
                final List<String> tarCodigos = new ArrayList<>();
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO.getCodigo());

                cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);

                try {
                    final List<TransferObject> lstAnexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    final List<String> fileNames = new ArrayList<>();

                    for (final TransferObject anexo : lstAnexos) {
                        final String dataFormatada = DateHelper.format((Date) anexo.getAttribute(Columns.AAD_DATA), "yyyyMMdd");
                        final String fileName = diretorioRaizArquivos + File.separatorChar + "anexo" + File.separatorChar + dataFormatada +
                                File.separatorChar + soaAdeCodigo + File.separatorChar + (String) anexo.getAttribute(Columns.AAD_NOME);
                        fileNames.add(fileName);
                    }

                    final AutDesconto autDes = AutDescontoHome.findByPrimaryKey(soaAdeCodigo);
                    final String arqAAssinar = "sign_" + autDes.getAdeNumero() + ".pdf";

                    if (CodedValues.SAD_CODIGOS_ATIVOS.contains(autDes.getStatusAutorizacaoDesconto().getSadCodigo())) {
                        try {
                            // gera arquivo PDF único contendo todos os anexos da solicitação
                            final String nomePDFAAssinar = PDFHelper.gerarPDFAssinaturaDigital(soaAdeCodigo, fileNames, arqAAssinar, responsavel);
                            final File arqAAssinarFile = new File(nomePDFAAssinar);
                            final List<String> assinaturas = new ArrayList<>();

                            final String csaAdeEmail = ConsignatariaHome.findByAdeCodigo(soaAdeCodigo).getCsaEmail();
                            assinaturas.add(csaAdeEmail);

                            final List<TransferObject> assinaturasComCPF = new ArrayList<>();
                            if (assinaturaSomenteCertificado) {
                                final Servidor servidor = ServidorHome.findByPrimaryKey(autDes.getRegistroServidor().getSerCodigo());

                                final TransferObject assinatura = new CustomTransferObject();
                                assinatura.setAttribute(Columns.SER_CPF, servidor.getSerCpf());
                                assinatura.setAttribute(Columns.SER_EMAIL, servidor.getSerEmail());

                                assinaturasComCPF.add(assinatura);
                            }

                            final String mensagemEmail = ApplicationResourcesHelper.getMessage("mensagem.email.seguem.dados.solicitacao.credito.eletronico.documentacao.assinatura", responsavel);

                            // Envio de arquivo para serviço externo de assinatura digital
                            final Documento regAssinatura = AssinaturaHelper.getInstance().enviarArquivo(arqAAssinarFile, assinaturas, assinaturasComCPF, mensagemEmail);

                            alterarStatusSolicitacaoAutorizacao(soaAdeCodigo, tisCodigos, StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO,
                                    StatusSolicitacaoEnum.DOCUMENTACAO_ENVIADA_PARA_ASSINATURA, responsavel);

                            //cria dado de autorização para armazenar a chave do documento no serviço externo de assinatura digital
                            DadosAutorizacaoDescontoHome.create(soaAdeCodigo, CodedValues.TDA_CHAVE_ASSINATURA_DOC_SOLICITACAO, regAssinatura.chave);

                            // remove arquivo do servidor após assinatura. Aquele deve estar apenas com o serviço externo de assinatura digital
                            final String caminhoArq = arqAAssinarFile.getAbsolutePath();
                            final File dirFile = new File(caminhoArq);
                            dirFile.delete();
                            arqAAssinarFile.delete();

                        } catch (final Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                } catch (AutorizacaoControllerException | FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new SimulacaoControllerException(ex);
                }
            }
        }

    }

    /**
     * Verifica se documentação anexa de uma solicitação foi assinada no serviço de assinatura digital
     *
     * @param adeCodigo   - código da solicitação. Se nulo, verifica todas as solicitações que tenha documentação anexa
     * @param responsavel
     * @throws SimulacaoControllerException
     */
    @Override
    public void verificarAssinaturaAnexosSolicitacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final List<TransferObject> dadosAutorizacao = autorizacaoController.lstDadoAutDesconto(adeCodigo, CodedValues.TDA_CHAVE_ASSINATURA_DOC_SOLICITACAO, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST_LOTE_WEB, responsavel);

            if ((dadosAutorizacao != null) && !dadosAutorizacao.isEmpty()) {
                final AssinaturaHelper helper = AssinaturaHelper.getInstance();
                final List<String> tisCodigos = new ArrayList<>();
                tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());

                for (final TransferObject dadoAutorizacao : dadosAutorizacao) {
                    final String chaveDocumento = (String) dadoAutorizacao.getAttribute(Columns.DAD_VALOR);
                    final String dadAdeCodigo = !TextHelper.isNull(adeCodigo) ? adeCodigo : (String) dadoAutorizacao.getAttribute(Columns.DAD_ADE_CODIGO);

                    final TransferObject dadAde = pesquisarConsignacaoController.findAutDesconto(dadAdeCodigo, responsavel);
                    if (CodedValues.SAD_CODIGOS_ATIVOS.contains(dadAde.getAttribute(Columns.SAD_CODIGO)) && !TextHelper.isNull(chaveDocumento)) {
                        final Documento documento = helper.recuperarDocumento(chaveDocumento);

                        if (documento.assinado) {
                            // cria ocorrência ade de validação digital
                            autorizacaoController.criaOcorrenciaADE(dadAdeCodigo, CodedValues.TOC_VALIDACAO_DIGITAL,
                                    ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.solicitacao.anexos", responsavel,
                                            StatusSolicitacaoEnum.DOCUMENTACAO_ENVIADA_PARA_ASSINATURA.getCodigo(),
                                            StatusSolicitacaoEnum.DOCUMENTACAO_ASSINADA_DIGITALMENTE.getCodigo())
                                    , responsavel);
                            alterarStatusSolicitacaoAutorizacao(dadAdeCodigo, tisCodigos, StatusSolicitacaoEnum.DOCUMENTACAO_ENVIADA_PARA_ASSINATURA,
                                    StatusSolicitacaoEnum.DOCUMENTACAO_ASSINADA_DIGITALMENTE, responsavel);

                            // remove dado de autorização com a chave ao documento na plataforma de assinatura digital
                            final DadosAutorizacaoDescontoId id = new DadosAutorizacaoDescontoId(dadAdeCodigo, CodedValues.TDA_CHAVE_ASSINATURA_DOC_SOLICITACAO);
                            final DadosAutorizacaoDesconto dadAssinatura = DadosAutorizacaoDescontoHome.findByPrimaryKey(id);
                            AbstractEntityHome.remove(dadAssinatura);

                            //envia e-mail ao servidor alertando da aprovação final da documentação da solicitação
                            final TransferObject ade = pesquisarConsignacaoController.findAutDesconto(dadAdeCodigo, responsavel);
                            final String rseCodigo = (String) ade.getAttribute(Columns.RSE_CODIGO);
                            final Servidor servidor = ServidorHome.findByRseCodigo(rseCodigo);
                            EnviaEmailHelper.enviarEmailDocAprovadaServidor(servidor.getSerEmail(), dadAdeCodigo, responsavel);
                        }
                    }
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException(ex);
        } catch (final ViewHelperException ex) {
            // Exceção de envio de e-mail. Não faz rollback
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erro.recuperar.doc.assinatura", responsavel);
        }
    }

    private void alterarStatusSolicitacaoAutorizacao(String adeCodigo, List<String> tisCodigos, StatusSolicitacaoEnum statusIni, StatusSolicitacaoEnum statusFim, AcessoSistema responsavel) throws SimulacaoControllerException {
        if (TextHelper.isNull(adeCodigo) || (tisCodigos == null)) {
            return;
        }

        List<SolicitacaoAutorizacao> lstSolicitacoes = null;
        try {
            String[] tiposSoa = new String[tisCodigos.size()];
            if ((tisCodigos != null) && !tisCodigos.isEmpty()) {
                tiposSoa = tisCodigos.toArray(tiposSoa);
            }
            lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tiposSoa, statusIni.getCodigo());
        } catch (final FindException e) {
            throw new SimulacaoControllerException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
        }

        if (lstSolicitacoes != null) {
            try {
                // retorna objeto da base representando o status fim
                final StatusSolicitacao ssoFim = StatusSolicitacaoHome.findByPrimaryKey(statusFim.getCodigo());

                for (final SolicitacaoAutorizacao solicitacao : lstSolicitacoes) {
                    solicitacao.setStatusSolicitacao(ssoFim);
                    AbstractEntityHome.update(solicitacao);
                }
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            } catch (final UpdateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new SimulacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            }
        }
    }

    @Override
    public List<TransferObject> lstRegistrosSolicitacaoAutorizacao(String adeCodigo, List<String> tisCodigos, List<String> ssoCodigos, AcessoSistema responsavel) throws SimulacaoControllerException {
        List<SolicitacaoAutorizacao> lstSolicitacoes = null;
        try {
            // tratamento necessário, pois chamadas de jsp ainda enviam apenas List<Object>
            final String[] tisCodigosArray = tisCodigos != null ? new String[tisCodigos.size()] : null;
            if (tisCodigos != null) {
                int count = 0;
                for (final Object tisCodigo : tisCodigos) {
                    tisCodigosArray[count] = tisCodigo.toString();
                    count++;
                }
            }

            final String[] ssoCodigosArray = ssoCodigos != null ? new String[ssoCodigos.size()] : null;
            if (ssoCodigos != null) {
                int count = 0;
                for (final Object ssoCodigo : ssoCodigos) {
                    ssoCodigosArray[count] = ssoCodigo.toString();
                    count++;
                }
            }


            if ((ssoCodigos != null) && (ssoCodigos.size() == 1)) {
                if (!TextHelper.isNull(adeCodigo)) {
                    lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos != null ? tisCodigos.toArray(tisCodigosArray) : null, ssoCodigos != null ? ssoCodigos.get(0).toString() : null);
                } else {
                    lstSolicitacoes = SolicitacaoAutorizacaoHome.findByTipoStatus(tisCodigos != null ? tisCodigos.toArray(tisCodigosArray) : null, ssoCodigos != null ? ssoCodigos.get(0).toString() : null);
                }
            } else if (!TextHelper.isNull(adeCodigo)) {
                lstSolicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos != null ? tisCodigos.toArray(tisCodigosArray) : null, ssoCodigos != null ? ssoCodigos.toArray(ssoCodigosArray) : null);
            } else {
                lstSolicitacoes = SolicitacaoAutorizacaoHome.findByTipoStatus(tisCodigos != null ? tisCodigos.toArray(tisCodigosArray) : null, ssoCodigos != null ? ssoCodigos.toArray(ssoCodigosArray) : null);
            }

            if ((lstSolicitacoes != null) && !lstSolicitacoes.isEmpty()) {
                final List<TransferObject> lstResult = new ArrayList<>();

                for (final SolicitacaoAutorizacao solicitacao : lstSolicitacoes) {
                    final TransferObject soaTO = new CustomTransferObject();
                    soaTO.setAttribute(Columns.SOA_CODIGO, solicitacao.getSoaCodigo());
                    soaTO.setAttribute(Columns.SOA_DATA, solicitacao.getSoaData());
                    soaTO.setAttribute(Columns.SOA_DATA_VALIDADE, solicitacao.getSoaDataValidade());
                    soaTO.setAttribute(Columns.SOA_ADE_CODIGO, solicitacao.getAutDesconto().getAdeCodigo());
                    soaTO.setAttribute(Columns.TIS_CODIGO, solicitacao.getTipoSolicitacao().getTisCodigo());
                    soaTO.setAttribute(Columns.SSO_CODIGO, solicitacao.getStatusSolicitacao().getSsoCodigo());

                    lstResult.add(soaTO);
                }

                return lstResult;
            }

            return null;
        } catch (final FindException e) {
            throw new SimulacaoControllerException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
        }

    }

    /**
     * Retorna os prazos com status ativo na entidade Prazo e na PrazoConsignataria
     *
     * @param svcCodigo : código do serviço
     * @param csaCodigo : código da consignatária
     * @param usuCodigo : responsável pela alteração
     * @return : uma lista de prazos ativos para a consignatária representada pelo csaCodigo
     * @throws SimulacaoControllerException
     */
    @Override
    public List<PrazoTransferObject> findPrazoCsaByServico(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final List<Prazo> prazosBean = PrazoHome.findAtivoByCsaServico(csaCodigo, svcCodigo);

            final List<PrazoTransferObject> result = new ArrayList<>();
            for (final Prazo prazoBean : prazosBean) {
                result.add(setPrazoValues(prazoBean));
            }

            Collections.sort(result, (o1, o2) -> {
                final Short prz1 = o1.getPrzVlr();
                final Short prz2 = o2.getPrzVlr();
                return prz1.compareTo(prz2);
            });

            return result;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", responsavel);
        }
    }

    /**
     * Procura pelo prazo, se existir muda o status para ativo, se não
     * cira um novo
     *
     * @param csaCodigo : código da consignatária
     * @param przCodigo : código do prazo
     * @param usuCodigo : responsável pela alteração
     * @return : o código do novo na consignatária
     * @throws SimulacaoControllerException
     */
    @Override
    public String desbloqueiaPrazoCsa(String csaCodigo, String przCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            PrazoConsignataria prazoCsaBean = null;
            try {
                prazoCsaBean = PrazoConsignatariaHome.findByCsaPrazo(csaCodigo, przCodigo);
                prazoCsaBean.setPrzCsaAtivo(CodedValues.STS_ATIVO);
                AbstractEntityHome.update(prazoCsaBean);
            } catch (final FindException ex) {
                prazoCsaBean = PrazoConsignatariaHome.create(csaCodigo, przCodigo);
            }
            final String przCsaCodigo = prazoCsaBean.getPrzCsaCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO_CONSIGNATARIA, Log.UNLOCK, Log.LOG_INFORMACAO);
            log.setPrazoConsignataria(przCsaCodigo);
            log.write();
            return przCsaCodigo;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erro.nao.possivel.desbloquear.prazo.selecionado", responsavel);
        }
    }

    /**
     * Seta o status do prazo na consignatária para bloqueado
     *
     * @param csaCodigo : código da consignatária
     * @param przCodigo : código do prazo
     * @param usuCodigo : responsável pela alteração
     * @throws SimulacaoControllerException
     */
    @Override
    public void bloqueiaPrazoCsa(String csaCodigo, String przCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final PrazoConsignataria prazoCsaBean = PrazoConsignatariaHome.findByCsaPrazo(csaCodigo, przCodigo);
            prazoCsaBean.setPrzCsaAtivo(CodedValues.STS_INATIVO);
            AbstractEntityHome.update(prazoCsaBean);
            final LogDelegate log = new LogDelegate(responsavel, Log.PRAZO_CONSIGNATARIA, Log.LOCK, Log.LOG_INFORMACAO);
            log.setPrazoConsignataria(prazoCsaBean.getPrzCsaCodigo());
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", responsavel);
        }
    }

    // Coeficientes
    @Override
    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, short prazo, BigDecimal vlrParcela,
                                                    BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException {
        return getCoeficienteAtivo(csaCodigo, svcCodigo, null, null, prazo, (short) 0, true, vlrParcela, vlrLiberado, responsavel);
    }

    @Override
    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, short prazo, short dia, BigDecimal vlrParcela,
                                                    BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException {
        return getCoeficienteAtivo(csaCodigo, svcCodigo, null, null, prazo, dia, true, vlrParcela, vlrLiberado, responsavel);
    }

    @Override
    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, short prazo, short dia, BigDecimal vlrParcela,
                                                    BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException {
        return getCoeficienteAtivo(csaCodigo, svcCodigo, orgCodigo, rseCodigo, prazo, dia, true, vlrParcela, vlrLiberado, responsavel);
    }

    /**
     * Listagem dos coeficientes ativos cadastrados na base de dados.
     *
     * @param csaCodigo
     * @param svcCodigo
     * @param orgCodigo
     * @param rseCodigo
     * @param prazo
     * @param dia
     * @param validaBloqSerCnvCsa
     * @param responsavel
     * @return Lista de coeficientes ativos cadastrados na base dados.
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, short prazo, short dia, boolean validaBloqSerCnvCsa, BigDecimal vlrParcela, BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaCoeficienteAtivoQuery query = new ListaCoeficienteAtivoQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.rseCodigo = rseCodigo;
            query.prazo = prazo;
            query.dia = dia;
            query.validaBloqSerCnvCsa = validaBloqSerCnvCsa;

            List<TransferObject> coeficientes = query.executarDTO();

            if (ParamSist.paramEquals(CodedValues.TPC_USA_DEFINICAO_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {
                coeficientes = ajustarListaComDefinicaoTaxaJuros(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, prazo, responsavel, coeficientes);
            }

            return coeficientes;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject getDefinicaoTaxaJuros(String dtjCodigo) throws SimulacaoControllerException {
        try {
            final BuscarDefinicaoTaxaJurosPorCodigoQuery query = new BuscarDefinicaoTaxaJurosPorCodigoQuery();
            query.dtjCodigo = dtjCodigo;
            final List<TransferObject> executarDTO = query.executarDTO();
            if ((executarDTO != null) && !executarDTO.isEmpty()) {
                return (CustomTransferObject) executarDTO.get(0);
            }
            return null;

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
    }

    @Override
    public String getTipoCoeficienteAtivo(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ObtemTipoCoeficienteAtivoQuery query = new ObtemTipoCoeficienteAtivoQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;

            String tipo = null;
            final List<String> lista = query.executarLista();
            if (lista.size() > 0) {
                tipo = lista.get(0).toString();
            }
            return tipo;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaCoeficienteQuery query = new ListaCoeficienteQuery();
            query.tipo = CodedValues.CFT_MENSAL;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Se tem relacionamento para compartilhamento de taxas, verifica em qual serviço deve ser usado para recuperar as taxas.
     * @param svcCodigo             Serviço a ser analisado.
     * @param somenteComBloqueioCad Somente recupera servico dono das taxas compartilhadas se possui bloqueio de cadastro.
     * @param responsavel           Responsável pela operação.
     * @return Serviço origem do relacionamento, de onde as taxas serão usadas.
     * @throws SimulacaoControllerException Exceção padrão da classe.
     */
    @Override
    public String getSvcTaxaCompartilhada(String svcCodigo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            boolean temRelacionamentoCompTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);
            if (somenteComBloqueioCad) {
                final boolean temBloqueioCadTaxasComp = ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CAD_TAXAS_COMPARTILHADAS, responsavel);
                temRelacionamentoCompTaxas = temRelacionamentoCompTaxas && temBloqueioCadTaxasComp;
            }
            if (temRelacionamentoCompTaxas) {
                // Se o serviço atual é o destino de um relacionamento, então pega o código do serviço
                // de origem, pois será nele que o cadastro de taxas existirá
                final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                queryRel.tntCodigo = CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS;
                queryRel.svcCodigoOrigem = null;
                queryRel.svcCodigoDestino = svcCodigo;
                final List<TransferObject> svcCodigosRel = queryRel.executarDTO();
                for (final TransferObject cto : svcCodigosRel) {
                    final String svcCodigoRel = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                    if (!svcCodigoRel.equals(svcCodigo)) {
                        LOG.debug("MUDANDO O SERVIÇO PARA OBTER CADASTRO DE TAXAS.");
                        svcCodigo = svcCodigoRel;
                        break;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return svcCodigo;
    }

    @Override
    public List<TransferObject> getCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            svcCodigo = getSvcTaxaCompartilhada(svcCodigo, somenteComBloqueioCad, responsavel);

            final ListaCoeficienteQuery query = new ListaCoeficienteQuery();
            query.tipo = CodedValues.CFT_MENSAL;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Coeficiente Diário
    @Override
    public List<TransferObject> getCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaCoeficienteQuery query = new ListaCoeficienteQuery();
            query.tipo = CodedValues.CFT_DIARIO;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            svcCodigo = getSvcTaxaCompartilhada(svcCodigo, somenteComBloqueioCad, responsavel);

            final ListaCoeficienteQuery query = new ListaCoeficienteQuery();
            query.tipo = CodedValues.CFT_DIARIO;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject getCoeficienteAtivo(String cftCodigo) throws SimulacaoControllerException {
        try {
            return coeficienteAtivoController.getCoeficienteAtivo(cftCodigo);
        } catch (final CoeficienteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erro.prazo.nao.encontrado", (AcessoSistema) null);
        }
    }

    /**
     * Retorna uma lista de códigos de serviços que tem prazos
     * cadastrados e ativos.
     *
     * @param csaCodigo : código da consignatária, se nulo retorna para todas as csa
     * @param usuCodigo : responsável pela operação
     * @return : lista de svcCodigo
     * @throws SimulacaoControllerException
     */
    @Override
    public List<String> getSvcCodigosParaCadastroTaxas(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaServicosParaCadastroTaxasQuery query = new ListaServicosParaCadastroTaxasQuery();
            query.csaCodigo = csaCodigo;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna uma lista de códigos de serviços que não tem convênio ou não tem prazos habilitados para a consignatária.
     *
     * @param csaCodigo : código da consignatária, se nulo retorna para todas as csa
     * @return : lista de svcCodigo
     * @throws SimulacaoControllerException
     */
    @Override
    public List<String> getSvcCodigosSemPrazoConvenioCsa(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaServicosSemPrazoConvenioCsaQuery query = new ListaServicosSemPrazoConvenioCsaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosParaCadastroTaxas(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaServicosParaCadastroTaxasQuery query = new ListaServicosParaCadastroTaxasQuery();
            query.apenasCodigo = false;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Cria um coeficiente desconto
     *
     * @param adeCodigo          : código da autorização desconto
     * @param cftCodigo          : código do coeficiente
     * @param cdeVlrLiberado     : valor liberado
     * @param cdeVlrLiberadoCalc : valor liberado Calculado
     * @param cdeTxtContato      : texto para contato com o servidor
     * @param cdeVlrTac          : valor da tac
     * @param cdeVlrIof          : valor da iof
     * @param usuCodigo          : responsável pela operação
     * @return : código do coeficiente desconto
     * @throws SimulacaoControllerException
     */
    @Override
    public String createCoeficienteDesconto(String adeCodigo, String cftCodigo, BigDecimal cdeVlrLiberado, BigDecimal cdeVlrLiberadoCalc, String cdeTxtContato, Short cdeRanking, BigDecimal cdeVlrTac, BigDecimal cdeVlrIof, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final CoeficienteDesconto cdeBean = CoeficienteDescontoHome.create(adeCodigo, cftCodigo, cdeVlrLiberado, cdeVlrLiberadoCalc, cdeTxtContato, cdeRanking, cdeVlrTac, cdeVlrIof);

            final String cdeCodigo = cdeBean.getCdeCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_DESCONTO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCoeficienteDesconto(cdeCodigo);
            log.setAutorizacaoDesconto(adeCodigo);
            log.setCoeficiente(cftCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.liberado.arg0", responsavel, cdeVlrLiberado.toString()));
            if (cdeVlrTac != null) {
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.tac.arg0", responsavel, cdeVlrTac.toString()));
            }
            if (cdeVlrIof != null) {
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.iof.arg0", responsavel, cdeVlrIof.toString()));
            }
            log.write();
            return cdeCodigo;
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza um coeficiente desconto
     *
     * @param cdeCodigo      : código do coeficiente desconto
     * @param cftCodigo      : código do coeficiente
     * @param cdeVlrLiberado : valor liberado
     * @param usuCodigo      : responsável pela operação
     * @throws SimulacaoControllerException
     */
    @Override
    public void updateCoeficienteDesconto(String cdeCodigo, String cftCodigo, BigDecimal cdeVlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final CoeficienteDesconto cdeBean = CoeficienteDescontoHome.findByPrimaryKey(cdeCodigo);

            cdeBean.setCoeficiente(CoeficienteHome.findByPrimaryKey(cftCodigo));
            cdeBean.setCdeVlrLiberado(cdeVlrLiberado);

            AbstractEntityHome.update(cdeBean);

            final LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_DESCONTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setCoeficienteDesconto(cdeCodigo);
            log.setCoeficiente(cftCodigo);
            log.addChangedField(Columns.CDE_VLR_LIBERADO, cdeVlrLiberado);
            log.write();
        } catch (LogControllerException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject findCdeByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        return findCdeByAdeCodigo(adeCodigo, false, responsavel);
    }

    /**
     * Faz um find no coeficiente desconto pelo código da autorização
     *
     * @param adeCodigo : código da autorização
     * @param usuCodigo : responsável pela operação
     * @return : um coeficiente desconto
     * @throws SimulacaoControllerException
     */
    @Override
    public CustomTransferObject findCdeByAdeCodigo(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            CoeficienteDesconto cdeBean = null;
            if (arquivado) {
                cdeBean = CoeficienteDescontoHome.findArquivadoByAdeCodigo(adeCodigo);
            } else {
                cdeBean = CoeficienteDescontoHome.findByAdeCodigo(adeCodigo);
            }

            final CustomTransferObject result = new CustomTransferObject();

            result.setAttribute(Columns.CDE_CODIGO, cdeBean.getCdeCodigo());
            result.setAttribute(Columns.CDE_ADE_CODIGO, cdeBean.getAutDesconto().getAdeCodigo());
            result.setAttribute(Columns.CDE_CFT_CODIGO, cdeBean.getCoeficiente().getCftCodigo());
            result.setAttribute(Columns.CDE_VLR_LIBERADO, cdeBean.getCdeVlrLiberado());
            result.setAttribute(Columns.CDE_VLR_LIBERADO_CALC, cdeBean.getCdeVlrLiberadoCalc());
            result.setAttribute(Columns.CDE_VLR_IOF, cdeBean.getCdeVlrIof());
            result.setAttribute(Columns.CDE_VLR_TAC, cdeBean.getCdeVlrTac());
            result.setAttribute(Columns.CDE_TXT_CONTATO, cdeBean.getCdeTxtContato());
            result.setAttribute(Columns.CDE_RANKING, cdeBean.getCdeRanking());

            // Seta as informações sobre a forma de pagamento
            result.setAttribute(Columns.CDE_FORMA_CRED, cdeBean.getCdeFormaCred());
            result.setAttribute(Columns.CDE_AGENCIA_CRED, cdeBean.getCdeAgenciaCred());
            result.setAttribute(Columns.CDE_AGENCIA_DV_CRED, cdeBean.getCdeAgenciaDvCred());
            result.setAttribute(Columns.CDE_CONTA_CRED, cdeBean.getCdeContaCred());
            result.setAttribute(Columns.CDE_CONTA_DV_CRED, cdeBean.getCdeContaDvCred());
            result.setAttribute(Columns.CDE_BCO_CODIGO, cdeBean.getBanco() != null ? cdeBean.getBanco().getBcoCodigo() : null);

            return result;
        } catch (final FindException ex) {
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getSvcPrazo(String csaCodigo, String svcCodigo, boolean prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        return getSvcPrazo(csaCodigo, svcCodigo, prazo, false, null, responsavel);
    }

    @Override
    public List<TransferObject> getSvcPrazo(String csaCodigo, String svcCodigo, boolean prazo, boolean prazoMultiploDoze, String prazosInformados, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaServicoPrazoAtivoQuery query = new ListaServicoPrazoAtivoQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;
            query.prazoMultiploDoze = prazoMultiploDoze;
            query.prazosInformados = prazosInformados;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setTaxaJuros(String csaCodigo, String svcCodigo, List<TransferObject> coeficientes, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            // Seleciona prazos ativos.
            final List<PrazoTransferObject> prazosAtivos = findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
            // Verfica se todos os prazos ativos foram informados
            if (prazosAtivos.size() > coeficientes.size()) {
                final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                if (temCET) {
                    throw new SimulacaoControllerException("mensagem.informacao.cet.taxas", responsavel);
                } else {
                    throw new SimulacaoControllerException("mensagem.informacao.taxa.juros.taxas", responsavel);
                }
            }

            for (final PrazoTransferObject prazo : prazosAtivos) {
                boolean prazoInformado = false;
                for (final TransferObject coeficiente : coeficientes) {
                    if (prazo.getPrzVlr() == (short) coeficiente.getAttribute(Columns.PRZ_VLR)) {
                        prazoInformado = true;
                        break;
                    }
                }

                if (!prazoInformado) {
                    final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                    if (temCET) {
                        throw new SimulacaoControllerException("mensagem.informacao.cet.taxas", responsavel);
                    } else {
                        throw new SimulacaoControllerException("mensagem.informacao.taxa.juros.taxas", responsavel);
                    }
                }
            }

            // Obtém os parâmetros de serviço necessários
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final String tipoOrdenacao = paramSvcCse.getTpsOrdenacaoCadastroTaxas();
            final String csaCodigoLimiteTaxa = paramSvcCse.getTpsCsaLimiteSuperiorTabelaJurosCet();
            boolean finalizarVigenciaTabelasTaxa = false;

            // Verifica se as taxas cadastradas seguem ordenação dos valores pelo prazo especificado no sistema
            if (!TextHelper.isNull(tipoOrdenacao) && !CodedValues.ORDEM_TAXAS_NA.equals(tipoOrdenacao)) {
                float vlrAnterior = 0.0f;

                for (final TransferObject coeficiente : coeficientes) {
                    final CustomTransferObject original = (CustomTransferObject) coeficiente;

                    if (original.getAttribute(Columns.CFT_VLR) != null) {
                        final float vlrCorrente = Float.parseFloat(original.getAttribute(Columns.CFT_VLR).toString());

                        if ((vlrAnterior > 0.0f) && (vlrCorrente != 0.0f)) {
                            if (CodedValues.ORDEM_TAXAS_DESC.equals(tipoOrdenacao) && (vlrCorrente > vlrAnterior)) {
                                throw new SimulacaoControllerException("mensagem.erro.taxa.juros.valor.ordem.decrescente", responsavel);
                            } else if (CodedValues.ORDEM_TAXAS_ASC.equals(tipoOrdenacao) && (vlrCorrente < vlrAnterior)) {
                                throw new SimulacaoControllerException("mensagem.erro.taxa.juros.valor.ordem.crescente", responsavel);
                            }
                        }

                        if (vlrCorrente > 0.0f) {
                            vlrAnterior = vlrCorrente;
                        }
                    }
                }
            }

            // Valida as taxas informadas pelo limite do serviço
            final List<TransferObject> limiteTaxa = getLimiteTaxas(svcCodigo, responsavel);
            if (limiteTaxa.size() > 0) {
                BigDecimal maxTaxa = null;
                BigDecimal vlrComp = null;
                BigDecimal przComp = null;
                for (final TransferObject cto : coeficientes) {
                    if ((cto.getAttribute(Columns.CFT_VLR) != null) && !"".equals(cto.getAttribute(Columns.CFT_VLR))) {
                        vlrComp = new BigDecimal(cto.getAttribute(Columns.CFT_VLR).toString());
                    }

                    if ((cto.getAttribute(Columns.PRZ_VLR) != null) && !"".equals(cto.getAttribute(Columns.PRZ_VLR))) {
                        przComp = new BigDecimal(cto.getAttribute(Columns.PRZ_VLR).toString());
                    }

                    maxTaxa = null;
                    for (final TransferObject ctoLte : limiteTaxa) {
                        if ((maxTaxa == null) &&
                                (ctoLte.getAttribute(Columns.LTJ_PRAZO_REF) != null) && !"".equals(ctoLte.getAttribute(Columns.LTJ_PRAZO_REF)) &&
                                (new BigDecimal(ctoLte.getAttribute(Columns.LTJ_PRAZO_REF).toString()).compareTo(przComp) >= 0)) {
                            maxTaxa = new BigDecimal(ctoLte.getAttribute(Columns.LTJ_JUROS_MAX).toString());
                        }
                    }

                    if ((maxTaxa != null) && (maxTaxa.compareTo(vlrComp) < 0)) {
                        try {
                            throw new SimulacaoControllerException("mensagem.erro.valor.informado.taxa.invalido.maximo.permitido.para.prazo.valor", responsavel, String.valueOf(przComp.intValue()), NumberHelper.reformat(String.valueOf(maxTaxa.doubleValue()), "en", NumberHelper.getLang()));
                        } catch (final ParseException ex) {
                            LOG.debug("Erro ao tentar formatar a mensagem: " + ex.getMessage());
                        }
                    }
                }
            }

            // Se tem consignatária limite da tabela de taxas
            if (!TextHelper.isNull(csaCodigoLimiteTaxa)) {
                // Busca a tabela de taxas atual da consignatária limite para este serviço
                final Map<String, BigDecimal> taxasLimiteMap = new HashMap<>();
                final List<TransferObject> taxasLimite = getTaxas(null, csaCodigoLimiteTaxa, svcCodigo, null, false, false, responsavel);
                if ((taxasLimite != null) && (taxasLimite.size() > 0)) {
                    for (final TransferObject valor : taxasLimite) {
                        taxasLimiteMap.put(valor.getAttribute(Columns.PRZ_VLR).toString(), (BigDecimal) valor.getAttribute(Columns.CFT_VLR));
                    }
                }

                // Se não é a consignatária limite, então valida as novas taxas pela consignatária limite
                if (!csaCodigo.equals(csaCodigoLimiteTaxa)) {
                    final StringBuilder mensagemErro = new StringBuilder();
                    for (final TransferObject valor : coeficientes) {
                        final BigDecimal cftVlr = new BigDecimal(valor.getAttribute(Columns.CFT_VLR).toString());
                        final String prazo = valor.getAttribute(Columns.PRZ_VLR).toString();
                        final BigDecimal cftVlrLimite = taxasLimiteMap.get(prazo);
                        if ((cftVlrLimite != null) && (cftVlrLimite.signum() > 0) && (cftVlrLimite.compareTo(cftVlr) != 1)) {
                            // Se o limite para o prazo existe, é maior que zero, e não é maior que o novo valor cadastrado,
                            // então retorna erro para o usuário alertando que a tabela de taxas deve ser menor
                            mensagemErro.append(ApplicationResourcesHelper.getMessage("mensagem.erro.valor.maximo.permitido.para.prazo.valor", responsavel, prazo, NumberHelper.reformat(String.valueOf(cftVlrLimite.doubleValue() - 0.01), "en", NumberHelper.getLang())));
                        }
                    }
                    if (mensagemErro.length() > 0) {
                        throw new SimulacaoControllerException("mensagem.erro.valores.informados.para.tabela.taxas.acima.tabela.limite", responsavel, mensagemErro.toString());
                    }
                } else {
                    // Se é a consignatária limite, então verifica se o novo cadastro está reduzindo as taxas e em caso afirmativo
                    // as tabelas de taxas das demais entidades deverá ter a vigência finalizada:
                    // Itera nas novas taxas e verifica se alguma é menor
                    final Iterator<TransferObject> itCoeficientes = coeficientes.iterator();
                    while (itCoeficientes.hasNext() && !finalizarVigenciaTabelasTaxa) {
                        final TransferObject valor = itCoeficientes.next();
                        final BigDecimal cftVlrNovo = new BigDecimal(valor.getAttribute(Columns.CFT_VLR).toString());
                        final String prazo = valor.getAttribute(Columns.PRZ_VLR).toString();
                        final BigDecimal cftVlrAtual = taxasLimiteMap.get(prazo);
                        if ((cftVlrAtual == null) ||
                                ((cftVlrAtual.signum() == 0) && (cftVlrNovo.signum() > 0)) ||
                                (cftVlrAtual.compareTo(cftVlrNovo) == 1)) {
                            // Se o valor limite atual não existe (exemplo de um novo prazo habilitado para o serviço), ou
                            // o valor atual existe e é zero e o novo é diferente de zero, ou
                            // ambos são diferente de zero e o atual é maior que o novo,
                            // então houve uma redução na tabela de taxas limite
                            finalizarVigenciaTabelasTaxa = true;
                        }
                    }
                }
            }

            final Map<Object, TransferObject> candidatos = new HashMap<>();
            final CustomTransferObject dataTaxaJuros = SimulacaoHelper.calcularDataTaxaJuros(svcCodigo, responsavel);
            final Date dataIniVig = DateHelper.parse((String) dataTaxaJuros.getAttribute(Columns.CFT_DATA_INI_VIG) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            final Date dataFimVig = DateHelper.parse((String) dataTaxaJuros.getAttribute(Columns.CFT_DATA_FIM_VIG) + " 23:59:59", "yyyy-MM-dd HH:mm:ss");

            /*
             * 1. Remove os coeficientes da consignatária para o serviço cuja data de
             * início de vigência é maior ou igual a data de vigência no novo coeficiente
             * que será inserido.
             */
            final ListaTaxasJurosComDataIniVigMaiorQuery query1 = new ListaTaxasJurosComDataIniVigMaiorQuery();
            query1.csaCodigo = csaCodigo;
            query1.svcCodigo = svcCodigo;
            query1.cftDataIniVig = dataIniVig;

            List<TransferObject> cftCandidatos = query1.executarDTO();
            for (final TransferObject cto : cftCandidatos) {
                candidatos.put(cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO), cto);
                coeficienteAtivoController.removeCoeficienteAtivo(cto, responsavel);
            }

            /*
             * 2. Atualiza a data de término de vigência das taxas que estão sem fim de vigência,
             * desabilitando estas taxas do cadastro.
             */
            final ListaTaxasJurosQuery query2 = new ListaTaxasJurosQuery();
            final CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            parametrosQuery.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            parametrosQuery.setAttribute("ATIVO", false);
            parametrosQuery.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
            query2.setCriterios(parametrosQuery);

            cftCandidatos = query2.executarDTO();
            for (final TransferObject cto : cftCandidatos) {
                candidatos.put(cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO), cto);
                cto.setAttribute(Columns.CFT_DATA_FIM_VIG, dataFimVig);
                coeficienteAtivoController.updateCoeficienteAtivo(cto, responsavel);
            }

            /*
             * 3. Antecipa o fim de vigência das taxas que estão com esta data maior que
             * a data fim vigência calculada. Isso pode ocorrer por dois motivos: alteração
             * da configuração do serviço ou o caso de serviço com limite de taxas pelo
             * cadastro de uma determinada consignatária com carência para termino.
             */
            final ListaTaxasJurosComDataFimVigMaiorQuery query3 = new ListaTaxasJurosComDataFimVigMaiorQuery();
            query3.csaCodigo = csaCodigo;
            query3.svcCodigo = svcCodigo;
            query3.cftDataFimVig = dataFimVig;

            cftCandidatos = query3.executarDTO();
            for (final TransferObject cto : cftCandidatos) {
                candidatos.put(cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO), cto);
                cto.setAttribute(Columns.CFT_DATA_FIM_VIG, dataFimVig);
                coeficienteAtivoController.updateCoeficienteAtivo(cto, responsavel);
            }

            /*
             * 4. Insere as novas taxas de juros para a consignatária / serviço
             */
            final ListaServicoPrazoAtivoQuery query4 = new ListaServicoPrazoAtivoQuery();
            query4.csaCodigo = csaCodigo;
            query4.svcCodigo = svcCodigo;
            query4.prazo = true;

            final Map<Short, String> prazos = query4.executarMapa();

            // Recuperar prazos possíveis
            String prazosPossiveis = "";
            final Iterator<Short> it = prazos.keySet().iterator();
            while (it.hasNext()) {
                final Object prazo = it.next();

                prazosPossiveis += !TextHelper.isNull(prazo) ? prazo.toString() : "";
                if (it.hasNext()) {
                    prazosPossiveis += ", ";
                }
            }

            final int diasVigenciaParamSvc = intParamSvcDiasVigenciaValue(svcCodigo, paramSvcCse);
            for (final TransferObject cft : coeficientes) {
                final Object przCsaCodigo = prazos.get(cft.getAttribute(Columns.PRZ_VLR));
                if (TextHelper.isNull(przCsaCodigo)) {
                    throw new AutorizacaoControllerException("mensagem.erro.prazos.permitidos.para.este.servico.sao.arg0", responsavel, prazosPossiveis);
                }

                cft.setAttribute(Columns.CFT_DATA_INI_VIG, dataIniVig);
                cft.setAttribute(Columns.CFT_DIA, (short) 0);
                cft.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, przCsaCodigo);
                cft.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                cft.setAttribute(Columns.SVC_CODIGO, svcCodigo);

                //DESENV-17250 : configurando dias de vingência definidos no parâmetro de serviço por consignante.
                if (diasVigenciaParamSvc > 0) {
                    cft.setAttribute(Columns.CFT_DATA_FIM_VIG, DateHelper.addDays(dataIniVig, diasVigenciaParamSvc));
                }

                coeficienteAtivoController.createCoeficienteAtivo(cft, candidatos.get(przCsaCodigo), responsavel);
            }

            /*
             * 5. Se tem consignatária limite de taxa e a tabela foi alterada, então finaliza a vigência das
             * tabelas de taxas das demais entidades consignatárias.
             */
            if (finalizarVigenciaTabelasTaxa) {
                final ListaConsignatariaTaxasSuperioresLimiteQuery query5 = new ListaConsignatariaTaxasSuperioresLimiteQuery();
                query5.svcCodigo = svcCodigo;
                query5.csaCodigo = csaCodigoLimiteTaxa;
                final List<TransferObject> consignatarias = query5.executarDTO();

                if ((consignatarias != null) && (consignatarias.size() > 0)) {
                    int diasVigenciaTaxasSuperiorLimite = 0;
                    try {
                        diasVigenciaTaxasSuperiorLimite = Integer.parseInt(paramSvcCse.getTpsDiasVigenciaTaxasSuperiorLimite());
                    } catch (final Exception ex) {
                        LOG.error("Valor incorreto para o parâmetro de serviço " + CodedValues.TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE + " para o serviço " + svcCodigo);
                        diasVigenciaTaxasSuperiorLimite = 0;
                    }

                    // Calcula a data final de vigência + carência
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(dataFimVig);
                    cal.add(Calendar.DAY_OF_MONTH, diasVigenciaTaxasSuperiorLimite);
                    final Date dataFimVigMaisCarencia = cal.getTime();

                    for (final TransferObject csa : consignatarias) {
                        final String proximoCsaCodigo = csa.getAttribute(Columns.CSA_CODIGO).toString();
                        final String proximoCsaNome = csa.getAttribute(Columns.CSA_NOME).toString();
                        final String proximoCsaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);

                        LOG.debug("Finalizando Vigência da tabela de taxas da Consignatária: " + proximoCsaNome);

                        try {
                            final LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UPDATE, Log.LOG_AVISO);
                            log.setConsignataria(proximoCsaCodigo);
                            log.setServico(svcCodigo);
                            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.finalizando.vigencia.tabela.taxa.juros.cet", responsavel));
                            if (!TextHelper.isNull(proximoCsaEmail)) {
                                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.email.para.aviso.arg0", responsavel, proximoCsaEmail));
                            }
                            log.write();
                        } catch (final LogControllerException ex) {
                            // Ignora erro de gravação de log
                            LOG.error(ex.getMessage(), ex);
                        }

                        // Lista as taxas desta consignatária para atualização
                        final ListaTaxaJurosAcimaLimiteQuery query6 = new ListaTaxaJurosAcimaLimiteQuery();
                        query6.csaCodigo = proximoCsaCodigo;
                        query6.svcCodigo = svcCodigo;
                        query6.csaCodigoLimiteTaxa = csaCodigoLimiteTaxa;
                        final List<TransferObject> taxasFimVig = query6.executarDTO();
                        for (final TransferObject cto : taxasFimVig) {
                            cto.setAttribute(Columns.CFT_DATA_FIM_VIG, dataFimVigMaisCarencia);
                            coeficienteAtivoController.updateCoeficienteAtivo(cto, responsavel);
                        }

                        if (!TextHelper.isNull(proximoCsaEmail)) {
                            try {
                                // Envia e-mail para a consignatária de aviso sobre a alteração da tabela de taxas
                                EnviaEmailHelper.enviarEmailAlteracaoTabelaTaxaLimite(proximoCsaEmail, proximoCsaNome, AcessoSistema.ENTIDADE_CSA, dataFimVigMaisCarencia, coeficientes, taxasFimVig, responsavel);
                            } catch (final ViewHelperException ex) {
                                // Se ocorrerem erros no envio de e-mail de aviso não deve ser desfeito o processo
                                LOG.error(ex.getMessage(), ex);
                            }
                        } else {
                            LOG.warn("A Consignatária " + proximoCsaNome + " não possui e-mail cadastrado para aviso sobre alteração da tabela de taxas.");
                        }
                    }
                }
            }


            /*
             * 6. Se tem consignatária limite de taxa e a tabela foi alterada independente se para maior ou menor,
             * então envia e-mail para as demais entidades consignatárias avisando sobre a alteração
             */
            if (!TextHelper.isNull(csaCodigoLimiteTaxa) && csaCodigo.equals(csaCodigoLimiteTaxa)) {
                final ListaConsignatariaComTaxasQuery query7 = new ListaConsignatariaComTaxasQuery();
                query7.svcCodigo = svcCodigo;
                final List<TransferObject> consignatarias = query7.executarDTO();
                for (final TransferObject csa : consignatarias) {
                    final String proximoCsaNome = csa.getAttribute(Columns.CSA_NOME).toString();
                    final String proximoCsaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);
                    if (!TextHelper.isNull(proximoCsaEmail)) {
                        try {
                            // Envia e-mail para a consignatária de aviso sobre a alteração da tabela de taxas
                            EnviaEmailHelper.enviarEmailAlteracaoTabelaTaxaLimite(proximoCsaEmail, proximoCsaNome, AcessoSistema.ENTIDADE_CSA, null, coeficientes, null, responsavel);
                        } catch (final ViewHelperException ex) {
                            // Se ocorrerem erros no envio de e-mail de aviso não deve ser desfeito o processo
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }

                final ConsignanteTransferObject cseTO = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                // Envia e-mail para o consignante de aviso sobre a alteração da tabela de taxas
                try {
                    EnviaEmailHelper.enviarEmailAlteracaoTabelaTaxaLimite(cseTO.getCseEmail(), cseTO.getCseNome(), AcessoSistema.ENTIDADE_CSE, null, coeficientes, null, responsavel);
                } catch (final ViewHelperException ex) {
                    // Se ocorrerem erros no envio de e-mail de aviso não deve ser desfeito o processo
                    LOG.error(ex.getMessage(), ex);
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.setServico(svcCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.editando.taxa.de.juros", responsavel));
            log.write();

        } catch (final LogControllerException ex) {
            // Somente se for uma LogControllerException é que não se deve fazer o rollback
            // da transação. Para todas as outras Exception deve-se fazer o rollback.
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> getTaxas(String periodo, String csaCodigo, String svcCodigo, Integer prazo, boolean ativo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            svcCodigo = getSvcTaxaCompartilhada(svcCodigo, somenteComBloqueioCad, responsavel);

            final ListaTaxasJurosQuery query = new ListaTaxasJurosQuery();
            final CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute("PERIODO", periodo);
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            parametrosQuery.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            parametrosQuery.setAttribute("PRAZO", prazo);
            parametrosQuery.setAttribute("ATIVO", ativo);
            parametrosQuery.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
            query.setCriterios(parametrosQuery);

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Metodo para recuperar os limites de taxa de juros cadastrados para um serviço
     *
     * @param svcCodigo
     * @return CustomTransferObject contentdo prazo e valor da taxa
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> getLimiteTaxas(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        final List<TransferObject> retorno = new ArrayList<>();

        try {
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.LTJ_SVC_CODIGO, svcCodigo);

            final int total = limiteTaxaJurosController.countLimiteTaxaJuros(criterio, responsavel);
            final List<TransferObject> limites = limiteTaxaJurosController.listaLimiteTaxaJuros(criterio, 0, total, responsavel);

            if ((limites != null) && (limites.size() > 0)) {
                TransferObject cto = null;
                for (final TransferObject limite : limites) {
                    cto = new CustomTransferObject();
                    cto.setAttribute(Columns.LTJ_PRAZO_REF, limite.getAttribute(Columns.LTJ_PRAZO_REF));
                    cto.setAttribute(Columns.LTJ_JUROS_MAX, limite.getAttribute(Columns.LTJ_JUROS_MAX));
                    cto.setAttribute(Columns.LTJ_VLR_REF, limite.getAttribute(Columns.LTJ_VLR_REF));
                    retorno.add(cto);
                }
            }
        } catch (final LimiteTaxaJurosControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }

        return retorno;
    }


    /**
     * Recupera taxa de juros com valor superior a taxa limite informada
     *
     * @param svcCodigo
     * @param prazoMax
     * @param taxaLimite
     * @param responsavel
     * @return Retorna taxa de juros com valor superior a taxa limite informada
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> getTaxaSuperiorTaxaLimite(String svcCodigo, Short prazoMax, BigDecimal taxaLimite, AcessoSistema responsavel) throws SimulacaoControllerException {
        final List<TransferObject> retorno = new ArrayList<>();

        // Recupera taxas ativas
        final List<TransferObject> taxas = getTaxas(null, null, svcCodigo, null, true, true, responsavel);

        // Recupera prazo anterior para definir a variação entre os prazos
        Short prazoMin = 0;
        final List<TransferObject> lstLimiteTaxa = getLimiteTaxas(svcCodigo, responsavel);
        if ((lstLimiteTaxa != null) && !lstLimiteTaxa.isEmpty()) {
            for (final TransferObject limiteTaxa : lstLimiteTaxa) {
                final Short prazoLimite = !TextHelper.isNull(limiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF)) ? Short.parseShort(limiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF).toString()) : 0;
                if (prazoLimite.compareTo(prazoMax) < 0) {
                    prazoMin = prazoLimite;
                }
            }
        }

        // Seleciona os coeficientes entre o prazo mínimo e máximo que possuem valor superior ao máximo definido
        if ((taxas != null) && !taxas.isEmpty()) {
            for (final TransferObject taxa : taxas) {
                final Short przVlr = !TextHelper.isNull(taxa.getAttribute(Columns.PRZ_VLR)) ? Short.parseShort(taxa.getAttribute(Columns.PRZ_VLR).toString()) : 0;
                final BigDecimal cftVlr = !TextHelper.isNull(taxa.getAttribute(Columns.CFT_VLR)) ? BigDecimal.valueOf(Double.parseDouble(taxa.getAttribute(Columns.CFT_VLR).toString())) : BigDecimal.ZERO;

                if ((przVlr.compareTo(prazoMin) > 0) && (przVlr.compareTo(prazoMax) <= 0) && (cftVlr.compareTo(taxaLimite) > 0)) {
                    retorno.add(taxa);
                }
            }
        }

        return retorno;
    }


    /**
     * Recupera a regras de taxa de juros com valor superior a taxa limite informada
     *
     * @param svcCodigo
     * @param prazoMax
     * @param taxaLimite
     * @param responsavel
     * @return Retorna taxa de juros com valor superior a taxa limite informada
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> getRegraJurosTaxaLimite(String svcCodigo, Short prazoMax, BigDecimal taxaLimite, AcessoSistema responsavel) throws SimulacaoControllerException {
        final List<TransferObject> retorno = new ArrayList<>();
        final CustomTransferObject criterio = new CustomTransferObject();

        criterio.setAttribute("SVC_CODIGO", svcCodigo);
        criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_TABELA_ATIVA);

        try {
         // Recupera regras taxas de juros ativas
            final List<TransferObject> regraTaxaJuros = definicaoTaxaJurosController.listaDefinicaoRegraTaxaJuros(criterio, 0, 0, responsavel);

         // Recupera prazo anterior para definir a variação entre os prazos
            Short prazoMin = 0;
            final List<TransferObject> lstLimiteTaxa = getLimiteTaxas(svcCodigo, responsavel);
            if ((lstLimiteTaxa != null) && !lstLimiteTaxa.isEmpty()) {
                for (final TransferObject limiteTaxa : lstLimiteTaxa) {
                    final Short prazoLimite = !TextHelper.isNull(limiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF)) ? Short.parseShort(limiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF).toString()) : 0;
                    if (prazoLimite.compareTo(prazoMax) < 0) {
                        prazoMin = prazoLimite;
                    }
                }
            }

            // Seleciona as regras de taxa de juros entre o prazo mínimo e máximo que possuem valor superior ao máximo definido
            if ((regraTaxaJuros != null) && !regraTaxaJuros.isEmpty()) {
                for (final TransferObject regraTaxaJuro : regraTaxaJuros) {
                    final Short dtjPrazoFim = Short.parseShort(regraTaxaJuro.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM).toString());
                    final BigDecimal dtjTaxaJuros = !TextHelper.isNull(regraTaxaJuro.getAttribute(Columns.DTJ_TAXA_JUROS)) ? BigDecimal.valueOf(Double.parseDouble(regraTaxaJuro.getAttribute(Columns.DTJ_TAXA_JUROS).toString())) : BigDecimal.ZERO;
                    if ((dtjPrazoFim.compareTo(prazoMin) > 0) && (dtjPrazoFim.compareTo(prazoMax) <= 0) && (dtjTaxaJuros.compareTo(taxaLimite) > 0)) {
                        retorno.add(regraTaxaJuro);
                    }
                }
            }
        } catch (final DefinicaoTaxaJurosControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
        return retorno;
    }

    /**
     * Pesquisa os serviços que possuem o cadastro de taxas bloqueado, de acordo com o parametro TPC_BLOQUEIA_CAD_TAXAS_COMPARTILHADAS.
     *
     * @param responsavel Responsável pela operação.
     * @return Mapeamento de serviços que possuem o cadastro de taxas bloqueado, por serem destino do relacionamento de compartilhamento.
     * Chave: Destino do relacionamento. Valor: Origem do relacionamento.
     * @throws SimulacaoControllerException Exceção padrão da classe.
     */
    @Override
    public Map<String, String> getSvcCadTaxaBloqueado(AcessoSistema responsavel) throws SimulacaoControllerException {
        final Map<String, String> mapSvcBloqueados = new HashMap<>();
        try {
            final boolean temRelacionamentoCompTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);
            final boolean temBloqueioCadTaxasComp = ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CAD_TAXAS_COMPARTILHADAS, responsavel);
            if (temRelacionamentoCompTaxas && temBloqueioCadTaxasComp) {
                // Se o serviço atual é o destino de um relacionamento, então não pode editar as taxas
                final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                queryRel.tntCodigo = CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS;
                queryRel.svcCodigoOrigem = null;
                queryRel.svcCodigoDestino = null;
                final List<TransferObject> svcCodigosRel = queryRel.executarDTO();
                for (final TransferObject cto : svcCodigosRel) {
                    final String svcCodigoOriRel = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                    final String svcCodigoDesRel = cto.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString();
                    if (!svcCodigoOriRel.equals(svcCodigoDesRel)) {
                        mapSvcBloqueados.put(svcCodigoDesRel, svcCodigoOriRel);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return mapSvcBloqueados;
    }

    /**
     * Metodo para retornar o valor da TAC calculada
     *
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param vlrLiberado
     * @param cftVlr
     * @param responsavel
     * @return BigDecimal Valor da TAC calculada
     */
    @Override
    public BigDecimal calculaTAC(String svcCodigo, String csaCodigo, String orgCodigo, BigDecimal vlrLiberado, BigDecimal cftVlr, AcessoSistema responsavel) throws SimulacaoControllerException {
        return simulacaoMetodoBrasileiro.calculaTAC(svcCodigo, csaCodigo, orgCodigo, vlrLiberado, cftVlr, responsavel);
    }

    @Override
    public List<TransferObject> simularConsignacao(String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        return simularConsignacao(null, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, przVlr, adeAnoMesIni, validaBloqSerCnvCsa, false, adePeriodicidade, responsavel);
    }

    @Override
    public List<TransferObject> simularConsignacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        if (!CodedValues.FUN_SIMULAR_RENEGOCIACAO.equals(responsavel.getFunCodigo()) && (przVlr <= 0)) {
            throw new SimulacaoControllerException("mensagem.informe.ade.prazo", responsavel);
        }
        final Object metodo = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);

        // Gera log para para registrar a simulação realizada
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.SIMULAR_CONSIGNACAO, Log.LOG_CONSULTA);
            log.setFunCodigo(CodedValues.FUN_SIM_CONSIGNACAO);
            if (vlrParcela != null) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.consultar.simulacao.por.vlr.parcela", responsavel, vlrParcela.toString(), String.valueOf(przVlr)));
            } else if (vlrLiberado != null) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.consultar.simulacao.por.vlr.liberado", responsavel, vlrLiberado.toString(), String.valueOf(przVlr)));
            }
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        if (TextHelper.isNull(metodo) || CodedValues.MCS_BRASILEIRO.equals(metodo)) {
            return simulacaoMetodoBrasileiro.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, przVlr, adeAnoMesIni, validaBloqSerCnvCsa, utilizaLimiteTaxa, adePeriodicidade, responsavel);
        } else if (CodedValues.MCS_MEXICANO.equals(metodo)) {
            return simulacaoMetodoMexicano.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, przVlr, adeAnoMesIni, validaBloqSerCnvCsa, utilizaLimiteTaxa, adePeriodicidade, responsavel);
        } else if (CodedValues.MCS_INDIANO.equals(metodo)) {
            return simulacaoMetodoIndiano.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, przVlr, adeAnoMesIni, validaBloqSerCnvCsa, utilizaLimiteTaxa, adePeriodicidade, responsavel);
        } else {
            throw new SimulacaoControllerException("mensagem.erro.metodo.calculo.simulacao.naoImplementado", responsavel);
        }
    }

    @Override
    public BigDecimal alterarValorTaxaJuros(boolean alteraVlrLiberado, BigDecimal vlrParcela, BigDecimal vlrLiberado, BigDecimal cftVlrNovo, BigDecimal adeTac, BigDecimal adeOp, int przVlr,
                                            String orgCodigo, String svcCodigo, String csaCodigo, String adePeridiocidade, AcessoSistema responsavel) throws SimulacaoControllerException {

        // Verifica parâmetro que indica se simula por taxas de juros ou coeficientes
        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        final String metodoSimulacao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);
        final boolean metodoIndiano = !TextHelper.isNull(metodoSimulacao) && CodedValues.MCS_INDIANO.equals(metodoSimulacao);

        // Obtém o período atual para simulação
        final Date prazoIni = getPeriodoAtualSimulacao(orgCodigo, responsavel);

        // Calcula os dias de carência
        final int dc = SimulacaoHelper.calculateDC(DateHelper.getSystemDatetime(), prazoIni, orgCodigo, responsavel);

        // Determina se deve ser calculado IOF para a Consignatária
        final boolean calcTacIofCse = simulacaoMetodoBrasileiro.calculaTacIofSimulacao(svcCodigo, responsavel);
        final boolean calcIof = simulacaoMetodoBrasileiro.calculaIofSimulacao(calcTacIofCse, svcCodigo, csaCodigo, responsavel);

        //TODO: deve ser feito para o método mexicano
        if (alteraVlrLiberado) {
            // Calcula novo valor liberado
            if (!metodoIndiano) {
                if (simulacaoPorTaxaJuros) {
                    try {
                        final BigDecimal[] retorno = simulacaoMetodoBrasileiro.calcularValorLiberado(vlrLiberado, vlrParcela, cftVlrNovo, przVlr, dc, prazoIni, svcCodigo, csaCodigo, orgCodigo, calcIof, calcTacIofCse, responsavel);
                        vlrLiberado = retorno[0];
                    } catch (final Exception ex) {
                        throw new SimulacaoControllerException(ex);
                    }
                } else {
                    // Pega a taxa desta ADE (TAC e OP)
                    final BigDecimal taxa = adeTac.add(adeOp);

                    // Valor Liberado = (Valor Prestação / Coeficiente) -  (TAC + OP)
                    vlrLiberado = vlrParcela.divide(cftVlrNovo, 2, java.math.RoundingMode.DOWN).subtract(taxa);
                    if (vlrLiberado.signum() == -1) {
                        vlrLiberado = new BigDecimal("0");
                    }
                }
            } else {
                final BigDecimal cftVlrMensal = cftVlrNovo.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);
                try {
                    final BigDecimal[] retorno = simulacaoMetodoIndiano.calcularValorLiberado(vlrParcela, cftVlrMensal, przVlr, prazoIni, adePeridiocidade, responsavel);
                    vlrLiberado = retorno[0];
                } catch (final ViewHelperException e) {
                    throw new SimulacaoControllerException(e);
                }
            }

            return vlrLiberado;
        } else {
            // Calcula novo valor da parcela
            if (!metodoIndiano) {
                if (simulacaoPorTaxaJuros) {
                    try {
                        final BigDecimal[] retorno = simulacaoMetodoBrasileiro.calcularValorParcela(vlrLiberado, vlrParcela, cftVlrNovo, przVlr, dc, prazoIni, svcCodigo, csaCodigo, orgCodigo, calcIof, calcTacIofCse, adePeridiocidade, responsavel);
                        vlrParcela = retorno[0];
                    } catch (final Exception ex) {
                        throw new SimulacaoControllerException(ex);
                    }
                } else {
                    // Pega a taxa desta ADE (TAC e OP)
                    final BigDecimal taxa = adeTac.add(adeOp);

                    // Valor Prestação = (Valor Liberado + TAC + OP) x Coeficiente
                    vlrParcela = vlrLiberado.add(taxa).multiply(cftVlrNovo);
                }
            } else {
                final BigDecimal cftVlrMensal = cftVlrNovo.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);
                try {
                    final BigDecimal[] retorno = simulacaoMetodoIndiano.calcularValorParcela(vlrLiberado, cftVlrMensal, przVlr, prazoIni, orgCodigo, adePeridiocidade, responsavel);
                    vlrParcela = retorno[0];
                } catch (final ViewHelperException ex) {
                    throw new SimulacaoControllerException(ex);
                }
            }

            return vlrParcela;
        }
    }

    /**
     * Obtém o periodo atual de lançamento
     *
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    private Date getPeriodoAtualSimulacao(String orgCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            return PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
        } catch (final PeriodoException ex) {
            throw new SimulacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> selecionarLinhasSimulacao(List<TransferObject> simulacao, String rseCodigo, BigDecimal rseMargemRest, int qtdeConsignatariasSimulacao, boolean filtrarSomenteLimite, boolean restringirIdadeSimulacao, AcessoSistema responsavel) throws SimulacaoControllerException {
        return selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, filtrarSomenteLimite, restringirIdadeSimulacao, null, CodedValues.FUN_SIM_CONSIGNACAO, responsavel);
    }

    /**
     * Filtra a lista de simulação, definindo quais linhas podem ser escolhidas pelo
     * servidor, de acordo com a margem, valor da parcela, valor liberado mínimo e máximo,
     * e limite de consignatárias no ranking. O parâmetro filtrarSomenteLimite significa
     * que não deve ser olhado as demais questões relativas a simulação, somente o limite
     * de consignatárias no ranking. O parâmetro restringirIdadeSimulacao define se deve ser
     * exibido na lista de simulação as simulações que estão fora do parâmetro de idade mínima
     * e máxima setada pela entidade consignatária ou pelo consignante, caso não seja informada
     * pela consignatária.
     *
     * @param simulacao
     * @param rseCodigo
     * @param rseMargemRest
     * @param qtdeConsignatariasSimulacao
     * @param filtrarSomenteLimite
     * @param restringirIdadeSimulacao
     * @param csaCodigoExclusaoRanking
     * @param funcaoRanking
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> selecionarLinhasSimulacao(List<TransferObject> simulacao, String rseCodigo, BigDecimal rseMargemRest, int qtdeConsignatariasSimulacao, boolean filtrarSomenteLimite, boolean restringirIdadeSimulacao, String csaCodigoExclusaoRanking, String funcaoRanking, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final List<TransferObject> retorno = new ArrayList<>();
            if ((simulacao != null) && (simulacao.size() > 0)) {
                // Monta lista de consignatárias e serviços presentes na simulação
                final List<String> csaCodigos = new ArrayList<>();
                final List<String> svcCodigos = new ArrayList<>();
                for (final TransferObject cto : simulacao) {
                    if (!csaCodigos.contains(cto.getAttribute(Columns.CSA_CODIGO).toString())) {
                        csaCodigos.add(cto.getAttribute(Columns.CSA_CODIGO).toString());
                    }
                    if (!svcCodigos.contains(cto.getAttribute(Columns.SVC_CODIGO).toString())) {
                        svcCodigos.add(cto.getAttribute(Columns.SVC_CODIGO).toString());
                    }
                }

                Integer idadeServidor = null;
                if (!TextHelper.isNull(rseCodigo)) {
                    try {
                        final ObtemDataNascServidorQuery dataNascQuery = new ObtemDataNascServidorQuery(rseCodigo);
                        final List<Date> lstDataNasc = dataNascQuery.executarLista();
                        final Date serDataNasc = (lstDataNasc != null) && !lstDataNasc.isEmpty() ? lstDataNasc.get(0) : null;
                        if (serDataNasc != null) {
                            idadeServidor = DateHelper.getAge(serDataNasc);
                        }
                    } catch (final HQueryException e) {
                        LOG.error(e.getMessage(), e);
                        throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
                    }
                }

                // Monta o cache de parâmetro de idade mínima e máxima setado pelo consignante que será usado caso o da consignatária não esteja parametrizado
                final Map<String, Map<String, Integer>> paramIdadeCse = new HashMap<>();
                for (final String svcCodigo : svcCodigos) {
                    Integer idadeMinCse = null, idadeMaxCse = null;
                    if (TextHelper.isNull(idadeMinCse) && TextHelper.isNull(idadeMaxCse)) {
                        final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);

                        idadeMinCse = TextHelper.isNum(paramSvcCse.getTpsIdadeMinimaSerSolicSimulacao()) ? Integer.parseInt(paramSvcCse.getTpsIdadeMinimaSerSolicSimulacao()) : null;
                        idadeMaxCse = TextHelper.isNum(paramSvcCse.getTpsIdadeMaximaSerSolicSimulacao()) ? Integer.parseInt(paramSvcCse.getTpsIdadeMaximaSerSolicSimulacao()) : null;

                        Map<String, Integer> valores = paramIdadeCse.get(svcCodigo);
                        if (valores == null) {
                            valores = new HashMap<>();
                            paramIdadeCse.put(svcCodigo, valores);
                        }

                        valores.put("IDADE_MIN", idadeMinCse);
                        valores.put("IDADE_MAX", idadeMaxCse);
                    }
                }

                // Parâmetros de sistema
                final boolean ordenacaoSequencial = ParamSist.paramEquals(CodedValues.TPC_RANKING_ORDENACAO_SEQUENCIAL, CodedValues.TPC_SIM, responsavel);
                // Parâmetros de convênio (CSA/SVC):
                final List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MINIMO);
                tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MAXIMO);
                tpsCodigos.add(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING);
                tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR);
                tpsCodigos.add(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO);

                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigos, csaCodigos, tpsCodigos, false, responsavel);

                // Monta cache com os parâmetros setados pelas consignatárias para os serviços listados na simulação
                final HashMap<Object, HashMap<Object, Object>> vlrLibMax = new HashMap<>();
                final HashMap<Object, HashMap<Object, Object>> vlrLibMin = new HashMap<>();
                final HashMap<Object, HashMap<Object, Object>> paramCsaContaLimiteRanking = new HashMap<>();
                final HashMap<Object, HashMap<Object, Object>> paramCsaPercMargemSimulador = new HashMap<>();
                final HashMap<String, HashMap<String, TransferObject>> paramIdadeMinMax = new HashMap<>();
                for (final TransferObject next : paramSvcCsa) {
                    final String svcCodigo = next.getAttribute(Columns.PSC_SVC_CODIGO).toString();
                    final String csaCodigo = next.getAttribute(Columns.PSC_CSA_CODIGO).toString();

                    if (CodedValues.TPS_VLR_LIBERADO_MINIMO.equals(next.getAttribute(Columns.TPS_CODIGO).toString())) {
                        HashMap<Object, Object> valor = vlrLibMin.get(csaCodigo);
                        if (valor == null) {
                            valor = new HashMap<>();
                            vlrLibMin.put(csaCodigo, valor);
                        }
                        valor.put(svcCodigo, next.getAttribute(Columns.PSC_VLR));

                    } else if (CodedValues.TPS_VLR_LIBERADO_MAXIMO.equals(next.getAttribute(Columns.TPS_CODIGO).toString())) {
                        HashMap<Object, Object> valor = vlrLibMax.get(csaCodigo);
                        if (valor == null) {
                            valor = new HashMap<>();
                            vlrLibMax.put(csaCodigo, valor);
                        }
                        valor.put(svcCodigo, next.getAttribute(Columns.PSC_VLR));

                    } else if (CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING.equals(next.getAttribute(Columns.TPS_CODIGO).toString())) {
                        HashMap<Object, Object> valor = paramCsaContaLimiteRanking.get(csaCodigo);
                        if (valor == null) {
                            valor = new HashMap<>();
                            paramCsaContaLimiteRanking.put(csaCodigo, valor);
                        }
                        valor.put(svcCodigo, next.getAttribute(Columns.PSC_VLR));

                    } else if (CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR.equals(next.getAttribute(Columns.TPS_CODIGO).toString())) {
                        HashMap<Object, Object> valor = paramCsaPercMargemSimulador.get(csaCodigo);
                        if (valor == null) {
                            valor = new HashMap<>();
                            paramCsaPercMargemSimulador.put(csaCodigo, valor);
                        }
                        valor.put(svcCodigo, next.getAttribute(Columns.PSC_VLR));

                    } else if (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(next.getAttribute(Columns.TPS_CODIGO).toString())) {
                        HashMap<String, TransferObject> valor = paramIdadeMinMax.get(csaCodigo.toString());

                        if (valor == null) {
                            valor = new HashMap<>();
                            paramIdadeMinMax.put(csaCodigo.toString(), valor);
                        }
                        final TransferObject idadeTO = new CustomTransferObject();
                        if (!TextHelper.isNull(next.getAttribute(Columns.PSC_VLR))) {
                            idadeTO.setAttribute("IDADE_MIN", next.getAttribute(Columns.PSC_VLR));
                        }
                        if (!TextHelper.isNull(next.getAttribute(Columns.PSC_VLR_REF))) {
                            idadeTO.setAttribute("IDADE_MAX", next.getAttribute(Columns.PSC_VLR_REF));
                        }
                        valor.put(svcCodigo, idadeTO);
                    }
                }

                final Set<BigDecimal> taxasDistintas = new HashSet<>();
                CustomTransferObject coeficiente = null;
                String svcCodigo = null;
                String csaCodigo = null;
                String csaNome = null;
                String csaTitulo = null;
                Integer motivoIndisponibilidade = null;
                String prazo = null;
                BigDecimal vlrParcela = null;
                BigDecimal vlrLiberado = null;
                BigDecimal cftVlr = null;
                int ranking = -1;
                boolean vlrOk = false;
                boolean csaNaoConsideradaLimiteRanking = false;
                boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);

                for (int i = 0; i < simulacao.size(); i++) {
                    boolean incluiRetorno = true;
                    motivoIndisponibilidade = null;
                    coeficiente = (CustomTransferObject) simulacao.get(i);

                    svcCodigo = coeficiente.getAttribute(Columns.SVC_CODIGO).toString();
                    csaCodigo = (String) coeficiente.getAttribute(Columns.CSA_CODIGO);

                    if (!TextHelper.isNull(csaCodigoExclusaoRanking) && csaCodigoExclusaoRanking.equals(csaCodigo)) {
                        continue;
                    }

                    csaNome = (String) coeficiente.getAttribute(Columns.CSA_NOME);
                    csaTitulo = (String) coeficiente.getAttribute("TITULO");

                    vlrParcela = coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) coeficiente.getAttribute("VLR_PARCELA");
                    vlrLiberado = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) coeficiente.getAttribute("VLR_LIBERADO");
                    cftVlr = coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) coeficiente.getAttribute(Columns.CFT_VLR);
                    prazo = coeficiente.getAttribute(Columns.PRZ_VLR).toString();
                    ranking = coeficiente.getAttribute("RANKING") != null ? Integer.parseInt(coeficiente.getAttribute("RANKING").toString()) : -1;
                    csaNaoConsideradaLimiteRanking = (paramCsaContaLimiteRanking.get(csaCodigo) != null) &&
                            "N".equalsIgnoreCase(paramCsaContaLimiteRanking.get(csaCodigo).toString());

                    if (filtrarSomenteLimite) {
                        // Se deve filtrar somente pelo limite de consignatárias no ranking,
                        // então não verifica o valor liberado ou o valor da prestação
                        // de acordo com a margem ou os parâmetros
                        vlrOk = true;
                    } else {                        
                        if(!exibeCETMinMax) {
                        	vlrOk = (vlrParcela.compareTo(rseMargemRest) <= 0) && (vlrLiberado.doubleValue() > 0);
	                        if ((vlrParcela.compareTo(rseMargemRest) > 0) && (vlrLiberado.doubleValue() > 0)) {
	                            motivoIndisponibilidade = 1;
	                        }
                        } else {
                        	vlrOk = true;
                        }

                        // Verifica se a Consignatária trabalha com o prazo
                        final boolean possuiPrazo = ((cftVlr.signum() > 0) && PrazoSvcCsa.temPrazo(svcCodigo, csaCodigo, prazo, responsavel));
                        if (!possuiPrazo) {
                        	if(exibeCETMinMax) {
                        		vlrOk = false; 
                        	}                        	
                            motivoIndisponibilidade = 3;
                        }

                        if (vlrLibMin.get(csaCodigo) != null) {
                            final Map<Object, Object> param = vlrLibMin.get(csaCodigo);
                            if (!TextHelper.isNull(param.get(svcCodigo))) {
                                if (vlrOk) {
                                    try {
                                        vlrOk = vlrLiberado.doubleValue() >= Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"));
                                    } catch (NumberFormatException | ParseException ex) {
                                        LOG.error("Conteúdo inválido para o parâmetro de Valor Liberado Mínimo para a Consignatária '" + csaNome + "'");
                                        vlrOk = false;
                                    }
                                }

                                try {
                                    if ((vlrLiberado.doubleValue() < Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"))) && ((vlrLiberado.intValue() > 0) && possuiPrazo)) {
                                        motivoIndisponibilidade = 5;
                                    }
                                } catch (NumberFormatException | ParseException ex) {
                                    LOG.error(ex.getMessage());
                                }
                            }
                        }

                        if (vlrLibMax.get(csaCodigo) != null) {
                            final Map<Object, Object> param = vlrLibMax.get(csaCodigo);
                            if (!TextHelper.isNull(param.get(svcCodigo))) {
                                if (vlrOk) {
                                    try {
                                        vlrOk = vlrLiberado.doubleValue() <= Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"));
                                    } catch (NumberFormatException | ParseException ex) {
                                        LOG.error("Conteúdo inválido para o parâmetro de Valor Liberado Máximo para a Consignatária '" + csaNome + "'");
                                        vlrOk = false;
                                    }
                                }

                                try {
                                    if ((vlrLiberado.doubleValue() > Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"))) && possuiPrazo) {
                                        motivoIndisponibilidade = 4;
                                    }
                                } catch (NumberFormatException | ParseException ex) {
                                    LOG.error(ex.getMessage());
                                }
                            }
                        }


                        if (paramCsaPercMargemSimulador.get(csaCodigo) != null) {
                            final Map<Object, Object> param = paramCsaPercMargemSimulador.get(csaCodigo);
                            if (!TextHelper.isNull(param.get(svcCodigo))) {
                                if (vlrOk) {
                                    try {
                                        double percentualMargem = Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"));
                                        percentualMargem = percentualMargem / 100.00;
                                        percentualMargem = percentualMargem < 0 ? 1 : percentualMargem > 1 ? 1 : percentualMargem;
                                        vlrOk = vlrParcela.doubleValue() <= (rseMargemRest.doubleValue() * percentualMargem);
                                    } catch (NumberFormatException | ParseException ex) {
                                        LOG.error("Conteúdo inválido para o parâmetro de Percentual da Margem permitida no Simulador para a Consignatária '" + csaNome + "'");
                                        vlrOk = false;
                                    }
                                }

                                try {
                                    double percentualMargem = Double.parseDouble(NumberHelper.reformat(param.get(svcCodigo).toString(), NumberHelper.getLang(), "en"));
                                    percentualMargem = percentualMargem / 100.00;
                                    percentualMargem = percentualMargem < 0 ? 1 : percentualMargem > 1 ? 1 : percentualMargem;
                                    if ((vlrParcela.doubleValue() > (rseMargemRest.doubleValue() * percentualMargem)) && possuiPrazo) {
                                        motivoIndisponibilidade = 2;
                                    }
                                } catch (NumberFormatException | ParseException ex) {
                                    LOG.error(ex.getMessage());
                                }
                            }
                        }

                        // Validação pela idade do servidor
                        if (restringirIdadeSimulacao) {
                            Integer idadeMin = null, idadeMax = null;
                            final Map<String, TransferObject> valores = paramIdadeMinMax.get(csaCodigo);
                            if (valores != null) {
                                final TransferObject cto = valores.get(svcCodigo);
                                idadeMin = (cto != null) && !TextHelper.isNull(cto.getAttribute("IDADE_MIN")) ? Integer.parseInt(cto.getAttribute("IDADE_MIN").toString()) : null;
                                idadeMax = (cto != null) && !TextHelper.isNull(cto.getAttribute("IDADE_MAX")) ? Integer.parseInt(cto.getAttribute("IDADE_MAX").toString()) : null;

                            }
                            // Se o parâmetro não foi setado pela consignatária, utiliza o parâmetro setado pelo consignante para o serviço
                            if ((idadeMin == null) && (idadeMax == null)) {
                                if ((paramIdadeCse.get(svcCodigo) != null) && (paramIdadeCse.get(svcCodigo).get("IDADE_MIN") != null)) {
                                    idadeMin = paramIdadeCse.get(svcCodigo).get("IDADE_MIN");
                                }

                                if ((paramIdadeCse.get(svcCodigo) != null) && (paramIdadeCse.get(svcCodigo).get("IDADE_MAX") != null)) {
                                    idadeMax = paramIdadeCse.get(svcCodigo).get("IDADE_MAX");
                                }
                            }
                            if (((idadeMin != null) || (idadeMax != null)) && (idadeServidor != null)) {
                                incluiRetorno = (((idadeMin == null) || (idadeServidor.compareTo(idadeMin) >= 0)) && ((idadeMax == null) || (idadeServidor.compareTo(idadeMax) <= 0)));
                            }
                        }
                    }

                    // Se tem limite de consignatárias no ranking
                    if (qtdeConsignatariasSimulacao != Integer.MAX_VALUE) {
                        if (csaNaoConsideradaLimiteRanking || ((ranking <= qtdeConsignatariasSimulacao) && !vlrOk)) {
                            // Se a consignatária não deve contar nos limites de ranking  OU
                            // a consignatária ficou fora por outra questão (vlr liberado por exemplo) e
                            // ela estaria entre o X primeiros, então incrementa o total dando lugar para outra entrar
                            if (!ordenacaoSequencial) {
                                qtdeConsignatariasSimulacao++;
                            }
                        } else if (ordenacaoSequencial) {
                            // Se é ordenação sequencial (ranking de taxas) então contabiliza a quantidade
                            // de taxas distintas para limitação do ranking
                            taxasDistintas.add(new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString()));
                        }
                    }

                    // Permite apenas X consignatárias no Ranking de Simulação, ou as Cooperativas
                    if (!ordenacaoSequencial) {
                        vlrOk = vlrOk && ((ranking <= qtdeConsignatariasSimulacao) || csaNaoConsideradaLimiteRanking);
                        if ((ranking > qtdeConsignatariasSimulacao)) {
                            motivoIndisponibilidade = 6;
                        }
                    } else {
                        vlrOk = vlrOk && ((taxasDistintas.size() <= qtdeConsignatariasSimulacao) || csaNaoConsideradaLimiteRanking);
                        if ((taxasDistintas.size() > qtdeConsignatariasSimulacao)) {
                            motivoIndisponibilidade = 6;
                        }
                    }

                    if ((motivoIndisponibilidade == null) && !responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO)) {
                        motivoIndisponibilidade = 0;
                    }

                    coeficiente.setAttribute("TITULO", csaTitulo.toUpperCase());
                    coeficiente.setAttribute("MOTIVO_INDISPONIBILIDADE", motivoIndisponibilidade);

                    // Seta no transfer object o flag dizendo se é permitido ou não
                    coeficiente.setAttribute("OK", Boolean.valueOf(vlrOk));
                    if (!vlrOk) {
                        coeficiente.setAttribute("RELEVANCIA", Integer.valueOf(CodedValues.CSA_NAO_PROMOVIDA) + 1); //DESENV-15679: linha de simulação indisponível tem relevância a menor possível.
                    }
                    if (incluiRetorno) {
                        // Inclui simulação no retorno
                        retorno.add(coeficiente);
                    }
                }
            }

            if ((retorno.size() > 0) && !TextHelper.isNull(retorno.get(0).getAttribute("RELEVANCIA")) && !TextHelper.isNull(retorno.get(0).getAttribute("SIMULA_POR_PARCELA"))) {
            	boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
            	// Ordena as consignatárias
            	if (exibeCETMinMax) {
                    // Ordena pelo CFT_VLR_MINIMO em ordem crescente
            		Collections.sort(retorno, (o1, o2) -> {
            			final int relevanciaO1 = (Integer) o1.getAttribute("RELEVANCIA");  //DESENV-15679: ordenação pela relevância (csas promovidas) tem peso maior (peso = 2) do que pela ordem e é sempre decrescente.
                        final int relevanciaO2 = (Integer) o2.getAttribute("RELEVANCIA");
                        final int relevanciaDif = relevanciaO1 - relevanciaO2;
                        
            		    BigDecimal v1 = o1.getAttribute("CFT_VLR_MINIMO_FUN_" + funcaoRanking) != null ? (BigDecimal) o1.getAttribute("CFT_VLR_MINIMO_FUN_" + funcaoRanking) : (BigDecimal) o1.getAttribute(Columns.CFT_VLR_MINIMO);
            		    BigDecimal v2 = o2.getAttribute("CFT_VLR_MINIMO_FUN_" + funcaoRanking) != null ? (BigDecimal) o2.getAttribute("CFT_VLR_MINIMO_FUN_" + funcaoRanking) : (BigDecimal) o2.getAttribute(Columns.CFT_VLR_MINIMO);
            		    // Trata valores nulos como maiores (vão para o final)
            		    if (v1 == null) v1 = BigDecimal.valueOf(Double.MAX_VALUE);
            		    if (v2 == null) v2 = BigDecimal.valueOf(Double.MAX_VALUE);

            		    v1 = BigDecimal.valueOf(2 * relevanciaDif).add(v1);
            		    int result = v1.compareTo(v2); // crescente
            		    if (result == 0) {
                            result = (o1.getAttribute("TITULO_" + funcaoRanking) != null ? (String) o1.getAttribute("TITULO_" + funcaoRanking) : o1.getAttribute("TITULO").toString())
                                    .compareTo(o2.getAttribute("TITULO_" + funcaoRanking) != null ? (String) o2.getAttribute("TITULO_" + funcaoRanking) : o2.getAttribute("TITULO").toString());
                        }
            		    return result;
            		});

                } else if ((Boolean) retorno.get(0).getAttribute("SIMULA_POR_PARCELA")) {
                    // Se a simulação é pela parcela, então a ordenação é feita
                    // de forma decrescente pelo valor liberado
                    Collections.sort(retorno, (o1, o2) -> {
                        final int relevanciaO1 = (Integer) o1.getAttribute("RELEVANCIA");  //DESENV-15679: ordenação pela relevância (csas promovidas) tem peso maior (peso = 2) do que pela ordem e é sempre decrescente.
                        final int relevanciaO2 = (Integer) o2.getAttribute("RELEVANCIA");
                        final int relevanciaDif = relevanciaO1 - relevanciaO2;

                        int result = (2 * relevanciaDif) + ((o2.getAttribute("VLR_ORDEM_" + funcaoRanking) != null ? (BigDecimal) o2.getAttribute("VLR_ORDEM_" + funcaoRanking) : (BigDecimal) o2.getAttribute("VLR_ORDEM"))
                                .compareTo(o1.getAttribute("VLR_ORDEM_" + funcaoRanking) != null ? (BigDecimal) o1.getAttribute("VLR_ORDEM_" + funcaoRanking) : (BigDecimal) o1.getAttribute("VLR_ORDEM")));
                        if (result == 0) {
                            result = (o1.getAttribute("TITULO_" + funcaoRanking) != null ? (String) o1.getAttribute("TITULO_" + funcaoRanking) : o1.getAttribute("TITULO").toString())
                                    .compareTo(o2.getAttribute("TITULO_" + funcaoRanking) != null ?  (String) o2.getAttribute("TITULO_" + funcaoRanking) : o2.getAttribute("TITULO").toString());
                        }
                        return result;
                    });

                } else {
                    // Se a simulação é pelo valor liberado, então a ordenação é feita
                    // de forma crescente pela parcela
                    Collections.sort(retorno, (o1, o2) -> {
                        final int relevanciaO1 = (Integer) o1.getAttribute("RELEVANCIA"); //DESENV-15679: ordenação pela relevância (csas promovidas) tem peso maior (peso = 2) do que pela ordem e é sempre decrescente.
                        final int relevanciaO2 = (Integer) o2.getAttribute("RELEVANCIA");
                        final int relevanciaDif = relevanciaO1 - relevanciaO2;

                        int result = (2 * relevanciaDif) + (o1.getAttribute("VLR_ORDEM_" + funcaoRanking) != null ? (BigDecimal) o1.getAttribute("VLR_ORDEM_" + funcaoRanking) : (BigDecimal) o1.getAttribute("VLR_ORDEM"))
                                .compareTo(o2.getAttribute("VLR_ORDEM_" + funcaoRanking) != null ? (BigDecimal) o2.getAttribute("VLR_ORDEM_" + funcaoRanking) : (BigDecimal) o2.getAttribute("VLR_ORDEM"));
                        if (result == 0) {
                            result = (o1.getAttribute("TITULO_" + funcaoRanking) != null ? (String) o1.getAttribute("TITULO_" + funcaoRanking) : o1.getAttribute("TITULO").toString())
                                    .compareTo(o2.getAttribute("TITULO_" + funcaoRanking) != null ?  (String) o2.getAttribute("TITULO_" + funcaoRanking) : o2.getAttribute("TITULO").toString());
                        }
                        return result;
                    });
                }
            }

            setaRankingSimulacao(retorno, funcaoRanking, responsavel);

            return retorno;

        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> buscarRegrasTaxasJuros(TransferObject criterio, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final BuscarDefinicaoTaxaJurosQuery query = new BuscarDefinicaoTaxaJurosQuery();
            query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
            query.orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
            query.svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
            query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            query.idade = (Integer) criterio.getAttribute("RSE_IDADE");
            query.tempServico = (Integer) criterio.getAttribute("RSE_TEMPO_SERVICO");
            query.salario = (BigDecimal) criterio.getAttribute(Columns.RSE_SALARIO);
            query.margem = (BigDecimal) criterio.getAttribute(Columns.RSE_MARGEM);
            query.valorTotal = (BigDecimal) criterio.getAttribute(Columns.ADE_VLR_LIQUIDO);
            query.valorContrato = (BigDecimal) criterio.getAttribute(Columns.ADE_VLR);
            query.prazo = (Integer) criterio.getAttribute(Columns.PRZ_VLR);

            final boolean buscaFunc = (criterio.getAttribute("funcSimu") != null) && criterio.getAttribute("funcSimu").equals(true);
            if (buscaFunc) {
                final List<String> funcoes = new ArrayList<>();
                if (responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO)) {
                    funcoes.add(CodedValues.FUN_SIM_CONSIGNACAO);
                }
                if (responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE)) {
                    funcoes.add(CodedValues.FUN_SOLICITAR_PORTABILIDADE);
                }
                if (responsavel.temPermissao(CodedValues.FUN_SIMULAR_RENEGOCIACAO)) {
                    funcoes.add(CodedValues.FUN_SIMULAR_RENEGOCIACAO);
                }
                query.funCodigos = funcoes;
            }

            final List<TransferObject> dtos = query.executarDTO();
            return verificaDtjDuplicados(dtos);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject getParamSvcCsaMensagemSolicitacaoOutroSvc(String svcCodigo, String csaCodigo, short prazo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaParamMensagemSolicitacaoOutroSvcQuery query = new ListaParamMensagemSolicitacaoOutroSvcQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.dia = dia;
            query.prazo = prazo;
            query.maxResults = 1;

            final List<TransferObject> resultado = query.executarDTO();
            return ((resultado != null) && !resultado.isEmpty() ? resultado.get(0) : null);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void copiaTaxaJuros(String svcCodigo, String svcCodigoDestino, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            // Seleciona prazos ativos.
            final List<PrazoTransferObject> prazosAtivos = findPrazoCsaByServico(svcCodigoDestino, csaCodigo, responsavel);

            final List<String> prazosDestino = new ArrayList<>();
            for (final PrazoTransferObject prazo : prazosAtivos) {
                prazosDestino.add(prazo.getAttribute(Columns.PRZ_VLR).toString());
            }

            // Seleciona coeficientes cadastrados.
            List<TransferObject> coeficientes = getTaxas(null, csaCodigo, svcCodigo, null, false, true, responsavel);
            if ((coeficientes == null) || coeficientes.isEmpty()) {
                // Se não encontrou taxas com data fim vigência nula, procura por aquela que
                // tem fim vigência maior que data atual e estará ativa
                coeficientes = getTaxas(null, csaCodigo, svcCodigo, null, true, true, responsavel);
            }

            if ((coeficientes == null) || coeficientes.isEmpty()) {
                throw new SimulacaoControllerException("mensagem.erro.nao.possivel.copiar.taxas.servico", responsavel);
            }

            final List<TransferObject> coeficientesDestino = new ArrayList<>();
            for (final TransferObject coeficiente : coeficientes) {
                if (prazosDestino.remove(coeficiente.getAttribute(Columns.PRZ_VLR).toString())) {
                    coeficientesDestino.add(coeficiente);
                }
            }

            final float valorZerado = 0;
            for (final String prazo : prazosDestino) {
                final TransferObject taxaZerada = new CustomTransferObject();
                taxaZerada.setAttribute(Columns.CFT_VLR, valorZerado);
                taxaZerada.setAttribute(Columns.PRZ_VLR, Short.parseShort(prazo));
                coeficientesDestino.add(taxaZerada);
            }

            setTaxaJuros(csaCodigo, svcCodigoDestino, coeficientesDestino, responsavel);

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
    }

    private int intParamSvcDiasVigenciaValue(String svcCodigo, ParamSvcTO paramSvcCse) {
        return ((paramSvcCse != null) && (paramSvcCse.getTpsDiasVigenciaCet() != null)) ? paramSvcCse.getTpsDiasVigenciaCet().intValue() : 0;
    }

    @Override
    public List<TransferObject> lstRegistrosSolicitacao(String adeCodigo, List<String> tisCodigos, List<String> ssoCodigos, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final ListaSolicitacaoQuery query = new ListaSolicitacaoQuery();
            query.adeCodigo = adeCodigo;
            query.tisCodigos = tisCodigos;
            query.ssoCodigos = ssoCodigos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> verificaDtjDuplicados(List<TransferObject> dtj) {
        final List<TransferObject> result = new ArrayList<>();

        if ((dtj != null) && !dtj.isEmpty()) {
            for (final TransferObject dto1 : dtj) {
                boolean found = false;

                for (final TransferObject dto2 : result) {
                    final BigDecimal dtjTaxa1 = (BigDecimal) dto1.getAttribute(Columns.CFT_VLR);
                    final BigDecimal dtjTaxa2 = (BigDecimal) dto2.getAttribute(Columns.CFT_VLR);

                    final Integer dtjPrazo1 = (Integer) dto1.getAttribute(Columns.PRZ_VLR);
                    final Integer dtjPrazo2 = (Integer) dto2.getAttribute(Columns.PRZ_VLR);

                    final String csaCodigo1 = (String) dto1.getAttribute(Columns.CSA_CODIGO);
                    final String csaCodigo2 = (String) dto2.getAttribute(Columns.CSA_CODIGO);

                    final String svcCodigo1 = (String) dto1.getAttribute(Columns.SVC_CODIGO);
                    final String svcCodigo2 = (String) dto2.getAttribute(Columns.SVC_CODIGO);

                    final String funCodigo1 = dto1.getAttribute(Columns.DTJ_FUN_CODIGO) != null ? (String) dto1.getAttribute(Columns.DTJ_FUN_CODIGO) : "";
                    final String funCodigo2 = dto2.getAttribute(Columns.DTJ_FUN_CODIGO) != null ? (String) dto2.getAttribute(Columns.DTJ_FUN_CODIGO) : "";

                    if ((dtjTaxa1.compareTo(dtjTaxa2) == 0) && (dtjPrazo1 == dtjPrazo2) && csaCodigo1.equals(csaCodigo2) && svcCodigo1.equals(svcCodigo2) && funCodigo1.equals(funCodigo2)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    result.add(dto1);
                }
            }
        }

        return result;
    }


    /**
     * Retorna a lista de coeficientes/taxas/CETs para serem utilizados na simulação
     * @param csaCodigo
     * @param svcCodigo
     * @param orgCodigo
     * @param rseCodigo
     * @param vlrParcela
     * @param vlrLiberado
     * @param numParcelas
     * @param validaBloqSerCnvCsa
     * @param utilizaLimiteTaxa
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> getCoeficienteSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short numParcelas, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, AcessoSistema responsavel) throws SimulacaoControllerException {
        List<TransferObject> coeficientes = null;
        try {
            if (!utilizaLimiteTaxa) {
                final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                coeficientes = getCoeficienteAtivo(csaCodigo, svcCodigo, orgCodigo, rseCodigo, numParcelas, dia, validaBloqSerCnvCsa, vlrParcela, vlrLiberado, responsavel);
            } else {
                final ListaLimiteTaxaPorPrazoCsaQuery query = new ListaLimiteTaxaPorPrazoCsaQuery();
                query.svcCodigo = svcCodigo;
                query.csaCodigo = csaCodigo;
                query.orgCodigo = orgCodigo;
                query.prazo = numParcelas;
                coeficientes = query.executarDTO();
            }

            // Verifica se foi passado um código de registro servidor, e caso positivo, verifica se este tem associação a posto
            if (!TextHelper.isNull(rseCodigo)) {
                final RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, responsavel);
                final String posCodigo = rse.getPosCodigo();
                // Se o registro servidor está associado a um posto, verifica se existem bloqueios de posto por CSA/SVC para solicitação
                if (!TextHelper.isNull(posCodigo)) {
                    final ListaBloqueioSolicitacaoPorPostoCsaSvcQuery bloqQuery = new ListaBloqueioSolicitacaoPorPostoCsaSvcQuery();
                    bloqQuery.posCodigo = posCodigo;
                    final List<TransferObject> lstBloqueios = bloqQuery.executarDTO();
                    // Caso existam bloqueios, filtra a listagem de coeficientes removendo do resultado os convênios bloqueados
                    if ((lstBloqueios != null) && !lstBloqueios.isEmpty()) {
                        final Set<String> hashBloqueios = new HashSet<>();
                        for (final TransferObject bloqueio : lstBloqueios) {
                            final String csaCodigoBloqueado = bloqueio.getAttribute(Columns.CSA_CODIGO).toString();
                            final String svcCodigoBloqueado = bloqueio.getAttribute(Columns.SVC_CODIGO).toString();
                            hashBloqueios.add(String.format(CHAVE_HASH_BLOQ_POSTO_CSA_SVC, csaCodigoBloqueado, svcCodigoBloqueado));
                        }

                        final List<TransferObject> coeficientesFiltrados = new ArrayList<>();
                        for (final TransferObject coeficiente : coeficientes) {
                            final String csaCodigoResultado = coeficiente.getAttribute(Columns.CSA_CODIGO).toString();
                            final String svcCodigoResultado = coeficiente.getAttribute(Columns.SVC_CODIGO).toString();
                            if (!hashBloqueios.contains(String.format(CHAVE_HASH_BLOQ_POSTO_CSA_SVC, csaCodigoResultado, svcCodigoResultado))) {
                                coeficientesFiltrados.add(coeficiente);
                            }
                        }

                        coeficientes = coeficientesFiltrados;
                    }
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
        return coeficientes;
    }


    /**
     * Caso esteja habilitado a definicao taxa de juros, realiza um ajuste na lista
     * de coeficientes para remover o coeficiente definido na tabela coeficientes, e
     * inclui o coeficiente baseado na tabela de definicao de taxa de juros
     */
    private List<TransferObject> ajustarListaComDefinicaoTaxaJuros(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, AcessoSistema responsavel, List<TransferObject> coeficientes) throws SimulacaoControllerException {
        try {
            final BigDecimal valorTotal = (vlrParcela != null ? vlrParcela.multiply(new BigDecimal(przVlr)) : null);
            final List<TransferObject> defTaxaJurosList = buscarDefinicaoTaxaJuros(csaCodigo, orgCodigo, svcCodigo, rseCodigo, valorTotal, vlrLiberado, (int) przVlr, responsavel);

            if ((defTaxaJurosList != null) && !defTaxaJurosList.isEmpty()) {

                // Cria mapa com os dados de definição de taxa por CSA/SVC
                final Map<String, List<TransferObject>> defTaxaJurosMap = new HashMap<>();
                for (final TransferObject dtjTO : defTaxaJurosList) {
                    final String csaCodigoLinha = dtjTO.getAttribute(Columns.CSA_CODIGO).toString();
                    final String svcCodigoLinha = dtjTO.getAttribute(Columns.SVC_CODIGO).toString();
                    final String chaveLinha = String.format(CHAVE_HASH_DEF_TX_JUROS_CSA_SVC, csaCodigoLinha, svcCodigoLinha);
                    List<TransferObject> lst = defTaxaJurosMap.get(chaveLinha);
                    if (lst == null) {
                        lst = new ArrayList<>();
                        defTaxaJurosMap.put(chaveLinha, lst);
                    }
                    lst.add(dtjTO);
                }

                // Cria mapa com os dados de coeficiente por CSA/SVC
                final Map<String, List<TransferObject>> coeficientesMap = new HashMap<>();
                for (final TransferObject cftTO : coeficientes) {
                    final String csaCodigoLinha = cftTO.getAttribute(Columns.CSA_CODIGO).toString();
                    final String svcCodigoLinha = cftTO.getAttribute(Columns.SVC_CODIGO).toString();
                    final String chaveLinha = String.format(CHAVE_HASH_DEF_TX_JUROS_CSA_SVC, csaCodigoLinha, svcCodigoLinha);
                    List<TransferObject> lst = coeficientesMap.get(chaveLinha);
                    if (lst == null) {
                        lst = new ArrayList<>();
                        coeficientesMap.put(chaveLinha, lst);
                    }
                    lst.add(cftTO);
                }

                // Para cada coeficiente/taxa, procura regra de definição para mesma CSA e SVC para atualizar a configuração
                for (final TransferObject cft : coeficientes) {
                    //final List<TransferObject> lst = defTaxaJurosList.stream().filter(defTaxaJuros -> cft.getAttribute(Columns.CSA_CODIGO).equals(defTaxaJuros.getAttribute(Columns.CSA_CODIGO))
                    //        && cft.getAttribute(Columns.SVC_CODIGO).equals(defTaxaJuros.getAttribute(Columns.SVC_CODIGO))).collect(Collectors.toList());

                    final String csaCodigoLinha = cft.getAttribute(Columns.CSA_CODIGO).toString();
                    final String svcCodigoLinha = cft.getAttribute(Columns.SVC_CODIGO).toString();
                    final String chaveLinha = String.format(CHAVE_HASH_DEF_TX_JUROS_CSA_SVC, csaCodigoLinha, svcCodigoLinha);
                    final List<TransferObject> lst = defTaxaJurosMap.get(chaveLinha);

                    if ((lst != null) && !lst.isEmpty()) {
                        final HashMap<String, List<BigDecimal>> newTransfer = new HashMap<>();
                        for (final TransferObject dtj : lst) {
                            final short prazoCoeficiente = Short.parseShort(cft.getAttribute(Columns.PRZ_VLR).toString());
                            final short prazoDtjIni = Short.parseShort(dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_INI).toString());
                            final short prazoDtjFim = Short.parseShort(dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM).toString());

                            if (((prazoCoeficiente < prazoDtjIni) || (prazoCoeficiente > prazoDtjFim))) {
                                continue;
                            }

                            final String funCodigo = (dtj.getAttribute(Columns.DTJ_FUN_CODIGO) != null ? dtj.getAttribute(Columns.DTJ_FUN_CODIGO).toString() : responsavel.getFunCodigo());
                            final BigDecimal cftVlr = (BigDecimal) dtj.getAttribute(Columns.CFT_VLR);
                            final BigDecimal cftVlrMinimo = (BigDecimal) dtj.getAttribute(Columns.CFT_VLR_MINIMO);

							List<BigDecimal> cftVlrs = new ArrayList<>();
							cftVlrs.add(cftVlr);
							cftVlrs.add(cftVlrMinimo);

                            newTransfer.put(Columns.CFT_VLR + "_" + funCodigo, cftVlrs);
                            cft.setAttribute(Columns.DTJ_CODIGO + "_" + funCodigo, dtj.getAttribute(Columns.DTJ_CODIGO));
                        }
                        cft.setAttribute(Columns.DTJ_TAXA_JUROS, newTransfer);
                    }
                }

                // Para cada regra de taxa, procura aquelas que não tenham coeficiente/taxa para criar a entrada na lista de resultado
                for (final TransferObject dtjTO : defTaxaJurosList) {
                    // final List<TransferObject> lst = coeficientes.stream().filter(coeficiente -> dtjTO.getAttribute(Columns.CSA_CODIGO).equals(coeficiente.getAttribute(Columns.CSA_CODIGO))
                    //        && dtjTO.getAttribute(Columns.SVC_CODIGO).equals(coeficiente.getAttribute(Columns.SVC_CODIGO))).collect(Collectors.toList());

                    final String csaCodigoLinha = dtjTO.getAttribute(Columns.CSA_CODIGO).toString();
                    final String svcCodigoLinha = dtjTO.getAttribute(Columns.SVC_CODIGO).toString();
                    final String chaveLinha = String.format(CHAVE_HASH_DEF_TX_JUROS_CSA_SVC, csaCodigoLinha, svcCodigoLinha);
                    final List<TransferObject> lst = coeficientesMap.get(chaveLinha);

                    final String funCodigo = (dtjTO.getAttribute(Columns.DTJ_FUN_CODIGO) != null ? dtjTO.getAttribute(Columns.DTJ_FUN_CODIGO).toString() : responsavel.getFunCodigo());
                    final String chaveDtjPorFuncao = Columns.DTJ_CODIGO + "_" + funCodigo;
                    final String chaveCftPorFuncao = Columns.CFT_VLR + "_" + funCodigo;

                    // Se a definição não tem entrada na lista de coeficientes, adiciona a configuração
                    if ((lst == null) || lst.isEmpty()) {
                        final TransferObject cft = new CustomTransferObject();
                        final HashMap<String, List<BigDecimal>> newTransfer = new HashMap<>();
                        final BigDecimal cftVlr = (BigDecimal) dtjTO.getAttribute(Columns.CFT_VLR);
                        final BigDecimal cftVlrMinimo = (BigDecimal) dtjTO.getAttribute(Columns.CFT_VLR_MINIMO);
                        cft.setAttribute(chaveDtjPorFuncao, dtjTO.getAttribute(Columns.DTJ_CODIGO));
                        cft.setAttribute(Columns.CSA_CODIGO, dtjTO.getAttribute(Columns.CSA_CODIGO));
                        cft.setAttribute(Columns.PRZ_VLR, dtjTO.getAttribute(Columns.PRZ_VLR));
                        cft.setAttribute(Columns.CSA_NOME_ABREV, dtjTO.getAttribute(Columns.CSA_NOME_ABREV));
                        cft.setAttribute(Columns.CSA_NOME, dtjTO.getAttribute(Columns.CSA_NOME));
                        cft.setAttribute(Columns.SVC_CODIGO, dtjTO.getAttribute(Columns.SVC_CODIGO));
                        cft.setAttribute(Columns.SVC_DESCRICAO, dtjTO.getAttribute(Columns.SVC_DESCRICAO));
                        cft.setAttribute(Columns.SVC_IDENTIFICADOR, dtjTO.getAttribute(Columns.SVC_IDENTIFICADOR));
                        cft.setAttribute(Columns.CFT_VLR, dtjTO.getAttribute(Columns.CFT_VLR));
                        cft.setAttribute(Columns.CFT_VLR_MINIMO, dtjTO.getAttribute(Columns.CFT_VLR_MINIMO));
                        cft.setAttribute(Columns.CSA_IDENTIFICADOR, dtjTO.getAttribute(Columns.CSA_IDENTIFICADOR));
                        cft.setAttribute(Columns.CFT_DATA_INI_VIG, dtjTO.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI));
                        cft.setAttribute(Columns.CFT_DATA_FIM_VIG, dtjTO.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM));
                        List<BigDecimal> cftVlrs = new ArrayList<>();
						cftVlrs.add(cftVlr);
						cftVlrs.add(cftVlrMinimo);
                        newTransfer.put(chaveCftPorFuncao, cftVlrs);
                        cft.setAttribute(Columns.DTJ_TAXA_JUROS, newTransfer);
                        cft.setAttribute("RELEVANCIA", dtjTO.getAttribute("RELEVANCIA"));

                        coeficientes.add(cft);

                    } else {
                        // Se tem entrada, veja se precisa atualizar a configuração para a função
                        for (final TransferObject obj : lst) {
                            HashMap<String, List<BigDecimal>> newTransfer = (HashMap<String, List<BigDecimal>>) obj.getAttribute(Columns.DTJ_TAXA_JUROS);
                            if (newTransfer == null) {
                                newTransfer = new HashMap<>();
                                obj.setAttribute(Columns.DTJ_TAXA_JUROS, newTransfer);
                            }

                            // Se não tem entrada para a função, cria
                            if (obj.getAttribute(chaveDtjPorFuncao) == null) {
                                obj.setAttribute(chaveDtjPorFuncao, dtjTO.getAttribute(Columns.DTJ_CODIGO));
                                List<BigDecimal> cftVlrs = new ArrayList<>();
        						cftVlrs.add((BigDecimal) dtjTO.getAttribute(Columns.CFT_VLR));
        						cftVlrs.add((BigDecimal) dtjTO.getAttribute(Columns.CFT_VLR_MINIMO));
                                newTransfer.put(chaveCftPorFuncao, cftVlrs);
                            }
                        }
                    }
                }
            }

            return coeficientes;

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
    }

    /**
     * Busca definicao de taxa de juros de acordo com os parametros passados
     *
     * @throws FindException
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> buscarDefinicaoTaxaJuros(String csaCodigo, String orgCodigo, String svcCodigo, String rseCodigo, BigDecimal valorTotal, BigDecimal valorContrato, Integer prazo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            final TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.ADE_VLR_LIQUIDO, valorTotal);
            criterio.setAttribute(Columns.ADE_VLR, valorContrato);
            criterio.setAttribute(Columns.PRZ_VLR, prazo);
            criterio.setAttribute("funcSimu", true);

            if (!TextHelper.isNull(rseCodigo)) {
                RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, true, responsavel);

                String urlSistemaExterno = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONSULTAR_MARGEM_SISTEMA_EXTERNO, responsavel);
                if(!TextHelper.isNull(urlSistemaExterno)) {
                    urlSistemaExterno = !urlSistemaExterno.endsWith("/") ? urlSistemaExterno + "/" : urlSistemaExterno;
                    rse = consultarMargemController.atualizaMargemServidorExternoRegistroServidor(urlSistemaExterno, rse, responsavel);
                }

                final ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);

                final java.util.Date serDataNasc = (java.util.Date) servidor.getAttribute(Columns.SER_DATA_NASC);
                final java.util.Date rseDataAdmissao = rse.getRseDataAdmissao();
                final BigDecimal rseSalario = rse.getRseSalario();

                BigDecimal rseMargem = null;
                if (!TextHelper.isNull(svcCodigo)) {
                    final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);
                    if ((paramSvcCse != null) && (paramSvcCse.getTpsIncideMargem() != null)) {
                        final Short incMargem = paramSvcCse.getTpsIncideMargem();

                        if (CodedValues.INCIDE_MARGEM_SIM.shortValue() == incMargem) {
                            rseMargem = rse.getRseMargem();
                        } else if (CodedValues.INCIDE_MARGEM_SIM_2.shortValue() == incMargem) {
                            rseMargem = rse.getRseMargem2();
                        } else if (CodedValues.INCIDE_MARGEM_SIM_3.shortValue() == incMargem) {
                            rseMargem = rse.getRseMargem3();
                        } else {
                            final List<TransferObject> lstMargens = servidorController.listarMargensRse(rseCodigo, svcCodigo, responsavel);
                            if ((lstMargens != null) && !lstMargens.isEmpty()) {
                                final List<TransferObject> margemInc = lstMargens.stream().filter(margem -> margem.getAttribute(Columns.MAR_CODIGO).equals(incMargem)).collect(Collectors.toList());
                                rseMargem = ((margemInc != null) && !margemInc.isEmpty()) ? (BigDecimal) margemInc.get(0).getAttribute(Columns.MRS_MARGEM) : null;
                            }
                        }
                    }
                }

                final Integer age = serDataNasc != null ? DateHelper.getAge(serDataNasc) : null;
                final Integer tempServico = rseDataAdmissao != null ? DateHelper.yearDiff(rseDataAdmissao, new java.util.Date()) : null;

                criterio.setAttribute("RSE_IDADE", age);
                criterio.setAttribute("RSE_TEMPO_SERVICO", tempServico);
                criterio.setAttribute(Columns.RSE_SALARIO, rseSalario);
                criterio.setAttribute(Columns.RSE_MARGEM, rseMargem);
                criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            }

            return buscarRegrasTaxasJuros(criterio, responsavel);

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Define a posição do coeficiente conforme o parâmetro de sistema
     * passando a função que será considerada no rankeamento das consignatarias
     * CodedValues.TPC_RANKING_ORDENACAO_SEQUENCIAL.
     *
     * @param coeficientes
     * @param funcaoRanking
     * @param responsavel
     * @return A lista com os rankings atualizados conforme a configuração do
     * parâmetro CodedValues.TPC_RANKING_ORDENACAO_SEQUENCIAL.
     */
    @Override
    public void setaRankingSimulacao(List<TransferObject> coeficientes, String funcaoRanking, AcessoSistema responsavel) {
        if ((coeficientes != null) && !coeficientes.isEmpty()) {
            // Define as posições do ranking
            final boolean ordenacaoSequencial = ParamSist.paramEquals(CodedValues.TPC_RANKING_ORDENACAO_SEQUENCIAL, CodedValues.TPC_SIM, responsavel);
            BigDecimal vlrAnterior = null;
            int posicao = 0;
            int offset = 1;

            for (final TransferObject coeficiente : coeficientes) {
                final BigDecimal valor = coeficiente.getAttribute("VLR_ORDEM_" + funcaoRanking) != null ? (BigDecimal) coeficiente.getAttribute("VLR_ORDEM_" + funcaoRanking) : (BigDecimal) coeficiente.getAttribute("VLR_ORDEM");

                if (!valor.equals(vlrAnterior)) {
                    posicao += offset;
                    offset = 1;
                } else if (!ordenacaoSequencial) {
                    offset++;
                }
                vlrAnterior = valor;
                coeficiente.setAttribute("RANKING", String.valueOf(posicao));
            }
        }
    }

    @Override
    public List<TransferObject> buscarTaxasParaConsignatarias(final AcessoSistema responsavel, final String rseCodigo, final String orgCodigo,
            final String svcCodigo, final TransferObject ... consignatarias) throws SimulacaoControllerException {

        final List<TransferObject> consignatariasComTaxa = new ArrayList<>();

        // Verifica se o serviço tem cadastro de prazos e taxas, buscando a lista de taxas ativas
        final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        final List<TransferObject> taxasSimulacao = getCoeficienteAtivo(null, svcCodigo, orgCodigo, rseCodigo, (short) 0, dia, true, null, null, responsavel);

        if (taxasSimulacao != null && !taxasSimulacao.isEmpty()) {
            
            // Se existem taxas, prepara a lista de retorno, incluindo a menor taxa retornada na listagem
            for (final TransferObject csa : consignatarias) {
                // Para cada consignatária, busca a menor taxa na lista de taxas retornadas
                BigDecimal menorTaxa = null;

                for (final TransferObject taxa : taxasSimulacao) {
                    if (csa.getAttribute(Columns.CSA_CODIGO).equals(taxa.getAttribute(Columns.CSA_CODIGO))) {
                        final BigDecimal taxaAtual = (BigDecimal) taxa.getAttribute(Columns.CFT_VLR);
                        if (menorTaxa == null || menorTaxa.compareTo(taxaAtual) > 0) {
                            menorTaxa = taxaAtual;
                        }
                    }
                }

                if (menorTaxa != null) {
                    csa.setAttribute(Columns.CFT_VLR, menorTaxa);
                    consignatariasComTaxa.add(csa);
                }
            }

        }

        return consignatariasComTaxa;

    }

}
