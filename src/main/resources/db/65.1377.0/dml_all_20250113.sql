-- DESENV-22770
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('978', 'Quantidade mínimas de contratos incluídos no dia para notificar o gestor', 'INT', '0', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('978', '1', '0');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA) 
VALUES ('333', 'Serviço é levado em consideração para contagem de inclusões por dia por consignatária', 'N', 'N', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailLimiteAtingidoCsa', '<@cse_nome>: Limite Inclusão Diária CSA Atingido', 'Prezados, <br> a consignatária <@csa_nome> atingiu o número de <@numero_contratos> contratos incluídos neste dia. <br> Atenciosamente <@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('46', 'E-mail de notificação ao gestor que o limite de inclusão da consignatária no dia foi atingido', 'I');

