-- DESENV-22084
INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('41', 'E-mail de notificação a CSA os vínculos bloqueados e desbloqueados', 'I');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('243', 'Bloqueio de vínculo');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('244', 'Desbloqueio de vínculo');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailVinculosBloqDesbloq', '<@nome_sistema> - <@nome_consignante>: Bloqueio/Desbloqueio de vínculos', 'Prezada <@csa_nome>,<br>Segue a relação de vínculos bloqueados/desbloqueados:<br><br>Bloqueados<br><@bloqueados><br><br>Desbloqueados<br><@desbloqueados>');

