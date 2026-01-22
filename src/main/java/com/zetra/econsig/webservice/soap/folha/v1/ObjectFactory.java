//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3
// Consulte https://eclipse-ee4j.github.io/jaxb-ri
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem.
//


package com.zetra.econsig.webservice.soap.folha.v1;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.zetra.econsig.webservice.soap.folha.v1 package.
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

    private static final javax.xml.namespace.QName _CadastrarConsignatariaResponseCodRetorno_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codRetorno");
    private static final javax.xml.namespace.QName _AtualizarMargemCpf_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "cpf");
    private static final javax.xml.namespace.QName _AtualizarMargemOrgaoCodigo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "orgaoCodigo");
    private static final javax.xml.namespace.QName _AtualizarMargemEstabelecimentoCodigo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "estabelecimentoCodigo");
    private static final javax.xml.namespace.QName _AtualizarMargemMargem1_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "margem1");
    private static final javax.xml.namespace.QName _AtualizarMargemMargem2_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "margem2");
    private static final javax.xml.namespace.QName _AtualizarMargemMargem3_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "margem3");
    private static final javax.xml.namespace.QName _ConsultarConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codigo");
    private static final javax.xml.namespace.QName _ConsultarOrgaoCodigoOrgao_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codigoOrgao");
    private static final javax.xml.namespace.QName _ConsultarOrgaoCodigoEstabelecimento_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codigoEstabelecimento");
    private static final javax.xml.namespace.QName _ConsultarServicoCodigoServico_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codigoServico");
    private static final javax.xml.namespace.QName _ConsultarServicoCodigoNaturezaServico_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codigoNaturezaServico");
    private static final javax.xml.namespace.QName _CadastrarUsuarioResponseSenha_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "senha");
    private static final javax.xml.namespace.QName _ConsultarMovimentoFinanceiroMatricula_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "matricula");
    private static final javax.xml.namespace.QName _ConsultarMovimentoFinanceiroConsignatariaCodigo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "consignatariaCodigo");
    private static final javax.xml.namespace.QName _ConsultarMovimentoFinanceiroServicoCodigo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "servicoCodigo");
    private static final javax.xml.namespace.QName _ConsultarMovimentoFinanceiroCodVerba_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "codVerba");
    private static final javax.xml.namespace.QName _DownloadArquivoIntegracaoResponseArquivo_QNAME = new javax.xml.namespace.QName("FolhaService-v1_0", "arquivo");
    private static final javax.xml.namespace.QName _ArquivoCodigoOrgao_QNAME = new javax.xml.namespace.QName("Arquivo", "codigoOrgao");
    private static final javax.xml.namespace.QName _ArquivoCodigoEstabelecimento_QNAME = new javax.xml.namespace.QName("Arquivo", "codigoEstabelecimento");
    private static final javax.xml.namespace.QName _PerfilAtivo_QNAME = new javax.xml.namespace.QName("Perfil", "ativo");
    private static final javax.xml.namespace.QName _PerfilDataExpiracao_QNAME = new javax.xml.namespace.QName("Perfil", "dataExpiracao");
    private static final javax.xml.namespace.QName _PerfilIpAcesso_QNAME = new javax.xml.namespace.QName("Perfil", "ipAcesso");
    private static final javax.xml.namespace.QName _PerfilDdnsAcesso_QNAME = new javax.xml.namespace.QName("Perfil", "ddnsAcesso");
    private static final javax.xml.namespace.QName _ParametroServicoValor_QNAME = new javax.xml.namespace.QName("ParametroServico", "valor");
    private static final javax.xml.namespace.QName _ParametroServicoValorRef_QNAME = new javax.xml.namespace.QName("ParametroServico", "valorRef");
    private static final javax.xml.namespace.QName _ParametroSistemaValor_QNAME = new javax.xml.namespace.QName("ParametroSistema", "valor");
    private static final javax.xml.namespace.QName _ConsignanteEmail_QNAME = new javax.xml.namespace.QName("Consignante", "email");
    private static final javax.xml.namespace.QName _ConsignanteEmailFolha_QNAME = new javax.xml.namespace.QName("Consignante", "emailFolha");
    private static final javax.xml.namespace.QName _ConsignanteResponsavel_QNAME = new javax.xml.namespace.QName("Consignante", "responsavel");
    private static final javax.xml.namespace.QName _ConsignanteCargoResponsavel_QNAME = new javax.xml.namespace.QName("Consignante", "cargoResponsavel");
    private static final javax.xml.namespace.QName _ConsignanteTelefoneResponsavel_QNAME = new javax.xml.namespace.QName("Consignante", "telefoneResponsavel");
    private static final javax.xml.namespace.QName _ConsignanteResponsavel2_QNAME = new javax.xml.namespace.QName("Consignante", "responsavel2");
    private static final javax.xml.namespace.QName _ConsignanteCargoResponsavel2_QNAME = new javax.xml.namespace.QName("Consignante", "cargoResponsavel2");
    private static final javax.xml.namespace.QName _ConsignanteTelefoneResponsavel2_QNAME = new javax.xml.namespace.QName("Consignante", "telefoneResponsavel2");
    private static final javax.xml.namespace.QName _ConsignanteResponsavel3_QNAME = new javax.xml.namespace.QName("Consignante", "responsavel3");
    private static final javax.xml.namespace.QName _ConsignanteCargoResponsavel3_QNAME = new javax.xml.namespace.QName("Consignante", "cargoResponsavel3");
    private static final javax.xml.namespace.QName _ConsignanteTelefoneResponsavel3_QNAME = new javax.xml.namespace.QName("Consignante", "telefoneResponsavel3");
    private static final javax.xml.namespace.QName _ConsignanteLogradouro_QNAME = new javax.xml.namespace.QName("Consignante", "logradouro");
    private static final javax.xml.namespace.QName _ConsignanteNumero_QNAME = new javax.xml.namespace.QName("Consignante", "numero");
    private static final javax.xml.namespace.QName _ConsignanteComplemento_QNAME = new javax.xml.namespace.QName("Consignante", "complemento");
    private static final javax.xml.namespace.QName _ConsignanteBairro_QNAME = new javax.xml.namespace.QName("Consignante", "bairro");
    private static final javax.xml.namespace.QName _ConsignanteCidade_QNAME = new javax.xml.namespace.QName("Consignante", "cidade");
    private static final javax.xml.namespace.QName _ConsignanteUf_QNAME = new javax.xml.namespace.QName("Consignante", "uf");
    private static final javax.xml.namespace.QName _ConsignanteCep_QNAME = new javax.xml.namespace.QName("Consignante", "cep");
    private static final javax.xml.namespace.QName _ConsignanteTelefone_QNAME = new javax.xml.namespace.QName("Consignante", "telefone");
    private static final javax.xml.namespace.QName _ConsignanteFax_QNAME = new javax.xml.namespace.QName("Consignante", "fax");
    private static final javax.xml.namespace.QName _ConsignanteAtivo_QNAME = new javax.xml.namespace.QName("Consignante", "ativo");
    private static final javax.xml.namespace.QName _ConsignanteIpAcesso_QNAME = new javax.xml.namespace.QName("Consignante", "ipAcesso");
    private static final javax.xml.namespace.QName _ConsignanteDdnsAcesso_QNAME = new javax.xml.namespace.QName("Consignante", "ddnsAcesso");
    private static final javax.xml.namespace.QName _ConsignanteCodigoFolha_QNAME = new javax.xml.namespace.QName("Consignante", "codigoFolha");
    private static final javax.xml.namespace.QName _ConsignanteDataCobranca_QNAME = new javax.xml.namespace.QName("Consignante", "dataCobranca");
    private static final javax.xml.namespace.QName _ConsignanteTipoConsignante_QNAME = new javax.xml.namespace.QName("Consignante", "tipoConsignante");
    private static final javax.xml.namespace.QName _ConsignanteSistemaFolha_QNAME = new javax.xml.namespace.QName("Consignante", "sistemaFolha");
    private static final javax.xml.namespace.QName _ConsignanteIdentificadorInterno_QNAME = new javax.xml.namespace.QName("Consignante", "identificadorInterno");
    private static final javax.xml.namespace.QName _UsuarioSenha_QNAME = new javax.xml.namespace.QName("Usuario", "senha");
    private static final javax.xml.namespace.QName _UsuarioEmail_QNAME = new javax.xml.namespace.QName("Usuario", "email");
    private static final javax.xml.namespace.QName _UsuarioCpf_QNAME = new javax.xml.namespace.QName("Usuario", "cpf");
    private static final javax.xml.namespace.QName _UsuarioTelefone_QNAME = new javax.xml.namespace.QName("Usuario", "telefone");
    private static final javax.xml.namespace.QName _UsuarioPerfilCodigo_QNAME = new javax.xml.namespace.QName("Usuario", "perfilCodigo");
    private static final javax.xml.namespace.QName _UsuarioCentralizador_QNAME = new javax.xml.namespace.QName("Usuario", "centralizador");
    private static final javax.xml.namespace.QName _UsuarioEntidadeCodigo_QNAME = new javax.xml.namespace.QName("Usuario", "entidadeCodigo");
    private static final javax.xml.namespace.QName _UsuarioEntidadeMaeCodigo_QNAME = new javax.xml.namespace.QName("Usuario", "entidadeMaeCodigo");
    private static final javax.xml.namespace.QName _ServicoCodigoNaturezaServico_QNAME = new javax.xml.namespace.QName("Servico", "codigoNaturezaServico");
    private static final javax.xml.namespace.QName _ServicoNaturezaServico_QNAME = new javax.xml.namespace.QName("Servico", "naturezaServico");
    private static final javax.xml.namespace.QName _ServicoAtivo_QNAME = new javax.xml.namespace.QName("Servico", "ativo");
    private static final javax.xml.namespace.QName _ServicoObservacao_QNAME = new javax.xml.namespace.QName("Servico", "observacao");
    private static final javax.xml.namespace.QName _ServicoPrioridade_QNAME = new javax.xml.namespace.QName("Servico", "prioridade");
    private static final javax.xml.namespace.QName _EstabelecimentoEmail_QNAME = new javax.xml.namespace.QName("Estabelecimento", "email");
    private static final javax.xml.namespace.QName _EstabelecimentoResponsavel_QNAME = new javax.xml.namespace.QName("Estabelecimento", "responsavel");
    private static final javax.xml.namespace.QName _EstabelecimentoNumero_QNAME = new javax.xml.namespace.QName("Estabelecimento", "numero");
    private static final javax.xml.namespace.QName _EstabelecimentoLogradouro_QNAME = new javax.xml.namespace.QName("Estabelecimento", "logradouro");
    private static final javax.xml.namespace.QName _EstabelecimentoComplemento_QNAME = new javax.xml.namespace.QName("Estabelecimento", "complemento");
    private static final javax.xml.namespace.QName _EstabelecimentoBairro_QNAME = new javax.xml.namespace.QName("Estabelecimento", "bairro");
    private static final javax.xml.namespace.QName _EstabelecimentoCidade_QNAME = new javax.xml.namespace.QName("Estabelecimento", "cidade");
    private static final javax.xml.namespace.QName _EstabelecimentoUf_QNAME = new javax.xml.namespace.QName("Estabelecimento", "uf");
    private static final javax.xml.namespace.QName _EstabelecimentoCep_QNAME = new javax.xml.namespace.QName("Estabelecimento", "cep");
    private static final javax.xml.namespace.QName _EstabelecimentoTelefone_QNAME = new javax.xml.namespace.QName("Estabelecimento", "telefone");
    private static final javax.xml.namespace.QName _EstabelecimentoFax_QNAME = new javax.xml.namespace.QName("Estabelecimento", "fax");
    private static final javax.xml.namespace.QName _EstabelecimentoAtivo_QNAME = new javax.xml.namespace.QName("Estabelecimento", "ativo");
    private static final javax.xml.namespace.QName _EstabelecimentoResponsavel2_QNAME = new javax.xml.namespace.QName("Estabelecimento", "responsavel2");
    private static final javax.xml.namespace.QName _EstabelecimentoResponsavel3_QNAME = new javax.xml.namespace.QName("Estabelecimento", "responsavel3");
    private static final javax.xml.namespace.QName _EstabelecimentoCargoResponsavel_QNAME = new javax.xml.namespace.QName("Estabelecimento", "cargoResponsavel");
    private static final javax.xml.namespace.QName _EstabelecimentoCargoResponsavel2_QNAME = new javax.xml.namespace.QName("Estabelecimento", "cargoResponsavel2");
    private static final javax.xml.namespace.QName _EstabelecimentoCargoResponsavel3_QNAME = new javax.xml.namespace.QName("Estabelecimento", "cargoResponsavel3");
    private static final javax.xml.namespace.QName _EstabelecimentoTelefoneResponsavel_QNAME = new javax.xml.namespace.QName("Estabelecimento", "telefoneResponsavel");
    private static final javax.xml.namespace.QName _EstabelecimentoTelefoneResponsavel2_QNAME = new javax.xml.namespace.QName("Estabelecimento", "telefoneResponsavel2");
    private static final javax.xml.namespace.QName _EstabelecimentoTelefoneResponsavel3_QNAME = new javax.xml.namespace.QName("Estabelecimento", "telefoneResponsavel3");
    private static final javax.xml.namespace.QName _OrgaoNomeAbreviado_QNAME = new javax.xml.namespace.QName("Orgao", "nomeAbreviado");
    private static final javax.xml.namespace.QName _OrgaoCnpj_QNAME = new javax.xml.namespace.QName("Orgao", "cnpj");
    private static final javax.xml.namespace.QName _OrgaoEmail_QNAME = new javax.xml.namespace.QName("Orgao", "email");
    private static final javax.xml.namespace.QName _OrgaoResponsavel_QNAME = new javax.xml.namespace.QName("Orgao", "responsavel");
    private static final javax.xml.namespace.QName _OrgaoNumero_QNAME = new javax.xml.namespace.QName("Orgao", "numero");
    private static final javax.xml.namespace.QName _OrgaoLogradouro_QNAME = new javax.xml.namespace.QName("Orgao", "logradouro");
    private static final javax.xml.namespace.QName _OrgaoComplemento_QNAME = new javax.xml.namespace.QName("Orgao", "complemento");
    private static final javax.xml.namespace.QName _OrgaoBairro_QNAME = new javax.xml.namespace.QName("Orgao", "bairro");
    private static final javax.xml.namespace.QName _OrgaoCidade_QNAME = new javax.xml.namespace.QName("Orgao", "cidade");
    private static final javax.xml.namespace.QName _OrgaoUf_QNAME = new javax.xml.namespace.QName("Orgao", "uf");
    private static final javax.xml.namespace.QName _OrgaoCep_QNAME = new javax.xml.namespace.QName("Orgao", "cep");
    private static final javax.xml.namespace.QName _OrgaoTelefone_QNAME = new javax.xml.namespace.QName("Orgao", "telefone");
    private static final javax.xml.namespace.QName _OrgaoFax_QNAME = new javax.xml.namespace.QName("Orgao", "fax");
    private static final javax.xml.namespace.QName _OrgaoDiaCorte_QNAME = new javax.xml.namespace.QName("Orgao", "diaCorte");
    private static final javax.xml.namespace.QName _OrgaoDiaRepasse_QNAME = new javax.xml.namespace.QName("Orgao", "diaRepasse");
    private static final javax.xml.namespace.QName _OrgaoAtivo_QNAME = new javax.xml.namespace.QName("Orgao", "ativo");
    private static final javax.xml.namespace.QName _OrgaoResponsavel2_QNAME = new javax.xml.namespace.QName("Orgao", "responsavel2");
    private static final javax.xml.namespace.QName _OrgaoResponsavel3_QNAME = new javax.xml.namespace.QName("Orgao", "responsavel3");
    private static final javax.xml.namespace.QName _OrgaoCargoResponsavel_QNAME = new javax.xml.namespace.QName("Orgao", "cargoResponsavel");
    private static final javax.xml.namespace.QName _OrgaoCargoResponsavel2_QNAME = new javax.xml.namespace.QName("Orgao", "cargoResponsavel2");
    private static final javax.xml.namespace.QName _OrgaoCargoResponsavel3_QNAME = new javax.xml.namespace.QName("Orgao", "cargoResponsavel3");
    private static final javax.xml.namespace.QName _OrgaoTelefoneResponsavel_QNAME = new javax.xml.namespace.QName("Orgao", "telefoneResponsavel");
    private static final javax.xml.namespace.QName _OrgaoTelefoneResponsavel2_QNAME = new javax.xml.namespace.QName("Orgao", "telefoneResponsavel2");
    private static final javax.xml.namespace.QName _OrgaoTelefoneResponsavel3_QNAME = new javax.xml.namespace.QName("Orgao", "telefoneResponsavel3");
    private static final javax.xml.namespace.QName _OrgaoIpAcesso_QNAME = new javax.xml.namespace.QName("Orgao", "ipAcesso");
    private static final javax.xml.namespace.QName _OrgaoDdnsAcesso_QNAME = new javax.xml.namespace.QName("Orgao", "ddnsAcesso");
    private static final javax.xml.namespace.QName _ConvenioVerbaConvenioRef_QNAME = new javax.xml.namespace.QName("Convenio", "verbaConvenioRef");
    private static final javax.xml.namespace.QName _ConvenioVerbaConvenioFerias_QNAME = new javax.xml.namespace.QName("Convenio", "verbaConvenioFerias");
    private static final javax.xml.namespace.QName _ConvenioNomeOrgao_QNAME = new javax.xml.namespace.QName("Convenio", "nomeOrgao");
    private static final javax.xml.namespace.QName _ConvenioNomeConsignataria_QNAME = new javax.xml.namespace.QName("Convenio", "nomeConsignataria");
    private static final javax.xml.namespace.QName _ConvenioDescricaoServico_QNAME = new javax.xml.namespace.QName("Convenio", "descricaoServico");
    private static final javax.xml.namespace.QName _ConsignatariaEmail_QNAME = new javax.xml.namespace.QName("Consignataria", "email");
    private static final javax.xml.namespace.QName _ConsignatariaCnpj_QNAME = new javax.xml.namespace.QName("Consignataria", "cnpj");
    private static final javax.xml.namespace.QName _ConsignatariaResponsavel_QNAME = new javax.xml.namespace.QName("Consignataria", "responsavel");
    private static final javax.xml.namespace.QName _ConsignatariaLogradouro_QNAME = new javax.xml.namespace.QName("Consignataria", "logradouro");
    private static final javax.xml.namespace.QName _ConsignatariaNumero_QNAME = new javax.xml.namespace.QName("Consignataria", "numero");
    private static final javax.xml.namespace.QName _ConsignatariaComplemento_QNAME = new javax.xml.namespace.QName("Consignataria", "complemento");
    private static final javax.xml.namespace.QName _ConsignatariaBairro_QNAME = new javax.xml.namespace.QName("Consignataria", "bairro");
    private static final javax.xml.namespace.QName _ConsignatariaCidade_QNAME = new javax.xml.namespace.QName("Consignataria", "cidade");
    private static final javax.xml.namespace.QName _ConsignatariaUf_QNAME = new javax.xml.namespace.QName("Consignataria", "uf");
    private static final javax.xml.namespace.QName _ConsignatariaFax_QNAME = new javax.xml.namespace.QName("Consignataria", "fax");
    private static final javax.xml.namespace.QName _ConsignatariaNumeroBanco_QNAME = new javax.xml.namespace.QName("Consignataria", "numeroBanco");
    private static final javax.xml.namespace.QName _ConsignatariaNumeroConta_QNAME = new javax.xml.namespace.QName("Consignataria", "numeroConta");
    private static final javax.xml.namespace.QName _ConsignatariaNumeroAgencia_QNAME = new javax.xml.namespace.QName("Consignataria", "numeroAgencia");
    private static final javax.xml.namespace.QName _ConsignatariaDigitoConta_QNAME = new javax.xml.namespace.QName("Consignataria", "digitoConta");
    private static final javax.xml.namespace.QName _ConsignatariaAtivo_QNAME = new javax.xml.namespace.QName("Consignataria", "ativo");
    private static final javax.xml.namespace.QName _ConsignatariaResponsavel2_QNAME = new javax.xml.namespace.QName("Consignataria", "responsavel2");
    private static final javax.xml.namespace.QName _ConsignatariaResponsavel3_QNAME = new javax.xml.namespace.QName("Consignataria", "responsavel3");
    private static final javax.xml.namespace.QName _ConsignatariaCargoResponsavel_QNAME = new javax.xml.namespace.QName("Consignataria", "cargoResponsavel");
    private static final javax.xml.namespace.QName _ConsignatariaCargoResponsavel2_QNAME = new javax.xml.namespace.QName("Consignataria", "cargoResponsavel2");
    private static final javax.xml.namespace.QName _ConsignatariaCargoResponsavel3_QNAME = new javax.xml.namespace.QName("Consignataria", "cargoResponsavel3");
    private static final javax.xml.namespace.QName _ConsignatariaTelefoneResponsavel_QNAME = new javax.xml.namespace.QName("Consignataria", "telefoneResponsavel");
    private static final javax.xml.namespace.QName _ConsignatariaTelefoneResponsavel2_QNAME = new javax.xml.namespace.QName("Consignataria", "telefoneResponsavel2");
    private static final javax.xml.namespace.QName _ConsignatariaTelefoneResponsavel3_QNAME = new javax.xml.namespace.QName("Consignataria", "telefoneResponsavel3");
    private static final javax.xml.namespace.QName _ConsignatariaTextoContato_QNAME = new javax.xml.namespace.QName("Consignataria", "textoContato");
    private static final javax.xml.namespace.QName _ConsignatariaContato_QNAME = new javax.xml.namespace.QName("Consignataria", "contato");
    private static final javax.xml.namespace.QName _ConsignatariaTelefoneContato_QNAME = new javax.xml.namespace.QName("Consignataria", "telefoneContato");
    private static final javax.xml.namespace.QName _ConsignatariaEndereco2_QNAME = new javax.xml.namespace.QName("Consignataria", "endereco2");
    private static final javax.xml.namespace.QName _ConsignatariaNomeAbreviado_QNAME = new javax.xml.namespace.QName("Consignataria", "nomeAbreviado");
    private static final javax.xml.namespace.QName _ConsignatariaDataExpiracao_QNAME = new javax.xml.namespace.QName("Consignataria", "dataExpiracao");
    private static final javax.xml.namespace.QName _ConsignatariaNumeroContrato_QNAME = new javax.xml.namespace.QName("Consignataria", "numeroContrato");
    private static final javax.xml.namespace.QName _ConsignatariaIpAcesso_QNAME = new javax.xml.namespace.QName("Consignataria", "ipAcesso");
    private static final javax.xml.namespace.QName _ConsignatariaDdnsAcesso_QNAME = new javax.xml.namespace.QName("Consignataria", "ddnsAcesso");
    private static final javax.xml.namespace.QName _ConsignatariaExigeEnderecoAcesso_QNAME = new javax.xml.namespace.QName("Consignataria", "exigeEnderecoAcesso");
    private static final javax.xml.namespace.QName _ConsignatariaUnidadeOrganizacional_QNAME = new javax.xml.namespace.QName("Consignataria", "unidadeOrganizacional");
    private static final javax.xml.namespace.QName _ConsignatariaNaturezaCodigo_QNAME = new javax.xml.namespace.QName("Consignataria", "naturezaCodigo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zetra.econsig.webservice.soap.folha.v1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CadastrarConsignataria }
     *
     * @return
     *     the new instance of {@link CadastrarConsignataria }
     */
    public CadastrarConsignataria createCadastrarConsignataria() {
        return new CadastrarConsignataria();
    }

    /**
     * Create an instance of {@link Consignataria }
     *
     * @return
     *     the new instance of {@link Consignataria }
     */
    public Consignataria createConsignataria() {
        return new Consignataria();
    }

    /**
     * Create an instance of {@link CadastrarConsignatariaResponse }
     *
     * @return
     *     the new instance of {@link CadastrarConsignatariaResponse }
     */
    public CadastrarConsignatariaResponse createCadastrarConsignatariaResponse() {
        return new CadastrarConsignatariaResponse();
    }

    /**
     * Create an instance of {@link CadastrarConvenio }
     *
     * @return
     *     the new instance of {@link CadastrarConvenio }
     */
    public CadastrarConvenio createCadastrarConvenio() {
        return new CadastrarConvenio();
    }

    /**
     * Create an instance of {@link Convenio }
     *
     * @return
     *     the new instance of {@link Convenio }
     */
    public Convenio createConvenio() {
        return new Convenio();
    }

    /**
     * Create an instance of {@link CadastrarConvenioResponse }
     *
     * @return
     *     the new instance of {@link CadastrarConvenioResponse }
     */
    public CadastrarConvenioResponse createCadastrarConvenioResponse() {
        return new CadastrarConvenioResponse();
    }

    /**
     * Create an instance of {@link CadastrarOrgao }
     *
     * @return
     *     the new instance of {@link CadastrarOrgao }
     */
    public CadastrarOrgao createCadastrarOrgao() {
        return new CadastrarOrgao();
    }

    /**
     * Create an instance of {@link Orgao }
     *
     * @return
     *     the new instance of {@link Orgao }
     */
    public Orgao createOrgao() {
        return new Orgao();
    }

    /**
     * Create an instance of {@link CadastrarOrgaoResponse }
     *
     * @return
     *     the new instance of {@link CadastrarOrgaoResponse }
     */
    public CadastrarOrgaoResponse createCadastrarOrgaoResponse() {
        return new CadastrarOrgaoResponse();
    }

    /**
     * Create an instance of {@link CadastrarEstabelecimento }
     *
     * @return
     *     the new instance of {@link CadastrarEstabelecimento }
     */
    public CadastrarEstabelecimento createCadastrarEstabelecimento() {
        return new CadastrarEstabelecimento();
    }

    /**
     * Create an instance of {@link Estabelecimento }
     *
     * @return
     *     the new instance of {@link Estabelecimento }
     */
    public Estabelecimento createEstabelecimento() {
        return new Estabelecimento();
    }

    /**
     * Create an instance of {@link CadastrarEstabelecimentoResponse }
     *
     * @return
     *     the new instance of {@link CadastrarEstabelecimentoResponse }
     */
    public CadastrarEstabelecimentoResponse createCadastrarEstabelecimentoResponse() {
        return new CadastrarEstabelecimentoResponse();
    }

    /**
     * Create an instance of {@link CadastrarServico }
     *
     * @return
     *     the new instance of {@link CadastrarServico }
     */
    public CadastrarServico createCadastrarServico() {
        return new CadastrarServico();
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
     * Create an instance of {@link CadastrarServicoResponse }
     *
     * @return
     *     the new instance of {@link CadastrarServicoResponse }
     */
    public CadastrarServicoResponse createCadastrarServicoResponse() {
        return new CadastrarServicoResponse();
    }

    /**
     * Create an instance of {@link AtualizarMargem }
     *
     * @return
     *     the new instance of {@link AtualizarMargem }
     */
    public AtualizarMargem createAtualizarMargem() {
        return new AtualizarMargem();
    }

    /**
     * Create an instance of {@link AtualizarMargemResponse }
     *
     * @return
     *     the new instance of {@link AtualizarMargemResponse }
     */
    public AtualizarMargemResponse createAtualizarMargemResponse() {
        return new AtualizarMargemResponse();
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
     * Create an instance of {@link ConsultarConsignataria }
     *
     * @return
     *     the new instance of {@link ConsultarConsignataria }
     */
    public ConsultarConsignataria createConsultarConsignataria() {
        return new ConsultarConsignataria();
    }

    /**
     * Create an instance of {@link ConsultarConsignatariaResponse }
     *
     * @return
     *     the new instance of {@link ConsultarConsignatariaResponse }
     */
    public ConsultarConsignatariaResponse createConsultarConsignatariaResponse() {
        return new ConsultarConsignatariaResponse();
    }

    /**
     * Create an instance of {@link ConsultarOrgao }
     *
     * @return
     *     the new instance of {@link ConsultarOrgao }
     */
    public ConsultarOrgao createConsultarOrgao() {
        return new ConsultarOrgao();
    }

    /**
     * Create an instance of {@link ConsultarOrgaoResponse }
     *
     * @return
     *     the new instance of {@link ConsultarOrgaoResponse }
     */
    public ConsultarOrgaoResponse createConsultarOrgaoResponse() {
        return new ConsultarOrgaoResponse();
    }

    /**
     * Create an instance of {@link ConsultarConvenio }
     *
     * @return
     *     the new instance of {@link ConsultarConvenio }
     */
    public ConsultarConvenio createConsultarConvenio() {
        return new ConsultarConvenio();
    }

    /**
     * Create an instance of {@link ConsultarConvenioResponse }
     *
     * @return
     *     the new instance of {@link ConsultarConvenioResponse }
     */
    public ConsultarConvenioResponse createConsultarConvenioResponse() {
        return new ConsultarConvenioResponse();
    }

    /**
     * Create an instance of {@link ConsultarEstabelecimento }
     *
     * @return
     *     the new instance of {@link ConsultarEstabelecimento }
     */
    public ConsultarEstabelecimento createConsultarEstabelecimento() {
        return new ConsultarEstabelecimento();
    }

    /**
     * Create an instance of {@link ConsultarEstabelecimentoResponse }
     *
     * @return
     *     the new instance of {@link ConsultarEstabelecimentoResponse }
     */
    public ConsultarEstabelecimentoResponse createConsultarEstabelecimentoResponse() {
        return new ConsultarEstabelecimentoResponse();
    }

    /**
     * Create an instance of {@link ConsultarServico }
     *
     * @return
     *     the new instance of {@link ConsultarServico }
     */
    public ConsultarServico createConsultarServico() {
        return new ConsultarServico();
    }

    /**
     * Create an instance of {@link ConsultarServicoResponse }
     *
     * @return
     *     the new instance of {@link ConsultarServicoResponse }
     */
    public ConsultarServicoResponse createConsultarServicoResponse() {
        return new ConsultarServicoResponse();
    }

    /**
     * Create an instance of {@link CadastrarUsuario }
     *
     * @return
     *     the new instance of {@link CadastrarUsuario }
     */
    public CadastrarUsuario createCadastrarUsuario() {
        return new CadastrarUsuario();
    }

    /**
     * Create an instance of {@link Usuario }
     *
     * @return
     *     the new instance of {@link Usuario }
     */
    public Usuario createUsuario() {
        return new Usuario();
    }

    /**
     * Create an instance of {@link CadastrarUsuarioResponse }
     *
     * @return
     *     the new instance of {@link CadastrarUsuarioResponse }
     */
    public CadastrarUsuarioResponse createCadastrarUsuarioResponse() {
        return new CadastrarUsuarioResponse();
    }

    /**
     * Create an instance of {@link ModificarUsuario }
     *
     * @return
     *     the new instance of {@link ModificarUsuario }
     */
    public ModificarUsuario createModificarUsuario() {
        return new ModificarUsuario();
    }

    /**
     * Create an instance of {@link ModificarUsuarioResponse }
     *
     * @return
     *     the new instance of {@link ModificarUsuarioResponse }
     */
    public ModificarUsuarioResponse createModificarUsuarioResponse() {
        return new ModificarUsuarioResponse();
    }

    /**
     * Create an instance of {@link ModificarConsignante }
     *
     * @return
     *     the new instance of {@link ModificarConsignante }
     */
    public ModificarConsignante createModificarConsignante() {
        return new ModificarConsignante();
    }

    /**
     * Create an instance of {@link Consignante }
     *
     * @return
     *     the new instance of {@link Consignante }
     */
    public Consignante createConsignante() {
        return new Consignante();
    }

    /**
     * Create an instance of {@link ModificarConsignanteResponse }
     *
     * @return
     *     the new instance of {@link ModificarConsignanteResponse }
     */
    public ModificarConsignanteResponse createModificarConsignanteResponse() {
        return new ModificarConsignanteResponse();
    }

    /**
     * Create an instance of {@link ModificarParametroSistema }
     *
     * @return
     *     the new instance of {@link ModificarParametroSistema }
     */
    public ModificarParametroSistema createModificarParametroSistema() {
        return new ModificarParametroSistema();
    }

    /**
     * Create an instance of {@link ParametroSistema }
     *
     * @return
     *     the new instance of {@link ParametroSistema }
     */
    public ParametroSistema createParametroSistema() {
        return new ParametroSistema();
    }

    /**
     * Create an instance of {@link ModificarParametroSistemaResponse }
     *
     * @return
     *     the new instance of {@link ModificarParametroSistemaResponse }
     */
    public ModificarParametroSistemaResponse createModificarParametroSistemaResponse() {
        return new ModificarParametroSistemaResponse();
    }

    /**
     * Create an instance of {@link ModificarParametroServico }
     *
     * @return
     *     the new instance of {@link ModificarParametroServico }
     */
    public ModificarParametroServico createModificarParametroServico() {
        return new ModificarParametroServico();
    }

    /**
     * Create an instance of {@link ParametroServico }
     *
     * @return
     *     the new instance of {@link ParametroServico }
     */
    public ParametroServico createParametroServico() {
        return new ParametroServico();
    }

    /**
     * Create an instance of {@link ModificarParametroServicoResponse }
     *
     * @return
     *     the new instance of {@link ModificarParametroServicoResponse }
     */
    public ModificarParametroServicoResponse createModificarParametroServicoResponse() {
        return new ModificarParametroServicoResponse();
    }

    /**
     * Create an instance of {@link AtualizarCalendarioFolha }
     *
     * @return
     *     the new instance of {@link AtualizarCalendarioFolha }
     */
    public AtualizarCalendarioFolha createAtualizarCalendarioFolha() {
        return new AtualizarCalendarioFolha();
    }

    /**
     * Create an instance of {@link AtualizarCalendarioFolhaResponse }
     *
     * @return
     *     the new instance of {@link AtualizarCalendarioFolhaResponse }
     */
    public AtualizarCalendarioFolhaResponse createAtualizarCalendarioFolhaResponse() {
        return new AtualizarCalendarioFolhaResponse();
    }

    /**
     * Create an instance of {@link ConsultarMovimentoFinanceiro }
     *
     * @return
     *     the new instance of {@link ConsultarMovimentoFinanceiro }
     */
    public ConsultarMovimentoFinanceiro createConsultarMovimentoFinanceiro() {
        return new ConsultarMovimentoFinanceiro();
    }

    /**
     * Create an instance of {@link ConsultarMovimentoFinanceiroResponse }
     *
     * @return
     *     the new instance of {@link ConsultarMovimentoFinanceiroResponse }
     */
    public ConsultarMovimentoFinanceiroResponse createConsultarMovimentoFinanceiroResponse() {
        return new ConsultarMovimentoFinanceiroResponse();
    }

    /**
     * Create an instance of {@link MovimentoFinanceiro }
     *
     * @return
     *     the new instance of {@link MovimentoFinanceiro }
     */
    public MovimentoFinanceiro createMovimentoFinanceiro() {
        return new MovimentoFinanceiro();
    }

    /**
     * Create an instance of {@link ConsultarPerfilUsuario }
     *
     * @return
     *     the new instance of {@link ConsultarPerfilUsuario }
     */
    public ConsultarPerfilUsuario createConsultarPerfilUsuario() {
        return new ConsultarPerfilUsuario();
    }

    /**
     * Create an instance of {@link ConsultarPerfilUsuarioResponse }
     *
     * @return
     *     the new instance of {@link ConsultarPerfilUsuarioResponse }
     */
    public ConsultarPerfilUsuarioResponse createConsultarPerfilUsuarioResponse() {
        return new ConsultarPerfilUsuarioResponse();
    }

    /**
     * Create an instance of {@link Perfil }
     *
     * @return
     *     the new instance of {@link Perfil }
     */
    public Perfil createPerfil() {
        return new Perfil();
    }

    /**
     * Create an instance of {@link ListarArquivoIntegracao }
     *
     * @return
     *     the new instance of {@link ListarArquivoIntegracao }
     */
    public ListarArquivoIntegracao createListarArquivoIntegracao() {
        return new ListarArquivoIntegracao();
    }

    /**
     * Create an instance of {@link TipoArquivo }
     *
     * @return
     *     the new instance of {@link TipoArquivo }
     */
    public TipoArquivo createTipoArquivo() {
        return new TipoArquivo();
    }

    /**
     * Create an instance of {@link ListarArquivoIntegracaoResponse }
     *
     * @return
     *     the new instance of {@link ListarArquivoIntegracaoResponse }
     */
    public ListarArquivoIntegracaoResponse createListarArquivoIntegracaoResponse() {
        return new ListarArquivoIntegracaoResponse();
    }

    /**
     * Create an instance of {@link Arquivo }
     *
     * @return
     *     the new instance of {@link Arquivo }
     */
    public Arquivo createArquivo() {
        return new Arquivo();
    }

    /**
     * Create an instance of {@link EnviarArquivoIntegracao }
     *
     * @return
     *     the new instance of {@link EnviarArquivoIntegracao }
     */
    public EnviarArquivoIntegracao createEnviarArquivoIntegracao() {
        return new EnviarArquivoIntegracao();
    }

    /**
     * Create an instance of {@link EnviarArquivoIntegracaoResponse }
     *
     * @return
     *     the new instance of {@link EnviarArquivoIntegracaoResponse }
     */
    public EnviarArquivoIntegracaoResponse createEnviarArquivoIntegracaoResponse() {
        return new EnviarArquivoIntegracaoResponse();
    }

    /**
     * Create an instance of {@link DownloadArquivoIntegracao }
     *
     * @return
     *     the new instance of {@link DownloadArquivoIntegracao }
     */
    public DownloadArquivoIntegracao createDownloadArquivoIntegracao() {
        return new DownloadArquivoIntegracao();
    }

    /**
     * Create an instance of {@link DownloadArquivoIntegracaoResponse }
     *
     * @return
     *     the new instance of {@link DownloadArquivoIntegracaoResponse }
     */
    public DownloadArquivoIntegracaoResponse createDownloadArquivoIntegracaoResponse() {
        return new DownloadArquivoIntegracaoResponse();
    }

    /**
     * Create an instance of {@link PapelUsuario }
     *
     * @return
     *     the new instance of {@link PapelUsuario }
     */
    public PapelUsuario createPapelUsuario() {
        return new PapelUsuario();
    }

    /**
     * Create an instance of {@link SituacaoUsuario }
     *
     * @return
     *     the new instance of {@link SituacaoUsuario }
     */
    public SituacaoUsuario createSituacaoUsuario() {
        return new SituacaoUsuario();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarConsignatariaResponse.class)
    public JAXBElement<java.lang.String> createCadastrarConsignatariaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarConsignatariaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarConvenioResponse.class)
    public JAXBElement<java.lang.String> createCadastrarConvenioResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarConvenioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarOrgaoResponse.class)
    public JAXBElement<java.lang.String> createCadastrarOrgaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarOrgaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarEstabelecimentoResponse.class)
    public JAXBElement<java.lang.String> createCadastrarEstabelecimentoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarEstabelecimentoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarServicoResponse.class)
    public JAXBElement<java.lang.String> createCadastrarServicoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarServicoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "cpf", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.String> createAtualizarMargemCpf(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemCpf_QNAME, java.lang.String.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "orgaoCodigo", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.String> createAtualizarMargemOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemOrgaoCodigo_QNAME, java.lang.String.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "estabelecimentoCodigo", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.String> createAtualizarMargemEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemEstabelecimentoCodigo_QNAME, java.lang.String.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "margem1", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.Double> createAtualizarMargemMargem1(java.lang.Double value) {
        return new JAXBElement<>(_AtualizarMargemMargem1_QNAME, java.lang.Double.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "margem2", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.Double> createAtualizarMargemMargem2(java.lang.Double value) {
        return new JAXBElement<>(_AtualizarMargemMargem2_QNAME, java.lang.Double.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "margem3", scope = AtualizarMargem.class)
    public JAXBElement<java.lang.Double> createAtualizarMargemMargem3(java.lang.Double value) {
        return new JAXBElement<>(_AtualizarMargemMargem3_QNAME, java.lang.Double.class, AtualizarMargem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = AtualizarMargemResponse.class)
    public JAXBElement<java.lang.String> createAtualizarMargemResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, AtualizarMargemResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigo", scope = ConsultarConsignataria.class)
    public JAXBElement<java.lang.String> createConsultarConsignatariaCodigo(java.lang.String value) {
        return new JAXBElement<>(_ConsultarConsignatariaCodigo_QNAME, java.lang.String.class, ConsultarConsignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarConsignatariaResponse.class)
    public JAXBElement<java.lang.String> createConsultarConsignatariaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarConsignatariaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoOrgao", scope = ConsultarOrgao.class)
    public JAXBElement<java.lang.String> createConsultarOrgaoCodigoOrgao(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoOrgao_QNAME, java.lang.String.class, ConsultarOrgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = ConsultarOrgao.class)
    public JAXBElement<java.lang.String> createConsultarOrgaoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, ConsultarOrgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarOrgaoResponse.class)
    public JAXBElement<java.lang.String> createConsultarOrgaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarOrgaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = ConsultarConvenio.class)
    public JAXBElement<java.lang.String> createConsultarConvenioCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, ConsultarConvenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarConvenioResponse.class)
    public JAXBElement<java.lang.String> createConsultarConvenioResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarConvenioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = ConsultarEstabelecimento.class)
    public JAXBElement<java.lang.String> createConsultarEstabelecimentoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, ConsultarEstabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarEstabelecimentoResponse.class)
    public JAXBElement<java.lang.String> createConsultarEstabelecimentoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarEstabelecimentoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoServico", scope = ConsultarServico.class)
    public JAXBElement<java.lang.String> createConsultarServicoCodigoServico(java.lang.String value) {
        return new JAXBElement<>(_ConsultarServicoCodigoServico_QNAME, java.lang.String.class, ConsultarServico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoNaturezaServico", scope = ConsultarServico.class)
    public JAXBElement<java.lang.String> createConsultarServicoCodigoNaturezaServico(java.lang.String value) {
        return new JAXBElement<>(_ConsultarServicoCodigoNaturezaServico_QNAME, java.lang.String.class, ConsultarServico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarServicoResponse.class)
    public JAXBElement<java.lang.String> createConsultarServicoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarServicoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = CadastrarUsuarioResponse.class)
    public JAXBElement<java.lang.String> createCadastrarUsuarioResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, CadastrarUsuarioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "senha", scope = CadastrarUsuarioResponse.class)
    public JAXBElement<java.lang.String> createCadastrarUsuarioResponseSenha(java.lang.String value) {
        return new JAXBElement<>(_CadastrarUsuarioResponseSenha_QNAME, java.lang.String.class, CadastrarUsuarioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ModificarUsuarioResponse.class)
    public JAXBElement<java.lang.String> createModificarUsuarioResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ModificarUsuarioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "senha", scope = ModificarUsuarioResponse.class)
    public JAXBElement<java.lang.String> createModificarUsuarioResponseSenha(java.lang.String value) {
        return new JAXBElement<>(_CadastrarUsuarioResponseSenha_QNAME, java.lang.String.class, ModificarUsuarioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ModificarConsignanteResponse.class)
    public JAXBElement<java.lang.String> createModificarConsignanteResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ModificarConsignanteResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ModificarParametroSistemaResponse.class)
    public JAXBElement<java.lang.String> createModificarParametroSistemaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ModificarParametroSistemaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ModificarParametroServicoResponse.class)
    public JAXBElement<java.lang.String> createModificarParametroServicoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ModificarParametroServicoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = AtualizarCalendarioFolhaResponse.class)
    public JAXBElement<java.lang.String> createAtualizarCalendarioFolhaResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, AtualizarCalendarioFolhaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "matricula", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroMatricula(java.lang.String value) {
        return new JAXBElement<>(_ConsultarMovimentoFinanceiroMatricula_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "cpf", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroCpf(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemCpf_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "orgaoCodigo", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroOrgaoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemOrgaoCodigo_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "estabelecimentoCodigo", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroEstabelecimentoCodigo(java.lang.String value) {
        return new JAXBElement<>(_AtualizarMargemEstabelecimentoCodigo_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "consignatariaCodigo", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroConsignatariaCodigo(java.lang.String value) {
        return new JAXBElement<>(_ConsultarMovimentoFinanceiroConsignatariaCodigo_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "servicoCodigo", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroServicoCodigo(java.lang.String value) {
        return new JAXBElement<>(_ConsultarMovimentoFinanceiroServicoCodigo_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codVerba", scope = ConsultarMovimentoFinanceiro.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroCodVerba(java.lang.String value) {
        return new JAXBElement<>(_ConsultarMovimentoFinanceiroCodVerba_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiro.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarMovimentoFinanceiroResponse.class)
    public JAXBElement<java.lang.String> createConsultarMovimentoFinanceiroResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarMovimentoFinanceiroResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ConsultarPerfilUsuarioResponse.class)
    public JAXBElement<java.lang.String> createConsultarPerfilUsuarioResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ConsultarPerfilUsuarioResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = ListarArquivoIntegracao.class)
    public JAXBElement<java.lang.String> createListarArquivoIntegracaoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, ListarArquivoIntegracao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = ListarArquivoIntegracaoResponse.class)
    public JAXBElement<java.lang.String> createListarArquivoIntegracaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, ListarArquivoIntegracaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = EnviarArquivoIntegracao.class)
    public JAXBElement<java.lang.String> createEnviarArquivoIntegracaoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, EnviarArquivoIntegracao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = EnviarArquivoIntegracaoResponse.class)
    public JAXBElement<java.lang.String> createEnviarArquivoIntegracaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, EnviarArquivoIntegracaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codigoEstabelecimento", scope = DownloadArquivoIntegracao.class)
    public JAXBElement<java.lang.String> createDownloadArquivoIntegracaoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ConsultarOrgaoCodigoEstabelecimento_QNAME, java.lang.String.class, DownloadArquivoIntegracao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "codRetorno", scope = DownloadArquivoIntegracaoResponse.class)
    public JAXBElement<java.lang.String> createDownloadArquivoIntegracaoResponseCodRetorno(java.lang.String value) {
        return new JAXBElement<>(_CadastrarConsignatariaResponseCodRetorno_QNAME, java.lang.String.class, DownloadArquivoIntegracaoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     */
    @XmlElementDecl(namespace = "FolhaService-v1_0", name = "arquivo", scope = DownloadArquivoIntegracaoResponse.class)
    public JAXBElement<byte[]> createDownloadArquivoIntegracaoResponseArquivo(byte[] value) {
        return new JAXBElement<>(_DownloadArquivoIntegracaoResponseArquivo_QNAME, byte[].class, DownloadArquivoIntegracaoResponse.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Arquivo", name = "codigoOrgao", scope = Arquivo.class)
    public JAXBElement<java.lang.String> createArquivoCodigoOrgao(java.lang.String value) {
        return new JAXBElement<>(_ArquivoCodigoOrgao_QNAME, java.lang.String.class, Arquivo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Arquivo", name = "codigoEstabelecimento", scope = Arquivo.class)
    public JAXBElement<java.lang.String> createArquivoCodigoEstabelecimento(java.lang.String value) {
        return new JAXBElement<>(_ArquivoCodigoEstabelecimento_QNAME, java.lang.String.class, Arquivo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Perfil", name = "ativo", scope = Perfil.class)
    public JAXBElement<java.lang.Short> createPerfilAtivo(java.lang.Short value) {
        return new JAXBElement<>(_PerfilAtivo_QNAME, java.lang.Short.class, Perfil.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "Perfil", name = "dataExpiracao", scope = Perfil.class)
    public JAXBElement<XMLGregorianCalendar> createPerfilDataExpiracao(XMLGregorianCalendar value) {
        return new JAXBElement<>(_PerfilDataExpiracao_QNAME, XMLGregorianCalendar.class, Perfil.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Perfil", name = "ipAcesso", scope = Perfil.class)
    public JAXBElement<java.lang.String> createPerfilIpAcesso(java.lang.String value) {
        return new JAXBElement<>(_PerfilIpAcesso_QNAME, java.lang.String.class, Perfil.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Perfil", name = "ddnsAcesso", scope = Perfil.class)
    public JAXBElement<java.lang.String> createPerfilDdnsAcesso(java.lang.String value) {
        return new JAXBElement<>(_PerfilDdnsAcesso_QNAME, java.lang.String.class, Perfil.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroServico", name = "valor", scope = ParametroServico.class)
    public JAXBElement<java.lang.String> createParametroServicoValor(java.lang.String value) {
        return new JAXBElement<>(_ParametroServicoValor_QNAME, java.lang.String.class, ParametroServico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroServico", name = "valorRef", scope = ParametroServico.class)
    public JAXBElement<java.lang.String> createParametroServicoValorRef(java.lang.String value) {
        return new JAXBElement<>(_ParametroServicoValorRef_QNAME, java.lang.String.class, ParametroServico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "ParametroSistema", name = "valor", scope = ParametroSistema.class)
    public JAXBElement<java.lang.String> createParametroSistemaValor(java.lang.String value) {
        return new JAXBElement<>(_ParametroSistemaValor_QNAME, java.lang.String.class, ParametroSistema.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "email", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteEmail(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteEmail_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "emailFolha", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteEmailFolha(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteEmailFolha_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "responsavel", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteResponsavel_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "cargoResponsavel", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCargoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCargoResponsavel_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "telefoneResponsavel", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteTelefoneResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteTelefoneResponsavel_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "responsavel2", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteResponsavel2_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "cargoResponsavel2", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCargoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCargoResponsavel2_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "telefoneResponsavel2", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteTelefoneResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteTelefoneResponsavel2_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "responsavel3", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteResponsavel3_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "cargoResponsavel3", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCargoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCargoResponsavel3_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "telefoneResponsavel3", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteTelefoneResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteTelefoneResponsavel3_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "logradouro", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteLogradouro(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteLogradouro_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "numero", scope = Consignante.class)
    public JAXBElement<java.lang.Integer> createConsignanteNumero(java.lang.Integer value) {
        return new JAXBElement<>(_ConsignanteNumero_QNAME, java.lang.Integer.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "complemento", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteComplemento(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteComplemento_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "bairro", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteBairro(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteBairro_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "cidade", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCidade(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCidade_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "uf", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteUf(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteUf_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "cep", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCep(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCep_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "telefone", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteTelefone(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteTelefone_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "fax", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteFax(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteFax_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "ativo", scope = Consignante.class)
    public JAXBElement<java.lang.Short> createConsignanteAtivo(java.lang.Short value) {
        return new JAXBElement<>(_ConsignanteAtivo_QNAME, java.lang.Short.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "ipAcesso", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteIpAcesso(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteIpAcesso_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "ddnsAcesso", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteDdnsAcesso(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteDdnsAcesso_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "codigoFolha", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteCodigoFolha(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteCodigoFolha_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "dataCobranca", scope = Consignante.class)
    public JAXBElement<XMLGregorianCalendar> createConsignanteDataCobranca(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ConsignanteDataCobranca_QNAME, XMLGregorianCalendar.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "tipoConsignante", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteTipoConsignante(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteTipoConsignante_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "sistemaFolha", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteSistemaFolha(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteSistemaFolha_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignante", name = "identificadorInterno", scope = Consignante.class)
    public JAXBElement<java.lang.String> createConsignanteIdentificadorInterno(java.lang.String value) {
        return new JAXBElement<>(_ConsignanteIdentificadorInterno_QNAME, java.lang.String.class, Consignante.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "senha", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioSenha(java.lang.String value) {
        return new JAXBElement<>(_UsuarioSenha_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "email", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioEmail(java.lang.String value) {
        return new JAXBElement<>(_UsuarioEmail_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "cpf", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioCpf(java.lang.String value) {
        return new JAXBElement<>(_UsuarioCpf_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "telefone", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioTelefone(java.lang.String value) {
        return new JAXBElement<>(_UsuarioTelefone_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "perfilCodigo", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioPerfilCodigo(java.lang.String value) {
        return new JAXBElement<>(_UsuarioPerfilCodigo_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "centralizador", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioCentralizador(java.lang.String value) {
        return new JAXBElement<>(_UsuarioCentralizador_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "entidadeCodigo", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioEntidadeCodigo(java.lang.String value) {
        return new JAXBElement<>(_UsuarioEntidadeCodigo_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Usuario", name = "entidadeMaeCodigo", scope = Usuario.class)
    public JAXBElement<java.lang.String> createUsuarioEntidadeMaeCodigo(java.lang.String value) {
        return new JAXBElement<>(_UsuarioEntidadeMaeCodigo_QNAME, java.lang.String.class, Usuario.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Servico", name = "codigoNaturezaServico", scope = Servico.class)
    public JAXBElement<java.lang.String> createServicoCodigoNaturezaServico(java.lang.String value) {
        return new JAXBElement<>(_ServicoCodigoNaturezaServico_QNAME, java.lang.String.class, Servico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Servico", name = "naturezaServico", scope = Servico.class)
    public JAXBElement<java.lang.String> createServicoNaturezaServico(java.lang.String value) {
        return new JAXBElement<>(_ServicoNaturezaServico_QNAME, java.lang.String.class, Servico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Servico", name = "ativo", scope = Servico.class)
    public JAXBElement<java.lang.Short> createServicoAtivo(java.lang.Short value) {
        return new JAXBElement<>(_ServicoAtivo_QNAME, java.lang.Short.class, Servico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Servico", name = "observacao", scope = Servico.class)
    public JAXBElement<java.lang.String> createServicoObservacao(java.lang.String value) {
        return new JAXBElement<>(_ServicoObservacao_QNAME, java.lang.String.class, Servico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Servico", name = "prioridade", scope = Servico.class)
    public JAXBElement<java.lang.String> createServicoPrioridade(java.lang.String value) {
        return new JAXBElement<>(_ServicoPrioridade_QNAME, java.lang.String.class, Servico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "email", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoEmail(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoEmail_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "responsavel", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoResponsavel_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "numero", scope = Estabelecimento.class)
    public JAXBElement<java.lang.Integer> createEstabelecimentoNumero(java.lang.Integer value) {
        return new JAXBElement<>(_EstabelecimentoNumero_QNAME, java.lang.Integer.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "logradouro", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoLogradouro(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoLogradouro_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "complemento", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoComplemento(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoComplemento_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "bairro", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoBairro(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoBairro_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "cidade", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoCidade(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoCidade_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "uf", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoUf(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoUf_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "cep", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoCep(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoCep_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "telefone", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoTelefone(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoTelefone_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "fax", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoFax(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoFax_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "ativo", scope = Estabelecimento.class)
    public JAXBElement<java.lang.Short> createEstabelecimentoAtivo(java.lang.Short value) {
        return new JAXBElement<>(_EstabelecimentoAtivo_QNAME, java.lang.Short.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "responsavel2", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoResponsavel2_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "responsavel3", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoResponsavel3_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "cargoResponsavel", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoCargoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoCargoResponsavel_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "cargoResponsavel2", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoCargoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoCargoResponsavel2_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "cargoResponsavel3", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoCargoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoCargoResponsavel3_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "telefoneResponsavel", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoTelefoneResponsavel(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoTelefoneResponsavel_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "telefoneResponsavel2", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoTelefoneResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoTelefoneResponsavel2_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Estabelecimento", name = "telefoneResponsavel3", scope = Estabelecimento.class)
    public JAXBElement<java.lang.String> createEstabelecimentoTelefoneResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_EstabelecimentoTelefoneResponsavel3_QNAME, java.lang.String.class, Estabelecimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "nomeAbreviado", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoNomeAbreviado(java.lang.String value) {
        return new JAXBElement<>(_OrgaoNomeAbreviado_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cnpj", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCnpj(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCnpj_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "email", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoEmail(java.lang.String value) {
        return new JAXBElement<>(_OrgaoEmail_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "responsavel", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_OrgaoResponsavel_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "numero", scope = Orgao.class)
    public JAXBElement<java.lang.Integer> createOrgaoNumero(java.lang.Integer value) {
        return new JAXBElement<>(_OrgaoNumero_QNAME, java.lang.Integer.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "logradouro", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoLogradouro(java.lang.String value) {
        return new JAXBElement<>(_OrgaoLogradouro_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "complemento", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoComplemento(java.lang.String value) {
        return new JAXBElement<>(_OrgaoComplemento_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "bairro", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoBairro(java.lang.String value) {
        return new JAXBElement<>(_OrgaoBairro_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cidade", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCidade(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCidade_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "uf", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoUf(java.lang.String value) {
        return new JAXBElement<>(_OrgaoUf_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cep", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCep(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCep_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "telefone", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoTelefone(java.lang.String value) {
        return new JAXBElement<>(_OrgaoTelefone_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "fax", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoFax(java.lang.String value) {
        return new JAXBElement<>(_OrgaoFax_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "diaCorte", scope = Orgao.class)
    public JAXBElement<java.lang.Integer> createOrgaoDiaCorte(java.lang.Integer value) {
        return new JAXBElement<>(_OrgaoDiaCorte_QNAME, java.lang.Integer.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "diaRepasse", scope = Orgao.class)
    public JAXBElement<java.lang.Integer> createOrgaoDiaRepasse(java.lang.Integer value) {
        return new JAXBElement<>(_OrgaoDiaRepasse_QNAME, java.lang.Integer.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "ativo", scope = Orgao.class)
    public JAXBElement<java.lang.Short> createOrgaoAtivo(java.lang.Short value) {
        return new JAXBElement<>(_OrgaoAtivo_QNAME, java.lang.Short.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "responsavel2", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_OrgaoResponsavel2_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "responsavel3", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_OrgaoResponsavel3_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cargoResponsavel", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCargoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCargoResponsavel_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cargoResponsavel2", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCargoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCargoResponsavel2_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "cargoResponsavel3", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoCargoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_OrgaoCargoResponsavel3_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "telefoneResponsavel", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoTelefoneResponsavel(java.lang.String value) {
        return new JAXBElement<>(_OrgaoTelefoneResponsavel_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "telefoneResponsavel2", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoTelefoneResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_OrgaoTelefoneResponsavel2_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "telefoneResponsavel3", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoTelefoneResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_OrgaoTelefoneResponsavel3_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "ipAcesso", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoIpAcesso(java.lang.String value) {
        return new JAXBElement<>(_OrgaoIpAcesso_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Orgao", name = "ddnsAcesso", scope = Orgao.class)
    public JAXBElement<java.lang.String> createOrgaoDdnsAcesso(java.lang.String value) {
        return new JAXBElement<>(_OrgaoDdnsAcesso_QNAME, java.lang.String.class, Orgao.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Convenio", name = "verbaConvenioRef", scope = Convenio.class)
    public JAXBElement<java.lang.String> createConvenioVerbaConvenioRef(java.lang.String value) {
        return new JAXBElement<>(_ConvenioVerbaConvenioRef_QNAME, java.lang.String.class, Convenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Convenio", name = "verbaConvenioFerias", scope = Convenio.class)
    public JAXBElement<java.lang.String> createConvenioVerbaConvenioFerias(java.lang.String value) {
        return new JAXBElement<>(_ConvenioVerbaConvenioFerias_QNAME, java.lang.String.class, Convenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Convenio", name = "nomeOrgao", scope = Convenio.class)
    public JAXBElement<java.lang.String> createConvenioNomeOrgao(java.lang.String value) {
        return new JAXBElement<>(_ConvenioNomeOrgao_QNAME, java.lang.String.class, Convenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Convenio", name = "nomeConsignataria", scope = Convenio.class)
    public JAXBElement<java.lang.String> createConvenioNomeConsignataria(java.lang.String value) {
        return new JAXBElement<>(_ConvenioNomeConsignataria_QNAME, java.lang.String.class, Convenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Convenio", name = "descricaoServico", scope = Convenio.class)
    public JAXBElement<java.lang.String> createConvenioDescricaoServico(java.lang.String value) {
        return new JAXBElement<>(_ConvenioDescricaoServico_QNAME, java.lang.String.class, Convenio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "email", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaEmail(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaEmail_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "cnpj", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaCnpj(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaCnpj_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "responsavel", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaResponsavel_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "logradouro", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaLogradouro(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaLogradouro_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "numero", scope = Consignataria.class)
    public JAXBElement<java.lang.Integer> createConsignatariaNumero(java.lang.Integer value) {
        return new JAXBElement<>(_ConsignatariaNumero_QNAME, java.lang.Integer.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "complemento", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaComplemento(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaComplemento_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "bairro", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaBairro(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaBairro_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "cidade", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaCidade(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaCidade_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "uf", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaUf(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaUf_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "fax", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaFax(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaFax_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "numeroBanco", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNumeroBanco(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNumeroBanco_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "numeroConta", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNumeroConta(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNumeroConta_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "numeroAgencia", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNumeroAgencia(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNumeroAgencia_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "digitoConta", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaDigitoConta(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaDigitoConta_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "ativo", scope = Consignataria.class)
    public JAXBElement<java.lang.Short> createConsignatariaAtivo(java.lang.Short value) {
        return new JAXBElement<>(_ConsignatariaAtivo_QNAME, java.lang.Short.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "responsavel2", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaResponsavel2_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "responsavel3", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaResponsavel3_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "cargoResponsavel", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaCargoResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaCargoResponsavel_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "cargoResponsavel2", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaCargoResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaCargoResponsavel2_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "cargoResponsavel3", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaCargoResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaCargoResponsavel3_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "telefoneResponsavel", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaTelefoneResponsavel(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaTelefoneResponsavel_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "telefoneResponsavel2", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaTelefoneResponsavel2(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaTelefoneResponsavel2_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "telefoneResponsavel3", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaTelefoneResponsavel3(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaTelefoneResponsavel3_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "textoContato", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaTextoContato(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaTextoContato_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "contato", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaContato(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaContato_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "telefoneContato", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaTelefoneContato(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaTelefoneContato_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "endereco2", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaEndereco2(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaEndereco2_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "nomeAbreviado", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNomeAbreviado(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNomeAbreviado_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "dataExpiracao", scope = Consignataria.class)
    public JAXBElement<XMLGregorianCalendar> createConsignatariaDataExpiracao(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ConsignatariaDataExpiracao_QNAME, XMLGregorianCalendar.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "numeroContrato", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNumeroContrato(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNumeroContrato_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "ipAcesso", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaIpAcesso(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaIpAcesso_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "ddnsAcesso", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaDdnsAcesso(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaDdnsAcesso_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "exigeEnderecoAcesso", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaExigeEnderecoAcesso(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaExigeEnderecoAcesso_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "unidadeOrganizacional", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaUnidadeOrganizacional(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaUnidadeOrganizacional_QNAME, java.lang.String.class, Consignataria.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     */
    @XmlElementDecl(namespace = "Consignataria", name = "naturezaCodigo", scope = Consignataria.class)
    public JAXBElement<java.lang.String> createConsignatariaNaturezaCodigo(java.lang.String value) {
        return new JAXBElement<>(_ConsignatariaNaturezaCodigo_QNAME, java.lang.String.class, Consignataria.class, value);
    }

}
