-- DESENV-14494
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7' WHERE ACR_CODIGO  = '15319';
UPDATE tb_acesso_recurso SET FUN_CODIGO = 235 WHERE ACR_RECURSO = '/v3/manterConsultaBI' AND ACR_OPERACAO = 'excluirConsulta';
UPDATE tb_acesso_recurso SET FUN_CODIGO = 234 WHERE ACR_RECURSO = '/v3/manterConsultaBI' AND ACR_OPERACAO IS NULL;
