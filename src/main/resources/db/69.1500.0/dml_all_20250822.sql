-- DML
UPDATE tb_modelo_email
SET MEM_TEXTO = 'Prezado (a) <@csa_nome>,<br>Informamos que as seguintes regras de convênio foram alteradas para <@nome_consignante>: <br> <@dados_regras_noescape> <br> Atenciosamente,<br> <img src="<@logoSistema>" width="30%" title="logo" alt="titulo.logo">'
WHERE MEM_CODIGO = 'enviarEmailCsaNotificacaoRegras';

