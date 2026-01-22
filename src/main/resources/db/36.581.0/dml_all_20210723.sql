-- DESENV-16281
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O valor informado para {0} não pode exceder {1}.' WHERE TEX_CHAVE ='mensagem.erro.taxa.juros.valor.excedido';