-- DESENV-9113

INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) VALUES ('45', 'Relacionamento de Resíduo Odontológico', 'N', 'N');
INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) VALUES ('46', 'Relacionamento de Crédito Operadora Odontológico', 'N', 'N'); 
INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) VALUES ('47', 'Relacionamento de Crédito Consignante Odontológico', 'N', 'N'); 
INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) VALUES ('48', 'Relacionamento de Débito Operadora Odontológico', 'N', 'N'); 
INSERT INTO tb_tipo_natureza (TNT_CODIGO, TNT_DESCRICAO, TNT_CSE_ALTERA, TNT_SUP_ALTERA) VALUES ('49', 'Relacionamento de Débito Consignante Odontológico', 'N', 'N'); 

update tb_tipo_natureza set tnt_descricao = 'Relacionamento de Crédito Operadora Plano Saúde' where tnt_codigo = '40';
update tb_tipo_natureza set tnt_descricao = 'Relacionamento de Crédito Consignante Plano Saúde' where tnt_codigo = '41';
update tb_tipo_natureza set tnt_descricao = 'Relacionamento de Débito Operadora Plano Saúde' where tnt_codigo = '42';
update tb_tipo_natureza set tnt_descricao = 'Relacionamento de Débito Consignante Plano Saúde' where tnt_codigo = '43';

delete from tb_natureza_editavel_nse where NSE_CODIGO = '9' and TNT_CODIGO = '40';
delete from tb_natureza_editavel_nse where NSE_CODIGO = '9' and TNT_CODIGO = '41'; 
delete from tb_natureza_editavel_nse where NSE_CODIGO = '9' and TNT_CODIGO = '42';
delete from tb_natureza_editavel_nse where NSE_CODIGO = '9' and TNT_CODIGO = '43';

INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('45', '9');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('46', '9');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('47', '9');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('48', '9');
INSERT INTO tb_natureza_editavel_nse (TNT_CODIGO, NSE_CODIGO) VALUES ('49', '9');
