-- DESENV-11173
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '76' AND ACR_RECURSO IN ('/margem/consignacao.jsp', '/margem/pesquisa.jsp', '/margem/seleciona_servidor.jsp'));
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '76' AND ACR_RECURSO IN ('/margem/consignacao.jsp', '/margem/pesquisa.jsp', '/margem/seleciona_servidor.jsp'));
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '76' AND ACR_RECURSO IN ('/margem/consignacao.jsp', '/margem/pesquisa.jsp', '/margem/seleciona_servidor.jsp');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listar'            WHERE ACR_CODIGO = '12939';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'incluir'           WHERE ACR_CODIGO = '12940';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar'            WHERE ACR_CODIGO = '12941';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar'            WHERE ACR_CODIGO = '12942';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar'           WHERE ACR_CODIGO = '12943';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'pesquisarServidor' WHERE ACR_CODIGO = '12944';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterPermissionario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir'           WHERE ACR_CODIGO = '12945';

UPDATE tb_acesso_recurso SET FUN_CODIGO = '271' WHERE ACR_CODIGO IN ('12940','12941','12942');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15309', '2', '270', '/v3/manterPermissionario', 'acao', 'consultar', 1, 'S', 'S', null, 'N', '2'); 
