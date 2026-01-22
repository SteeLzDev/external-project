//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3
// Consulte https://eclipse-ee4j.github.io/jaxb-ri
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem.
//


package com.zetra.econsig.webservice.soap.operacional.v4;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.zetra.econsig.webservice.soap.hostahostservice.v4 package.
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

    private static final javax.xml.namespace.QName _AlongarConsignacaoCliente_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cliente");
    private static final javax.xml.namespace.QName _AlongarConsignacaoConvenio_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "convenio");
    private static final javax.xml.namespace.QName _AlongarConsignacaoAdeNumero_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "adeNumero");
    private static final javax.xml.namespace.QName _AlongarConsignacaoAdeIdentificador_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "adeIdentificador");
    private static final javax.xml.namespace.QName _AlongarConsignacaoNovoAdeIdentificador_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "novoAdeIdentificador");
    private static final javax.xml.namespace.QName _AlongarConsignacaoDataNascimento_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataNascimento");
    private static final javax.xml.namespace.QName _AlongarConsignacaoValorLiberado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "valorLiberado");
    private static final javax.xml.namespace.QName _AlongarConsignacaoCodVerba_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "codVerba");
    private static final javax.xml.namespace.QName _AlongarConsignacaoServicoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "servicoCodigo");
    private static final javax.xml.namespace.QName _AlongarConsignacaoCarencia_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "carencia");
    private static final javax.xml.namespace.QName _AlongarConsignacaoCorrespondenteCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "correspondenteCodigo");
    private static final javax.xml.namespace.QName _AlongarConsignacaoValorTac_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "valorTac");
    private static final javax.xml.namespace.QName _AlongarConsignacaoIndice_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "indice");
    private static final javax.xml.namespace.QName _AlongarConsignacaoValorIof_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "valorIof");
    private static final javax.xml.namespace.QName _AlongarConsignacaoValorMensVin_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "valorMensVin");
    private static final javax.xml.namespace.QName _AlongarConsignacaoCpf_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cpf");
    private static final javax.xml.namespace.QName _AlongarConsignacaoOrgaoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "orgaoCodigo");
    private static final javax.xml.namespace.QName _AlongarConsignacaoEstabelecimentoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "estabelecimentoCodigo");
    private static final javax.xml.namespace.QName _AlongarConsignacaoBanco_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "banco");
    private static final javax.xml.namespace.QName _AlongarConsignacaoAgencia_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "agencia");
    private static final javax.xml.namespace.QName _AlongarConsignacaoConta_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "conta");
    private static final javax.xml.namespace.QName _AlongarConsignacaoNaturezaServicoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "naturezaServicoCodigo");
    private static final javax.xml.namespace.QName _AlongarConsignacaoResponseCodRetorno_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "codRetorno");
    private static final javax.xml.namespace.QName _AlongarConsignacaoResponseBoleto_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "boleto");
    private static final javax.xml.namespace.QName _AlterarConsignacaoSenhaServidor_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "senhaServidor");
    private static final javax.xml.namespace.QName _AlterarConsignacaoTaxaJuros_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "taxaJuros");
    private static final javax.xml.namespace.QName _AlterarConsignacaoPeriodo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "periodo");
    private static final javax.xml.namespace.QName _AlterarConsignacaoAnexo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "anexo");
    private static final javax.xml.namespace.QName _AutorizarReservaCodigoMotivoOperacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "codigoMotivoOperacao");
    private static final javax.xml.namespace.QName _AutorizarReservaObsMotivoOperacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "obsMotivoOperacao");
    private static final javax.xml.namespace.QName _ConsultarMargemMatriculaMultipla_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "matriculaMultipla");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoCodigoVerba_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "codigoVerba");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSdvSolicitado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "sdvSolicitado");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSdvSolicitadoNaoCadastrado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "sdvSolicitadoNaoCadastrado");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSdvSolicitadoCadastrado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "sdvSolicitadoCadastrado");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSdvNaoSolicitado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "sdvNaoSolicitado");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoDataInclusaoInicio_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataInclusaoInicio");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoDataInclusaoFim_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataInclusaoFim");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoIntegraFolha_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "integraFolha");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoCodigoMargem_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "codigoMargem");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSituacaoContrato_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "situacaoContrato");
    private static final javax.xml.namespace.QName _DetalharConsultaConsignacaoSituacaoServidor_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "situacaoServidor");
    private static final javax.xml.namespace.QName _IncluirAnexoConsignacaoDescricaoAnexo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "descricaoAnexo");
    private static final javax.xml.namespace.QName _IncluirDadoConsignacaoDadoValor_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dadoValor");
    private static final javax.xml.namespace.QName _InserirSolicitacaoPrazo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "prazo");
    private static final javax.xml.namespace.QName _ListarDadoConsignacaoDadoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dadoCodigo");
    private static final javax.xml.namespace.QName _PesquisarServidorPrimeiroNome_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "primeiroNome");
    private static final javax.xml.namespace.QName _PesquisarServidorUltimoNome_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "ultimoNome");
    private static final javax.xml.namespace.QName _PesquisarServidorEstIdentificador_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "estIdentificador");
    private static final javax.xml.namespace.QName _PesquisarServidorOrgIdentificador_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "orgIdentificador");
    private static final javax.xml.namespace.QName _PesquisarServidorMatricula_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "matricula");
    private static final javax.xml.namespace.QName _ConsultarParametrosResponseParametroSet_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "parametroSet");
    private static final javax.xml.namespace.QName _ConsultarParcelaPrdNumero_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "prdNumero");
    private static final javax.xml.namespace.QName _ConsultarParcelaDataDesconto_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataDesconto");
    private static final javax.xml.namespace.QName _LiquidarParcelaPrdNumero_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "prdNumero");
    private static final javax.xml.namespace.QName _LiquidarParcelaDataDesconto_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataDesconto");
    private static final javax.xml.namespace.QName _CadastrarServidorNome_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nome");
    private static final javax.xml.namespace.QName _CadastrarServidorTitulacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "titulacao");
    private static final javax.xml.namespace.QName _CadastrarServidorNomeMeio_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nomeMeio");
    private static final javax.xml.namespace.QName _CadastrarServidorNomePai_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nomePai");
    private static final javax.xml.namespace.QName _CadastrarServidorNomeMae_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nomeMae");
    private static final javax.xml.namespace.QName _CadastrarServidorNomeConjuge_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nomeConjuge");
    private static final javax.xml.namespace.QName _CadastrarServidorCidadeNascimento_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cidadeNascimento");
    private static final javax.xml.namespace.QName _CadastrarServidorUfNascimento_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "ufNascimento");
    private static final javax.xml.namespace.QName _CadastrarServidorSexo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "sexo");
    private static final javax.xml.namespace.QName _CadastrarServidorEstadoCivil_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "estadoCivil");
    private static final javax.xml.namespace.QName _CadastrarServidorNacionalidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nacionalidade");
    private static final javax.xml.namespace.QName _CadastrarServidorIdentidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "identidade");
    private static final javax.xml.namespace.QName _CadastrarServidorDataIdentidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataIdentidade");
    private static final javax.xml.namespace.QName _CadastrarServidorUfIdentidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "ufIdentidade");
    private static final javax.xml.namespace.QName _CadastrarServidorEmissorIdentidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "emissorIdentidade");
    private static final javax.xml.namespace.QName _CadastrarServidorNumCarteiraTrabalho_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "numCarteiraTrabalho");
    private static final javax.xml.namespace.QName _CadastrarServidorNumPis_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "numPis");
    private static final javax.xml.namespace.QName _CadastrarServidorLogradouro_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "logradouro");
    private static final javax.xml.namespace.QName _CadastrarServidorNro_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "nro");
    private static final javax.xml.namespace.QName _CadastrarServidorComplemento_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "complemento");
    private static final javax.xml.namespace.QName _CadastrarServidorBairro_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "bairro");
    private static final javax.xml.namespace.QName _CadastrarServidorCidade_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cidade");
    private static final javax.xml.namespace.QName _CadastrarServidorUf_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "uf");
    private static final javax.xml.namespace.QName _CadastrarServidorCep_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cep");
    private static final javax.xml.namespace.QName _CadastrarServidorDddTelefone_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dddTelefone");
    private static final javax.xml.namespace.QName _CadastrarServidorTelefone_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "telefone");
    private static final javax.xml.namespace.QName _CadastrarServidorDddCelular_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dddCelular");
    private static final javax.xml.namespace.QName _CadastrarServidorCelular_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "celular");
    private static final javax.xml.namespace.QName _CadastrarServidorEmail_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "email");
    private static final javax.xml.namespace.QName _CadastrarServidorSubOrgaoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "subOrgaoCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorSituacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "situacao");
    private static final javax.xml.namespace.QName _CadastrarServidorCategoria_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "categoria");
    private static final javax.xml.namespace.QName _CadastrarServidorCargoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "cargoCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorMunicipioLotacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "municipioLotacao");
    private static final javax.xml.namespace.QName _CadastrarServidorEstabilizado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "estabilizado");
    private static final javax.xml.namespace.QName _CadastrarServidorDataFimEngajamento_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataFimEngajamento");
    private static final javax.xml.namespace.QName _CadastrarServidorDataLimitePermanencia_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataLimitePermanencia");
    private static final javax.xml.namespace.QName _CadastrarServidorVinculoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "vinculoCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorClt_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "clt");
    private static final javax.xml.namespace.QName _CadastrarServidorDataAdmissao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataAdmissao");
    private static final javax.xml.namespace.QName _CadastrarServidorDataContracheque_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "dataContracheque");
    private static final javax.xml.namespace.QName _CadastrarServidorPrazoServidor_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "prazoServidor");
    private static final javax.xml.namespace.QName _CadastrarServidorBancoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "bancoCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorBancoAlternativo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "bancoAlternativo");
    private static final javax.xml.namespace.QName _CadastrarServidorAgenciaAlternativa_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "agenciaAlternativa");
    private static final javax.xml.namespace.QName _CadastrarServidorContaAlternativa_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "contaAlternativa");
    private static final javax.xml.namespace.QName _CadastrarServidorObservacao_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "observacao");
    private static final javax.xml.namespace.QName _CadastrarServidorPraca_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "praca");
    private static final javax.xml.namespace.QName _CadastrarServidorSalario_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "salario");
    private static final javax.xml.namespace.QName _CadastrarServidorProventos_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "proventos");
    private static final javax.xml.namespace.QName _CadastrarServidorDescontosCompulsorios_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "descontosCompulsorios");
    private static final javax.xml.namespace.QName _CadastrarServidorDescontosFacultativos_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "descontosFacultativos");
    private static final javax.xml.namespace.QName _CadastrarServidorBaseCalculo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "baseCalculo");
    private static final javax.xml.namespace.QName _CadastrarServidorAssociado_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "associado");
    private static final javax.xml.namespace.QName _CadastrarServidorOutrosDescontos_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "outrosDescontos");
    private static final javax.xml.namespace.QName _CadastrarServidorMatriculaInstitucional_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "matriculaInstitucional");
    private static final javax.xml.namespace.QName _CadastrarServidorPadraoCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "padraoCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorUnidadeCodigo_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "unidadeCodigo");
    private static final javax.xml.namespace.QName _CadastrarServidorResponseServidor_QNAME = new javax.xml.namespace.QName("HostaHostService-v4_0", "servidor");
    private static final javax.xml.namespace.QName _ParametroSetQtdMaxParcelas_QNAME = new javax.xml.namespace.QName("ParametroSet", "qtdMaxParcelas");
    private static final javax.xml.namespace.QName _ParametroSetDiasInfoSaldoDevedor_QNAME = new javax.xml.namespace.QName("ParametroSet", "diasInfoSaldoDevedor");
    private static final javax.xml.namespace.QName _ParametroSetDiasAprovSaldoDevedor_QNAME = new javax.xml.namespace.QName("ParametroSet", "diasAprovSaldoDevedor");
    private static final javax.xml.namespace.QName _ParametroSetDiasInfoPgSaldoDevedor_QNAME = new javax.xml.namespace.QName("ParametroSet", "diasInfoPgSaldoDevedor");
    private static final javax.xml.namespace.QName _ParametroSetDiasLiquidacaoAdeCompra_QNAME = new javax.xml.namespace.QName("ParametroSet", "diasLiquidacaoAdeCompra");
    private static final javax.xml.namespace.QName _ParametroSetUsaCet_QNAME = new javax.xml.namespace.QName("ParametroSet", "usaCet");
    private static final javax.xml.namespace.QName _SimulacaoServico_QNAME = new javax.xml.namespace.QName("Simulacao", "servico");
    private static final javax.xml.namespace.QName _SimulacaoServicoCodigo_QNAME = new javax.xml.namespace.QName("Simulacao", "servicoCodigo");
    private static final javax.xml.namespace.QName _InfoMargemValorMargemLimite_QNAME = new javax.xml.namespace.QName("InfoMargem", "valorMargemLimite");
    private static final javax.xml.namespace.QName _ServidorDataNascimento_QNAME = new javax.xml.namespace.QName("Servidor", "dataNascimento");
    private static final javax.xml.namespace.QName _ServidorSituacaoServidor_QNAME = new javax.xml.namespace.QName("Servidor", "situacaoServidor");
    private static final javax.xml.namespace.QName _ServidorCategoria_QNAME = new javax.xml.namespace.QName("Servidor", "categoria");
    private static final javax.xml.namespace.QName _ServidorDataAdmissao_QNAME = new javax.xml.namespace.QName("Servidor", "dataAdmissao");
    private static final javax.xml.namespace.QName _ServidorPrazoServidor_QNAME = new javax.xml.namespace.QName("Servidor", "prazoServidor");
    private static final javax.xml.namespace.QName _ServidorSalarioLiquido_QNAME = new javax.xml.namespace.QName("Servidor", "salarioLiquido");
    private static final javax.xml.namespace.QName _ServidorSalarioBruto_QNAME = new javax.xml.namespace.QName("Servidor", "salarioBruto");
    private static final javax.xml.namespace.QName _ResumoConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("Resumo", "consignatariaCodigo");
    private static final javax.xml.namespace.QName _BoletoTaxaJuros_QNAME = new javax.xml.namespace.QName("Boleto", "taxaJuros");
    private static final javax.xml.namespace.QName _BoletoConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("Boleto", "consignatariaCodigo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zetra.econsig.webservice.soap.hostahostservice.v4
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AlongarConsignacao }
     *
     * @return
     *     the new instance of {@link AlongarConsignacao }
     */
    public AlongarConsignacao createAlongarConsignacao() {
        return new AlongarConsignacao();
    }

    /**
     * Create an instance of {@link AlongarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link AlongarConsignacaoResponse }
     */
    public AlongarConsignacaoResponse createAlongarConsignacaoResponse() {
        return new AlongarConsignacaoResponse();
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
     * Create an instance of {@link AlterarConsignacao }
     *
     * @return
     *     the new instance of {@link AlterarConsignacao }
     */
    public AlterarConsignacao createAlterarConsignacao() {
        return new AlterarConsignacao();
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
     * Create an instance of {@link AlterarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link AlterarConsignacaoResponse }
     */
    public AlterarConsignacaoResponse createAlterarConsignacaoResponse() {
        return new AlterarConsignacaoResponse();
    }

    /**
     * Create an instance of {@link AutorizarReserva }
     *
     * @return
     *     the new instance of {@link AutorizarReserva }
     */
    public AutorizarReserva createAutorizarReserva() {
        return new AutorizarReserva();
    }

    /**
     * Create an instance of {@link AutorizarReservaResponse }
     *
     * @return
     *     the new instance of {@link AutorizarReservaResponse }
     */
    public AutorizarReservaResponse createAutorizarReservaResponse() {
        return new AutorizarReservaResponse();
    }

    /**
     * Create an instance of {@link CancelarConsignacao }
     *
     * @return
     *     the new instance of {@link CancelarConsignacao }
     */
    public CancelarConsignacao createCancelarConsignacao() {
        return new CancelarConsignacao();
    }

    /**
     * Create an instance of {@link CancelarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link CancelarConsignacaoResponse }
     */
    public CancelarConsignacaoResponse createCancelarConsignacaoResponse() {
        return new CancelarConsignacaoResponse();
    }

    /**
     * Create an instance of {@link CancelarRenegociacao }
     *
     * @return
     *     the new instance of {@link CancelarRenegociacao }
     */
    public CancelarRenegociacao createCancelarRenegociacao() {
        return new CancelarRenegociacao();
    }

    /**
     * Create an instance of {@link CancelarRenegociacaoResponse }
     *
     * @return
     *     the new instance of {@link CancelarRenegociacaoResponse }
     */
    public CancelarRenegociacaoResponse createCancelarRenegociacaoResponse() {
        return new CancelarRenegociacaoResponse();
    }

    /**
     * Create an instance of {@link CancelarReserva }
     *
     * @return
     *     the new instance of {@link CancelarReserva }
     */
    public CancelarReserva createCancelarReserva() {
        return new CancelarReserva();
    }

    /**
     * Create an instance of {@link CancelarReservaResponse }
     *
     * @return
     *     the new instance of {@link CancelarReservaResponse }
     */
    public CancelarReservaResponse createCancelarReservaResponse() {
        return new CancelarReservaResponse();
    }

    /**
     * Create an instance of {@link ConfirmarReserva }
     *
     * @return
     *     the new instance of {@link ConfirmarReserva }
     */
    public ConfirmarReserva createConfirmarReserva() {
        return new ConfirmarReserva();
    }

    /**
     * Create an instance of {@link ConfirmarReservaResponse }
     *
     * @return
     *     the new instance of {@link ConfirmarReservaResponse }
     */
    public ConfirmarReservaResponse createConfirmarReservaResponse() {
        return new ConfirmarReservaResponse();
    }

    /**
     * Create an instance of {@link ConfirmarSolicitacao }
     *
     * @return
     *     the new instance of {@link ConfirmarSolicitacao }
     */
    public ConfirmarSolicitacao createConfirmarSolicitacao() {
        return new ConfirmarSolicitacao();
    }

    /**
     * Create an instance of {@link ConfirmarSolicitacaoResponse }
     *
     * @return
     *     the new instance of {@link ConfirmarSolicitacaoResponse }
     */
    public ConfirmarSolicitacaoResponse createConfirmarSolicitacaoResponse() {
        return new ConfirmarSolicitacaoResponse();
    }

    /**
     * Create an instance of {@link ConsultarConsignacao }
     *
     * @return
     *     the new instance of {@link ConsultarConsignacao }
     */
    public ConsultarConsignacao createConsultarConsignacao() {
        return new ConsultarConsignacao();
    }

    /**
     * Create an instance of {@link ConsultarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link ConsultarConsignacaoResponse }
     */
    public ConsultarConsignacaoResponse createConsultarConsignacaoResponse() {
        return new ConsultarConsignacaoResponse();
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
     * Create an instance of {@link ConsultarMargem }
     *
     * @return
     *     the new instance of {@link ConsultarMargem }
     */
    public ConsultarMargem createConsultarMargem() {
        return new ConsultarMargem();
    }

    /**
     * Create an instance of {@link ConsultarMargemResponse }
     *
     * @return
     *     the new instance of {@link ConsultarMargemResponse }
     */
    public ConsultarMargemResponse createConsultarMargemResponse() {
        return new ConsultarMargemResponse();
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
     * Create an instance of {@link DetalharConsultaConsignacao }
     *
     * @return
     *     the new instance of {@link DetalharConsultaConsignacao }
     */
    public DetalharConsultaConsignacao createDetalharConsultaConsignacao() {
        return new DetalharConsultaConsignacao();
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
     * Create an instance of {@link SituacaoServidor }
     *
     * @return
     *     the new instance of {@link SituacaoServidor }
     */
    public SituacaoServidor createSituacaoServidor() {
        return new SituacaoServidor();
    }

    /**
     * Create an instance of {@link DetalharConsultaConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link DetalharConsultaConsignacaoResponse }
     */
    public DetalharConsultaConsignacaoResponse createDetalharConsultaConsignacaoResponse() {
        return new DetalharConsultaConsignacaoResponse();
    }

    /**
     * Create an instance of {@link IncluirAnexoConsignacao }
     *
     * @return
     *     the new instance of {@link IncluirAnexoConsignacao }
     */
    public IncluirAnexoConsignacao createIncluirAnexoConsignacao() {
        return new IncluirAnexoConsignacao();
    }

    /**
     * Create an instance of {@link IncluirAnexoConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link IncluirAnexoConsignacaoResponse }
     */
    public IncluirAnexoConsignacaoResponse createIncluirAnexoConsignacaoResponse() {
        return new IncluirAnexoConsignacaoResponse();
    }

    /**
     * Create an instance of {@link IncluirDadoConsignacao }
     *
     * @return
     *     the new instance of {@link IncluirDadoConsignacao }
     */
    public IncluirDadoConsignacao createIncluirDadoConsignacao() {
        return new IncluirDadoConsignacao();
    }

    /**
     * Create an instance of {@link IncluirDadoConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link IncluirDadoConsignacaoResponse }
     */
    public IncluirDadoConsignacaoResponse createIncluirDadoConsignacaoResponse() {
        return new IncluirDadoConsignacaoResponse();
    }

    /**
     * Create an instance of {@link DadoConsignacao }
     *
     * @return
     *     the new instance of {@link DadoConsignacao }
     */
    public DadoConsignacao createDadoConsignacao() {
        return new DadoConsignacao();
    }

    /**
     * Create an instance of {@link InserirSolicitacao }
     *
     * @return
     *     the new instance of {@link InserirSolicitacao }
     */
    public InserirSolicitacao createInserirSolicitacao() {
        return new InserirSolicitacao();
    }

    /**
     * Create an instance of {@link InserirSolicitacaoResponse }
     *
     * @return
     *     the new instance of {@link InserirSolicitacaoResponse }
     */
    public InserirSolicitacaoResponse createInserirSolicitacaoResponse() {
        return new InserirSolicitacaoResponse();
    }

    /**
     * Create an instance of {@link LiquidarConsignacao }
     *
     * @return
     *     the new instance of {@link LiquidarConsignacao }
     */
    public LiquidarConsignacao createLiquidarConsignacao() {
        return new LiquidarConsignacao();
    }

    /**
     * Create an instance of {@link LiquidarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link LiquidarConsignacaoResponse }
     */
    public LiquidarConsignacaoResponse createLiquidarConsignacaoResponse() {
        return new LiquidarConsignacaoResponse();
    }

    /**
     * Create an instance of {@link ListarDadoConsignacao }
     *
     * @return
     *     the new instance of {@link ListarDadoConsignacao }
     */
    public ListarDadoConsignacao createListarDadoConsignacao() {
        return new ListarDadoConsignacao();
    }

    /**
     * Create an instance of {@link ListarDadoConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link ListarDadoConsignacaoResponse }
     */
    public ListarDadoConsignacaoResponse createListarDadoConsignacaoResponse() {
        return new ListarDadoConsignacaoResponse();
    }

    /**
     * Create an instance of {@link ListarSolicitacao }
     *
     * @return
     *     the new instance of {@link ListarSolicitacao }
     */
    public ListarSolicitacao createListarSolicitacao() {
        return new ListarSolicitacao();
    }

    /**
     * Create an instance of {@link ListarSolicitacaoResponse }
     *
     * @return
     *     the new instance of {@link ListarSolicitacaoResponse }
     */
    public ListarSolicitacaoResponse createListarSolicitacaoResponse() {
        return new ListarSolicitacaoResponse();
    }

    /**
     * Create an instance of {@link Solicitacao }
     *
     * @return
     *     the new instance of {@link Solicitacao }
     */
    public Solicitacao createSolicitacao() {
        return new Solicitacao();
    }

    /**
     * Create an instance of {@link PesquisarServidor }
     *
     * @return
     *     the new instance of {@link PesquisarServidor }
     */
    public PesquisarServidor createPesquisarServidor() {
        return new PesquisarServidor();
    }

    /**
     * Create an instance of {@link PesquisarServidorResponse }
     *
     * @return
     *     the new instance of {@link PesquisarServidorResponse }
     */
    public PesquisarServidorResponse createPesquisarServidorResponse() {
        return new PesquisarServidorResponse();
    }

    /**
     * Create an instance of {@link ReativarConsignacao }
     *
     * @return
     *     the new instance of {@link ReativarConsignacao }
     */
    public ReativarConsignacao createReativarConsignacao() {
        return new ReativarConsignacao();
    }

    /**
     * Create an instance of {@link ReativarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link ReativarConsignacaoResponse }
     */
    public ReativarConsignacaoResponse createReativarConsignacaoResponse() {
        return new ReativarConsignacaoResponse();
    }

    /**
     * Create an instance of {@link RenegociarConsignacao }
     *
     * @return
     *     the new instance of {@link RenegociarConsignacao }
     */
    public RenegociarConsignacao createRenegociarConsignacao() {
        return new RenegociarConsignacao();
    }

    /**
     * Create an instance of {@link RenegociarConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link RenegociarConsignacaoResponse }
     */
    public RenegociarConsignacaoResponse createRenegociarConsignacaoResponse() {
        return new RenegociarConsignacaoResponse();
    }

    /**
     * Create an instance of {@link ReservarMargem }
     *
     * @return
     *     the new instance of {@link ReservarMargem }
     */
    public ReservarMargem createReservarMargem() {
        return new ReservarMargem();
    }

    /**
     * Create an instance of {@link ReservarMargemResponse }
     *
     * @return
     *     the new instance of {@link ReservarMargemResponse }
     */
    public ReservarMargemResponse createReservarMargemResponse() {
        return new ReservarMargemResponse();
    }

    /**
     * Create an instance of {@link SimularConsignacao }
     *
     * @return
     *     the new instance of {@link SimularConsignacao }
     */
    public SimularConsignacao createSimularConsignacao() {
        return new SimularConsignacao();
    }

    /**
     * Create an instance of {@link SimularConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link SimularConsignacaoResponse }
     */
    public SimularConsignacaoResponse createSimularConsignacaoResponse() {
        return new SimularConsignacaoResponse();
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
     * Create an instance of {@link SuspenderConsignacao }
     *
     * @return
     *     the new instance of {@link SuspenderConsignacao }
     */
    public SuspenderConsignacao createSuspenderConsignacao() {
        return new SuspenderConsignacao();
    }

    /**
     * Create an instance of {@link SuspenderConsignacaoResponse }
     *
     * @return
     *     the new instance of {@link SuspenderConsignacaoResponse }
     */
    public SuspenderConsignacaoResponse createSuspenderConsignacaoResponse() {
        return new SuspenderConsignacaoResponse();
    }

    /**
     * Create an instance of {@link ValidarAcesso }
     *
     * @return
     *     the new instance of {@link ValidarAcesso }
     */
    public ValidarAcesso createValidarAcesso() {
        return new ValidarAcesso();
    }

    /**
     * Create an instance of {@link ValidarAcessoResponse }
     *
     * @return
     *     the new instance of {@link ValidarAcessoResponse }
     */
    public ValidarAcessoResponse createValidarAcessoResponse() {
        return new ValidarAcessoResponse();
    }

    /**
     * Create an instance of {@link ConsultarParametros }
     *
     * @return
     *     the new instance of {@link ConsultarParametros }
     */
    public ConsultarParametros createConsultarParametros() {
        return new ConsultarParametros();
    }

    /**
     * Create an instance of {@link ConsultarParametrosResponse }
     *
     * @return
     *     the new instance of {@link ConsultarParametrosResponse }
     */
    public ConsultarParametrosResponse createConsultarParametrosResponse() {
        return new ConsultarParametrosResponse();
    }

    /**
     * Create an instance of {@link ParametroSet }
     *
     * @return
     *     the new instance of {@link ParametroSet }
     */
    public ParametroSet createParametroSet() {
        return new ParametroSet();
    }

    /**
     * Create an instance of {@link CadastrarTaxaDeJuros }
     *
     * @return
     *     the new instance of {@link CadastrarTaxaDeJuros }
     */
    public CadastrarTaxaDeJuros createCadastrarTaxaDeJuros() {
        return new CadastrarTaxaDeJuros();
    }

    /**
     * Create an instance of {@link TaxaDeJuros }
     *
     * @return
     *     the new instance of {@link TaxaDeJuros }
     */
    public TaxaDeJuros createTaxaDeJuros() {
        return new TaxaDeJuros();
    }

    /**
     * Create an instance of {@link CadastrarTaxaDeJurosResponse }
     *
     * @return
     *     the new instance of {@link CadastrarTaxaDeJurosResponse }
     */
    public CadastrarTaxaDeJurosResponse createCadastrarTaxaDeJurosResponse() {
        return new CadastrarTaxaDeJurosResponse();
    }

    /**
     * Create an instance of {@link ConsultarParcela }
     *
     * @return
     *     the new instance of {@link ConsultarParcela }
     */
    public ConsultarParcela createConsultarParcela() {
        return new ConsultarParcela();
    }

    /**
     * Create an instance of {@link ConsultarParcelaResponse }
     *
     * @return
     *     the new instance of {@link ConsultarParcelaResponse }
     */
    public ConsultarParcelaResponse createConsultarParcelaResponse() {
        return new ConsultarParcelaResponse();
    }

    /**
     * Create an instance of {@link Parcela }
     *
     * @return
     *     the new instance of {@link Parcela }
     */
    public Parcela createParcela() {
        return new Parcela();
    }

    /**
     * Create an instance of {@link LiquidarParcela }
     *
     * @return
     *     the new instance of {@link LiquidarParcela }
     */
    public LiquidarParcela createLiquidarParcela() {
        return new LiquidarParcela();
    }

    /**
     * Create an instance of {@link LiquidarParcelaResponse }
     *
     * @return
     *     the new instance of {@link LiquidarParcelaResponse }
     */
    public LiquidarParcelaResponse createLiquidarParcelaResponse() {
        return new LiquidarParcelaResponse();
    }

    /**
     * Create an instance of {@link CadastrarServidor }
     *
     * @return
     *     the new instance of {@link CadastrarServidor }
     */
    public CadastrarServidor createCadastrarServidor() {
        return new CadastrarServidor();
    }

    /**
     * Create an instance of {@link CadastrarServidorResponse }
     *
     * @return
     *     the new instance of {@link CadastrarServidorResponse }
     */
    public CadastrarServidorResponse createCadastrarServidorResponse() {
        return new CadastrarServidorResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Long> createAlongarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "novoAdeIdentificador", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoNovoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNovoAdeIdentificador_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = AlongarConsignacao.class)
    public JAXBElement<XMLGregorianCalendar> createAlongarConsignacaoDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlongarConsignacaoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "carencia", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Integer> createAlongarConsignacaoCarencia(java.lang.Integer value) {
        return new JAXBElement<>(_AlongarConsignacaoCarencia_QNAME, java.lang.Integer.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "correspondenteCodigo", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoCorrespondenteCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCorrespondenteCodigo_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorTac", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlongarConsignacaoValorTac(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorTac_QNAME, java.lang.Double.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "indice", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoIndice(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoIndice_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorIof", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlongarConsignacaoValorIof(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorIof_QNAME, java.lang.Double.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorMensVin", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlongarConsignacaoValorMensVin(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorMensVin_QNAME, java.lang.Double.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "banco", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoBanco(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoBanco_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agencia", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoAgencia(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAgencia_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "conta", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoConta(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConta_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "naturezaServicoCodigo", scope = AlongarConsignacao.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoNaturezaServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNaturezaServicoCodigo_QNAME, java.lang.String.class, AlongarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = AlongarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createAlongarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, AlongarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = AlongarConsignacaoResponse.class)
    public JAXBElement<Boleto> createAlongarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, AlongarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Long> createAlterarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "novoAdeIdentificador", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoNovoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNovoAdeIdentificador_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlterarConsignacaoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorTac", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlterarConsignacaoValorTac(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorTac_QNAME, java.lang.Double.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorIof", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlterarConsignacaoValorIof(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorIof_QNAME, java.lang.Double.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorMensVin", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlterarConsignacaoValorMensVin(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorMensVin_QNAME, java.lang.Double.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "senhaServidor", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoSenhaServidor(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoSenhaServidor_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "indice", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoIndice(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoIndice_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "carencia", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Integer> createAlterarConsignacaoCarencia(java.lang.Integer value) {
        return new JAXBElement<>(_AlongarConsignacaoCarencia_QNAME, java.lang.Integer.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "taxaJuros", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.Double> createAlterarConsignacaoTaxaJuros(java.lang.Double value) {
        return new JAXBElement<>(_AlterarConsignacaoTaxaJuros_QNAME, java.lang.Double.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "periodo", scope = AlterarConsignacao.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoPeriodo(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoPeriodo_QNAME, java.lang.String.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "anexo", scope = AlterarConsignacao.class)
    public JAXBElement<Anexo> createAlterarConsignacaoAnexo(Anexo value) {
        return new JAXBElement<>(_AlterarConsignacaoAnexo_QNAME, Anexo.class, AlterarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = AlterarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createAlterarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, AlterarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = AlterarConsignacaoResponse.class)
    public JAXBElement<Boleto> createAlterarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, AlterarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.String> createAutorizarReservaCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.String> createAutorizarReservaConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.Long> createAutorizarReservaAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.String> createAutorizarReservaAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.String> createAutorizarReservaCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = AutorizarReserva.class)
    public JAXBElement<java.lang.String> createAutorizarReservaObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, AutorizarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = AutorizarReservaResponse.class)
    public JAXBElement<java.lang.String> createAutorizarReservaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, AutorizarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = AutorizarReservaResponse.class)
    public JAXBElement<Boleto> createAutorizarReservaResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, AutorizarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.Long> createCancelarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = CancelarConsignacao.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, CancelarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = CancelarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createCancelarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, CancelarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = CancelarConsignacaoResponse.class)
    public JAXBElement<Boleto> createCancelarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, CancelarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.Long> createCancelarRenegociacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "senhaServidor", scope = CancelarRenegociacao.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoSenhaServidor(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoSenhaServidor_QNAME, java.lang.String.class, CancelarRenegociacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = CancelarRenegociacaoResponse.class)
    public JAXBElement<java.lang.String> createCancelarRenegociacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, CancelarRenegociacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = CancelarRenegociacaoResponse.class)
    public JAXBElement<Boleto> createCancelarRenegociacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, CancelarRenegociacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = CancelarReserva.class)
    public JAXBElement<java.lang.String> createCancelarReservaCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = CancelarReserva.class)
    public JAXBElement<java.lang.String> createCancelarReservaConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = CancelarReserva.class)
    public JAXBElement<java.lang.Long> createCancelarReservaAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = CancelarReserva.class)
    public JAXBElement<java.lang.String> createCancelarReservaAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = CancelarReserva.class)
    public JAXBElement<java.lang.String> createCancelarReservaCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = CancelarReserva.class)
    public JAXBElement<java.lang.String> createCancelarReservaObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, CancelarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = CancelarReservaResponse.class)
    public JAXBElement<java.lang.String> createCancelarReservaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, CancelarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = CancelarReservaResponse.class)
    public JAXBElement<Boleto> createCancelarReservaResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, CancelarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.String> createConfirmarReservaCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.String> createConfirmarReservaConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.Long> createConfirmarReservaAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.String> createConfirmarReservaAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.String> createConfirmarReservaCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = ConfirmarReserva.class)
    public JAXBElement<java.lang.String> createConfirmarReservaObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, ConfirmarReserva.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConfirmarReservaResponse.class)
    public JAXBElement<java.lang.String> createConfirmarReservaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConfirmarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = ConfirmarReservaResponse.class)
    public JAXBElement<Boleto> createConfirmarReservaResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, ConfirmarReservaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.Long> createConfirmarSolicitacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "novoAdeIdentificador", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoNovoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNovoAdeIdentificador_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "banco", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoBanco(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoBanco_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agencia", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoAgencia(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAgencia_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "conta", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoConta(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConta_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = ConfirmarSolicitacao.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, ConfirmarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConfirmarSolicitacaoResponse.class)
    public JAXBElement<java.lang.String> createConfirmarSolicitacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConfirmarSolicitacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = ConfirmarSolicitacaoResponse.class)
    public JAXBElement<Boleto> createConfirmarSolicitacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, ConfirmarSolicitacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.Long> createConsultarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = ConsultarConsignacao.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, ConsultarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConsultarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createConsultarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConsultarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = ConsultarConsignacaoResponse.class)
    public JAXBElement<Boleto> createConsultarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, ConsultarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "senhaServidor", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemSenhaServidor(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoSenhaServidor_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.String> createConsultarMargemServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "matriculaMultipla", scope = ConsultarMargem.class)
    public JAXBElement<java.lang.Boolean> createConsultarMargemMatriculaMultipla(java.lang.Boolean value) {
        return new JAXBElement<>(_ConsultarMargemMatriculaMultipla_QNAME, java.lang.Boolean.class, ConsultarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConsultarMargemResponse.class)
    public JAXBElement<java.lang.String> createConsultarMargemResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConsultarMargemResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Long> createDetalharConsultaConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "correspondenteCodigo", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoCorrespondenteCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCorrespondenteCodigo_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoVerba", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoCodigoVerba(java.lang.String value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoCodigoVerba_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "sdvSolicitado", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Boolean> createDetalharConsultaConsignacaoSdvSolicitado(java.lang.Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSdvSolicitado_QNAME, java.lang.Boolean.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "sdvSolicitadoNaoCadastrado", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Boolean> createDetalharConsultaConsignacaoSdvSolicitadoNaoCadastrado(java.lang.Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSdvSolicitadoNaoCadastrado_QNAME, java.lang.Boolean.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "sdvSolicitadoCadastrado", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Boolean> createDetalharConsultaConsignacaoSdvSolicitadoCadastrado(java.lang.Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSdvSolicitadoCadastrado_QNAME, java.lang.Boolean.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "sdvNaoSolicitado", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Boolean> createDetalharConsultaConsignacaoSdvNaoSolicitado(java.lang.Boolean value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSdvNaoSolicitado_QNAME, java.lang.Boolean.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "periodo", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoPeriodo(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlterarConsignacaoPeriodo_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataInclusaoInicio", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoDataInclusaoInicio(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoDataInclusaoInicio_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataInclusaoFim", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<XMLGregorianCalendar> createDetalharConsultaConsignacaoDataInclusaoFim(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoDataInclusaoFim_QNAME, XMLGregorianCalendar.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "integraFolha", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Short> createDetalharConsultaConsignacaoIntegraFolha(java.lang.Short value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoIntegraFolha_QNAME, java.lang.Short.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMargem", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.Short> createDetalharConsultaConsignacaoCodigoMargem(java.lang.Short value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoCodigoMargem_QNAME, java.lang.Short.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "indice", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoIndice(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoIndice_QNAME, java.lang.String.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "situacaoContrato", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<SituacaoContrato> createDetalharConsultaConsignacaoSituacaoContrato(SituacaoContrato value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSituacaoContrato_QNAME, SituacaoContrato.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "situacaoServidor", scope = DetalharConsultaConsignacao.class)
    public JAXBElement<SituacaoServidor> createDetalharConsultaConsignacaoSituacaoServidor(SituacaoServidor value) {
        return new JAXBElement<>(_DetalharConsultaConsignacaoSituacaoServidor_QNAME, SituacaoServidor.class, DetalharConsultaConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = DetalharConsultaConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createDetalharConsultaConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, DetalharConsultaConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = DetalharConsultaConsignacaoResponse.class)
    public JAXBElement<Boleto> createDetalharConsultaConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, DetalharConsultaConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = IncluirAnexoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirAnexoConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, IncluirAnexoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = IncluirAnexoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirAnexoConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, IncluirAnexoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = IncluirAnexoConsignacao.class)
    public JAXBElement<java.lang.Long> createIncluirAnexoConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, IncluirAnexoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = IncluirAnexoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirAnexoConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, IncluirAnexoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "descricaoAnexo", scope = IncluirAnexoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirAnexoConsignacaoDescricaoAnexo(java.lang.String value) {
        return new JAXBElement<>(_IncluirAnexoConsignacaoDescricaoAnexo_QNAME, java.lang.String.class, IncluirAnexoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = IncluirAnexoConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createIncluirAnexoConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, IncluirAnexoConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = IncluirDadoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirDadoConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, IncluirDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = IncluirDadoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirDadoConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, IncluirDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = IncluirDadoConsignacao.class)
    public JAXBElement<java.lang.Long> createIncluirDadoConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, IncluirDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = IncluirDadoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirDadoConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, IncluirDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dadoValor", scope = IncluirDadoConsignacao.class)
    public JAXBElement<java.lang.String> createIncluirDadoConsignacaoDadoValor(java.lang.String value) {
        return new JAXBElement<>(_IncluirDadoConsignacaoDadoValor_QNAME, java.lang.String.class, IncluirDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = IncluirDadoConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createIncluirDadoConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, IncluirDadoConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "senhaServidor", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoSenhaServidor(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoSenhaServidor_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = InserirSolicitacao.class)
    public JAXBElement<XMLGregorianCalendar> createInserirSolicitacaoDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prazo", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.Integer> createInserirSolicitacaoPrazo(java.lang.Integer value) {
        return new JAXBElement<>(_InserirSolicitacaoPrazo_QNAME, java.lang.Integer.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.Double> createInserirSolicitacaoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = InserirSolicitacao.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, InserirSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = InserirSolicitacaoResponse.class)
    public JAXBElement<java.lang.String> createInserirSolicitacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, InserirSolicitacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = InserirSolicitacaoResponse.class)
    public JAXBElement<Boleto> createInserirSolicitacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, InserirSolicitacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.Long> createLiquidarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "periodo", scope = LiquidarConsignacao.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoPeriodo(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoPeriodo_QNAME, java.lang.String.class, LiquidarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = LiquidarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createLiquidarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, LiquidarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = LiquidarConsignacaoResponse.class)
    public JAXBElement<Boleto> createLiquidarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, LiquidarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ListarDadoConsignacao.class)
    public JAXBElement<java.lang.String> createListarDadoConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ListarDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ListarDadoConsignacao.class)
    public JAXBElement<java.lang.String> createListarDadoConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ListarDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ListarDadoConsignacao.class)
    public JAXBElement<java.lang.Long> createListarDadoConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ListarDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ListarDadoConsignacao.class)
    public JAXBElement<java.lang.String> createListarDadoConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ListarDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dadoCodigo", scope = ListarDadoConsignacao.class)
    public JAXBElement<java.lang.String> createListarDadoConsignacaoDadoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ListarDadoConsignacaoDadoCodigo_QNAME, java.lang.String.class, ListarDadoConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ListarDadoConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createListarDadoConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ListarDadoConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.Long> createListarSolicitacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = ListarSolicitacao.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, ListarSolicitacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ListarSolicitacaoResponse.class)
    public JAXBElement<java.lang.String> createListarSolicitacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ListarSolicitacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "primeiroNome", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorPrimeiroNome(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorPrimeiroNome_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "ultimoNome", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorUltimoNome(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorUltimoNome_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = PesquisarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createPesquisarServidorDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estIdentificador", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorEstIdentificador(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorEstIdentificador_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgIdentificador", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorOrgIdentificador(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorOrgIdentificador_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "matricula", scope = PesquisarServidor.class)
    public JAXBElement<java.lang.String> createPesquisarServidorMatricula(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorMatricula_QNAME, java.lang.String.class, PesquisarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = PesquisarServidorResponse.class)
    public JAXBElement<java.lang.String> createPesquisarServidorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, PesquisarServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.Long> createReativarConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = ReativarConsignacao.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, ReativarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ReativarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createReativarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ReativarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = ReativarConsignacaoResponse.class)
    public JAXBElement<Boleto> createReativarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, ReativarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "novoAdeIdentificador", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoNovoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNovoAdeIdentificador_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = RenegociarConsignacao.class)
    public JAXBElement<XMLGregorianCalendar> createRenegociarConsignacaoDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Double> createRenegociarConsignacaoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "carencia", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Integer> createRenegociarConsignacaoCarencia(java.lang.Integer value) {
        return new JAXBElement<>(_AlongarConsignacaoCarencia_QNAME, java.lang.Integer.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "correspondenteCodigo", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoCorrespondenteCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCorrespondenteCodigo_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorTac", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Double> createRenegociarConsignacaoValorTac(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorTac_QNAME, java.lang.Double.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "indice", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoIndice(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoIndice_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorIof", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Double> createRenegociarConsignacaoValorIof(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorIof_QNAME, java.lang.Double.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorMensVin", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Double> createRenegociarConsignacaoValorMensVin(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorMensVin_QNAME, java.lang.Double.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "banco", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoBanco(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoBanco_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agencia", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoAgencia(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAgencia_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "conta", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoConta(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConta_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "taxaJuros", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.Double> createRenegociarConsignacaoTaxaJuros(java.lang.Double value) {
        return new JAXBElement<>(_AlterarConsignacaoTaxaJuros_QNAME, java.lang.Double.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "naturezaServicoCodigo", scope = RenegociarConsignacao.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoNaturezaServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNaturezaServicoCodigo_QNAME, java.lang.String.class, RenegociarConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = RenegociarConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createRenegociarConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, RenegociarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = RenegociarConsignacaoResponse.class)
    public JAXBElement<Boleto> createRenegociarConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, RenegociarConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = ReservarMargem.class)
    public JAXBElement<XMLGregorianCalendar> createReservarMargemDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prazo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Integer> createReservarMargemPrazo(java.lang.Integer value) {
        return new JAXBElement<>(_InserirSolicitacaoPrazo_QNAME, java.lang.Integer.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Double> createReservarMargemValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "correspondenteCodigo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemCorrespondenteCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCorrespondenteCodigo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "carencia", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Integer> createReservarMargemCarencia(java.lang.Integer value) {
        return new JAXBElement<>(_AlongarConsignacaoCarencia_QNAME, java.lang.Integer.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorTac", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Double> createReservarMargemValorTac(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorTac_QNAME, java.lang.Double.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "indice", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemIndice(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoIndice_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorIof", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Double> createReservarMargemValorIof(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorIof_QNAME, java.lang.Double.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorMensVin", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Double> createReservarMargemValorMensVin(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorMensVin_QNAME, java.lang.Double.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "banco", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemBanco(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoBanco_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agencia", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemAgencia(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAgencia_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "conta", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemConta(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConta_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "taxaJuros", scope = ReservarMargem.class)
    public JAXBElement<java.lang.Double> createReservarMargemTaxaJuros(java.lang.Double value) {
        return new JAXBElement<>(_AlterarConsignacaoTaxaJuros_QNAME, java.lang.Double.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "naturezaServicoCodigo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemNaturezaServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoNaturezaServicoCodigo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "periodo", scope = ReservarMargem.class)
    public JAXBElement<java.lang.String> createReservarMargemPeriodo(java.lang.String value) {
        return new JAXBElement<>(_AlterarConsignacaoPeriodo_QNAME, java.lang.String.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "anexo", scope = ReservarMargem.class)
    public JAXBElement<Anexo> createReservarMargemAnexo(Anexo value) {
        return new JAXBElement<>(_AlterarConsignacaoAnexo_QNAME, Anexo.class, ReservarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ReservarMargemResponse.class)
    public JAXBElement<java.lang.String> createReservarMargemResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ReservarMargemResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = ReservarMargemResponse.class)
    public JAXBElement<Boleto> createReservarMargemResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, ReservarMargemResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prazo", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.Integer> createSimularConsignacaoPrazo(java.lang.Integer value) {
        return new JAXBElement<>(_InserirSolicitacaoPrazo_QNAME, java.lang.Integer.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "valorLiberado", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.Double> createSimularConsignacaoValorLiberado(java.lang.Double value) {
        return new JAXBElement<>(_AlongarConsignacaoValorLiberado_QNAME, java.lang.Double.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = SimularConsignacao.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, SimularConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = SimularConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createSimularConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, SimularConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.Long> createSuspenderConsignacaoAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codigoMotivoOperacao", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoCodigoMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaCodigoMotivoOperacao_QNAME, java.lang.String.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "obsMotivoOperacao", scope = SuspenderConsignacao.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoObsMotivoOperacao(java.lang.String value) {
        return new JAXBElement<>(_AutorizarReservaObsMotivoOperacao_QNAME, java.lang.String.class, SuspenderConsignacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = SuspenderConsignacaoResponse.class)
    public JAXBElement<java.lang.String> createSuspenderConsignacaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, SuspenderConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "boleto", scope = SuspenderConsignacaoResponse.class)
    public JAXBElement<Boleto> createSuspenderConsignacaoResponseBoleto(Boleto value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseBoleto_QNAME, Boleto.class, SuspenderConsignacaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ValidarAcesso.class)
    public JAXBElement<java.lang.String> createValidarAcessoCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ValidarAcesso.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ValidarAcesso.class)
    public JAXBElement<java.lang.String> createValidarAcessoConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ValidarAcesso.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = ValidarAcesso.class)
    public JAXBElement<java.lang.String> createValidarAcessoCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, ValidarAcesso.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ValidarAcessoResponse.class)
    public JAXBElement<java.lang.String> createValidarAcessoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ValidarAcessoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConsultarParametros.class)
    public JAXBElement<java.lang.String> createConsultarParametrosCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConsultarParametros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConsultarParametros.class)
    public JAXBElement<java.lang.String> createConsultarParametrosConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConsultarParametros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = ConsultarParametros.class)
    public JAXBElement<java.lang.String> createConsultarParametrosCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, ConsultarParametros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = ConsultarParametros.class)
    public JAXBElement<java.lang.String> createConsultarParametrosServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, ConsultarParametros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConsultarParametrosResponse.class)
    public JAXBElement<java.lang.String> createConsultarParametrosResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConsultarParametrosResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParametroSet }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ParametroSet }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "parametroSet", scope = ConsultarParametrosResponse.class)
    public JAXBElement<ParametroSet> createConsultarParametrosResponseParametroSet(ParametroSet value) {
        return new JAXBElement<>(_ConsultarParametrosResponseParametroSet_QNAME, ParametroSet.class, ConsultarParametrosResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = CadastrarTaxaDeJuros.class)
    public JAXBElement<java.lang.String> createCadastrarTaxaDeJurosCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, CadastrarTaxaDeJuros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = CadastrarTaxaDeJuros.class)
    public JAXBElement<java.lang.String> createCadastrarTaxaDeJurosConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, CadastrarTaxaDeJuros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codVerba", scope = CadastrarTaxaDeJuros.class)
    public JAXBElement<java.lang.String> createCadastrarTaxaDeJurosCodVerba(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCodVerba_QNAME, java.lang.String.class, CadastrarTaxaDeJuros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servicoCodigo", scope = CadastrarTaxaDeJuros.class)
    public JAXBElement<java.lang.String> createCadastrarTaxaDeJurosServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoServicoCodigo_QNAME, java.lang.String.class, CadastrarTaxaDeJuros.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = CadastrarTaxaDeJurosResponse.class)
    public JAXBElement<java.lang.String> createCadastrarTaxaDeJurosResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, CadastrarTaxaDeJurosResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.String> createConsultarParcelaCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.String> createConsultarParcelaConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.Long> createConsultarParcelaAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.String> createConsultarParcelaAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prdNumero", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.Short> createConsultarParcelaPrdNumero(java.lang.Short value) {
        return new JAXBElement<>(_ConsultarParcelaPrdNumero_QNAME, java.lang.Short.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataDesconto", scope = ConsultarParcela.class)
    public JAXBElement<java.lang.String> createConsultarParcelaDataDesconto(java.lang.String value) {
        return new JAXBElement<>(_ConsultarParcelaDataDesconto_QNAME, java.lang.String.class, ConsultarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = ConsultarParcelaResponse.class)
    public JAXBElement<java.lang.String> createConsultarParcelaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, ConsultarParcelaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.String> createLiquidarParcelaCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.String> createLiquidarParcelaConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeNumero", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.Long> createLiquidarParcelaAdeNumero(java.lang.Long value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeNumero_QNAME, java.lang.Long.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "adeIdentificador", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.String> createLiquidarParcelaAdeIdentificador(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAdeIdentificador_QNAME, java.lang.String.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prdNumero", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.Short> createLiquidarParcelaPrdNumero(java.lang.Short value) {
        return new JAXBElement<>(_LiquidarParcelaPrdNumero_QNAME, java.lang.Short.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataDesconto", scope = LiquidarParcela.class)
    public JAXBElement<java.lang.String> createLiquidarParcelaDataDesconto(java.lang.String value) {
        return new JAXBElement<>(_LiquidarParcelaDataDesconto_QNAME, java.lang.String.class, LiquidarParcela.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = LiquidarParcelaResponse.class)
    public JAXBElement<java.lang.String> createLiquidarParcelaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, LiquidarParcelaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cliente", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCliente(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCliente_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "convenio", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorConvenio(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConvenio_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nome", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNome(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNome_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "titulacao", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorTitulacao(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorTitulacao_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "primeiroNome", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorPrimeiroNome(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorPrimeiroNome_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nomeMeio", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNomeMeio(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNomeMeio_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "ultimoNome", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorUltimoNome(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorUltimoNome_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cpf", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCpf(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoCpf_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nomePai", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNomePai(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNomePai_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nomeMae", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNomeMae(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNomeMae_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nomeConjuge", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNomeConjuge(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNomeConjuge_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataNascimento", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_AlongarConsignacaoDataNascimento_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cidadeNascimento", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCidadeNascimento(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCidadeNascimento_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "ufNascimento", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorUfNascimento(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorUfNascimento_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "sexo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorSexo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorSexo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estadoCivil", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorEstadoCivil(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorEstadoCivil_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nacionalidade", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNacionalidade(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNacionalidade_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "identidade", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorIdentidade(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorIdentidade_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataIdentidade", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataIdentidade(XMLGregorianCalendar value) {
        return new JAXBElement<>(_CadastrarServidorDataIdentidade_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "ufIdentidade", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorUfIdentidade(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorUfIdentidade_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "emissorIdentidade", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorEmissorIdentidade(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorEmissorIdentidade_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "numCarteiraTrabalho", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNumCarteiraTrabalho(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNumCarteiraTrabalho_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "numPis", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNumPis(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNumPis_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "logradouro", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorLogradouro(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorLogradouro_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "nro", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorNro(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorNro_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "complemento", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorComplemento(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorComplemento_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "bairro", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorBairro(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorBairro_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cidade", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCidade(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCidade_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "uf", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorUf(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorUf_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cep", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCep(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCep_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dddTelefone", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorDddTelefone(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorDddTelefone_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "telefone", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorTelefone(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorTelefone_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dddCelular", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorDddCelular(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorDddCelular_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "celular", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCelular(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCelular_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "email", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorEmail(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorEmail_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "matricula", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorMatricula(java.lang.String value) {
        return new JAXBElement<>(_PesquisarServidorMatricula_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabelecimentoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoEstabelecimentoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "orgaoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoOrgaoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "subOrgaoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorSubOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorSubOrgaoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "situacao", scope = CadastrarServidor.class)
    public JAXBElement<SituacaoServidor> createCadastrarServidorSituacao(SituacaoServidor value) {
        return new JAXBElement<>(_CadastrarServidorSituacao_QNAME, SituacaoServidor.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "categoria", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCategoria(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCategoria_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "cargoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorCargoCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorCargoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "municipioLotacao", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorMunicipioLotacao(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorMunicipioLotacao_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "estabilizado", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Boolean> createCadastrarServidorEstabilizado(java.lang.Boolean value) {
        return new JAXBElement<>(_CadastrarServidorEstabilizado_QNAME, java.lang.Boolean.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataFimEngajamento", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataFimEngajamento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_CadastrarServidorDataFimEngajamento_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataLimitePermanencia", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataLimitePermanencia(XMLGregorianCalendar value) {
        return new JAXBElement<>(_CadastrarServidorDataLimitePermanencia_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "vinculoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorVinculoCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorVinculoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "clt", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Boolean> createCadastrarServidorClt(java.lang.Boolean value) {
        return new JAXBElement<>(_CadastrarServidorClt_QNAME, java.lang.Boolean.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataAdmissao", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataAdmissao(XMLGregorianCalendar value) {
        return new JAXBElement<>(_CadastrarServidorDataAdmissao_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "dataContracheque", scope = CadastrarServidor.class)
    public JAXBElement<XMLGregorianCalendar> createCadastrarServidorDataContracheque(XMLGregorianCalendar value) {
        return new JAXBElement<>(_CadastrarServidorDataContracheque_QNAME, XMLGregorianCalendar.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "prazoServidor", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Integer> createCadastrarServidorPrazoServidor(java.lang.Integer value) {
        return new JAXBElement<>(_CadastrarServidorPrazoServidor_QNAME, java.lang.Integer.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "bancoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorBancoCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorBancoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "banco", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorBanco(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoBanco_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agencia", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorAgencia(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoAgencia_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "conta", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorConta(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoConta_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "bancoAlternativo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorBancoAlternativo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorBancoAlternativo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "agenciaAlternativa", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorAgenciaAlternativa(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorAgenciaAlternativa_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "contaAlternativa", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorContaAlternativa(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorContaAlternativa_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "observacao", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorObservacao(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorObservacao_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "praca", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorPraca(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorPraca_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "salario", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorSalario(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorSalario_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "proventos", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorProventos(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorProventos_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "descontosCompulsorios", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorDescontosCompulsorios(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorDescontosCompulsorios_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "descontosFacultativos", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorDescontosFacultativos(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorDescontosFacultativos_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "baseCalculo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorBaseCalculo(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorBaseCalculo_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "associado", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Boolean> createCadastrarServidorAssociado(java.lang.Boolean value) {
        return new JAXBElement<>(_CadastrarServidorAssociado_QNAME, java.lang.Boolean.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "outrosDescontos", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.Double> createCadastrarServidorOutrosDescontos(java.lang.Double value) {
        return new JAXBElement<>(_CadastrarServidorOutrosDescontos_QNAME, java.lang.Double.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "matriculaInstitucional", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorMatriculaInstitucional(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorMatriculaInstitucional_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "padraoCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorPadraoCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorPadraoCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "unidadeCodigo", scope = CadastrarServidor.class)
    public JAXBElement<java.lang.String> createCadastrarServidorUnidadeCodigo(java.lang.String value) {
        return new JAXBElement<>(_CadastrarServidorUnidadeCodigo_QNAME, java.lang.String.class, CadastrarServidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "codRetorno", scope = CadastrarServidorResponse.class)
    public JAXBElement<java.lang.String> createCadastrarServidorResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_AlongarConsignacaoResponseCodRetorno_QNAME, java.lang.String.class, CadastrarServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Servidor }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Servidor }{@code >}
     */
    @XmlElementDecl(namespace = "HostaHostService-v4_0", name = "servidor", scope = CadastrarServidorResponse.class)
    public JAXBElement<Servidor> createCadastrarServidorResponseServidor(Servidor value) {
        return new JAXBElement<>(_CadastrarServidorResponseServidor_QNAME, Servidor.class, CadastrarServidorResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "qtdMaxParcelas", scope = ParametroSet.class)
    public JAXBElement<java.lang.Integer> createParametroSetQtdMaxParcelas(java.lang.Integer value) {
        return new JAXBElement<>(_ParametroSetQtdMaxParcelas_QNAME, java.lang.Integer.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "diasInfoSaldoDevedor", scope = ParametroSet.class)
    public JAXBElement<java.lang.Short> createParametroSetDiasInfoSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_ParametroSetDiasInfoSaldoDevedor_QNAME, java.lang.Short.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "diasAprovSaldoDevedor", scope = ParametroSet.class)
    public JAXBElement<java.lang.Short> createParametroSetDiasAprovSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_ParametroSetDiasAprovSaldoDevedor_QNAME, java.lang.Short.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "diasInfoPgSaldoDevedor", scope = ParametroSet.class)
    public JAXBElement<java.lang.Short> createParametroSetDiasInfoPgSaldoDevedor(java.lang.Short value) {
        return new JAXBElement<>(_ParametroSetDiasInfoPgSaldoDevedor_QNAME, java.lang.Short.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "diasLiquidacaoAdeCompra", scope = ParametroSet.class)
    public JAXBElement<java.lang.Short> createParametroSetDiasLiquidacaoAdeCompra(java.lang.Short value) {
        return new JAXBElement<>(_ParametroSetDiasLiquidacaoAdeCompra_QNAME, java.lang.Short.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSet", name = "usaCet", scope = ParametroSet.class)
    public JAXBElement<java.lang.Boolean> createParametroSetUsaCet(java.lang.Boolean value) {
        return new JAXBElement<>(_ParametroSetUsaCet_QNAME, java.lang.Boolean.class, ParametroSet.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Simulacao", name = "servico", scope = Simulacao.class)
    public JAXBElement<java.lang.String> createSimulacaoServico(java.lang.String value) {
        return new JAXBElement<>(_SimulacaoServico_QNAME, java.lang.String.class, Simulacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Simulacao", name = "servicoCodigo", scope = Simulacao.class)
    public JAXBElement<java.lang.String> createSimulacaoServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_SimulacaoServicoCodigo_QNAME, java.lang.String.class, Simulacao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "InfoMargem", name = "valorMargemLimite", scope = InfoMargem.class)
    public JAXBElement<java.lang.Double> createInfoMargemValorMargemLimite(java.lang.Double value) {
        return new JAXBElement<>(_InfoMargemValorMargemLimite_QNAME, java.lang.Double.class, InfoMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "dataNascimento", scope = Servidor.class)
    public JAXBElement<XMLGregorianCalendar> createServidorDataNascimento(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ServidorDataNascimento_QNAME, XMLGregorianCalendar.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "situacaoServidor", scope = Servidor.class)
    public JAXBElement<SituacaoServidor> createServidorSituacaoServidor(SituacaoServidor value) {
        return new JAXBElement<>(_ServidorSituacaoServidor_QNAME, SituacaoServidor.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "categoria", scope = Servidor.class)
    public JAXBElement<java.lang.String> createServidorCategoria(java.lang.String value) {
        return new JAXBElement<>(_ServidorCategoria_QNAME, java.lang.String.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "dataAdmissao", scope = Servidor.class)
    public JAXBElement<XMLGregorianCalendar> createServidorDataAdmissao(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ServidorDataAdmissao_QNAME, XMLGregorianCalendar.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "prazoServidor", scope = Servidor.class)
    public JAXBElement<java.lang.Integer> createServidorPrazoServidor(java.lang.Integer value) {
        return new JAXBElement<>(_ServidorPrazoServidor_QNAME, java.lang.Integer.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "salarioLiquido", scope = Servidor.class)
    public JAXBElement<java.lang.Double> createServidorSalarioLiquido(java.lang.Double value) {
        return new JAXBElement<>(_ServidorSalarioLiquido_QNAME, java.lang.Double.class, Servidor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "Servidor", name = "salarioBruto", scope = Servidor.class)
    public JAXBElement<java.lang.Double> createServidorSalarioBruto(java.lang.Double value) {
        return new JAXBElement<>(_ServidorSalarioBruto_QNAME, java.lang.Double.class, Servidor.class, value);
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

}
