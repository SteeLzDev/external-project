-- DESENV-9266
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'filtrar' WHERE ACR_CODIGO = '11554';
UPDATE tb_acesso_recurso SET FUN_CODIGO = '161' WHERE ACR_CODIGO IN ('10653', '10654', '10655', '10656','12143');

DELETE from tb_ajuda WHERE ACR_CODIGO IN ('10705', '10706', '10707', '10708', '12160');
DELETE from tb_acesso_usuario WHERE ACR_CODIGO IN ('10705', '10706', '10707', '10708', '12160');
DELETE from tb_acesso_recurso WHERE ACR_CODIGO IN ('10705', '10706', '10707', '10708', '12160');
