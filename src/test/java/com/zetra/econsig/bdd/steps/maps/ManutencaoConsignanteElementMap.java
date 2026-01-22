package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ManutencaoConsignanteElementMap {
	
	@FindBy(id = "CSE_RESPONSAVEL")
	public WebElement cseResponsavel;
	
	@FindBy(id = "CSE_RESP_CARGO")
	public WebElement cargoCse;
	
	@FindBy(id = "CSE_CEP")
	public WebElement cep;
	
	@FindBy(partialLinkText = "Bloquear consignante")
	public WebElement bloquear;
	
	@FindBy(partialLinkText = "Desbloquear consignante")
	public WebElement desbloquear;

	@FindBy(partialLinkText = "Alterar configurações de margem")
	public WebElement configuracoesMargem;
	
	@FindBy(partialLinkText = "Alterar os parametros")
	public WebElement alterarParametros;
	
	@FindBy(partialLinkText = "Configurar auditoria")
	public WebElement configurarAuditoria;
		
	@FindBy(id = "motivo")
	public WebElement motivo;
	
	@FindBy(linkText = "Confirmar")
	public WebElement confirmar;
	
	@FindBy(id = "modalTitulo")
	public WebElement modalTitulo;	
	
	@FindBy(id = "mar_exibe_org_1")
	public WebElement exibeOrgaoMargem1;	
	
	@FindBy(id = "mar_exibe_cor_1")
	public WebElement exibeCorMargem1;	
	
	@FindBy(id = "mar_exibe_sup_1")
	public WebElement exibeSupMargem1;	
	
	@FindBy(id = "mar_porcentagem_2")
	public WebElement porcentagemMargem2;	
	
	@FindBy(id = "mar_exibe_ser_2")
	public WebElement exibeSerMargem2;	
	
	@FindBy(id = "mar_exibe_csa_3")
	public WebElement exibeCsaMargem3;	
	
	@FindBy(id = "mar_descricao_3")
	public WebElement nomeMargem3;

	@FindBy(name = "btnSalvar")
	public WebElement salvarConfiguracoes;	
	
	@FindBy(css = "#exibeMargemCard > dl > dt:nth-child(3)")
	public WebElement card2NomeMargem;
		
	@FindBy(id = "checkGrupo193")
	public WebElement todosGeral;

	@FindBy(id = "checkGrupo400")
	public WebElement todosOperacional;	
	
	@FindBy(id = "funcao280")
	public WebElement funcaoAtualizarProcessoPortabilidade;

	@FindBy(id = "funcao81")
	public WebElement funcaoConfirmarSolicitacao;	

	@FindBy(id = "funcao274")
	public WebElement funcaoEditarPostos;	
	
	@FindBy(id = "checkGrupo199")
	public WebElement todosManutencaoServidor;

	@FindBy(id = "checkGrupo123")
	public WebElement todosManutencaoServicos;
	
	@FindBy(id = "checkGrupo202")
	public WebElement todosAdministracaoCse;
	
	@FindBy(id = "checkGrupo259")
	public WebElement todosIntegracaoFolha;
	
	@FindBy(id = "303")
	public WebElement prazoExpiracaoSenhaServidor;	
	
	@FindBy(id = "198")
	public WebElement quantidadeMensagensExibidasAposLogin;

	@FindBy(id = "264Sim")
	public WebElement exigeCertificadoDigitalParaConsignataria;
	
	@FindBy(id = "266Sim")
	public WebElement exigeCertificadoDigitalParaConsignante;
	
	@FindBy(id = "funcao370")
	public WebElement funcaoRelatorioComunicacao;
	
	@FindBy(id = "checkGrupo479")
	public WebElement todosManutencaoOrgao;
	
	@FindBy(id = "funcao142")
	public WebElement funcaoCriarGrupoConsignataria;
	
	@FindBy(id = "checkGrupo204")
	public WebElement todosAdministracao;

	@FindBy(id = "225Sim")
	public WebElement verificarCadastroEnderecoAcesso;	
}
