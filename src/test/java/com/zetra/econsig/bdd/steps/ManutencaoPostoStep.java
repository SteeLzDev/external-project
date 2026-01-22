package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zetra.econsig.bdd.steps.pages.ManutencaoPostoPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoPostoStep {

    private ManutencaoPostoPage manutencaoPostoPage;

    @Before
    public void setUp() throws Exception {
        manutencaoPostoPage = new ManutencaoPostoPage(getWebDriver());
    }

    @Entao("exibe a lista de postos")
    public void exibe_lista() throws Throwable {
        log.info("Então exibe a lista de postos");

        assertTrue(getWebDriver().getPageSource().contains("SARGENTE TESTE26"));
        assertTrue(getWebDriver().getPageSource().contains("504,50"));
    }

    @Quando("acessar opcao Editar do codigo {string}")
    public void clicar_opcao_editar(String codigo) throws Throwable {
        log.info("Quando acessar opção Editar do código {}", codigo);

        manutencaoPostoPage.clicarEditar(codigo);
    }

    @E("alterar o campo codigo {string}")
    public void preencher_campo_codigo(String codigo) throws Throwable {
        log.info("E alterar o campo codigo {}", codigo);

        manutencaoPostoPage.preencherCodigo(codigo);
    }

    @E("alterar o campo descricao {string}")
    public void preencher_campo_descricao(String descricao) throws Throwable {
        log.info("E alterar o campo descrição {}", descricao);

        manutencaoPostoPage.preencherDescricao(descricao);
    }

    @E("alterar o campo valor soldo")
    public void preencher_campo_valor_soldo() throws Throwable {
        log.info("E alterar o campo valor soldo");

        manutencaoPostoPage.preencherValorSaldo("10,75");
    }

    @E("alterar o campo percentualtaxacond {string}")
    public void preencher_campo_percentualtaxacond(String valor) throws Throwable {
        log.info("E alterar o campo percentualtaxacond {}", valor);

        manutencaoPostoPage.preencherPercentualTaxaCond(valor);
    }

    @E("clicar em salvar")
    public void clicar_opcao_salvar() throws Throwable {
        log.info("E clicar em salvar");

        manutencaoPostoPage.clicarSalvar();
    }
}
