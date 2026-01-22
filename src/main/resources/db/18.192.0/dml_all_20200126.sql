-- DESENV-12036
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('1', 'Margem');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('2', 'Margem Complementar');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('3', 'Retorno');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('4', 'Retorno Atrasado');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('5', 'Retorno de Férias');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('6', 'Crítica');
INSERT INTO tb_tipo_bloco_processamento (TBP_CODIGO, TBP_DESCRICAO) VALUES ('7', 'Transferido');

INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('1', 'Em preparação');
INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('2', 'Aguard. Processamento');
INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('3', 'Em Processamento');
INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('4', 'Processado com Sucesso');
INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('5', 'Processado com Erro');
INSERT INTO tb_status_bloco_processamento (SBP_CODIGO, SBP_DESCRICAO) VALUES ('6', 'Cancelado');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('688', 'Percentual máximo de linhas sem mapeamento em relação à quantidade de parcelas aguardando retorno', 'FLOAT', '0.00', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('688', '1', '0.00');


INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('689', 'Percentual máximo de rejeito pela quantidade de blocos e quantidade de parcelas aguardando retorno', 'FLOAT', '5.00', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('689', '1', '5.00');
