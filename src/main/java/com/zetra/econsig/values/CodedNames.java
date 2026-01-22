package com.zetra.econsig.values;

/**
 * <p>Title: CodedNames</p>
 * <p>Description: This interface defines names in code used as args for lookup().</p>
 * <p>Copyright: Copyright (c) 2003-2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class CodedNames {
    private CodedNames() {
        // para evitar aviso do sonarlint
    }

    // Nome do atribuito de sessão que indica se é via Centralizador de Acesso
    public static final String ATTR_SESSION_CENTRALIZADOR = "acesso.centralizador";

    // Nome do atribuito de sessão que indica o token do leitor de digitais
    public static final String ATTR_SESSION_TOKEN_LEITOR = "token.leitor.digitais";

    // Nome do atribuito de sessão que indica que a digital do servidor foi validada com sucesso
    public static final String ATTR_SESSION_SER_DIGITAL_VALIDA = "servidor.digital.validada";

    // Nome do atribuito de sessão que indica que a digital do servidor foi validada com sucesso
    public static final String ATTR_SESSION_SER_DIGITAL_TENTATIVAS = "servidor.digital.qtde.tentativas";

    // Nome do cabeçalho enviado no SOAP pelo Centralizador com o IP de origem do cliente
    public static final String HEADER_CLIENT_REMOTE_ADDR = "Client-Remote-Address";
    
    // Nome do cabeçalho enviado no SOAP pelo Centralizador com a porta de origem do cliente
    public static final String HEADER_CLIENT_REMOTE_PORT = "Client-Remote-Port";

    public static final String TEMPLATE_TERMO_CONSENTIMENTO_DADOS_SERVIDOR = "termoConsentimentoDadosServidor_v4.msg";
    public static final String TEMPLATE_TERMO_DESCONTO_PARCIAL_SERVIDOR = "termoDescontoParcialServidor_v4.msg";
    public static final String TEMPLATE_TERMO_CADASTRO_EMAIL_SERVIDOR = "termoCadastroEmailServidor_v4.msg";
    public static final String TEMPLATE_BOLETO_AUT_DESCONTO = "boleto_v3.msg";
    public static final String TEMPLATE_EXTRATO_AUT_DESCONTO = "extrato.msg";
    public static final String TEMPLATE_DECLARACAO_MARGEM = "declaracao_margem.msg";
    public static final String TEMPLATE_MENSAGEM_SOLICITACAO_BENEFICIO = "mensagem_solicitacao_beneficio_v4.msg";
}
