-- DESENV-13732

INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('0', 'Bloqueio manual');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('1', 'Pendência em processo de portabilidade');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('2', 'Pendência em informação de saldo solicitado pelo servidor');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('3', 'Pendência em comunicação sem resposta');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('4', 'Pendência em liquidação de consignação com saldo devedor pago');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('5', 'Pendência em mensagem sem leitura');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('6', 'Pendência em consignação sem a quantidade mínima de anexos');
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('7', 'Data de expiração vencida');

DROP TEMPORARY TABLE IF EXISTS tmp_atualiza_motivo_bloqueio_csa;
CREATE TEMPORARY TABLE tmp_atualiza_motivo_bloqueio_csa (CSA_CODIGO VARCHAR(32), TMB_CODIGO VARCHAR(32), PRIMARY KEY (CSA_CODIGO));

INSERT INTO tmp_atualiza_motivo_bloqueio_csa (CSA_CODIGO, TMB_CODIGO)
SELECT csa.CSA_CODIGO,
  CASE occ.TOC_CODIGO 
       WHEN '37'  THEN '1'
       WHEN '68'  THEN '2'
       WHEN '72'  THEN '3'
       WHEN '101' THEN '4'
       WHEN '106' THEN '5'
       WHEN '176' THEN '6'
       WHEN '177' THEN '7'
  END as TMB_CODIGO
FROM tb_ocorrencia_consignataria occ
INNER JOIN tb_consignataria csa ON (occ.CSA_CODIGO = csa.CSA_CODIGO)
WHERE occ.TOC_CODIGO IN ('37','68','72','101','106','176','177')
AND csa.CSA_ATIVO = 0
AND csa.TMB_CODIGO IS NULL
GROUP BY csa.CSA_CODIGO
HAVING COUNT(DISTINCT occ.TOC_CODIGO) = 1
;

UPDATE tb_consignataria csa
INNER JOIN tmp_atualiza_motivo_bloqueio_csa tmp ON (csa.CSA_CODIGO = tmp.CSA_CODIGO)
SET csa.TMB_CODIGO = tmp.TMB_CODIGO
;

UPDATE tb_consignataria SET TMB_CODIGO = 0 WHERE CSA_ATIVO = 0 AND TMB_CODIGO IS NULL;
