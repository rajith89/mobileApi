package com.udipoc.api.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {

    private String status;
    private Object identity;
    private List<DocumentsResponse> documents;
}
