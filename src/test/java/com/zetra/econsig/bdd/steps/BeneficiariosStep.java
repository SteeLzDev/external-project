package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.ConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BeneficiariosStep {

	@Autowired
	private EconsigHelper econsigHelper;

    private MenuPage menuPage;
	private AcoesUsuarioPage acoesUsuarioPage;
    private BeneficiariosPage beneficiariosPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;
    private ConsignacaoPage consignacaoPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        consultarConsignacaoPage = new ConsultarConsignacaoPage(getWebDriver());
        consignacaoPage = new ConsignacaoPage(getWebDriver());
    }

	@Quando("pesquisar o servidor {string}")
	public void pesquisarServidor(String matricula) throws Throwable {
		log.info("Quando pesquisar o servidor {}", matricula);

		consultarConsignacaoPage.preencherMatricula(matricula);
		consultarConsignacaoPage.clicarPesquisar();
	}

	@Quando("cadastrar novo beneficiario com CPF {string} com papel Servidor")
	public void cadastrarNovoBeneficiarioServidor(String cpf) throws Throwable {
		log.info("Quando cadastrar novo beneficiario com CPF {} com papel Servidor", cpf);

		beneficiariosPage.clicarNovoBeneficiario();
		beneficiariosPage.selecionarTipoBeneficiario("Dependente");
		beneficiariosPage.preencherNome("Mariana de Almeida");
		beneficiariosPage.preencherCPF(cpf);
		beneficiariosPage.preencherRG("12369854");
		beneficiariosPage.marcarSexoFeminino();
		beneficiariosPage.preencherDadosContatos("32", "3265-9856", "32", "96985-6398");
		beneficiariosPage.preencherNomeMae("Maria Antonia de Almeida");
		beneficiariosPage.preencherGrauParentesco("Filho");
		beneficiariosPage.preencherDataNascimento("12/12/1989");
		beneficiariosPage.preencherNacionalidade("Brasileiro");
		beneficiariosPage.preencherEstadoCivil("Solteiro(a)");
	}

	@Quando("cadastrar novo beneficiario com CPF {string} com papel Suporte")
	public void cadastrarNovoBeneficiarioSuporte(String cpf) throws Throwable {
		log.info("Quando cadastrar novo beneficiario com CPF {} com papel Suporte", cpf);

		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarNovoBeneficiario();
		beneficiariosPage.selecionarTipoBeneficiario("Agregado");
		beneficiariosPage.preencherNome("Carla da Silva");
		beneficiariosPage.preencherCPF(cpf);
		beneficiariosPage.preencherRG("12369854");
		beneficiariosPage.marcarSexoFeminino();
		beneficiariosPage.preencherDadosContatos("32", "3265-9856", "32", "96985-6398");
		beneficiariosPage.preencherNomeMae("Emauela da Silva");
		beneficiariosPage.preencherGrauParentesco("Filho");
		beneficiariosPage.preencherDataNascimento("12/12/1989");
		beneficiariosPage.preencherNacionalidade("Brasileiro");
		beneficiariosPage.preencherEstadoCivil("Divorciado(a)");
	}

	@Quando("cadastrar endereco {string}, {string}, {string}, {string}, {string} e {string}")
	public void cadastrarEndereco(String cep, String logradouro, String numero, String bairro, String uf, String cidade)
			throws Throwable {
		log.info("Quando cadastrar endereço {}, {}, {}, {}, {} e {}", cep, logradouro, numero, bairro, uf, cidade);

		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarEditarEnderecoServidor();
		beneficiariosPage.clicarCadastrarNovoEndereco();
		beneficiariosPage.preencherCEP(cep);
		beneficiariosPage.preencherLogradouro(logradouro);
		beneficiariosPage.preencherNumero(numero);
		beneficiariosPage.preencherBairro(bairro);
		beneficiariosPage.preencherUF(uf);
		beneficiariosPage.preencherCidade(cidade);
	}

	@Quando("editar endereco do servidor")
	public void editarEndereco() throws Throwable {
		log.info("Quando editar endereço do servidor");

		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarEditarEnderecoServidor();

		// criar novo endereco
		beneficiariosPage.criarNovoEndereco();

		// editar o endereco
		beneficiariosPage.clicarEditar();
		beneficiariosPage.preencherCEP("30.110-012");
		beneficiariosPage.preencherLogradouro("Avenida do Contorno");
		beneficiariosPage.preencherUF("Minas Gerais");
		beneficiariosPage.preencherCidade("Belo Horizonte");
		beneficiariosPage.preencherBairro("Floresta");
		beneficiariosPage.preencherNumero("39");
		beneficiariosPage.preencherComplemento("33");
	}

	@Quando("excluir endereco do servidor")
	public void excluirEndereco() throws Throwable {
		log.info("Quando excluir endereço do servidor");

		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarEditarEnderecoServidor();

		// excluir endereco
		beneficiariosPage.clicarExcluirEndereco();

		assertEquals("Confirma a exclusão do registro deste endereço?", econsigHelper.getMensagemPopUp(getWebDriver()));

	}

	@Quando("cadastrar novo endereco do servidor")
	public void cadastrarNovoEndereco() throws Throwable {
		log.info("Quando cadastrar novo endereço do servidor");

		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarEditarEnderecoServidor();
		beneficiariosPage.clicarCadastrarNovoEndereco();
		beneficiariosPage.preencherCEP("31.710-400");
		beneficiariosPage.preencherLogradouro("Avenida Portugal");
		beneficiariosPage.preencherUF("Minas Gerais");
		beneficiariosPage.preencherCidade("Belo Horizonte");
		beneficiariosPage.preencherBairro("Itapoã");
		beneficiariosPage.preencherNumero("3589");
		beneficiariosPage.preencherComplemento("A");
	}

	@Quando("editar os dados do beneficiarios")
	public void editarDadosBeneficiarios() throws Throwable {
		log.info("Quando editar os dados do beneficiarios");

		acoesUsuarioPage.clicarOpcoes("21987458523", "6");
		beneficiariosPage.clicarEditar();

		beneficiariosPage.preencherRG("96869854");
		beneficiariosPage.preencherDadosContatos("32", "3265-9856", "32", "96985-6398");
		beneficiariosPage.preencherEstadoCivil("Outros");
	}

	@Quando("excluir beneficiario")
	public void excluirBeneficiario() throws Throwable {
		log.info("Quando excluir beneficiario");

		consignacaoPage.clicarOpcoes("859.141.930-87");
		beneficiariosPage.clicarExcluir();

		assertEquals("Confirma exclusão do beneficiário?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("anexar arquivo beneficiario")
	public void anexarArquivoBeneficiario() throws Throwable {
		log.info("Quando anexar arquivo beneficiario");

        acoesUsuarioPage.clicarOpcoes("Titular", "0");
		beneficiariosPage.anexarArquivo();
	}

	@Quando("editar dados do anexo beneficiario")
	public void editarAnexoArquivoBeneficiario() throws Throwable {
		log.info("Quando editar dados do anexo beneficiário");

        acoesUsuarioPage.clicarOpcoes("Titular", "0");

		// incluir anexo antes
		beneficiariosPage.incluirAnexo();

		// editar anexo
		acoesUsuarioPage.clicarOpcoes("CPF", "0");
		beneficiariosPage.clicarEditar();
		beneficiariosPage.editarTipoDocumento("CPF");
		beneficiariosPage.preencherDescricaoAnexo("Descricao alterado");
		beneficiariosPage.preencherDataValidade("01/01/2022");
	}

	@Quando("excluir anexo beneficiario")
	public void excluirAnexo() throws Throwable {
		log.info("Quando excluir anexo beneficiario");

        acoesUsuarioPage.clicarOpcoes("Titular", "0");

		// incluir anexo antes
		beneficiariosPage.incluirAnexo();

		// excluir anexo
		acoesUsuarioPage.clicarOpcoes("CPF", "0");
		beneficiariosPage.clicarExcluir();

		assertEquals("Confirma exclusão do anexo do beneficiário?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Entao("tentar incluir anexo sem informar campos obrigatorios")
	public void tentarAnexarArquivoBeneficiario() throws Throwable {
		log.info("Quando tentar incluir anexo sem informar campos obrigatórios");

		acoesUsuarioPage.clicarOpcoes("Titular", "0");
		beneficiariosPage.clicarAnexar();
		beneficiariosPage.clicarNovoAnexo();

		// nao informar tipo documento
		beneficiariosPage.clicarSalvar();
		assertEquals("O tipo de documento deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		// nao informar anexo
		beneficiariosPage.selecionarTipoDocumento("CPF");
		beneficiariosPage.clicarSalvar();
		assertEquals("O anexo deve ser inserido.", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("clicar no botao salvar")
	public void clicarSalvar() throws Throwable {
		log.info("Quando clicar em salvar");

		beneficiariosPage.clicarSalvar();
	}

	@Quando("verificar link para download")
	public void verificarLinkDownload() throws Throwable {
		log.info("Entao verificar link para download ");

		beneficiariosPage.clicarCancelar();
		acoesUsuarioPage.clicarOpcoes("CPF", "0");

		assertTrue(beneficiariosPage.verificarDownload());
	}

	@Quando("clicar em Novo Beneficiario")
	public void clicarNovoBeneficiario() throws Throwable {
		log.info("Quando clicar em Novo Beneficiario");

		beneficiariosPage.clicarNovoBeneficiario();
	}

	@Quando("tentar cadastrar beneficiario sem informar os campos obrigatorios")
	public void tentarCadastrarBeneficiario() throws Throwable {
		log.info("Quando tentar cadastrar beneficiario sem informar os campos obrigatórios");

		beneficiariosPage.tentarCadastrarBeneficiarioSemTipoBeneficiario();
		assertEquals("O tipo beneficiário deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemNome();
		assertEquals("O nome deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemCPF();
		assertEquals("O CPF deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		// Antes estava dentro do método do beneficiariosPage
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuBeneficiario();
		beneficiariosPage.tentarCadastrarBeneficiarioSemSexo();
		assertEquals("O sexo deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemNomeMae();
		assertEquals("O nome da mãe deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

        // Antes estava dentro do método do beneficiariosPage
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuBeneficiario();
		beneficiariosPage.tentarCadastrarBeneficiarioSemParentesco();
		assertEquals("O grau parentesco deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemDataNascimento();
		assertEquals("A data de nascimento deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

        // Antes estava dentro do método do beneficiariosPage
        menuPage.acessarMenuManutencao();
        menuPage.acessarItemMenuBeneficiario();
		beneficiariosPage.tentarCadastrarBeneficiarioSemNacionalidade();
		assertEquals("A nacionalidade deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

        // Antes estava dentro do método do beneficiariosPage
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuBeneficiario();
		beneficiariosPage.tentarCadastrarBeneficiarioSemEstadoCivil();
		assertEquals("O estado civil deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemDadosContatos("", "3625-9858", "32", "98745-9632");

		assertEquals("O DDD do telefone deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemDadosContatos("32", "", "32", "98745-9632");

		assertEquals("O telefone deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemDadosContatos("32", "3625-9858", "", "98745-9632");

		assertEquals("O DDD do celular deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiariosPage.tentarCadastrarBeneficiarioSemDadosContatos("32", "3625-9858", "32", "");

		assertEquals("O celular deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Entao("exibe a mensagem de erro {string}")
	public void verificarMensagemErro(String mensagem) throws Throwable {
		log.info("Entao exibe a mensagem de erro {}", mensagem);

		assertEquals(mensagem, econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Entao("exibe o alerta com a mensagem {string}")
	public void verificarAlerta(String mensagem) throws Throwable {
		log.info("Entao exibe o alerta com a mensagem {}", mensagem);

		assertEquals(mensagem, econsigHelper.getMensagemPopUp(getWebDriver()));
	}
}
