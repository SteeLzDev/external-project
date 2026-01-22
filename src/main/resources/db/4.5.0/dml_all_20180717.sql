-- DESENV-9158

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Listagem de ranking com o resultado da simulação em {0}' WHERE TEX_CHAVE = 'mensagem.simulacao.resultado' AND TEX_TEXTO = 'Listagem de ranking com o resultado da simulacao em {0}';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'mensagem.upload.arquivo.resultado';
