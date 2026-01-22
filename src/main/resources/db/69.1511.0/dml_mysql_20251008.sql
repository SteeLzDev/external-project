UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = NULL, TPC_DOMINIO = 'ALFA' WHERE TPC_CODIGO in ('993', '994', '995', '996');

DELETE t1
FROM tb_param_sist_consignante t1
LEFT JOIN tb_param_sist_consignante t995 ON t995.TPC_CODIGO = '995'
LEFT JOIN tb_param_sist_consignante t996 ON t996.TPC_CODIGO = '996'
WHERE t1.TPC_CODIGO IN ('993', '994')
AND (t995.PSI_VLR IS NULL OR t995.PSI_VLR = '')
AND (t996.PSI_VLR IS NULL OR t996.PSI_VLR = '');

DELETE ltu.*
FROM tb_termo_adesao tad
INNER JOIN tb_leitura_termo_usuario ltu ON (tad.tad_codigo = ltu.tad_codigo)
WHERE tad.tad_data > '2025-10-03 23:59:59' AND tad.TAD_CODIGO = '1' AND tad.TAD_TITULO like '%Financeira do eConsig%';

DELETE FROM tb_termo_adesao tad
WHERE tad.tad_data > '2025-10-03 23:59:59' AND tad.TAD_CODIGO = '1' AND tad.TAD_TITULO like '%Financeira do eConsig%';
