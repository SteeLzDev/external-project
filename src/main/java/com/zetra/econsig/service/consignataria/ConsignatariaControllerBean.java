package com.zetra.econsig.service.consignataria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.GrupoConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CRMException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailCsaAjustaConsignacoesMargem;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Agendamento;
import com.zetra.econsig.persistence.entity.AgendamentoHome;
import com.zetra.econsig.persistence.entity.AnexoConsignataria;
import com.zetra.econsig.persistence.entity.AnexoConsignatariaHome;
import com.zetra.econsig.persistence.entity.AnexoCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.AnexoCredenciamentoCsaHome;
import com.zetra.econsig.persistence.entity.Cidade;
import com.zetra.econsig.persistence.entity.CidadeHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenhaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenioHome;
import com.zetra.econsig.persistence.entity.CorrespondenteHome;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.persistence.entity.CredenciamentoCsaHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsa;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaId;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaSvcHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaSvcId;
import com.zetra.econsig.persistence.entity.EmpresaCorrespondente;
import com.zetra.econsig.persistence.entity.EmpresaCorrespondenteHome;
import com.zetra.econsig.persistence.entity.EnderecoConsignataria;
import com.zetra.econsig.persistence.entity.EnderecoConsignatariaHome;
import com.zetra.econsig.persistence.entity.FuncaoPerfilMasterNca;
import com.zetra.econsig.persistence.entity.FuncaoPerfilMasterNcaHome;
import com.zetra.econsig.persistence.entity.InformacaoCsaServidor;
import com.zetra.econsig.persistence.entity.InformacaoCsaServidorHome;
import com.zetra.econsig.persistence.entity.LimiteMargemCsaOrg;
import com.zetra.econsig.persistence.entity.ModeloTermoAditivo;
import com.zetra.econsig.persistence.entity.ModeloTermoAditivoHome;
import com.zetra.econsig.persistence.entity.ModeloTermoTag;
import com.zetra.econsig.persistence.entity.ModeloTermoTagHome;
import com.zetra.econsig.persistence.entity.NaturezaConsignataria;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignataria;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignatariaHome;
import com.zetra.econsig.persistence.entity.OcorrenciaCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.OcorrenciaCredenciamentoCsaHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusCredenciamento;
import com.zetra.econsig.persistence.entity.StatusCredenciamentoHome;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.persistence.entity.TipoEnderecoHome;
import com.zetra.econsig.persistence.entity.TipoGrupoConsignataria;
import com.zetra.econsig.persistence.entity.TipoGrupoConsignatariaHome;
import com.zetra.econsig.persistence.entity.TipoMotivoBloqueio;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.entity.VinculoConsignatariaHome;
import com.zetra.econsig.persistence.entity.VinculoCsaRse;
import com.zetra.econsig.persistence.entity.VinculoCsaRseHome;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidorHome;
import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNcaCodigosQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoOcorrenciaPeriodoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoUsuCsaCorSemAnexosMinQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignatariasPortabilidadeCartaoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListCsaTaxaJurosLiberadaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaBloqueioCsaCoeficienteExpiradoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaBloqueioCsaTaxaJurosExpiradoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaAExpirarQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaComAdeSerQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaConvenioQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaIdentificadorQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaSaldoDevedorServidorQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCredenciamentoConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoDesbloqueadoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoExpiradoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteBloqueadoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaConvenioSvcComTpsCodigoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaPenalidadeExpiradaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaPermiteContatoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaPrazoDesbloqAutomaticoPassadoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaTaxaJurosDiasAntesExpiradoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsasComAdeRenegociaveisQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsasComAdeStatusAguardoByRseCodigoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaEnderecosConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaGrupoConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaInformacaoCsaServidorQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaLimiteMargemCsaOrgByCsaCodigoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaNaturezaConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaOcorrenciaConsignatariaQuery;
import com.zetra.econsig.persistence.query.consignataria.ObtemAnexoConsignatariaQuery;
import com.zetra.econsig.persistence.query.correspondente.ListaAssociacaoEmpresaCorrespondenteQuery;
import com.zetra.econsig.persistence.query.correspondente.ListaCorrespondenteConvenioQuery;
import com.zetra.econsig.persistence.query.correspondente.ListaCorrespondentesQuery;
import com.zetra.econsig.persistence.query.correspondente.ListaEmpresaCorrespondenteQuery;
import com.zetra.econsig.persistence.query.destinatarioemailcsasvc.ListaDestinatarioEmailCsaSvcQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailCsaQuery;
import com.zetra.econsig.persistence.query.modelotermoaditivo.ListaCodTituloModeloTermoAditivoQuery;
import com.zetra.econsig.persistence.query.ocorrenciaconsignataria.ListaOccBloqDesbloqVinculosByCsaQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoCsaCoeficienteExpiradoQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemPapelUsuarioQuery;
import com.zetra.econsig.persistence.query.vinculo.ListaConsignatariasExisteCnvVinculoRegistroServidorQuery;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.values.StatusCredenciamentoEnum;
import com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum;
import com.zetra.econsig.values.TipoMotivoBloqueioEnum;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webclient.crm.CRMClient;

