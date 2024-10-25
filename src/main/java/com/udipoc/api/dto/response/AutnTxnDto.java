package com.udipoc.api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AutnTxnDto {

	String transactionID;
	LocalDateTime requestdatetime;
	String authtypeCode;
	String statusCode;
	String statusComment;
	String referenceIdType;
	String entityName;
	
	
}