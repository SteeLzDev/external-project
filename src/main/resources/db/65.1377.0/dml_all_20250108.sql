-- DESENV-22797
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Prazo em minutos para fechamento do leilão via simulação de consignação' WHERE TPC_CODIGO = '483';

UPDATE tb_param_sist_consignante SET PSI_VLR = PSI_VLR * 60 WHERE TPC_CODIGO = '483';
