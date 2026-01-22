-- DESENV-16846
-- INCLUSÃO DO PARÂMETRO

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('873', 'Quantidade de períodos que devem ser usados no cálculo da média de margem folha do servidor', 'INT', '12', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO,CSE_CODIGO,PSI_VLR) 
VALUES ('873','1','12');

-- SQL PARA AJUSTES DA MÉDIA MARGEM ITEM 7 DA TAREFA

CALL dropTableIfExists('tmp_media_margem_folha');
CALL createTemporaryTable('tmp_media_margem_folha (RSE_CODIGO varchar(32), MAR_CODIGO smallint, MEDIA decimal(13,2), primary key (RSE_CODIGO, MAR_CODIGO))');

INSERT INTO tmp_media_margem_folha (RSE_CODIGO, MAR_CODIGO, MEDIA)
SELECT RSE_CODIGO, MAR_CODIGO, AVG(HMA_MARGEM_FOLHA)
FROM tb_historico_margem_folha
WHERE HMA_DATA >= SYSDATE - INTERVAL '12' MONTH
  AND HMA_MARGEM_FOLHA > 0
GROUP BY RSE_CODIGO, MAR_CODIGO
;

UPDATE tb_registro_servidor rse
SET RSE_MEDIA_MARGEM = (
  SELECT MEDIA 
  FROM tmp_media_margem_folha tmp
  WHERE rse.RSE_CODIGO = tmp.RSE_CODIGO
    and tmp.MAR_CODIGO = 1
);

UPDATE tb_registro_servidor rse
SET RSE_MEDIA_MARGEM_2 = (
  SELECT MEDIA 
  FROM tmp_media_margem_folha tmp
  WHERE rse.RSE_CODIGO = tmp.RSE_CODIGO
    and tmp.MAR_CODIGO = 2
);

UPDATE tb_registro_servidor rse
SET RSE_MEDIA_MARGEM_3 = (
  SELECT MEDIA 
  FROM tmp_media_margem_folha tmp
  WHERE rse.RSE_CODIGO = tmp.RSE_CODIGO
    and tmp.MAR_CODIGO = 3
);

UPDATE tb_margem_registro_servidor mrs
SET MRS_MEDIA_MARGEM = (
  SELECT MEDIA 
  FROM tmp_media_margem_folha tmp
  WHERE mrs.RSE_CODIGO = tmp.RSE_CODIGO
    and mrs.MAR_CODIGO = tmp.MAR_CODIGO
);
