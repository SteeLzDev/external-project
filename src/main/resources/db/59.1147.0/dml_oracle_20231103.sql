-- DESENV-17859
-- ORACLE
UPDATE tb_texto_sistema
SET TEX_CHAVE = 'del.' || TEX_CHAVE
WHERE TEX_CHAVE LIKE 'mensagem.erro.soap.executar%'
AND EXISTS (
SELECT tex2.TEX_CHAVE FROM tb_texto_sistema tex2
WHERE (tb_texto_sistema.TEX_CHAVE = REPLACE(tex2.TEX_CHAVE, 'mensagem.erro.axis.executar', 'mensagem.erro.soap.executar'))
);

UPDATE tb_texto_sistema SET TEX_CHAVE = REPLACE(TEX_CHAVE, 'mensagem.erro.axis.executar', 'mensagem.erro.soap.executar') WHERE TEX_CHAVE LIKE 'mensagem.erro.axis.executar%';

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE like 'del.mensagem.erro.soap.executar%';

