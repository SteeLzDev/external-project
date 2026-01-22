-- DESENV-11303
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'N' WHERE ACR_RECURSO = '/v3/manterServico' AND ACR_OPERACAO IN ('consultarServico', 'consultarServicoOrg', 'consultarCampo', 'editarServico');
