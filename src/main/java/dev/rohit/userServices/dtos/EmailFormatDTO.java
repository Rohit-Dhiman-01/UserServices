package dev.rohit.userServices.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonSerialize
public class EmailFormatDTO {
    private String to;
    private String from;
    private String content;
    private String subject;
}

