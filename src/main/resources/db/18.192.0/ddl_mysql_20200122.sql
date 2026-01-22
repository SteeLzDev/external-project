/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     22/01/2020 12:35:17                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_ope_nao_confirmadas                        */
/*==============================================================*/
create table tb_arquivo_ope_nao_confirmadas
(
   AON_CODIGO           varchar(32) not null,
   ONC_CODIGO           varchar(32) not null,
   AON_NOME             varchar(255) not null,
   AON_TAMANHO          int not null,
   AON_CONTEUDO         longtext not null,
   primary key (AON_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_arquivo_ope_nao_confirmadas add constraint FK_R_793 foreign key (ONC_CODIGO)
      references tb_operacao_nao_confirmada (ONC_CODIGO) on delete restrict on update restrict;

