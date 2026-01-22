-- DESENV-13701
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('741', 'Dividir o comando da folha em valores semanais na exportação de movimento', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('741', '1', 'N');

INSERT INTO tb_tipo_param_orgao (TAO_CODIGO, TAO_DESCRICAO, TAO_DOMINIO, TAO_VLR_DEFAULT, TAO_SUP_ALTERA, TAO_SUP_CONSULTA, TAO_CSE_ALTERA, TAO_CSE_CONSULTA, TAO_ORG_ALTERA, TAO_ORG_CONSULTA) 
VALUES ('1', 'Dividir o comando da folha em valores semanais na exportação de movimento', 'SN', 'N', 'N', 'N', 'N', 'N', 'N', 'N');
