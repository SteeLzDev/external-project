-- DESENV-9288
UPDATE tb_acesso_recurso SET acr_recurso = '/v3/atualizarProcessoPortabilidade', acr_parametro = 'acao', acr_operacao = 'iniciar' WHERE acr_codigo IN ('13006', '13008', '13010', '13012', '13014');
UPDATE tb_acesso_recurso SET acr_recurso = '/v3/atualizarProcessoPortabilidade', acr_parametro = 'acao', acr_operacao = 'atualizarContrato' WHERE acr_codigo IN ('13007', '13009', '13011', '13013', '13015');
