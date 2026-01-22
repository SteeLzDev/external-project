-- DESENV-13073
update tb_db_ocorrencia
set DBO_ARQUIVO = replace(DBO_ARQUIVO, '16.168.0', '15.168.0')
where DBO_ARQUIVO like '16.168.0%';
