-- DESENV-8921

update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarVariacaoMargem', ACR_PARAMETRO = 'acao' where acr_codigo in ('11378', '12390', '11379', '11381', '11366', '11368', '11369', '12387', '11367', '12386', '11362', '11365', '11364', '11363', '12964');

update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarVariacaoMargem', ACR_PARAMETRO = 'acao', acr_operacao = 'pesquisarServidor' where acr_codigo in ('12385', '11361', '11360', '11359', '11358');

update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarVariacaoMargem', ACR_PARAMETRO = 'acao', acr_operacao = 'iniciar' where acr_codigo in ('11354', '11356', '11357', '12384', '11355');

update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarVariacaoMargem', ACR_PARAMETRO = 'acao', acr_operacao = 'iniciarMargem' where acr_codigo in ('11377', '11375', '11374', '12389', '11376');

delete from tb_ajuda where acr_codigo in ('11350', '11351', '11352', '11353', '12383');
delete from tb_acesso_recurso where acr_codigo in ('11350', '11351', '11352', '11353', '12383');
