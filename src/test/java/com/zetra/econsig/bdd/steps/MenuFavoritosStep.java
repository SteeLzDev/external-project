package com.zetra.econsig.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.values.ItemMenuEnum;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MenuFavoritosStep {

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Before
	public void setUp() {
		itemMenuFavoritoService.excluirItemMenuFavoritos();
	}

	@Dado("que tenha incluido o item de menu Calculo de Beneficios no favoritos para usuario Suporte")
	public void incluirFavoritosCalculoDeBeneficios() {
		log.info("Dado que tenha incluido o item de menu Cálculo de Benefícios no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CALCULO_BENEFICIOS.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Perfil Suporte no favoritos para usuario suporte")
	public void incluirFavoritosPerfilSuporte() {
		log.info("Dado que tenha incluido o item de menu Perfil Suporte no favoritos para usuário suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.PERFIS_SUPORTE.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Listar Contratos Pendentes no favoritos para usuario Suporte")
	public void incluirFavoritosListarContratosPendentes() {
		log.info("Dado que tenha incluido o item de menu Listar Contratos Pendentes no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.LISTAR_CONTRATOS_PENDENTES_BENEFICIO.getCodigo()));

	}

	@Dado("que tenha incluido o item de menu Faturamento de Beneficios no favoritos para usuario Suporte")
	public void incluirFavoritosFaturamentoBeneficios() {
		log.info("Dado que tenha incluido o item de menu Faturamento de Benefícios no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTAR_FATURAMENTO_BENEFICIOS.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Posto no favoritos para usuario CSE")
	public void incluirFavoritosPosto() {
		log.info("Dado que tenha incluido o item de menu Posto no favoritos para usuário CSE");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTA_POSTO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Reativar Contrato Beneficio no favoritos para usuario Suporte")
	public void incluirFavoritosReativarContratoBeneficio() {
		log.info(
				"Dado que tenha incluido o item de menu Reativar Contrato Benefício no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.REATIVAR_CONTRATO_BENEFICIO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Comissionamento e Agenciamento Analitico no favoritos para usuario Suporte")
	public void incluirFavoritosRelComissionamentoAgenciamentoAnalitico() {
		log.info(
				"Dado que tenha incluido o item de menu Comissionamento e Agenciamento Analítico no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_COMISSIONAMENTO_AGENCIAMENTO_ANALITICO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Beneficiario por Data Nascimento no favoritos para usuario Suporte")
	public void incluirFavoritosRelBeneficiarioDataNascimento() {
		log.info(
				"Dado que tenha incluido o item de menu Beneficiário por Data Nascimento no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_BENEFICIARIO_DATA_NASCIMENTO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Exclusao de Beneficiarios por Periodo no favoritos para usuario Suporte")
	public void incluirFavoritosRelExclusaoBeneficiariosPorPeriodo() {
		log.info(
				"Dado que tenha incluido o item de menu Exclusão de Beneficiários por Período no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_EXCLUSAO_BENEFICIARIO_PERIODO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Contratos de Beneficios no favoritos para usuario Suporte")
	public void incluirFavoritosRelContratosBeneficios() {
		log.info("Dado que tenha incluido o item de menu Contratos de Benefícios no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_CONTRATOS_BENEFICIOS.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Reservar Margem no favoritos para usuario {string}")
	public void incluirFavoritosReservarMargem(String usuario) {
		log.info("Dado que tenha incluido o item de menu Reservar Margem no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
		} else if (usuario.contains("csa")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.csa1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
		} else if (usuario.contains("cor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cor1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));	
		}
		
	}

	@Dado("que tenha incluido o item de menu Simular alteracao de plano no favoritos para usuario Suporte")
	public void incluirFavoritosSimularAlteracaoPlano() {
		log.info("Dado que tenha incluido o item de menu Simular alteração de plano no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.SIMULACAO_ALTERACAO_BENEFICIOS.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Simular plano de saude e odontologico no favoritos para usuario Suporte")
	public void incluirFavoritosSimularPlanoSaude() {
		log.info(
				"Dado que tenha incluido o item de menu Simular plano de saúde e odontológico no favoritos para usuário Suporte");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.SIMULACAO_BENEFICIOS.getCodigo()));
	}
	
	@Dado("que tenha incluido o item de menu Simular Consignacao nos favoritos para o usuario servidor")
	public void incluirFavoritosSimularConsignacao() { 
		log.info("Dado que tenha incluido o item de menu Simular Consignacao nos favoritos para o usuario servidor");
		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.servidor1.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.SIMULAR_CONSIGNACAO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Servicos no favoritos para usuario {string}")
	public void incluirFavoritosServicos(String usuario) {
		log.info("Dado que tenha incluido o item de menu Serviços no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.SERVICOS.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.SERVICOS.getCodigo()));
		}
	}


	@Dado("que tenha incluido o item de menu relatorio movimento financeiro do servidor nos favoritos para o usuario csa")
	public void incluirFavoritosRelatorioMovimentoFinanceiroDoServidor() {
		log.info("E que tenha incluido o item de menu relatorio movimento financeiro do servidor nos favoritos para o usuario csa");
		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RELATORIO_MOVIMENTO_FINANCEIRO_SERVIDOR.getCodigo()));
	}
	
	@Dado("que tenha incluido o item de menu Consignante no favoritos para usuario {string}")
	public void incluirFavoritosConsignante(String usuario) {
		log.info("Dado que tenha incluido o item de menu Consignante no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.CONSIGNANTE.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.CONSIGNANTE.getCodigo()));
		}
	}

	@Dado("que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA")
	public void incluirFavoritosConsignataria() {
		log.info("Dado que tenha incluido o item de menu Consignantária no favoritos para usuário CSA");

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.csa1.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSIGNATARIA.getCodigo()));
		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSIGNATARIA.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Consignantarias no favoritos para usuario {string}")
	public void incluirFavoritosConsignantarias(String usuario) {
		log.info("Dado que tenha incluido o item de menu Consignantárias no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.CONSIGNATARIAS.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.CONSIGNATARIAS.getCodigo()));
		}
	}

	@Dado("que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario {string}")
	public void incluirFavoritosUsuarioServidor(String usuario) {
		log.info("Dado que tenha incluido o item de menu Usuários Servidores no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.USUARIOS_SERVIDORES.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.USUARIOS_SERVIDORES.getCodigo()));
		}
	}


	@Dado("que tenha incluido o item de menu Informar rescisao no favoritos")
	public void incluirFavoritosInformarRescisao() {
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.INFORMAR_RESCISAO.getCodigo()));
	}

	@Dado("que tenha incluido o item de menu Orgaos no favoritos para usuario {string}")
	public void incluirFavoritosOrgaos(String usuario) {
		log.info("Dado que tenha incluido o item de menu Órgãos no favoritos para usuário {}", usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.ORGAOS.getCodigo()));
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					Integer.toString(ItemMenuEnum.ORGAOS.getCodigo()));
		}
	}

	@Dado("que tenha incluido o item de menu {string} no favoritos para usuário {string}")
	public void incluirFavoritosMenuUsuario(String itemCodigo, String usuario) {
		log.info("Dado que tenha incluido o item de menu {} no favoritos para usuário {}", itemCodigo, usuario);

		if (usuario.contains("cse")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cse1.getLogin()).getUsuCodigo(),
					itemCodigo);
		} else if (usuario.contains("csa")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.csa1.getLogin()).getUsuCodigo(),
					itemCodigo);
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(),
					itemCodigo);
		} else if (usuario.contains("cor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.cor1.getLogin()).getUsuCodigo(),
					itemCodigo);
		} else if (usuario.contains("zetra_igor")) {
			itemMenuFavoritoService.incluirItemMenuFavorito(
					usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
					itemCodigo);
		}
	}
}
