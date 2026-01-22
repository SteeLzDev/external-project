-- DESENV-17859
-- MYSQL
UPDATE tb_texto_sistema tex1
INNER JOIN tb_texto_sistema tex2 ON (tex1.TEX_CHAVE = REPLACE(tex2.TEX_CHAVE, 'mensagem.erro.axis.executar', 'mensagem.erro.soap.executar'))
SET tex1.TEX_CHAVE = CONCAT('del.', tex1.TEX_CHAVE)
WHERE tex1.TEX_CHAVE LIKE 'mensagem.erro.soap.executar%';

UPDATE tb_texto_sistema SET TEX_CHAVE = REPLACE(TEX_CHAVE, 'mensagem.erro.axis.executar', 'mensagem.erro.soap.executar') WHERE TEX_CHAVE LIKE 'mensagem.erro.axis.executar%';

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE like 'del.mensagem.erro.soap.executar%';

