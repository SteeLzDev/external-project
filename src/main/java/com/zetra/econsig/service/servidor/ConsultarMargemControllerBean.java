package com.zetra.econsig.service.servidor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.command.EnviarEmailServidorConsultaMargemCommand;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidor;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidorFactory;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailHome;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.HistoricoConsultaMargemHome;
import com.zetra.econsig.persistence.entity.LimiteMargemCsaOrg;
import com.zetra.econsig.persistence.entity.LimiteMargemCsaOrgHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoForaMargemQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosAbertosQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosAlteradosQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosConcluidosFeriasQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosEncerradosQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosLiqPrzIndeterminadoQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosNaoIncideMargemQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemSaldoRenegQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemVlrFolhaMenorQuery;
import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemVlrFolhaPercentualQuery;
import com.zetra.econsig.persistence.query.margem.ListaContratosCompulsoriosQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemDisponivelCompulsorioQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidorQuery;
import com.zetra.econsig.persistence.query.margem.ListaSomaMargensExtrasQuery;
import com.zetra.econsig.persistence.query.margem.ListaSomaMargensPrincipaisQuery;
import com.zetra.econsig.persistence.query.margem.ObtemTotalValorExcedenteProporcionalQuery;
import com.zetra.econsig.persistence.query.margem.ObtemTotalValorMargemRetidaQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCsaLimiteMargemQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCsaQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemDatasUltimoPeriodoRetornoQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ParamEmailExternoServidorEnum;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: ConsultarMargemControllerBean</p>
 * <p>Description: Session Bean para a operação de Consulta de Margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConsultarMargemControllerBean implements ConsultarMargemController {
    private static final Logger LOG = LoggerFactory.getLogger(ConsultarMargemControllerBean.class);

    private static final BigDecimal MARGEM_ZERADA = new BigDecimal("0.00");

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    /**
     * Operação de consulta para determinar se o servidor tem margem
     * para fazer um contrato de valor menor ou igual ao parâmetro adeVlr.
     * @param rseCodigo
     * @param adeVlr
     * @param svcCodigo
     * @param serAtivo
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public boolean servidorTemMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, boolean serAtivo, AcessoSistema responsavel) throws ServidorControllerException {
        final List<MargemTO> margens = consultarMargem(rseCodigo, adeVlr, svcCodigo, null, null, false, true, null, true, true, true, null, null, responsavel);
        if ((margens != null) && (margens.size() > 0)) {
            final MargemTO margem = margens.get(0);
            return margem.temMargemDisponivel();
        }
        return false;
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean senhaServidorOK, boolean serAtivo, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, null, false, senhaServidorOK, null, serAtivo, false, false, null, null, responsavel);
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean mobile, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, null, mobile, senhaServidorOK, senhaUtilizada, serAtivo, false, false, null, nseCodigo, responsavel);
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, null, false, senhaServidorOK, senhaUtilizada, serAtivo, false, false, null, nseCodigo, responsavel);
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean senhaServidorOK, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, marCodigo, false, senhaServidorOK, null, serAtivo, ignoraExibicaoMargem, false, adeCodigosRenegociacao, null, responsavel);
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, marCodigo, false, senhaServidorOK, senhaUtilizada, serAtivo, ignoraExibicaoMargem, false, adeCodigosRenegociacao, nseCodigo, responsavel);
    }

    @Override
    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean mobile, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, marCodigo, mobile, senhaServidorOK, senhaUtilizada, serAtivo, ignoraExibicaoMargem, false, adeCodigosRenegociacao, nseCodigo, responsavel);
    }

    /**
     * Operação de consulta das margens do servidor. Retorna uma lista de
     * MargemTO já preenchida de acordo com as configurações de exibição de margem.
     * @param rseCodigo : Código do registro servidor
     * @param adeVlr : Valor do contrato que está sendo testado
     * @param svcCodigo : Código do serviço que está sendo consultado, retornando a margem ligada a ele
     * @param csaCodigo : Código da consignatária que realiza a consulta
     * @param marCodigo : Código da margem que deve ser consultada
     * @param mobile : True se a solicitação é via mobile
     * @param senhaServidorOK : Indica que a senha do servidor foi informada
     * @param senhaUtilizada : Senha utilizada para a consulta de margem, caso obrigatório
     * @param serAtivo : Valida se o servidor está ativo
     * @param ignoraExibicaoMargem : True para ignorar as configurações de exibição, e retornar os valores de margem
     * @param adicionaMargemLateral : True se deve adicionar à margem restante o valor disponível da margem casada lateralmente
     * @param adeCodigosRenegociacao : Código dos contratos sendo renegociados/comprados
     * @param nseCodigo : Código da natureza de serviço
     * @param responsavel : Responsável pela consulta
     * @return
     * @throws ServidorControllerException
     */
    private List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean mobile, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, boolean ignoraExibicaoMargem, boolean adicionaMargemLateral, List<String> adeCodigosRenegociacao, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final List<MargemTO> margens = new ArrayList<>();
        try {
            if (responsavel.isCsaCor()) {
                csaCodigo = responsavel.getCsaCodigo();
                final String paramCancelaSolicitacaoServidor = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_CANCELA_SOLICITACAO_AO_PESQUISAR_SERVIDOR, responsavel);
                final boolean cancelaSolicitacaoServidor = (!TextHelper.isNull(paramCancelaSolicitacaoServidor) && CodedValues.TPA_SIM.equals(paramCancelaSolicitacaoServidor));
                if (cancelaSolicitacaoServidor) {
                    cancelarConsignacaoController.cancelarExpiradasCsa(rseCodigo, null, responsavel);
                }
            }

            // Busca o registro servidor
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
            final String orgCodigo = registroServidor.getOrgCodigo();
            final String vrsCodigo = registroServidor.getVrsCodigo();

            // Verifica status do servidor
            if (serAtivo) {
                if (registroServidor.isExcluido()) {
                    // Servidor excluído não pode fazer novas reservas.
                    throw new ServidorControllerException("mensagem.servidorExcluido", responsavel);
                } else if (registroServidor.isBloqueado()) {
                    // Verifica se servidor bloqueado pode fazer nova reserva
                    final boolean permiteIncluirAdeRseBloqueado = (svcCodigo != null ? ParamSvcTO.getParamSvcTO(svcCodigo, responsavel).isTpsPermiteIncluirAdeRseBloqueado() : false);
                    if (!permiteIncluirAdeRseBloqueado) {
                        // DESENV-16085: Servidor bloqueado sem seu respectivo parâmetro de sistema pertencente ao papel que está desativado não pode fazer novas reservas.
                        if ((responsavel.isCseOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSE_ORG, CodedValues.TPC_NAO, responsavel)) ||
                                (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSA_COR, CodedValues.TPC_NAO, responsavel)) ||
                                (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SUP, CodedValues.TPC_NAO, responsavel)) ||
                                (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER, CodedValues.TPC_NAO, responsavel))) {
                            throw new ServidorControllerException("mensagem.servidorBloqueado", responsavel);
                        }
                    }
                }
            }

            // Verifica se a consignatária/correspondente pode consultar a margem do servidor
            final String funCodigo = responsavel.getFunCodigo();
            if (responsavel.isCsaCor()) {
                // Só verifica limite de consultas de margem nas operações Consultar Margem, Consultar Consignação e Extrato de Dívida,
                // que são funções que CSA/COR podem visualizar a margem do servidor.
                if ((funCodigo == null) || CodedValues.FUN_CONS_MARGEM.equals(funCodigo) || CodedValues.FUN_CONS_CONSIGNACAO.equals(funCodigo) || CodedValues.FUN_EMITIR_EXTRATO_DIVIDA_SERVIDOR.equals(funCodigo)) {
                    final boolean podeConsultarMargem = ControleConsulta.getInstance().podeConsultar(csaCodigo, responsavel.getUsuCodigo(), rseCodigo, responsavel);
                    if (!podeConsultarMargem) {
                        throw new ServidorControllerException("mensagem.erro.numero.consultas.excedido", responsavel);
                    }
                }
            }

            // Valida bloqueio de vínculo na consulta de margem
            if (CodedValues.FUN_CONS_MARGEM.equals(responsavel.getFunCodigo())) {
                validarBloqVinculoServidor(csaCodigo, svcCodigo, vrsCodigo, responsavel);
            }

            String mensagemAdicional = null;
            if (responsavel.isCsaCor() && CodedValues.FUN_CONS_MARGEM.equals(responsavel.getFunCodigo()) && 
                CodedValues.TPA_SIM.equals(ParamCsa.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_BLOQ_CONSULTA_MARGEM_SERVIDOR_COM_BLOQUEIO, responsavel))) {
                // Se é uma operação de consulta de margem, e a consignatária bloqueia consulta de margem quando
                // servidor tem bloqueios de verba, serviço ou natureza serviço, então verifica se existem bloqueios
                try {
                    mensagemAdicional = autorizacaoController.verificaLimiteAoConsultarMargem(rseCodigo, orgCodigo, csaCodigo, svcCodigo, responsavel);
                } catch (final ZetraException ex) {
                    throw new ServidorControllerException(ex);
                }
            }

            // Parametro de sistema que exige ou não a senha para visualizar margem
            final boolean exigeSenha = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);

            // Verifica se está de acordo com parâmetro de sistema
            senhaServidorOK = (!exigeSenha || (exigeSenha && senhaServidorOK));

            if (exigeSenha && senhaServidorOK && !TextHelper.isNull(senhaUtilizada)) {
                final boolean usaSenhaAutorizacaoSer = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                final boolean usaSenhaAutorizacaoTodasOpe = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, CodedValues.TPC_SIM, responsavel);
                final boolean consomeSenhaConsultaMargem = ParamSist.paramEquals(CodedValues.TPC_CONSULTA_MARGEM_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel);

                if (usaSenhaAutorizacaoSer && usaSenhaAutorizacaoTodasOpe && consomeSenhaConsultaMargem) {
                    autorizacaoController.consumirSenhaDeAutorizacao(null, null, rseCodigo, svcCodigo, csaCodigo, senhaUtilizada, true, false, false, true, responsavel);
                }
            }

            // Pesquisa os tipos de margem, juntamente com o valor para o registro servidor
            final ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;
            if (!TextHelper.isNull(nseCodigo)) {
                query.nseCodigo = nseCodigo;
            }

            if (funCodigo != null) {
                if (CodedValues.FUN_CONS_EXTRATO_MARGEM.equals(funCodigo) || CodedValues.FUN_EXP_MOV_FINANCEIRO.equals(funCodigo)) {
                    // No extrato de margem, retorna as margens mesmo que não possuam
                    // convênio ativo, já que são utilizadas para os cálculos do extrato.
                    query.temConvenioAtivo = false;
                } else if (CodedValues.FUN_ALTERAR_MULTIPLOS_CONTRATOS.equals(funCodigo)) {
                    // Na alteração múltipla de consignação, devem ser retornadas as margens assim marcadas
                    // e devemos ignorar a configuração de exibição de margem
                    query.alteracaoMultiplaAde = true;
                    ignoraExibicaoMargem = true;
                }
            }

            boolean temMargem = false;
            boolean exibeValorForaMargem = true;
            final List<MargemTO> lstMargens = query.executarDTO(MargemTO.class);

            // Se deve exbir as margens dependentes, de acordo com o papel do usuário e dos parâmetros disponíveis, busca estes registros
            if ((responsavel.isCseOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSE_ORG, CodedValues.TPC_SIM, responsavel)) ||
                    (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSA_COR, CodedValues.TPC_SIM, responsavel)) ||
                    (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SUP, CodedValues.TPC_SIM, responsavel)) ||
                    (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SER, CodedValues.TPC_SIM, responsavel))) {

                final List<Short> marCodigosPai = lstMargens.stream().map(MargemTO::getMarCodigo).toList();

                final ListaMargemRegistroServidorQuery queryDependentes = new ListaMargemRegistroServidorQuery();
                queryDependentes.rseCodigo = rseCodigo;
                queryDependentes.temConvenioAtivo = false;
                queryDependentes.marCodigosPai = marCodigosPai;
                final List<MargemTO> lstMargensDependentes = queryDependentes.executarDTO(MargemTO.class);
                if ((lstMargensDependentes != null) && !lstMargensDependentes.isEmpty()) {
                    lstMargens.addAll(lstMargensDependentes);

                    lstMargens.sort((m1, m2) -> {
                        final Short seq1 = m1.getMarSequencia() != null ? m1.getMarSequencia() : 0;
                        final Short seq2 = m2.getMarSequencia() != null ? m2.getMarSequencia() : 0;
                        return (seq1.equals(seq2) ? m1.getMarCodigo().compareTo(m2.getMarCodigo()) : seq1.compareTo(seq2));
                    });
                }
            }

            String urlSistemaExterno = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONSULTAR_MARGEM_SISTEMA_EXTERNO, responsavel);
            if (!TextHelper.isNull(urlSistemaExterno)) {
                urlSistemaExterno = !urlSistemaExterno.endsWith("/") ? urlSistemaExterno + "/" : urlSistemaExterno;
                registroServidor = atualizaMargemServidorExternoRegistroServidor(urlSistemaExterno, registroServidor, responsavel);
            }

            for (final MargemTO margem : lstMargens) {
                final Short codigoMargem = margem.getMarCodigo();

                if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    margem.setMrsMargem(registroServidor.getRseMargem());
                    margem.setMrsMargemRest(registroServidor.getRseMargemRest());
                    margem.setMrsMargemUsada(registroServidor.getRseMargemUsada());
                    margem.setMrsMediaMargem(registroServidor.getRseMediaMargem());
                } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    margem.setMrsMargem(registroServidor.getRseMargem2());
                    margem.setMrsMargemRest(registroServidor.getRseMargemRest2());
                    margem.setMrsMargemUsada(registroServidor.getRseMargemUsada2());
                    margem.setMrsMediaMargem(registroServidor.getRseMediaMargem2());
                } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    margem.setMrsMargem(registroServidor.getRseMargem3());
                    margem.setMrsMargemRest(registroServidor.getRseMargemRest3());
                    margem.setMrsMargemUsada(registroServidor.getRseMargemUsada3());
                    margem.setMrsMediaMargem(registroServidor.getRseMediaMargem3());
                }

                margem.setServidorBloqueado(registroServidor.isBloqueado());

                if (margem.getMrsMargemRest() != null) {
                    // Calcula margem proporcional
                    margem.setMrsMargemRest(calcularMargemProporcional(codigoMargem, margem.getMrsMargemRest(), registroServidor, csaCodigo, adeCodigosRenegociacao, responsavel));

                    // Calcula margem casada lateralmente
                    if (adicionaMargemLateral) {
                        margem.setMrsMargemRest(calcularMargemLateral(codigoMargem, margem.getMrsMargemRest(), registroServidor, responsavel));
                    }

                    // Limita a margem para a consignatária
                    margem.setMrsMargemRest(limitarMargemRestanteCsa(codigoMargem, rseCodigo, margem.getMrsMargem(), margem.getMrsMargemRest(), margem.getMrsMargemUsada(), csaCodigo, orgCodigo, responsavel));

                    //Verifica se a margem restante é positiva
                    if (margem.getMrsMargemRest().compareTo(new BigDecimal(0)) == 1) {
                        temMargem = true;
                    }

                    // Adiciona na lista de resultado
                    margens.add(setMargemTO(margem, adeVlr, rseCodigo, senhaServidorOK, ignoraExibicaoMargem, exibeValorForaMargem, responsavel));

                    // Exibe valor fora da margem apenas na primeira margem, caso o usuário possa ver múltiplas
                    // margens, pois o valor fora da margem independe da margem a ser consultada, então iria
                    // refazer o mesmo cálculo N vezes.
                    exibeValorForaMargem = false;
                }

                if (mensagemAdicional != null) {
                    margem.addObservacao(mensagemAdicional);
                    // Limpa a variável para que a mensagem não seja adicionada a todas as margens
                    mensagemAdicional = null;
                }
            }

            if (!Log.ignorarLogConsultarServidor(responsavel)) {
                // Grava log de consulta de margem
                final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.FIND, Log.LOG_INFORMACAO);
                log.setRegistroServidor(rseCodigo);
                log.add(adeVlr != null ? ApplicationResourcesHelper.getMessage("mensagem.consultando.margem.valor", responsavel, NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang())) : ApplicationResourcesHelper.getMessage("mensagem.consultando.margem", responsavel));

                for (final MargemTO margem : lstMargens) {
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.margem.rest.resultado", responsavel, margem.getMarDescricao(), (margem.getMrsMargemRest() != null ? NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang()) : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel))));
                }

                log.write();
            }

            if (CodedValues.FUN_CONS_MARGEM.equals(funCodigo) && responsavel.isCsaCor()) {
                final CanalEnum canal = mobile ? CanalEnum.REST : responsavel.getCanal() != null ? responsavel.getCanal() : CanalEnum.WEB;
                HistoricoConsultaMargemHome.create(rseCodigo, responsavel.getUsuCodigo(), temMargem, canal);
            }

            final boolean tpcPodeEnviarEmail = (ParamSist.getInstance().getParam(CodedValues.TPC_ENVIAR_EMAIL_CONSULTA_MARGEM, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_ENVIAR_EMAIL_CONSULTA_MARGEM, responsavel));

            if (!responsavel.isSer() && tpcPodeEnviarEmail ) {
                final boolean podeEnviarEmail = DestinatarioEmailHome.verificaSeOperadorPodeEnviarEmail(responsavel.getFunCodigo(), responsavel.getPapCodigo(), CodedValues.PAP_SERVIDOR);

                if (podeEnviarEmail) {
                    final String consultarEmailExternoClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_BUSCA_EMAIL_SERVIDOR_API_EXTERNA, responsavel);
                    String serEmail;
                    String empresaNome;
                    final ServidorTransferObject ser = servidorController.findServidorByRseCodigo(registroServidor.getRseCodigo(), responsavel);

                    if (!TextHelper.isNull(consultarEmailExternoClassName)) {
                        final ConsultarEmailExternoServidor consultarEmailExternoServidor = ConsultarEmailExternoServidorFactory.getClasseConsultarEmailExternoServidor(consultarEmailExternoClassName);
                        final CustomTransferObject resultadoConsultaAPIExterna = consultarEmailExternoServidor.consultarEmailExternoServidor(ser.getSerCpf());
                        if (HttpStatus.OK.equals(resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_STATUS.getChave()))) {
                            serEmail = (String) resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_SUCCESS_DATA.getChave());
                        } else {
                            serEmail = ser.getSerEmail();
                        }
                    } else {
                        serEmail = ser.getSerEmail();
                    }

                    if (!TextHelper.isNull(serEmail)) {
                        final ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                        if (responsavel.isCsaCor()) {
                            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
                            empresaNome = csa.getCsaNome();
                        } else {
                            empresaNome = "";
                        }
                        final EnviarEmailServidorConsultaMargemCommand sendEmail = new EnviarEmailServidorConsultaMargemCommand();
                        sendEmail.setEmail(serEmail);
                        sendEmail.setCsaNome(empresaNome);
                        sendEmail.setSerNome(ser.getSerNome());
                        sendEmail.setCseNome(cse.getCseNome());
                        sendEmail.setResponsavel(responsavel);
                        sendEmail.execute();
                    }
                }
            }
        } catch (HQueryException | ParametroControllerException | LogControllerException | AutorizacaoControllerException | CreateException | ConsignatariaControllerException | ViewHelperException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        }

        return margens;
    }

    @Override
    public Map<String, String> consultarMargemDisponivelTotal(AcessoSistema responsavel) throws ServidorControllerException {
        final Map<String, String> margens = new HashMap<>();

        try {
            // Verifica se o responsável pode consultar a margem do servidor
            if (!responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM)) {
                throw new ServidorControllerException("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            }

            // Consulta a soma das margens padrão da registro_servidor
            final ListaSomaMargensPrincipaisQuery query = new ListaSomaMargensPrincipaisQuery();
            query.responsavel = responsavel;

            final List<TransferObject> margensPrincipais = query.executarDTO();
            final TransferObject resultado = margensPrincipais.get(0);
            margens.put(MargemHelper.getInstance().getMarDescricao((short) 1, responsavel), resultado.getAttribute(Columns.RSE_MARGEM_REST).toString());
            margens.put(MargemHelper.getInstance().getMarDescricao((short) 2, responsavel), resultado.getAttribute(Columns.RSE_MARGEM_REST_2).toString());
            margens.put(MargemHelper.getInstance().getMarDescricao((short) 3, responsavel), resultado.getAttribute(Columns.RSE_MARGEM_REST_3).toString());

            // Consulta a soma das margens margens extras
            final ListaSomaMargensExtrasQuery queryExtras = new ListaSomaMargensExtrasQuery();
            queryExtras.responsavel = responsavel;

            final List<TransferObject> margensExtras = queryExtras.executarDTO();
            for (final TransferObject margem : margensExtras) {
                margens.put((String) margem.getAttribute(Columns.MAR_DESCRICAO), margem.getAttribute("VALOR").toString());
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return margens;
    }

    /**
     * Atualiza o TransferObject com os dados da margem. Se a margem pode ser visualizada,
     * será retornado a descrição da margem e o valor. Se não, será retornado apenas
     * a mensagem dizendo se a margem é menor ou maior que o valor informado.
     * @param margem
     * @param adeVlr
     * @param rseCodigo
     * @param senhaServidorOk
     * @param responsavel
     * @return
     */
    private MargemTO setMargemTO(MargemTO margem, BigDecimal adeVlr, String rseCodigo, boolean senhaServidorOK, boolean ignoraExibicaoMargem, boolean exibeValorForaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        BigDecimal margemRest = margem.getMrsMargemRest() != null ? margem.getMrsMargemRest() : MARGEM_ZERADA;
        final ExibeMargem exibeMargem = new ExibeMargem(margem, responsavel);
        boolean temAutorizacaoCsa = false;
        if(responsavel.isCsa() && !TextHelper.isNull(rseCodigo)) {
            try {
                final boolean exigeSenhaSer = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, null, true, null, responsavel);
                temAutorizacaoCsa = !exigeSenhaSer;
            } catch (final ZetraException e) {
            }
        }

        final boolean isExibeValor = exibeMargem.isExibeValor();

        if (temAutorizacaoCsa || ((isExibeValor || ignoraExibicaoMargem) && senhaServidorOK)) {
            if(!temAutorizacaoCsa) {
                margemRest = ((margemRest.doubleValue() < 0) && !exibeMargem.isSemRestricao() && !ignoraExibicaoMargem) ? MARGEM_ZERADA : margemRest;
            }
            margem.setMrsMargemRest(margemRest);
            margem.setTemMargemDisponivel(((adeVlr != null) && (adeVlr.compareTo(margemRest) <= 0)) || ((adeVlr == null) && (margemRest.signum() == 1)));
        } else if (adeVlr != null) {
            margem.setMrsMargem(null);
            margem.setMrsMargemRest(null);
            if (adeVlr.compareTo(margemRest) <= 0) {
                margem.setObservacao(ApplicationResourcesHelper.getMessage("mensagem.valor.parcela.menor.param", responsavel, margem.getMarDescricao()));
                margem.setTemMargemDisponivel(true);
            } else {
                margem.setObservacao(ApplicationResourcesHelper.getMessage("mensagem.valor.parcela.maior.param", responsavel, margem.getMarDescricao()));
                margem.setTemMargemDisponivel(false);
            }
        } else {
            margem.setMarDescricao(null);
            margem.setMrsMargem(null);
            margem.setMrsMargemRest(null);
            margem.setTemMargemDisponivel(false);
        }

        retemMargemRevisao(margem, exibeMargem, rseCodigo, responsavel);

        if (exibeValorForaMargem) {
            exibirValorForaMargem(margem, exibeMargem, rseCodigo, responsavel);
        }

        return margem;
    }

    /**
     * Atualiza o TransferObject de margem, realizando o ajuste de retenção de margem
     * para revisão, caso o parâmetro esteja habilitado, e a margem possa ser exibida
     * sem restrições. O valor retido será o somatório dos contratos suspensos marcados
     * para este fim.
     * @param margem
     * @param exibeMargem
     * @param rseCodigo
     * @param responsavel
     * @throws ServidorControllerException
     */
    private void retemMargemRevisao(MargemTO margem, ExibeMargem exibeMargem, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final boolean retemMargemRevisao = ParamSist.paramEquals(CodedValues.TPC_RETEM_MARGEM_REVISAO_ACAO_SUSPENSAO, CodedValues.TPC_SIM, responsavel);
        if (retemMargemRevisao && exibeMargem.isExibeValor() && exibeMargem.isSemRestricao() && (margem.getMrsMargemRest() != null)) {
            // Se exibe margem sem restrição, e a margem está preenchida, verifica se o servidor
            // possui contratos suspensos com margem retida
            BigDecimal margemRetida = null;
            try {
                final ObtemTotalValorMargemRetidaQuery query = new ObtemTotalValorMargemRetidaQuery();
                query.rseCodigo = rseCodigo;
                query.marCodigo = margem.getMarCodigo();
                margemRetida = query.executarSomatorio();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
            if ((margemRetida != null) && (margemRetida.signum() == 1)) {
                /**
                 * Adiciona o valor da margem retida à margem restante caso a margem restante não seja negativa.
                 * Se a margem restante for negativa, exibe sempre o valor zerado.
                 */
                if (margem.getMrsMargemRest().signum() < 0) {
                    margem.setMrsMargemRest(BigDecimal.ZERO);
                } else {
                    margem.setMrsMargemRest(margem.getMrsMargemRest().add(margemRetida));
                }
                // Inclui observação de margem retida, por revisão de margem
                final String valorMargemRetida = NumberHelper.format(margemRetida.doubleValue(), NumberHelper.getLang());
                margem.addObservacao(ApplicationResourcesHelper.getMessage("rotulo.margem.valorRetido", responsavel, valorMargemRetida));
            }
        }
    }

    /**
     * Exibe o somatório do valor das consignações abertas associados a serviço/consignatária
     * que possuem o parâmetro TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM habilitado.
     * @param margem
     * @param exibeMargem
     * @param rseCodigo
     * @param responsavel
     * @throws ServidorControllerException
     */
    private void exibirValorForaMargem(MargemTO margem, ExibeMargem exibeMargem, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        if (exibeMargem.isExibeValor() && exibeMargem.isSemRestricao() && (margem.getMrsMargemRest() != null)) {
            try {
                // Verifica se existem serviços de consignatária habilitados para o parâmetro TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM
                final List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM);
                final ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
                query.tpsCodigos = tpsCodigos;
                query.pscVlr = CodedValues.PSC_BOOLEANO_SIM;
                final List<TransferObject> lstParamSvcCsa = query.executarDTO();

                // Se existem serviços de consignatária habilitados para o parâmetro TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM
                // então obtém o somatório do valor fora da margem para este servidor
                if ((lstParamSvcCsa != null) && !lstParamSvcCsa.isEmpty()) {
                    final ObtemTotalValorConsignacaoForaMargemQuery queryVlrTotal = new ObtemTotalValorConsignacaoForaMargemQuery();
                    queryVlrTotal.rseCodigo = rseCodigo;
                    queryVlrTotal.sadCodigos = CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA;
                    final BigDecimal vlrTotal = queryVlrTotal.executarSomatorio();

                    // Se o servidor possui consignações do serviço e consignatária que está habilitado o parâmetro
                    // TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM, então exibe mensagem no resultado da consulta da margem
                    if ((vlrTotal != null) && (vlrTotal.signum() > 0)) {
                        final String valorForaMargem = NumberHelper.format(vlrTotal.doubleValue(), NumberHelper.getLang());
                        margem.addObservacao(ApplicationResourcesHelper.getMessage("rotulo.margem.valorForaMargem", responsavel, valorForaMargem));
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Verifica se o vínculo do registro servidor está bloqueado para a CSA/SVC e
     * esta optou por não retornar margem na consulta via SOAP
     * @param csaCodigo
     * @param svcCodigo
     * @param orgCodigo
     * @param vrsCodigo
     * @param responsavel
     * @throws ServidorControllerException
     * @throws AutorizacaoControllerException
     */
    private void validarBloqVinculoServidor(String csaCodigo, String svcCodigo, String vrsCodigo, AcessoSistema responsavel) throws ServidorControllerException, ConsignatariaControllerException {
        try {
            if (responsavel.isCsaCor() && !TextHelper.isNull(vrsCodigo)) {
                final String paramBloqVinc = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
                boolean bloqPadrao = CodedValues.TPA_SIM.equals(paramBloqVinc);
                if (!bloqPadrao && CanalEnum.SOAP.equals(responsavel.getCanal())) {
                    final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_BLOQ_CONSULTA_MARGEM_VINCULO_HOST_A_HOST, responsavel);
                    bloqPadrao = "S".equalsIgnoreCase(pcsVlr);
                }
                if (bloqPadrao) {
                    autorizacaoController.verificaBloqueioVinculoCnv(csaCodigo, svcCodigo, vrsCodigo, responsavel);
                }
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.nao.possivel.validar.vinculo.servidor", responsavel, ex);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            final String[] vinculo = ex.getMessage().split(":");
            VinculoConsignataria vcsa = null;
            if (!TextHelper.isNull(vrsCodigo) && !TextHelper.isNull(csaCodigo)) {
                vcsa = consignatariaController.findVinculoCsaPorVrsCsa(csaCodigo, vrsCodigo, responsavel);
            }
            if (vcsa == null) {
                throw new ServidorControllerException("mensagem.vinculoNaoPermiteConsultarMargem", responsavel, vinculo.length > 1 ? vinculo[1] : "");
            } else {
                throw new ServidorControllerException("mensagem.vinculoNaoPermiteConsultarMargem.vinculoCsa", responsavel, vinculo.length > 1 ? vinculo[1] : "", vcsa.getVcsDescricao());
            }
        }
    }

    /**
     * Soma o valor dos contratos com tratamento especial de margem, que são contratos de serviços que
     * não incidem sobre margem e possuem o TPS_CODIGO=224 habilitado.
     * @param margemRest
     * @param rseCodigo
     * @param responsavel
     * @return Soma dos valores dos contratos
     * @throws ServidorControllerException
     */
    @Override
    public BigDecimal somarContratosTratamentoEspecialMargem(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ObtemTotalValorConsignacaoQuery query = new ObtemTotalValorConsignacaoQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = null;
            query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
            query.tratamentoEspecialMargem = true;
            final double somaFinanc = query.executarSomatorio(BigDecimal.ZERO).doubleValue();
            return new BigDecimal(somaFinanc);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Calcula o valor da margem proporcional disponível para a consignatária. Atualmente
     * o método só funciona para margem 1 proporcional casada com a margem 3, através dos
     * parâmetros de sistema TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3 e
     * TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA.
     * @param marCodigo  : Código da margem -> só será afetado se igual a 1
     * @param margemRest : Margem disponível -> será recalculada, caso não seja margem 1, retornará o mesmo valor
     * @param registroServidor : Dados do registro servidor, de onde as margens serão obtidas
     * @param csaCodigo : Código da consignatária
     * @param adeCodigosRenegociacao : Códigos dos contratos sendo renegociados/comprados
     * @param responsavel : Usuário responsável pela operação
     * @return
     * @throws ServidorControllerException
     * @TODO Generalizar conceito para aplicar às demais combinações de margem, especialmente as margens extras
     */
    @Override
    public BigDecimal calcularMargemProporcional(Short marCodigo, BigDecimal margemRest, RegistroServidorTO registroServidor, String csaCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel)) {

                final String rseCodigo = registroServidor.getRseCodigo();
                final String orgCodigo = registroServidor.getOrgCodigo();

                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) && !TextHelper.isNull(csaCodigo)) {
                    double margemDisponivel = 0;

                    // Verifica se os contratos renegociados/comprados são de consignatária que não possui convênio
                    // com serviço relacionado à financiamento de dívida de cartão. Caso seja, calcula a margem
                    // proporcional como se fosse esta consignatária, ao invés daquela que está realizando a operação.
                    if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
                        // Localiza o csaCodigo do contrato (será sempre 1 no caso de financiamento)
                        final String csaCodigoContrato = ConsignatariaHome.findByAdeCodigo(adeCodigosRenegociacao.get(0)).getCsaCodigo();
                        // Verifica se a consignatária do contrato possui convênio ativo com serviço relacionado a financiamento de dívida
                        final List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(null, csaCodigoContrato, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
                        if ((svcCodigosDestino == null) || (svcCodigosDestino.size() == 0)) {
                            csaCodigo = csaCodigoContrato;
                        }
                    }

                    final ObtemTotalValorConsignacaoQuery query1 = new ObtemTotalValorConsignacaoQuery();
                    query1.rseCodigo = rseCodigo;
                    query1.adeIncMargem = marCodigo;
                    query1.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaFinancGeral = query1.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final ObtemTotalValorConsignacaoQuery query2 = new ObtemTotalValorConsignacaoQuery();
                    query2.rseCodigo = rseCodigo;
                    query2.csaCodigo = csaCodigo;
                    query2.adeIncMargem = marCodigo;
                    query2.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaFinancCsa = query2.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final ObtemTotalValorConsignacaoQuery query3 = new ObtemTotalValorConsignacaoQuery();
                    query3.rseCodigo = rseCodigo;
                    query3.adeIncMargem = CodedValues.INCIDE_MARGEM_SIM_3;
                    query3.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaCartaoGeral = query3.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final ObtemTotalValorConsignacaoQuery query4 = new ObtemTotalValorConsignacaoQuery();
                    query4.rseCodigo = rseCodigo;
                    query4.csaCodigo = csaCodigo;
                    query4.adeIncMargem = CodedValues.INCIDE_MARGEM_SIM_3;
                    query4.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaCartaoCsa = query4.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final ObtemTotalValorExcedenteProporcionalQuery query5 = new ObtemTotalValorExcedenteProporcionalQuery();
                    query5.rseCodigo = rseCodigo;
                    query5.adeIncMargem = marCodigo;
                    query5.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaExcedenteGeral = query5.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final ObtemTotalValorExcedenteProporcionalQuery query6 = new ObtemTotalValorExcedenteProporcionalQuery();
                    query6.rseCodigo = rseCodigo;
                    query6.csaCodigo = csaCodigo;
                    query6.adeIncMargem = marCodigo;
                    query6.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                    final double somaExcedenteCsa = query6.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    // Se a consignatária não tem cartão, a margem proporcional será zero
                    if (somaCartaoCsa > 0) {

                        /**
                         * PROPORCAO-CSA = (SOMA(CARTAO-CSA) + SOMA(FINANC-CSA) - SOMA(EXCEDENTE-CSA))
                         *               / (SOMA(CARTAO-GERAL) + SOMA(FINANC-GERAL) - SOMA(EXCEDENTE-GERAL))
                         *
                         * OBS 1: A proporção deve ser no máximo 1 (Math.min(x, 1))
                         * OBS 2: A divisão não pode ser por zero (Math.max(y, 1))
                         */
                        final double proporcaoCsa = Math.min(((somaCartaoCsa + somaFinancCsa) - somaExcedenteCsa) / Math.max((somaCartaoGeral + somaFinancGeral) - somaExcedenteGeral, 1), 1);

                        final double margemExtraFolha = registroServidor.getRseMargem().doubleValue();
                        final double margemExtraDisp = registroServidor.getRseMargemRest().doubleValue();
                        final double margemCartaoFolha = (registroServidor.getRseMargem3() != null ? registroServidor.getRseMargem3().doubleValue() : 0);
                        final double margemCartaoDisp = (registroServidor.getRseMargemRest3() != null ? registroServidor.getRseMargemRest3().doubleValue() : 0);

                        /**
                         * MARGEM DISP = ((MARGEM EXTRA FOLHA - MARGEM CARTAO FOLHA) * PROPORCAO-CSA) - SOMA(EXCEDENTE-CSA) + MARGEM CARTAO DISP
                         *
                         * OBS 1: Só soma a margem disponível cartão se for positiva (Math.max(z, 0))
                         * OBS 2: Por segurança, realiza o mínimo entre o proporcional calculado e a margem restante real
                         */
                        margemDisponivel = (((margemExtraFolha - margemCartaoFolha) * proporcaoCsa) - somaExcedenteCsa) + Math.max(margemCartaoDisp, 0);

                        margemDisponivel = Math.min(margemDisponivel, margemExtraDisp);
                    }

                    // Retorna o valor da margem proporcional calculada
                    margemRest = new BigDecimal(margemDisponivel).setScale(2, java.math.RoundingMode.HALF_UP);

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    /**
                     * MARGEM DISP = MARGEM DISP + SOMA(EXCEDENTE-GERAL)
                     *
                     * OBS 1: Como as margens são casadas pela esquerda, o consumo do financiamento estará reduzindo
                     * a margem cartão, porém ela primeiramente deve reduzir da margem de financiamento.
                     */

                    final ObtemTotalValorExcedenteProporcionalQuery query5 = new ObtemTotalValorExcedenteProporcionalQuery();
                    query5.rseCodigo = rseCodigo;
                    query5.adeIncMargem = CodedValues.INCIDE_MARGEM_SIM;
                    query5.sadCodigos = Arrays.asList(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO);
                    final double somaExcedenteGeral = query5.executarSomatorio(BigDecimal.ZERO).doubleValue();

                    final double margemDisponivel = margemRest.doubleValue() + somaExcedenteGeral;
                    margemRest = new BigDecimal(margemDisponivel).setScale(2, java.math.RoundingMode.HALF_UP);
                }
            }

            return margemRest;
        } catch (final HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public BigDecimal calcularMargemLateral(Short marCodigo, BigDecimal margemRest, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_MARGEM_ORIGINAL_EXCEDE_ATE_MARGEM_LATERAL, CodedValues.TPC_SIM, responsavel)) {
            // Processa casamento de margem 1 e 3 lateral
            if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel)) {
                final BigDecimal rseMargemRest3 = registroServidor.getRseMargemRest3();
                if ((rseMargemRest3 != null) && (rseMargemRest3.signum() > 0)) {
                    // Se a margem original já estiver negativa, a margem rest lateral já estará impactada por ela.
                    // Então, neste caso faz um MAX entre o valor restante e Zero para que quando a margem original
                    // estiver negativa, não seja abatido duplamente da margem restante lateral.
                    margemRest = rseMargemRest3.add(margemRest.max(BigDecimal.ZERO));
                }
            }

            // Verifica casamento lateral de margens extras
            if (CasamentoMargem.getInstance().temCasamentoDoTipo(CasamentoMargem.LATERAL)) {
                final List<Short> grupos = CasamentoMargem.getInstance().getGrupos();
                if ((grupos != null) && (grupos.size() > 0)) {
                    for (final Short grupo : grupos) {
                        // Para cada grupo de casamento de margem (podem ser múltiplos) verifica se é um grupo de
                        // casamento lateral de margem.
                        final String tipo = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                        if (CasamentoMargem.LATERAL.equals(tipo)) {
                            final List<Short> marCodigosCasamento = CasamentoMargem.getInstance().getMargensCasadas(grupo);

                            // Navega pelas margens do grupo de casamento, encontra a margem que está sendo afetada pela operação
                            // na lista, na orgem do casamento do grupo
                            for (int i = 0; i < marCodigosCasamento.size(); i++) {
                                final Short marCodigoCasamento = marCodigosCasamento.get(i);
                                if (marCodigo.equals(marCodigoCasamento) && ((i + 1) < marCodigosCasamento.size())) {
                                    // Pega a próxima margem lateral, caso exista, e faz o mesmo tratamento do caso da margem 1 casada com 3
                                    final Short marCodigoLateral = marCodigosCasamento.get(i + 1);

                                    try {
                                        // Obtém o valor da margem lateral para fazer a verificação se é possível exceder a margem original
                                        final MargemRegistroServidor mrs = MargemRegistroServidorHome.findByPrimaryKey(new MargemRegistroServidorId(marCodigoLateral, registroServidor.getRseCodigo()));
                                        final BigDecimal mrsMargemRestLateral = mrs.getMrsMargemRest();

                                        if ((mrsMargemRestLateral != null) && (mrsMargemRestLateral.signum() > 0)) {
                                            // Se a margem original já estiver negativa, a margem rest lateral já estará impactada por ela.
                                            // Então, neste caso faz um MAX entre o valor restante e Zero para que quando a margem original
                                            // estiver negativa, não seja abatido duplamente da margem restante lateral.
                                            margemRest = mrsMargemRestLateral.add(margemRest.max(BigDecimal.ZERO));
                                        }
                                    } catch (final FindException ex) {
                                        LOG.error(ex.getMessage(), ex);
                                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return margemRest;
    }

    /**
     * Limita a margem restante representada pelo código de margem (adeIncMargem) que tenha cadastrado nos
     * parâmetros de serviço por consignatária um percentual a ser aplicado sobre a margem.
     * @param marCodigo      : Código da margem
     * @param rseCodigo      : Código do registro servidor
     * @param rseMargem      : Margem folha enviada pela folha
     * @param rseMargemRest  : Margem restante disponível -> será recalculada, caso não seja CSA/COR ou não tenha parâmetro de limitação, retornará o mesmo valor
     * @param rseMargemUsada : Margem usada atual
     * @param csaCodigo      : Código da consignatária
     * @param responsavel    : Usuário responsável pela operação
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public BigDecimal limitarMargemRestanteCsa(Short marCodigo, String rseCodigo, BigDecimal rseMargemFolha, BigDecimal rseMargemRest, BigDecimal rseMargemUsada, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Calcula margem limite para a CSA de acordo com o tipo de margem que a consignatária optou por reduzir
            // na configuração dos parâmetros de serviço da consignatária
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_LIMITAR_USO_MARGEM_SERVIDOR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
                LimiteMargemCsaOrg limiteMargemCsaOrg;

                try {
                    limiteMargemCsaOrg = LimiteMargemCsaOrgHome.findByPrimaryKey(marCodigo, csaCodigo, orgCodigo);
                } catch (final FindException ex) {
                    limiteMargemCsaOrg = null;
                }
                final BigDecimal lmcValor = (limiteMargemCsaOrg != null) && (limiteMargemCsaOrg.getLmcValor() != null) && (limiteMargemCsaOrg.getLmcValor().compareTo(BigDecimal.ONE) != 1) ? limiteMargemCsaOrg.getLmcValor() : BigDecimal.ZERO;

                if (lmcValor.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal margemCalculada = rseMargemFolha.multiply(lmcValor).setScale(2, java.math.RoundingMode.DOWN);
                    margemCalculada = margemCalculada.subtract(rseMargemUsada);
                    rseMargemRest = margemCalculada.min(rseMargemRest);
                } else {
                    final ListaParamSvcCsaLimiteMargemQuery query = new ListaParamSvcCsaLimiteMargemQuery();
                    query.csaCodigo = csaCodigo;
                    query.rseCodigo = rseCodigo;
                    query.marCodigo = marCodigo;
                    final List<?> paramPercentualMargem = query.executarLista();

                    if ((paramPercentualMargem != null) && !paramPercentualMargem.isEmpty() && (paramPercentualMargem.get(0) != null)) {
                        // Na DESENV-17859, o hibernate passou de Double para Float, e na DESENV-21058 voltou para Double. Interpreta
                        // o resultado de acordo com o tipo do objeto retornado, evitando inconsistência do hibernate e seus mapeamentos
                        BigDecimal percentualLimite = BigDecimal.ZERO;
                        if (paramPercentualMargem.get(0) instanceof final Double doubleValue) {
                            percentualLimite = BigDecimal.valueOf(doubleValue).setScale(2, java.math.RoundingMode.DOWN);
                        } else if (paramPercentualMargem.get(0) instanceof final Float floatValue) {
                            percentualLimite = BigDecimal.valueOf(floatValue.doubleValue()).setScale(2, java.math.RoundingMode.DOWN);
                        } else if (paramPercentualMargem.get(0) instanceof final BigDecimal bigDecimalValue) {
                            percentualLimite = bigDecimalValue;
                        } else {
                            percentualLimite = BigDecimal.valueOf(Double.parseDouble(paramPercentualMargem.get(0).toString())).setScale(2, java.math.RoundingMode.DOWN);
                        }

                        LOG.debug("Valor pertentual limite: {}", percentualLimite);

                        LOG.debug("Valor margem folha, margem usada, margem restante: {}, {}, {}", rseMargemFolha, rseMargemUsada, rseMargemRest);

                        BigDecimal margemCalculada = rseMargemFolha.multiply(percentualLimite).setScale(2, java.math.RoundingMode.DOWN);
                        margemCalculada = margemCalculada.subtract(rseMargemUsada);
                        
                        rseMargemRest = margemCalculada.min(rseMargemRest);
                        
                        LOG.debug("Valor final margem restante: {}", rseMargemRest);

                    }
                }
            }

            return rseMargemRest;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public BigDecimal getMargemDisponivelCompulsorio(String rseCodigo, String svcCodigo, String svcPrioridade, Short adeIncMargem, boolean controlaMargem, String adeCodigo, AcessoSistema responsavel) throws ServidorControllerException {
    	try {
            BigDecimal saldoPeriodo = new BigDecimal("0");
            final ListaMargemDisponivelCompulsorioQuery query = new ListaMargemDisponivelCompulsorioQuery();
            query.rseCodigo = rseCodigo;
            query.adeIncMargem = adeIncMargem;
            query.controlaMargem = controlaMargem;
            query.adeCodigo = adeCodigo;
            query.alteracao = (!TextHelper.isNull(adeCodigo));
            List<TransferObject> values = query.executarDTO();
            if ((values != null) && (!values.isEmpty())) {
                final TransferObject cto = values.get(0);
                if (cto.getAttribute("MARGEM") != null) {
                    saldoPeriodo = saldoPeriodo.add((BigDecimal) cto.getAttribute("MARGEM"));
                }
                if (cto.getAttribute("MARGEM_USADA") != null) {
                    saldoPeriodo = saldoPeriodo.subtract((BigDecimal) cto.getAttribute("MARGEM_USADA"));
                }
            }

            // Soma do valor dos contratos que podem ser liberados para a inclusão de um compulsório
            BigDecimal margemDisponivel = new BigDecimal("0.00");
            final ListaContratosCompulsoriosQuery queryVlr = new ListaContratosCompulsoriosQuery();
            queryVlr.somatorioValor = true;
            queryVlr.rseCodigo = rseCodigo;
            queryVlr.svcCodigo = svcCodigo;
            queryVlr.svcPrioridade = svcPrioridade;
            values = queryVlr.executarDTO();
            if ((values != null) && (!values.isEmpty())) {
                final TransferObject to = values.get(0);
                if (!TextHelper.isNull(to.getAttribute("VLR"))) {
                    margemDisponivel = new BigDecimal(to.getAttribute("VLR").toString());
                }
            }

            boolean consideraMargemRestAtualCompulsorio = false;
            final CustomTransferObject paramSvcConsideraMargemRestAtualCompulsorio = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO, responsavel);
            if ((paramSvcConsideraMargemRestAtualCompulsorio != null) && (paramSvcConsideraMargemRestAtualCompulsorio.getAttribute(Columns.PSE_VLR) != null)) {
                consideraMargemRestAtualCompulsorio = ("1".equals(paramSvcConsideraMargemRestAtualCompulsorio.getAttribute(Columns.PSE_VLR).toString()));
            }

            // Adiciona a margem restante disponível no período, somente se ela for positiva
            if ((saldoPeriodo.signum() > 0) || consideraMargemRestAtualCompulsorio) {
                margemDisponivel = margemDisponivel.add(saldoPeriodo);
            }

            return margemDisponivel;

        } catch (HQueryException | ParametroControllerException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

	@Override
    public List<TransferObject> lstExtratoMargemRse(String rseCodigo, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ObtemDatasUltimoPeriodoRetornoQuery query = new ObtemDatasUltimoPeriodoRetornoQuery();
            query.orgCodigo = orgCodigo;
            final List<TransferObject> result = query.executarDTO();
            TransferObject ultimoPeriodo = null;
            if ((result != null) && (!result.isEmpty())) {
                ultimoPeriodo = result.get(0);
            }

            if(ultimoPeriodo == null){
                final TransferObject periodoAnteriorResult = new CustomTransferObject();
                final Date periodoAnterior = PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);
                final Date dataFinPeriodo = DateHelper.addSeconds(PeriodoHelper.getInstance().getDataIniPeriodoAtual(orgCodigo, responsavel), -1);

                periodoAnteriorResult.setAttribute(Columns.HIE_PERIODO, periodoAnterior);
                periodoAnteriorResult.setAttribute(Columns.HIE_DATA_FIM, DateHelper.format(dataFinPeriodo, "yyyy-MM-dd HH:mm:ss"));
                ultimoPeriodo = periodoAnteriorResult;
            }

            if ((ultimoPeriodo != null) && (ultimoPeriodo.getAttribute(Columns.HIE_PERIODO) != null) && (ultimoPeriodo.getAttribute(Columns.HIE_DATA_FIM) != null)) {
                final String dataFimUltPeriodo = DateHelper.reformat(ultimoPeriodo.getAttribute(Columns.HIE_DATA_FIM).toString(), "yyyy-MM-dd", "yyyy-MM-dd 23:59:59");
                final Date ultPeriodo = DateHelper.toPeriodDate((Date) ultimoPeriodo.getAttribute(Columns.HIE_PERIODO));
                final Date fimUltPeriodo = DateHelper.parse(dataFimUltPeriodo, "yyyy-MM-dd HH:mm:ss");

                final List<TransferObject> resultado = new ArrayList<>();

                // PASSO 1) SOMA OS CONTRATOS ABERTOS DO SERVIDOR, DE ACORDO COM O PARAMETRO DE CONTROLE DE MARGEM
                final ListaExtratoMargemContratosAbertosQuery query1 = new ListaExtratoMargemContratosAbertosQuery(rseCodigo);
                resultado.addAll(query1.executarDTO());

                // PASSO 2) VE A DIFERENCA ENTRE OS CONTRATOS AGUARD. LIQUIDACAO E OS AGUARD. CONFIRMACAO
                // DOS PROCESSOS DE RENEGOCIACAO E COMPRA. INCLUI OS LIQUIDADOS E CONCLUIDOS, POIS
                // AINDA PRENDEM O VALOR DA MARGEM DO SERVIDOR
                final ListaExtratoMargemSaldoRenegQuery query2 = new ListaExtratoMargemSaldoRenegQuery(rseCodigo);
                resultado.addAll(query2.executarDTO());

                // PASSO 3) ADICIONA NA MARGEM USADA, O VALOR PAGO A MENOS PELA FOLHA
                // OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S') E
                // QUE SUBTRAEM DA MARGEM OS PAGAMENTOS PARCIAIS (TPC 323 != 'N')
                if (ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel) && !ParamSist.paramEquals(CodedValues.TPC_SUBTRAI_PAGAMENTO_PARCIAL_MARGEM, CodedValues.TPC_NAO, responsavel)) {
                    final ListaExtratoMargemVlrFolhaMenorQuery query3 = new ListaExtratoMargemVlrFolhaMenorQuery(rseCodigo);
                    resultado.addAll(query3.executarDTO());
                }

                // PASSO 4) ADICIONA A MARGEM USADA A DIFERENCA ENTRE O VALOR PAGO PELA FOLHA E O VALOR
                // DOS CONTRATOS DO TIPO PERCENTUAL, SOMENTE OS QUE FORAM PAGOS NO ULTIMO RETORNO
                // OBS: SÓ PARA SISTEMAS QUE CONTROLAM MARGEM (TPC 23 != 'S')
                if (!ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel)) {
                    final ListaExtratoMargemVlrFolhaPercentualQuery query4 = new ListaExtratoMargemVlrFolhaPercentualQuery(rseCodigo, ultPeriodo);
                    resultado.addAll(query4.executarDTO());
                }

                // PASSO 5) SE CONTROLA MARGEM, ADICIONA NA MARGEM USADA OS CONTRATOS CONCLUIDOS POR DESCONTO DE
                // FÉRIAS NO PERIODO DE ACORDO COM O PARAMETRO DE SISTEMA
                // OBS: SÓ PARA SISTEMAS QUE CONTROLAM MARGEM (TPC 23 != 'S') E
                // QUE NÃO LIBERAM MARGEM NA CONCLUSÃO DOS CONTRATOS EM FÉRIAS (TPC 289 = N - Default: Sim) OU
                // QUE NÃO LIBERAM MARGEM NA CONCLUSÃO DOS CONTRATOS EM GERAL (TPC 62 != S - Default: Não) OU
                if (!ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel) && (ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS, CodedValues.TPC_NAO, responsavel) || !ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, responsavel))) {
                    final ListaExtratoMargemContratosConcluidosFeriasQuery query5 = new ListaExtratoMargemContratosConcluidosFeriasQuery(rseCodigo, ultPeriodo, fimUltPeriodo);
                    resultado.addAll(query5.executarDTO());
                }

                // PASSO 6) SE NAO CONTROLA MARGEM, SUBTRAI DA MARGEM USADA OS CONTRATOS LIQUIDADOS/CONCLUIDOS NO PERIODO
                // DE ACORDO COM OS PARAMETROS DE SISTEMA
                // OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S')
                if (ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel)) {
                    final ListaExtratoMargemContratosEncerradosQuery query6 = new ListaExtratoMargemContratosEncerradosQuery(rseCodigo, ultPeriodo, fimUltPeriodo);
                    resultado.addAll(query6.executarDTO());
                }

                // PASSO 7) OU SUBTRAI DA MARGEM USADA O VALOR DOS CONTRATOS ALTERADOS NO SISTEMA, QUE POSSUEM OCORRENCIA DE ALTERAÇÃO
                // OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S')
                if (ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel)) {
                    final ListaExtratoMargemContratosAlteradosQuery query7 = new ListaExtratoMargemContratosAlteradosQuery(rseCodigo, fimUltPeriodo);
                    resultado.addAll(query7.executarDTO());
                }

                // PASSO 8) ADICIONA NA MARGEM USADA OS CONTRATOS DE PRAZO INDETERMINADO LIQUIDADOS
                // ASSOCIADOS A SERVIÇO QUE SÓ LIBERA MARGEM APÓS A CARGA DE MARGEM
                final ListaExtratoMargemContratosLiqPrzIndeterminadoQuery query8 = new ListaExtratoMargemContratosLiqPrzIndeterminadoQuery(rseCodigo);
                resultado.addAll(query8.executarDTO());

                // PASSO 9) REMOVER DA LISTA DE RESULTADOS OS CONTRATOS COM CARÊNCIA QUE JÁ SÃO CONHECIDOS DA FOLHA QUANDO O PARÂMETRO DE SISTEMA QUE LIBERAR MARGEM ESTA HABILITADO (872) DESENV-16566
                if(ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_LIBERA_MARGEM_ENVIADA_PELA_FOLHA_CONSIG_CARENCIA, AcessoSistema.getAcessoUsuarioSistema())
                        && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN, AcessoSistema.getAcessoUsuarioSistema())) {
                    try {
                        final Estabelecimento estabelecimento = EstabelecimentoHome.findByOrgao(orgCodigo);
                        final Date ultPeriodoRetorno = impRetornoController.getUltimoPeriodoRetorno(orgCodigo, estabelecimento.getEstCodigo(), responsavel);

                        final List<String> sadCodigos = new ArrayList<>(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQUIDA);
                        final List<TransferObject> contratosComParcela = pesquisarConsignacaoController.pesquisarContratosComParcela(rseCodigo, sadCodigos, null, null, null, responsavel);

                        int parcelasPagasTotal = 0;
                        int parcelasProcessadasTotal =0;

                        for (final TransferObject contrato : contratosComParcela) {
                            final Date prdDataDesconto = (Date) contrato.getAttribute(Columns.PRD_DATA_DESCONTO);
                            if(prdDataDesconto.compareTo(ultPeriodoRetorno) != 0) {
                                continue;
                            }
                            parcelasProcessadasTotal++;
                            final String spdCodigo = (String) contrato.getAttribute(Columns.SPD_CODIGO);
                            // Somente quando todas as parcelas processadas para o periodo são pagas precisamos então retirar esse contrato da lista e deixar somente os contratos que a folha não conhece.
                            if((CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) || CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo))) {
                                parcelasPagasTotal++;
                            }
                        }

                        // Validação para verificar se o contrato é um contrato em carência exportado que a folha já o conhece, montamos a lista para depois remover ele dos resultados, pois eles não
                        // devem ser contatos no extrado de margem, pois a folha já o conhece
                        if((parcelasPagasTotal > 0) && (parcelasPagasTotal == parcelasProcessadasTotal)) {
                            final List<TransferObject> resultadoFinal = new ArrayList<>();
                            for(final TransferObject ade : resultado) {
                                final Date adeUltPeriodoExportacao = ade.getAttribute(Columns.ADE_ULT_PERIODO_EXPORTACAO) != null ? (Date) ade.getAttribute(Columns.ADE_ULT_PERIODO_EXPORTACAO) : null;
                                final Date adeAnoMesIni = (Date) ade.getAttribute(Columns.ADE_ANO_MES_INI);
                                final Short adeIncMargem = (Short) ade.getAttribute(Columns.ADE_INC_MARGEM);

                                if ((TextHelper.isNull(adeUltPeriodoExportacao) || (adeUltPeriodoExportacao.compareTo(ultPeriodoRetorno) != 0) || (adeAnoMesIni.compareTo(adeUltPeriodoExportacao) <= 0) || (adeAnoMesIni.compareTo(ultPeriodoRetorno) <= 0) || adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO))) {
                                        resultadoFinal.add(ade);
                                    }
                            }

                            if((resultadoFinal != null) && !resultadoFinal.isEmpty()) {
                                resultado.clear();
                                resultado.addAll(resultadoFinal);
                            } else {
                                resultado.clear();
                            }
                        }

                    } catch (FindException | ImpRetornoControllerException | AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // PASSO 10) ADICIONA NA LISTA OS CONTRATOS QUE NÃO INCIDEM NA MARGEM PARA CONSTRUÇÃO DO CARD, ELES NÃO SERÃO USADOS PARA CALCULO
                if (ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, responsavel)) {
                    final List<MargemTO> lstMargem = consultarMargem(rseCodigo, null, null, null, true, true, responsavel);
                    boolean percentualMargemPreenchidos = true;
                    for (final MargemTO margemTO : lstMargem) {
                        if (TextHelper.isNull(margemTO.getMarPorcentagem())) {
                            percentualMargemPreenchidos = false;
                            break;
                        }
                    }
                    boolean exibeContratosNaoIncideMargem = false;
                    if (percentualMargemPreenchidos) {
                        try {
                            if (!TextHelper.isNull(rseCodigo)) {
                                final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, false, responsavel);
                                exibeContratosNaoIncideMargem = (responsavel.isSer() || responsavel.isSup()) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_SALARIO));
                            }
                        } catch (final ServidorControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }

                    if (exibeContratosNaoIncideMargem) {
                        final ListaExtratoMargemContratosNaoIncideMargemQuery query10 = new ListaExtratoMargemContratosNaoIncideMargemQuery(rseCodigo);
                        resultado.addAll(query10.executarDTO());
                    }
                }

                return resultado;
            }
        } catch (final ParseException ex) {
            throw new ServidorControllerException("mensagem.erro.datas.ultimo.periodo.incorretas", responsavel, ex);
        } catch (final DAOException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final PeriodoException e) {
            throw new RuntimeException(e);
        }
        return Collections.emptyList();
    }

    /**
     * Operação de consulta de margem cadastrada como margem limite por consignatária.
     * Retorna um objeto MargemTO preenchido de acordo com as configurações de exibição de margem limite.
     * @param rseCodigo : Código do registro servidor
     * @param csaCodigo
     * @param codMargemLimite : Código da margem que deve ser consultada
     * @param responsavel : Responsável pela consulta
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public MargemTO consultarMargemLimitePorCsa(String rseCodigo, String csaCodigo, Short codMargemLimite, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException {
        MargemTO margem = null;
        try {
            // verifica se a margem cadastrada no parâmetro existe
            final MargemTO marTO = MargemHelper.getInstance().getMargem(codMargemLimite, responsavel);
            // retorna null caso o código da margem limite seja zero ou um valor inválido
            if (codMargemLimite.equals(CodedValues.INCIDE_MARGEM_NAO) || (marTO == null)) {
                return null;
            }

            // Busca o registro servidor
            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);

            // recupera total de margem utilizada do servidor pela consignatária
            double margemUsadaCsa = 0;
            if (csaCodigo != null) {
                final ObtemTotalValorConsignacaoQuery queryMargemUsada = new ObtemTotalValorConsignacaoQuery();
                queryMargemUsada.rseCodigo = rseCodigo;
                queryMargemUsada.csaCodigo = csaCodigo;
                queryMargemUsada.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
                    queryMargemUsada.adeCodigosExceto = adeCodigosRenegociacao;
                }
                margemUsadaCsa = queryMargemUsada.executarSomatorio(BigDecimal.ZERO).doubleValue();
            }

            // Pesquisa os tipos de margem, juntamente com o valor para o registro servidor
            final ListaMargemRegistroServidorQuery queryMargemServidor = new ListaMargemRegistroServidorQuery();
            queryMargemServidor.rseCodigo = rseCodigo;
            queryMargemServidor.marCodigo = codMargemLimite;
            // Na consulta de limite de margem por consignatária, retorna as margens mesmo que não possuam
            // convênio ativo, já que são utilizadas para validações.
            queryMargemServidor.temConvenioAtivo = false;

            // recupera o limite de margem do servidor por consignatária
            final List<MargemTO> lstMargens = queryMargemServidor.executarDTO(MargemTO.class);
            for (final MargemTO lstMargen : lstMargens) {
                margem = lstMargen;
                final Short codigoMargem = margem.getMarCodigo();
                // prioriza dados da tabela de registro servidor
                if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    margem.setMrsMargem(registroServidor.getRseMargem());
                } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    margem.setMrsMargem(registroServidor.getRseMargem2());
                } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    margem.setMrsMargem(registroServidor.getRseMargem3());
                }
            }
            // informa como margem usada o valor total dos contratos ativos da consignatária
            margem.setMrsMargemUsada(new BigDecimal(margemUsadaCsa));
            // subtrai da margem restante o valor total dos contratos ativos da consignatária
            margem.setMrsMargemRest(margem.getMrsMargem().subtract(new BigDecimal(margemUsadaCsa)));

            return margem;

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista as natureza de serviço que incidem na margem de código <marCodigo>
     * @param marCodigo
     * @param responsavel
     * @return
     * @throws MargemControllerException
     */
    @Override
    public List<TransferObject> lstMargemNatureza(String marCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaMargemNaturezaServicoQuery margens = new ListaMargemNaturezaServicoQuery();
            margens.marCodigo = marCodigo;

            return margens.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a margem registro servidor do mar_codigo passado se existir.
     * @param rseCodigo
     * @param marCodigo
     * @param responsavel
     * @throws MargemControllerException
     */
    @Override
    public MargemRegistroServidor getMargemRegistroServidor(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException{
        MargemRegistroServidor margemRegistroServidor = null;
        if ((marCodigo != null)
                && !marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)
                && !marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)
                && !marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)
                && !marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            try {
                final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(marCodigo, rseCodigo);
                margemRegistroServidor = MargemRegistroServidorHome.findByPrimaryKey(mrsPK);
            } catch (final FindException ex) {
                LOG.warn("Registro de margem " + marCodigo + " não encontrado para o registro servidor " + rseCodigo);
            }
        }
        return margemRegistroServidor;
    }

    /**
     * Retorna o registro servidor com o valor da margem atualizado
     * @param url
     * @param RegistroServidorTO
     * @param responsavel
     * @throws MargemControllerException
     */
    @Override
    public RegistroServidorTO atualizaMargemServidorExternoRegistroServidor(String url, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
            final ServidorTransferObject servidor = servidorController.findServidor(registroServidor.getSerCodigo(), responsavel);
            boolean atualizarMargem = false;

            final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            final HashMap<String, String> body = new HashMap<>();
            body.put("apiKey", cse.getCseIdentificadorInterno());
            body.put("register", "CPF="+servidor.getSerCpf().replace(".", "").replace("-", ""));

            final JSONObject jsonObject = new JSONObject(body);

            final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
            final ResponseEntity<String> response = restTemplate.exchange(url+"api/margem/v1", HttpMethod.POST, httpEntity, String.class);

            if((response == null) || ((response.getStatusCode() != HttpStatus.NOT_FOUND) && (response.getStatusCode() != HttpStatus.OK))) {
                throw new ServidorControllerException("mensagem.consultar.margem.sistema.externo.erro", responsavel, registroServidor.getRseMatricula());
            }

            if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
                registroServidor.setSrsCodigo(CodedValues.SRS_BLOQUEADO);
                atualizarMargem = true;
            } else {
                final JSONParser parser = new JSONParser();
                final JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());

                final BigDecimal rseMargem = jsonResponse.get("RSE_MARGEM") != null ? new BigDecimal(String.valueOf(jsonResponse.get("RSE_MARGEM"))) : BigDecimal.ZERO;
                final BigDecimal rseMargem2 = jsonResponse.get("RSE_MARGEM_2") != null ? new BigDecimal(String.valueOf(jsonResponse.get("RSE_MARGEM_2"))) : BigDecimal.ZERO;
                final BigDecimal rseMargem3 = jsonResponse.get("RSE_MARGEM_3") != null ? new BigDecimal(String.valueOf(jsonResponse.get("RSE_MARGEM_3"))) : BigDecimal.ZERO;

        		if ((TextHelper.isNull(registroServidor.getRseMargem()) || (registroServidor.getRseMargem().compareTo(rseMargem) != 0)) ||
    				(TextHelper.isNull(registroServidor.getRseMargem2()) || (registroServidor.getRseMargem2().compareTo(rseMargem2) != 0)) ||
    				(TextHelper.isNull(registroServidor.getRseMargem3()) || (registroServidor.getRseMargem3().compareTo(rseMargem3) != 0))) {
                    registroServidor.setRseMargem(rseMargem);
                    registroServidor.setRseMargem2(rseMargem2);
                    registroServidor.setRseMargem3(rseMargem3);

                    atualizarMargem = true;
                }
            }

            if(atualizarMargem) {
                servidorController.updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);

                final List<String> rseCodigos = new ArrayList<>();
                rseCodigos.add(registroServidor.getRseCodigo());
                margemController.recalculaMargemComHistorico("RSE", rseCodigos , responsavel);

                return servidorController.findRegistroServidor(registroServidor.getRseCodigo(), responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.consultar.margem.sistema.externo.erro", responsavel, registroServidor.getRseMatricula());
        }
        return registroServidor;
    }
}
