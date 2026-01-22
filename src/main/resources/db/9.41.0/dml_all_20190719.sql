-- DESENV-10826
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('646', 'Validar limite de consignações por verba/serviço/natureza de serviço na transferência de consignação', 'SN', 'N', 'N', 'N', 'N', 'N'); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('646', '1', 'N');
