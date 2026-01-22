#!/bin/bash

# removendo a pasta
rm -rf /tmp/eConsigTestes

# criando pastas
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/temp/upload/anexo
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/anexo
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/boleto
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/politica_privacidade
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/beneficio/fatura/csa/A981808080808080808080808080AF85/H2006081614068580808080800000001
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/lote
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/lote/xml
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/lote
mkdir -p /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/lote/cse

# copiando arquivos
cp src/test/resources/files/boleto_v3.msg /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/boleto/boleto_v3.msg

cp src/test/resources/files/exp_arq_faturamento_beneficio_tradutor.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_faturamento_beneficio_tradutor.xml
cp src/test/resources/files/exp_arq_faturamento_beneficio_saida.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_faturamento_beneficio_saida.xml
cp src/test/resources/files/exp_arq_residuo_faturamento_beneficio_saida.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_residuo_faturamento_beneficio_saida.xml
cp src/test/resources/files/exp_arq_residuo_faturamento_beneficio_tradutor.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_residuo_faturamento_beneficio_tradutor.xml
cp src/test/resources/files/exp_arq_previa_faturamento_beneficio_saida.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_previa_faturamento_beneficio_saida.xml
cp src/test/resources/files/exp_arq_previa_faturamento_beneficio_tradutor.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/fatura/csa/A981808080808080808080808080AF85/exp_arq_previa_faturamento_beneficio_tradutor.xml

# copiando arquivos de teste de importação de cadastro de margens
cp src/test/resources/files/cadastroMargem/imp_margem_entrada.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/imp_margem_entrada.xml
cp src/test/resources/files/cadastroMargem/imp_margem_tradutor.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/imp_margem_tradutor.xml
cp src/test/resources/files/cadastroMargem/imp_margem_saida.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/imp_margem_saida.xml
cp src/test/resources/files/cadastroMargem/margem_acima_media.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media.txt
cp src/test/resources/files/cadastroMargem/margem_acima_media_2.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_2.txt
cp src/test/resources/files/cadastroMargem/margem_acima_media_3.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_3.txt
cp src/test/resources/files/cadastroMargem/margem_acima_media_4.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_4.txt
cp src/test/resources/files/cadastroMargem/margem_acima_media_5.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_5.txt
cp src/test/resources/files/cadastroMargem/margem_acima_media_6.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_6.txt

# copiando arquivos de teste de processamento de lote
cp src/test/resources/files/lote/lote_default_entrada.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/lote/xml/lote_default_entrada.xml
cp src/test/resources/files/lote/lote_default_tradutor.xml /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/conf/lote/xml/lote_default_tradutor.xml
cp src/test/resources/files/lote/lote_com_apenas_um_ade.txt /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/lote/cse/lote_com_apenas_um_ade.txt

cp src/test/resources/files/carlotajoaquina_20200501_20210318103454.zip /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/beneficio/fatura/csa/A981808080808080808080808080AF85/H2006081614068580808080800000001/carlotajoaquina_20200501_20210318103454.zip

cp src/test/resources/files/ser.msg /tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/politica_privacidade/ser.msg
