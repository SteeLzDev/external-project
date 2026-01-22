-- DESENV-9315
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/restricaoAcesso', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/restricaoacesso/lst_restricao_acesso.jsp' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/restricaoAcesso', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar'  WHERE ACR_RECURSO = '/restricaoacesso/edt_restricao_acesso.jsp' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/restricaoAcesso', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar'  WHERE ACR_RECURSO = '/restricaoacesso/edt_restricao_acesso.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/restricaoAcesso', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE ACR_RECURSO = '/restricaoacesso/lst_restricao_acesso.jsp' AND ACR_OPERACAO = 'excluir';
