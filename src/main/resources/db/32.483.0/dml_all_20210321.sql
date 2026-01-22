-- DESENV-9285
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharFinanciamentoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'solicitar' WHERE ACR_CODIGO = '13672';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharFinanciamentoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'aprovar' WHERE ACR_CODIGO = '13673';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharFinanciamentoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '13674';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharFinanciamentoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar' WHERE ACR_CODIGO = '13675';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharFinanciamentoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' WHERE ACR_CODIGO = '13676';

delete from tb_natureza_editavel_nse where tnt_codigo = '19' and nse_codigo = '1';
insert into tb_natureza_editavel_nse (tnt_codigo, nse_codigo) values ('19', '7');
