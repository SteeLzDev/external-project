-- DESENV-17184
DELETE FROM tb_ajuda WHERE ACR_CODIGO = '15649';
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO = '15649';
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO = '15649';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'redirecionarBoletoSdv' WHERE ACR_RECURSO = '/recompra/foward_boleto_sdv.jsp';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/exibirMensagens', ACR_PARAMETRO = NULL, ACR_OPERACAO = NULL WHERE ACR_CODIGO IN ('11786', '11787', '11788', '11789', '11790', '12533');
