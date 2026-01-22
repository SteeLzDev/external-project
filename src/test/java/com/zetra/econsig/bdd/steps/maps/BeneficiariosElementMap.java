package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BeneficiariosElementMap {
	
	@FindBy(className = "btn-mais-opcoes")
	public WebElement botaoAcoes;

	@FindBy(linkText = "Novo beneficiário")
	public WebElement novoBeneficiario;
	
	@FindBy(linkText = "Editar endereços deste servidor")
	public WebElement editarEnderecoServidor;
	
	@FindBy(linkText = "Cadastrar novo endereço")
	public WebElement cadastrarNovoEndereco;
	
	@FindBy(id = "tib_codigo")
	public WebElement tipoBeneficiario;
	
	@FindBy(id = "bfcNome")
	public WebElement nome;
	
	@FindBy(id = "bfc_cpf")
	public WebElement cpf;
	
	@FindBy(name = "bfc_rg")
	public WebElement rg;
	
	@FindBy(id = "smasculino")
	public WebElement sexoMasculino;
	
	@FindBy(id = "sfeminino")
	public WebElement sexoFeminino;
	
	@FindBy(id = "BFC_DDD_TELEFONE")
	public WebElement dddTelefone;
	
	@FindBy(id = "bfc_telefone")
	public WebElement numeroTelefone;
	
	@FindBy(id = "BFC_DDD_CELULAR")
	public WebElement dddCelular;
	
	@FindBy(id = "bfc_celular")
	public WebElement numeroCelular;
	
	@FindBy(name = "bfc_nome_mae")
	public WebElement nomeMae;
	
	@FindBy(id = "grp_codigo")
	public WebElement grauParentesco;
	
	@FindBy(id = "bfc_data_nascimento")
	public WebElement dataNascimento;
	
	@FindBy(id = "nac_codigo")
	public WebElement nacionalidade;
	
	@FindBy(id = "est_cvl_codigo")
	public WebElement estadoCivil;
	
	@FindBy(id = "mde_codigo")
	public WebElement motivoDependencia;
	
	@FindBy(linkText = "Salvar")
	public WebElement salvar;
	
	@FindBy(linkText = "Cancelar")
	public WebElement cancelar;
	
	@FindBy(linkText = "Editar")
	public WebElement editar;

	@FindBy(linkText = "Excluir")
	public WebElement excluir;
	
	@FindBy(linkText = "Anexar")
	public WebElement anexar;

	@FindBy(linkText = "Download")
	public WebElement download;
	
	@FindBy(partialLinkText = "Novo anexo")
	public WebElement novoAnexo;
	
	@FindBy(id = "ens_cep")
	public WebElement cep;
	
	@FindBy(id = "tie_codigo")
	public WebElement tipoEndereco;
	
	@FindBy(id = "ens_logradouro")
	public WebElement logradouro;
	
	@FindBy(id = "ens_numero")
	public WebElement numeroEndereco;
	
	@FindBy(id = "ens_complemento")
	public WebElement complemento;
	
	@FindBy(id = "ens_bairro")
	public WebElement bairro;
	
	@FindBy(name = "ens_uf")
	public WebElement uf;
	
	@FindBy(id = "ens_municipio")
	public WebElement cidade;

	@FindBy(id = "tar_codigo")
	public WebElement tipoDocumento;

	@FindBy(id = "abf_data_validade")
	public WebElement dataValidade;

	@FindBy(name = "abf_descricao")
	public WebElement descricaoAnexo;
	
	@FindBy(name = "FILE1")
	public WebElement anexo;	
	
	@FindBy(id = "modalSimularBeneficio")
	public WebElement modalSimulacao;
}