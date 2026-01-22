-- DESENV-19077
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = 'ESCOLHA[0=Não' || chr(to_number('3B', 'XX')) || '1=Email' || chr(to_number('3B', 'XX')) || '2=SMS' || chr(to_number('3B', 'XX')) || '3=Email ou SMS]' WHERE TPC_CODIGO = '551';

UPDATE tb_param_sist_consignante SET PSI_VLR = '0' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'N';

UPDATE tb_param_sist_consignante SET PSI_VLR = '1' WHERE TPC_CODIGO = '551' AND PSI_VLR = 'S';

