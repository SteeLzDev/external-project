-- DESENV-12021
UPDATE tb_texto_sistema SET TEX_TEXTO = '${rotulo.usuario.singular} (e-mail ou login)' WHERE TEX_CHAVE = 'mensagem.confirmacao.operacao.sensivel.usuario';
