-- DESENV-7868

INSERT INTO tb_tipo_param_validacao_arq (TVA_CODIGO, TVA_CHAVE, TVA_DESCRICAO) VALUES ('70', 'historico.extensoes', 'Extensões permitidas para arquivo de histórico'); 
INSERT INTO tb_param_validacao_arq_cse (TVA_CODIGO, CSE_CODIGO, VAC_VALOR) VALUES ('70', '1', 'ZIP,TXT');
