-- DESENV-9107

update tb_acesso_recurso set ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', acr_operacao = 'iniciar' where acr_codigo in ('10874','10875','10876','10877','12213');
update tb_acesso_recurso set ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', acr_operacao = 'acompanhar' where acr_codigo = '12852';
