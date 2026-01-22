-- DESENV-21908
UPDATE tb_param_sist_consignante SET PSI_VLR = CASE WHEN PSI_VLR = 'N' THEN '0' WHEN PSI_VLR = 'S' THEN '1' ELSE '0' END WHERE TPC_CODIGO = '925';

-- ORACLE
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = 'ESCOLHA[0=Não' || chr(to_number('3B', 'XX')) || '1=Csa' || chr(to_number('3B', 'XX')) || '2=Cse' || chr(to_number('3B', 'XX')) || '3=Csa/Cse]', TPC_VLR_DEFAULT = '0' WHERE TPC_CODIGO = '925';

