package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ServidoresElementMap {

	@FindBy(id = "RSE_MATRICULA")
	public WebElement matricula;

	@FindBy(id = "SER_CPF")
	public WebElement cpf;

	@FindBy(id = "btnPesquisar")
	public WebElement botaoPesquisar;

	@FindBy(id = "edtServidor_nome")
	public WebElement servidorNome;

	@FindBy(id = "edtServidor_cpf")
	public WebElement servidorCPF;

	@FindBy(id = "edtRegistroServidor_matricula")
	public WebElement servidorMatricula;

	@FindBy(css = "fieldset:nth-child(1) > .legend > span")
	public WebElement textoInformacao;

	@FindBy(linkText = "Concluir")
	public WebElement botaoConcluir;

	@FindBy(linkText = "Exibir ocorrências deste servidor")
	public WebElement acaoExibirOcorrencia;

	@FindBy(linkText = "Bloquear os serviços deste servidor")
	public WebElement acaoBloquearServicos;

	@FindBy(linkText = "Consultar os serviços deste servidor")
	public WebElement acaoConsultarServicos;

	@FindBy(linkText = "Bloquear os serviços deste servidor por natureza")
	public WebElement acaoBloquearServicosPorNatureza;

	@FindBy(linkText = "Consultar os serviços deste servidor por natureza")
	public WebElement acaoConsultarServicosPorNatureza;

	@FindBy(linkText = "Bloquear as verbas deste servidor")
	public WebElement acaoBloquearVerbas;

	@FindBy(linkText = "Consultar as verbas deste servidor")
	public WebElement acaoConsultarVerbas;

	@FindBy(linkText = "Transferir valores entre margens deste servidor")
	public WebElement acaoTransferirValoresEntreMargens;

	@FindBy(linkText = "Consultar contracheques deste servidor")
	public WebElement acaoConsultarContracheques;

	@FindBy(linkText = "Solicitar o saldo devedor a todas consignatárias.")
	public WebElement acaoSolicitarSaldoDevedor;

	@FindBy(id = "svc_B3858080808080808080808088887ED6")
	public WebElement servicoEmprestimo;

	@FindBy(id = "nse_1")
	public WebElement servicoPorNaturezaNatureza;

	@FindBy(id = "cnv_751F8080808080808080808080809Z85")
	public WebElement convenioEmprestimo;

	@FindBy(id = "TRANSF_PARCIAL")
	public WebElement transParcial;

	@FindBy(id = "TERMO_ACEITE")
	public WebElement marcarTermoTransferencia;

	@FindBy(id = "VALOR_TRANSF")
	public WebElement valorTrans;

	@FindBy(id = "acoes")
	public WebElement botaoAcoes;

	@FindBy(id = "edtServidor_datanascimento")
	public WebElement servidorDataNascimento;

	@FindBy(linkText = "Gerar nova senha")
	public WebElement botaoGerarSenha;

	@FindBy(id = "idMsgSuccessSession")
	public WebElement txtMensagemSucesso;

	@FindBy(className = "novaSenha")
	public WebElement novaSenha;

	@FindBy(css = ".mb-0")
	public WebElement txtInformacao;

	@FindBy(id = "btnEnvia")
	public WebElement botaoPesquisarMargem;

	@FindBy(linkText = "Editar endereços deste servidor")
	public WebElement acaoEditarEnderecos;

	@FindBy(linkText = "Cadastrar dispensa de validação de digital deste servidor")
	public WebElement acaoCadastrarDispensaValidacaoDigital;

	@FindBy(id = "cadastrarServidor_titulacao")
	public WebElement cadastrarServidorTitulo;

	@FindBy(id = "cadastrarServidor_primeiroNome")
	public WebElement cadastrarServidorNome;

	@FindBy(id = "cadastrarServidor_nomemeio")
	public WebElement cadastrarServidorNomeDoMeio;

	@FindBy(id = "cadastrarServidor_ultimonome")
	public WebElement cadastrarServidorUltimoNome;

	@FindBy(id = "cadastrarServidor_nome")
	public WebElement cadastrarServidorNomeCompleto;

	@FindBy(id = "cadastrarServidor_nomepai")
	public WebElement cadastrarServidorNomePai;

	@FindBy(id = "cadastrarServidor_nomemae")
	public WebElement cadastrarServidorNomeMae;

	@FindBy(id = "cadastrarServidor_datanascimento")
	public WebElement cadastrarServidorDataNascimento;

	@FindBy(id = "cadastrarServidor_nacionalidade")
	public WebElement cadastrarServidorNacionalidade;

	@FindBy(id = "cadastrarServidor_estadocivil")
	public WebElement cadastrarServidorEstadoCivil;

	@FindBy(id = "sexoMaculino")
	public WebElement cadastrarServidorSexo;

	@FindBy(id = "cadastrarServidor_cpf")
	public WebElement cadastrarCpfServidor;

	@FindBy(id = "cadastrarServidor_logradouro")
	public WebElement cadastrarServidorLogradouro;

	@FindBy(id = "cadastrarServidor_nro")
	public WebElement cadastrarServidorNumero;

	@FindBy(id = "cadastrarServidor_bairro")
	public WebElement cadastrarServidorBairro;

	@FindBy(id = "cadastrarServidor_cidade")
	public WebElement cadastrarServidorCidade;

	@FindBy(name = "cadastrarServidor_uf")
	public WebElement cadastrarServidorUF;

	@FindBy(id = "cadastrarServidor_cep")
	public WebElement cadastrarServidorCep;

	@FindBy(id = "cadastrarServidor_matricula")
	public WebElement cadastrarServidorMatricula;

	@FindBy(id = "cadastrarServidor_orgao")
	public WebElement cadastrarServidorOrgao;

    @FindBy(id = "TMO_CODIGO")
    public WebElement motivoOperacao;

    @FindBy(id = "ADE_OBS")
    public WebElement observacao;
}
