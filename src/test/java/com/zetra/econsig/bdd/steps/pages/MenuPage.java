package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.MenuElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class MenuPage extends BasePage {

	private final MenuElementMap menuElementMap;

	public MenuPage(WebDriver webDriver) {
		super(webDriver);
		menuElementMap = PageFactory.initElements(webDriver, MenuElementMap.class);
	}

	public void acessarPaginaInicial() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.paginaInicial.isEnabled());

		menuElementMap.paginaInicial.click();

		await.until(() -> webDriver.getTitle().contains("eConsig - Principal"));
	}

	public void acessarMenuManutencao() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.manutencoes));

		while (menuElementMap.manutencoes.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.manutencoes.click();
		}
	}

	public void acessarMenuOperacional() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.operacional));

		while (menuElementMap.operacional.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.operacional.click();
		}
	}

	public void acessarMenuFavoritos() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.favoritos));

		while (menuElementMap.favoritos.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.favoritos.click();
		}
	}

	public void acessarMenuRelatorios() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.relatorios));

		while (menuElementMap.relatorios.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.relatorios.click();
		}
	}

	public void acessarMenuSistema() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.sistema));

		while (menuElementMap.sistema.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.sistema.click();
		}
	}

	public void acessarMenuRescisao() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.rescisao));

		while (menuElementMap.rescisao.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.rescisao.click();
		}
	}

	public void acessarMenuBeneficios() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(menuElementMap.beneficios));

		while (menuElementMap.beneficios.getDomAttribute("aria-expanded").contains("false")) {
			menuElementMap.beneficios.click();
		}
	}

	public void acessarItemMenuAlterarSenha() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.alterarSenha, "Alterar Senha"));

		menuElementMap.alterarSenha.click();
	}

	public void acessarItemMenuConsultarMargem() {
		waitDriver
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.consultarMargem, "Consultar Margem"));

		menuElementMap.consultarMargem.click();
	}

	public void acessarItemMenuEstabelecimentos() {
		waitDriver.pollingEvery(Duration.ofSeconds(1)).until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.estabelecimentos, "Estabelecimentos"));

		menuElementMap.estabelecimentos.click();
	}

	public void acessarItemMenuDadosCadastrais() {
		waitDriver.pollingEvery(Duration.ofSeconds(1))
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.dadosCadastrais, "Dados cadastrais"));

		menuElementMap.dadosCadastrais.click();
	}

	public void acessarItemMenuOperacionalSolicitarEmprestimo() {
		waitDriver.pollingEvery(Duration.ofSeconds(1)).until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.simularConsignacao, "Solicitar empréstimo"));

		menuElementMap.simularConsignacao.click();
	}

	public void acessarItemMenuGrupoServicos() {
		waitDriver
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.grupoServicos, "Grupo de Serviços"));

		menuElementMap.grupoServicos.click();
	}

	public void acessarFavoritoServidores() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.servidores, "Servidores"));

		menuElementMap.servidores.click();
	}

	public void acessarFavoritosConsignante() {
		waitDriver.pollingEvery(Duration.ofSeconds(1)).until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoConsignante, "Consignante"));

		menuElementMap.manutencaoConsignante.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosOrgao() {
		waitDriver.pollingEvery(Duration.ofSeconds(1))
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoOrgao, "Órgãos"));

		menuElementMap.manutencaoOrgao.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarItemMenuBeneficiario() {
		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoBeneficiario, "Benefíciários"));

		menuElementMap.manutencaoBeneficiario.click();
	}

	public void acessarItemMenuConsignatarias() {
		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoConsignatarias, "Consignatárias"));

		menuElementMap.manutencaoConsignatarias.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarItemMenuConsignataria() {
		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoConsignataria, "Consignatária"));

		menuElementMap.manutencaoConsignataria.click();

		await.until(() -> webDriver.getPageSource().contains("Manutenção de consignatária"));
	}

	public void acessarItemMenuCorrespondentes() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoCorrespondentes,
				"Correspondentes"));

		menuElementMap.manutencaoCorrespondentes.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarItemMenuCorrespondente() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoCorrespondentes,
				"Correspondente"));

		menuElementMap.manutencaoCorrespondentes.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosConsignatarias() {
		await.pollDelay(2, TimeUnit.SECONDS).until(() -> menuElementMap.manutencaoConsignatarias.getText(),
				is("Consignatárias"));

		menuElementMap.manutencaoConsignatarias.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosOrgaos() {
		await.pollDelay(2, TimeUnit.SECONDS).until(() -> menuElementMap.manutencaoOrgao.getText(), is("Órgãos"));

		menuElementMap.manutencaoOrgao.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosRelatorioCorrespondentes() {
		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.relCorrespondentes, "Correspondentes"));

		menuElementMap.relCorrespondentes.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getTitle(),
				is("Relatório de Conferência de Cadastro de Correspondentes"));
	}

	public void acessarFavoritoRelatorioEmpresaCorrespondentes() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relEmpresasCorrespondentes,
				"Empresas Correspondentes"));

		menuElementMap.relEmpresasCorrespondentes.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getTitle(),
				is("Relatório de Conferência de Empresas Correspondentes"));
	}

	public void acessarFavoritosRelatorioMargens() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relatorioMargens, "Margens"));

		menuElementMap.relatorioMargens.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getTitle(),
				is("Relatório de Conferência de Cadastro de Margens"));
	}

	public void acessarItemMenuOrgao() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoOrgao, "Órgãos"));

		menuElementMap.manutencaoOrgao.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosRelatorioConfCadOrgao() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relCadOrgao, "Órgãos"));

		menuElementMap.relCadOrgao.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório de Conferência de Cadastro de Órgãos"));
	}

	public void acessarFavoritosUsuarios() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relUsuarios, "Usuários"));

		menuElementMap.relUsuarios.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosInformarRescisao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.informarRescisaoFavoritos.getText(),
				is("Informar rescisão"));

		js.executeScript("arguments[0].click()", menuElementMap.informarRescisaoFavoritos);

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosReservarMargem() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.reservarMargemFavorito.getText(),
				is("Reservar Margem"));

		menuElementMap.reservarMargemFavorito.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosConfirmarReserva() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.confirmarReserva.getText(),
				is("Confirmar Reserva"));

		menuElementMap.confirmarReserva.click();

		await.until(() -> webDriver.getTitle(), not("eConsig - Principal"));
	}

	public void acessarFavoritosIncluirConsignacao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.incluirConsignacaoFavorito.getText(),
				is("Incluir Consignação"));

		menuElementMap.incluirConsignacaoFavorito.click();
	}

	public void acessarFavoritosSuporte() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.manutencaoSuporteFavoritos.getText(),
				is("Usuários Suporte"));

		menuElementMap.manutencaoSuporteFavoritos.click();
	}

	public void acessarFavoritosConsultarConsignacao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(
				() -> menuElementMap.manutencaoConsultarConsignacaoFavoritos.getText(), is("Consultar Consignação"));

		menuElementMap.manutencaoConsultarConsignacaoFavoritos.click();
	}

	public void acessarItemMenuAlongarConsignacao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.alongarConsignacao.getText(),
				is("Alongar Contrato"));

		menuElementMap.alongarConsignacao.click();
	}

	public void acessarItemMenuAlterarContrato() {
		waitDriver
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.alterarContrato, "Alterar Contrato"));

		menuElementMap.alterarContrato.click();
	}

	public void acessarItemMenuAlterarMultiplosContratos() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.alterarMultiplosContratos,
				"Alterar Múltiplos Contratos"));

		menuElementMap.alterarMultiplosContratos.click();
	}

	public void acessarFavoritosRelatorioVerbas() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relVerbas, "Verbas"));

		menuElementMap.relVerbas.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Conferência de Cadastro de Verbas"));
	}

	public void acessarItemMenuRelAcompPortMargemConsignavel() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relAcompPortMargemConsignavel,
				"Acomp. Portabilidade de Margem Consignável"));

		menuElementMap.relAcompPortMargemConsignavel.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource()
				.contains("Relatório de Acompanhamento de Portabilidade de Margem Consignável"));
	}

	public void acessarItemMenuRelAltValorRetorno() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relAltValorRetorno,
				"Alteração Valor no Retorno"));

		menuElementMap.relAltValorRetorno.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Alteração de Valor no Retorno"));
	}

	public void acessarFavoritosReimplantarCapitalDevido() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.reimplantarCapitalDevido,
				"Reimplantar Capital Devido"));

		menuElementMap.reimplantarCapitalDevido.click();

		waitDriver.until(ExpectedConditions.titleIs("Reimplantar Capital Devido"));
	}

	public void acessarItemMenuRelAuditoria() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.auditoria, "Auditoria"));

		menuElementMap.auditoria.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório de Auditoria"));
	}

	public void acessarItemMenuRelAumentoValorAcimaLimite() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relAumentoValorAcimaLimite,
				"Aumento de Valor Acima do Limite"));

		menuElementMap.relAumentoValorAcimaLimite.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Aumento de Valor Acima do Limite"));
	}

	public void acessarItemMenuRelatorioBloqueioServidores() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.relBloqueioServidores,
				"Bloqueio de Servidores"));

		menuElementMap.relBloqueioServidores.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Bloqueio de Servidores"));
	}

	public void acessarFavoritosRelatorioComprometimentoMargem() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relComprometimentoMargem.getText(),
				is("Comprometimento de Margem"));

		menuElementMap.relComprometimentoMargem.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório de Comprometimento de Margem"));
	}

	public void acessarFavoritosRelatorioConsignacoes() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioConsignacao.getText(),
				is("Consignações"));

		menuElementMap.relatorioConsignacao.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Consignações"));
	}

	public void acessarFavoritosRelatorioOcorrenciaConsignacoes() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioOcorrenciaConsignacao.getText(),
				is("Ocorrência de Consignação"));

		menuElementMap.relatorioOcorrenciaConsignacao.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Ocorrência de Consignação"));
	}

	public void acessarFavoritosRelatorioContratoLiquidadoPosCorte() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioContratoLiquidadoPosCorte.getText(),
				containsString("Contrato Liquidado"));

		menuElementMap.relatorioContratoLiquidadoPosCorte.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatório de Contrato Liquidado Pós-Corte"));
	}

	public void acessarFavoritosRelatorioEstatisticoRetorno() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioEstatisticoRetorno.getText(),
				containsString("Estatístico"));

		menuElementMap.relatorioEstatisticoRetorno.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Relatórios disponíveis para download"));
	}

	public void acessarFavoritosRelatorioEstatistico() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioEstatistico.getText(),
				containsString("Estatístico"));

		menuElementMap.relatorioEstatistico.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório Estatístico"));
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource().contains("Relatório Estatístico"));
	}

	public void acessarFavoritosRelatorioGerencialGeral() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioGerencialGeral.getText(),
				containsString("Gerencial"));

		menuElementMap.relatorioGerencialGeral.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório Gerencial Geral"));
	}

	public void acessarFavoritosRelatorioGerencialPercentualCarteira() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioGerencialPercentualCarteira.getText(),
				containsString("Gerencial Percentual"));

		menuElementMap.relatorioGerencialPercentualCarteira.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório Gerencial de Percentual de Carteira"));
	}

	public void acessarFavoritosRelatorioInformacaoBancariaDivergente() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioInformacaoBancariaDivergente.getText(),
				containsString("Inform. Bancária"));

		menuElementMap.relatorioInformacaoBancariaDivergente.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource()
				.contains("Relatório de Contratos com Informação Bancária Divergente da Conta Salário"));
	}

	public void acessarFavoritosRelatorioIntegracaoConsignatarias() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioIntegracaoConsignatarias.getText(),
				containsString("Integração"));

		menuElementMap.relatorioIntegracaoConsignatarias.click();

		await.until(() -> webDriver.getPageSource().contains("Relatórios disponíveis para download"));
	}

	public void acessarFavoritosRelatorioIntegracao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioIntegracao.getText(),
				containsString("Integração"));

		menuElementMap.relatorioIntegracao.click();

		await.until(() -> webDriver.getPageSource().contains("Relatórios disponíveis para download"));
	}

	public void acessarFavoritosRelatorioLimiteContratoEntidade() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioLimiteContratoEntidade.getText(),
				containsString("Limite de Contrato por"));

		menuElementMap.relatorioLimiteContratoEntidade.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Relatório de Limite de Contrato por Entidade"));
	}

	public void acessarFavoritosRelatorioLimiteContratoGrupoServico() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioLimiteContratoGrupoServico.getText(),
				containsString("Limite de Contrato por Grupo de Serviço"));

		menuElementMap.relatorioLimiteContratoGrupoServico.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Relatório de Limite de Contrato por Grupo de Serviço"));
	}

	public void acessarfavoritosRelatorioMovimentoFinanceiroDoServidor() {
		await.pollDelay(1, TimeUnit.SECONDS).until(
				() -> menuElementMap.relatorioMovimentoFinanceiroDoServidor.getText(),
				containsString("Movimento Financeiro do Servidor"));
		menuElementMap.relatorioMovimentoFinanceiroDoServidor.click();

		waitDriver.until(ExpectedConditions.titleIs("Relatório de Movimento Financeiro do Servidor"));
	}

	public void acessarFavoritosPosto() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.listaPostosFavoritos.getText(), is("Posto"));

		menuElementMap.listaPostosFavoritos.click();

		await.until(() -> webDriver.getPageSource(), containsString("Lista de postos"));
	}

	public void acessarItemMenuBeneficio() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.manutencaoBeneficio, "Benefícios"));

		menuElementMap.manutencaoBeneficio.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Lista de benefícios"));
	}

	public void acessarFavoritosCalculoBeneficio() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.manutencaoCalculoBeneficioFavoritos.getText(),
				is("Cálculo de Benefícios"));

		menuElementMap.manutencaoCalculoBeneficioFavoritos.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Lista de cálculo de benefícios"));
	}

	public void acessarFavoritosListarContratosPendentes() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.listaContratosPendentesFavoritos.getText(),
				is("Listar Contratos Pendentes"));

		menuElementMap.listaContratosPendentesFavoritos.click();

		await.until(() -> webDriver.getPageSource(), containsString("Listar Contratos Pendentes"));
	}

	public void acessarItemMenuConsultarContratoBeneficios() {
		waitDriver
				.until(ExpectedConditions.textToBePresentInElement(menuElementMap.beneficioConsultarContratosBeneficio,
						"Consultar contrato de plano de saúde e odontológico"));

		menuElementMap.beneficioConsultarContratosBeneficio.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Consultar Contrato de Benefício"));
	}

	public void acessarFavoritosSimularPlano() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.simularPlanodeSaudeFavoritos.getText(),
				is("Simular plano de saúde e odontológico"));

		menuElementMap.simularPlanodeSaudeFavoritos.click();

		await.until(() -> webDriver.getPageSource(), containsString("Simular plano de saúde e odontológico"));
	}

	public void acessarFavoritosSimularConsignacao() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.SimularConsignacao.getText(),
				is("Simular Consignação"));

		menuElementMap.SimularConsignacao.click();

		await.until(() -> webDriver.getPageSource(), containsString("Simular Consignação"));

	}

	public void acessarFavoritosSimularAlteracaoPlano() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.simularAlteracaodePlanoFavoritos.getText(),
				is("Simular alteração de plano"));

		menuElementMap.simularAlteracaodePlanoFavoritos.click();

		await.until(() -> webDriver.getPageSource(), containsString("Simular alteração de plano"));
	}

	public void acessarItemMenuIncluirBeneficiario() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(
				menuElementMap.beneficioIncluirBeneficiarioPlanoVigente, "Incluir beneficiário em plano vigente"));

		menuElementMap.beneficioIncluirBeneficiarioPlanoVigente.click();

		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Simulação Inclusão de Benefícios em plano vigente"));
	}

	public void acessarFavoritosFaturamentoBeneficios() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.faturamentoBeneficiosFavoritos.getText(),
				containsString("Faturamento de Benefícios"));

		menuElementMap.faturamentoBeneficiosFavoritos.click();

		await.until(() -> webDriver.getPageSource(), containsString("Nome da operadora"));
	}

	public void acessarFavoritosRelatorioComissionamentoAgAnalitico() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioComissionamentoAgAnalitico.getText(),
				containsString("Comissionamento e Agenciamento Analítico para Operadoras"));

		menuElementMap.relatorioComissionamentoAgAnalitico.click();

		await.pollDelay(2, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Relatório de Comissionamento e Agenciamento Analítico"));
	}

	public void acessarFavoritosRelatorioBeneficiarioDataNascimento() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioBeneficiarioDataNascimento.getText(),
				containsString("Beneficiário por Data"));

		menuElementMap.relatorioBeneficiarioDataNascimento.click();

		await.until(() -> webDriver.getPageSource(), containsString("Relatório Beneficiário por Data Nascimento"));
	}

	public void acessarFavoritosRelatorioExclusaoBeneficiariosPorPeriodo() {
		await.pollDelay(1, TimeUnit.SECONDS).until(
				() -> menuElementMap.relatorioExclusaoBeneficiariosPorPeriodo.getText(),
				containsString("Exclusão de Benefíciarios por Período"));

		menuElementMap.relatorioExclusaoBeneficiariosPorPeriodo.click();

		await.until(() -> webDriver.getPageSource(),
				containsString("Relatório de Exclusão de Beneficiários por Período"));
	}

	public void acessarFavoritosRelatorioContratosBeneficios() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.relatorioContratosBeneficios.getText(),
				containsString("Contratos de Benefícios"));

		menuElementMap.relatorioContratosBeneficios.click();

		await.until(() -> webDriver.getPageSource(), containsString("Relatório de Contratos de Benefícios"));
	}

	public void acessarFavoritosReativarContratoBeneficio() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.reativarContratoBeneficioFavoritos.getText(),
				containsString("Reativar Contrato"));

		menuElementMap.reativarContratoBeneficioFavoritos.click();
	}

	public void acessarFavoritosServicos() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.servicosFavoritos.getText(),
				containsString("Serviços"));

		menuElementMap.servicosFavoritos.click();
	}

	public void acessarFavoritosUsuariosServidores() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.usuariosServidoresFavoritos.getText(),
				containsString("Usuários Servidores"));

		menuElementMap.usuariosServidoresFavoritos.click();
	}

	public void acessarFavoritosPerfisSuporte() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> menuElementMap.perfisSuporteFavoritos.getText(),
				containsString("Perfis Suporte"));

		menuElementMap.perfisSuporteFavoritos.click();

	}

	public void acessarItemMenuOperacionalAcompPortMargemConsignavel() {
		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(menuElementMap.opreacionalAcompanharPortabilidadeDeMargem,
						"Acompanhar Portabilidade de Margem Consignável"));

		menuElementMap.opreacionalAcompanharPortabilidadeDeMargem.click();
	}

	public void acessarItemMenuRescisaoReterVerbaRescisoria() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(menuElementMap.reterVerbaRescisoria,
				"Reter Verba Rescisória"));

		menuElementMap.reterVerbaRescisoria.click();
	}
}
