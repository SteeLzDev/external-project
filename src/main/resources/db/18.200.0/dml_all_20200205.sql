-- DESENV-9291
UPDATE tb_acesso_recurso set ACR_RECURSO = "/v3/retirarConsignacaoCompra", ACR_PARAMETRO = "acao", ACR_OPERACAO = "retirarContratoDeCompra" where ACR_CODIGO in ('11398','11399','11400','11401','12395');
UPDATE tb_acesso_recurso set ACR_RECURSO = "/v3/retirarConsignacaoCompra", ACR_PARAMETRO = "acao", ACR_OPERACAO = "efetivarAcao" where ACR_CODIGO in ('11645','11646','11647','11648','12494');
