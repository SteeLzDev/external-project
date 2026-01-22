-- DESENV-22776

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'rotulo.totp.titulo';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O código de segurança será utilizado como duplo fator de autenticação e autorização de operações no sistema. É necessário que o aplicativo Google Authenticator esteja instalado em seu celular. Clique em confirmar para gerar o código de segurança que deverá ser lido pelo aplicativo.' WHERE TEX_CHAVE = 'mensagem.cadastro.totp';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Confirma a geração do código de segurança para uso em operações no sistema? É necessário que o aplicativo Google Authenticator esteja instalado em seu celular.' WHERE TEX_CHAVE = 'mensagem.cadastro.totp.popup';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O código de segurança será utilizado como duplo fator de autenticação e autorização de operações no sistema. Caso queira desabilitar o código de segurança, informe o código gerado pelo aplicativo e clique em Remover.' WHERE TEX_CHAVE = 'mensagem.totp.cadastrado';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Confirma exclusão do código de segurança?' WHERE TEX_CHAVE = 'mensagem.exclusao.totp.popup';
