-- DESENV-9314
UPDATE tb_acesso_recurso
SET ACR_RECURSO = '/v3/editarReclamacao', 
ACR_PARAMETRO = 'acao', 
ACR_OPERACAO = 'iniciar'
WHERE FUN_CODIGO = '304'
AND PAP_CODIGO in ('1', '6', '7')
AND ACR_RECURSO = '/reclamacao/lst_reclamacao.jsp';

UPDATE tb_acesso_recurso
SET ACR_RECURSO = '/v3/editarReclamacao', 
ACR_PARAMETRO = 'acao', 
ACR_OPERACAO = 'detalharReclamacao'
WHERE FUN_CODIGO = '304'
AND PAP_CODIGO in ('1', '6', '7')
AND ACR_RECURSO = '/reclamacao/detalhe_reclamacao.jsp';

UPDATE tb_acesso_recurso
SET ACR_RECURSO = '/v3/editarReclamacao', 
ACR_PARAMETRO = 'acao', 
ACR_OPERACAO = 'editarReclamacao'
WHERE FUN_CODIGO = '305'
AND PAP_CODIGO = '6'
AND ACR_RECURSO = '/reclamacao/edt_reclamacao.jsp';
