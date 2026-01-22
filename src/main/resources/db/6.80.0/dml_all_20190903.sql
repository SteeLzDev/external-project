-- DESENV-12312
UPDATE tb_acesso_recurso SET acr_fim_fluxo = 'S' WHERE FUN_CODIGO = '23' AND ACR_OPERACAO = 'efetivarAcaoServico';
