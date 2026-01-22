-- Dando permissão da função 240 - Ativar Taxa de Juros com Data Futura para papel consignataria

INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('2', '240');

INSERT INTO tb_funcao_permitida_nca (NCA_CODIGO, FUN_CODIGO) VALUES ('3', '240');

UPDATE tb_funcao SET GRF_CODIGO = '11' WHERE FUN_CODIGO = '240';
