-- DESENV-21908
UPDATE tb_param_sist_consignante SET PSI_VLR = CASE WHEN PSI_VLR = 'N' THEN '0' WHEN PSI_VLR = 'S' THEN '1' ELSE '0' END WHERE TPC_CODIGO = '925';

-- MYSQL
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = CONCAT('ESCOLHA[0=Não', 0x3b, '1=Csa', 0x3b, '2=Cse', 0x3b, '3=Csa/Cse]'), TPC_VLR_DEFAULT = '0' WHERE TPC_CODIGO = '925';

