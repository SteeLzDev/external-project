/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     29/01/2025 18:10:53                          */
/*==============================================================*/


drop table if exists tmp_tb_dados_fato_parcela;

rename table tb_dados_fato_parcela to tmp_tb_dados_fato_parcela;

/*==============================================================*/
/* Table: tb_dados_fato_parcela                                 */
/*==============================================================*/
create table tb_dados_fato_parcela
(
   FAP_CODIGO           int not null,
   DFP_NOME             varchar(255) not null,
   DFP_CPF              char(19) not null,
   DFP_MATRICULA        varchar(20) not null,
   DFP_NUMERO           bigint not null,
   DFP_IDENTIFICADOR    varchar(40) not null,
   DFP_INDICE           varchar(32),
   DFP_VALOR            decimal(13,2) not null,
   DFP_VALOR_LIBERADO   decimal(13,2),
   DFP_PRAZO            int,
   DFP_PAGAS            int,
   DFP_NUMERO_PARCELA   smallint,
   DFP_PERIODO          date not null,
   DFP_VALOR_PREVISTO   decimal(13,2) not null,
   DFP_VALOR_REALIZADO  decimal(13,2),
   DFP_DATA_REALIZADO   date,
   DFP_STATUS_PARCELA   varchar(40) not null
) ENGINE = InnoDB;

insert into tb_dados_fato_parcela (FAP_CODIGO, DFP_NOME, DFP_CPF, DFP_MATRICULA, DFP_NUMERO, DFP_IDENTIFICADOR, DFP_INDICE, DFP_VALOR, DFP_VALOR_LIBERADO, DFP_PRAZO, DFP_PAGAS, DFP_NUMERO_PARCELA, DFP_PERIODO, DFP_VALOR_PREVISTO, DFP_VALOR_REALIZADO, DFP_STATUS_PARCELA)
select FAP_CODIGO, DFP_NOME, DFP_CPF, DFP_MATRICULA, DFP_NUMERO, DFP_IDENTIFICADOR, DFP_INDICE, DFP_VALOR, DFP_VALOR_LIBERADO, DFP_PRAZO, DFP_PAGAS, DFP_NUMERO_PARCELA, DFP_PERIODO, DFP_VALOR_PREVISTO, DFP_VALOR_REALIZADO, DFP_STATUS_PARCELA
from tmp_tb_dados_fato_parcela;

drop table if exists tmp_tb_dados_fato_parcela;

alter table tb_dados_fato_parcela add constraint FK_R_976 foreign key (FAP_CODIGO)
      references tb_fato_parcela (FAP_CODIGO) on delete restrict on update restrict;

