-- DESENV-9212
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp' AND FUN_CODIGO = '58' AND ACR_OPERACAO = 'confirmar_reserva');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp' AND FUN_CODIGO = '58' AND ACR_OPERACAO = 'confirmar_reserva');
DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp' AND FUN_CODIGO = '58' AND ACR_OPERACAO = 'confirmar_reserva'; 
