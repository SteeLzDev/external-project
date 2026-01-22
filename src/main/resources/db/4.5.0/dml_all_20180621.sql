-- DESENV-8834

INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) 
VALUES ('52', 'Relacionamento para Bloquear Inclusão no Destino', 'N', 'N');

INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO)
SELECT '52', NSE_CODIGO FROM tb_natureza_servico;
