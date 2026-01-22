-- DESENV-10470
delete from tb_acesso_recurso where acr_recurso = '/v3/simularConsignacao' and acr_parametro = 'acao' and acr_operacao = 'iniciarLeilao';
