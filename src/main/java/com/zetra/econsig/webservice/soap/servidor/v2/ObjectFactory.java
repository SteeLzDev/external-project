//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v2;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zetra.econsig.webservice.soap.servidor.v2 package. 
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

    private static final QName _CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME = new QName("ServidorService-v2_0", "estabelecimentoCodigo");
    private static final QName _CancelarSolicitacaoServidorOrgaoCodigo_QNAME = new QName("ServidorService-v2_0", "orgaoCodigo");
    private static final QName _CancelarSolicitacaoServidorLoginServidor_QNAME = new QName("ServidorService-v2_0", "loginServidor");
    private static final QName _CancelarSolicitacaoServidorCodigoMotivoOperacao_QNAME = new QName("ServidorService-v2_0", "codigoMotivoOperacao");
    private static final QName _CancelarSolicitacaoServidorObsMotivoOperacao_QNAME = new QName("ServidorService-v2_0", "obsMotivoOperacao");
    private static final QName _CancelarSolicitacaoServidorResponseCodRetorno_QNAME = new QName("ServidorService-v2_0", "codRetorno");
    private static final QName _CancelarSolicitacaoServidorResponseBoleto_QNAME = new QName("ServidorService-v2_0", "boleto");
    private static final QName _ConsultarConsignacaoServidorAdeNumero_QNAME = new QName("ServidorService-v2_0", "adeNumero");
    private static final QName _ConsultarDadosCadastraisServidorResponseDadosServidor_QNAME = new QName("ServidorService-v2_0", "dadosServidor");
    private static final QName _ConsultarMargemServidorCodVerba_QNAME = new QName("ServidorService-v2_0", "codVerba");
    private static final QName _ConsultarMargemServidorServicoCodigo_QNAME = new QName("ServidorService-v2_0", "servicoCodigo");
    private static final QName _ConsultarMargemServidorResponseInfoMargem_QNAME = new QName("ServidorService-v2_0", "infoMargem");
    private static final QName _DetalharConsultaConsignacaoServidorAdeIdentificador_QNAME = new QName("ServidorService-v2_0", "adeIdentificador");
    private static final QName _DetalharConsultaConsignacaoServidorConsignatariaCodigo_QNAME = new QName("ServidorService-v2_0", "consignatariaCodigo");
    private static final QName _DetalharConsultaConsignacaoServidorCorrespondenteCodigo_QNAME = new QName("ServidorService-v2_0", "correspondenteCodigo");
    private static final QName _DetalharConsultaConsignacaoServidorCodigoVerba_QNAME = new QName("ServidorService-v2_0", "codigoVerba");
    private static final QName _DetalharConsultaConsignacaoServidorSdvSolicitado_QNAME = new QName("ServidorService-v2_0", "sdvSolicitado");
    private static final QName _DetalharConsultaConsignacaoServidorSdvSolicitadoNaoCadastrado_QNAME = new QName("ServidorService-v2_0", "sdvSolicitadoNaoCadastrado");
    private static final QName _DetalharConsultaConsignacaoServidorSdvSolicitadoCadastrado_QNAME = new QName("ServidorService-v2_0", "sdvSolicitadoCadastrado");
    private static final QName _DetalharConsultaConsignacaoServidorSdvNaoSolicitado_QNAME = new QName("ServidorService-v2_0", "sdvNaoSolicitado");
    private static final QName _DetalharConsultaConsignacaoServidorPeriodo_QNAME = new QName("ServidorService-v2_0", "periodo");
    private static final QName _DetalharConsultaConsignacaoServidorDataInclusaoInicio_QNAME = new QName("ServidorService-v2_0", "dataInclusaoInicio");
    private static final QName _DetalharConsultaConsignacaoServidorDataInclusaoFim_QNAME = new QName("ServidorService-v2_0", "dataInclusaoFim");
    private static final QName _DetalharConsultaConsignacaoServidorIntegraFolha_QNAME = new QName("ServidorService-v2_0", "integraFolha");
    private static final QName _DetalharConsultaConsignacaoServidorCodigoMargem_QNAME = new QName("ServidorService-v2_0", "codigoMargem");
    private static final QName _DetalharConsultaConsignacaoServidorIndice_QNAME = new QName("ServidorService-v2_0", "indice");
    private static final QName _DetalharConsultaConsignacaoServidorSituacaoContrato_QNAME = new QName("ServidorService-v2_0", "situacaoContrato");
    private static final QName _InserirSolicitacaoServidorEndereco_QNAME = new QName("ServidorService-v2_0", "endereco");
    private static final QName _InserirSolicitacaoServidorNumero_QNAME = new QName("ServidorService-v2_0", "numero");
    private static final QName _InserirSolicitacaoServidorComplemento_QNAME = new QName("ServidorService-v2_0", "complemento");
    private static final QName _InserirSolicitacaoServidorBairro_QNAME = new QName("ServidorService-v2_0", "bairro");
    private static final QName _InserirSolicitacaoServidorCidade_QNAME = new QName("ServidorService-v2_0", "cidade");
    private static final QName _InserirSolicitacaoServidorUf_QNAME = new QName("ServidorService-v2_0", "uf");
    private static final QName _InserirSolicitacaoServidorCep_QNAME = new QName("ServidorService-v2_0", "cep");
    private static final QName _InserirSolicitacaoServidorTelefone_QNAME = new QName("ServidorService-v2_0", "telefone");
    private static final QName _InserirSolicitacaoServidorCelular_QNAME = new QName("ServidorService-v2_0", "celular");
    private static final QName _InserirSolicitacaoServidorEmail_QNAME = new QName("ServidorService-v2_0", "email");
    private static final QName _InserirSolicitacaoServidorMunicipioLotacao_QNAME = new QName("ServidorService-v2_0", "municipioLotacao");
    private static final QName _InserirSolicitacaoServidorTermoAceite_QNAME = new QName("ServidorService-v2_0", "termoAceite");
    private static final QName _SimularConsignacaoServidorValorParcela_QNAME = new QName("ServidorService-v2_0", "valorParcela");
    private static final QName _SimularConsignacaoServidorValorLiberado_QNAME = new QName("ServidorService-v2_0", "valorLiberado");
    private static final QName _SimularConsignacaoServidorPrazo_QNAME = new QName("ServidorService-v2_0", "prazo");
    private static final QName _SimularConsignacaoServidorResponsePodeSolicitar_QNAME = new QName("ServidorService-v2_0", "podeSolicitar");
    private static final QName _SimularConsignacaoServidorResponsePodeSimular_QNAME = new QName("ServidorService-v2_0", "podeSimular");
    private static final QName _ConsultarContraChequeServidorResponseContracheque_QNAME = new QName("ServidorService-v2_0", "contracheque");
    private static final QName _GerarSenhaAutorizacaoServidorResponseSenhaServidor_QNAME = new QName("ServidorService-v2_0", "senhaServidor");
    private static final QName _RecuperarPerguntaDadosCadastraisUsuario_QNAME = new QName("ServidorService-v2_0", "usuario");
    private static final QName _RecuperarPerguntaDadosCadastraisResponseNumeroPergunta_QNAME = new QName("ServidorService-v2_0", "numeroPergunta");
    private static final QName _RecuperarPerguntaDadosCadastraisResponseTextoPergunta_QNAME = new QName("ServidorService-v2_0", "textoPergunta");
    private static final QName _CadastrarEmailServidorBanco_QNAME = new QName("ServidorService-v2_0", "banco");
    private static final QName _CadastrarEmailServidorAgencia_QNAME = new QName("ServidorService-v2_0", "agencia");
    private static final QName _CadastrarEmailServidorConta_QNAME = new QName("ServidorService-v2_0", "conta");
    private static final QName _CadastrarEmailServidorExigeGrupoPerguntas_QNAME = new QName("ServidorService-v2_0", "exigeGrupoPerguntas");
    private static final QName _SimulacaoServico_QNAME = new QName("Simulacao", "servico");
    private static final QName _SimulacaoServicoCodigo_QNAME = new QName("Simulacao", "servicoCodigo");
    private static final QName _DadosServidorEmail_QNAME = new QName("DadosServidor", "email");
    private static final QName _ResumoConsignatariaCodigo_QNAME = new QName("Resumo", "consignatariaCodigo");
    private static final QName _BoletoTaxaJuros_QNAME = new QName("Boleto", "taxaJuros");
    private static final QName _BoletoConsignatariaCodigo_QNAME = new QName("Boleto", "consignatariaCodigo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zetra.econsig.webservice.soap.servidor.v2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CancelarSolicitacaoServidor }
     * 
     * @return
     *     the new instance of {@link CancelarSolicitacaoServidor }
     */
    public CancelarSolicitacaoServidor createCancelarSolicitacaoServidor() {
        return new CancelarSolicitacaoServidor();
    }

    /**
     * Create an instance of {@link CancelarSolicitacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link CancelarSolicitacaoServidorResponse }
     */
    public CancelarSolicitacaoServidorResponse createCancelarSolicitacaoServidorResponse() {
        return new CancelarSolicitacaoServidorResponse();
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
     * Create an instance of {@link ConsultarConsignacaoServidor }
     * 
     * @return
     *     the new instance of {@link ConsultarConsignacaoServidor }
     */
    public ConsultarConsignacaoServidor createConsultarConsignacaoServidor() {
        return new ConsultarConsignacaoServidor();
    }

    /**
     * Create an instance of {@link ConsultarConsignacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link ConsultarConsignacaoServidorResponse }
     */
    public ConsultarConsignacaoServidorResponse createConsultarConsignacaoServidorResponse() {
        return new ConsultarConsignacaoServidorResponse();
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
     * Create an instance of {@link ConsultarDadosCadastraisServidor }
     * 
     * @return
     *     the new instance of {@link ConsultarDadosCadastraisServidor }
     */
    public ConsultarDadosCadastraisServidor createConsultarDadosCadastraisServidor() {
        return new ConsultarDadosCadastraisServidor();
    }

    /**
     * Create an instance of {@link ConsultarDadosCadastraisServidorResponse }
     * 
     * @return
     *     the new instance of {@link ConsultarDadosCadastraisServidorResponse }
     */
    public ConsultarDadosCadastraisServidorResponse createConsultarDadosCadastraisServidorResponse() {
        return new ConsultarDadosCadastraisServidorResponse();
    }

    /**
     * Create an instance of {@link DadosServidor }
     * 
     * @return
     *     the new instance of {@link DadosServidor }
     */
    public DadosServidor createDadosServidor() {
        return new DadosServidor();
    }

    /**
     * Create an instance of {@link ConsultarMargemServidor }
     * 
     * @return
     *     the new instance of {@link ConsultarMargemServidor }
     */
    public ConsultarMargemServidor createConsultarMargemServidor() {
        return new ConsultarMargemServidor();
    }

    /**
     * Create an instance of {@link ConsultarMargemServidorResponse }
     * 
     * @return
     *     the new instance of {@link ConsultarMargemServidorResponse }
     */
    public ConsultarMargemServidorResponse createConsultarMargemServidorResponse() {
        return new ConsultarMargemServidorResponse();
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
     * Create an instance of {@link InfoMargem }
     * 
     * @return
     *     the new instance of {@link InfoMargem }
     */
    public InfoMargem createInfoMargem() {
        return new InfoMargem();
    }

    /**
     * Create an instance of {@link DetalharConsultaConsignacaoServidor }
     * 
     * @return
     *     the new instance of {@link DetalharConsultaConsignacaoServidor }
     */
    public DetalharConsultaConsignacaoServidor createDetalharConsultaConsignacaoServidor() {
        return new DetalharConsultaConsignacaoServidor();
    }

    /**
     * Create an instance of {@link SituacaoContrato }
     * 
     * @return
     *     the new instance of {@link SituacaoContrato }
     */
    public SituacaoContrato createSituacaoContrato() {
        return new SituacaoContrato();
    }

    /**
     * Create an instance of {@link DetalharConsultaConsignacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link DetalharConsultaConsignacaoServidorResponse }
     */
    public DetalharConsultaConsignacaoServidorResponse createDetalharConsultaConsignacaoServidorResponse() {
        return new DetalharConsultaConsignacaoServidorResponse();
    }

    /**
     * Create an instance of {@link InserirSolicitacaoServidor }
     * 
     * @return
     *     the new instance of {@link InserirSolicitacaoServidor }
     */
    public InserirSolicitacaoServidor createInserirSolicitacaoServidor() {
        return new InserirSolicitacaoServidor();
    }

    /**
     * Create an instance of {@link InserirSolicitacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link InserirSolicitacaoServidorResponse }
     */
    public InserirSolicitacaoServidorResponse createInserirSolicitacaoServidorResponse() {
        return new InserirSolicitacaoServidorResponse();
    }

    /**
     * Create an instance of {@link SimularConsignacaoServidor }
     * 
     * @return
     *     the new instance of {@link SimularConsignacaoServidor }
     */
    public SimularConsignacaoServidor createSimularConsignacaoServidor() {
        return new SimularConsignacaoServidor();
    }

    /**
     * Create an instance of {@link SimularConsignacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link SimularConsignacaoServidorResponse }
     */
    public SimularConsignacaoServidorResponse createSimularConsignacaoServidorResponse() {
        return new SimularConsignacaoServidorResponse();
    }

    /**
     * Create an instance of {@link Simulacao }
     * 
     * @return
     *     the new instance of {@link Simulacao }
     */
    public Simulacao createSimulacao() {
        return new Simulacao();
    }

    /**
     * Create an instance of {@link ConsultarContraChequeServidor }
     * 
     * @return
     *     the new instance of {@link ConsultarContraChequeServidor }
     */
    public ConsultarContraChequeServidor createConsultarContraChequeServidor() {
        return new ConsultarContraChequeServidor();
    }

    /**
     * Create an instance of {@link ConsultarContraChequeServidorResponse }
     * 
     * @return
     *     the new instance of {@link ConsultarContraChequeServidorResponse }
     */
    public ConsultarContraChequeServidorResponse createConsultarContraChequeServidorResponse() {
        return new ConsultarContraChequeServidorResponse();
    }

    /**
     * Create an instance of {@link Contracheque }
     * 
     * @return
     *     the new instance of {@link Contracheque }
     */
    public Contracheque createContracheque() {
        return new Contracheque();
    }

    /**
     * Create an instance of {@link GerarSenhaAutorizacaoServidor }
     * 
     * @return
     *     the new instance of {@link GerarSenhaAutorizacaoServidor }
     */
    public GerarSenhaAutorizacaoServidor createGerarSenhaAutorizacaoServidor() {
        return new GerarSenhaAutorizacaoServidor();
    }

    /**
     * Create an instance of {@link GerarSenhaAutorizacaoServidorResponse }
     * 
     * @return
     *     the new instance of {@link GerarSenhaAutorizacaoServidorResponse }
     */
    public GerarSenhaAutorizacaoServidorResponse createGerarSenhaAutorizacaoServidorResponse() {
        return new GerarSenhaAutorizacaoServidorResponse();
    }

    /**
     * Create an instance of {@link VerificarLimitesSenhaAutorizacao }
     * 
     * @return
     *     the new instance of {@link VerificarLimitesSenhaAutorizacao }
     */
    public VerificarLimitesSenhaAutorizacao createVerificarLimitesSenhaAutorizacao() {
        return new VerificarLimitesSenhaAutorizacao();
    }

    /**
     * Create an instance of {@link VerificarLimitesSenhaAutorizacaoResponse }
     * 
     * @return
     *     the new instance of {@link VerificarLimitesSenhaAutorizacaoResponse }
     */
    public VerificarLimitesSenhaAutorizacaoResponse createVerificarLimitesSenhaAutorizacaoResponse() {
        return new VerificarLimitesSenhaAutorizacaoResponse();
    }

    /**
     * Create an instance of {@link RecuperarPerguntaDadosCadastrais }
     * 
     * @return
     *     the new instance of {@link RecuperarPerguntaDadosCadastrais }
     */
    public RecuperarPerguntaDadosCadastrais createRecuperarPerguntaDadosCadastrais() {
        return new RecuperarPerguntaDadosCadastrais();
    }

    /**
     * Create an instance of {@link RecuperarPerguntaDadosCadastraisResponse }
     * 
     * @return
     *     the new instance of {@link RecuperarPerguntaDadosCadastraisResponse }
     */
    public RecuperarPerguntaDadosCadastraisResponse createRecuperarPerguntaDadosCadastraisResponse() {
        return new RecuperarPerguntaDadosCadastraisResponse();
    }

    /**
     * Create an instance of {@link VerificarRespostaDadosCadastrais }
     * 
     * @return
     *     the new instance of {@link VerificarRespostaDadosCadastrais }
     */
    public VerificarRespostaDadosCadastrais createVerificarRespostaDadosCadastrais() {
        return new VerificarRespostaDadosCadastrais();
    }

    /**
     * Create an instance of {@link VerificarRespostaDadosCadastraisResponse }
     * 
     * @return
     *     the new instance of {@link VerificarRespostaDadosCadastraisResponse }
     */
    public VerificarRespostaDadosCadastraisResponse createVerificarRespostaDadosCadastraisResponse() {
        return new VerificarRespostaDadosCadastraisResponse();
    }

    /**
     * Create an instance of {@link VerificarEmailServidor }
     * 
     * @return
     *     the new instance of {@link VerificarEmailServidor }
     */
    public VerificarEmailServidor createVerificarEmailServidor() {
        return new VerificarEmailServidor();
    }

    /**
     * Create an instance of {@link VerificarEmailServidorResponse }
     * 
     * @return
     *     the new instance of {@link VerificarEmailServidorResponse }
     */
    public VerificarEmailServidorResponse createVerificarEmailServidorResponse() {
        return new VerificarEmailServidorResponse();
    }

    /**
     * Create an instance of {@link CadastrarEmailServidor }
     * 
     * @return
     *     the new instance of {@link CadastrarEmailServidor }
     */
    public CadastrarEmailServidor createCadastrarEmailServidor() {
        return new CadastrarEmailServidor();
    }

    /**
     * Create an instance of {@link CadastrarEmailServidorResponse }
     * 
     * @return
     *     the new instance of {@link CadastrarEmailServidorResponse }
     */
    public CadastrarEmailServidorResponse createCadastrarEmailServidorResponse() {
        return new CadastrarEmailServidorResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = CancelarSolicitacaoServidor.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, CancelarSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = CancelarSolicitacaoServidor.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, CancelarSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = CancelarSolicitacaoServidor.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, CancelarSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codigoMotivoOperacao", scope = CancelarSolicitacaoServidor.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorCodigoMotivoOperacao(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorCodigoMotivoOperacao_QNAME, String.class, CancelarSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "obsMotivoOperacao", scope = CancelarSolicitacaoServidor.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorObsMotivoOperacao(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorObsMotivoOperacao_QNAME, String.class, CancelarSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = CancelarSolicitacaoServidorResponse.class)
    public JAXBElement<String> createCancelarSolicitacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, CancelarSolicitacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "boleto", scope = CancelarSolicitacaoServidorResponse.class)
    public JAXBElement<Boleto> createCancelarSolicitacaoServidorResponseBoleto(Boleto value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseBoleto_QNAME, Boleto.class, CancelarSolicitacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = ConsultarConsignacaoServidor.class)
    public JAXBElement<String> createConsultarConsignacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, ConsultarConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = ConsultarConsignacaoServidor.class)
    public JAXBElement<String> createConsultarConsignacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, ConsultarConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = ConsultarConsignacaoServidor.class)
    public JAXBElement<String> createConsultarConsignacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, ConsultarConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "adeNumero", scope = ConsultarConsignacaoServidor.class)
    public JAXBElement<Long> createConsultarConsignacaoServidorAdeNumero(Long value) {
        return new JAXBElement<>(_ConsultarConsignacaoServidorAdeNumero_QNAME, Long.class, ConsultarConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = ConsultarConsignacaoServidorResponse.class)
    public JAXBElement<String> createConsultarConsignacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, ConsultarConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "boleto", scope = ConsultarConsignacaoServidorResponse.class)
    public JAXBElement<Boleto> createConsultarConsignacaoServidorResponseBoleto(Boleto value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseBoleto_QNAME, Boleto.class, ConsultarConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = ConsultarDadosCadastraisServidor.class)
    public JAXBElement<String> createConsultarDadosCadastraisServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, ConsultarDadosCadastraisServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = ConsultarDadosCadastraisServidor.class)
    public JAXBElement<String> createConsultarDadosCadastraisServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, ConsultarDadosCadastraisServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = ConsultarDadosCadastraisServidor.class)
    public JAXBElement<String> createConsultarDadosCadastraisServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, ConsultarDadosCadastraisServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = ConsultarDadosCadastraisServidorResponse.class)
    public JAXBElement<String> createConsultarDadosCadastraisServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, ConsultarDadosCadastraisServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DadosServidor }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DadosServidor }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "dadosServidor", scope = ConsultarDadosCadastraisServidorResponse.class)
    public JAXBElement<DadosServidor> createConsultarDadosCadastraisServidorResponseDadosServidor(DadosServidor value) {
        return new JAXBElement<>(_ConsultarDadosCadastraisServidorResponseDadosServidor_QNAME, DadosServidor.class, ConsultarDadosCadastraisServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = ConsultarMargemServidor.class)
    public JAXBElement<String> createConsultarMargemServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, ConsultarMargemServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = ConsultarMargemServidor.class)
    public JAXBElement<String> createConsultarMargemServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, ConsultarMargemServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = ConsultarMargemServidor.class)
    public JAXBElement<String> createConsultarMargemServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, ConsultarMargemServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codVerba", scope = ConsultarMargemServidor.class)
    public JAXBElement<String> createConsultarMargemServidorCodVerba(String value) {
        return new JAXBElement<>(_ConsultarMargemServidorCodVerba_QNAME, String.class, ConsultarMargemServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "servicoCodigo", scope = ConsultarMargemServidor.class)
    public JAXBElement<String> createConsultarMargemServidorServicoCodigo(String value) {
        return new JAXBElement<>(_ConsultarMargemServidorServicoCodigo_QNAME, String.class, ConsultarMargemServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = ConsultarMargemServidorResponse.class)
    public JAXBElement<String> createConsultarMargemServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, ConsultarMargemServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoMargem }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InfoMargem }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "infoMargem", scope = ConsultarMargemServidorResponse.class)
    public JAXBElement<InfoMargem> createConsultarMargemServidorResponseInfoMargem(InfoMargem value) {
        return new JAXBElement<>(_ConsultarMargemServidorResponseInfoMargem_QNAME, InfoMargem.class, ConsultarMargemServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "adeNumero", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Long> createDetalharConsultaConsignacaoServidorAdeNumero(Long value) {
        return new JAXBElement<>(_ConsultarConsignacaoServidorAdeNumero_QNAME, Long.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "adeIdentificador", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorAdeIdentificador(String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorAdeIdentificador_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "consignatariaCodigo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorConsignatariaCodigo(String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorConsignatariaCodigo_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "correspondenteCodigo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorCorrespondenteCodigo(String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorCorrespondenteCodigo_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "servicoCodigo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorServicoCodigo(String value) {
        return new JAXBElement<>(_ConsultarMargemServidorServicoCodigo_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codigoVerba", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorCodigoVerba(String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorCodigoVerba_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "sdvSolicitado", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Boolean> createDetalharConsultaConsignacaoServidorSdvSolicitado(Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorSdvSolicitado_QNAME, Boolean.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "sdvSolicitadoNaoCadastrado", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Boolean> createDetalharConsultaConsignacaoServidorSdvSolicitadoNaoCadastrado(Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorSdvSolicitadoNaoCadastrado_QNAME, Boolean.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "sdvSolicitadoCadastrado", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Boolean> createDetalharConsultaConsignacaoServidorSdvSolicitadoCadastrado(Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorSdvSolicitadoCadastrado_QNAME, Boolean.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "sdvNaoSolicitado", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Boolean> createDetalharConsultaConsignacaoServidorSdvNaoSolicitado(Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorSdvNaoSolicitado_QNAME, Boolean.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "periodo", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoServidorPeriodo(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorPeriodo_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "dataInclusaoInicio", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoServidorDataInclusaoInicio(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorDataInclusaoInicio_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "dataInclusaoFim", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoServidorDataInclusaoFim(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorDataInclusaoFim_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "integraFolha", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Short> createDetalharConsultaConsignacaoServidorIntegraFolha(Short value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorIntegraFolha_QNAME, Short.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codigoMargem", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<Short> createDetalharConsultaConsignacaoServidorCodigoMargem(Short value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorCodigoMargem_QNAME, Short.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "indice", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorIndice(String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorIndice_QNAME, String.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "situacaoContrato", scope = DetalharConsultaConsignacaoServidor.class)
    public JAXBElement<SituacaoContrato> createDetalharConsultaConsignacaoServidorSituacaoContrato(SituacaoContrato value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorSituacaoContrato_QNAME, SituacaoContrato.class, DetalharConsultaConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = DetalharConsultaConsignacaoServidorResponse.class)
    public JAXBElement<String> createDetalharConsultaConsignacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, DetalharConsultaConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "boleto", scope = DetalharConsultaConsignacaoServidorResponse.class)
    public JAXBElement<Boleto> createDetalharConsultaConsignacaoServidorResponseBoleto(Boleto value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseBoleto_QNAME, Boleto.class, DetalharConsultaConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "endereco", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorEndereco(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorEndereco_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "numero", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorNumero(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorNumero_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "complemento", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorComplemento(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorComplemento_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "bairro", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorBairro(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorBairro_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "cidade", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorCidade(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorCidade_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "uf", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorUf(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorUf_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "cep", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorCep(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorCep_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "telefone", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorTelefone(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorTelefone_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "celular", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorCelular(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorCelular_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "email", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorEmail(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorEmail_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "municipioLotacao", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<String> createInserirSolicitacaoServidorMunicipioLotacao(String value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorMunicipioLotacao_QNAME, String.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "termoAceite", scope = InserirSolicitacaoServidor.class)
    public JAXBElement<Boolean> createInserirSolicitacaoServidorTermoAceite(Boolean value) {
        return new JAXBElement<>(_InserirSolicitacaoServidorTermoAceite_QNAME, Boolean.class, InserirSolicitacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = InserirSolicitacaoServidorResponse.class)
    public JAXBElement<String> createInserirSolicitacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, InserirSolicitacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "boleto", scope = InserirSolicitacaoServidorResponse.class)
    public JAXBElement<Boleto> createInserirSolicitacaoServidorResponseBoleto(Boleto value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseBoleto_QNAME, Boleto.class, InserirSolicitacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = SimularConsignacaoServidor.class)
    public JAXBElement<String> createSimularConsignacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = SimularConsignacaoServidor.class)
    public JAXBElement<String> createSimularConsignacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = SimularConsignacaoServidor.class)
    public JAXBElement<String> createSimularConsignacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "servicoCodigo", scope = SimularConsignacaoServidor.class)
    public JAXBElement<String> createSimularConsignacaoServidorServicoCodigo(String value) {
        return new JAXBElement<>(_ConsultarMargemServidorServicoCodigo_QNAME, String.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "valorParcela", scope = SimularConsignacaoServidor.class)
    public JAXBElement<Double> createSimularConsignacaoServidorValorParcela(Double value) {
        return new JAXBElement<>(_SimularConsignacaoServidorValorParcela_QNAME, Double.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "valorLiberado", scope = SimularConsignacaoServidor.class)
    public JAXBElement<Double> createSimularConsignacaoServidorValorLiberado(Double value) {
        return new JAXBElement<>(_SimularConsignacaoServidorValorLiberado_QNAME, Double.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "prazo", scope = SimularConsignacaoServidor.class)
    public JAXBElement<Integer> createSimularConsignacaoServidorPrazo(Integer value) {
        return new JAXBElement<>(_SimularConsignacaoServidorPrazo_QNAME, Integer.class, SimularConsignacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = SimularConsignacaoServidorResponse.class)
    public JAXBElement<String> createSimularConsignacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, SimularConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "podeSolicitar", scope = SimularConsignacaoServidorResponse.class)
    public JAXBElement<Boolean> createSimularConsignacaoServidorResponsePodeSolicitar(Boolean value) {
        return new JAXBElement<>(_SimularConsignacaoServidorResponsePodeSolicitar_QNAME, Boolean.class, SimularConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "podeSimular", scope = SimularConsignacaoServidorResponse.class)
    public JAXBElement<Boolean> createSimularConsignacaoServidorResponsePodeSimular(Boolean value) {
        return new JAXBElement<>(_SimularConsignacaoServidorResponsePodeSimular_QNAME, Boolean.class, SimularConsignacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = ConsultarContraChequeServidor.class)
    public JAXBElement<String> createConsultarContraChequeServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, ConsultarContraChequeServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = ConsultarContraChequeServidor.class)
    public JAXBElement<String> createConsultarContraChequeServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, ConsultarContraChequeServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = ConsultarContraChequeServidor.class)
    public JAXBElement<String> createConsultarContraChequeServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, ConsultarContraChequeServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "periodo", scope = ConsultarContraChequeServidor.class)
    public JAXBElement<XMLGregorianCalendar> createConsultarContraChequeServidorPeriodo(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoServidorPeriodo_QNAME, XMLGregorianCalendar.class, ConsultarContraChequeServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = ConsultarContraChequeServidorResponse.class)
    public JAXBElement<String> createConsultarContraChequeServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, ConsultarContraChequeServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Contracheque }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Contracheque }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "contracheque", scope = ConsultarContraChequeServidorResponse.class)
    public JAXBElement<Contracheque> createConsultarContraChequeServidorResponseContracheque(Contracheque value) {
        return new JAXBElement<>(_ConsultarContraChequeServidorResponseContracheque_QNAME, Contracheque.class, ConsultarContraChequeServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = GerarSenhaAutorizacaoServidor.class)
    public JAXBElement<String> createGerarSenhaAutorizacaoServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, GerarSenhaAutorizacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = GerarSenhaAutorizacaoServidor.class)
    public JAXBElement<String> createGerarSenhaAutorizacaoServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, GerarSenhaAutorizacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = GerarSenhaAutorizacaoServidor.class)
    public JAXBElement<String> createGerarSenhaAutorizacaoServidorLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, GerarSenhaAutorizacaoServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = GerarSenhaAutorizacaoServidorResponse.class)
    public JAXBElement<String> createGerarSenhaAutorizacaoServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, GerarSenhaAutorizacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "senhaServidor", scope = GerarSenhaAutorizacaoServidorResponse.class)
    public JAXBElement<String> createGerarSenhaAutorizacaoServidorResponseSenhaServidor(String value) {
        return new JAXBElement<>(_GerarSenhaAutorizacaoServidorResponseSenhaServidor_QNAME, String.class, GerarSenhaAutorizacaoServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = VerificarLimitesSenhaAutorizacao.class)
    public JAXBElement<String> createVerificarLimitesSenhaAutorizacaoEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, VerificarLimitesSenhaAutorizacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = VerificarLimitesSenhaAutorizacao.class)
    public JAXBElement<String> createVerificarLimitesSenhaAutorizacaoOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, VerificarLimitesSenhaAutorizacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = VerificarLimitesSenhaAutorizacao.class)
    public JAXBElement<String> createVerificarLimitesSenhaAutorizacaoLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, VerificarLimitesSenhaAutorizacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = VerificarLimitesSenhaAutorizacaoResponse.class)
    public JAXBElement<String> createVerificarLimitesSenhaAutorizacaoResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, VerificarLimitesSenhaAutorizacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = RecuperarPerguntaDadosCadastrais.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, RecuperarPerguntaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = RecuperarPerguntaDadosCadastrais.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, RecuperarPerguntaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = RecuperarPerguntaDadosCadastrais.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, RecuperarPerguntaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "usuario", scope = RecuperarPerguntaDadosCadastrais.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisUsuario(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisUsuario_QNAME, String.class, RecuperarPerguntaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = RecuperarPerguntaDadosCadastraisResponse.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, RecuperarPerguntaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "numeroPergunta", scope = RecuperarPerguntaDadosCadastraisResponse.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisResponseNumeroPergunta(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisResponseNumeroPergunta_QNAME, String.class, RecuperarPerguntaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "textoPergunta", scope = RecuperarPerguntaDadosCadastraisResponse.class)
    public JAXBElement<String> createRecuperarPerguntaDadosCadastraisResponseTextoPergunta(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisResponseTextoPergunta_QNAME, String.class, RecuperarPerguntaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = VerificarRespostaDadosCadastrais.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, VerificarRespostaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = VerificarRespostaDadosCadastrais.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, VerificarRespostaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "loginServidor", scope = VerificarRespostaDadosCadastrais.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisLoginServidor(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorLoginServidor_QNAME, String.class, VerificarRespostaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "usuario", scope = VerificarRespostaDadosCadastrais.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisUsuario(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisUsuario_QNAME, String.class, VerificarRespostaDadosCadastrais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = VerificarRespostaDadosCadastraisResponse.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, VerificarRespostaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "numeroPergunta", scope = VerificarRespostaDadosCadastraisResponse.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisResponseNumeroPergunta(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisResponseNumeroPergunta_QNAME, String.class, VerificarRespostaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "textoPergunta", scope = VerificarRespostaDadosCadastraisResponse.class)
    public JAXBElement<String> createVerificarRespostaDadosCadastraisResponseTextoPergunta(String value) {
        return new JAXBElement<>(_RecuperarPerguntaDadosCadastraisResponseTextoPergunta_QNAME, String.class, VerificarRespostaDadosCadastraisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = VerificarEmailServidor.class)
    public JAXBElement<String> createVerificarEmailServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, VerificarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = VerificarEmailServidor.class)
    public JAXBElement<String> createVerificarEmailServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, VerificarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = VerificarEmailServidorResponse.class)
    public JAXBElement<String> createVerificarEmailServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, VerificarEmailServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "estabelecimentoCodigo", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorEstabelecimentoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorEstabelecimentoCodigo_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "orgaoCodigo", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorOrgaoCodigo(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorOrgaoCodigo_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "banco", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorBanco(String value) {
        return new JAXBElement<>(_CadastrarEmailServidorBanco_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "agencia", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorAgencia(String value) {
        return new JAXBElement<>(_CadastrarEmailServidorAgencia_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "conta", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorConta(String value) {
        return new JAXBElement<>(_CadastrarEmailServidorConta_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "exigeGrupoPerguntas", scope = CadastrarEmailServidor.class)
    public JAXBElement<String> createCadastrarEmailServidorExigeGrupoPerguntas(String value) {
        return new JAXBElement<>(_CadastrarEmailServidorExigeGrupoPerguntas_QNAME, String.class, CadastrarEmailServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "ServidorService-v2_0", name = "codRetorno", scope = CadastrarEmailServidorResponse.class)
    public JAXBElement<String> createCadastrarEmailServidorResponseCodRetorno(String value) {
        return new JAXBElement<>(_CancelarSolicitacaoServidorResponseCodRetorno_QNAME, String.class, CadastrarEmailServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "Simulacao", name = "servico", scope = Simulacao.class)
    public JAXBElement<String> createSimulacaoServico(String value) {
        return new JAXBElement<>(_SimulacaoServico_QNAME, String.class, Simulacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "Simulacao", name = "servicoCodigo", scope = Simulacao.class)
    public JAXBElement<String> createSimulacaoServicoCodigo(String value) {
        return new JAXBElement<>(_SimulacaoServicoCodigo_QNAME, String.class, Simulacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "DadosServidor", name = "email", scope = DadosServidor.class)
    public JAXBElement<String> createDadosServidorEmail(String value) {
        return new JAXBElement<>(_DadosServidorEmail_QNAME, String.class, DadosServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "Resumo", name = "consignatariaCodigo", scope = Resumo.class)
    public JAXBElement<String> createResumoConsignatariaCodigo(String value) {
        return new JAXBElement<>(_ResumoConsignatariaCodigo_QNAME, String.class, Resumo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     */
    @XmlElementDecl(namespace = "Boleto", name = "taxaJuros", scope = Boleto.class)
    public JAXBElement<Double> createBoletoTaxaJuros(Double value) {
        return new JAXBElement<>(_BoletoTaxaJuros_QNAME, Double.class, Boleto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "Boleto", name = "consignatariaCodigo", scope = Boleto.class)
    public JAXBElement<String> createBoletoConsignatariaCodigo(String value) {
        return new JAXBElement<>(_BoletoConsignatariaCodigo_QNAME, String.class, Boleto.class, value);
    }

}
