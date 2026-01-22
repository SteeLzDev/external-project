-- DESENV-20474
-- MYSQL
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Anexar arquivo é obrigatório na inclusão de contratos para o papel consignante, órgão e suporte.' WHERE TEX_CHAVE = 'rotulo.param.svc.exigir.anexar.arquivo.inclusao';

UPDATE tb_tipo_param_svc SET TPS_DESCRICAO = 'Anexar arquivo é obrigatório na inclusão de contratos para o papel consignante, órgão e suporte.' WHERE TPS_CODIGO = '210';

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA)
VALUES ('325', 'Anexar arquivo é obrigatório na inclusão de contratos para o papel consignatária e corresponde.', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA)
VALUES ('326', 'Anexar arquivo é obrigatório na inclusão de contratos para o papel servidor.', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA)
VALUES ('327', 'Quantidade mínima de anexos na inclusão de contratos.', 'N', 'N', 'N');

INSERT INTO tb_param_svc_consignante
SELECT CONCAT('I', DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), SUBSTRING(LPAD(svc.svc_codigo, 12, '0'), 1, 12), SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), svc.svc_codigo, '325', cse.cse_codigo, pse.pse_vlr, NULL
FROM tb_servico svc
INNER JOIN tb_param_svc_consignante pse ON (pse.svc_codigo = svc.svc_codigo AND pse.tps_codigo='210')
INNER JOIN tb_consignante cse ON (cse.cse_codigo = pse.cse_codigo);

INSERT INTO tb_param_svc_consignante
SELECT CONCAT('I', DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), SUBSTRING(LPAD(svc.svc_codigo, 12, '0'), 1, 12), SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), svc.svc_codigo, '326', cse.cse_codigo, pse.pse_vlr, NULL
FROM tb_servico svc
INNER JOIN tb_param_svc_consignante pse ON (pse.svc_codigo = svc.svc_codigo AND pse.tps_codigo='210')
INNER JOIN tb_consignante cse ON (cse.cse_codigo = pse.cse_codigo);

