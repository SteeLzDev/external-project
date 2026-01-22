-- DESENV-9223

update tb_acesso_recurso set ACR_FIM_FLUXO = 'N' where FUN_CODIGO IN ('27','28') and ACR_OPERACAO = 'efetivarAcao' and ACR_FIM_FLUXO = 'S';
