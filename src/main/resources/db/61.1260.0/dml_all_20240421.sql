-- DESENV-21163
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('92', 'percentual de parcelas pagas que se deve alertar à CSA para que ela decida se quer ofertar refinanciamento.', 'FLOAT', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('93', 'E-mails de alerta % de parcelas pagas (separados por ponto e vírgula).', 'ALFA', 'N', 'N', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailRefinanciamentoCsa', '<@nome_sistema> - Alerta de percentual de parcelas pagas atingido', 'Prezada <@nome_csa>, Informamos que os contratos abaixo atingiram <@percentual>% de parcelas pagas: <@ade_numero>');

INSERT INTO tb_tipo_ocorrencia (toc_codigo, TOC_DESCRICAO) VALUES ('231', 'Email de porcentagem de parcelas enviada para a consignataria');

