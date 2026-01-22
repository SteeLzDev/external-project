-- DESENV-12048
UPDATE tb_item_menu SET ITM_DESCRICAO = 'Consultar contrato de plano de saúde e odontológico' WHERE ITM_CODIGO = '204';
UPDATE tb_item_menu SET ITM_DESCRICAO = 'Simular plano de saúde e odontológico' WHERE ITM_CODIGO = '208';
UPDATE tb_item_menu SET ITM_DESCRICAO = 'Incluir beneficiário em plano vigente' WHERE ITM_CODIGO = '211';
UPDATE tb_item_menu SET ITM_DESCRICAO = 'Simular alteração de plano' WHERE ITM_CODIGO = '212';

UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Habilita Módulo de Benefícios de Saúde' WHERE TPC_CODIGO = '578';
