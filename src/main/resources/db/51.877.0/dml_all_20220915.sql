-- DESENV-18038
UPDATE tb_param_sist_consignante SET PSI_VLR = '0' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'N';

UPDATE tb_param_sist_consignante SET PSI_VLR = '1' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'S';

