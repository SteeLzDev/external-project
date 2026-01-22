-- DESENV-11181
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterFuncoesAuditaveis', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '248' AND ACR_RECURSO = '/auditoria/funcoes_auditaveis.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterFuncoesAuditaveis', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' WHERE FUN_CODIGO = '248' AND ACR_RECURSO = '/auditoria/mantem_func_auditoria.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/auditarOperacoes', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '248' AND ACR_RECURSO = '/auditoria/log_auditoria.jsp' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/auditarOperacoes', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'auditar' WHERE FUN_CODIGO = '248' AND ACR_RECURSO = '/auditoria/log_auditoria.jsp' AND ACR_OPERACAO = 'auditar';
