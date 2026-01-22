//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="convenio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="titulacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="primeiroNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeMeio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ultimoNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomePai" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeMae" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeConjuge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="cidadeNascimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ufNascimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sexo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estadoCivil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nacionalidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="identidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataIdentidade" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="ufIdentidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="emissorIdentidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numCarteiraTrabalho" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numPis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="logradouro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="uf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dddTelefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dddCelular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="celular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="subOrgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="situacao" type="{SituacaoServidor}SituacaoServidor" minOccurs="0"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="municipioLotacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabilizado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="dataFimEngajamento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataLimitePermanencia" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="vinculoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="clt" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataContracheque" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="bancoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="banco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="conta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bancoAlternativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agenciaAlternativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="contaAlternativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="praca" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="salario" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="proventos" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="descontosCompulsorios" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="descontosFacultativos" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="baseCalculo" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="associado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="outrosDescontos" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="matriculaInstitucional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="padraoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="unidadeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cliente",
    "convenio",
    "usuario",
    "senha",
    "nome",
    "titulacao",
    "primeiroNome",
    "nomeMeio",
    "ultimoNome",
    "cpf",
    "nomePai",
    "nomeMae",
    "nomeConjuge",
    "dataNascimento",
    "cidadeNascimento",
    "ufNascimento",
    "sexo",
    "estadoCivil",
    "nacionalidade",
    "identidade",
    "dataIdentidade",
    "ufIdentidade",
    "emissorIdentidade",
    "numCarteiraTrabalho",
    "numPis",
    "logradouro",
    "nro",
    "complemento",
    "bairro",
    "cidade",
    "uf",
    "cep",
    "dddTelefone",
    "telefone",
    "dddCelular",
    "celular",
    "email",
    "matricula",
    "estabelecimentoCodigo",
    "orgaoCodigo",
    "subOrgaoCodigo",
    "situacao",
    "categoria",
    "cargoCodigo",
    "municipioLotacao",
    "estabilizado",
    "dataFimEngajamento",
    "dataLimitePermanencia",
    "vinculoCodigo",
    "clt",
    "dataAdmissao",
    "dataContracheque",
    "prazoServidor",
    "bancoCodigo",
    "banco",
    "agencia",
    "conta",
    "bancoAlternativo",
    "agenciaAlternativa",
    "contaAlternativa",
    "observacao",
    "praca",
    "salario",
    "proventos",
    "descontosCompulsorios",
    "descontosFacultativos",
    "baseCalculo",
    "associado",
    "outrosDescontos",
    "matriculaInstitucional",
    "padraoCodigo",
    "unidadeCodigo"
})
@XmlRootElement(name = "cadastrarServidor", namespace = "HostaHostService-v7_0")
public class CadastrarServidor {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v7_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v7_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "nome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nome;
    @XmlElementRef(name = "titulacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> titulacao;
    @XmlElementRef(name = "primeiroNome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> primeiroNome;
    @XmlElementRef(name = "nomeMeio", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeMeio;
    @XmlElementRef(name = "ultimoNome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ultimoNome;
    @XmlElementRef(name = "cpf", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "nomePai", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomePai;
    @XmlElementRef(name = "nomeMae", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeMae;
    @XmlElementRef(name = "nomeConjuge", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeConjuge;
    @XmlElementRef(name = "dataNascimento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataNascimento;
    @XmlElementRef(name = "cidadeNascimento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidadeNascimento;
    @XmlElementRef(name = "ufNascimento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ufNascimento;
    @XmlElementRef(name = "sexo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> sexo;
    @XmlElementRef(name = "estadoCivil", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estadoCivil;
    @XmlElementRef(name = "nacionalidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nacionalidade;
    @XmlElementRef(name = "identidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> identidade;
    @XmlElementRef(name = "dataIdentidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataIdentidade;
    @XmlElementRef(name = "ufIdentidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ufIdentidade;
    @XmlElementRef(name = "emissorIdentidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> emissorIdentidade;
    @XmlElementRef(name = "numCarteiraTrabalho", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numCarteiraTrabalho;
    @XmlElementRef(name = "numPis", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numPis;
    @XmlElementRef(name = "logradouro", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> logradouro;
    @XmlElementRef(name = "nro", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nro;
    @XmlElementRef(name = "complemento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> complemento;
    @XmlElementRef(name = "bairro", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bairro;
    @XmlElementRef(name = "cidade", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidade;
    @XmlElementRef(name = "uf", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> uf;
    @XmlElementRef(name = "cep", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cep;
    @XmlElementRef(name = "dddTelefone", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> dddTelefone;
    @XmlElementRef(name = "telefone", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "dddCelular", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> dddCelular;
    @XmlElementRef(name = "celular", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> celular;
    @XmlElementRef(name = "email", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "matricula", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matricula;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estabelecimentoCodigo;
    @XmlElementRef(name = "orgaoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> orgaoCodigo;
    @XmlElementRef(name = "subOrgaoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> subOrgaoCodigo;
    @XmlElementRef(name = "situacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoServidor> situacao;
    @XmlElementRef(name = "categoria", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> categoria;
    @XmlElementRef(name = "cargoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoCodigo;
    @XmlElementRef(name = "municipioLotacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> municipioLotacao;
    @XmlElementRef(name = "estabilizado", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> estabilizado;
    @XmlElementRef(name = "dataFimEngajamento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataFimEngajamento;
    @XmlElementRef(name = "dataLimitePermanencia", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataLimitePermanencia;
    @XmlElementRef(name = "vinculoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> vinculoCodigo;
    @XmlElementRef(name = "clt", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> clt;
    @XmlElementRef(name = "dataAdmissao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataAdmissao;
    @XmlElementRef(name = "dataContracheque", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataContracheque;
    @XmlElementRef(name = "prazoServidor", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> prazoServidor;
    @XmlElementRef(name = "bancoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bancoCodigo;
    @XmlElementRef(name = "banco", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> banco;
    @XmlElementRef(name = "agencia", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agencia;
    @XmlElementRef(name = "conta", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> conta;
    @XmlElementRef(name = "bancoAlternativo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bancoAlternativo;
    @XmlElementRef(name = "agenciaAlternativa", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agenciaAlternativa;
    @XmlElementRef(name = "contaAlternativa", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> contaAlternativa;
    @XmlElementRef(name = "observacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> observacao;
    @XmlElementRef(name = "praca", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> praca;
    @XmlElementRef(name = "salario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salario;
    @XmlElementRef(name = "proventos", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> proventos;
    @XmlElementRef(name = "descontosCompulsorios", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> descontosCompulsorios;
    @XmlElementRef(name = "descontosFacultativos", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> descontosFacultativos;
    @XmlElementRef(name = "baseCalculo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> baseCalculo;
    @XmlElementRef(name = "associado", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> associado;
    @XmlElementRef(name = "outrosDescontos", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> outrosDescontos;
    @XmlElementRef(name = "matriculaInstitucional", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matriculaInstitucional;
    @XmlElementRef(name = "padraoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> padraoCodigo;
    @XmlElementRef(name = "unidadeCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> unidadeCodigo;

    /**
     * Obtém o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCliente(JAXBElement<java.lang.String> value) {
        this.cliente = value;
    }

    /**
     * Obtém o valor da propriedade convenio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConvenio() {
        return convenio;
    }

    /**
     * Define o valor da propriedade convenio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConvenio(JAXBElement<java.lang.String> value) {
        this.convenio = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setUsuario(java.lang.String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSenha(java.lang.String value) {
        this.senha = value;
    }

    /**
     * Obtém o valor da propriedade nome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNome() {
        return nome;
    }

    /**
     * Define o valor da propriedade nome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNome(JAXBElement<java.lang.String> value) {
        this.nome = value;
    }

    /**
     * Obtém o valor da propriedade titulacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTitulacao() {
        return titulacao;
    }

    /**
     * Define o valor da propriedade titulacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTitulacao(JAXBElement<java.lang.String> value) {
        this.titulacao = value;
    }

    /**
     * Obtém o valor da propriedade primeiroNome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPrimeiroNome() {
        return primeiroNome;
    }

    /**
     * Define o valor da propriedade primeiroNome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPrimeiroNome(JAXBElement<java.lang.String> value) {
        this.primeiroNome = value;
    }

    /**
     * Obtém o valor da propriedade nomeMeio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeMeio() {
        return nomeMeio;
    }

    /**
     * Define o valor da propriedade nomeMeio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeMeio(JAXBElement<java.lang.String> value) {
        this.nomeMeio = value;
    }

    /**
     * Obtém o valor da propriedade ultimoNome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUltimoNome() {
        return ultimoNome;
    }

    /**
     * Define o valor da propriedade ultimoNome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUltimoNome(JAXBElement<java.lang.String> value) {
        this.ultimoNome = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCpf(JAXBElement<java.lang.String> value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade nomePai.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomePai() {
        return nomePai;
    }

    /**
     * Define o valor da propriedade nomePai.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomePai(JAXBElement<java.lang.String> value) {
        this.nomePai = value;
    }

    /**
     * Obtém o valor da propriedade nomeMae.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeMae() {
        return nomeMae;
    }

    /**
     * Define o valor da propriedade nomeMae.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeMae(JAXBElement<java.lang.String> value) {
        this.nomeMae = value;
    }

    /**
     * Obtém o valor da propriedade nomeConjuge.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeConjuge() {
        return nomeConjuge;
    }

    /**
     * Define o valor da propriedade nomeConjuge.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeConjuge(JAXBElement<java.lang.String> value) {
        this.nomeConjuge = value;
    }

    /**
     * Obtém o valor da propriedade dataNascimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define o valor da propriedade dataNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataNascimento(JAXBElement<XMLGregorianCalendar> value) {
        this.dataNascimento = value;
    }

    /**
     * Obtém o valor da propriedade cidadeNascimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCidadeNascimento() {
        return cidadeNascimento;
    }

    /**
     * Define o valor da propriedade cidadeNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCidadeNascimento(JAXBElement<java.lang.String> value) {
        this.cidadeNascimento = value;
    }

    /**
     * Obtém o valor da propriedade ufNascimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUfNascimento() {
        return ufNascimento;
    }

    /**
     * Define o valor da propriedade ufNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUfNascimento(JAXBElement<java.lang.String> value) {
        this.ufNascimento = value;
    }

    /**
     * Obtém o valor da propriedade sexo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSexo() {
        return sexo;
    }

    /**
     * Define o valor da propriedade sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSexo(JAXBElement<java.lang.String> value) {
        this.sexo = value;
    }

    /**
     * Obtém o valor da propriedade estadoCivil.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * Define o valor da propriedade estadoCivil.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstadoCivil(JAXBElement<java.lang.String> value) {
        this.estadoCivil = value;
    }

    /**
     * Obtém o valor da propriedade nacionalidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNacionalidade() {
        return nacionalidade;
    }

    /**
     * Define o valor da propriedade nacionalidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNacionalidade(JAXBElement<java.lang.String> value) {
        this.nacionalidade = value;
    }

    /**
     * Obtém o valor da propriedade identidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIdentidade() {
        return identidade;
    }

    /**
     * Define o valor da propriedade identidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIdentidade(JAXBElement<java.lang.String> value) {
        this.identidade = value;
    }

    /**
     * Obtém o valor da propriedade dataIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataIdentidade() {
        return dataIdentidade;
    }

    /**
     * Define o valor da propriedade dataIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataIdentidade(JAXBElement<XMLGregorianCalendar> value) {
        this.dataIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade ufIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUfIdentidade() {
        return ufIdentidade;
    }

    /**
     * Define o valor da propriedade ufIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUfIdentidade(JAXBElement<java.lang.String> value) {
        this.ufIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade emissorIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEmissorIdentidade() {
        return emissorIdentidade;
    }

    /**
     * Define o valor da propriedade emissorIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEmissorIdentidade(JAXBElement<java.lang.String> value) {
        this.emissorIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade numCarteiraTrabalho.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumCarteiraTrabalho() {
        return numCarteiraTrabalho;
    }

    /**
     * Define o valor da propriedade numCarteiraTrabalho.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumCarteiraTrabalho(JAXBElement<java.lang.String> value) {
        this.numCarteiraTrabalho = value;
    }

    /**
     * Obtém o valor da propriedade numPis.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumPis() {
        return numPis;
    }

    /**
     * Define o valor da propriedade numPis.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumPis(JAXBElement<java.lang.String> value) {
        this.numPis = value;
    }

    /**
     * Obtém o valor da propriedade logradouro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getLogradouro() {
        return logradouro;
    }

    /**
     * Define o valor da propriedade logradouro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setLogradouro(JAXBElement<java.lang.String> value) {
        this.logradouro = value;
    }

    /**
     * Obtém o valor da propriedade nro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNro() {
        return nro;
    }

    /**
     * Define o valor da propriedade nro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNro(JAXBElement<java.lang.String> value) {
        this.nro = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setComplemento(JAXBElement<java.lang.String> value) {
        this.complemento = value;
    }

    /**
     * Obtém o valor da propriedade bairro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBairro() {
        return bairro;
    }

    /**
     * Define o valor da propriedade bairro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBairro(JAXBElement<java.lang.String> value) {
        this.bairro = value;
    }

    /**
     * Obtém o valor da propriedade cidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCidade() {
        return cidade;
    }

    /**
     * Define o valor da propriedade cidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCidade(JAXBElement<java.lang.String> value) {
        this.cidade = value;
    }

    /**
     * Obtém o valor da propriedade uf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUf() {
        return uf;
    }

    /**
     * Define o valor da propriedade uf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUf(JAXBElement<java.lang.String> value) {
        this.uf = value;
    }

    /**
     * Obtém o valor da propriedade cep.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCep(JAXBElement<java.lang.String> value) {
        this.cep = value;
    }

    /**
     * Obtém o valor da propriedade dddTelefone.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDddTelefone() {
        return dddTelefone;
    }

    /**
     * Define o valor da propriedade dddTelefone.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDddTelefone(JAXBElement<java.lang.String> value) {
        this.dddTelefone = value;
    }

    /**
     * Obtém o valor da propriedade telefone.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefone() {
        return telefone;
    }

    /**
     * Define o valor da propriedade telefone.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefone(JAXBElement<java.lang.String> value) {
        this.telefone = value;
    }

    /**
     * Obtém o valor da propriedade dddCelular.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDddCelular() {
        return dddCelular;
    }

    /**
     * Define o valor da propriedade dddCelular.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDddCelular(JAXBElement<java.lang.String> value) {
        this.dddCelular = value;
    }

    /**
     * Obtém o valor da propriedade celular.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCelular() {
        return celular;
    }

    /**
     * Define o valor da propriedade celular.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCelular(JAXBElement<java.lang.String> value) {
        this.celular = value;
    }

    /**
     * Obtém o valor da propriedade email.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEmail() {
        return email;
    }

    /**
     * Define o valor da propriedade email.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<java.lang.String> value) {
        this.email = value;
    }

    /**
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMatricula(JAXBElement<java.lang.String> value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<java.lang.String> value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<java.lang.String> value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade subOrgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSubOrgaoCodigo() {
        return subOrgaoCodigo;
    }

    /**
     * Define o valor da propriedade subOrgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSubOrgaoCodigo(JAXBElement<java.lang.String> value) {
        this.subOrgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public JAXBElement<SituacaoServidor> getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public void setSituacao(JAXBElement<SituacaoServidor> value) {
        this.situacao = value;
    }

    /**
     * Obtém o valor da propriedade categoria.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCategoria() {
        return categoria;
    }

    /**
     * Define o valor da propriedade categoria.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCategoria(JAXBElement<java.lang.String> value) {
        this.categoria = value;
    }

    /**
     * Obtém o valor da propriedade cargoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoCodigo() {
        return cargoCodigo;
    }

    /**
     * Define o valor da propriedade cargoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoCodigo(JAXBElement<java.lang.String> value) {
        this.cargoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade municipioLotacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMunicipioLotacao() {
        return municipioLotacao;
    }

    /**
     * Define o valor da propriedade municipioLotacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMunicipioLotacao(JAXBElement<java.lang.String> value) {
        this.municipioLotacao = value;
    }

    /**
     * Obtém o valor da propriedade estabilizado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getEstabilizado() {
        return estabilizado;
    }

    /**
     * Define o valor da propriedade estabilizado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setEstabilizado(JAXBElement<java.lang.Boolean> value) {
        this.estabilizado = value;
    }

    /**
     * Obtém o valor da propriedade dataFimEngajamento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataFimEngajamento() {
        return dataFimEngajamento;
    }

    /**
     * Define o valor da propriedade dataFimEngajamento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataFimEngajamento(JAXBElement<XMLGregorianCalendar> value) {
        this.dataFimEngajamento = value;
    }

    /**
     * Obtém o valor da propriedade dataLimitePermanencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataLimitePermanencia() {
        return dataLimitePermanencia;
    }

    /**
     * Define o valor da propriedade dataLimitePermanencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataLimitePermanencia(JAXBElement<XMLGregorianCalendar> value) {
        this.dataLimitePermanencia = value;
    }

    /**
     * Obtém o valor da propriedade vinculoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getVinculoCodigo() {
        return vinculoCodigo;
    }

    /**
     * Define o valor da propriedade vinculoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setVinculoCodigo(JAXBElement<java.lang.String> value) {
        this.vinculoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade clt.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getClt() {
        return clt;
    }

    /**
     * Define o valor da propriedade clt.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setClt(JAXBElement<java.lang.Boolean> value) {
        this.clt = value;
    }

    /**
     * Obtém o valor da propriedade dataAdmissao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataAdmissao() {
        return dataAdmissao;
    }

    /**
     * Define o valor da propriedade dataAdmissao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataAdmissao(JAXBElement<XMLGregorianCalendar> value) {
        this.dataAdmissao = value;
    }

    /**
     * Obtém o valor da propriedade dataContracheque.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataContracheque() {
        return dataContracheque;
    }

    /**
     * Define o valor da propriedade dataContracheque.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataContracheque(JAXBElement<XMLGregorianCalendar> value) {
        this.dataContracheque = value;
    }

    /**
     * Obtém o valor da propriedade prazoServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getPrazoServidor() {
        return prazoServidor;
    }

    /**
     * Define o valor da propriedade prazoServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setPrazoServidor(JAXBElement<java.lang.Integer> value) {
        this.prazoServidor = value;
    }

    /**
     * Obtém o valor da propriedade bancoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBancoCodigo() {
        return bancoCodigo;
    }

    /**
     * Define o valor da propriedade bancoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBancoCodigo(JAXBElement<java.lang.String> value) {
        this.bancoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade banco.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBanco() {
        return banco;
    }

    /**
     * Define o valor da propriedade banco.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBanco(JAXBElement<java.lang.String> value) {
        this.banco = value;
    }

    /**
     * Obtém o valor da propriedade agencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAgencia() {
        return agencia;
    }

    /**
     * Define o valor da propriedade agencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAgencia(JAXBElement<java.lang.String> value) {
        this.agencia = value;
    }

    /**
     * Obtém o valor da propriedade conta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConta() {
        return conta;
    }

    /**
     * Define o valor da propriedade conta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConta(JAXBElement<java.lang.String> value) {
        this.conta = value;
    }

    /**
     * Obtém o valor da propriedade bancoAlternativo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBancoAlternativo() {
        return bancoAlternativo;
    }

    /**
     * Define o valor da propriedade bancoAlternativo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBancoAlternativo(JAXBElement<java.lang.String> value) {
        this.bancoAlternativo = value;
    }

    /**
     * Obtém o valor da propriedade agenciaAlternativa.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAgenciaAlternativa() {
        return agenciaAlternativa;
    }

    /**
     * Define o valor da propriedade agenciaAlternativa.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAgenciaAlternativa(JAXBElement<java.lang.String> value) {
        this.agenciaAlternativa = value;
    }

    /**
     * Obtém o valor da propriedade contaAlternativa.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getContaAlternativa() {
        return contaAlternativa;
    }

    /**
     * Define o valor da propriedade contaAlternativa.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setContaAlternativa(JAXBElement<java.lang.String> value) {
        this.contaAlternativa = value;
    }

    /**
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setObservacao(JAXBElement<java.lang.String> value) {
        this.observacao = value;
    }

    /**
     * Obtém o valor da propriedade praca.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPraca() {
        return praca;
    }

    /**
     * Define o valor da propriedade praca.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPraca(JAXBElement<java.lang.String> value) {
        this.praca = value;
    }

    /**
     * Obtém o valor da propriedade salario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getSalario() {
        return salario;
    }

    /**
     * Define o valor da propriedade salario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setSalario(JAXBElement<java.lang.Double> value) {
        this.salario = value;
    }

    /**
     * Obtém o valor da propriedade proventos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getProventos() {
        return proventos;
    }

    /**
     * Define o valor da propriedade proventos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setProventos(JAXBElement<java.lang.Double> value) {
        this.proventos = value;
    }

    /**
     * Obtém o valor da propriedade descontosCompulsorios.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getDescontosCompulsorios() {
        return descontosCompulsorios;
    }

    /**
     * Define o valor da propriedade descontosCompulsorios.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setDescontosCompulsorios(JAXBElement<java.lang.Double> value) {
        this.descontosCompulsorios = value;
    }

    /**
     * Obtém o valor da propriedade descontosFacultativos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getDescontosFacultativos() {
        return descontosFacultativos;
    }

    /**
     * Define o valor da propriedade descontosFacultativos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setDescontosFacultativos(JAXBElement<java.lang.Double> value) {
        this.descontosFacultativos = value;
    }

    /**
     * Obtém o valor da propriedade baseCalculo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getBaseCalculo() {
        return baseCalculo;
    }

    /**
     * Define o valor da propriedade baseCalculo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setBaseCalculo(JAXBElement<java.lang.Double> value) {
        this.baseCalculo = value;
    }

    /**
     * Obtém o valor da propriedade associado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getAssociado() {
        return associado;
    }

    /**
     * Define o valor da propriedade associado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setAssociado(JAXBElement<java.lang.Boolean> value) {
        this.associado = value;
    }

    /**
     * Obtém o valor da propriedade outrosDescontos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getOutrosDescontos() {
        return outrosDescontos;
    }

    /**
     * Define o valor da propriedade outrosDescontos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setOutrosDescontos(JAXBElement<java.lang.Double> value) {
        this.outrosDescontos = value;
    }

    /**
     * Obtém o valor da propriedade matriculaInstitucional.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMatriculaInstitucional() {
        return matriculaInstitucional;
    }

    /**
     * Define o valor da propriedade matriculaInstitucional.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMatriculaInstitucional(JAXBElement<java.lang.String> value) {
        this.matriculaInstitucional = value;
    }

    /**
     * Obtém o valor da propriedade padraoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPadraoCodigo() {
        return padraoCodigo;
    }

    /**
     * Define o valor da propriedade padraoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPadraoCodigo(JAXBElement<java.lang.String> value) {
        this.padraoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade unidadeCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUnidadeCodigo() {
        return unidadeCodigo;
    }

    /**
     * Define o valor da propriedade unidadeCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUnidadeCodigo(JAXBElement<java.lang.String> value) {
        this.unidadeCodigo = value;
    }

}
