package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ConsignacaoElementMap {

	@FindBy(css = ".position-relative:nth-child(1) > .dropdown-item")
	public WebElement alongarContrato;

	@FindBy(id = "acoes")
	public WebElement botaoAcoes;

	@FindBy(id = "adeVlr")
	public WebElement valorPrestacao;

	@FindBy(id = "adeVlrLiquido")
	public WebElement valorLiquido;

	@FindBy(id = "adePrazo")
	public WebElement numeroPrestacao;

	@FindBy(id = "adePrazoEdt")
	public WebElement nroPrestacaoAlterarContrato;

	@FindBy(id = "senha")
	public WebElement senhaAutorizacaoServidor;

	@FindBy(linkText = "Confirmar")
	public WebElement botaoConfirmar;

	@FindBy(linkText = "Salvar")
	public WebElement botaoSalvar;

	@FindBy(css = ".card:nth-child(1) .col-6:nth-child(10)")
	public WebElement txtValorPrestacao;

	@FindBy(css = ".card:nth-child(1) .col-6:nth-child(12)")
	public WebElement txtNumeroPrestacao;

	@FindBy(css = ".card:nth-child(1) .col-6:nth-child(28)")
	public WebElement txtValorLiquido;

	@FindBy(partialLinkText = "Alterar")
	public WebElement alterarContrato;

	@FindBy(partialLinkText = "Editar")
	public WebElement editarContrato;

	@FindBy(linkText = "Cancelar consignação")
	public WebElement cancelarConsignacao;

	@FindBy(linkText = "Desfazer cancelamento da consignação")
	public WebElement desfazerCancelamentoConsignacao;

	@FindBy(linkText = "Editar anexos da consignação")
	public WebElement editarAnexoConsignacao;

	@FindBy(id = "upload-btn-FILE1")
	public WebElement botaoAnexar;

	@FindBy(id = "AAD_DESCRICAO")
	public WebElement descricaoDoArquivo;

	@FindBy(id = "editfield")
	public WebElement descricaoDoArquivoAlterar;

	@FindBy(id = "upload-btn-FILE1")
	public WebElement selecionarArquivo;

	@FindBy(id = "FILE1")
	public WebElement file1;

	@FindBy(partialLinkText = "Opções")
	public WebElement opcoes;

	@FindBy(linkText = "Editar")
	public WebElement editarAnexo;

	@FindBy(linkText = "Bloquear")
	public WebElement bloquearAnexo;

	@FindBy(linkText = "Desbloquear")
	public WebElement desbloquearAnexo;

	@FindBy(linkText = "Liquidar consignação")
	public WebElement liquidarConsignacao;

	@FindBy(linkText = "Liquidar parcela")
	public WebElement liquidarParcela;

	@FindBy(linkText = "Desliquidar consignação")
	public WebElement desliquidarConsignacao;

	@FindBy(linkText = "Liquidar")
	public WebElement liquidar;

	@FindBy(css = "a[onclick='downloadAnexoVisualizacao(0)']")
	public WebElement downloadAnexo;

	@FindBy(css = "a[title='Remover anexo']")
	public WebElement removerAnexo;

	@FindBy(linkText = "Registrar ocorrência consignação")
	public WebElement registrarOcorrencia;

	@FindBy(id = "tocCodigo")
	public WebElement tipoOcorrencia;

	@FindBy(id = "ocaObs")
	public WebElement observacao;

	@FindBy(linkText = "Suspender consignação")
	public WebElement suspenderConsignacao;

	@FindBy(linkText = "Reativar consignação")
	public WebElement reativarConsignacao;

	@FindBy(id = "dataReativacaoAutomatica")
	public WebElement dataReativacao;

	@FindBy(linkText = "Solicitar saldo devedor para informação")
	public WebElement solicitarSaldoDevedorInformativo;

	@FindBy(linkText = "Solicitar saldo devedor para liquidação")
	public WebElement solicitarSaldoDevedorParaLiquidacao;

	@FindBy(id = "btnSolicitaSaldo")
	public WebElement botacoConfirmarSolicitarSaldo;

	@FindBy(id = "btnSolicitaSaldoLiq")
	public WebElement botacoConfirmarSolicitarSaldoLiq;

	@FindBy(linkText = "Deferir consignação")
	public WebElement deferirConsignacao;

	@FindBy(linkText = "Renegociar consignação")
	public WebElement renegociarConsignacao;

	@FindBy(partialLinkText = "Controle de renegociação - ADE")
	public WebElement adeAntigo;

	@FindBy(linkText = "Visualizar autorização de desconto")
	public WebElement visualizarAutorizacaoDesconto;

	@FindBy(linkText = "Reimplantar consignação")
	public WebElement reimplantarConsignacao;

    @FindBy(name = "obs")
	public WebElement observacaoReimplantar;

	@FindBy(id = "btnEnvia")
	public WebElement confirmarReimplantacao;

	@FindBy(css = ".modal-footer.pt-0 > div > a.btn.btn-primary")
	public WebElement confirmarModal;

}

