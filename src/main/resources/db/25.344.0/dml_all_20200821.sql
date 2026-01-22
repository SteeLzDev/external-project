-- DESENV-9300
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterEmpresaCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/correspondente/lst_empresa_correspondente.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterEmpresaCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'modificaEmpresa' WHERE ACR_RECURSO = '/correspondente/modifica_empresa_correspondente.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterEmpresaCorrespondente', ACR_PARAMETRO = 'acao' WHERE ACR_RECURSO = '/correspondente/edt_empresa_correspondente.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterEmpresaCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'associar' WHERE ACR_RECURSO = '/correspondente/associa_empresa_correspondente.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso set acr_recurso = '/v3/manterEmpresaCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'detalhar' WHERE ACR_RECURSO = '/correspondente/edt_empresa_correspondente.jsp' AND ACR_OPERACAO = 'consultar';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/correspondente/associa_empresa_correspondente.jsp' AND ACR_OPERACAO = 'consultar');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/correspondente/associa_empresa_correspondente.jsp' AND ACR_OPERACAO = 'consultar');
DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO = '/correspondente/associa_empresa_correspondente.jsp' AND ACR_OPERACAO = 'consultar';
