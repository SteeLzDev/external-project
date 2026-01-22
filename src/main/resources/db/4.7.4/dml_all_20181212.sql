-- DESENV-10354
UPDATE tb_tipo_natureza SET TNT_DESCRICAO = 'Controle de Migração de Benefícios', TNT_CSE_ALTERA = NULL WHERE TNT_CODIGO = '53';
INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO) VALUES ('5', 'Cancelamento de benefícios por migração de benefícios'); 
