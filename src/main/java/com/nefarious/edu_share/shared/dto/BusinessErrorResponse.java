package com.nefarious.edu_share.shared.dto;

import com.nefarious.edu_share.shared.interfaces.BusinessError;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessErrorResponse {
    private BusinessError businessError;
    private String message;
    private long timestamp;
}
