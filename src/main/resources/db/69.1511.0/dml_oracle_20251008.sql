UPDATE tb_tipo_param_sist_consignante SET TPC_VLR_DEFAULT = NULL, TPC_DOMINIO = 'ALFA' WHERE TPC_CODIGO in ('993', '994', '995', '996');

DELETE FROM tb_param_sist_consignante t1
WHERE t1.TPC_CODIGO IN ('993', '994')
  AND NOT EXISTS (
    SELECT 1 FROM tb_param_sist_consignante t995
    WHERE t995.TPC_CODIGO = '995'
      AND t995.PSI_VLR IS NOT NULL
      AND TRIM(t995.PSI_VLR) <> ''
  )
  AND NOT EXISTS (
    SELECT 1 FROM tb_param_sist_consignante t996
    WHERE t996.TPC_CODIGO = '996'
      AND t996.PSI_VLR IS NOT NULL
      AND TRIM(t996.PSI_VLR) <> ''
  );

DELETE FROM tb_leitura_termo_usuario ltu
WHERE ltu.tad_codigo IN (
  SELECT tad.tad_codigo
  FROM tb_termo_adesao tad
  WHERE tad.tad_data > TO_DATE('2025-10-03 23:59:59', 'YYYY-MM-DD HH24:MI:SS')
    AND tad.TAD_CODIGO = '1'
    AND tad.TAD_TITULO LIKE '%Financeira do eConsig%'
);

DELETE FROM tb_termo_adesao
WHERE tad_data > TO_DATE('2025-10-03 23:59:59', 'YYYY-MM-DD HH24:MI:SS')
  AND TAD_CODIGO = '1'
  AND TAD_TITULO LIKE '%Financeira do eConsig%';
