-- DESENV-22968
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = 'ALFA' WHERE TPC_CODIGO = '871';

DELETE FROM tb_param_sist_consignante WHERE TPC_CODIGO = '871' AND LENGTH(PSI_VLR) = 1;

