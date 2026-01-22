package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.BeneficiariosElementMap;
import com.zetra.econsig.bdd.steps.maps.EconsigElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class BeneficiariosPage extends BasePage {

    private static final String arquivo = "src/test/resources/files/arquivo_para_teste.pdf";

    private final BeneficiariosElementMap beneficiariosElementMap;

    private final EconsigElementMap econsigElementMap;

    public BeneficiariosPage(WebDriver webDriver) {
        super(webDriver);
        beneficiariosElementMap = PageFactory.initElements(webDriver, BeneficiariosElementMap.class);
        econsigElementMap = PageFactory.initElements(webDriver, EconsigElementMap.class);
    }

    public void clicarMaisAcoes() {
        while (beneficiariosElementMap.botaoAcoes.getDomAttribute("aria-expanded").contains("false")) {
            beneficiariosElementMap.botaoAcoes.click();
        }
    }

    public void clicarNovoBeneficiario() {
        waitDriver.withTimeout(Duration.ofSeconds(1)).until(ExpectedConditions.visibilityOf(beneficiariosElementMap.novoBeneficiario));

        js.executeScript("arguments[0].click()", beneficiariosElementMap.novoBeneficiario);
    }

    public void aguardaModalSimulacaoFicarInvisivel() {
        waitDriver.until(ExpectedConditions.invisibilityOfAllElements(beneficiariosElementMap.modalSimulacao));

    }

    public void clicarEditarEnderecoServidor() {
        beneficiariosElementMap.editarEnderecoServidor.click();

        await.until(() -> webDriver.getPageSource(), containsString("Manutenção de Endereços do Servidor"));
    }

    public void clicarCadastrarNovoEndereco() {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getCurrentUrl().contains("editarServidor"));

        beneficiariosElementMap.cadastrarNovoEndereco.click();
    }

    public void preencherNome(String nome) {
        await.until(() -> webDriver.getCurrentUrl().contains("alterarBeneficiarios"));

        while (!beneficiariosElementMap.nome.getDomProperty("value").matches(nome)) {
            beneficiariosElementMap.nome.clear();
            beneficiariosElementMap.nome.sendKeys(nome);
        }
    }

    public void preencherCPF(String cpf) {
        beneficiariosElementMap.cpf.sendKeys(cpf);
    }

    public void preencherRG(String rg) {
        await.until(() -> webDriver.getPageSource(), containsString("Telefone"));

        beneficiariosElementMap.rg.clear();
        beneficiariosElementMap.rg.sendKeys(rg);
    }

    public void marcarSexoFeminino() {
        beneficiariosElementMap.sexoFeminino.click();
    }

    public void marcarSexoMasculino() {
        beneficiariosElementMap.sexoMasculino.click();
    }

    public void preencherDadosContatos(String dddTelefone, String telefone, String dddCelular, String celular) {

        beneficiariosElementMap.numeroTelefone.clear();
        beneficiariosElementMap.dddCelular.clear();
        beneficiariosElementMap.numeroCelular.clear();

        while (!beneficiariosElementMap.dddTelefone.getDomProperty("value").matches(dddTelefone)) {
            beneficiariosElementMap.dddTelefone.clear();
            beneficiariosElementMap.dddTelefone.sendKeys(dddTelefone);
        }

        beneficiariosElementMap.numeroTelefone.sendKeys(telefone);
        beneficiariosElementMap.dddCelular.sendKeys(dddCelular);
        beneficiariosElementMap.numeroCelular.sendKeys(celular);
    }

    public void preencherNomeMae(String nome) {
        beneficiariosElementMap.nomeMae.sendKeys(nome);
    }

    public void preencherGrauParentesco(String grauParentesco) {
        beneficiariosElementMap.grauParentesco.sendKeys(grauParentesco);
    }

    public void preencherDataNascimento(String dataNascimento) {
        beneficiariosElementMap.dataNascimento.sendKeys(dataNascimento);
    }

    public void preencherNacionalidade(String nacionalidade) {
        beneficiariosElementMap.nacionalidade.sendKeys(nacionalidade);
    }

    public void preencherEstadoCivil(String estadoCivil) {
        beneficiariosElementMap.estadoCivil.sendKeys(estadoCivil);
    }

    public void selecionarTipoBeneficiario(String tipoBeneficiario) {
        await.until(() -> webDriver.getPageSource(), containsString("Grau de parentesco"));

        Select select = new Select(beneficiariosElementMap.tipoBeneficiario);
        select.selectByVisibleText(tipoBeneficiario);
    }

    public void clicarSalvar() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(beneficiariosElementMap.salvar));
        js.executeScript("arguments[0].click()", beneficiariosElementMap.salvar);
    }

    public void clicarCancelar() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(beneficiariosElementMap.cancelar));

        beneficiariosElementMap.cancelar.click();
    }

    public void preencherCEP(String cep) {
        waitDriver.until(ExpectedConditions.attributeToBeNotEmpty(beneficiariosElementMap.tipoEndereco, "value"));

        while (!beneficiariosElementMap.cep.getDomProperty("value").matches(cep)) {
            beneficiariosElementMap.cep.clear();
            beneficiariosElementMap.cep.sendKeys(cep);
        }
    }

    public void cadastrarBeneficiario(String tipoBeneficiario, String nome, String cpf, String nomeMae, String grauParentesco, String dataNascimento, String nacionalidade, String estadoCivil, String dddTelefone, String telefone, String dddCelular, String celular) {
        limparDadosBeneficiario();
        selecionarTipoBeneficiario(tipoBeneficiario);
        preencherNome(nome);
        preencherCPF(cpf);
        preencherNomeMae(nomeMae);
        preencherGrauParentesco(grauParentesco);
        preencherDataNascimento(dataNascimento);
        preencherNacionalidade(nacionalidade);
        preencherEstadoCivil(estadoCivil);
        preencherDadosContatos(dddTelefone, telefone, dddCelular, celular);
    }

    public void limparDadosBeneficiario() {
        beneficiariosElementMap.nome.clear();
        beneficiariosElementMap.cpf.clear();
        beneficiariosElementMap.nomeMae.clear();
        beneficiariosElementMap.dataNascimento.clear();
    }

    public void tentarCadastrarBeneficiarioSemTipoBeneficiario() {
        preencherNome("Carla da Silva");
        preencherCPF("117.521.220-20");
        preencherNomeMae("Emanuela da Silva");
        preencherGrauParentesco("Filho");
        preencherDataNascimento("12/12/1989");
        preencherNacionalidade("Brasileiro");
        preencherEstadoCivil("Divorciado(a)");
        marcarSexoFeminino();
        preencherDadosContatos("32", "3265-9856", "32", "96985-6398");

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemNome() {
        cadastrarBeneficiario("Agregado", "", "117.521.220-20", "Emanuela da Silva", "Filho", "12/12/1989", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemCPF() {
        cadastrarBeneficiario("Agregado", "Carla da Silva", "", "Emanuela da Silva", "Filho", "12/12/1989", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemNomeMae() {
        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "", "Filho", "12/12/1989", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemParentesco() {
        clicarNovoBeneficiario();

        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "", "12/12/1989", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemDataNascimento() {
        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "Filho", "", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemNacionalidade() {
        clicarNovoBeneficiario();

        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "Filho", "12/12/1989", "", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemEstadoCivil() {
        clicarNovoBeneficiario();

        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "Filho", "12/12/1989", "Brasileiro", "", "32", "3265-9856", "32", "96985-6398");
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemDadosContatos(String dddTelefone, String telefone, String dddCelular, String celular) {
        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "Filho", "12/12/1989", "Brasileiro", "Divorciado(a)", dddTelefone, telefone, dddCelular, celular);
        marcarSexoFeminino();

        clicarSalvar();
    }

    public void tentarCadastrarBeneficiarioSemSexo() {
        clicarNovoBeneficiario();

        cadastrarBeneficiario("Agregado", "Carla da Silva", "117.521.220-20", "Emanuela da Silva", "Filho", "12/12/1989", "Brasileiro", "Divorciado(a)", "32", "3265-9856", "32", "96985-6398");

        clicarSalvar();
    }

    public void preencherLogradouro(String logradouro) {
        beneficiariosElementMap.logradouro.clear();
        beneficiariosElementMap.logradouro.sendKeys(logradouro);
    }

    public void preencherNumero(String numero) {
        beneficiariosElementMap.numeroEndereco.sendKeys(numero);
    }

    public void preencherComplemento(String complemento) {
        beneficiariosElementMap.complemento.sendKeys(complemento);
    }

    public void preencherBairro(String bairro) {
        beneficiariosElementMap.bairro.clear();
        beneficiariosElementMap.bairro.sendKeys(bairro);
    }

    public void preencherUF(String uf) {
        if (beneficiariosElementMap.uf.getTagName().equalsIgnoreCase("select")) {
            Select drpDown = new Select(beneficiariosElementMap.uf);
            drpDown.selectByVisibleText(uf);
        } else {
            beneficiariosElementMap.uf.sendKeys(uf);
        }
    }

    public void preencherCidade(String cidade) {
        if (beneficiariosElementMap.cidade.getTagName().equalsIgnoreCase("select")) {
            Select drpDown = new Select(beneficiariosElementMap.cidade);
            drpDown.selectByVisibleText(cidade);
        } else {
            beneficiariosElementMap.cidade.sendKeys(cidade);
        }
    }

    public void clicarEditar() {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource().contains("Beneficiário"));

        beneficiariosElementMap.editar.click();
    }

    public void clicarExcluir() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(beneficiariosElementMap.excluir));

        js.executeScript("arguments[0].click()", beneficiariosElementMap.excluir);
    }

    public void clicarExcluirEndereco() {
        await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(), containsString("Manutenção de Endereços do Servidor"));

        beneficiariosElementMap.excluir.click();
    }

    public void clicarAnexar() {
        beneficiariosElementMap.anexar.click();
    }

    public void clicarNovoAnexo() {
        await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(), containsString("Lista de anexo de Beneficiários"));

        beneficiariosElementMap.novoAnexo.click();

        await.until(() -> webDriver.getPageSource(), containsString("INCLUSÃO DE NOVO ANEXO BENEFICIÁRIO"));
    }

    public void selecionarTipoDocumento(String tipoDocumento) {
        Select select = new Select(beneficiariosElementMap.tipoDocumento);
        select.selectByVisibleText(tipoDocumento);
    }

    public void editarTipoDocumento(String tipoDocumento) {
        waitDriver.until(ExpectedConditions.visibilityOf(beneficiariosElementMap.tipoDocumento));

        beneficiariosElementMap.tipoDocumento.sendKeys(tipoDocumento);
    }

    public void preencherDataValidade(String data) {
        while (!beneficiariosElementMap.dataValidade.getDomProperty("value").matches(data)) {
            beneficiariosElementMap.dataValidade.clear();
            beneficiariosElementMap.dataValidade.sendKeys(data);
        }
    }

    public void preencherDescricaoAnexo(String descricao) {
        beneficiariosElementMap.descricaoAnexo.clear();
        beneficiariosElementMap.descricaoAnexo.sendKeys(descricao);
    }

    public void anexarArquivoBeneficiario() throws IOException {
        beneficiariosElementMap.anexo.sendKeys(new File(arquivo).getCanonicalPath());
    }

    public void incluirAnexo() throws IOException {
        clicarAnexar();
        clicarNovoAnexo();
        selecionarTipoDocumento("CPF");
        anexarArquivoBeneficiario();
        clicarSalvar();
        // aguarda mensagem de sucesso
        await.pollDelay(Duration.ofSeconds(1)).until(() -> econsigElementMap.txtMensagemSucesso.getText(), notNullValue());

        clicarCancelar();
    }

    public boolean verificarDownload() {
        return beneficiariosElementMap.download.isEnabled();
    }

    public void criarNovoEndereco() {
        clicarCadastrarNovoEndereco();
        preencherCEP("31.710-400");
        preencherLogradouro("Avenida Portugal");
        preencherUF("Minas Gerais");
        preencherCidade("Belo Horizonte");
        preencherBairro("Itapoã");
        preencherNumero("3589");
        preencherComplemento("A");
        clicarSalvar();

        // aguarda mensagem de sucesso
        await.pollDelay(Duration.ofSeconds(1)).until(() -> econsigElementMap.txtMensagemSucesso.getText(), notNullValue());

        clicarCancelar();

        // aguarda retorna para a tela anterior
        await.until(() -> webDriver.getPageSource(), containsString("Manutenção de Endereços do Servidor"));
    }

    public void anexarArquivo() throws IOException {
        clicarAnexar();
        clicarNovoAnexo();
        selecionarTipoDocumento("CPF");
        preencherDescricaoAnexo("Descricao anexo");
        anexarArquivoBeneficiario();
        clicarSalvar();
        // aguarda mensagem de sucesso
        await.pollDelay(Duration.ofSeconds(1)).until(() -> econsigElementMap.txtMensagemSucesso.getText(), notNullValue());
    }
}
