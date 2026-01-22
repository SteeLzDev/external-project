-- DESENV-9284
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/suspenderConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmarSuspensao' WHERE FUN_CODIGO = '30' AND ACR_RECURSO = '/margem/confirmar_suspensao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/suspenderConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'suspenderConsignacao' WHERE FUN_CODIGO = '30' AND ACR_RECURSO = '/margem/suspender_consignacao.jsp';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '30' AND PAP_CODIGO = '4');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '30' AND PAP_CODIGO = '4');
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '30' AND PAP_CODIGO = '4';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'rotulo.efetiva.acao.consignacao.suspender';
