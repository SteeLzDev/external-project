-- DESENV-13416
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.extrato.divida.servidor.subtitulo';
