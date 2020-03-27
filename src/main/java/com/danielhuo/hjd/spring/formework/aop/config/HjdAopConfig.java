package com.danielhuo.hjd.spring.formework.aop.config;

import lombok.Data;


@Data
public class HjdAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
