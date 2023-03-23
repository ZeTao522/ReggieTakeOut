package com.zzt.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleMail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private String subject;
    private String text;
}
