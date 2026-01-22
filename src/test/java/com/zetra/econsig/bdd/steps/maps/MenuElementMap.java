package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MenuElementMap {

	@FindBy(partialLinkText = "Página inicial")
	public WebElement paginaInicial;

	@FindBy(linkText = "Confirmar Reserva")
	public WebElement confirmarReserva;

	@FindBy(linkText = "Manutenções")
	public WebElement manutencoes;

	@FindBy(linkText = "Operacional")
	public WebElement operacional;

	@FindBy(linkText = "Relatórios")
	public WebElement relatorios;

	@FindBy(linkText = "Sistema")
	public WebElement sistema;

	@FindBy(linkText = "Rescisão")
	public WebElement rescisao;

	@FindBy(linkText = "Favoritos")
	public WebElement favoritos;

	@FindBy(linkText = "Alterar Senha")
	public WebElement alterarSenha;

	@FindBy(linkText = "Estabelecimentos")
	public WebElement estabelecimentos;

	@FindBy(linkText = "Grupo de Serviços")
	public WebElement grupoServicos;

	@FindBy(linkText = "Servidores")
	public WebElement servidores;

	@FindBy(linkText = "Consignante")
	public WebElement manutencaoConsignante;

	@FindBy(linkText = "Consignatárias")
	public WebElement manutencaoConsignatarias;

	@FindBy(linkText = "Consignatária")
	public WebElement manutencaoConsignataria;

	@FindBy(linkText = "Benefíciários")
	public WebElement manutencaoBeneficiario;

	@FindBy(linkText = "Benefícios")
	public WebElement manutencaoBeneficio;

	@FindBy(linkText = "Órgãos")
	public WebElement manutencaoOrgao;

	@FindBy(linkText = "Usuários Suporte")
	public WebElement manutencaoSuporteFavoritos;

	@FindBy(linkText = "Consultar Consignação")
	public WebElement manutencaoConsultarConsignacaoFavoritos;

	@FindBy(linkText = "Alongar Contrato")
	public WebElement alongarConsignacao;

	@FindBy(linkText = "Simular Consignação")
	public WebElement SimularConsignacao;
	
	@FindBy(linkText = "Alterar Contrato")
	public WebElement alterarContrato;

	@FindBy(linkText = "Alterar Múltiplos Contratos")
	public WebElement alterarMultiplosContratos;

	@FindBy(linkText = "Consultar Margem")
	public WebElement consultarMargem;

	@FindBy(linkText = "Acomp. Portabilidade de Margem Consignável")
	public WebElement relAcompPortMargemConsignavel;

	@FindBy(linkText = "Alteração Valor no Retorno")
	public WebElement relAltValorRetorno;

	@FindBy(linkText = "Auditoria")
	public WebElement auditoria;

	@FindBy(linkText = "Aumento de Valor Acima do Limite")
	public WebElement relAumentoValorAcimaLimite;

	@FindBy(css = "h1[class='page-title']")
	public WebElement tituloRelAumentoValorAcimaLimite;

	@FindBy(linkText = "Bloqueio de Servidores")
	public WebElement relBloqueioServidores;

	@FindBy(linkText = "Usuários")
	public WebElement relUsuarios;

	@FindBy(linkText = "Verbas")
	public WebElement relVerbas;

	@FindBy(linkText = "Reservar Margem")
	public WebElement reservarMargemFavorito;

	@FindBy(css = "#menuFavoritos > ul > li:nth-child(1) > a")
	public WebElement relCadOrgao;

	@FindBy(linkText = "Margens")
	public WebElement relatorioMargens;

	@FindBy(linkText = "Correspondentes")
	public WebElement relCorrespondentes;

	@FindBy(linkText = "Empresas Correspondentes")
	public WebElement relEmpresasCorrespondentes;

	@FindBy(linkText = "Comprometimento de Margem")
	public WebElement relComprometimentoMargem;

	@FindBy(linkText = "Consignações")
	public WebElement relatorioConsignacao;

	@FindBy(linkText = "Ocorrência de Consignação")
	public WebElement relatorioOcorrenciaConsignacao;

	@FindBy(linkText = "Contrato Liquidado Pós-Corte")
	public WebElement relatorioContratoLiquidadoPosCorte;

	@FindBy(linkText = "Estatístico Retorno")
	public WebElement relatorioEstatisticoRetorno;

	@FindBy(linkText = "Estatístico")
	public WebElement relatorioEstatistico;

	@FindBy(linkText = "Gerencial Geral")
	public WebElement relatorioGerencialGeral;

	@FindBy(linkText = "Gerencial Percentual Carteira")
	public WebElement relatorioGerencialPercentualCarteira;

	@FindBy(linkText = "Inform. Bancária Divergente")
	public WebElement relatorioInformacaoBancariaDivergente;

	@FindBy(linkText = "Integração Consignatária")
	public WebElement relatorioIntegracaoConsignatarias;

	@FindBy(linkText = "Integração")
	public WebElement relatorioIntegracao;

	@FindBy(linkText = "Limite de Contrato por Entidade")
	public WebElement relatorioLimiteContratoEntidade;

	@FindBy(linkText = "Limite de Contrato por Grupo de Serviço")
	public WebElement relatorioLimiteContratoGrupoServico;

	@FindBy(linkText = "Posto")
	public WebElement listaPostosFavoritos;

	@FindBy(linkText = "Cálculo de Benefícios")
	public WebElement manutencaoCalculoBeneficioFavoritos;
	
	@FindBy(linkText = "Movimento Financeiro do Servidor")
	public WebElement relatorioMovimentoFinanceiroDoServidor; 
	
	@FindBy(linkText = "Faturamento de Benefícios")
	public WebElement faturamentoBeneficiosFavoritos;

	@FindBy(linkText = "Reativar Contrato Benefício")
	public WebElement reativarContratoBeneficioFavoritos;

	@FindBy(linkText = "Comissionamento e Agenciamento Analítico para Operadoras")
	public WebElement relatorioComissionamentoAgAnalitico;

	@FindBy(linkText = "Beneficiário por Data Nascimento")
	public WebElement relatorioBeneficiarioDataNascimento;

	@FindBy(linkText = "Exclusão de Benefíciarios por Período")
	public WebElement relatorioExclusaoBeneficiariosPorPeriodo;

	@FindBy(linkText = "Contratos de Benefícios")
	public WebElement relatorioContratosBeneficios;

	@FindBy(linkText = "Serviços")
	public WebElement servicosFavoritos;
	
	@FindBy(partialLinkText = "Benefícios")
	public WebElement beneficios;

	@FindBy(linkText = "Listar Contratos Pendentes")
	public WebElement beneficioListarContratosPendentes;

	@FindBy(linkText = "Listar Contratos Pendentes")
	public WebElement listaContratosPendentesFavoritos;

	@FindBy(linkText = "Consultar contrato de plano de saúde e odontológico")
	public WebElement beneficioConsultarContratosBeneficio;

	@FindBy(linkText = "Simular plano de saúde e odontológico")
	public WebElement simularPlanodeSaudeFavoritos;

	@FindBy(linkText = "Simular alteração de plano")
	public WebElement simularAlteracaodePlanoFavoritos;

	@FindBy(linkText = "Incluir beneficiário em plano vigente")
	public WebElement beneficioIncluirBeneficiarioPlanoVigente;

	@FindBy(linkText = "Incluir Consignação")
	public WebElement incluirConsignacaoFavorito;

	@FindBy(partialLinkText = "Correspondente")
	public WebElement manutencaoCorrespondentes;

	@FindBy(linkText = "Perfis Suporte")
	public WebElement perfisSuporteFavoritos;

	@FindBy(linkText = "Informar rescisão")
	public WebElement informarRescisaoFavoritos;

	@FindBy(linkText = "Usuários Servidores")
	public WebElement usuariosServidoresFavoritos;

	@FindBy(linkText = "Dados cadastrais")
	public WebElement dadosCadastrais;

	@FindBy(linkText = "Solicitar empréstimo")
	public WebElement simularConsignacao;

	@FindBy(linkText = "Acompanhar Portabilidade de Margem Consignável")
	public WebElement opreacionalAcompanharPortabilidadeDeMargem;
	
	@FindBy(linkText = "Reimplantar Capital Devido")
	public WebElement reimplantarCapitalDevido;

	@FindBy(linkText = "Reter Verba Rescisória")
	public WebElement reterVerbaRescisoria;
}