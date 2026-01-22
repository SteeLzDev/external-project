-- DESENV-13669
-- Substitui a quebra de linha, caso exista, para incluir a chave com nome do arquivo
UPDATE tb_texto_sistema SET TEX_TEXTO = REPLACE(TEX_TEXTO, '.<br>', ': \"{0}\".<br>') WHERE TEX_CHAVE = 'mensagem.erro.upload.nome.arquivo' AND LOCATE('.<br>', TEX_TEXTO) > 0;

-- Caso não exista a quebra de linha, atualiza a chave inteira
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O nome do arquivo não está na formatação correta: \"{0}\".<br> Favor alterar o nome do arquivo conforme o padrão estabelecido.' WHERE TEX_CHAVE = 'mensagem.erro.upload.nome.arquivo' AND LOCATE('.<br>', TEX_TEXTO) = 0;
