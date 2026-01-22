/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/05/2021 14:15:40                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_avaliacao_faq                                      */
/*==============================================================*/
create table tb_avaliacao_faq
(
   AVF_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   FAQ_CODIGO           varchar(32) not null,
   AVF_NOTA             char(1) not null,
   AVF_DATA             datetime not null,
   AVF_COMENTARIO       text not null,
   primary key (AVF_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_avaliacao_faq add constraint FK_R_830 foreign key (FAQ_CODIGO)
      references tb_faq (FAQ_CODIGO) on delete restrict on update restrict;

alter table tb_avaliacao_faq add constraint FK_R_831 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

