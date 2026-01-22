-- DESENV-10473
update tb_texto_sistema set TEX_TEXTO = 'Leilão Reverso', TEX_DATA_ALTERACAO = now() where TEX_CHAVE = 'rotulo.menu.leilao.reverso';
