-- DESENV-14177
REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'edtAtualizarDadosServidor.email',
       CASE PSI_VLR WHEN 'S' THEN 'O' ELSE 'N' END
FROM tb_param_sist_consignante
WHERE TPC_CODIGO = '493';

REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'edtAtualizarDadosServidor.telefone',
       CASE PSI_VLR WHEN 'S' THEN 'O' ELSE 'N' END
FROM tb_param_sist_consignante
WHERE TPC_CODIGO = '611';

REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'edtAtualizarDadosServidor.celular',
       CASE PSI_VLR WHEN 'S' THEN 'O' ELSE 'N' END
FROM tb_param_sist_consignante
WHERE TPC_CODIGO = '611';

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('765', 'Exige atualização de dados pelo servidor no primeiro acesso ao sistema', 'SN', 'N', 'N', 'N', 'N', 'N', '1');

REPLACE INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
SELECT '765', psc.cse_codigo,
       CASE PSI_VLR WHEN 'S' THEN 'S' ELSE 'N' END
FROM tb_param_sist_consignante psc
WHERE TPC_CODIGO IN ('611', '493') 
ORDER BY psi_vlr DESC 
LIMIT 1;
