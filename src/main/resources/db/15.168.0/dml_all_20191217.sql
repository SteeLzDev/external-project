-- DESENV-9301
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterGrupoServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '124';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterGrupoServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/exclui_grupo_servicos.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterGrupoServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'inserir' WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/ins_grupo_servico.jsp' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterGrupoServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar', ACR_FIM_FLUXO = 'N' WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/edt_grupo_servico.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterGrupoServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar'  WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/ins_grupo_servico.jsp' AND ACR_OPERACAO = 'editar';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/edt_grupo_servico.jsp' AND ACR_OPERACAO = 'consultar');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/edt_grupo_servico.jsp' AND ACR_OPERACAO = 'consultar');
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '123' AND ACR_RECURSO = '/servicos/edt_grupo_servico.jsp' AND ACR_OPERACAO = 'consultar';
