-- DESENV-20776
UPDATE tb_tipo_param_consignataria SET TPA_DOMINIO = 'FLOAT' WHERE TPA_CODIGO = '85';

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('228', 'Convênio bloqueado para o servidor por variação da margem além do percentual permitido pela CSA');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('229', 'Convênio desbloq. para o servidor por variação da margem dentro do percentual permitido pela CSA');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailBloqSerLimCsa', '<@nome_sistema>: Arquivo Relação Bloqueio convênios Servdiores', 'Prezado,<br>foi gerado o arquivo com a relação de servidores que tiveram o convênio bloqueado, pois a variação da margem atingiu o limite estipilado pela consignatária.<br><br>Gentileza acessar o sistema para verificação.');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailCsaBloqSerCnv', '<@nome_sistema> - <@nome_consignante>: Bloqueios de verba por matrícula que tiveram variação de margem a maior', 'Prezada <@csaNome>,<br>As seguintes verbas foram bloqueadas porque as matrículas tiveram variação de margem a maior:<br><@quantidadePorVerba>');

