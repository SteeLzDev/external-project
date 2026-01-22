package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.CoeficienteCorrecaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.PriceHelper;
import com.zetra.econsig.helper.consignacao.PriceHelper.TabelaPrice;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.CargoRegistroServidor;
import com.zetra.econsig.persistence.entity.CargoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ConsignatariaPermiteTdaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoId;
import com.zetra.econsig.persistence.entity.DadosServidor;
import com.zetra.econsig.persistence.entity.DadosServidorHome;
import com.zetra.econsig.persistence.entity.DadosServidorId;
import com.zetra.econsig.persistence.entity.HistoricoMargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemRse;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaDadosAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParamServicoAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignataria;
import com.zetra.econsig.persistence.entity.ParamSvcConsignatariaHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoId;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.ServicoPermiteTdaHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.StatusSolicitacaoHome;
import com.zetra.econsig.persistence.entity.TipoDadoAdicional;
import com.zetra.econsig.persistence.entity.TipoDadoAdicionalHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.admin.ListaCargoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaTodosTipoDadoAdicionalQuery;
import com.zetra.econsig.persistence.query.compra.ContaComprasAbertasServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaDadosAdicionaisServidorByAdeCodigoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaDadosAutorizacaoByAdeCodigoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaDadosAutorizacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaDuplicaParcelaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaReajusteAdeQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaTipoDadoAdicionalQuery;
import com.zetra.econsig.persistence.query.consignacao.ListarNaturezaConsignacaoPorServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListarStatusConsignacaoPorServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoPorCnvSerQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemDecisaoJudicialQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemQtdAdeRelSvcRequerDeferimentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoRseQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorRenegociacaoDentroPrazoQuery;
import com.zetra.econsig.persistence.query.consignacao.PesquisaAdeLstIndiceQuery;
import com.zetra.econsig.persistence.query.consignataria.ObtemCsaBloqRelSvcRequerDeferimentoQuery;
import com.zetra.econsig.persistence.query.margem.ListaContratosCompulsoriosQuery;
import com.zetra.econsig.persistence.query.margem.ListaContratosIncideMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoComposicaoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemDisponivelCompulsorioQuery;
import com.zetra.econsig.persistence.query.parametro.ObtemParamSvcAdeQuery;
import com.zetra.econsig.persistence.query.parametro.ObtemTaxasAdeQuery;
import com.zetra.econsig.persistence.query.servidor.RecuperaSomaAdeVlrRegistroServidorQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCorPodeModificarAdeQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsaCorPodeConsultarAdeCompraQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsaCorPodeModificarAdeCompraQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsaPodeModificarAdeQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioEstPodeModificarAdeQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioOrgPodeModificarAdeQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioServidorPodeModificarAdeQuery;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.coeficiente.CoeficienteCorrecaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.TpsExigeConfirmacaoRenegociacaoValoresEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: AutorizacaoControllerBean</p>
 * <p>Description: Session Bean para manipulacao de autorizações.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service("autorizacaoController")
