-- DESENV-10099

UPDATE tb_tipo_arquivo
SET TAR_NOTIFICACAO_UPLOAD = 'S'
WHERE TAR_CODIGO IN ('1', '2', '3', '4', '5', '6', '11', '17', '26', '27', '39', '40', '41', '42');

DELETE FROM tb_destinatario_email WHERE FUN_CODIGO = '66';

-- Configuração padrão: somente o papel que enviou recebe notificação
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) VALUES ('66', '1', '1');
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) VALUES ('66', '3', '3');

-- Envio para CSA/COR somente se o parâmetro 545 está habilitado
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) SELECT '66', '1', '2' FROM tb_param_sist_consignante WHERE TPC_CODIGO = '545' AND PSI_VLR = 'S';
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) SELECT '66', '3', '2' FROM tb_param_sist_consignante WHERE TPC_CODIGO = '545' AND PSI_VLR = 'S';

-- Upload de Arquivo Genérico
INSERT INTO tb_tipo_arquivo (TAR_CODIGO, TAR_DESCRICAO, TAR_QTD_DIAS_LIMPEZA, TAR_UPLOAD_SUP, TAR_UPLOAD_CSE, TAR_UPLOAD_ORG, TAR_UPLOAD_CSA, TAR_UPLOAD_COR, TAR_NOTIFICACAO_UPLOAD)
VALUES ('43', 'Arquivo genérico', 0, 'S', 'S', 'S', 'N', 'N', 'S');

DELETE FROM tb_destinatario_email WHERE FUN_CODIGO = '84';

-- Configuração padrão: somente o papel que enviou recebe notificação
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) VALUES ('84', '1', '1');
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) VALUES ('84', '3', '3');

-- Envio para CSA/COR somente se o parâmetro 545 está habilitado
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) SELECT '84', '1', '2' FROM tb_param_sist_consignante WHERE TPC_CODIGO = '545' AND PSI_VLR = 'S';
INSERT INTO tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) SELECT '84', '3', '2' FROM tb_param_sist_consignante WHERE TPC_CODIGO = '545' AND PSI_VLR = 'S';
