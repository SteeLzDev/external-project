-- DESENV-12897
update tb_texto_sistema
set TEX_TEXTO = replace(TEX_TEXTO, ': {0}', '.')
where TEX_CHAVE = 'mensagem.erro.interno.boleto.nao.encontrado';
