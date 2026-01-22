-- DESENV-9276
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '223' AND ACR_RECURSO = '/servidor/extrato_consolidado.jsp');
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '223' AND ACR_RECURSO = '/servidor/extrato_consolidado.jsp';
