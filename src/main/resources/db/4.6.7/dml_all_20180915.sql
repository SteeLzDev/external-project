-- DESENV-9666

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO) 
VALUES ('4', 'Cancelar contrato de benefício por inadimplência');

INSERT INTO tb_tipo_arquivo (TAR_CODIGO, TAR_DESCRICAO, TAR_QTD_DIAS_LIMPEZA, TAR_UPLOAD_SUP, TAR_UPLOAD_CSE, TAR_UPLOAD_ORG, TAR_UPLOAD_CSA, TAR_UPLOAD_COR) 
VALUES ('40', 'Cancelamento de benefícios por inadimplência', '0', 'S', 'N', 'N', 'N', 'N'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14926', '7', '66', '/v3/uploadArquivoCancelamentoporinadimplencia', 'acao', 'upload', '1', 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14927', '7', '66', '/v3/uploadArquivoCancelamentoporinadimplencia', 'acao', 'carregar', '1', 'S', 'S', NULL, 'N', '2');