@Transactional
public class AutorizacaoControllerBean extends ValidacoesControllerBean implements AutorizacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizacaoControllerBean.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CoeficienteCorrecaoController coeficienteCorrecaoController;

    @Autowired
    private PontuacaoServidorController pontuacaoServidorController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private SegurancaController segurancaController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    /**
     * Verifica se um usuário tem permissão de realizar operações na consignação
     * informada pelo parâmetro "adeCodigo",  de acordo com sua entidade.
     * Caso não tenha será lançado uma exceção e será gravado log de erro.
     * @param adeCodigo String
     * @param responsavel AcessoSistema
     * @return boolean
     * @throws AutorizacaoControllerException
     */
    @Override
    public boolean usuarioPodeModificarAde(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return usuarioPodeModificarAde(adeCodigo, true, true, responsavel);
    }

    @Override
    public boolean usuarioPodeModificarAde(String adeCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final boolean podeModificar = entidadeUsuarioPodeModificarAde(adeCodigo, responsavel);
        if (!podeModificar) {
            if (gravaLog) {
                try {
                    // Grava log de Erro
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_ERRO_SEGURANCA);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.modificar.esta.consignacao", responsavel)));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }

            if (lancaExcecao) {
                throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.modificar.esta.consignacao", responsavel);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se um usuário tem permissão de realizar operações na consignação
     * informada pelo parâmetro "adeCodigo",  de acordo com sua entidade compradora.
     * Caso não tenha será lançado uma exceção e será gravado log de erro.
     * @param adeCodigo String
     * @param responsavel AcessoSistema
     * @return boolean
     * @throws AutorizacaoControllerException
     */
    @Override
    public boolean usuarioPodeModificarAdeCompra(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return usuarioPodeModificarAdeCompra(adeCodigo, true, true, responsavel);
    }

    @Override
    public boolean usuarioPodeModificarAdeCompra(String adeCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        boolean podeModificar = false;

        if (responsavel.isCsaCor()) {
            try {
                // Se é usuário de CSA/COR, verifica se o mesmo tem permissão de alterar esta consignação
                final UsuarioCsaCorPodeModificarAdeCompraQuery query = new UsuarioCsaCorPodeModificarAdeCompraQuery();
                query.adeCodigo = adeCodigo;
                query.usuCodigo = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = ((result != null) && (result.size() > 0));
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(ex);
            }
        } else {
            // Demais usuários segue a lógica da modificação de contratos normais
            podeModificar = entidadeUsuarioPodeModificarAde(adeCodigo, responsavel);
        }

        if (!podeModificar) {
            if (gravaLog) {
                try {
                    // Grava log de Erro
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.FIND, Log.LOG_ERRO_SEGURANCA);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.modificar.esta.consignacao", responsavel)));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }

            if (lancaExcecao) {
                throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.modificar.esta.consignacao", responsavel);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se um usuário tem permissão de consultar a consignação
     * informada pelo parâmetro "adeCodigo",  de acordo com sua entidade.
     * Caso não tenha será lançado uma exceção e será gravado log de erro.
     * @param adeCodigo String
     * @param responsavel AcessoSistema
     * @return boolean
     * @throws AutorizacaoControllerException
     */
    @Override
    public boolean usuarioPodeConsultarAde(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final boolean podeModificar = entidadeUsuarioPodeModificarAde(adeCodigo, responsavel);
        if (!podeModificar) {
            // Se não pode modificar, então significa que o usuário não pertence
            // a entidade ao qual o contrato pertence. Verifica então, para usuários CSA/COR
            // se existe relacionamento de compra de sua entidade para esta consignação.
            boolean podeConsultar = false;
            if (responsavel.isCsaCor()) {
                try {
                    // Se é usuário de CSA/COR, verifica se o mesmo tem permissão de consultar esta consignação
                    // através de um relacionamento de compra pela origem
                    UsuarioCsaCorPodeConsultarAdeCompraQuery query = new UsuarioCsaCorPodeConsultarAdeCompraQuery();
                    query.origem = true;
                    query.adeCodigo = adeCodigo;
                    query.usuCodigo = responsavel.getUsuCodigo();
                    List<?> result = query.executarLista();
                    podeConsultar = ((result != null) && (result.size() > 0));

                    if (!podeConsultar) {
                        // Verifica relacionamento de compra pelo destino
                        query = new UsuarioCsaCorPodeConsultarAdeCompraQuery();
                        query.origem = false;
                        query.adeCodigo = adeCodigo;
                        query.usuCodigo = responsavel.getUsuCodigo();
                        result = query.executarLista();
                        podeConsultar = ((result != null) && (result.size() > 0));
                    }

                    if (!podeConsultar && CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo())) {
                        // Verifica se o responsável está tentando visualizar um contrato do registro servidor que autorizou a operação
                        final ListaConsignacaoQuery lstCsaQuery = new ListaConsignacaoQuery(responsavel);
                        lstCsaQuery.adeCodigo = responsavel.getAdeCodigosOperacao();
                        lstCsaQuery.rseCodigo = responsavel.getRseCodigoOperacao();
                        lstCsaQuery.tipo = responsavel.getTipoEntidade();
                        result = lstCsaQuery.executarLista();
                        podeConsultar = ((result != null) && (result.size() > 0));
                    }

                } catch (final HQueryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException(ex);
                }
            }
            if (!podeConsultar) {
                try {
                    // Grava log de Erro
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.FIND, Log.LOG_ERRO_SEGURANCA);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.para.consultar.esta.consignacao", responsavel)));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }

                throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.para.consultar.esta.consignacao", responsavel);
            }
        }
        return true;
    }

    /**
     * Retorna TRUE se o usuário, representado pelo parâmetro "responsavel", pertence a uma
     * entidade, seja CSE/ORG/SUP/CSA/COR/SER, que tenha permissão de realizar operações
     * sobre a consignação, representada pelo parâmetro "adeCodigo". Usuário CSE/SUP podem
     * modificar qualquer consignação. Usuário de ORG deve ser do órgão ligado ao contrato.
     * Usuários de CSA/COR devem ser da entidade dona do contrato. Usuário SER apenas para
     * seus próprios contratos.
     * @param adeCodigo String
     * @param responsavel AcessoSistema
     * @return
     * @throws AutorizacaoControllerException
     */
    private boolean entidadeUsuarioPodeModificarAde(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            boolean podeModificar = false;

            if (responsavel.isSistema() || responsavel.isCse() || responsavel.isSup()) {
                podeModificar = true;

            } else if (responsavel.isOrg()) {
                // Se é usuário de ORG, verifica se o mesmo tem permissão de alterar esta consignação
                List<?> result;

                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    final UsuarioEstPodeModificarAdeQuery query = new UsuarioEstPodeModificarAdeQuery();
                    query.adeCodigo = adeCodigo;
                    query.usuCodigo = responsavel.getUsuCodigo();
                    result = query.executarLista();
                } else {
                    final UsuarioOrgPodeModificarAdeQuery query = new UsuarioOrgPodeModificarAdeQuery();
                    query.adeCodigo = adeCodigo;
                    query.usuCodigo = responsavel.getUsuCodigo();
                    result = query.executarLista();
                }

                podeModificar = ((result != null) && (result.size() > 0) );


            } else if (responsavel.isCsa()) {
                // Se é usuário de CSA, verifica se o mesmo tem permissão de alterar esta consignação
                final UsuarioCsaPodeModificarAdeQuery query = new UsuarioCsaPodeModificarAdeQuery();
                query.adeCodigo = adeCodigo;
                query.usuCodigo = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = ((result != null) && (result.size() > 0));

                // Verifica então se é usuário de CSA que tenha permissão de deferir/indeferir consignação
                // que pode consultar/modificar contratos de outras CSAs.
                if (!podeModificar && (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa())) {
                    podeModificar = (responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                            (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()));
                }
            } else if (responsavel.isCor()) {
                final UsuarioCorPodeModificarAdeQuery query = new UsuarioCorPodeModificarAdeQuery();
                query.adeCodigo = adeCodigo;
                query.usuCodigo = responsavel.getUsuCodigo();

                // Se é usuário de COR, verifica se o mesmo tem permissão de alterar esta consignação
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA) ||
                        CodedValues.FUN_CONF_SOLICITACAO.equals(responsavel.getFunCodigo()) ||
                        CodedValues.FUN_LISTAR_SOLICITACAO_CONTRATOS.equals(responsavel.getFunCodigo())) {
                    // Se é usuário de COR que tenha permissão de ver os contratos da CSA,
                    // ou a operação permite a visualização de todos de sua CSA.
                    query.podeAcessarCsa = true;
                }

                final List<?> result = query.executarLista();
                podeModificar = ((result != null) && (result.size() > 0));

            } else if (responsavel.isSer()) {
                // Verifica se é usuário servidor que está realizando a operação
                final UsuarioServidorPodeModificarAdeQuery query = new UsuarioServidorPodeModificarAdeQuery();
                query.adeCodigo = adeCodigo;
                query.usuCodigo = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = ((result != null) && (result.size() > 0));
            }

            return podeModificar;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Grava o tipo de dado para a autorização de desconto informada pelo adeCodigo.
     * @param adeCodigo : código da autorização
     * @param tdaCodigo : tipo do dado a ser gravado
     * @param dadValor  : valor a ser gravado
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void setDadoAutDesconto(String adeCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        TipoDadoAdicional tda;
        try {
            tda = TipoDadoAdicionalHome.findByPrimaryKey(tdaCodigo);
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.nao.encontrado", responsavel, ex);
        }

        AutDesconto ade;
        try {
            ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavel, ex);
        }

        try {
            final Convenio cnv = ade.getVerbaConvenio().getConvenio();
            final String svcCodigo = cnv.getServico().getSvcCodigo();
            final String sptExibe = ServicoPermiteTdaHome.getServicoPermiteTipoDadoAdicional(svcCodigo, tdaCodigo);

            if (CodedValues.CAS_NAO.equals(sptExibe)) {
                // Tipo de dado não é exibido para o serviço, então não pode ser gravado no banco
                throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.nao.permitido.servico", responsavel, tda.getTdaDescricao());
            } else if (CodedValues.CAS_BLOQUEADO.equals(sptExibe)) {
                // Tipo de dado é exibido para o serviço, porém bloqueado, então ignora tentativa de alteração do valor
                return;
            } else if (CodedValues.CAS_OBRIGATORIO.equals(sptExibe) && TextHelper.isNull(dadValor)) {
                // Tipo de dado é exibido para o serviço e é obrigatório porém o valor não foi informado
                throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.obrigatorio.servico", responsavel, tda.getTdaDescricao());
            }

            final String csaCodigo = cnv.getConsignataria().getCsaCodigo();
            final String cptExibe = ConsignatariaPermiteTdaHome.getConsignatariaPermiteTipoDadoAdicional(csaCodigo, tdaCodigo);

            if (CodedValues.CAS_NAO.equals(cptExibe)) {
                // Tipo de dado não é exibido para a consignatária, então não pode ser gravado no banco
                throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.nao.permitido.consignataria", responsavel, tda.getTdaDescricao());
            } else if (CodedValues.CAS_BLOQUEADO.equals(cptExibe)) {
                // Tipo de dado é exibido para a consignatária, porém bloqueado, então ignora tentativa de alteração do valor
                return;
            } else if (CodedValues.CAS_OBRIGATORIO.equals(cptExibe) && TextHelper.isNull(dadValor)) {
                // Tipo de dado é exibido para a consignatária e é obrigatório porém o valor não foi informado
                throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.obrigatorio.consignataria", responsavel, tda.getTdaDescricao());
            }
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        if (Log.AUTORIZACAO.equals(tda.getTipoEntidade().getTenCodigo()) || Log.BENEFICIARIO.equals(tda.getTipoEntidade().getTenCodigo())) {
            gravarDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);

        } else if (Log.SERVIDOR.equals(tda.getTipoEntidade().getTenCodigo())) {
            try {
                final String serCodigo = ade.getRegistroServidor().getServidor().getSerCodigo();
                servidorController.gravarDadoServidor(serCodigo, tdaCodigo, dadValor, responsavel);
            } catch (final ServidorControllerException ex) {
                throw new AutorizacaoControllerException(ex);
            }
        } else {
            throw new AutorizacaoControllerException("mensagem.erro.tipo.entidade.invalido", responsavel);
        }
    }

    private void gravarDadoAutDesconto(String adeCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            String operacao;
            DadosAutorizacaoDesconto dad = null;
            String vlrAnterior = null;
            try {
                // Verifica se o dado já existe na tabela
                dad = DadosAutorizacaoDescontoHome.findByPrimaryKey(new DadosAutorizacaoDescontoId(adeCodigo, tdaCodigo));
                vlrAnterior = dad.getDadValor();

                // Se existe, verifica se deve ser atualizado ou removido
                if (!TextHelper.isNull(dadValor)) {
                    operacao = Log.UPDATE;
                    dad.setDadValor(dadValor);
                    AbstractEntityHome.update(dad);
                } else {
                    operacao = Log.DELETE;
                    AbstractEntityHome.remove(dad);
                }
            } catch (final FindException ex) {
                if (dadValor != null) {
                    // Senão, cria o novo dado para a autorização de desconto
                    operacao = Log.CREATE;
                    DadosAutorizacaoDescontoHome.create(adeCodigo, tdaCodigo, dadValor);
                } else {
                    // Se não existe, mas o valor não foi passado, não realiza operação
                    operacao = null;
                }
            }

            if (operacao != null) {
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.DADOS_AUTORIZACAO_DESCONTO, operacao, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.setTipoDadoAdicional(tdaCodigo);
                log.addChangedField(Columns.DAD_VALOR, dadValor);
                log.write();

                // Grava ocorrencia da operação
                String tocCodigo;
                String obs;
                if (Log.CREATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_CRIACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.criacao.dados.autorizacao", responsavel, tdaCodigo, dadValor);
                } else if (Log.UPDATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.alteracao.dados.autorizacao", responsavel, tdaCodigo, vlrAnterior, dadValor);
                } else {
                    tocCodigo = CodedValues.TOC_EXCLUSAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.exclusao.dados.autorizacao", responsavel, tdaCodigo, vlrAnterior);
                }
                // Só registra alteração quando realmente tem alteração de valor
                if (Log.UPDATE.equals(operacao) && dadValor.equals(vlrAnterior)){
                    return;
                }
                OcorrenciaDadosAutorizacaoHome.create(adeCodigo, tocCodigo, responsavel.getUsuCodigo(), tdaCodigo, null, obs, vlrAnterior, dadValor, responsavel.getIpUsuario());
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getValorDadoAutDesconto(adeCodigo, tdaCodigo, false, responsavel);
    }

    @Override
    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        TipoDadoAdicional tda;
        try {
            tda = TipoDadoAdicionalHome.findByPrimaryKey(tdaCodigo);
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.tipo.dado.autorizacao.nao.encontrado", responsavel, ex);
        }

        try {
            if (Log.AUTORIZACAO.equals(tda.getTipoEntidade().getTenCodigo()) || Log.BENEFICIARIO.equals(tda.getTipoEntidade().getTenCodigo())) {
                DadosAutorizacaoDesconto dad = null;
                if (arquivado) {
                    // Obtém o dado para a autorização de desconto já arquivado
                    dad = DadosAutorizacaoDescontoHome.findArquivadoByPrimaryKey(new DadosAutorizacaoDescontoId(adeCodigo, tdaCodigo));
                } else {
                    // Obtém o dado para a autorização de desconto
                    dad = DadosAutorizacaoDescontoHome.findByPrimaryKey(new DadosAutorizacaoDescontoId(adeCodigo, tdaCodigo));
                }
                return dad.getDadValor();

            } else if (Log.SERVIDOR.equals(tda.getTipoEntidade().getTenCodigo())) {
                String serCodigo;
                try {
                    serCodigo = AutDescontoHome.findByPrimaryKey(adeCodigo).getRegistroServidor().getServidor().getSerCodigo();
                } catch (final FindException ex) {
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
                return getValorDadoServidor(serCodigo, tdaCodigo, responsavel);

            } else {
                throw new AutorizacaoControllerException("mensagem.erro.tipo.entidade.invalido", responsavel);
            }
        } catch (final FindException ex) {
            return null;
        }
    }

    @Override
    public String getValorDadoServidor(String serCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final DadosServidor das = DadosServidorHome.findByPrimaryKey(new DadosServidorId(serCodigo, tdaCodigo));
            return das.getDasValor();
        } catch (final FindException ex) {
            return null;
        }
    }

    @Override
    public List<TransferObject> lstDadoAutDesconto(String adeCodigo, String tdaCodigo, VisibilidadeTipoDadoAdicionalEnum visibilidade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
        	if (visibilidade == null) {
                throw new AutorizacaoControllerException("mensagem.erro.parametros.ausentes", responsavel);
            }
        	if (TextHelper.isNull(adeCodigo)) {
        	    final ListaDadosAutorizacaoQuery query = new ListaDadosAutorizacaoQuery();
                query.tdaCodigo = tdaCodigo;
                query.responsavel = responsavel;
                query.visibilidade = visibilidade;
                return query.executarDTO();
        	}

        	final ListaDadosAutorizacaoByAdeCodigoQuery query = new ListaDadosAutorizacaoByAdeCodigoQuery();
            query.adeCodigo = adeCodigo;
            query.tdaCodigo = tdaCodigo;
            query.responsavel = responsavel;
            query.visibilidade = visibilidade;
            final List<TransferObject> dados1 = query.executarDTO();

            final ListaDadosAdicionaisServidorByAdeCodigoQuery query2 = new ListaDadosAdicionaisServidorByAdeCodigoQuery();
            query2.adeCodigo = adeCodigo;
            query2.tdaCodigo = tdaCodigo;
            query2.responsavel = responsavel;
            query2.visibilidade = visibilidade;
            final List<TransferObject> dados2 = query2.executarDTO();

            final List<TransferObject> dados = new ArrayList<>(dados1);
            dados.addAll(dados2);

            Collections.sort(dados, (o1, o2) -> ((String) o2.getAttribute(Columns.TDA_DESCRICAO)).compareTo((String) o1.getAttribute(Columns.TDA_DESCRICAO)));

            return dados;
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if ((acao == null) || (visibilidade == null)) {
                throw new AutorizacaoControllerException("mensagem.erro.parametros.ausentes", responsavel);
            }

            final ListaTipoDadoAdicionalQuery query = new ListaTipoDadoAdicionalQuery();
            query.responsavel = responsavel;
            query.acao = acao;
            query.visibilidade = visibilidade;
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstTodosTipoDadoAdicional(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaTodosTipoDadoAdicionalQuery query = new ListaTodosTipoDadoAdicionalQuery();

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Corrige o valor da ade, para o valor presente de acordo
     * com as configurações do serviço e com a data do evento.
     * @param adeVlr
     * @param dataEvento
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public BigDecimal corrigirValorPresente(BigDecimal adeVlr, java.util.Date dataEvento, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            final String ccrTccCodigo = ((pse != null) && (pse.getPseVlr() != null)) ? pse.getPseVlr() : null;

            if ((ccrTccCodigo != null) && (adeVlr != null)) {
                final Calendar calendarHoje = Calendar.getInstance();
                final Calendar calendarEvento = Calendar.getInstance();
                calendarEvento.setTime(dataEvento);

                // Busca parâmetro de sistema para verifica se utiliza valor acumulado no cálculo do contrato.
                pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_CALCULO_VALOR_ACUMULADO, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);

                // Instancia variaveis e verifica qual metodo chamar.
                BigDecimal cftEvento = null;
                BigDecimal cftHoje = null;
                if ("1".equals(pse.getPseVlr())) {
                    cftEvento = coeficienteCorrecaoController.getPrimeiroCoeficienteCorrecao(ccrTccCodigo, calendarEvento.get(Calendar.MONTH) + 1, calendarEvento.get(Calendar.YEAR), CodedValues.TIPO_CCR_VLR_ACUMULADO);
                    cftHoje = coeficienteCorrecaoController.getUltimoCoeficienteCorrecao(ccrTccCodigo, calendarHoje.get(Calendar.MONTH) + 1, calendarHoje.get(Calendar.YEAR), CodedValues.TIPO_CCR_VLR_ACUMULADO);
                } else {
                    cftEvento = coeficienteCorrecaoController.getPrimeiroCoeficienteCorrecao(ccrTccCodigo, calendarEvento.get(Calendar.MONTH) + 1, calendarEvento.get(Calendar.YEAR));
                    cftHoje = coeficienteCorrecaoController.getUltimoCoeficienteCorrecao(ccrTccCodigo, calendarHoje.get(Calendar.MONTH) + 1, calendarHoje.get(Calendar.YEAR));
                }

                // Faz os calculos com valores recuperados.
                if ((cftEvento != null) && (cftHoje != null)) {
                    return adeVlr.multiply(cftHoje.divide(cftEvento, java.math.RoundingMode.DOWN));
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.existem.coeficientes.cadastrados.data", responsavel);
                }
            }
        } catch (final FindException ex) {
            LOG.error("Parâmetro de correção pelo valor acumulado não encontrado. " + ex.getMessage());
        } catch (final CoeficienteCorrecaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Retorna a soma dos valores dos contratos, para um registro servidor
     * dado uma lista de serviços e de status de contratos.
     * Caso o valor da soma total dos contratos para o registro servidor seja nulo, o valor de retorno será zero.
     *
     * @param rseCodigo
     * @param svcCodigo
     * @param sadCodigos
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    private BigDecimal getSomaAdeVlr(String rseCodigo, String svcCodigo, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<String> svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigo);

            final RecuperaSomaAdeVlrRegistroServidorQuery query = new RecuperaSomaAdeVlrRegistroServidorQuery(rseCodigo, svcCodigos, sadCodigos);

            BigDecimal retorno = BigDecimal.ZERO;
            final List<TransferObject> lista = query.executarDTO();
            if ((lista != null) && !lista.isEmpty()) {
                final TransferObject to = lista.iterator().next();
                if ((to != null) && (to.getAttribute("TOTAL") != null)) {
                    retorno = new BigDecimal(to.getAttribute("TOTAL").toString());
                }
            }
            return retorno;

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Valida o teto de desconto do cargo do servidor.
     * Verifica o valor de referencia do cargo e o percentual
     * máximo permitido de acordo com o parametro de serviço.
     * @param rseCodigo
     * @param svcCodigo
     * @param adeVlr
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public boolean validarTetoDescontoPeloCargo(String rseCodigo, String svcCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws AutorizacaoControllerException {
        BigDecimal crsVlrReferencia = null;

        try {
            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            if (rseBean.getCargoRegistroServidor() != null) {
                final CargoRegistroServidor cargoRegistroServidor = CargoRegistroServidorHome.findByPrimaryKey(rseBean.getCargoRegistroServidor().getCrsCodigo());
                crsVlrReferencia = cargoRegistroServidor.getCrsVlrReferencia();
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (crsVlrReferencia != null) {
            // Se o cargo possui um valor de referência, então valida o teto
            // de desconto de acordo com o valor de referencia e o percentual
            // informado no parâmetro de servico

            double percentualMax = 0;
            // Busca o parâmetro de serviço com o percentual sobre o valor
            // de referencia do cargo
            try {
                final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                if ((pse != null) && (pse.getPseVlr() != null) && !"".equals(pse.getPseVlr())) {
                    percentualMax = Double.parseDouble(pse.getPseVlr());
                }
            } catch (final FindException ex) {
                LOG.debug(ex.getMessage());
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Busca o somatório dos contratos já existentes
            BigDecimal totalContratos = getSomaAdeVlr(rseCodigo, svcCodigo, CodedValues.SAD_CODIGOS_ATIVOS, responsavel);
            LOG.debug("Total Contratos Existentes: " + totalContratos);
            LOG.debug("Ade Vlr Novo: " + adeVlr);

            // Adiciona o valor do novo contrato
            totalContratos = totalContratos.add(adeVlr);
            // Calcula o teto máximo
            final BigDecimal tetoMaximo = crsVlrReferencia.multiply(new BigDecimal(String.valueOf(percentualMax))).divide(new BigDecimal("100.00"), 2, java.math.RoundingMode.DOWN);

            LOG.debug("Total: " + totalContratos);
            LOG.debug("Percentual Max: " + percentualMax);
            LOG.debug("Teto Máximo: " + tetoMaximo);

            if (totalContratos.compareTo(tetoMaximo) == 1) {
                final String diff = NumberHelper.format(totalContratos.subtract(tetoMaximo).doubleValue(), NumberHelper.getLang());
                // Se total de contratos maior que o teto máximo, lança exceção
                throw new AutorizacaoControllerException("mensagem.erro.valor.contrato.excede.teto.maximo.desconto.cargo.servidor", responsavel, diff);
            }
        }
        return true;
    }

    /**
     * Retorna o valor de desconto de um contrato.
     * Verifica se o sistema controla o saldo devedor e calcula o valor baseado no limite máximo, se definido.
     * @param adeVlr Valor original do contrato
     * @param rseCodigo Código do registro servidor
     * @param crsCodigo Código do cargo do registro servidor
     * @param parametros Valores dos parâmetros já recuperados.
     * @return O mínimo entre o valor da ADE e o valor máximo permitido configurado em parâmetro.
     * @throws AutorizacaoControllerException
     */
    protected BigDecimal calcularValorDescontoParcela(String rseCodigo, String crsCodigo, String svcCodigo, BigDecimal adeVlr, Map<String, Object> parametros) throws AutorizacaoControllerException {
        BigDecimal vlrDesconto = adeVlr;

        // Verifica se há controle de saldo devedor.
        final CustomTransferObject paramControleSdv = getParametroSvc(CodedValues.TPS_CONTROLA_SALDO, svcCodigo, "", false, parametros);
        final boolean controlaSaldoDevedor = ((paramControleSdv != null) && (paramControleSdv.getAttribute(Columns.PSE_VLR) != null) &&
                "1".equals(paramControleSdv.getAttribute(Columns.PSE_VLR)));

        if (controlaSaldoDevedor) {
            // Busca parâmetro de Controle de Valor máximo de desconto
            final CustomTransferObject tpsControleVlrMaxDesconto = getParametroSvc(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO, svcCodigo, "", false, parametros);
            final String paramControleVlrMaxDesconto = (tpsControleVlrMaxDesconto != null ? (String) tpsControleVlrMaxDesconto.getAttribute(Columns.PSE_VLR): null);
            final boolean possuiControleVlrMaxDesconto = (paramControleVlrMaxDesconto != null) && !"0".equals(paramControleVlrMaxDesconto);

            if (possuiControleVlrMaxDesconto) {
                if ((crsCodigo == null) && (rseCodigo != null)) {
                    try {
                        final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                        if (rse.getCargoRegistroServidor() != null) {
                            crsCodigo = rse.getCargoRegistroServidor().getCrsCodigo();
                        }
                    } catch (final FindException e) {
                        throw new AutorizacaoControllerException("mensagem.erro.recuperar.servidor", (AcessoSistema) null, e);
                    }
                }

                /*
                 * Se o serviço possui controle de valor máximo de desconto, limita
                 * o adeVlr pelo mínimo entre o teto e o valor informado pelo usuário
                 */
                if (CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO.equals(paramControleVlrMaxDesconto)) {
                    // Controle de teto pelo cargo do servidor. Busca os dados do cargo do servidor
                    if (crsCodigo != null) {
                        TransferObject cargoRegistroServidor = null;

                        try {
                            final ListaCargoRegistroServidorQuery query = new ListaCargoRegistroServidorQuery();
                            query.crsCodigo = crsCodigo;
                            final List<TransferObject> result = query.executarDTO();
                            if ((result != null) && (result.size() > 0)) {
                                cargoRegistroServidor = result.get(0);
                            }
                        } catch (final HQueryException e) {
                            throw new AutorizacaoControllerException("mensagem.erro.recuperar.cargo.servidor", (AcessoSistema) null, e);
                        }

                        // Se o cargo do servidor possui valor máximo de desconto então aplica o valor
                        if ((cargoRegistroServidor != null) && (cargoRegistroServidor.getAttribute(Columns.CRS_VLR_DESC_MAX) != null)) {
                            // Mínimo entre o valor máximo de desconto e o teto máximo do cargo
                            final BigDecimal vlrMaxDescontoCargo = (BigDecimal) cargoRegistroServidor.getAttribute(Columns.CRS_VLR_DESC_MAX);
                            vlrDesconto = adeVlr != null ? vlrMaxDescontoCargo.min(adeVlr) : vlrMaxDescontoCargo;
                        }
                    }
                } else {
                    // Demais controles de teto máximo de desconto
                }
            }
        }

        return vlrDesconto;
    }

    @Override
    public BigDecimal calcularValorDescontoParcela(String rseCodigo, String svcCodigo, BigDecimal adeVlr) throws AutorizacaoControllerException {
        return this.calcularValorDescontoParcela(rseCodigo, null, svcCodigo, adeVlr, null);
    }

    @Override
    public void liberarEstoque(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
            if (!CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo)) {
                throw new AutorizacaoControllerException("mensagem.erro.situacao.atual.nao.permite.liberar.autorizacao", responsavel);
            }

            final String ocaCodigo = modificaSituacaoADE(autdes, CodedValues.SAD_ESTOQUE, responsavel);
            if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                // grava motivo da operacao
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            }

            // Gera o Log de auditoria
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.LIBERAR_ESTOQUE, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw new AutorizacaoControllerException(ex);
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    // Composição de margem
    @Override
    public List<TransferObject> historicoComposicaoMargem(String strRseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaHistoricoComposicaoMargemQuery query = new ListaHistoricoComposicaoMargemQuery();
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(strRseCodigo);
            log.write();
            query.strRseCodigo = strRseCodigo;
            return query.executarDTO();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setParamSvcADE(String adeCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Busca os parâmetros que devem ser salvos
            final ObtemTaxasAdeQuery query = new ObtemTaxasAdeQuery();
            query.adeCodigo = adeCodigo;
            query.tpsCodigos = tpsCodigos;

            // Lista dos parametros que devem ser salvos
            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && (result.size() > 0)) {
                for (final TransferObject paramSvcCsa : result) {
                    final String pscCodigo = (String) paramSvcCsa.getAttribute(Columns.PSC_CODIGO);
                    final String tpsCodigo = (String) paramSvcCsa.getAttribute(Columns.PSC_TPS_CODIGO);
                    final Object pscVlr = paramSvcCsa.getAttribute(Columns.PSC_VLR);

                    // Grava o parâmetro
                    ParamServicoAutorizacaoDescontoHome.create(adeCodigo, pscCodigo);

                    // Grava o log do parâmetro
                    final LogDelegate log = new LogDelegate(responsavel, Log.PARAM_SVC_AUT_DESCONTO, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.setParamSvcCsa(pscCodigo);
                    log.setTipoParamSvc(tpsCodigo);
                    log.addChangedField(Columns.PSC_VLR, pscVlr);
                    log.write();
                }
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, String> getParamSvcADE(String adeCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemParamSvcAdeQuery query = new ObtemParamSvcAdeQuery();
            query.adeCodigo = adeCodigo;
            query.tpsCodigos = tpsCodigos;
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstDuplicaParcela(String csaCodigo, String cnvCodVerba,
            String adeIndice, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaDuplicaParcelaQuery query = new ListaDuplicaParcelaQuery();
            query.csaCodigo = csaCodigo;
            query.cnvCodVerba = cnvCodVerba;
            query.adeIndice = adeIndice;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstReajustaAde(String csaCodigo, CustomTransferObject regras,
            AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            regras.setAttribute("CSA_CODIGO", csaCodigo);
            final ListaReajusteAdeQuery query = new ListaReajusteAdeQuery(regras);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * DESEN-13123 - Método utilizado para o governo do paraná
     * @param dadosServidor
     * @param listaDadosAde
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void salvarDadosAutorizacaoConsignacao(DadosServidor dadosServidor, List<DadosAutorizacaoDesconto> listaDadosAde, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Grava o dado adicional de servidor
            servidorController.gravarDadoServidor(dadosServidor.getSerCodigo(), dadosServidor.getTdaCodigo(), dadosServidor.getDasValor(), responsavel);
            // Gava os dados adicionais de autorização
            for (final DadosAutorizacaoDesconto dad : listaDadosAde) {
                gravarDadoAutDesconto(dad.getAdeCodigo(), dad.getTdaCodigo(), dad.getDadValor(), responsavel);
            }
        } catch (final ServidorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /** ======================================== GERENCIAMENTO DE MARGEM ======================================== **/

    /**
     * Atualiza a margem do registro servidor passado por parâmetro, subtraindo
     * o adeVlr passado da margem restante e somando esse mesmo valor na margem
     * usada.
     * @param rseCodigo       : código do registro do servidor
     * @param adeIncMargem    : tipo de incidência na margem
     * @param adeVlr          : valor da autorização
     * @param validaMargem    : se true e o servidor não possuir margem restante suficiente, gera exceção
     * @param validaBloqueado : se true verifica se o servidor está bloqueado
     * @param serAtivo        : se true verifica se o servidor está ativo
     * @param ocaCodigo       : código da ocorrência que gerou a alteração de margem
     * @param csaCodigo       : código da consignatária
     * @param adeCodigosRenegociacao : Código dos contratos sendo renegociados/comprados
     * @return List<HistoricoMargemRse>
     * @throws AutorizacaoControllerException
     */
    protected List<HistoricoMargemRse> atualizaMargem(String rseCodigo, Short adeIncMargem,
            BigDecimal adeVlr, boolean validaMargem, boolean validaBloqueado, boolean serAtivo, String ocaCodigo, String csaCodigo,
            String svcCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {

        try {
            // Default: margem 1
            adeIncMargem = (adeIncMargem == null ? CodedValues.INCIDE_MARGEM_SIM : adeIncMargem);

            final RegistroServidor registroSer = RegistroServidorHome.findByPrimaryKeyForUpdate(rseCodigo);
            final RegistroServidorTO rseDto = RegistroServidorDtoAssembler.createDto(registroSer, true);

            // Verifica se o servidor esta excluido ou bloqueado
            if (serAtivo) {
                if (registroSer.isExcluido()) {
                    final CustomTransferObject permiteAlterarAdeRseExcluido = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO, svcCodigo, "", false, null);
                    if((permiteAlterarAdeRseExcluido == null) || TextHelper.isNull(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR))){
                        // Servidor excluído não pode fazer novas reservas.
                        throw new AutorizacaoControllerException("mensagem.servidorExcluido", responsavel);
                    }
                } else if (validaBloqueado && registroSer.isBloqueado()) {
                    // Verifica se servidor bloqueado pode fazer nova reserva
                    final CustomTransferObject tpsPermiteReservaRseBloq = getParametroSvc(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO, svcCodigo, Boolean.TRUE, false, null);
                    if (!((Boolean) tpsPermiteReservaRseBloq.getAttribute(Columns.PSE_VLR)).booleanValue()) {
                        // Servidor bloqueado não pode fazer novas reservas.
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueado", responsavel);
                    }
                }
            }

            // Lista de históricos de margem gerados pela atualização
            final List<HistoricoMargemRse> historicos = new ArrayList<>();

            final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);
            final boolean margem1ProporcionalMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3, CodedValues.TPC_SIM, responsavel);

            if (validaMargem) {
                verificaMargem(rseDto, adeIncMargem, adeVlr, csaCodigo, svcCodigo, (margem1ProporcionalMargem3 && adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)), adeCodigosRenegociacao, responsavel);
            }

            // Guarda os valores originais das margens restantes para gravação do histórico
            final BigDecimal margemRest1Antes = registroSer.getRseMargemRest();
            final BigDecimal margemRest2Antes = registroSer.getRseMargemRest2();
            final BigDecimal margemRest3Antes = registroSer.getRseMargemRest3();

            if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                // Margem rest Antiga
                final BigDecimal margemRestAntigo = registroSer.getRseMargemRest();

                registroSer.setRseMargemUsada(registroSer.getRseMargemUsada().add(adeVlr));
                registroSer.setRseMargemRest(registroSer.getRseMargemRest().subtract(adeVlr));

                // Margem 1 Atual
                final BigDecimal margemRest = registroSer.getRseMargemRest();

                if (margem1CasadaMargem3) {
                    final BigDecimal varMargemRest = margemRest.subtract(margemRestAntigo);

                    final BigDecimal margem3 = (registroSer.getRseMargem3() != null) ? registroSer.getRseMargem3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = (registroSer.getRseMargemRest3() != null) ? registroSer.getRseMargemRest3() : new BigDecimal("0.00");
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");

                    if (margemRest.compareTo(margemRest3) == -1) {
                        // Se a margem rest 1 é menor do que a margem rest 3, então
                        // limita a margem rest 3 ao valor da margem rest 1
                        registroSer.setRseMargemRest3(registroSer.getRseMargemRest());
                    } else if (varMargemRest.compareTo(new BigDecimal("0.00")) == 1) {
                        // Se a variação da margem rest 1 é positiva, ou seja está liberando mais margem, então
                        // a margem rest 3 será o mínimo entre a margem rest 1 e o valor correto da margem rest 3 (margem - usada)
                        final BigDecimal min = registroSer.getRseMargemRest().min(margem3.subtract(margemUsada3));
                        registroSer.setRseMargemRest3(min);
                    }

                } else if (margem123Casadas) {
                    final BigDecimal margem2 = (registroSer.getRseMargem2() != null) ? registroSer.getRseMargem2() : new BigDecimal("0.00");
                    final BigDecimal margemUsada2 = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                    final BigDecimal margemRest2 = margemRest.min(margem2.subtract(margemUsada2));
                    registroSer.setRseMargemRest2(margemRest2);

                    final BigDecimal margem3 = (registroSer.getRseMargem3() != null) ? registroSer.getRseMargem3() : new BigDecimal("0.00");
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                    registroSer.setRseMargemRest3(margemRest3);

                } else if (margem1CasadaMargem3Esq) {
                    // Se margem 1 casada pela esquerda com a 3, e a reserva incide na margem 1
                    // então subtrai da margem 3 o mesmo valor extraido da margem 1
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = (registroSer.getRseMargemRest3() != null) ? registroSer.getRseMargemRest3() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada3(margemUsada3.add(adeVlr));
                    registroSer.setRseMargemRest3(margemRest3.subtract(adeVlr));

                } else if (margem123CasadasEsq) {
                    // Se margem 1 casada pela esquerda com a 2 e 3, e a reserva incide na margem 1
                    // então subtrai da margem 2 e 3 o mesmo valor extraido da margem 1
                    final BigDecimal margemUsada2 = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                    final BigDecimal margemRest2 = (registroSer.getRseMargemRest2() != null) ? registroSer.getRseMargemRest2() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada2(margemUsada2.add(adeVlr));
                    registroSer.setRseMargemRest2(margemRest2.subtract(adeVlr));

                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = (registroSer.getRseMargemRest3() != null) ? registroSer.getRseMargemRest3() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada3(margemUsada3.add(adeVlr));
                    registroSer.setRseMargemRest3(margemRest3.subtract(adeVlr));

                } else if (margem1CasadaMargem3Lateral) {
                    // Se margem 1 casada pela com a 3 lateralmente, e a reserva incide na margem 1
                    // então subtrai da margem 3 o valor negativo da margem 1 restante
                    final BigDecimal margem3 = (registroSer.getRseMargem3() != null) ? registroSer.getRseMargem3() : new BigDecimal("0.00");
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    // RSE_MARGEM_REST_3 = RSE_MARGEM_3 - RSE_MARGEM_USADA_3 + MIN(0, RSE_MARGEM_REST)
                    registroSer.setRseMargemRest3(margem3.subtract(margemUsada3).add(margemRest.min(BigDecimal.ZERO)));
                }

            } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                final BigDecimal margemUsada = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                final BigDecimal margemRest = (registroSer.getRseMargemRest2() != null) ? registroSer.getRseMargemRest2() : new BigDecimal("0.00");

                registroSer.setRseMargemUsada2(margemUsada.add(adeVlr));
                registroSer.setRseMargemRest2(margemRest.subtract(adeVlr));

                if (margem123Casadas) {
                    final BigDecimal margemRest1 = registroSer.getRseMargemRest().subtract(adeVlr);
                    registroSer.setRseMargemUsada(registroSer.getRseMargemUsada().add(adeVlr));
                    registroSer.setRseMargemRest(margemRest1);

                    final BigDecimal margem2 = (registroSer.getRseMargem2() != null) ? registroSer.getRseMargem2() : new BigDecimal("0.00");
                    final BigDecimal margemUsada2 = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                    final BigDecimal margemRest2 = margemRest1.min(margem2.subtract(margemUsada2));
                    registroSer.setRseMargemRest2(margemRest2);

                    final BigDecimal margem3 = (registroSer.getRseMargem3() != null) ? registroSer.getRseMargem3() : new BigDecimal("0.00");
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                    registroSer.setRseMargemRest3(margemRest3);

                } else if (margem123CasadasEsq) {
                    // Se margem 1 casada pela esquerda com a 2 e 3, e a reserva incide na margem 2
                    // então subtrai da margem 1 e 3 o mesmo valor extraido da margem 1
                    final BigDecimal margemUsada1 = (registroSer.getRseMargemUsada() != null) ? registroSer.getRseMargemUsada() : new BigDecimal("0.00");
                    final BigDecimal margemRest1 = (registroSer.getRseMargemRest() != null) ? registroSer.getRseMargemRest() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada(margemUsada1.add(adeVlr));
                    registroSer.setRseMargemRest(margemRest1.subtract(adeVlr));

                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = (registroSer.getRseMargemRest3() != null) ? registroSer.getRseMargemRest3() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada3(margemUsada3.add(adeVlr));
                    registroSer.setRseMargemRest3(margemRest3.subtract(adeVlr));
                }

            } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                final BigDecimal margemUsada = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                final BigDecimal margemRest = (registroSer.getRseMargemRest3() != null) ? registroSer.getRseMargemRest3() : new BigDecimal("0.00");

                registroSer.setRseMargemUsada3(margemUsada.add(adeVlr));
                registroSer.setRseMargemRest3(margemRest.subtract(adeVlr));

                if (margem1CasadaMargem3) {
                    // Se margem 1 casada com 3, e o contrato incide na margem 3, abate
                    // o mesmo valor também da margem 1
                    registroSer.setRseMargemUsada(registroSer.getRseMargemUsada().add(adeVlr));
                    registroSer.setRseMargemRest(registroSer.getRseMargemRest().subtract(adeVlr));

                } else if (margem123Casadas) {
                    final BigDecimal margemRest1 = registroSer.getRseMargemRest().subtract(adeVlr);
                    registroSer.setRseMargemUsada(registroSer.getRseMargemUsada().add(adeVlr));
                    registroSer.setRseMargemRest(margemRest1);

                    final BigDecimal margem2 = (registroSer.getRseMargem2() != null) ? registroSer.getRseMargem2() : new BigDecimal("0.00");
                    BigDecimal margemUsada2 = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                    margemUsada2 = margemUsada2.add(adeVlr);
                    final BigDecimal margemRest2 = margemRest1.min(margem2.subtract(margemUsada2));
                    registroSer.setRseMargemUsada2(margemUsada2);
                    registroSer.setRseMargemRest2(margemRest2);

                    final BigDecimal margem3 = (registroSer.getRseMargem3() != null) ? registroSer.getRseMargem3() : new BigDecimal("0.00");
                    final BigDecimal margemUsada3 = (registroSer.getRseMargemUsada3() != null) ? registroSer.getRseMargemUsada3() : new BigDecimal("0.00");
                    final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                    registroSer.setRseMargemRest3(margemRest3);

                } else if (margem1CasadaMargem3Esq) {
                    // Se margem 1 casada pela esquerda com a 3, e a reserva incide na margem 3
                    // então subtrai da margem 1 o mesmo valor extraido da margem 1
                    final BigDecimal margemUsada1 = (registroSer.getRseMargemUsada() != null) ? registroSer.getRseMargemUsada() : new BigDecimal("0.00");
                    final BigDecimal margemRest1 = (registroSer.getRseMargemRest() != null) ? registroSer.getRseMargemRest() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada(margemUsada1.add(adeVlr));
                    registroSer.setRseMargemRest(margemRest1.subtract(adeVlr));

                } else if (margem123CasadasEsq) {
                    // Se margem 1 casada pela esquerda com a 2 e 3, e a reserva incide na margem 3
                    // então subtrai da margem 1 e 2 o mesmo valor extraido da margem 3
                    final BigDecimal margemUsada1 = (registroSer.getRseMargemUsada() != null) ? registroSer.getRseMargemUsada() : new BigDecimal("0.00");
                    final BigDecimal margemRest1 = (registroSer.getRseMargemRest() != null) ? registroSer.getRseMargemRest() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada(margemUsada1.add(adeVlr));
                    registroSer.setRseMargemRest(margemRest1.subtract(adeVlr));

                    final BigDecimal margemUsada2 = (registroSer.getRseMargemUsada2() != null) ? registroSer.getRseMargemUsada2() : new BigDecimal("0.00");
                    final BigDecimal margemRest2 = (registroSer.getRseMargemRest2() != null) ? registroSer.getRseMargemRest2() : new BigDecimal("0.00");
                    registroSer.setRseMargemUsada2(margemUsada2.add(adeVlr));
                    registroSer.setRseMargemRest2(margemRest2.subtract(adeVlr));
                } else if (margem1CasadaMargem3Lateral) {
                    // Se margem 1 casada pela com a 3 lateralmente, e a reserva incide na margem 3
                    // então não afeta a margem 1, e o diferencial aplicado na restante 3 e usada 3
                    // é o suficiente para deixar a margem correta.
                }

            } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                // Controle de margem pela tabela tb_margem_registro_servidor
                final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, rseCodigo);
                final MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);

                final BigDecimal margemUsada = (mrsBean.getMrsMargemUsada() != null) ? mrsBean.getMrsMargemUsada() : new BigDecimal("0.00");
                final BigDecimal margemRest = (mrsBean.getMrsMargemRest() != null) ? mrsBean.getMrsMargemRest() : new BigDecimal("0.00");

                mrsBean.setMrsMargemUsada(margemUsada.add(adeVlr));
                mrsBean.setMrsMargemRest(margemRest.subtract(adeVlr));

                AbstractEntityHome.update(mrsBean);

                // Atualiza os casamentos de margem extras
                final List<HistoricoMargemRse> historicosMargemExtra = atualizaMargemExtraCasada(rseCodigo, adeIncMargem, adeVlr, margemRest, ocaCodigo, null, responsavel);
                historicos.addAll(historicosMargemExtra);

                // Verifica se a situação atual do servidor foi alterada pela rotina de segurança ao atualizar a margem extra casada
                final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(registroSer.getRseCodigo());
                if (!rse.getSrsCodigo().equalsIgnoreCase(registroSer.getSrsCodigo())) {
                    // Recupera a nova situação do servidor
                    registroSer.setSrsCodigo(rse.getSrsCodigo());
                }
            }

            AbstractEntityHome.update(registroSer);

            // Finaliza o histórico de margem. Grava os valores
            // atuais de margem restante associado a ocorrencia
            final List<HistoricoMargemRse> historicosMargem = gravarHistoricoMargem(registroSer.getRseCodigo(), ocaCodigo,
                    margemRest1Antes, registroSer.getRseMargemRest(),
                    margemRest2Antes, registroSer.getRseMargemRest2(),
                    margemRest3Antes, registroSer.getRseMargemRest3(),
                    null,
                    responsavel);
            historicos.addAll(historicosMargem);

            // Recalcula a pontuação do usuário, se necessário
            try {
                pontuacaoServidorController.calcularPontuacao(rseCodigo, responsavel);
            } catch (final ZetraException ex) {
                // Não pode lançar exceção pois dá efeitos colaterais em outros métodos
                LOG.error(ex.getMessage(), ex);
            }

            // Retorna o histórico de Margem
            return historicos;


        } catch (FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza os casamentos de margens extras, de acordo com a configuração presente
     * na tabela tb_casamento_margem.
     * @param rseCodigo       : Código do registro servidor
     * @param adeIncMargem    : Código da margem afetada inicialmente
     * @param adeVlr          : Valor afetado da margem
     * @param margemRestAntes : Valor da margem antes
     * @param ocaCodigo       : Código da ocorrência que gerou a modificação
     * @throws AutorizacaoControllerException
     */
    private List<HistoricoMargemRse> atualizaMargemExtraCasada(String rseCodigo, Short adeIncMargem, BigDecimal adeVlr, BigDecimal margemRestAntes, String ocaCodigo, String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Hash com os objetos de margem do servidor para atualização
            final Map<Short, MargemRegistroServidor> mrsMap = new HashMap<>();
            // Hash com os valores restantes de margem do servidor antes da atualização, para gravação de histórico
            final Map<Short, BigDecimal> margemAntesMap = new HashMap<>();
            // Hash com os valores restantes de margem do servidor depois da atualização, para gravação de histórico
            final Map<Short, BigDecimal> margemDepoisMap = new HashMap<>();

            // Busca a margem originalmente afetada e coloca no hash, necessário para posterior utilização
            MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, rseCodigo);
            MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);
            mrsMap.put(adeIncMargem, mrsBean);
            margemAntesMap.put(adeIncMargem, margemRestAntes);
            margemDepoisMap.put(adeIncMargem, mrsBean.getMrsMargemRest());

            // Lista de margens que foram afetadas, e podem desencadear efeitos em outras margens
            final List<Short> marCodigosOrigem = new ArrayList<>();
            marCodigosOrigem.add(adeIncMargem);

            // Lista dos históricos de margem, gerados pelas atualizações das margens
            final List<HistoricoMargemRse> historicos = new ArrayList<>();

            // Navega nos grupos de casamento:
            final List<Short> grupos = CasamentoMargem.getInstance().getGrupos();
            if ((grupos != null) && (grupos.size() > 0)) {
                for (final Short grupo : grupos) {
                    final String tipo = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                    final List<Short> marCodigosCasamento = CasamentoMargem.getInstance().getMargensCasadas(grupo);

                    // 1) Se a margem afetada, desencadeia efeito em uma margem deste grupo:
                    final Set<Short> marCodigosAfetados = CasamentoMargem.getInstance().getMargensAfetadas(grupo, marCodigosOrigem);

                    for (final Short marCodigoAfetado : marCodigosAfetados) {
                        // 1.1) Busca o objeto de margem para o casamento
                        mrsPK = new MargemRegistroServidorId(marCodigoAfetado, rseCodigo);
                        mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);

                        // 1.2) Adiciona o objeto de margem para posterior utilização
                        mrsMap.put(marCodigoAfetado, mrsBean);
                        if (!margemAntesMap.containsKey(marCodigoAfetado)) {
                            margemAntesMap.put(marCodigoAfetado, mrsBean.getMrsMargemRest());
                        }

                        // 1.3) Afeta a margem usada do casamento
                        mrsBean.setMrsMargemUsada(mrsBean.getMrsMargemUsada().add(adeVlr));

                        // 1.4) Adiciona o código desta margem na lista de margens usadas afetadas
                        marCodigosOrigem.add(marCodigoAfetado);
                    }

                    // 1.4) Recalcula as margens restantes do grupo de casamento, caso tenha alguma margem afetada
                    for (int i = 0; i < marCodigosCasamento.size(); i++) {
                        final Short marCodigo = marCodigosCasamento.get(i);
                        if (mrsMap.containsKey(marCodigo)) {
                            mrsBean = mrsMap.get(marCodigo);
                        } else {
                            mrsPK = new MargemRegistroServidorId(marCodigo, rseCodigo);
                            mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);
                            mrsMap.put(marCodigo, mrsBean);
                            if (!margemAntesMap.containsKey(marCodigo)) {
                                margemAntesMap.put(marCodigo, mrsBean.getMrsMargemRest());
                            }
                        }

                        if (CasamentoMargem.DIREITA.equals(tipo)) {
                            LOG.debug("Calcula grupo " + grupo + " de margem casada pela direita [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
                            if (i == 0) {
                                // Se é a margem base: rest = margem - usada
                                mrsBean.setMrsMargemRest(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada()));
                            } else {
                                // Se é outra margem, então: rest = min(rest_anterior, margem - usada)
                                final MargemRegistroServidor mrsBeanAnterior = mrsMap.get(marCodigosCasamento.get(i - 1));
                                final BigDecimal margemRestAnterior = mrsBeanAnterior.getMrsMargemRest();
                                mrsBean.setMrsMargemRest(margemRestAnterior.min(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada())));
                            }

                        } else if (CasamentoMargem.ESQUERDA.equals(tipo)) {
                            LOG.debug("Calcula grupo " + grupo + " de margem casada pela esquerda [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
                            mrsBean.setMrsMargemRest(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada()));

                        } else if (CasamentoMargem.LATERAL.equals(tipo)) {
                            LOG.debug("Calcula grupo " + grupo + " de margem casada lateralmente [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
                            if (i == 0) {
                                // Se é a margem base: rest = margem - usada
                                mrsBean.setMrsMargemRest(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada()));
                            } else {
                                // Se é outra margem, então: rest = min(rest_anterior, margem - usada)
                                final MargemRegistroServidor mrsBeanAnterior = mrsMap.get(marCodigosCasamento.get(i - 1));
                                final BigDecimal margemRestAnterior = mrsBeanAnterior.getMrsMargemRest();
                                mrsBean.setMrsMargemRest(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada()).add(margemRestAnterior.min(BigDecimal.ZERO)));
                            }

                        } else if (CasamentoMargem.MINIMO.equals(tipo)) {
                            LOG.debug("Calcula grupo " + grupo + " de margem casada limitada ao mínimo [" + TextHelper.join(marCodigosCasamento, ",") + "]: " + DateHelper.getSystemDatetime());
                            if (i > 0) {
                                // Se não é a margem base: então: rest = min(rest_anterior, margem - usada)
                                final MargemRegistroServidor mrsBeanAnterior = mrsMap.get(marCodigosCasamento.get(i - 1));
                                final BigDecimal margemRestAnterior = mrsBeanAnterior.getMrsMargemRest();
                                mrsBean.setMrsMargemRest(margemRestAnterior.min(mrsBean.getMrsMargem().subtract(mrsBean.getMrsMargemUsada())));
                            }
                        }

                        // Executa a atualização do registro
                        AbstractEntityHome.update(mrsBean);

                        // Salva o valor de margem depois, sobrepondo no map para manter o último valor,
                        // para salvar depois o histórico de margem apenas uma vez
                        margemDepoisMap.put(marCodigo, mrsBean.getMrsMargemRest());
                    }
                }

                if (!margemDepoisMap.isEmpty()) {
                    final List<Short> marCodigosAtualizados = new ArrayList<>(margemDepoisMap.keySet());
                    Collections.sort(marCodigosAtualizados);
                    boolean liberouMargem = false;

                    for (final Short marCodigo : marCodigosAtualizados) {
                        // Grava histórico de margem
                        LOG.debug("Margem " + marCodigo + " antes: " + margemAntesMap.get(marCodigo) + " | Margem " + marCodigo + " depois: " + margemDepoisMap.get(marCodigo));
                        final HistoricoMargemRse hmrBean = gravarHistoricoMargem(rseCodigo, marCodigo, ocaCodigo, margemAntesMap.get(marCodigo), margemDepoisMap.get(marCodigo), responsavel);
                        if (hmrBean != null) {
                            historicos.add(hmrBean);
                            liberouMargem |= (margemDepoisMap.get(marCodigo).compareTo(margemAntesMap.get(marCodigo)) > 0);
                        }
                    }

                    if (liberouMargem) {
                        // Se é uma operação de liberação de margem, então registra esta operação no controle de segurança
                        registrarControleSeguranca(rseCodigo, adeCodigo, responsavel);
                    }
                }

            } else {
                // Grava histórico de margem daquela inicialmente afetada, já que não possui casamento
                LOG.debug("Margem " + adeIncMargem + " antes: " + margemRestAntes + " | Margem " + adeIncMargem + " depois: " + mrsBean.getMrsMargemRest());
                final HistoricoMargemRse hmrBean = gravarHistoricoMargem(rseCodigo, adeIncMargem, ocaCodigo, margemRestAntes, mrsBean.getMrsMargemRest(), responsavel);
                if (hmrBean != null) {
                    historicos.add(hmrBean);

                    if (mrsBean.getMrsMargemRest().compareTo(margemRestAntes) > 0) {
                        // Se é uma operação de liberação de margem, então registra esta operação no controle de segurança
                        registrarControleSeguranca(rseCodigo, adeCodigo, responsavel);
                    }
                }
            }
            return historicos;
        } catch (final FindException ex) {
            try {
                final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKeyForUpdate(rseCodigo);

                // O registro servidor não possui a margem extra relativa ao contrato.
                // Verifica a situação do servidor, caso seja excluído e a operação de liquidação, ignora a atualização.
                final List<String> funLiquidacao = new ArrayList<>();
                funLiquidacao.add(CodedValues.FUN_LIQ_CONTRATO);
                funLiquidacao.add(CodedValues.FUN_CANC_COMPRA);
                funLiquidacao.add(CodedValues.FUN_CANC_CONSIGNACAO);
                funLiquidacao.add(CodedValues.FUN_CANC_RENEGOCIACAO);
                funLiquidacao.add(CodedValues.FUN_CANC_RESERVA);
                funLiquidacao.add(CodedValues.FUN_CANC_SOLICITACAO);
                funLiquidacao.add(CodedValues.FUN_CONF_LIQUIDACAO);
                funLiquidacao.add(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA);

                if (funLiquidacao.contains(responsavel.getFunCodigo()) &&
                    CodedValues.SRS_INATIVOS.contains(registroServidor.getStatusRegistroServidor().getSrsCodigo())) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.margem.nao.cadastrada", responsavel));
                    return new ArrayList<>();
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erro.margem.nao.cadastrada", responsavel, ex);
                }
            } catch (final FindException e) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verificação de margem para serviços compulsórios.
     * Se não foi possível reservar todo o valor, verifica se o serviço tem
     * limite para autorizações sem margem. Verifica também se não é um compulsório,
     * e caso positivo, pesquisa o vlr total dos contratos que podem ser estocados
     * liberando assim vlr de margem para a inclusão
     * @param rseCodigo            : código do registro servidor envolvido na reserva
     * @param svcCodigo            : código do serviço compulsório
     * @param csaCodigo            : código da consignatária
     * @param adeCodigo            : código da consignação a ser incluida
     * @param ocaCodigo            : código da ocorrência gerado
     * @param adeVlr               : valor da consignação
     * @param vlrLimiteSemMargem   : valor permitido para reserva sem margem
     * @param adeIncMargem         : incide margem
     * @param parametros           : parâmetros
     * @param validaBloqueado      : se true verifica se o servidor está bloqueado
     * @param serAtivo             : se true verifica se o servidor está ativo
     * @param responsavel          : usuário que executa a operação
     * @return List<HistoricoMargemRse>
     * @throws AutorizacaoControllerException
     */
    protected List<HistoricoMargemRse> atualizaMargemCompulsorios(String rseCodigo, String svcCodigo, String csaCodigo, String adeCodigo, String ocaCodigo,
            BigDecimal adeVlr, BigDecimal vlrLimiteSemMargem, Short adeIncMargem, Map<String, Object> parametros,
            boolean validaBloqueado, boolean serAtivo, boolean alteracao, AcessoSistema responsavel) throws AutorizacaoControllerException {

        try {
            List<HistoricoMargemRse> historicos = null;

            BigDecimal vlrContratos = new BigDecimal("0.00");
            BigDecimal margemDisponivelCompulsorio = new BigDecimal("0.00");

            boolean servicoCompulsorio = false;
            boolean consideraMargemRestAtualCompulsorio = false;
            final boolean temControleCompulsorios = (ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel));
            try {
                if (temControleCompulsorios) {
                    // O serviço só é um compulsório caso o sistema tenha controle de compulsórios
                    final CustomTransferObject param = getParametroSvc(CodedValues.TPS_SERVICO_COMPULSORIO, svcCodigo, "", false, parametros);
                    if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
                        servicoCompulsorio = ("1".equals(param.getAttribute(Columns.PSE_VLR).toString()));
                    }
                    final CustomTransferObject paramSvcConsideraMargemRestAtualCompulsorio = getParametroSvc(CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO, svcCodigo, "", false, parametros);
                    if ((paramSvcConsideraMargemRestAtualCompulsorio != null) && (paramSvcConsideraMargemRestAtualCompulsorio.getAttribute(Columns.PSE_VLR) != null)) {
                        consideraMargemRestAtualCompulsorio = ("1".equals(paramSvcConsideraMargemRestAtualCompulsorio.getAttribute(Columns.PSE_VLR).toString()));
                    }
                }
            } catch (final NumberFormatException ex2) {
                LOG.error(ex2.getMessage(), ex2);
            }
            /*
             * Se for um compulsório, pesquisa o valor total passivel de liberação
             * para inclusão de um novo compulsório
             */
            String svcPrioridade = null;
            if (servicoCompulsorio) {
                try {
                    final Servico servico = ServicoHome.findByPrimaryKey(svcCodigo);
                    svcPrioridade = servico.getSvcPrioridade() != null ? servico.getSvcPrioridade() : null;
                    if ((svcPrioridade != null) && !"".equals(svcPrioridade)) {
                    	/*
                         * é compulsório e tem prioridade definida, então busca
                         * a soma dos valores dos contratos
                         */
                        final ListaContratosCompulsoriosQuery query = new ListaContratosCompulsoriosQuery();
                        query.somatorioValor = true;
                        query.rseCodigo = rseCodigo;
                        query.svcCodigo = svcCodigo;
                        query.svcPrioridade = svcPrioridade;
                        List<TransferObject> values = query.executarDTO();
                        if ((values != null) && !values.isEmpty()) {
                            final TransferObject to = values.get(0);
                            if (!TextHelper.isNull(to.getAttribute("VLR"))) {
                                vlrContratos = new BigDecimal(to.getAttribute("VLR").toString());
                            }
                        }

                        final boolean controlaMargem = !ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel);

                        // Obtem margem restante disponível no período
                        values = null;
                        BigDecimal saldoPeriodo = BigDecimal.ZERO;
                        final ListaMargemDisponivelCompulsorioQuery queryMargem = new ListaMargemDisponivelCompulsorioQuery();
                        queryMargem.rseCodigo = rseCodigo;
                        queryMargem.adeIncMargem = adeIncMargem;
                        queryMargem.controlaMargem = controlaMargem;
                        queryMargem.adeCodigo = adeCodigo;
                        queryMargem.alteracao = alteracao;
                        values = queryMargem.executarDTO();
                        if ((values != null) && !values.isEmpty()) {
                            final TransferObject cto = values.get(0);
                            if (cto.getAttribute("MARGEM") != null) {
                                saldoPeriodo = saldoPeriodo.add((BigDecimal) cto.getAttribute("MARGEM"));
                            }
                            if (cto.getAttribute("MARGEM_USADA") != null) {
                                saldoPeriodo = saldoPeriodo.subtract((BigDecimal) cto.getAttribute("MARGEM_USADA"));
                            }
                        }

                        // Soma do valor dos contratos que podem ser liberados para a inclusão de um compulsório
                        margemDisponivelCompulsorio = vlrContratos;
                        // Adiciona a margem restante disponível no período, somente se ela for positiva e o parâmetro de consideração for false
                        if ((saldoPeriodo.signum() > 0) || consideraMargemRestAtualCompulsorio) {
                            margemDisponivelCompulsorio = margemDisponivelCompulsorio.add(saldoPeriodo);
                        }
                    } else {
                        /*
                         * Se o serviço não tem priodidade definida então deve ser tratado
                         * como se não fosse compulsório
                         */
                        servicoCompulsorio = false;
                    }
                } catch (final HQueryException e) {
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
                }
            }
            if ((vlrLimiteSemMargem.signum() > 0) || (margemDisponivelCompulsorio.signum() > 0)) {
                if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                    LOG.debug("ERRO!! NAO ACEITOU A MARGEM COM O INCIDE MARGEM 0");
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
                }

                /*
                 * Se o serviço permite consignações sem margem, ou é um compulsório,
                 * então verifica se o valor do contrato é permitido.
                 */
                final RegistroServidor registroSer = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                BigDecimal rseMargemRest = obtemMargemRestante(RegistroServidorDtoAssembler.createDto(registroSer, true), adeIncMargem, csaCodigo, false, null, responsavel);

                if (servicoCompulsorio && (rseMargemRest.signum() == -1)) {
                    /*
                     * Se a margem restante é negativa, exibe o valor zero, deixando
                     * que o compulsório possa derrubar os contratos possíveis.
                     */
                    rseMargemRest = new BigDecimal("0.00");
                }

                if ((rseMargemRest.add(vlrLimiteSemMargem).add(vlrContratos).compareTo(adeVlr) >= 0) ||
                        (margemDisponivelCompulsorio.compareTo(adeVlr) >= 0)) {
                    /*
                     * Se o valor da margem + valor limite de inclusão sem margem +
                     * valor dos contratos para inclusão de compulsório ou
                     * o valor a margem disponível para compulsório for maior ou
                     * igual ao ADE_VLR, então atualiza a margem sem verificar se
                     * irá ficar negativa e pode prosseguir com a inclusão do contrato
                     */
                    try {
                        historicos = atualizaMargem(rseCodigo, adeIncMargem, adeVlr, false, validaBloqueado, serAtivo, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                    } catch (final Exception e) {
                        LOG.error(e.getMessage(), e);
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
                    }

                    if (servicoCompulsorio) {
                        /*
                         * Se o serviço é compulsório, verifica se é necessário
                         * colocar em estoque algum contrato para continuar com a
                         * inclusão
                         */
                        BigDecimal diff = adeVlr.subtract(margemDisponivelCompulsorio.subtract(vlrContratos)).subtract(vlrLimiteSemMargem);
                        if (diff.signum() > 0) {
                            /*
                             * O Diff é o valor que deve ser liberado com o estoque de outros
                             * contratos, já que o ade_vlr é maior que a soma da margem
                             * com o valor limite de inclusão sem margem
                             */
                            try {
                                /*
                                 * Antes de listar os contratos a serem "estocados", verifica se o servidor
                                 * possui operações ainda não confirmadas, e caso existam, impede a inclusão
                                 * do compulsório, evitando que um contrato mais antigo seja colocado em estoque.
                                 */
                                final List<String> sadCodigos = new ArrayList<>(CodedValues.SAD_CODIGOS_AGUARD_CONF);
                                sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);
                                final ObtemTotalConsignacaoRseQuery totalAdeNaoConfQuery = new ObtemTotalConsignacaoRseQuery();
                                totalAdeNaoConfQuery.rseCodigo = rseCodigo;
                                totalAdeNaoConfQuery.sadCodigos = sadCodigos;
                                if (totalAdeNaoConfQuery.executarContador() > 0) {
                                    throw new AutorizacaoControllerException("mensagem.erro.operacao.compulsorio.nao.permitida.existem.operacoes.nao.confirmadas.servidor", responsavel, ApplicationResourcesHelper.getMessage(alteracao ? "rotulo.acoes.alterar" : "rotulo.acoes.incluir", responsavel).toLowerCase());
                                }

                                /*
                                 * Lista os contratos que serão colocados em estoque para a inclusão do
                                 * compulsório, em ordem decrescente da data de inclusão.
                                 */
                                final ListaContratosCompulsoriosQuery query = new ListaContratosCompulsoriosQuery();
                                query.rseCodigo = rseCodigo;
                                query.svcCodigo = svcCodigo;
                                query.svcPrioridade = svcPrioridade;
                                final List<TransferObject> contratosIncComp = query.executarDTO();
                                if ((contratosIncComp != null) && !contratosIncComp.isEmpty()) {
                                    String adeCodigoLiberacao = null;
                                    BigDecimal adeVlrLiberacao = null;
                                    BigDecimal saldoEmEstoque = null;
                                    String adeCodigoDestinoCompulsorio = null;
                                    String adeCodigoDestinoCompulsorioComSaldo = null;
                                    boolean usouSaldoEstoque = false;
                                    for (final TransferObject proxAde : contratosIncComp) {
                                    	adeCodigoDestinoCompulsorio = (String) proxAde.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO);
                                    	adeCodigoDestinoCompulsorioComSaldo = (String) proxAde.getAttribute("ADE_SALDO_ESTOQUE");
                                    	if (!usouSaldoEstoque) {
                                    		saldoEmEstoque = (BigDecimal) proxAde.getAttribute("SALDO_ESTOQUE");
                                    		// Utiliza o saldo em estoque somente quando o contrato listado é o contrato com saldo em estoque
                                    		if ((saldoEmEstoque.compareTo(new BigDecimal("0.00")) > 0)
                                    				&& !TextHelper.isNull(adeCodigoDestinoCompulsorio)
                                    				&& !TextHelper.isNull(adeCodigoDestinoCompulsorioComSaldo)
                                    				&& adeCodigoDestinoCompulsorio.equals(adeCodigoDestinoCompulsorioComSaldo)) {

                                    			diff = diff.subtract(saldoEmEstoque);
                                        		usouSaldoEstoque = true;

                                        		adeCodigoLiberacao = (String) proxAde.getAttribute(Columns.ADE_CODIGO);
                                                colocarEmEstoqueParaIncComp(adeCodigoLiberacao, adeCodigo, responsavel);
                                                if (diff.signum() <= 0) {
                                                    /*
                                                     * Se já liberou valor suficiente, então
                                                     * não precisa mais estocar contratos.
                                                     */
                                                    break;
                                                }
                                    		}
                                    	}

                                    	// Se é destino compulsório, já usou o saldo restante e não deve deduzir novamente o valor a ser liberado
                                    	// Ou quando o contrato já participou de um fluxo de compulsório e estoque, porém não tem mais saldo de estoque (adeCodigoDestinoCompulsorioComSaldo),
                                    	// é preciso considerar o contrato como se nunca tivesse participado do fluxo
                                    	if (TextHelper.isNull(adeCodigoDestinoCompulsorio) || (TextHelper.isNull(adeCodigoDestinoCompulsorioComSaldo) && !usouSaldoEstoque)) {
                                            adeCodigoLiberacao = (String) proxAde.getAttribute(Columns.ADE_CODIGO);
                                            adeVlrLiberacao = (BigDecimal) proxAde.getAttribute(Columns.ADE_VLR);
                                            colocarEmEstoqueParaIncComp(adeCodigoLiberacao, adeCodigo, responsavel);

                                            diff = diff.subtract(adeVlrLiberacao);
                                            if (diff.signum() <= 0) {
                                                /*
                                                 * Se já liberou valor suficiente, então
                                                 * não precisa mais estocar contratos.
                                                 */
                                                break;
                                            }
                                    	}

                                    }
                                    if (diff.signum() > 0) {
                                        /*
                                         * Se ainda tem diferença a ser liberada e os contratos
                                         * acabaram então retorna erro de insuficiência de margem.
                                         */
                                        LOG.debug("ERRO!! Ainda tem diferença a ser liberada");
                                        throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
                                    }
                                } else {
                                    /*
                                     * Existe uma diferença a ser liberada, porém não foram encontrados
                                     * contratos para serem liberados. Retorna erro de insuficiência de
                                     * margem.
                                     */
                                    LOG.debug("ERRO!! Existe uma diferença a ser liberada, porém não foram encontrados");
                                    throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
                                }
                            } catch (final HQueryException e) {
                                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
                            }
                        } else {
                            /*
                             * Não é necessário liberação de margem através
                             * do estoque de contratos deferidos ou em andamento.
                             * Prossegue a inclusão do contrato.
                             */
                        }
                    } else {
                        /*
                         * Não é um compulsório, então prossegue com a inclusão
                         * do novo contrato.
                         */
                    }
                } else {
                    /*
                     * Caso o ADE_VLR seja maior do que o possível, então retorna
                     * erro de insuficiência de margem.
                     */
                    LOG.debug("ERRO!! ADE_VLR maior do que o possível");
                    throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
                }
            } else {
                /*
                 * Se não tem limite de inclusão sem margem, e o serviço compulsório
                 * não tem valor disponivel para inclusão, então retorna
                 * erro de insuficiência de margem.
                 */
                LOG.debug("ERRO!! Não tem limite de inclusão sem margem, e o serviço compulsório não tem valor disponivel para inclusão");
                throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
            }

            // Não é necessário atualização, pois a margem é efetivamente alterada no método atualizaMargem
            // RegistroServidorHome.update(registroSer);

            return historicos;
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Muda o status de um contrato para ESTOQUE liberando assim margem para inclusão
     * de contratos compulsórios
     * @param adeCodigo
     * @param adeCodigoCompulsorio
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void colocarEmEstoqueParaIncComp(String adeCodigo, String adeCodigoCompulsorio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            final String sadCodigoNovo = CodedValues.SAD_ESTOQUE_MENSAL;
            final String sadCodigoAtual = autdes.getStatusAutorizacaoDesconto().getSadCodigo();
            final List<String> sadCodigoCompulsorio = new ArrayList<>();

            if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_ESTOQUE_NAO_CONTABILIZAM_MARGEM, responsavel)) {
                sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_SEM_ESTOQUE);
            } else {
                sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_COM_ESTOQUE);
            }

            if (sadCodigoCompulsorio.contains(sadCodigoAtual)) {
                if (CodedValues.SAD_DEFERIDA.equals(sadCodigoAtual) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigoAtual)) {
                    // Altera o status apenas dos Deferidos e Em Andamento
                    autdes.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(sadCodigoNovo));
                    AbstractEntityHome.update(autdes);

                    // Grava ocorrência de alteração de status
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, sadCodigoAtual, sadCodigoNovo), responsavel);

                    // Caso não exista email cadatrado da CSA o email não é enviado, porém não paramos o processo de estoque, deixamos dar continuidade.
                    EnviaEmailHelper.enviarEmailConsignatariaContratoColocadoEmEstoque(autdes, responsavel);
                }

                if (CodedValues.SAD_EMANDAMENTO.equals(sadCodigoAtual)) {
                    // Se o contratos estava em andamento, insere ocorrência de liquidação para
                    // que o sistema mande uma exclusão para a folha de pagamentos
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.liquidacao.contrato", responsavel), responsavel);
                }

                try {
                    // Caso o relacionamento entre o contrato origem e destino já existam, faz a alteração da data do relacionamento.
                    // Isso pode ocorrer no caso de alteração de contrato compulsório, onde um contrato que já havia sido retirado para inclusão
                    // do compulsório, deverá ser retirado novamente da folha para a alteração comportar o valor
                    final RelacionamentoAutorizacaoId id = new RelacionamentoAutorizacaoId(adeCodigoCompulsorio, adeCodigo, CodedValues.TNT_CONTROLE_COMPULSORIOS);
                    final RelacionamentoAutorizacao relAdeBean = RelacionamentoAutorizacaoHome.findByPrimaryKey(id);
                    relAdeBean.setRadData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    relAdeBean.setUsuario(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                    AbstractEntityHome.update(relAdeBean);
                } catch (final FindException ex) {
                    // Insere relacionamento entre os contratos com natureza de Controle de Compulsórios
                    RelacionamentoAutorizacaoHome.create(adeCodigoCompulsorio, adeCodigo, CodedValues.TNT_CONTROLE_COMPULSORIOS, responsavel.getUsuCodigo());
                }

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SUSPENDER_CONSIGNACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.somente.contratos.deferidos.ou.andamento.podem.ser.colocados.estoque.para.inclusao.compulsorios", responsavel);
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof AutorizacaoControllerException) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    protected boolean permiteRenegociarComprarMargem3NegativaCasada(RegistroServidorTO registroSer, Short adeIncMargem, BigDecimal adeVlrAnterior, BigDecimal adeVlrNovo, boolean validaMargemNegativa, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final boolean margemCasada = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel) ||
                    ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel) ||
                    ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, responsavel) ||
                    ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel) ||
                    ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel);

            final boolean margem3Negativa = validaMargemNegativa ? ((registroSer.getRseMargemRest3() != null) && (registroSer.getRseMargemRest3().signum() < 0)) : true;
            final boolean permiteRenegociarComprarMargem3NegativaCasada = margemCasada && adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3) && margem3Negativa && ((adeVlrAnterior != null) && (adeVlrNovo.compareTo(adeVlrAnterior) <= 0)) ? ParamSist.getBoolParamSist(CodedValues.TPC_RENEGOCIACAO_COMPRA_MARGEM_3_NEG_CASADA, responsavel) : false;

            if (permiteRenegociarComprarMargem3NegativaCasada) {
                final String rseCodigo = registroSer.getRseCodigo();
                final List<Short> incideMargens = new ArrayList<>();

                if (ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel) ||
                        ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel) ||
                        ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, responsavel)) {

                    incideMargens.add(CodedValues.INCIDE_MARGEM_SIM);
                }

                if (ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel) ||
                        ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel)) {

                    incideMargens.add(CodedValues.INCIDE_MARGEM_SIM_2);
                }

                final ListaContratosIncideMargemQuery query = new ListaContratosIncideMargemQuery();
                query.count = true;
                query.rseCodigo = rseCodigo;
                query.adeIncideMargens = incideMargens;

                return query.executarContador() > 0;
            }

            return false;

        } catch (final HQueryException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void verificaMargemRenegociacao(RegistroServidorTO registroSer, Short adeIncMargem, BigDecimal adeVlrAnterior, BigDecimal adeVlrNovo, String csaCodigo, List<String> adeCodigosRenegociacao, boolean compra, Integer prazoNovo, BigDecimal vlrDesconto, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, ParametroControllerException {
        final BigDecimal margemRest = obtemMargemRestante(registroSer, adeIncMargem, csaCodigo, true, adeCodigosRenegociacao, responsavel);
        final BigDecimal margemRestOri = obtemMargemRestante(registroSer, adeIncMargem, csaCodigo, true, adeCodigosRenegociacao, responsavel);

        if (margemRest.add(adeVlrAnterior).subtract(adeVlrNovo).signum() < 0) {
            // Se a margem pós operação ficará negativa, verifica se é permitida esta operação
            final boolean permiteRenegociarComprarMargem3NegativaCasada = permiteRenegociarComprarMargem3NegativaCasada(registroSer, adeIncMargem, adeVlrAnterior, adeVlrNovo, true, responsavel) &&
                ((adeIncMargem != null) && adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3));

            // DESENV-16086 - Verifica se a portabilidade permite manter o servidor com margem negativa.
            final boolean permiteCompraSerMargemNegativa = verificaPermitePortabilidadeServidorMargemNegativa(prazoNovo, adeVlrNovo, adeVlrAnterior, adeCodigosRenegociacao, compra, responsavel);

            final boolean condicionaPortabilidadeRenegociacao = verificaCondicionaPortabilidadeRenegociacao(vlrDesconto, margemRest, margemRestOri, adeVlrNovo, adeVlrAnterior, svcCodigo, responsavel);

            if (!permiteRenegociarComprarMargem3NegativaCasada && !permiteCompraSerMargemNegativa && !condicionaPortabilidadeRenegociacao) {
                throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
            }
        }
    }

    protected void verificaMargem(RegistroServidorTO registroSer, Short adeIncMargem, BigDecimal adeVlr, String csaCodigo, String svcCodigo, boolean calculaMargemProporcional, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final BigDecimal margemRest = obtemMargemRestante(registroSer, adeIncMargem, csaCodigo, calculaMargemProporcional, adeCodigosRenegociacao, responsavel);

        if (margemRest.subtract(adeVlr).signum() < 0) {
            // Verifica se exibe margem na crítica de lote
            final CustomTransferObject paramExibeMargem = getParametroSvc(CodedValues.TPS_EXIBE_MARGEM_CRITICA_LOTE, svcCodigo, "", false, null);
            final boolean exibeMargemViaLote = ((paramExibeMargem != null) && (paramExibeMargem.getAttribute(Columns.PSE_VLR) != null) &&
                    "1".equals(paramExibeMargem.getAttribute(Columns.PSE_VLR)));

            final MargemTO marTO = MargemHelper.getInstance().getMargem(adeIncMargem, responsavel);
            final ExibeMargem exibeMargem = new ExibeMargem(marTO, responsavel);

            // Se for lote e o parâmetro que exibe margem na importação de lote habilitado, inclui margem disponíveml na mensagem de erro
            if (exibeMargemViaLote && CodedValues.FUNCOES_IMPORTACAO_LOTE.contains(responsavel.getFunCodigo()) && exibeMargem.isExibeValor()) {
                throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel.arg0", responsavel, NumberHelper.format(margemRest.doubleValue(), NumberHelper.getLang(), true));
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
            }
        }
    }

    protected BigDecimal obtemMargemRestante(RegistroServidorTO registroSer, Short adeIncMargem, String csaCodigo, boolean calculaMargemProporcional, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) {
        final String rseCodigo = registroSer.getRseCodigo();
        BigDecimal margem = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal margemRest = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal margemUsada = BigDecimal.ZERO;

        // Default: margem 1
        adeIncMargem = (adeIncMargem == null ? CodedValues.INCIDE_MARGEM_SIM : adeIncMargem);

        if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
            margem = (registroSer.getRseMargem() != null ? registroSer.getRseMargem() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemRest = (registroSer.getRseMargemRest() != null ? registroSer.getRseMargemRest() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemUsada = (registroSer.getRseMargemUsada() != null ? registroSer.getRseMargemUsada() : BigDecimal.ZERO);

        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
            margem = (registroSer.getRseMargem2() != null ? registroSer.getRseMargem2() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemRest = (registroSer.getRseMargemRest2() != null ? registroSer.getRseMargemRest2() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemUsada = (registroSer.getRseMargemUsada2() != null ? registroSer.getRseMargemUsada2() : BigDecimal.ZERO);

        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            margem = (registroSer.getRseMargem3() != null ? registroSer.getRseMargem3() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemRest = (registroSer.getRseMargemRest3() != null ? registroSer.getRseMargemRest3() : BigDecimal.valueOf(Double.MIN_VALUE));
            margemUsada = (registroSer.getRseMargemUsada3() != null ? registroSer.getRseMargemUsada3() : BigDecimal.ZERO);

        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            // Não indice margem, então retorna o valor máximo
            margemRest = BigDecimal.valueOf(Double.MAX_VALUE);

        } else {
            // Margem Extra: busca o registro de margem
            try {
                final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, rseCodigo);
                final MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKey(mrsPK);
                margem = mrsBean.getMrsMargem();
                margemRest = mrsBean.getMrsMargemRest();
                margemUsada = mrsBean.getMrsMargemUsada();
            } catch (final FindException ex) {
                LOG.warn("Registro de margem " + adeIncMargem + " não encontrado para o registro servidor " + rseCodigo);
                margemRest = BigDecimal.valueOf(Double.MIN_VALUE);
            }
        }

        if (calculaMargemProporcional && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3, CodedValues.TPC_SIM, responsavel) &&
                (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM) || adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3))) {
            try {
                margemRest = consultarMargemController.calcularMargemProporcional(adeIncMargem, margemRest, registroSer, csaCodigo, adeCodigosRenegociacao, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                margemRest = BigDecimal.valueOf(Double.MIN_VALUE);
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_MARGEM_ORIGINAL_EXCEDE_ATE_MARGEM_LATERAL, CodedValues.TPC_SIM, responsavel) &&
                ((adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM) && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel)) ||
                        CasamentoMargem.getInstance().temCasamentoDoTipo(CasamentoMargem.LATERAL))) {
            try {
                margemRest = consultarMargemController.calcularMargemLateral(adeIncMargem, margemRest, registroSer, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                margemRest = BigDecimal.valueOf(Double.MIN_VALUE);
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_LIMITAR_USO_MARGEM_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                !adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            try {
                // Limita a margem para a consignatária
                margemRest = consultarMargemController.limitarMargemRestanteCsa(adeIncMargem, rseCodigo, margem, margemRest, margemUsada, csaCodigo, registroSer.getOrgCodigo(), responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                margemRest = BigDecimal.valueOf(Double.MIN_VALUE);
            }
        }

        return margemRest;
    }

    protected BigDecimal obtemMargemFolha(RegistroServidorTO registroSer, Short adeIncMargem, AcessoSistema responsavel) {
        BigDecimal margem = BigDecimal.valueOf(Double.MIN_VALUE);

        // Default: margem 1
        adeIncMargem = (adeIncMargem == null ? CodedValues.INCIDE_MARGEM_SIM : adeIncMargem);

        if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
            margem = (registroSer.getRseMargem() != null ? registroSer.getRseMargem() : BigDecimal.valueOf(Double.MIN_VALUE));

        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
            margem = (registroSer.getRseMargem2() != null ? registroSer.getRseMargem2() : BigDecimal.valueOf(Double.MIN_VALUE));

        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            margem = (registroSer.getRseMargem3() != null ? registroSer.getRseMargem3() : BigDecimal.valueOf(Double.MIN_VALUE));

        } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            // Margem Extra: busca o registro de margem
            try {
                final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, registroSer.getRseCodigo());
                final MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKey(mrsPK);
                margem = mrsBean.getMrsMargem();
            } catch (final FindException ex) {
                LOG.warn("Registro de margem " + adeIncMargem + " não encontrado para o registro servidor " + registroSer.getRseCodigo());
            }
        }

        return margem;
    }

    /**
     * Grava o histórico de margem, salvando os valores originais das margens fixas (1,2,3) do servidor informado nos parâmetros.
     * @param rseCodigo         : Código do registro servidor que teve atualizações de margem
     * @param ocaCodigo         : Código da ocorrência do evento que gerou atualização de margens
     * @param margemRest1Antes  : Valor da margem 1 antes da atualização
     * @param margemRest1Depois : Valor da margem 1 depois da atualização
     * @param margemRest2Antes  : Valor da margem 2 antes da atualização
     * @param margemRest2Depois : Valor da margem 2 depois da atualização
     * @param margemRest3Antes  : Valor da margem 3 antes da atualização
     * @param margemRest3Depois : Valor da margem 3 depois da atualização
     * @param responsavel
     * @return List<HistoricoMargemRse>
     * @throws AutorizacaoControllerException
     */
    private List<HistoricoMargemRse> gravarHistoricoMargem(String rseCodigo, String ocaCodigo, BigDecimal margemRest1Antes, BigDecimal margemRest1Depois,
            BigDecimal margemRest2Antes, BigDecimal margemRest2Depois,
            BigDecimal margemRest3Antes, BigDecimal margemRest3Depois,
            String adeCodigo,
            AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<HistoricoMargemRse> historicos = new ArrayList<>();
            boolean liberouMargem = false;

            if ((margemRest1Antes != null) && (margemRest1Depois != null) && !margemRest1Antes.equals(margemRest1Depois)) {
                final HistoricoMargemRse hmrBean1 = HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM,
                        ocaCodigo, OperacaoHistoricoMargemEnum.CONSIGNACAO.getCodigo(), margemRest1Antes, margemRest1Depois);
                historicos.add(hmrBean1);
                liberouMargem |= (margemRest1Depois.compareTo(margemRest1Antes) > 0);
            }

            if ((margemRest2Antes != null) && (margemRest2Depois != null) && !margemRest2Antes.equals(margemRest2Depois)) {
                final HistoricoMargemRse hmrBean2 = HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_2,
                        ocaCodigo, OperacaoHistoricoMargemEnum.CONSIGNACAO.getCodigo(), margemRest2Antes, margemRest2Depois);
                historicos.add(hmrBean2);
                liberouMargem |= (margemRest2Depois.compareTo(margemRest2Antes) > 0);
            }

            if ((margemRest3Antes != null) && (margemRest3Depois != null) && !margemRest3Antes.equals(margemRest3Depois)) {
                final HistoricoMargemRse hmrBean3 = HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_3,
                        ocaCodigo, OperacaoHistoricoMargemEnum.CONSIGNACAO.getCodigo(), margemRest3Antes, margemRest3Depois);
                historicos.add(hmrBean3);
                liberouMargem |= (margemRest3Depois.compareTo(margemRest3Antes) > 0);
            }

            if (liberouMargem) {
                // Se é uma operação de liberação de margem, então registra esta operação no controle de segurança
                registrarControleSeguranca(rseCodigo, adeCodigo, responsavel);
            }

            return historicos;
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Grava o histórico de margem, salvando os valores originais da margem extra do servidor informado nos parâmetros.
     * @param rseCodigo        : Código do registro servidor que teve atualizações de margem
     * @param marCodigo        : Código da margem afetada
     * @param ocaCodigo        : Código da ocorrência do evento que gerou atualização de margens
     * @param margemRestAntes  : Valor da margem antes da atualização
     * @param margemRestDepois : Valor da margem depois da atualização
     * @param responsavel
     * @return HistoricoMargemRse
     * @throws AutorizacaoControllerException
     */
    private HistoricoMargemRse gravarHistoricoMargem(String rseCodigo, Short marCodigo, String ocaCodigo, BigDecimal margemRestAntes, BigDecimal margemRestDepois, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if ((margemRestAntes != null) && (margemRestDepois != null) && !margemRestAntes.equals(margemRestDepois)) {
                return HistoricoMargemRegistroServidorHome.create(rseCodigo, marCodigo, ocaCodigo, OperacaoHistoricoMargemEnum.CONSIGNACAO.getCodigo(),
                        margemRestAntes, margemRestDepois);
            }
            return null;
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Registra a operação que liberou margem do servidor para o usuário operador do sistema
     * @param rseCodigo   : Código do registro servidor que teve atualizações de margem
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void registrarControleSeguranca(String rseCodigo, String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            segurancaController.registrarOperacoesLiberacaoMargem(rseCodigo, adeCodigo, responsavel);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void atualizaHistoricosMargem(List<HistoricoMargemRse> historicos, String ocaCodigo, String adeCodigo, String tocCodigo) throws AutorizacaoControllerException {
        this.atualizaHistoricosMargem(historicos, ocaCodigo, null, adeCodigo, tocCodigo);
    }

    protected void atualizaHistoricosMargem(List<HistoricoMargemRse> historicos, String ocaCodigo, Date ocaPeriodo, String adeCodigo, String tocCodigo) throws AutorizacaoControllerException {
        try {
            OcorrenciaAutorizacao oca = null;

            if ((historicos != null) && (historicos.size() > 0)) {
                for (final HistoricoMargemRse historico : historicos) {
                    if ((historico != null) && (historico.getOcorrenciaAutorizacao() == null)) {
                        if (oca == null) {
                            if (ocaCodigo != null) {
                                oca = OcorrenciaAutorizacaoHome.findByPrimaryKey(ocaCodigo);
                            } else if ((adeCodigo != null) && (tocCodigo != null)) {
                                final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, tocCodigo);
                                if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                                    oca = ocorrencias.iterator().next();
                                }
                            }
                        }
                        if (oca != null) {
                            if (ocaPeriodo != null) {
                                oca.setOcaPeriodo(ocaPeriodo);
                                AbstractEntityHome.update(oca);
                            }
                            historico.setOcorrenciaAutorizacao(oca);
                            AbstractEntityHome.update(historico);
                        }
                    }
                }
            }
        } catch (final FindException e) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        } catch (final UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
    }

    @Override
    public boolean temMargem(String rseCodigo, String svcCodigo, BigDecimal adeVlr, Short incMargem, boolean serAtivo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (!incMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            final BigDecimal vlrDesconto = this.calcularValorDescontoParcela(rseCodigo, svcCodigo, adeVlr);

            try {
                // Faz a consulta de margem, passando o serviço. Se não der exceção o servidor tem margem
                return consultarMargemController.servidorTemMargem(rseCodigo, vlrDesconto, svcCodigo, serAtivo, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(ex);
            }
        }

        return true;
    }

    /** ====================================== FIM GERENCIAMENTO DE MARGEM ====================================== **/

    /** ======================================== MÉTODOS UTILITÁRIOS ======================================== **/

    /**
     * Verifica se o usuário é pertence ao gestor e possui permissão para alteração avançada de consignação.
     *
     * @param responsavel Usuário que será validado.
     * @return Retorna <code>true</code> se o usuário possui permissão para alteração avançada de consignação.
     */
    protected boolean usuarioPossuiAltAvancadaAde(AcessoSistema responsavel) {
        return (responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO)) ||
                (responsavel.isCseSupOrg() && CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL)) ||
                ((responsavel.isCseSup() || responsavel.isCsa()) && CodedValues.FUN_ALTERAR_MULTIPLOS_CONTRATOS.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_ALTERAR_MULTIPLOS_CONTRATOS)) ||
                (responsavel.isCsa() && CodedValues.FUN_AJUSTAR_CONSIGNACOES_A_MARGEM.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_AJUSTAR_CONSIGNACOES_A_MARGEM)) ||
                (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel) && CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL)) ||
                responsavel.isSistema()
                ;
    }

    /**
     * Verifica se uma nova reserva para o serviço identificado por "svcCodigo", feito pela
     * consignatária identificada por "csaCodigo", para o servidor "rseCodigo", requer
     * deferimento manual.
     * @param svcCodigo : código do serviço da nova reserva
     * @param csaCodigo : código da consignatária da nova reserva
     * @param rseCodigo : código do registro servidor da nova reserva
     * @param comSenha  : TRUE se a senha foi informada
     * @param responsavel : responsável pela operação
     * @return
     */
    protected boolean usuarioPodeDeferir(String svcCodigo, String csaCodigo, String rseCodigo, boolean comSenha, AcessoSistema responsavel) {
        // Parâmetro de Serviço/Consignatária TPS_CNV_PODE_DEFERIR
        boolean cnvPodeDeferir = true;
        try {
            final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, CodedValues.TPS_CNV_PODE_DEFERIR);
            cnvPodeDeferir = ((psc == null) || (psc.getPscVlr() == null) || !"N".equals(psc.getPscVlr()));
        } catch (final FindException ex) {
            // Parâmetro não existe, então permanece o valor default: TRUE
        }

        // Parâmetro de Serviço TPS_REQUER_DEFERIMENTO_RESERVAS
        boolean svcRequerDeferimento = false;
        try {
            final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            svcRequerDeferimento = ((pse != null) && (pse.getPseVlr() != null) && "1".equals(pse.getPseVlr()));
        } catch (final FindException ex) {
            // Parâmetro não existe, então permanece o valor default: FALSE
        }

        // Parâmetro de Sistema TPC_SER_SENHA_DEFERE_RESERVA
        final boolean senhaServidorDefereReserva = ParamSist.paramEquals(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, CodedValues.TPC_SIM, responsavel);

        // Se o serviço não requer deferimento e a consignatária pode deferir OU
        // foi informado senha do servidor e esta defere a reserva ENTÃO
        // retorna TRUE
        boolean podeDeferir = (!svcRequerDeferimento && cnvPodeDeferir) || (comSenha && senhaServidorDefereReserva);

        // Se o deferimento não é manual, verifica relacionamento de serviço que torna o deferimento manual
        if (podeDeferir && NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO)) {
            try {
                final ObtemQtdAdeRelSvcRequerDeferimentoQuery query = new ObtemQtdAdeRelSvcRequerDeferimentoQuery();
                query.svcCodigoDestino = svcCodigo;
                query.rseCodigo = rseCodigo;
                podeDeferir = (query.executarContador() == 0);
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return podeDeferir;
    }

    protected boolean usuarioPodeConfirmarReserva(AcessoSistema responsavel) {
        return ((responsavel != null) && responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA));
    }

    protected boolean usuarioPodeConfirmarEmDoisPassos(AcessoSistema responsavel) {
        return ((responsavel != null) && responsavel.temPermissao(CodedValues.FUN_RESERVA_CONFIRMA_DOIS_PASSOS));
    }

    protected boolean usuarioPodeConfirmarRenegociacao(AcessoSistema responsavel) {
        return ((responsavel != null) && responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO));
    }

    protected boolean usuarioPodeConfirmarLiquidacao(List<AutDesconto> adeList, boolean renegociacao, boolean podeConfirmarRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException, ParametroControllerException, FindException {
        boolean podeConfirmarLiquidacao = true;

        if (!responsavel.isSer() && (adeList != null) && !adeList.isEmpty() && efetuarLiquidacaoDuasEtapas(responsavel)) {
            final boolean exigeDuplaConfLiquidacao = exigirDuplaConfirmacaoLiquidacao(responsavel);

            for (final AutDesconto ade : adeList) {
                final String adeCodigo = ade.getAdeCodigo();
                final String sadCodigo = ade.getSadCodigo();

                if (CodedValues.FUN_LIQ_CONTRATO.equals(responsavel.getFunCodigo())) {
                    if (CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadCodigo) && !renegociacao) {
                        throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.status.invalido", responsavel, ade.getStatusAutorizacaoDesconto().getSadDescricao());
                    } else if (!responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO)) {
                        // Se existe liquidação em duas etapas, e o usuário que realiza uma liquidação não tem
                        // permissão de confirmar liquidação, apenas altera o status do contrato para Aguard. Liquidação.
                        if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA)) {
                            // Se já estiver na situação Aguard. Liq. Compra, verifica função de confirmação de liquidação de compra
                            podeConfirmarLiquidacao &= true;
                        } else {
                            // Em qualquer outro caso de liquidação, com usuário sem permissão de confirmar liquidação, atribui
                            // o flag para não permitir a confirmação da liquidação.
                            podeConfirmarLiquidacao &= false;
                        }
                    }
                } else if (CodedValues.FUN_CONF_LIQUIDACAO.equals(responsavel.getFunCodigo())) {
                    if (!CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadCodigo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.status.invalido", responsavel, ade.getStatusAutorizacaoDesconto().getSadDescricao());
                    } else {
                        // Operação de confirmação de liquidação deve operar apenas sobre contratos na situação Aguard. Liquidação que não sejam origem
                        // de processo de renegociação pendente, ou seja o destino esteja Aguard. Confirmação ou Aguard. Deferimento
                        final Collection<RelacionamentoAutorizacao> radRenegociacaoAtivo = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO, CodedValues.SAD_CODIGOS_AGUARD_CONF);
                        if ((radRenegociacaoAtivo != null) && !radRenegociacaoAtivo.isEmpty()) {
                            throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.status.invalido", responsavel, ade.getStatusAutorizacaoDesconto().getSadDescricao());
                        }
                    }
                }

                if (exigeDuplaConfLiquidacao && podeConfirmarLiquidacao && (!renegociacao || !podeConfirmarRenegociacao)) {
                    // Verifica se a ADE atual do loop já tem uma confirmação de liquidação, se não tiver, não pode confirmar
                    final List<OcorrenciaAutorizacao> ocaList = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE);
                    if ((ocaList != null) && !ocaList.isEmpty()) {
                        final List<String> usuCodigosConfLiq = ocaList.stream().map(OcorrenciaAutorizacao::getUsuCodigo).distinct().toList();
                        if (usuCodigosConfLiq.size() < 2) {
                            // Se tem menos de duas ocorrência de confirmação, verifica se é o mesmo usuário que está tentando confirmar a operação
                            if (responsavel.getUsuCodigo().equals(usuCodigosConfLiq.get(0))) {
                                // Lança exceção informando que a liquidação deve ser confirmada por um segundo usuário
                                throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.conf.outro.usuario", responsavel);
                            } else if (!responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO)) {
                                // Se não é o mesmo, mas o usuário atual não tem permissão de confirmar liquidação, então também não pode confirmar a liquidação
                                podeConfirmarLiquidacao = false;
                            }
                        }
                    } else {
                        podeConfirmarLiquidacao = false;
                    }
                }
            }
        }

        return podeConfirmarLiquidacao;
    }

    @Override
    public boolean efetuarLiquidacaoDuasEtapas(AcessoSistema responsavel) throws ParametroControllerException {
        // Verifica se a liquidação se dá em duas etapas, ou se pode ser feita diretamente. Mesmo em sistema que tenha liquidação
        // em duas etapas, caso o usuário tenha permissão de confirmar liquidação, a liquidação será direta, sem necessidade de confirmação.
        boolean habilitaLiqDuasEtapas = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, CodedValues.TPC_SIM, responsavel);

        if (!habilitaLiqDuasEtapas && responsavel.isCsaCor() && CanalEnum.WEB.equals(responsavel.getCanal()) && !responsavel.isOperacaoViaLote()) {
            // Se o parâmetro de sistema não está habilitado, verifica se o parâmetro de consignatária que sobrepõe
            // esta configuração está habilitado, fazendo liquidação em duas etapas mesmo que o sistema não tenha
            final String pcsVlr = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS_CSA, responsavel);
            habilitaLiqDuasEtapas = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
        }

        return habilitaLiqDuasEtapas;
    }

    @Override
    public boolean exigirDuplaConfirmacaoLiquidacao(AcessoSistema responsavel) throws ParametroControllerException {
        boolean exigeDuplaConfLiquidacao = false;
        if (responsavel.isCsaCor() && CanalEnum.WEB.equals(responsavel.getCanal()) && !responsavel.isOperacaoViaLote()) {
            final String pcsVlr = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_EXIGE_DUPLA_CONFIRMACAO_LIQUIDACAO_ADE, responsavel);
            exigeDuplaConfLiquidacao = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
        }
        return exigeDuplaConfLiquidacao;
    }

    /**
     * Caso o contrato vá para situação SAD_AGUARD_DEFER, e o serviço possui prazo
     * para deferimento automático, inclui ocorrência de informação, informando
     * que o contrato está pendente de deferimento até a data calculada pelo parâmetro.
     * @param autdes : Contrato que está sendo incluído / confirmado
     * @param svcCodigo : Serviço do contrato
     * @param parametros : Cache de parâmetros
     * @param responsavel : Responsável pela operação
     * @throws AutorizacaoControllerException
     */
    protected void alertarPrazoDeferimentoAut(AutDesconto autdes, String svcCodigo, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final String adeCodigo = autdes.getAdeCodigo();

        // Busca parâmetro de dias para deferimento automático do contrato
        final TransferObject tpsPrazoDeferAutomatico = getParametroSvc(CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS, svcCodigo, "", false, parametros);
        final String paramPrazoDeferAutomatico = (tpsPrazoDeferAutomatico != null ? (String) tpsPrazoDeferAutomatico.getAttribute(Columns.PSE_VLR) : "");
        if (!TextHelper.isNull(paramPrazoDeferAutomatico)) {
            Integer diasDeferAutomatico = 0;
            try {
                diasDeferAutomatico = Integer.valueOf(paramPrazoDeferAutomatico);
            } catch (final NumberFormatException ex) {
                LOG.warn("Valor incorreto para o parâmetro de serviço '" + CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS + "' do serviço '" + svcCodigo + "'.");
                diasDeferAutomatico = 0;
            }
            if (diasDeferAutomatico > 0) {
                final Date adeData = autdes.getAdeData();
                Date dataDeferAut = null;

                // Calcula a data de deferimento automático
                if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_DEFER_AUTOMATICO_ADE, CodedValues.TPC_SIM, responsavel)) {
                    // Utilizando dias úteis
                    try {
                        dataDeferAut = calendarioController.findProximoDiaUtil(adeData, diasDeferAutomatico);
                    } catch (final CalendarioControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                } else {
                    // Utilizando dias corridos
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(adeData);
                    cal.add(Calendar.DAY_OF_MONTH, diasDeferAutomatico);
                    dataDeferAut = cal.getTime();
                }

                if (dataDeferAut != null) {
                    // Inclui nova ocorrência no contrato informando a data para deferimento automático
                    final String mensagem = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.aguard.deferimento", responsavel, DateHelper.toDateString(dataDeferAut));
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, mensagem, AcessoSistema.getAcessoUsuarioSistema());
                }
            }
        }

        // Se o sistema permite deferimento pelas consignatárias, verifica qual consignatária
        // deve ser alertada que existem contratos pendentes de deferimento
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) &&
                NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO)) {
            try {
                final List<String> adeCodigos = new ArrayList<>();
                adeCodigos.add(adeCodigo);
                final ObtemCsaBloqRelSvcRequerDeferimentoQuery query = new ObtemCsaBloqRelSvcRequerDeferimentoQuery();
                query.adeCodigos = adeCodigos;
                query.ativo = false;
                final List<String> csaCodigos = query.executarLista();
                if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                    // Se o servidor possui contratos no serviço relacionado para requerer deferimento
                    // manual, envia e-mail à consignatária que deve proceder o deferimento
                    for (final String csaCodigo : csaCodigos) {
                        EnviaEmailHelper.enviarEmailCsaPendenciaDeferimento(csaCodigo, adeCodigo, responsavel);
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            } catch (final ViewHelperException ex) {
                // Não lança exceção em caso de erro no envio de e-mail, apenas registra no log
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return modificaSituacaoADE(autdes, status, responsavel, true, true);
    }

    @Override
    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel,
            boolean geraOcorrencia, boolean liberaMargem) throws AutorizacaoControllerException {
        return this.modificaSituacaoADE(autdes, status, responsavel, geraOcorrencia, null, liberaMargem);
    }

    @Override
    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel, boolean geraOcorrencia,
            java.util.Date ocaPeriodo, boolean liberaMargem) throws AutorizacaoControllerException {
        try {
            final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            // Verifica se a alteração de status não foi realizada por outro cliente
            final String sadAtual = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
            if (sadAtual.equals(status)) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.ja.esta.nesta.situacao", responsavel);
            }

            // Guarda os valores originais das margens restantes para gravação do histórico de margem
            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKeyForUpdate(autdes.getRegistroServidor().getRseCodigo());
            final BigDecimal margemRest1Antes = registroServidor.getRseMargemRest();
            final BigDecimal margemRest2Antes = registroServidor.getRseMargemRest2();
            final BigDecimal margemRest3Antes = registroServidor.getRseMargemRest3();

            final String adeCodigo = autdes.getAdeCodigo();
            final String tipoVlr = autdes.getAdeTipoVlr() != null ? autdes.getAdeTipoVlr() : CodedValues.TIPO_VLR_FIXO;
            BigDecimal adeVlr = autdes.getAdeVlr();

            final BigDecimal adeVlrFolha = autdes.getAdeVlrFolha();
            final Short adeIncMargem = autdes.getAdeIncMargem() != null ? autdes.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;

            // Altera o status da ade
            autdes.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(status));

            // Verifica alteração das datas ADE_DATA_CONFIRMACAO, ADE_DATA_DEFERIMENTO e ADE_DATA_EXCLUSAO,
            // de acordo com as migrações de status da consignação.
            final Date dataAtual = DateHelper.getSystemDatetime();

            if ((CodedValues.SAD_SOLICITADO.equals(sadAtual) ||
                    CodedValues.SAD_AGUARD_CONF.equals(sadAtual)) &&
                    CodedValues.SAD_AGUARD_DEFER.equals(status)) {
                // Atualiza data de confirmação
                autdes.setAdeDataConfirmacao(dataAtual);

            } else if ((CodedValues.SAD_SOLICITADO.equals(sadAtual) ||
                    CodedValues.SAD_AGUARD_CONF.equals(sadAtual)) &&
                    CodedValues.SAD_DEFERIDA.equals(status)) {
                // Atualiza data de confirmação e deferimento
                autdes.setAdeDataConfirmacao(dataAtual);
                autdes.setAdeDataDeferimento(dataAtual);

            } else if (CodedValues.SAD_AGUARD_DEFER.equals(sadAtual) &&
                    CodedValues.SAD_DEFERIDA.equals(status)) {
                // Atualiza data de deferimento
                autdes.setAdeDataDeferimento(dataAtual);

            } else if (CodedValues.SAD_CANCELADA.equals(status) ||
                    CodedValues.SAD_LIQUIDADA.equals(status) ||
                    CodedValues.SAD_CONCLUIDO.equals(status) ||
                    CodedValues.SAD_ENCERRADO.equals(status)) {
                // Atualiza data de exclusão
                autdes.setAdeDataExclusao(dataAtual);

            } else if ((CodedValues.SAD_CANCELADA.equals(sadAtual) ||
                    CodedValues.SAD_LIQUIDADA.equals(sadAtual)) && (
                            CodedValues.SAD_DEFERIDA.equals(status) ||
                            CodedValues.SAD_EMANDAMENTO.equals(status))) {
                // Desliquidação ou Descancelamento: limpa a data de exclusão
                autdes.setAdeDataExclusao(null);

            } else if (CodedValues.SAD_ENCERRADO.equals(sadAtual) && !CodedValues.SAD_CODIGOS_INATIVOS.contains(status)) {
                // Reabertura de consignação encerrada: limpa a data de exclusão
                autdes.setAdeDataExclusao(null);
            }

            // Salva as alterações
            AbstractEntityHome.update(autdes);

            // Grava ocorrência de alteração
            String ocaCodigo = null;
            if (geraOcorrencia) {
                ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, sadAtual, status), null, null, null, ocaPeriodo, null, responsavel);
            }

            LOG.debug("SITUAÇÃO ATUAL: " + sadAtual + ", SITUAÇÃO NOVA: " + status);
            LOG.debug("INCIDE MARGEM: " + adeIncMargem);

            /* As mudanças de status abaixo liberam a margem utilizada no desconto,
               para ser usada em outras reservas. */
            if (liberaMargem) {
                if ((CodedValues.SAD_SOLICITADO.equals(sadAtual) ||
                        CodedValues.SAD_AGUARD_CONF.equals(sadAtual) ||
                        CodedValues.SAD_AGUARD_DEFER.equals(sadAtual) ||
                        CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadAtual) ||
                        CodedValues.SAD_DEFERIDA.equals(sadAtual) ||
                        CodedValues.SAD_EMANDAMENTO.equals(sadAtual) ||
                        CodedValues.SAD_EMCARENCIA.equals(sadAtual) ||
                        CodedValues.SAD_ESTOQUE.equals(sadAtual) ||
                        CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadAtual) ||
                        CodedValues.SAD_ESTOQUE_MENSAL.equals(sadAtual) ||
                        CodedValues.SAD_SUSPENSA.equals(sadAtual) ||
                        CodedValues.SAD_SUSPENSA_CSE.equals(sadAtual)) &&
                        (CodedValues.SAD_INDEFERIDA.equals(status) ||
                                CodedValues.SAD_LIQUIDADA.equals(status) ||
                                CodedValues.SAD_CANCELADA.equals(status) ||
                                CodedValues.SAD_CONCLUIDO.equals(status) ||
                                CodedValues.SAD_ENCERRADO.equals(status))) {

                    // Caso o contrato seja fruto de renegociação e esteja dentro do prazo de cancelamento, e seja um contrato feito com valor menor do total de renegocições
                    // precisamos pegar o valor total da renegociação e não o atual para que a margem retorne corretamente, então o valor do contrato na verdade deve ser a soma das renegociações
                    final BigDecimal vlrTotalRenegociao = getValorContratoTotalRenegociacao(autdes, responsavel);

                    if (vlrTotalRenegociao.compareTo(adeVlr) > 0) {
                        ocaCodigo = criaOcorrenciaADE(autdes.getAdeCodigo(), CodedValues.TOC_LIBERACAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO, ApplicationResourcesHelper.getMessage("mensagem.info.ocorrencia.margem.liberada.encerrou.contrato.destino", responsavel), responsavel);
                        adeVlr = vlrTotalRenegociao;
                    }

                    if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                        // Margem rest Antiga
                        final BigDecimal margemRestAntigo = registroServidor.getRseMargemRest();

                        BigDecimal margem = registroServidor.getRseMargem();
                        BigDecimal margemRest = registroServidor.getRseMargemRest();
                        BigDecimal margemUsada = registroServidor.getRseMargemUsada();

                        LOG.debug("MARGEM USADA: " + margemUsada + ", VALOR DA ADE.: " + adeVlr + (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) ? "%" : ""));
                        LOG.debug("MARGEM REST ANTES: " + registroServidor.getRseMargemRest());

                        if (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) && (adeVlrFolha != null)) {
                            margemUsada = margemUsada.subtract(adeVlrFolha);
                        } else {
                            margemUsada = margemUsada.subtract(adeVlr);
                        }
                        registroServidor.setRseMargemUsada(margemUsada);
                        registroServidor.setRseMargemRest(margem.subtract(margemUsada));

                        LOG.debug("MARGEM REST DEPOIS: " + registroServidor.getRseMargemRest());

                        // Margem 1 Atual
                        margem = registroServidor.getRseMargem();
                        margemRest = registroServidor.getRseMargemRest();
                        margemUsada = registroServidor.getRseMargemUsada();
                        if (margem1CasadaMargem3) {
                            final BigDecimal varMargemRest = margemRest.subtract(margemRestAntigo);

                            if (margemRest.compareTo(registroServidor.getRseMargemRest3()) == -1) {
                                registroServidor.setRseMargemRest3(registroServidor.getRseMargemRest());
                            } else if (varMargemRest.compareTo(new BigDecimal("0.00")) == 1) {
                                final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                                final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                                final BigDecimal min = registroServidor.getRseMargemRest().min(margem3.subtract(margemUsada3));
                                registroServidor.setRseMargemRest3(min);
                            }

                        } else if (margem123Casadas) {
                            final BigDecimal margem2 = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                            final BigDecimal margemUsada2 = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");
                            final BigDecimal margemRest2 = margemRest.min(margem2.subtract(margemUsada2));
                            registroServidor.setRseMargemRest2(margemRest2);

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                            registroServidor.setRseMargemRest3(margemRest3);

                        } else if (margem1CasadaMargem3Esq) {
                            // Se margem 1 casada pela esquerda com a 3, e o contrato incide na margem 1
                            // então libera da margem 3 o mesmo valor liberado da margem 1
                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada3(margemUsada3.subtract(adeVlr));
                            registroServidor.setRseMargemRest3(margem3.subtract(margemUsada3.subtract(adeVlr)));

                        } else if (margem123CasadasEsq) {
                            // Se margem 1 casada pela esquerda com a 2 e 3, e o contrato incide na margem 1
                            // então libera da margem 2 e 3 o mesmo valor liberado da margem 1
                            final BigDecimal margem2 = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                            final BigDecimal margemUsada2 = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada2(margemUsada2.subtract(adeVlr));
                            registroServidor.setRseMargemRest2(margem2.subtract(margemUsada2.subtract(adeVlr)));

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada3(margemUsada3.subtract(adeVlr));
                            registroServidor.setRseMargemRest3(margem3.subtract(margemUsada3.subtract(adeVlr)));

                        } else if (margem1CasadaMargem3Lateral) {
                            // Se margem 1 casada pela com a 3 lateralmente, e a reserva incide na margem 1
                            // então subtrai da margem 3 o valor negativo da margem 1 restante
                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            // RSE_MARGEM_REST_3 = RSE_MARGEM_3 - RSE_MARGEM_USADA_3 + MIN(0, RSE_MARGEM_REST)
                            registroServidor.setRseMargemRest3(margem3.subtract(margemUsada3).add(margemRest.min(BigDecimal.ZERO)));
                        }

                    } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                        final BigDecimal margem = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                        BigDecimal margemUsada = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");

                        LOG.debug("MARGEM USADA: " + margemUsada + ", VALOR DA ADE.: " + adeVlr + (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) ? "%" : ""));
                        LOG.debug("MARGEM REST ANTES: " + registroServidor.getRseMargemRest2());

                        if (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) && (adeVlrFolha != null)) {
                            margemUsada = margemUsada.subtract(adeVlrFolha);
                        } else {
                            margemUsada = margemUsada.subtract(adeVlr);
                        }
                        registroServidor.setRseMargemUsada2(margemUsada);
                        registroServidor.setRseMargemRest2(margem.subtract(margemUsada));

                        LOG.debug("MARGEM REST DEPOIS: " + registroServidor.getRseMargemRest2());

                        if (margem123Casadas) {
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");
                            margemUsada1 = margemUsada1.subtract(adeVlr);
                            final BigDecimal margemRest1 = margem1.subtract(margemUsada1);
                            registroServidor.setRseMargemUsada(margemUsada1);
                            registroServidor.setRseMargemRest(margemRest1);

                            final BigDecimal margem2 = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                            final BigDecimal margemUsada2 = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");
                            final BigDecimal margemRest2 = margemRest1.min(margem2.subtract(margemUsada2));
                            registroServidor.setRseMargemRest2(margemRest2);

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                            registroServidor.setRseMargemRest3(margemRest3);

                        } else if (margem123CasadasEsq) {
                            // Se margem 1 casada pela esquerda com a 2 e 3, e o contrato incide na margem 2
                            // então libera da margem 1 e 3 o mesmo valor liberado da margem 2
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            final BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada(margemUsada1.subtract(adeVlr));
                            registroServidor.setRseMargemRest(margem1.subtract(margemUsada1.subtract(adeVlr)));

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada3(margemUsada3.subtract(adeVlr));
                            registroServidor.setRseMargemRest3(margem3.subtract(margemUsada3.subtract(adeVlr)));
                        }

                    } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                        final BigDecimal margem = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                        BigDecimal margemUsada = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");

                        LOG.debug("MARGEM USADA: " + margemUsada + ", VALOR DA ADE.: " + adeVlr + (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) ? "%" : ""));
                        LOG.debug("MARGEM REST ANTES: " + registroServidor.getRseMargemRest3());

                        if (CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) && (adeVlrFolha != null)) {
                            margemUsada = margemUsada.subtract(adeVlrFolha);
                        } else {
                            margemUsada = margemUsada.subtract(adeVlr);
                        }
                        registroServidor.setRseMargemUsada3(margemUsada);
                        registroServidor.setRseMargemRest3(margem.subtract(margemUsada));

                        if (margem1CasadaMargem3) {
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");

                            margemUsada1 = margemUsada1.subtract(adeVlr);
                            registroServidor.setRseMargemUsada(margemUsada1);
                            registroServidor.setRseMargemRest(margem1.subtract(margemUsada1));

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            final BigDecimal min = registroServidor.getRseMargemRest().min(margem3.subtract(margemUsada3));

                            registroServidor.setRseMargemRest3(min);

                        } else if (margem123Casadas) {
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");
                            margemUsada1 = margemUsada1.subtract(adeVlr);
                            final BigDecimal margemRest1 = margem1.subtract(margemUsada1);
                            registroServidor.setRseMargemUsada(margemUsada1);
                            registroServidor.setRseMargemRest(margemRest1);

                            final BigDecimal margem2 = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                            BigDecimal margemUsada2 = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");
                            margemUsada2 = margemUsada2.subtract(adeVlr);
                            final BigDecimal margemRest2 = margemRest1.min(margem2.subtract(margemUsada2));
                            registroServidor.setRseMargemUsada2(margemUsada2);
                            registroServidor.setRseMargemRest2(margemRest2);

                            final BigDecimal margem3 = (registroServidor.getRseMargem3() != null) ? registroServidor.getRseMargem3() : new BigDecimal("0.00");
                            final BigDecimal margemUsada3 = (registroServidor.getRseMargemUsada3() != null) ? registroServidor.getRseMargemUsada3() : new BigDecimal("0.00");
                            final BigDecimal margemRest3 = margemRest2.min(margem3.subtract(margemUsada3));
                            registroServidor.setRseMargemRest3(margemRest3);

                        } else if (margem1CasadaMargem3Esq) {
                            // Se margem 1 casada pela esquerda com a 3, e o contrato incide na margem 3
                            // então libera da margem 1 o mesmo valor liberado da margem 3
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            final BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada(margemUsada1.subtract(adeVlr));
                            registroServidor.setRseMargemRest(margem1.subtract(margemUsada1.subtract(adeVlr)));

                        } else if (margem123CasadasEsq) {
                            // Se margem 1 casada pela esquerda com a 2 e 3, e o contrato incide na margem 3
                            // então libera da margem 1 e 2 o mesmo valor liberado da margem 3
                            final BigDecimal margem1 = (registroServidor.getRseMargem() != null) ? registroServidor.getRseMargem() : new BigDecimal("0.00");
                            final BigDecimal margemUsada1 = (registroServidor.getRseMargemUsada() != null) ? registroServidor.getRseMargemUsada() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada(margemUsada1.subtract(adeVlr));
                            registroServidor.setRseMargemRest(margem1.subtract(margemUsada1.subtract(adeVlr)));

                            final BigDecimal margem2 = (registroServidor.getRseMargem2() != null) ? registroServidor.getRseMargem2() : new BigDecimal("0.00");
                            final BigDecimal margemUsada2 = (registroServidor.getRseMargemUsada2() != null) ? registroServidor.getRseMargemUsada2() : new BigDecimal("0.00");
                            registroServidor.setRseMargemUsada2(margemUsada2.subtract(adeVlr));
                            registroServidor.setRseMargemRest2(margem2.subtract(margemUsada2.subtract(adeVlr)));

                        } else if (margem1CasadaMargem3Lateral) {
                            // Se margem 1 casada pela com a 3 lateralmente, e a reserva incide na margem 3
                            // então subtrai da margem 3 o valor negativo da margem 1 restante
                            final BigDecimal margemRest1 = (registroServidor.getRseMargemRest() != null) ? registroServidor.getRseMargemRest() : new BigDecimal("0.00");
                            // RSE_MARGEM_REST_3 = RSE_MARGEM_3 - RSE_MARGEM_USADA_3 + MIN(0, RSE_MARGEM_REST)
                            registroServidor.setRseMargemRest3(margem.subtract(margemUsada).add(margemRest1.min(BigDecimal.ZERO)));
                        }

                        LOG.debug("MARGEM REST DEPOIS: " + registroServidor.getRseMargemRest3());

                    } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        // Controle de margem pela tabela tb_margem_registro_servidor
                        final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, registroServidor.getRseCodigo());
                        MargemRegistroServidor mrsBean = null;

                        try {
                            mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);
                        } catch (final FindException ex) {
                            // O registro servidor não possui a margem extra relativa ao contrato.
                            // Verifica a situação do servidor, caso seja excluído e a operação de liquidação, ignora a atualização.
                            final List<String> funLiquidacao = new ArrayList<>();
                            funLiquidacao.add(CodedValues.FUN_LIQ_CONTRATO);
                            funLiquidacao.add(CodedValues.FUN_CANC_COMPRA);
                            funLiquidacao.add(CodedValues.FUN_CANC_CONSIGNACAO);
                            funLiquidacao.add(CodedValues.FUN_CANC_RENEGOCIACAO);
                            funLiquidacao.add(CodedValues.FUN_CANC_RESERVA);
                            funLiquidacao.add(CodedValues.FUN_CANC_SOLICITACAO);
                            funLiquidacao.add(CodedValues.FUN_CONF_LIQUIDACAO);
                            funLiquidacao.add(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA);

                            if (funLiquidacao.contains(responsavel.getFunCodigo()) &&
                                CodedValues.SRS_INATIVOS.contains(registroServidor.getStatusRegistroServidor().getSrsCodigo())) {
                                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.margem.nao.cadastrada", responsavel));
                            } else {
                                throw new AutorizacaoControllerException("mensagem.erro.margem.nao.cadastrada", responsavel, ex);
                            }
                        }

                        if (mrsBean != null) {
                            BigDecimal margemUsada = (mrsBean.getMrsMargemUsada() != null) ? mrsBean.getMrsMargemUsada() : new BigDecimal("0.00");
                            final BigDecimal margemRest = (mrsBean.getMrsMargemRest() != null) ? mrsBean.getMrsMargemRest() : new BigDecimal("0.00");

                            margemUsada = margemUsada.subtract(adeVlr);
                            mrsBean.setMrsMargemUsada(margemUsada);
                            mrsBean.setMrsMargemRest(mrsBean.getMrsMargem().subtract(margemUsada));

                            AbstractEntityHome.update(mrsBean);

                            // Atualiza os casamentos de margem extras
                            atualizaMargemExtraCasada(registroServidor.getRseCodigo(), adeIncMargem, adeVlr.negate(), margemRest, ocaCodigo, adeCodigo, responsavel);

                            // Verifica se a situação atual do servidor foi alterada pela rotina de segurança ao atualizar a margem extra casada
                            final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(registroServidor.getRseCodigo());
                            if (!rse.getSrsCodigo().equalsIgnoreCase(registroServidor.getSrsCodigo())) {
                                // Recupera a nova situação do servidor
                                registroServidor.setSrsCodigo(rse.getSrsCodigo());
                            }
                        }
                    }
                }

                AbstractEntityHome.update(registroServidor);

                // Finaliza o histórico de margem. Grava os valores
                // atuais de margem restante associado a ocorrencia
                gravarHistoricoMargem(registroServidor.getRseCodigo(), ocaCodigo,
                        margemRest1Antes, registroServidor.getRseMargemRest(),
                        margemRest2Antes, registroServidor.getRseMargemRest2(),
                        margemRest3Antes, registroServidor.getRseMargemRest3(),
                        adeCodigo,
                        responsavel);
            }

            // Recalcula a pontuação do usuário, se necessário
            pontuacaoServidorController.calcularPontuacao(registroServidor.getRseCodigo(), responsavel);

            // Retorna o código da ocorrencia de alteração de status
            return ocaCodigo;

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public String criaOcorrenciaADEValidando(String adeCodigo, String tocCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            return criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, null, null, null, null, null, responsavel);
        } else {
            throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.modificar.esta.consignacao", responsavel);
        }
    }

    @Override
    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, null, null, null, null, null, responsavel);
    }

    @Override
    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, null, null, null, ocaPeriodo, null, responsavel);
    }

    protected String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, BigDecimal ocaAdeVlrAnt, BigDecimal ocaAdeVlrNovo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, ocaAdeVlrAnt, ocaAdeVlrNovo, null, null, null, responsavel);
    }

    protected String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, null, null, null, null, tmoCodigo, responsavel);
    }

    @Override
    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, BigDecimal ocaAdeVlrAnt, BigDecimal ocaAdeVlrNovo, Date ocaData, Date ocaPeriodo, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (ocaPeriodo == null) {
                final String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
                if (CodedValues.TOC_RELANCAMENTO.equals(tocCodigo)) {
                    ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtualInclusao(orgCodigo, null, responsavel);
                } else {
                    ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                }
            }

            responsavel = (responsavel == null ? AcessoSistema.getAcessoUsuarioSistema() : responsavel);
            return OcorrenciaAutorizacaoHome.create(adeCodigo, tocCodigo, responsavel.getUsuCodigo(), ocaObs, ocaAdeVlrAnt, ocaAdeVlrNovo, responsavel.getIpUsuario(), ocaData, ocaPeriodo, tmoCodigo).getOcaCodigo();
        } catch (CreateException|FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final PeriodoException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void removeOcorrenciaADE(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        removeOcorrenciaADE(adeCodigo, tocCodigo, true, false, false, responsavel);
    }

    protected void removeOcorrenciaADE(String adeCodigo, String tocCodigo, boolean todas, boolean primeira, boolean ultima, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            Collection<OcorrenciaAutorizacao> ocorrencias = null;
            if (todas || primeira) {
                ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, tocCodigo);
            } else if (ultima) {
                ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, tocCodigo);
            }
            if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                for (final OcorrenciaAutorizacao ocaBean : ocorrencias) {
                    AbstractEntityHome.remove(ocaBean);
                    if (!todas) {
                        return;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private static final Pattern regex_pattern = Pattern.compile("[0-9]+");

    /**
     * Verfica se o indice passado pode ser usado. Encontra o próximo disponível se não for passado nenhum indice.
     * @param adeCodigo : código da ADE cujo indice será comparado com o passado
     * @param rseCodigo : código do servidor - a restrição de indice é para uma matricula/verba
     * @param cnvCodigo : código do convênio do novo contrato
     * @param adeIndice : código do convênio - a restrição de indice é para uma matricula/verba
     * @param adeCodReg : tipo de registro (Desconto ou Crédito)
     * @param adeCodigosRenegociacao : códigos dos contratos que estão sendo renegociados e não devem ser considerados na pesquisa
     * @param ignoraTpcAdeQQStatus : TRUE para ignorar a configuração do parâmetro de sistema TPC_INDICE_AUTOMATICO_SEQUENCIAL_TODAS_ADES
     * @param responsavel : Responsável pela operação
     * @return o indice disponível para uso.
     * @throws AutorizacaoControllerException
     */
    @Override
    public String verificaAdeIndice(String adeCodigo, String rseCodigo, String cnvCodigo,
            String adeIndice, String adeCodReg, List<String> adeCodigosRenegociacao, boolean ignoraTpcAdeQQStatus, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Quando o indice está configurado para ser único por registro servidor, ele deve sobrepor as configurações de repetição de indice
            final boolean indiceUnicoRegistroSer = ParamSist.getBoolParamSist(CodedValues.TPC_INDICE_UNICO_REGISTRO_SER_INDEPENDENTE_CONVENIO, responsavel);

            // Limite numérico do indice
            final int limiteIndice = ((ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel))) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;

            // Verifica se o parametro de indice repetido é igual a sim
            final boolean repeteIndice = ParamSist.paramEquals(CodedValues.TPC_INDICE_REPETIDO, CodedValues.TPC_SIM, responsavel) && !indiceUnicoRegistroSer;

            // Busca o parametro de indice padrão
            String indiceDefault = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel).toString() : "";

            // Verifica se o índice é numérico
            final boolean indiceNumerico = ParamSist.paramEquals(CodedValues.TPC_INDICE_NUMERICO, CodedValues.TPC_SIM, responsavel);

            // Verifica se o índice numérico zero não é aceito
            final boolean bloqueiaIndiceZero = indiceNumerico && ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_INDICE_ZERO, CodedValues.TPC_SIM, responsavel);

            // Verifica se será considerado o valor minímo do indice para ser gerado automaticamente.
            final String indiceMinimoAutomatico = (indiceNumerico && ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel) && (ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MIN_INDICE, responsavel) != null)) ? ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MIN_INDICE, responsavel).toString() : "1";

            // Verifica se o índice numérico sequencial deve considerar consignações de todos os status
            final boolean consideraAdeQQStatus = !ignoraTpcAdeQQStatus && ParamSist.paramEquals(CodedValues.TPC_INDICE_AUTOMATICO_SEQUENCIAL_TODAS_ADES, CodedValues.TPC_SIM, responsavel);

            if ((consideraAdeQQStatus || indiceUnicoRegistroSer) && !indiceNumerico) {
                throw new AutorizacaoControllerException("mensagem.erro.indice.nao.numerico.configuracao.incorreta", responsavel);
            }

            // Busca o convênio da nova consignação
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(cnvCodigo);
            final String svcCodigo = cnvBean.getServico().getSvcCodigo();
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();

            // Busca o indice default caso o indice seja nulo ou vazio,
            boolean repetirIndiceCsa = !indiceUnicoRegistroSer;
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_INDICE);
            tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);

            final List<TransferObject> paramCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject vo : paramCsa) {
                if (CodedValues.TPS_INDICE.equals(vo.getAttribute(Columns.TPS_CODIGO)) && ((vo.getAttribute(Columns.PSC_VLR) != null) && !"".equals(vo.getAttribute(Columns.PSC_VLR)))) {
                    indiceDefault = vo.getAttribute(Columns.PSC_VLR).toString();
                }

                if (CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA.equals(vo.getAttribute(Columns.TPS_CODIGO)) && ((vo.getAttribute(Columns.PSC_VLR) != null) && CodedValues.TPC_NAO.equals(vo.getAttribute(Columns.PSC_VLR)))) {
                    repetirIndiceCsa = false;
                }
            }

            // Situações de contratos que não podem haver índices duplicados
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
            sadCodigos.add(CodedValues.SAD_EMCARENCIA);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_MARGEM);

            // Monta o list com os índices que já existem no sistema para o servidor, verificando também
            // contratos em outros serviços, de acordo com o relacionamento TNT_UNICIDADE_CAD_INDICE
            // Se "consideraAdeQQStatus", então faz a busca sem filtro de status ou das consignações
            // a serem renegociadas, para que o índice destas, seja considerado no cálculo do maior indice.
            final PesquisaAdeLstIndiceQuery searchIndices = new PesquisaAdeLstIndiceQuery();
            searchIndices.rseCodigo = rseCodigo;
            searchIndices.cnvCodigo = cnvCodigo;
            searchIndices.sadCodigos = (consideraAdeQQStatus ? null : sadCodigos);
            searchIndices.adeCodigosRenegociacao = (consideraAdeQQStatus ? null : adeCodigosRenegociacao);
            final List<TransferObject> autdes = searchIndices.executarDTO();

            boolean crescente = true;
            if ("gerar_maior".equalsIgnoreCase(adeIndice)) {
                adeIndice = null;
                crescente = false;
            }

            boolean gera = false;
            // Se existe índice default seta o adeIndice, vazio ou nulo, com este valor, caso o servidor
            // ainda não tenha consignação para a consignatária
            if (((adeIndice == null) || "".equals(adeIndice)) &&
                    ((indiceDefault != null) && !"".equals(indiceDefault)) && (autdes.size() == 0)) {
                adeIndice = indiceDefault;
            } else if ((adeIndice == null) || "".equals(adeIndice)) {
                // Se o parametro de índice padrão estiver null ou vazio, gera um novo índice
                gera = true;
            }
            if (gera) {
                if (autdes.size() == 0) {
                    // Se não existir nenhuma autorização para o servidor seta o índice para o primeiro valor
                    adeIndice = indiceMinimoAutomatico;
                } else if (autdes.size() > limiteIndice) {
                    if (consideraAdeQQStatus) {
                        // Caso a quantidade de consignações do servidor esteja acima do limite máximo numérico do índice (parâmetro de sistema 179),
                        // utilizar então o primeiro índice disponível considerando apenas consignações não encerradas, como se o TPC_INDICE_AUTOMATICO_SEQUENCIAL_TODAS_ADES fosse = N.
                        return verificaAdeIndice(adeCodigo, rseCodigo, cnvCodigo, adeIndice, adeCodReg, adeCodigosRenegociacao, true, responsavel);
                    }

                    // Servidor já possui o limite de contratos para este convênio
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.contrato.servidor.ja.possui.contratos.neste.convenio", responsavel, String.valueOf(limiteIndice + 1));
                } else {
                    // Se existir autorização para o servidor seta o índice para o próximo valor
                    // Cria comparador para a ordenação das parcelas
                    final Comparator<TransferObject> c = (t1, t2) -> {
                        final Pattern regex = regex_pattern;
                        final int limite_indice = ((ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel))) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;
                        String vlr1 = StringUtils.repeat("0", String.valueOf(limite_indice).length());
                        String vlr2 = StringUtils.repeat("0", String.valueOf(limite_indice).length());
                        // desconsidera índice nulo e alfanumérico
                        if (((t1.getAttribute(Columns.ADE_INDICE)) != null)
                                && regex.matcher((String) t1.getAttribute(Columns.ADE_INDICE)).matches()) {
                            vlr1 = t1.getAttribute(Columns.ADE_INDICE).toString();
                            if (vlr1.length() < String.valueOf(limite_indice).length()) {
                                vlr1 = StringUtils.repeat("0", String.valueOf(limite_indice).length() - vlr1.length()) + vlr1;
                            }
                        }
                        // desconsidera índice nulo e alfanumérico
                        if ((t2.getAttribute(Columns.ADE_INDICE) != null)
                                && regex.matcher((String) t2.getAttribute(Columns.ADE_INDICE)).matches()) {
                            vlr2 = t2.getAttribute(Columns.ADE_INDICE).toString();
                            if (vlr2.length() < String.valueOf(limite_indice).length()) {
                                vlr2 = StringUtils.repeat("0", String.valueOf(limite_indice).length() - vlr2.length()) + vlr2;
                            }
                        }
                        return vlr2.compareTo(vlr1);
                    };

                    // Ordena o list pelo índice em ordem decrescente
                    Collections.sort(autdes, c);

                    Iterator<TransferObject> ite = autdes.iterator();
                    CustomTransferObject ade = new CustomTransferObject();
                    if ((adeCodigo != null) && !"".equals(adeCodigo)) {
                        while (ite.hasNext()) {
                            ade = (CustomTransferObject) ite.next();
                            // Se o contrato já possui um índice mantêm este índice para este contrato
                            if (adeCodigo.equals(ade.getAttribute(Columns.ADE_CODIGO).toString())) {
                                adeIndice = (ade.getAttribute(Columns.ADE_INDICE) != null) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
                            }
                        }
                    }

                    // Procura o próximo valor para o índice
                    int indice = 0;
                    int adeInd = limiteIndice;
                    if (crescente) {
                        final String primeiroAdeIndice = (String) ((CustomTransferObject) autdes.get(0)).getAttribute(Columns.ADE_INDICE);
                        // O índice pode ser alfanumérico se todos os indíces são alfanuméricos
                        final String regex = "[0-9]+";
                        adeInd = Integer.parseInt(!TextHelper.isNull(primeiroAdeIndice) && primeiroAdeIndice.matches(regex) && (Integer.parseInt(primeiroAdeIndice) >= Integer.parseInt(indiceMinimoAutomatico)) ? primeiroAdeIndice : "0") + 1;
                        adeInd = adeInd < Integer.parseInt(indiceMinimoAutomatico) ? Integer.parseInt(indiceMinimoAutomatico) : adeInd;
                        if (adeInd > limiteIndice) {
                            adeInd = (bloqueiaIndiceZero ? 1 : 0);
                            // a Lista de ADE´s está ordenada do maior índice para o menor.
                            for (int a = autdes.size() - 1; a >= 0; a--) {
                                ade = (CustomTransferObject) autdes.get(a);
                                indice = Integer.parseInt(ade.getAttribute(Columns.ADE_INDICE) != null ? ade.getAttribute(Columns.ADE_INDICE).toString() : StringUtils.repeat("0", String.valueOf(limiteIndice).length()));
                                if (bloqueiaIndiceZero && (indice == 0)) {
                                    continue;
                                } else if (indice == adeInd) {
                                    adeInd++;
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {
                        ite = autdes.iterator();
                        while (ite.hasNext() && (indice != adeInd)) {
                            ade = (CustomTransferObject) ite.next();
                            indice = Integer.parseInt(ade.getAttribute(Columns.ADE_INDICE) != null ? ade.getAttribute(Columns.ADE_INDICE).toString() : StringUtils.repeat("0", String.valueOf(limiteIndice).length()));
                            if (indice == adeInd) {
                                adeInd--;
                            }
                        }
                    }
                    adeIndice = String.valueOf(adeInd);
                }
                // Se o servidor não possui contratos na base com indice
                if ((adeIndice == null) || "".equals(adeIndice)) {
                    adeIndice = StringUtils.repeat("0", String.valueOf(limiteIndice).length());
                }

            } else {
                // Formata índice de acordo com a quantidade de caracteres do parâmetro de limite de índice
                if ((adeIndice != null) && (adeIndice.length() < String.valueOf(limiteIndice).length())) {
                    adeIndice = StringUtils.repeat("0", String.valueOf(limiteIndice).length() - adeIndice.length()) + adeIndice;
                }

                for (final TransferObject ade : autdes) {
                    final String adeIndiceExistente = (String) ade.getAttribute(Columns.ADE_INDICE);

                    adeCodReg = ((adeCodReg != null) && !"".equals(adeCodReg)) ? adeCodReg : CodedValues.COD_REG_DESCONTO;
                    final String sadCodigo = (String) ade.getAttribute(Columns.ADE_SAD_CODIGO);

                    if (((!repeteIndice || !repetirIndiceCsa) && (adeIndiceExistente != null) &&
                            adeIndiceExistente.equals(adeIndice)) &&
                            ((adeCodigo == null) || !adeCodigo.equals(ade.getAttribute(Columns.ADE_CODIGO).toString()))) {
                        // Servidor já possui uma reserva para este índice
                        if (CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) {
                            throw new AutorizacaoControllerException("mensagem.erro.servidor.ja.possui.contrato.em.carencia.para.este.indice", responsavel);
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.servidor.ja.possui.contrato.para.este.indice", responsavel);
                        }
                    }
                }
            }
            // Formata o índice de acordo com a quantidade de caracteres do parâmetro de limite de índice
            if ((adeIndice != null) && (adeIndice.length() < String.valueOf(limiteIndice).length())) {
                adeIndice = StringUtils.repeat("0", String.valueOf(limiteIndice).length() - adeIndice.length()) + adeIndice;
            } else if ((adeIndice != null) && (adeIndice.length() > String.valueOf(limiteIndice).length())) {
                // Retorna erro se a quantidade de caracteres do índice informado for maior que a quantidade de caracteres do parâmetro de limite de índice
                throw new AutorizacaoControllerException("mensagem.erro.valor.invalido.para.indice.informado", responsavel);
            }

            if (indiceNumerico) {
                try {
                    final int valorNumericoIndice = Integer.parseInt(adeIndice);
                    if (bloqueiaIndiceZero && (valorNumericoIndice == 0)) {
                        throw new AutorizacaoControllerException("mensagem.erro.indice.deve.ser.maior.zero", responsavel);
                    }
                } catch (final NumberFormatException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.invalido.para.indice.informado", responsavel);
                }
            }

            return adeIndice;
        } catch (final Exception ex) {
            LOG.error("Exception[" + ex.getClass() + "]: " + ex.getMessage());

            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Consome a senha de autorização do servidor.
     * São quatro os requisitos para o consumo da senha de autorização do serivodor:
     * 1. A operação deve exigir o consumo.
     * 2. A operação deve exigir a utilização de senha de servidor (para reserva ou confirmação).
     * 3. O sistema deve estar configurado para trabalhar com senha de autorização.
     * 4. O sistema deve estar configurado para consumir a senha de autorização ao deferir ou incluir contratos.
     * @param adeCodigo : Código do contrato que está sofrendo a operação que provocou o consumo da senha.
     * @param sadCodigo : Status do contrato que está sofrendo a operação que provocou o consumo da senha.
     * @param rseCodigo : Código do registro servidor
     * @param svcCodigo : Código do serviço ligado ao contrato
     * @param csaCodigo : Código da consignatária ligada ao contrato
     * @param senhaUtilizada : Senha utilizada na operação
     * @param exigeSenhaCadastrada : Indica se um erro deve ser gerado caso não haja senha cadastrada.
     * @param inclusao : Indica se é operação de inclusão
     * @param confSolicitacao : Indica se é operação de conf/deferimento de solicitação
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void consumirSenhaDeAutorizacao(String adeCodigo, String sadCodigo, String rseCodigo, String svcCodigo, String csaCodigo, String senhaUtilizada, boolean exigeSenhaCadastrada, boolean inclusao, boolean confSolicitacao, boolean consultaMargem, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final boolean usaSenhaAutorizacaoSer = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean usaSenhaAutorizacaoTodasOpe = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, CodedValues.TPC_SIM, responsavel);

            // Verifica se existe utiliza senha de consulta na reserva de margem caso o sistema utilize senha de autorização mas não utilize para todas as operações
            boolean usaSenhaConsultaReservaMargem = false;
            if (!TextHelper.isNull(svcCodigo) && usaSenhaAutorizacaoSer && !usaSenhaAutorizacaoTodasOpe) {
                try {
                    final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                    usaSenhaConsultaReservaMargem = ((pse.getPseVlr() != null) && "1".equals(pse.getPseVlr().trim()));
                } catch (final FindException ex) {
                    // Não existe o parâmetro para serviço
                }
            }

            // Só consome senha se utiliza senha de autorização e se não é senha de consulta
            if (usaSenhaAutorizacaoSer && !usaSenhaConsultaReservaMargem) {
                // Só consome senha se é obrigatória para a reserva
                final boolean senhaObrigatoriaReserva = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);
                // ou para confirmação de solicitação
                final boolean senhaObrigatoriaConfSolic = parametroController.senhaServidorObrigatoriaConfSolicitacao(svcCodigo, responsavel);
                // ou para a consulta de margem
                final boolean senhaObrigatoriaConsMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);

                // Parâmetros de sistema sobre consumo da senha
                final boolean consomeSenhaDeferimento = ParamSist.paramEquals(CodedValues.TPC_DEFERIMENTO_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel);
                final boolean consomeSenhaInclusao = ParamSist.paramEquals(CodedValues.TPC_INCLUSAO_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel);
                final boolean consomeSenhaConsultaMargem = ParamSist.paramEquals(CodedValues.TPC_CONSULTA_MARGEM_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel);
                final boolean usaMultiplasSenhasAut = ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
                final boolean omitirCampoSenhaOpcional = !senhaObrigatoriaReserva && ParamSist.paramEquals(CodedValues.TPC_OMITIR_CAMPO_SENHA_OPCIONAL, CodedValues.TPC_SIM, responsavel);

                // Atualmente o sistema só suporta múltiplas senhas de autorização com o consumo na inclusão.
                if (usaMultiplasSenhasAut && !consomeSenhaInclusao) {
                    throw new AutorizacaoControllerException("mensagem.erro.multiplas.senhas.autorizacao.apenas.com.consumo.na.inclusao", responsavel);
                }
                // Atualmente o sistema só suporta consumo de senha na consulta de margem com senha múltipla
                if (consomeSenhaConsultaMargem && (!usaMultiplasSenhasAut || !usaSenhaAutorizacaoTodasOpe)) {
                    throw new AutorizacaoControllerException("mensagem.erro.multiplas.senhas.autorizacao.desabilitado.com.consumo.na.consulta.margem", responsavel);
                }

                // Determina se a senha deve ser consumida
                boolean consumirSenha = false;

                if (!inclusao && !confSolicitacao && senhaObrigatoriaReserva && consomeSenhaDeferimento && !consomeSenhaInclusao) {
                    // Deferimento de Aguard. Conf e Aguard. Deferimento: a senha deve ser obrigatória para a reserva
                    // e a senha deve ser consumida somente no deferimento, pois se for consumida na inclusão,
                    // já terá sido consumida.
                    consumirSenha = true;
                } else if (!inclusao && confSolicitacao && senhaObrigatoriaConfSolic && (consomeSenhaDeferimento || consomeSenhaInclusao)) {
                    // Confirmação/Deferimento de Solicitação: a senha deve ser obrigatória para a confirmação de solicitação
                    // e a senha deve ser consumida tanto no deferimento quanto na inclusão. Na inclusão de uma solicitação a
                    // senha não é exigida, então será consumida no deferimento.
                    consumirSenha = true;
                } else if (inclusao && !confSolicitacao && senhaObrigatoriaReserva && (consomeSenhaDeferimento || consomeSenhaInclusao)) {
                    // Inclusão de contratos, independente da situação: a senha deve ser obrigatória para a reserva
                    // e a senha deve ser consumida tanto no deferimento quanto na inclusão.
                    consumirSenha = true;
                } else if (consultaMargem && senhaObrigatoriaConsMargem && usaSenhaAutorizacaoTodasOpe && consomeSenhaConsultaMargem) {
                    // Consulta de margem que exige senha, em sistema que usa a senha de autorização em todas as operações
                    // e consumo da senha obrigatório na consulta de margem
                    consumirSenha = true;
                } else if (!responsavel.isSer() && geraSenhaAutOtp && usaSenhaAutorizacaoTodasOpe && consomeSenhaConsultaMargem && !omitirCampoSenhaOpcional) {
                    // Se gerar senha autorizacao otp, consome senha
                    consumirSenha = true;
                }

                if (consumirSenha) {
                    // Recupera o usuário servidor a partir do código do registro servidor
                    final TransferObject usuarioServidor = usuarioController.getSenhaServidor(rseCodigo, responsavel);
                    if (usuarioServidor == null) {
                        throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
                    }

                    // Recupera os dados da senha de autorização: senha, qtd de operações e data de expiração
                    final String usuCodigo = (String) usuarioServidor.getAttribute(Columns.USU_CODIGO);
                    String senha = null;
                    Short qtdOperacoes = null;
                    Date dataExpiracaoSenha = null;

                    // Se utiliza múltiplas senhas de autorização, recupera o registro de senha
                    if (usaMultiplasSenhasAut) {
                        if (TextHelper.isNull(senhaUtilizada)) {
                            LOG.error("Tentativa de consumo de senha de autorização múltipla porém a senha utilizada não foi informada.");
                            throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
                        }

                        final TransferObject senhaAutorizacaoServidor = usuarioController.obtemSenhaAutorizacaoServidor(usuCodigo, senhaUtilizada, responsavel);
                        if (senhaAutorizacaoServidor != null) {
                            senha = (String) senhaAutorizacaoServidor.getAttribute(Columns.SAS_SENHA);
                            qtdOperacoes = (Short) senhaAutorizacaoServidor.getAttribute(Columns.SAS_QTD_OPERACOES);
                            dataExpiracaoSenha = (Date) senhaAutorizacaoServidor.getAttribute(Columns.SAS_DATA_EXPIRACAO);
                        }
                    } else {
                        senha = (String) usuarioServidor.getAttribute(Columns.USU_SENHA_2);
                        qtdOperacoes = (usuarioServidor.getAttribute(Columns.USU_OPERACOES_SENHA_2) != null ? (Short) usuarioServidor.getAttribute(Columns.USU_OPERACOES_SENHA_2) : 0);
                        dataExpiracaoSenha = (usuarioServidor.getAttribute(Columns.USU_DATA_EXP_SENHA_2) != null ? (Date) usuarioServidor.getAttribute(Columns.USU_DATA_EXP_SENHA_2) : null);
                    }

                    // Verifica se a senha existe e é obrigatória
                    final boolean senhaCadastrada = ((senha != null) && !CodedValues.USU_SENHA_SERVIDOR_CANCELADA.equalsIgnoreCase(senha));
                    if (!senhaCadastrada && exigeSenhaCadastrada) {
                        throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
                    }

                    // Só consome senhas que existam.
                    if (senhaCadastrada) {
                        // Verifica quantas operações ainda são permitidas para a senha
                        if (qtdOperacoes <= 0) {
                            // Se igual a Zero, nenhuma nova operação é permitida
                            throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
                        } else if ((qtdOperacoes == 1) && !consomeSenhaInclusao) {
                            // Caso seja a última operação, verifica quantas compras abertas o servidor
                            // possui, onde o novo contrato não seja este que gerou o evento de consumo
                            // da senha. Caso exista alguma compra, então não permite a nova operação,
                            // pois a senha presente deve ser desta operação de compra.
                            // SE CONSOME NA INCLUSÃO: não é necessário verificar pois no ato da compra
                            // a operação já terá consumido uma senha de autorização.
                            final ContaComprasAbertasServidorQuery query = new ContaComprasAbertasServidorQuery();
                            query.rseCodigo = rseCodigo;
                            query.adeCodigo = adeCodigo;

                            // Se há alguma compra aberta, impede o consumo da senha.
                            if (query.executarContador() > 0) {
                                throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.erro.uso.invalido.compra", responsavel);
                            }
                        }

                        // Se consome senha na inclusão, ou na reserva o contrato já nasce deferido,
                        // ou é um deferimento de reserva, ou consulta de margem então prossegue com o consumo da senha
                        if (consomeSenhaInclusao || consomeSenhaConsultaMargem || CodedValues.SAD_DEFERIDA.equals(sadCodigo)) {
                            // Decrementa a quantidade de operações permitidas, e caso o valor resultante
                            // seja maior que zero, então não realiza o consumo da senha.
                            qtdOperacoes--;
                            if (qtdOperacoes > 0) {
                                // Não consome a senha, apenas atualiza o total de operações restantes
                                usuarioController.alterarOperacoesSenhaAutorizacao(usuCodigo, qtdOperacoes, senhaUtilizada, responsavel);
                                return;
                            }

                            // Se a senha não pode estar expirada.
                            if (ParamSist.paramEquals(CodedValues.TPC_SENHA_EXP_SERVIDOR_RESERVA_MARGEM, CodedValues.TPC_NAO, responsavel) && ((dataExpiracaoSenha != null) && (dataExpiracaoSenha.compareTo(DateHelper.getSystemDate()) < 0) && exigeSenhaCadastrada)) {
                                throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.expirada", responsavel);
                            }

                            usuarioController.consomeSenhaAutorizacao(usuCodigo, senha, responsavel);
                        }
                    } else {
                        LOG.warn("Tentativa de consumo de senha de autorização do servidor sem senha cadastrada.");
                    }
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.consumo.senha.autorizacao.servidor", responsavel);
        }
    }

    @Override
    public TabelaPrice calcularTabelaPrice(CustomTransferObject autdes, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final BigDecimal valorLiquidoLiberado = !TextHelper.isNull(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO)) ? new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString()) : null;
            final BigDecimal prestacao = !TextHelper.isNull(autdes.getAttribute(Columns.ADE_VLR)) ? (BigDecimal) autdes.getAttribute(Columns.ADE_VLR) : null;
            final Integer qtdePrestacoes = !TextHelper.isNull(autdes.getAttribute(Columns.ADE_PRAZO)) ? (Integer) autdes.getAttribute(Columns.ADE_PRAZO) : 0;
            final BigDecimal taxaJuros = !TextHelper.isNull(autdes.getAttribute(Columns.CFT_VLR)) ? new BigDecimal(autdes.getAttribute(Columns.CFT_VLR).toString()) : null;
            final String orgCodigo = (String) autdes.getAttribute(Columns.ORG_CODIGO);

            if (TextHelper.isNull(valorLiquidoLiberado)) {
                throw new AutorizacaoControllerException("mensagem.informe.valor.liquido.liberado", responsavel);
            }

            if (TextHelper.isNull(prestacao)) {
                throw new AutorizacaoControllerException("mensagem.valorParcelaInvalido", responsavel);
            }

            if (TextHelper.isNull(qtdePrestacoes)) {
                throw new AutorizacaoControllerException("mensagem.informe.prd.total", responsavel);
            }

            if (TextHelper.isNull(taxaJuros)) {
                throw new AutorizacaoControllerException("mensagem.informe.ade.valor.taxa.juros", responsavel);
            }

            return PriceHelper.calcula(valorLiquidoLiberado, prestacao, qtdePrestacoes, taxaJuros, orgCodigo, responsavel);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Collection<OcorrenciaAutorizacao> findOcorrenciaByAdeTocUsuCodigo(String adeCodigo, String tocCodigo, String usuCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        Collection<OcorrenciaAutorizacao> retorno = null;
        try {
            retorno = OcorrenciaAutorizacaoHome.findByAdeTocUsuCodigo(adeCodigo, tocCodigo, usuCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return retorno;
    }

    @Override
    public Collection<OcorrenciaAutorizacao> findByAdeTocCodigoOcaPeriodo(String adeCodigo, String tocCodigo, java.sql.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        Collection<OcorrenciaAutorizacao> retorno = null;
        try {
            retorno = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOcaPeriodo(adeCodigo, tocCodigo, ocaPeriodo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return retorno;
    }

    @Override
    public Collection<OcorrenciaAutorizacao> findOcorrenciaByAdeTocCsaCodigo(String adeCodigo, String tocCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        Collection<OcorrenciaAutorizacao> retorno = null;
        try {
            retorno = OcorrenciaAutorizacaoHome.findByAdeTocCsaCodigo(adeCodigo, tocCodigo, csaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return retorno;
    }

    @Override
    public List<TransferObject> obtemConsignacaoPorCnvSerQuery(String cnvCodigo, String rseCodigo, List<String> sadCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemConsignacaoPorCnvSerQuery adesPorCnvSer = new ObtemConsignacaoPorCnvSerQuery();
            adesPorCnvSer.cnvCodigo = cnvCodigo;
            adesPorCnvSer.rseCodigo = rseCodigo;
            adesPorCnvSer.sadCodigos = sadCodigos;
            adesPorCnvSer.criterio = criterio;
            return adesPorCnvSer.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Valida se contrato alterado está na(s) última(s) parcela(s), levando-se em conta se período corrente é agrupado com outros. Se estiver na condição citada, a alteração é barrada.
     * @param responsavel
     * @param orgCodigo
     * @param adePrazoOld
     * @param adePrdPagas
     * @param adePrazoNew
     * @param qtdParcelasEmProcessamento
     * @throws AutorizacaoControllerException
     * @throws PeriodoException
     */
    protected void validaAlteracaoUltimasParcelas(String orgCodigo, Integer adePrazoOld, int adePrdPagas, Integer adePrazoNew, int qtdParcelasEmProcessamento, AcessoSistema responsavel) throws AutorizacaoControllerException, PeriodoException {
        String paramBloqAltUltPrz = (String) ParamSist.getInstance().getParam(CodedValues.TPC_BLOQUEIA_ALTERACAO_ADE_ULTIMA_PARCELA, responsavel);
        paramBloqAltUltPrz = TextHelper.isNull(paramBloqAltUltPrz) ? "" : paramBloqAltUltPrz;

        switch (paramBloqAltUltPrz){
            case CodedValues.TPC_SIM :
                // Se o prazo atual for igual a quantidade já paga somado da quantidade de parcelas em processamento,
                // então a última parcela já está na folha, e possível alteração pode não ser efetivada.
                // Verifica parâmetro que bloqueia a alteração independente do prazo e, em caso positivo, bloqueia a
                // alteração e envia mensagem de erro ao usuário.
                if (((adePrazoOld != null) && (adePrazoOld.intValue() == (adePrdPagas + qtdParcelasEmProcessamento)))) {
                    throw new AutorizacaoControllerException("mensagem.ultimaParcelaEmProcessamento", responsavel);
                }
                break;
            case CodedValues.TPC_TODOS :
                final List<java.util.Date> periodosAtuais = periodoController.obtemPeriodoAgrupado(orgCodigo, null, responsavel);
                final int qtdPeriodosAgrupadosCorrente = ((periodosAtuais != null) && !periodosAtuais.isEmpty()) ? periodosAtuais.size() : 0;

                //DESENV-9786: para este valor do parâmetro 527 verfica se as parcelas restantes estarão compreendidas nos períodos agrupados correntes,
                //             independente se estão em processamento ou não.
                if (((adePrazoOld != null) && ((adePrazoOld.intValue() - (adePrdPagas + qtdParcelasEmProcessamento)) <= qtdPeriodosAgrupadosCorrente))) {
                    throw new AutorizacaoControllerException(qtdPeriodosAgrupadosCorrente == 1 ? "mensagem.ultimaParcelaEmProcessamento" : "mensagem.ultimasParcelasEmProcessamento", responsavel);
                }
                break;
            default :
                // Se o prazo atual for igual a quantidade já paga somado da quantidade de parcelas em processamento,
                // então a última parcela já está na folha, e possível alteração pode não ser efetivada.
                // Em qualquer caso, verifica se
                // o prazo não foi alterado e também bloqueia a alteração, pois não terá efeito.
                if (((adePrazoOld != null) && (adePrazoOld.intValue() == (adePrdPagas + qtdParcelasEmProcessamento))) && ((adePrazoNew != null) && adePrazoOld.equals(adePrazoNew))) {
                    throw new AutorizacaoControllerException("mensagem.ultimaParcelaEmProcessamento", responsavel);
                }
                break;
        }
    }

    @Override
    public boolean podeConfirmarRenegociacao(BigDecimal adeVlr, String svcCodigo, String csaCodigo, BigDecimal vlrTotalRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        boolean podeConfirmar = usuarioPodeConfirmarRenegociacao(responsavel);

        // DESENV-14162 regra item 6) para definição de que casos exigirá confirmação de renegociação
        try {
            if (responsavel.isCsaCor() && (!podeConfirmar || exigirDuplaConfirmacaoLiquidacao(responsavel))) {
                final List<TransferObject> tpsCsaRenegExigeConfirmacao = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, Arrays.asList(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO), false, responsavel);

                if ((tpsCsaRenegExigeConfirmacao != null) && !tpsCsaRenegExigeConfirmacao.isEmpty()) {
                    final TpsExigeConfirmacaoRenegociacaoValoresEnum tpsCsaRenegEnum = TpsExigeConfirmacaoRenegociacaoValoresEnum.recuperaTpsExigeConfirmacaoRenegociacao((String) tpsCsaRenegExigeConfirmacao.get(0).getAttribute(Columns.PSC_VLR));

                    if (((adeVlr.compareTo(vlrTotalRenegociacao) < 0) && tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MAIOR)) ||
                            ((adeVlr.compareTo(vlrTotalRenegociacao) > 0) && tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MENOR)) ||
                            (tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.NENHUMA))) {
                        podeConfirmar = true;
                    } else {
                        podeConfirmar = false;
                    }
                }

            }
        } catch (final ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException(e);
        }

        return podeConfirmar;
    }

    @Override
    public List<TransferObject> buscarStatusConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListarStatusConsignacaoPorServidorQuery query = new ListarStatusConsignacaoPorServidorQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> buscarNaturezaConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListarNaturezaConsignacaoPorServidorQuery query = new ListarNaturezaConsignacaoPorServidorQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Collection<OcorrenciaAutorizacao> findByAdeTocCodigo(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        Collection<OcorrenciaAutorizacao> retorno = null;
        try {
            retorno = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, tocCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return retorno;
    }

    protected boolean verificaPermitePortabilidadeServidorMargemNegativa (Integer prazoNovo, BigDecimal adeVlrNovo, BigDecimal adeVlrAnterior, List<String> adeCodigosRenegociacao, boolean compra, AcessoSistema responsavel) throws AutorizacaoControllerException {
        boolean permiteCompraSerMargemNegativa = false;
        Integer maxPrazo = 0;
        BigDecimal adeVlrSoma = BigDecimal.ZERO;
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, CodedValues.TPC_SIM, responsavel) && compra) {

                for (final String adeCodigo : adeCodigosRenegociacao) {
                    final CustomTransferObject autDesconto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    maxPrazo = Math.max((int) autDesconto.getAttribute(Columns.ADE_PRAZO), maxPrazo);

                    if(TextHelper.isNull(adeVlrAnterior)) {
                        adeVlrSoma = adeVlrSoma.add((BigDecimal) autDesconto.getAttribute(Columns.ADE_VLR));
                    }
                }

                final boolean valorPermitido = adeVlrNovo.compareTo(adeVlrSoma.compareTo(BigDecimal.ZERO) > 0 ? adeVlrSoma : adeVlrAnterior) < 0;

                if(valorPermitido && (prazoNovo < maxPrazo)) {
                    permiteCompraSerMargemNegativa = true;
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return permiteCompraSerMargemNegativa;
    }

    private boolean verificaCondicionaPortabilidadeRenegociacao(BigDecimal vlrDesconto, BigDecimal margemRest, BigDecimal margemRestOri, BigDecimal adeVlrNovo, BigDecimal adeVlrAnterior, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, ParametroControllerException {
        boolean condiciona = false;

        // DESENV-16211 - Mato Grosso do Sul - Restrições Para Portabilidade
        final String funcao = responsavel.getFunCodigo();
        if (!TextHelper.isNull(funcao)) {
            BigDecimal perMaxParc = null;

            if (CodedValues.FUN_COMP_CONTRATO.equals(funcao) || CodedValues.FUN_SOLICITAR_PORTABILIDADE.equals(funcao)) {
                final CustomTransferObject paramSvcCondicionaPortabilidade = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE, responsavel);
                perMaxParc = ((paramSvcCondicionaPortabilidade != null) && !TextHelper.isNull(paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR))) ? new BigDecimal((String) paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR)) : null;
                condiciona = perMaxParc != null;

            } else if (CodedValues.FUN_RENE_CONTRATO.equals(funcao)) {
                final CustomTransferObject paramSvcCondicionaRenegociacao = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO, responsavel);
                perMaxParc = ((paramSvcCondicionaRenegociacao != null) && !TextHelper.isNull(paramSvcCondicionaRenegociacao.getAttribute(Columns.PSE_VLR))) ? new BigDecimal((String) paramSvcCondicionaRenegociacao.getAttribute(Columns.PSE_VLR)) : null;
                condiciona = perMaxParc != null;
            }

            if (condiciona) {
                // Verifica valor máximo percentual da parcela
                final BigDecimal vlrMax = adeVlrAnterior.multiply(perMaxParc).divide(new BigDecimal(100));
                // Verifica se a nova parecela esta abaixo do valor maximo
                if (vlrDesconto.compareTo(vlrMax) > 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.novo.contrato.nao.pode.ser.maior.que.arg0.porcento.do.valor.atual", responsavel, NumberHelper.format(perMaxParc.doubleValue(), NumberHelper.getLang()));
                }

                final BigDecimal margemRestAtualizada = margemRest.add(adeVlrAnterior).subtract(adeVlrNovo);
                if (margemRestAtualizada.compareTo(margemRestOri) < 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.positiva.margem.servidor", responsavel);
                }
            }
        }

        return condiciona;
    }
    /** ======================================== FIM MÉTODOS UTILITÁRIOS ======================================== **/


    @Override
    public void registraNotificacoesCse(List<String> adesCodigosIncluir, List<String> adesCodigosRemover, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            for (final String ade : adesCodigosIncluir) {
                if ((ade != null) && !ade.isEmpty()) {
                    final AutDesconto consignacao = AutDescontoHome.findByPrimaryKey(ade);

                    if (TextHelper.isNull(consignacao.getAdeDataNotificacaoCse())) {
                        criaOcorrenciaADE(ade, CodedValues.TOC_CONSIGNACAO_NOTIFICADA_CSE, ApplicationResourcesHelper.getMessage("mensagem.registrar.notificacao.cse", responsavel), responsavel);
                        AutDescontoHome.updateNotificaCse(ade, true);
                    }
                }
            }
            for (final String ade : adesCodigosRemover) {
                if ((ade != null) && !ade.isEmpty()) {
                    removeOcorrenciaADE(ade, CodedValues.TOC_CONSIGNACAO_NOTIFICADA_CSE, responsavel);
                    AutDescontoHome.updateNotificaCse(ade, false);
                }
            }

        } catch (AutorizacaoControllerException | UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void registraValorLiberadoConsignacao(List<String> adesCodigosIncluir, List<String> adesCodigosRemover, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            for (final String ade : adesCodigosIncluir) {
                if ((ade != null) && !ade.isEmpty()) {
                    final AutDesconto consignacao = AutDescontoHome.findByPrimaryKey(ade);
                    if (TextHelper.isNull(consignacao.getAdeDataLiberacaoValor())) {
                        criaOcorrenciaADE(ade, CodedValues.TOC_DATA_VALOR_CONSIGNACAO_LIBERADO_SER, ApplicationResourcesHelper.getMessage("mensagem.registrar.valor.liberado.ocaObs", responsavel), responsavel);
                        AutDescontoHome.updateRegistraValorLiberadoConsignacao(ade, true);
                    }
                }
            }
            for (final String ade : adesCodigosRemover) {
                if ((ade != null) && !ade.isEmpty()) {
                    removeOcorrenciaADE(ade, CodedValues.TOC_DATA_VALOR_CONSIGNACAO_LIBERADO_SER, responsavel);
                    AutDescontoHome.updateRegistraValorLiberadoConsignacao(ade, false);
                }
            }

        } catch (AutorizacaoControllerException | UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean verificaContratoSuspensoPodeRenegociar(String adeCodigo, String sadCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel) && CodedValues.SAD_SUSPENSA.equals(sadCodigo)) {
            final Collection<OcorrenciaAutorizacao> existeOcorrenciaSuspenscao = findByAdeTocCodigo(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA, responsavel);
            return (existeOcorrenciaSuspenscao != null) && !existeOcorrenciaSuspenscao.isEmpty();
        }
        return false;
    }

    @Override
    public StatusSolicitacao findStatusSolicitacao(String ssoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            return StatusSolicitacaoHome.findByPrimaryKey(ssoCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public java.sql.Date calcularDataIniFimMargemExtra(String rseCodigo, java.sql.Date dataInicioFimAde, Short adeIncMargem, boolean periodoIni, boolean periodoFim, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if(((adeIncMargem == null) && (dataInicioFimAde != null)) || (dataInicioFimAde == null)) {
            return dataInicioFimAde;
        }

        if(adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO) || adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM) || adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) || adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
        } else {
            try {
                final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(adeIncMargem, rseCodigo);
                final MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKey(mrsPK);
                final java.sql.Date mrsPeriodoIni = mrsBean.getMrsPeriodoIni() != null ? new java.sql.Date(mrsBean.getMrsPeriodoIni().getTime()) : null;
                final java.sql.Date mrsPeriodoFim = mrsBean.getMrsPeriodoFim() != null ? new java.sql.Date(mrsBean.getMrsPeriodoFim().getTime()) : null;

                if(periodoIni && (mrsPeriodoIni != null) && (mrsPeriodoIni.compareTo(dataInicioFimAde) > 0)) {
                    return mrsPeriodoIni;
                } else if (periodoFim && (mrsPeriodoFim != null) && (dataInicioFimAde.compareTo(mrsPeriodoFim) > 0)) {
                    throw new AutorizacaoControllerException("mensagem.erro.calcular.data.inicio.fim.margem.extra.rse.data.fim.maior", responsavel);
                }
            } catch (final FindException ex) {
                LOG.warn("Registro de margem " + adeIncMargem + " não encontrado para o registro servidor " + rseCodigo);
            }
        }
        return dataInicioFimAde;

    }

    @Override
    public TransferObject verificaAdeTemDecisaoJudicial(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
         try {
             final ObtemDecisaoJudicialQuery qr = new ObtemDecisaoJudicialQuery();
             qr.adeCodigo = adeCodigo;

             final List<TransferObject> dados = qr.executarDTO();
             if ((dados != null) && !dados.isEmpty()) {
                 return dados.get(0);
             }
             return null;
         } catch (final HQueryException ex){
             LOG.error(ex.getMessage(), ex);
             throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
         }
    }

    @Override
    public void verificaAlteracaoReativacaoDecisaoJudicial(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
     // Alteração no contrato só pode acontecer se não houver decisão judicial vinculada ou se a decisão estiver revogada
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel) && (CodedValues.FUN_ALT_CONSIGNACAO.equals(responsavel.getFunCodigo())
                || CodedValues.FUN_REAT_CONSIGNACAO.equals(responsavel.getFunCodigo()) || CodedValues.FUN_ALTERAR_MULTIPLOS_CONTRATOS.equals(responsavel.getFunCodigo()))) {
            final TransferObject decisaoJudicial = verificaAdeTemDecisaoJudicial(adeCodigo, responsavel);
            if ((decisaoJudicial != null) && (decisaoJudicial.getAttribute(Columns.DJU_DATA_REVOGACAO) == null)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.alteracao.decisao.judicial.bloqueada", responsavel));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException("mensagem.erro.alteracao.decisao.judicial.bloqueada", responsavel);
            }
        }
    }

    private BigDecimal getValorContratoTotalRenegociacao(AutDesconto autdes, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, responsavel)) {
                final ObtemTotalValorRenegociacaoDentroPrazoQuery qr = new ObtemTotalValorRenegociacaoDentroPrazoQuery();
                qr.adeCodigo = autdes.getAdeCodigo();

                final List<TransferObject> dados = qr.executarDTO();
                if ((dados != null) && !dados.isEmpty()) {
                    final BigDecimal valorTotalRenegociacao = !TextHelper.isNull(dados.get(0).getAttribute(Columns.OCA_ADE_VLR_ANT)) ? (BigDecimal) dados.get(0).getAttribute(Columns.OCA_ADE_VLR_ANT) : autdes.getAdeVlr();

                    if (valorTotalRenegociacao.compareTo(autdes.getAdeVlr()) > 0) {
                        LOG.debug("CONTRATO FRUTO DE RENEGOCIAÇÃO ENCERRADA DENTRO DO PRAZO PARA LIBERAR MARGEM, UTILIZA O VALOR TOTAL DO CONTRATO RENEGOCIADO PARA MENOR: " +
                                  valorTotalRenegociacao);
                        return valorTotalRenegociacao;
                    }
                }
            }
            return autdes.getAdeVlr();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }
}
