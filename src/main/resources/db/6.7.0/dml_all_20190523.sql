-- DESENV-9287
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'informarPgtSdv' WHERE ACR_RECURSO = '/recompra/inf_pgt_saldo_devedor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'solicitarRecalcSdv' WHERE ACR_RECURSO = '/recompra/sol_recalculo_saldo_devedor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'rejeitarPgtoSdv' WHERE ACR_RECURSO = '/recompra/rej_pgt_saldo_devedor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'aprovarSaldoDevedor' WHERE ACR_RECURSO = '/recompra/aprovar_saldo_devedor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'detalharConsignacao' WHERE ACR_RECURSO = '/margem/editar_consignacao.jsp' AND FUN_CODIGO = '137';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'emitirBoleto' WHERE ACR_RECURSO = '/margem/boleto.jsp' AND FUN_CODIGO = '137';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'emitirBoletoExterno' WHERE ACR_RECURSO = '/simulacao/boleto.jsp' AND FUN_CODIGO = '137';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'detalharPesquisa' WHERE ACR_RECURSO = '/recompra/detalhar_pesquisa.jsp';
