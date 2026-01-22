-- DESENV-14064
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Considerar contratos não exportados na rotina de obtenção de contratos sem margem na exportação.' WHERE TPC_CODIGO = '753'; 
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Utiliza biblioteca nativa para criação de processos externos.' WHERE TPC_CODIGO = '748'; 

UPDATE tb_tipo_param_consignataria SET TPA_DESCRICAO = 'Permite inclusão via Lote Web para servidor excluído.' WHERE TPA_CODIGO = '57';
