//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3
// Consulte https://eclipse-ee4j.github.io/jaxb-ri
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem.
//


package com.zetra.econsig.webservice.soap.compra.v1;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.zetra.econsig.webservice.soap.compra.v1 package.
 * <p>An ObjectFactory allows you to programmatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private static final javax.xml.namespace.QName _AcompanharCompraContratoCliente_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "cliente");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoConvenio_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "convenio");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoAdeNumero_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "adeNumero");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoMatricula_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "matricula");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoCpf_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "cpf");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoDiasParaBloqueio_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "diasParaBloqueio");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoSaldoDevedorInformado_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "saldoDevedorInformado");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoDiasUteisSemInfoSaldoDevedor_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "diasUteisSemInfoSaldoDevedor");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoSaldoDevedorAprovado_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "saldoDevedorAprovado");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoDiasUteisSemAprovacaoSaldoDevedor_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "diasUteisSemAprovacaoSaldoDevedor");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoSaldoDevedorPago_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "saldoDevedorPago");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoDiasUteisSemPagamentoSaldoDevedor_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "diasUteisSemPagamentoSaldoDevedor");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoContratoLiquidado_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "contratoLiquidado");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoDiasUteisSemLiquidacao_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "diasUteisSemLiquidacao");
    private static final javax.xml.namespace.QName _AcompanharCompraContratoResponseCodRetorno_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "codRetorno");
    private static final javax.xml.namespace.QName _ComprarContratoNovoAdeIdentificador_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "novoAdeIdentificador");
    private static final javax.xml.namespace.QName _ComprarContratoDataNascimento_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "dataNascimento");
    private static final javax.xml.namespace.QName _ComprarContratoValorLiberado_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorLiberado");
    private static final javax.xml.namespace.QName _ComprarContratoCodVerba_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "codVerba");
    private static final javax.xml.namespace.QName _ComprarContratoServicoCodigo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "servicoCodigo");
    private static final javax.xml.namespace.QName _ComprarContratoCarencia_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "carencia");
    private static final javax.xml.namespace.QName _ComprarContratoCorrespondenteCodigo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "correspondenteCodigo");
    private static final javax.xml.namespace.QName _ComprarContratoValorTac_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorTac");
    private static final javax.xml.namespace.QName _ComprarContratoIndice_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "indice");
    private static final javax.xml.namespace.QName _ComprarContratoValorIof_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorIof");
    private static final javax.xml.namespace.QName _ComprarContratoValorMensVin_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorMensVin");
    private static final javax.xml.namespace.QName _ComprarContratoOrgaoCodigo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "orgaoCodigo");
    private static final javax.xml.namespace.QName _ComprarContratoEstabelecimentoCodigo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "estabelecimentoCodigo");
    private static final javax.xml.namespace.QName _ComprarContratoBanco_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "banco");
    private static final javax.xml.namespace.QName _ComprarContratoAgencia_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "agencia");
    private static final javax.xml.namespace.QName _ComprarContratoConta_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "conta");
    private static final javax.xml.namespace.QName _ComprarContratoTaxaJuros_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "taxaJuros");
    private static final javax.xml.namespace.QName _ComprarContratoNaturezaServicoCodigo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "naturezaServicoCodigo");
    private static final javax.xml.namespace.QName _ComprarContratoAnexo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "anexo");
    private static final javax.xml.namespace.QName _ComprarContratoResponseBoleto_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "boleto");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorDataVencimento_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "dataVencimento");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorValorSaldoDevedor2_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorSaldoDevedor2");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorDataVencimento2_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "dataVencimento2");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorValorSaldoDevedor3_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "valorSaldoDevedor3");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorDataVencimento3_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "dataVencimento3");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorNumeroPrestacoes_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "numeroPrestacoes");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorNomeFavorecido_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "nomeFavorecido");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorCnpjFavorecido_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "cnpjFavorecido");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorNumeroContrato_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "numeroContrato");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorLinkBoleto_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "linkBoleto");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorObservacao_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "observacao");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorDetalheSaldoDevedor_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "detalheSaldoDevedor");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorAnexoDsdSaldoCompra_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "anexoDsdSaldoCompra");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorAnexoBoletoDsdSaldo_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "anexoBoletoDsdSaldo");
    private static final javax.xml.namespace.QName _InformarSaldoDevedorPropostaRefinanciamento_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "propostaRefinanciamento");
    private static final javax.xml.namespace.QName _RetirarContratoDaCompraCodigoMotivoOperacao_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "codigoMotivoOperacao");
    private static final javax.xml.namespace.QName _RetirarContratoDaCompraObsMotivoOperacao_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "obsMotivoOperacao");
    private static final javax.xml.namespace.QName _ConsultarConsignacaoParaCompraAdeIdentificador_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "adeIdentificador");
    private static final javax.xml.namespace.QName _ConsultarConsignacaoParaCompraSenhaServidor_QNAME = new javax.xml.namespace.QName("CompraService-v1_0", "senhaServidor");
    private static final javax.xml.namespace.QName _ResumoConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("Resumo", "consignatariaCodigo");
    private static final javax.xml.namespace.QName _BoletoTaxaJuros_QNAME = new javax.xml.namespace.QName("Boleto", "taxaJuros");
    private static final javax.xml.namespace.QName _BoletoConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("Boleto", "consignatariaCodigo");
    private static final javax.xml.namespace.QName _InfoCompraAdeNumero_QNAME = new javax.xml.namespace.QName("InfoCompra", "adeNumero");
    private static final javax.xml.namespace.QName _InfoCompraCodigoConsignataria_QNAME = new javax.xml.namespace.QName("InfoCompra", "codigoConsignataria");
    private static final javax.xml.namespace.QName _InfoCompraNomeConsignataria_QNAME = new javax.xml.namespace.QName("InfoCompra", "nomeConsignataria");
    private static final javax.xml.namespace.QName _InfoCompraNomeServidor_QNAME = new javax.xml.namespace.QName("InfoCompra", "nomeServidor");
    private static final javax.xml.namespace.QName _InfoCompraMatricula_QNAME = new javax.xml.namespace.QName("InfoCompra", "matricula");
    private static final javax.xml.namespace.QName _InfoCompraCpf_QNAME = new javax.xml.namespace.QName("InfoCompra", "cpf");
    private static final javax.xml.namespace.QName _InfoCompraDataCompra_QNAME = new javax.xml.namespace.QName("InfoCompra", "dataCompra");
    private static final javax.xml.namespace.QName _InfoCompraDataInfoSaldoDevedor_QNAME = new javax.xml.namespace.QName("InfoCompra", "dataInfoSaldoDevedor");
    private static final javax.xml.namespace.QName _InfoCompraValorSaldoDevedor_QNAME = new javax.xml.namespace.QName("InfoCompra", "valorSaldoDevedor");
    private static final javax.xml.namespace.QName _InfoCompraDataAprovacaoSaldoDevedor_QNAME = new javax.xml.namespace.QName("InfoCompra", "dataAprovacaoSaldoDevedor");
    private static final javax.xml.namespace.QName _InfoCompraDataPagamentoSaldoDevedor_QNAME = new javax.xml.namespace.QName("InfoCompra", "dataPagamentoSaldoDevedor");
    private static final javax.xml.namespace.QName _InfoCompraSituacao_QNAME = new javax.xml.namespace.QName("InfoCompra", "situacao");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zetra.econsig.webservice.soap.compra.v1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AcompanharCompraContrato }
     *
     * @return
     *     the new instance of {@link AcompanharCompraContrato }
     */
    public AcompanharCompraContrato createAcompanharCompraContrato() {
        return new AcompanharCompraContrato();
    }

    /**
     * Create an instance of {@link AcompanharCompraContratoResponse }
     *
     * @return
     *     the new instance of {@link AcompanharCompraContratoResponse }
     */
    public AcompanharCompraContratoResponse createAcompanharCompraContratoResponse() {
        return new AcompanharCompraContratoResponse();
    }

    /**
     * Create an instance of {@link InfoCompra }
     *
     * @return
     *     the new instance of {@link InfoCompra }
     */
    public InfoCompra createInfoCompra() {
        return new InfoCompra();
    }

    /**
     * Create an instance of {@link ComprarContrato }
     *
     * @return
     *     the new instance of {@link ComprarContrato }
     */
    public ComprarContrato createComprarContrato() {
        return new ComprarContrato();
    }

    /**
     * Create an instance of {@link Anexo }
     *
     * @return
     *     the new instance of {@link Anexo }
     */
    public Anexo createAnexo() {
        return new Anexo();
    }

    /**
     * Create an instance of {@link ComprarContratoResponse }
     *
     * @return
     *     the new instance of {@link ComprarContratoResponse }
     */
    public ComprarContratoResponse createComprarContratoResponse() {
        return new ComprarContratoResponse();
    }

    /**
     * Create an instance of {@link Boleto }
     *
     * @return
     *     the new instance of {@link Boleto }
     */
    public Boleto createBoleto() {
        return new Boleto();
    }

    /**
     * Create an instance of {@link Historico }
     *
     * @return
     *     the new instance of {@link Historico }
     */
    public Historico createHistorico() {
        return new Historico();
    }

    /**
     * Create an instance of {@link Resumo }
     *
     * @return
     *     the new instance of {@link Resumo }
     */
    public Resumo createResumo() {
        return new Resumo();
    }

    /**
     * Create an instance of {@link Servico }
     *
     * @return
     *     the new instance of {@link Servico }
     */
    public Servico createServico() {
        return new Servico();
    }

    /**
     * Create an instance of {@link InformarSaldoDevedor }
     *
     * @return
     *     the new instance of {@link InformarSaldoDevedor }
     */
    public InformarSaldoDevedor createInformarSaldoDevedor() {
        return new InformarSaldoDevedor();
    }

    /**
     * Create an instance of {@link InformarSaldoDevedorResponse }
     *
     * @return
     *     the new instance of {@link InformarSaldoDevedorResponse }
     */
    public InformarSaldoDevedorResponse createInformarSaldoDevedorResponse() {
        return new InformarSaldoDevedorResponse();
    }

    /**
     * Create an instance of {@link InformarPagamentoSaldoDevedor }
     *
     * @return
     *     the new instance of {@link InformarPagamentoSaldoDevedor }
     */
    public InformarPagamentoSaldoDevedor createInformarPagamentoSaldoDevedor() {
        return new InformarPagamentoSaldoDevedor();
    }

    /**
     * Create an instance of {@link InformarPagamentoSaldoDevedorResponse }
     *
     * @return
     *     the new instance of {@link InformarPagamentoSaldoDevedorResponse }
     */
    public InformarPagamentoSaldoDevedorResponse createInformarPagamentoSaldoDevedorResponse() {
        return new InformarPagamentoSaldoDevedorResponse();
    }

    /**
     * Create an instance of {@link RetirarContratoDaCompra }
     *
     * @return
     *     the new instance of {@link RetirarContratoDaCompra }
     */
    public RetirarContratoDaCompra createRetirarContratoDaCompra() {
        return new RetirarContratoDaCompra();
    }

    /**
     * Create an instance of {@link RetirarContratoDaCompraResponse }
     *
     * @return
     *     the new instance of {@link RetirarContratoDaCompraResponse }
     */
    public RetirarContratoDaCompraResponse createRetirarContratoDaCompraResponse() {
        return new RetirarContratoDaCompraResponse();
    }

    /**
     * Create an instance of {@link CancelarCompra }
     *
     * @return
     *     the new instance of {@link CancelarCompra }
     */
    public CancelarCompra createCancelarCompra() {
        return new CancelarCompra();
    }

    /**
     * Create an instance of {@link CancelarCompraResponse }
     *
     * @return
     *     the new instance of {@link CancelarCompraResponse }
     */
    public CancelarCompraResponse createCancelarCompraResponse() {
        return new CancelarCompraResponse();
    }

    /**
     * Create an instance of {@link LiquidarCompra }
     *
     * @return
     *     the new instance of {@link LiquidarCompra }
     */
    public LiquidarCompra createLiquidarCompra() {
        return new LiquidarCompra();
    }

    /**
     * Create an instance of {@link LiquidarCompraResponse }
     *
     * @return
     *     the new instance of {@link LiquidarCompraResponse }
     */
    public LiquidarCompraResponse createLiquidarCompraResponse() {
        return new LiquidarCompraResponse();
    }

    /**
     * Create an instance of {@link RejeitarPgSaldoDevedor }
     *
     * @return
     *     the new instance of {@link RejeitarPgSaldoDevedor }
     */
    public RejeitarPgSaldoDevedor createRejeitarPgSaldoDevedor() {
        return new RejeitarPgSaldoDevedor();
    }

    /**
     * Create an instance of {@link RejeitarPgSaldoDevedorResponse }
     *
     * @return
     *     the new instance of {@link RejeitarPgSaldoDevedorResponse }
     */
    public RejeitarPgSaldoDevedorResponse createRejeitarPgSaldoDevedorResponse() {
        return new RejeitarPgSaldoDevedorResponse();
    }

    /**
     * Create an instance of {@link SolicitarRecalculoSaldoDevedor }
     *
     * @return
     *     the new instance of {@link SolicitarRecalculoSaldoDevedor }
     */
    public SolicitarRecalculoSaldoDevedor createSolicitarRecalculoSaldoDevedor() {
        return new SolicitarRecalculoSaldoDevedor();
    }

    /**
     * Create an instance of {@link SolicitarRecalculoSaldoDevedorResponse }
     *
     * @return
     *     the new instance of {@link SolicitarRecalculoSaldoDevedorResponse }
     */
    public SolicitarRecalculoSaldoDevedorResponse createSolicitarRecalculoSaldoDevedorResponse() {
        return new SolicitarRecalculoSaldoDevedorResponse();
    }

    /**
     * Create an instance of {@link ConsultarConsignacaoParaCompra }
     *
     * @return
     *     the new instance of {@link ConsultarConsignacaoParaCompra }
     */
    public ConsultarConsignacaoParaCompra createConsultarConsignacaoParaCompra() {
        return new ConsultarConsignacaoParaCompra();
    }

    /**
     * Create an instance of {@link ConsultarConsignacaoParaCompraResponse }
     *
     * @return
     *     the new instance of {@link ConsultarConsignacaoParaCompraResponse }
     */
    public ConsultarConsignacaoParaCompraResponse createConsultarConsignacaoParaCompraResponse() {
        return new ConsultarConsignacaoParaCompraResponse();
    }

    /**
     * Create an instance of {@link Servidor }
     *
     * @return
     *     the new instance of {@link Servidor }
     */
    public Servidor createServidor() {
        return new Servidor();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.String> createAcompanharCompraContratoCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.String> createAcompanharCompraContratoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "adeNumero", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Long> createAcompanharCompraContratoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AcompanharCompraContratoAdeNumero_QNAME, java.lang.Long.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "matricula", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.String> createAcompanharCompraContratoMatricula(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoMatricula_QNAME, java.lang.String.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cpf", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.String> createAcompanharCompraContratoCpf(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCpf_QNAME, java.lang.String.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "diasParaBloqueio", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Short> createAcompanharCompraContratoDiasParaBloqueio(java.lang.Short value) {
        return new JAXBElement<>(_AcompanharCompraContratoDiasParaBloqueio_QNAME, java.lang.Short.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "saldoDevedorInformado", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Boolean> createAcompanharCompraContratoSaldoDevedorInformado(java.lang.Boolean value) {
        return new JAXBElement<>(_AcompanharCompraContratoSaldoDevedorInformado_QNAME, java.lang.Boolean.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "diasUteisSemInfoSaldoDevedor", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Short> createAcompanharCompraContratoDiasUteisSemInfoSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_AcompanharCompraContratoDiasUteisSemInfoSaldoDevedor_QNAME, java.lang.Short.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "saldoDevedorAprovado", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Boolean> createAcompanharCompraContratoSaldoDevedorAprovado(java.lang.Boolean value) {
        return new JAXBElement<>(_AcompanharCompraContratoSaldoDevedorAprovado_QNAME, java.lang.Boolean.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "diasUteisSemAprovacaoSaldoDevedor", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Short> createAcompanharCompraContratoDiasUteisSemAprovacaoSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_AcompanharCompraContratoDiasUteisSemAprovacaoSaldoDevedor_QNAME, java.lang.Short.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "saldoDevedorPago", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Boolean> createAcompanharCompraContratoSaldoDevedorPago(java.lang.Boolean value) {
        return new JAXBElement<>(_AcompanharCompraContratoSaldoDevedorPago_QNAME, java.lang.Boolean.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "diasUteisSemPagamentoSaldoDevedor", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Short> createAcompanharCompraContratoDiasUteisSemPagamentoSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_AcompanharCompraContratoDiasUteisSemPagamentoSaldoDevedor_QNAME, java.lang.Short.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "contratoLiquidado", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Boolean> createAcompanharCompraContratoContratoLiquidado(java.lang.Boolean value) {
        return new JAXBElement<>(_AcompanharCompraContratoContratoLiquidado_QNAME, java.lang.Boolean.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "diasUteisSemLiquidacao", scope = AcompanharCompraContrato.class)
    public JAXBElement<java.lang.Short> createAcompanharCompraContratoDiasUteisSemLiquidacao(java.lang.Short value) {
        return new JAXBElement<>(_AcompanharCompraContratoDiasUteisSemLiquidacao_QNAME, java.lang.Short.class, AcompanharCompraContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = AcompanharCompraContratoResponse.class)
    public JAXBElement<java.lang.String> createAcompanharCompraContratoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, AcompanharCompraContratoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "novoAdeIdentificador", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoNovoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoNovoAdeIdentificador_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "dataNascimento", scope = ComprarContrato.class)
    public JAXBElement<XMLGregorianCalendar> createComprarContratoDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ComprarContratoDataNascimento_QNAME, XMLGregorianCalendar.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorLiberado", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Double> createComprarContratoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_ComprarContratoValorLiberado_QNAME, java.lang.Double.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codVerba", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoCodVerba(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoCodVerba_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "servicoCodigo", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoServicoCodigo_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "carencia", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Integer> createComprarContratoCarencia(java.lang.Integer value) {
        return new JAXBElement<>(_ComprarContratoCarencia_QNAME, java.lang.Integer.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "correspondenteCodigo", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoCorrespondenteCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoCorrespondenteCodigo_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorTac", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Double> createComprarContratoValorTac(java.lang.Double value) {
        return new JAXBElement<>(_ComprarContratoValorTac_QNAME, java.lang.Double.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "indice", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoIndice(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoIndice_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorIof", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Double> createComprarContratoValorIof(java.lang.Double value) {
        return new JAXBElement<>(_ComprarContratoValorIof_QNAME, java.lang.Double.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorMensVin", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Double> createComprarContratoValorMensVin(java.lang.Double value) {
        return new JAXBElement<>(_ComprarContratoValorMensVin_QNAME, java.lang.Double.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cpf", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoCpf(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCpf_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "orgaoCodigo", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoOrgaoCodigo_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "estabelecimentoCodigo", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoEstabelecimentoCodigo_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "banco", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoBanco(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoBanco_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "agencia", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoAgencia(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoAgencia_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "conta", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoConta(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoConta_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "taxaJuros", scope = ComprarContrato.class)
    public JAXBElement<java.lang.Double> createComprarContratoTaxaJuros(java.lang.Double value) {
        return new JAXBElement<>(_ComprarContratoTaxaJuros_QNAME, java.lang.Double.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "naturezaServicoCodigo", scope = ComprarContrato.class)
    public JAXBElement<java.lang.String> createComprarContratoNaturezaServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoNaturezaServicoCodigo_QNAME, java.lang.String.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "anexo", scope = ComprarContrato.class)
    public JAXBElement<Anexo> createComprarContratoAnexo(Anexo value) {
        return new JAXBElement<>(_ComprarContratoAnexo_QNAME, Anexo.class, ComprarContrato.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = ComprarContratoResponse.class)
    public JAXBElement<java.lang.String> createComprarContratoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, ComprarContratoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "boleto", scope = ComprarContratoResponse.class)
    public JAXBElement<Boleto> createComprarContratoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_ComprarContratoResponseBoleto_QNAME, Boleto.class, ComprarContratoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "dataVencimento", scope = InformarSaldoDevedor.class)
    public JAXBElement<XMLGregorianCalendar> createInformarSaldoDevedorDataVencimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InformarSaldoDevedorDataVencimento_QNAME, XMLGregorianCalendar.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorSaldoDevedor2", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.Double> createInformarSaldoDevedorValorSaldoDevedor2(java.lang.Double value) {
        return new JAXBElement<>(_InformarSaldoDevedorValorSaldoDevedor2_QNAME, java.lang.Double.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "dataVencimento2", scope = InformarSaldoDevedor.class)
    public JAXBElement<XMLGregorianCalendar> createInformarSaldoDevedorDataVencimento2(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InformarSaldoDevedorDataVencimento2_QNAME, XMLGregorianCalendar.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "valorSaldoDevedor3", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.Double> createInformarSaldoDevedorValorSaldoDevedor3(java.lang.Double value) {
        return new JAXBElement<>(_InformarSaldoDevedorValorSaldoDevedor3_QNAME, java.lang.Double.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "dataVencimento3", scope = InformarSaldoDevedor.class)
    public JAXBElement<XMLGregorianCalendar> createInformarSaldoDevedorDataVencimento3(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InformarSaldoDevedorDataVencimento3_QNAME, XMLGregorianCalendar.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "numeroPrestacoes", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.Short> createInformarSaldoDevedorNumeroPrestacoes(java.lang.Short value) {
        return new JAXBElement<>(_InformarSaldoDevedorNumeroPrestacoes_QNAME, java.lang.Short.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "banco", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorBanco(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoBanco_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "agencia", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorAgencia(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoAgencia_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "conta", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorConta(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoConta_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "nomeFavorecido", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorNomeFavorecido(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorNomeFavorecido_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cnpjFavorecido", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorCnpjFavorecido(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorCnpjFavorecido_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "numeroContrato", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.Long> createInformarSaldoDevedorNumeroContrato(java.lang.Long value) {
        return new JAXBElement<>(_InformarSaldoDevedorNumeroContrato_QNAME, java.lang.Long.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "linkBoleto", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorLinkBoleto(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorLinkBoleto_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "observacao", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorObservacao(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorObservacao_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "detalheSaldoDevedor", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorDetalheSaldoDevedor(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorDetalheSaldoDevedor_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "anexoDsdSaldoCompra", scope = InformarSaldoDevedor.class)
    public JAXBElement<Anexo> createInformarSaldoDevedorAnexoDsdSaldoCompra(Anexo value) {
        return new JAXBElement<>(_InformarSaldoDevedorAnexoDsdSaldoCompra_QNAME, Anexo.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "anexoBoletoDsdSaldo", scope = InformarSaldoDevedor.class)
    public JAXBElement<Anexo> createInformarSaldoDevedorAnexoBoletoDsdSaldo(Anexo value) {
        return new JAXBElement<>(_InformarSaldoDevedorAnexoBoletoDsdSaldo_QNAME, Anexo.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "propostaRefinanciamento", scope = InformarSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorPropostaRefinanciamento(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorPropostaRefinanciamento_QNAME, java.lang.String.class, InformarSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = InformarSaldoDevedorResponse.class)
    public JAXBElement<java.lang.String> createInformarSaldoDevedorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, InformarSaldoDevedorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = InformarPagamentoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarPagamentoSaldoDevedorCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, InformarPagamentoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = InformarPagamentoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarPagamentoSaldoDevedorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, InformarPagamentoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "observacao", scope = InformarPagamentoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createInformarPagamentoSaldoDevedorObservacao(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorObservacao_QNAME, java.lang.String.class, InformarPagamentoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "anexo", scope = InformarPagamentoSaldoDevedor.class)
    public JAXBElement<Anexo> createInformarPagamentoSaldoDevedorAnexo(Anexo value) {
        return new JAXBElement<>(_ComprarContratoAnexo_QNAME, Anexo.class, InformarPagamentoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = InformarPagamentoSaldoDevedorResponse.class)
    public JAXBElement<java.lang.String> createInformarPagamentoSaldoDevedorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, InformarPagamentoSaldoDevedorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = RetirarContratoDaCompra.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, RetirarContratoDaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = RetirarContratoDaCompra.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, RetirarContratoDaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "observacao", scope = RetirarContratoDaCompra.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraObservacao(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorObservacao_QNAME, java.lang.String.class, RetirarContratoDaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codigoMotivoOperacao", scope = RetirarContratoDaCompra.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraCodigoMotivoOperacao_QNAME, java.lang.String.class, RetirarContratoDaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "obsMotivoOperacao", scope = RetirarContratoDaCompra.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraObsMotivoOperacao_QNAME, java.lang.String.class, RetirarContratoDaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = RetirarContratoDaCompraResponse.class)
    public JAXBElement<java.lang.String> createRetirarContratoDaCompraResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, RetirarContratoDaCompraResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = CancelarCompra.class)
    public JAXBElement<java.lang.String> createCancelarCompraCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, CancelarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = CancelarCompra.class)
    public JAXBElement<java.lang.String> createCancelarCompraConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, CancelarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codigoMotivoOperacao", scope = CancelarCompra.class)
    public JAXBElement<java.lang.String> createCancelarCompraCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraCodigoMotivoOperacao_QNAME, java.lang.String.class, CancelarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "obsMotivoOperacao", scope = CancelarCompra.class)
    public JAXBElement<java.lang.String> createCancelarCompraObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraObsMotivoOperacao_QNAME, java.lang.String.class, CancelarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = CancelarCompraResponse.class)
    public JAXBElement<java.lang.String> createCancelarCompraResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, CancelarCompraResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = LiquidarCompra.class)
    public JAXBElement<java.lang.String> createLiquidarCompraCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, LiquidarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = LiquidarCompra.class)
    public JAXBElement<java.lang.String> createLiquidarCompraConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, LiquidarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codigoMotivoOperacao", scope = LiquidarCompra.class)
    public JAXBElement<java.lang.String> createLiquidarCompraCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraCodigoMotivoOperacao_QNAME, java.lang.String.class, LiquidarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "obsMotivoOperacao", scope = LiquidarCompra.class)
    public JAXBElement<java.lang.String> createLiquidarCompraObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_RetirarContratoDaCompraObsMotivoOperacao_QNAME, java.lang.String.class, LiquidarCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = LiquidarCompraResponse.class)
    public JAXBElement<java.lang.String> createLiquidarCompraResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, LiquidarCompraResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = RejeitarPgSaldoDevedor.class)
    public JAXBElement<java.lang.String> createRejeitarPgSaldoDevedorCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, RejeitarPgSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = RejeitarPgSaldoDevedor.class)
    public JAXBElement<java.lang.String> createRejeitarPgSaldoDevedorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, RejeitarPgSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "observacao", scope = RejeitarPgSaldoDevedor.class)
    public JAXBElement<java.lang.String> createRejeitarPgSaldoDevedorObservacao(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorObservacao_QNAME, java.lang.String.class, RejeitarPgSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = RejeitarPgSaldoDevedorResponse.class)
    public JAXBElement<java.lang.String> createRejeitarPgSaldoDevedorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, RejeitarPgSaldoDevedorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = SolicitarRecalculoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createSolicitarRecalculoSaldoDevedorCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, SolicitarRecalculoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = SolicitarRecalculoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createSolicitarRecalculoSaldoDevedorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, SolicitarRecalculoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "observacao", scope = SolicitarRecalculoSaldoDevedor.class)
    public JAXBElement<java.lang.String> createSolicitarRecalculoSaldoDevedorObservacao(java.lang.String value) {
        return new JAXBElement<>(_InformarSaldoDevedorObservacao_QNAME, java.lang.String.class, SolicitarRecalculoSaldoDevedor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = SolicitarRecalculoSaldoDevedorResponse.class)
    public JAXBElement<java.lang.String> createSolicitarRecalculoSaldoDevedorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, SolicitarRecalculoSaldoDevedorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cliente", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraCliente(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCliente_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "convenio", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraConvenio(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoConvenio_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "adeNumero", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.Long> createConsultarConsignacaoParaCompraAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AcompanharCompraContratoAdeNumero_QNAME, java.lang.Long.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "adeIdentificador", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_ConsultarConsignacaoParaCompraAdeIdentificador_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "cpf", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraCpf(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoCpf_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "senhaServidor", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraSenhaServidor(java.lang.String value) {
        return new JAXBElement<>(_ConsultarConsignacaoParaCompraSenhaServidor_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "banco", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraBanco(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoBanco_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "agencia", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraAgencia(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoAgencia_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "conta", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraConta(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoConta_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "orgaoCodigo", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoOrgaoCodigo_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "estabelecimentoCodigo", scope = ConsultarConsignacaoParaCompra.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ComprarContratoEstabelecimentoCodigo_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "codRetorno", scope = ConsultarConsignacaoParaCompraResponse.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoParaCompraResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AcompanharCompraContratoResponseCodRetorno_QNAME, java.lang.String.class, ConsultarConsignacaoParaCompraResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "CompraService-v1_0", name = "boleto", scope = ConsultarConsignacaoParaCompraResponse.class)
    public JAXBElement<Boleto> createConsultarConsignacaoParaCompraResponseBoleto(Boleto value) {
        return new JAXBElement<>(_ComprarContratoResponseBoleto_QNAME, Boleto.class, ConsultarConsignacaoParaCompraResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Resumo", name = "consignatariaCodigo", scope = Resumo.class)
    public JAXBElement<java.lang.String> createResumoConsignatariaCodigo(java.lang.String value) {
        return new JAXBElement<>(_ResumoConsignatariaCodigo_QNAME, java.lang.String.class, Resumo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "Boleto", name = "taxaJuros", scope = Boleto.class)
    public JAXBElement<java.lang.Double> createBoletoTaxaJuros(java.lang.Double value) {
        return new JAXBElement<>(_BoletoTaxaJuros_QNAME, java.lang.Double.class, Boleto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Boleto", name = "consignatariaCodigo", scope = Boleto.class)
    public JAXBElement<java.lang.String> createBoletoConsignatariaCodigo(java.lang.String value) {
        return new JAXBElement<>(_BoletoConsignatariaCodigo_QNAME, java.lang.String.class, Boleto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "adeNumero", scope = InfoCompra.class)
    public JAXBElement<java.lang.Long> createInfoCompraAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_InfoCompraAdeNumero_QNAME, java.lang.Long.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "codigoConsignataria", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraCodigoConsignataria(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraCodigoConsignataria_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "nomeConsignataria", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraNomeConsignataria(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraNomeConsignataria_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "nomeServidor", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraNomeServidor(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraNomeServidor_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "matricula", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraMatricula(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraMatricula_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "cpf", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraCpf(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraCpf_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "dataCompra", scope = InfoCompra.class)
    public JAXBElement<XMLGregorianCalendar> createInfoCompraDataCompra(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InfoCompraDataCompra_QNAME, XMLGregorianCalendar.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "dataInfoSaldoDevedor", scope = InfoCompra.class)
    public JAXBElement<XMLGregorianCalendar> createInfoCompraDataInfoSaldoDevedor(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InfoCompraDataInfoSaldoDevedor_QNAME, XMLGregorianCalendar.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "valorSaldoDevedor", scope = InfoCompra.class)
    public JAXBElement<java.lang.Double> createInfoCompraValorSaldoDevedor(java.lang.Double value) {
        return new JAXBElement<>(_InfoCompraValorSaldoDevedor_QNAME, java.lang.Double.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "dataAprovacaoSaldoDevedor", scope = InfoCompra.class)
    public JAXBElement<XMLGregorianCalendar> createInfoCompraDataAprovacaoSaldoDevedor(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InfoCompraDataAprovacaoSaldoDevedor_QNAME, XMLGregorianCalendar.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "dataPagamentoSaldoDevedor", scope = InfoCompra.class)
    public JAXBElement<XMLGregorianCalendar> createInfoCompraDataPagamentoSaldoDevedor(XMLGregorianCalendar value) {
        return new JAXBElement<>(_InfoCompraDataPagamentoSaldoDevedor_QNAME, XMLGregorianCalendar.class, InfoCompra.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "InfoCompra", name = "situacao", scope = InfoCompra.class)
    public JAXBElement<java.lang.String> createInfoCompraSituacao(java.lang.String value) {
        return new JAXBElement<>(_InfoCompraSituacao_QNAME, java.lang.String.class, InfoCompra.class, value);
    }

}
