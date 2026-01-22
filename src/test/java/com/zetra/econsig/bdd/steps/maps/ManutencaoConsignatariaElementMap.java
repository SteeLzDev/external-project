package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ManutencaoConsignatariaElementMap {

    @FindBy(id = "dataDesbloqueioAutomatica")
    public WebElement dataDesbloqueio;

    @FindBy(id = "OCC_OBS")
    public WebElement observacao;

    @FindBy(id = "tmoCodigo")
    public WebElement motivoOperacao;

    @FindBy(id = "CSA_CNPJ")
    public WebElement cnpj;

    @FindBy(id = "tpeCodigo")
    public WebElement penalizarConsignataria;

    @FindBy(id = "NCA_CODIGO")
    public WebElement natureza;

    @FindBy(id = "permiteIncluirAdeNao")
    public WebElement naoPermiteIncluirAde;

    @FindBy(id = "TGC_CODIGO")
    public WebElement grupoConsignataria;

    @FindBy(id = "CSA_NRO_CONTRATO")
    public WebElement numeroContrato;

    @FindBy(id = "CSA_EMAIL_EXPIRACAO")
    public WebElement emailExpiracao;

    @FindBy(id = "CSA_NRO_BCO")
    public WebElement banco;

    @FindBy(id = "CSA_NRO_AGE")
    public WebElement agencia;

    @FindBy(id = "CSA_NRO_CTA")
    public WebElement conta;

    @FindBy(id = "CSA_DIG_CTA")
    public WebElement digito;

    @FindBy(id = "CSA_TXT_CONTATO")
    public WebElement instrucoesContato;

    @FindBy(id = "vIPSim")
    public WebElement exigeEnderecoAcesso;

    @FindBy(id = "vIPNão")
    public WebElement naoExigeEnderecoAcesso;

    @FindBy(id = "CSA_IDENTIFICADOR_INTERNO")
    public WebElement codigoZetrasoft;

    @FindBy(id = "CSA_IDENTIFICADOR")
    public WebElement codigo;

    @FindBy(name = "CSA_UF")
    public WebElement uf;

    @FindBy(id = "TPA_10Sim")
    public WebElement exigeCertificado;

    @FindBy(linkText = "Edt. Param. consignatária")
    public WebElement editarParamConsignataria;

    @FindBy(linkText = "Penalidade")
    public WebElement penalidade;

    @FindBy(linkText = "Imprimir")
    public WebElement imprimir;

    @FindBy(linkText = "Criar nova consignatária")
    public WebElement criarNovaConsignataria;

    @FindBy(id = "CSA_NOME")
    public WebElement nome;

    @FindBy(id = "CSA_NOME_ABREV")
    public WebElement nomeAbrev;

    @FindBy(id = "CSA_DATA_EXPIRACAO")
    public WebElement dataExpiracaoContratual;

    @FindBy(id = "CSA_DATA_EXPIRACAO_CADASTRAL")
    public WebElement dataExpiracaoCadastral;

    @FindBy(id = "CSA_CNPJ_CTA")
    public WebElement cnpjDadosBancarios;

    @FindBy(id = "CSA_CONTATO")
    public WebElement contato;

    @FindBy(id = "CSA_CONTATO_TEL")
    public WebElement contatoTelefone;

    @FindBy(id = "CSA_RESPONSAVEL")
    public WebElement responsavel1;

    @FindBy(id = "CSA_RESP_CARGO")
    public WebElement cargoResponsavel1;

    @FindBy(id = "CSA_RESP_TELEFONE")
    public WebElement telefoneResponsavel1;

    @FindBy(id = "CSA_LOGRADOURO")
    public WebElement logradouro;

    @FindBy(id = "CSA_NRO")
    public WebElement numero;

    @FindBy(id = "CSA_BAIRRO")
    public WebElement bairro;

    @FindBy(id = "CSA_CIDADE")
    public WebElement cidade;

    @FindBy(id = "CSA_CEP")
    public WebElement cep;

    @FindBy(id = "CSA_ENDERECO_2")
    public WebElement enderecoAlternativo;

    @FindBy(id = "CSA_TEL")
    public WebElement telefoneContato;

    @FindBy(id = "CSA_FAX")
    public WebElement fax;

    @FindBy(id = "CSA_EMAIL")
    public WebElement email;

    @FindBy(id = "chk_751F8080808080808080808080809780")
    public WebElement orgaoCarlotaJoaquina;

    @FindBy(id = "cv_751F8080808080808080808080809780")
    public WebElement codigoVerba;

    @FindBy(id = "SVC_CODIGO")
    public WebElement comboServico;

    @FindBy(partialLinkText = "Índices")
    public WebElement indice;

    @FindBy(linkText = "Novo índice")
    public WebElement novoIndice;

    @FindBy(name = "indCodigo")
    public WebElement codigoIndice;

    @FindBy(name = "indDescricao")
    public WebElement descricaoIndice;

    @FindBy(linkText = "Editar índice")
    public WebElement editarIndice;

    @FindBy(linkText = "Excluir índice")
    public WebElement excluirIndice;

    @FindBy(id = "adeIndice")
    public WebElement adeIndice;

    @FindBy(css = "dl > dd:nth-child(24)")
    public WebElement txtAdeIndice;

    @FindBy(partialLinkText = "Prazos")
    public WebElement prazos;

    @FindBy(id = "PRZ_VLR_INI")
    public WebElement prazoInicial;

    @FindBy(id = "PRZ_VLR_FIM")
    public WebElement prazoFinal;

    @FindBy(linkText = "Inserir")
    public WebElement inserir;

    @FindBy(linkText = "Desbloquear todos")
    public WebElement desbloquearTodos;

    @FindBy(linkText = "Bloquear todos")
    public WebElement bloquearTodos;

    @FindBy(css = "dd:nth-child(12)")
    public WebElement txtPrazos;

    @FindBy(linkText = "Editar parâmetros de serviço desta consignatária.")
    public WebElement editarParamServico;

    @FindBy(linkText = "Editar parâm. posto graduação")
    public WebElement editarParamPostoGraduacao;

    @FindBy(linkText = "Configurar auditoria sobre operações")
    public WebElement configurarAuditoria;

    @FindBy(id = "checkGrupo248")
    public WebElement todosGeral;

    @FindBy(id = "checkGrupo457")
    public WebElement todosOperacional;

    @FindBy(id = "funcao280")
    public WebElement funcaoAtualizarProcessoPortabilidade;

    @FindBy(id = "funcao81")
    public WebElement funcaoConfirmarSolicitacao;

    @FindBy(linkText = "Editar regra de taxa de juros")
    public WebElement editarRegraTaxaJuros;
}
