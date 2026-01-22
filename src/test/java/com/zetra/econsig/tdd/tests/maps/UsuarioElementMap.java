package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UsuarioElementMap {

    @FindBy(id = "acoes")
    public WebElement maisAcoes;

    @FindBy(partialLinkText = "Opções")
    public WebElement opcoes;

    @FindBy(linkText = "Listar usuários")
    public WebElement listarUsuarios;

    @FindBy(linkText = "Criar novo usuário")
    public WebElement criarNovoUsuario;

    @FindBy(linkText = "Criar novo órgão")
    public WebElement criarNovoOrgao;

    @FindBy(linkText = "Corresp.")
    public WebElement correspondente;

    @FindBy(partialLinkText = "Cancelar")
    public WebElement cancelar;

    @FindBy(className = "novaSenha")
    public WebElement novaSenha;

    @FindBy(id = "usuNome")
    public WebElement nomeUsuario;

    @FindBy(id = "usuLogin")
    public WebElement usuarioLogin;

    @FindBy(id = "usuEmail")
    public WebElement email;

    @FindBy(id = "iDicaSenha")
    public WebElement dicaSenha;

    @FindBy(id = "usuCpf")
    public WebElement cpf;

    @FindBy(id = "usuTel")
    public WebElement telefone;

    @FindBy(id = "novoIp")
    public WebElement ipAcessoAtual;

    @FindBy(id = "listaIps")
    public WebElement listaIps;

    @FindBy(linkText = "Usar ip atual")
    public WebElement btnIPsAcessoAtual;

    @FindBy(css = ".row:nth-child(5) .btn:nth-child(1) > svg")
    public WebElement btnIncluirIPsAcesso;

    @FindBy(xpath = "//a[@onclick=\"insereIp('numero_ip', 'novoIp','listaIps','false'); return false;\"]")
    public WebElement btnIncluirIPsAcessoCsaCaso1;

    @FindBy(xpath = "//a[@onclick=\"insereIp('numero_ip', 'novoIp','listaIps','false'); return false;\"]")
    public WebElement btnIncluirIPsAcessoCsaCaso2;

    @FindBy(css = "div.form-group.col-sm-1.mt-4 > a:nth-child(1)")
    public WebElement btnIncluirIPsAcessoCse;

    @FindBy(css = "#no-back > div.main > div > form > div.card > div.card-body > div:nth-child(1) > div.form-group.col-sm-1.mt-4 > a:nth-child(1) > svg")
    public WebElement btnIncluirIPsAcessoRestricaoFuncao;

    @FindBy(id = "novoDDNS")
    public WebElement enderecoAcesso;

    @FindBy(css = ".row:nth-child(6) .btn:nth-child(1)")
    public WebElement btnIncluirEnderecoAcesso;

    @FindBy(css = "#no-back > div.main > div > form > div.card > div.card-body > div:nth-child(2) > div.form-group.col-sm-1.mt-4 > a:nth-child(1) > svg")
    public WebElement btnIncluirEnderecoAcessoRestricaoFuncao;

    @FindBy(id = "montaFuncoesPerfil")
    public WebElement perfil;

    @FindBy(css = "button[class*='btn-primary']")
    public WebElement validar;

    @FindBy(id = "btnEnvia")
    public WebElement salvarDesfazerCancelamento;

    @FindBy(linkText = "Bloquear/Desbloquear")
    public WebElement opcaoBloquearDesbloquear;

    @FindBy(partialLinkText = "Bloquear /")
    public WebElement opcaoBloquearDesbloquearServidor;

    @FindBy(linkText = "Editar")
    public WebElement opcaoEditar;

    @FindBy(linkText = "Editar funções")
    public WebElement editarFuncoes;

    @FindBy(linkText = "Editar restrições de acesso")
    public WebElement editarRestricoesAcesso;

    @FindBy(linkText = "Histórico")
    public WebElement opcaoExibirHistorico;

    @FindBy(linkText = "Excluir")
    public WebElement opcaoExcluir;

    @FindBy(linkText = "Reinicializar senha")
    public WebElement opcaoReinicializarSenha;

    @FindBy(css = "div[class='alert alert-warning']")
    public WebElement txtMensagemAlerta;

    @FindBy(css = ".alert > .mb-0")
    public WebElement txtMensagemSucesso;

    @FindBy(id = "TMO_CODIGO")
    public WebElement motivoOperacao;

    @FindBy(id = "ADE_OBS")
    public WebElement observacao;

    @FindBy(id = "campo_tipo_motivo_obs")
    public WebElement campoTipoMotivoObs;

    @FindBy(partialLinkText = "Usuário")
    public WebElement usuarios;

    @FindBy(id = "FILTRO")
    public WebElement filtroUsuario;

    @FindBy(id = "FILTRO_TIPO")
    public WebElement filtrarPor;

    @FindBy(partialLinkText = "Pesquisar")
    public WebElement pesquisar;

    @FindBy(linkText = "Listar usuários")
    public WebElement listaUsuariosOrg;

    @FindBy(linkText = "Salvar")
    public WebElement botaoSalvar;

    @FindBy(id = "usuDataFimVig")
    public WebElement dataValido;

    @FindBy(id = "USU_CENTRALIZADOR_SIM")
    public WebElement usuarioCentralizadorSim;

    @FindBy(id = "USU_CENTRALIZADOR_NAO")
    public WebElement usuarioCentralizadorNao;

    @FindBy(id = "USU_EXIGE_CERTIFICADO_SIM")
    public WebElement exigeCertificadoDigitalSim;

    @FindBy(id = "USU_EXIGE_CERTIFICADO_NAO")
    public WebElement exigeCertificadoDigitalNao;

    @FindBy(css = "#idMsgSuccessSession > font")
    public WebElement senhaReinicializada;

    @FindBy(id = "228")
    public WebElement alteracaoAvancadaConsignacao;

    @FindBy(id = "81")
    public WebElement confirmarSolicitacao;

    @FindBy(id = "200")
    public WebElement editarAnexosConsignacao;

    @FindBy(id = "140")
    public WebElement renegociarContratoTerceiros;

    @FindBy(id = "57")
    public WebElement reservarMargem;

    @FindBy(id = "campoFiltro")
    public WebElement filtro;

    @FindBy(id = "filtroTipo")
    public WebElement filtroTipo;

    @FindBy(id = "FILTRAR")
    public WebElement botaoFiltrar;

    @FindBy(css = "td:nth-child(1)")
    public WebElement txtFuncao;

    @FindBy(css = "td:nth-child(2)")
    public WebElement txtDescricao;

    @FindBy(id = "acoes")
    public WebElement botaoAcoes;

    @FindBy(linkText = "Serviços")
    public WebElement servicos;

    @FindBy(linkText = "Consultar convênios")
    public WebElement consultarConvenios;

    @FindBy(partialLinkText = "erfis de usuário")
    public WebElement listarPerfilUsuario;

    @FindBy(linkText = "Alterar senha")
    public WebElement opcaoAlterarSenha;

    @FindBy(partialLinkText = "Confirmar")
    public WebElement botaoConfirmar;
}
