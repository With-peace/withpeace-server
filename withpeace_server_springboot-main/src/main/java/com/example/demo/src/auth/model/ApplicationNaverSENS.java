package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNaverSENS {
    private String sendFrom = "01065903347";
    private String secretKey = "T8xJQQaFImtCg2epZPYIGqCXlN4xJXGQeHQvH4Iu";
    private String accessKey = "jHy4ZgmOSB1FeHzcrAuB";
    private String seviceId = "ncp:sms:kr:279152038875:withpeace_sms";
}