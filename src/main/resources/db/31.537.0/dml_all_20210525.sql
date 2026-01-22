-- DESENV-15884
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Desculpe, não conseguimos atender a sua solicitação', TEX_DATA_ALTERACAO = NOW() WHERE TEX_CHAVE='mobile.title.error';

