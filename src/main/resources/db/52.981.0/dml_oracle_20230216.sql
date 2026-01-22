-- DESENV-19489
UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = 'N' WHERE TPC_CODIGO = '343' AND TPC_VLR_DEFAULT IS NULL;
UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = 'N' WHERE TPC_CODIGO = '748' AND TPC_VLR_DEFAULT IS NULL;
UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = 'N' WHERE TPC_CODIGO = '902' AND TPC_VLR_DEFAULT IS NULL;
UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = '0', TPC_DOMINIO = 'ESCOLHA[0=Desabilitado' || chr(to_number('3B', 'XX')) || '1=SMS' || chr(to_number('3B', 'XX')) || '2=Email' || chr(to_number('3B', 'XX')) || '3=SMS/Email]' WHERE TPC_CODIGO = '539' AND TPC_VLR_DEFAULT IS NULL;

