package com.zetra.econsig.bdd.steps.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ManutencaoConsignatariaElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ManutencaoConsignatariaPage extends BasePage {

    private final ManutencaoConsignatariaElementMap manutencaoConsignatariaElementMap;

    public ManutencaoConsignatariaPage(WebDriver webDriver) {
        super(webDriver);
        manutencaoConsignatariaElementMap = PageFactory.initElements(webDriver, ManutencaoConsignatariaElementMap.class);
    }

    public void preencherDataParaDesbloqueio(String data) {
        while (!manutencaoConsignatariaElementMap.dataDesbloqueio.getDomProperty("value").matches(data)) {
            manutencaoConsignatariaElementMap.dataDesbloqueio.clear();
            manutencaoConsignatariaElementMap.dataDesbloqueio.sendKeys(data);
        }
    }

    public void preencherObservacao(String observacao) {
        await.until(() -> webDriver.getPageSource().contains("Motivo da operação"));

        while (manutencaoConsignatariaElementMap.observacao.getDomProperty("value").isEmpty()) {
            manutencaoConsignatariaElementMap.observacao.sendKeys(observacao);
        }
    }

    public void selecionarMotivo(String motivo) {
        await.until(() -> webDriver.getPageSource().contains("MOTIVO"));

        final Select select = new Select(manutencaoConsignatariaElementMap.motivoOperacao);
        select.selectByVisibleText(motivo);
    }

    public void selecionarPenalidade(String penalidade) {
        await.until(() -> webDriver.getPageSource().contains("Penalizar consignatária"));

        final Select select = new Select(manutencaoConsignatariaElementMap.penalizarConsignataria);
        select.selectByVisibleText(penalidade);
    }

    public void preencherCNPJ(String cnpj) {
        manutencaoConsignatariaElementMap.cnpj.clear();
        manutencaoConsignatariaElementMap.cnpj.sendKeys(cnpj);
    }

    public void selecionarNatureza(String natureza) {
        await.until(() -> webDriver.getPageSource().contains("Edição de dados"));

        final Select select = new Select(manutencaoConsignatariaElementMap.natureza);
        select.selectByVisibleText(natureza);
    }

    public void selecionarGrupoConsignataria(String grupoConsignataria) {
        final Select select = new Select(manutencaoConsignatariaElementMap.grupoConsignataria);
        select.selectByVisibleText(grupoConsignataria);
    }

    public void marcarExigeEnderecoAcesso() {
        waitDriver
                  .until(ExpectedConditions.elementToBeClickable(manutencaoConsignatariaElementMap.exigeEnderecoAcesso));

        while (!manutencaoConsignatariaElementMap.exigeEnderecoAcesso.isSelected()) {
            js.executeScript("arguments[0].click()", manutencaoConsignatariaElementMap.exigeEnderecoAcesso);
        }
    }

    public void selecionarNaoPermiteIncluirAde() {
        waitDriver
                  .until(ExpectedConditions.elementToBeClickable(manutencaoConsignatariaElementMap.naoPermiteIncluirAde));

        while (!manutencaoConsignatariaElementMap.naoPermiteIncluirAde.isSelected()) {
            manutencaoConsignatariaElementMap.naoPermiteIncluirAde.click();
        }
    }

    public void preencherCodigo(String codigo) {
        waitDriver.until(ExpectedConditions.visibilityOf(manutencaoConsignatariaElementMap.codigo));

        while (!manutencaoConsignatariaElementMap.codigo.getDomProperty("value").matches(codigo)) {
            manutencaoConsignatariaElementMap.codigo.clear();
            manutencaoConsignatariaElementMap.codigo.sendKeys(codigo);
        }

    }

    public void preencherNumeroContrato(String numeroContrato) {
        manutencaoConsignatariaElementMap.numeroContrato.clear();
        manutencaoConsignatariaElementMap.numeroContrato.sendKeys(numeroContrato);
    }

    public void preencherEmailExpiracao(String emailExpiracao) {
        manutencaoConsignatariaElementMap.emailExpiracao.clear();
        manutencaoConsignatariaElementMap.emailExpiracao.sendKeys(emailExpiracao);
    }

    public void preencherBanco(String banco) {
        manutencaoConsignatariaElementMap.banco.sendKeys(banco);
        manutencaoConsignatariaElementMap.banco.sendKeys(banco);
    }

    public void preencherAgencia(String agencia) {
        manutencaoConsignatariaElementMap.agencia.clear();
        manutencaoConsignatariaElementMap.agencia.sendKeys(agencia);
    }

    public void preencherConta(String conta) {
        manutencaoConsignatariaElementMap.conta.clear();
        manutencaoConsignatariaElementMap.conta.sendKeys(conta);
    }

    public void preencherDigito(String digito) {
        manutencaoConsignatariaElementMap.digito.clear();
        manutencaoConsignatariaElementMap.digito.sendKeys(digito);
    }

    public void preencherInstrucoesContato(String instrucoesContato) {
        manutencaoConsignatariaElementMap.instrucoesContato.clear();
        manutencaoConsignatariaElementMap.instrucoesContato.sendKeys(instrucoesContato);
    }

    public void selecionarExigeEnderecoAcesso() {
        manutencaoConsignatariaElementMap.exigeEnderecoAcesso.click();
    }

    public void selecionarNaoExigeEnderecoAcesso() {
        manutencaoConsignatariaElementMap.naoExigeEnderecoAcesso.click();
    }

    public void selecionarCodigoZetrasoft(String codigoZetrasoft) {
        final Select select = new Select(manutencaoConsignatariaElementMap.codigoZetrasoft);
        select.selectByVisibleText(codigoZetrasoft);
    }

    public void selecionarUF(String uf) {
        final Select select = new Select(manutencaoConsignatariaElementMap.uf);
        select.selectByVisibleText(uf);
    }

    public void alterarConsignatariaCse() {
        selecionarNatureza("Associação de Servidor Publico");
        selecionarGrupoConsignataria("geral");
        preencherNumeroContrato("96857452");
        preencherEmailExpiracao("csa@gmail.com");
        preencherInstrucoesContato("Testes automatizado, instruçoes contato para servidor entrar em contato");
        preencherBanco("");
        preencherAgencia("");
        preencherConta("");
        preencherDigito("");
        selecionarUF("Minas Gerais");
    }

    public void alterarConsignatariaSup() {
        selecionarNatureza("Sindicato");
        preencherBanco("104");
        preencherAgencia("2896");
        preencherConta("36985");
        preencherDigito("2");
    }

    public void clicarEditarParamConsignataria() {
        manutencaoConsignatariaElementMap.editarParamConsignataria.click();

        await.until(() -> webDriver.getPageSource().contains("Parâmetros da consignatária"));
    }

    public void marcarExigeCertificadoDigital() {
        while (!manutencaoConsignatariaElementMap.exigeCertificado.isSelected()) {
            manutencaoConsignatariaElementMap.exigeCertificado.click();
        }
    }

    public void clicarImprimir() {
        manutencaoConsignatariaElementMap.imprimir.click();
    }

    public void clicarCriarNovaConsignataria() {
        manutencaoConsignatariaElementMap.criarNovaConsignataria.click();

        await.pollDelay(Duration.ofSeconds(1))
             .until(() -> webDriver.getPageSource().contains("Inclusão de nova consignatária"));
    }

    public void preencherNome(String nome) {
        manutencaoConsignatariaElementMap.nome.sendKeys(nome);
    }

    public boolean isExibeServico(String servico) {
        boolean exibeServico = false;
        final Select select = new Select(manutencaoConsignatariaElementMap.comboServico);

        for (final WebElement element : select.getOptions()) {
            if (element.getText() == servico) {
                exibeServico = true;
                break;
            }
        }
        return exibeServico;
    }

    public String getIndiceReservarMargem() {
        return manutencaoConsignatariaElementMap.adeIndice.getDomProperty("value");
    }

    public List<WebElement> getIndicesReservarMargem() {
        final Select select = new Select(manutencaoConsignatariaElementMap.adeIndice);

        return select.getOptions();
    }

    public void selecionarIndiceReservarMargem(String indice) {
        final Select select = new Select(manutencaoConsignatariaElementMap.adeIndice);
        select.selectByVisibleText(indice);
    }

    public String getTextoIndiceReservarMargem() {
        return manutencaoConsignatariaElementMap.txtAdeIndice.getText();
    }

    public String getTextoPrazosReservarMargem() {
        return manutencaoConsignatariaElementMap.txtPrazos.getText();
    }

    public void preencherCadastroConsignataria(String codigo, String numeroContrato, String cnpj) {
        preencherCodigo(codigo);
        preencherNome("Banco Caixa Economica Federal");
        manutencaoConsignatariaElementMap.nomeAbrev.sendKeys("CEF");
        selecionarNatureza("Instituição Financeira Privada");
        selecionarGrupoConsignataria("geral");
        preencherNumeroContrato(numeroContrato);
        manutencaoConsignatariaElementMap.dataExpiracaoContratual.sendKeys("01/12/2020");
        manutencaoConsignatariaElementMap.dataExpiracaoCadastral.sendKeys("01/12/2020");
        preencherEmailExpiracao("cef@cef.com");
        preencherCNPJ(cnpj);
        preencherBanco("104");
        preencherAgencia("2896");
        preencherConta("36985");
        preencherDigito("2");
        manutencaoConsignatariaElementMap.cnpjDadosBancarios.sendKeys("35.983.125/0001-63");
        manutencaoConsignatariaElementMap.contato.sendKeys("Antonio Carlos");
        manutencaoConsignatariaElementMap.contatoTelefone.sendKeys("3136527485");
        manutencaoConsignatariaElementMap.cargoResponsavel1.sendKeys("Gerente");
        manutencaoConsignatariaElementMap.telefoneResponsavel1.sendKeys("31985748596");
        manutencaoConsignatariaElementMap.logradouro.sendKeys("Avenida Brasil");
        manutencaoConsignatariaElementMap.numero.sendKeys("2569");
        manutencaoConsignatariaElementMap.bairro.sendKeys("Centro");
        manutencaoConsignatariaElementMap.cidade.sendKeys("Rio de Janeiro");
        selecionarUF("Rio de Janeiro");
        manutencaoConsignatariaElementMap.cep.sendKeys("21698-256");
        manutencaoConsignatariaElementMap.enderecoAlternativo.sendKeys("Avenida dois, 1523, Copacabana");
        manutencaoConsignatariaElementMap.telefoneContato.sendKeys("2165874555");
        manutencaoConsignatariaElementMap.fax.sendKeys("2132657485");
        manutencaoConsignatariaElementMap.email.sendKeys("caixa@caixa.com");
        preencherInstrucoesContato("Testes automatizado, instruçoes contato para servidor entrar em contato");
    }

    public void desmarcarOrgaoCarlotaJoaquina() {
        while (manutencaoConsignatariaElementMap.orgaoCarlotaJoaquina.isSelected()) {
            manutencaoConsignatariaElementMap.orgaoCarlotaJoaquina.click();
        }
    }

    public void marcarOrgaoCarlotaJoaquina() {
        while (!manutencaoConsignatariaElementMap.orgaoCarlotaJoaquina.isSelected()) {
            manutencaoConsignatariaElementMap.orgaoCarlotaJoaquina.click();
        }
    }

    public void preencherCodigoVerbaOrgaoCarlotaJoaquina(String codigo) {
        manutencaoConsignatariaElementMap.codigoVerba.sendKeys(codigo);
    }

    public void clicarIndice() {
        manutencaoConsignatariaElementMap.indice.click();
    }

    public void clicarEditarIndice() {
        manutencaoConsignatariaElementMap.editarIndice.click();
    }

    public void clicarExcluirIndice() {
        manutencaoConsignatariaElementMap.excluirIndice.click();
    }

    public void clicarNovoIndice() {
        await.pollDelay(Duration.ofSeconds(1))
             .until(() -> webDriver.getPageSource().contains("Lista de índices"));

        manutencaoConsignatariaElementMap.novoIndice.click();
    }

    public void preencherCodigoIndice(String codigo) {
        waitDriver.until(ExpectedConditions.visibilityOf(manutencaoConsignatariaElementMap.codigoIndice));

        while (!manutencaoConsignatariaElementMap.codigoIndice.getDomProperty("value").matches(codigo)) {
            manutencaoConsignatariaElementMap.codigoIndice.clear();
            manutencaoConsignatariaElementMap.codigoIndice.sendKeys(codigo);
        }
    }

    public void preencherDescricaoIndice(String descricao) {
        while (!manutencaoConsignatariaElementMap.descricaoIndice.getDomProperty("value").matches(descricao)) {
            manutencaoConsignatariaElementMap.descricaoIndice.clear();
            manutencaoConsignatariaElementMap.descricaoIndice.sendKeys(descricao);
        }
    }

    public void preencherPrazoInicial(String prazo) {
        while (!manutencaoConsignatariaElementMap.prazoInicial.getDomProperty("value").matches(prazo)) {
            manutencaoConsignatariaElementMap.prazoInicial.clear();
            manutencaoConsignatariaElementMap.prazoInicial.sendKeys(prazo);
        }
    }

    public void preencherPrazoFinal(String prazo) {
        while (!manutencaoConsignatariaElementMap.prazoFinal.getDomProperty("value").matches(prazo)) {
            manutencaoConsignatariaElementMap.prazoFinal.clear();
            manutencaoConsignatariaElementMap.prazoFinal.sendKeys(prazo);
        }
    }

    public void clicarPrazos() {
        manutencaoConsignatariaElementMap.prazos.click();

        await.pollDelay(Duration.ofSeconds(1))
             .until(() -> webDriver.getPageSource().contains("Inserção de prazos"));
    }

    public void clicarPenalidade() {
        manutencaoConsignatariaElementMap.penalidade.click();

        await.until(() -> webDriver.getPageSource().contains("Penalizar consignatária"));

    }

    public void clicarInserir() {
        manutencaoConsignatariaElementMap.inserir.click();
    }

    public void clicarEditarParamServico() {
        manutencaoConsignatariaElementMap.editarParamServico.click();
    }

    public void clicarDesbloquearTodos() {
        manutencaoConsignatariaElementMap.desbloquearTodos.click();

        await.pollDelay(Duration.ofSeconds(1))
             .until(() -> webDriver.getPageSource().contains("Desbloqueado"));
    }

    public void clicarBloquearTodos() {
        manutencaoConsignatariaElementMap.bloquearTodos.click();

        await.until(() -> webDriver.getPageSource().contains("Bloqueado"));
    }

    public void clicarConfigurarAuditoria() {
        manutencaoConsignatariaElementMap.configurarAuditoria.click();
    }

    public void selecionarFuncoes() {
        js.executeScript("arguments[0].click()", manutencaoConsignatariaElementMap.todosGeral);

        js.executeScript("arguments[0].click()", manutencaoConsignatariaElementMap.todosOperacional);

        js.executeScript("arguments[0].click()", manutencaoConsignatariaElementMap.funcaoAtualizarProcessoPortabilidade);

        js.executeScript("arguments[0].click()", manutencaoConsignatariaElementMap.funcaoConfirmarSolicitacao);

        await.until(() -> webDriver.getPageSource().contains("Validação de Lote"));
    }

    public void clicarBloquear(String codigo) {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource().contains("Bloquear"));

        final WebElement main = webDriver.findElement(By.cssSelector(".table"));
        final List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
        for (final WebElement row : rows) {
            if (row.findElement(By.xpath(".//td")).getText().equals(codigo)) {
                final WebElement btnOpcoes = row.findElement(By.linkText("Bloquear"));
                btnOpcoes.click();
                return;
            }
        }
    }

    public boolean isSituacaoPrazosBloqueados() {
        boolean situacao = true;

        final WebElement main = webDriver.findElement(By.cssSelector(".table"));
        final List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
        for (final WebElement row : rows) {
            if ("Desbloqueado".equals(row.findElement(By.xpath(".//td[2]")).getText())) {
                situacao = false;
                break;
            }
        }
        return situacao;
    }

    public boolean isSituacaoPrazoBloqueado(String prazo) {
        boolean situacao = false;

        final WebElement main = webDriver.findElement(By.cssSelector(".table"));
        final List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));
        for (final WebElement row : rows) {
            if (row.findElement(By.xpath(".//td")).getText().equals(prazo) && "Bloqueado".equals(row.findElement(By.xpath(".//td[2]")).getText())) {
                situacao = true;
                break;
            }
        }
        return situacao;
    }

    public void clicarAcaoEditarPostoGraduacao() {
        manutencaoConsignatariaElementMap.editarParamPostoGraduacao.click();
    }

    public List<WebElement> listarAcoes() {
        final WebElement listarAcoes = webDriver.findElement(By.className("dropdown-menu"));
        return listarAcoes.findElements(By.className("dropdown-item"));
    }

    public void clicarEditarRegraTaxaJuros() {
        manutencaoConsignatariaElementMap.editarRegraTaxaJuros.click();

        await.until(() -> webDriver.getPageSource().contains("Editar regra de taxa de juros"));
    }
}
