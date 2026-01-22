-- DESENV-9123

INSERT INTO tb_tipo_natureza (tnt_codigo, tnt_descricao, tnt_cse_altera, tnt_sup_altera) VALUES ('50', 'Relacionamento de Subsídio Débito Pro Rata', 'N', 'N');
INSERT INTO tb_tipo_natureza (tnt_codigo, tnt_descricao, tnt_cse_altera, tnt_sup_altera) VALUES ('51', 'Relacionamento de Subsídio Crédito Pro Rata', 'N', 'N');

INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('50', '4'); 
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('50', '9');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('51', '4');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('51', '9');
