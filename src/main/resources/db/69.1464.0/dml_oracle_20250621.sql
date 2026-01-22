-- DESENV-23425
DELETE FROM tb_param_sist_consignante WHERE TPC_CODIGO IN ('252', '253', '779', '793', '813', '748');

UPDATE tb_tipo_param_sist_consignante SET TPC_CSE_ALTERA = 'N', TPC_CSE_CONSULTA = 'N', TPC_SUP_ALTERA = 'N', TPC_SUP_CONSULTA = 'N', TPC_DESCRICAO = TPC_DESCRICAO || ' - Não utilizado' 
WHERE TPC_CODIGO in ('252', '253', '779', '793', '813', '748');

