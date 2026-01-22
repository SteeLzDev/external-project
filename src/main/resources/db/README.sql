/*

INSTRUÇÕES (DESENV-8807):

1) Os arquivos SQL de atualização de banco de dados devem seguir a seguinte nomenclatura:
1.1) DDL para MySQL: X.Y.Z/ddl_mysql_AAAAMMDD.sql
1.2) DML para MySQL: X.Y.Z/ddl_mysql_AAAAMMDD.sql
1.3) DDL para Oracle: X.Y.Z/ddl_oracle_AAAAMMDD.sql
1.4) DML para Oracle: X.Y.Z/ddl_oracle_AAAAMMDD.sql
1.5) DDL para todos os bancos: X.Y.Z/ddl_all_AAAAMMDD.sql
1.6) DML para todos os bancos: X.Y.Z/ddl_all_AAAAMMDD.sql

2) Sendo que:
2.1) X.Y.Z significa o número de versão que o arquivo foi adicionado (major.minor.patch), ex.: 4.4.0.
2.2) DDL significa "Data Definition Language" ou seja comandos SQL de alteração da estrutura do banco de dados.
2.3) DML significa "Data Modification Language" ou seja comandos SQL de alteração dos dados.
2.4) MySQL e Oracle são os bancos de dados suportados pelo eConsig.
2.5) ALL significa comandos que independem do tipo de banco de dados.
2.6) AAAAMMDD significa o ano, mês e dia da inclusão do SQL.

3) Os arquivos devem estar no charset ISO_8859_1, que é o charset padrão do sistema.

4) Os arquivos são executados na seguinte ordem:
4.1) Inicialmente ordenados numericamente pela versão.
4.2) Dentro da mesma versão, os DDLs são executados antes dos DMLs.
4.3) Dentro da mesma versão, e mesmo tipo, são executados pela ordem da data.

5) As seguintes configurações estão disponíveis para customização no arquivo SQL, incluídos como comentários por padrão no início do arquivo:
-- @@delimiter = \p{Punct}
-- @@ignoreDuplicateKeyError = (true|false|yes|no|1|0)
-- @@ignoreForeignKeyError = (true|false|yes|no|1|0)

6) O delimitador padrão é o ponto e vírgula (;). O delimitador deve ser alterado apenas no caso de comandos multi-linha, como criação de triggers ou procedures.

7) Por padrão, erros de duplicate key são ignorados (Somente para MySQL).

8) Por padrão, os demais erros interrompem a execução da atualização.

9) O processo de atualização obtém o conteúdo de cada arquivo, remove o conteúdo irrelevante como os comentários, e separa os comandos pelo delimitador.

10) Cada comando SQL é executado isoladamente. Caso ocorra erro não ignorado, se for um arquivo DML, o sistema fará o rollback dos comandos já executados deste arquivo.
    Assim, para prosseguir, deverá ser identificada a falha, corrigido o arquivo SQL e o processo dará sequência de onde parou. Caso o erro ocorra em um arquivo DDL,
    como o MySQL não faz transação de alteração de estrutura, a recuperação de falha deverá prosseguir do comando onde ocorreu o erro, pois não haverá rollback.

11) O processo de atualização, caso identifique algum arquivo a ser executado, irá bloquear o sistema enquanto as alterações de banco ocorrem.
    Caso algum erro interrompa o processo, o sistema continuará bloqueado para análise manual do problema.
    O sistema NÃO DEVE DE FORMA ALGUMA ser desbloqueado manualmente sem análise e recuperação da falha.

12) Os arquivos SQL executados são registrados na tabela tb_db_ocorrencia. Para que o mesmo arquivo seja executado novamente, é necessário apagar desta
    tabela qualquer registro que DBO_ARQUIVO contenha o nome do arquivo. EM REGRA ISSO NÃO É RECOMENDADO.

13) A rotina de execução de alterações de bancos de dados no deploy do sistema terá a limitação em atualizar a estrutura de tabelas que são utilizadas 
    antes da execução dos SQLs, como por exemplo as tabelas abaixo:

    tb_consignante : por causa do bloqueio
    tb_tipo_ocorrencia : criação de ocorrência de bloqueio precisa recuperar a descrição da ocorrência
    tb_texto_sistema : os textos são carregados para gravar ocorrência de bloqueio.
*/
