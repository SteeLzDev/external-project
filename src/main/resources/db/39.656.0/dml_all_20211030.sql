-- DESENV-16730
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('866', 'Mantém, na carga de margem, o status de servidor bloqueado manualmente via sistema', 'SN', 'N', 'N', 'N', 'N', 'N', '2');

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('866', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('192', 'Bloqueio manual de registro servidor');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('193', 'Desbloqueio manual de registro servidor');


-- ROTEIRO MYSQL PARA CRIAR OCORRÊNCIAS DE BLOQUEIO/DESBLOQUEIO MANUAL (AVALIAR SE PRECISA RODAR EM TODOS)
SET @rownum := 0;

-- CRIA OCORRÊNCIA DE BLOQUEIO MANUAL DE RSE : TOC_CODIGO = 192
INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO, TMO_CODIGO)
SELECT CONCAT('X', DATE_FORMAT(ORS_DATA, '%Y%m%d%H%i%s'), '-', LPAD(@rownum := @rownum+1, 16, '0')) AS ORS_CODIGO, '192' AS TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, 'SERVIDOR BLOQUEADO MANUALMENTE.', ORS_IP_ACESSO, TMO_CODIGO
FROM tb_ocorrencia_registro_ser ors
WHERE usu_codigo <> '1'
and ors_obs like '%<B>SITUACAO ALTERADA:</B> DE \'Ativo\' PARA \'Bloqueado\'%'
;

-- CRIA OCORRÊNCIA DE DESBLOQUEIO MANUAL DE RSE : TOC_CODIGO = 193
INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO, TMO_CODIGO)
SELECT CONCAT('Y', DATE_FORMAT(ORS_DATA, '%Y%m%d%H%i%s'), '-', LPAD(@rownum := @rownum+1, 16, '0')) AS ORS_CODIGO, '193' AS TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, 'SERVIDOR DESBLOQUEADO MANUALMENTE.', ORS_IP_ACESSO, TMO_CODIGO
FROM tb_ocorrencia_registro_ser ors
WHERE usu_codigo <> '1'
AND ors_obs like '%<B>SITUACAO ALTERADA:</B> DE \'Bloqueado\' PARA \'Ativo\'%'
;

