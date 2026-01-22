-- DESENV-19688
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Verifique o processo' WHERE TEX_CHAVE IN ('rotulo.download.erro.interno', 'rotulo.compact.erro.interno', 'rotulo.delete.erro.interno');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Verifique o processo.' WHERE TEX_CHAVE = 'mensagem.erroInternoSistema';

UPDATE tb_texto_sistema SET TEX_TEXTO = 'VERIFIQUE O PROCESSO' WHERE TEX_CHAVE IN ('mensagem.erroInternoSistema.lote', 'mensagem.licencaSistemaInvalida.lote');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'YC VERIFIQUE O PROCESSO' WHERE TEX_CHAVE IN ('mensagem.erroInternoSistema.febraban', 'mensagem.licencaSistemaInvalida.febraban');

