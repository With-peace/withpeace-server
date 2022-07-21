package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequestDto {
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<MessagesRequestDto> messages;
}