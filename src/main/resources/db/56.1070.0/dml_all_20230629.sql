-- DESENV-20098
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('928', 'Consolida descontos para integração da folha retorno', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
SELECT '928', '1', COALESCE(psi.PSI_VLR , 'N')
FROM tb_tipo_param_sist_consignante tpc
LEFT OUTER JOIN tb_param_sist_consignante psi on (tpc.TPC_CODIGO = psi.TPC_CODIGO)
WHERE tpc.TPC_CODIGO = '19';

