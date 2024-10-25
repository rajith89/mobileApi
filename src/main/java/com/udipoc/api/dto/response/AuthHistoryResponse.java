package com.udipoc.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthHistoryResponse implements Serializable {
   /* private HashMap<String, Object> response;
    private List<HashMap<String,String>> errors = new ArrayList<>();*/
    private int serialNumber;
    private String idUsed;
    private String authModality;
    private String date;
    private String time;
    private String partnerName;
    private String partnerTransactionId;
    private String authResponse;
    private String responseCode;
}