/**
 * <p>Title: ConsignatariaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConsignatariaControllerBean implements ConsignatariaController {
    private static final String MENSAGEM_ERRO_NAO_POSSIVEL_RETORNAR_LISTA_CONSIGNATARIAS = "mensagem.erro.nao.possivel.retornar.lista.consignatarias";

    private static final String INVALID_ARGUMENT_VALUE = "Invalid argument value";

    private static final String MENSAGEM_INFORME_CONSIGNATARIA_QUE_SERA_PENALIZADA = "mensagem.informe.consignataria.que.sera.penalizada";

    private static final String MENSAGEM_ERRO_CORRESPONDENTE_NAO_ENCONTRADO = "mensagem.erro.correspondente.nao.encontrado";

    private static final String MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO = "mensagem.erro.periodo.parse.invalido";

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String YYYY_MM_DD_00_00_00 = "yyyy-MM-dd 00:00:00";

    private static final String YYYY_MM_DD_23_59_59 = "yyyy-MM-dd 23:59:59";

    private static final String MENSAGEM_ERRO_INTERNO_SISTEMA = "mensagem.erroInternoSistema";

    private static final String MENSAGEM_ERRO_CONSIGNATARIA_FOI_BLOQUEADA_AUTOMATICAMENTE_PELO_SISTEMA_POR_MOTIVO = "mensagem.erro.consignataria.foi.bloqueada.automaticamente.pelo.sistema.por.motivo";

    private static final int TAMANHO_MSG_ERRO_DEFAULT = 100;

    private static final String COMPLEMENTO_DEFAULT = " ";

    private static final String LINHA_INVALIDA = "LINHA_INVALIDA";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignatariaControllerBean.class);

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private CorrespondenteController correspondenteController;

    @Autowired
    private CRMClient crmClient;

    @Autowired
    private ServidorController servidorController;

    // Correspondente
    @Override
    public CorrespondenteTransferObject findCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return setCorrespondenteValues(findCorrespondenteBean(correspondente, responsavel));
    }

    @Override
    public CorrespondenteTransferObject findCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final CorrespondenteTransferObject criterio = new CorrespondenteTransferObject(corCodigo);
        return findCorrespondente(criterio, responsavel);
    }

    @Override
    public CorrespondenteTransferObject findCorrespondenteByIdn(String corIdentificador, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final CorrespondenteTransferObject criterio = new CorrespondenteTransferObject();
        criterio.setCorIdentificador(corIdentificador);
        criterio.setCsaCodigo(csaCodigo);
        return findCorrespondente(criterio, responsavel);
    }

    private Correspondente findCorrespondenteBean(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        Correspondente correspondenteBean = null;
        try {
            if (correspondente.getCorCodigo() != null) {
                correspondenteBean = CorrespondenteHome.findByPrimaryKey(correspondente.getCorCodigo());
            } else if ((correspondente.getCorIdentificador() != null) && (correspondente.getCsaCodigo() != null)) {
                correspondenteBean = CorrespondenteHome.findByIdn(correspondente.getCorIdentificador(), correspondente.getCsaCodigo());
            } else {
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_CORRESPONDENTE_NAO_ENCONTRADO, responsavel);
            }
        } catch (final FindException e) {
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_CORRESPONDENTE_NAO_ENCONTRADO, responsavel);
        }
        return correspondenteBean;
    }

    private CorrespondenteTransferObject setCorrespondenteValues(Correspondente correspondenteBean) {
        final CorrespondenteTransferObject correspondente = new CorrespondenteTransferObject(correspondenteBean.getCorCodigo());
        correspondente.setCsaCodigo(correspondenteBean.getConsignataria().getCsaCodigo());
        correspondente.setCsaNome(correspondenteBean.getConsignataria().getCsaNome());
        correspondente.setCsaNomeAbrev(correspondenteBean.getConsignataria().getCsaNomeAbrev());
        correspondente.setCsaIdentificadorInterno(correspondenteBean.getConsignataria().getCsaIdentificadorInterno());
        correspondente.setCsaCnpj(correspondenteBean.getConsignataria().getCsaCnpj());
        correspondente.setCorIdentificador(correspondenteBean.getCorIdentificador());
        correspondente.setCorAtivo(correspondenteBean.getCorAtivo());
        correspondente.setCorNome(correspondenteBean.getCorNome());
        correspondente.setCorEmail(correspondenteBean.getCorEmail());
        correspondente.setCorResponsavel(correspondenteBean.getCorResponsavel());
        correspondente.setCorResponsavel2(correspondenteBean.getCorResponsavel2());
        correspondente.setCorResponsavel3(correspondenteBean.getCorResponsavel3());
        correspondente.setCorRespCargo(correspondenteBean.getCorRespCargo());
        correspondente.setCorRespCargo2(correspondenteBean.getCorRespCargo2());
        correspondente.setCorRespCargo3(correspondenteBean.getCorRespCargo3());
        correspondente.setCorRespTelefone(correspondenteBean.getCorRespTelefone());
        correspondente.setCorRespTelefone2(correspondenteBean.getCorRespTelefone2());
        correspondente.setCorRespTelefone3(correspondenteBean.getCorRespTelefone3());
        correspondente.setCorLogradouro(correspondenteBean.getCorLogradouro());
        correspondente.setCorNro(correspondenteBean.getCorNro());
        correspondente.setCorCompl(correspondenteBean.getCorCompl());
        correspondente.setCorBairro(correspondenteBean.getCorBairro());
        correspondente.setCorCidade(correspondenteBean.getCorCidade());
        correspondente.setCorUf(correspondenteBean.getCorUf());
        correspondente.setCorCep(correspondenteBean.getCorCep());
        correspondente.setCorTel(correspondenteBean.getCorTel());
        correspondente.setCorFax(correspondenteBean.getCorFax());
        correspondente.setCorCnpj(correspondenteBean.getCorCnpj());
        correspondente.setCorIdentificadorAntigo(correspondenteBean.getCorIdentificadorAntigo());
        correspondente.setCorIPAcesso(correspondenteBean.getCorIpAcesso());
        correspondente.setCorDDNSAcesso(correspondenteBean.getCorDdnsAcesso());
        correspondente.setCorExigeEnderecoAcesso(correspondenteBean.getCorExigeEnderecoAcesso());
        correspondente.setEcoCodigo(correspondenteBean.getEmpresaCorrespondente() != null ? correspondenteBean.getEmpresaCorrespondente().getEcoCodigo() : null);

        return correspondente;
    }

    @Override
    public String createCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        String corCodigo = null;
        try {
            Correspondente corBean = null;
            try {
                // Verifica se já existe correspondente com mesmo na mesma consignatária
                corBean = CorrespondenteHome.findByIdn(correspondente.getCorIdentificador(), correspondente.getCsaCodigo());
            } catch (final FindException e) {
            }
            if (corBean != null) {
                throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.este.correspondente.existe.outro.mesmo.codigo.e.consignataria", responsavel);
            }
            final Correspondente correspondenteBean = CorrespondenteHome.create(correspondente.getCsaCodigo(), correspondente.getCorNome(), correspondente.getCorEmail(), correspondente.getCorResponsavel(),
                                                                                correspondente.getCorLogradouro(), correspondente.getCorNro(), correspondente.getCorCompl(), correspondente.getCorBairro(),
                                                                                correspondente.getCorCidade(), correspondente.getCorUf(), correspondente.getCorCep(), correspondente.getCorTel(), correspondente.getCorFax(),
                                                                                correspondente.getCorIdentificador(), correspondente.getCorAtivo(), correspondente.getCorResponsavel2(), correspondente.getCorResponsavel3(),
                                                                                correspondente.getCorRespCargo(), correspondente.getCorRespCargo2(), correspondente.getCorRespCargo3(),
                                                                                correspondente.getCorRespTelefone(), correspondente.getCorRespTelefone2(), correspondente.getCorRespTelefone3(),
                                                                                correspondente.getCorCnpj(), correspondente.getCorIdentificadorAntigo(), correspondente.getCorIPAcesso(),
                                                                                correspondente.getCorDDNSAcesso(), correspondente.getCorExigeEnderecoAcesso(), correspondente.getEcoCodigo());
            corCodigo = correspondenteBean.getCorCodigo();

            final List<Convenio> convenios = ConvenioHome.findByChave(correspondente.getCsaCodigo(), CodedValues.SCV_ATIVO);
            final Iterator<Convenio> ite = convenios.iterator();
            while (ite.hasNext()) {
                final String cnvCodigo = ite.next().getCnvCodigo();
                CorrespondenteConvenioHome.create(corCodigo, cnvCodigo, CodedValues.SCV_ATIVO);
            }

            // Cadastra empresa correspondente caso seja habilitado o cadastro no sistema
            cadastraEmpresaCorrespondente(correspondente.getEcoCodigo(), correspondenteBean.getCorCodigo(), correspondente, responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCorrespondente(corCodigo);
            log.setConsignataria(correspondente.getCsaCodigo());
            log.setEmpresaCorrespondente(correspondente.getEcoCodigo());
            log.getUpdatedFields(correspondente.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.este.correspondente.erro.interno", responsavel, ex.getMessage());
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ConsignatariaControllerException excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.este.correspondente.erro.interno", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf(INVALID_ARGUMENT_VALUE) != -1) {
                excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.este.correspondente.existe.outro.mesmo.codigo", responsavel);
            }
            throw excecao;
        }
        return corCodigo;
    }

    private void cadastraEmpresaCorrespondente(String ecoCodigo, String corCodigo, CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final boolean cadastraEmpCorrespondente = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);

        // Se possui cadastro de empresa correspondente, liga o novo correspondente à empresa pesquisada pelo usuário
        if (cadastraEmpCorrespondente) {
            if (!TextHelper.isNull(ecoCodigo)) {
                final CustomTransferObject filtro = new CustomTransferObject();
                filtro.setAttribute(Columns.ECO_CODIGO, ecoCodigo);
                final TransferObject empresaTO = findEmpresaCorrespondente(filtro, responsavel);
                empresaTO.setAttribute(Columns.COR_CODIGO, corCodigo);

                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAtributos(empresaTO.getAtributos());
                associaEmpresaCorrespondente(criterio, responsavel);
            } else {
                final CustomTransferObject novaEmpresaTO = new CustomTransferObject();
                novaEmpresaTO.setAttribute(Columns.ECO_NOME, correspondente.getCorNome());
                novaEmpresaTO.setAttribute(Columns.ECO_CNPJ, correspondente.getCorCnpj());
                novaEmpresaTO.setAttribute(Columns.ECO_EMAIL, correspondente.getCorEmail());
                novaEmpresaTO.setAttribute(Columns.ECO_RESPONSAVEL, correspondente.getCorResponsavel());
                novaEmpresaTO.setAttribute(Columns.ECO_RESPONSAVEL_2, correspondente.getCorResponsavel2());
                novaEmpresaTO.setAttribute(Columns.ECO_RESPONSAVEL_3, correspondente.getCorResponsavel3());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_CARGO, correspondente.getCorRespCargo());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_CARGO_2, correspondente.getCorRespCargo2());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_CARGO_3, correspondente.getCorRespCargo3());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_TELEFONE, correspondente.getCorRespTelefone());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_TELEFONE_2, correspondente.getCorRespTelefone2());
                novaEmpresaTO.setAttribute(Columns.ECO_RESP_TELEFONE_3, correspondente.getCorRespTelefone3());
                novaEmpresaTO.setAttribute(Columns.ECO_LOGRADOURO, correspondente.getCorLogradouro());
                novaEmpresaTO.setAttribute(Columns.ECO_NRO, correspondente.getCorNro());
                novaEmpresaTO.setAttribute(Columns.ECO_COMPL, correspondente.getCorCompl());
                novaEmpresaTO.setAttribute(Columns.ECO_BAIRRO, correspondente.getCorBairro());
                novaEmpresaTO.setAttribute(Columns.ECO_CIDADE, correspondente.getCorCidade());
                novaEmpresaTO.setAttribute(Columns.ECO_UF, correspondente.getCorUf());
                novaEmpresaTO.setAttribute(Columns.ECO_CEP, correspondente.getCorCep());
                novaEmpresaTO.setAttribute(Columns.ECO_TEL, correspondente.getCorTel());
                novaEmpresaTO.setAttribute(Columns.ECO_FAX, correspondente.getCorFax());
                novaEmpresaTO.setAttribute(Columns.ECO_IDENTIFICADOR, !TextHelper.isNull(correspondente.getCorCnpj()) ? correspondente.getCorCnpj().replaceAll("\\.|\\/|\\-", "") : correspondente.getCorIdentificador());
                novaEmpresaTO.setAttribute(Columns.ECO_ATIVO, !TextHelper.isNull(correspondente.getCorAtivo()) ? correspondente.getCorAtivo() : CodedValues.STS_ATIVO);

                String novoEcoCodigo = null;
                try {
                    novoEcoCodigo = createEmpresaCorrespondente(novaEmpresaTO, responsavel);
                } catch (final ConsignatariaControllerException csaex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    LOG.error(csaex.getMessage(), csaex);
                    throw csaex;
                }
                novaEmpresaTO.setAttribute(Columns.COR_CODIGO, corCodigo);
                novaEmpresaTO.setAttribute(Columns.ECO_CODIGO, novoEcoCodigo);

                associaEmpresaCorrespondente(novaEmpresaTO, responsavel);
            }
        }
    }

    @Override
    public void updateCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        updateCorrespondente(correspondente, false, null, null, responsavel);
    }

    @Override
    public void updateCorrespondente(CorrespondenteTransferObject correspondente, boolean bloquearDesbloquear, String tmoCodigo, String ocrObs, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final Correspondente correspondenteBean = findCorrespondenteBean(correspondente, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setCorrespondente(correspondenteBean.getCorCodigo());

            final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_COR);

            // Compara a versão do cache com a passada por parâmetro
            final CorrespondenteTransferObject correspondenteCache = setCorrespondenteValues(correspondenteBean);
            final CustomTransferObject merge = log.getUpdatedFields(correspondente.getAtributos(), correspondenteCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.COR_IDENTIFICADOR)) {

                // Verifica se não existe outro correspondente com o mesmo ID na mesma CSA
                final CorrespondenteTransferObject teste = new CorrespondenteTransferObject();
                teste.setCsaCodigo(correspondenteCache.getCsaCodigo());
                teste.setCorIdentificador((String) merge.getAttribute(Columns.COR_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findCorrespondenteBean(teste, responsavel);
                    existe = true;
                } catch (final ConsignatariaControllerException ex) {
                }
                if (existe) {
                    throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.alterar.este.correspondente.existe.outro.mesmo.codigo.e.consignataria", responsavel);
                }
                correspondenteBean.setCorIdentificador((String) merge.getAttribute(Columns.COR_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.COR_NOME)) {
                correspondenteBean.setCorNome((String) merge.getAttribute(Columns.COR_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.COR_EMAIL)) {
                correspondenteBean.setCorEmail((String) merge.getAttribute(Columns.COR_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESPONSAVEL)) {
                correspondenteBean.setCorResponsavel((String) merge.getAttribute(Columns.COR_RESPONSAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESPONSAVEL_2)) {
                correspondenteBean.setCorResponsavel2((String) merge.getAttribute(Columns.COR_RESPONSAVEL_2));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESPONSAVEL_3)) {
                correspondenteBean.setCorResponsavel3((String) merge.getAttribute(Columns.COR_RESPONSAVEL_3));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_CARGO)) {
                correspondenteBean.setCorRespCargo((String) merge.getAttribute(Columns.COR_RESP_CARGO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_CARGO_2)) {
                correspondenteBean.setCorRespCargo2((String) merge.getAttribute(Columns.COR_RESP_CARGO_2));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_CARGO_3)) {
                correspondenteBean.setCorRespCargo3((String) merge.getAttribute(Columns.COR_RESP_CARGO_3));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_TELEFONE)) {
                correspondenteBean.setCorRespTelefone((String) merge.getAttribute(Columns.COR_RESP_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_TELEFONE_2)) {
                correspondenteBean.setCorRespTelefone2((String) merge.getAttribute(Columns.COR_RESP_TELEFONE_2));
            }
            if (merge.getAtributos().containsKey(Columns.COR_RESP_TELEFONE_3)) {
                correspondenteBean.setCorRespTelefone3((String) merge.getAttribute(Columns.COR_RESP_TELEFONE_3));
            }
            if (merge.getAtributos().containsKey(Columns.COR_LOGRADOURO)) {
                correspondenteBean.setCorLogradouro((String) merge.getAttribute(Columns.COR_LOGRADOURO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_NRO)) {
                correspondenteBean.setCorNro((Integer) merge.getAttribute(Columns.COR_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_COMPL)) {
                correspondenteBean.setCorCompl((String) merge.getAttribute(Columns.COR_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.COR_BAIRRO)) {
                correspondenteBean.setCorBairro((String) merge.getAttribute(Columns.COR_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_CIDADE)) {
                correspondenteBean.setCorCidade((String) merge.getAttribute(Columns.COR_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.COR_UF)) {
                correspondenteBean.setCorUf((String) merge.getAttribute(Columns.COR_UF));
            }
            if (merge.getAtributos().containsKey(Columns.COR_CEP)) {
                correspondenteBean.setCorCep((String) merge.getAttribute(Columns.COR_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.COR_TEL)) {
                correspondenteBean.setCorTel((String) merge.getAttribute(Columns.COR_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.COR_FAX)) {
                correspondenteBean.setCorFax((String) merge.getAttribute(Columns.COR_FAX));
            }
            if (merge.getAtributos().containsKey(Columns.COR_IDENTIFICADOR)) {
                correspondenteBean.setCorIdentificador((String) merge.getAttribute(Columns.COR_IDENTIFICADOR));
            }
            if (merge.getAtributos().containsKey(Columns.COR_ATIVO)) {
                final Short corAtivoOld = correspondenteBean.getCorAtivo();
                final Short corAtivoNew = (Short) merge.getAttribute(Columns.COR_ATIVO);

                if (corAtivoOld.equals(CodedValues.STS_ATIVO) && corAtivoNew.equals(CodedValues.STS_INATIVO) && responsavel.isCseSup()) {
                    // Se está bloqueando, e é gestor, bloqueia com status diferenciado
                    correspondenteBean.setCorAtivo(CodedValues.STS_INATIVO_CSE);
                } else if (corAtivoOld.equals(CodedValues.STS_INATIVO_CSE) && corAtivoNew.equals(CodedValues.STS_ATIVO) && !responsavel.isCseSup()) {
                    // Se está desbloqueando, e foi bloqueado pelo gestor, não deixa a consignatária desbloqueá-lo
                    throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.desbloquear.correspondente.pois.foi.bloqueado.pelo.consignante", responsavel);
                } else if (corAtivoOld.equals(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA) && !responsavel.isSup()) {
                    // Se está desbloqueando, e foi bloqueado por segurança, não deixa desbloqueá-lo
                    throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.desbloquear.correspondenete.arg0.pois.foi.bloqueado.por.seguranca", responsavel, correspondenteBean.getCorNome());
                } else {
                    correspondenteBean.setCorAtivo(corAtivoNew);
                }

                if (bloquearDesbloquear) {
                    if (corAtivoOld.equals(CodedValues.STS_ATIVO) && !corAtivoNew.equals(CodedValues.STS_ATIVO)) {
                        ocrObs = !TextHelper.isNull(ocrObs) ? ocrObs : ApplicationResourcesHelper.getMessage("mensagem.informacao.correspondente.bloqueada.sistema", responsavel);
                        correspondenteController.createOcorrenciaCorrespondente(correspondenteBean.getCorCodigo(), CodedValues.TOC_CORRESPONDENTE_BLOQUEADA, ocrObs, tmoCodigo, responsavel);
                    } else {
                        ocrObs = !TextHelper.isNull(ocrObs) ? ocrObs : ApplicationResourcesHelper.getMessage("mensagem.informacao.correspondente.desbloqueada.sistema", responsavel);
                        correspondenteController.createOcorrenciaCorrespondente(correspondenteBean.getCorCodigo(), CodedValues.TOC_CORRESPONDENTE_DESBLOQUEADA, ocrObs, tmoCodigo, responsavel);
                    }
                }
            }
            if (merge.getAtributos().containsKey(Columns.COR_CNPJ)) {
                correspondenteBean.setCorCnpj((String) merge.getAttribute(Columns.COR_CNPJ));
            }
            if (merge.getAtributos().containsKey(Columns.COR_IDENTIFICADOR_ANTIGO)) {
                correspondenteBean.setCorIdentificadorAntigo((String) merge.getAttribute(Columns.COR_IDENTIFICADOR_ANTIGO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_IP_ACESSO) && podeEditarEnderecoAcesso) {
                correspondenteBean.setCorIpAcesso((String) merge.getAttribute(Columns.COR_IP_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_DDNS_ACESSO) && podeEditarEnderecoAcesso) {
                correspondenteBean.setCorDdnsAcesso((String) merge.getAttribute(Columns.COR_DDNS_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_EXIGE_ENDERECO_ACESSO)) {
                correspondenteBean.setCorExigeEnderecoAcesso((String) merge.getAttribute(Columns.COR_EXIGE_ENDERECO_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.COR_ECO_CODIGO)) {
                correspondenteBean.setEmpresaCorrespondente(merge.getAttribute(Columns.COR_ECO_CODIGO) != null ? EmpresaCorrespondenteHome.findByPrimaryKey((String) merge.getAttribute(Columns.COR_ECO_CODIGO)) : null);
                if (merge.getAttribute(Columns.COR_ECO_CODIGO) != null) {
                    log.setEmpresaCorrespondente((String) merge.getAttribute(Columns.COR_ECO_CODIGO));
                }
            }

            AbstractEntityHome.update(correspondenteBean);

            if (merge.getAtributos().containsKey(Columns.COR_CNPJ)) {
                // Cadastra empresa correspondente caso seja habilitado o cadastro no sistema

                String ecoCodigo = "";
                try {
                    final EmpresaCorrespondente eco = EmpresaCorrespondenteHome.findByCNPJ((String) merge.getAttribute(Columns.COR_CNPJ));
                    ecoCodigo = eco.getEcoCodigo();
                } catch (final FindException e) {
                }
                if (((TextHelper.isNull(ecoCodigo) || (correspondenteBean.getEmpresaCorrespondente() == null)) || (!TextHelper.isNull(correspondenteBean.getEmpresaCorrespondente().getEcoCodigo()) && !ecoCodigo.equals(correspondenteBean.getEmpresaCorrespondente().getEcoCodigo())))) {
                    cadastraEmpresaCorrespondente(ecoCodigo, correspondenteBean.getCorCodigo(), correspondente, responsavel);
                }
            }

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException e) {
            throw new ConsignatariaControllerException(e);
        } catch (final UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public void removeCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final Correspondente correspondenteBean = findCorrespondenteBean(correspondente, responsavel);
            final String corCodigo = correspondenteBean.getCorCodigo();
            correspondenteBean.setCorAtivo(CodedValues.STS_INDISP);

            // Foi inserido o NextId para permitir o reuso do identificador antigo por novos correspondentes
            correspondenteBean.setCorIdentificadorAntigo(correspondenteBean.getCorIdentificador());
            correspondenteBean.setCorIdentificador(DBHelper.getNextId());

            AbstractEntityHome.update(correspondenteBean);

            final LogDelegate log = new LogDelegate(responsavel, Log.CORRESPONDENTE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setCorrespondente(corCodigo);
            log.write();

        } catch (LogControllerException | MissingPrimaryKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        } catch (final UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstCorrespondentes(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return lstCorrespondentes(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstCorrespondentes(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCorrespondentesQuery query = new ListaCorrespondentesQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.corAtivo = criterio.getAttribute(Columns.COR_ATIVO);
                query.corIdentificador = (String) criterio.getAttribute(Columns.COR_IDENTIFICADOR);
                query.corNome = (String) criterio.getAttribute(Columns.COR_NOME);
                query.csaCodigo = (String) criterio.getAttribute(Columns.COR_CSA_CODIGO);
                query.ecoCodigo = (String) criterio.getAttribute(Columns.COR_ECO_CODIGO);
            }

            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public int countCorrespondentes(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCorrespondentesQuery query = new ListaCorrespondentesQuery();
            query.count = true;

            if (criterio != null) {
                query.corAtivo = criterio.getAttribute(Columns.COR_ATIVO);
                query.corIdentificador = (String) criterio.getAttribute(Columns.COR_IDENTIFICADOR);
                query.corNome = (String) criterio.getAttribute(Columns.COR_NOME);
                query.csaCodigo = (String) criterio.getAttribute(Columns.COR_CSA_CODIGO);

            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public List<Correspondente> findCorrespondenteByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return CorrespondenteHome.findByCsa(csaCodigo);
        } catch (final FindException e) {
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_CORRESPONDENTE_NAO_ENCONTRADO, responsavel);
        }
    }

    // Consignataria
    @Override
    public ConsignatariaTransferObject findConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return setConsignatariaValues(findConsignatariaBean(consignataria));
    }

    @Override
    public ConsignatariaTransferObject findConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ConsignatariaTransferObject criterio = new ConsignatariaTransferObject(csaCodigo);
        return findConsignataria(criterio, responsavel);
    }

    @Override
    public ConsignatariaTransferObject findConsignatariaByIdn(String csaIdentificador, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ConsignatariaTransferObject criterio = new ConsignatariaTransferObject();
        criterio.setCsaIdentificador(csaIdentificador);
        return findConsignataria(criterio, responsavel);
    }

    @Override
    public ConsignatariaTransferObject findConsignatariaByCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return setConsignatariaValues(ConsignatariaHome.findByCorrespondente(corCodigo));
        } catch (final FindException ex) {
            throw new ConsignatariaControllerException("mensagem.erro.consignataria.nao.encontrada", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<Consignataria> findConsignatariaComEmailCadastrado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return ConsignatariaHome.findByEmailCadastrado();
        } catch (final FindException ex) {
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private Consignataria findConsignatariaBean(ConsignatariaTransferObject consignataria) throws ConsignatariaControllerException {
        Consignataria consignatariaBean = null;
        try {
            if (consignataria.getCsaCodigo() != null) {
                consignatariaBean = ConsignatariaHome.findByPrimaryKey(consignataria.getCsaCodigo());
            } else if (consignataria.getCsaIdentificador() != null) {
                consignatariaBean = ConsignatariaHome.findByIdn(consignataria.getCsaIdentificador());
            } else {
                throw new ConsignatariaControllerException("mensagem.erro.consignataria.nao.encontrada", (AcessoSistema) null);
            }
        } catch (final FindException e) {
            throw new ConsignatariaControllerException("mensagem.erro.consignataria.nao.encontrada", (AcessoSistema) null);
        }
        return consignatariaBean;
    }

    private ConsignatariaTransferObject setConsignatariaValues(Consignataria consignatariaBean) {
        final ConsignatariaTransferObject consignataria = new ConsignatariaTransferObject(consignatariaBean.getCsaCodigo());
        consignataria.setCsaNroAge(consignatariaBean.getCsaNroAge());
        consignataria.setCsaDigCta(consignatariaBean.getCsaDigCta());
        consignataria.setCsaAtivo(consignatariaBean.getCsaAtivo());
        consignataria.setCsaIdentificador(consignatariaBean.getCsaIdentificador());
        consignataria.setCsaNome(consignatariaBean.getCsaNome());
        consignataria.setCsaCnpj(consignatariaBean.getCsaCnpj());
        consignataria.setCsaCnpjCta(consignatariaBean.getCsaCnpjCta());
        consignataria.setCsaEmail(consignatariaBean.getCsaEmail());
        consignataria.setCsaEmailExpiracao(consignatariaBean.getCsaEmailExpiracao());
        consignataria.setCsaEmailDesbloqueio(consignatariaBean.getCsaEmailDesbloqueio());
        consignataria.setCsaResponsavel(consignatariaBean.getCsaResponsavel());
        consignataria.setCsaResponsavel2(consignatariaBean.getCsaResponsavel2());
        consignataria.setCsaResponsavel3(consignatariaBean.getCsaResponsavel3());
        consignataria.setCsaRespCargo(consignatariaBean.getCsaRespCargo());
        consignataria.setCsaRespCargo2(consignatariaBean.getCsaRespCargo2());
        consignataria.setCsaRespCargo3(consignatariaBean.getCsaRespCargo3());
        consignataria.setCsaRespTelefone(consignatariaBean.getCsaRespTelefone());
        consignataria.setCsaRespTelefone2(consignatariaBean.getCsaRespTelefone2());
        consignataria.setCsaRespTelefone3(consignatariaBean.getCsaRespTelefone3());
        consignataria.setCsaLogradouro(consignatariaBean.getCsaLogradouro());
        consignataria.setCsaNro(consignatariaBean.getCsaNro());
        consignataria.setCsaCompl(consignatariaBean.getCsaCompl());
        consignataria.setCsaBairro(consignatariaBean.getCsaBairro());
        consignataria.setCsaCidade(consignatariaBean.getCsaCidade());
        consignataria.setCsaUf(consignatariaBean.getCsaUf());
        consignataria.setCsaCep(consignatariaBean.getCsaCep());
        consignataria.setCsaTel(consignatariaBean.getCsaTel());
        consignataria.setCsaFax(consignatariaBean.getCsaFax());
        consignataria.setCsaNroBco(consignatariaBean.getCsaNroBco());
        consignataria.setCsaNroCta(consignatariaBean.getCsaNroCta());
        consignataria.setCsaTxtContato(consignatariaBean.getCsaTxtContato());
        consignataria.setCsaContato(consignatariaBean.getCsaContato());
        consignataria.setCsaContatoTel(consignatariaBean.getCsaContatoTel());
        consignataria.setCsaEndereco2(consignatariaBean.getCsaEndereco2());
        consignataria.setCsaNomeAbreviado(consignatariaBean.getCsaNomeAbrev());
        consignataria.setTgcCodigo(consignatariaBean.getTipoGrupoConsignataria() != null ? consignatariaBean.getTipoGrupoConsignataria().getTgcCodigo() : null);
        consignataria.setCsaIdentificadorInterno(consignatariaBean.getCsaIdentificadorInterno());
        consignataria.setCsaDataExpiracao(consignatariaBean.getCsaDataExpiracao());
        consignataria.setCsaDataExpiracaoCadastral(consignatariaBean.getCsaDataExpiracaoCadastral());
        consignataria.setCsaNroContrato(consignatariaBean.getCsaNroContrato());
        consignataria.setCsaIPAcesso(consignatariaBean.getCsaIpAcesso());
        consignataria.setCsaDDNSAcesso(consignatariaBean.getCsaDdnsAcesso());
        consignataria.setCsaExigeEnderecoAcesso(consignatariaBean.getCsaExigeEnderecoAcesso());
        consignataria.setCsaProjetoInadimplencia(consignatariaBean.getCsaProjetoInadimplencia());
        consignataria.setCsaUnidadeOrganizacional(consignatariaBean.getCsaUnidadeOrganizacional());
        consignataria.setCsaNroContratoZetra(consignatariaBean.getCsaNroContratoZetra());
        consignataria.setCsaInstrucaoAnexo(consignatariaBean.getCsaInstrucaoAnexo());
        consignataria.setCsaNcaNatureza(consignatariaBean.getNaturezaConsignataria() != null ? consignatariaBean.getNaturezaConsignataria().getNcaCodigo() : null);
        consignataria.setCsaPermiteIncluirAde(consignatariaBean.getCsaPermiteIncluirAde());
        consignataria.setCsaDataAtualizacaoCadastral(consignatariaBean.getCsaDataAtualizacaoCadastral());
        consignataria.setCsaCodigoAns(!TextHelper.isNull(consignatariaBean.getCsaCodigoAns()) ? consignatariaBean.getCsaCodigoAns() : null);
        consignataria.setCsaEmailProjInadimplencia(!TextHelper.isNull(consignatariaBean.getCsaEmailProjInadimplencia()) ? consignatariaBean.getCsaEmailProjInadimplencia() : null);
        consignataria.setCsaDataDesbloqAutomatico(consignatariaBean.getCsaDataDesbloqAutomatico());
        consignataria.setTmbCodigo(!TextHelper.isNull(consignatariaBean.getTipoMotivoBloqueio()) ? consignatariaBean.getTipoMotivoBloqueio().getTmbCodigo() : null);
        consignataria.setCsaNumeroProcessoContrato(consignatariaBean.getCsaNumContrato());
        consignataria.setCsaDataInicioContrato(consignatariaBean.getCsaDataIniContrato());
        consignataria.setCsaDataRenovacaoContrato(consignatariaBean.getCsaDataRenovacaoContrato());
        consignataria.setCsaObsContrato(consignatariaBean.getCsaObsContrato());
        consignataria.setCsaPermiteApi(consignatariaBean.getCsaPermiteApi());
        consignataria.setCsaEmailContato(consignatariaBean.getCsaEmailContato());
        consignataria.setCsaWhatsapp(consignatariaBean.getCsaWhatsapp());
        consignataria.setCsaConsultaMargemSemSenha(consignatariaBean.getCsaConsultaMargemSemSenha());
        consignataria.setCsaEmailNotificacaoRco(consignatariaBean.getCsaEmailNotificacaoRco());
        return consignataria;
    }

    @Override
    public String createConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        String csaCodigo = null;
        try {
            final Consignataria csaBean = getConsignatariaBean(consignataria);
            final boolean habilitaAmbienteDeTestes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel);

            if (csaBean != null) {
                throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.esta.consignataria.existe.outra.mesmo.codigo", responsavel);
            }
            if (TextHelper.isNull(consignataria.getCsaNcaNatureza())) {
                if (habilitaAmbienteDeTestes) {
                    consignataria.setCsaNcaNatureza(NaturezaConsignatariaEnum.OUTROS.getCodigo());
                } else {
                    throw new ConsignatariaControllerException("mensagem.erro.natureza.consignataria.deve.ser.informada", responsavel);
                }
            }

            final Consignataria consignatariaBean = ConsignatariaHome.create(consignataria.getCsaIdentificador(), consignataria.getCsaNome(), consignataria.getCsaEmail(), consignataria.getCsaCnpj(), consignataria.getCsaCnpjCta(), consignataria.getCsaResponsavel(), consignataria.getCsaLogradouro(), consignataria.getCsaNro(), consignataria.getCsaCompl(), consignataria.getCsaBairro(), consignataria.getCsaCidade(), consignataria.getCsaUf(), consignataria.getCsaCep(), consignataria.getCsaTel(), consignataria.getCsaFax(), consignataria.getCsaNroBco(), consignataria.getCsaNroCta(),
                                                                             consignataria.getCsaNroAge(), consignataria.getCsaDigCta(), consignataria.getCsaAtivo(), consignataria.getCsaResponsavel2(), consignataria.getCsaResponsavel3(), consignataria.getCsaRespCargo(), consignataria.getCsaRespCargo2(), consignataria.getCsaRespCargo3(), consignataria.getCsaRespTelefone(), consignataria.getCsaRespTelefone2(), consignataria.getCsaRespTelefone3(), consignataria.getCsaTxtContato(), consignataria.getCsaContato(), consignataria.getCsaContatoTel(), consignataria.getCsaEndereco2(),
                                                                             consignataria.getCsaNomeAbreviado(), consignataria.getTgcCodigo(),
                                                                             consignataria.getCsaIdentificadorInterno(), consignataria.getCsaDataExpiracao(), consignataria.getCsaNroContrato(), consignataria.getCsaIPAcesso(), consignataria.getCsaDDNSAcesso(), consignataria.getCsaExigeEnderecoAcesso(), consignataria.getCsaUnidadeOrganizacional(), consignataria.getCsaNroContratoZetra(), consignataria.getCsaNcaNatureza(), consignataria.getCsaProjetoInadimplencia(), consignataria.getCsaEmailExpiracao(), consignataria.getCsaDataExpiracaoCadastral(), consignataria.getCsaInstrucaoAnexo(),
                                                                             consignataria.getCsaPermiteIncluirAde(),
                                                                             consignataria.getCsaCodigoAns(), consignataria.getCsaEmailProjInadimplencia(), consignataria.getCsaNumeroProcessoContrato(), consignataria.getCsaDataInicioContrato(), consignataria.getCsaDataRenovacaoContrato(), consignataria.getCsaObsContrato(), consignataria.getCsaPermiteApi(), consignataria.getCsaWhatsapp(), consignataria.getCsaEmailContato(), consignataria.getCsaConsultaMargemSemSenha(), consignataria.getCsaEmailNotificacaoRco());

            csaCodigo = consignatariaBean.getCsaCodigo();

            LogDelegate log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.CREATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.setGrupoConsignataria(consignataria.getTgcCodigo());
            log.setNaturezaConsignataria(consignataria.getCsaNcaNatureza());
            log.getUpdatedFields(consignataria.getAtributos(), null);
            log.write();

            createUsuarioMasterConsignataria(consignatariaBean, responsavel);

            if (responsavel.isSup() && ParamSist.getBoolParamSist(CodedValues.TPC_MANUTENCAO_CSA_UTILIZA_CRM, responsavel) && (!TextHelper.isNull(consignataria.getCsaCnpj()) || !TextHelper.isNull(consignataria.getCsaIdentificadorInterno()))) {
                final String csaIdentificadorInterno = consignataria.getCsaIdentificadorInterno();
                if (!TextHelper.isNull(csaIdentificadorInterno) && csaIdentificadorInterno.startsWith("ZZ")) {
                    final Integer idInterno = TextHelper.isNum(csaIdentificadorInterno.substring(2)) ? Integer.parseInt(csaIdentificadorInterno.substring(2).toString()) : null;
                    if (!TextHelper.isNull(idInterno) && (idInterno.compareTo(80000) >= 0)) {
                        throw new CRMException("mensagem.codigo.zetra.outro.maximo", responsavel);
                    }
                }

                final String csaIdInterno = crmClient.updateServiceProvider(consignatariaBean.getCsaNome(), consignataria.getCsaCnpj(), csaIdentificadorInterno);
                if (!TextHelper.isNull(csaIdInterno) &&
                    (TextHelper.isNull(consignatariaBean.getCsaIdentificadorInterno()) || !csaIdInterno.equals(consignatariaBean.getCsaIdentificadorInterno()))) {

                    consignatariaBean.setCsaIdentificadorInterno(csaIdInterno);
                    AbstractEntityHome.update(consignatariaBean);

                    log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setConsignataria(csaCodigo);
                    log.add(Columns.CSA_IDENTIFICADOR_INTERNO, csaIdInterno);
                    log.write();
                }
            }

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException | UpdateException | CRMException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ConsignatariaControllerException excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.esta.consignataria.erro.interno", responsavel, ex.getMessage());
            final String mensagem = ex.getMessage();
            if ((mensagem != null) && (mensagem.indexOf(INVALID_ARGUMENT_VALUE) != -1)) {
                excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.esta.consignataria.existe.outra.mesmo.codigo", responsavel);
            }
            throw excecao;
        }
        return csaCodigo;
    }

    private Consignataria getConsignatariaBean(ConsignatariaTransferObject consignataria) {
        try {
            // Verifica se já existe consignatária com mesmo identificador
            return ConsignatariaHome.findByIdn(consignataria.getCsaIdentificador());
        } catch (final FindException ex) {
            // Não há necessidade de imprimir o log
            // LOG.error(ex.getMessage(), ex);
        }

        return null;
    }

    private String createUsuarioMasterConsignataria(Consignataria csaBean, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<String> funcoesPerfilMaster = new ArrayList<>();

            // Codigos de grupos de funções não desejadas para o perfíl além das listadas no método lstFuncoesPermitidasPerfil
            final Set<String> grfCodigoNaoPermitido = new HashSet<>(Arrays.asList(CodedValues.GRUPO_FUNCAO_ADMINISTRADOR));

            // DESENV-12656 - quando é definido uma lista de funções para usuário Master de CSA para a natureza desta
            List<FuncaoPerfilMasterNca> funcaoMasterNcaList = null;
            try {
                funcaoMasterNcaList = FuncaoPerfilMasterNcaHome.findByNcaCodigo(csaBean.getNaturezaConsignataria().getNcaCodigo());
            } catch (final FindException ex) {
                LOG.info(ex.getMessage(), ex);
            }

            // Lista as permissões permitidas baseado em natureza, entre outras coisas
            final List<TransferObject> funcoesPermitidas = usuarioController.lstFuncoesPermitidasPerfil(AcessoSistema.ENTIDADE_CSA, csaBean.getCsaCodigo(), responsavel);
            for (final TransferObject funcaoPermitida : funcoesPermitidas) {
                final String funCodigo = funcaoPermitida.getAttribute(Columns.FUN_CODIGO).toString();
                final String grfCodigo = funcaoPermitida.getAttribute(Columns.FUN_GRF_CODIGO).toString();
                // Descartando os grupos indesejados e função de auditor (pois é obrigatório e-mail)
                if (!grfCodigoNaoPermitido.contains(grfCodigo) && !CodedValues.FUN_USUARIO_AUDITOR.equals(funCodigo)) {
                    funcoesPerfilMaster.add(funCodigo);
                }
            }

            if ((funcaoMasterNcaList != null) && !funcaoMasterNcaList.isEmpty()) {
                if (!funcoesPerfilMaster.isEmpty()) {
                    final List<FuncaoPerfilMasterNca> funMasterNcaFiltredLst = funcaoMasterNcaList.stream().filter(funMasterNca -> funcoesPerfilMaster.contains(funMasterNca.getFuncao().getFunCodigo())).collect(Collectors.toList());
                    funcoesPerfilMaster.clear();
                    funcoesPerfilMaster.addAll(funMasterNcaFiltredLst.stream().map(funMasterNca -> funMasterNca.getFuncao().getFunCodigo()).collect(Collectors.toList()));
                } else {
                    funcoesPerfilMaster.addAll(funcaoMasterNcaList.stream().map(funMasterNca -> funMasterNca.getFuncao().getFunCodigo()).collect(Collectors.toList()));
                }
            }

            final String perfil = usuarioController.createPerfil(AcessoSistema.ENTIDADE_CSA, csaBean.getCsaCodigo(), "MASTER", null, null, null, null, null, funcoesPerfilMaster, null, null, responsavel);

            final java.sql.Date data = DateHelper.toSQLDate(new Date());

            final String usuSenha = null;

            final UsuarioTransferObject usuarioTransferObject = new UsuarioTransferObject();
            usuarioTransferObject.setStuCodigo(CodedValues.STU_BLOQUEADO);
            usuarioTransferObject.setUsuLogin("MASTER" + csaBean.getCsaIdentificador());
            usuarioTransferObject.setUsuSenha(usuSenha);
            usuarioTransferObject.setUsuSenha2(null);
            usuarioTransferObject.setUsuNome(csaBean.getCsaNome());
            usuarioTransferObject.setUsuEmail(csaBean.getCsaEmail());
            usuarioTransferObject.setUsuTel(null);
            usuarioTransferObject.setUsuDicaSenha(null);
            usuarioTransferObject.setUsuTipoBloq(null);
            usuarioTransferObject.setUsuDataExpSenha(data);
            usuarioTransferObject.setUsuDataExpSenha2(null);
            usuarioTransferObject.setUsuIpAcesso(null);
            usuarioTransferObject.setUsuDDNSAcesso(null);
            usuarioTransferObject.setUsuCPF(null);
            usuarioTransferObject.setUsuCentralizador("N");
            usuarioTransferObject.setUsuExigeCertificado(null);
            usuarioTransferObject.setUsuMatriculaInst(null);
            usuarioTransferObject.setUsuChaveRecuperarSenha(null);
            usuarioTransferObject.setUsuDataFimVig(null);
            usuarioTransferObject.setUsuDeficienteVisual(null);

            return usuarioController.createUsuario(usuarioTransferObject, perfil, csaBean.getCsaCodigo(), AcessoSistema.ENTIDADE_CSA, null, false, usuSenha, false, responsavel);

        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return lstOcaConsignatarias(octoOcaConsignataria, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaOcorrenciaConsignatariaQuery query = new ListaOcorrenciaConsignatariaQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            final CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, octoOcaConsignataria.getCsaCodigo());
            parametrosQuery.setAttribute(Columns.OCC_CODIGO, octoOcaConsignataria.getOccCodigo());
            parametrosQuery.setAttribute(Columns.TOC_CODIGO, octoOcaConsignataria.getTocCodigo());
            query.setCriterios(parametrosQuery);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public int countOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaOcorrenciaConsignatariaQuery query = new ListaOcorrenciaConsignatariaQuery();
            final CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, octoOcaConsignataria.getCsaCodigo());
            parametrosQuery.setAttribute(Columns.OCC_CODIGO, octoOcaConsignataria.getOccCodigo());
            parametrosQuery.setAttribute(Columns.TOC_CODIGO, octoOcaConsignataria.getTocCodigo());
            query.setCriterios(parametrosQuery);
            query.count = true;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.contar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstOcaConsignataria(String csaCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaOcorrenciaConsignatariaQuery query = new ListaOcorrenciaConsignatariaQuery();
            final CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            parametrosQuery.setAttribute("tocCodigos", tocCodigos);
            query.setCriterios(parametrosQuery);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    private String createOcorrenciaConsignataria(String csaCodigo, String tocCodigo, String occObs, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final OcorrenciaConsignataria occBean = OcorrenciaConsignatariaHome.create(csaCodigo, responsavel.getUsuCodigo(), tocCodigo, occObs, tpeCodigo, tmoCodigo, responsavel.getIpUsuario());
            registrarMotivoBloqueioCsa(csaCodigo, tocCodigo, responsavel);

            if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_NOTIFICAO_GESTOR_SOBRE_BLOQUEIO_CONSIGNATARIAS, responsavel) && CodedValues.TOC_BLOQUEIA_CONSIGNATARIA.equals(tocCodigo)) {
                occObs = !TextHelper.isNull(occObs) ? occObs : ApplicationResourcesHelper.getMessage("mensagem.bloqueio.consignataria.sem.detalhar.motivo", responsavel);
                final String usuario = !responsavel.isSistema() ? responsavel.getUsuLogin() : ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueio.consignataria.sistema", responsavel);
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                final List<TransferObject> consignatarias = lstConsignatarias(criterio, responsavel);
                if ((consignatarias != null) && !consignatarias.isEmpty()) {
                    final String csaNome = (String) consignatarias.get(0).getAttribute(Columns.CSA_NOME);
                    final String csaNomeAbrev = (String) consignatarias.get(0).getAttribute(Columns.CSA_NOME_ABREV);

                    final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
                    EnviaEmailHelper.enviarEmailNotificacaoCseBloqueioConsignataria(cse.getCseEmail(), occObs, csaNome, csaNomeAbrev, usuario, responsavel);
                }
            }

            return occBean.getOccCodigo();
        } catch (final com.zetra.econsig.exception.CreateException | FindException | ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.ocorrencia.erro.interno", responsavel, ex.getMessage());
        }
    }

    private void removeOcorrenciasConsignataria(List<OcorrenciaConsignataria> ocorrencias, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((ocorrencias != null) && !ocorrencias.isEmpty()) {
                for (final OcorrenciaConsignataria occBean : ocorrencias) {
                    AbstractEntityHome.remove(occBean);
                }
            }
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.excluir.ocorrencia.erro.interno", responsavel, ex, ex.getMessage());
        }
    }

    private void registrarMotivoBloqueioCsa(String csaCodigo, String tocCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        if (!TextHelper.isNull(tocCodigo) && !TextHelper.isNull(csaCodigo)) {
            TipoMotivoBloqueioEnum motivoBloqueio = null;
            motivoBloqueio = switch (tocCodigo) {
                case CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA -> TipoMotivoBloqueioEnum.PENDENCIA_PROCESSO_PORTABILIDADE;
                case CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO -> TipoMotivoBloqueioEnum.PENDENCIA_INFORMACAO_SALDO_DEVEDOR_SERVIDOR;
                case CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE -> TipoMotivoBloqueioEnum.PENDENCIA_COMUNICACAO_SEM_RESPOSTA;
                case CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ -> TipoMotivoBloqueioEnum.PENDENCIA_LIQUIDACAO_ADE_SALDO_PAGO;
                case CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA -> TipoMotivoBloqueioEnum.PENDENCIA_MENSAGEM_SEM_LEITURA;
                case CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS -> TipoMotivoBloqueioEnum.PENDENCIA_QTD_MINIMA_ANEXOS_ADE;
                case CodedValues.TOC_BLOQ_CSA_POR_DATA_EXPIRACAO, CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO -> TipoMotivoBloqueioEnum.DATA_EXPIRACAO_VENCIDA;
                case CodedValues.TOC_BLOQUEIO_AUTOMATICO_SEGURANCA -> TipoMotivoBloqueioEnum.BLOQUEIO_AUTOMATICO_SEGURANCA;
                case CodedValues.TOC_DESBLOQUEIO_CONSIGNATARIA_PENDENTE -> TipoMotivoBloqueioEnum.DESBLOQUEIO_PENDENTE_APROVACAO;
                case CodedValues.TOC_BLOQ_CSA_POR_CET_EXPIRADO -> TipoMotivoBloqueioEnum.PENDENCIA_CET_EXPIRADO;
                case CodedValues.TOC_BLOQ_SOLICITACAO_SALDO_DEVEDOR_RESCISAO_NAO_ATENDIDA -> TipoMotivoBloqueioEnum.PENDENCIA_INFO_SALDO_DEVEDOR_RESCISAO;
                default -> null;
            };
            if (motivoBloqueio != null) {
                try {
                    ConsignatariaHome.updateMotivoBloqueio(csaCodigo, motivoBloqueio.getCodigo());
                } catch (final UpdateException ex) {
                    LOG.error(ex.getMessage(), ex);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                }
            }
        }
    }

    @Override
    public void updateConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        updateConsignataria(consignataria, false, responsavel);
    }

    private void updateConsignataria(ConsignatariaTransferObject consignataria, boolean alteracaoStatus, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final Consignataria consignatariaBean = findConsignatariaBean(consignataria);
            final String csaCodigo = consignatariaBean.getCsaCodigo();
            LogDelegate log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);

            final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSA);

            // Compara a versão do cache com a passada por parâmetro
            final ConsignatariaTransferObject consignatariaCache = setConsignatariaValues(consignatariaBean);
            final CustomTransferObject merge = log.getUpdatedFields(consignataria.getAtributos(), consignatariaCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR)) {

                // Verifica se não existe outra consignataria com o mesmo ID
                final ConsignatariaTransferObject teste = new ConsignatariaTransferObject();
                teste.setCsaIdentificador((String) merge.getAttribute(Columns.CSA_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findConsignatariaBean(teste);
                    existe = true;
                } catch (final ConsignatariaControllerException ex) {
                }
                if (existe) {
                    throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.alterar.consignataria.existe.outro.mesmo.codigo", responsavel);
                }
                consignatariaBean.setCsaIdentificador((String) merge.getAttribute(Columns.CSA_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.CSA_ATIVO)) {
                final Short csaAtivoOld = consignatariaBean.getCsaAtivo() != null ? consignatariaBean.getCsaAtivo() : CodedValues.STS_ATIVO;
                final Short csaAtivo = (Short) merge.getAttribute(Columns.CSA_ATIVO);

                final boolean bloquearCsa = !csaAtivo.equals(CodedValues.STS_ATIVO) && csaAtivoOld.equals(CodedValues.STS_ATIVO);
                final boolean desbloquearCsa = csaAtivo.equals(CodedValues.STS_ATIVO) && !csaAtivoOld.equals(CodedValues.STS_ATIVO);

                final String ncaCodigo = consignatariaBean.getNcaCodigo();
                if (responsavel.isSistema() && bloquearCsa && !TextHelper.isNull(ncaCodigo) && ncaCodigo.equals(NaturezaConsignatariaEnum.ORGAO_PUBLICO.getCodigo())) {
                    // Se o usuário do sistema está bloqueando uma consignatária que tem natureza preenchida e é igual a ORGAO_PUBLICO
                    // então não deixa bloquear, ignora alteração de status e gera um WARN no log
                    LOG.warn(String.format("Sistema tentando bloquear CSA = %s de natureza ORGAO_PUBLICO.", csaCodigo));
                    Thread.dumpStack();
                } else {
                    // Altera o status da consignatária
                    consignatariaBean.setCsaAtivo(csaAtivo);

                    if (csaAtivoOld.equals(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA) && !responsavel.isSup()) {
                        // Se está desbloqueando, e foi bloqueado por segurança, não deixa desbloqueá-lo
                        throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.desbloquear.consignataria.arg0.pois.foi.bloqueado.por.seguranca", responsavel, consignatariaBean.getCsaNome());
                    } else if (desbloquearCsa) {
                        if (!alteracaoStatus) {
                            // Não precisa chamar o "registrarDesbloqueioCsa" se for alteração de status pois o método "desbloqueiaCsa" já faz isso
                            registrarDesbloqueioCsa(csaCodigo, null, null, null, responsavel);
                        }
                        // Remove o tipo de bloqueio ao desbloquear uma CSA
                        consignatariaBean.setTipoMotivoBloqueio(null);
                    } else if (bloquearCsa) {
                        if (!alteracaoStatus) {
                            // Não precisa chamar o "registrarBloqueioCsa" se for alteração de status pois o método "bloqueiaCsa" já faz isso
                            registrarBloqueioCsa(csaCodigo, consignataria.getCsaDataDesbloqAutomatico(), null, null, null, responsavel);
                        }
                        // Regista o tipo de bloqueio ao bloquear uma CSA: grava como bloqueio manual, se for automático
                        // o método registrarMotivoBloqueioCsa() irá registrar o motivo correto
                        consignatariaBean.setTipoMotivoBloqueio(new TipoMotivoBloqueio(TipoMotivoBloqueioEnum.BLOQUEIO_MANUAL.getCodigo()));
                    }
                }
            }

            if (merge.getAtributos().containsKey(Columns.CSA_NRO_AGE)) {
                consignatariaBean.setCsaNroAge((String) merge.getAttribute(Columns.CSA_NRO_AGE));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DIG_CTA)) {
                consignatariaBean.setCsaDigCta((String) merge.getAttribute(Columns.CSA_DIG_CTA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR)) {
                consignatariaBean.setCsaIdentificador((String) merge.getAttribute(Columns.CSA_IDENTIFICADOR));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NOME)) {
                consignatariaBean.setCsaNome((String) merge.getAttribute(Columns.CSA_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CNPJ)) {
                consignatariaBean.setCsaCnpj((String) merge.getAttribute(Columns.CSA_CNPJ));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CNPJ_CTA)) {
                consignatariaBean.setCsaCnpjCta((String) merge.getAttribute(Columns.CSA_CNPJ_CTA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_EMAIL)) {
                consignatariaBean.setCsaEmail((String) merge.getAttribute(Columns.CSA_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_EMAIL_EXPIRACAO)) {
                consignatariaBean.setCsaEmailExpiracao((String) merge.getAttribute(Columns.CSA_EMAIL_EXPIRACAO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESPONSAVEL)) {
                consignatariaBean.setCsaResponsavel((String) merge.getAttribute(Columns.CSA_RESPONSAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESPONSAVEL_2)) {
                consignatariaBean.setCsaResponsavel2((String) merge.getAttribute(Columns.CSA_RESPONSAVEL_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESPONSAVEL_3)) {
                consignatariaBean.setCsaResponsavel3((String) merge.getAttribute(Columns.CSA_RESPONSAVEL_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_CARGO)) {
                consignatariaBean.setCsaRespCargo((String) merge.getAttribute(Columns.CSA_RESP_CARGO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_CARGO_2)) {
                consignatariaBean.setCsaRespCargo2((String) merge.getAttribute(Columns.CSA_RESP_CARGO_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_CARGO_3)) {
                consignatariaBean.setCsaRespCargo3((String) merge.getAttribute(Columns.CSA_RESP_CARGO_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_TELEFONE)) {
                consignatariaBean.setCsaRespTelefone((String) merge.getAttribute(Columns.CSA_RESP_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_TELEFONE_2)) {
                consignatariaBean.setCsaRespTelefone2((String) merge.getAttribute(Columns.CSA_RESP_TELEFONE_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_RESP_TELEFONE_3)) {
                consignatariaBean.setCsaRespTelefone3((String) merge.getAttribute(Columns.CSA_RESP_TELEFONE_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_LOGRADOURO)) {
                consignatariaBean.setCsaLogradouro((String) merge.getAttribute(Columns.CSA_LOGRADOURO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NRO)) {
                consignatariaBean.setCsaNro((Integer) merge.getAttribute(Columns.CSA_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_COMPL)) {
                consignatariaBean.setCsaCompl((String) merge.getAttribute(Columns.CSA_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_BAIRRO)) {
                consignatariaBean.setCsaBairro((String) merge.getAttribute(Columns.CSA_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CIDADE)) {
                consignatariaBean.setCsaCidade((String) merge.getAttribute(Columns.CSA_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_UF)) {
                consignatariaBean.setCsaUf((String) merge.getAttribute(Columns.CSA_UF));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CEP)) {
                consignatariaBean.setCsaCep((String) merge.getAttribute(Columns.CSA_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_TEL)) {
                consignatariaBean.setCsaTel((String) merge.getAttribute(Columns.CSA_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_FAX)) {
                consignatariaBean.setCsaFax((String) merge.getAttribute(Columns.CSA_FAX));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NRO_BCO)) {
                consignatariaBean.setCsaNroBco((String) merge.getAttribute(Columns.CSA_NRO_BCO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NRO_CTA)) {
                consignatariaBean.setCsaNroCta((String) merge.getAttribute(Columns.CSA_NRO_CTA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_TXT_CONTATO)) {
                consignatariaBean.setCsaTxtContato((String) merge.getAttribute(Columns.CSA_TXT_CONTATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_INSTRUCAO_ANEXO)) {
                consignatariaBean.setCsaInstrucaoAnexo((String) merge.getAttribute(Columns.CSA_INSTRUCAO_ANEXO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CONTATO)) {
                consignatariaBean.setCsaContato((String) merge.getAttribute(Columns.CSA_CONTATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CONTATO_TEL)) {
                consignatariaBean.setCsaContatoTel((String) merge.getAttribute(Columns.CSA_CONTATO_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_ENDERECO_2)) {
                consignatariaBean.setCsaEndereco2((String) merge.getAttribute(Columns.CSA_ENDERECO_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NOME_ABREV)) {
                consignatariaBean.setCsaNomeAbrev((String) merge.getAttribute(Columns.CSA_NOME_ABREV));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_TGC_CODIGO)) {
                consignatariaBean.setTipoGrupoConsignataria(merge.getAttribute(Columns.CSA_TGC_CODIGO) != null ? TipoGrupoConsignatariaHome.findByPrimaryKey((String) merge.getAttribute(Columns.CSA_TGC_CODIGO)) : null);
                if (merge.getAttribute(Columns.CSA_TGC_CODIGO) != null) {
                    log.setGrupoConsignataria((String) merge.getAttribute(Columns.CSA_TGC_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR_INTERNO)) {
                consignatariaBean.setCsaIdentificadorInterno((String) merge.getAttribute(Columns.CSA_IDENTIFICADOR_INTERNO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_EXPIRACAO)) {
                consignatariaBean.setCsaDataExpiracao((Date) merge.getAttribute(Columns.CSA_DATA_EXPIRACAO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_EXPIRACAO_CADASTRAL)) {
                consignatariaBean.setCsaDataExpiracaoCadastral((Date) merge.getAttribute(Columns.CSA_DATA_EXPIRACAO_CADASTRAL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NRO_CONTRATO)) {
                consignatariaBean.setCsaNroContrato((String) merge.getAttribute(Columns.CSA_NRO_CONTRATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_IP_ACESSO) && podeEditarEnderecoAcesso) {
                consignatariaBean.setCsaIpAcesso((String) merge.getAttribute(Columns.CSA_IP_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DDNS_ACESSO) && podeEditarEnderecoAcesso) {
                consignatariaBean.setCsaDdnsAcesso((String) merge.getAttribute(Columns.CSA_DDNS_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_EXIGE_ENDERECO_ACESSO)) {
                consignatariaBean.setCsaExigeEnderecoAcesso((String) merge.getAttribute(Columns.CSA_EXIGE_ENDERECO_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_PROJETO_INADIMPLENCIA)) {
                consignatariaBean.setCsaProjetoInadimplencia((String) merge.getAttribute(Columns.CSA_PROJETO_INADIMPLENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_UNIDADE_ORGANIZACIONAL)) {
                consignatariaBean.setCsaUnidadeOrganizacional((String) merge.getAttribute(Columns.CSA_UNIDADE_ORGANIZACIONAL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NRO_CONTRATO_ZETRA) && responsavel.isSup()) {
                consignatariaBean.setCsaNroContratoZetra((String) merge.getAttribute(Columns.CSA_NRO_CONTRATO_ZETRA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_PERMITE_INCLUIR_ADE)) {
                consignatariaBean.setCsaPermiteIncluirAde((String) merge.getAttribute(Columns.CSA_PERMITE_INCLUIR_ADE));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NCA_NATUREZA)) {
                consignatariaBean.setNaturezaConsignataria(new NaturezaConsignataria((String) merge.getAttribute(Columns.CSA_NCA_NATUREZA)));
                log.setNaturezaConsignataria((String) merge.getAttribute(Columns.CSA_NCA_NATUREZA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_ATUALIZACAO_CADASTRAL)) {
                consignatariaBean.setCsaDataAtualizacaoCadastral((java.util.Date) merge.getAttribute(Columns.CSA_DATA_ATUALIZACAO_CADASTRAL));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_CODIGO_ANS)) {
                consignatariaBean.setCsaCodigoAns((String) merge.getAttribute(Columns.CSA_CODIGO_ANS));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_EMAIL_PROJ_INADIMPLENCIA)) {
                consignatariaBean.setCsaEmailProjInadimplencia((String) merge.getAttribute(Columns.CSA_EMAIL_PROJ_INADIMPLENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_DESBLOQ_AUTOMATICO)) {
                consignatariaBean.setCsaDataDesbloqAutomatico((java.util.Date) merge.getAttribute(Columns.CSA_DATA_DESBLOQ_AUTOMATICO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_TMB_CODIGO)) {
                consignatariaBean.setTmbCodigo((String) merge.getAttribute(Columns.CSA_TMB_CODIGO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_EMAIL_DESBLOQUEIO)) {
                consignatariaBean.setCsaEmailDesbloqueio((String) merge.getAttribute(Columns.CSA_EMAIL_DESBLOQUEIO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_INICIO_CONTRATO)) {
                consignatariaBean.setCsaDataIniContrato((Date) merge.getAttribute(Columns.CSA_DATA_INICIO_CONTRATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_DATA_RENOVACAO_CONTRATO)) {
                consignatariaBean.setCsaDataRenovacaoContrato((Date) merge.getAttribute(Columns.CSA_DATA_RENOVACAO_CONTRATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_NUMERO_PROCESSO_CONTRATO)) {
                consignatariaBean.setCsaNumContrato((String) merge.getAttribute(Columns.CSA_NUMERO_PROCESSO_CONTRATO));
            }
            if (merge.getAtributos().containsKey(Columns.CSA_OBS_CONTRATO)) {
                consignatariaBean.setCsaObsContrato((String) merge.getAttribute(Columns.CSA_OBS_CONTRATO));
            }

            if (merge.getAtributos().containsKey(Columns.CSA_PERMITE_API)) {
                consignatariaBean.setCsaPermiteApi((String) merge.getAttribute(Columns.CSA_PERMITE_API));
            }

            if (merge.getAtributos().containsKey(Columns.CSA_WHATSAPP)) {
                consignatariaBean.setCsaWhatsapp((String) merge.getAttribute(Columns.CSA_WHATSAPP));
            }

            if (merge.getAtributos().containsKey(Columns.CSA_EMAIL_CONTATO)) {
                consignatariaBean.setCsaEmailContato((String) merge.getAttribute(Columns.CSA_EMAIL_CONTATO));
            }

            if (merge.getAtributos().containsKey(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA)) {
                final String permissao = (String) merge.getAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA);
                if("N".equals(permissao)) {
                    final List<ConsultaMargemSemSenha> listaConsultaMargemSemSenha = listaConsignatariaConsultaMargemSemSenhaByCsaCodigo(consignatariaBean.getCsaCodigo(), responsavel);
                    if(!listaConsultaMargemSemSenha.isEmpty()) {
                        for (final ConsultaMargemSemSenha consultaMargemSemSenha : listaConsultaMargemSemSenha) {
                            updateConsignatariaConsultaMargemSemSenha(consultaMargemSemSenha.getCssCodigo(), permissao, responsavel);
                        }
                    }
                }
                consignatariaBean.setCsaConsultaMargemSemSenha(permissao);
            }

		    final String tpaNotificaCsaAlteracaoRco = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, responsavel);
			final boolean podeNotificaCsaAlteracaoRco = (!TextHelper.isNull(tpaNotificaCsaAlteracaoRco) && CodedValues.TPC_SIM.equals(tpaNotificaCsaAlteracaoRco));
            final boolean podeNotificarCsaAlteracaoRegraConvenio = (responsavel.isSup() || responsavel.isCsa()) && podeNotificaCsaAlteracaoRco;
            if (podeNotificarCsaAlteracaoRegraConvenio && merge.getAtributos().containsKey(Columns.CSA_EMAIL_NOTIFICACAO_RCO)) {
            	final String emailNotificacaoRCO = (String) merge.getAttribute(Columns.CSA_EMAIL_NOTIFICACAO_RCO);
                consignatariaBean.setCsaEmailNotificacaoRco(emailNotificacaoRCO);
                if (emailNotificacaoRCO != null) {
                    log.add(Columns.CSA_EMAIL_NOTIFICACAO_RCO, emailNotificacaoRCO);
                }
            }

            AbstractEntityHome.update(consignatariaBean);

            log.write();

            final boolean exigeAtualizacaoCadastralCsaCnpj = responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA) && CodedValues.FUN_EDT_CONSIGNATARIA.equals(responsavel.getFunCodigo()) && ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel);
            final boolean podeRealizarChamadaCRM = responsavel.isSup() || exigeAtualizacaoCadastralCsaCnpj;

            if (podeRealizarChamadaCRM && ParamSist.getBoolParamSist(CodedValues.TPC_MANUTENCAO_CSA_UTILIZA_CRM, responsavel) && (merge.getAtributos().containsKey(Columns.CSA_CNPJ) || merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR_INTERNO))) {
                final String csaIdentificadorInterno = consignatariaBean.getCsaIdentificadorInterno();
                if (merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR_INTERNO) && !TextHelper.isNull(csaIdentificadorInterno) && csaIdentificadorInterno.startsWith("ZZ")) {
                    final Integer idInterno = TextHelper.isNum(csaIdentificadorInterno.substring(2)) ? Integer.parseInt(csaIdentificadorInterno.substring(2).toString()) : null;
                    if (!TextHelper.isNull(idInterno) && (idInterno.compareTo(80000) >= 0)) {
                        throw new CRMException("mensagem.codigo.zetra.outro.maximo", responsavel);
                    }
                }

                final String csaIdInterno = crmClient.updateServiceProvider(consignatariaBean.getCsaNome(), consignatariaBean.getCsaCnpj(), csaIdentificadorInterno);
                if (!TextHelper.isNull(csaIdInterno) &&
                    (TextHelper.isNull(csaIdentificadorInterno) || !csaIdInterno.equals(csaIdentificadorInterno))) {

                    consignatariaBean.setCsaIdentificadorInterno(csaIdInterno);
                    AbstractEntityHome.update(consignatariaBean);

                    log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setConsignataria(csaCodigo);
                    if (merge.getAtributos().containsKey(Columns.CSA_CNPJ)) {
                        log.add(Columns.CSA_CNPJ, csaIdInterno);
                    }
                    if (merge.getAtributos().containsKey(Columns.CSA_IDENTIFICADOR_INTERNO)) {
                        log.add(Columns.CSA_IDENTIFICADOR_INTERNO, csaIdInterno);
                    }
                    log.write();
                }
            }

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.log.ao.atualizar.consignataria", responsavel, ex);
        } catch (final FindException e) {
            throw new ConsignatariaControllerException(e);
        } catch (UpdateException | CRMException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(e);
        } catch (final ParametroControllerException e) {
        	LOG.error(e.getMessage(), e);
        	throw new ConsignatariaControllerException(e);
		}
    }

    @Override
    public void removeConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final Consignataria consignatariaBean = findConsignatariaBean(consignataria);
            final String csaCodigo = consignatariaBean.getCsaCodigo();
            AbstractEntityHome.remove(consignatariaBean);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setConsignataria(csaCodigo);
            logDelegate.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.excluir.consignataria.selecionado.pois.possui.dependentes", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return lstConsignatarias(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstConsignatarias(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaQuery query = new ListaConsignatariaQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.csaIdentificador = (String) criterio.getAttribute(Columns.CSA_IDENTIFICADOR);

                if (criterio.getAttribute(Columns.CSA_CODIGO) != null) {
                    if (criterio.getAttribute(Columns.CSA_CODIGO) instanceof List) {
                        query.csaCodigos = (List<String>) criterio.getAttribute(Columns.CSA_CODIGO);
                    } else {
                        query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                    }
                }

                final String csaNome = (String) criterio.getAttribute(Columns.CSA_NOME);
                if (!TextHelper.isNull(csaNome)) {
                    query.csaNome = csaNome;
                } else {
                    query.csaNome = (String) criterio.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.csaNomeAbrev = (String) criterio.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                }
                query.csaAtivo = criterio.getAttribute(Columns.CSA_ATIVO);

                final String ncaExibeSer = (String) criterio.getAttribute(Columns.NCA_EXIBE_SER);
                if (!TextHelper.isNull(ncaExibeSer)) {
                    query.ncaExibeSer = ncaExibeSer;
                }

                final String projetoInadimplencia = (String) criterio.getAttribute(Columns.CSA_PROJETO_INADIMPLENCIA);
                if (!TextHelper.isNull(projetoInadimplencia)) {
                    query.csaProjetoInadimplencia = projetoInadimplencia;
                }

                query.ncaCodigo = (String) criterio.getAttribute(Columns.CSA_NCA_NATUREZA);

                final String cnvCodVerba = (String) criterio.getAttribute(Columns.CNV_COD_VERBA);
                if (!TextHelper.isNull(cnvCodVerba)) {
                    query.cnvCodVerba = cnvCodVerba;
                }

                query.csaConsultaMargemSemSenha = (criterio.getAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA) != null) && CodedValues.TPC_SIM.equals(criterio.getAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA));
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public int countConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaQuery query = new ListaConsignatariaQuery();
            query.count = true;

            if (criterio != null) {
                query.csaIdentificador = (String) criterio.getAttribute(Columns.CSA_IDENTIFICADOR);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                final String csaNome = (String) criterio.getAttribute(Columns.CSA_NOME);
                if (!TextHelper.isNull(csaNome)) {
                    query.csaNome = csaNome;
                } else {
                    query.csaNome = (String) criterio.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.csaNomeAbrev = (String) criterio.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                }
                query.csaAtivo = criterio.getAttribute(Columns.CSA_ATIVO);

                final String ncaExibeSer = (String) criterio.getAttribute(Columns.NCA_EXIBE_SER);
                if (!TextHelper.isNull(ncaExibeSer)) {
                    query.ncaExibeSer = ncaExibeSer;
                }

                query.ncaCodigo = (String) criterio.getAttribute(Columns.CSA_NCA_NATUREZA);

                final String cnvCodVerba = (String) criterio.getAttribute(Columns.CNV_COD_VERBA);
                if (!TextHelper.isNull(cnvCodVerba)) {
                    query.cnvCodVerba = cnvCodVerba;
                }
            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public Map<String, String> getCsaIdentificadorMap(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return new ListaConsignatariaIdentificadorQuery().executarMapa();
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    /**
     * Bloqueia a consignatária, inserindo uma observação para o bloqueio e
     * incluindo uma penalidade caso seja informada.
     * OBS: irá bloquear a consignatárias mesmo que seja da natureza ORGAO_PUBLICO pois é chamda em bloqueio manual
     *
     * @param consignataria Consignatária que será bloqueada
     * @param observacao    Observação que será incluída para o bloqueio, caso não seja informada será incluída uma observação padrão.
     * @param tpeCodigo     Código da penalidade que será incluída caso seja informada.
     * @param responsavel   Responsável pelo bloqueio da consignatária.
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaCsa(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((consignataria == null) || TextHelper.isNull(consignataria.getCsaCodigo())) {
                throw new ConsignatariaControllerException(MENSAGEM_INFORME_CONSIGNATARIA_QUE_SERA_PENALIZADA, responsavel);
            }

            consignataria.setCsaAtivo(CodedValues.STS_INATIVO);
            updateConsignataria(consignataria, true, responsavel);

            registrarBloqueioCsa(consignataria.getCsaCodigo(), consignataria.getCsaDataDesbloqAutomatico(), observacao, tpeCodigo, tmoCodigo, responsavel);
        } catch (final ConsignatariaControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Registra ocorrência de bloqueio manual de CSA, removendo os bloqueios automáticos
     * para que a consignatária não seja desbloqueada de forma automática
     *
     * @param csaCodigo
     * @param csaDataDesbloqAutomatico
     * @param observacao
     * @param tpeCodigo
     * @param responsavel
     * @throws ConsignatariaControllerException
     */
    private void registrarBloqueioCsa(String csaCodigo, Date csaDataDesbloqAutomatico, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        String occObs = ApplicationResourcesHelper.getMessage("rotulo.consignataria.bloqueada.singular", responsavel).toUpperCase();
        if (csaDataDesbloqAutomatico != null) {
            occObs = ApplicationResourcesHelper.getMessage("rotulo.consignataria.bloqueada.ate.data.arg0", responsavel, DateHelper.format(csaDataDesbloqAutomatico, LocaleHelper.getDatePattern())).toUpperCase();
        }
        if (!TextHelper.isNull(observacao)) {
            occObs += ". " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, observacao).toUpperCase();
        }

        // Exclui ocorrências de bloqueio automático que eventualmente existam.
        // Faz isso porque o bloqueio/desbloqueio manual deve sobrepor o comportamento automatizado.
        removerOcorrenciasBloqueioAutomatico(csaCodigo, responsavel);

        // Registra ocorrência de bloqueio
        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, occObs, tpeCodigo, tmoCodigo, responsavel);

        final boolean possuiDesblAutCsaPrazoPenalidade = ParamSist.getBoolParamSist(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, responsavel);

        // Registro de penalidade com prazo
        if (possuiDesblAutCsaPrazoPenalidade && !TextHelper.isNull(tpeCodigo)) {
            createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO, occObs, tpeCodigo, null, responsavel);
        }
    }

    /**
     * Desbloqueia a consignatária, inserindo uma observação para o desbloqueio e
     * incluindo uma penalidade caso seja informada.
     *
     * @param consignataria Consignatária que será desbloqueada
     * @param observacao    Observação que será incluída para o desbloqueio, caso não seja informada será incluída uma observação padrão.
     * @param tpeCodigo     Código da penalidade que será incluída caso seja informada.
     * @param responsavel   Responsável pelo desbloqueio da consignatária.
     * @throws ConsignatariaControllerException
     */
    @Override
    public void desbloqueiaCsa(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((consignataria == null) || TextHelper.isNull(consignataria.getCsaCodigo())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ConsignatariaControllerException(MENSAGEM_INFORME_CONSIGNATARIA_QUE_SERA_PENALIZADA, responsavel);
            }

            // se a consignatária está bloqueada e possui ocorrência de penalidade, só permitir o desbloqueio se o usuário autenticado for Suporte/Gestor ou usuário do sistema.
            final boolean possuiDesblAutCsaPrazoPenalidade = ParamSist.getBoolParamSist(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, responsavel);
            if (possuiDesblAutCsaPrazoPenalidade) {
                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO);
                final List<TransferObject> lstOcorrenciaPenalidade = lstOcaConsignataria(consignataria.getCsaCodigo(), tocCodigos, responsavel);

                if ((lstOcorrenciaPenalidade != null) && !lstOcorrenciaPenalidade.isEmpty() && (!responsavel.isCseSup() && !CodedValues.USU_CODIGO_SISTEMA.equals(responsavel.getUsuCodigo()))) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new ConsignatariaControllerException("mensagem.erro.consignataria.bloqueio.com.penalidade", responsavel);
                }
            }

            //DESENV-18063 - DESENV-18347 se a consignatária foi bloqueada por um usuário Suporte e possui o parametro ativo, a CSE só pode desbloqueiar somente após aprovação do Suporte
            final String tpaCsaDesbloqueadaQualquerPapel = parametroController.getParamCsa(consignataria.getCsaCodigo(), CodedValues.TPA_DESBLOQUEIA_CSA_APROVACAO_POR_SUP, responsavel);
            boolean usuSup = false;
            final boolean consignatariaFoiBloqueadaManualmente = consignatariaFoiBloqueadaManualmente(consignataria.getCsaCodigo(), responsavel);

            if (!TextHelper.isNull(tpaCsaDesbloqueadaQualquerPapel) && CodedValues.TPA_NAO.equals(tpaCsaDesbloqueadaQualquerPapel) && consignatariaFoiBloqueadaManualmente && !responsavel.isSup()) {
                final OcorrenciaConsignataria ocaCsaBloqueio = OcorrenciaConsignatariaHome.findByCsaTocCodigoMaxData(consignataria.getCsaCodigo(), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, true);

                final ObtemPapelUsuarioQuery query = new ObtemPapelUsuarioQuery();
                query.usuCodigo = ocaCsaBloqueio.getUsuCodigo();
                final List<TransferObject> resultado = query.executarDTO();
                usuSup = (resultado.get(0).getAttribute(Columns.USP_CSE_CODIGO) != null) && !TextHelper.isNull(resultado.get(0).getAttribute(Columns.USP_CSE_CODIGO).toString());
            }

            if (responsavel.isCseOrg() && usuSup) {
                consignataria = findConsignataria(consignataria, responsavel);
                consignataria.setTmbCodigo(TipoMotivoBloqueioEnum.DESBLOQUEIO_PENDENTE_APROVACAO.getCodigo());
                consignataria.setCsaAtivo(CodedValues.STS_DESBLOQUEIO_PENDENTE);
                updateConsignataria(consignataria, AcessoSistema.getAcessoUsuarioSistema());
                createOcorrenciaConsignataria(consignataria.getCsaCodigo(), CodedValues.TOC_DESBLOQUEIO_CONSIGNATARIA_PENDENTE, ApplicationResourcesHelper.getMessage("mensagem.info.desblqueio.consignataria.pendente", responsavel), null, null, responsavel);
                EnviaEmailHelper.enviarEmailSuporteCsaPendente(responsavel.getUsuNome(), consignataria.getCsaNome(), consignataria.getCsaEmailDesbloqueio(), responsavel);
            } else {
                consignataria.setCsaDataDesbloqAutomatico(null);
                consignataria.setCsaAtivo(CodedValues.STS_ATIVO);
                updateConsignataria(consignataria, true, responsavel);
                registrarDesbloqueioCsa(consignataria.getCsaCodigo(), observacao, tpeCodigo, tmoCodigo, responsavel);
            }

        } catch (FindException | HQueryException | ViewHelperException | ParametroControllerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registra ocorrência de desbloqueio manual de CSA, removendo os bloqueios automáticos
     *
     * @param csaCodigo
     * @param observacao
     * @param tpeCodigo
     * @param responsavel
     * @throws ConsignatariaControllerException
     */
    private void registrarDesbloqueioCsa(String csaCodigo, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final StringBuilder occObs = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.desbloqueada.singular", responsavel).toUpperCase());
        if (!TextHelper.isNull(observacao)) {
            occObs.append(". ").append(ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, observacao).toUpperCase());
        }

        // Exclui ocorrências de bloqueio automático que eventualmente existam.
        // Faz isso porque o bloqueio/desbloqueio manual deve sobrepor o comportamento automatizado.
        removerOcorrenciasBloqueioAutomatico(csaCodigo, responsavel);

        // Registra ocorrência de desbloqueio
        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_DESBLOQUEIA_CONSIGNATARIA, occObs.toString(), tpeCodigo, tmoCodigo, responsavel);
    }

    /**
     * Remove as ocorrências de bloqueio automático de uma consignatária.
     *
     * @param csaCodigo
     * @param responsavel
     * @throws ConsignatariaControllerException
     */
    private void removerOcorrenciasBloqueioAutomatico(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomatico = recuperarOcorrenciasBloqueioAutomatico(csaCodigo, null);
        if ((ocorrenciasBloqueioAutomatico != null) && !ocorrenciasBloqueioAutomatico.isEmpty()) {
            removeOcorrenciasConsignataria(ocorrenciasBloqueioAutomatico, responsavel);
        }
    }

    /**
     * Recupera todas as ocorrências de bloqueio automático da consignatária.
     *
     * @param csaCodigo
     * @param tipoOcorrenciaBloqueio
     * @return
     */
    private List<OcorrenciaConsignataria> recuperarOcorrenciasBloqueioAutomatico(String csaCodigo, String tipoOcorrenciaBloqueio) {
        final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomatico = new ArrayList<>();

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoPorSaldo = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO);
                if (ocorrenciasBloqueioAutomaticoPorSaldo != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoPorSaldo);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoPorCompra = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA);
                if (ocorrenciasBloqueioAutomaticoPorCompra != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoPorCompra);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoPorPenalidade = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO);
                if (ocorrenciasBloqueioAutomaticoPorPenalidade != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoPorPenalidade);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasConsignatariaComPendencia = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE);
                if (ocorrenciasConsignatariaComPendencia != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasConsignatariaComPendencia);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoAnexoPendenteLiq = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ);
                if (ocorrenciasBloqueioAutomaticoAnexoPendenteLiq != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoAnexoPendenteLiq);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoConfirmacaoNaoLida = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA);
                if (ocorrenciasBloqueioAutomaticoConfirmacaoNaoLida != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoConfirmacaoNaoLida);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoSemMinimoAnexos = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS);
                if (ocorrenciasBloqueioAutomaticoSemMinimoAnexos != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoSemMinimoAnexos);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }

        if ((tipoOcorrenciaBloqueio == null) || CodedValues.TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO.equals(tipoOcorrenciaBloqueio)) {
            try {
                final List<OcorrenciaConsignataria> ocorrenciasBloqueioAutomaticoNaoConfirmarLiquidacao = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO);
                if (ocorrenciasBloqueioAutomaticoNaoConfirmarLiquidacao != null) {
                    ocorrenciasBloqueioAutomatico.addAll(ocorrenciasBloqueioAutomaticoNaoConfirmarLiquidacao);
                }
            } catch (final FindException ex) {
                // Nenhuma ocorrência.
            }
        }
        return ocorrenciasBloqueioAutomatico;
    }

    /**
     * Recupera uma mensagem de alerta a ser exibida para o usuário ao efetuar um desbloqueio da consignatária
     *
     * @param csaCodigo
     * @return
     */
    @Override
    public String recuperarMensagemDesbloqueioConsignataria(String csaCodigo) {
        String mensagemDesbloqueio = null;

        final List<OcorrenciaConsignataria> ocorrenciasPendenciaSaldo = recuperarOcorrenciasBloqueioAutomatico(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO);
        final List<OcorrenciaConsignataria> ocorrenciasPendenciaCompra = recuperarOcorrenciasBloqueioAutomatico(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA);
        if ((ocorrenciasPendenciaSaldo != null) && !ocorrenciasPendenciaSaldo.isEmpty() && (ocorrenciasPendenciaCompra != null) && !ocorrenciasPendenciaCompra.isEmpty()) {
            mensagemDesbloqueio = ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_CONSIGNATARIA_FOI_BLOQUEADA_AUTOMATICAMENTE_PELO_SISTEMA_POR_MOTIVO, (AcessoSistema) null, ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.pendencia.informacao.saldo.devedor.processo.compra", (AcessoSistema) null));
        } else if ((ocorrenciasPendenciaSaldo != null) && !ocorrenciasPendenciaSaldo.isEmpty()) {
            mensagemDesbloqueio = ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_CONSIGNATARIA_FOI_BLOQUEADA_AUTOMATICAMENTE_PELO_SISTEMA_POR_MOTIVO, (AcessoSistema) null, ApplicationResourcesHelper.getMessage("mensagem.erro.pendencias.informacao.saldo.devedor", (AcessoSistema) null));
        } else if ((ocorrenciasPendenciaCompra != null) && !ocorrenciasPendenciaCompra.isEmpty()) {
            mensagemDesbloqueio = ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_CONSIGNATARIA_FOI_BLOQUEADA_AUTOMATICAMENTE_PELO_SISTEMA_POR_MOTIVO, (AcessoSistema) null, ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.pendencia.processo.compra", (AcessoSistema) null));
        }

        if (mensagemDesbloqueio != null) {
            mensagemDesbloqueio += (" " + ApplicationResourcesHelper.getMessage("mensagem.erro.se.essas.pendencias.nao.forem.solucionados.amanha.consignataria.sera.bloqueada.novamente", (AcessoSistema) null));
        }

        return mensagemDesbloqueio;
    }

    /**
     * Insere uma penalidade para a consignatária.
     *
     * @param consignataria Consignatária que será penalizada.
     * @param observacao    Observação obrigatória que será incluída para a penalidade.
     * @param tpeCodigo     Código obrigatório da penalidade que será incluída.
     * @param responsavel   Responsável pela inclusão da penalidade para a consignatária.
     * @throws ConsignatariaControllerException
     */
    @Override
    public void inserePenalidade(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((consignataria == null) || TextHelper.isNull(consignataria.getCsaCodigo())) {
                throw new ConsignatariaControllerException(MENSAGEM_INFORME_CONSIGNATARIA_QUE_SERA_PENALIZADA, responsavel);
            }
            if (TextHelper.isNull(tpeCodigo) || TextHelper.isNull(observacao)) {
                throw new ConsignatariaControllerException("mensagem.informe.uma.penalidade.para.consignataria", responsavel);
            }
            createOcorrenciaConsignataria(consignataria.getCsaCodigo(), CodedValues.TOC_PENALIDADE, observacao, tpeCodigo, tmoCodigo, responsavel);
        } catch (final ConsignatariaControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.inserir.penalidade.para.consignataria.selecionada", responsavel);
        }
    }

    /**
     * Bloqueia consignatárias expiradas, ou seja que a data de expiração (csaDataExpiracao)
     * ou a data de expiração cadastral (csaDataExpiracaoCadastral) são datas passadas.
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @param responsavel Responsável pela inclusão da penalidade para a consignatária.
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaCsaExpiradas(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaQuery query = new ListaConsignatariaQuery();
            query.dataExpiracao = DateHelper.getSystemDatetime();
            query.csaAtivo = CodedValues.STS_ATIVO;

            final List<ConsignatariaTransferObject> lstConsignatarias = query.executarDTO(ConsignatariaTransferObject.class);
            for (final ConsignatariaTransferObject cto : lstConsignatarias) {
                final String csaCodigo = cto.getCsaCodigo();
                final String ncaCodigo = cto.getCsaNcaNatureza();
                if ((TextHelper.isNull(ncaCodigo) || !ncaCodigo.equals(NaturezaConsignatariaEnum.ORGAO_PUBLICO.getCodigo())) && cto.getCsaAtivo().equals(CodedValues.STS_ATIVO)) {
                    cto.setCsaAtivo(CodedValues.STS_INATIVO);
                    updateConsignataria(cto, AcessoSistema.getAcessoUsuarioSistema());
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_POR_DATA_EXPIRACAO, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.data.expiracao", responsavel), null, null, AcessoSistema.getAcessoUsuarioSistema());
                }
            }
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    /**
     * Bloqueia as consignatárias informadas no parâmetro "csaCodigos", inserindo uma ocorrência
     * para cada consignatária bloqueada, representada pelo "tocCodigo" e "observacao".
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @param csaCodigos
     * @param observacao
     * @param tocCodigo
     * @param responsavel
     * @throws ConsignatariaControllerException
     */
    @Override
    public List<String> bloquearConsignatarias(List<String> csaCodigos, String observacao, String tocCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            final List<String> csaJaBloqueados = new ArrayList<>();
            final List<String> csaNaoPodeBloquear = new ArrayList<>();
            for (final String csaCodigo : csaCodigos) {
                try {
                    final Consignataria csa = ConsignatariaHome.findByPrimaryKey(csaCodigo);
                    // Se já está bloqueada não pode prosseguir com o bloqueio
                    if ((csa.getCsaAtivo().shortValue() == CodedValues.STS_INATIVO.shortValue()) ||
                        (csa.getCsaAtivo().shortValue() == CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.shortValue())) {
                        csaJaBloqueados.add(csaCodigo);
                        continue;
                    }
                    // Se não pode ser bloqueada, não pode prosseguir com o bloqueio
                    if (!TextHelper.isNull(csa.getNcaCodigo()) && csa.getNcaCodigo().equals(NaturezaConsignatariaEnum.ORGAO_PUBLICO.getCodigo())) {
                        csaNaoPodeBloquear.add(csaCodigo);
                        continue;
                    }
                } catch (final FindException e) {
                    LOG.info("Consignatária não encontrada", e);
                    continue;
                }

                // Altera o status da consignatária
                final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                cto.setCsaAtivo(CodedValues.STS_INATIVO);
                updateConsignataria(cto, responsavel);

                // Incliu ocorrência de bloqueio para a consignatária
                createOcorrenciaConsignataria(csaCodigo, tocCodigo, observacao, null, null, responsavel);
            }

            csaCodigos.removeAll(csaJaBloqueados);
            csaCodigos.removeAll(csaNaoPodeBloquear);
        }

        return csaCodigos;
    }

    /**
     * Bloqueia uma lista de consignatárias, relacionada às consignações informadas e insere uma ocorrência para cada uma.
     *
     * @param adesResponsaveisBloqueio Lista de contratos contendo o codigo das consignatarias a serem bloqueadas.
     * @param tocCodigo                Tipo da ocorrência a ser adicionada.
     * @param observacao               Observacao a ser adicionada na ocorrência.
     * @param responsavel              Responsavel pela operacao.
     * @return A lista de consignatárias bloqueadas.
     * @throws ConsignatariaControllerException
     */
    @Override
    public List<String> bloquearConsignatariasContratos(List<TransferObject> adesResponsaveisBloqueio, String observacao, String tocCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        if ((adesResponsaveisBloqueio == null) || adesResponsaveisBloqueio.isEmpty()) {
            return null;
        }

        // Extrai os códigos das consignatárias da lista
        final List<String> csaCodigos = new ArrayList<>(adesResponsaveisBloqueio.stream().map(t -> (String) t.getAttribute(Columns.CSA_CODIGO)).collect(Collectors.toSet()));

        return bloquearConsignatarias(csaCodigos, observacao, tocCodigo, responsavel);
    }

    /**
     * Bloqueia consignatária cuja solicitação de liquidação de contrato com saldo pago e anexo e não foi liquidada.
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaCsaSolicitacaoSaldoPagoComAnexoNaoLiquidado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<ConsignatariaTransferObject> lstCsa = saldoDevedorController.lstSolicitacaoSaldoPagoComAnexoNaoLiquidado(null, responsavel);
            if ((lstCsa != null) && !lstCsa.isEmpty()) {
                // Obtém os códigos de CSA distintos em que ela está ativa e a natureza não é de órgão público
                final Set<String> csaCodigos = filtrarConsignatariaAtivaNaoOrgaoPublico(lstCsa);
                if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                    for (final String csaCodigo : csaCodigos) {
                        // Altera o status da consignatária
                        final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                        cto.setCsaAtivo(CodedValues.STS_INATIVO);
                        updateConsignataria(cto, AcessoSistema.getAcessoUsuarioSistema());

                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.pela.nao.liquidacao.contrato.pago.com.anexo", responsavel), null, null, responsavel);
                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ, ApplicationResourcesHelper.getMessage("mensagem.info.consignataria.possui.pendencias.com.solicitacao.liquidacao.contrato.pago.com.anexo", responsavel), null, null, responsavel);
                    }
                }
            }
        } catch (final SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public void bloqueiaCsaCetExpirado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        // Lista as consignatárias que já estão com o coeficiente / taxa de juros / CET expirado (data fim vig < data atual)
        final List<TransferObject> lstCsa = lstBloqueioConsignatariaCoeficienteExpirado(null, responsavel);

        if (ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {
            final List<TransferObject> lstCsaTx = lstBloqueioConsignatariaTaxaJurosExpirado(null, responsavel);
            if (!lstCsaTx.isEmpty()) {
                lstCsa.addAll(lstCsaTx);
            }
        }

        if ((lstCsa != null) && !lstCsa.isEmpty()) {
            // Obtém os códigos de CSA distintos em que ela está ativa e a natureza não é de órgão público
            final Set<String> csaCodigos = filtrarConsignatariaAtivaNaoOrgaoPublico(lstCsa);
            if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                for (final String csaCodigo : csaCodigos) {
                    // Altera o status da consignatária
                    final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                    cto.setCsaAtivo(CodedValues.STS_INATIVO);
                    updateConsignataria(cto, AcessoSistema.getAcessoUsuarioSistema());

                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.por.cet.expirado", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_POR_CET_EXPIRADO, ApplicationResourcesHelper.getMessage("mensagem.info.consignataria.possui.pendencias.por.cet.expirado", responsavel), null, null, responsavel);
                }
            }
        }
    }

    /**
     * Bloqueia consignatária cuja mensagem que exige confirmação não foi lida.
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaCsaMensagemNaoLida() throws ConsignatariaControllerException {
        try {
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            final List<TransferObject> lstMensagem = mensagemController.lstMensagemCsaBloqueio(null, responsavel);
            if ((lstMensagem != null) && !lstMensagem.isEmpty()) {
                // Obtém os códigos de CSA distintos em que ela está ativa e a natureza não é de órgão público
                final Set<String> csaCodigos = filtrarConsignatariaAtivaNaoOrgaoPublico(lstMensagem);
                if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                    for (final String csaCodigo : csaCodigos) {
                        // Altera o status da consignatária
                        final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                        cto.setCsaAtivo(CodedValues.STS_INATIVO);
                        updateConsignataria(cto, responsavel);

                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.pela.nao.leitura.mensagem", responsavel), null, null, responsavel);
                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA, ApplicationResourcesHelper.getMessage("mensagem.info.consignataria.possui.mensagem.exige.confirmacao.leitura.nao.lida", responsavel), null, null, responsavel);
                    }
                }
            }
        } catch (final MensagemControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public void desbloqueiaCsaPrazoDesbloqAutomatico() throws ConsignatariaControllerException {
        try {
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            final ListaCsaPrazoDesbloqAutomaticoPassadoQuery query = new ListaCsaPrazoDesbloqAutomaticoPassadoQuery();
            final List<String> csaCodigos = query.executarLista();
            if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                for (final String csaCodigo : csaCodigos) {
                    // Verifica se a consignatária possui alguma pendência passível de bloqueio
                    final boolean desbloqueada = verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                    // Se a consignatária não foi desbloqueada, então ela possui pendência em outro processo
                    // Devemos remover a data de desbloqueio automático dela, para que o desbloqueio seja
                    // controlado pelas pendências restantes
                    if (!desbloqueada) {
                        final ConsignatariaTransferObject consignataria = new ConsignatariaTransferObject(csaCodigo);
                        consignataria.setCsaDataDesbloqAutomatico(null);
                        updateConsignataria(consignataria, responsavel);
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(ex);
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public void desbloqueiaCsaPenalidadeExpirada() throws ConsignatariaControllerException {
        try {
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            final List<String> csaCodigos = lstCsaPenalidadeExpirada(null, responsavel);

            if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                for (final String csaCodigo : csaCodigos) {
                    // Verifica se a consignatária possui alguma pendência passível de bloqueio
                    verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                }
            }
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    private List<String> lstCsaPenalidadeExpirada(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCsaPenalidadeExpiradaQuery query = new ListaCsaPenalidadeExpiradaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(ex);
        }
    }

    /**
     * Verifica se a consignatária informada pelo parâmetro pode ser desbloqueada, ou seja, se não possui mais
     * nenhuma pendência relativa ao processo de compra ou solicitacao de saldo devedor. Se ela puder ser desbloqueada
     * pelo controle de compra, mas tiver pendencia de solicitacao de saldo, as ocorrencias de pendencia de compra
     * serão transferidas para pendencia de saldo e vice-versa.
     *
     * @param csaCodigo   Codigo da consignataria.
     * @param responsavel Responsavel pela operacao (csa ou cor).
     * @return True se a consignataria foi desbloqueada automaticamente. False, caso contrario.
     * @throws ConsignatariaControllerException Excecao padrao.
     */
    @Override
    public boolean verificarDesbloqueioAutomaticoConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ConsignatariaTransferObject csa = findConsignataria(csaCodigo, responsavel);
        return verificarDesbloqueioAutomaticoConsignataria(csa, responsavel);
    }

    private boolean verificarDesbloqueioAutomaticoConsignataria(ConsignatariaTransferObject csa, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((csa.getCsaAtivo() != null) && csa.getCsaAtivo().equals(CodedValues.STS_INATIVO)) {
                final String csaCodigo = csa.getCsaCodigo();

                // DESENV-740 : Sobrepõe o responsável para o usuário do sistema de forma que as ocorrências de desbloqueio não fiquem
                // associadas ao usuário que executou a operação, o que pode causar confusão, visto que pode não ser usuário da entidade.
                responsavel = AcessoSistema.getAcessoUsuarioSistema();

                //Quando a consignatária for bloqueada manualmente e existe data de bloqueio e este dia chegou o sistema previsa identificar
                //se existem outros bloqueios porém se não existir outros bloqueios, ela deve ser desbloqueada.
                boolean possuiBloqueioManualDataDesbloqueio = false;

                // Caso a consignatária tenha sido bloqueada manualmente, só poderá ter um desbloqueio automático
                // caso possua data de desbloqueio automático passada
                if (consignatariaFoiBloqueadaManualmente(csaCodigo, responsavel)) {
                    if ((csa.getCsaDataDesbloqAutomatico() == null) || (csa.getCsaDataDesbloqAutomatico().compareTo(DateHelper.getSystemDatetime()) > 0)) {
                        // Se não tem data de desbloqueio automático, ou tem mais ele ainda é futuro, então
                        // não deixa realizar o desbloqueio automático
                        return false;
                    }
                    possuiBloqueioManualDataDesbloqueio = true;
                }

                boolean podeDesbloquearControleCompra = true;
                boolean podeDesbloquearSolicitacaoSaldo = true;
                boolean podeDesbloquearCmnPendente = true;
                boolean podeDesbloquearAdeLiqSolicitacaoSaldoPagoComAnexo = true;
                boolean podeDesbloquearMensagemLeituraPendente = true;
                boolean podeDesbloquearAdeSemMinAnexos = true;
                boolean podeDesbloquearPenalidadeExpirada = true;
                boolean podeDesbloquearPorCetExpirado = true;

                boolean possuiOcorrenciaControleCompra = false;
                boolean possuiOcorrenciaSolicitacaoSaldo = false;
                boolean possuiOcorrenciaCmnPendente = false;
                boolean possuiOcorrenciaAdeLiqSolicitacaoSaldoPagoComAnexo = false;
                boolean possuiOcorrenciaMensagemLeituraPendente = false;
                boolean possuiOcorrenciaAdeSemMinAnexos = false;
                boolean possuiOcorrenciaPenalidade = false;
                boolean possuiOcorrenciaCetExpirado = false;
                boolean possuiOcorrenciaSolicitacaoSaldoRescisaoNaoAtendida = false;

                final boolean desbloqueioAutomaticoCompra = ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);
                final boolean desbloqueioAutomaticoSaldo = ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_SOLICIT_SALDO, CodedValues.TPC_SIM, responsavel);
                final boolean desbloqueioAutomaticoCmnPendente = ParamSist.paramEquals(CodedValues.TPC_DESBLOQ_AUT_CSA_CMN_SEM_RESPOSTA, CodedValues.TPC_SIM, responsavel);
                final boolean desbloqueioAutomaticoAdeLiqSolicitacaoSaldoPagoComAnexo = ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, CodedValues.TPC_SIM, responsavel);
                final boolean desbloqueioAutomaticoMensagemLeituraPendente = (ParamSist.getIntParamSist(CodedValues.TPC_DIAS_BLOQ_CSA_MENSAGEM_NAO_LIDA, 0, responsavel) > 0);
                final boolean desbloqueioAutomaticoAdeSemNumAnexosMin = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, CodedValues.TPC_SIM, responsavel);
                final boolean desbloqueioAutomaticoPenalidadeExpirada = ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, CodedValues.TPC_SIM, responsavel);

                // Informacoes sobre os bloqueios de solicitacao de saldo devedor
                final List<String> pseVlrs = new ArrayList<>();
                pseVlrs.add(CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR);
                pseVlrs.add(CodedValues.SISTEMA_CALCULA_SALDO_DEVEDOR);
                pseVlrs.add(CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR);
                final List<TransferObject> servicosQuePermitemCadastroSaldo = servicoController.selectServicosComParametro(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR, null, AcessoSistema.ENTIDADE_CSA, csaCodigo, pseVlrs, false, false, responsavel);

                // Se possui módulo de compra com processo avançado, bloqueios e penalidades
                List<OcorrenciaConsignataria> ocorrenciasCompra = new ArrayList<>();
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel)) {

                    // Verifica se pode ser desbloqueada
                    podeDesbloquearControleCompra = compraContratoController.consignatariaNaoPossuiPendenciaCompra(csaCodigo, responsavel);

                    // Verifica se possui ocorrencia de pendencia de compra
                    ocorrenciasCompra = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA);
                    possuiOcorrenciaControleCompra = ((ocorrenciasCompra != null) && !ocorrenciasCompra.isEmpty());
                }

                // Se possui serviço que permite solicitação de saldo pelo servidor
                List<OcorrenciaConsignataria> ocorrenciasSaldo = new ArrayList<>();
                List<OcorrenciaConsignataria> ocorrenciasSaldoRescisaoNaoAtendida = new ArrayList<>();
                boolean isRescisao = false;
                if ((servicosQuePermitemCadastroSaldo != null) && !servicosQuePermitemCadastroSaldo.isEmpty()) {

                    // Verifica se pode ser desbloqueada
                    final Map<String, Boolean> verificaDesbloqueioSaldo = saldoDevedorController.consignatariaNaoPossuiPendenciaSaldoDevedor(csaCodigo, responsavel);
                    podeDesbloquearSolicitacaoSaldo = verificaDesbloqueioSaldo.get(CodedValues.PODE_DESBLOQUEAR_CSA);

                    if(!podeDesbloquearSolicitacaoSaldo) {
                        isRescisao = verificaDesbloqueioSaldo.get(CodedValues.IS_CSA_COM_ADE_COM_RESCISAO);
                    }

                    // Verifica se possui ocorrencia de pendencia de solicitacao de saldo devedor
                    ocorrenciasSaldo = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO);

                    // Verifica se possui ocorrencia de pendencia de solicitacao de saldo devedor de rescisão não atendida
                    ocorrenciasSaldoRescisaoNaoAtendida = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_SOLICITACAO_SALDO_DEVEDOR_RESCISAO_NAO_ATENDIDA);

                    possuiOcorrenciaSolicitacaoSaldo = ((ocorrenciasSaldo != null) && !ocorrenciasSaldo.isEmpty());

                    possuiOcorrenciaSolicitacaoSaldoRescisaoNaoAtendida = ((ocorrenciasSaldoRescisaoNaoAtendida != null) && !ocorrenciasSaldoRescisaoNaoAtendida.isEmpty());
                }

                // Se possui bloqueio automático por comunicação pendente
                List<OcorrenciaConsignataria> ocorrenciasCmn = new ArrayList<>();
                if ((ParamSist.getIntParamSist(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER, 0, responsavel) > 0) ||
                    (ParamSist.getIntParamSist(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG, 0, responsavel) > 0)) {

                    podeDesbloquearCmnPendente = !comunicacaoController.temComunicacoesParaBloqueioCsa(csaCodigo, responsavel);

                    // Verifica se possui ocorrencia de pendencia de comunicação
                    ocorrenciasCmn = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE);
                    possuiOcorrenciaCmnPendente = ((ocorrenciasCmn != null) && !ocorrenciasCmn.isEmpty());
                }

                // Se possui bloqueio automatico por consignação não liquidada com saldo pago
                List<OcorrenciaConsignataria> ocorrenciasLiquidacao = new ArrayList<>();
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, CodedValues.TPC_SIM, responsavel)) {

                    // Verifica se pode ser desbloqueada
                    final List<ConsignatariaTransferObject> lstSolicitacaoSaldoPagoComAnexoNaoLiquidado = saldoDevedorController.lstSolicitacaoSaldoPagoComAnexoNaoLiquidado(csaCodigo, responsavel);
                    podeDesbloquearAdeLiqSolicitacaoSaldoPagoComAnexo = (lstSolicitacaoSaldoPagoComAnexoNaoLiquidado == null) || lstSolicitacaoSaldoPagoComAnexoNaoLiquidado.isEmpty();

                    // Verifica se possui ocorrencia de pendencia de solicitacao de saldo devedor
                    ocorrenciasLiquidacao = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ);
                    possuiOcorrenciaAdeLiqSolicitacaoSaldoPagoComAnexo = ((ocorrenciasLiquidacao != null) && !ocorrenciasLiquidacao.isEmpty());
                }

                // Se possui bloqueio automatico na confirmação de leitura de mensagem
                List<OcorrenciaConsignataria> ocorrenciasMensagemLeituraPendente = new ArrayList<>();
                if (desbloqueioAutomaticoMensagemLeituraPendente) {

                    // Verifica se pode ser desbloqueada
                    final List<TransferObject> lstMensagemComPendenciaLeitura = mensagemController.lstMensagemCsaBloqueio(csaCodigo, responsavel);
                    podeDesbloquearMensagemLeituraPendente = (lstMensagemComPendenciaLeitura == null) || lstMensagemComPendenciaLeitura.isEmpty();

                    // Verifica se possui ocorrencia de pendencia de leitura de mensagem
                    ocorrenciasMensagemLeituraPendente = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA);
                    possuiOcorrenciaMensagemLeituraPendente = ((ocorrenciasMensagemLeituraPendente != null) && !ocorrenciasMensagemLeituraPendente.isEmpty());
                }

                // Se possui loqueio automatico na verificação de contratos feitos por CSA/COR sem número mínimo de anexos definido pelo parâmetro de serviço 284
                List<OcorrenciaConsignataria> ocorrenciasBloqCsaAdesSemMinAnexos = new ArrayList<>();
                if (desbloqueioAutomaticoAdeSemNumAnexosMin) {
                    final Agendamento agendamentoBloqCsaAdeSemMinAnexos = AgendamentoHome.findByPrimaryKey(AgendamentoEnum.BLOQUEIO_CSA_ADE_SEM_NUM_ANEXOS_MINIMO.getCodigo());
                    final List<TransferObject> lstAdesNaoConformidade = lstAdesUsuCsaCorSemNumAnexoMin(csaCodigo, agendamentoBloqCsaAdeSemMinAnexos.getAgdDataCadastro(), responsavel);
                    podeDesbloquearAdeSemMinAnexos = ((lstAdesNaoConformidade == null) || lstAdesNaoConformidade.isEmpty());

                    ocorrenciasBloqCsaAdesSemMinAnexos = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS);
                    possuiOcorrenciaAdeSemMinAnexos = ((ocorrenciasBloqCsaAdesSemMinAnexos != null) && !ocorrenciasBloqCsaAdesSemMinAnexos.isEmpty());
                }

                // Se possui bloqueio automatico por penalidade expirada
                List<OcorrenciaConsignataria> ocorrenciasBloqCsaPrazoPenalidade = new ArrayList<>();
                if (desbloqueioAutomaticoPenalidadeExpirada) {
                    ocorrenciasBloqCsaPrazoPenalidade = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO);
                    final List<String> lstCsaPenalidadeExpirada = lstCsaPenalidadeExpirada(csaCodigo, responsavel);

                    possuiOcorrenciaPenalidade = ((ocorrenciasBloqCsaPrazoPenalidade != null) && !ocorrenciasBloqCsaPrazoPenalidade.isEmpty());

                    // Se possui ocorrência de penalidade e não possui ocorrência expirada, significa que ainda deve permanecer desbloqueada
                    podeDesbloquearPenalidadeExpirada = possuiOcorrenciaPenalidade ? ((lstCsaPenalidadeExpirada != null) && !lstCsaPenalidadeExpirada.isEmpty()) : true;
                }

                // Se possui bloqueio por solicitação de liquidação e informou a liquidação é necessário desbloquear a consignatária.
                final List<OcorrenciaConsignataria> ocorrenciaBloqueioSolicitacaoLiquidacao = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO);
                final List<TransferObject> lstCsaSolicitacaoLiquidacaoNaoAtendida = lstSolicitacaoLiquidacaoNaoConfirmada(csaCodigo, responsavel);

                final boolean possuiOcorrenciaSolicitacaoLiquidacaoAtendida = (ocorrenciaBloqueioSolicitacaoLiquidacao != null) && !ocorrenciaBloqueioSolicitacaoLiquidacao.isEmpty();
                final boolean podeDesbloquearSoliciticadoLiquidacaoAtendida = (lstCsaSolicitacaoLiquidacaoNaoAtendida == null) || lstCsaSolicitacaoLiquidacaoNaoAtendida.isEmpty();

                // Verifica se pode desbloquear por bloqueio manual com data de desbloqueio automático passada
                final boolean podeDesbloquearPorDataDesbloqAutomaticoPassada = ((csa.getCsaDataDesbloqAutomatico() == null) || (csa.getCsaDataDesbloqAutomatico().compareTo(DateHelper.getSystemDatetime()) <= 0));

                // Se possui bloqueio automático por CET expirado
                List<OcorrenciaConsignataria> ocorrenciasBloqCsaCetExpirado = new ArrayList<>();
                if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CSA_POR_CET_EXPIRADO, CodedValues.TPC_SIM, responsavel)) {
                    // Verifica se pode ser desbloqueada
                    final List<TransferObject> lstCsaCetExpirado = lstBloqueioConsignatariaCoeficienteExpirado(csaCodigo, responsavel);
                    if (ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {
                        final List<TransferObject> lstCsaTx = lstBloqueioConsignatariaTaxaJurosExpirado(csaCodigo, responsavel);
                        if (!lstCsaTx.isEmpty()) {
                            lstCsaCetExpirado.addAll(lstCsaTx);
                        }
                    }

                    podeDesbloquearPorCetExpirado = ((lstCsaCetExpirado == null) || lstCsaCetExpirado.isEmpty());

                    // Verifica se possui ocorrência de pendência por CET expirado
                    ocorrenciasBloqCsaCetExpirado = OcorrenciaConsignatariaHome.findByCsaTocCodigo(csaCodigo, CodedValues.TOC_BLOQ_CSA_POR_CET_EXPIRADO);
                    possuiOcorrenciaCetExpirado = ((ocorrenciasBloqCsaCetExpirado != null) && !ocorrenciasBloqCsaCetExpirado.isEmpty());
                }

                // Determina se a CSA pode ser desbloqueada
                boolean podeDesbloquear = true;

                // Avalia quais ocorrências de pendência podem ser removidas, caso o motivo da pendência não exista mais mas a ocorrência sim
                final List<OcorrenciaConsignataria> ocorrenciasARemover = new ArrayList<>();

                // Pendência de compra
                if (podeDesbloquearControleCompra && possuiOcorrenciaControleCompra) {
                    ocorrenciasARemover.addAll(ocorrenciasCompra);
                    if (!desbloqueioAutomaticoCompra) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearControleCompra) {
                    podeDesbloquear = false;
                }
                // Pendência de solicitação de saldo
                if (podeDesbloquearSolicitacaoSaldo && (possuiOcorrenciaSolicitacaoSaldo || possuiOcorrenciaSolicitacaoSaldoRescisaoNaoAtendida)) {
                    ocorrenciasARemover.addAll(ocorrenciasSaldo);
                    ocorrenciasARemover.addAll(ocorrenciasSaldoRescisaoNaoAtendida);
                    if (!desbloqueioAutomaticoSaldo) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearSolicitacaoSaldo) {
                    podeDesbloquear = false;
                }
                // Pendência de Comunicação não Respondida
                if (podeDesbloquearCmnPendente && possuiOcorrenciaCmnPendente) {
                    ocorrenciasARemover.addAll(ocorrenciasCmn);
                    if (!desbloqueioAutomaticoCmnPendente) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearCmnPendente) {
                    podeDesbloquear = false;
                }
                // Pendência de liquidação em consignação com saldo solicitado/pago com anexo
                if (podeDesbloquearAdeLiqSolicitacaoSaldoPagoComAnexo && possuiOcorrenciaAdeLiqSolicitacaoSaldoPagoComAnexo) {
                    ocorrenciasARemover.addAll(ocorrenciasLiquidacao);
                    if (!desbloqueioAutomaticoAdeLiqSolicitacaoSaldoPagoComAnexo) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearAdeLiqSolicitacaoSaldoPagoComAnexo) {
                    podeDesbloquear = false;
                }
                // Pendência de mensagem aguardando leitura
                if (podeDesbloquearMensagemLeituraPendente && possuiOcorrenciaMensagemLeituraPendente) {
                    ocorrenciasARemover.addAll(ocorrenciasMensagemLeituraPendente);
                    if (!desbloqueioAutomaticoMensagemLeituraPendente) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearMensagemLeituraPendente) {
                    podeDesbloquear = false;
                }
                // Pendência de anexos mínimo em consignação
                if (podeDesbloquearAdeSemMinAnexos && possuiOcorrenciaAdeSemMinAnexos) {
                    ocorrenciasARemover.addAll(ocorrenciasBloqCsaAdesSemMinAnexos);
                    if (!desbloqueioAutomaticoAdeSemNumAnexosMin) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearAdeSemMinAnexos) {
                    podeDesbloquear = false;
                }
                // Pendência de penalidade com data para bloqueio
                if (podeDesbloquearPenalidadeExpirada && possuiOcorrenciaPenalidade) {
                    ocorrenciasARemover.addAll(ocorrenciasBloqCsaPrazoPenalidade);
                    if (!desbloqueioAutomaticoPenalidadeExpirada) {
                        podeDesbloquear = false;
                    }
                } else if (!podeDesbloquearPenalidadeExpirada) {
                    podeDesbloquear = false;
                }
                // Pendência em consignação com solicitação de liquidação não atendida
                if (podeDesbloquearSoliciticadoLiquidacaoAtendida && possuiOcorrenciaSolicitacaoLiquidacaoAtendida) {
                    ocorrenciasARemover.addAll(ocorrenciaBloqueioSolicitacaoLiquidacao);
                } else if (!podeDesbloquearSoliciticadoLiquidacaoAtendida) {
                    podeDesbloquear = false;
                }
                // Pendência de CET expirado
                if (podeDesbloquearPorCetExpirado && possuiOcorrenciaCetExpirado) {
                    ocorrenciasARemover.addAll(ocorrenciasBloqCsaCetExpirado);
                } else if (!podeDesbloquearPorCetExpirado) {
                    podeDesbloquear = false;
                }

                // Bloqueio manual com data de desbloqueio automático
                if (!podeDesbloquearPorDataDesbloqAutomaticoPassada) {
                    podeDesbloquear = false;
                }

                // Remove as ocorrências de pendência que não são mais válidas
                if (!ocorrenciasARemover.isEmpty()) {
                    removeOcorrenciasConsignataria(ocorrenciasARemover, responsavel);
                }

                // Quando o podeDesbloquear é true e existe alguma ocorrência de bloqueio, significa que o bloqueio não foi automático, pois no bloqueio manual remove todas as ocorrênicas de bloqueio automático.
                if (podeDesbloquear && (possuiOcorrenciaControleCompra || possuiOcorrenciaSolicitacaoSaldo || possuiOcorrenciaCmnPendente || possuiOcorrenciaAdeLiqSolicitacaoSaldoPagoComAnexo || possuiOcorrenciaMensagemLeituraPendente || possuiOcorrenciaAdeSemMinAnexos || possuiOcorrenciaPenalidade || possuiOcorrenciaSolicitacaoLiquidacaoAtendida || possuiBloqueioManualDataDesbloqueio || possuiOcorrenciaCetExpirado)) {
                    desbloqueiaCsa(csa, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.desbloqueio.automatico", responsavel), null, null, responsavel);
                    return true;

                } else if (!podeDesbloquearControleCompra) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.pendencia.processo.compra", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.nao.atender.processo.compra", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearSolicitacaoSaldo) {
                    String mensagemOcorrencia = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.pendencia.solicitacao.saldo.devedor", responsavel);
                    String mensagemBloqueio = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.solicitacao.saldo.devedor", responsavel);
                    String tocCodigo = CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO;
                    if(isRescisao) {
                        mensagemOcorrencia =ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.possui.pendencias.por.solicitacao.saldo.devedor.rescisao.nao.atendida", responsavel);
                        mensagemBloqueio = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.bloqueio.automatico.consignataria.por.solicitacao.saldo.devedor.rescisao.nao.atendida", responsavel);
                        tocCodigo = CodedValues.TOC_BLOQ_SOLICITACAO_SALDO_DEVEDOR_RESCISAO_NAO_ATENDIDA;
                    }
                    createOcorrenciaConsignataria(csaCodigo, tocCodigo, mensagemOcorrencia, null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, mensagemBloqueio, null, null, responsavel);
                } else if (!podeDesbloquearCmnPendente) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.comunicacoes.pendentes", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.comunicacoes.pendentes", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearAdeLiqSolicitacaoSaldoPagoComAnexo) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.pendencia.solicitacao.liquidacao.contrato", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.liquidacao.contrato", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearMensagemLeituraPendente) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.possui.mensagem.exige.confirmacao.leitura.pendente", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.consignataria.pela.nao.leitura.mensagem", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearAdeSemMinAnexos) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.possui.ade.sem.min.anexos", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.csa.com.ades.sem.min.anexos", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearPenalidadeExpirada) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.possui.penalidade", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.bloqueio.automatico.csa.com.penalidade", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearSoliciticadoLiquidacaoAtendida) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.solicitacao.liquidacao.nao.atendidada", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.automatico.solicitacao.liquidacao.nao.atendidada", responsavel), null, null, responsavel);

                } else if (!podeDesbloquearPorCetExpirado) {
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_POR_CET_EXPIRADO, ApplicationResourcesHelper.getMessage("mensagem.info.consignataria.possui.pendencias.por.cet.expirado", responsavel), null, null, responsavel);
                    createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.por.cet.expirado", responsavel), null, null, responsavel);
                }

                // Retorna falso indicando que não houve desbloqueio
                return false;

            } else {
                // Se já está ativa, retorna positivo para inexistência de bloqueios
                return false;
            }
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public boolean verificarDesbloqueioAutomaticoConsignatariaPorAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ConsignatariaTransferObject csa = setConsignatariaValues(ConsignatariaHome.findByAdeCodigo(adeCodigo));
            return verificarDesbloqueioAutomaticoConsignataria(csa, responsavel);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    private boolean consignatariaFoiBloqueadaManualmente(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            // Precisamos garantir que não existe o bloqueio manual, o bloqueio manual é feito pelo usuário via web e não pelo usuário padrão do sistema
            // assim quando o usuário for diferente do sistema sabemos que foi um bloqueio manual.
            final OcorrenciaConsignataria ocaCsaBloqueio = OcorrenciaConsignatariaHome.findByCsaTocCodigoMaxData(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, true);
            final OcorrenciaConsignataria ocaCsaDesbloqueio = OcorrenciaConsignatariaHome.findByCsaTocCodigoMaxData(csaCodigo, CodedValues.TOC_DESBLOQUEIA_CONSIGNATARIA, false);

            final Date dataBloqueio = ocaCsaBloqueio != null ? ocaCsaBloqueio.getOccData() : null;
            final Date dataDesbloqueio = ocaCsaDesbloqueio != null ? ocaCsaDesbloqueio.getOccData() : null;

            return ((dataBloqueio != null) && ((dataDesbloqueio == null) || (dataBloqueio.compareTo(dataDesbloqueio) > 0)));
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Recebe uma lista de consignatárias e inclui a ocorrência passada como parâmetro para cada uma.
     * Método pertencia a CompraContratoControllerBean.
     *
     * @param csaCodigos  Lista de codigos de consignataria.
     * @param tocCodigo   Tipo da ocorrencia.
     * @param observacao  Observacao a ser adicionada na ocorrencia.
     * @param responsavel Responsavel pela operacao.
     * @throws ConsignatariaControllerException
     */
    @Override
    public void incluirOcorrenciaConsignatarias(Collection<String> csaCodigos, String tocCodigo, String observacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            for (final String csaCodigo : csaCodigos) {
                createOcorrenciaConsignataria(csaCodigo, tocCodigo, observacao, null, null, responsavel);
            }
        }
    }

    //Grupo de Consignatárias
    //Busca lista de consignatárias existentes
    @Override
    public List<TransferObject> lstGrupoConsignataria(String strTgcCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaGrupoConsignatariaQuery query = new ListaGrupoConsignatariaQuery();
            query.tgcCodigo = strTgcCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    //Inclusão de novo grupo de consignatária
    @Override
    public String insGrupoConsignataria(String tgcIdentificador, String tgcDescricao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            //Cria o grupo de consignataria
            final TipoGrupoConsignataria tgc = TipoGrupoConsignatariaHome.create(tgcIdentificador, tgcDescricao);

            //Cria log
            final LogDelegate logTipoGrupoConsignataria = new LogDelegate(responsavel, Log.GRUPO_CONSIGNATARIA, Log.CREATE, Log.LOG_INFORMACAO);
            logTipoGrupoConsignataria.setGrupoConsignataria(tgc.getTgcCodigo());
            logTipoGrupoConsignataria.add(tgcIdentificador + " - " + tgcDescricao);
            logTipoGrupoConsignataria.write();

            return tgc.getTgcCodigo();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //Erro ao incluir grupo
            ConsignatariaControllerException excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.grupo.consignataria.existe.outro.mesmo.codigo", responsavel);
            if (ex.getMessage().indexOf(INVALID_ARGUMENT_VALUE) != -1) {
                excecao = new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.grupo.consignataria.erro.interno", responsavel, ex.getMessage());
            }
            throw excecao;
        }
    }

    @Override
    public GrupoConsignatariaTransferObject findGrupoCsaByIdentificador(String tgcIdentificador) throws ConsignatariaControllerException {
        final GrupoConsignatariaTransferObject grupoCsa = new GrupoConsignatariaTransferObject();
        grupoCsa.setGrupoCsaIdentificador(tgcIdentificador);
        return setGrupoCsaValues(findGrupoCsaBean(grupoCsa));
    }

    private TipoGrupoConsignataria findGrupoCsaBean(GrupoConsignatariaTransferObject grupoCsa) throws ConsignatariaControllerException {
        TipoGrupoConsignataria grupoCsaBean = null;
        try {
            if (grupoCsa.getGrupoCsaCodigo() != null) {
                grupoCsaBean = TipoGrupoConsignatariaHome.findByPrimaryKey(grupoCsa.getGrupoCsaCodigo());
            } else if (grupoCsa.getGrupoCsaIdentificador() != null) {
                grupoCsaBean = TipoGrupoConsignatariaHome.findByIdn(grupoCsa.getGrupoCsaIdentificador());
            } else {
                throw new ConsignatariaControllerException("mensagem.erro.grupo.consignataria.nao.encontrada", (AcessoSistema) null);
            }
        } catch (final FindException e) {
            throw new ConsignatariaControllerException("mensagem.erro.grupo.consignataria.nao.encontrada", (AcessoSistema) null);
        }
        return grupoCsaBean;
    }

    //Grupo de consignatarias
    private GrupoConsignatariaTransferObject setGrupoCsaValues(TipoGrupoConsignataria grupoCsaBean) {
        final GrupoConsignatariaTransferObject grupoCsa = new GrupoConsignatariaTransferObject(grupoCsaBean.getTgcCodigo());
        grupoCsa.setGrupoCsaIdentificador(grupoCsaBean.getTgcIdentificador());
        grupoCsa.setGrupoCsaDescricao(String.valueOf(grupoCsaBean.getTgcDescricao()));

        return grupoCsa;
    }

    //Edita grupo de consignatária
    @Override
    public void edtGrupoConsignataria(GrupoConsignatariaTransferObject grupoCsa, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final TipoGrupoConsignataria grupoCsaBean = findGrupoCsaBean(grupoCsa);
            final LogDelegate log = new LogDelegate(responsavel, Log.GRUPO_CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setGrupoConsignataria(grupoCsaBean.getTgcCodigo());
            log.getUpdatedFields(grupoCsa.getAtributos(), null);

            // Compara a versão do cache com a passada por parâmetro
            final GrupoConsignatariaTransferObject grupoCsaCache = setGrupoCsaValues(grupoCsaBean);
            final CustomTransferObject merge = log.getUpdatedFields(grupoCsa.getAtributos(), grupoCsaCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.TGC_IDENTIFICADOR)) {

                // Verifica se não existe outra consignataria com o mesmo ID
                final GrupoConsignatariaTransferObject grupoCsaOutro = new GrupoConsignatariaTransferObject();
                grupoCsaOutro.setGrupoCsaIdentificador((String) merge.getAttribute(Columns.TGC_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findGrupoCsaBean(grupoCsaOutro);
                    existe = true;
                } catch (final ConsignatariaControllerException ex) {
                }
                if (existe) {
                    throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.alterar.grupo.consignataria.existe.outro.mesmo.codigo", responsavel);
                }
                grupoCsaBean.setTgcIdentificador((String) merge.getAttribute(Columns.TGC_IDENTIFICADOR));
            }
            if (merge.getAtributos().containsKey(Columns.TGC_DESCRICAO)) {
                grupoCsaBean.setTgcDescricao((String) merge.getAttribute(Columns.TGC_DESCRICAO));
            }

            AbstractEntityHome.update(grupoCsaBean);

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(e);
        }
    }

    //Exclui grupo de consignatária
    @Override
    public void renGrupoConsignataria(String strGrupoCsaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            //Verifica se existe grupo criado
            final TipoGrupoConsignataria tgc = TipoGrupoConsignatariaHome.findByPrimaryKey(strGrupoCsaCodigo);

            //Cria log
            final LogDelegate logTipoGrupoConsignataria = new LogDelegate(responsavel, Log.GRUPO_CONSIGNATARIA, Log.DELETE, Log.LOG_INFORMACAO);
            logTipoGrupoConsignataria.setGrupoConsignataria(tgc.getTgcCodigo());
            logTipoGrupoConsignataria.add(tgc.getTgcIdentificador() + " - " + tgc.getTgcDescricao());
            logTipoGrupoConsignataria.write();

            //Exclui o grupo de consignataria
            AbstractEntityHome.remove(tgc);

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.gerar.log", responsavel, ex);
        } catch (final FindException e) {
            throw new ConsignatariaControllerException("mensagem.erro.interno.grupo.consignataria.nao.encontrada", responsavel);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.excluir.grupo.consignataria.selecionado.pois.possui.dependentes", responsavel, ex);
        }
    }

    // Empresa Correspondente
    @Override
    public String createEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        String ecoCodigo = null;
        try {
            if ((criterio == null) || TextHelper.isNull(criterio.getAttribute(Columns.ECO_IDENTIFICADOR)) || TextHelper.isNull(criterio.getAttribute(Columns.ECO_NOME)) || TextHelper.isNull(criterio.getAttribute(Columns.ECO_ATIVO)) || TextHelper.isNull(criterio.getAttribute(Columns.ECO_CNPJ))) {

                throw new ConsignatariaControllerException("mensagem.erro.campos.obrigatorios.nao.informados.para.inclusao.empresa.correspondente", responsavel);
            }

            validaIdentCnpjEmpresaCor(criterio, responsavel);

            final Short ecoAtivo = (Short) criterio.getAttribute(Columns.ECO_ATIVO);
            final String ecoBairro = (String) criterio.getAttribute(Columns.ECO_BAIRRO);
            final String ecoCep = (String) criterio.getAttribute(Columns.ECO_CEP);
            final String ecoCidade = (String) criterio.getAttribute(Columns.ECO_CIDADE);
            final String ecoCnpj = (String) criterio.getAttribute(Columns.ECO_CNPJ);
            final String ecoCompl = (String) criterio.getAttribute(Columns.ECO_COMPL);
            final String ecoEmail = (String) criterio.getAttribute(Columns.ECO_EMAIL);
            final String ecoFax = (String) criterio.getAttribute(Columns.ECO_FAX);
            final String ecoIdentificador = (String) criterio.getAttribute(Columns.ECO_IDENTIFICADOR);
            final String ecoLogradouro = (String) criterio.getAttribute(Columns.ECO_LOGRADOURO);
            final String ecoNome = (String) criterio.getAttribute(Columns.ECO_NOME);
            final Integer ecoNro = (Integer) criterio.getAttribute(Columns.ECO_NRO);
            final String ecoRespCargo = (String) criterio.getAttribute(Columns.ECO_RESP_CARGO);
            final String ecoRespCargo2 = (String) criterio.getAttribute(Columns.ECO_RESP_CARGO_2);
            final String ecoRespCargo3 = (String) criterio.getAttribute(Columns.ECO_RESP_CARGO_3);
            final String ecoResponsavel = (String) criterio.getAttribute(Columns.ECO_RESPONSAVEL);
            final String ecoResponsavel2 = (String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_2);
            final String ecoResponsavel3 = (String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_3);
            final String ecoRespTelefone = (String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE);
            final String ecoRespTelefone2 = (String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_2);
            final String ecoRespTelefone3 = (String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_3);
            final String ecoTel = (String) criterio.getAttribute(Columns.ECO_TEL);
            final String ecoUf = (String) criterio.getAttribute(Columns.ECO_UF);

            final EmpresaCorrespondente bean = EmpresaCorrespondenteHome.create(ecoAtivo, ecoBairro, ecoCep, ecoCidade, ecoCnpj, ecoCompl, ecoEmail, ecoFax, ecoIdentificador, ecoLogradouro, ecoNome, ecoNro, ecoRespCargo, ecoRespCargo2, ecoRespCargo3, ecoResponsavel, ecoResponsavel2, ecoResponsavel3, ecoRespTelefone, ecoRespTelefone2, ecoRespTelefone3, ecoTel, ecoUf);
            ecoCodigo = bean.getEcoCodigo();

            final LogDelegate log = new LogDelegate(responsavel, Log.EMPRESA_CORRESPONDENTE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setEmpresaCorrespondente(ecoCodigo);
            log.getUpdatedFields(criterio.getAtributos(), null);
            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.incluir.esta.empresa.correspondente", responsavel, ex);
        }

        return ecoCodigo;
    }

    private void validaIdentCnpjEmpresaCor(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final String ecoCodigo = !TextHelper.isNull(criterio.getAttribute(Columns.ECO_CODIGO)) ? criterio.getAttribute(Columns.ECO_CODIGO).toString() : "";

        // Valida se existe alguma empresa correspondente com o mesmo identificador informado
        if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_IDENTIFICADOR))) {
            try {
                final EmpresaCorrespondente existe = EmpresaCorrespondenteHome.findByIdentificador(criterio.getAttribute(Columns.ECO_IDENTIFICADOR).toString());
                if (existe != null) {
                    if (!TextHelper.isNull(ecoCodigo)) {
                        if (!existe.getEcoCodigo().equals(ecoCodigo)) {
                            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.empresa.correspondente.existe.outra.mesmo.codigo", responsavel);
                        }
                    } else {
                        throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.empresa.correspondente.existe.outra.mesmo.codigo", responsavel);
                    }
                }
            } catch (final FindException e) {
                // Não foi encontrada nenhuma empresa correspondente com o mesmo identificador informado
            }
        }

        // Valida se existe alguma empresa correspondente com o mesmo CNPJ informado
        if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_CNPJ))) {
            try {
                final EmpresaCorrespondente existe = EmpresaCorrespondenteHome.findByCNPJ(criterio.getAttribute(Columns.ECO_CNPJ).toString());
                if (existe != null) {
                    if (!TextHelper.isNull(ecoCodigo)) {
                        if (!existe.getEcoCodigo().equals(ecoCodigo)) {
                            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.empresa.correspondente.existe.outra.mesmo.cnpj", responsavel);
                        }
                    } else {
                        throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.empresa.correspondente.existe.outra.mesmo.cnpj", responsavel);
                    }
                }
            } catch (final FindException e) {
                // Não foi encontrada nenhuma empresa correspondente com o mesmo CNPJ informado
            }
        }
    }

    @Override
    public void updateEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final String ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
            final EmpresaCorrespondente empresa = EmpresaCorrespondenteHome.findByPrimaryKey(ecoCodigo);

            validaIdentCnpjEmpresaCor(criterio, responsavel);

            if (empresa != null) {
                final CorrespondenteTransferObject correspondente = new CorrespondenteTransferObject();

                if (criterio.getAttribute(Columns.ECO_ATIVO) != null) {
                    empresa.setEcoAtivo((Short) criterio.getAttribute(Columns.ECO_ATIVO));
                    correspondente.setCorAtivo((Short) criterio.getAttribute(Columns.ECO_ATIVO));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_BAIRRO))) {
                    empresa.setEcoBairro((String) criterio.getAttribute(Columns.ECO_BAIRRO));
                    correspondente.setCorBairro((String) criterio.getAttribute(Columns.ECO_BAIRRO));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_CEP))) {
                    empresa.setEcoCep((String) criterio.getAttribute(Columns.ECO_CEP));
                    correspondente.setCorCep((String) criterio.getAttribute(Columns.ECO_CEP));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_CIDADE))) {
                    empresa.setEcoCidade((String) criterio.getAttribute(Columns.ECO_CIDADE));
                    correspondente.setCorCidade((String) criterio.getAttribute(Columns.ECO_CIDADE));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_CNPJ))) {
                    empresa.setEcoCnpj((String) criterio.getAttribute(Columns.ECO_CNPJ));
                    correspondente.setCorCnpj((String) criterio.getAttribute(Columns.ECO_CNPJ));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_COMPL))) {
                    empresa.setEcoCompl((String) criterio.getAttribute(Columns.ECO_COMPL));
                    correspondente.setCorCompl((String) criterio.getAttribute(Columns.ECO_COMPL));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_EMAIL))) {
                    empresa.setEcoEmail((String) criterio.getAttribute(Columns.ECO_EMAIL));
                    correspondente.setCorEmail((String) criterio.getAttribute(Columns.ECO_EMAIL));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_FAX))) {
                    empresa.setEcoFax((String) criterio.getAttribute(Columns.ECO_FAX));
                    correspondente.setCorFax((String) criterio.getAttribute(Columns.ECO_FAX));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_IDENTIFICADOR))) {
                    empresa.setEcoIdentificador((String) criterio.getAttribute(Columns.ECO_IDENTIFICADOR));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_LOGRADOURO))) {
                    empresa.setEcoLogradouro((String) criterio.getAttribute(Columns.ECO_LOGRADOURO));
                    correspondente.setCorLogradouro((String) criterio.getAttribute(Columns.ECO_LOGRADOURO));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_NOME))) {
                    empresa.setEcoNome((String) criterio.getAttribute(Columns.ECO_NOME));
                    correspondente.setCorNome((String) criterio.getAttribute(Columns.ECO_NOME));
                }
                if (criterio.getAttribute(Columns.ECO_NRO) != null) {
                    empresa.setEcoNro((Integer) criterio.getAttribute(Columns.ECO_NRO));
                    correspondente.setCorNro((Integer) criterio.getAttribute(Columns.ECO_NRO));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_CARGO))) {
                    empresa.setEcoRespCargo((String) criterio.getAttribute(Columns.ECO_RESP_CARGO));
                    correspondente.setCorRespCargo((String) criterio.getAttribute(Columns.ECO_RESP_CARGO));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_CARGO_2))) {
                    empresa.setEcoRespCargo2((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_2));
                    correspondente.setCorRespCargo2((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_2));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_CARGO_3))) {
                    empresa.setEcoRespCargo3((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_3));
                    correspondente.setCorRespCargo3((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_3));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESPONSAVEL))) {
                    empresa.setEcoResponsavel((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL));
                    correspondente.setCorResponsavel((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESPONSAVEL_2))) {
                    empresa.setEcoResponsavel2((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_2));
                    correspondente.setCorResponsavel2((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_2));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESPONSAVEL_3))) {
                    empresa.setEcoResponsavel3((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_3));
                    correspondente.setCorResponsavel3((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_3));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_TELEFONE))) {
                    empresa.setEcoRespTelefone((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE));
                    correspondente.setCorRespTelefone((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_TELEFONE_2))) {
                    empresa.setEcoRespTelefone2((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_2));
                    correspondente.setCorRespTelefone2((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_2));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_RESP_TELEFONE_3))) {
                    empresa.setEcoRespTelefone3((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_3));
                    correspondente.setCorRespTelefone3((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_3));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_TEL))) {
                    empresa.setEcoTel((String) criterio.getAttribute(Columns.ECO_TEL));
                    correspondente.setCorTel((String) criterio.getAttribute(Columns.ECO_TEL));
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.ECO_UF))) {
                    empresa.setEcoUf((String) criterio.getAttribute(Columns.ECO_UF));
                    correspondente.setCorUf((String) criterio.getAttribute(Columns.ECO_UF));
                }

                AbstractEntityHome.update(empresa);

                final LogDelegate log = new LogDelegate(responsavel, Log.EMPRESA_CORRESPONDENTE, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setEmpresaCorrespondente(ecoCodigo);
                log.getUpdatedFields(criterio.getAtributos(), null);
                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException ex) {
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void removeEmpresaCorrespondente(String ecoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final EmpresaCorrespondente empresa = new EmpresaCorrespondente();
            empresa.setEcoCodigo(ecoCodigo);
            AbstractEntityHome.remove(empresa);

            final LogDelegate log = new LogDelegate(responsavel, Log.EMPRESA_CORRESPONDENTE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setEmpresaCorrespondente(ecoCodigo);
            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.excluir.esta.empresa.correspondente.pois.esta.associada.algum.correspondente", responsavel);
        }
    }

    @Override
    public int countEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaEmpresaCorrespondenteQuery query = new ListaEmpresaCorrespondenteQuery();
            query.count = true;

            if (criterio != null) {
                query.ecoAtivo = (Short) criterio.getAttribute(Columns.ECO_ATIVO);
                query.ecoIdentificador = (String) criterio.getAttribute(Columns.ECO_IDENTIFICADOR);
                query.ecoNome = (String) criterio.getAttribute(Columns.ECO_NOME);
                query.ecoCnpj = (String) criterio.getAttribute(Columns.ECO_CNPJ);
            }

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.retornar.quantidade.empresas.correspondentes", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstEmpresaCorrespondente(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaEmpresaCorrespondenteQuery query = new ListaEmpresaCorrespondenteQuery();

            if (criterio != null) {
                query.ecoAtivo = (Short) criterio.getAttribute(Columns.ECO_ATIVO);
                query.ecoIdentificador = (String) criterio.getAttribute(Columns.ECO_IDENTIFICADOR);
                query.ecoNome = (String) criterio.getAttribute(Columns.ECO_NOME);
                query.ecoCnpj = (String) criterio.getAttribute(Columns.ECO_CNPJ);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                query.ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
            }

            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.retornar.lista.empresas.correspondentes", responsavel, ex);
        }
    }

    @Override
    public TransferObject findEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        TransferObject transferObject = null;
        try {
            final ListaEmpresaCorrespondenteQuery query = new ListaEmpresaCorrespondenteQuery();
            query.ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
            query.ecoIdentificador = (String) criterio.getAttribute(Columns.ECO_IDENTIFICADOR);
            String ecoCnpj = (String) criterio.getAttribute(Columns.ECO_CNPJ);

            if (!TextHelper.isNull(ecoCnpj)) {
                ecoCnpj = ecoCnpj.replace(".", "").replace("-", "").replace("/", "");
                query.ecoCnpj = ecoCnpj;
            }

            final List<TransferObject> lista = query.executarDTO();
            final Iterator<TransferObject> it = lista.iterator();
            if (it.hasNext()) {
                transferObject = it.next();
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.recuperar.empresa.correspondente", responsavel, ex);
        }
        return transferObject;
    }

    private int countAssociacaoEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaAssociacaoEmpresaCorrespondenteQuery query = new ListaAssociacaoEmpresaCorrespondenteQuery();
            query.ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.retornar.quantidade.associacoes.da.empresa.correspondente", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstAssociacaoEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaAssociacaoEmpresaCorrespondenteQuery query = new ListaAssociacaoEmpresaCorrespondenteQuery();
            query.ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.retornar.quantidade.associacoes.da.empresa.correspondente", responsavel, ex);
        }
    }

    @Override
    public void associaEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final String limiteAssociacoes = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_ASSOCIACOES_POR_EMPRESA_COR, responsavel);
        if (!TextHelper.isNull(limiteAssociacoes)) {
            final int limite = Integer.parseInt(limiteAssociacoes);
            final int total = countAssociacaoEmpresaCorrespondente(criterio, responsavel);
            if (total >= limite) {
                throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.realizar.associacao.limite.maximo.associacoes.por.empresa.correspondente.alcancado", responsavel, String.valueOf(limite));
            }
        }

        final String corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);

        if (!TextHelper.isNull(corCodigo)) {
            final CorrespondenteTransferObject correspondente = new CorrespondenteTransferObject();
            correspondente.setAttribute(Columns.COR_CODIGO, corCodigo);
            correspondente.setEcoCodigo((String) criterio.getAttribute(Columns.ECO_CODIGO));
            correspondente.setCorCnpj((String) criterio.getAttribute(Columns.ECO_CNPJ));

            updateCorrespondente(correspondente, responsavel);

        } else {
            final CorrespondenteTransferObject correspondente = new CorrespondenteTransferObject();
            correspondente.setCorAtivo((Short) criterio.getAttribute(Columns.ECO_ATIVO));
            correspondente.setCorBairro((String) criterio.getAttribute(Columns.ECO_BAIRRO));
            correspondente.setCorCep((String) criterio.getAttribute(Columns.ECO_CEP));
            correspondente.setCorCidade((String) criterio.getAttribute(Columns.ECO_CIDADE));
            correspondente.setCorCnpj((String) criterio.getAttribute(Columns.ECO_CNPJ));
            correspondente.setCorCompl((String) criterio.getAttribute(Columns.ECO_COMPL));
            correspondente.setCorEmail((String) criterio.getAttribute(Columns.ECO_EMAIL));
            correspondente.setCorFax((String) criterio.getAttribute(Columns.ECO_FAX));
            correspondente.setCorLogradouro((String) criterio.getAttribute(Columns.ECO_LOGRADOURO));
            correspondente.setCorNome((String) criterio.getAttribute(Columns.ECO_NOME));
            correspondente.setCorNro((Integer) criterio.getAttribute(Columns.ECO_NRO));
            correspondente.setCorRespCargo((String) criterio.getAttribute(Columns.ECO_RESP_CARGO));
            correspondente.setCorRespCargo2((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_2));
            correspondente.setCorRespCargo3((String) criterio.getAttribute(Columns.ECO_RESP_CARGO_3));
            correspondente.setCorResponsavel((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL));
            correspondente.setCorResponsavel2((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_2));
            correspondente.setCorResponsavel3((String) criterio.getAttribute(Columns.ECO_RESPONSAVEL_3));
            correspondente.setCorRespTelefone((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE));
            correspondente.setCorRespTelefone2((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_2));
            correspondente.setCorRespTelefone3((String) criterio.getAttribute(Columns.ECO_RESP_TELEFONE_3));
            correspondente.setCorTel((String) criterio.getAttribute(Columns.ECO_TEL));
            correspondente.setCorUf((String) criterio.getAttribute(Columns.ECO_UF));
            correspondente.setEcoCodigo((String) criterio.getAttribute(Columns.ECO_CODIGO));

            correspondente.setCorIdentificador((String) criterio.getAttribute(Columns.COR_IDENTIFICADOR));
            correspondente.setCsaCodigo((String) criterio.getAttribute(Columns.COR_CSA_CODIGO));

            final CustomTransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.COR_CSA_CODIGO, criterio.getAttribute(Columns.COR_CSA_CODIGO));
            final List<Short> statusCor = new ArrayList<>();
            statusCor.add(CodedValues.STS_ATIVO);
            statusCor.add(CodedValues.STS_INATIVO);
            statusCor.add(CodedValues.STS_INATIVO_CSE);
            statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
            to.setAttribute(Columns.COR_ATIVO, statusCor);
            final List<TransferObject> lista = lstCorrespondentes(to, responsavel);

            final StringBuilder codigos = new StringBuilder();
            boolean usado = false;
            final Iterator<TransferObject> it = lista.iterator();
            while (it.hasNext()) {
                final TransferObject cto = it.next();
                codigos.append(cto.getAttribute(Columns.COR_IDENTIFICADOR).toString());
                if (cto.getAttribute(Columns.COR_IDENTIFICADOR).toString().equals(correspondente.getCorIdentificador())) {
                    usado = true;
                }
                if (it.hasNext()) {
                    codigos.append(", ");
                }
            }
            if (usado) {
                throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.correspondente.existe.outro.mesmo.codigo.consignataria.lista.codigos.usados.consignataria", responsavel, codigos.toString());
            }

            createCorrespondente(correspondente, responsavel);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return this.lstConsignatariaSerTemAde(serCodigo, null, false, responsavel);
    }

    @Override
    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, String rseCodigo, boolean sadAtivos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaComAdeSerQuery lstCsa = new ListaConsignatariaComAdeSerQuery();
            lstCsa.serCodigo = serCodigo;
            lstCsa.rseCodigo = rseCodigo;
            lstCsa.somenteAtivos = sadAtivos;

            return lstCsa.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_RETORNAR_LISTA_CONSIGNATARIAS, responsavel, ex);
        }

    }

    /**
     * retorna consignatárias com data a expirar no dia dado pelo parâmetro
     *
     * @param dataExpiracao
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public List<ConsignatariaTransferObject> lstConsignatariasAExpirar(Date dataExpiracao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaAExpirarQuery lstCsa = new ListaConsignatariaAExpirarQuery();
            lstCsa.dataExpiracao = dataExpiracao;

            return lstCsa.executarDTO(ConsignatariaTransferObject.class);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_RETORNAR_LISTA_CONSIGNATARIAS, responsavel, ex);
        }
    }

    /**
     * Lista consignatárias que possuem contratos do servidor passíveis de serem renegociados para serviço alvo
     *
     * @param rseCodigo   código do registro servidor dono dos contratos
     * @param svcCodigo   serviço destino da renegociação
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public List<TransferObject> lstConsignatariasComAdeRenegociaveis(String rseCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaCsasComAdeRenegociaveisQuery query = new ListaCsasComAdeRenegociaveisQuery();
        query.rseCodigo = rseCodigo;
        query.svcCodigo = svcCodigo;
        query.orgCodigo = orgCodigo;

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);

        query.sadCodigos = sadCodigos;

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_RETORNAR_LISTA_CONSIGNATARIAS, responsavel, ex);
        }

    }

    @Override
    public List<TransferObject> lstNatureza() throws ConsignatariaControllerException {
        try {
            final ListaNaturezaConsignatariaQuery query = new ListaNaturezaConsignatariaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<Consignataria> lstConsignatariaProjetoInadimplencia() throws ConsignatariaControllerException {
        try {
            return ConsignatariaHome.findByProjetoInadimplencia(CodedValues.TPC_SIM);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nenhuma.consignataria.encontrada", (AcessoSistema) null);
        }
    }

    @Override
    public void enviaEmailAlertaProximidadeCorte(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<TransferObject> parametros = parametroController.selectParamCsa(null, CodedValues.TPA_QTDE_DIAS_ENVIO_EMAIL_ALERTA_PROX_CORTE, responsavel);

            if ((parametros != null) && !parametros.isEmpty()) {
                Date dataFimPeriodo = null;
                final List<TransferObject> periodos = periodoController.obtemPeriodoAtual(null, null, responsavel);
                if ((periodos != null) && !periodos.isEmpty()) {
                    for (final TransferObject periodo : periodos) {
                        final Date pexDataFim = (Date) periodo.getAttribute(Columns.PEX_DATA_FIM);
                        if ((pexDataFim != null) && ((dataFimPeriodo == null) || (pexDataFim.compareTo(dataFimPeriodo) < 0))) {
                            dataFimPeriodo = pexDataFim;
                        }
                    }
                }

                for (final TransferObject parametro : parametros) {
                    final Integer pcsVlr = !TextHelper.isNull(parametro.getAttribute(Columns.PCS_VLR)) ? Integer.parseInt(parametro.getAttribute(Columns.PCS_VLR).toString()) : 0;

                    if (pcsVlr.compareTo(0) > 0) {
                        final int dayDiff = DateHelper.dayDiff(dataFimPeriodo);
                        if ((dayDiff <= 0) && (Math.abs(dayDiff) < pcsVlr)) {
                            final String csaCodigo = parametro.getAttribute(Columns.PCS_CSA_CODIGO).toString();

                            final ConsignatariaTransferObject consignataria = new ConsignatariaTransferObject(csaCodigo);
                            final Consignataria consignatariaBean = findConsignatariaBean(consignataria);

                            // Enviar email para csa
                            try {
                                final String csaEmail = consignatariaBean.getCsaEmail();
                                if (!TextHelper.isNull(csaEmail)) {
                                    EnviaEmailHelper.enviarEmailCsaAlertaProximidadeCorte(csaEmail, dataFimPeriodo, responsavel);
                                }
                            } catch (final ViewHelperException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }
        } catch (ParametroControllerException | PeriodoException e) {
            throw new ConsignatariaControllerException("mensagem.erro.enviar.email.proximidade.corte", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaConvenioQuery query = new ListaConsignatariaConvenioQuery();
            query.responsavel = responsavel;

            if (criterio != null) {
                query.csaAtivo = criterio.getAttribute(Columns.CSA_ATIVO);
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstCorrespondenteConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCorrespondenteConvenioQuery query = new ListaCorrespondenteConvenioQuery();
            query.responsavel = responsavel;

            if (criterio != null) {
                query.corAtivo = criterio.getAttribute(Columns.COR_ATIVO);
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public void enviarEmailAlertaRetornoServidor(AcessoSistema responsavel) throws ConsignatariaControllerException {
        final String dataRetorno = (String) ParamSist.getInstance().getParam(CodedValues.TPC_RSE_DATA_RETORNO, responsavel);
        if ((dataRetorno != null) && !"0".equals(dataRetorno)) {
            final List<Integer> diasParam = new ArrayList<>();
            final String[] datasArray = TextHelper.split(dataRetorno, ",|;");
            for (final String element : datasArray) {
                try {
                    final Integer val = Integer.valueOf(element.trim());
                    if (val > 0) {
                        diasParam.add(val);
                    }
                } catch (final NumberFormatException nfe) {
                    throw new ConsignatariaControllerException("mensagem.erro.retorno.servidor.parametro", responsavel, "TPC_RSE_DATA_RETORNO");
                }
            }

            try {
                EnviaEmailHelper.enviarEmailAlertaRetornoServidor(diasParam, responsavel);
            } catch (final ViewHelperException e) {
                throw new ConsignatariaControllerException(e);
            }
        }
    }

    @Override
    public Consignataria findConsignatariaByNumeroContratoBeneficio(String numeroContratoBenificio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        Consignataria consignataria = null;
        try {
            consignataria = ConsignatariaHome.findConsignatariaByNumeroContratoBeneficio(numeroContratoBenificio);
        } catch (final FindException e) {
            throw new ConsignatariaControllerException(e);
        }

        return consignataria;
    }

    @Override
    public List<Consignataria> lstConsignatariaByNcaCodigo(String ncaCodigo, AcessoSistema resposavel) throws ConsignatariaControllerException {
        List<Consignataria> operadoras = null;
        try {
            operadoras = ConsignatariaHome.lstConsignatariaByNcaCodigo(ncaCodigo, resposavel);
        } catch (final Exception e) {
            throw new ConsignatariaControllerException(e);
        }
        return operadoras;
    }

    @Override
    public List<TransferObject> lstConsignatariaByNaturezas(List<String> ncaCodigos, AcessoSistema resposavel) throws ConsignatariaControllerException {
        try {
            final ListarConsignatariaByNcaCodigosQuery query = new ListarConsignatariaByNcaCodigosQuery();
            query.ncaCodigos = ncaCodigos;

            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getCause(), e);
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariasSaldoDevedorServidor(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaConsignatariaSaldoDevedorServidorQuery query = new ListaConsignatariaSaldoDevedorServidorQuery();

            if (responsavel.isCsa()) {
                query.csaCodigo = responsavel.getCsaCodigo();
            }

            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getCause(), e);
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaByNatureza(String ncaCodigo, AcessoSistema resposavel) throws ConsignatariaControllerException {
        final List<TransferObject> retorno = new ArrayList<>();

        List<Consignataria> operadoras = null;
        try {
            operadoras = ConsignatariaHome.lstConsignatariaByNcaCodigo(ncaCodigo, resposavel);
        } catch (final Exception e) {
            throw new ConsignatariaControllerException(e);
        }

        for (final Consignataria consignataria : operadoras) {
            retorno.add(setConsignatariaValues(consignataria));
        }

        return retorno;
    }

    /**
     * A lista de Consignataria não contem todos os objetos, ajustar a Query e a montagem do objeto para devolver os valores necessarios.
     *
     * @param ncaCodigo
     * @param scvCodigo
     * @param nseCodigo
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public List<Consignataria> lstConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServico(String ncaCodigo, String scvCodigo, String nseCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<Consignataria> consignatarias = new ArrayList<>();

            final ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery query = new ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery();
            query.ncaCodigo = ncaCodigo;
            query.scvCodigo = scvCodigo;
            query.nseCodigo = nseCodigo;

            final List<TransferObject> resultados = query.executarDTO();

            for (final TransferObject resultado : resultados) {
                final Consignataria consignataria = new Consignataria();
                final String csaCodigo = resultado.getAttribute(Columns.CSA_CODIGO).toString();
                final String csaNome = resultado.getAttribute(Columns.CSA_NOME).toString();

                consignataria.setCsaCodigo(csaCodigo);
                consignataria.setCsaNome(csaNome);

                consignatarias.add(consignataria);
            }

            return consignatarias;
        } catch (final HQueryException e) {
            LOG.error(e.getCause(), e);
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaPorNaturezaServico(String nseCodigo, TipoFiltroPesquisaFluxoEnum nFiltrarPor, String nFiltro) throws ConsignatariaControllerException {
        return lstConsignatariaPorNaturezaServico(null, nseCodigo, nFiltrarPor, nFiltro);
    }

    @Override
    public List<TransferObject> lstConsignatariaPorNaturezaServico(String orgCodigo, String nseCodigo, TipoFiltroPesquisaFluxoEnum nFiltrarPor, String nFiltro) throws ConsignatariaControllerException {
        try {
            final ListarConsignatariaByNaturezaServicoQuery query = new ListarConsignatariaByNaturezaServicoQuery();
            query.nseCodigo = nseCodigo;
            query.tipoFiltro = nFiltrarPor;
            query.filtro = nFiltro;
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getCause(), e);
            throw new ConsignatariaControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return this.lstContratosCsaOcorrenciaPeriodo(csaCodigo, corCodigo, tocCodigos, null, dataIni, dataFim, mesAno, offset, count, responsavel);
    }

    /**
     * pesquisa contratos que tem ocorrências específicas em um determinado período
     *
     * @param tocCodigos  - tipos de ocorrências buscadas
     * @param sadCodigos  - filtro de status dos contratos
     * @param dataIni     - data início do período
     * @param dataFim     - data fim do período.
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> lstContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo,
                                                                 List<String> tocCodigos, List<String> sadCodigos, Date dataIni, Date dataFim, String mesAno, int offset,
                                                                 int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaConsignacaoOcorrenciaPeriodoQuery query = new ListaConsignacaoOcorrenciaPeriodoQuery();
        query.tocCodigos = tocCodigos;

        if (dataIni != null) {
            try {
                query.dataIni = DateHelper.parse(DateHelper.format(dataIni, YYYY_MM_DD_00_00_00), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (dataFim != null) {
            try {
                query.dataFim = DateHelper.parse(DateHelper.format(dataFim, YYYY_MM_DD_23_59_59), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (csaCodigo != null) {
            query.csaCodigo = csaCodigo;
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            query.sadCodigos = sadCodigos;
        }

        if (corCodigo != null) {
            query.corCodigo = corCodigo;
        }
        final List<Date> primeiroUltimoDiaDoMes = DateHelper.getPrimeiroUltimoDiaDoMes(mesAno);

        query.periodoIni = primeiroUltimoDiaDoMes.get(0);
        query.periodoFim = primeiroUltimoDiaDoMes.get(1);

        if (offset != -1) {
            query.firstResult = offset;
        }

        if (count != -1) {
            query.maxResults = count;
        }

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Bloqueia consignatárias que possuírem contratos ativos sem o mínimo anexos exigidos pelo parâmetro de serviço 284
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @param dataIniVerificacao - data a partir da qual os contratos inseridos serão verificados
     * @param responsavel
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaConsignatariasComAdeSemNumAnexosMin(Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel)) {
            final List<TransferObject> lstAdes = lstAdesUsuCsaCorSemNumAnexoMin(null, dataIniVerificacao, responsavel);

            if ((lstAdes != null) && !lstAdes.isEmpty()) {
                // Obtém os códigos de CSA distintos em que ela está ativa e a natureza não é de órgão público
                final Set<String> csaCodigos = filtrarConsignatariaAtivaNaoOrgaoPublico(lstAdes);
                if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                    for (final String csaCodigo : csaCodigos) {
                        final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                        cto.setCsaAtivo(CodedValues.STS_INATIVO);
                        updateConsignataria(cto, AcessoSistema.getAcessoUsuarioSistema());

                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.consignataria.com.contrato.ativo.sem.minimo.anexo", responsavel), null, null, AcessoSistema.getAcessoUsuarioSistema());
                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.ade.usu.csa.cor.sem.min.anexos", responsavel), null, null, responsavel);
                    }
                }
            }
        }
    }

    @Override
    public List<TransferObject> lstAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return this.lstAdesUsuCsaCorSemNumAnexoMin(csaCodigo, dataIniVerificacao, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaConsignacaoUsuCsaCorSemAnexosMinQuery lstCsasSemAnexoMin = new ListaConsignacaoUsuCsaCorSemAnexosMinQuery();
        lstCsasSemAnexoMin.dataIniVerificacao = dataIniVerificacao;
        lstCsasSemAnexoMin.csaCodigo = csaCodigo;
        if (count != -1) {
            lstCsasSemAnexoMin.firstResult = offset;
            lstCsasSemAnexoMin.maxResults = count;
        }
        lstCsasSemAnexoMin.responsavel = responsavel;

        List<TransferObject> lstAdes = null;
        try {
            lstAdes = lstCsasSemAnexoMin.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
        }
        return lstAdes;
    }

    @Override
    public int countAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final List<TransferObject> lstAdes = lstAdesUsuCsaCorSemNumAnexoMin(csaCodigo, dataIniVerificacao, responsavel);

        if ((lstAdes != null) && !lstAdes.isEmpty()) {
            return lstAdes.size();
        }

        return 0;
    }

    /**
     * conta contratos que tem ocorrências específicas em um determinado período
     *
     * @param tocCodigos  - tipos de ocorrências buscadas
     * @param dataIni     - data início do período
     * @param dataFim     - data fim do período.
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public int countContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaConsignacaoOcorrenciaPeriodoQuery query = new ListaConsignacaoOcorrenciaPeriodoQuery();
        query.tocCodigos = tocCodigos;

        if (dataIni != null) {
            try {
                query.dataIni = DateHelper.parse(DateHelper.format(dataIni, YYYY_MM_DD_00_00_00), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (dataFim != null) {
            try {
                query.dataFim = DateHelper.parse(DateHelper.format(dataFim, YYYY_MM_DD_23_59_59), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (csaCodigo != null) {
            query.csaCodigo = csaCodigo;
        }

        if (corCodigo != null) {
            query.corCodigo = corCodigo;
        }

        query.count = true;

        final List<Date> primeiroUltimoDiaDoMes = DateHelper.getPrimeiroUltimoDiaDoMes(mesAno);

        query.periodoIni = primeiroUltimoDiaDoMes.get(0);
        query.periodoFim = primeiroUltimoDiaDoMes.get(1);

        try {
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public BigDecimal sumContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaConsignacaoOcorrenciaPeriodoQuery query = new ListaConsignacaoOcorrenciaPeriodoQuery();
        query.tocCodigos = tocCodigos;

        if (dataIni != null) {
            try {
                query.dataIni = DateHelper.parse(DateHelper.format(dataIni, YYYY_MM_DD_00_00_00), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (dataFim != null) {
            try {
                query.dataFim = DateHelper.parse(DateHelper.format(dataFim, YYYY_MM_DD_23_59_59), YYYY_MM_DD_HH_MM_SS);
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_PERIODO_PARSE_INVALIDO, responsavel, ex);
            }
        }

        if (csaCodigo != null) {
            query.csaCodigo = csaCodigo;
        }
        if (corCodigo != null) {
            query.corCodigo = corCodigo;
        }

        query.sum = true;

        final List<Date> primeiroUltimoDiaDoMes = DateHelper.getPrimeiroUltimoDiaDoMes(mesAno);

        query.periodoIni = primeiroUltimoDiaDoMes.get(0);
        query.periodoFim = primeiroUltimoDiaDoMes.get(1);

        try {
            return query.executarSomatorio();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void criarOcorrenciaAtualizarDados(String csaCodigo, String tocObs, AcessoSistema responsavel) throws ConsignatariaControllerException {
        // Registra ocorrência de Atualização cadastral
        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_ALTERACAO_DADOS_CADASTRAIS_ENTIDADE, tocObs, null, null, responsavel);
    }

    @Override
    public List<TransferObject> lstEnderecoConsignatariaByCsaCodigo(String csaCodigo, int count, int offset, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaEnderecosConsignatariaQuery query = new ListaEnderecosConsignatariaQuery();
        query.csaCodigo = csaCodigo;

        if (count != -1) {
            query.maxResults = count;
        }

        if (offset != -1) {
            query.firstResult = offset;
        }

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public EnderecoConsignataria findEnderecoConsignatariaByPKCsaCodigo(String encCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return EnderecoConsignatariaHome.findByPrimaryKeyAndCsaCodigo(encCodigo, csaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.nao.encontrado", responsavel);
        }
    }

    @Override
    public int countEnderecoConsignatariaByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ListaEnderecosConsignatariaQuery query = new ListaEnderecosConsignatariaQuery();
        query.csaCodigo = csaCodigo;
        query.count = true;

        try {
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return TipoEnderecoHome.listAll();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.tipo.endereco.consignataria.nao.encontrado", responsavel);
        }
    }

    @Override
    public EnderecoConsignataria createEnderecoConsignataria(String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro, String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final EnderecoConsignataria enderecoConsignataria = EnderecoConsignatariaHome.create(csaCodigo, tieCodigo, encLogradouro, encNumero, encComplemento, encBairro, encMunicipio, encUf, encCep, encLatitude, encLongitude);

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CONSIGNATARIA, Log.CREATE, Log.LOG_INFORMACAO);
            log.setEnderecoConsignataria(enderecoConsignataria.getEncCodigo());
            log.write();

            return enderecoConsignataria;
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public EnderecoConsignataria updateEnderecoConsignataria(String encCodigo, String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro, String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final EnderecoConsignataria enderecoConsignataria = EnderecoConsignatariaHome.update(encCodigo, csaCodigo, tieCodigo, encLogradouro, encNumero, encComplemento, encBairro, encMunicipio, encUf, encCep, encLatitude, encLongitude);

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setEnderecoConsignataria(enderecoConsignataria.getEncCodigo());
            log.write();

            return enderecoConsignataria;
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.atualizar", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public EnderecoConsignataria removeEnderecoConsignataria(String encCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {

            final EnderecoConsignataria enderecoConsignataria = new EnderecoConsignataria();
            enderecoConsignataria.setEncCodigo(encCodigo);
            final Consignataria consignataria = new Consignataria();
            consignataria.setCsaCodigo(csaCodigo);
            enderecoConsignataria.setConsignataria(consignataria);
            AbstractEntityHome.remove(enderecoConsignataria);

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CONSIGNATARIA, Log.DELETE, Log.LOG_INFORMACAO);
            log.setEnderecoConsignataria(enderecoConsignataria.getEncCodigo());
            log.write();

            return enderecoConsignataria;
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.remover", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Bloqueia consignatária que não liquidou a consignação que foi solicitada.
     * OBS: não bloqueia consignatárias da natureza ORGAO_PUBLICO
     *
     * @return
     * @throws ConsignatariaControllerException
     */
    @Override
    public void bloqueiaCsaNaoConfirmacaoLiquidacao() throws ConsignatariaControllerException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        try {
            final List<TransferObject> lstCsaParaBloqueio = lstSolicitacaoLiquidacaoNaoConfirmada(null, responsavel);

            if ((lstCsaParaBloqueio != null) && !lstCsaParaBloqueio.isEmpty()) {
                // Obtém os códigos de CSA distintos em que ela está ativa e a natureza não é de órgão público
                final Set<String> csaCodigos = filtrarConsignatariaAtivaNaoOrgaoPublico(lstCsaParaBloqueio);
                if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                    for (final String csaCodigo : csaCodigos) {
                        // Altera o status da consignatária
                        final ConsignatariaTransferObject cto = new ConsignatariaTransferObject(csaCodigo);
                        cto.setCsaAtivo(CodedValues.STS_INATIVO);
                        updateConsignataria(cto, responsavel);
                        createOcorrenciaConsignataria(csaCodigo, CodedValues.TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.info.bloqueio.automatico.consignataria.pela.nao.confirmacao.liquidacao", responsavel), null, null, responsavel);
                    }

                    for (final TransferObject csa : lstCsaParaBloqueio) {
                        final String soaCodigo = (String) csa.getAttribute(Columns.SOA_CODIGO);
                        final Date dataValidade = (Date) csa.getAttribute(Columns.SOA_DATA_VALIDADE);
                        final Date soaDataValidade = dataValidade == null ? DateHelper.getSystemDatetime() : dataValidade;

                        if (dataValidade == null) {
                            final SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);
                            solicitacaoAutorizacao.setSoaDataValidade(soaDataValidade);
                            AbstractEntityHome.update(solicitacaoAutorizacao);
                        }
                    }
                }
            }
        } catch (ConsignatariaControllerException | FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private List<TransferObject> lstSolicitacaoLiquidacaoNaoConfirmada(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final Object paramQtdDiasBloqCsaNaoConfirmao = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQUEIO_CSA_NAO_ATENDEU_SOLICITACAO_LIQUIDACAO, AcessoSistema.getAcessoUsuarioSistema());
        final int diasBloqueioNaoConfirmacao = (!TextHelper.isNull(paramQtdDiasBloqCsaNaoConfirmao) && TextHelper.isNum(paramQtdDiasBloqCsaNaoConfirmao)) ? Integer.parseInt(paramQtdDiasBloqCsaNaoConfirmao.toString()) : 0;

        if (diasBloqueioNaoConfirmacao > 0) {
            try {
                final ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery query = new ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery();
                query.diasBloqueioNaoConfirmacao = diasBloqueioNaoConfirmacao;
                query.csaCodigo = csaCodigo;
                return query.executarDTO();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }
        }

        return null;
    }

    /**
     * Envia email consignatarias que tiveram seus contratos ajustados à margem por decisão judicial
     *
     * @param csaCodigo
     * @throws ConsignatariaControllerException
     */
    @Override
    public void enviarEmailNotificacaoConsignacaoAjustadoMargem(List<TransferObject> autDes, AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CustomTransferObject decisaoJudicial = new CustomTransferObject();

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                final Cidade cidade = CidadeHome.findByPrimaryKey(parametros.getCidCodigo());

                decisaoJudicial.setAttribute("numeroProcesso", parametros.getDjuNumProcesso());
                decisaoJudicial.setAttribute("tipoJustica", parametros.getTjuCodigo());
                decisaoJudicial.setAttribute("uf", cidade.getUf().getUfNome());
                decisaoJudicial.setAttribute("comarca", cidade.getCidNome());
                decisaoJudicial.setAttribute("dataDecisao", DateHelper.toDateString(parametros.getDjuData()));
                decisaoJudicial.setAttribute("textoDecisao", parametros.getDjuTexto());
            }

            final ProcessaEnvioEmailCsaAjustaConsignacoesMargem processoEmail = new ProcessaEnvioEmailCsaAjustaConsignacoesMargem(autDes, decisaoJudicial, responsavel);
            processoEmail.start();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * lista consignatárias que tenham convênio ativo com o serviço dado configurado com algum valor.
     *
     * @param tpsCodigo código do parâmetro de serviço de consignante usado como filtro.
     */
    @Override
    public List<TransferObject> lstConsignatariaByTpsCodigo(String tpsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCsaConvenioSvcComTpsCodigoQuery lstCsa = new ListaCsaConvenioSvcComTpsCodigoQuery(tpsCodigo, responsavel);
            return lstCsa.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaCoeficienteAtivoExpirado(Integer diasParaExpiracao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<TransferObject> result = new ArrayList<>();

            final ListaCsaCoeficienteAtivoExpiradoQuery lstCsa = new ListaCsaCoeficienteAtivoExpiradoQuery(diasParaExpiracao);
            final List<TransferObject> lst = lstCsa.executarDTO();

            if (ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {

                final ListaCsaTaxaJurosDiasAntesExpiradoQuery lstCsaTx = new ListaCsaTaxaJurosDiasAntesExpiradoQuery(diasParaExpiracao);
                final List<TransferObject> lstTx = lstCsaTx.executarDTO();

                if ((lst != null) && !lst.isEmpty()) {
                    lst.forEach(ls -> lstTx.forEach(list -> {
                        if (!ls.getAttribute(Columns.CSA_CODIGO).equals(list.getAttribute(Columns.CSA_CODIGO))) {
                            result.add(list);
                        }
                    }));
                } else {
                    result.addAll(lstTx);
                }
            } else {
                result.addAll(lst);
            }
            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private List<TransferObject> lstBloqueioConsignatariaCoeficienteExpirado(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaBloqueioCsaCoeficienteExpiradoQuery lstCsa = new ListaBloqueioCsaCoeficienteExpiradoQuery(csaCodigo);
            return lstCsa.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private List<TransferObject> lstBloqueioConsignatariaTaxaJurosExpirado(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaBloqueioCsaTaxaJurosExpiradoQuery lstCsa = new ListaBloqueioCsaTaxaJurosExpiradoQuery(csaCodigo);
            return lstCsa.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaCoeficienteAtivoDesbloqueado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<TransferObject> result = new ArrayList<>();
            final ListaCsaCoeficienteAtivoDesbloqueadoQuery lstCsa = new ListaCsaCoeficienteAtivoDesbloqueadoQuery();
            final List<TransferObject> lst = lstCsa.executarDTO();

            if (ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {

                final ListCsaTaxaJurosLiberadaQuery lstCsaTx = new ListCsaTaxaJurosLiberadaQuery();
                final List<TransferObject> lstTx = lstCsaTx.executarDTO();

                if ((lst != null) && !lst.isEmpty()) {
                    lst.forEach(ls -> lstTx.forEach(list -> {
                        if (!ls.getAttribute(Columns.CSA_CODIGO).equals(list.getAttribute(Columns.CSA_CODIGO))) {
                            result.add(list);
                        }
                    }));
                } else {
                    result.addAll(lstTx);
                }
            } else {
                result.addAll(lst);
            }
            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstConsignatariaCoeficienteBloqueado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<TransferObject> result = new ArrayList<>();
            final ListaCsaCoeficienteBloqueadoQuery lstCsa = new ListaCsaCoeficienteBloqueadoQuery();
            final List<TransferObject> lst = lstCsa.executarDTO();

            if (ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {

                final ListaBloqueioCsaTaxaJurosExpiradoQuery lstCsaTx = new ListaBloqueioCsaTaxaJurosExpiradoQuery(null);
                final List<TransferObject> lstTx = lstCsaTx.executarDTO();

                if ((lst != null) && !lst.isEmpty()) {
                    lst.forEach(ls -> lstTx.forEach(list -> {
                        if (!ls.getAttribute(Columns.CSA_CODIGO).equals(list.getAttribute(Columns.CSA_CODIGO))) {
                            result.add(list);
                        }
                    }));
                } else {
                    result.addAll(lstTx);
                }
            } else {
                result.addAll(lst);
            }
            return result;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void criarCredenciamentoConsignataria(String csaCodigo, String scrCodigo, Date creDataIni, Date creDataFim, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            CredenciamentoCsaHome.create(csaCodigo, scrCodigo, creDataIni, creDataFim);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCredenciamentoCsaDashboard(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCredenciamentoConsignatariaQuery lstCredenciamento = new ListaCredenciamentoConsignatariaQuery();
            lstCredenciamento.csaCodigo = responsavel.getCsaCodigo();
            return lstCredenciamento.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public List<StatusCredenciamento> lstStatusCredenciamentoByScrCodigos(List<String> scrCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return StatusCredenciamentoHome.lstStatusCredByScrCodigos(scrCodigos);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCredenciamentoCsaDashboardFiltro(Date creDataIni, Date creDataFim, List<String> scrCodigos, List<String> csaCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCredenciamentoConsignatariaQuery lstCredenciamento = new ListaCredenciamentoConsignatariaQuery();
            lstCredenciamento.csaCodigo = responsavel.getCsaCodigo();
            lstCredenciamento.creDataIni = creDataIni;
            lstCredenciamento.creDataFim = creDataFim;
            lstCredenciamento.scrCodigos = scrCodigos;
            lstCredenciamento.csaCodigos = csaCodigos;
            return lstCredenciamento.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstLimiteMargemCsaOrgByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaLimiteMargemCsaOrgByCsaCodigoQuery lstLimiteMargemCsaOrg = new ListaLimiteMargemCsaOrgByCsaCodigoQuery();
            lstLimiteMargemCsaOrg.csaCodigo = csaCodigo;
            return lstLimiteMargemCsaOrg.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public void salvarLimiteMargemCsaOrg(List<LimiteMargemCsaOrg> lstLimiteMargemCsaOrg, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            for (final LimiteMargemCsaOrg limiteMargem : lstLimiteMargemCsaOrg) {
                AbstractEntityHome.update(limiteMargem);
            }
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.criar", responsavel, ex);
        }
    }

    @Override
    public List<OcorrenciaCredenciamentoCsa> lstOcorrenciaCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return OcorrenciaCredenciamentoCsaHome.findByCreCodigo(creCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.ocorrencia", responsavel, ex);
        }
    }

    @Override
    public List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return AnexoCredenciamentoCsaHome.findByCreCodigo(creCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.anexo", responsavel, ex);
        }
    }

    @Override
    public CredenciamentoCsa findByCsaCodigoCredenciamentoCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return CredenciamentoCsaHome.findByCsaCodigo(csaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.nao.existe", responsavel, ex);
        }
    }

    @Override
    public CredenciamentoCsa findByCreCodigoCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.nao.existe", responsavel, ex);
        }
    }

    @Override
    public String registrarAnexoCredenciamentoCsa(String creCodigo, String nomesAnexo, String tarCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final AnexoCredenciamentoCsa anexoCredenciamentoCsa = AnexoCredenciamentoCsaHome.create(nomesAnexo, creCodigo, tarCodigo, responsavel);
            return anexoCredenciamentoCsa.getAncCodigo();
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.anexo", responsavel, ex);
        }
    }

    @Override
    public void alterarStatusNotificarCseCredenciamento(String creCodigo, List<String> anexos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo())) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_CSA_ENVIADO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.credenciamento.enviado", responsavel), null, responsavel.getIpUsuario());

                final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
                EnviaEmailHelper.enviarEmailNotificacaoCseModuloCredenciamento(cse.getCseEmail(), credenciamentoCsa.getConsignataria().getCsaNomeAbrev(), anexos, responsavel);
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.anexo", responsavel, ex);
        }
    }

    @Override
    public void aprovarCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo())) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_CSA_APROVADO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.credenciamento.aprovado", responsavel), null, responsavel.getIpUsuario());

                EnviaEmailHelper.notificarCsaAprovacaoDocumentacaoCsa(credenciamentoCsa, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.aprovacao.email", responsavel), responsavel);
            }

        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.aprovacao", responsavel, ex);
        }
    }

    @Override
    public void reprovarCredenciamentoCsa(String creCodigo, String tmoCodigo, String tmoObs, boolean reprovarAssTermo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo()) && !reprovarAssTermo) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_CSA_REPROVADO, tmoObs, tmoCodigo, responsavel.getIpUsuario());

                EnviaEmailHelper.notificarCsaSituacaoCredenciamento(credenciamentoCsa, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.reprovacao.email", responsavel), responsavel);
            } else if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo()) && reprovarAssTermo) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                final String mensagem = TextHelper.isNull(tmoObs) ? ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.credenciamento.termo.ass.reprovado", responsavel) : tmoObs;
                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_TERMO_ASS_REPROV, mensagem, null, responsavel.getIpUsuario());
                EnviaEmailHelper.notificarCsaSituacaoCredenciamento(credenciamentoCsa, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.reprovacao.email.ass.termo", responsavel), responsavel);
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.reprovacao", responsavel, ex);
        }
    }

    @Override
    public void preencherTermoCredenciamentoCsa(String creCodigo, String anexoTermoPreenchido, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_TERMO_PREENCHIDO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.credenciamento.termo.preenchido", responsavel), null, responsavel.getIpUsuario());

                final String csaEmail = credenciamentoCsa.getConsignataria().getCsaEmail();
                if (TextHelper.isNull(csaEmail)) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
                    throw new ConsignatariaControllerException("mensagem.erro.email.destinatario.invalido", responsavel);
                }

                EnviaEmailHelper.notificarCsaPreenchimentoTermoAditivoCredenciamento(credenciamentoCsa, anexoTermoPreenchido, responsavel);
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.termo.preenchimento", responsavel, ex);
        }
    }

    @Override
    public List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsaTipoArquivo(String creCodigo, String tarCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return AnexoCredenciamentoCsaHome.findByCreCodigoTarCodigo(creCodigo, tarCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.anexo", responsavel, ex);
        }
    }

    @Override
    public void assinarTermoAditivoCredenciamentoCsa(String creCodigo, String anexoAssTermo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_TERMO_ASSINADO_CSA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.termo.ass.csa", responsavel), null, responsavel.getIpUsuario());

                final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
                EnviaEmailHelper.notificarCseAssTermoAditivo(cse.getCseEmail(), credenciamentoCsa.getConsignataria().getCsaNomeAbrev(), anexoAssTermo, responsavel);
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.termo.ass", responsavel, ex);
        }
    }

    @Override
    public void assinarTermoAditivoCseCredenciamentoCsa(String creCodigo, String anexoAssTermoCse, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            final List<TransferObject> usuarioFuncaoAprovarSup = usuarioController.listarUsuariosFuncaoEspecifica(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA, AcessoSistema.ENTIDADE_SUP, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            final List<TransferObject> usuarioFuncaoAprovarCse = usuarioController.listarUsuariosFuncaoEspecifica(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA, AcessoSistema.ENTIDADE_CSE, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            final List<TransferObject> lstUsuariosFuncaoAprovacao = new ArrayList<>(usuarioFuncaoAprovarSup);
            lstUsuariosFuncaoAprovacao.addAll(usuarioFuncaoAprovarCse);

            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo()) && !lstUsuariosFuncaoAprovacao.isEmpty()) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_TERMO_PREENCHIDO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.termo.ass.cse", responsavel), null, responsavel.getIpUsuario());

                for (final TransferObject usuAprovacao : lstUsuariosFuncaoAprovacao) {
                    final String usuEmail = (String) usuAprovacao.getAttribute(Columns.USU_EMAIL);
                    final String usuNome = (String) usuAprovacao.getAttribute(Columns.USU_NOME);

                    EnviaEmailHelper.notificarUsuAprovacaoAssTermoCseAditivo(usuEmail, usuNome, anexoAssTermoCse, responsavel);
                }
            } else {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.FINALIZADO.getCodigo());
                credenciamentoCsa.setCreDataFim(DateHelper.getSystemDatetime());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_FINALIZADO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.arquivo.termo.finalizado", responsavel), null, responsavel.getIpUsuario());
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException |
                 UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.termo.ass", responsavel, ex);
        }
    }

    @Override
    public void finalizarCredenciamentoCsa(String creCodigo, String anexoAssTermo, boolean desbloquearCsa, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final CredenciamentoCsa credenciamentoCsa = CredenciamentoCsaHome.findByPrimaryKey(creCodigo);
            if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) {
                credenciamentoCsa.setScrCodigo(StatusCredenciamentoEnum.FINALIZADO.getCodigo());
                credenciamentoCsa.setCreDataFim(DateHelper.getSystemDatetime());
                AbstractEntityHome.update(credenciamentoCsa);

                OcorrenciaCredenciamentoCsaHome.create(creCodigo, responsavel.getUsuCodigo(), CodedValues.TOC_DOC_CREDENCIAMENTO_FINALIZADO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.concluido.sucesso", responsavel), null, responsavel.getIpUsuario());

                final int qtdMesesExperiacaoCsa = ParamSist.getIntParamSist(CodedValues.TPC_QTD_MESES_EXPIRACAO_CADASTRO_CSA_CREDENCIAMENTO, 0, responsavel);
                final Date dataExpiracaoCsaCredenciamento = DateHelper.addMonths(DateHelper.getSystemDate(), qtdMesesExperiacaoCsa);
                final ConsignatariaTransferObject consignataria = findConsignataria(credenciamentoCsa.getCsaCodigo(), responsavel);
                consignataria.setCsaDataExpiracao(dataExpiracaoCsaCredenciamento);
                consignataria.setCsaDataExpiracaoCadastral(dataExpiracaoCsaCredenciamento);
                updateConsignataria(consignataria, responsavel);

                if (desbloquearCsa) {
                	desbloqueiaCsa(consignataria, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.consignataria.desbloqueio.credenciamento", responsavel), null, null, responsavel);
                }

                final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
                EnviaEmailHelper.notificarCredenciamentoConcluido(cse.getCseEmail(), credenciamentoCsa.getConsignataria().getCsaNomeAbrev(), credenciamentoCsa.getConsignataria().getCsaEmail(), anexoAssTermo, responsavel);
            }
        } catch (FindException | UpdateException | ViewHelperException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.credenciamento.termo.ass", responsavel, ex);
        }
    }

    @Override
    public int countServicosCsaCetExpirado(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaServicoCsaCoeficienteExpiradoQuery query = new ListaServicoCsaCoeficienteExpiradoQuery(true, csaCodigo);
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosCsaCetExpirado(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaServicoCsaCoeficienteExpiradoQuery query = new ListaServicoCsaCoeficienteExpiradoQuery(false, csaCodigo);

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private final Set<String> filtrarConsignatariaAtivaNaoOrgaoPublico(List<? extends TransferObject> lstConsignatarias) {
        return lstConsignatarias.stream()
                                .filter(t -> t.getAttribute(Columns.CSA_ATIVO).equals(CodedValues.STS_ATIVO))
                                .filter(t -> TextHelper.isNull(t.getAttribute(Columns.NCA_CODIGO)) || !t.getAttribute(Columns.NCA_CODIGO).equals(NaturezaConsignatariaEnum.ORGAO_PUBLICO.getCodigo()))
                                .map(t -> (String) t.getAttribute(Columns.CSA_CODIGO))
                                .collect(Collectors.toSet());
    }

    @Override
    public String getEmailCsaNotificacaoOperacao(String funCodigo, String papCodigoOperador, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        final ConsignatariaTransferObject csa = findConsignataria(csaCodigo, responsavel);
        try {
            final DestinatarioEmailCsa bean = DestinatarioEmailCsaHome.findByPrimaryKey(new DestinatarioEmailCsaId(funCodigo, papCodigoOperador, csaCodigo));
            if ("N".equalsIgnoreCase(bean.getDemReceber())) {
                // Se existe o registro e a consignatária optou por não receber, então retorna nulo
                return null;
            } else {
            	// Verifica se há restrição por serviço
            	final TransferObject registroCsaSvc = getDestinatarioEmailCsaSvcById(new DestinatarioEmailCsaSvcId(funCodigo, papCodigoOperador, csaCodigo, svcCodigo), responsavel);
	            final boolean servicoPermitido = !TextHelper.isNull(registroCsaSvc);
	            if (!servicoPermitido) {
                    return null;
                }
	            // Se existe o registro, o serviço é permitido e a consignatária optou por receber, verifica se tem e-mail específico para o envio
	            return !TextHelper.isNull(bean.getDemEmail()) ? bean.getDemEmail() : csa.getCsaEmail();
            }
        } catch (final FindException ex) {
            // Se a CSA não tem configuração específica sobre a função, então deve retornar
            // o e-mail geral da consignatária
            return csa.getCsaEmail();
        }
    }

    @Override
    public List<TransferObject> lstFuncoesEnvioEmailCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return new FuncoesEnvioEmailCsaQuery(csaCodigo).executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void salvarFuncoesEnvioEmailCsa(List<DestinatarioEmailCsa> listaInc, List<DestinatarioEmailCsa> listaAlt, List<DestinatarioEmailCsa> listaExc, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            if ((listaInc != null) && !listaInc.isEmpty()) {
                for (final DestinatarioEmailCsa demInc : listaInc) {
                    DestinatarioEmailCsaHome.create(demInc.getFunCodigo(), demInc.getPapCodigo(), demInc.getCsaCodigo(), demInc.getDemReceber(), demInc.getDemEmail());

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSA, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setFuncao(demInc.getFunCodigo());
                    log.setPapel(demInc.getPapCodigo());
                    log.setConsignataria(demInc.getCsaCodigo());
                    log.addChangedField(Columns.DEM_RECEBER, demInc.getDemReceber());
                    log.addChangedField(Columns.DEM_EMAIL, demInc.getDemEmail());
                    log.write();
                }
            }
            if ((listaAlt != null) && !listaAlt.isEmpty()) {
                for (final DestinatarioEmailCsa demAlt : listaAlt) {
                    final DestinatarioEmailCsa dem = DestinatarioEmailCsaHome.findByPrimaryKey(demAlt.getFunCodigo(), demAlt.getPapCodigo(), demAlt.getCsaCodigo());
                    final String demReceberOld = dem.getDemReceber();
                    final String demEmailOld = dem.getDemEmail();

                    dem.setDemReceber(demAlt.getDemReceber());
                    dem.setDemEmail(demAlt.getDemEmail());
                    AbstractEntityHome.update(dem);

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSA, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setFuncao(demAlt.getFunCodigo());
                    log.setPapel(demAlt.getPapCodigo());
                    log.setConsignataria(demAlt.getCsaCodigo());
                    if (!demReceberOld.equals(dem.getDemReceber())) {
                        log.addChangedField(Columns.DEM_RECEBER, dem.getDemReceber(), demReceberOld);
                    }
                    if (((demEmailOld != null) && (dem.getDemEmail() == null)) || ((demEmailOld == null) && (dem.getDemEmail() != null)) || ((demEmailOld != null) && (dem.getDemEmail() != null) && !demEmailOld.equals(dem.getDemEmail()))) {
                        log.addChangedField(Columns.DEM_EMAIL, dem.getDemEmail(), demEmailOld);
                    }
                    log.write();
                }
            }
            if ((listaExc != null) && !listaExc.isEmpty()) {
                for (final DestinatarioEmailCsa demExc : listaExc) {
                    final DestinatarioEmailCsa dem = DestinatarioEmailCsaHome.findByPrimaryKey(demExc.getFunCodigo(), demExc.getPapCodigo(), demExc.getCsaCodigo());
                    AbstractEntityHome.remove(dem);

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSA, Log.DELETE, Log.LOG_INFORMACAO);
                    log.setFuncao(demExc.getFunCodigo());
                    log.setPapel(demExc.getPapCodigo());
                    log.setConsignataria(demExc.getCsaCodigo());
                    log.write();
                }
            }
        } catch (CreateException | UpdateException | RemoveException | FindException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public TransferObject getDestinatarioEmailCsaSvcById(DestinatarioEmailCsaSvcId id, AcessoSistema responsavel) throws ConsignatariaControllerException {
    	try {
            final ListaDestinatarioEmailCsaSvcQuery lstDcs = new ListaDestinatarioEmailCsaSvcQuery();
            lstDcs.funCodigo = id.getFunCodigo();
            lstDcs.papCodigo = id.getPapCodigo();
            lstDcs.csaCodigo = id.getCsaCodigo();
            lstDcs.svcCodigo = id.getSvcCodigo();
            final List<TransferObject> registrosCsaSvc = lstDcs.executarDTO();
            return (registrosCsaSvc != null) && !registrosCsaSvc.isEmpty() ? registrosCsaSvc.get(0) : null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosDestinatarioEmailCsaSvc(String funCodigo, String papCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
    	try {
            final ListaDestinatarioEmailCsaSvcQuery lstDcs = new ListaDestinatarioEmailCsaSvcQuery();
            lstDcs.funCodigo = funCodigo;
            lstDcs.papCodigo = papCodigo;
            lstDcs.csaCodigo = csaCodigo;
            final List<TransferObject> registrosCsaSvc = lstDcs.executarDTO();
            return (registrosCsaSvc != null) && !registrosCsaSvc.isEmpty() ? registrosCsaSvc : new ArrayList<>();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public Map<String, List<String>> mapaServicosDestinatarioEmailCsaSvc(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
    	 try {
			final Map<String, List<String>> mapaServicosCsa = new HashMap<>();
			final ListaDestinatarioEmailCsaSvcQuery lstDcs = new ListaDestinatarioEmailCsaSvcQuery();
            lstDcs.csaCodigo = csaCodigo;
            final List<TransferObject> registrosCsaSvc = lstDcs.executarDTO();

			if((registrosCsaSvc != null) && !registrosCsaSvc.isEmpty()) {
				 for (final TransferObject registro : registrosCsaSvc) {
					final String chave = registro.getAttribute(Columns.FUN_CODIGO).toString() + "_" + registro.getAttribute(Columns.PAP_CODIGO).toString();
					final String svcCodigo = registro.getAttribute(Columns.SVC_CODIGO).toString();

					mapaServicosCsa.computeIfAbsent(chave, k -> new ArrayList<>()).add(svcCodigo);
				 }
			 }
			return mapaServicosCsa;
         } catch (final HQueryException ex) {
             LOG.error(ex.getMessage(), ex);
             throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
         }
    }

    @Override
    public void salvarServicosDestinatarioEmailCsaSvc(Map<String, Set<String>> mapaServicosCsa, AcessoSistema responsavel) throws ConsignatariaControllerException {
		try {
			for (final Entry<String, Set<String>> entry : mapaServicosCsa.entrySet()) {
	    		final String[] chave =  entry.getKey().split("_");
	    		final String funCodigo = chave[0];
	    		final String papCodigo = chave[1];
	    		final String csaCodigo = chave[2];

	    		DestinatarioEmailCsaSvcHome.removeAllByFuncaoPapelCsa(funCodigo, papCodigo, csaCodigo);

	    		final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSA_SVC, Log.DELETE, Log.LOG_INFORMACAO);
                log.setFuncao(funCodigo);
                log.setPapel(papCodigo);
                log.setConsignataria(csaCodigo);
                log.write();

	    		final Set<String> servicosNew = entry.getValue();
	    		if(!servicosNew.isEmpty()) {
		    		for(final String svcCodigo : servicosNew) {
		    			DestinatarioEmailCsaSvcHome.create(funCodigo, papCodigo, csaCodigo, svcCodigo);

		    			final LogDelegate log2 = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSA_SVC, Log.CREATE, Log.LOG_INFORMACAO);
		    			log2.setFuncao(funCodigo);
		    			log2.setPapel(papCodigo);
		    			log2.setConsignataria(csaCodigo);
		    			log.setServico(svcCodigo);
		    			log2.write();
		    		}
	    		}
			}
		} catch (FindException | RemoveException | CreateException | LogControllerException ex) {
			LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
		}
    }

    @Override
    public List<TransferObject> lstConsignatariaCoeficienteAtivo(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCsaCoeficienteAtivoQuery lstCsa = new ListaCsaCoeficienteAtivoQuery();
            final List<TransferObject> csaCoeficiente = lstCsa.executarDTO();
            return (csaCoeficiente != null) && !csaCoeficiente.isEmpty() ? csaCoeficiente : new ArrayList<>();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void createRegistroAnexoCsa(AnexoConsignataria anexoConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            AnexoConsignatariaHome.create(anexoConsignataria);
        } catch (final CreateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean excluiAnexoConsignataria(AcessoSistema responsavel, String csaCodigo, String nomeArquivo) throws HQueryException, UpdateException {
        List<TransferObject> search = null;
        try {
            final ObtemAnexoConsignatariaQuery bean = new ObtemAnexoConsignatariaQuery();
            bean.nomeArquivo = nomeArquivo;
            bean.csaCodigo = csaCodigo;
            search = bean.executarDTO();

            final AnexoConsignataria Axc = new AnexoConsignataria();

            if (!search.isEmpty()) {
                Axc.setAxcCodigo((String) search.get(0).getAttribute(Columns.AXC_AXC_CODIGO));
                Axc.setCsaCodigo((String) search.get(0).getAttribute(Columns.AXC_CSA_CODIGO));
                Axc.setUsuCodigo((String) search.get(0).getAttribute(Columns.AXC_USU_CODIGO));
                Axc.setTarCodigo((String) search.get(0).getAttribute(Columns.AXC_TAR_CODIGO));
                Axc.setAxcAtivo(CodedValues.AXC_INATIVO);
                Axc.setAxcNome((String) search.get(0).getAttribute(Columns.AXC_AXC_NOME));
                Axc.setAxcData((Date) search.get(0).getAttribute(Columns.AXC_AXC_DATA));
                Axc.setAxcIpAcesso((String) search.get(0).getAttribute(Columns.AXC_AXC_IP_ACESSO));

                final AnexoConsignatariaHome anexo = new AnexoConsignatariaHome();
                anexo.updateAnexo(Axc);
            } else {
                LOG.debug("não existe registro do arquivo no banco");
                return false;
            }

            return true;
        } catch (final HQueryException e) {
            throw new HQueryException(e);
        } catch (final UpdateException e) {
            throw new UpdateException(e);
        }
    }

    @Override
    public List<TransferObject> lstInformacaoCsaServidor(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws HQueryException {
        try {
            final ListaInformacaoCsaServidorQuery query = new ListaInformacaoCsaServidorQuery();

            if (criterio != null) {
                query.csaCodigo = (String) criterio.getAttribute(Columns.ICS_CSA_CODIGO);
                query.serCodigo = (String) criterio.getAttribute(Columns.ICS_SER_CODIGO);
            }

            if (size != -1) {
                query.maxResults = size;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new HQueryException(e);
        }

    }

    @Override
    public InformacaoCsaServidor findInformacaoCsaServidorByIcsCodigo(String icsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return InformacaoCsaServidorHome.findInformacaoCsaServidorByIcsCodigo(icsCodigo, responsavel);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public InformacaoCsaServidor updateInformacaoCsaServidor(String icsCodigo, String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws UpdateException, ConsignatariaControllerException {
        try {
            return InformacaoCsaServidorHome.update(icsCodigo, csaCodigo, serCodigo, icsValor, responsavel);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.endereco.consignataria.atualizar", responsavel, ex);
        }
    }

    @Override
    public InformacaoCsaServidor createInformacaoCsaServidor(String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws ConsignatariaControllerException, MissingPrimaryKeyException {
        try {
            return InformacaoCsaServidorHome.create(csaCodigo, serCodigo, icsValor, responsavel);
        } catch (CreateException | MissingPrimaryKeyException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.informacao.csa.servidor.erro.criar.novo", responsavel, ex);
        }
    }

    @Override
    public void removeInformacaoCsaServidor(String icsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final InformacaoCsaServidor informacaoCsaServidor = InformacaoCsaServidorHome.findInformacaoCsaServidorByIcsCodigo(icsCodigo, responsavel);
            AbstractEntityHome.remove(informacaoCsaServidor);
        } catch (FindException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void notificaCsaNovosVinculos(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<VinculoRegistroServidor> vinculosRegistroServidor = VinculoRegistroServidorHome.listaNovosVinculos();

            if ((vinculosRegistroServidor != null) && !vinculosRegistroServidor.isEmpty()) {
                final ListaConsignatariasExisteCnvVinculoRegistroServidorQuery query = new ListaConsignatariasExisteCnvVinculoRegistroServidorQuery();
                final List<TransferObject> listaCsaEnviarNotificacao = query.executarDTO();

                EnviaEmailHelper.notificarCsaNovosVinculosRse(vinculosRegistroServidor, listaCsaEnviarNotificacao, responsavel);
            }
        } catch (final HQueryException | ViewHelperException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listaCsaPermiteContato(List<String> csaCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCsaPermiteContatoQuery query = new ListaCsaPermiteContatoQuery();
            query.csaCodigos = csaCodigos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public VinculoConsignataria findVinculoCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return VinculoConsignatariaHome.find(vcsCodigo);
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<VinculoConsignataria> findVinculosCsa(String csa, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return VinculoConsignatariaHome.listVinculosCsa(csa);
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<VinculoRegistroServidor> findVinculosRseParaCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return VinculoRegistroServidorHome.listVinculosRseParaCsa(csaCodigo);
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<VinculoRegistroServidor> findVinculoRseCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return VinculoRegistroServidorHome.findVcsCodigo(vcsCodigo);
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public int salvarEditarVinculoRseCsa(VinculoConsignataria vinculoConsignataria, String vrsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            int verifyCode = 0;
            if (vinculoConsignataria.getVcsCodigo() == null) {
                verifyCode = VinculoConsignatariaHome.verifyDuplic(vinculoConsignataria.getVcsDescricao(), vinculoConsignataria.getVcsIdentificador(), vinculoConsignataria.getCsaCodigo());
                if (verifyCode == 0) {
                    vinculoConsignataria = VinculoConsignatariaHome.create(vinculoConsignataria);
                    if (!vrsCodigo.isEmpty()) {
                        final VinculoCsaRse vinculoCsaRse = new VinculoCsaRse();
                        vinculoCsaRse.setVcsCodigo(vinculoConsignataria.getVcsCodigo());
                        vinculoCsaRse.setVrsCodigo(vrsCodigo);
                        VinculoCsaRseHome.create(vinculoCsaRse);
                    }
                }
            } else {
                AbstractEntityHome.update(vinculoConsignataria);
                final List<VinculoCsaRse> vinculoVerify = VinculoCsaRseHome.verifyExists(vinculoConsignataria.getVcsCodigo());
                final VinculoCsaRse vinculoCsaRsee = !vinculoVerify.isEmpty() ? vinculoVerify.get(0) : null;
                final VinculoCsaRse vinculoCsaRse = new VinculoCsaRse();
                if (vinculoCsaRsee != null) {
                    if (!vrsCodigo.isEmpty()) {
                        AbstractEntityHome.remove(vinculoCsaRsee);
                        vinculoCsaRse.setVcsCodigo(vinculoConsignataria.getVcsCodigo());
                        vinculoCsaRse.setVrsCodigo(vrsCodigo);
                        VinculoCsaRseHome.create(vinculoCsaRse);
                    } else {
                        vinculoCsaRse.setVcsCodigo(vinculoConsignataria.getVcsCodigo());
                        vinculoCsaRse.setVrsCodigo(vinculoCsaRsee.getVrsCodigo());
                        AbstractEntityHome.remove(vinculoCsaRse);
                    }
                } else if (!vrsCodigo.isEmpty()) {
                    vinculoCsaRse.setVcsCodigo(vinculoConsignataria.getVcsCodigo());
                    vinculoCsaRse.setVrsCodigo(vrsCodigo);
                    VinculoCsaRseHome.create(vinculoCsaRse);
                }
            }
            return verifyCode;
        } catch (CreateException | FindException | UpdateException | RemoveException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void excluirVinculoCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<VinculoCsaRse> vinculoVerify = VinculoCsaRseHome.verifyExists(vcsCodigo);
            final VinculoCsaRse vinculoCsaRsee = !vinculoVerify.isEmpty() ? vinculoVerify.get(0) : null;
            if (vinculoCsaRsee != null) {
                AbstractEntityHome.remove(vinculoCsaRsee);
                final VinculoConsignataria vinculoConsignataria = VinculoConsignatariaHome.find(vcsCodigo);
                AbstractEntityHome.remove(vinculoConsignataria);
            } else {
                final VinculoConsignataria vinculoConsignataria = VinculoConsignatariaHome.find(vcsCodigo);
                AbstractEntityHome.remove(vinculoConsignataria);
            }
        } catch (FindException | RemoveException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public VinculoConsignataria findVinculoCsaPorVrsCsa(String csaCodigo, String vrsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<VinculoConsignataria> vinculoConsignatarias = VinculoConsignatariaHome.findByVrsAndCsa(csaCodigo, vrsCodigo);

            if (!vinculoConsignatarias.isEmpty()) {
                return vinculoConsignatarias.get(0);
            }
            return null;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public String impCadastroConsignatarias(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ConsignatariaControllerException {
        int totalIncluidos = 0;
        int totalAlterados = 0;
        int totalRegistros = 0;
        int totalProblema = 0;

        LeitorArquivoTexto leitor;
        Escritor escritor;
        Tradutor tradutor;

        // Verifica permissão
        if (!responsavel.isCseSupOrg() && !responsavel.isSistema()) {
            throw new ConsignatariaControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        // Grava o arquivo de lote no sistema de arquivo
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final StringBuilder pathLote = new StringBuilder().append(absolutePath).append(File.separator).append("cadastroConsignatarias").append(File.separator);

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            pathLote.append("est").append(File.separatorChar).append(responsavel.getCodigoEntidadePai()).append(File.separator);
        } else if (responsavel.isOrg()) {
            pathLote.append("cse").append(java.io.File.separatorChar).append(responsavel.getCodigoEntidade()).append(File.separator);
        } else {
            pathLote.append("cse").append(File.separator);
        }

        // Verifica se o caminho para a gravação existe
        final File dir = new java.io.File(pathLote.toString());
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ConsignatariaControllerException("mensagem.erro.importar.bloqueio.servidor.diretorio.nao.existe", responsavel);
        }

        // Recupera parâmetros de configuração do sistema
        final String pathLoteDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar;

        // Recupera layout de importação dos desligados e bloqueados
        String entradaImpLote = null;
        String tradutorImpLote = null;

        final String entradaImpLoteDefault = pathLoteDefault + "imp_cad_consignatarias_entrada.xml";
        final String tradutorImpLoteDefault = pathLoteDefault + "imp_cad_consignatarias_tradutor.xml";

        final File arqConfEntradaDefault = new File(entradaImpLoteDefault);
        final File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new ConsignatariaControllerException("mensagem.erro.sistema.arquivos.importacao.cadastro.consignatarias.ausentes", responsavel);
        } else {
            entradaImpLote = entradaImpLoteDefault;
            tradutorImpLote = tradutorImpLoteDefault;
        }

        String fileName = pathLote.append(nomeArquivoEntrada).toString();

        // Verifica se o arquivo existe
        final File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            LOG.error("Arquivo não encontrado: " + fileName);
            throw new ConsignatariaControllerException("mensagem.erro.sistema.arquivo.cadastro.consignatarias.nao.encontrado", responsavel);
        }

        if (!validar) {
            // Renomeia o arquivo que será processado para que não ocorra duplicação do processamento
            FileHelper.rename(fileName, fileName + ".prc");
            fileName += ".prc";
        }

        LOG.debug("nome do arquivo ... " + fileName);
        leitor = new LeitorArquivoTexto(entradaImpLote, fileName);

        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        final HashMap<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        escritor = new EscritorMemoria(entrada);
        tradutor = new Tradutor(tradutorImpLote, leitor, escritor);

        final String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();

        final List<String> critica = new ArrayList<>();
        try {
            final ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);
            if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
                critica.add(ApplicationResourcesHelper.getMessage("rotulo.critica.operacao.temporariamente.indisponivel", responsavel, restricao.getDescricao()));
            }
        } catch (final ZetraException e) {
            critica.add(ApplicationResourcesHelper.getMessage("mensagem.erro.critica.operacao.temporariamente.indisponivel", responsavel));
        }

        String msgErro;
        try {
            tradutor.iniciaTraducao();
        } catch (final ParserException e) {
            LOG.error("Erro em iniciar tradução.");
            throw new ConsignatariaControllerException(e);
        }

        boolean proximo = true;
        try {
            // Faz o loop de cada linha do arquivo para realizar as traduções
            while (proximo) {
                try {
                    proximo = tradutor.traduzProximo();

                    // Realiza a validação de segurança contra ataque de XSS nos campos do lote
                    for (final String key : entrada.keySet()) {
                        final Object value = entrada.get(key);
                        if (value instanceof String) {
                            // Se for String, realiza o tratamento anti-XSS
                            entrada.put(key, XSSPreventionFilter.stripXSS((String) value));
                        }
                    }

                    if (!proximo) {
                        break;
                    }

                    msgErro = "";

                    // Criar rotina para importação de arquivo de desligados e bloqueados, configurada em leiaute XML
                    if ((entrada.get(LINHA_INVALIDA) == null) || "N".equals(entrada.get(LINHA_INVALIDA).toString())) {
                        final String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
                        final String csaNome = (String) entrada.get("CSA_NOME");
                        final String csaNomeAbrev = (String) entrada.get("CSA_NOME_ABREV");
                        final String csaNroContrato = (String) entrada.get("CSA_NRO_CONTRATO");
                        final String csaDataRenovacaoContrato = (String) entrada.get("CSA_DATA_RENOVACAO_CONTRATO");
                        final String csaDataExpiracaoCadastral = (String) entrada.get("CSA_DATA_EXPIRACAO_CADASTRAL");
                        final String csaCnpj = (String) entrada.get("CSA_CNPJ");
                        final String csaResponsavel = (String) entrada.get("CSA_RESPONSAVEL");
                        final String csaLogradouro = (String) entrada.get("CSA_LOGRADOURO");
                        final String csaNro = (String) entrada.get("CSA_NRO");
                        final String csaBairro = (String) entrada.get("CSA_BAIRRO");
                        final String csaCidade = (String) entrada.get("CSA_CIDADE");
                        final String csaEmail = (String) entrada.get("CSA_EMAIL");
                        final String ncaCodigo = (String) entrada.get("NCA_CODIGO");
                        final String csaNroBco = (String) entrada.get("CSA_NRO_BCO");
                        final String csaNroAge = (String) entrada.get("CSA_NRO_AGE");
                        final String csaNroCta = (String) entrada.get("CSA_NRO_CTA");
                        final String csaDigCta = (String) entrada.get("CSA_DIG_CTA");
                        final String csaContato = (String) entrada.get("CSA_CONTATO");
                        final String csaContatoTel = (String) entrada.get("CSA_CONTATO_TEL");
                        final String csaRespCargo = (String) entrada.get("CSA_RESP_CARGO");
                        final String csaRespTelefone = (String) entrada.get("CSA_RESP_TELEFONE");
                        final String csaResponsavel2 = (String) entrada.get("CSA_RESPONSAVEL_2");
                        final String csaRespCargo2 = (String) entrada.get("CSA_RESP_CARGO_2");
                        final String csaRespTelefone2 = (String) entrada.get("CSA_RESP_TELEFONE_2");
                        final String csaResponsavel3 = (String) entrada.get("CSA_RESPONSAVEL_3");
                        final String csacsaRespCargo3 = (String) entrada.get("CSA_RESP_CARGO_3");
                        final String csaRespTelefone3 = (String) entrada.get("CSA_RESP_TELEFONE_3");
                        final String csaCompl = (String) entrada.get("CSA_COMPL");
                        final String csaUf = (String) entrada.get("CSA_UF");
                        final String csaCep = (String) entrada.get("CSA_CEP");
                        final String csaTel = (String) entrada.get("CSA_TEL");
                        final String csaFax = (String) entrada.get("CSA_FAX");
                        final String csacsaEndereco2 = (String) entrada.get("CSA_ENDERECO_2");
                        final String csaPermiteIncluirAde = TextHelper.isNull(entrada.get("CSA_PERMITE_INCLUIR_ADE")) ? CodedValues.TPC_SIM : (String) entrada.get("CSA_PERMITE_INCLUIR_ADE");
                        final String csaAtivo = TextHelper.isNull(entrada.get("CSA_ATIVO")) ? CodedValues.PSE_BOOLEANO_SIM : (String) entrada.get("CSA_ATIVO");

                        if (TextHelper.isNull(csaIdentificador)) {
                            throw new ConsignatariaControllerException("mensagem.erro.cadastro.consignataria.csa.identificador.obrigatorio", responsavel);
                        }

                        if (TextHelper.isNull(csaNome)) {
                            throw new ConsignatariaControllerException("mensagem.erro.cadastro.consignataria.csa.nome.obrigatorio", responsavel);
                        }

                        if (TextHelper.isNull(csaEmail)) {
                            throw new ConsignatariaControllerException("mensagem.erro.cadastro.consignataria.csa.email.obrigatorio", responsavel);
                        }

                        if (TextHelper.isNull(ncaCodigo)) {
                            throw new ConsignatariaControllerException("mensagem.erro.cadastro.consignataria.csa.nca.obrigatorio", responsavel);
                        }

                        ConsignatariaTransferObject consignataria = new ConsignatariaTransferObject();
                        boolean existeConsignataria = true;
                        try {
                            consignataria = findConsignatariaByIdn(csaIdentificador, responsavel);
                        } catch (final ConsignatariaControllerException ex) {
                            if ("mensagem.erro.consignataria.nao.encontrada".equals(ex.getMessageKey())) {
                                //Não faz nada, pois se não encontrou iremos criar
                                existeConsignataria = false;
                            } else {
                                LOG.error(ex.getMessage(), ex);
                                throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel);
                            }
                        }

                        consignataria.setCsaIdentificador(csaIdentificador);
                        consignataria.setCsaNome(csaNome);
                        if (TextHelper.isNull(csaNomeAbrev)) {
                            consignataria.setCsaNomeAbreviado(csaNome);
                        } else {
                            consignataria.setCsaNomeAbreviado(csaNomeAbrev);
                        }
                        consignataria.setCsaNroContrato(csaNroContrato);
                        consignataria.setCsaCnpj(csaCnpj);
                        consignataria.setCsaResponsavel(csaResponsavel);
                        consignataria.setCsaLogradouro(csaLogradouro);
                        consignataria.setCsaNro(Integer.valueOf(csaNro));
                        consignataria.setCsaBairro(csaBairro);
                        consignataria.setCsaCidade(csaCidade);
                        consignataria.setCsaEmail(csaEmail);
                        consignataria.setCsaNcaNatureza(ncaCodigo);
                        consignataria.setCsaPermiteApi(CodedValues.PSC_BOOLEANO_NAO);

                        if (!TextHelper.isNull(csaDataRenovacaoContrato)) {
                            consignataria.setCsaDataRenovacaoContrato(DateHelper.objectToDate(csaDataRenovacaoContrato));
                        }

                        if (!TextHelper.isNull(csaDataExpiracaoCadastral)) {
                            consignataria.setCsaDataExpiracaoCadastral(DateHelper.objectToDate(csaDataExpiracaoCadastral));
                        }

                        consignataria.setCsaNroBco(csaNroBco);
                        consignataria.setCsaNroAge(csaNroAge);
                        consignataria.setCsaNroCta(csaNroCta);
                        consignataria.setCsaDigCta(csaDigCta);
                        consignataria.setCsaContato(csaContato);
                        consignataria.setCsaContatoTel(csaContatoTel);
                        consignataria.setCsaRespCargo(csaRespCargo);
                        consignataria.setCsaRespTelefone(csaRespTelefone);
                        consignataria.setCsaResponsavel2(csaResponsavel2);
                        consignataria.setCsaRespCargo2(csaRespCargo2);
                        consignataria.setCsaRespTelefone2(csaRespTelefone2);
                        consignataria.setCsaResponsavel3(csaResponsavel3);
                        consignataria.setCsaRespCargo3(csacsaRespCargo3);
                        consignataria.setCsaRespTelefone3(csaRespTelefone3);
                        consignataria.setCsaCompl(csaCompl);
                        consignataria.setCsaUf(csaUf);
                        consignataria.setCsaCep(csaCep);
                        consignataria.setCsaTel(csaTel);
                        consignataria.setCsaFax(csaFax);
                        consignataria.setCsaEndereco2(csacsaEndereco2);
                        consignataria.setCsaPermiteIncluirAde(csaPermiteIncluirAde);
                        consignataria.setCsaAtivo(Short.valueOf(csaAtivo));

                        if (!validar) {
                            if (existeConsignataria) {
                                updateConsignataria(consignataria, responsavel);
                            } else {
                                createConsignataria(consignataria, responsavel);
                            }
                        } else if (existeConsignataria) {
                            totalAlterados++;
                            critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, ApplicationResourcesHelper.getMessage("mensagem.info.cadastro.consignataria.alterado.validado", responsavel)));
                        } else {
                            totalIncluidos++;
                            critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, ApplicationResourcesHelper.getMessage("mensagem.info.cadastro.consignataria.inclusao.validado", responsavel)));
                        }
                    } else {
                        totalProblema++;
                        msgErro = "S".equalsIgnoreCase(entrada.get(LINHA_INVALIDA).toString()) ? ApplicationResourcesHelper.getMessage("mensagem.linhaInvalida", responsavel) : entrada.get(LINHA_INVALIDA).toString();
                        critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                } catch (final ParserException e) {
                    if ((e.getMessageKey() != null) && ((e.getMessageKey().indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1) || (e.getMessageKey().indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1))) {
                        throw new ConsignatariaControllerException(e);
                    }

                    LOG.error("Erro de Parser no Importar Cadastro de consignatárias : " + e.getMessage(), e);

                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, e.getMessage()));

                } catch (final Exception e) {
                    String mensagem = e.getMessage();
                    if (e instanceof final ZetraException ze) {
                        mensagem = ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE);
                        if ((ze.getMessageKey() != null) && "mensagem.linhaInvalida".equals(ze.getMessageKey())) {
                            mensagem += ": " + e.getMessage();
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem));
                }

                totalRegistros++;
            }
        } finally {
            try {
                tradutor.encerraTraducao();
            } catch (final ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        String nomeArqSaida = null;
        String nomeArqSaidaTxt = null;
        String nomeArqSaidaZip = null;

        try {
            if (!critica.isEmpty()) {
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

                final String pathSaida = pathLote.toString();

                if (!validar) {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                } else {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel);
                }

                nomeArqSaida += nomeArquivoEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                final PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (validar) {
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.arquivo", responsavel, nomeArquivoEntrada), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados", responsavel, String.valueOf(totalRegistros)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados.inclusao", responsavel, String.valueOf(totalIncluidos)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados.alteracao", responsavel, String.valueOf(totalAlterados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.invalidos", responsavel, String.valueOf(totalProblema)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }

                if ((leitor.getLinhaHeader() != null) && !"".equals(leitor.getLinhaHeader().trim())) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.lineSeparator()));
                if ((leitor.getLinhaFooter() != null) && !"".equals(leitor.getLinhaFooter().trim())) {
                    // Imprime a linha de footer no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null));
                }
                arqSaida.close();

                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);
            }
        } catch (final IOException ex) {
            throw new ConsignatariaControllerException(ex);
        }

        if (!validar) {
            FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
        }

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return nomeArqSaidaZip;
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        // Concatena a mensagem de erro no final da linha de entrada
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    @Override
    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return listaConsignatariaConsultaMargemSemSenhaByRseCodigoByCsaCodigo(rseCodigo, null, responsavel);
    }

    @Override
    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByRseCodigoByCsaCodigo(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return ConsultaMargemSemSenhaHome.findByRseCodigoCsaCodigo(rseCodigo, csaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            return ConsultaMargemSemSenhaHome.findByCsaCodigo(csaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public ConsultaMargemSemSenha createConsignatariaConsultaMargemSemSenha(String rseCodigo, String csaCodigo, Date cssDataIni, Date cssDataFim, AcessoSistema responsavel) throws ConsignatariaControllerException {

        try {
            if (TextHelper.isNull(rseCodigo)) {
                throw new ConsignatariaControllerException("mensagem.erro.rseCodigo.deve.ser.informado", responsavel);
            }
            if (TextHelper.isNull(csaCodigo)) {
                throw new ConsignatariaControllerException("mensagem.erro.csaCodigo.deve.ser.informado", responsavel);
            }
            if (TextHelper.isNull(cssDataIni)) {
                throw new ConsignatariaControllerException("mensagem.erro.cssDataIni.deve.ser.informada", responsavel);
            }
            if (TextHelper.isNull(cssDataFim)) {
                throw new ConsignatariaControllerException("mensagem.erro.cssDataFim.deve.ser.informada", responsavel);
            }

            final ConsultaMargemSemSenha consultaMargemSemSenha = ConsultaMargemSemSenhaHome.create(rseCodigo, csaCodigo, cssDataIni, cssDataFim);

            final ConsignatariaTransferObject consignataria = findConsignataria(csaCodigo, responsavel);
            final String csaNome = !TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome();
            final String observacao = ApplicationResourcesHelper.getMessage("mensagem.info.autorizacao.sem.senha.ser.autorizado", responsavel, csaNome);
            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_SER_AUTORIZA_CSA_OPERECAR_SEM_SENHA, observacao, null, responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO_CSA_OPERAR_SEM_SENHA, Log.CREATE , Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setConsultaMargemSemSenha(consultaMargemSemSenha.getCssCodigo());
            log.setConsignataria(csaCodigo);
            log.add(observacao);
            log.write();

            return consultaMargemSemSenha;
        } catch (final CreateException | ConsignatariaControllerException | ServidorControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.consultar.margem.consignataria.erro.interno", responsavel, ex);
        }
    }

    @Override
    public ConsultaMargemSemSenha updateConsignatariaConsultaMargemSemSenha(String cssCodigo, String permissao, AcessoSistema responsavel) throws UpdateException, ConsignatariaControllerException {

        try {
            final ConsultaMargemSemSenha consultaMargemSemSenha = ConsultaMargemSemSenhaHome.findByPrimaryKey(cssCodigo);
            if("S".equals(permissao)) {
                consultaMargemSemSenha.setCssDataRevogacaoSer(DateHelper.getSystemDatetime());
            } else {
                consultaMargemSemSenha.setCssDataRevogacaoSup(DateHelper.getSystemDatetime());
            }
            AbstractEntityHome.update(consultaMargemSemSenha);

            final ConsignatariaTransferObject consignataria = findConsignataria(consultaMargemSemSenha.getCsaCodigo(), responsavel);
            final String csaNome = !TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome();
            final String observacao = ApplicationResourcesHelper.getMessage("mensagem.info.autorizacao.sem.senha.ser.regovacao", responsavel, csaNome);
            servidorController.criaOcorrenciaRSE(consultaMargemSemSenha.getRseCodigo(), CodedValues.TOC_SER_REVOGA_AUTORIZACAO_CSA_OPERECAR_SEM_SENHA, observacao, null, responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.REVOGACAO_CSA_OPERAR_SEM_SENHA, Log.UPDATE , Log.LOG_INFORMACAO);
            log.setRegistroServidor(consultaMargemSemSenha.getRseCodigo());
            log.setConsultaMargemSemSenha(consultaMargemSemSenha.getCssCodigo());
            log.setConsignataria(consultaMargemSemSenha.getCsaCodigo());
            log.add(observacao);
            log.write();

            return consultaMargemSemSenha;
        } catch (final UpdateException | FindException | LogControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.consultar.margem.consignataria.erro.interno", responsavel, ex);
        }
    }

    @Override
    public List<ConsultaMargemSemSenha> listaCsaConsultaMargemSemSenhaAlertaPermissaoRetirada(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final List<ConsultaMargemSemSenha> lstCsas = ConsultaMargemSemSenhaHome.findAlertaCsaByRseCodigoCsaCodigo(rseCodigo, csaCodigo);
            for (final ConsultaMargemSemSenha csa : lstCsas) {
                csa.setCssDataAlerta(DateHelper.getSystemDatetime());
                AbstractEntityHome.update(csa);
            }
            return lstCsas;
        } catch (final FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, String> findCsasComOperacoesEmAndamentoByRseCodigo(AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaCsasComAdeStatusAguardoByRseCodigoQuery query = new ListaCsasComAdeStatusAguardoByRseCodigoQuery();
            query.rseCodigo = responsavel.getRseCodigo();

            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public String incluirOcorrenciaConsignataria(String csaCodigo, String tocCodigo, String observacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final OcorrenciaConsignataria occBean = OcorrenciaConsignatariaHome.create(csaCodigo, responsavel.getUsuCodigo(), tocCodigo, observacao, null, null, responsavel.getIpUsuario());
            return occBean.getOccCodigo();
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.ocorrencia.erro.interno", responsavel, ex.getMessage());
        }
    }

    @Override
    public List<TransferObject> listaOccBloqDesbloqVinculosByCsa(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaOccBloqDesbloqVinculosByCsaQuery query = new ListaOccBloqDesbloqVinculosByCsaQuery();
            query.csaCodigo = csaCodigo;
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public int countOccBloqDesbloqVinculosByCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ListaOccBloqDesbloqVinculosByCsaQuery query = new ListaOccBloqDesbloqVinculosByCsaQuery();
            query.csaCodigo = csaCodigo;
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public void enviarEmailNotificacaoVinculosBloqDesbloq(String csaCodigo, List<String> occCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            final ConsignatariaTransferObject consignataria = findConsignataria(csaCodigo, responsavel);
            final String paramEmailCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_NOVO_VINCULO, responsavel);

            if(!TextHelper.isNull(paramEmailCsa)) {
                EnviaEmailHelper.enviarEmailCsaVinculosBloqDesbloq(paramEmailCsa, csaCodigo, consignataria.getCsaNome(), occCodigos);
            } else if(!TextHelper.isNull(consignataria.getCsaEmail())) {
                EnviaEmailHelper.enviarEmailCsaVinculosBloqDesbloq(consignataria.getCsaEmail(), csaCodigo, consignataria.getCsaNome(), occCodigos);
            }
        } catch (ParametroControllerException | ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public List<TransferObject> listaCsaPortabilidadeCartao(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try{
            final ListaConsignatariasPortabilidadeCartaoQuery qr = new ListaConsignatariasPortabilidadeCartaoQuery();
            qr.csaCodigo = csaCodigo;

            return qr.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaCodTituloModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try{
            final ListaCodTituloModeloTermoAditivoQuery qr = new ListaCodTituloModeloTermoAditivoQuery();
            qr.mtaCodigo = mtaCodigo;
            return qr.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public List<ModeloTermoTag> listaTagsModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try{
            return ModeloTermoTagHome.findByMtaCodigo(mtaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

    @Override
    public ModeloTermoAditivo findModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try{
            return ModeloTermoAditivoHome.findByPrimaryKey(mtaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(ex);
        }
    }

	@Override
	public void enviarNotificacaoAlteracaoRegrasConvenio(String csaCodigo, List<RegrasConvenioParametrosBean> dadosAlterados, AcessoSistema responsavel) throws ConsignatariaControllerException {
		try {
			final ConsignatariaTransferObject consignataria = findConsignataria(csaCodigo, responsavel);
			final String emailsCsaRco = consignataria.getCsaEmailNotificacaoRco();
	        if(!TextHelper.isNull(emailsCsaRco)) {
	            EnviaEmailHelper.enviarEmailCsaAlteracaoRegrasConvenio(dadosAlterados, consignataria.getCsaNome(), emailsCsaRco, responsavel);
	        }
		} catch (final ViewHelperException ex) {
			LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
	}
}
