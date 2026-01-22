-- DESENV-17726
UPDATE tb_texto_sistema
SET TEX_TEXTO='Período de exportação inválido. Favor verificar as datas iniciais e finais cadastradas para o período atual.'
WHERE TEX_CHAVE='mensagem.erro.folha.exportacao.periodo.invalido.verificar.datas'