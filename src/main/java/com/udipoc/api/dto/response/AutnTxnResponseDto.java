package com.udipoc.api.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AutnTxnResponseDto {

	/** Variable To hold id */
	private String id;

	/** Variable To hold version */
	private String version;

	/** The error List */
	private List<RsError> errors;

	/** List to hold AutnTxnDto */
	private Map<String, List<AutnTxnDto>> response;
	/** The id. */

	/** The resTime value */
	private String responseTime;

}
