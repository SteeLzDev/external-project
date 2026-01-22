-- DESENV-13834
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('762', 'Identificação da semana no resultado da exportação semanal', CONCAT('ESCOLHA[1=Número da semana no ano', 0x3b, '2=Ordem da semana no período]'), '1', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_tipo_param_orgao (TAO_CODIGO, TAO_DESCRICAO, TAO_DOMINIO, TAO_VLR_DEFAULT, TAO_SUP_ALTERA, TAO_SUP_CONSULTA, TAO_CSE_ALTERA, TAO_CSE_CONSULTA, TAO_ORG_ALTERA, TAO_ORG_CONSULTA) 
VALUES ('5', 'Identificação da semana no resultado da exportação semanal', CONCAT('ESCOLHA[1=Número da semana no ano', 0x3b, '2=Ordem da semana no período]'), '1', 'N', 'N', 'N', 'N', 'N', 'N');
