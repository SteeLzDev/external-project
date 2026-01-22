-- DESENV-22449
-- MYSQL
INSERT IGNORE INTO tb_tipo_registro_servidor (TRS_CODIGO, TRS_DESCRICAO) VALUES ('0', 'PADRÃO');
INSERT IGNORE INTO tb_tipo_registro_servidor (TRS_CODIGO, TRS_DESCRICAO) VALUES ('1', 'EXTERIOR');
INSERT IGNORE INTO tb_tipo_registro_servidor (TRS_CODIGO, TRS_DESCRICAO) VALUES ('2', 'TEMPORÁRIO');
INSERT IGNORE INTO tb_tipo_registro_servidor (TRS_CODIGO, TRS_DESCRICAO) VALUES ('3', 'PENSIONISTA');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA)
VALUES ('330', 'Prazo é limitado para os servidores temporários', 'N', 'N', 'N');

