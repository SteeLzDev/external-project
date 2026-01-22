-- DESENV-9297
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConvenioCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar' ,   ACR_FIM_FLUXO = 'N' WHERE ACR_CODIGO in ('10215', '10216', '12023');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConvenioCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultar', ACR_FIM_FLUXO = 'N' WHERE ACR_CODIGO in ('11476', '11477', '12427');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConvenioCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar',   ACR_FIM_FLUXO = 'N' WHERE ACR_CODIGO in ('11478', '11479', '12428');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConvenioCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar',    ACR_FIM_FLUXO = 'S' WHERE ACR_CODIGO in ('10223', '10224', '12026');
