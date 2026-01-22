-- DESENV-21206
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = 'INT' WHERE TPC_CODIGO = '333';

UPDATE tb_param_sist_consignante
SET PSI_VLR = CASE WHEN PSI_VLR = 'S' THEN '1' WHEN PSI_VLR = 'N' THEN '0' ELSE '0' END
WHERE TPC_CODIGO = '333' 
AND PSI_VLR IS NOT NULL;

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'mensagem.indisponibilidade.renegociacao.contrato.na.ultima.parcela';

