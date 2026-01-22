-- DESENV-19077
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = CONCAT('ESCOLHA[0=Não', 0x3b, '1=Email', 0x3b, '2=SMS', 0x3b, '3=Email ou SMS]') WHERE TPC_CODIGO = '551';

UPDATE tb_param_sist_consignante SET PSI_VLR = '0' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'N';

UPDATE tb_param_sist_consignante SET PSI_VLR = '1' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'S';

