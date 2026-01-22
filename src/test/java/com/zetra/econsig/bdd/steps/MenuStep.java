package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MenuStep {

    private MenuPage menuPage;
    private AcoesUsuarioPage acoesUsuarioPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
    }

	@Quando("acessar menu Manutencao > Consignante")
	public void acessarMenuManutencaoConsignante() throws Throwable {
		log.info("Quando acessar menu Manutenção > Consignante");

		menuPage.acessarMenuManutencao();
		menuPage.acessarFavoritosConsignante();
	}

	@Quando("acessar menu Favoritos > Consignante")
	public void acessarMenuFavoritosConsignante() throws Throwable {
		log.info("Quando acessar menu Favoritos > Consignante");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsignante();
	}

	@Quando("acessar menu Manutencao > Consignantarias")
	public void acessarMenuManutencaoConsignatarias() throws Throwable {
		log.info("Quando acessar menu Manutenção > Consignantárias");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsignatarias();
	}

	@Quando("acessar menu Manutencao > Orgaos")
	public void acessarMenuManutencaoOrgaos() throws Throwable {
		log.info("Quando acessar menu Manutenção > Órgãos");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosOrgaos();
	}

	@Quando("acessar menu Manutencao > Consignantaria")
	public void acessarMenuManutencaoConsignataria() throws Throwable {
		log.info("Quando acessar menu Manutenção > Consignantária");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuConsignataria();
	}

	@Quando("acessar menu Manutencao > Consignatarias {string} > Correspondente")
	public void acessarMenuManutencaoCorrespondente(String codigoCsa) throws Throwable {
		log.info("Quando acessar menu Manutenção > Consignatárias {} > Correspondente", codigoCsa);

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias(codigoCsa);
		acoesUsuarioPage.clicarListarUsuariosCor();
	}

	@Quando("acessar menu Manutencao > Correspondentes")
	public void acessarMenuManutencaoCorrespondenteComCSA() throws Throwable {
		log.info("Quando acessar menu Manutenção > Correspondentes");

		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondentes();
	}

	@Quando("acessar menu Manutencao > Usuario Servidor")
	public void acessarMenuManutencaoUsuarioServidor() throws Throwable {
		log.info("Quando acessar menu Manutenção > Usuário Servidor");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosUsuariosServidores();
	}

	@Quando("acessar menu Manutencao > Correspondente")
	public void acessarMenuManutencaoCorrespondenteComCOR() throws Throwable {
		log.info("Quando acessar menu Manutenção > Correspondente");

		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
	}

	@Quando("acessar menu Sistema > Perfis Suporte")
	public void acessarMenuSistemaPerfisSuporte() throws Throwable {
		log.info("Quando acessar menu Sistema > Perfis Suporte");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosPerfisSuporte();
	}

	@Quando("acessar menu Manutencao > Servicos")
	public void acessarMenuFavoritosServicos() throws Throwable {
		log.info("Quando acessar menu Manutenção > Serviços");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosServicos();
	}

	@Quando("acessar menu Manutencao > Beneficiarios")
	public void acessarMenuManutencaoBeneficiario() throws Throwable {
		log.info("Quando acessar menu Manutenção > Beneficiarios");

		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuBeneficiario();
	}

	@Quando("acessar menu Manutencao > Beneficios")
	public void acessarMenuManutencaoBeneficio() throws Throwable {
		log.info("Quando acessar menu Manutenção > Beneficios");

		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuBeneficio();
	}

	@Quando("acessar menu Beneficios > Calculo de Beneficios")
	public void acessarMenuManutencaoCalculoBeneficio() throws Throwable {
		log.info("Quando acessar menu Manutenção > Beneficios");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosCalculoBeneficio();
	}

	@E("Usuario navega para Pagina PesquisarServidor via menu ReservarMargem")
	public void usuCSASelectReservarMargem() throws Throwable {
		log.info("E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();
	}

	@E("Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva")
	public void usuSelectConfirmarReserva() throws Throwable {
		log.info("E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConfirmarReserva();
	}

	@E("Usuario navega para Pagina PesquisarServidor via menu Consultar Consignacao")
	public void usuConsultaConsignacao() throws Throwable {
		log.info("Usuario navega para Pagina PesquisarServidor via menu Consultar Consignacao");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();
	}

	@Quando("acessar menu Beneficios > Listar Contratos Pendentes")
	public void acessarFavoritosListarContratosPendentes() throws Throwable {
		log.info("Quando acessar menu Beneficios > Listar Contratos Pendentes");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosListarContratosPendentes();
	}

	@Quando("acessar o menu Beneficios > Simular plano de saude e odontologico")
	public void acessar_menu_beneficios_simular() throws Throwable {
		log.info("Quando acessar o menu Beneficios > Simular plano de saúde e odontológico");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSimularPlano();
	}

	@Quando("acessar menu Beneficios > Incluir beneficiario em plano vigente")
	public void acessarMenuIncluirBeneficiarioPlanoVigente() throws Throwable {
		log.info("Quando acessar menu Beneficios > Consultar Contrato de Plano de Saúde e Odontológico");

		menuPage.acessarMenuBeneficios();
		menuPage.acessarItemMenuIncluirBeneficiario();
	}

	@Quando("acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico")
	public void acessarMenuConsultarContrato() throws Throwable {
		log.info("Quando acessar menu Beneficios > Consultar Contrato de Plano de Saúde e Odontológico");

		menuPage.acessarMenuBeneficios();
		menuPage.acessarItemMenuConsultarContratoBeneficios();
	}

	@Quando("acessar menu Manutencao > Faturamento de Beneficios")
	public void acessarFavoritosfaturamentoBeneficios() throws Throwable {
		log.info("Quando acessar menu Manutenção > Faturamento de Benefícios");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosFaturamentoBeneficios();
	}

	@Quando("acessar menu Relatorios > Comissionamento e Agenciamento Analitico")
	public void acessarFavoritosRelatorioComissionamentoAgAnalitico() throws Throwable {
		log.info("Quando acessar menu Relatórios > Comissionamento e Agenciamento Analítico");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioComissionamentoAgAnalitico();
	}

	@Quando("acessar menu Relatorios > Beneficiario por Data Nascimento")
	public void acessarFavoritosRelatorioBeneficiarioDataNascimento() throws Throwable {
		log.info("Quando acessar menu Relatórios > Beneficiário por Data Nascimento");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioBeneficiarioDataNascimento();
	}

	@Quando("acessar menu Relatorios > Exclusao de Beneficiarios por Periodo")
	public void acessarFavoritosRelatorioExclusaoBeneficiarios() throws Throwable {
		log.info("Quando acessar menu Relatórios > Concessão e Beneficiários");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioExclusaoBeneficiariosPorPeriodo();
	}

	@Quando("acessar menu Relatorios > Contratos de Beneficios")
	public void acessarFavoritosRelatorioContratosBeneficios() throws Throwable {
		log.info("Quando acessar menu Relatórios > Contratos de Benefícios");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioContratosBeneficios();
	}

	@Quando("acessar menu Beneficios > Reativar Contrato Beneficio")
	public void acessarFavoritosReativarContratoBeneficio() throws Throwable {
		log.info("Quando acessar menu Beneficios > Reativar Contrato Benefício");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReativarContratoBeneficio();
	}

	@Quando("acessar o menu Beneficios > Simular alteracao de plano")
	public void acessar_menu_beneficios_simular_alteracao() throws Throwable {
		log.info("Quando acessar o menu Beneficios > Simular alteração de plano");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSimularAlteracaoPlano();
	}

	@Quando("acessar menu Manutencao > Posto")
	public void acessar_menu_manutencao_consignante() throws Throwable {
		log.info("Quando acessar menu Manutenção > Posto");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosPosto();
	}

	@Quando("acessar menu Rescisão > Reter Verba Rescisória")
	public void acessar_menu_rescisao_reter_verba_rescisoria() throws Throwable {
		log.info("Quando acessar menu Rescisão > Reter Verba Rescisória");

		menuPage.acessarMenuRescisao();
		menuPage.acessarItemMenuRescisaoReterVerbaRescisoria();
	}
}
