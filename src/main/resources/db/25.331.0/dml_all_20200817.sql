-- DESENV-14230
UPDATE tb_texto_sistema 
SET TEX_TEXTO = 'Operação concluída com sucesso. Um e-mail foi enviado para {0} com um link de acesso ao sistema onde poderá redefinir sua senha.' 
WHERE TEX_CHAVE = 'mensagem.reinicializar.senha.usuario.sucesso';

