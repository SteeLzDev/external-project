-- DESENV-10880
INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('10', 'E-mail notificação alteração de código de verba dos convênios do Serviço', 'I'); 

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_CSA_ALTERA, TPS_SUP_ALTERA) 
VALUES ('274', 'E-mail para notificar sobre alterações de código de verba dos convênios do Serviço', 'N', 'N', 'N'); 

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('enviaNotifAlterCodVerbaConvCSA', 'Alteração do código de verba', '<br>O sistema identificou a alteração do código de verba do serviço <@servico>. Feita por: <@responsavel> <br>Data da alteração: <@agora>.<br><@orgaosAtivados><br><@orgaosDesativados><br><@codVerbaAlterado>');
